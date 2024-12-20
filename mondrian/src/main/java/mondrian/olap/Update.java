/* Decompiler 37ms, total 331ms, lines 61 */
package mondrian.olap;

import java.io.PrintWriter;
import java.util.List;

public class Update extends QueryPart {
    private final String cubeName;
    private List<Update.UpdateClause> updateClauses;

    Update(String cubeName, List<Update.UpdateClause> updateClauses) {
        this.cubeName = cubeName;
        this.updateClauses = updateClauses;
    }

    public void unparse(PrintWriter pw) {
        pw.print("UPDATE CUBE [" + this.cubeName + "]");
    }

    public Object[] getChildren() {
        return new Object[]{this.cubeName};
    }

    public String getCubeName() {
        return this.cubeName;
    }

    public List<Update.UpdateClause> getUpdateClauses() {
        return this.updateClauses;
    }

    public static class UpdateClause extends QueryPart {
        private final Exp tuple;
        private Exp value;
        private Update.Allocation allocation;
        private Exp weight;

        public UpdateClause(Exp tuple, Exp value, Update.Allocation allocation, Exp weight) {
            this.tuple = tuple;
            this.value = value;
            this.allocation = allocation;
            this.weight = weight;
        }

        public Exp getTupleExp() {
            return this.tuple;
        }

        public Exp getValueExp() {
            return this.value;
        }
    }

    public static enum Allocation {
        NO_ALLOCATION,
        USE_EQUAL_ALLOCATION,
        USE_EQUAL_INCREMENT,
        USE_WEIGHTED_ALLOCATION,
        USE_WEIGHTED_INCREMENT;
    }
}