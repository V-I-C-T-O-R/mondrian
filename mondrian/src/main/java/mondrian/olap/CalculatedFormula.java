/* Decompiler 17ms, total 1108ms, lines 38 */
package mondrian.olap;

import java.io.PrintWriter;

public class CalculatedFormula extends QueryPart {
    private final String cubeName;
    private final Formula e;

    CalculatedFormula(String cubeName, Formula e) {
        this.cubeName = cubeName;
        this.e = e;
    }

    public void unparse(PrintWriter pw) {
        pw.print("CREATE SESSION MEMBER ");
        pw.print("[" + this.cubeName + "]");
        pw.print(".");
        if (this.e != null) {
            pw.print(this.e.getIdentifier());
            pw.print(" AS ");
        }

        pw.println();
    }

    public Object[] getChildren() {
        return new Object[]{this.e};
    }

    public String getCubeName() {
        return this.cubeName;
    }

    public Formula getFormula() {
        return this.e;
    }
}