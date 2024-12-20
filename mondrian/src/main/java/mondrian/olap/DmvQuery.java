/* Decompiler 4ms, total 521ms, lines 24 */
package mondrian.olap;

import java.util.List;

public class DmvQuery extends QueryPart {
    private final String tableName;
    private final List<String> columns;
    private final Exp whereExpression;

    public DmvQuery(String tableName, List<String> columns, Exp whereExpression) {
        this.tableName = tableName;
        this.columns = columns;
        this.whereExpression = whereExpression;
    }

    public String getTableName() {
        return this.tableName;
    }

    public Exp getWhereExpression() {
        return this.whereExpression;
    }
}