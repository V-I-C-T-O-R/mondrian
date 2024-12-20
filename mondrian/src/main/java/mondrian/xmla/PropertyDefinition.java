/* Decompiler 170ms, total 892ms, lines 72 */
package mondrian.xmla;

import java.util.Set;
import mondrian.xmla.RowsetDefinition.Type;
import org.olap4j.impl.Olap4jUtil;
import org.olap4j.metadata.XmlaConstants.Access;
import org.olap4j.metadata.XmlaConstants.AxisFormat;
import org.olap4j.metadata.XmlaConstants.Content;
import org.olap4j.metadata.XmlaConstants.Format;
import org.olap4j.metadata.XmlaConstants.MdxSupport;
import org.olap4j.metadata.XmlaConstants.Method;
import org.olap4j.metadata.XmlaConstants.StateSupport;
import org.olap4j.metadata.XmlaConstants.VisualMode;

public enum PropertyDefinition {
    AxisFormat(Type.Enumeration, Olap4jUtil.enumSetAllOf(AxisFormat.class), Access.Write, "", Method.EXECUTE, "Determines the format used within an MDDataSet result set to describe the axes of the multidimensional dataset. This property can have the values listed in the following table: TupleFormat (default), ClusterFormat, CustomFormat."),
    BeginRange(Type.Integer, (Set)null, Access.Write, "-1", Method.EXECUTE, "Contains a zero-based integer value corresponding to a CellOrdinal attribute value. (The CellOrdinal attribute is part of the Cell element in the CellData section of MDDataSet.)\nUsed together with the EndRange property, the client application can use this property to restrict an OLAP dataset returned by a command to a specific range of cells. If -1 is specified, all cells up to the cell specified in the EndRange property are returned.\nThe default value for this property is -1."),
    Catalog(Type.String, (Set)null, Access.ReadWrite, "", Method.DISCOVER_AND_EXECUTE, "When establishing a session with an Analysis Services instance to send an XMLA command, this property is equivalent to the OLE DB property, DBPROP_INIT_CATALOG.\nWhen you set this property during a session to change the current database for the session, this property is equivalent to the OLE DB property, DBPROP_CURRENTCATALOG.\nThe default value for this property is an empty string."),
    Content(Type.EnumString, Olap4jUtil.enumSetAllOf(Content.class), Access.Write, org.olap4j.metadata.XmlaConstants.Content.DEFAULT.name(), Method.DISCOVER_AND_EXECUTE, "An enumerator that specifies what type of data is returned in the result set.\nNone: Allows the structure of the command to be verified, but not executed. Analogous to using Prepare to check syntax, and so on.\nSchema: Contains the XML schema (which indicates column information, and so on) that relates to the requested query.\nData: Contains only the data that was requested.\nSchemaData: Returns both the schema information as well as the data."),
    Cube(Type.String, (Set)null, Access.ReadWrite, "", Method.EXECUTE, "The cube context for the Command parameter. If the command contains a cube name (such as an MDX FROM clause) the setting of this property is ignored."),
    DataSourceInfo(Type.String, (Set)null, Access.ReadWrite, "", Method.DISCOVER_AND_EXECUTE, "A string containing provider specific information, required to access the data source."),
    Deep(Type.Boolean, (Set)null, Access.ReadWrite, "", Method.DISCOVER, "In an MDSCHEMA_CUBES request, whether to include sub-elements (dimensions, hierarchies, levels, measures, named sets) of each cube."),
    EmitInvisibleMembers(Type.Boolean, (Set)null, Access.ReadWrite, "", Method.DISCOVER, "Whether to include members whose VISIBLE property is false, or measures whose MEASURE_IS_VISIBLE property is false."),
    EndRange(Type.Integer, (Set)null, Access.Write, "-1", Method.EXECUTE, "An integer value corresponding to a CellOrdinal used to restrict an MDDataSet returned by a command to a specific range of cells. Used in conjunction with the BeginRange property. If unspecified, all cells are returned in the rowset. The value -1 means unspecified."),
    Format(Type.EnumString, Olap4jUtil.enumSetAllOf(Format.class), Access.Write, "Native", Method.DISCOVER_AND_EXECUTE, "Enumerator that determines the format of the returned result set. Values include:\nTabular: a flat or hierarchical rowset. Similar to the XML RAW format in SQL. The Format property should be set to Tabular for OLE DB for Data Mining commands.\nMultidimensional: Indicates that the result set will use the MDDataSet format (Execute method only).\nNative: The client does not request a specific format, so the provider may return the format  appropriate to the query. (The actual result type is identified by namespace of the result.)"),
    LocaleIdentifier(Type.UnsignedInteger, (Set)null, Access.ReadWrite, "None", Method.DISCOVER_AND_EXECUTE, "Use this to read or set the numeric locale identifier for this request. The default is provider-specific.\nFor the complete hexadecimal list of language identifiers, search on \"Language Identifiers\" in the MSDN Library at http://www.msdn.microsoft.com.\nAs an extension to the XMLA standard, Mondrian also allows locale codes as specified by ISO-639 and ISO-3166 and as used by Java; for example 'en-US'.\n"),
    MDXSupport(Type.EnumString, Olap4jUtil.enumSetAllOf(MdxSupport.class), Access.Read, "Core", Method.DISCOVER, "Enumeration that describes the degree of MDX support. At initial release Core is the only value in the enumeration. In future releases, other values will be defined for this enumeration."),
    Password(Type.String, (Set)null, Access.Read, "", Method.DISCOVER_AND_EXECUTE, "This property is deprecated in XMLA 1.1. To support legacy applications, the provider accepts but ignores the Password property setting when it is used with the Discover and Execute method"),
    ProviderName(Type.String, (Set)null, Access.Read, "Mondrian XML for Analysis Provider", Method.DISCOVER, "The XML for Analysis Provider name."),
    ProviderVersion(Type.String, (Set)null, Access.Read, "10.50.1600.1", Method.DISCOVER, "The version of the Mondrian XMLA Provider"),
    ResponseMimeType(Type.String, (Set)null, Access.ReadWrite, "None", Method.DISCOVER_AND_EXECUTE, "Accepted mime type for RPC response; accepted are 'text/xml' (default), 'application/xml' (equivalent to 'text/xml'), or 'application/json'. If not specified, value in the 'Accept' header of the HTTP request is used."),
    StateSupport(Type.EnumString, Olap4jUtil.enumSetAllOf(StateSupport.class), Access.Read, "None", Method.DISCOVER, "Property that specifies the degree of support in the provider for state. For information about state in XML for Analysis, see \"Support for Statefulness in XML for Analysis.\" Minimum enumeration values are as follows:\nNone - No support for sessions or stateful operations.\nSessions - Provider supports sessions."),
    Timeout(Type.UnsignedInteger, (Set)null, Access.ReadWrite, "Undefined", Method.DISCOVER_AND_EXECUTE, "A numeric time-out specifying in seconds the amount of time to wait for a request to be successful."),
    UserName(Type.String, (Set)null, Access.Read, "", Method.DISCOVER_AND_EXECUTE, "Returns the UserName the server associates with the command.\nThis property is deprecated as writeable in XMLA 1.1. To support legacy applications, servers accept but ignore the password setting when it is used with the Execute method."),
    VisualMode(Type.Enumeration, Olap4jUtil.enumSetAllOf(VisualMode.class), Access.Write, Integer.toString(org.olap4j.metadata.XmlaConstants.VisualMode.VISUAL.ordinal()), Method.DISCOVER_AND_EXECUTE, "This property is equivalent to the OLE DB property, MDPROP_VISUALMODE.\nThe default value for this property is zero (0), equivalent to DBPROPVAL_VISUAL_MODE_DEFAULT."),
    TableFields(Type.String, (Set)null, Access.Read, "", Method.DISCOVER_AND_EXECUTE, "List of fields to return for drill-through.\nThe default value of this property is the empty string,in which case, all fields are returned."),
    AdvancedFlag(Type.Boolean, (Set)null, Access.Read, "false", Method.DISCOVER_AND_EXECUTE, ""),
    SafetyOptions(Type.Integer, (Set)null, Access.ReadWrite, "0", Method.DISCOVER_AND_EXECUTE, "Determines whether unsafe libraries can be registered and loaded by client applications."),
    MdxMissingMemberMode(Type.String, (Set)null, Access.Write, "", Method.DISCOVER_AND_EXECUTE, "Indicates whether missing members are ignored in MDX statements."),
    DbpropMsmdMDXCompatibility(Type.Integer, (Set)null, Access.ReadWrite, "0", Method.DISCOVER_AND_EXECUTE, "An enumeration value that determines how placeholder members in a ragged or\nunbalanced hierarchy are treated."),
    MdpropMdxSubqueries(Type.Integer, (Set)null, Access.Read, "", Method.DISCOVER, "A bitmask that indicates the level of support for subqueries in MDX."),
    ClientProcessID(Type.Integer, (Set)null, Access.ReadWrite, "0", Method.DISCOVER_AND_EXECUTE, "The ID of the client process."),
    SspropInitAppName(Type.String, (Set)null, Access.ReadWrite, "", Method.DISCOVER_AND_EXECUTE, "The name of the client application."),
    DbpropMsmdSubqueries(Type.Integer, (Set)null, Access.ReadWrite, "1", Method.DISCOVER_AND_EXECUTE, "An enumeration value that determines the behavior of subqueries.");

    final Type type;
    final Set<? extends Enum> enumSet;
    final Access access;
    final Method usage;
    final String value;
    final String description;

    private PropertyDefinition(Type type, Set<? extends Enum> enumSet, Access access, String value, Method usage, String description) {
        assert description.indexOf(13) == -1;

        assert value.indexOf(13) == -1;

        assert enumSet != null == type.isEnum();

        this.type = type;
        this.enumSet = enumSet;
        this.access = access;
        this.usage = usage;
        this.value = value;
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}