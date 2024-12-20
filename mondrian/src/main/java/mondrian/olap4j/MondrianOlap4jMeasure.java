/* Decompiler 11ms, total 511ms, lines 56 */
package mondrian.olap4j;

import mondrian.olap.Property;
import mondrian.rolap.RolapAggregator;
import mondrian.rolap.RolapMeasure;
import mondrian.rolap.RolapStoredMeasure;
import org.olap4j.metadata.Datatype;
import org.olap4j.metadata.Measure;
import org.olap4j.metadata.Measure.Aggregator;

class MondrianOlap4jMeasure extends MondrianOlap4jMember implements Measure {
    MondrianOlap4jMeasure(MondrianOlap4jSchema olap4jSchema, RolapMeasure measure) {
        super(olap4jSchema, measure);
    }

    public Aggregator getAggregator() {
        if (!(this.member instanceof RolapStoredMeasure)) {
            return Aggregator.UNKNOWN;
        } else {
            RolapAggregator aggregator = ((RolapStoredMeasure)this.member).getAggregator();
            if (aggregator == RolapAggregator.Avg) {
                return Aggregator.AVG;
            } else if (aggregator == RolapAggregator.Count) {
                return Aggregator.COUNT;
            } else if (aggregator == RolapAggregator.DistinctCount) {
                return Aggregator.UNKNOWN;
            } else if (aggregator == RolapAggregator.Max) {
                return Aggregator.MAX;
            } else if (aggregator == RolapAggregator.Min) {
                return Aggregator.MIN;
            } else {
                return aggregator == RolapAggregator.Sum ? Aggregator.SUM : Aggregator.UNKNOWN;
            }
        }
    }

    public Datatype getDatatype() {
        String datatype = (String)this.member.getPropertyValue(Property.DATATYPE.getName());
        if (datatype != null) {
            if (datatype.equals("Integer")) {
                return Datatype.INTEGER;
            }

            if (datatype.equals("Numeric")) {
                return Datatype.DOUBLE;
            }
        }

        return Datatype.STRING;
    }

    public String getDisplayFolder() {
        return (String)this.member.getPropertyValue("DISPLAY_FOLDER");
    }
}