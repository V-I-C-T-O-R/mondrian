/* Decompiler 18ms, total 492ms, lines 22 */
package mondrian.rolap;

import mondrian.olap.Dimension;

public class RolapWritebackAttribute extends RolapWritebackColumn {
    private final Dimension dimension;
    private final String columnName;

    public RolapWritebackAttribute(Dimension dimension, String columnName) {
        this.dimension = dimension;
        this.columnName = columnName;
    }

    public Dimension getDimension() {
        return this.dimension;
    }

    public String getColumnName() {
        return this.columnName;
    }
}