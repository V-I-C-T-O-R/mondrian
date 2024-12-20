/* Decompiler 1193ms, total 1812ms, lines 395 */
package mondrian.xmla;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import mondrian.olap.MondrianException;
import mondrian.olap.Util;
import mondrian.util.XmlParserFactoryProducer;
import mondrian.xmla.Enumeration.ResponseMimeType;
import mondrian.xmla.Rowset.Row;
import mondrian.xmla.RowsetDefinition.Column;
import mondrian.xmla.RowsetDefinition.Type;
import mondrian.xmla.XmlaHandler.ConnectionFactory;
import mondrian.xmla.impl.DefaultXmlaResponse;
import org.olap4j.OlapConnection;
import org.olap4j.OlapException;
import org.olap4j.impl.LcidLocale;
import org.olap4j.metadata.XmlaConstants.Format;
import org.olap4j.metadata.XmlaConstants.Method;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

public class XmlaUtil implements XmlaConstants {
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final String validCharactersExp = "^[:A-Z_a-zÀÖØ-öø-˿Ͱ-ͽ\u037f-\u1fff\u200c\u200d⁰-\u218fⰀ-\u2fef、-\ud7ff豈-\ufdcfﷰ-�][:A-Z_a-zÀÖØ-öø-˿Ͱ-ͽ\u037f-\u1fff\u200c\u200d⁰-\u218fⰀ-\u2fef、-\udfff豈-\ufdcfﷰ-�\\-\\.0-9·̀-ͯ‿-⁀]*\\Z";
    private static Pattern validCharactersPatern = Pattern.compile("^[:A-Z_a-zÀÖØ-öø-˿Ͱ-ͽ\u037f-\u1fff\u200c\u200d⁰-\u218fⰀ-\u2fef、-\ud7ff豈-\ufdcfﷰ-�][:A-Z_a-zÀÖØ-öø-˿Ͱ-ͽ\u037f-\u1fff\u200c\u200d⁰-\u218fⰀ-\u2fef、-\udfff豈-\ufdcfﷰ-�\\-\\.0-9·̀-ͯ‿-⁀]*\\Z");

    private static String encodeChar(char c) {
        StringBuilder buf = new StringBuilder();
        buf.append("_x");
        String str = Integer.toHexString(c);

        for(int i = 4 - str.length(); i > 0; --i) {
            buf.append("0");
        }

        return buf.append(str).append("_").toString();
    }

    private static String encodeElementName(String name) {
        StringBuilder buf = new StringBuilder();
        char[] nameChars = name.toCharArray();
        char[] var3 = nameChars;
        int var4 = nameChars.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            char ch = var3[var5];
            boolean b = validCharactersPatern.matcher("" + ch).matches();
            if (b) {
                buf.append(ch);
            } else {
                buf.append(encodeChar(ch));
            }
        }

        return buf.toString();
    }

    public static void element2Text(Element elem, StringWriter writer) throws XmlaException {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(new DOMSource(elem), new StreamResult(writer));
        } catch (Exception var4) {
            throw new XmlaException("Client", "00USMC02", "DOM parse errors occur", var4);
        }
    }

    public static Element text2Element(String text) throws XmlaException {
        return _2Element(new InputSource(new StringReader(text)));
    }

    public static Element stream2Element(InputStream stream) throws XmlaException {
        return _2Element(new InputSource(stream));
    }

    private static Element _2Element(InputSource source) throws XmlaException {
        try {
            DocumentBuilderFactory factory = XmlParserFactoryProducer.createSecureDocBuilderFactory();
            factory.setIgnoringElementContentWhitespace(true);
            factory.setIgnoringComments(true);
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(source);
            return doc.getDocumentElement();
        } catch (Exception var4) {
            throw new XmlaException("Client", "00USMC02", "DOM parse errors occur", var4);
        }
    }

    public static Element[] filterChildElements(Element parent, String ns, String lname) {
        List<Element> elems = new ArrayList();
        NodeList nlst = parent.getChildNodes();
        int i = 0;

        for(int nlen = nlst.getLength(); i < nlen; ++i) {
            Node n = nlst.item(i);
            if (n instanceof Element) {
                Element e = (Element)n;
                if ((ns == null || ns.equals(e.getNamespaceURI())) && (lname == null || lname.equals(e.getLocalName()))) {
                    elems.add(e);
                }
            }
        }

        return (Element[])elems.toArray(new Element[elems.size()]);
    }

    public static String textInElement(Element elem) {
        StringBuilder buf = new StringBuilder(100);
        elem.normalize();
        NodeList nlst = elem.getChildNodes();
        int i = 0;

        for(int nlen = nlst.getLength(); i < nlen; ++i) {
            Node n = nlst.item(i);
            if (n instanceof Text) {
                String data = ((Text)n).getData();
                buf.append(data);
            }
        }

        return buf.toString();
    }

    public static Throwable rootThrowable(Throwable throwable) {
        Throwable rootThrowable = throwable.getCause();
        return rootThrowable != null && rootThrowable instanceof MondrianException ? rootThrowable(rootThrowable) : throwable;
    }

    public static String normalizeNumericString(String numericStr) {
        int index = numericStr.indexOf(46);
        if (index > 0) {
            if (numericStr.indexOf(101) != -1) {
                return numericStr;
            }

            if (numericStr.indexOf(69) != -1) {
                return numericStr;
            }

            boolean found = false;
            int p = numericStr.length();

            char c;
            for(c = numericStr.charAt(p - 1); c == '0'; c = numericStr.charAt(p - 1)) {
                found = true;
                --p;
            }

            if (c == '.') {
                --p;
            }

            if (found) {
                return numericStr.substring(0, p);
            }
        }

        return numericStr;
    }

    public static XmlaUtil.MetadataRowset getMetadataRowset(final OlapConnection connection, String methodName, final Map<String, Object> restrictionMap) throws OlapException {
        RowsetDefinition rowsetDefinition = RowsetDefinition.valueOf(methodName);
        ConnectionFactory connectionFactory = new ConnectionFactory() {
            public OlapConnection getConnection(String catalog, String schema, String roleName, Properties props) throws SQLException {
                return connection;
            }

            public Map<String, Object> getPreConfiguredDiscoverDatasourcesResponse() {
                return null;
            }
        };
        XmlaRequest request = new XmlaRequest() {
            public Method getMethod() {
                return Method.DISCOVER;
            }

            public Map<String, String> getProperties() {
                return Collections.emptyMap();
            }

            public Map<String, Object> getRestrictions() {
                return restrictionMap;
            }

            public String getStatement() {
                return null;
            }

            public String getRoleName() {
                return null;
            }

            public String getRequestType() {
                throw new UnsupportedOperationException();
            }

            public boolean isDrillThrough() {
                throw new UnsupportedOperationException();
            }

            public Format getFormat() {
                throw new UnsupportedOperationException();
            }

            public String getUsername() {
                return null;
            }

            public String getPassword() {
                return null;
            }

            public String getSessionId() {
                return null;
            }

            public String getAuthenticatedUser() {
                return null;
            }

            public String[] getAuthenticatedUserGroups() {
                return null;
            }
        };
        Rowset rowset = rowsetDefinition.getRowset(request, new XmlaHandler(connectionFactory, "xmla") {
            public OlapConnection getConnection(XmlaRequest request, Map<String, String> propMap) {
                return connection;
            }
        });
        List<Row> rowList = new ArrayList();
        rowset.populate(new DefaultXmlaResponse(new ByteArrayOutputStream(), Charset.defaultCharset().name(), ResponseMimeType.SOAP), connection, rowList);
        XmlaUtil.MetadataRowset result = new XmlaUtil.MetadataRowset();
        List<Column> colDefs = new ArrayList();
        Column[] var10 = rowsetDefinition.columnDefinitions;
        int var11 = var10.length;

        for(int var12 = 0; var12 < var11; ++var12) {
            Column columnDefinition = var10[var12];
            if (columnDefinition.type != Type.Rowset) {
                colDefs.add(columnDefinition);
            }
        }

        Iterator var17 = rowList.iterator();

        while(var17.hasNext()) {
            Row row = (Row)var17.next();
            Object[] values = new Object[colDefs.size()];
            int k = -1;

            Object o;
            for(Iterator var14 = colDefs.iterator(); var14.hasNext(); values[k] = o) {
                Column colDef = (Column)var14.next();
                o = row.get(colDef.name);
                if (o instanceof List) {
                    o = toString((List)o);
                } else if (o instanceof String[]) {
                    o = toString(Arrays.asList((String[])o));
                }

                ++k;
            }

            result.rowList.add(Arrays.asList(values));
        }

        String columnName;
        for(var17 = colDefs.iterator(); var17.hasNext(); result.headerList.add(columnName)) {
            Column colDef = (Column)var17.next();
            columnName = colDef.name;
            if (LOWERCASE_PATTERN.matcher(columnName).matches()) {
                columnName = Util.camelToUpper(columnName);
            }

            if (columnName.equals("VALUE")) {
                columnName = "PROPERTY_VALUE";
            }
        }

        return result;
    }

    private static <T> String toString(List<T> list) {
        StringBuilder buf = new StringBuilder();
        int k = -1;

        Object t;
        for(Iterator var3 = list.iterator(); var3.hasNext(); buf.append(t)) {
            t = var3.next();
            ++k;
            if (k > 0) {
                buf.append(", ");
            }
        }

        return buf.toString();
    }

    public static ResponseMimeType chooseResponseMimeType(String accept) {
        String[] var1 = accept.split(",");
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            String s = var1[var3];
            s = s.trim();
            int semicolon = s.indexOf(";");
            if (semicolon >= 0) {
                s = s.substring(0, semicolon);
            }

            ResponseMimeType mimeType = (ResponseMimeType)ResponseMimeType.MAP.get(s);
            if (mimeType != null) {
                return mimeType;
            }
        }

        return null;
    }

    public static boolean shouldEmitInvisibleMembers(XmlaRequest request) {
        String value = (String)request.getProperties().get(PropertyDefinition.EmitInvisibleMembers.name());
        return Boolean.parseBoolean(value);
    }

    public static Locale convertToLocale(String value) {
        if (value != null) {
            try {
                short lcid = Short.valueOf(value);
                return LcidLocale.lcidToLocale(lcid);
            } catch (NumberFormatException var4) {
                try {
                    return Util.parseLocale(value);
                } catch (RuntimeException var3) {
                }
            } catch (RuntimeException var5) {
            }
        }

        return null;
    }

    public static class ElementNameEncoder {
        private final Map<String, String> map = new ConcurrentHashMap();
        public static final XmlaUtil.ElementNameEncoder INSTANCE = new XmlaUtil.ElementNameEncoder();

        public String encode(String name) {
            String encoded = (String)this.map.get(name);
            if (encoded == null) {
                encoded = XmlaUtil.encodeElementName(name);
                this.map.put(name, encoded);
            }

            return encoded;
        }
    }

    public static class Wildcard {
        public final String pattern;

        public Wildcard(String pattern) {
            this.pattern = pattern;
        }
    }

    public static class MetadataRowset {
        public final List<String> headerList = new ArrayList();
        public final List<List<Object>> rowList = new ArrayList();
    }
}