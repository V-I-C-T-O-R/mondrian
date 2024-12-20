/* Decompiler 110ms, total 666ms, lines 424 */
package mondrian.xmla.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mondrian.olap.Util;
import mondrian.xmla.ServerObject;
import mondrian.xmla.XmlaConstants;
import mondrian.xmla.XmlaException;
import mondrian.xmla.XmlaRequest;
import mondrian.xmla.XmlaUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.olap4j.metadata.XmlaConstants.Literal;
import org.olap4j.metadata.XmlaConstants.Method;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DefaultXmlaRequest implements XmlaRequest, XmlaConstants {
    private static final Logger LOGGER = LogManager.getLogger(DefaultXmlaRequest.class);
    private static final String MSG_INVALID_XMLA = "Invalid XML/A message";
    private Method method;
    private Map<String, String> properties;
    private final String roleName;
    private String statement;
    private boolean drillthrough;
    private String command;
    private Map<String, String> parameters = Collections.unmodifiableMap(new HashMap());
    private ServerObject serverObject;
    private String objectDefinition;
    private String requestType;
    private Map<String, Object> restrictions;
    private final String username;
    private final String password;
    private final String sessionId;
    private String authenticatedUser = null;
    private String[] authenticatedUserGroups = null;

    public DefaultXmlaRequest(Element xmlaRoot, String roleName, String username, String password, String sessionId) throws XmlaException {
        this.init(xmlaRoot);
        this.roleName = roleName;
        this.username = username;
        this.password = password;
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public Method getMethod() {
        return this.method;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public Map<String, Object> getRestrictions() {
        if (this.method != Method.DISCOVER) {
            throw new IllegalStateException("Only METHOD_DISCOVER has restrictions");
        } else {
            return this.restrictions;
        }
    }

    public String getStatement() {
        if (this.method != Method.EXECUTE) {
            throw new IllegalStateException("Only METHOD_EXECUTE has statement");
        } else {
            return this.statement;
        }
    }

    public String getRoleName() {
        return this.roleName;
    }

    public String getCommand() {
        return this.command;
    }

    public String getRequestType() {
        if (this.method != Method.DISCOVER) {
            throw new IllegalStateException("Only METHOD_DISCOVER has requestType");
        } else {
            return this.requestType;
        }
    }

    public boolean isDrillThrough() {
        if (this.method != Method.EXECUTE) {
            throw new IllegalStateException("Only METHOD_EXECUTE determines drillthrough");
        } else {
            return this.drillthrough;
        }
    }

    public ServerObject getServerObject() {
        return this.serverObject;
    }

    public String getObjectDefinition() {
        return this.objectDefinition;
    }

    protected final void init(Element xmlaRoot) throws XmlaException {
        if ("urn:schemas-microsoft-com:xml-analysis".equals(xmlaRoot.getNamespaceURI())) {
            String lname = xmlaRoot.getLocalName();
            if ("Discover".equals(lname)) {
                this.method = Method.DISCOVER;
                this.initDiscover(xmlaRoot);
            } else {
                if (!"Execute".equals(lname)) {
                    StringBuilder buf = new StringBuilder(100);
                    buf.append("Invalid XML/A message");
                    buf.append(": Bad method name \"");
                    buf.append(lname);
                    buf.append("\"");
                    throw new XmlaException("Client", "00HSBB02", "XMLA SOAP bad method", Util.newError(buf.toString()));
                }

                this.method = Method.EXECUTE;
                this.initExecute(xmlaRoot);
            }

        } else {
            StringBuilder buf = new StringBuilder(100);
            buf.append("Invalid XML/A message");
            buf.append(": Bad namespace url \"");
            buf.append(xmlaRoot.getNamespaceURI());
            buf.append("\"");
            throw new XmlaException("Client", "00HSBB03", "XMLA SOAP bad method namespace", Util.newError(buf.toString()));
        }
    }

    private void initDiscover(Element discoverRoot) throws XmlaException {
        Element[] childElems = XmlaUtil.filterChildElements(discoverRoot, "urn:schemas-microsoft-com:xml-analysis", "RequestType");
        StringBuilder buf;
        if (childElems.length != 1) {
            buf = new StringBuilder(100);
            buf.append("Invalid XML/A message");
            buf.append(": Wrong number of RequestType elements: ");
            buf.append(childElems.length);
            throw new XmlaException("Client", "00HSBB04", "XMLA SOAP bad Discover RequestType element", Util.newError(buf.toString()));
        } else {
            this.requestType = XmlaUtil.textInElement(childElems[0]);
            childElems = XmlaUtil.filterChildElements(discoverRoot, "urn:schemas-microsoft-com:xml-analysis", "Properties");
            if (childElems.length != 1) {
                buf = new StringBuilder(100);
                buf.append("Invalid XML/A message");
                buf.append(": Wrong number of Properties elements: ");
                buf.append(childElems.length);
                throw new XmlaException("Client", "00HSBB06", "XMLA SOAP bad Discover or Execute Properties element", Util.newError(buf.toString()));
            } else {
                this.initProperties(childElems[0]);
                childElems = XmlaUtil.filterChildElements(discoverRoot, "urn:schemas-microsoft-com:xml-analysis", "Restrictions");
                if (childElems.length != 1) {
                    buf = new StringBuilder(100);
                    buf.append("Invalid XML/A message");
                    buf.append(": Wrong number of Restrictions elements: ");
                    buf.append(childElems.length);
                    throw new XmlaException("Client", "00HSBB05", "XMLA SOAP bad Discover Restrictions element", Util.newError(buf.toString()));
                } else {
                    this.initRestrictions(childElems[0]);
                }
            }
        }
    }

    private void initExecute(Element executeRoot) throws XmlaException {
        Element[] childElems = XmlaUtil.filterChildElements(executeRoot, "urn:schemas-microsoft-com:xml-analysis", "Command");
        StringBuilder buf;
        if (childElems.length != 1) {
            buf = new StringBuilder(100);
            buf.append("Invalid XML/A message");
            buf.append(": Wrong number of Command elements: ");
            buf.append(childElems.length);
            throw new XmlaException("Client", "00HSBB07", "XMLA SOAP bad Execute Command element", Util.newError(buf.toString()));
        } else {
            this.initCommand(childElems[0]);
            childElems = XmlaUtil.filterChildElements(executeRoot, "urn:schemas-microsoft-com:xml-analysis", "Properties");
            if (childElems.length != 1) {
                buf = new StringBuilder(100);
                buf.append("Invalid XML/A message");
                buf.append(": Wrong number of Properties elements: ");
                buf.append(childElems.length);
                throw new XmlaException("Client", "00HSBB06", "XMLA SOAP bad Discover or Execute Properties element", Util.newError(buf.toString()));
            } else {
                this.initProperties(childElems[0]);
                childElems = XmlaUtil.filterChildElements(executeRoot, "urn:schemas-microsoft-com:xml-analysis", "Parameters");
                if (childElems.length > 0) {
                    this.initParameters(childElems[0]);
                }

            }
        }
    }

    private void initRestrictions(Element restrictionsRoot) throws XmlaException {
        Map<String, List<String>> restrictions = new HashMap();
        Element[] childElems = XmlaUtil.filterChildElements(restrictionsRoot, "urn:schemas-microsoft-com:xml-analysis", "RestrictionList");
        if (childElems.length == 1) {
            NodeList nlst = childElems[0].getChildNodes();
            int i = 0;

            for(int nlen = nlst.getLength(); i < nlen; ++i) {
                Node n = nlst.item(i);
                if (n instanceof Element) {
                    Element e = (Element)n;
                    if ("urn:schemas-microsoft-com:xml-analysis".equals(e.getNamespaceURI())) {
                        String key = e.getLocalName();
                        List values;
                        if (restrictions.containsKey(key)) {
                            values = (List)restrictions.get(key);
                        } else {
                            values = new ArrayList();
                            restrictions.put(key, values);
                        }

                        NodeList propertyValues = e.getChildNodes();
                        int j = 0;

                        for(int pvlen = propertyValues.getLength(); j < pvlen; ++j) {
                            String value = "";
                            Node vn = propertyValues.item(j);
                            if (vn instanceof Element) {
                                Element ve = (Element)vn;
                                value = XmlaUtil.textInElement(ve);
                            } else {
                                value = XmlaUtil.textInElement(e);
                            }

                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("DefaultXmlaRequest.initRestrictions:  key=\"" + key + "\", value=\"" + value + "\"");
                            }

                            ((List)values).add(value);
                        }
                    }
                }
            }
        } else if (childElems.length > 1) {
            StringBuilder buf = new StringBuilder(100);
            buf.append("Invalid XML/A message");
            buf.append(": Wrong number of RestrictionList elements: ");
            buf.append(childElems.length);
            throw new XmlaException("Client", "00HSBB08", "XMLA SOAP too many Discover RestrictionList element", Util.newError(buf.toString()));
        }

        String key = Literal.CATALOG_NAME.name();
        if (this.properties.containsKey(key) && !restrictions.containsKey(key)) {
            List<String> values = new ArrayList();
            restrictions.put((String)this.properties.get(key), values);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("DefaultXmlaRequest.initRestrictions:  key=\"" + key + "\", value=\"" + (String)this.properties.get(key) + "\"");
            }
        }

        this.restrictions = Collections.unmodifiableMap(restrictions);
    }

    private void initProperties(Element propertiesRoot) throws XmlaException {
        Map<String, String> properties = new HashMap();
        Element[] childElems = XmlaUtil.filterChildElements(propertiesRoot, "urn:schemas-microsoft-com:xml-analysis", "PropertyList");
        if (childElems.length == 1) {
            NodeList nlst = childElems[0].getChildNodes();
            int i = 0;

            for(int nlen = nlst.getLength(); i < nlen; ++i) {
                Node n = nlst.item(i);
                if (n instanceof Element) {
                    Element e = (Element)n;
                    if ("urn:schemas-microsoft-com:xml-analysis".equals(e.getNamespaceURI())) {
                        String key = e.getLocalName();
                        String value = XmlaUtil.textInElement(e);
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("DefaultXmlaRequest.initProperties:  key=\"" + key + "\", value=\"" + value + "\"");
                        }

                        properties.put(key, value);
                    }
                }
            }
        } else if (childElems.length > 1) {
            StringBuilder buf = new StringBuilder(100);
            buf.append("Invalid XML/A message");
            buf.append(": Wrong number of PropertyList elements: ");
            buf.append(childElems.length);
            throw new XmlaException("Client", "00HSBB09", "XMLA SOAP bad Discover or Execute PropertyList element", Util.newError(buf.toString()));
        }

        this.properties = Collections.unmodifiableMap(properties);
    }

    private void initParameters(Element parameterElement) throws XmlaException {
        Map<String, String> parameters = new HashMap();
        NodeList nlst = parameterElement.getChildNodes();
        int i = 0;

        for(int nlen = nlst.getLength(); i < nlen; ++i) {
            Node n = nlst.item(i);
            if (n instanceof Element) {
                String name = null;
                Element[] nameElems = XmlaUtil.filterChildElements((Element)n, "urn:schemas-microsoft-com:xml-analysis", "Name");
                if (nameElems.length > 0) {
                    name = XmlaUtil.textInElement(nameElems[0]);
                    String value = null;
                    Element[] valueElems = XmlaUtil.filterChildElements((Element)n, "urn:schemas-microsoft-com:xml-analysis", "Value");
                    if (nameElems.length > 0) {
                        value = XmlaUtil.textInElement(valueElems[0]);
                    }

                    parameters.put(name, value);
                }
            }
        }

        this.parameters = Collections.unmodifiableMap(parameters);
    }

    private void initCommand(Element commandRoot) throws XmlaException {
        Element[] commandElements = XmlaUtil.filterChildElements(commandRoot, (String)null, (String)null);
        StringBuilder buf;
        if (commandElements.length != 1) {
            buf = new StringBuilder(100);
            buf.append("Invalid XML/A message");
            buf.append(": Wrong number of Command children elements: ");
            buf.append(commandElements.length);
            throw new XmlaException("Client", "00HSBB07", "XMLA SOAP bad Execute Command element", Util.newError(buf.toString()));
        } else {
            this.command = commandElements[0].getLocalName();
            if (this.command != null && this.command.toUpperCase().equals("STATEMENT")) {
                this.statement = XmlaUtil.textInElement(commandElements[0]).replaceAll("\\r", "");
                this.drillthrough = this.statement.toUpperCase().indexOf("DRILLTHROUGH") != -1;
            } else if (this.command != null && this.command.toUpperCase().equals("ALTER")) {
                Element[] objectElements = XmlaUtil.filterChildElements(commandElements[0], (String)null, "Object");
                if (objectElements.length > 1) {
                    buf = new StringBuilder(100);
                    buf.append("Invalid XML/A message");
                    buf.append(": Wrong number of Objects elements: ");
                    buf.append(objectElements.length);
                    throw new XmlaException("Client", "00HSBB09", "XMLA SOAP bad Discover or Execute PropertyList element", Util.newError(buf.toString()));
                }

                if (objectElements.length > 0) {
                    String databaseId = null;
                    Element[] databaseIdElements = XmlaUtil.filterChildElements(objectElements[0], (String)null, "DatabaseID");
                    if (databaseIdElements.length > 1) {
                        buf = new StringBuilder(100);
                        buf.append("Invalid XML/A message");
                        buf.append(": Wrong number of DatabaseID elements: ");
                        buf.append(databaseIdElements.length);
                        throw new XmlaException("Client", "00HSBB09", "XMLA SOAP bad Discover or Execute PropertyList element", Util.newError(buf.toString()));
                    }

                    if (databaseIdElements.length > 0) {
                        databaseId = XmlaUtil.textInElement(databaseIdElements[0]).replaceAll("\\r", "");
                    }

                    this.serverObject = new ServerObject(databaseId);
                }

                Element[] objectDefinitionElements = XmlaUtil.filterChildElements(commandElements[0], (String)null, "ObjectDefinition");
                if (objectDefinitionElements.length > 1) {
                    buf = new StringBuilder(100);
                    buf.append("Invalid XML/A message");
                    buf.append(": Wrong number of ObjectDefinition elements: ");
                    buf.append(objectDefinitionElements.length);
                    throw new XmlaException("Client", "00HSBB09", "XMLA SOAP bad Discover or Execute PropertyList element", Util.newError(buf.toString()));
                }

                if (objectDefinitionElements.length > 0) {
                    this.objectDefinition = XmlaUtil.textInElement(objectDefinitionElements[0]);
                }
            } else if (this.command == null || !this.command.toUpperCase().equals("CANCEL")) {
                buf = new StringBuilder(100);
                buf.append("Invalid XML/A message");
                buf.append(": Wrong child of Command elements: ");
                buf.append(this.command);
                throw new XmlaException("Client", "00HSBB07", "XMLA SOAP bad Execute Command element", Util.newError(buf.toString()));
            }

        }
    }

    public void setProperty(String key, String value) {
        HashMap<String, String> newProperties = new HashMap(this.properties);
        newProperties.put(key, value);
        this.properties = Collections.unmodifiableMap(newProperties);
    }

    public void setAuthenticatedUser(String authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

    public String getAuthenticatedUser() {
        return this.authenticatedUser;
    }

    public void setAuthenticatedUserGroups(String[] authenticatedUserGroups) {
        this.authenticatedUserGroups = authenticatedUserGroups;
    }

    public String[] getAuthenticatedUserGroups() {
        return this.authenticatedUserGroups;
    }
}