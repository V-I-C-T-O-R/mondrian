/* Decompiler 15ms, total 309ms, lines 22 */
package mondrian.rolap;

import mondrian.olap.Member;
import mondrian.olap.OlapElement;

public class RolapDrillThroughMeasure extends RolapDrillThroughColumn {
    private final Member measure;

    public RolapDrillThroughMeasure(String name, Member measure) {
        super(name);
        this.measure = measure;
    }

    public Member getMeasure() {
        return this.measure;
    }

    public OlapElement getOlapElement() {
        return this.measure;
    }
}