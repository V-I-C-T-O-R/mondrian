/* Decompiler 23ms, total 507ms, lines 33 */
package mondrian.rolap;

import java.util.ArrayList;
import java.util.List;

public class RolapWritebackTable {
    private final String name;
    private final String schema;
    private List<RolapWritebackColumn> columnList;

    public RolapWritebackTable(String name, String schema, List<RolapWritebackColumn> columnList) {
        this.name = name;
        this.schema = schema;
        this.columnList = columnList;
        if (this.columnList == null) {
            this.columnList = new ArrayList();
        }

    }

    public String getName() {
        return this.name;
    }

    public String getSchema() {
        return this.schema;
    }

    public List<RolapWritebackColumn> getColumns() {
        return this.columnList;
    }
}