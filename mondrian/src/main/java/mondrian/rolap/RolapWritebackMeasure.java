/* Decompiler 11ms, total 309ms, lines 22 */
package mondrian.rolap;

import mondrian.olap.Member;

public class RolapWritebackMeasure extends RolapWritebackColumn {
    private final Member measure;
    private final String columnName;

    public RolapWritebackMeasure(Member measure, String columnName) {
        this.measure = measure;
        this.columnName = columnName;
    }

    public Member getMeasure() {
        return this.measure;
    }

    public String getColumnName() {
        return this.columnName;
    }
}