/* Decompiler 136ms, total 611ms, lines 188 */
package mondrian.xmla.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import mondrian.olap.Util;
import mondrian.util.ArrayStack;
import mondrian.xmla.SaxWriter;

class JsonSaxWriter implements SaxWriter {
    private final StringBuilder buf = new StringBuilder();
    private int indent;
    private String[] indentStrings;
    private String indentString;
    private final ArrayStack<JsonSaxWriter.Frame> stack;
    private OutputStream outputStream;
    private static final String[] INITIAL_INDENT_STRINGS = new String[]{"", "  ", "    ", "      ", "        ", "          ", "            ", "              ", "                ", "                  "};

    public JsonSaxWriter(OutputStream outputStream) {
        this.indentStrings = INITIAL_INDENT_STRINGS;
        this.indentString = this.indentStrings[0];
        this.stack = new ArrayStack();
        this.outputStream = outputStream;
    }

    public void startDocument() {
        this.stack.push(new JsonSaxWriter.Frame((String)null));
    }

    public void endDocument() {
        this.stack.pop();
        this.flush();
    }

    public void startSequence(String name, String subName) {
        this.comma();
        this.buf.append(this.indentString);
        if (name == null) {
            name = subName;
        }

        if (((JsonSaxWriter.Frame)this.stack.peek()).name != null) {
            assert name.equals(((JsonSaxWriter.Frame)this.stack.peek()).name) : "In sequence [" + this.stack.peek() + "], element name [" + name + "]";

            this.buf.append("[");
        } else {
            Util.quoteForMdx(this.buf, name);
            this.buf.append(": [");
        }

        assert subName != null;

        this.stack.push(new JsonSaxWriter.Frame(subName));
        this.indent();
    }

    public void endSequence() {
        assert this.stack.peek() != null : "not in sequence";

        this.stack.pop();
        this.outdent();
        this.buf.append("\n");
        this.buf.append(this.indentString);
        this.buf.append("]");
    }

    public void startElement(String name) {
        this.comma();
        this.buf.append(this.indentString);
        if (((JsonSaxWriter.Frame)this.stack.peek()).name != null) {
            assert name.equals(((JsonSaxWriter.Frame)this.stack.peek()).name) : "In sequence [" + this.stack.peek() + "], element name [" + name + "]";

            this.buf.append("{");
        } else {
            Util.quoteForMdx(this.buf, name);
            this.buf.append(": {");
        }

        this.stack.push(new JsonSaxWriter.Frame((String)null));
        this.indent();
    }

    public void startElement(String name, Object... attrs) {
        this.startElement(name);
    }

    public void endElement() {
        JsonSaxWriter.Frame prev = (JsonSaxWriter.Frame)this.stack.pop();

        assert prev.name == null : "Ended an element, but in sequence " + prev.name;

        this.buf.append("\n");
        this.outdent();
        this.buf.append(this.indentString);
        this.buf.append("}");
    }

    public void element(String name, Object... attrs) {
        this.startElement(name, attrs);
        this.endElement();
    }

    public void characters(String data) {
        this.value(data);
    }

    public void characters(Object data) {
        throw new UnsupportedOperationException();
    }

    public void textElement(String name, Object data) {
        this.comma();
        this.buf.append(this.indentString);
        Util.quoteForMdx(this.buf, name);
        this.buf.append(": ");
        this.value(data);
    }

    public void completeBeforeElement(String tagName) {
        throw new UnsupportedOperationException();
    }

    public void verbatim(String text) {
        throw new UnsupportedOperationException();
    }

    public void flush() {
        try {
            this.outputStream.write(this.buf.toString().substring(1).getBytes());
        } catch (IOException var2) {
            throw Util.newError(var2, "While encoding JSON response");
        }
    }

    private void indent() {
        ++this.indent;
        if (this.indent >= this.indentStrings.length) {
            int newLength = this.indentStrings.length * 2 + 1;

            assert this.indentStrings[1].length() == 2;

            char[] chars = new char[newLength * 2];
            Arrays.fill(chars, ' ');
            String s = new String(chars);
            this.indentStrings = new String[newLength];

            for(int i = 0; i < newLength; ++i) {
                this.indentStrings[i] = s.substring(0, i * 2);
            }
        }

        this.indentString = this.indentStrings[this.indent];
    }

    private void outdent() {
        this.indentString = this.indentStrings[--this.indent];
    }

    private void value(Object value) {
        if (value instanceof String) {
            String s = (String)value;
            Util.quoteForMdx(this.buf, s);
        } else {
            this.buf.append(value);
        }

    }

    private void comma() {
        if (((JsonSaxWriter.Frame)this.stack.peek()).ordinal++ > 0) {
            this.buf.append(",\n");
        } else {
            this.buf.append("\n");
        }

    }

    private static class Frame {
        final String name;
        int ordinal;

        Frame(String name) {
            this.name = name;
        }
    }
}