/* Decompiler 7ms, total 939ms, lines 42 */
package mondrian.rolap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import mondrian.olap.OlapElement;

public class RolapDrillThroughAction extends RolapAction {
    private final boolean isDefault;
    private List<RolapDrillThroughColumn> columnList;

    public RolapDrillThroughAction(String name, String caption, String description, boolean isDefault, List<RolapDrillThroughColumn> columnList) {
        super(name, caption, description);
        this.isDefault = isDefault;
        this.columnList = columnList;
        if (this.columnList == null) {
            this.columnList = new ArrayList();
        }

    }

    public boolean getIsDefault() {
        return this.isDefault;
    }

    public List<RolapDrillThroughColumn> getColumns() {
        return this.columnList;
    }

    public List<OlapElement> getOlapElements() {
        List<OlapElement> olapElementList = new ArrayList();
        Iterator var2 = this.columnList.iterator();

        while(var2.hasNext()) {
            RolapDrillThroughColumn rolapDrillThroughColumn = (RolapDrillThroughColumn)var2.next();
            olapElementList.add(rolapDrillThroughColumn.getOlapElement());
        }

        return olapElementList;
    }
}