/* Decompiler 27ms, total 769ms, lines 40 */
package mondrian.rolap;

import mondrian.olap.Dimension;
import mondrian.olap.Hierarchy;
import mondrian.olap.Level;
import mondrian.olap.OlapElement;

public class RolapDrillThroughAttribute extends RolapDrillThroughColumn {
    private final Dimension dimension;
    private final Hierarchy hierarchy;
    private final Level level;

    public RolapDrillThroughAttribute(String name, Dimension dimension, Hierarchy hierarchy, Level level) {
        super(name);
        this.dimension = dimension;
        this.hierarchy = hierarchy;
        this.level = level;
    }

    public Dimension getDimension() {
        return this.dimension;
    }

    public Hierarchy getHierarchy() {
        return this.hierarchy;
    }

    public Level getLevel() {
        return this.level;
    }

    public OlapElement getOlapElement() {
        if (this.level != null) {
            return this.level;
        } else {
            return (OlapElement)(this.hierarchy != null ? this.hierarchy : this.dimension);
        }
    }
}