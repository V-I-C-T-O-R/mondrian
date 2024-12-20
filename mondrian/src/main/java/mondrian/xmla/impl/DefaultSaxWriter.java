/* Decompiler 265ms, total 1226ms, lines 320 */
package mondrian.xmla.impl;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.regex.Pattern;
import mondrian.util.ArrayStack;
import mondrian.xmla.SaxWriter;
import org.eigenbase.xom.XMLUtil;
import org.eigenbase.xom.XOMUtil;
import org.xml.sax.Attributes;

public class DefaultSaxWriter implements SaxWriter {
    private static final int STATE_IN_TAG = 0;
    private static final int STATE_END_ELEMENT = 1;
    private static final int STATE_AFTER_TAG = 2;
    private static final int STATE_CHARACTERS = 3;
    private final PrintWriter writer;
    private int indent;
    private String indentStr;
    private final ArrayStack<String> stack;
    private int state;
    private static final Pattern nlPattern = Pattern.compile("\\r\\n|\\r|\\n");
    private static final Attributes EmptyAttributes = new Attributes() {
        public int getLength() {
            return 0;
        }

        public String getURI(int index) {
            return null;
        }

        public String getLocalName(int index) {
            return null;
        }

        public String getQName(int index) {
            return null;
        }

        public String getType(int index) {
            return null;
        }

        public String getValue(int index) {
            return null;
        }

        public int getIndex(String uri, String localName) {
            return 0;
        }

        public int getIndex(String qName) {
            return 0;
        }

        public String getType(String uri, String localName) {
            return null;
        }

        public String getType(String qName) {
            return null;
        }

        public String getValue(String uri, String localName) {
            return null;
        }

        public String getValue(String qName) {
            return null;
        }
    };

    public DefaultSaxWriter(OutputStream stream) {
        this((Writer)(new OutputStreamWriter(stream)));
    }

    public DefaultSaxWriter(OutputStream stream, String xmlEncoding) throws UnsupportedEncodingException {
        this((Writer)(new OutputStreamWriter(stream, xmlEncoding)));
    }

    public DefaultSaxWriter(Writer writer) {
        this(new PrintWriter(writer), 0);
    }

    public DefaultSaxWriter(PrintWriter writer, int initialIndent) {
        this.indentStr = "  ";
        this.stack = new ArrayStack();
        this.state = 1;
        this.writer = writer;
        this.indent = initialIndent;
    }

    private void _startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        this._checkTag();
        if (this.indent > 0) {
            this.writer.println();
        }

        int i;
        for(i = 0; i < this.indent; ++i) {
            this.writer.write(this.indentStr);
        }

        ++this.indent;
        this.writer.write(60);
        this.writer.write(qName);

        for(i = 0; i < atts.getLength(); ++i) {
            XMLUtil.printAtt(this.writer, atts.getQName(i), atts.getValue(i));
        }

        this.state = 0;
    }

    private void _checkTag() {
        if (this.state == 0) {
            this.state = 2;
            this.writer.print(">");
        }

    }

    private void _endElement(String namespaceURI, String localName, String qName) {
        --this.indent;
        if (this.state == 0) {
            this.writer.write("/>");
        } else {
            if (this.state != 3) {
                this.writer.println();

                for(int i = 0; i < this.indent; ++i) {
                    this.writer.write(this.indentStr);
                }
            }

            this.writer.write("</");
            this.writer.write(qName);
            this.writer.write(62);
        }

        this.state = 1;
    }

    private void _characters(char[] ch, int start, int length) {
        this._checkTag();
        String s = new String(ch, start, length);
        if (XOMUtil.stringHasXMLSpecials(s)) {
            XMLUtil.stringEncodeXML(s, this.writer);
        } else {
            this.writer.print(s);
        }

        this.state = 3;
    }

    public void characters(String s) {
        if (s != null && s.length() > 0) {
            this._characters(s.toCharArray(), 0, s.length());
        }

    }

    public void characters(Object data) {
        this.characters(nlPattern.matcher(data.toString()).replaceAll(" "));
    }

    public void startSequence(String name, String subName) {
        if (name != null) {
            this.startElement(name);
        } else {
            this.stack.push(null);
        }

    }

    public void endSequence() {
        if (this.stack.peek() == null) {
            this.stack.pop();
        } else {
            this.endElement();
        }

    }

    public final void textElement(String name, Object data) {
        this.startElement(name);
        this.characters(data.toString());
        this.endElement();
    }

    public void element(String tagName, Object... attributes) {
        this.startElement(tagName, attributes);
        this.endElement();
    }

    public void startElement(String tagName) {
        this._startElement((String)null, (String)null, tagName, EmptyAttributes);
        this.stack.add(tagName);
    }

    public void startElement(String tagName, Object... attributes) {
        this._startElement((String)null, (String)null, tagName, new DefaultSaxWriter.StringAttributes(attributes));

        assert tagName != null;

        this.stack.add(tagName);
    }

    public void endElement() {
        String tagName = (String)this.stack.pop();
        this._endElement((String)null, (String)null, tagName);
    }

    public void startDocument() {
        if (this.stack.size() != 0) {
            throw new IllegalStateException("Document already started");
        }
    }

    public void endDocument() {
        if (this.stack.size() != 0) {
            throw new IllegalStateException("Document may have unbalanced elements");
        } else {
            this.writer.flush();
        }
    }

    public void completeBeforeElement(String tagName) {
        if (this.stack.indexOf(tagName) != -1) {
            for(String currentTagName = (String)this.stack.peek(); !tagName.equals(currentTagName); currentTagName = (String)this.stack.peek()) {
                this._endElement((String)null, (String)null, currentTagName);
                this.stack.pop();
            }

        }
    }

    public void verbatim(String text) {
        this._checkTag();
        this.writer.print(text);
    }

    public void flush() {
        this.writer.flush();
    }

    public static class StringAttributes implements Attributes {
        private final Object[] strings;

        public StringAttributes(Object[] strings) {
            this.strings = strings;
        }

        public int getLength() {
            return this.strings.length / 2;
        }

        public String getURI(int index) {
            return null;
        }

        public String getLocalName(int index) {
            return null;
        }

        public String getQName(int index) {
            return (String)this.strings[index * 2];
        }

        public String getType(int index) {
            return null;
        }

        public String getValue(int index) {
            return stringValue(this.strings[index * 2 + 1]);
        }

        public int getIndex(String uri, String localName) {
            return -1;
        }

        public int getIndex(String qName) {
            int count = this.strings.length / 2;

            for(int i = 0; i < count; ++i) {
                String string = (String)this.strings[i * 2];
                if (string.equals(qName)) {
                    return i;
                }
            }

            return -1;
        }

        public String getType(String uri, String localName) {
            return null;
        }

        public String getType(String qName) {
            return null;
        }

        public String getValue(String uri, String localName) {
            return null;
        }

        public String getValue(String qName) {
            int index = this.getIndex(qName);
            return index < 0 ? null : stringValue(this.strings[index * 2 + 1]);
        }

        private static String stringValue(Object s) {
            return s == null ? null : s.toString();
        }
    }
}