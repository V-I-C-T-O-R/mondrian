/* Decompiler 7933ms, total 9950ms, lines 5208 */
package mondrian.olap;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import mondrian.rolap.sql.SqlQuery;
import mondrian.rolap.sql.SqlQuery.CodeSet;
import mondrian.spi.Dialect.Datatype;
import org.eigenbase.xom.DOMElementParser;
import org.eigenbase.xom.DOMWrapper;
import org.eigenbase.xom.ElementDef;
import org.eigenbase.xom.NodeDef;
import org.eigenbase.xom.XMLAttrVector;
import org.eigenbase.xom.XMLOutput;
import org.eigenbase.xom.XOMException;

public class MondrianDef {
    public static String[] _elements = new String[]{"Schema", "CubeDimension", "Cube", "VirtualCube", "CubeUsages", "CubeUsage", "VirtualCubeDimension", "VirtualCubeMeasure", "DimensionUsage", "Dimension", "Hierarchy", "Level", "Closure", "Property", "Measure", "CalculatedMember", "CalculatedMemberProperty", "NamedSet", "Formula", "MemberReaderParameter", "RelationOrJoin", "Relation", "View", "SQL", "SchemaView", "Join", "Table", "Hint", "InlineTable", "ColumnDefs", "ColumnDef", "Rows", "Row", "Value", "AggTable", "AggName", "AggPattern", "AggExclude", "AggColumnName", "AggFactCount", "AggMeasureFactCount", "AggIgnoreColumn", "AggForeignKey", "AggLevel", "AggLevelProperty", "AggMeasure", "Expression", "Column", "ExpressionView", "KeyExpression", "ParentExpression", "OrdinalExpression", "NameExpression", "CaptionExpression", "MeasureExpression", "Role", "Grant", "SchemaGrant", "CubeGrant", "DimensionGrant", "HierarchyGrant", "MemberGrant", "RoleMember", "Union", "RoleUsage", "UserDefinedFunction", "Parameter", "Annotations", "Annotation", "Script", "ElementFormatter", "CellFormatter", "MemberFormatter", "PropertyFormatter", "DrillThroughColumn", "DrillThroughAttribute", "DrillThroughMeasure", "Action", "DrillThroughAction", "WritebackColumn", "WritebackAttribute", "WritebackMeasure", "WritebackTable", "DimensionAttribute", "DataItem", "ColumnBinding"};

    public static Class getXMLDefClass() {
        return MondrianDef.class;
    }

    public static class ColumnBinding extends ElementDef {
        public String tableID;
        public String columnID;

        public ColumnBinding() {
        }

        public ColumnBinding(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.tableID = (String)_parser.getAttribute("tableID", "String", (String)null, (String[])null, true);
                this.columnID = (String)_parser.getAttribute("columnID", "String", (String)null, (String[])null, true);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "ColumnBinding";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "tableID", this.tableID, _indent + 1);
            displayAttribute(_out, "columnID", this.columnID, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("ColumnBinding", (new XMLAttrVector()).add("tableID", this.tableID).add("columnID", this.columnID));
            _out.endTag("ColumnBinding");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.ColumnBinding _cother = (MondrianDef.ColumnBinding)_other;
            boolean _diff = displayAttributeDiff("tableID", this.tableID, _cother.tableID, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("columnID", this.columnID, _cother.columnID, _out, _indent + 1);
            return _diff;
        }
    }

    public static class DataItem extends ElementDef {
        public static final String[] _dataType_values = new String[]{"WChar", "Integer", "BigInt", "Single", "Double", "Date", "Currency", "UnsignedTinyInt", "UnsignedSmallInt", "UnsignedInt", "UnsignedBigInt", "Bool", "Smallint", "Tinyint", "Binary"};
        public String dataType;
        public Integer dataSize;
        public String mimeType;
        public static final String[] _nullProcessing_values = new String[]{"Preserve", "Error", "UnknownMember", "ZeroOrBlank", "Automatic"};
        public String nullProcessing;
        public static final String[] _trimming_values = new String[]{"Left", "Right", "LeftRight", "None"};
        public String trimming;
        public static final String[] _invalidXmlCharacters_values = new String[]{"Preserve", "Remove", "Replace"};
        public String invalidXmlCharacters;
        public String collation;
        public static final String[] _format_values = new String[]{"TrimRight", "TrimLeft", "TrimAll", "TrimNone"};
        public String format;
        public MondrianDef.ColumnBinding source;

        public DataItem() {
        }

        public DataItem(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.dataType = (String)_parser.getAttribute("dataType", "String", (String)null, _dataType_values, true);
                this.dataSize = (Integer)_parser.getAttribute("dataSize", "Integer", (String)null, (String[])null, false);
                this.mimeType = (String)_parser.getAttribute("mimeType", "String", (String)null, (String[])null, false);
                this.nullProcessing = (String)_parser.getAttribute("nullProcessing", "String", (String)null, _nullProcessing_values, false);
                this.trimming = (String)_parser.getAttribute("trimming", "String", (String)null, _trimming_values, false);
                this.invalidXmlCharacters = (String)_parser.getAttribute("invalidXmlCharacters", "String", (String)null, _invalidXmlCharacters_values, false);
                this.collation = (String)_parser.getAttribute("collation", "String", (String)null, (String[])null, false);
                this.format = (String)_parser.getAttribute("format", "String", (String)null, _format_values, false);
                this.source = (MondrianDef.ColumnBinding)_parser.getElement(MondrianDef.ColumnBinding.class, false);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "DataItem";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "dataType", this.dataType, _indent + 1);
            displayAttribute(_out, "dataSize", this.dataSize, _indent + 1);
            displayAttribute(_out, "mimeType", this.mimeType, _indent + 1);
            displayAttribute(_out, "nullProcessing", this.nullProcessing, _indent + 1);
            displayAttribute(_out, "trimming", this.trimming, _indent + 1);
            displayAttribute(_out, "invalidXmlCharacters", this.invalidXmlCharacters, _indent + 1);
            displayAttribute(_out, "collation", this.collation, _indent + 1);
            displayAttribute(_out, "format", this.format, _indent + 1);
            displayElement(_out, "source", this.source, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("DataItem", (new XMLAttrVector()).add("dataType", this.dataType).add("dataSize", this.dataSize).add("mimeType", this.mimeType).add("nullProcessing", this.nullProcessing).add("trimming", this.trimming).add("invalidXmlCharacters", this.invalidXmlCharacters).add("collation", this.collation).add("format", this.format));
            displayXMLElement(_out, this.source);
            _out.endTag("DataItem");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.DataItem _cother = (MondrianDef.DataItem)_other;
            boolean _diff = displayAttributeDiff("dataType", this.dataType, _cother.dataType, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("dataSize", this.dataSize, _cother.dataSize, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("mimeType", this.mimeType, _cother.mimeType, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("nullProcessing", this.nullProcessing, _cother.nullProcessing, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("trimming", this.trimming, _cother.trimming, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("invalidXmlCharacters", this.invalidXmlCharacters, _cother.invalidXmlCharacters, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("collation", this.collation, _cother.collation, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("format", this.format, _cother.format, _out, _indent + 1);
            _diff = _diff && displayElementDiff("source", this.source, _cother.source, _out, _indent + 1);
            return _diff;
        }
    }

    public static class DimensionAttribute extends ElementDef {
        public String name;
        public String id;
        public String description;
        public static final String[] _usage_values = new String[]{"Regular", "Key", "Parent"};
        public String usage;
        public Long estimatedCount;
        public MondrianDef.DataItem[] keyColumns;
        public MondrianDef.DataItem nameColumn;
        public MondrianDef.DataItem valueColumn;

        public DimensionAttribute() {
        }

        public DimensionAttribute(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.id = (String)_parser.getAttribute("id", "String", (String)null, (String[])null, false);
                this.description = (String)_parser.getAttribute("description", "String", (String)null, (String[])null, false);
                this.usage = (String)_parser.getAttribute("usage", "String", (String)null, _usage_values, false);
                this.estimatedCount = (Long)_parser.getAttribute("estimatedCount", "Long", (String)null, (String[])null, false);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.DataItem.class, 0, 0);
                this.keyColumns = new MondrianDef.DataItem[_tempArray.length];

                for(int _i = 0; _i < this.keyColumns.length; ++_i) {
                    this.keyColumns[_i] = (MondrianDef.DataItem)_tempArray[_i];
                }

                this.nameColumn = (MondrianDef.DataItem)_parser.getElement(MondrianDef.DataItem.class, false);
                this.valueColumn = (MondrianDef.DataItem)_parser.getElement(MondrianDef.DataItem.class, false);
            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "DimensionAttribute";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "id", this.id, _indent + 1);
            displayAttribute(_out, "description", this.description, _indent + 1);
            displayAttribute(_out, "usage", this.usage, _indent + 1);
            displayAttribute(_out, "estimatedCount", this.estimatedCount, _indent + 1);
            displayElementArray(_out, "keyColumns", this.keyColumns, _indent + 1);
            displayElement(_out, "nameColumn", this.nameColumn, _indent + 1);
            displayElement(_out, "valueColumn", this.valueColumn, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("DimensionAttribute", (new XMLAttrVector()).add("name", this.name).add("id", this.id).add("description", this.description).add("usage", this.usage).add("estimatedCount", this.estimatedCount));
            displayXMLElementArray(_out, this.keyColumns);
            displayXMLElement(_out, this.nameColumn);
            displayXMLElement(_out, this.valueColumn);
            _out.endTag("DimensionAttribute");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.DimensionAttribute _cother = (MondrianDef.DimensionAttribute)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("id", this.id, _cother.id, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("description", this.description, _cother.description, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("usage", this.usage, _cother.usage, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("estimatedCount", this.estimatedCount, _cother.estimatedCount, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("keyColumns", this.keyColumns, _cother.keyColumns, _out, _indent + 1);
            _diff = _diff && displayElementDiff("nameColumn", this.nameColumn, _cother.nameColumn, _out, _indent + 1);
            _diff = _diff && displayElementDiff("valueColumn", this.valueColumn, _cother.valueColumn, _out, _indent + 1);
            return _diff;
        }
    }

    public static class WritebackTable extends ElementDef {
        public String name;
        public String schema;
        public MondrianDef.WritebackColumn[] columns;
        public MondrianDef.Annotations annotations;

        public WritebackTable() {
        }

        public WritebackTable(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.schema = (String)_parser.getAttribute("schema", "String", (String)null, (String[])null, false);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.WritebackColumn.class, 0, 0);
                this.columns = new MondrianDef.WritebackColumn[_tempArray.length];

                for(int _i = 0; _i < this.columns.length; ++_i) {
                    this.columns[_i] = (MondrianDef.WritebackColumn)_tempArray[_i];
                }

                this.annotations = (MondrianDef.Annotations)_parser.getElement(MondrianDef.Annotations.class, false);
            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "WritebackTable";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "schema", this.schema, _indent + 1);
            displayElementArray(_out, "columns", this.columns, _indent + 1);
            displayElement(_out, "annotations", this.annotations, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("WritebackTable", (new XMLAttrVector()).add("name", this.name).add("schema", this.schema));
            displayXMLElementArray(_out, this.columns);
            displayXMLElement(_out, this.annotations);
            _out.endTag("WritebackTable");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.WritebackTable _cother = (MondrianDef.WritebackTable)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("schema", this.schema, _cother.schema, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("columns", this.columns, _cother.columns, _out, _indent + 1);
            _diff = _diff && displayElementDiff("annotations", this.annotations, _cother.annotations, _out, _indent + 1);
            return _diff;
        }
    }

    public static class WritebackMeasure extends MondrianDef.WritebackColumn {
        public String name;
        public String column;

        public WritebackMeasure() {
        }

        public WritebackMeasure(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.column = (String)_parser.getAttribute("column", "String", (String)null, (String[])null, true);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "WritebackMeasure";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "column", this.column, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("WritebackMeasure", (new XMLAttrVector()).add("name", this.name).add("column", this.column));
            _out.endTag("WritebackMeasure");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.WritebackMeasure _cother = (MondrianDef.WritebackMeasure)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("column", this.column, _cother.column, _out, _indent + 1);
            return _diff;
        }
    }

    public static class WritebackAttribute extends MondrianDef.WritebackColumn {
        public String dimension;
        public String column;

        public WritebackAttribute() {
        }

        public WritebackAttribute(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.dimension = (String)_parser.getAttribute("dimension", "String", (String)null, (String[])null, true);
                this.column = (String)_parser.getAttribute("column", "String", (String)null, (String[])null, true);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "WritebackAttribute";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "dimension", this.dimension, _indent + 1);
            displayAttribute(_out, "column", this.column, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("WritebackAttribute", (new XMLAttrVector()).add("dimension", this.dimension).add("column", this.column));
            _out.endTag("WritebackAttribute");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.WritebackAttribute _cother = (MondrianDef.WritebackAttribute)_other;
            boolean _diff = displayAttributeDiff("dimension", this.dimension, _cother.dimension, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("column", this.column, _cother.column, _out, _indent + 1);
            return _diff;
        }
    }

    public abstract static class WritebackColumn extends ElementDef {
        public WritebackColumn() {
        }

        public WritebackColumn(DOMWrapper _def) throws XOMException {
        }

        public String getName() {
            return "WritebackColumn";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("WritebackColumn", new XMLAttrVector());
            _out.endTag("WritebackColumn");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            return true;
        }
    }

    public static class DrillThroughAction extends MondrianDef.Action {
        public Boolean _default;
        public MondrianDef.DrillThroughColumn[] columns;

        public DrillThroughAction() {
        }

        public DrillThroughAction(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this._default = (Boolean)_parser.getAttribute("default", "Boolean", (String)null, (String[])null, false);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.caption = (String)_parser.getAttribute("caption", "String", (String)null, (String[])null, false);
                this.description = (String)_parser.getAttribute("description", "String", (String)null, (String[])null, false);
                this.annotations = (MondrianDef.Annotations)_parser.getElement(MondrianDef.Annotations.class, false);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.DrillThroughColumn.class, 0, 0);
                this.columns = new MondrianDef.DrillThroughColumn[_tempArray.length];

                for(int _i = 0; _i < this.columns.length; ++_i) {
                    this.columns[_i] = (MondrianDef.DrillThroughColumn)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "DrillThroughAction";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "default", this._default, _indent + 1);
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "caption", this.caption, _indent + 1);
            displayAttribute(_out, "description", this.description, _indent + 1);
            displayElement(_out, "annotations", this.annotations, _indent + 1);
            displayElementArray(_out, "columns", this.columns, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("DrillThroughAction", (new XMLAttrVector()).add("default", this._default).add("name", this.name).add("caption", this.caption).add("description", this.description));
            displayXMLElement(_out, this.annotations);
            displayXMLElementArray(_out, this.columns);
            _out.endTag("DrillThroughAction");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.DrillThroughAction _cother = (MondrianDef.DrillThroughAction)_other;
            boolean _diff = displayAttributeDiff("default", this._default, _cother._default, _out, _indent + 1);
            _diff = _diff && displayElementDiff("annotations", this.annotations, _cother.annotations, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("columns", this.columns, _cother.columns, _out, _indent + 1);
            return _diff;
        }
    }

    public abstract static class Action extends ElementDef {
        public String name;
        public String caption;
        public String description;
        public MondrianDef.Annotations annotations;

        public Action() {
        }

        public Action(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.caption = (String)_parser.getAttribute("caption", "String", (String)null, (String[])null, false);
                this.description = (String)_parser.getAttribute("description", "String", (String)null, (String[])null, false);
                this.annotations = (MondrianDef.Annotations)_parser.getElement(MondrianDef.Annotations.class, false);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "Action";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "caption", this.caption, _indent + 1);
            displayAttribute(_out, "description", this.description, _indent + 1);
            displayElement(_out, "annotations", this.annotations, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Action", (new XMLAttrVector()).add("name", this.name).add("caption", this.caption).add("description", this.description));
            displayXMLElement(_out, this.annotations);
            _out.endTag("Action");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Action _cother = (MondrianDef.Action)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("caption", this.caption, _cother.caption, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("description", this.description, _cother.description, _out, _indent + 1);
            _diff = _diff && displayElementDiff("annotations", this.annotations, _cother.annotations, _out, _indent + 1);
            return _diff;
        }
    }

    public static class DrillThroughMeasure extends MondrianDef.DrillThroughColumn {
        public String name;
        public String measure;

        public DrillThroughMeasure() {
        }

        public DrillThroughMeasure(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.measure = (String)_parser.getAttribute("measure", "String", (String)null, (String[])null, true);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "DrillThroughMeasure";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "measure", this.measure, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("DrillThroughMeasure", (new XMLAttrVector()).add("name", this.name).add("measure", this.measure));
            _out.endTag("DrillThroughMeasure");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.DrillThroughMeasure _cother = (MondrianDef.DrillThroughMeasure)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("measure", this.measure, _cother.measure, _out, _indent + 1);
            return _diff;
        }
    }

    public static class DrillThroughAttribute extends MondrianDef.DrillThroughColumn {
        public String name;
        public String dimension;
        public String hierarchy;
        public String level;

        public DrillThroughAttribute() {
        }

        public DrillThroughAttribute(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.dimension = (String)_parser.getAttribute("dimension", "String", (String)null, (String[])null, true);
                this.hierarchy = (String)_parser.getAttribute("hierarchy", "String", (String)null, (String[])null, true);
                this.level = (String)_parser.getAttribute("level", "String", (String)null, (String[])null, true);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "DrillThroughAttribute";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "dimension", this.dimension, _indent + 1);
            displayAttribute(_out, "hierarchy", this.hierarchy, _indent + 1);
            displayAttribute(_out, "level", this.level, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("DrillThroughAttribute", (new XMLAttrVector()).add("name", this.name).add("dimension", this.dimension).add("hierarchy", this.hierarchy).add("level", this.level));
            _out.endTag("DrillThroughAttribute");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.DrillThroughAttribute _cother = (MondrianDef.DrillThroughAttribute)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("dimension", this.dimension, _cother.dimension, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("hierarchy", this.hierarchy, _cother.hierarchy, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("level", this.level, _cother.level, _out, _indent + 1);
            return _diff;
        }
    }

    public abstract static class DrillThroughColumn extends ElementDef {
        public DrillThroughColumn() {
        }

        public DrillThroughColumn(DOMWrapper _def) throws XOMException {
        }

        public String getName() {
            return "DrillThroughColumn";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("DrillThroughColumn", new XMLAttrVector());
            _out.endTag("DrillThroughColumn");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            return true;
        }
    }

    public static class PropertyFormatter extends MondrianDef.ElementFormatter {
        public PropertyFormatter() {
        }

        public PropertyFormatter(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.className = (String)_parser.getAttribute("className", "String", (String)null, (String[])null, false);
                this.script = (MondrianDef.Script)_parser.getElement(MondrianDef.Script.class, false);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "PropertyFormatter";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "className", this.className, _indent + 1);
            displayElement(_out, "script", this.script, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("PropertyFormatter", (new XMLAttrVector()).add("className", this.className));
            displayXMLElement(_out, this.script);
            _out.endTag("PropertyFormatter");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.PropertyFormatter _cother = (MondrianDef.PropertyFormatter)_other;
            boolean _diff = displayElementDiff("script", this.script, _cother.script, _out, _indent + 1);
            return _diff;
        }
    }

    public static class MemberFormatter extends MondrianDef.ElementFormatter {
        public MemberFormatter() {
        }

        public MemberFormatter(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.className = (String)_parser.getAttribute("className", "String", (String)null, (String[])null, false);
                this.script = (MondrianDef.Script)_parser.getElement(MondrianDef.Script.class, false);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "MemberFormatter";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "className", this.className, _indent + 1);
            displayElement(_out, "script", this.script, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("MemberFormatter", (new XMLAttrVector()).add("className", this.className));
            displayXMLElement(_out, this.script);
            _out.endTag("MemberFormatter");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.MemberFormatter _cother = (MondrianDef.MemberFormatter)_other;
            boolean _diff = displayElementDiff("script", this.script, _cother.script, _out, _indent + 1);
            return _diff;
        }
    }

    public static class CellFormatter extends MondrianDef.ElementFormatter {
        public CellFormatter() {
        }

        public CellFormatter(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.className = (String)_parser.getAttribute("className", "String", (String)null, (String[])null, false);
                this.script = (MondrianDef.Script)_parser.getElement(MondrianDef.Script.class, false);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "CellFormatter";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "className", this.className, _indent + 1);
            displayElement(_out, "script", this.script, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("CellFormatter", (new XMLAttrVector()).add("className", this.className));
            displayXMLElement(_out, this.script);
            _out.endTag("CellFormatter");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.CellFormatter _cother = (MondrianDef.CellFormatter)_other;
            boolean _diff = displayElementDiff("script", this.script, _cother.script, _out, _indent + 1);
            return _diff;
        }
    }

    public abstract static class ElementFormatter extends ElementDef {
        public String className;
        public MondrianDef.Script script;

        public ElementFormatter() {
        }

        public ElementFormatter(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.className = (String)_parser.getAttribute("className", "String", (String)null, (String[])null, false);
                this.script = (MondrianDef.Script)_parser.getElement(MondrianDef.Script.class, false);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "ElementFormatter";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "className", this.className, _indent + 1);
            displayElement(_out, "script", this.script, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("ElementFormatter", (new XMLAttrVector()).add("className", this.className));
            displayXMLElement(_out, this.script);
            _out.endTag("ElementFormatter");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.ElementFormatter _cother = (MondrianDef.ElementFormatter)_other;
            boolean _diff = displayAttributeDiff("className", this.className, _cother.className, _out, _indent + 1);
            _diff = _diff && displayElementDiff("script", this.script, _cother.script, _out, _indent + 1);
            return _diff;
        }
    }

    public static class Script extends ElementDef {
        public String language;
        public String cdata;

        public Script() {
        }

        public Script(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.language = (String)_parser.getAttribute("language", "String", "JavaScript", (String[])null, false);
                this.cdata = _parser.getText();
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "Script";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "language", this.language, _indent + 1);
            displayString(_out, "cdata", this.cdata, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Script", (new XMLAttrVector()).add("language", this.language));
            _out.cdata(this.cdata);
            _out.endTag("Script");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Script _cother = (MondrianDef.Script)_other;
            boolean _diff = displayAttributeDiff("language", this.language, _cother.language, _out, _indent + 1);
            _diff = _diff && displayStringDiff("cdata", this.cdata, _cother.cdata, _out, _indent + 1);
            return _diff;
        }
    }

    public static class Annotation extends ElementDef {
        public String name;
        public String cdata;

        public Annotation() {
        }

        public Annotation(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.cdata = _parser.getText();
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "Annotation";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayString(_out, "cdata", this.cdata, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Annotation", (new XMLAttrVector()).add("name", this.name));
            _out.cdata(this.cdata);
            _out.endTag("Annotation");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Annotation _cother = (MondrianDef.Annotation)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayStringDiff("cdata", this.cdata, _cother.cdata, _out, _indent + 1);
            return _diff;
        }
    }

    public static class Annotations extends ElementDef {
        public MondrianDef.Annotation[] array;

        public Annotations() {
        }

        public Annotations(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.Annotation.class, 0, 0);
                this.array = new MondrianDef.Annotation[_tempArray.length];

                for(int _i = 0; _i < this.array.length; ++_i) {
                    this.array[_i] = (MondrianDef.Annotation)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "Annotations";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayElementArray(_out, "array", this.array, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Annotations", new XMLAttrVector());
            displayXMLElementArray(_out, this.array);
            _out.endTag("Annotations");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Annotations _cother = (MondrianDef.Annotations)_other;
            boolean _diff = displayElementArrayDiff("array", this.array, _cother.array, _out, _indent + 1);
            return _diff;
        }
    }

    public static class Parameter extends ElementDef {
        public String name;
        public String description;
        public static final String[] _type_values = new String[]{"String", "Numeric", "Integer", "Boolean", "Date", "Time", "Timestamp", "Member"};
        public String type;
        public Boolean modifiable;
        public String defaultValue;

        public Parameter() {
        }

        public Parameter(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.description = (String)_parser.getAttribute("description", "String", (String)null, (String[])null, false);
                this.type = (String)_parser.getAttribute("type", "String", "String", _type_values, true);
                this.modifiable = (Boolean)_parser.getAttribute("modifiable", "Boolean", "true", (String[])null, false);
                this.defaultValue = (String)_parser.getAttribute("defaultValue", "String", (String)null, (String[])null, false);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "Parameter";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "description", this.description, _indent + 1);
            displayAttribute(_out, "type", this.type, _indent + 1);
            displayAttribute(_out, "modifiable", this.modifiable, _indent + 1);
            displayAttribute(_out, "defaultValue", this.defaultValue, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Parameter", (new XMLAttrVector()).add("name", this.name).add("description", this.description).add("type", this.type).add("modifiable", this.modifiable).add("defaultValue", this.defaultValue));
            _out.endTag("Parameter");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Parameter _cother = (MondrianDef.Parameter)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("description", this.description, _cother.description, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("type", this.type, _cother.type, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("modifiable", this.modifiable, _cother.modifiable, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("defaultValue", this.defaultValue, _cother.defaultValue, _out, _indent + 1);
            return _diff;
        }
    }

    public static class UserDefinedFunction extends ElementDef {
        public String name;
        public String className;
        public MondrianDef.Script script;

        public UserDefinedFunction() {
        }

        public UserDefinedFunction(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.className = (String)_parser.getAttribute("className", "String", (String)null, (String[])null, false);
                this.script = (MondrianDef.Script)_parser.getElement(MondrianDef.Script.class, false);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "UserDefinedFunction";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "className", this.className, _indent + 1);
            displayElement(_out, "script", this.script, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("UserDefinedFunction", (new XMLAttrVector()).add("name", this.name).add("className", this.className));
            displayXMLElement(_out, this.script);
            _out.endTag("UserDefinedFunction");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.UserDefinedFunction _cother = (MondrianDef.UserDefinedFunction)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("className", this.className, _cother.className, _out, _indent + 1);
            _diff = _diff && displayElementDiff("script", this.script, _cother.script, _out, _indent + 1);
            return _diff;
        }
    }

    public static class RoleUsage extends ElementDef {
        public String roleName;

        public RoleUsage() {
        }

        public RoleUsage(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.roleName = (String)_parser.getAttribute("roleName", "String", (String)null, (String[])null, true);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "RoleUsage";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "roleName", this.roleName, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("RoleUsage", (new XMLAttrVector()).add("roleName", this.roleName));
            _out.endTag("RoleUsage");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.RoleUsage _cother = (MondrianDef.RoleUsage)_other;
            boolean _diff = displayAttributeDiff("roleName", this.roleName, _cother.roleName, _out, _indent + 1);
            return _diff;
        }
    }

    public static class Union extends ElementDef {
        public MondrianDef.RoleUsage[] roleUsages;

        public Union() {
        }

        public Union(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.RoleUsage.class, 0, 0);
                this.roleUsages = new MondrianDef.RoleUsage[_tempArray.length];

                for(int _i = 0; _i < this.roleUsages.length; ++_i) {
                    this.roleUsages[_i] = (MondrianDef.RoleUsage)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "Union";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayElementArray(_out, "roleUsages", this.roleUsages, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Union", new XMLAttrVector());
            displayXMLElementArray(_out, this.roleUsages);
            _out.endTag("Union");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Union _cother = (MondrianDef.Union)_other;
            boolean _diff = displayElementArrayDiff("roleUsages", this.roleUsages, _cother.roleUsages, _out, _indent + 1);
            return _diff;
        }
    }

    public static class RoleMember extends ElementDef {
        public String name;

        public RoleMember() {
        }

        public RoleMember(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "RoleMember";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("RoleMember", (new XMLAttrVector()).add("name", this.name));
            _out.endTag("RoleMember");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.RoleMember _cother = (MondrianDef.RoleMember)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            return _diff;
        }
    }

    public static class MemberGrant extends ElementDef {
        public String member;
        public static final String[] _access_values = new String[]{"all", "none"};
        public String access;

        public MemberGrant() {
        }

        public MemberGrant(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.member = (String)_parser.getAttribute("member", "String", (String)null, (String[])null, true);
                this.access = (String)_parser.getAttribute("access", "String", (String)null, _access_values, true);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "MemberGrant";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "member", this.member, _indent + 1);
            displayAttribute(_out, "access", this.access, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("MemberGrant", (new XMLAttrVector()).add("member", this.member).add("access", this.access));
            _out.endTag("MemberGrant");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.MemberGrant _cother = (MondrianDef.MemberGrant)_other;
            boolean _diff = displayAttributeDiff("member", this.member, _cother.member, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("access", this.access, _cother.access, _out, _indent + 1);
            return _diff;
        }
    }

    public static class HierarchyGrant extends ElementDef implements MondrianDef.Grant {
        public static final String[] _access_values = new String[]{"all", "custom", "none", "all_dimensions"};
        public String access;
        public String hierarchy;
        public String topLevel;
        public String bottomLevel;
        public String rollupPolicy;
        public MondrianDef.MemberGrant[] memberGrants;

        public HierarchyGrant() {
        }

        public HierarchyGrant(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.hierarchy = (String)_parser.getAttribute("hierarchy", "String", (String)null, (String[])null, true);
                this.topLevel = (String)_parser.getAttribute("topLevel", "String", (String)null, (String[])null, false);
                this.bottomLevel = (String)_parser.getAttribute("bottomLevel", "String", (String)null, (String[])null, false);
                this.rollupPolicy = (String)_parser.getAttribute("rollupPolicy", "String", (String)null, (String[])null, false);
                this.access = (String)_parser.getAttribute("access", "String", (String)null, _access_values, true);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.MemberGrant.class, 0, 0);
                this.memberGrants = new MondrianDef.MemberGrant[_tempArray.length];

                for(int _i = 0; _i < this.memberGrants.length; ++_i) {
                    this.memberGrants[_i] = (MondrianDef.MemberGrant)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "HierarchyGrant";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "hierarchy", this.hierarchy, _indent + 1);
            displayAttribute(_out, "topLevel", this.topLevel, _indent + 1);
            displayAttribute(_out, "bottomLevel", this.bottomLevel, _indent + 1);
            displayAttribute(_out, "rollupPolicy", this.rollupPolicy, _indent + 1);
            displayAttribute(_out, "access", this.access, _indent + 1);
            displayElementArray(_out, "memberGrants", this.memberGrants, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("HierarchyGrant", (new XMLAttrVector()).add("hierarchy", this.hierarchy).add("topLevel", this.topLevel).add("bottomLevel", this.bottomLevel).add("rollupPolicy", this.rollupPolicy).add("access", this.access));
            displayXMLElementArray(_out, this.memberGrants);
            _out.endTag("HierarchyGrant");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.HierarchyGrant _cother = (MondrianDef.HierarchyGrant)_other;
            boolean _diff = displayAttributeDiff("hierarchy", this.hierarchy, _cother.hierarchy, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("topLevel", this.topLevel, _cother.topLevel, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("bottomLevel", this.bottomLevel, _cother.bottomLevel, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("rollupPolicy", this.rollupPolicy, _cother.rollupPolicy, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("memberGrants", this.memberGrants, _cother.memberGrants, _out, _indent + 1);
            return _diff;
        }
    }

    public static class DimensionGrant extends ElementDef implements MondrianDef.Grant {
        public static final String[] _access_values = new String[]{"all", "custom", "none", "all_dimensions"};
        public String access;
        public String dimension;

        public DimensionGrant() {
        }

        public DimensionGrant(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.dimension = (String)_parser.getAttribute("dimension", "String", (String)null, (String[])null, true);
                this.access = (String)_parser.getAttribute("access", "String", (String)null, _access_values, true);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "DimensionGrant";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "dimension", this.dimension, _indent + 1);
            displayAttribute(_out, "access", this.access, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("DimensionGrant", (new XMLAttrVector()).add("dimension", this.dimension).add("access", this.access));
            _out.endTag("DimensionGrant");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.DimensionGrant _cother = (MondrianDef.DimensionGrant)_other;
            boolean _diff = displayAttributeDiff("dimension", this.dimension, _cother.dimension, _out, _indent + 1);
            return _diff;
        }
    }

    public static class CubeGrant extends ElementDef implements MondrianDef.Grant {
        public static final String[] _access_values = new String[]{"all", "custom", "none", "all_dimensions"};
        public String access;
        public String cube;
        public MondrianDef.DimensionGrant[] dimensionGrants;
        public MondrianDef.HierarchyGrant[] hierarchyGrants;

        public CubeGrant() {
        }

        public CubeGrant(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.cube = (String)_parser.getAttribute("cube", "String", (String)null, (String[])null, true);
                this.access = (String)_parser.getAttribute("access", "String", (String)null, _access_values, true);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.DimensionGrant.class, 0, 0);
                this.dimensionGrants = new MondrianDef.DimensionGrant[_tempArray.length];

                int _i;
                for(_i = 0; _i < this.dimensionGrants.length; ++_i) {
                    this.dimensionGrants[_i] = (MondrianDef.DimensionGrant)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.HierarchyGrant.class, 0, 0);
                this.hierarchyGrants = new MondrianDef.HierarchyGrant[_tempArray.length];

                for(_i = 0; _i < this.hierarchyGrants.length; ++_i) {
                    this.hierarchyGrants[_i] = (MondrianDef.HierarchyGrant)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "CubeGrant";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "cube", this.cube, _indent + 1);
            displayAttribute(_out, "access", this.access, _indent + 1);
            displayElementArray(_out, "dimensionGrants", this.dimensionGrants, _indent + 1);
            displayElementArray(_out, "hierarchyGrants", this.hierarchyGrants, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("CubeGrant", (new XMLAttrVector()).add("cube", this.cube).add("access", this.access));
            displayXMLElementArray(_out, this.dimensionGrants);
            displayXMLElementArray(_out, this.hierarchyGrants);
            _out.endTag("CubeGrant");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.CubeGrant _cother = (MondrianDef.CubeGrant)_other;
            boolean _diff = displayAttributeDiff("cube", this.cube, _cother.cube, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("dimensionGrants", this.dimensionGrants, _cother.dimensionGrants, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("hierarchyGrants", this.hierarchyGrants, _cother.hierarchyGrants, _out, _indent + 1);
            return _diff;
        }
    }

    public static class SchemaGrant extends ElementDef implements MondrianDef.Grant {
        public static final String[] _access_values = new String[]{"all", "custom", "none", "all_dimensions"};
        public String access;
        public MondrianDef.CubeGrant[] cubeGrants;

        public SchemaGrant() {
        }

        public SchemaGrant(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.access = (String)_parser.getAttribute("access", "String", (String)null, _access_values, true);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.CubeGrant.class, 0, 0);
                this.cubeGrants = new MondrianDef.CubeGrant[_tempArray.length];

                for(int _i = 0; _i < this.cubeGrants.length; ++_i) {
                    this.cubeGrants[_i] = (MondrianDef.CubeGrant)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "SchemaGrant";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "access", this.access, _indent + 1);
            displayElementArray(_out, "cubeGrants", this.cubeGrants, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("SchemaGrant", (new XMLAttrVector()).add("access", this.access));
            displayXMLElementArray(_out, this.cubeGrants);
            _out.endTag("SchemaGrant");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.SchemaGrant _cother = (MondrianDef.SchemaGrant)_other;
            boolean _diff = displayElementArrayDiff("cubeGrants", this.cubeGrants, _cother.cubeGrants, _out, _indent + 1);
            return _diff;
        }
    }

    public interface Grant extends NodeDef {
    }

    public static class Role extends ElementDef {
        public String name;
        public MondrianDef.Annotations annotations;
        public MondrianDef.SchemaGrant[] schemaGrants;
        public MondrianDef.Union union;
        public MondrianDef.RoleMember[] members;

        public Role() {
        }

        public Role(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.annotations = (MondrianDef.Annotations)_parser.getElement(MondrianDef.Annotations.class, false);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.SchemaGrant.class, 0, 0);
                this.schemaGrants = new MondrianDef.SchemaGrant[_tempArray.length];

                int _i;
                for(_i = 0; _i < this.schemaGrants.length; ++_i) {
                    this.schemaGrants[_i] = (MondrianDef.SchemaGrant)_tempArray[_i];
                }

                this.union = (MondrianDef.Union)_parser.getElement(MondrianDef.Union.class, false);
                _tempArray = _parser.getArray(MondrianDef.RoleMember.class, 0, 0);
                this.members = new MondrianDef.RoleMember[_tempArray.length];

                for(_i = 0; _i < this.members.length; ++_i) {
                    this.members[_i] = (MondrianDef.RoleMember)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "Role";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayElement(_out, "annotations", this.annotations, _indent + 1);
            displayElementArray(_out, "schemaGrants", this.schemaGrants, _indent + 1);
            displayElement(_out, "union", this.union, _indent + 1);
            displayElementArray(_out, "members", this.members, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Role", (new XMLAttrVector()).add("name", this.name));
            displayXMLElement(_out, this.annotations);
            displayXMLElementArray(_out, this.schemaGrants);
            displayXMLElement(_out, this.union);
            displayXMLElementArray(_out, this.members);
            _out.endTag("Role");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Role _cother = (MondrianDef.Role)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayElementDiff("annotations", this.annotations, _cother.annotations, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("schemaGrants", this.schemaGrants, _cother.schemaGrants, _out, _indent + 1);
            _diff = _diff && displayElementDiff("union", this.union, _cother.union, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("members", this.members, _cother.members, _out, _indent + 1);
            return _diff;
        }
    }

    public static class MeasureExpression extends MondrianDef.ExpressionView {
        public MeasureExpression() {
        }

        public MeasureExpression(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.SQL.class, 1, 0);
                this.expressions = new MondrianDef.SQL[_tempArray.length];

                for(int _i = 0; _i < this.expressions.length; ++_i) {
                    this.expressions[_i] = (MondrianDef.SQL)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "MeasureExpression";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayElementArray(_out, "expressions", this.expressions, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("MeasureExpression", new XMLAttrVector());
            displayXMLElementArray(_out, this.expressions);
            _out.endTag("MeasureExpression");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.MeasureExpression _cother = (MondrianDef.MeasureExpression)_other;
            boolean _diff = displayElementArrayDiff("expressions", this.expressions, _cother.expressions, _out, _indent + 1);
            return _diff;
        }
    }

    public static class CaptionExpression extends MondrianDef.ExpressionView {
        public CaptionExpression() {
        }

        public CaptionExpression(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.SQL.class, 1, 0);
                this.expressions = new MondrianDef.SQL[_tempArray.length];

                for(int _i = 0; _i < this.expressions.length; ++_i) {
                    this.expressions[_i] = (MondrianDef.SQL)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "CaptionExpression";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayElementArray(_out, "expressions", this.expressions, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("CaptionExpression", new XMLAttrVector());
            displayXMLElementArray(_out, this.expressions);
            _out.endTag("CaptionExpression");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.CaptionExpression _cother = (MondrianDef.CaptionExpression)_other;
            boolean _diff = displayElementArrayDiff("expressions", this.expressions, _cother.expressions, _out, _indent + 1);
            return _diff;
        }
    }

    public static class NameExpression extends MondrianDef.ExpressionView {
        public NameExpression() {
        }

        public NameExpression(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.SQL.class, 1, 0);
                this.expressions = new MondrianDef.SQL[_tempArray.length];

                for(int _i = 0; _i < this.expressions.length; ++_i) {
                    this.expressions[_i] = (MondrianDef.SQL)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "NameExpression";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayElementArray(_out, "expressions", this.expressions, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("NameExpression", new XMLAttrVector());
            displayXMLElementArray(_out, this.expressions);
            _out.endTag("NameExpression");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.NameExpression _cother = (MondrianDef.NameExpression)_other;
            boolean _diff = displayElementArrayDiff("expressions", this.expressions, _cother.expressions, _out, _indent + 1);
            return _diff;
        }
    }

    public static class OrdinalExpression extends MondrianDef.ExpressionView {
        public OrdinalExpression() {
        }

        public OrdinalExpression(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.SQL.class, 1, 0);
                this.expressions = new MondrianDef.SQL[_tempArray.length];

                for(int _i = 0; _i < this.expressions.length; ++_i) {
                    this.expressions[_i] = (MondrianDef.SQL)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "OrdinalExpression";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayElementArray(_out, "expressions", this.expressions, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("OrdinalExpression", new XMLAttrVector());
            displayXMLElementArray(_out, this.expressions);
            _out.endTag("OrdinalExpression");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.OrdinalExpression _cother = (MondrianDef.OrdinalExpression)_other;
            boolean _diff = displayElementArrayDiff("expressions", this.expressions, _cother.expressions, _out, _indent + 1);
            return _diff;
        }
    }

    public static class ParentExpression extends MondrianDef.ExpressionView {
        public ParentExpression() {
        }

        public ParentExpression(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.SQL.class, 1, 0);
                this.expressions = new MondrianDef.SQL[_tempArray.length];

                for(int _i = 0; _i < this.expressions.length; ++_i) {
                    this.expressions[_i] = (MondrianDef.SQL)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "ParentExpression";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayElementArray(_out, "expressions", this.expressions, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("ParentExpression", new XMLAttrVector());
            displayXMLElementArray(_out, this.expressions);
            _out.endTag("ParentExpression");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.ParentExpression _cother = (MondrianDef.ParentExpression)_other;
            boolean _diff = displayElementArrayDiff("expressions", this.expressions, _cother.expressions, _out, _indent + 1);
            return _diff;
        }
    }

    public static class KeyExpression extends MondrianDef.ExpressionView {
        public KeyExpression() {
        }

        public KeyExpression(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.SQL.class, 1, 0);
                this.expressions = new MondrianDef.SQL[_tempArray.length];

                for(int _i = 0; _i < this.expressions.length; ++_i) {
                    this.expressions[_i] = (MondrianDef.SQL)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "KeyExpression";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayElementArray(_out, "expressions", this.expressions, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("KeyExpression", new XMLAttrVector());
            displayXMLElementArray(_out, this.expressions);
            _out.endTag("KeyExpression");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.KeyExpression _cother = (MondrianDef.KeyExpression)_other;
            boolean _diff = displayElementArrayDiff("expressions", this.expressions, _cother.expressions, _out, _indent + 1);
            return _diff;
        }
    }

    public abstract static class ExpressionView extends ElementDef implements MondrianDef.Expression {
        public MondrianDef.SQL[] expressions;

        public ExpressionView() {
        }

        public ExpressionView(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.SQL.class, 1, 0);
                this.expressions = new MondrianDef.SQL[_tempArray.length];

                for(int _i = 0; _i < this.expressions.length; ++_i) {
                    this.expressions[_i] = (MondrianDef.SQL)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "ExpressionView";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayElementArray(_out, "expressions", this.expressions, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("ExpressionView", new XMLAttrVector());
            displayXMLElementArray(_out, this.expressions);
            _out.endTag("ExpressionView");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.ExpressionView _cother = (MondrianDef.ExpressionView)_other;
            boolean _diff = displayElementArrayDiff("expressions", this.expressions, _cother.expressions, _out, _indent + 1);
            return _diff;
        }

        public String toString() {
            return this.expressions[0].cdata;
        }

        public String getExpression(SqlQuery query) {
            return MondrianDef.SQL.toCodeSet(this.expressions).chooseQuery(query.getDialect());
        }

        public String getGenericExpression() {
            for(int i = 0; i < this.expressions.length; ++i) {
                if (this.expressions[i].dialect.equals("generic")) {
                    return this.expressions[i].cdata;
                }
            }

            return this.expressions[0].cdata;
        }

        public String getTableAlias() {
            return null;
        }

        public int hashCode() {
            int h = 17;

            for(int i = 0; i < this.expressions.length; ++i) {
                h = 37 * h + this.expressions[i].hashCode();
            }

            return h;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof MondrianDef.ExpressionView)) {
                return false;
            } else {
                MondrianDef.ExpressionView that = (MondrianDef.ExpressionView)obj;
                if (this.expressions.length != that.expressions.length) {
                    return false;
                } else {
                    for(int i = 0; i < this.expressions.length; ++i) {
                        if (!this.expressions[i].equals(that.expressions[i])) {
                            return false;
                        }
                    }

                    return true;
                }
            }
        }
    }

    public static class Column extends ElementDef implements MondrianDef.Expression {
        public String table;
        public String name;
        private String genericExpression;

        public Column() {
        }

        public Column(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.table = (String)_parser.getAttribute("table", "String", (String)null, (String[])null, false);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "Column";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "table", this.table, _indent + 1);
            displayAttribute(_out, "name", this.name, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Column", (new XMLAttrVector()).add("table", this.table).add("name", this.name));
            _out.endTag("Column");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Column _cother = (MondrianDef.Column)_other;
            boolean _diff = displayAttributeDiff("table", this.table, _cother.table, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            return _diff;
        }

        public Column(String table, String name) {
            this();
            Util.assertTrue(name != null);
            this.table = table;
            this.name = name;
            this.genericExpression = table == null ? name : table + "." + name;
        }

        public String getExpression(SqlQuery query) {
            return query.getDialect().quoteIdentifier(this.table, this.name);
        }

        public String getGenericExpression() {
            return this.genericExpression;
        }

        public String getColumnName() {
            return this.name;
        }

        public String getTableAlias() {
            return this.table;
        }

        public int hashCode() {
            return this.name.hashCode() ^ (this.table == null ? 0 : this.table.hashCode());
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof MondrianDef.Column)) {
                return false;
            } else {
                MondrianDef.Column that = (MondrianDef.Column)obj;
                return this.name.equals(that.name) && Util.equals(this.table, that.table);
            }
        }
    }

    public interface Expression extends NodeDef {
        String getExpression(SqlQuery var1);

        String getGenericExpression();

        String getTableAlias();
    }

    public static class AggMeasure extends ElementDef {
        public String column;
        public String name;
        public String rollupType;

        public AggMeasure() {
        }

        public AggMeasure(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.column = (String)_parser.getAttribute("column", "String", (String)null, (String[])null, true);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.rollupType = (String)_parser.getAttribute("rollupType", "String", (String)null, (String[])null, false);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "AggMeasure";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "column", this.column, _indent + 1);
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "rollupType", this.rollupType, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("AggMeasure", (new XMLAttrVector()).add("column", this.column).add("name", this.name).add("rollupType", this.rollupType));
            _out.endTag("AggMeasure");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.AggMeasure _cother = (MondrianDef.AggMeasure)_other;
            boolean _diff = displayAttributeDiff("column", this.column, _cother.column, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("rollupType", this.rollupType, _cother.rollupType, _out, _indent + 1);
            return _diff;
        }

        public String getNameAttribute() {
            return this.name;
        }

        public String getColumn() {
            return this.column;
        }

        public String getRollupType() {
            return this.rollupType;
        }
    }

    public static class AggLevelProperty extends ElementDef {
        public String name;
        public String column;

        public AggLevelProperty() {
        }

        public AggLevelProperty(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, false);
                this.column = (String)_parser.getAttribute("column", "String", (String)null, (String[])null, false);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "AggLevelProperty";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "column", this.column, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("AggLevelProperty", (new XMLAttrVector()).add("name", this.name).add("column", this.column));
            _out.endTag("AggLevelProperty");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.AggLevelProperty _cother = (MondrianDef.AggLevelProperty)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("column", this.column, _cother.column, _out, _indent + 1);
            return _diff;
        }
    }

    public static class AggLevel extends ElementDef {
        public String column;
        public String ordinalColumn;
        public String captionColumn;
        public String name;
        public String nameColumn;
        public Boolean collapsed;
        public MondrianDef.AggLevelProperty[] properties;

        public AggLevel() {
        }

        public AggLevel(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.column = (String)_parser.getAttribute("column", "String", (String)null, (String[])null, true);
                this.ordinalColumn = (String)_parser.getAttribute("ordinalColumn", "String", (String)null, (String[])null, false);
                this.captionColumn = (String)_parser.getAttribute("captionColumn", "String", (String)null, (String[])null, false);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.nameColumn = (String)_parser.getAttribute("nameColumn", "String", (String)null, (String[])null, false);
                this.collapsed = (Boolean)_parser.getAttribute("collapsed", "Boolean", "true", (String[])null, false);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.AggLevelProperty.class, 0, 0);
                this.properties = new MondrianDef.AggLevelProperty[_tempArray.length];

                for(int _i = 0; _i < this.properties.length; ++_i) {
                    this.properties[_i] = (MondrianDef.AggLevelProperty)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "AggLevel";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "column", this.column, _indent + 1);
            displayAttribute(_out, "ordinalColumn", this.ordinalColumn, _indent + 1);
            displayAttribute(_out, "captionColumn", this.captionColumn, _indent + 1);
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "nameColumn", this.nameColumn, _indent + 1);
            displayAttribute(_out, "collapsed", this.collapsed, _indent + 1);
            displayElementArray(_out, "properties", this.properties, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("AggLevel", (new XMLAttrVector()).add("column", this.column).add("ordinalColumn", this.ordinalColumn).add("captionColumn", this.captionColumn).add("name", this.name).add("nameColumn", this.nameColumn).add("collapsed", this.collapsed));
            displayXMLElementArray(_out, this.properties);
            _out.endTag("AggLevel");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.AggLevel _cother = (MondrianDef.AggLevel)_other;
            boolean _diff = displayAttributeDiff("column", this.column, _cother.column, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("ordinalColumn", this.ordinalColumn, _cother.ordinalColumn, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("captionColumn", this.captionColumn, _cother.captionColumn, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("nameColumn", this.nameColumn, _cother.nameColumn, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("collapsed", this.collapsed, _cother.collapsed, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("properties", this.properties, _cother.properties, _out, _indent + 1);
            return _diff;
        }

        public String getNameAttribute() {
            return this.name;
        }

        public String getColumnName() {
            return this.column;
        }

        public boolean isCollapsed() {
            return this.collapsed;
        }
    }

    public static class AggForeignKey extends ElementDef {
        public String factColumn;
        public String aggColumn;

        public AggForeignKey() {
        }

        public AggForeignKey(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.factColumn = (String)_parser.getAttribute("factColumn", "String", (String)null, (String[])null, true);
                this.aggColumn = (String)_parser.getAttribute("aggColumn", "String", (String)null, (String[])null, true);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "AggForeignKey";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "factColumn", this.factColumn, _indent + 1);
            displayAttribute(_out, "aggColumn", this.aggColumn, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("AggForeignKey", (new XMLAttrVector()).add("factColumn", this.factColumn).add("aggColumn", this.aggColumn));
            _out.endTag("AggForeignKey");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.AggForeignKey _cother = (MondrianDef.AggForeignKey)_other;
            boolean _diff = displayAttributeDiff("factColumn", this.factColumn, _cother.factColumn, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("aggColumn", this.aggColumn, _cother.aggColumn, _out, _indent + 1);
            return _diff;
        }

        public String getFactFKColumnName() {
            return this.factColumn;
        }

        public String getAggregateFKColumnName() {
            return this.aggColumn;
        }
    }

    public static class AggIgnoreColumn extends MondrianDef.AggColumnName {
        public AggIgnoreColumn() {
        }

        public AggIgnoreColumn(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.column = (String)_parser.getAttribute("column", "String", (String)null, (String[])null, true);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "AggIgnoreColumn";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "column", this.column, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("AggIgnoreColumn", (new XMLAttrVector()).add("column", this.column));
            _out.endTag("AggIgnoreColumn");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.AggIgnoreColumn _cother = (MondrianDef.AggIgnoreColumn)_other;
            return true;
        }
    }

    public static class AggMeasureFactCount extends MondrianDef.AggColumnName {
        public String factColumn;

        public AggMeasureFactCount() {
        }

        public AggMeasureFactCount(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.factColumn = (String)_parser.getAttribute("factColumn", "String", (String)null, (String[])null, true);
                this.column = (String)_parser.getAttribute("column", "String", (String)null, (String[])null, true);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "AggMeasureFactCount";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "factColumn", this.factColumn, _indent + 1);
            displayAttribute(_out, "column", this.column, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("AggMeasureFactCount", (new XMLAttrVector()).add("factColumn", this.factColumn).add("column", this.column));
            _out.endTag("AggMeasureFactCount");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.AggMeasureFactCount _cother = (MondrianDef.AggMeasureFactCount)_other;
            boolean _diff = displayAttributeDiff("factColumn", this.factColumn, _cother.factColumn, _out, _indent + 1);
            return _diff;
        }

        public String getFactColumn() {
            return this.factColumn;
        }
    }

    public static class AggFactCount extends MondrianDef.AggColumnName {
        public AggFactCount() {
        }

        public AggFactCount(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.column = (String)_parser.getAttribute("column", "String", (String)null, (String[])null, true);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "AggFactCount";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "column", this.column, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("AggFactCount", (new XMLAttrVector()).add("column", this.column));
            _out.endTag("AggFactCount");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.AggFactCount _cother = (MondrianDef.AggFactCount)_other;
            return true;
        }
    }

    public abstract static class AggColumnName extends ElementDef {
        public String column;

        public AggColumnName() {
        }

        public AggColumnName(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.column = (String)_parser.getAttribute("column", "String", (String)null, (String[])null, true);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "AggColumnName";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "column", this.column, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("AggColumnName", (new XMLAttrVector()).add("column", this.column));
            _out.endTag("AggColumnName");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.AggColumnName _cother = (MondrianDef.AggColumnName)_other;
            boolean _diff = displayAttributeDiff("column", this.column, _cother.column, _out, _indent + 1);
            return _diff;
        }

        public String getColumnName() {
            return this.column;
        }
    }

    public static class AggExclude extends ElementDef {
        public String pattern;
        public String name;
        public Boolean ignorecase;

        public AggExclude() {
        }

        public AggExclude(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.pattern = (String)_parser.getAttribute("pattern", "String", (String)null, (String[])null, false);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, false);
                this.ignorecase = (Boolean)_parser.getAttribute("ignorecase", "Boolean", "true", (String[])null, false);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "AggExclude";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "pattern", this.pattern, _indent + 1);
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "ignorecase", this.ignorecase, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("AggExclude", (new XMLAttrVector()).add("pattern", this.pattern).add("name", this.name).add("ignorecase", this.ignorecase));
            _out.endTag("AggExclude");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.AggExclude _cother = (MondrianDef.AggExclude)_other;
            boolean _diff = displayAttributeDiff("pattern", this.pattern, _cother.pattern, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("ignorecase", this.ignorecase, _cother.ignorecase, _out, _indent + 1);
            return _diff;
        }

        public String getNameAttribute() {
            return this.name;
        }

        public String getPattern() {
            return this.pattern;
        }

        public boolean isIgnoreCase() {
            return this.ignorecase;
        }
    }

    public static class AggPattern extends MondrianDef.AggTable {
        public String pattern;
        public MondrianDef.AggExclude[] excludes;

        public AggPattern() {
        }

        public AggPattern(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.pattern = (String)_parser.getAttribute("pattern", "String", (String)null, (String[])null, true);
                this.ignorecase = (Boolean)_parser.getAttribute("ignorecase", "Boolean", "true", (String[])null, false);
                this.factcount = (MondrianDef.AggFactCount)_parser.getElement(MondrianDef.AggFactCount.class, true);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.AggMeasureFactCount.class, 0, 0);
                this.measuresfactcount = new MondrianDef.AggMeasureFactCount[_tempArray.length];

                int _i;
                for(_i = 0; _i < this.measuresfactcount.length; ++_i) {
                    this.measuresfactcount[_i] = (MondrianDef.AggMeasureFactCount)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.AggIgnoreColumn.class, 0, 0);
                this.ignoreColumns = new MondrianDef.AggIgnoreColumn[_tempArray.length];

                for(_i = 0; _i < this.ignoreColumns.length; ++_i) {
                    this.ignoreColumns[_i] = (MondrianDef.AggIgnoreColumn)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.AggForeignKey.class, 0, 0);
                this.foreignKeys = new MondrianDef.AggForeignKey[_tempArray.length];

                for(_i = 0; _i < this.foreignKeys.length; ++_i) {
                    this.foreignKeys[_i] = (MondrianDef.AggForeignKey)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.AggMeasure.class, 0, 0);
                this.measures = new MondrianDef.AggMeasure[_tempArray.length];

                for(_i = 0; _i < this.measures.length; ++_i) {
                    this.measures[_i] = (MondrianDef.AggMeasure)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.AggLevel.class, 0, 0);
                this.levels = new MondrianDef.AggLevel[_tempArray.length];

                for(_i = 0; _i < this.levels.length; ++_i) {
                    this.levels[_i] = (MondrianDef.AggLevel)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.AggExclude.class, 0, 0);
                this.excludes = new MondrianDef.AggExclude[_tempArray.length];

                for(_i = 0; _i < this.excludes.length; ++_i) {
                    this.excludes[_i] = (MondrianDef.AggExclude)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "AggPattern";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "pattern", this.pattern, _indent + 1);
            displayAttribute(_out, "ignorecase", this.ignorecase, _indent + 1);
            displayElement(_out, "factcount", this.factcount, _indent + 1);
            displayElementArray(_out, "measuresfactcount", this.measuresfactcount, _indent + 1);
            displayElementArray(_out, "ignoreColumns", this.ignoreColumns, _indent + 1);
            displayElementArray(_out, "foreignKeys", this.foreignKeys, _indent + 1);
            displayElementArray(_out, "measures", this.measures, _indent + 1);
            displayElementArray(_out, "levels", this.levels, _indent + 1);
            displayElementArray(_out, "excludes", this.excludes, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("AggPattern", (new XMLAttrVector()).add("pattern", this.pattern).add("ignorecase", this.ignorecase));
            displayXMLElement(_out, this.factcount);
            displayXMLElementArray(_out, this.measuresfactcount);
            displayXMLElementArray(_out, this.ignoreColumns);
            displayXMLElementArray(_out, this.foreignKeys);
            displayXMLElementArray(_out, this.measures);
            displayXMLElementArray(_out, this.levels);
            displayXMLElementArray(_out, this.excludes);
            _out.endTag("AggPattern");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.AggPattern _cother = (MondrianDef.AggPattern)_other;
            boolean _diff = displayAttributeDiff("pattern", this.pattern, _cother.pattern, _out, _indent + 1);
            _diff = _diff && displayElementDiff("factcount", this.factcount, _cother.factcount, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("measuresfactcount", this.measuresfactcount, _cother.measuresfactcount, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("ignoreColumns", this.ignoreColumns, _cother.ignoreColumns, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("foreignKeys", this.foreignKeys, _cother.foreignKeys, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("measures", this.measures, _cother.measures, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("levels", this.levels, _cother.levels, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("excludes", this.excludes, _cother.excludes, _out, _indent + 1);
            return _diff;
        }

        public String getPattern() {
            return this.pattern;
        }

        public MondrianDef.AggExclude[] getAggExcludes() {
            return this.excludes;
        }
    }

    public static class AggName extends MondrianDef.AggTable {
        public String name;
        public String approxRowCount;

        public AggName() {
        }

        public AggName(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.approxRowCount = (String)_parser.getAttribute("approxRowCount", "String", (String)null, (String[])null, false);
                this.ignorecase = (Boolean)_parser.getAttribute("ignorecase", "Boolean", "true", (String[])null, false);
                this.factcount = (MondrianDef.AggFactCount)_parser.getElement(MondrianDef.AggFactCount.class, true);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.AggMeasureFactCount.class, 0, 0);
                this.measuresfactcount = new MondrianDef.AggMeasureFactCount[_tempArray.length];

                int _i;
                for(_i = 0; _i < this.measuresfactcount.length; ++_i) {
                    this.measuresfactcount[_i] = (MondrianDef.AggMeasureFactCount)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.AggIgnoreColumn.class, 0, 0);
                this.ignoreColumns = new MondrianDef.AggIgnoreColumn[_tempArray.length];

                for(_i = 0; _i < this.ignoreColumns.length; ++_i) {
                    this.ignoreColumns[_i] = (MondrianDef.AggIgnoreColumn)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.AggForeignKey.class, 0, 0);
                this.foreignKeys = new MondrianDef.AggForeignKey[_tempArray.length];

                for(_i = 0; _i < this.foreignKeys.length; ++_i) {
                    this.foreignKeys[_i] = (MondrianDef.AggForeignKey)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.AggMeasure.class, 0, 0);
                this.measures = new MondrianDef.AggMeasure[_tempArray.length];

                for(_i = 0; _i < this.measures.length; ++_i) {
                    this.measures[_i] = (MondrianDef.AggMeasure)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.AggLevel.class, 0, 0);
                this.levels = new MondrianDef.AggLevel[_tempArray.length];

                for(_i = 0; _i < this.levels.length; ++_i) {
                    this.levels[_i] = (MondrianDef.AggLevel)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "AggName";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "approxRowCount", this.approxRowCount, _indent + 1);
            displayAttribute(_out, "ignorecase", this.ignorecase, _indent + 1);
            displayElement(_out, "factcount", this.factcount, _indent + 1);
            displayElementArray(_out, "measuresfactcount", this.measuresfactcount, _indent + 1);
            displayElementArray(_out, "ignoreColumns", this.ignoreColumns, _indent + 1);
            displayElementArray(_out, "foreignKeys", this.foreignKeys, _indent + 1);
            displayElementArray(_out, "measures", this.measures, _indent + 1);
            displayElementArray(_out, "levels", this.levels, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("AggName", (new XMLAttrVector()).add("name", this.name).add("approxRowCount", this.approxRowCount).add("ignorecase", this.ignorecase));
            displayXMLElement(_out, this.factcount);
            displayXMLElementArray(_out, this.measuresfactcount);
            displayXMLElementArray(_out, this.ignoreColumns);
            displayXMLElementArray(_out, this.foreignKeys);
            displayXMLElementArray(_out, this.measures);
            displayXMLElementArray(_out, this.levels);
            _out.endTag("AggName");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.AggName _cother = (MondrianDef.AggName)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("approxRowCount", this.approxRowCount, _cother.approxRowCount, _out, _indent + 1);
            _diff = _diff && displayElementDiff("factcount", this.factcount, _cother.factcount, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("measuresfactcount", this.measuresfactcount, _cother.measuresfactcount, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("ignoreColumns", this.ignoreColumns, _cother.ignoreColumns, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("foreignKeys", this.foreignKeys, _cother.foreignKeys, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("measures", this.measures, _cother.measures, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("levels", this.levels, _cother.levels, _out, _indent + 1);
            return _diff;
        }

        public String getNameAttribute() {
            return this.name;
        }

        public String getApproxRowCountAttribute() {
            return this.approxRowCount;
        }
    }

    public abstract static class AggTable extends ElementDef {
        public Boolean ignorecase;
        public MondrianDef.AggFactCount factcount;
        public MondrianDef.AggMeasureFactCount[] measuresfactcount;
        public MondrianDef.AggIgnoreColumn[] ignoreColumns;
        public MondrianDef.AggForeignKey[] foreignKeys;
        public MondrianDef.AggMeasure[] measures;
        public MondrianDef.AggLevel[] levels;

        public AggTable() {
        }

        public AggTable(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.ignorecase = (Boolean)_parser.getAttribute("ignorecase", "Boolean", "true", (String[])null, false);
                this.factcount = (MondrianDef.AggFactCount)_parser.getElement(MondrianDef.AggFactCount.class, true);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.AggMeasureFactCount.class, 0, 0);
                this.measuresfactcount = new MondrianDef.AggMeasureFactCount[_tempArray.length];

                int _i;
                for(_i = 0; _i < this.measuresfactcount.length; ++_i) {
                    this.measuresfactcount[_i] = (MondrianDef.AggMeasureFactCount)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.AggIgnoreColumn.class, 0, 0);
                this.ignoreColumns = new MondrianDef.AggIgnoreColumn[_tempArray.length];

                for(_i = 0; _i < this.ignoreColumns.length; ++_i) {
                    this.ignoreColumns[_i] = (MondrianDef.AggIgnoreColumn)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.AggForeignKey.class, 0, 0);
                this.foreignKeys = new MondrianDef.AggForeignKey[_tempArray.length];

                for(_i = 0; _i < this.foreignKeys.length; ++_i) {
                    this.foreignKeys[_i] = (MondrianDef.AggForeignKey)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.AggMeasure.class, 0, 0);
                this.measures = new MondrianDef.AggMeasure[_tempArray.length];

                for(_i = 0; _i < this.measures.length; ++_i) {
                    this.measures[_i] = (MondrianDef.AggMeasure)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.AggLevel.class, 0, 0);
                this.levels = new MondrianDef.AggLevel[_tempArray.length];

                for(_i = 0; _i < this.levels.length; ++_i) {
                    this.levels[_i] = (MondrianDef.AggLevel)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "AggTable";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "ignorecase", this.ignorecase, _indent + 1);
            displayElement(_out, "factcount", this.factcount, _indent + 1);
            displayElementArray(_out, "measuresfactcount", this.measuresfactcount, _indent + 1);
            displayElementArray(_out, "ignoreColumns", this.ignoreColumns, _indent + 1);
            displayElementArray(_out, "foreignKeys", this.foreignKeys, _indent + 1);
            displayElementArray(_out, "measures", this.measures, _indent + 1);
            displayElementArray(_out, "levels", this.levels, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("AggTable", (new XMLAttrVector()).add("ignorecase", this.ignorecase));
            displayXMLElement(_out, this.factcount);
            displayXMLElementArray(_out, this.measuresfactcount);
            displayXMLElementArray(_out, this.ignoreColumns);
            displayXMLElementArray(_out, this.foreignKeys);
            displayXMLElementArray(_out, this.measures);
            displayXMLElementArray(_out, this.levels);
            _out.endTag("AggTable");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.AggTable _cother = (MondrianDef.AggTable)_other;
            boolean _diff = displayAttributeDiff("ignorecase", this.ignorecase, _cother.ignorecase, _out, _indent + 1);
            _diff = _diff && displayElementDiff("factcount", this.factcount, _cother.factcount, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("measuresfactcount", this.measuresfactcount, _cother.measuresfactcount, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("ignoreColumns", this.ignoreColumns, _cother.ignoreColumns, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("foreignKeys", this.foreignKeys, _cother.foreignKeys, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("measures", this.measures, _cother.measures, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("levels", this.levels, _cother.levels, _out, _indent + 1);
            return _diff;
        }

        public boolean isIgnoreCase() {
            return this.ignorecase;
        }

        public MondrianDef.AggFactCount getAggFactCount() {
            return this.factcount;
        }

        public MondrianDef.AggMeasureFactCount[] getMeasuresFactCount() {
            return this.measuresfactcount;
        }

        public MondrianDef.AggIgnoreColumn[] getAggIgnoreColumns() {
            return this.ignoreColumns;
        }

        public MondrianDef.AggForeignKey[] getAggForeignKeys() {
            return this.foreignKeys;
        }

        public MondrianDef.AggMeasure[] getAggMeasures() {
            return this.measures;
        }

        public MondrianDef.AggLevel[] getAggLevels() {
            return this.levels;
        }
    }

    public static class Value extends ElementDef {
        public String column;
        public String cdata;

        public Value() {
        }

        public Value(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.column = (String)_parser.getAttribute("column", "String", (String)null, (String[])null, true);
                this.cdata = _parser.getText();
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "Value";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "column", this.column, _indent + 1);
            displayString(_out, "cdata", this.cdata, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Value", (new XMLAttrVector()).add("column", this.column));
            _out.cdata(this.cdata);
            _out.endTag("Value");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Value _cother = (MondrianDef.Value)_other;
            boolean _diff = displayAttributeDiff("column", this.column, _cother.column, _out, _indent + 1);
            _diff = _diff && displayStringDiff("cdata", this.cdata, _cother.cdata, _out, _indent + 1);
            return _diff;
        }
    }

    public static class Row extends ElementDef {
        public MondrianDef.Value[] values;

        public Row() {
        }

        public Row(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.Value.class, 0, 0);
                this.values = new MondrianDef.Value[_tempArray.length];

                for(int _i = 0; _i < this.values.length; ++_i) {
                    this.values[_i] = (MondrianDef.Value)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "Row";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayElementArray(_out, "values", this.values, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Row", new XMLAttrVector());
            displayXMLElementArray(_out, this.values);
            _out.endTag("Row");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Row _cother = (MondrianDef.Row)_other;
            boolean _diff = displayElementArrayDiff("values", this.values, _cother.values, _out, _indent + 1);
            return _diff;
        }
    }

    public static class Rows extends ElementDef {
        public MondrianDef.Row[] array;

        public Rows() {
        }

        public Rows(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.Row.class, 0, 0);
                this.array = new MondrianDef.Row[_tempArray.length];

                for(int _i = 0; _i < this.array.length; ++_i) {
                    this.array[_i] = (MondrianDef.Row)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "Rows";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayElementArray(_out, "array", this.array, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Rows", new XMLAttrVector());
            displayXMLElementArray(_out, this.array);
            _out.endTag("Rows");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Rows _cother = (MondrianDef.Rows)_other;
            boolean _diff = displayElementArrayDiff("array", this.array, _cother.array, _out, _indent + 1);
            return _diff;
        }
    }

    public static class ColumnDef extends ElementDef {
        public String name;
        public static final String[] _type_values = new String[]{"String", "Numeric", "Integer", "Boolean", "Date", "Time", "Timestamp"};
        public String type;

        public ColumnDef() {
        }

        public ColumnDef(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.type = (String)_parser.getAttribute("type", "String", (String)null, _type_values, true);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "ColumnDef";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "type", this.type, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("ColumnDef", (new XMLAttrVector()).add("name", this.name).add("type", this.type));
            _out.endTag("ColumnDef");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.ColumnDef _cother = (MondrianDef.ColumnDef)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("type", this.type, _cother.type, _out, _indent + 1);
            return _diff;
        }
    }

    public static class ColumnDefs extends ElementDef {
        public MondrianDef.ColumnDef[] array;

        public ColumnDefs() {
        }

        public ColumnDefs(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.ColumnDef.class, 0, 0);
                this.array = new MondrianDef.ColumnDef[_tempArray.length];

                for(int _i = 0; _i < this.array.length; ++_i) {
                    this.array[_i] = (MondrianDef.ColumnDef)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "ColumnDefs";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayElementArray(_out, "array", this.array, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("ColumnDefs", new XMLAttrVector());
            displayXMLElementArray(_out, this.array);
            _out.endTag("ColumnDefs");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.ColumnDefs _cother = (MondrianDef.ColumnDefs)_other;
            boolean _diff = displayElementArrayDiff("array", this.array, _cother.array, _out, _indent + 1);
            return _diff;
        }
    }

    public static class InlineTable extends MondrianDef.Relation {
        public String alias;
        public MondrianDef.ColumnDefs columnDefs;
        public MondrianDef.Rows rows;

        public InlineTable() {
        }

        public InlineTable(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.alias = (String)_parser.getAttribute("alias", "String", (String)null, (String[])null, false);
                this.columnDefs = (MondrianDef.ColumnDefs)_parser.getElement(MondrianDef.ColumnDefs.class, true);
                this.rows = (MondrianDef.Rows)_parser.getElement(MondrianDef.Rows.class, true);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "InlineTable";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "alias", this.alias, _indent + 1);
            displayElement(_out, "columnDefs", this.columnDefs, _indent + 1);
            displayElement(_out, "rows", this.rows, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("InlineTable", (new XMLAttrVector()).add("alias", this.alias));
            displayXMLElement(_out, this.columnDefs);
            displayXMLElement(_out, this.rows);
            _out.endTag("InlineTable");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.InlineTable _cother = (MondrianDef.InlineTable)_other;
            boolean _diff = displayAttributeDiff("alias", this.alias, _cother.alias, _out, _indent + 1);
            _diff = _diff && displayElementDiff("columnDefs", this.columnDefs, _cother.columnDefs, _out, _indent + 1);
            _diff = _diff && displayElementDiff("rows", this.rows, _cother.rows, _out, _indent + 1);
            return _diff;
        }

        public InlineTable(MondrianDef.InlineTable inlineTable) {
            this.alias = inlineTable.alias;
            this.columnDefs = new MondrianDef.ColumnDefs();
            this.columnDefs.array = (MondrianDef.ColumnDef[])inlineTable.columnDefs.array.clone();
            this.rows = new MondrianDef.Rows();
            this.rows.array = (MondrianDef.Row[])inlineTable.rows.array.clone();
        }

        public String getAlias() {
            return this.alias;
        }

        public String toString() {
            return "<inline data>";
        }

        public MondrianDef.InlineTable find(String seekAlias) {
            return seekAlias.equals(this.alias) ? this : null;
        }

        public boolean equals(Object o) {
            if (o instanceof MondrianDef.InlineTable) {
                MondrianDef.InlineTable that = (MondrianDef.InlineTable)o;
                return this.alias.equals(that.alias);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return this.toString().hashCode();
        }
    }

    public static class Hint extends ElementDef {
        public String type;
        public String cdata;

        public Hint() {
        }

        public Hint(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.type = (String)_parser.getAttribute("type", "String", (String)null, (String[])null, true);
                this.cdata = _parser.getText();
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "Hint";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "type", this.type, _indent + 1);
            displayString(_out, "cdata", this.cdata, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Hint", (new XMLAttrVector()).add("type", this.type));
            _out.cdata(this.cdata);
            _out.endTag("Hint");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Hint _cother = (MondrianDef.Hint)_other;
            boolean _diff = displayAttributeDiff("type", this.type, _cother.type, _out, _indent + 1);
            _diff = _diff && displayStringDiff("cdata", this.cdata, _cother.cdata, _out, _indent + 1);
            return _diff;
        }
    }

    public static class Table extends MondrianDef.Relation {
        public String name;
        public String schema;
        public String alias;
        public MondrianDef.SQL filter;
        public MondrianDef.AggExclude[] aggExcludes;
        public MondrianDef.AggTable[] aggTables;
        public MondrianDef.Hint[] tableHints;
        private Map<String, String> hintMap;

        public Table() {
        }

        public Table(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.schema = (String)_parser.getAttribute("schema", "String", (String)null, (String[])null, false);
                this.alias = (String)_parser.getAttribute("alias", "String", (String)null, (String[])null, false);
                this.filter = (MondrianDef.SQL)_parser.getElement(MondrianDef.SQL.class, false);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.AggExclude.class, 0, 0);
                this.aggExcludes = new MondrianDef.AggExclude[_tempArray.length];

                int _i;
                for(_i = 0; _i < this.aggExcludes.length; ++_i) {
                    this.aggExcludes[_i] = (MondrianDef.AggExclude)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.AggTable.class, 0, 0);
                this.aggTables = new MondrianDef.AggTable[_tempArray.length];

                for(_i = 0; _i < this.aggTables.length; ++_i) {
                    this.aggTables[_i] = (MondrianDef.AggTable)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.Hint.class, 0, 0);
                this.tableHints = new MondrianDef.Hint[_tempArray.length];

                for(_i = 0; _i < this.tableHints.length; ++_i) {
                    this.tableHints[_i] = (MondrianDef.Hint)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "Table";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "schema", this.schema, _indent + 1);
            displayAttribute(_out, "alias", this.alias, _indent + 1);
            displayElement(_out, "filter", this.filter, _indent + 1);
            displayElementArray(_out, "aggExcludes", this.aggExcludes, _indent + 1);
            displayElementArray(_out, "aggTables", this.aggTables, _indent + 1);
            displayElementArray(_out, "tableHints", this.tableHints, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Table", (new XMLAttrVector()).add("name", this.name).add("schema", this.schema).add("alias", this.alias));
            displayXMLElement(_out, this.filter);
            displayXMLElementArray(_out, this.aggExcludes);
            displayXMLElementArray(_out, this.aggTables);
            displayXMLElementArray(_out, this.tableHints);
            _out.endTag("Table");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Table _cother = (MondrianDef.Table)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("schema", this.schema, _cother.schema, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("alias", this.alias, _cother.alias, _out, _indent + 1);
            _diff = _diff && displayElementDiff("filter", this.filter, _cother.filter, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("aggExcludes", this.aggExcludes, _cother.aggExcludes, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("aggTables", this.aggTables, _cother.aggTables, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("tableHints", this.tableHints, _cother.tableHints, _out, _indent + 1);
            return _diff;
        }

        public Table(MondrianDef.Table table) {
            this(table.schema, table.name, table.alias, table.tableHints);
        }

        public Table(String schema, String name, String alias, MondrianDef.Hint[] tablehints) {
            this();
            this.schema = schema;
            this.name = name;
            this.alias = alias;
            this.hintMap = this.buildHintMap(tablehints);
        }

        public Table(MondrianDef.Table tbl, String possibleName) {
            this(tbl.schema, tbl.name, possibleName, tbl.tableHints);
            if (tbl.filter != null) {
                this.filter = new MondrianDef.SQL();
                this.filter.dialect = tbl.filter.dialect;
                if (tbl.filter.cdata != null) {
                    this.filter.cdata = tbl.filter.cdata.replace(tbl.alias == null ? tbl.name : tbl.alias, possibleName);
                }
            }

        }

        private Map<String, String> buildHintMap(MondrianDef.Hint[] th) {
            Map<String, String> h = new HashMap();
            if (th != null) {
                for(int i = 0; i < th.length; ++i) {
                    h.put(th[i].type, th[i].cdata);
                }
            }

            return h;
        }

        public String getAlias() {
            return this.alias != null ? this.alias : this.name;
        }

        public String toString() {
            return this.schema == null ? this.name : this.schema + "." + this.name;
        }

        public MondrianDef.Table find(String seekAlias) {
            return seekAlias.equals(this.name) ? this : (this.alias != null && seekAlias.equals(this.alias) ? this : null);
        }

        public boolean equals(Object o) {
            if (!(o instanceof MondrianDef.Table)) {
                return false;
            } else {
                MondrianDef.Table that = (MondrianDef.Table)o;
                return this.name.equals(that.name) && Util.equals(this.alias, that.alias) && Util.equals(this.schema, that.schema);
            }
        }

        public int hashCode() {
            return this.toString().hashCode();
        }

        public String getFilter() {
            return this.filter == null ? null : this.filter.cdata;
        }

        public MondrianDef.AggExclude[] getAggExcludes() {
            return this.aggExcludes;
        }

        public MondrianDef.AggTable[] getAggTables() {
            return this.aggTables;
        }

        public Map<String, String> getHintMap() {
            if (this.hintMap == null) {
                this.hintMap = this.buildHintMap(this.tableHints);
            }

            return this.hintMap;
        }
    }

    public static class Join extends MondrianDef.RelationOrJoin {
        public String leftAlias;
        public String leftKey;
        public String rightAlias;
        public String rightKey;
        public MondrianDef.RelationOrJoin left;
        public MondrianDef.RelationOrJoin right;

        public Join() {
        }

        public Join(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.leftAlias = (String)_parser.getAttribute("leftAlias", "String", (String)null, (String[])null, false);
                this.leftKey = (String)_parser.getAttribute("leftKey", "String", (String)null, (String[])null, true);
                this.rightAlias = (String)_parser.getAttribute("rightAlias", "String", (String)null, (String[])null, false);
                this.rightKey = (String)_parser.getAttribute("rightKey", "String", (String)null, (String[])null, true);
                this.left = (MondrianDef.RelationOrJoin)_parser.getElement(MondrianDef.RelationOrJoin.class, true);
                this.right = (MondrianDef.RelationOrJoin)_parser.getElement(MondrianDef.RelationOrJoin.class, true);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "Join";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "leftAlias", this.leftAlias, _indent + 1);
            displayAttribute(_out, "leftKey", this.leftKey, _indent + 1);
            displayAttribute(_out, "rightAlias", this.rightAlias, _indent + 1);
            displayAttribute(_out, "rightKey", this.rightKey, _indent + 1);
            displayElement(_out, "left", this.left, _indent + 1);
            displayElement(_out, "right", this.right, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Join", (new XMLAttrVector()).add("leftAlias", this.leftAlias).add("leftKey", this.leftKey).add("rightAlias", this.rightAlias).add("rightKey", this.rightKey));
            displayXMLElement(_out, this.left);
            displayXMLElement(_out, this.right);
            _out.endTag("Join");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Join _cother = (MondrianDef.Join)_other;
            boolean _diff = displayAttributeDiff("leftAlias", this.leftAlias, _cother.leftAlias, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("leftKey", this.leftKey, _cother.leftKey, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("rightAlias", this.rightAlias, _cother.rightAlias, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("rightKey", this.rightKey, _cother.rightKey, _out, _indent + 1);
            _diff = _diff && displayElementDiff("left", this.left, _cother.left, _out, _indent + 1);
            _diff = _diff && displayElementDiff("right", this.right, _cother.right, _out, _indent + 1);
            return _diff;
        }

        public Join(String leftAlias, String leftKey, MondrianDef.RelationOrJoin left, String rightAlias, String rightKey, MondrianDef.RelationOrJoin right) {
            this.leftAlias = leftAlias;
            this.leftKey = leftKey;
            this.left = left;
            this.rightAlias = rightAlias;
            this.rightKey = rightKey;
            this.right = right;
        }

        public String getLeftAlias() {
            if (this.leftAlias != null) {
                return this.leftAlias;
            } else if (this.left instanceof MondrianDef.Relation) {
                return ((MondrianDef.Relation)this.left).getAlias();
            } else {
                throw Util.newInternal("alias is required because " + this.left + " is not a table");
            }
        }

        public String getRightAlias() {
            if (this.rightAlias != null) {
                return this.rightAlias;
            } else if (this.right instanceof MondrianDef.Relation) {
                return ((MondrianDef.Relation)this.right).getAlias();
            } else if (this.right instanceof MondrianDef.Join) {
                return ((MondrianDef.Join)this.right).getLeftAlias();
            } else {
                throw Util.newInternal("alias is required because " + this.right + " is not a table");
            }
        }

        public String toString() {
            return "(" + this.left + ") join (" + this.right + ") on " + this.leftAlias + "." + this.leftKey + " = " + this.rightAlias + "." + this.rightKey;
        }

        public MondrianDef.Relation find(String seekAlias) {
            MondrianDef.Relation relation = this.left.find(seekAlias);
            if (relation == null) {
                relation = this.right.find(seekAlias);
            }

            return relation;
        }
    }

    public static class SchemaView extends MondrianDef.Relation {
        public String source;
        public String alias;

        public SchemaView() {
        }

        public SchemaView(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.source = (String)_parser.getAttribute("source", "String", (String)null, (String[])null, true);
                this.alias = (String)_parser.getAttribute("alias", "String", (String)null, (String[])null, true);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "SchemaView";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "source", this.source, _indent + 1);
            displayAttribute(_out, "alias", this.alias, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("SchemaView", (new XMLAttrVector()).add("source", this.source).add("alias", this.alias));
            _out.endTag("SchemaView");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.SchemaView _cother = (MondrianDef.SchemaView)_other;
            boolean _diff = displayAttributeDiff("source", this.source, _cother.source, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("alias", this.alias, _cother.alias, _out, _indent + 1);
            return _diff;
        }

        public MondrianDef.SchemaView find(String seekAlias) {
            return seekAlias.equals(this.alias) ? this : null;
        }

        public String getAlias() {
            return this.alias;
        }
    }

    public static class SQL extends ElementDef {
        public String dialect;
        public String cdata;

        public SQL() {
        }

        public SQL(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.dialect = (String)_parser.getAttribute("dialect", "String", "generic", (String[])null, true);
                this.cdata = _parser.getText();
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "SQL";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "dialect", this.dialect, _indent + 1);
            displayString(_out, "cdata", this.cdata, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("SQL", (new XMLAttrVector()).add("dialect", this.dialect));
            _out.cdata(this.cdata);
            _out.endTag("SQL");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.SQL _cother = (MondrianDef.SQL)_other;
            boolean _diff = displayAttributeDiff("dialect", this.dialect, _cother.dialect, _out, _indent + 1);
            _diff = _diff && displayStringDiff("cdata", this.cdata, _cother.cdata, _out, _indent + 1);
            return _diff;
        }

        public int hashCode() {
            return this.dialect.hashCode();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof MondrianDef.SQL)) {
                return false;
            } else {
                MondrianDef.SQL that = (MondrianDef.SQL)obj;
                return this.dialect.equals(that.dialect) && Util.equals(this.cdata, that.cdata);
            }
        }

        public static CodeSet toCodeSet(MondrianDef.SQL[] sqls) {
            CodeSet codeSet = new CodeSet();
            MondrianDef.SQL[] var2 = sqls;
            int var3 = sqls.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                MondrianDef.SQL sql = var2[var4];
                codeSet.put(sql.dialect, sql.cdata);
            }

            return codeSet;
        }
    }

    public static class View extends MondrianDef.Relation {
        public String alias;
        public MondrianDef.SQL[] selects;

        public View() {
        }

        public View(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.alias = (String)_parser.getAttribute("alias", "String", (String)null, (String[])null, true);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.SQL.class, 1, 0);
                this.selects = new MondrianDef.SQL[_tempArray.length];

                for(int _i = 0; _i < this.selects.length; ++_i) {
                    this.selects[_i] = (MondrianDef.SQL)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "View";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "alias", this.alias, _indent + 1);
            displayElementArray(_out, "selects", this.selects, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("View", (new XMLAttrVector()).add("alias", this.alias));
            displayXMLElementArray(_out, this.selects);
            _out.endTag("View");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.View _cother = (MondrianDef.View)_other;
            boolean _diff = displayAttributeDiff("alias", this.alias, _cother.alias, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("selects", this.selects, _cother.selects, _out, _indent + 1);
            return _diff;
        }

        public View(MondrianDef.View view) {
            this.alias = view.alias;
            this.selects = (MondrianDef.SQL[])view.selects.clone();
        }

        public String toString() {
            return this.selects[0].cdata;
        }

        public MondrianDef.View find(String seekAlias) {
            return seekAlias.equals(this.alias) ? this : null;
        }

        public String getAlias() {
            return this.alias;
        }

        public CodeSet getCodeSet() {
            return MondrianDef.SQL.toCodeSet(this.selects);
        }

        public void addCode(String dialect, String code) {
            if (this.selects == null) {
                this.selects = new MondrianDef.SQL[1];
            } else {
                MondrianDef.SQL[] olds = this.selects;
                this.selects = new MondrianDef.SQL[olds.length + 1];
                System.arraycopy(olds, 0, this.selects, 0, olds.length);
            }

            MondrianDef.SQL sql = new MondrianDef.SQL();
            sql.dialect = dialect;
            sql.cdata = code;
            this.selects[this.selects.length - 1] = sql;
        }

        public boolean equals(Object o) {
            if (o instanceof MondrianDef.View) {
                MondrianDef.View that = (MondrianDef.View)o;
                if (!Util.equals(this.alias, that.alias)) {
                    return false;
                } else if (this.selects != null && that.selects != null && this.selects.length == that.selects.length) {
                    for(int i = 0; i < this.selects.length; ++i) {
                        if (!Util.equals(this.selects[i].dialect, that.selects[i].dialect) || !Util.equals(this.selects[i].cdata, that.selects[i].cdata)) {
                            return false;
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    public abstract static class Relation extends MondrianDef.RelationOrJoin {
        public Relation() {
        }

        public Relation(DOMWrapper _def) throws XOMException {
        }

        public String getName() {
            return "Relation";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Relation", new XMLAttrVector());
            _out.endTag("Relation");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            return true;
        }

        public abstract String getAlias();
    }

    public abstract static class RelationOrJoin extends ElementDef {
        public RelationOrJoin() {
        }

        public RelationOrJoin(DOMWrapper _def) throws XOMException {
        }

        public String getName() {
            return "RelationOrJoin";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("RelationOrJoin", new XMLAttrVector());
            _out.endTag("RelationOrJoin");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            return true;
        }

        public abstract MondrianDef.Relation find(String var1);

        public boolean equals(Object o) {
            return this == o;
        }

        public int hashCode() {
            return System.identityHashCode(this);
        }
    }

    public static class MemberReaderParameter extends ElementDef {
        public String name;
        public String value;

        public MemberReaderParameter() {
        }

        public MemberReaderParameter(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, false);
                this.value = (String)_parser.getAttribute("value", "String", (String)null, (String[])null, false);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "MemberReaderParameter";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "value", this.value, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("MemberReaderParameter", (new XMLAttrVector()).add("name", this.name).add("value", this.value));
            _out.endTag("MemberReaderParameter");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.MemberReaderParameter _cother = (MondrianDef.MemberReaderParameter)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("value", this.value, _cother.value, _out, _indent + 1);
            return _diff;
        }
    }

    public static class Formula extends ElementDef {
        public String cdata;

        public Formula() {
        }

        public Formula(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.cdata = _parser.getText();
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "Formula";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayString(_out, "cdata", this.cdata, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Formula", new XMLAttrVector());
            _out.cdata(this.cdata);
            _out.endTag("Formula");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Formula _cother = (MondrianDef.Formula)_other;
            boolean _diff = displayStringDiff("cdata", this.cdata, _cother.cdata, _out, _indent + 1);
            return _diff;
        }
    }

    public static class NamedSet extends ElementDef {
        public String name;
        public String caption;
        public String description;
        public String displayFolder;
        public String formula;
        public MondrianDef.Annotations annotations;
        public MondrianDef.Formula formulaElement;

        public NamedSet() {
        }

        public NamedSet(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.caption = (String)_parser.getAttribute("caption", "String", (String)null, (String[])null, false);
                this.description = (String)_parser.getAttribute("description", "String", (String)null, (String[])null, false);
                this.displayFolder = (String)_parser.getAttribute("displayFolder", "String", (String)null, (String[])null, false);
                this.formula = (String)_parser.getAttribute("formula", "String", (String)null, (String[])null, false);
                this.annotations = (MondrianDef.Annotations)_parser.getElement(MondrianDef.Annotations.class, false);
                this.formulaElement = (MondrianDef.Formula)_parser.getElement(MondrianDef.Formula.class, false);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "NamedSet";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "caption", this.caption, _indent + 1);
            displayAttribute(_out, "description", this.description, _indent + 1);
            displayAttribute(_out, "displayFolder", this.displayFolder, _indent + 1);
            displayAttribute(_out, "formula", this.formula, _indent + 1);
            displayElement(_out, "annotations", this.annotations, _indent + 1);
            displayElement(_out, "formulaElement", this.formulaElement, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("NamedSet", (new XMLAttrVector()).add("name", this.name).add("caption", this.caption).add("description", this.description).add("displayFolder", this.displayFolder).add("formula", this.formula));
            displayXMLElement(_out, this.annotations);
            displayXMLElement(_out, this.formulaElement);
            _out.endTag("NamedSet");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.NamedSet _cother = (MondrianDef.NamedSet)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("caption", this.caption, _cother.caption, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("description", this.description, _cother.description, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("displayFolder", this.displayFolder, _cother.displayFolder, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("formula", this.formula, _cother.formula, _out, _indent + 1);
            _diff = _diff && displayElementDiff("annotations", this.annotations, _cother.annotations, _out, _indent + 1);
            _diff = _diff && displayElementDiff("formulaElement", this.formulaElement, _cother.formulaElement, _out, _indent + 1);
            return _diff;
        }

        public String getFormula() {
            return this.formulaElement != null ? this.formulaElement.cdata : this.formula;
        }
    }

    public static class CalculatedMemberProperty extends ElementDef {
        public String name;
        public String caption;
        public String description;
        public String expression;
        public String value;

        public CalculatedMemberProperty() {
        }

        public CalculatedMemberProperty(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.caption = (String)_parser.getAttribute("caption", "String", (String)null, (String[])null, false);
                this.description = (String)_parser.getAttribute("description", "String", (String)null, (String[])null, false);
                this.expression = (String)_parser.getAttribute("expression", "String", (String)null, (String[])null, false);
                this.value = (String)_parser.getAttribute("value", "String", (String)null, (String[])null, false);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "CalculatedMemberProperty";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "caption", this.caption, _indent + 1);
            displayAttribute(_out, "description", this.description, _indent + 1);
            displayAttribute(_out, "expression", this.expression, _indent + 1);
            displayAttribute(_out, "value", this.value, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("CalculatedMemberProperty", (new XMLAttrVector()).add("name", this.name).add("caption", this.caption).add("description", this.description).add("expression", this.expression).add("value", this.value));
            _out.endTag("CalculatedMemberProperty");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.CalculatedMemberProperty _cother = (MondrianDef.CalculatedMemberProperty)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("caption", this.caption, _cother.caption, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("description", this.description, _cother.description, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("expression", this.expression, _cother.expression, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("value", this.value, _cother.value, _out, _indent + 1);
            return _diff;
        }
    }

    public static class CalculatedMember extends ElementDef {
        public String name;
        public String formatString;
        public String caption;
        public String description;
        public String formula;
        public String dimension;
        public String hierarchy;
        public String parent;
        public Boolean visible;
        public String displayFolder;
        public MondrianDef.Annotations annotations;
        public MondrianDef.Formula formulaElement;
        public MondrianDef.CellFormatter cellFormatter;
        public MondrianDef.CalculatedMemberProperty[] memberProperties;

        public CalculatedMember() {
        }

        public CalculatedMember(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.formatString = (String)_parser.getAttribute("formatString", "String", (String)null, (String[])null, false);
                this.caption = (String)_parser.getAttribute("caption", "String", (String)null, (String[])null, false);
                this.description = (String)_parser.getAttribute("description", "String", (String)null, (String[])null, false);
                this.formula = (String)_parser.getAttribute("formula", "String", (String)null, (String[])null, false);
                this.dimension = (String)_parser.getAttribute("dimension", "String", (String)null, (String[])null, false);
                this.hierarchy = (String)_parser.getAttribute("hierarchy", "String", (String)null, (String[])null, false);
                this.parent = (String)_parser.getAttribute("parent", "String", (String)null, (String[])null, false);
                this.visible = (Boolean)_parser.getAttribute("visible", "Boolean", (String)null, (String[])null, false);
                this.displayFolder = (String)_parser.getAttribute("displayFolder", "String", (String)null, (String[])null, false);
                this.annotations = (MondrianDef.Annotations)_parser.getElement(MondrianDef.Annotations.class, false);
                this.formulaElement = (MondrianDef.Formula)_parser.getElement(MondrianDef.Formula.class, false);
                this.cellFormatter = (MondrianDef.CellFormatter)_parser.getElement(MondrianDef.CellFormatter.class, false);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.CalculatedMemberProperty.class, 0, 0);
                this.memberProperties = new MondrianDef.CalculatedMemberProperty[_tempArray.length];

                for(int _i = 0; _i < this.memberProperties.length; ++_i) {
                    this.memberProperties[_i] = (MondrianDef.CalculatedMemberProperty)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "CalculatedMember";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "formatString", this.formatString, _indent + 1);
            displayAttribute(_out, "caption", this.caption, _indent + 1);
            displayAttribute(_out, "description", this.description, _indent + 1);
            displayAttribute(_out, "formula", this.formula, _indent + 1);
            displayAttribute(_out, "dimension", this.dimension, _indent + 1);
            displayAttribute(_out, "hierarchy", this.hierarchy, _indent + 1);
            displayAttribute(_out, "parent", this.parent, _indent + 1);
            displayAttribute(_out, "visible", this.visible, _indent + 1);
            displayAttribute(_out, "displayFolder", this.displayFolder, _indent + 1);
            displayElement(_out, "annotations", this.annotations, _indent + 1);
            displayElement(_out, "formulaElement", this.formulaElement, _indent + 1);
            displayElement(_out, "cellFormatter", this.cellFormatter, _indent + 1);
            displayElementArray(_out, "memberProperties", this.memberProperties, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("CalculatedMember", (new XMLAttrVector()).add("name", this.name).add("formatString", this.formatString).add("caption", this.caption).add("description", this.description).add("formula", this.formula).add("dimension", this.dimension).add("hierarchy", this.hierarchy).add("parent", this.parent).add("visible", this.visible).add("displayFolder", this.displayFolder));
            displayXMLElement(_out, this.annotations);
            displayXMLElement(_out, this.formulaElement);
            displayXMLElement(_out, this.cellFormatter);
            displayXMLElementArray(_out, this.memberProperties);
            _out.endTag("CalculatedMember");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.CalculatedMember _cother = (MondrianDef.CalculatedMember)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("formatString", this.formatString, _cother.formatString, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("caption", this.caption, _cother.caption, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("description", this.description, _cother.description, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("formula", this.formula, _cother.formula, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("dimension", this.dimension, _cother.dimension, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("hierarchy", this.hierarchy, _cother.hierarchy, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("parent", this.parent, _cother.parent, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("visible", this.visible, _cother.visible, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("displayFolder", this.displayFolder, _cother.displayFolder, _out, _indent + 1);
            _diff = _diff && displayElementDiff("annotations", this.annotations, _cother.annotations, _out, _indent + 1);
            _diff = _diff && displayElementDiff("formulaElement", this.formulaElement, _cother.formulaElement, _out, _indent + 1);
            _diff = _diff && displayElementDiff("cellFormatter", this.cellFormatter, _cother.cellFormatter, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("memberProperties", this.memberProperties, _cother.memberProperties, _out, _indent + 1);
            return _diff;
        }

        public String getFormula() {
            return this.formulaElement != null ? this.formulaElement.cdata : this.formula;
        }

        public String getFormatString() {
            MondrianDef.CalculatedMemberProperty[] var1 = this.memberProperties;
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                MondrianDef.CalculatedMemberProperty prop = var1[var3];
                if (prop.name.equals(mondrian.olap.Property.FORMAT_STRING.name)) {
                    return prop.value;
                }
            }

            return this.formatString;
        }
    }

    public static class Measure extends ElementDef {
        public String name;
        public String column;
        public static final String[] _datatype_values = new String[]{"String", "Numeric", "Integer"};
        public String datatype;
        public String formatString;
        public String backColor;
        public String aggregator;
        public String formatter;
        public String caption;
        public String description;
        public Boolean visible;
        public String displayFolder;
        public MondrianDef.Annotations annotations;
        public MondrianDef.MeasureExpression measureExp;
        public MondrianDef.CellFormatter cellFormatter;
        public MondrianDef.CalculatedMemberProperty[] memberProperties;

        public Measure() {
        }

        public Measure(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.column = (String)_parser.getAttribute("column", "String", (String)null, (String[])null, false);
                this.datatype = (String)_parser.getAttribute("datatype", "String", (String)null, _datatype_values, false);
                this.formatString = (String)_parser.getAttribute("formatString", "String", (String)null, (String[])null, false);
                this.backColor = (String)_parser.getAttribute("backColor", "String", (String)null, (String[])null, false);
                this.aggregator = (String)_parser.getAttribute("aggregator", "String", (String)null, (String[])null, true);
                this.formatter = (String)_parser.getAttribute("formatter", "String", (String)null, (String[])null, false);
                this.caption = (String)_parser.getAttribute("caption", "String", (String)null, (String[])null, false);
                this.description = (String)_parser.getAttribute("description", "String", (String)null, (String[])null, false);
                this.visible = (Boolean)_parser.getAttribute("visible", "Boolean", (String)null, (String[])null, false);
                this.displayFolder = (String)_parser.getAttribute("displayFolder", "String", (String)null, (String[])null, false);
                this.annotations = (MondrianDef.Annotations)_parser.getElement(MondrianDef.Annotations.class, false);
                this.measureExp = (MondrianDef.MeasureExpression)_parser.getElement(MondrianDef.MeasureExpression.class, false);
                this.cellFormatter = (MondrianDef.CellFormatter)_parser.getElement(MondrianDef.CellFormatter.class, false);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.CalculatedMemberProperty.class, 0, 0);
                this.memberProperties = new MondrianDef.CalculatedMemberProperty[_tempArray.length];

                for(int _i = 0; _i < this.memberProperties.length; ++_i) {
                    this.memberProperties[_i] = (MondrianDef.CalculatedMemberProperty)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "Measure";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "column", this.column, _indent + 1);
            displayAttribute(_out, "datatype", this.datatype, _indent + 1);
            displayAttribute(_out, "formatString", this.formatString, _indent + 1);
            displayAttribute(_out, "backColor", this.backColor, _indent + 1);
            displayAttribute(_out, "aggregator", this.aggregator, _indent + 1);
            displayAttribute(_out, "formatter", this.formatter, _indent + 1);
            displayAttribute(_out, "caption", this.caption, _indent + 1);
            displayAttribute(_out, "description", this.description, _indent + 1);
            displayAttribute(_out, "visible", this.visible, _indent + 1);
            displayAttribute(_out, "displayFolder", this.displayFolder, _indent + 1);
            displayElement(_out, "annotations", this.annotations, _indent + 1);
            displayElement(_out, "measureExp", this.measureExp, _indent + 1);
            displayElement(_out, "cellFormatter", this.cellFormatter, _indent + 1);
            displayElementArray(_out, "memberProperties", this.memberProperties, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Measure", (new XMLAttrVector()).add("name", this.name).add("column", this.column).add("datatype", this.datatype).add("formatString", this.formatString).add("backColor", this.backColor).add("aggregator", this.aggregator).add("formatter", this.formatter).add("caption", this.caption).add("description", this.description).add("visible", this.visible).add("displayFolder", this.displayFolder));
            displayXMLElement(_out, this.annotations);
            displayXMLElement(_out, this.measureExp);
            displayXMLElement(_out, this.cellFormatter);
            displayXMLElementArray(_out, this.memberProperties);
            _out.endTag("Measure");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Measure _cother = (MondrianDef.Measure)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("column", this.column, _cother.column, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("datatype", this.datatype, _cother.datatype, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("formatString", this.formatString, _cother.formatString, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("backColor", this.backColor, _cother.backColor, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("aggregator", this.aggregator, _cother.aggregator, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("formatter", this.formatter, _cother.formatter, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("caption", this.caption, _cother.caption, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("description", this.description, _cother.description, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("visible", this.visible, _cother.visible, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("displayFolder", this.displayFolder, _cother.displayFolder, _out, _indent + 1);
            _diff = _diff && displayElementDiff("annotations", this.annotations, _cother.annotations, _out, _indent + 1);
            _diff = _diff && displayElementDiff("measureExp", this.measureExp, _cother.measureExp, _out, _indent + 1);
            _diff = _diff && displayElementDiff("cellFormatter", this.cellFormatter, _cother.cellFormatter, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("memberProperties", this.memberProperties, _cother.memberProperties, _out, _indent + 1);
            return _diff;
        }
    }

    public static class Property extends ElementDef {
        public String name;
        public String column;
        public static final String[] _type_values = new String[]{"String", "Numeric", "Integer", "Long", "Boolean", "Date", "Time", "Timestamp"};
        public String type;
        public String formatter;
        public String caption;
        public String description;
        public Boolean dependsOnLevelValue;
        public MondrianDef.PropertyFormatter propertyFormatter;

        public Property() {
        }

        public Property(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, false);
                this.column = (String)_parser.getAttribute("column", "String", (String)null, (String[])null, false);
                this.type = (String)_parser.getAttribute("type", "String", "String", _type_values, false);
                this.formatter = (String)_parser.getAttribute("formatter", "String", (String)null, (String[])null, false);
                this.caption = (String)_parser.getAttribute("caption", "String", (String)null, (String[])null, false);
                this.description = (String)_parser.getAttribute("description", "String", (String)null, (String[])null, false);
                this.dependsOnLevelValue = (Boolean)_parser.getAttribute("dependsOnLevelValue", "Boolean", (String)null, (String[])null, false);
                this.propertyFormatter = (MondrianDef.PropertyFormatter)_parser.getElement(MondrianDef.PropertyFormatter.class, false);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "Property";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "column", this.column, _indent + 1);
            displayAttribute(_out, "type", this.type, _indent + 1);
            displayAttribute(_out, "formatter", this.formatter, _indent + 1);
            displayAttribute(_out, "caption", this.caption, _indent + 1);
            displayAttribute(_out, "description", this.description, _indent + 1);
            displayAttribute(_out, "dependsOnLevelValue", this.dependsOnLevelValue, _indent + 1);
            displayElement(_out, "propertyFormatter", this.propertyFormatter, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Property", (new XMLAttrVector()).add("name", this.name).add("column", this.column).add("type", this.type).add("formatter", this.formatter).add("caption", this.caption).add("description", this.description).add("dependsOnLevelValue", this.dependsOnLevelValue));
            displayXMLElement(_out, this.propertyFormatter);
            _out.endTag("Property");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Property _cother = (MondrianDef.Property)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("column", this.column, _cother.column, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("type", this.type, _cother.type, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("formatter", this.formatter, _cother.formatter, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("caption", this.caption, _cother.caption, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("description", this.description, _cother.description, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("dependsOnLevelValue", this.dependsOnLevelValue, _cother.dependsOnLevelValue, _out, _indent + 1);
            _diff = _diff && displayElementDiff("propertyFormatter", this.propertyFormatter, _cother.propertyFormatter, _out, _indent + 1);
            return _diff;
        }
    }

    public static class Closure extends ElementDef {
        public String parentColumn;
        public String childColumn;
        public MondrianDef.Table table;

        public Closure() {
        }

        public Closure(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.parentColumn = (String)_parser.getAttribute("parentColumn", "String", (String)null, (String[])null, true);
                this.childColumn = (String)_parser.getAttribute("childColumn", "String", (String)null, (String[])null, true);
                this.table = (MondrianDef.Table)_parser.getElement(MondrianDef.Table.class, true);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "Closure";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "parentColumn", this.parentColumn, _indent + 1);
            displayAttribute(_out, "childColumn", this.childColumn, _indent + 1);
            displayElement(_out, "table", this.table, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Closure", (new XMLAttrVector()).add("parentColumn", this.parentColumn).add("childColumn", this.childColumn));
            displayXMLElement(_out, this.table);
            _out.endTag("Closure");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Closure _cother = (MondrianDef.Closure)_other;
            boolean _diff = displayAttributeDiff("parentColumn", this.parentColumn, _cother.parentColumn, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("childColumn", this.childColumn, _cother.childColumn, _out, _indent + 1);
            _diff = _diff && displayElementDiff("table", this.table, _cother.table, _out, _indent + 1);
            return _diff;
        }
    }

    public static class Level extends ElementDef {
        public String approxRowCount;
        public String name;
        public Boolean visible;
        public String table;
        public String column;
        public String nameColumn;
        public String ordinalColumn;
        public String parentColumn;
        public String nullParentValue;
        public static final String[] _type_values = new String[]{"String", "Numeric", "Integer", "Boolean", "Date", "Time", "Timestamp"};
        public String type;
        public static final String[] _internalType_values = new String[]{"int", "long", "Object", "String"};
        public String internalType;
        public Boolean uniqueMembers;
        public static final String[] _levelType_values = new String[]{"Regular", "TimeYears", "TimeHalfYears", "TimeHalfYear", "TimeQuarters", "TimeMonths", "TimeWeeks", "TimeDays", "TimeHours", "TimeMinutes", "TimeSeconds", "TimeUndefined"};
        public String levelType;
        public static final String[] _hideMemberIf_values = new String[]{"Never", "IfBlankName", "IfParentsName"};
        public String hideMemberIf;
        public String formatter;
        public String caption;
        public String description;
        public String captionColumn;
        public MondrianDef.Annotations annotations;
        public MondrianDef.KeyExpression keyExp;
        public MondrianDef.NameExpression nameExp;
        public MondrianDef.CaptionExpression captionExp;
        public MondrianDef.OrdinalExpression ordinalExp;
        public MondrianDef.ParentExpression parentExp;
        public MondrianDef.MemberFormatter memberFormatter;
        public MondrianDef.Closure closure;
        public MondrianDef.Property[] properties;

        public Level() {
        }

        public Level(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.approxRowCount = (String)_parser.getAttribute("approxRowCount", "String", (String)null, (String[])null, false);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.visible = (Boolean)_parser.getAttribute("visible", "Boolean", "true", (String[])null, false);
                this.table = (String)_parser.getAttribute("table", "String", (String)null, (String[])null, false);
                this.column = (String)_parser.getAttribute("column", "String", (String)null, (String[])null, false);
                this.nameColumn = (String)_parser.getAttribute("nameColumn", "String", (String)null, (String[])null, false);
                this.ordinalColumn = (String)_parser.getAttribute("ordinalColumn", "String", (String)null, (String[])null, false);
                this.parentColumn = (String)_parser.getAttribute("parentColumn", "String", (String)null, (String[])null, false);
                this.nullParentValue = (String)_parser.getAttribute("nullParentValue", "String", (String)null, (String[])null, false);
                this.type = (String)_parser.getAttribute("type", "String", "String", _type_values, false);
                this.internalType = (String)_parser.getAttribute("internalType", "String", (String)null, _internalType_values, false);
                this.uniqueMembers = (Boolean)_parser.getAttribute("uniqueMembers", "Boolean", "false", (String[])null, false);
                this.levelType = (String)_parser.getAttribute("levelType", "String", "Regular", _levelType_values, false);
                this.hideMemberIf = (String)_parser.getAttribute("hideMemberIf", "String", "Never", _hideMemberIf_values, false);
                this.formatter = (String)_parser.getAttribute("formatter", "String", (String)null, (String[])null, false);
                this.caption = (String)_parser.getAttribute("caption", "String", (String)null, (String[])null, false);
                this.description = (String)_parser.getAttribute("description", "String", (String)null, (String[])null, false);
                this.captionColumn = (String)_parser.getAttribute("captionColumn", "String", (String)null, (String[])null, false);
                this.annotations = (MondrianDef.Annotations)_parser.getElement(MondrianDef.Annotations.class, false);
                this.keyExp = (MondrianDef.KeyExpression)_parser.getElement(MondrianDef.KeyExpression.class, false);
                this.nameExp = (MondrianDef.NameExpression)_parser.getElement(MondrianDef.NameExpression.class, false);
                this.captionExp = (MondrianDef.CaptionExpression)_parser.getElement(MondrianDef.CaptionExpression.class, false);
                this.ordinalExp = (MondrianDef.OrdinalExpression)_parser.getElement(MondrianDef.OrdinalExpression.class, false);
                this.parentExp = (MondrianDef.ParentExpression)_parser.getElement(MondrianDef.ParentExpression.class, false);
                this.memberFormatter = (MondrianDef.MemberFormatter)_parser.getElement(MondrianDef.MemberFormatter.class, false);
                this.closure = (MondrianDef.Closure)_parser.getElement(MondrianDef.Closure.class, false);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.Property.class, 0, 0);
                this.properties = new MondrianDef.Property[_tempArray.length];

                for(int _i = 0; _i < this.properties.length; ++_i) {
                    this.properties[_i] = (MondrianDef.Property)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "Level";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "approxRowCount", this.approxRowCount, _indent + 1);
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "visible", this.visible, _indent + 1);
            displayAttribute(_out, "table", this.table, _indent + 1);
            displayAttribute(_out, "column", this.column, _indent + 1);
            displayAttribute(_out, "nameColumn", this.nameColumn, _indent + 1);
            displayAttribute(_out, "ordinalColumn", this.ordinalColumn, _indent + 1);
            displayAttribute(_out, "parentColumn", this.parentColumn, _indent + 1);
            displayAttribute(_out, "nullParentValue", this.nullParentValue, _indent + 1);
            displayAttribute(_out, "type", this.type, _indent + 1);
            displayAttribute(_out, "internalType", this.internalType, _indent + 1);
            displayAttribute(_out, "uniqueMembers", this.uniqueMembers, _indent + 1);
            displayAttribute(_out, "levelType", this.levelType, _indent + 1);
            displayAttribute(_out, "hideMemberIf", this.hideMemberIf, _indent + 1);
            displayAttribute(_out, "formatter", this.formatter, _indent + 1);
            displayAttribute(_out, "caption", this.caption, _indent + 1);
            displayAttribute(_out, "description", this.description, _indent + 1);
            displayAttribute(_out, "captionColumn", this.captionColumn, _indent + 1);
            displayElement(_out, "annotations", this.annotations, _indent + 1);
            displayElement(_out, "keyExp", this.keyExp, _indent + 1);
            displayElement(_out, "nameExp", this.nameExp, _indent + 1);
            displayElement(_out, "captionExp", this.captionExp, _indent + 1);
            displayElement(_out, "ordinalExp", this.ordinalExp, _indent + 1);
            displayElement(_out, "parentExp", this.parentExp, _indent + 1);
            displayElement(_out, "memberFormatter", this.memberFormatter, _indent + 1);
            displayElement(_out, "closure", this.closure, _indent + 1);
            displayElementArray(_out, "properties", this.properties, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Level", (new XMLAttrVector()).add("approxRowCount", this.approxRowCount).add("name", this.name).add("visible", this.visible).add("table", this.table).add("column", this.column).add("nameColumn", this.nameColumn).add("ordinalColumn", this.ordinalColumn).add("parentColumn", this.parentColumn).add("nullParentValue", this.nullParentValue).add("type", this.type).add("internalType", this.internalType).add("uniqueMembers", this.uniqueMembers).add("levelType", this.levelType).add("hideMemberIf", this.hideMemberIf).add("formatter", this.formatter).add("caption", this.caption).add("description", this.description).add("captionColumn", this.captionColumn));
            displayXMLElement(_out, this.annotations);
            displayXMLElement(_out, this.keyExp);
            displayXMLElement(_out, this.nameExp);
            displayXMLElement(_out, this.captionExp);
            displayXMLElement(_out, this.ordinalExp);
            displayXMLElement(_out, this.parentExp);
            displayXMLElement(_out, this.memberFormatter);
            displayXMLElement(_out, this.closure);
            displayXMLElementArray(_out, this.properties);
            _out.endTag("Level");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Level _cother = (MondrianDef.Level)_other;
            boolean _diff = displayAttributeDiff("approxRowCount", this.approxRowCount, _cother.approxRowCount, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("visible", this.visible, _cother.visible, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("table", this.table, _cother.table, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("column", this.column, _cother.column, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("nameColumn", this.nameColumn, _cother.nameColumn, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("ordinalColumn", this.ordinalColumn, _cother.ordinalColumn, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("parentColumn", this.parentColumn, _cother.parentColumn, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("nullParentValue", this.nullParentValue, _cother.nullParentValue, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("type", this.type, _cother.type, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("internalType", this.internalType, _cother.internalType, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("uniqueMembers", this.uniqueMembers, _cother.uniqueMembers, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("levelType", this.levelType, _cother.levelType, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("hideMemberIf", this.hideMemberIf, _cother.hideMemberIf, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("formatter", this.formatter, _cother.formatter, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("caption", this.caption, _cother.caption, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("description", this.description, _cother.description, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("captionColumn", this.captionColumn, _cother.captionColumn, _out, _indent + 1);
            _diff = _diff && displayElementDiff("annotations", this.annotations, _cother.annotations, _out, _indent + 1);
            _diff = _diff && displayElementDiff("keyExp", this.keyExp, _cother.keyExp, _out, _indent + 1);
            _diff = _diff && displayElementDiff("nameExp", this.nameExp, _cother.nameExp, _out, _indent + 1);
            _diff = _diff && displayElementDiff("captionExp", this.captionExp, _cother.captionExp, _out, _indent + 1);
            _diff = _diff && displayElementDiff("ordinalExp", this.ordinalExp, _cother.ordinalExp, _out, _indent + 1);
            _diff = _diff && displayElementDiff("parentExp", this.parentExp, _cother.parentExp, _out, _indent + 1);
            _diff = _diff && displayElementDiff("memberFormatter", this.memberFormatter, _cother.memberFormatter, _out, _indent + 1);
            _diff = _diff && displayElementDiff("closure", this.closure, _cother.closure, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("properties", this.properties, _cother.properties, _out, _indent + 1);
            return _diff;
        }

        public MondrianDef.Expression getKeyExp() {
            if (this.keyExp != null) {
                return this.keyExp;
            } else {
                return this.column != null ? new MondrianDef.Column(this.table, this.column) : null;
            }
        }

        public MondrianDef.Expression getNameExp() {
            if (this.nameExp != null) {
                return this.nameExp;
            } else {
                return this.nameColumn != null && !Util.equals(this.nameColumn, this.column) ? new MondrianDef.Column(this.table, this.nameColumn) : null;
            }
        }

        public MondrianDef.Expression getCaptionExp() {
            if (this.captionExp != null) {
                return this.captionExp;
            } else {
                return this.captionColumn != null ? new MondrianDef.Column(this.table, this.captionColumn) : null;
            }
        }

        public MondrianDef.Expression getOrdinalExp() {
            if (this.ordinalExp != null) {
                return this.ordinalExp;
            } else {
                return this.ordinalColumn != null ? new MondrianDef.Column(this.table, this.ordinalColumn) : null;
            }
        }

        public MondrianDef.Expression getParentExp() {
            if (this.parentExp != null) {
                return this.parentExp;
            } else {
                return this.parentColumn != null ? new MondrianDef.Column(this.table, this.parentColumn) : null;
            }
        }

        public MondrianDef.Expression getPropertyExp(int i) {
            return new MondrianDef.Column(this.table, this.properties[i].column);
        }

        public Datatype getDatatype() {
            return Datatype.valueOf(this.type);
        }
    }

    public static class Hierarchy extends ElementDef {
        public String name;
        public Boolean visible;
        public Boolean hasAll;
        public String allMemberName;
        public String allMemberCaption;
        public String allLevelName;
        public String primaryKey;
        public String primaryKeyTable;
        public String defaultMember;
        public String memberReaderClass;
        public String caption;
        public String description;
        public String displayFolder;
        public String uniqueKeyLevelName;
        public String origin;
        public MondrianDef.Annotations annotations;
        public MondrianDef.RelationOrJoin relation;
        public MondrianDef.Level[] levels;
        public MondrianDef.MemberReaderParameter[] memberReaderParameters;

        public Hierarchy() {
        }

        public Hierarchy(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, false);
                this.visible = (Boolean)_parser.getAttribute("visible", "Boolean", "true", (String[])null, false);
                this.hasAll = (Boolean)_parser.getAttribute("hasAll", "Boolean", (String)null, (String[])null, true);
                this.allMemberName = (String)_parser.getAttribute("allMemberName", "String", (String)null, (String[])null, false);
                this.allMemberCaption = (String)_parser.getAttribute("allMemberCaption", "String", (String)null, (String[])null, false);
                this.allLevelName = (String)_parser.getAttribute("allLevelName", "String", (String)null, (String[])null, false);
                this.primaryKey = (String)_parser.getAttribute("primaryKey", "String", (String)null, (String[])null, false);
                this.primaryKeyTable = (String)_parser.getAttribute("primaryKeyTable", "String", (String)null, (String[])null, false);
                this.defaultMember = (String)_parser.getAttribute("defaultMember", "String", (String)null, (String[])null, false);
                this.memberReaderClass = (String)_parser.getAttribute("memberReaderClass", "String", (String)null, (String[])null, false);
                this.caption = (String)_parser.getAttribute("caption", "String", (String)null, (String[])null, false);
                this.description = (String)_parser.getAttribute("description", "String", (String)null, (String[])null, false);
                this.displayFolder = (String)_parser.getAttribute("displayFolder", "String", (String)null, (String[])null, false);
                this.uniqueKeyLevelName = (String)_parser.getAttribute("uniqueKeyLevelName", "String", (String)null, (String[])null, false);
                this.origin = (String)_parser.getAttribute("origin", "String", (String)null, (String[])null, false);
                this.annotations = (MondrianDef.Annotations)_parser.getElement(MondrianDef.Annotations.class, false);
                this.relation = (MondrianDef.RelationOrJoin)_parser.getElement(MondrianDef.RelationOrJoin.class, false);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.Level.class, 0, 0);
                this.levels = new MondrianDef.Level[_tempArray.length];

                int _i;
                for(_i = 0; _i < this.levels.length; ++_i) {
                    this.levels[_i] = (MondrianDef.Level)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.MemberReaderParameter.class, 0, 0);
                this.memberReaderParameters = new MondrianDef.MemberReaderParameter[_tempArray.length];

                for(_i = 0; _i < this.memberReaderParameters.length; ++_i) {
                    this.memberReaderParameters[_i] = (MondrianDef.MemberReaderParameter)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "Hierarchy";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "visible", this.visible, _indent + 1);
            displayAttribute(_out, "hasAll", this.hasAll, _indent + 1);
            displayAttribute(_out, "allMemberName", this.allMemberName, _indent + 1);
            displayAttribute(_out, "allMemberCaption", this.allMemberCaption, _indent + 1);
            displayAttribute(_out, "allLevelName", this.allLevelName, _indent + 1);
            displayAttribute(_out, "primaryKey", this.primaryKey, _indent + 1);
            displayAttribute(_out, "primaryKeyTable", this.primaryKeyTable, _indent + 1);
            displayAttribute(_out, "defaultMember", this.defaultMember, _indent + 1);
            displayAttribute(_out, "memberReaderClass", this.memberReaderClass, _indent + 1);
            displayAttribute(_out, "caption", this.caption, _indent + 1);
            displayAttribute(_out, "description", this.description, _indent + 1);
            displayAttribute(_out, "displayFolder", this.displayFolder, _indent + 1);
            displayAttribute(_out, "uniqueKeyLevelName", this.uniqueKeyLevelName, _indent + 1);
            displayAttribute(_out, "origin", this.origin, _indent + 1);
            displayElement(_out, "annotations", this.annotations, _indent + 1);
            displayElement(_out, "relation", this.relation, _indent + 1);
            displayElementArray(_out, "levels", this.levels, _indent + 1);
            displayElementArray(_out, "memberReaderParameters", this.memberReaderParameters, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Hierarchy", (new XMLAttrVector()).add("name", this.name).add("visible", this.visible).add("hasAll", this.hasAll).add("allMemberName", this.allMemberName).add("allMemberCaption", this.allMemberCaption).add("allLevelName", this.allLevelName).add("primaryKey", this.primaryKey).add("primaryKeyTable", this.primaryKeyTable).add("defaultMember", this.defaultMember).add("memberReaderClass", this.memberReaderClass).add("caption", this.caption).add("description", this.description).add("displayFolder", this.displayFolder).add("uniqueKeyLevelName", this.uniqueKeyLevelName).add("origin", this.origin));
            displayXMLElement(_out, this.annotations);
            displayXMLElement(_out, this.relation);
            displayXMLElementArray(_out, this.levels);
            displayXMLElementArray(_out, this.memberReaderParameters);
            _out.endTag("Hierarchy");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Hierarchy _cother = (MondrianDef.Hierarchy)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("visible", this.visible, _cother.visible, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("hasAll", this.hasAll, _cother.hasAll, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("allMemberName", this.allMemberName, _cother.allMemberName, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("allMemberCaption", this.allMemberCaption, _cother.allMemberCaption, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("allLevelName", this.allLevelName, _cother.allLevelName, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("primaryKey", this.primaryKey, _cother.primaryKey, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("primaryKeyTable", this.primaryKeyTable, _cother.primaryKeyTable, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("defaultMember", this.defaultMember, _cother.defaultMember, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("memberReaderClass", this.memberReaderClass, _cother.memberReaderClass, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("caption", this.caption, _cother.caption, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("description", this.description, _cother.description, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("displayFolder", this.displayFolder, _cother.displayFolder, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("uniqueKeyLevelName", this.uniqueKeyLevelName, _cother.uniqueKeyLevelName, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("origin", this.origin, _cother.origin, _out, _indent + 1);
            _diff = _diff && displayElementDiff("annotations", this.annotations, _cother.annotations, _out, _indent + 1);
            _diff = _diff && displayElementDiff("relation", this.relation, _cother.relation, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("levels", this.levels, _cother.levels, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("memberReaderParameters", this.memberReaderParameters, _cother.memberReaderParameters, _out, _indent + 1);
            return _diff;
        }
    }

    public static class Dimension extends MondrianDef.CubeDimension {
        public static final String[] _type_values = new String[]{"StandardDimension", "TimeDimension"};
        public String type;
        public String usagePrefix;
        public MondrianDef.DimensionAttribute[] Attributes;
        public MondrianDef.Hierarchy[] hierarchies;

        public Dimension() {
        }

        public Dimension(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.type = (String)_parser.getAttribute("type", "String", (String)null, _type_values, false);
                this.usagePrefix = (String)_parser.getAttribute("usagePrefix", "String", (String)null, (String[])null, false);
                this.visible = (Boolean)_parser.getAttribute("visible", "Boolean", "true", (String[])null, false);
                this.foreignKey = (String)_parser.getAttribute("foreignKey", "String", (String)null, (String[])null, false);
                this.highCardinality = (Boolean)_parser.getAttribute("highCardinality", "Boolean", "false", (String[])null, false);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.caption = (String)_parser.getAttribute("caption", "String", (String)null, (String[])null, false);
                this.description = (String)_parser.getAttribute("description", "String", (String)null, (String[])null, false);
                this.annotations = (MondrianDef.Annotations)_parser.getElement(MondrianDef.Annotations.class, false);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.DimensionAttribute.class, 0, 0);
                this.Attributes = new MondrianDef.DimensionAttribute[_tempArray.length];

                int _i;
                for(_i = 0; _i < this.Attributes.length; ++_i) {
                    this.Attributes[_i] = (MondrianDef.DimensionAttribute)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.Hierarchy.class, 0, 0);
                this.hierarchies = new MondrianDef.Hierarchy[_tempArray.length];

                for(_i = 0; _i < this.hierarchies.length; ++_i) {
                    this.hierarchies[_i] = (MondrianDef.Hierarchy)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "Dimension";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "type", this.type, _indent + 1);
            displayAttribute(_out, "usagePrefix", this.usagePrefix, _indent + 1);
            displayAttribute(_out, "visible", this.visible, _indent + 1);
            displayAttribute(_out, "foreignKey", this.foreignKey, _indent + 1);
            displayAttribute(_out, "highCardinality", this.highCardinality, _indent + 1);
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "caption", this.caption, _indent + 1);
            displayAttribute(_out, "description", this.description, _indent + 1);
            displayElement(_out, "annotations", this.annotations, _indent + 1);
            displayElementArray(_out, "Attributes", this.Attributes, _indent + 1);
            displayElementArray(_out, "hierarchies", this.hierarchies, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Dimension", (new XMLAttrVector()).add("type", this.type).add("usagePrefix", this.usagePrefix).add("visible", this.visible).add("foreignKey", this.foreignKey).add("highCardinality", this.highCardinality).add("name", this.name).add("caption", this.caption).add("description", this.description));
            displayXMLElement(_out, this.annotations);
            displayXMLElementArray(_out, this.Attributes);
            displayXMLElementArray(_out, this.hierarchies);
            _out.endTag("Dimension");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Dimension _cother = (MondrianDef.Dimension)_other;
            boolean _diff = displayAttributeDiff("type", this.type, _cother.type, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("usagePrefix", this.usagePrefix, _cother.usagePrefix, _out, _indent + 1);
            _diff = _diff && displayElementDiff("annotations", this.annotations, _cother.annotations, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("Attributes", this.Attributes, _cother.Attributes, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("hierarchies", this.hierarchies, _cother.hierarchies, _out, _indent + 1);
            return _diff;
        }

        public MondrianDef.Dimension getDimension(MondrianDef.Schema schema) {
            Util.assertPrecondition(schema != null, "schema != null");
            return this;
        }

        public DimensionType getDimensionType() {
            return this.type == null ? null : DimensionType.valueOf(this.type);
        }
    }

    public static class DimensionUsage extends MondrianDef.CubeDimension {
        public String source;
        public String level;
        public String usagePrefix;

        public DimensionUsage() {
        }

        public DimensionUsage(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.source = (String)_parser.getAttribute("source", "String", (String)null, (String[])null, true);
                this.level = (String)_parser.getAttribute("level", "String", (String)null, (String[])null, false);
                this.usagePrefix = (String)_parser.getAttribute("usagePrefix", "String", (String)null, (String[])null, false);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.caption = (String)_parser.getAttribute("caption", "String", (String)null, (String[])null, false);
                this.visible = (Boolean)_parser.getAttribute("visible", "Boolean", "true", (String[])null, false);
                this.description = (String)_parser.getAttribute("description", "String", (String)null, (String[])null, false);
                this.foreignKey = (String)_parser.getAttribute("foreignKey", "String", (String)null, (String[])null, false);
                this.highCardinality = (Boolean)_parser.getAttribute("highCardinality", "Boolean", "false", (String[])null, false);
                this.annotations = (MondrianDef.Annotations)_parser.getElement(MondrianDef.Annotations.class, false);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "DimensionUsage";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "source", this.source, _indent + 1);
            displayAttribute(_out, "level", this.level, _indent + 1);
            displayAttribute(_out, "usagePrefix", this.usagePrefix, _indent + 1);
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "caption", this.caption, _indent + 1);
            displayAttribute(_out, "visible", this.visible, _indent + 1);
            displayAttribute(_out, "description", this.description, _indent + 1);
            displayAttribute(_out, "foreignKey", this.foreignKey, _indent + 1);
            displayAttribute(_out, "highCardinality", this.highCardinality, _indent + 1);
            displayElement(_out, "annotations", this.annotations, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("DimensionUsage", (new XMLAttrVector()).add("source", this.source).add("level", this.level).add("usagePrefix", this.usagePrefix).add("name", this.name).add("caption", this.caption).add("visible", this.visible).add("description", this.description).add("foreignKey", this.foreignKey).add("highCardinality", this.highCardinality));
            displayXMLElement(_out, this.annotations);
            _out.endTag("DimensionUsage");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.DimensionUsage _cother = (MondrianDef.DimensionUsage)_other;
            boolean _diff = displayAttributeDiff("source", this.source, _cother.source, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("level", this.level, _cother.level, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("usagePrefix", this.usagePrefix, _cother.usagePrefix, _out, _indent + 1);
            _diff = _diff && displayElementDiff("annotations", this.annotations, _cother.annotations, _out, _indent + 1);
            return _diff;
        }

        public MondrianDef.Dimension getDimension(MondrianDef.Schema schema) {
            Util.assertPrecondition(schema != null, "schema != null");

            for(int i = 0; i < schema.dimensions.length; ++i) {
                if (schema.dimensions[i].name.equals(this.source)) {
                    return schema.dimensions[i];
                }
            }

            throw Util.newInternal("Cannot find shared dimension '" + this.source + "'");
        }
    }

    public static class VirtualCubeMeasure extends ElementDef {
        public String cubeName;
        public String name;
        public Boolean visible;
        public MondrianDef.Annotations annotations;

        public VirtualCubeMeasure() {
        }

        public VirtualCubeMeasure(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.cubeName = (String)_parser.getAttribute("cubeName", "String", (String)null, (String[])null, false);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, false);
                this.visible = (Boolean)_parser.getAttribute("visible", "Boolean", (String)null, (String[])null, false);
                this.annotations = (MondrianDef.Annotations)_parser.getElement(MondrianDef.Annotations.class, false);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "VirtualCubeMeasure";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "cubeName", this.cubeName, _indent + 1);
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "visible", this.visible, _indent + 1);
            displayElement(_out, "annotations", this.annotations, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("VirtualCubeMeasure", (new XMLAttrVector()).add("cubeName", this.cubeName).add("name", this.name).add("visible", this.visible));
            displayXMLElement(_out, this.annotations);
            _out.endTag("VirtualCubeMeasure");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.VirtualCubeMeasure _cother = (MondrianDef.VirtualCubeMeasure)_other;
            boolean _diff = displayAttributeDiff("cubeName", this.cubeName, _cother.cubeName, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("visible", this.visible, _cother.visible, _out, _indent + 1);
            _diff = _diff && displayElementDiff("annotations", this.annotations, _cother.annotations, _out, _indent + 1);
            return _diff;
        }
    }

    public static class VirtualCubeDimension extends MondrianDef.CubeDimension {
        public String cubeName;

        public VirtualCubeDimension() {
        }

        public VirtualCubeDimension(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.cubeName = (String)_parser.getAttribute("cubeName", "String", (String)null, (String[])null, false);
                this.caption = (String)_parser.getAttribute("caption", "String", (String)null, (String[])null, false);
                this.visible = (Boolean)_parser.getAttribute("visible", "Boolean", "true", (String[])null, false);
                this.description = (String)_parser.getAttribute("description", "String", (String)null, (String[])null, false);
                this.foreignKey = (String)_parser.getAttribute("foreignKey", "String", (String)null, (String[])null, false);
                this.highCardinality = (Boolean)_parser.getAttribute("highCardinality", "Boolean", "false", (String[])null, false);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, false);
                this.annotations = (MondrianDef.Annotations)_parser.getElement(MondrianDef.Annotations.class, false);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "VirtualCubeDimension";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "cubeName", this.cubeName, _indent + 1);
            displayAttribute(_out, "caption", this.caption, _indent + 1);
            displayAttribute(_out, "visible", this.visible, _indent + 1);
            displayAttribute(_out, "description", this.description, _indent + 1);
            displayAttribute(_out, "foreignKey", this.foreignKey, _indent + 1);
            displayAttribute(_out, "highCardinality", this.highCardinality, _indent + 1);
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayElement(_out, "annotations", this.annotations, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("VirtualCubeDimension", (new XMLAttrVector()).add("cubeName", this.cubeName).add("caption", this.caption).add("visible", this.visible).add("description", this.description).add("foreignKey", this.foreignKey).add("highCardinality", this.highCardinality).add("name", this.name));
            displayXMLElement(_out, this.annotations);
            _out.endTag("VirtualCubeDimension");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.VirtualCubeDimension _cother = (MondrianDef.VirtualCubeDimension)_other;
            boolean _diff = displayAttributeDiff("cubeName", this.cubeName, _cother.cubeName, _out, _indent + 1);
            _diff = _diff && displayElementDiff("annotations", this.annotations, _cother.annotations, _out, _indent + 1);
            return _diff;
        }

        public MondrianDef.Dimension getDimension(MondrianDef.Schema schema) {
            Util.assertPrecondition(schema != null, "schema != null");
            if (this.cubeName == null) {
                return schema.getPublicDimension(this.name);
            } else {
                MondrianDef.Cube cube = schema.getCube(this.cubeName);
                return cube.getDimension(schema, this.name);
            }
        }
    }

    public static class CubeUsage extends ElementDef {
        public String cubeName;
        public Boolean ignoreUnrelatedDimensions;

        public CubeUsage() {
        }

        public CubeUsage(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.cubeName = (String)_parser.getAttribute("cubeName", "String", (String)null, (String[])null, true);
                this.ignoreUnrelatedDimensions = (Boolean)_parser.getAttribute("ignoreUnrelatedDimensions", "Boolean", "false", (String[])null, false);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "CubeUsage";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "cubeName", this.cubeName, _indent + 1);
            displayAttribute(_out, "ignoreUnrelatedDimensions", this.ignoreUnrelatedDimensions, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("CubeUsage", (new XMLAttrVector()).add("cubeName", this.cubeName).add("ignoreUnrelatedDimensions", this.ignoreUnrelatedDimensions));
            _out.endTag("CubeUsage");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.CubeUsage _cother = (MondrianDef.CubeUsage)_other;
            boolean _diff = displayAttributeDiff("cubeName", this.cubeName, _cother.cubeName, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("ignoreUnrelatedDimensions", this.ignoreUnrelatedDimensions, _cother.ignoreUnrelatedDimensions, _out, _indent + 1);
            return _diff;
        }
    }

    public static class CubeUsages extends ElementDef {
        public MondrianDef.CubeUsage[] cubeUsages;

        public CubeUsages() {
        }

        public CubeUsages(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.CubeUsage.class, 1, 0);
                this.cubeUsages = new MondrianDef.CubeUsage[_tempArray.length];

                for(int _i = 0; _i < this.cubeUsages.length; ++_i) {
                    this.cubeUsages[_i] = (MondrianDef.CubeUsage)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "CubeUsages";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayElementArray(_out, "cubeUsages", this.cubeUsages, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("CubeUsages", new XMLAttrVector());
            displayXMLElementArray(_out, this.cubeUsages);
            _out.endTag("CubeUsages");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.CubeUsages _cother = (MondrianDef.CubeUsages)_other;
            boolean _diff = displayElementArrayDiff("cubeUsages", this.cubeUsages, _cother.cubeUsages, _out, _indent + 1);
            return _diff;
        }
    }

    public static class VirtualCube extends ElementDef {
        public Boolean enabled;
        public String name;
        public String defaultMeasure;
        public String caption;
        public Boolean visible;
        public String description;
        public MondrianDef.Annotations annotations;
        public MondrianDef.CubeUsages cubeUsage;
        public MondrianDef.VirtualCubeDimension[] dimensions;
        public MondrianDef.VirtualCubeMeasure[] measures;
        public MondrianDef.CalculatedMember[] calculatedMembers;
        public MondrianDef.NamedSet[] namedSets;

        public VirtualCube() {
        }

        public VirtualCube(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.enabled = (Boolean)_parser.getAttribute("enabled", "Boolean", "true", (String[])null, false);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, false);
                this.defaultMeasure = (String)_parser.getAttribute("defaultMeasure", "String", (String)null, (String[])null, false);
                this.caption = (String)_parser.getAttribute("caption", "String", (String)null, (String[])null, false);
                this.visible = (Boolean)_parser.getAttribute("visible", "Boolean", "true", (String[])null, false);
                this.description = (String)_parser.getAttribute("description", "String", (String)null, (String[])null, false);
                this.annotations = (MondrianDef.Annotations)_parser.getElement(MondrianDef.Annotations.class, false);
                this.cubeUsage = (MondrianDef.CubeUsages)_parser.getElement(MondrianDef.CubeUsages.class, false);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.VirtualCubeDimension.class, 0, 0);
                this.dimensions = new MondrianDef.VirtualCubeDimension[_tempArray.length];

                int _i;
                for(_i = 0; _i < this.dimensions.length; ++_i) {
                    this.dimensions[_i] = (MondrianDef.VirtualCubeDimension)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.VirtualCubeMeasure.class, 0, 0);
                this.measures = new MondrianDef.VirtualCubeMeasure[_tempArray.length];

                for(_i = 0; _i < this.measures.length; ++_i) {
                    this.measures[_i] = (MondrianDef.VirtualCubeMeasure)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.CalculatedMember.class, 0, 0);
                this.calculatedMembers = new MondrianDef.CalculatedMember[_tempArray.length];

                for(_i = 0; _i < this.calculatedMembers.length; ++_i) {
                    this.calculatedMembers[_i] = (MondrianDef.CalculatedMember)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.NamedSet.class, 0, 0);
                this.namedSets = new MondrianDef.NamedSet[_tempArray.length];

                for(_i = 0; _i < this.namedSets.length; ++_i) {
                    this.namedSets[_i] = (MondrianDef.NamedSet)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "VirtualCube";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "enabled", this.enabled, _indent + 1);
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "defaultMeasure", this.defaultMeasure, _indent + 1);
            displayAttribute(_out, "caption", this.caption, _indent + 1);
            displayAttribute(_out, "visible", this.visible, _indent + 1);
            displayAttribute(_out, "description", this.description, _indent + 1);
            displayElement(_out, "annotations", this.annotations, _indent + 1);
            displayElement(_out, "cubeUsage", this.cubeUsage, _indent + 1);
            displayElementArray(_out, "dimensions", this.dimensions, _indent + 1);
            displayElementArray(_out, "measures", this.measures, _indent + 1);
            displayElementArray(_out, "calculatedMembers", this.calculatedMembers, _indent + 1);
            displayElementArray(_out, "namedSets", this.namedSets, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("VirtualCube", (new XMLAttrVector()).add("enabled", this.enabled).add("name", this.name).add("defaultMeasure", this.defaultMeasure).add("caption", this.caption).add("visible", this.visible).add("description", this.description));
            displayXMLElement(_out, this.annotations);
            displayXMLElement(_out, this.cubeUsage);
            displayXMLElementArray(_out, this.dimensions);
            displayXMLElementArray(_out, this.measures);
            displayXMLElementArray(_out, this.calculatedMembers);
            displayXMLElementArray(_out, this.namedSets);
            _out.endTag("VirtualCube");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.VirtualCube _cother = (MondrianDef.VirtualCube)_other;
            boolean _diff = displayAttributeDiff("enabled", this.enabled, _cother.enabled, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("defaultMeasure", this.defaultMeasure, _cother.defaultMeasure, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("caption", this.caption, _cother.caption, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("visible", this.visible, _cother.visible, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("description", this.description, _cother.description, _out, _indent + 1);
            _diff = _diff && displayElementDiff("annotations", this.annotations, _cother.annotations, _out, _indent + 1);
            _diff = _diff && displayElementDiff("cubeUsage", this.cubeUsage, _cother.cubeUsage, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("dimensions", this.dimensions, _cother.dimensions, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("measures", this.measures, _cother.measures, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("calculatedMembers", this.calculatedMembers, _cother.calculatedMembers, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("namedSets", this.namedSets, _cother.namedSets, _out, _indent + 1);
            return _diff;
        }

        public boolean isEnabled() {
            return this.enabled;
        }
    }

    public static class Cube extends ElementDef {
        public String name;
        public String caption;
        public Boolean visible;
        public String description;
        public String defaultMeasure;
        public Boolean cache;
        public Boolean enabled;
        public MondrianDef.Annotations annotations;
        public MondrianDef.Relation fact;
        public MondrianDef.WritebackTable[] writebacks;
        public MondrianDef.CubeDimension[] dimensions;
        public MondrianDef.Measure[] measures;
        public MondrianDef.CalculatedMember[] calculatedMembers;
        public MondrianDef.NamedSet[] namedSets;
        public MondrianDef.Action[] actions;

        public Cube() {
        }

        public Cube(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.caption = (String)_parser.getAttribute("caption", "String", (String)null, (String[])null, false);
                this.visible = (Boolean)_parser.getAttribute("visible", "Boolean", "true", (String[])null, false);
                this.description = (String)_parser.getAttribute("description", "String", (String)null, (String[])null, false);
                this.defaultMeasure = (String)_parser.getAttribute("defaultMeasure", "String", (String)null, (String[])null, false);
                this.cache = (Boolean)_parser.getAttribute("cache", "Boolean", "true", (String[])null, false);
                this.enabled = (Boolean)_parser.getAttribute("enabled", "Boolean", "true", (String[])null, false);
                this.annotations = (MondrianDef.Annotations)_parser.getElement(MondrianDef.Annotations.class, false);
                this.fact = (MondrianDef.Relation)_parser.getElement(MondrianDef.Relation.class, true);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.WritebackTable.class, 0, 0);
                this.writebacks = new MondrianDef.WritebackTable[_tempArray.length];

                int _i;
                for(_i = 0; _i < this.writebacks.length; ++_i) {
                    this.writebacks[_i] = (MondrianDef.WritebackTable)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.CubeDimension.class, 0, 0);
                this.dimensions = new MondrianDef.CubeDimension[_tempArray.length];

                for(_i = 0; _i < this.dimensions.length; ++_i) {
                    this.dimensions[_i] = (MondrianDef.CubeDimension)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.Measure.class, 0, 0);
                this.measures = new MondrianDef.Measure[_tempArray.length];

                for(_i = 0; _i < this.measures.length; ++_i) {
                    this.measures[_i] = (MondrianDef.Measure)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.CalculatedMember.class, 0, 0);
                this.calculatedMembers = new MondrianDef.CalculatedMember[_tempArray.length];

                for(_i = 0; _i < this.calculatedMembers.length; ++_i) {
                    this.calculatedMembers[_i] = (MondrianDef.CalculatedMember)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.NamedSet.class, 0, 0);
                this.namedSets = new MondrianDef.NamedSet[_tempArray.length];

                for(_i = 0; _i < this.namedSets.length; ++_i) {
                    this.namedSets[_i] = (MondrianDef.NamedSet)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.Action.class, 0, 0);
                this.actions = new MondrianDef.Action[_tempArray.length];

                for(_i = 0; _i < this.actions.length; ++_i) {
                    this.actions[_i] = (MondrianDef.Action)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "Cube";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "caption", this.caption, _indent + 1);
            displayAttribute(_out, "visible", this.visible, _indent + 1);
            displayAttribute(_out, "description", this.description, _indent + 1);
            displayAttribute(_out, "defaultMeasure", this.defaultMeasure, _indent + 1);
            displayAttribute(_out, "cache", this.cache, _indent + 1);
            displayAttribute(_out, "enabled", this.enabled, _indent + 1);
            displayElement(_out, "annotations", this.annotations, _indent + 1);
            displayElement(_out, "fact", this.fact, _indent + 1);
            displayElementArray(_out, "writebacks", this.writebacks, _indent + 1);
            displayElementArray(_out, "dimensions", this.dimensions, _indent + 1);
            displayElementArray(_out, "measures", this.measures, _indent + 1);
            displayElementArray(_out, "calculatedMembers", this.calculatedMembers, _indent + 1);
            displayElementArray(_out, "namedSets", this.namedSets, _indent + 1);
            displayElementArray(_out, "actions", this.actions, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Cube", (new XMLAttrVector()).add("name", this.name).add("caption", this.caption).add("visible", this.visible).add("description", this.description).add("defaultMeasure", this.defaultMeasure).add("cache", this.cache).add("enabled", this.enabled));
            displayXMLElement(_out, this.annotations);
            displayXMLElement(_out, this.fact);
            displayXMLElementArray(_out, this.writebacks);
            displayXMLElementArray(_out, this.dimensions);
            displayXMLElementArray(_out, this.measures);
            displayXMLElementArray(_out, this.calculatedMembers);
            displayXMLElementArray(_out, this.namedSets);
            displayXMLElementArray(_out, this.actions);
            _out.endTag("Cube");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Cube _cother = (MondrianDef.Cube)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("caption", this.caption, _cother.caption, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("visible", this.visible, _cother.visible, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("description", this.description, _cother.description, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("defaultMeasure", this.defaultMeasure, _cother.defaultMeasure, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("cache", this.cache, _cother.cache, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("enabled", this.enabled, _cother.enabled, _out, _indent + 1);
            _diff = _diff && displayElementDiff("annotations", this.annotations, _cother.annotations, _out, _indent + 1);
            _diff = _diff && displayElementDiff("fact", this.fact, _cother.fact, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("writebacks", this.writebacks, _cother.writebacks, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("dimensions", this.dimensions, _cother.dimensions, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("measures", this.measures, _cother.measures, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("calculatedMembers", this.calculatedMembers, _cother.calculatedMembers, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("namedSets", this.namedSets, _cother.namedSets, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("actions", this.actions, _cother.actions, _out, _indent + 1);
            return _diff;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        MondrianDef.Dimension getDimension(MondrianDef.Schema xmlSchema, String dimensionName) {
            for(int i = 0; i < this.dimensions.length; ++i) {
                if (this.dimensions[i].name.equals(dimensionName)) {
                    return this.dimensions[i].getDimension(xmlSchema);
                }
            }

            throw Util.newInternal("Cannot find dimension '" + dimensionName + "' in cube '" + this.name + "'");
        }
    }

    public abstract static class CubeDimension extends ElementDef {
        public String name;
        public String caption;
        public Boolean visible;
        public String description;
        public String foreignKey;
        public Boolean highCardinality;
        public MondrianDef.Annotations annotations;

        public CubeDimension() {
        }

        public CubeDimension(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.caption = (String)_parser.getAttribute("caption", "String", (String)null, (String[])null, false);
                this.visible = (Boolean)_parser.getAttribute("visible", "Boolean", "true", (String[])null, false);
                this.description = (String)_parser.getAttribute("description", "String", (String)null, (String[])null, false);
                this.foreignKey = (String)_parser.getAttribute("foreignKey", "String", (String)null, (String[])null, false);
                this.highCardinality = (Boolean)_parser.getAttribute("highCardinality", "Boolean", "false", (String[])null, false);
                this.annotations = (MondrianDef.Annotations)_parser.getElement(MondrianDef.Annotations.class, false);
            } catch (XOMException var3) {
                throw new XOMException("In " + this.getName() + ": " + var3.getMessage());
            }
        }

        public String getName() {
            return "CubeDimension";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "caption", this.caption, _indent + 1);
            displayAttribute(_out, "visible", this.visible, _indent + 1);
            displayAttribute(_out, "description", this.description, _indent + 1);
            displayAttribute(_out, "foreignKey", this.foreignKey, _indent + 1);
            displayAttribute(_out, "highCardinality", this.highCardinality, _indent + 1);
            displayElement(_out, "annotations", this.annotations, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("CubeDimension", (new XMLAttrVector()).add("name", this.name).add("caption", this.caption).add("visible", this.visible).add("description", this.description).add("foreignKey", this.foreignKey).add("highCardinality", this.highCardinality));
            displayXMLElement(_out, this.annotations);
            _out.endTag("CubeDimension");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.CubeDimension _cother = (MondrianDef.CubeDimension)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("caption", this.caption, _cother.caption, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("visible", this.visible, _cother.visible, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("description", this.description, _cother.description, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("foreignKey", this.foreignKey, _cother.foreignKey, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("highCardinality", this.highCardinality, _cother.highCardinality, _out, _indent + 1);
            _diff = _diff && displayElementDiff("annotations", this.annotations, _cother.annotations, _out, _indent + 1);
            return _diff;
        }

        public abstract MondrianDef.Dimension getDimension(MondrianDef.Schema var1);
    }

    public static class Schema extends ElementDef {
        public String name;
        public String description;
        public String measuresCaption;
        public String defaultRole;
        public MondrianDef.Annotations annotations;
        public MondrianDef.Parameter[] parameters;
        public MondrianDef.View[] views;
        public MondrianDef.Dimension[] dimensions;
        public MondrianDef.Cube[] cubes;
        public MondrianDef.VirtualCube[] virtualCubes;
        public MondrianDef.NamedSet[] namedSets;
        public MondrianDef.Role[] roles;
        public MondrianDef.UserDefinedFunction[] userDefinedFunctions;

        public Schema() {
        }

        public Schema(DOMWrapper _def) throws XOMException {
            try {
                DOMElementParser _parser = new DOMElementParser(_def, "", MondrianDef.class);
                this.name = (String)_parser.getAttribute("name", "String", (String)null, (String[])null, true);
                this.description = (String)_parser.getAttribute("description", "String", (String)null, (String[])null, false);
                this.measuresCaption = (String)_parser.getAttribute("measuresCaption", "String", (String)null, (String[])null, false);
                this.defaultRole = (String)_parser.getAttribute("defaultRole", "String", (String)null, (String[])null, false);
                this.annotations = (MondrianDef.Annotations)_parser.getElement(MondrianDef.Annotations.class, false);
                NodeDef[] _tempArray = _parser.getArray(MondrianDef.Parameter.class, 0, 0);
                this.parameters = new MondrianDef.Parameter[_tempArray.length];

                int _i;
                for(_i = 0; _i < this.parameters.length; ++_i) {
                    this.parameters[_i] = (MondrianDef.Parameter)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.View.class, 0, 0);
                this.views = new MondrianDef.View[_tempArray.length];

                for(_i = 0; _i < this.views.length; ++_i) {
                    this.views[_i] = (MondrianDef.View)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.Dimension.class, 0, 0);
                this.dimensions = new MondrianDef.Dimension[_tempArray.length];

                for(_i = 0; _i < this.dimensions.length; ++_i) {
                    this.dimensions[_i] = (MondrianDef.Dimension)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.Cube.class, 0, 0);
                this.cubes = new MondrianDef.Cube[_tempArray.length];

                for(_i = 0; _i < this.cubes.length; ++_i) {
                    this.cubes[_i] = (MondrianDef.Cube)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.VirtualCube.class, 0, 0);
                this.virtualCubes = new MondrianDef.VirtualCube[_tempArray.length];

                for(_i = 0; _i < this.virtualCubes.length; ++_i) {
                    this.virtualCubes[_i] = (MondrianDef.VirtualCube)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.NamedSet.class, 0, 0);
                this.namedSets = new MondrianDef.NamedSet[_tempArray.length];

                for(_i = 0; _i < this.namedSets.length; ++_i) {
                    this.namedSets[_i] = (MondrianDef.NamedSet)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.Role.class, 0, 0);
                this.roles = new MondrianDef.Role[_tempArray.length];

                for(_i = 0; _i < this.roles.length; ++_i) {
                    this.roles[_i] = (MondrianDef.Role)_tempArray[_i];
                }

                _tempArray = _parser.getArray(MondrianDef.UserDefinedFunction.class, 0, 0);
                this.userDefinedFunctions = new MondrianDef.UserDefinedFunction[_tempArray.length];

                for(_i = 0; _i < this.userDefinedFunctions.length; ++_i) {
                    this.userDefinedFunctions[_i] = (MondrianDef.UserDefinedFunction)_tempArray[_i];
                }

            } catch (XOMException var5) {
                throw new XOMException("In " + this.getName() + ": " + var5.getMessage());
            }
        }

        public String getName() {
            return "Schema";
        }

        public void display(PrintWriter _out, int _indent) {
            _out.println(this.getName());
            displayAttribute(_out, "name", this.name, _indent + 1);
            displayAttribute(_out, "description", this.description, _indent + 1);
            displayAttribute(_out, "measuresCaption", this.measuresCaption, _indent + 1);
            displayAttribute(_out, "defaultRole", this.defaultRole, _indent + 1);
            displayElement(_out, "annotations", this.annotations, _indent + 1);
            displayElementArray(_out, "parameters", this.parameters, _indent + 1);
            displayElementArray(_out, "views", this.views, _indent + 1);
            displayElementArray(_out, "dimensions", this.dimensions, _indent + 1);
            displayElementArray(_out, "cubes", this.cubes, _indent + 1);
            displayElementArray(_out, "virtualCubes", this.virtualCubes, _indent + 1);
            displayElementArray(_out, "namedSets", this.namedSets, _indent + 1);
            displayElementArray(_out, "roles", this.roles, _indent + 1);
            displayElementArray(_out, "userDefinedFunctions", this.userDefinedFunctions, _indent + 1);
        }

        public void displayXML(XMLOutput _out, int _indent) {
            _out.beginTag("Schema", (new XMLAttrVector()).add("name", this.name).add("description", this.description).add("measuresCaption", this.measuresCaption).add("defaultRole", this.defaultRole));
            displayXMLElement(_out, this.annotations);
            displayXMLElementArray(_out, this.parameters);
            displayXMLElementArray(_out, this.views);
            displayXMLElementArray(_out, this.dimensions);
            displayXMLElementArray(_out, this.cubes);
            displayXMLElementArray(_out, this.virtualCubes);
            displayXMLElementArray(_out, this.namedSets);
            displayXMLElementArray(_out, this.roles);
            displayXMLElementArray(_out, this.userDefinedFunctions);
            _out.endTag("Schema");
        }

        public boolean displayDiff(ElementDef _other, PrintWriter _out, int _indent) {
            MondrianDef.Schema _cother = (MondrianDef.Schema)_other;
            boolean _diff = displayAttributeDiff("name", this.name, _cother.name, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("description", this.description, _cother.description, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("measuresCaption", this.measuresCaption, _cother.measuresCaption, _out, _indent + 1);
            _diff = _diff && displayAttributeDiff("defaultRole", this.defaultRole, _cother.defaultRole, _out, _indent + 1);
            _diff = _diff && displayElementDiff("annotations", this.annotations, _cother.annotations, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("parameters", this.parameters, _cother.parameters, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("views", this.views, _cother.views, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("dimensions", this.dimensions, _cother.dimensions, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("cubes", this.cubes, _cother.cubes, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("virtualCubes", this.virtualCubes, _cother.virtualCubes, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("namedSets", this.namedSets, _cother.namedSets, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("roles", this.roles, _cother.roles, _out, _indent + 1);
            _diff = _diff && displayElementArrayDiff("userDefinedFunctions", this.userDefinedFunctions, _cother.userDefinedFunctions, _out, _indent + 1);
            return _diff;
        }

        MondrianDef.Cube getCube(String cubeName) {
            for(int i = 0; i < this.cubes.length; ++i) {
                if (this.cubes[i].name.equals(cubeName)) {
                    return this.cubes[i];
                }
            }

            throw Util.newInternal("Cannot find cube '" + cubeName + "'");
        }

        MondrianDef.Dimension getPublicDimension(String dimensionName) {
            for(int i = 0; i < this.dimensions.length; ++i) {
                if (this.dimensions[i].name.equals(dimensionName)) {
                    return this.dimensions[i];
                }
            }

            throw Util.newInternal("Cannot find public dimension '" + dimensionName + "'");
        }
    }
}