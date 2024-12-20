/* Decompiler 83ms, total 540ms, lines 120 */
package mondrian.olap4j;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import mondrian.olap.OlapElement;
import mondrian.olap.Property;
import mondrian.rolap.RolapCell;
import mondrian.rolap.SqlStatement;
import org.apache.logging.log4j.Logger;
import org.olap4j.AllocationPolicy;
import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.OlapException;
import org.olap4j.Scenario;

public class MondrianOlap4jCell implements Cell {
    private final int[] coordinates;
    private final MondrianOlap4jCellSet olap4jCellSet;
    final RolapCell cell;

    MondrianOlap4jCell(int[] coordinates, MondrianOlap4jCellSet olap4jCellSet, RolapCell cell) {
        assert coordinates != null;

        assert olap4jCellSet != null;

        assert cell != null;

        this.coordinates = coordinates;
        this.olap4jCellSet = olap4jCellSet;
        this.cell = cell;
    }

    public CellSet getCellSet() {
        return this.olap4jCellSet;
    }

    public RolapCell getRolapCell() {
        return this.cell;
    }

    public int getOrdinal() {
        return (Integer)this.cell.getPropertyValue(Property.CELL_ORDINAL.name);
    }

    public List<Integer> getCoordinateList() {
        ArrayList<Integer> list = new ArrayList(this.coordinates.length);
        int[] var2 = this.coordinates;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            int coordinate = var2[var4];
            list.add(coordinate);
        }

        return list;
    }

    public Object getPropertyValue(org.olap4j.metadata.Property property) {
        return this.cell.getPropertyValue(property.getName());
    }

    public boolean isEmpty() {
        return this.cell.isNull();
    }

    public boolean isError() {
        return this.cell.isError();
    }

    public boolean isNull() {
        return this.cell.isNull();
    }

    public double getDoubleValue() throws OlapException {
        Object o = this.cell.getValue();
        if (o instanceof Number) {
            Number number = (Number)o;
            return number.doubleValue();
        } else {
            throw this.olap4jCellSet.olap4jStatement.olap4jConnection.helper.createException(this, "not a number");
        }
    }

    public String getErrorText() {
        Object o = this.cell.getValue();
        return o instanceof Throwable ? ((Throwable)o).getMessage() : null;
    }

    public Object getValue() {
        return this.cell.getValue();
    }

    public String getFormattedValue() {
        return this.cell.getFormattedValue();
    }

    public ResultSet drillThrough() throws OlapException {
        return this.drillThroughInternal(-1, -1, new ArrayList(), false, (Logger)null, (int[])null);
    }

    ResultSet drillThroughInternal(int maxRowCount, int firstRowOrdinal, List<OlapElement> fields, boolean extendedContext, Logger logger, int[] rowCountSlot) throws OlapException {
        if (!this.cell.canDrillThrough()) {
            return null;
        } else {
            if (rowCountSlot != null) {
                rowCountSlot[0] = this.cell.getDrillThroughCount();
            }

            SqlStatement sqlStmt = this.cell.drillThroughInternal(maxRowCount, firstRowOrdinal, fields, extendedContext, logger);
            return sqlStmt.getWrappedResultSet();
        }
    }

    public void setValue(Object newValue, AllocationPolicy allocationPolicy, Object... allocationArgs) throws OlapException {
        Scenario scenario = this.olap4jCellSet.olap4jStatement.olap4jConnection.getScenario();
        this.cell.setValue(scenario, newValue, allocationPolicy, allocationArgs);
    }
}