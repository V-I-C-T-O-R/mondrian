/* Decompiler 50ms, total 374ms, lines 79 */
package mondrian.olap;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Subcube extends QueryPart {
    private final String cubeName;
    private final Subcube subcube;
    private final QueryAxis[] axes;
    private final QueryAxis slicerAxis;

    public Subcube(String cubeName, Subcube subcube, QueryAxis[] axes, QueryAxis slicerAxis) {
        this.cubeName = cubeName;
        this.subcube = subcube;
        this.axes = axes;
        this.slicerAxis = slicerAxis;
    }

    public void unparse(PrintWriter pw) {
        if (this.subcube != null) {
            pw.println("(");
            pw.println("select ");

            for(int i = 0; i < this.axes.length; ++i) {
                pw.print("  ");
                this.axes[i].unparse(pw);
                if (i < this.axes.length - 1) {
                    pw.println(",");
                    pw.print("  ");
                } else {
                    pw.println();
                }
            }

            pw.println("from ");
            if (this.subcube != null) {
                this.subcube.unparse(pw);
            }

            if (this.slicerAxis != null) {
                pw.print("where ");
                this.slicerAxis.unparse(pw);
                pw.println();
            }

            pw.println(")");
        } else {
            pw.println("[" + this.cubeName + "]");
        }

    }

    public Object[] getChildren() {
        return new Object[]{this.cubeName};
    }

    public String getCubeName() {
        return this.subcube != null ? this.subcube.getCubeName() : this.cubeName;
    }

    public List<Exp> getAxisExps() {
        ArrayList<Exp> exps = new ArrayList();
        if (this.subcube != null) {
            exps.addAll(this.subcube.getAxisExps());
        }

        for(int i = 0; i < this.axes.length; ++i) {
            exps.add(this.axes[i].getSet());
        }

        if (this.slicerAxis != null) {
            exps.add(this.slicerAxis.getSet());
        }

        return exps;
    }
}