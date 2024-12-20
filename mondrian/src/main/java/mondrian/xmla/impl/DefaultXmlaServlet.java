/* Decompiler 592ms, total 1312ms, lines 578 */
package mondrian.xmla.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import mondrian.server.Session;
import mondrian.util.XmlParserFactoryProducer;
import mondrian.xmla.SaxWriter;
import mondrian.xmla.XmlaException;
import mondrian.xmla.XmlaRequestCallback;
import mondrian.xmla.XmlaServlet;
import mondrian.xmla.XmlaUtil;
import mondrian.xmla.Enumeration.ResponseMimeType;
import mondrian.xmla.XmlaServlet.Phase;
import org.olap4j.impl.Olap4jUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class DefaultXmlaServlet extends XmlaServlet {
    protected static final String nl = System.getProperty("line.separator");
    private static final String REQUIRE_AUTHENTICATED_SESSIONS = "requireAuthenticatedSessions";
    private DocumentBuilderFactory domFactory = null;
    private boolean requireAuthenticatedSessions = false;
    private final Map<String, DefaultXmlaServlet.SessionInfo> sessionInfos = new HashMap();

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        this.domFactory = getDocumentBuilderFactory();
        this.requireAuthenticatedSessions = Boolean.parseBoolean(servletConfig.getInitParameter("requireAuthenticatedSessions"));
    }

    protected static DocumentBuilderFactory getDocumentBuilderFactory() {
        DocumentBuilderFactory factory;
        try {
            factory = XmlParserFactoryProducer.createSecureDocBuilderFactory();
        } catch (ParserConfigurationException var2) {
            throw new XmlaException("Server", "00SIEA01", "Servlet initialization error", var2);
        }

        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setNamespaceAware(true);
        return factory;
    }

    protected void unmarshallSoapMessage(HttpServletRequest request, Element[] requestSoapParts) throws XmlaException {
        try {
            ServletInputStream inputStream;
            try {
                inputStream = request.getInputStream();
            } catch (IllegalStateException var11) {
                throw new XmlaException("Server", "00USMA01", "Request input method invoked at illegal time", var11);
            } catch (IOException var12) {
                throw new XmlaException("Server", "00USMA02", "Request input Exception occurred", var12);
            }

            DocumentBuilder domBuilder;
            try {
                domBuilder = this.domFactory.newDocumentBuilder();
            } catch (ParserConfigurationException var10) {
                throw new XmlaException("Server", "00USMB01", "DocumentBuilder cannot be created which satisfies the configuration requested", var10);
            }

            Document soapDoc;
            try {
                soapDoc = domBuilder.parse(new InputSource(inputStream));
            } catch (IOException var8) {
                throw new XmlaException("Server", "00USMC01", "DOM parse IO errors occur", var8);
            } catch (SAXException var9) {
                throw new XmlaException("Client", "00USMC02", "DOM parse errors occur", var9);
            }

            Element envElem = soapDoc.getDocumentElement();
            if (LOGGER.isDebugEnabled()) {
                this.logXmlaRequest(envElem);
            }

            if ("Envelope".equals(envElem.getLocalName())) {
                if (!"http://schemas.xmlsoap.org/soap/envelope/".equals(envElem.getNamespaceURI())) {
                    throw new XmlaException("Client", "00USMC02", "DOM parse errors occur", new SAXException("Invalid SOAP message: Envelope element not in SOAP namespace"));
                } else {
                    Element[] childs = XmlaUtil.filterChildElements(envElem, "http://schemas.xmlsoap.org/soap/envelope/", "Header");
                    if (childs.length > 1) {
                        throw new XmlaException("Client", "00USMC02", "DOM parse errors occur", new SAXException("Invalid SOAP message: More than one Header elements"));
                    } else {
                        requestSoapParts[0] = childs.length == 1 ? childs[0] : null;
                        childs = XmlaUtil.filterChildElements(envElem, "http://schemas.xmlsoap.org/soap/envelope/", "Body");
                        if (childs.length != 1) {
                            throw new XmlaException("Client", "00USMC02", "DOM parse errors occur", new SAXException("Invalid SOAP message: Does not have one Body element"));
                        } else {
                            requestSoapParts[1] = childs[0];
                        }
                    }
                }
            } else {
                throw new XmlaException("Client", "00USMC02", "DOM parse errors occur", new SAXException("Invalid SOAP message: Top element not Envelope"));
            }
        } catch (XmlaException var13) {
            throw var13;
        } catch (Exception var14) {
            throw new XmlaException("Server", "00USMU01", "Unknown error unmarshalling soap message", var14);
        }
    }

    protected void logXmlaRequest(Element envElem) {
        StringWriter writer = new StringWriter();
        writer.write("XML/A request content");
        writer.write(nl);
        XmlaUtil.element2Text(envElem, writer);
        LOGGER.debug(writer.toString());
    }

    protected void handleSoapHeader(HttpServletResponse response, Element[] requestSoapParts, byte[][] responseSoapParts, Map<String, Object> context) throws XmlaException {
        try {
            Element hdrElem = requestSoapParts[0];
            String encoding = response.getCharacterEncoding();
            byte[] bytes = null;
            boolean authenticatedSession = false;
            boolean beginSession = false;
            String sessionIdStr = null;
            if (hdrElem != null && hdrElem.hasChildNodes()) {
                NodeList nlst = hdrElem.getChildNodes();
                int nlen = nlst.getLength();

                for(int i = 0; i < nlen; ++i) {
                    Node n = nlst.item(i);
                    if (n instanceof Element) {
                        Element e = (Element)n;
                        String localName = e.getLocalName();
                        if (localName.equals("Security") && "http://schemas.xmlsoap.org/ws/2002/04/secext".equals(e.getNamespaceURI())) {
                            NodeList childNodes = e.getChildNodes();
                            Element userNameToken = (Element)childNodes.item(1);
                            NodeList userNamePassword = userNameToken.getChildNodes();
                            Element username = (Element)userNamePassword.item(1);
                            Element password = (Element)userNamePassword.item(3);
                            String userNameStr = username.getChildNodes().item(0).getNodeValue();
                            context.put("username", userNameStr);
                            String passwordStr = "";
                            if (password.getChildNodes().item(0) != null) {
                                passwordStr = password.getChildNodes().item(0).getNodeValue();
                            }

                            context.put("password", passwordStr);
                            if ("".equals(passwordStr) || null == passwordStr) {
                                LOGGER.warn("Security header for user [" + userNameStr + "] provided without password");
                            }

                            authenticatedSession = true;
                        } else {
                            Attr attr = e.getAttributeNode("mustUnderstand");
                            if (attr != null) {
                                boolean mustUnderstandValue = attr.getValue() != null && attr.getValue().equals("1");
                                if (!mustUnderstandValue) {
                                    continue;
                                }
                            }

                            if ("urn:schemas-microsoft-com:xml-analysis".equals(e.getNamespaceURI())) {
                                String username;
                                if (localName.equals("BeginSession")) {
                                    sessionIdStr = this.generateSessionId(context);
                                    context.put("session_id", sessionIdStr);
                                    context.put("SessionState", "SessionStateBegin");
                                    Session.create(sessionIdStr);
                                } else if (localName.equals("Session")) {
                                    sessionIdStr = getSessionIdFromRequest(e, context);
                                    Session.get(sessionIdStr);
                                    Session.checkIn(sessionIdStr);
                                    DefaultXmlaServlet.SessionInfo sessionInfo = null;
                                    if (authenticatedSession) {
                                        sessionInfo = this.getSessionInfo(sessionIdStr);
                                    }

                                    if (sessionInfo != null) {
                                        context.put("username", sessionInfo.user);
                                        context.put("password", sessionInfo.password);
                                    }

                                    context.put("session_id", sessionIdStr);
                                    context.put("SessionState", "SessionStateWithin");
                                } else {
                                    if (!localName.equals("EndSession")) {
                                        username = "Invalid XML/A message: Unknown \"mustUnderstand\" XMLA Header element \"" + localName + "\"";
                                        throw new XmlaException("MustUnderstand", "00HSHA01", "SOAP Header must understand element not recognized", new RuntimeException(username));
                                    }

                                    sessionIdStr = getSessionIdFromRequest(e, context);
                                    context.put("session_id", sessionIdStr);
                                    context.put("SessionState", "SessionStateEnd");
                                }

                                if (sessionIdStr != null) {
                                    StringBuilder buf = new StringBuilder(100);
                                    buf.append("<Session ");
                                    buf.append("SessionId");
                                    buf.append("=\"");
                                    buf.append(sessionIdStr);
                                    buf.append("\" ");
                                    buf.append("xmlns=\"");
                                    buf.append("urn:schemas-microsoft-com:xml-analysis");
                                    buf.append("\" />");
                                    bytes = buf.toString().getBytes(encoding);
                                }

                                if (authenticatedSession) {
                                    username = (String)context.get("username");
                                    String password = (String)context.get("password");
                                    String sessionId = (String)context.get("session_id");
                                    LOGGER.debug("New authenticated session; storing credentials [" + username + "/********] for session id [" + sessionId + "]");
                                    this.saveSessionInfo(username, password, sessionId);
                                } else if (beginSession && this.requireAuthenticatedSessions) {
                                    throw new XmlaException("Client", "00CHHA02", "Error in Callback processHttpHeader Authorization", new Exception("Session Credentials NOT PROVIDED"));
                                }
                            }
                        }
                    }
                }
            }

            if (sessionIdStr == null) {
                String lifetimeSessionId = this.generateSessionId(context);
                context.put("session_id", lifetimeSessionId);
                Session.create(lifetimeSessionId);
                context.put("SessionState", "SessionStateEnd");
            }

            responseSoapParts[0] = bytes;
        } catch (XmlaException var24) {
            throw var24;
        } catch (Exception var25) {
            throw new XmlaException("Server", "00HSHU01", "Unknown error handle soap header", var25);
        }
    }

    protected String generateSessionId(Map<String, Object> context) {
        Iterator var2 = this.getCallbacks().iterator();

        String sessionId;
        do {
            if (!var2.hasNext()) {
                return Long.toString(17L * System.nanoTime() + 3L * System.currentTimeMillis(), 35);
            }

            XmlaRequestCallback callback = (XmlaRequestCallback)var2.next();
            sessionId = callback.generateSessionId(context);
        } while(sessionId == null);

        return sessionId;
    }

    private static String getSessionIdFromRequest(Element e, Map<String, Object> context) throws Exception {
        Attr attr = e.getAttributeNode("SessionId");
        if (attr == null) {
            throw new SAXException("Invalid XML/A message: Session Header element with no SessionId attribute");
        } else {
            String sessionId = attr.getValue();
            if (sessionId == null) {
                throw new SAXException("Invalid XML/A message: Session Header element with SessionId attribute but no attribute value");
            } else {
                return sessionId;
            }
        }
    }

    protected void handleSoapBody(HttpServletResponse response, Element[] requestSoapParts, byte[][] responseSoapParts, Map<String, Object> context) throws XmlaException {
        try {
            String encoding = response.getCharacterEncoding();
            Element hdrElem = requestSoapParts[0];
            Element bodyElem = requestSoapParts[1];
            Element[] dreqs = XmlaUtil.filterChildElements(bodyElem, "urn:schemas-microsoft-com:xml-analysis", "Discover");
            Element[] ereqs = XmlaUtil.filterChildElements(bodyElem, "urn:schemas-microsoft-com:xml-analysis", "Execute");
            if (dreqs.length + ereqs.length != 1) {
                throw new XmlaException("Client", "00HSBA01", "SOAP Body not correctly formed", new RuntimeException("Invalid XML/A message: Body has " + dreqs.length + " Discover Requests and " + ereqs.length + " Execute Requests"));
            } else {
                Element xmlaReqElem = dreqs.length == 0 ? ereqs[0] : dreqs[0];
                ByteArrayOutputStream osBuf = new ByteArrayOutputStream();
                String roleName = (String)context.get("role_name");
                String username = (String)context.get("username");
                String password = (String)context.get("password");
                String sessionId = (String)context.get("session_id");
                DefaultXmlaRequest xmlaReq = new DefaultXmlaRequest(xmlaReqElem, roleName, username, password, sessionId);
                String authenticatedUser = (String)context.get("AuthenticatedUser");
                xmlaReq.setAuthenticatedUser(authenticatedUser);
                String[] getAuthenticatedUserGroups = (String[])context.get("AuthenticatedUserGroups");
                xmlaReq.setAuthenticatedUserGroups(getAuthenticatedUserGroups);
                ResponseMimeType responseMimeType = ResponseMimeType.SOAP;
                String responseMimeTypeName = (String)xmlaReq.getProperties().get("ResponseMimeType");
                if (responseMimeTypeName != null) {
                    responseMimeType = (ResponseMimeType)ResponseMimeType.MAP.get(responseMimeTypeName);
                    if (responseMimeType != null) {
                        context.put("language", responseMimeType);
                    }
                }

                DefaultXmlaResponse xmlaRes = new DefaultXmlaResponse(osBuf, encoding, responseMimeType);

                try {
                    this.getXmlaHandler().process(xmlaReq, xmlaRes);
                } catch (XmlaException var23) {
                    throw var23;
                } catch (Exception var24) {
                    throw new XmlaException("Server", "00HSBB01", "XMLA SOAP Body processing error", var24);
                }

                responseSoapParts[1] = osBuf.toByteArray();
            }
        } catch (XmlaException var25) {
            throw var25;
        } catch (Exception var26) {
            throw new XmlaException("Server", "00HSBU01", "Unknown error handle soap body", var26);
        }
    }

    protected void marshallSoapMessage(HttpServletResponse response, byte[][] responseSoapParts, ResponseMimeType responseMimeType) throws XmlaException {
        try {
            String encoding = this.charEncoding != null ? this.charEncoding : response.getCharacterEncoding();
            if (this.charEncoding != null) {
                response.setCharacterEncoding(this.charEncoding);
            }

            switch(responseMimeType) {
                case JSON:
                    response.setContentType("application/json");
                    break;
                case SOAP:
                default:
                    response.setContentType("text/xml");
            }

            OutputStream outputStream = response.getOutputStream();
            byte[] soapHeader = responseSoapParts[0];
            byte[] soapBody = responseSoapParts[1];
            Object[] byteChunks = null;

            try {
                switch(responseMimeType) {
                    case JSON:
                        byteChunks = new Object[]{soapBody};
                        break;
                    case SOAP:
                    default:
                        String s0 = "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n<" + "SOAP-ENV" + ":Envelope xmlns:" + "SOAP-ENV" + "=\"" + "http://schemas.xmlsoap.org/soap/envelope/" + "\" " + "SOAP-ENV" + ":encodingStyle=\"" + "http://schemas.xmlsoap.org/soap/encoding/" + "\" >\n<" + "SOAP-ENV" + ":Header>\n";
                        String s2 = "</SOAP-ENV:Header>\n<SOAP-ENV:Body>\n";
                        String s4 = "\n</SOAP-ENV:Body>\n</SOAP-ENV:Envelope>\n";
                        byteChunks = new Object[]{s0.getBytes(encoding), soapHeader, s2.getBytes(encoding), soapBody, s4.getBytes(encoding)};
                }
            } catch (UnsupportedEncodingException var19) {
                LOGGER.warn("This should be handled at begin of processing request", var19);
            }

            StringBuilder buf;
            if (LOGGER.isDebugEnabled()) {
                buf = new StringBuilder(100);
                buf.append("XML/A response content").append(nl);

                try {
                    Object[] var26 = byteChunks;
                    int var28 = byteChunks.length;

                    for(int var12 = 0; var12 < var28; ++var12) {
                        Object byteChunk = var26[var12];
                        byte[] chunk = (byte[])byteChunk;
                        if (chunk != null && chunk.length > 0) {
                            buf.append(new String(chunk, encoding));
                        }
                    }
                } catch (UnsupportedEncodingException var21) {
                    LOGGER.warn("This should be handled at begin of processing request", var21);
                }

                LOGGER.debug(buf.toString());
            }

            if (LOGGER.isDebugEnabled()) {
                buf = new StringBuilder();
                buf.append("XML/A response content").append(nl);
            }

            try {
                int bufferSize = 4096;
                ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
                WritableByteChannel wch = Channels.newChannel(outputStream);
                Object[] var31 = byteChunks;
                int var32 = byteChunks.length;

                for(int var15 = 0; var15 < var32; ++var15) {
                    Object byteChunk = var31[var15];
                    if (byteChunk != null && ((byte[])byteChunk).length != 0) {
                        ReadableByteChannel rch = Channels.newChannel(new ByteArrayInputStream((byte[])byteChunk));

                        int readSize;
                        do {
                            buffer.clear();
                            readSize = rch.read(buffer);
                            buffer.flip();
                            int writeSize = 0;

                            while((writeSize += wch.write(buffer)) < readSize) {
                            }
                        } while(readSize == bufferSize);

                        rch.close();
                    }
                }

                outputStream.flush();
            } catch (IOException var20) {
                LOGGER.warn("Exception when transferring bytes over sockets", var20);
            }

        } catch (XmlaException var22) {
            throw var22;
        } catch (Exception var23) {
            throw new XmlaException("Server", "00MSMU01", "Unknown error marshalling soap message", var23);
        }
    }

    protected void handleFault(HttpServletResponse response, byte[][] responseSoapParts, Phase phase, Throwable t) {
        switch(phase) {
            case VALIDATE_HTTP_HEAD:
                response.setStatus(401);
                break;
            case INITIAL_PARSE:
            case CALLBACK_PRE_ACTION:
            case PROCESS_HEADER:
            case PROCESS_BODY:
            case CALLBACK_POST_ACTION:
            case SEND_RESPONSE:
                response.setStatus(200);
        }

        String code;
        String faultCode;
        String faultString;
        String detail;
        if (t instanceof XmlaException) {
            XmlaException xex = (XmlaException)t;
            code = xex.getCode();
            faultString = xex.getFaultString() + " " + xex.getDetail();
            faultCode = XmlaException.formatFaultCode(xex);
            detail = XmlaException.formatDetail(xex.getDetail());
        } else {
            t = XmlaException.getRootCause(t);
            code = "00UE001";
            faultString = "Internal Error " + t.getMessage();
            faultCode = XmlaException.formatFaultCode("Server", code);
            detail = XmlaException.formatDetail(t.getMessage());
        }

        String encoding = response.getCharacterEncoding();
        ByteArrayOutputStream osBuf = new ByteArrayOutputStream();

        try {
            SaxWriter writer = new DefaultSaxWriter(osBuf, encoding);
            writer.startDocument();
            writer.startElement("SOAP-ENV:Fault");
            writer.startElement("faultcode");
            writer.characters(faultCode);
            writer.endElement();
            writer.startElement("faultstring");
            writer.characters(faultString);
            writer.endElement();
            writer.startElement("faultactor");
            writer.characters("Mondrian");
            writer.endElement();
            byte var13 = -1;
            switch(code.hashCode()) {
                case -453960944:
                    if (code.equals("00HSBE02")) {
                        var13 = 0;
                    }
                    break;
                case -453766823:
                    if (code.equals("00HSHU01")) {
                        var13 = 1;
                    }
                    break;
                case -244240166:
                    if (code.equals("0xc10c000a")) {
                        var13 = 2;
                    }
            }

            switch(var13) {
                case 0:
                case 1:
                case 2:
                    code = "3238789130";
                    break;
                default:
                    code = "3238658121";
            }

            writer.startElement("detail");
            writer.startElement("Error", new Object[]{"ErrorCode", code, "Description", detail});
            writer.endElement();
            writer.endElement();
            writer.endElement();
            writer.endDocument();
        } catch (UnsupportedEncodingException var14) {
            LOGGER.warn("This should be handled at begin of processing request", var14);
        } catch (Exception var15) {
            LOGGER.error("Unexcepted runimt exception when handing SOAP fault :(");
        }

        responseSoapParts[1] = osBuf.toByteArray();
    }

    private DefaultXmlaServlet.SessionInfo getSessionInfo(String sessionId) {
        if (sessionId == null) {
            return null;
        } else {
            DefaultXmlaServlet.SessionInfo sessionInfo = null;
            synchronized(this.sessionInfos) {
                sessionInfo = (DefaultXmlaServlet.SessionInfo)this.sessionInfos.get(sessionId);
            }

            if (sessionInfo == null) {
                LOGGER.error("No login credentials for found for session [" + sessionId + "]");
            } else {
                LOGGER.debug("Found credentials for session id [" + sessionId + "], username=[" + sessionInfo.user + "] in servlet cache");
            }

            return sessionInfo;
        }
    }

    private DefaultXmlaServlet.SessionInfo saveSessionInfo(String username, String password, String sessionId) {
        synchronized(this.sessionInfos) {
            DefaultXmlaServlet.SessionInfo sessionInfo = (DefaultXmlaServlet.SessionInfo)this.sessionInfos.get(sessionId);
            if (sessionInfo != null && Olap4jUtil.equal(sessionInfo.user, username)) {
                if (password != null && password.length() > 0) {
                    sessionInfo = new DefaultXmlaServlet.SessionInfo(username, password);
                    this.sessionInfos.put(sessionId, sessionInfo);
                }
            } else {
                sessionInfo = new DefaultXmlaServlet.SessionInfo(username, password);
                this.sessionInfos.put(sessionId, sessionInfo);
            }

            return sessionInfo;
        }
    }

    private static class SessionInfo {
        final String user;
        final String password;

        public SessionInfo(String user, String password) {
            this.user = user;
            this.password = password;
        }
    }
}