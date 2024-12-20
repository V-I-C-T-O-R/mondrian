/* Decompiler 4ms, total 554ms, lines 24 */
package mondrian.olap;

import java.io.PrintWriter;

public class Refresh extends QueryPart {
    private final String cubeName;

    Refresh(String cubeName) {
        this.cubeName = cubeName;
    }

    public void unparse(PrintWriter pw) {
        pw.print("REFRESH CUBE [" + this.cubeName + "]");
    }

    public Object[] getChildren() {
        return new Object[]{this.cubeName};
    }

    public String getCubeName() {
        return this.cubeName;
    }
}