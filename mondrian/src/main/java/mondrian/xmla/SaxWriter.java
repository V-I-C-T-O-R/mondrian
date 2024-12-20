/* Decompiler 3ms, total 554ms, lines 32 */
package mondrian.xmla;

public interface SaxWriter {
    void startDocument();

    void endDocument();

    void startElement(String var1);

    void startElement(String var1, Object... var2);

    void endElement();

    void element(String var1, Object... var2);

    void characters(String var1);

    void characters(Object var1);

    void startSequence(String var1, String var2);

    void endSequence();

    void textElement(String var1, Object var2);

    void completeBeforeElement(String var1);

    void verbatim(String var1);

    void flush();
}