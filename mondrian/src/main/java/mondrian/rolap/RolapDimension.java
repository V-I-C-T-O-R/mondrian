/* Decompiler 1364ms, total 2040ms, lines 129 */
package mondrian.rolap;

import java.util.Collections;
import java.util.Map;
import mondrian.olap.Annotation;
import mondrian.olap.DimensionBase;
import mondrian.olap.DimensionType;
import mondrian.olap.Hierarchy;
import mondrian.olap.Level;
import mondrian.olap.Schema;
import mondrian.olap.Util;
import mondrian.olap.MondrianDef.CubeDimension;
import mondrian.olap.MondrianDef.Dimension;
import mondrian.resource.MondrianResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class RolapDimension extends DimensionBase {
    private static final Logger LOGGER = LogManager.getLogger(RolapDimension.class);
    private final Schema schema;
    private final Map<String, Annotation> annotationMap;

    RolapDimension(Schema schema, String name, String caption, boolean visible, String description, DimensionType dimensionType, boolean highCardinality, Map<String, Annotation> annotationMap) {
        super(name, caption, visible, description, dimensionType, highCardinality);

        assert annotationMap != null;

        this.schema = schema;
        this.annotationMap = annotationMap;
        this.hierarchies = new RolapHierarchy[0];
    }

    RolapDimension(RolapSchema schema, RolapCube cube, Dimension xmlDimension, CubeDimension xmlCubeDimension) {
        this(schema, xmlDimension.name, xmlDimension.caption, xmlDimension.visible, xmlDimension.description, xmlDimension.getDimensionType(), xmlDimension.highCardinality, RolapHierarchy.createAnnotationMap(xmlDimension.annotations));
        Util.assertPrecondition(schema != null);
        if (cube != null) {
            Util.assertTrue(cube.getSchema() == schema);
        }

        if (!Util.isEmpty(xmlDimension.caption)) {
            this.setCaption(xmlDimension.caption);
        }

        this.hierarchies = new RolapHierarchy[xmlDimension.hierarchies.length];

        int i;
        for(i = 0; i < xmlDimension.hierarchies.length; ++i) {
            RolapHierarchy hierarchy = new RolapHierarchy(cube, this, xmlDimension.hierarchies[i], xmlCubeDimension);
            this.hierarchies[i] = hierarchy;
        }

        if (this.dimensionType == null) {
            for(i = 0; i < this.hierarchies.length; ++i) {
                Level[] levels = this.hierarchies[i].getLevels();

                for(int j = 0; j < levels.length; ++j) {
                    Level lev = levels[j];
                    if (!lev.isAll()) {
                        if (this.dimensionType == null) {
                            this.dimensionType = lev.getLevelType().isTime() ? DimensionType.TimeDimension : (this.isMeasures() ? DimensionType.MeasuresDimension : DimensionType.StandardDimension);
                        } else {
                            if (this.dimensionType == DimensionType.TimeDimension && !lev.getLevelType().isTime() && !lev.isAll()) {
                                throw MondrianResource.instance().NonTimeLevelInTimeHierarchy.ex(this.getUniqueName());
                            }

                            if (this.dimensionType != DimensionType.TimeDimension && lev.getLevelType().isTime()) {
                                throw MondrianResource.instance().TimeLevelInNonTimeHierarchy.ex(this.getUniqueName());
                            }
                        }
                    }
                }
            }
        }

    }

    protected Logger getLogger() {
        return LOGGER;
    }

    void init(CubeDimension xmlDimension) {
        for(int i = 0; i < this.hierarchies.length; ++i) {
            if (this.hierarchies[i] != null) {
                ((RolapHierarchy)this.hierarchies[i]).init(xmlDimension);
            }
        }

    }

    RolapHierarchy newHierarchy(String subName, boolean hasAll, RolapHierarchy closureFor) {
        RolapHierarchy hierarchy = new RolapHierarchy(this, subName, this.caption, this.visible, this.description, (String)null, hasAll, closureFor, Collections.emptyMap());
        this.hierarchies = (Hierarchy[])Util.append(this.hierarchies, hierarchy);
        return hierarchy;
    }

    public Hierarchy getHierarchy() {
        return this.hierarchies[0];
    }

    public Schema getSchema() {
        return this.schema;
    }

    public Map<String, Annotation> getAnnotationMap() {
        return this.annotationMap;
    }

    protected int computeHashCode() {
        return this.isMeasuresDimension() ? System.identityHashCode(this) : super.computeHashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof RolapDimension)) {
            return false;
        } else if (this.isMeasuresDimension()) {
            RolapDimension that = (RolapDimension)o;
            return this == that;
        } else {
            return super.equals(o);
        }
    }

    private boolean isMeasuresDimension() {
        return this.getDimensionType() == DimensionType.MeasuresDimension;
    }
}