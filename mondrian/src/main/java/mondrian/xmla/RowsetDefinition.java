/* Decompiler 4243ms, total 5631ms, lines 4053 */
package mondrian.xmla;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import mondrian.olap.MondrianProperties;
import mondrian.olap.MondrianServer;
import mondrian.olap.SetBase;
import mondrian.olap.Util;
import mondrian.olap.Util.Functor1;
import mondrian.olap4j.MondrianOlap4jConnection;
import mondrian.olap4j.MondrianOlap4jHierarchy;
import mondrian.olap4j.MondrianOlap4jMember;
import mondrian.olap4j.MondrianOlap4jNamedSet;
import mondrian.rolap.RolapConnection;
import mondrian.rolap.RolapHierarchy;
import mondrian.rolap.RolapSchema;
import mondrian.server.FileRepository;
import mondrian.server.Repository;
import mondrian.util.Composite;
import mondrian.xmla.Rowset.Row;
import mondrian.xmla.Rowset.XmlElement;
import mondrian.xmla.XmlaConstants.Literal;
import mondrian.xmla.XmlaHandler.XmlaExtra;
import mondrian.xmla.XmlaHandler.XmlaExtra.FunctionDefinition;
import mondrian.xmla.XmlaUtil.ElementNameEncoder;
import org.olap4j.OlapConnection;
import org.olap4j.OlapException;
import org.olap4j.impl.ArrayNamedListImpl;
import org.olap4j.impl.Olap4jUtil;
import org.olap4j.mdx.IdentifierNode;
import org.olap4j.mdx.IdentifierSegment;
import org.olap4j.metadata.Catalog;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Measure;
import org.olap4j.metadata.Member;
import org.olap4j.metadata.MetadataElement;
import org.olap4j.metadata.NamedList;
import org.olap4j.metadata.NamedSet;
import org.olap4j.metadata.Property;
import org.olap4j.metadata.Schema;
import org.olap4j.metadata.XmlaConstant;
import org.olap4j.metadata.Member.TreeOp;
import org.olap4j.metadata.Property.ContentType;
import org.olap4j.metadata.Property.StandardCellProperty;
import org.olap4j.metadata.Property.StandardMemberProperty;
import org.olap4j.metadata.Property.TypeFlag;
import org.olap4j.metadata.XmlaConstants.DBType;
import org.olap4j.metadata.XmlaConstants.EnumWithDesc;
import org.olap4j.metadata.XmlaConstants.Method;

public enum RowsetDefinition {
    DISCOVER_DATASOURCES(0, "06C03D41-F66D-49F3-B1B8-987F7AF4CF18", "Returns a list of XML for Analysis data sources available on the server or Web Service.", new RowsetDefinition.Column[]{RowsetDefinition.DiscoverDatasourcesRowset.DataSourceName, RowsetDefinition.DiscoverDatasourcesRowset.DataSourceDescription, RowsetDefinition.DiscoverDatasourcesRowset.URL, RowsetDefinition.DiscoverDatasourcesRowset.DataSourceInfo, RowsetDefinition.DiscoverDatasourcesRowset.ProviderName, RowsetDefinition.DiscoverDatasourcesRowset.ProviderType, RowsetDefinition.DiscoverDatasourcesRowset.AuthenticationMode}, new RowsetDefinition.Column[]{RowsetDefinition.DiscoverDatasourcesRowset.DataSourceName}) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.DiscoverDatasourcesRowset(request, handler);
        }
    },
    DISCOVER_SCHEMA_ROWSETS(2, "EEA0302B-7922-4992-8991-0E605D0E5593", "Returns the names, values, and other information of all supported RequestType enumeration values.", new RowsetDefinition.Column[]{RowsetDefinition.DiscoverSchemaRowsetsRowset.SchemaName, RowsetDefinition.DiscoverSchemaRowsetsRowset.SchemaGuid, RowsetDefinition.DiscoverSchemaRowsetsRowset.Restrictions, RowsetDefinition.DiscoverSchemaRowsetsRowset.Description, RowsetDefinition.DiscoverSchemaRowsetsRowset.RestrictionsMask}, new RowsetDefinition.Column[]{RowsetDefinition.DiscoverSchemaRowsetsRowset.SchemaName}) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.DiscoverSchemaRowsetsRowset(request, handler);
        }

        protected void writeRowsetXmlSchemaRowDef(SaxWriter writer) {
            writer.startElement("xsd:complexType", new Object[]{"name", "row"});
            writer.startElement("xsd:sequence");
            RowsetDefinition.Column[] var2 = this.columnDefinitions;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                RowsetDefinition.Column column = var2[var4];
                String name = ElementNameEncoder.INSTANCE.encode(column.name);
                if (column == RowsetDefinition.DiscoverSchemaRowsetsRowset.Restrictions) {
                    writer.startElement("xsd:element", new Object[]{"sql:field", column.name, "name", name, "minOccurs", 0, "maxOccurs", "unbounded"});
                    writer.startElement("xsd:complexType");
                    writer.startElement("xsd:sequence");
                    writer.element("xsd:element", new Object[]{"name", "Name", "type", "xsd:string", "sql:field", "Name"});
                    writer.element("xsd:element", new Object[]{"name", "Type", "type", "xsd:string", "sql:field", "Type"});
                    writer.endElement();
                    writer.endElement();
                    writer.endElement();
                } else {
                    String xsdType = column.type.columnType;
                    Object[] attrs;
                    if (column.nullable) {
                        if (column.unbounded) {
                            attrs = new Object[]{"sql:field", column.name, "name", name, "type", xsdType, "minOccurs", 0, "maxOccurs", "unbounded"};
                        } else {
                            attrs = new Object[]{"sql:field", column.name, "name", name, "type", xsdType, "minOccurs", 0};
                        }
                    } else if (column.unbounded) {
                        attrs = new Object[]{"sql:field", column.name, "name", name, "type", xsdType, "maxOccurs", "unbounded"};
                    } else {
                        attrs = new Object[]{"sql:field", column.name, "name", name, "type", xsdType};
                    }

                    writer.element("xsd:element", attrs);
                }
            }

            writer.endElement();
            writer.endElement();
        }
    },
    DISCOVER_ENUMERATORS(3, "55A9E78B-ACCB-45B4-95A6-94C5065617A7", "Returns a list of names, data types, and enumeration values for enumerators supported by the provider of a specific data source.", new RowsetDefinition.Column[]{RowsetDefinition.DiscoverEnumeratorsRowset.EnumName, RowsetDefinition.DiscoverEnumeratorsRowset.EnumDescription, RowsetDefinition.DiscoverEnumeratorsRowset.EnumType, RowsetDefinition.DiscoverEnumeratorsRowset.ElementName, RowsetDefinition.DiscoverEnumeratorsRowset.ElementDescription, RowsetDefinition.DiscoverEnumeratorsRowset.ElementValue}, (RowsetDefinition.Column[])null) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.DiscoverEnumeratorsRowset(request, handler);
        }
    },
    DISCOVER_PROPERTIES(1, "4B40ADFB-8B09-4758-97BB-636E8AE97BCF", "Returns a list of information and values about the requested properties that are supported by the specified data source provider.", new RowsetDefinition.Column[]{RowsetDefinition.DiscoverPropertiesRowset.PropertyName, RowsetDefinition.DiscoverPropertiesRowset.PropertyDescription, RowsetDefinition.DiscoverPropertiesRowset.PropertyType, RowsetDefinition.DiscoverPropertiesRowset.PropertyAccessType, RowsetDefinition.DiscoverPropertiesRowset.IsRequired, RowsetDefinition.DiscoverPropertiesRowset.Value}, (RowsetDefinition.Column[])null) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.DiscoverPropertiesRowset(request, handler);
        }
    },
    DISCOVER_KEYWORDS(4, "1426C443-4CDD-4A40-8F45-572FAB9BBAA1", "Returns an XML list of keywords reserved by the provider.", new RowsetDefinition.Column[]{RowsetDefinition.DiscoverKeywordsRowset.Keyword}, (RowsetDefinition.Column[])null) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.DiscoverKeywordsRowset(request, handler);
        }
    },
    DISCOVER_LITERALS(5, "C3EF5ECB-0A07-4665-A140-B075722DBDC2", "Returns information about literals supported by the provider.", new RowsetDefinition.Column[]{RowsetDefinition.DiscoverLiteralsRowset.LiteralName, RowsetDefinition.DiscoverLiteralsRowset.LiteralValue, RowsetDefinition.DiscoverLiteralsRowset.LiteralInvalidChars, RowsetDefinition.DiscoverLiteralsRowset.LiteralInvalidStartingChars, RowsetDefinition.DiscoverLiteralsRowset.LiteralMaxLength, RowsetDefinition.DiscoverLiteralsRowset.LiteralNameEnumValue}, (RowsetDefinition.Column[])null) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.DiscoverLiteralsRowset(request, handler);
        }
    },
    DISCOVER_XML_METADATA(23, "3444B255-171E-4CB9-AD98-19E57888A75F", "Returns an XML document describing a requested object. The rowset that is returned always consists of one row and one column.", new RowsetDefinition.Column[]{RowsetDefinition.DiscoverXmlMetadataRowset.METADATA, RowsetDefinition.DiscoverXmlMetadataRowset.ObjectType, RowsetDefinition.DiscoverXmlMetadataRowset.DatabaseID}, (RowsetDefinition.Column[])null) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.DiscoverXmlMetadataRowset(request, handler);
        }
    },
    DBSCHEMA_CATALOGS(6, "C8B52211-5CF3-11CE-ADE5-00AA0044773D", "Identifies the physical attributes associated with catalogs accessible from the provider.", new RowsetDefinition.Column[]{RowsetDefinition.DbschemaCatalogsRowset.CatalogName, RowsetDefinition.DbschemaCatalogsRowset.Description, RowsetDefinition.DbschemaCatalogsRowset.Roles, RowsetDefinition.DbschemaCatalogsRowset.DateModified}, new RowsetDefinition.Column[]{RowsetDefinition.DbschemaCatalogsRowset.CatalogName}) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.DbschemaCatalogsRowset(request, handler);
        }
    },
    DBSCHEMA_COLUMNS(7, "C8B52214-5CF3-11CE-ADE5-00AA0044773D", (String)null, new RowsetDefinition.Column[]{RowsetDefinition.DbschemaColumnsRowset.TableCatalog, RowsetDefinition.DbschemaColumnsRowset.TableSchema, RowsetDefinition.DbschemaColumnsRowset.TableName, RowsetDefinition.DbschemaColumnsRowset.ColumnName, RowsetDefinition.DbschemaColumnsRowset.OrdinalPosition, RowsetDefinition.DbschemaColumnsRowset.ColumnHasDefault, RowsetDefinition.DbschemaColumnsRowset.ColumnFlags, RowsetDefinition.DbschemaColumnsRowset.IsNullable, RowsetDefinition.DbschemaColumnsRowset.DataType, RowsetDefinition.DbschemaColumnsRowset.CharacterMaximumLength, RowsetDefinition.DbschemaColumnsRowset.CharacterOctetLength, RowsetDefinition.DbschemaColumnsRowset.NumericPrecision, RowsetDefinition.DbschemaColumnsRowset.NumericScale}, new RowsetDefinition.Column[]{RowsetDefinition.DbschemaColumnsRowset.TableCatalog, RowsetDefinition.DbschemaColumnsRowset.TableSchema, RowsetDefinition.DbschemaColumnsRowset.TableName}) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.DbschemaColumnsRowset(request, handler);
        }
    },
    DBSCHEMA_PROVIDER_TYPES(8, "C8B5222C-5CF3-11CE-ADE5-00AA0044773D", (String)null, new RowsetDefinition.Column[]{RowsetDefinition.DbschemaProviderTypesRowset.TypeName, RowsetDefinition.DbschemaProviderTypesRowset.DataType, RowsetDefinition.DbschemaProviderTypesRowset.ColumnSize, RowsetDefinition.DbschemaProviderTypesRowset.LiteralPrefix, RowsetDefinition.DbschemaProviderTypesRowset.LiteralSuffix, RowsetDefinition.DbschemaProviderTypesRowset.IsNullable, RowsetDefinition.DbschemaProviderTypesRowset.CaseSensitive, RowsetDefinition.DbschemaProviderTypesRowset.Searchable, RowsetDefinition.DbschemaProviderTypesRowset.UnsignedAttribute, RowsetDefinition.DbschemaProviderTypesRowset.FixedPrecScale, RowsetDefinition.DbschemaProviderTypesRowset.AutoUniqueValue, RowsetDefinition.DbschemaProviderTypesRowset.IsLong, RowsetDefinition.DbschemaProviderTypesRowset.BestMatch}, new RowsetDefinition.Column[]{RowsetDefinition.DbschemaProviderTypesRowset.DataType}) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.DbschemaProviderTypesRowset(request, handler);
        }
    },
    DBSCHEMA_SCHEMATA(8, "c8b52225-5cf3-11ce-ade5-00aa0044773d", (String)null, new RowsetDefinition.Column[]{RowsetDefinition.DbschemaSchemataRowset.CatalogName, RowsetDefinition.DbschemaSchemataRowset.SchemaName, RowsetDefinition.DbschemaSchemataRowset.SchemaOwner}, new RowsetDefinition.Column[]{RowsetDefinition.DbschemaSchemataRowset.CatalogName, RowsetDefinition.DbschemaSchemataRowset.SchemaName, RowsetDefinition.DbschemaSchemataRowset.SchemaOwner}) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.DbschemaSchemataRowset(request, handler);
        }
    },
    DBSCHEMA_TABLES(9, "C8B52229-5CF3-11CE-ADE5-00AA0044773D", (String)null, new RowsetDefinition.Column[]{RowsetDefinition.DbschemaTablesRowset.TableCatalog, RowsetDefinition.DbschemaTablesRowset.TableSchema, RowsetDefinition.DbschemaTablesRowset.TableName, RowsetDefinition.DbschemaTablesRowset.TableType, RowsetDefinition.DbschemaTablesRowset.TableGuid, RowsetDefinition.DbschemaTablesRowset.Description, RowsetDefinition.DbschemaTablesRowset.TablePropId, RowsetDefinition.DbschemaTablesRowset.DateCreated, RowsetDefinition.DbschemaTablesRowset.DateModified}, new RowsetDefinition.Column[]{RowsetDefinition.DbschemaTablesRowset.TableType, RowsetDefinition.DbschemaTablesRowset.TableCatalog, RowsetDefinition.DbschemaTablesRowset.TableSchema, RowsetDefinition.DbschemaTablesRowset.TableName}) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.DbschemaTablesRowset(request, handler);
        }
    },
    DBSCHEMA_SOURCE_TABLES(23, "8c3f5858-2742-4976-9d65-eb4d493c693e", (String)null, new RowsetDefinition.Column[]{RowsetDefinition.DbschemaSourceTablesRowset.TableCatalog, RowsetDefinition.DbschemaSourceTablesRowset.TableSchema, RowsetDefinition.DbschemaSourceTablesRowset.TableName, RowsetDefinition.DbschemaSourceTablesRowset.TableType}, new RowsetDefinition.Column[]{RowsetDefinition.DbschemaSourceTablesRowset.TableCatalog, RowsetDefinition.DbschemaSourceTablesRowset.TableSchema, RowsetDefinition.DbschemaSourceTablesRowset.TableName, RowsetDefinition.DbschemaSourceTablesRowset.TableType}) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.DbschemaSourceTablesRowset(request, handler);
        }
    },
    DBSCHEMA_TABLES_INFO(10, "c8b522e0-5cf3-11ce-ade5-00aa0044773d", (String)null, new RowsetDefinition.Column[]{RowsetDefinition.DbschemaTablesInfoRowset.TableCatalog, RowsetDefinition.DbschemaTablesInfoRowset.TableSchema, RowsetDefinition.DbschemaTablesInfoRowset.TableName, RowsetDefinition.DbschemaTablesInfoRowset.TableType, RowsetDefinition.DbschemaTablesInfoRowset.TableGuid, RowsetDefinition.DbschemaTablesInfoRowset.Bookmarks, RowsetDefinition.DbschemaTablesInfoRowset.BookmarkType, RowsetDefinition.DbschemaTablesInfoRowset.BookmarkDataType, RowsetDefinition.DbschemaTablesInfoRowset.BookmarkMaximumLength, RowsetDefinition.DbschemaTablesInfoRowset.BookmarkInformation, RowsetDefinition.DbschemaTablesInfoRowset.TableVersion, RowsetDefinition.DbschemaTablesInfoRowset.Cardinality, RowsetDefinition.DbschemaTablesInfoRowset.Description, RowsetDefinition.DbschemaTablesInfoRowset.TablePropId}, (RowsetDefinition.Column[])null) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.DbschemaTablesInfoRowset(request, handler);
        }
    },
    MDSCHEMA_ACTIONS(11, "A07CCD08-8148-11D0-87BB-00C04FC33942", (String)null, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaActionsRowset.CatalogName, RowsetDefinition.MdschemaActionsRowset.SchemaName, RowsetDefinition.MdschemaActionsRowset.CubeName, RowsetDefinition.MdschemaActionsRowset.ActionName, RowsetDefinition.MdschemaActionsRowset.Coordinate, RowsetDefinition.MdschemaActionsRowset.CoordinateType}, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaActionsRowset.CatalogName, RowsetDefinition.MdschemaActionsRowset.SchemaName, RowsetDefinition.MdschemaActionsRowset.CubeName, RowsetDefinition.MdschemaActionsRowset.ActionName}) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.MdschemaActionsRowset(request, handler);
        }
    },
    MDSCHEMA_CUBES(12, "C8B522D8-5CF3-11CE-ADE5-00AA0044773D", (String)null, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaCubesRowset.CatalogName, RowsetDefinition.MdschemaCubesRowset.SchemaName, RowsetDefinition.MdschemaCubesRowset.CubeName, RowsetDefinition.MdschemaCubesRowset.CubeType, RowsetDefinition.MdschemaCubesRowset.CubeGuid, RowsetDefinition.MdschemaCubesRowset.CreatedOn, RowsetDefinition.MdschemaCubesRowset.LastSchemaUpdate, RowsetDefinition.MdschemaCubesRowset.SchemaUpdatedBy, RowsetDefinition.MdschemaCubesRowset.LastDataUpdate, RowsetDefinition.MdschemaCubesRowset.DataUpdatedBy, RowsetDefinition.MdschemaCubesRowset.Description, RowsetDefinition.MdschemaCubesRowset.IsDrillthroughEnabled, RowsetDefinition.MdschemaCubesRowset.IsLinkable, RowsetDefinition.MdschemaCubesRowset.IsWriteEnabled, RowsetDefinition.MdschemaCubesRowset.IsSqlEnabled, RowsetDefinition.MdschemaCubesRowset.CubeCaption, RowsetDefinition.MdschemaCubesRowset.BaseCubeName, RowsetDefinition.MdschemaCubesRowset.Dimensions, RowsetDefinition.MdschemaCubesRowset.Sets, RowsetDefinition.MdschemaCubesRowset.Measures, RowsetDefinition.MdschemaCubesRowset.CubeSource}, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaCubesRowset.CatalogName, RowsetDefinition.MdschemaCubesRowset.SchemaName, RowsetDefinition.MdschemaCubesRowset.CubeName, RowsetDefinition.MdschemaCubesRowset.CubeType, RowsetDefinition.MdschemaCubesRowset.BaseCubeName}) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.MdschemaCubesRowset(request, handler);
        }
    },
    MDSCHEMA_DIMENSIONS(13, "C8B522D9-5CF3-11CE-ADE5-00AA0044773D", (String)null, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaDimensionsRowset.CatalogName, RowsetDefinition.MdschemaDimensionsRowset.SchemaName, RowsetDefinition.MdschemaDimensionsRowset.CubeName, RowsetDefinition.MdschemaDimensionsRowset.DimensionName, RowsetDefinition.MdschemaDimensionsRowset.DimensionUniqueName, RowsetDefinition.MdschemaDimensionsRowset.DimensionGuid, RowsetDefinition.MdschemaDimensionsRowset.DimensionCaption, RowsetDefinition.MdschemaDimensionsRowset.DimensionOrdinal, RowsetDefinition.MdschemaDimensionsRowset.DimensionType, RowsetDefinition.MdschemaDimensionsRowset.DimensionCardinality, RowsetDefinition.MdschemaDimensionsRowset.DefaultHierarchy, RowsetDefinition.MdschemaDimensionsRowset.Description, RowsetDefinition.MdschemaDimensionsRowset.IsVirtual, RowsetDefinition.MdschemaDimensionsRowset.IsReadWrite, RowsetDefinition.MdschemaDimensionsRowset.DimensionUniqueSettings, RowsetDefinition.MdschemaDimensionsRowset.DimensionMasterUniqueName, RowsetDefinition.MdschemaDimensionsRowset.DimensionIsVisible, RowsetDefinition.MdschemaDimensionsRowset.Hierarchies}, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaDimensionsRowset.CatalogName, RowsetDefinition.MdschemaDimensionsRowset.SchemaName, RowsetDefinition.MdschemaDimensionsRowset.CubeName, RowsetDefinition.MdschemaDimensionsRowset.DimensionName}) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.MdschemaDimensionsRowset(request, handler);
        }
    },
    MDSCHEMA_FUNCTIONS(14, "A07CCD07-8148-11D0-87BB-00C04FC33942", (String)null, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaFunctionsRowset.FunctionName, RowsetDefinition.MdschemaFunctionsRowset.Description, RowsetDefinition.MdschemaFunctionsRowset.ParameterList, RowsetDefinition.MdschemaFunctionsRowset.ReturnType, RowsetDefinition.MdschemaFunctionsRowset.Origin, RowsetDefinition.MdschemaFunctionsRowset.InterfaceName, RowsetDefinition.MdschemaFunctionsRowset.LibraryName, RowsetDefinition.MdschemaFunctionsRowset.Caption}, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaFunctionsRowset.LibraryName, RowsetDefinition.MdschemaFunctionsRowset.InterfaceName, RowsetDefinition.MdschemaFunctionsRowset.FunctionName, RowsetDefinition.MdschemaFunctionsRowset.Origin}) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.MdschemaFunctionsRowset(request, handler);
        }
    },
    MDSCHEMA_HIERARCHIES(15, "C8B522DA-5CF3-11CE-ADE5-00AA0044773D", (String)null, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaHierarchiesRowset.CatalogName, RowsetDefinition.MdschemaHierarchiesRowset.SchemaName, RowsetDefinition.MdschemaHierarchiesRowset.CubeName, RowsetDefinition.MdschemaHierarchiesRowset.DimensionUniqueName, RowsetDefinition.MdschemaHierarchiesRowset.HierarchyName, RowsetDefinition.MdschemaHierarchiesRowset.HierarchyUniqueName, RowsetDefinition.MdschemaHierarchiesRowset.HierarchyGuid, RowsetDefinition.MdschemaHierarchiesRowset.HierarchyCaption, RowsetDefinition.MdschemaHierarchiesRowset.DimensionType, RowsetDefinition.MdschemaHierarchiesRowset.HierarchyCardinality, RowsetDefinition.MdschemaHierarchiesRowset.DefaultMember, RowsetDefinition.MdschemaHierarchiesRowset.AllMember, RowsetDefinition.MdschemaHierarchiesRowset.Description, RowsetDefinition.MdschemaHierarchiesRowset.Structure, RowsetDefinition.MdschemaHierarchiesRowset.IsVirtual, RowsetDefinition.MdschemaHierarchiesRowset.IsReadWrite, RowsetDefinition.MdschemaHierarchiesRowset.DimensionUniqueSettings, RowsetDefinition.MdschemaHierarchiesRowset.DimensionIsVisible, RowsetDefinition.MdschemaHierarchiesRowset.HierarchyOrdinal, RowsetDefinition.MdschemaHierarchiesRowset.DimensionIsShared, RowsetDefinition.MdschemaHierarchiesRowset.HierarchyIsVisibile, RowsetDefinition.MdschemaHierarchiesRowset.HierarchyOrigin, RowsetDefinition.MdschemaHierarchiesRowset.DisplayFolder, RowsetDefinition.MdschemaHierarchiesRowset.CubeSource, RowsetDefinition.MdschemaHierarchiesRowset.HierarchyVisibility, RowsetDefinition.MdschemaHierarchiesRowset.ParentChild, RowsetDefinition.MdschemaHierarchiesRowset.Levels}, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaHierarchiesRowset.CatalogName, RowsetDefinition.MdschemaHierarchiesRowset.SchemaName, RowsetDefinition.MdschemaHierarchiesRowset.CubeName, RowsetDefinition.MdschemaHierarchiesRowset.DimensionUniqueName, RowsetDefinition.MdschemaHierarchiesRowset.HierarchyName, RowsetDefinition.MdschemaHierarchiesRowset.HierarchyUniqueName, RowsetDefinition.MdschemaHierarchiesRowset.HierarchyOrigin, RowsetDefinition.MdschemaHierarchiesRowset.CubeSource, RowsetDefinition.MdschemaHierarchiesRowset.HierarchyVisibility}) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.MdschemaHierarchiesRowset(request, handler);
        }
    },
    MDSCHEMA_LEVELS(16, "C8B522DB-5CF3-11CE-ADE5-00AA0044773D", (String)null, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaLevelsRowset.CatalogName, RowsetDefinition.MdschemaLevelsRowset.SchemaName, RowsetDefinition.MdschemaLevelsRowset.CubeName, RowsetDefinition.MdschemaLevelsRowset.DimensionUniqueName, RowsetDefinition.MdschemaLevelsRowset.HierarchyUniqueName, RowsetDefinition.MdschemaLevelsRowset.LevelName, RowsetDefinition.MdschemaLevelsRowset.LevelUniqueName, RowsetDefinition.MdschemaLevelsRowset.LevelGuid, RowsetDefinition.MdschemaLevelsRowset.LevelCaption, RowsetDefinition.MdschemaLevelsRowset.LevelNumber, RowsetDefinition.MdschemaLevelsRowset.LevelCardinality, RowsetDefinition.MdschemaLevelsRowset.LevelType, RowsetDefinition.MdschemaLevelsRowset.CustomRollupSettings, RowsetDefinition.MdschemaLevelsRowset.LevelUniqueSettings, RowsetDefinition.MdschemaLevelsRowset.LevelIsVisible, RowsetDefinition.MdschemaLevelsRowset.Description, RowsetDefinition.MdschemaLevelsRowset.LevelOrigin, RowsetDefinition.MdschemaLevelsRowset.CubeSource, RowsetDefinition.MdschemaLevelsRowset.LevelVisibility}, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaLevelsRowset.CatalogName, RowsetDefinition.MdschemaLevelsRowset.SchemaName, RowsetDefinition.MdschemaLevelsRowset.CubeName, RowsetDefinition.MdschemaLevelsRowset.DimensionUniqueName, RowsetDefinition.MdschemaLevelsRowset.HierarchyUniqueName, RowsetDefinition.MdschemaLevelsRowset.LevelNumber}) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.MdschemaLevelsRowset(request, handler);
        }
    },
    MDSCHEMA_MEASUREGROUP_DIMENSIONS(13, "a07ccd33-8148-11d0-87bb-00c04fc33942", (String)null, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaMeasuregroupDimensionsRowset.CatalogName, RowsetDefinition.MdschemaMeasuregroupDimensionsRowset.SchemaName, RowsetDefinition.MdschemaMeasuregroupDimensionsRowset.CubeName, RowsetDefinition.MdschemaMeasuregroupDimensionsRowset.MeasuregroupName, RowsetDefinition.MdschemaMeasuregroupDimensionsRowset.MeasuregroupCardinality, RowsetDefinition.MdschemaMeasuregroupDimensionsRowset.DimensionUniqueName, RowsetDefinition.MdschemaMeasuregroupDimensionsRowset.DimensionCardinality, RowsetDefinition.MdschemaMeasuregroupDimensionsRowset.DimensionIsVisible, RowsetDefinition.MdschemaMeasuregroupDimensionsRowset.DimensionIsFactDimension, RowsetDefinition.MdschemaMeasuregroupDimensionsRowset.DimensionPath, RowsetDefinition.MdschemaMeasuregroupDimensionsRowset.DimensionGranularity}, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaMeasuregroupDimensionsRowset.CatalogName, RowsetDefinition.MdschemaMeasuregroupDimensionsRowset.SchemaName, RowsetDefinition.MdschemaMeasuregroupDimensionsRowset.CubeName, RowsetDefinition.MdschemaMeasuregroupDimensionsRowset.MeasuregroupName, RowsetDefinition.MdschemaMeasuregroupDimensionsRowset.DimensionUniqueName}) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.MdschemaMeasuregroupDimensionsRowset(request, handler);
        }
    },
    MDSCHEMA_MEASURES(17, "C8B522DC-5CF3-11CE-ADE5-00AA0044773D", (String)null, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaMeasuresRowset.CatalogName, RowsetDefinition.MdschemaMeasuresRowset.SchemaName, RowsetDefinition.MdschemaMeasuresRowset.CubeName, RowsetDefinition.MdschemaMeasuresRowset.MeasureName, RowsetDefinition.MdschemaMeasuresRowset.MeasureUniqueName, RowsetDefinition.MdschemaMeasuresRowset.MeasureCaption, RowsetDefinition.MdschemaMeasuresRowset.MeasureGuid, RowsetDefinition.MdschemaMeasuresRowset.MeasureAggregator, RowsetDefinition.MdschemaMeasuresRowset.DataType, RowsetDefinition.MdschemaMeasuresRowset.MeasureIsVisible, RowsetDefinition.MdschemaMeasuresRowset.LevelsList, RowsetDefinition.MdschemaMeasuresRowset.Description, RowsetDefinition.MdschemaMeasuresRowset.MeasuregroupName, RowsetDefinition.MdschemaMeasuresRowset.DisplayFolder, RowsetDefinition.MdschemaMeasuresRowset.FormatString, RowsetDefinition.MdschemaMeasuresRowset.CubeSource, RowsetDefinition.MdschemaMeasuresRowset.MeasureVisiblity}, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaMeasuresRowset.CatalogName, RowsetDefinition.MdschemaMeasuresRowset.SchemaName, RowsetDefinition.MdschemaMeasuresRowset.CubeName, RowsetDefinition.MdschemaMeasuresRowset.MeasureName, RowsetDefinition.MdschemaMeasuresRowset.MeasureUniqueName, RowsetDefinition.MdschemaMeasuresRowset.MeasuregroupName, RowsetDefinition.MdschemaMeasuresRowset.CubeSource, RowsetDefinition.MdschemaMeasuresRowset.MeasureVisiblity}) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.MdschemaMeasuresRowset(request, handler);
        }
    },
    MDSCHEMA_MEMBERS(18, "C8B522DE-5CF3-11CE-ADE5-00AA0044773D", (String)null, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaMembersRowset.CatalogName, RowsetDefinition.MdschemaMembersRowset.SchemaName, RowsetDefinition.MdschemaMembersRowset.CubeName, RowsetDefinition.MdschemaMembersRowset.DimensionUniqueName, RowsetDefinition.MdschemaMembersRowset.HierarchyUniqueName, RowsetDefinition.MdschemaMembersRowset.LevelUniqueName, RowsetDefinition.MdschemaMembersRowset.LevelNumber, RowsetDefinition.MdschemaMembersRowset.MemberOrdinal, RowsetDefinition.MdschemaMembersRowset.MemberName, RowsetDefinition.MdschemaMembersRowset.MemberUniqueName, RowsetDefinition.MdschemaMembersRowset.MemberType, RowsetDefinition.MdschemaMembersRowset.MemberGuid, RowsetDefinition.MdschemaMembersRowset.MemberCaption, RowsetDefinition.MdschemaMembersRowset.ChildrenCardinality, RowsetDefinition.MdschemaMembersRowset.ParentLevel, RowsetDefinition.MdschemaMembersRowset.ParentUniqueName, RowsetDefinition.MdschemaMembersRowset.ParentCount, RowsetDefinition.MdschemaMembersRowset.TreeOp_, RowsetDefinition.MdschemaMembersRowset.Depth}, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaMembersRowset.CatalogName, RowsetDefinition.MdschemaMembersRowset.SchemaName, RowsetDefinition.MdschemaMembersRowset.CubeName, RowsetDefinition.MdschemaMembersRowset.DimensionUniqueName, RowsetDefinition.MdschemaMembersRowset.HierarchyUniqueName, RowsetDefinition.MdschemaMembersRowset.LevelUniqueName, RowsetDefinition.MdschemaMembersRowset.LevelNumber, RowsetDefinition.MdschemaMembersRowset.MemberOrdinal}) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.MdschemaMembersRowset(request, handler);
        }
    },
    MDSCHEMA_PROPERTIES(19, "C8B522DD-5CF3-11CE-ADE5-00AA0044773D", (String)null, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaPropertiesRowset.CatalogName, RowsetDefinition.MdschemaPropertiesRowset.SchemaName, RowsetDefinition.MdschemaPropertiesRowset.CubeName, RowsetDefinition.MdschemaPropertiesRowset.DimensionUniqueName, RowsetDefinition.MdschemaPropertiesRowset.HierarchyUniqueName, RowsetDefinition.MdschemaPropertiesRowset.LevelUniqueName, RowsetDefinition.MdschemaPropertiesRowset.MemberUniqueName, RowsetDefinition.MdschemaPropertiesRowset.PropertyType, RowsetDefinition.MdschemaPropertiesRowset.PropertyName, RowsetDefinition.MdschemaPropertiesRowset.PropertyCaption, RowsetDefinition.MdschemaPropertiesRowset.DataType, RowsetDefinition.MdschemaPropertiesRowset.PropertyContentType, RowsetDefinition.MdschemaPropertiesRowset.Description, RowsetDefinition.MdschemaPropertiesRowset.PropertyOrigin, RowsetDefinition.MdschemaPropertiesRowset.CubeSource, RowsetDefinition.MdschemaPropertiesRowset.PropertyVisibility}, (RowsetDefinition.Column[])null) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.MdschemaPropertiesRowset(request, handler);
        }
    },
    MDSCHEMA_SETS(20, "A07CCD0B-8148-11D0-87BB-00C04FC33942", (String)null, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaSetsRowset.CatalogName, RowsetDefinition.MdschemaSetsRowset.SchemaName, RowsetDefinition.MdschemaSetsRowset.CubeName, RowsetDefinition.MdschemaSetsRowset.SetName, RowsetDefinition.MdschemaSetsRowset.Scope, RowsetDefinition.MdschemaSetsRowset.Description, RowsetDefinition.MdschemaSetsRowset.Expression, RowsetDefinition.MdschemaSetsRowset.Dimensions, RowsetDefinition.MdschemaSetsRowset.SetCaption, RowsetDefinition.MdschemaSetsRowset.DisplayFolder}, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaSetsRowset.CatalogName, RowsetDefinition.MdschemaSetsRowset.SchemaName, RowsetDefinition.MdschemaSetsRowset.CubeName}) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.MdschemaSetsRowset(request, handler);
        }
    },
    MDSCHEMA_KPIS(21, "2AE44109-ED3D-4842-B16F-B694D1CB0E3F", (String)null, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaKpisRowset.CatalogName, RowsetDefinition.MdschemaKpisRowset.SchemaName, RowsetDefinition.MdschemaKpisRowset.CubeName, RowsetDefinition.MdschemaKpisRowset.MeasuregroupName, RowsetDefinition.MdschemaKpisRowset.KpiName, RowsetDefinition.MdschemaKpisRowset.KpiCaption, RowsetDefinition.MdschemaKpisRowset.KpiDescription, RowsetDefinition.MdschemaKpisRowset.KpiDisplayFolder, RowsetDefinition.MdschemaKpisRowset.KpiValue, RowsetDefinition.MdschemaKpisRowset.KpiGoal, RowsetDefinition.MdschemaKpisRowset.KpiStatus, RowsetDefinition.MdschemaKpisRowset.KpiTrend, RowsetDefinition.MdschemaKpisRowset.KpiStatusGraphic, RowsetDefinition.MdschemaKpisRowset.KpiTrendGraphic, RowsetDefinition.MdschemaKpisRowset.KpiWeight, RowsetDefinition.MdschemaKpisRowset.KpiCurrentTimeMember, RowsetDefinition.MdschemaKpisRowset.KpiParentKpiName, RowsetDefinition.MdschemaKpisRowset.Scope}, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaKpisRowset.CatalogName, RowsetDefinition.MdschemaKpisRowset.SchemaName, RowsetDefinition.MdschemaKpisRowset.CubeName, RowsetDefinition.MdschemaKpisRowset.MeasuregroupName, RowsetDefinition.MdschemaKpisRowset.KpiName}) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.MdschemaKpisRowset(request, handler);
        }
    },
    MDSCHEMA_MEASUREGROUPS(22, "E1625EBF-FA96-42FD-BEA6-DB90ADAFD96B", (String)null, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaMeasuregroupsRowset.CatalogName, RowsetDefinition.MdschemaMeasuregroupsRowset.SchemaName, RowsetDefinition.MdschemaMeasuregroupsRowset.CubeName, RowsetDefinition.MdschemaMeasuregroupsRowset.MeasuregroupName, RowsetDefinition.MdschemaMeasuregroupsRowset.Description, RowsetDefinition.MdschemaMeasuregroupsRowset.IsWriteEnabled, RowsetDefinition.MdschemaMeasuregroupsRowset.MeasuregroupCaption}, new RowsetDefinition.Column[]{RowsetDefinition.MdschemaKpisRowset.CatalogName, RowsetDefinition.MdschemaKpisRowset.SchemaName, RowsetDefinition.MdschemaKpisRowset.CubeName, RowsetDefinition.MdschemaKpisRowset.MeasuregroupName}) {
        public Rowset getRowset(XmlaRequest request, XmlaHandler handler) {
            return new RowsetDefinition.MdschemaMeasuregroupsRowset(request, handler);
        }
    };

    final transient RowsetDefinition.Column[] columnDefinitions;
    final transient RowsetDefinition.Column[] sortColumnDefinitions;
    private static final String dateModified = "2005-01-25T17:35:32";
    private final String description;
    private final String schemaGuid;
    static final String UUID_PATTERN = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
    public static final Functor1<String, Catalog> CATALOG_NAME_GETTER = new Functor1<String, Catalog>() {
        public String apply(Catalog catalog) {
            return catalog.getName();
        }
    };
    public static final Functor1<String, Schema> SCHEMA_NAME_GETTER = new Functor1<String, Schema>() {
        public String apply(Schema schema) {
            return schema.getName();
        }
    };
    public static final Functor1<String, MetadataElement> ELEMENT_NAME_GETTER = new Functor1<String, MetadataElement>() {
        public String apply(MetadataElement element) {
            return element.getName();
        }
    };
    public static final Functor1<String, MetadataElement> ELEMENT_UNAME_GETTER = new Functor1<String, MetadataElement>() {
        public String apply(MetadataElement element) {
            return element.getUniqueName();
        }
    };
    public static final Functor1<org.olap4j.metadata.Member.Type, Member> MEMBER_TYPE_GETTER = new Functor1<org.olap4j.metadata.Member.Type, Member>() {
        public org.olap4j.metadata.Member.Type apply(Member member) {
            return member.getMemberType();
        }
    };
    public static final Functor1<String, PropertyDefinition> PROPDEF_NAME_GETTER = new Functor1<String, PropertyDefinition>() {
        public String apply(PropertyDefinition property) {
            return property.name();
        }
    };

    private RowsetDefinition(int ordinal, String schemaGuid, String description, RowsetDefinition.Column[] columnDefinitions, RowsetDefinition.Column[] sortColumnDefinitions) {
        Util.discard(ordinal);
        this.schemaGuid = schemaGuid;
        this.description = description;
        this.columnDefinitions = columnDefinitions;
        this.sortColumnDefinitions = sortColumnDefinitions;
    }

    public abstract Rowset getRowset(XmlaRequest var1, XmlaHandler var2);

    public RowsetDefinition.Column lookupColumn(String name) {
        RowsetDefinition.Column[] var2 = this.columnDefinitions;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            RowsetDefinition.Column columnDefinition = var2[var4];
            if (columnDefinition.name.equals(name)) {
                return columnDefinition;
            }
        }

        return null;
    }

    Comparator<Row> getComparator() {
        return this.sortColumnDefinitions == null ? null : new Comparator<Row>() {
            public int compare(Row row1, Row row2) {
                RowsetDefinition.Column[] var3 = RowsetDefinition.this.sortColumnDefinitions;
                int var4 = var3.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    RowsetDefinition.Column sortColumn = var3[var5];
                    Comparable val1 = (Comparable)row1.get(sortColumn.name);
                    Comparable val2 = (Comparable)row2.get(sortColumn.name);
                    if (val1 != null || val2 != null) {
                        if (val1 == null) {
                            return -1;
                        }

                        if (val2 == null) {
                            return 1;
                        }

                        int v;
                        if (val1 instanceof String && val2 instanceof String) {
                            v = ((String)val1).compareToIgnoreCase((String)val2);
                            if (v != 0) {
                                return v;
                            }
                        } else {
                            v = val1.compareTo(val2);
                            if (v != 0) {
                                return v;
                            }
                        }
                    }
                }

                return 0;
            }
        };
    }

    void writeRowsetXmlSchema(SaxWriter writer) {
        this.writeRowsetXmlSchemaTop(writer);
        this.writeRowsetXmlSchemaRowDef(writer);
        this.writeRowsetXmlSchemaBottom(writer);
    }

    protected void writeRowsetXmlSchemaTop(SaxWriter writer) {
        writer.startElement("xsd:schema", new Object[]{"xmlns:xsd", "http://www.w3.org/2001/XMLSchema", "xmlns", "urn:schemas-microsoft-com:xml-analysis:rowset", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance", "xmlns:sql", "urn:schemas-microsoft-com:xml-sql", "targetNamespace", "urn:schemas-microsoft-com:xml-analysis:rowset", "elementFormDefault", "qualified"});
        writer.startElement("xsd:element", new Object[]{"name", "root"});
        writer.startElement("xsd:complexType");
        writer.startElement("xsd:sequence");
        writer.element("xsd:element", new Object[]{"name", "row", "type", "row", "minOccurs", 0, "maxOccurs", "unbounded"});
        writer.endElement();
        writer.endElement();
        writer.endElement();
        writer.startElement("xsd:simpleType", new Object[]{"name", "uuid"});
        writer.startElement("xsd:restriction", new Object[]{"base", "xsd:string"});
        writer.element("xsd:pattern", new Object[]{"value", "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"});
        writer.endElement();
        writer.endElement();
    }

    protected void writeRowsetXmlSchemaRowDef(SaxWriter writer) {
        writer.startElement("xsd:complexType", new Object[]{"name", "row"});
        writer.startElement("xsd:sequence");
        RowsetDefinition.Column[] var2 = this.columnDefinitions;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            RowsetDefinition.Column column = var2[var4];
            String name = ElementNameEncoder.INSTANCE.encode(column.name);
            String xsdType = column.type.columnType;
            Object[] attrs;
            if (column.nullable) {
                if (column.unbounded) {
                    attrs = new Object[]{"sql:field", column.name, "name", name, "type", xsdType, "minOccurs", 0, "maxOccurs", "unbounded"};
                } else {
                    attrs = new Object[]{"sql:field", column.name, "name", name, "type", xsdType, "minOccurs", 0};
                }
            } else if (column.unbounded) {
                attrs = new Object[]{"sql:field", column.name, "name", name, "type", xsdType, "maxOccurs", "unbounded"};
            } else {
                attrs = new Object[]{"sql:field", column.name, "name", name, "type", xsdType};
            }

            writer.element("xsd:element", attrs);
        }

        writer.endElement();
        writer.endElement();
    }

    protected void writeRowsetXmlSchemaBottom(SaxWriter writer) {
        writer.endElement();
    }

    private static DBType getDBTypeFromProperty(Property prop) {
        switch(prop.getDatatype()) {
            case STRING:
                return DBType.WSTR;
            case INTEGER:
            case UNSIGNED_INTEGER:
            case DOUBLE:
                return DBType.R8;
            case BOOLEAN:
                return DBType.BOOL;
            default:
                return DBType.WSTR;
        }
    }

    public String getDescription() {
        return this.description;
    }

    static int getDimensionType(Dimension dim) throws OlapException {
        switch(dim.getDimensionType()) {
            case MEASURE:
                return 2;
            case TIME:
                return 1;
            default:
                return 3;
        }
    }

    static void serialize(StringBuilder buf, Collection<String> strings) {
        int k = 0;

        String name;
        for(Iterator var3 = Util.sort(strings).iterator(); var3.hasNext(); buf.append(name)) {
            name = (String)var3.next();
            if (k++ > 0) {
                buf.append(',');
            }
        }

    }

    private static Level lookupLevel(Cube cube, String levelUniqueName) {
        Iterator var2 = cube.getDimensions().iterator();

        while(var2.hasNext()) {
            Dimension dimension = (Dimension)var2.next();
            Iterator var4 = dimension.getHierarchies().iterator();

            while(var4.hasNext()) {
                Hierarchy hierarchy = (Hierarchy)var4.next();
                Iterator var6 = hierarchy.getLevels().iterator();

                while(var6.hasNext()) {
                    Level level = (Level)var6.next();
                    if (level.getUniqueName().equals(levelUniqueName)) {
                        return level;
                    }
                }
            }
        }

        return null;
    }

    static Iterable<Cube> sortedCubes(Schema schema) throws OlapException {
        return Util.sort(schema.getCubes(), new Comparator<Cube>() {
            public int compare(Cube o1, Cube o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    static Iterable<Cube> filteredCubes(Schema schema, Functor1<Boolean, Cube> cubeNameCond) throws OlapException {
        Iterable<Cube> iterable = Util.filter(sortedCubes(schema), new Functor1[]{cubeNameCond});
        return !(Boolean)cubeNameCond.apply(new RowsetDefinition.SharedDimensionHolderCube(schema)) ? iterable : Composite.of(new Iterable[]{Collections.singletonList(new RowsetDefinition.SharedDimensionHolderCube(schema)), iterable});
    }

    private static String getHierarchyName(Hierarchy hierarchy) {
        String hierarchyName = hierarchy.getName();
        if (MondrianProperties.instance().SsasCompatibleNaming.get() && !hierarchyName.equals(hierarchy.getDimension().getName())) {
            hierarchyName = hierarchy.getDimension().getName() + "." + hierarchyName;
        }

        return hierarchyName;
    }

    private static XmlaRequest wrapRequest(XmlaRequest request, Map<RowsetDefinition.Column, String> map) {
        final Map<String, Object> restrictionsMap = new HashMap(request.getRestrictions());
        Iterator var3 = map.entrySet().iterator();

        while(var3.hasNext()) {
            Entry<RowsetDefinition.Column, String> entry = (Entry)var3.next();
            restrictionsMap.put(((RowsetDefinition.Column)entry.getKey()).name, Collections.singletonList((String)entry.getValue()));
        }

        return new RowsetDefinition.DelegatingXmlaRequest(request) {
            public Map<String, Object> getRestrictions() {
                return restrictionsMap;
            }
        };
    }

    private static Iterable<Catalog> catIter(final OlapConnection connection, final Functor1<Boolean, Catalog>... conds) {
        return new Iterable<Catalog>() {
            public Iterator<Catalog> iterator() {
                try {
                    return new Iterator<Catalog>() {
                        final Iterator<Catalog> catalogIter = Util.filter(connection.getOlapCatalogs(), conds).iterator();

                        public boolean hasNext() {
                            return this.catalogIter.hasNext();
                        }

                        public Catalog next() {
                            Catalog catalog = (Catalog)this.catalogIter.next();

                            try {
                                connection.setCatalog(catalog.getName());
                                return catalog;
                            } catch (SQLException var3) {
                                throw new RuntimeException(var3);
                            }
                        }

                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                } catch (OlapException var2) {
                    throw new RuntimeException("Failed to obtain a list of catalogs form the connection object.", var2);
                }
            }
        };
    }

    // $FF: synthetic method
    RowsetDefinition(int x2, String x3, String x4, RowsetDefinition.Column[] x5, RowsetDefinition.Column[] x6, Object x7) {
        this(x2, x3, x4, x5, x6);
    }

    private static class SharedDimensionHolderCube implements Cube {
        private final Schema schema;

        public SharedDimensionHolderCube(Schema schema) {
            this.schema = schema;
        }

        public Schema getSchema() {
            return this.schema;
        }

        public NamedList<Dimension> getDimensions() {
            try {
                return this.schema.getSharedDimensions();
            } catch (OlapException var2) {
                throw new RuntimeException(var2);
            }
        }

        public NamedList<Hierarchy> getHierarchies() {
            NamedList<Hierarchy> hierarchyList = new ArrayNamedListImpl<Hierarchy>() {
                public String getName(Object hierarchy) {
                    return ((Hierarchy)hierarchy).getName();
                }
            };
            Iterator var2 = this.getDimensions().iterator();

            while(var2.hasNext()) {
                Dimension dimension = (Dimension)var2.next();
                hierarchyList.addAll(dimension.getHierarchies());
            }

            return hierarchyList;
        }

        public List<Measure> getMeasures() {
            return Collections.emptyList();
        }

        public NamedList<NamedSet> getSets() {
            throw new UnsupportedOperationException();
        }

        public Collection<Locale> getSupportedLocales() {
            throw new UnsupportedOperationException();
        }

        public Member lookupMember(List<IdentifierSegment> identifierSegments) throws OlapException {
            throw new UnsupportedOperationException();
        }

        public List<Member> lookupMembers(Set<TreeOp> treeOps, List<IdentifierSegment> identifierSegments) throws OlapException {
            throw new UnsupportedOperationException();
        }

        public boolean isDrillThroughEnabled() {
            return false;
        }

        public String getName() {
            return "";
        }

        public String getUniqueName() {
            return "";
        }

        public String getCaption() {
            return "";
        }

        public String getDescription() {
            return "";
        }

        public boolean isVisible() {
            return false;
        }
    }

    private static class DelegatingXmlaRequest implements XmlaRequest {
        protected final XmlaRequest request;

        public DelegatingXmlaRequest(XmlaRequest request) {
            this.request = request;
        }

        public Method getMethod() {
            return this.request.getMethod();
        }

        public Map<String, String> getProperties() {
            return this.request.getProperties();
        }

        public Map<String, Object> getRestrictions() {
            return this.request.getRestrictions();
        }

        public String getStatement() {
            return this.request.getStatement();
        }

        public String getRoleName() {
            return this.request.getRoleName();
        }

        public String getRequestType() {
            return this.request.getRequestType();
        }

        public boolean isDrillThrough() {
            return this.request.isDrillThrough();
        }

        public String getUsername() {
            return this.request.getUsername();
        }

        public String getPassword() {
            return this.request.getPassword();
        }

        public String getSessionId() {
            return this.request.getSessionId();
        }

        public String getAuthenticatedUser() {
            return this.request.getAuthenticatedUser();
        }

        public String[] getAuthenticatedUserGroups() {
            return this.request.getAuthenticatedUserGroups();
        }
    }

    static class MdschemaPropertiesRowset extends Rowset {
        private final Functor1<Boolean, Catalog> catalogCond;
        private final Functor1<Boolean, Schema> schemaNameCond;
        private final Functor1<Boolean, Cube> cubeNameCond;
        private final Functor1<Boolean, Dimension> dimensionUnameCond;
        private final Functor1<Boolean, Hierarchy> hierarchyUnameCond;
        private final Functor1<Boolean, Property> propertyNameCond;
        private static final RowsetDefinition.Column CatalogName;
        private static final RowsetDefinition.Column SchemaName;
        private static final RowsetDefinition.Column CubeName;
        private static final RowsetDefinition.Column DimensionUniqueName;
        private static final RowsetDefinition.Column HierarchyUniqueName;
        private static final RowsetDefinition.Column LevelUniqueName;
        private static final RowsetDefinition.Column MemberUniqueName;
        private static final RowsetDefinition.Column PropertyType;
        private static final RowsetDefinition.Column PropertyName;
        private static final RowsetDefinition.Column PropertyCaption;
        private static final RowsetDefinition.Column DataType;
        private static final RowsetDefinition.Column PropertyContentType;
        private static final RowsetDefinition.Column Description;
        private static final RowsetDefinition.Column PropertyOrigin;
        private static final RowsetDefinition.Column CubeSource;
        private static final RowsetDefinition.Column PropertyVisibility;

        MdschemaPropertiesRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.MDSCHEMA_PROPERTIES, request, handler);
            this.catalogCond = this.makeCondition(RowsetDefinition.CATALOG_NAME_GETTER, CatalogName);
            this.schemaNameCond = this.makeCondition(RowsetDefinition.SCHEMA_NAME_GETTER, SchemaName);
            this.cubeNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, CubeName);
            this.dimensionUnameCond = this.makeCondition(RowsetDefinition.ELEMENT_UNAME_GETTER, DimensionUniqueName);
            this.hierarchyUnameCond = this.makeCondition(RowsetDefinition.ELEMENT_UNAME_GETTER, HierarchyUniqueName);
            this.propertyNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, PropertyName);
        }

        protected boolean needConnection() {
            return false;
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException, SQLException {
            List<String> list = (List)this.restrictions.get(PropertyType.name);
            Set typeFlags;
            if (list == null) {
                typeFlags = Olap4jUtil.enumSetOf(TypeFlag.MEMBER, new TypeFlag[0]);
            } else {
                typeFlags = TypeFlag.getDictionary().forMask(Integer.valueOf((String)list.get(0)));
            }

            Iterator var6 = typeFlags.iterator();

            while(var6.hasNext()) {
                TypeFlag typeFlag = (TypeFlag)var6.next();
                switch(typeFlag) {
                    case MEMBER:
                        this.populateMember(rows);
                        break;
                    case CELL:
                        this.populateCell(rows);
                    case SYSTEM:
                    case BLOB:
                }
            }

        }

        private void populateCell(List<Row> rows) {
            StandardCellProperty[] var2 = StandardCellProperty.values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                StandardCellProperty property = var2[var4];
                Row row = new Row();
                row.set(PropertyType.name, TypeFlag.getDictionary().toMask(property.getType()));
                row.set(PropertyName.name, property.name());
                row.set(PropertyCaption.name, property.getCaption());
                row.set(DataType.name, property.getDatatype().xmlaOrdinal());
                this.addRow(row, rows);
            }

        }

        private void populateMember(List<Row> rows) throws SQLException {
            OlapConnection connection = this.handler.getConnection(this.request, Collections.emptyMap());
            Iterator var3 = RowsetDefinition.catIter(connection, this.catNameCond(), this.catalogCond).iterator();

            while(var3.hasNext()) {
                Catalog catalog = (Catalog)var3.next();
                this.populateCatalog(catalog, rows);
            }

        }

        protected void populateCatalog(Catalog catalog, List<Row> rows) throws XmlaException, SQLException {
            Iterator var3 = Util.filter(catalog.getSchemas(), new Functor1[]{this.schemaNameCond}).iterator();

            while(var3.hasNext()) {
                Schema schema = (Schema)var3.next();
                Iterator var5 = RowsetDefinition.filteredCubes(schema, this.cubeNameCond).iterator();

                while(var5.hasNext()) {
                    Cube cube = (Cube)var5.next();
                    this.populateCube(catalog, cube, rows);
                }
            }

        }

        protected void populateCube(Catalog catalog, Cube cube, List<Row> rows) throws XmlaException, SQLException {
            if (!(cube instanceof RowsetDefinition.SharedDimensionHolderCube)) {
                if (this.isRestricted(LevelUniqueName)) {
                    String levelUniqueName = this.getRestrictionValueAsString(LevelUniqueName);
                    if (levelUniqueName == null) {
                        return;
                    }

                    Level level = RowsetDefinition.lookupLevel(cube, levelUniqueName);
                    if (level == null) {
                        return;
                    }

                    this.populateLevel(catalog, cube, level, rows);
                } else {
                    Iterator var7 = Util.filter(cube.getDimensions(), new Functor1[]{this.dimensionUnameCond}).iterator();

                    while(var7.hasNext()) {
                        Dimension dimension = (Dimension)var7.next();
                        this.populateDimension(catalog, cube, dimension, rows);
                    }
                }

            }
        }

        private void populateDimension(Catalog catalog, Cube cube, Dimension dimension, List<Row> rows) throws SQLException {
            Iterator var5 = Util.filter(dimension.getHierarchies(), new Functor1[]{this.hierarchyUnameCond}).iterator();

            while(var5.hasNext()) {
                Hierarchy hierarchy = (Hierarchy)var5.next();
                this.populateHierarchy(catalog, cube, hierarchy, rows);
            }

        }

        private void populateHierarchy(Catalog catalog, Cube cube, Hierarchy hierarchy, List<Row> rows) throws SQLException {
            Iterator var5 = hierarchy.getLevels().iterator();

            while(var5.hasNext()) {
                Level level = (Level)var5.next();
                this.populateLevel(catalog, cube, level, rows);
            }

        }

        private void populateLevel(Catalog catalog, Cube cube, Level level, List<Row> rows) throws SQLException {
            XmlaExtra extra = XmlaHandler.getExtra(catalog.getMetaData().getConnection());
            Iterator var6 = Util.filter(extra.getLevelProperties(level), new Functor1[]{this.propertyNameCond}).iterator();

            while(var6.hasNext()) {
                Property property = (Property)var6.next();
                if (!extra.isPropertyInternal(property)) {
                    this.outputProperty(property, catalog, cube, level, rows);
                }
            }

        }

        private void outputProperty(Property property, Catalog catalog, Cube cube, Level level, List<Row> rows) {
            Hierarchy hierarchy = level.getHierarchy();
            Dimension dimension = hierarchy.getDimension();
            String propertyName = property.getName();
            Row row = new Row();
            row.set(CatalogName.name, catalog.getName());
            row.set(SchemaName.name, cube.getSchema().getName());
            row.set(CubeName.name, cube.getName());
            row.set(DimensionUniqueName.name, dimension.getUniqueName());
            row.set(HierarchyUniqueName.name, hierarchy.getUniqueName());
            row.set(LevelUniqueName.name, level.getUniqueName());
            row.set(PropertyName.name, propertyName);
            row.set(PropertyType.name, TypeFlag.MEMBER.xmlaOrdinal());
            row.set(PropertyContentType.name, ContentType.REGULAR.xmlaOrdinal());
            row.set(PropertyCaption.name, property.getCaption());
            DBType dbType = RowsetDefinition.getDBTypeFromProperty(property);
            row.set(DataType.name, dbType.xmlaOrdinal());
            String desc = cube.getName() + " Cube - " + RowsetDefinition.getHierarchyName(hierarchy) + " Hierarchy - " + level.getName() + " Level - " + property.getName() + " Property";
            row.set(Description.name, desc);
            row.set(CubeSource.name, 2);
            row.set(PropertyVisibility.name, 1);
            this.addRow(row, rows);
        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            CatalogName = new RowsetDefinition.Column("CATALOG_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, 0, true, "The name of the database.");
            SchemaName = new RowsetDefinition.Column("SCHEMA_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, 1, true, "The name of the schema to which this property belongs.");
            CubeName = new RowsetDefinition.Column("CUBE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, 2, true, "The name of the cube.");
            DimensionUniqueName = new RowsetDefinition.Column("DIMENSION_UNIQUE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, 3, true, "The unique name of the dimension.");
            HierarchyUniqueName = new RowsetDefinition.Column("HIERARCHY_UNIQUE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, 4, true, "The unique name of the hierarchy.");
            LevelUniqueName = new RowsetDefinition.Column("LEVEL_UNIQUE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, 5, true, "The unique name of the level to which this property belongs.");
            MemberUniqueName = new RowsetDefinition.Column("MEMBER_UNIQUE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, 6, true, "The unique name of the member to which the property belongs.");
            PropertyType = new RowsetDefinition.Column("PROPERTY_TYPE", RowsetDefinition.Type.Short, (Enumeration)null, true, 8, false, "A bitmap that specifies the type of the property");
            PropertyName = new RowsetDefinition.Column("PROPERTY_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, 7, false, "Name of the property.");
            PropertyCaption = new RowsetDefinition.Column("PROPERTY_CAPTION", RowsetDefinition.Type.String, (Enumeration)null, false, 10, false, "A label or caption associated with the property, used primarily for display purposes.");
            DataType = new RowsetDefinition.Column("DATA_TYPE", RowsetDefinition.Type.UnsignedShort, (Enumeration)null, false, 11, false, "Data type of the property.");
            PropertyContentType = new RowsetDefinition.Column("PROPERTY_CONTENT_TYPE", RowsetDefinition.Type.Short, (Enumeration)null, true, 9, true, "The type of the property.");
            Description = new RowsetDefinition.Column("DESCRIPTION", RowsetDefinition.Type.String, (Enumeration)null, false, 12, true, "A human-readable description of the measure.");
            PropertyOrigin = new RowsetDefinition.Column("PROPERTY_ORIGIN", RowsetDefinition.Type.UnsignedShort, (Enumeration)null, true, 14, true, "A default restriction is in place on MD_USER_DEFINED OR MD_SYSTEM_ENABLED.");
            CubeSource = new RowsetDefinition.Column("CUBE_SOURCE", RowsetDefinition.Type.UnsignedShort, (Enumeration)null, true, 15, true, "A bitmap with one of the following valid values:\n1 CUBE\n2 DIMENSION\nDefault restriction is a value of 1.");
            PropertyVisibility = new RowsetDefinition.Column("PROPERTY_VISIBILITY", RowsetDefinition.Type.UnsignedShort, (Enumeration)null, true, 16, true, "A bitmap with one of the following valid values:\n1 Visible\n2 Not visible\nDefault restriction is a value of 1.");
        }
    }

    static class MdschemaMeasuregroupsRowset extends Rowset {
        private final Functor1<Boolean, Catalog> catalogNameCond;
        private final Functor1<Boolean, Schema> schemaNameCond;
        private final Functor1<Boolean, Cube> cubeNameCond;
        private static final RowsetDefinition.Column CatalogName;
        private static final RowsetDefinition.Column SchemaName;
        private static final RowsetDefinition.Column CubeName;
        private static final RowsetDefinition.Column MeasuregroupName;
        private static final RowsetDefinition.Column Description;
        private static final RowsetDefinition.Column IsWriteEnabled;
        private static final RowsetDefinition.Column MeasuregroupCaption;

        MdschemaMeasuregroupsRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.MDSCHEMA_MEASUREGROUPS, request, handler);
            this.catalogNameCond = this.makeCondition(RowsetDefinition.CATALOG_NAME_GETTER, CatalogName);
            this.schemaNameCond = this.makeCondition(RowsetDefinition.SCHEMA_NAME_GETTER, SchemaName);
            this.cubeNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, CubeName);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException, SQLException {
            Iterator var4 = RowsetDefinition.catIter(connection, this.catNameCond(), this.catalogNameCond).iterator();

            while(var4.hasNext()) {
                Catalog catalog = (Catalog)var4.next();
                this.populateCatalog(connection, catalog, rows);
            }

        }

        protected void populateCatalog(OlapConnection connection, Catalog catalog, List<Row> rows) throws XmlaException, SQLException {
            Iterator var4 = Util.filter(catalog.getSchemas(), new Functor1[]{this.schemaNameCond}).iterator();

            while(var4.hasNext()) {
                Schema schema = (Schema)var4.next();
                Iterator var6 = RowsetDefinition.filteredCubes(schema, this.cubeNameCond).iterator();

                while(var6.hasNext()) {
                    Cube cube = (Cube)var6.next();
                    if (!(cube instanceof RowsetDefinition.SharedDimensionHolderCube)) {
                        this.populateCube(connection, catalog, cube, rows);
                    }
                }
            }

        }

        protected void populateCube(OlapConnection connection, Catalog catalog, Cube cube, List<Row> rows) throws XmlaException, SQLException {
            this.populateMeasuregroup(connection, catalog, cube, rows);
        }

        protected void populateMeasuregroup(OlapConnection connection, Catalog catalog, Cube cube, List<Row> rows) throws XmlaException, SQLException {
            Row row = new Row();
            row.set(CatalogName.name, catalog.getName());
            row.set(SchemaName.name, cube.getSchema().getName());
            row.set(CubeName.name, cube.getName());
            row.set(MeasuregroupName.name, cube.getName());
            row.set(Description.name, "");
            row.set(IsWriteEnabled.name, false);
            row.set(MeasuregroupCaption.name, cube.getName());
            this.addRow(row, rows);
        }

        static {
            CatalogName = new RowsetDefinition.Column("CATALOG_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the catalog to which this measure group belongs. NULL if the provider does not support catalogs.");
            SchemaName = new RowsetDefinition.Column("SCHEMA_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "Not supported.");
            CubeName = new RowsetDefinition.Column("CUBE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the cube to which this measure group belongs.");
            MeasuregroupName = new RowsetDefinition.Column("MEASUREGROUP_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the measure group.");
            Description = new RowsetDefinition.Column("DESCRIPTION", RowsetDefinition.Type.String, (Enumeration)null, false, true, "A human-readable description of the measure group.");
            IsWriteEnabled = new RowsetDefinition.Column("IS_WRITE_ENABLED", RowsetDefinition.Type.Boolean, (Enumeration)null, false, true, "A Boolean that indicates whether the measure group is write-enabled.");
            MeasuregroupCaption = new RowsetDefinition.Column("MEASUREGROUP_CAPTION", RowsetDefinition.Type.String, (Enumeration)null, false, true, "The display caption for the measure group.");
        }
    }

    static class MdschemaKpisRowset extends Rowset {
        private final Functor1<Boolean, Catalog> catalogCond;
        private final Functor1<Boolean, Schema> schemaNameCond;
        private final Functor1<Boolean, Cube> cubeNameCond;
        private final Functor1<Boolean, NamedSet> kpiNameCond;
        private static final RowsetDefinition.Column CatalogName;
        private static final RowsetDefinition.Column SchemaName;
        private static final RowsetDefinition.Column CubeName;
        private static final RowsetDefinition.Column MeasuregroupName;
        private static final RowsetDefinition.Column KpiName;
        private static final RowsetDefinition.Column KpiCaption;
        private static final RowsetDefinition.Column KpiDescription;
        private static final RowsetDefinition.Column KpiDisplayFolder;
        private static final RowsetDefinition.Column KpiValue;
        private static final RowsetDefinition.Column KpiGoal;
        private static final RowsetDefinition.Column KpiStatus;
        private static final RowsetDefinition.Column KpiTrend;
        private static final RowsetDefinition.Column KpiStatusGraphic;
        private static final RowsetDefinition.Column KpiTrendGraphic;
        private static final RowsetDefinition.Column KpiWeight;
        private static final RowsetDefinition.Column KpiCurrentTimeMember;
        private static final RowsetDefinition.Column KpiParentKpiName;
        private static final RowsetDefinition.Column Scope;

        MdschemaKpisRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.MDSCHEMA_KPIS, request, handler);
            this.catalogCond = this.makeCondition(RowsetDefinition.CATALOG_NAME_GETTER, CatalogName);
            this.schemaNameCond = this.makeCondition(RowsetDefinition.SCHEMA_NAME_GETTER, SchemaName);
            this.cubeNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, CubeName);
            this.kpiNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, KpiName);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException, OlapException {
            Iterator var4 = RowsetDefinition.catIter(connection, this.catNameCond(), this.catalogCond).iterator();

            while(var4.hasNext()) {
                Catalog catalog = (Catalog)var4.next();
                this.processCatalog(connection, catalog, rows);
            }

        }

        private void processCatalog(OlapConnection connection, Catalog catalog, List<Row> rows) throws OlapException {
            Iterator var4 = Util.filter(catalog.getSchemas(), new Functor1[]{this.schemaNameCond}).iterator();

            while(var4.hasNext()) {
                Schema schema = (Schema)var4.next();
                Iterator var6 = Util.filter(RowsetDefinition.sortedCubes(schema), new Functor1[]{this.cubeNameCond}).iterator();

                while(var6.hasNext()) {
                    Cube cube = (Cube)var6.next();
                    this.populateKpis(cube, catalog, rows);
                }
            }

        }

        private void populateKpis(Cube cube, Catalog catalog, List<Row> rows) {
        }

        static {
            CatalogName = new RowsetDefinition.Column("CATALOG_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, (String)null);
            SchemaName = new RowsetDefinition.Column("SCHEMA_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, (String)null);
            CubeName = new RowsetDefinition.Column("CUBE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, (String)null);
            MeasuregroupName = new RowsetDefinition.Column("MEASUREGROUP_NAME", RowsetDefinition.Type.String, (Enumeration)null, false, false, (String)null);
            KpiName = new RowsetDefinition.Column("KPI_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, (String)null);
            KpiCaption = new RowsetDefinition.Column("KPI_CAPTION", RowsetDefinition.Type.String, (Enumeration)null, false, false, (String)null);
            KpiDescription = new RowsetDefinition.Column("KPI_DESCRIPTION", RowsetDefinition.Type.String, (Enumeration)null, false, false, (String)null);
            KpiDisplayFolder = new RowsetDefinition.Column("KPI_DISPLAY_FOLDER", RowsetDefinition.Type.String, (Enumeration)null, false, false, (String)null);
            KpiValue = new RowsetDefinition.Column("KPI_VALUE", RowsetDefinition.Type.String, (Enumeration)null, false, false, (String)null);
            KpiGoal = new RowsetDefinition.Column("KPI_GOAL", RowsetDefinition.Type.String, (Enumeration)null, false, false, (String)null);
            KpiStatus = new RowsetDefinition.Column("KPI_STATUS", RowsetDefinition.Type.String, (Enumeration)null, false, false, (String)null);
            KpiTrend = new RowsetDefinition.Column("KPI_TREND", RowsetDefinition.Type.String, (Enumeration)null, false, false, (String)null);
            KpiStatusGraphic = new RowsetDefinition.Column("KPI_STATUS_GRAPHIC", RowsetDefinition.Type.String, (Enumeration)null, false, false, (String)null);
            KpiTrendGraphic = new RowsetDefinition.Column("KPI_TREND_GRAPHIC", RowsetDefinition.Type.String, (Enumeration)null, false, false, (String)null);
            KpiWeight = new RowsetDefinition.Column("KPI_WEIGHT", RowsetDefinition.Type.String, (Enumeration)null, false, false, (String)null);
            KpiCurrentTimeMember = new RowsetDefinition.Column("KPI_CURRENT_TIME_MEMBER", RowsetDefinition.Type.String, (Enumeration)null, false, false, (String)null);
            KpiParentKpiName = new RowsetDefinition.Column("KPI_PARENT_KPI_NAME", RowsetDefinition.Type.String, (Enumeration)null, false, false, (String)null);
            Scope = new RowsetDefinition.Column("SCOPE", RowsetDefinition.Type.Integer, (Enumeration)null, false, false, (String)null);
        }
    }

    static class MdschemaSetsRowset extends Rowset {
        private final Functor1<Boolean, Catalog> catalogCond;
        private final Functor1<Boolean, Schema> schemaNameCond;
        private final Functor1<Boolean, Cube> cubeNameCond;
        private final Functor1<Boolean, NamedSet> setNameCond;
        private static final String GLOBAL_SCOPE = "1";
        private static final RowsetDefinition.Column CatalogName;
        private static final RowsetDefinition.Column SchemaName;
        private static final RowsetDefinition.Column CubeName;
        private static final RowsetDefinition.Column SetName;
        private static final RowsetDefinition.Column SetCaption;
        private static final RowsetDefinition.Column Scope;
        private static final RowsetDefinition.Column Description;
        private static final RowsetDefinition.Column Expression;
        private static final RowsetDefinition.Column Dimensions;
        private static final RowsetDefinition.Column DisplayFolder;
        private static final RowsetDefinition.Column EvaluationContext;

        MdschemaSetsRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.MDSCHEMA_SETS, request, handler);
            this.catalogCond = this.makeCondition(RowsetDefinition.CATALOG_NAME_GETTER, CatalogName);
            this.schemaNameCond = this.makeCondition(RowsetDefinition.SCHEMA_NAME_GETTER, SchemaName);
            this.cubeNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, CubeName);
            this.setNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, SetName);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException, OlapException {
            Iterator var4 = RowsetDefinition.catIter(connection, this.catNameCond(), this.catalogCond).iterator();

            while(var4.hasNext()) {
                Catalog catalog = (Catalog)var4.next();
                this.processCatalog(connection, catalog, rows);
            }

        }

        private void processCatalog(OlapConnection connection, Catalog catalog, List<Row> rows) throws OlapException {
            Iterator var4 = Util.filter(catalog.getSchemas(), new Functor1[]{this.schemaNameCond}).iterator();

            while(var4.hasNext()) {
                Schema schema = (Schema)var4.next();
                Iterator var6 = Util.filter(RowsetDefinition.sortedCubes(schema), new Functor1[]{this.cubeNameCond}).iterator();

                while(var6.hasNext()) {
                    Cube cube = (Cube)var6.next();
                    this.populateNamedSets(cube, catalog, rows);
                }
            }

        }

        private void populateNamedSets(Cube cube, Catalog catalog, List<Row> rows) {
            Iterator var4 = Util.filter(cube.getSets(), new Functor1[]{this.setNameCond}).iterator();

            while(var4.hasNext()) {
                NamedSet namedSet = (NamedSet)var4.next();
                MondrianOlap4jNamedSet mondrianOlap4jNamedSet = (MondrianOlap4jNamedSet)namedSet;
                SetBase setBase = (SetBase)mondrianOlap4jNamedSet.getNamedSet();
                String dimensions = "";

                mondrian.olap.Hierarchy hierarchy;
                for(Iterator var9 = setBase.getHierarchies().iterator(); var9.hasNext(); dimensions = dimensions + hierarchy.getUniqueName()) {
                    hierarchy = (mondrian.olap.Hierarchy)var9.next();
                    if (!dimensions.equals("")) {
                        dimensions = dimensions + ",";
                    }
                }

                Row row = new Row();
                row.set(CatalogName.name, catalog.getName());
                row.set(SchemaName.name, cube.getSchema().getName());
                row.set(CubeName.name, cube.getName());
                row.set(SetName.name, namedSet.getName());
                row.set(Scope.name, "1");
                row.set(Description.name, namedSet.getDescription());
                row.set(Dimensions.name, dimensions);
                row.set(Expression.name, setBase.getExp().toString());
                row.set(SetCaption.name, namedSet.getCaption());
                row.set(DisplayFolder.name, setBase.getDisplayFolder());
                row.set(EvaluationContext.name, "1");
                this.addRow(row, rows);
            }

        }

        static {
            CatalogName = new RowsetDefinition.Column("CATALOG_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, (String)null);
            SchemaName = new RowsetDefinition.Column("SCHEMA_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, (String)null);
            CubeName = new RowsetDefinition.Column("CUBE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, (String)null);
            SetName = new RowsetDefinition.Column("SET_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, (String)null);
            SetCaption = new RowsetDefinition.Column("SET_CAPTION", RowsetDefinition.Type.String, (Enumeration)null, true, true, (String)null);
            Scope = new RowsetDefinition.Column("SCOPE", RowsetDefinition.Type.Integer, (Enumeration)null, true, false, (String)null);
            Description = new RowsetDefinition.Column("DESCRIPTION", RowsetDefinition.Type.String, (Enumeration)null, false, true, "A human-readable description of the measure.");
            Expression = new RowsetDefinition.Column("EXPRESSION", RowsetDefinition.Type.String, (Enumeration)null, false, true, "The expression for the set.");
            Dimensions = new RowsetDefinition.Column("DIMENSIONS", RowsetDefinition.Type.String, (Enumeration)null, false, true, "A comma delimited list of hierarchies included in the set.");
            DisplayFolder = new RowsetDefinition.Column("SET_DISPLAY_FOLDER", RowsetDefinition.Type.String, (Enumeration)null, false, true, "A string that identifies the path of the display folder that the client application uses to show the set. The folder level separator is defined by the client application. For the tools and clients supplied by Analysis Services, the backslash (\\) is the level separator. To provide multiple display folders, use a semicolon (;) to separate the folders.");
            EvaluationContext = new RowsetDefinition.Column("SET_EVALUATION_CONTEXT", RowsetDefinition.Type.Integer, (Enumeration)null, false, true, "The context for the set. The set can be static or dynamic.\nThis column can have one of the following values:\nMDSET_RESOLUTION_STATIC=1\nMDSET_RESOLUTION_DYNAMIC=2");
        }
    }

    static class MdschemaMembersRowset extends Rowset {
        private final Functor1<Boolean, Catalog> catalogCond;
        private final Functor1<Boolean, Schema> schemaNameCond;
        private final Functor1<Boolean, Cube> cubeNameCond;
        private final Functor1<Boolean, Dimension> dimensionUnameCond;
        private final Functor1<Boolean, Hierarchy> hierarchyUnameCond;
        private final Functor1<Boolean, Member> memberNameCond;
        private final Functor1<Boolean, Member> memberUnameCond;
        private final Functor1<Boolean, Member> memberTypeCond;
        private static final RowsetDefinition.Column CatalogName;
        private static final RowsetDefinition.Column SchemaName;
        private static final RowsetDefinition.Column CubeName;
        private static final RowsetDefinition.Column DimensionUniqueName;
        private static final RowsetDefinition.Column HierarchyUniqueName;
        private static final RowsetDefinition.Column LevelUniqueName;
        private static final RowsetDefinition.Column LevelNumber;
        private static final RowsetDefinition.Column MemberOrdinal;
        private static final RowsetDefinition.Column MemberName;
        private static final RowsetDefinition.Column MemberUniqueName;
        private static final RowsetDefinition.Column MemberType;
        private static final RowsetDefinition.Column MemberGuid;
        private static final RowsetDefinition.Column MemberCaption;
        private static final RowsetDefinition.Column ChildrenCardinality;
        private static final RowsetDefinition.Column ParentLevel;
        private static final RowsetDefinition.Column ParentUniqueName;
        private static final RowsetDefinition.Column ParentCount;
        private static final RowsetDefinition.Column TreeOp_;
        private static final RowsetDefinition.Column Depth;

        MdschemaMembersRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.MDSCHEMA_MEMBERS, request, handler);
            this.catalogCond = this.makeCondition(RowsetDefinition.CATALOG_NAME_GETTER, CatalogName);
            this.schemaNameCond = this.makeCondition(RowsetDefinition.SCHEMA_NAME_GETTER, SchemaName);
            this.cubeNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, CubeName);
            this.dimensionUnameCond = this.makeCondition(RowsetDefinition.ELEMENT_UNAME_GETTER, DimensionUniqueName);
            this.hierarchyUnameCond = this.makeCondition(RowsetDefinition.ELEMENT_UNAME_GETTER, HierarchyUniqueName);
            this.memberNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, MemberName);
            this.memberUnameCond = this.makeCondition(RowsetDefinition.ELEMENT_UNAME_GETTER, MemberUniqueName);
            this.memberTypeCond = this.makeCondition(RowsetDefinition.MEMBER_TYPE_GETTER, MemberType);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException, SQLException {
            Iterator var4 = RowsetDefinition.catIter(connection, this.catNameCond(), this.catalogCond).iterator();

            while(var4.hasNext()) {
                Catalog catalog = (Catalog)var4.next();
                this.populateCatalog(connection, catalog, rows);
            }

        }

        protected void populateCatalog(OlapConnection connection, Catalog catalog, List<Row> rows) throws XmlaException, SQLException {
            Iterator var4 = Util.filter(catalog.getSchemas(), new Functor1[]{this.schemaNameCond}).iterator();

            while(var4.hasNext()) {
                Schema schema = (Schema)var4.next();
                Iterator var6 = RowsetDefinition.filteredCubes(schema, this.cubeNameCond).iterator();

                while(var6.hasNext()) {
                    Cube cube = (Cube)var6.next();
                    if (this.isRestricted(MemberUniqueName)) {
                        this.outputUniqueMemberName(connection, catalog, cube, rows);
                    } else {
                        this.populateCube(connection, catalog, cube, rows);
                    }
                }
            }

        }

        protected void populateCube(OlapConnection connection, Catalog catalog, Cube cube, List<Row> rows) throws XmlaException, SQLException {
            if (this.isRestricted(LevelUniqueName)) {
                String levelUniqueName = this.getRestrictionValueAsString(LevelUniqueName);
                if (levelUniqueName == null) {
                    return;
                }

                Level level = RowsetDefinition.lookupLevel(cube, levelUniqueName);
                if (level != null) {
                    List<Member> members = level.getMembers();
                    this.outputMembers(connection, members, catalog, cube, rows);
                }
            } else {
                Iterator var8 = Util.filter(cube.getDimensions(), new Functor1[]{this.dimensionUnameCond}).iterator();

                while(var8.hasNext()) {
                    Dimension dimension = (Dimension)var8.next();
                    this.populateDimension(connection, catalog, cube, dimension, rows);
                }
            }

        }

        protected void populateDimension(OlapConnection connection, Catalog catalog, Cube cube, Dimension dimension, List<Row> rows) throws XmlaException, SQLException {
            Iterator var6 = Util.filter(dimension.getHierarchies(), new Functor1[]{this.hierarchyUnameCond}).iterator();

            while(var6.hasNext()) {
                Hierarchy hierarchy = (Hierarchy)var6.next();
                this.populateHierarchy(connection, catalog, cube, hierarchy, rows);
            }

        }

        protected void populateHierarchy(OlapConnection connection, Catalog catalog, Cube cube, Hierarchy hierarchy, List<Row> rows) throws XmlaException, SQLException {
            if (this.isRestricted(LevelNumber)) {
                int levelNumber = this.getRestrictionValueAsInt(LevelNumber);
                if (levelNumber == -1) {
                    LOGGER.warn("RowsetDefinition.populateHierarchy: LevelNumber invalid");
                    return;
                }

                NamedList<Level> levels = hierarchy.getLevels();
                if (levelNumber >= levels.size()) {
                    LOGGER.warn("RowsetDefinition.populateHierarchy: LevelNumber (" + levelNumber + ") is greater than number of levels (" + levels.size() + ") for hierarchy \"" + hierarchy.getUniqueName() + "\"");
                    return;
                }

                Level level = (Level)levels.get(levelNumber);
                List<Member> members = level.getMembers();
                this.outputMembers(connection, members, catalog, cube, rows);
            } else {
                Iterator var10 = hierarchy.getLevels().iterator();

                while(var10.hasNext()) {
                    Level level = (Level)var10.next();
                    this.outputMembers(connection, level.getMembers(), catalog, cube, rows);
                }
            }

        }

        private static boolean mask(int value, int mask) {
            return (value & mask) == mask;
        }

        private void populateMember(OlapConnection connection, Catalog catalog, Cube cube, Member member, int treeOp, List<Row> rows) throws SQLException {
            if (mask(treeOp, TreeOp.SELF.xmlaOrdinal())) {
                this.outputMember(connection, member, catalog, cube, rows);
            }

            Member child;
            if (mask(treeOp, TreeOp.SIBLINGS.xmlaOrdinal())) {
                child = member.getParentMember();
                NamedList siblings;
                if (child == null) {
                    siblings = member.getHierarchy().getRootMembers();
                } else {
                    siblings = Olap4jUtil.cast(child.getChildMembers());
                }

                Iterator var9 = siblings.iterator();

                while(var9.hasNext()) {
                    Member sibling = (Member)var9.next();
                    if (!sibling.equals(member)) {
                        this.populateMember(connection, catalog, cube, sibling, TreeOp.SELF.xmlaOrdinal(), rows);
                    }
                }
            }

            Iterator var11;
            if (mask(treeOp, TreeOp.DESCENDANTS.xmlaOrdinal())) {
                var11 = member.getChildMembers().iterator();

                while(var11.hasNext()) {
                    child = (Member)var11.next();
                    this.populateMember(connection, catalog, cube, child, TreeOp.SELF.xmlaOrdinal() | TreeOp.DESCENDANTS.xmlaOrdinal(), rows);
                }
            } else if (mask(treeOp, TreeOp.CHILDREN.xmlaOrdinal())) {
                var11 = member.getChildMembers().iterator();

                while(var11.hasNext()) {
                    child = (Member)var11.next();
                    this.populateMember(connection, catalog, cube, child, TreeOp.SELF.xmlaOrdinal(), rows);
                }
            }

            Member parent;
            if (mask(treeOp, TreeOp.ANCESTORS.xmlaOrdinal())) {
                parent = member.getParentMember();
                if (parent != null) {
                    this.populateMember(connection, catalog, cube, parent, TreeOp.SELF.xmlaOrdinal() | TreeOp.ANCESTORS.xmlaOrdinal(), rows);
                }
            } else if (mask(treeOp, TreeOp.PARENT.xmlaOrdinal())) {
                parent = member.getParentMember();
                if (parent != null) {
                    this.populateMember(connection, catalog, cube, parent, TreeOp.SELF.xmlaOrdinal(), rows);
                }
            }

        }

        protected ArrayList<RowsetDefinition.Column> pruneRestrictions(ArrayList<RowsetDefinition.Column> list) {
            if (list.contains(TreeOp_)) {
                list.remove(TreeOp_);
                list.remove(MemberUniqueName);
            }

            return list;
        }

        private void outputMembers(OlapConnection connection, List<Member> members, Catalog catalog, Cube cube, List<Row> rows) throws SQLException {
            Iterator var6 = members.iterator();

            while(var6.hasNext()) {
                Member member = (Member)var6.next();
                this.outputMember(connection, member, catalog, cube, rows);
            }

        }

        private void outputUniqueMemberName(OlapConnection connection, Catalog catalog, Cube cube, List<Row> rows) throws SQLException {
            Object unameRestrictions = this.restrictions.get(MemberUniqueName.name);
            List list;
            if (unameRestrictions instanceof String) {
                list = Collections.singletonList((String)unameRestrictions);
            } else {
                list = (List)unameRestrictions;
            }

            Iterator var7 = list.iterator();

            while(var7.hasNext()) {
                String memberUniqueName = (String)var7.next();
                IdentifierNode identifierNode = IdentifierNode.parseIdentifier(memberUniqueName);
                Member member = cube.lookupMember(identifierNode.getSegmentList());
                if (member == null) {
                    return;
                }

                if (this.isRestricted(TreeOp_)) {
                    int treeOp = this.getRestrictionValueAsInt(TreeOp_);
                    if (treeOp == -1) {
                        return;
                    }

                    this.populateMember(connection, catalog, cube, member, treeOp, rows);
                } else {
                    this.outputMember(connection, member, catalog, cube, rows);
                }
            }

        }

        private void outputMember(OlapConnection connection, Member member, Catalog catalog, Cube cube, List<Row> rows) throws SQLException {
            if ((Boolean)this.memberNameCond.apply(member)) {
                if ((Boolean)this.memberTypeCond.apply(member)) {
                    XmlaHandler.getExtra(connection).checkMemberOrdinal(member);
                    Boolean visible = (Boolean)member.getPropertyValue(StandardMemberProperty.$visible);
                    if (visible == null) {
                        visible = true;
                    }

                    if (visible || XmlaUtil.shouldEmitInvisibleMembers(this.request)) {
                        Level level = member.getLevel();
                        Hierarchy hierarchy = level.getHierarchy();
                        Dimension dimension = hierarchy.getDimension();
                        int adjustedLevelDepth = level.getDepth();
                        Row row = new Row();
                        row.set(CatalogName.name, catalog.getName());
                        row.set(SchemaName.name, cube.getSchema().getName());
                        row.set(CubeName.name, cube.getName());
                        row.set(DimensionUniqueName.name, dimension.getUniqueName());
                        row.set(HierarchyUniqueName.name, hierarchy.getUniqueName());
                        row.set(LevelUniqueName.name, level.getUniqueName());
                        row.set(LevelNumber.name, adjustedLevelDepth);
                        row.set(MemberOrdinal.name, 0);
                        row.set(MemberName.name, member.getName());
                        row.set(MemberUniqueName.name, member.getUniqueName());
                        row.set(MemberType.name, member.getMemberType().ordinal());
                        row.set(MemberCaption.name, member.getCaption());
                        row.set(ChildrenCardinality.name, member.getPropertyValue(StandardMemberProperty.CHILDREN_CARDINALITY));
                        row.set(ChildrenCardinality.name, 100);
                        if (adjustedLevelDepth == 0) {
                            row.set(ParentLevel.name, 0);
                        } else {
                            row.set(ParentLevel.name, adjustedLevelDepth - 1);
                            Member parentMember = member.getParentMember();
                            if (parentMember != null) {
                                row.set(ParentUniqueName.name, parentMember.getUniqueName());
                            }
                        }

                        row.set(ParentCount.name, member.getParentMember() == null ? 0 : 1);
                        row.set(Depth.name, member.getDepth());
                        this.addRow(row, rows);
                    }
                }
            }
        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            CatalogName = new RowsetDefinition.Column("CATALOG_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the catalog to which this member belongs.");
            SchemaName = new RowsetDefinition.Column("SCHEMA_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the schema to which this member belongs.");
            CubeName = new RowsetDefinition.Column("CUBE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "Name of the cube to which this member belongs.");
            DimensionUniqueName = new RowsetDefinition.Column("DIMENSION_UNIQUE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "Unique name of the dimension to which this member belongs.");
            HierarchyUniqueName = new RowsetDefinition.Column("HIERARCHY_UNIQUE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "Unique name of the hierarchy. If the member belongs to more than one hierarchy, there is one row for each hierarchy to which it belongs.");
            LevelUniqueName = new RowsetDefinition.Column("LEVEL_UNIQUE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, " Unique name of the level to which the member belongs.");
            LevelNumber = new RowsetDefinition.Column("LEVEL_NUMBER", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, true, false, "The distance of the member from the root of the hierarchy.");
            MemberOrdinal = new RowsetDefinition.Column("MEMBER_ORDINAL", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, false, false, "Ordinal number of the member. Sort rank of the member when members of this dimension are sorted in their natural sort order. If providers do not have the concept of natural ordering, this should be the rank when sorted by MEMBER_NAME.");
            MemberName = new RowsetDefinition.Column("MEMBER_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "Name of the member.");
            MemberUniqueName = new RowsetDefinition.Column("MEMBER_UNIQUE_NAME", RowsetDefinition.Type.StringSometimesArray, (Enumeration)null, true, false, " Unique name of the member.");
            MemberType = new RowsetDefinition.Column("MEMBER_TYPE", RowsetDefinition.Type.Integer, (Enumeration)null, true, false, "Type of the member.");
            MemberGuid = new RowsetDefinition.Column("MEMBER_GUID", RowsetDefinition.Type.UUID, (Enumeration)null, false, true, "Memeber GUID.");
            MemberCaption = new RowsetDefinition.Column("MEMBER_CAPTION", RowsetDefinition.Type.String, (Enumeration)null, true, false, "A label or caption associated with the member.");
            ChildrenCardinality = new RowsetDefinition.Column("CHILDREN_CARDINALITY", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, false, false, "Number of children that the member has.");
            ParentLevel = new RowsetDefinition.Column("PARENT_LEVEL", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, false, false, "The distance of the member's parent from the root level of the hierarchy.");
            ParentUniqueName = new RowsetDefinition.Column("PARENT_UNIQUE_NAME", RowsetDefinition.Type.String, (Enumeration)null, false, true, "Unique name of the member's parent.");
            ParentCount = new RowsetDefinition.Column("PARENT_COUNT", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, false, false, "Number of parents that this member has.");
            TreeOp_ = new RowsetDefinition.Column("TREE_OP", RowsetDefinition.Type.Enumeration, Enumeration.TREE_OP, true, true, "Tree Operation");
            Depth = new RowsetDefinition.Column("DEPTH", RowsetDefinition.Type.Integer, (Enumeration)null, false, true, "depth");
        }
    }

    public static class MdschemaMeasuresRowset extends Rowset {
        public static final int MDMEASURE_AGGR_UNKNOWN = 0;
        public static final int MDMEASURE_AGGR_SUM = 1;
        public static final int MDMEASURE_AGGR_COUNT = 2;
        public static final int MDMEASURE_AGGR_MIN = 3;
        public static final int MDMEASURE_AGGR_MAX = 4;
        public static final int MDMEASURE_AGGR_AVG = 5;
        public static final int MDMEASURE_AGGR_VAR = 6;
        public static final int MDMEASURE_AGGR_STD = 7;
        public static final int MDMEASURE_AGGR_CALCULATED = 127;
        private final Functor1<Boolean, Catalog> catalogCond;
        private final Functor1<Boolean, Schema> schemaNameCond;
        private final Functor1<Boolean, Cube> cubeNameCond;
        private final Functor1<Boolean, Measure> measureUnameCond;
        private final Functor1<Boolean, Measure> measureNameCond;
        private static final RowsetDefinition.Column CatalogName;
        private static final RowsetDefinition.Column SchemaName;
        private static final RowsetDefinition.Column CubeName;
        private static final RowsetDefinition.Column MeasureName;
        private static final RowsetDefinition.Column MeasureUniqueName;
        private static final RowsetDefinition.Column MeasureCaption;
        private static final RowsetDefinition.Column MeasureGuid;
        private static final RowsetDefinition.Column MeasureAggregator;
        private static final RowsetDefinition.Column DataType;
        private static final RowsetDefinition.Column MeasureIsVisible;
        private static final RowsetDefinition.Column LevelsList;
        private static final RowsetDefinition.Column Description;
        private static final RowsetDefinition.Column MeasuregroupName;
        private static final RowsetDefinition.Column DisplayFolder;
        private static final RowsetDefinition.Column FormatString;
        private static final RowsetDefinition.Column MeasureVisiblity;
        private static final RowsetDefinition.Column CubeSource;

        MdschemaMeasuresRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.MDSCHEMA_MEASURES, request, handler);
            this.catalogCond = this.makeCondition(RowsetDefinition.CATALOG_NAME_GETTER, CatalogName);
            this.schemaNameCond = this.makeCondition(RowsetDefinition.SCHEMA_NAME_GETTER, SchemaName);
            this.cubeNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, CubeName);
            this.measureNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, MeasureName);
            this.measureUnameCond = this.makeCondition(RowsetDefinition.ELEMENT_UNAME_GETTER, MeasureUniqueName);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException, SQLException {
            Iterator var4 = RowsetDefinition.catIter(connection, this.catNameCond(), this.catalogCond).iterator();

            while(var4.hasNext()) {
                Catalog catalog = (Catalog)var4.next();
                this.populateCatalog(connection, catalog, rows);
            }

        }

        protected void populateCatalog(OlapConnection connection, Catalog catalog, List<Row> rows) throws XmlaException, SQLException {
            StringBuilder buf = new StringBuilder(100);
            Iterator var5 = Util.filter(catalog.getSchemas(), new Functor1[]{this.schemaNameCond}).iterator();

            while(var5.hasNext()) {
                Schema schema = (Schema)var5.next();
                Iterator var7 = RowsetDefinition.filteredCubes(schema, this.cubeNameCond).iterator();

                label64:
                while(var7.hasNext()) {
                    Cube cube = (Cube)var7.next();
                    buf.setLength(0);
                    int j = 0;
                    Iterator var10 = cube.getDimensions().iterator();

                    while(true) {
                        Dimension dimension;
                        Iterator var12;
                        do {
                            if (!var10.hasNext()) {
                                String levelListStr = buf.toString();
                                List<Member> calcMembers = new ArrayList();
                                var12 = Util.filter(cube.getMeasures(), new Functor1[]{this.measureNameCond, this.measureUnameCond}).iterator();

                                while(var12.hasNext()) {
                                    Measure measure = (Measure)var12.next();
                                    if (measure.isCalculated()) {
                                        calcMembers.add(measure);
                                    } else {
                                        this.populateMember(connection, catalog, measure, cube, levelListStr, rows);
                                    }
                                }

                                var12 = calcMembers.iterator();

                                while(var12.hasNext()) {
                                    Member member = (Member)var12.next();
                                    this.populateMember(connection, catalog, member, cube, (String)null, rows);
                                }
                                continue label64;
                            }

                            dimension = (Dimension)var10.next();
                        } while(dimension.getDimensionType() == org.olap4j.metadata.Dimension.Type.MEASURE);

                        Level lastLevel;
                        for(var12 = dimension.getHierarchies().iterator(); var12.hasNext(); buf.append(lastLevel.getUniqueName())) {
                            Hierarchy hierarchy = (Hierarchy)var12.next();
                            NamedList<Level> levels = hierarchy.getLevels();
                            lastLevel = (Level)levels.get(levels.size() - 1);
                            if (j++ > 0) {
                                buf.append(',');
                            }
                        }
                    }
                }
            }

        }

        private void populateMember(OlapConnection connection, Catalog catalog, Member member, Cube cube, String levelListStr, List<Row> rows) throws SQLException {
            Boolean visible = (Boolean)member.getPropertyValue(StandardMemberProperty.$visible);
            if (visible == null) {
                visible = true;
            }

            if (visible || XmlaUtil.shouldEmitInvisibleMembers(this.request)) {
                RolapConnection rolapConnection = ((MondrianOlap4jConnection)connection).getMondrianConnection();
                if (rolapConnection.getRole().canAccess(((MondrianOlap4jMember)member).getOlapElement())) {
                    String desc = member.getDescription();
                    if (desc == null) {
                        desc = cube.getName() + " Cube - " + member.getName() + " Member";
                    }

                    String formatString = (String)member.getPropertyValue(StandardCellProperty.FORMAT_STRING);
                    Row row = new Row();
                    row.set(CatalogName.name, catalog.getName());
                    row.set(SchemaName.name, cube.getSchema().getName());
                    row.set(CubeName.name, cube.getName());
                    row.set(MeasureName.name, member.getName());
                    row.set(MeasureUniqueName.name, member.getUniqueName());
                    row.set(MeasureCaption.name, member.getCaption());
                    XmlaExtra extra = XmlaHandler.getExtra(connection);
                    row.set(MeasureAggregator.name, extra.getMeasureAggregator(member));
                    DBType dbType = DBType.WSTR;
                    String datatype = (String)member.getPropertyValue(StandardCellProperty.DATATYPE);
                    if (datatype != null) {
                        if (datatype.equals("Integer")) {
                            dbType = DBType.I4;
                        } else if (datatype.equals("Numeric")) {
                            dbType = DBType.R8;
                        } else {
                            dbType = DBType.WSTR;
                        }
                    }

                    row.set(DataType.name, dbType.xmlaOrdinal());
                    row.set(MeasureIsVisible.name, visible);
                    row.set(MeasuregroupName.name, cube.getName());
                    String displayFolder = extra.getMeasureDisplayFolder(member);
                    if (displayFolder == null) {
                        displayFolder = "";
                    }

                    row.set(DisplayFolder.name, displayFolder);
                    row.set(MeasureVisiblity.name, visible ? 1 : 2);
                    if (levelListStr != null) {
                        row.set(LevelsList.name, levelListStr);
                    }

                    row.set(Description.name, desc);
                    row.set(FormatString.name, formatString);
                    this.addRow(row, rows);
                }
            }
        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            CatalogName = new RowsetDefinition.Column("CATALOG_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the catalog to which this measure belongs.");
            SchemaName = new RowsetDefinition.Column("SCHEMA_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the schema to which this measure belongs.");
            CubeName = new RowsetDefinition.Column("CUBE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The name of the cube to which this measure belongs.");
            MeasureName = new RowsetDefinition.Column("MEASURE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The name of the measure.");
            MeasureUniqueName = new RowsetDefinition.Column("MEASURE_UNIQUE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The Unique name of the measure.");
            MeasureCaption = new RowsetDefinition.Column("MEASURE_CAPTION", RowsetDefinition.Type.String, (Enumeration)null, false, false, "A label or caption associated with the measure.");
            MeasureGuid = new RowsetDefinition.Column("MEASURE_GUID", RowsetDefinition.Type.UUID, (Enumeration)null, false, true, "Measure GUID.");
            MeasureAggregator = new RowsetDefinition.Column("MEASURE_AGGREGATOR", RowsetDefinition.Type.Integer, (Enumeration)null, false, false, "How a measure was derived.");
            DataType = new RowsetDefinition.Column("DATA_TYPE", RowsetDefinition.Type.UnsignedShort, (Enumeration)null, false, false, "Data type of the measure.");
            MeasureIsVisible = new RowsetDefinition.Column("MEASURE_IS_VISIBLE", RowsetDefinition.Type.Boolean, (Enumeration)null, false, false, "A Boolean that always returns True. If the measure is not visible, it will not be included in the schema rowset.");
            LevelsList = new RowsetDefinition.Column("LEVELS_LIST", RowsetDefinition.Type.String, (Enumeration)null, false, true, "A string that always returns NULL. EXCEPT that SQL Server returns non-null values!!!");
            Description = new RowsetDefinition.Column("DESCRIPTION", RowsetDefinition.Type.String, (Enumeration)null, false, true, "A human-readable description of the measure.");
            MeasuregroupName = new RowsetDefinition.Column("MEASUREGROUP_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the measure group to which the measure belongs.");
            DisplayFolder = new RowsetDefinition.Column("MEASURE_DISPLAY_FOLDER", RowsetDefinition.Type.String, (Enumeration)null, false, true, "The path to be used when displaying the measure in the user interface. Folder names will be separated by a semicolon. Nested folders are indicated by a backslash (\\).");
            FormatString = new RowsetDefinition.Column("DEFAULT_FORMAT_STRING", RowsetDefinition.Type.String, (Enumeration)null, false, true, "The default format string for the measure.");
            MeasureVisiblity = new RowsetDefinition.Column("MEASURE_VISIBILITY", RowsetDefinition.Type.UnsignedShort, (Enumeration)null, true, true, "A bitmap with one of the following valid values: 1 Visible, 2 Not visible.");
            CubeSource = new RowsetDefinition.Column("CUBE_SOURCE", RowsetDefinition.Type.UnsignedShort, (Enumeration)null, true, true, "A bitmap with one of the following valid values:\n1 CUBE\n2 DIMENSION\nDefault restriction is a value of 1.");
        }
    }

    static class MdschemaLevelsRowset extends Rowset {
        private final Functor1<Boolean, Catalog> catalogCond;
        private final Functor1<Boolean, Schema> schemaNameCond;
        private final Functor1<Boolean, Cube> cubeNameCond;
        private final Functor1<Boolean, Dimension> dimensionUnameCond;
        private final Functor1<Boolean, Hierarchy> hierarchyUnameCond;
        private final Functor1<Boolean, Level> levelUnameCond;
        private final Functor1<Boolean, Level> levelNameCond;
        public static final int MDLEVEL_TYPE_UNKNOWN = 0;
        public static final int MDLEVEL_TYPE_REGULAR = 0;
        public static final int MDLEVEL_TYPE_ALL = 1;
        public static final int MDLEVEL_TYPE_CALCULATED = 2;
        public static final int MDLEVEL_TYPE_TIME = 4;
        public static final int MDLEVEL_TYPE_RESERVED1 = 8;
        public static final int MDLEVEL_TYPE_TIME_YEARS = 20;
        public static final int MDLEVEL_TYPE_TIME_HALF_YEAR = 36;
        public static final int MDLEVEL_TYPE_TIME_QUARTERS = 68;
        public static final int MDLEVEL_TYPE_TIME_MONTHS = 132;
        public static final int MDLEVEL_TYPE_TIME_WEEKS = 260;
        public static final int MDLEVEL_TYPE_TIME_DAYS = 516;
        public static final int MDLEVEL_TYPE_TIME_HOURS = 772;
        public static final int MDLEVEL_TYPE_TIME_MINUTES = 1028;
        public static final int MDLEVEL_TYPE_TIME_SECONDS = 2052;
        public static final int MDLEVEL_TYPE_TIME_UNDEFINED = 4100;
        private static final RowsetDefinition.Column CatalogName;
        private static final RowsetDefinition.Column SchemaName;
        private static final RowsetDefinition.Column CubeName;
        private static final RowsetDefinition.Column DimensionUniqueName;
        private static final RowsetDefinition.Column HierarchyUniqueName;
        private static final RowsetDefinition.Column LevelName;
        private static final RowsetDefinition.Column LevelUniqueName;
        private static final RowsetDefinition.Column LevelGuid;
        private static final RowsetDefinition.Column LevelCaption;
        private static final RowsetDefinition.Column LevelNumber;
        private static final RowsetDefinition.Column LevelCardinality;
        private static final RowsetDefinition.Column LevelType;
        private static final RowsetDefinition.Column CustomRollupSettings;
        private static final RowsetDefinition.Column LevelUniqueSettings;
        private static final RowsetDefinition.Column LevelIsVisible;
        private static final RowsetDefinition.Column Description;
        private static final RowsetDefinition.Column LevelOrigin;
        private static final RowsetDefinition.Column CubeSource;
        private static final RowsetDefinition.Column LevelVisibility;

        MdschemaLevelsRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.MDSCHEMA_LEVELS, request, handler);
            this.catalogCond = this.makeCondition(RowsetDefinition.CATALOG_NAME_GETTER, CatalogName);
            this.schemaNameCond = this.makeCondition(RowsetDefinition.SCHEMA_NAME_GETTER, SchemaName);
            this.cubeNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, CubeName);
            this.dimensionUnameCond = this.makeCondition(RowsetDefinition.ELEMENT_UNAME_GETTER, DimensionUniqueName);
            this.hierarchyUnameCond = this.makeCondition(RowsetDefinition.ELEMENT_UNAME_GETTER, HierarchyUniqueName);
            this.levelUnameCond = this.makeCondition(RowsetDefinition.ELEMENT_UNAME_GETTER, LevelUniqueName);
            this.levelNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, LevelName);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException, SQLException {
            Iterator var4 = RowsetDefinition.catIter(connection, this.catNameCond(), this.catalogCond).iterator();

            while(var4.hasNext()) {
                Catalog catalog = (Catalog)var4.next();
                this.populateCatalog(connection, catalog, rows);
            }

        }

        protected void populateCatalog(OlapConnection connection, Catalog catalog, List<Row> rows) throws XmlaException, SQLException {
            Iterator var4 = Util.filter(catalog.getSchemas(), new Functor1[]{this.schemaNameCond}).iterator();

            while(var4.hasNext()) {
                Schema schema = (Schema)var4.next();
                Iterator var6 = RowsetDefinition.filteredCubes(schema, this.cubeNameCond).iterator();

                while(var6.hasNext()) {
                    Cube cube = (Cube)var6.next();
                    this.populateCube(connection, catalog, cube, rows);
                }
            }

        }

        protected void populateCube(OlapConnection connection, Catalog catalog, Cube cube, List<Row> rows) throws XmlaException, SQLException {
            Iterator var5 = Util.filter(cube.getDimensions(), new Functor1[]{this.dimensionUnameCond}).iterator();

            while(var5.hasNext()) {
                Dimension dimension = (Dimension)var5.next();
                if (dimension.isVisible()) {
                    this.populateDimension(connection, catalog, cube, dimension, rows);
                }
            }

        }

        protected void populateDimension(OlapConnection connection, Catalog catalog, Cube cube, Dimension dimension, List<Row> rows) throws XmlaException, SQLException {
            Iterator var6 = Util.filter(dimension.getHierarchies(), new Functor1[]{this.hierarchyUnameCond}).iterator();

            while(var6.hasNext()) {
                Hierarchy hierarchy = (Hierarchy)var6.next();
                this.populateHierarchy(connection, catalog, cube, hierarchy, rows);
            }

        }

        protected void populateHierarchy(OlapConnection connection, Catalog catalog, Cube cube, Hierarchy hierarchy, List<Row> rows) throws XmlaException, SQLException {
            Iterator var6 = Util.filter(hierarchy.getLevels(), new Functor1[]{this.levelUnameCond, this.levelNameCond}).iterator();

            while(var6.hasNext()) {
                Level level = (Level)var6.next();
                this.outputLevel(connection, catalog, cube, hierarchy, level, rows);
            }

        }

        protected boolean outputLevel(OlapConnection connection, Catalog catalog, Cube cube, Hierarchy hierarchy, Level level, List<Row> rows) throws XmlaException, SQLException {
            XmlaExtra extra = XmlaHandler.getExtra(connection);
            String desc = level.getDescription();
            if (desc == null) {
                desc = cube.getName() + " Cube - " + RowsetDefinition.getHierarchyName(hierarchy) + " Hierarchy - " + level.getName() + " Level";
            }

            Row row = new Row();
            row.set(CatalogName.name, catalog.getName());
            row.set(SchemaName.name, cube.getSchema().getName());
            row.set(CubeName.name, cube.getName());
            row.set(DimensionUniqueName.name, hierarchy.getDimension().getUniqueName());
            row.set(HierarchyUniqueName.name, hierarchy.getUniqueName());
            row.set(LevelName.name, level.getName());
            row.set(LevelUniqueName.name, level.getUniqueName());
            row.set(LevelCaption.name, level.getCaption());
            row.set(LevelNumber.name, level.getDepth());
            int n = extra.getLevelCardinality(level);
            row.set(LevelCardinality.name, n);
            row.set(LevelType.name, this.getLevelType(level));
            row.set(CustomRollupSettings.name, 0);
            int uniqueSettings = 0;
            if (level.getLevelType() == org.olap4j.metadata.Level.Type.ALL) {
                uniqueSettings |= 2;
            }

            if (extra.isLevelUnique(level)) {
                uniqueSettings |= 1;
            }

            row.set(LevelUniqueSettings.name, uniqueSettings);
            row.set(LevelIsVisible.name, level.isVisible());
            row.set(Description.name, desc);
            row.set(LevelOrigin.name, 0);
            this.addRow(row, rows);
            return true;
        }

        private int getLevelType(Level lev) {
            int ret = 0;
            switch(lev.getLevelType()) {
                case ALL:
                    ret = ret | 1;
                    break;
                case REGULAR:
                    ret = ret | 0;
                    break;
                case TIME_YEARS:
                    ret = ret | 20;
                    break;
                case TIME_HALF_YEAR:
                    ret = ret | 36;
                    break;
                case TIME_QUARTERS:
                    ret = ret | 68;
                    break;
                case TIME_MONTHS:
                    ret = ret | 132;
                    break;
                case TIME_WEEKS:
                    ret = ret | 260;
                    break;
                case TIME_DAYS:
                    ret = ret | 516;
                    break;
                case TIME_HOURS:
                    ret = ret | 772;
                    break;
                case TIME_MINUTES:
                    ret = ret | 1028;
                    break;
                case TIME_SECONDS:
                    ret = ret | 2052;
                    break;
                case TIME_UNDEFINED:
                    ret = ret | 4100;
                    break;
                default:
                    ret = ret | 0;
            }

            return ret;
        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            CatalogName = new RowsetDefinition.Column("CATALOG_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the catalog to which this level belongs.");
            SchemaName = new RowsetDefinition.Column("SCHEMA_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the schema to which this level belongs.");
            CubeName = new RowsetDefinition.Column("CUBE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The name of the cube to which this level belongs.");
            DimensionUniqueName = new RowsetDefinition.Column("DIMENSION_UNIQUE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The unique name of the dimension to which this level belongs.");
            HierarchyUniqueName = new RowsetDefinition.Column("HIERARCHY_UNIQUE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The unique name of the hierarchy.");
            LevelName = new RowsetDefinition.Column("LEVEL_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The name of the level.");
            LevelUniqueName = new RowsetDefinition.Column("LEVEL_UNIQUE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The properly escaped unique name of the level.");
            LevelGuid = new RowsetDefinition.Column("LEVEL_GUID", RowsetDefinition.Type.UUID, (Enumeration)null, false, true, "Level GUID.");
            LevelCaption = new RowsetDefinition.Column("LEVEL_CAPTION", RowsetDefinition.Type.String, (Enumeration)null, false, false, "A label or caption associated with the hierarchy.");
            LevelNumber = new RowsetDefinition.Column("LEVEL_NUMBER", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, false, false, "The distance of the level from the root of the hierarchy. Root level is zero (0).");
            LevelCardinality = new RowsetDefinition.Column("LEVEL_CARDINALITY", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, false, false, "The number of members in the level. This value can be an approximation of the real cardinality.");
            LevelType = new RowsetDefinition.Column("LEVEL_TYPE", RowsetDefinition.Type.Integer, (Enumeration)null, false, false, "Type of the level");
            CustomRollupSettings = new RowsetDefinition.Column("CUSTOM_ROLLUP_SETTINGS", RowsetDefinition.Type.Integer, (Enumeration)null, false, false, "A bitmap that specifies the custom rollup options.");
            LevelUniqueSettings = new RowsetDefinition.Column("LEVEL_UNIQUE_SETTINGS", RowsetDefinition.Type.Integer, (Enumeration)null, false, false, "A bitmap that specifies which columns contain unique values, if the level only has members with unique names or keys.");
            LevelIsVisible = new RowsetDefinition.Column("LEVEL_IS_VISIBLE", RowsetDefinition.Type.Boolean, (Enumeration)null, false, false, "A Boolean that indicates whether the level is visible.");
            Description = new RowsetDefinition.Column("DESCRIPTION", RowsetDefinition.Type.String, (Enumeration)null, false, true, "A human-readable description of the level. NULL if no description exists.");
            LevelOrigin = new RowsetDefinition.Column("LEVEL_ORIGIN", RowsetDefinition.Type.UnsignedShort, (Enumeration)null, true, true, "A bit map that defines how the level was sourced:\nMD_ORIGIN_USER_DEFINED identifies levels in a user defined hierarchy.\nMD_ORIGIN_ATTRIBUTE identifies levels in an attribute hierarchy.\nMD_ORIGIN_KEY_ATTRIBUTE identifies levels in a key attribute hierarchy.\nMD_ORIGIN_INTERNAL identifies levels in attribute hierarchies that are not enabled.\n");
            CubeSource = new RowsetDefinition.Column("CUBE_SOURCE", RowsetDefinition.Type.UnsignedShort, (Enumeration)null, true, true, "A bitmap with one of the following valid values:\n1 CUBE\n2 DIMENSION\nDefault restriction is a value of 1.");
            LevelVisibility = new RowsetDefinition.Column("LEVEL_VISIBILITY", RowsetDefinition.Type.UnsignedShort, (Enumeration)null, true, true, "A bitmap with one of the following values:\n1 Visible\n2 Not visible\nDefault restriction is a value of 1.");
        }
    }

    static class MdschemaHierarchiesRowset extends Rowset {
        private final Functor1<Boolean, Catalog> catalogCond;
        private final Functor1<Boolean, Schema> schemaNameCond;
        private final Functor1<Boolean, Cube> cubeNameCond;
        private final Functor1<Boolean, Dimension> dimensionUnameCond;
        private final Functor1<Boolean, Hierarchy> hierarchyUnameCond;
        private final Functor1<Boolean, Hierarchy> hierarchyNameCond;
        private static final RowsetDefinition.Column CatalogName;
        private static final RowsetDefinition.Column SchemaName;
        private static final RowsetDefinition.Column CubeName;
        private static final RowsetDefinition.Column DimensionUniqueName;
        private static final RowsetDefinition.Column HierarchyName;
        private static final RowsetDefinition.Column HierarchyUniqueName;
        private static final RowsetDefinition.Column HierarchyGuid;
        private static final RowsetDefinition.Column HierarchyCaption;
        private static final RowsetDefinition.Column DimensionType;
        private static final RowsetDefinition.Column HierarchyCardinality;
        private static final RowsetDefinition.Column DefaultMember;
        private static final RowsetDefinition.Column AllMember;
        private static final RowsetDefinition.Column Description;
        private static final RowsetDefinition.Column Structure;
        private static final RowsetDefinition.Column IsVirtual;
        private static final RowsetDefinition.Column IsReadWrite;
        private static final RowsetDefinition.Column DimensionUniqueSettings;
        private static final RowsetDefinition.Column DimensionIsVisible;
        private static final RowsetDefinition.Column HierarchyIsVisibile;
        private static final RowsetDefinition.Column HierarchyOrigin;
        private static final RowsetDefinition.Column DisplayFolder;
        private static final RowsetDefinition.Column CubeSource;
        private static final RowsetDefinition.Column HierarchyVisibility;
        private static final RowsetDefinition.Column HierarchyOrdinal;
        private static final RowsetDefinition.Column DimensionIsShared;
        private static final RowsetDefinition.Column Levels;
        private static final RowsetDefinition.Column ParentChild;

        MdschemaHierarchiesRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.MDSCHEMA_HIERARCHIES, request, handler);
            this.catalogCond = this.makeCondition(RowsetDefinition.CATALOG_NAME_GETTER, CatalogName);
            this.schemaNameCond = this.makeCondition(RowsetDefinition.SCHEMA_NAME_GETTER, SchemaName);
            this.cubeNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, CubeName);
            this.dimensionUnameCond = this.makeCondition(RowsetDefinition.ELEMENT_UNAME_GETTER, DimensionUniqueName);
            this.hierarchyUnameCond = this.makeCondition(RowsetDefinition.ELEMENT_UNAME_GETTER, HierarchyUniqueName);
            this.hierarchyNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, HierarchyName);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException, SQLException {
            Iterator var4 = RowsetDefinition.catIter(connection, this.catNameCond(), this.catalogCond).iterator();

            while(var4.hasNext()) {
                Catalog catalog = (Catalog)var4.next();
                this.populateCatalog(connection, catalog, rows);
            }

        }

        protected void populateCatalog(OlapConnection connection, Catalog catalog, List<Row> rows) throws XmlaException, SQLException {
            Iterator var4 = Util.filter(catalog.getSchemas(), new Functor1[]{this.schemaNameCond}).iterator();

            while(var4.hasNext()) {
                Schema schema = (Schema)var4.next();
                Iterator var6 = RowsetDefinition.filteredCubes(schema, this.cubeNameCond).iterator();

                while(var6.hasNext()) {
                    Cube cube = (Cube)var6.next();
                    this.populateCube(connection, catalog, cube, rows);
                }
            }

        }

        protected void populateCube(OlapConnection connection, Catalog catalog, Cube cube, List<Row> rows) throws XmlaException, SQLException {
            int ordinal = 0;

            Dimension dimension;
            for(Iterator var6 = cube.getDimensions().iterator(); var6.hasNext(); ordinal += dimension.getHierarchies().size()) {
                dimension = (Dimension)var6.next();
                boolean genOutput = (Boolean)this.dimensionUnameCond.apply(dimension);
                if (genOutput && dimension.isVisible()) {
                    this.populateDimension(connection, catalog, cube, dimension, ordinal, rows);
                }
            }

        }

        protected void populateDimension(OlapConnection connection, Catalog catalog, Cube cube, Dimension dimension, int ordinal, List<Row> rows) throws XmlaException, SQLException {
            NamedList<Hierarchy> hierarchies = dimension.getHierarchies();
            Iterator var8 = Util.filter(hierarchies, new Functor1[]{this.hierarchyNameCond, this.hierarchyUnameCond}).iterator();

            while(var8.hasNext()) {
                Hierarchy hierarchy = (Hierarchy)var8.next();
                this.populateHierarchy(connection, catalog, cube, dimension, hierarchy, ordinal + hierarchies.indexOf(hierarchy), rows);
            }

        }

        protected void populateHierarchy(OlapConnection connection, Catalog catalog, Cube cube, Dimension dimension, Hierarchy hierarchy, int ordinal, List<Row> rows) throws XmlaException, SQLException {
            XmlaExtra extra = XmlaHandler.getExtra(connection);
            String desc = hierarchy.getDescription();
            if (desc == null) {
                desc = cube.getName() + " Cube - " + RowsetDefinition.getHierarchyName(hierarchy) + " Hierarchy";
            }

            Row row = new Row();
            row.set(CatalogName.name, catalog.getName());
            row.set(SchemaName.name, cube.getSchema().getName());
            row.set(CubeName.name, cube.getName());
            row.set(DimensionUniqueName.name, dimension.getUniqueName());
            row.set(HierarchyName.name, hierarchy.getName());
            row.set(HierarchyUniqueName.name, hierarchy.getUniqueName());
            row.set(HierarchyCaption.name, hierarchy.getCaption());
            row.set(DimensionType.name, RowsetDefinition.getDimensionType(dimension));
            int cardinality = extra.getHierarchyCardinality(hierarchy);
            row.set(HierarchyCardinality.name, cardinality);
            row.set(DefaultMember.name, hierarchy.getDefaultMember().getUniqueName());
            if (hierarchy.hasAll()) {
                row.set(AllMember.name, ((Member)hierarchy.getRootMembers().get(0)).getUniqueName());
            }

            row.set(Description.name, desc);
            row.set(Structure.name, extra.getHierarchyStructure(hierarchy));
            row.set(IsVirtual.name, false);
            row.set(IsReadWrite.name, false);
            row.set(DimensionUniqueSettings.name, 0);
            row.set(DimensionIsVisible.name, dimension.isVisible());
            row.set(HierarchyIsVisibile.name, hierarchy.isVisible());
            row.set(HierarchyVisibility.name, hierarchy.isVisible() ? 1 : 2);
            MondrianOlap4jHierarchy mondrianOlap4jHierarchy = (MondrianOlap4jHierarchy)hierarchy;
            int hierarchyOrigin;
            if (dimension.getUniqueName().equals("[Measures]")) {
                hierarchyOrigin = 6;
            } else {
                RolapHierarchy rolapHierarchy = (RolapHierarchy)mondrianOlap4jHierarchy.getHierarchy();
                mondrian.olap.MondrianDef.Hierarchy xmlHierarchy = rolapHierarchy.getXmlHierarchy();

                try {
                    hierarchyOrigin = Integer.parseInt(xmlHierarchy.origin);
                } catch (NumberFormatException var17) {
                    hierarchyOrigin = 1;
                }
            }

            row.set(HierarchyOrigin.name, hierarchyOrigin);
            row.set(HierarchyOrdinal.name, ordinal);
            String displayFolder = mondrianOlap4jHierarchy.getDisplayFolder();
            if (displayFolder == null) {
                displayFolder = "";
            }

            row.set(DisplayFolder.name, displayFolder);
            row.set(DimensionIsShared.name, true);
            row.set(ParentChild.name, extra.isHierarchyParentChild(hierarchy));
            if (this.deep) {
                row.set(Levels.name, new RowsetDefinition.MdschemaLevelsRowset(RowsetDefinition.wrapRequest(this.request, Olap4jUtil.mapOf(RowsetDefinition.MdschemaLevelsRowset.CatalogName, catalog.getName(), new Object[]{RowsetDefinition.MdschemaLevelsRowset.SchemaName, cube.getSchema().getName(), RowsetDefinition.MdschemaLevelsRowset.CubeName, cube.getName(), RowsetDefinition.MdschemaLevelsRowset.DimensionUniqueName, dimension.getUniqueName(), RowsetDefinition.MdschemaLevelsRowset.HierarchyUniqueName, hierarchy.getUniqueName()})), this.handler));
            }

            this.addRow(row, rows);
        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            CatalogName = new RowsetDefinition.Column("CATALOG_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the catalog to which this hierarchy belongs.");
            SchemaName = new RowsetDefinition.Column("SCHEMA_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "Not supported");
            CubeName = new RowsetDefinition.Column("CUBE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The name of the cube to which this hierarchy belongs.");
            DimensionUniqueName = new RowsetDefinition.Column("DIMENSION_UNIQUE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The unique name of the dimension to which this hierarchy belongs.");
            HierarchyName = new RowsetDefinition.Column("HIERARCHY_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The name of the hierarchy. Blank if there is only a single hierarchy in the dimension.");
            HierarchyUniqueName = new RowsetDefinition.Column("HIERARCHY_UNIQUE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The unique name of the hierarchy.");
            HierarchyGuid = new RowsetDefinition.Column("HIERARCHY_GUID", RowsetDefinition.Type.UUID, (Enumeration)null, false, true, "Hierarchy GUID.");
            HierarchyCaption = new RowsetDefinition.Column("HIERARCHY_CAPTION", RowsetDefinition.Type.String, (Enumeration)null, false, false, "A label or a caption associated with the hierarchy.");
            DimensionType = new RowsetDefinition.Column("DIMENSION_TYPE", RowsetDefinition.Type.Short, (Enumeration)null, false, false, "The type of the dimension.");
            HierarchyCardinality = new RowsetDefinition.Column("HIERARCHY_CARDINALITY", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, false, false, "The number of members in the hierarchy.");
            DefaultMember = new RowsetDefinition.Column("DEFAULT_MEMBER", RowsetDefinition.Type.String, (Enumeration)null, false, true, "The default member for this hierarchy.");
            AllMember = new RowsetDefinition.Column("ALL_MEMBER", RowsetDefinition.Type.String, (Enumeration)null, false, true, "The member at the highest level of rollup in the hierarchy.");
            Description = new RowsetDefinition.Column("DESCRIPTION", RowsetDefinition.Type.String, (Enumeration)null, false, true, "A human-readable description of the hierarchy. NULL if no description exists.");
            Structure = new RowsetDefinition.Column("STRUCTURE", RowsetDefinition.Type.Short, (Enumeration)null, false, false, "The structure of the hierarchy.");
            IsVirtual = new RowsetDefinition.Column("IS_VIRTUAL", RowsetDefinition.Type.Boolean, (Enumeration)null, false, false, "Always returns False.");
            IsReadWrite = new RowsetDefinition.Column("IS_READWRITE", RowsetDefinition.Type.Boolean, (Enumeration)null, false, false, "A Boolean that indicates whether the Write Back to dimension column is enabled.");
            DimensionUniqueSettings = new RowsetDefinition.Column("DIMENSION_UNIQUE_SETTINGS", RowsetDefinition.Type.Integer, (Enumeration)null, false, false, "Always returns MDDIMENSIONS_MEMBER_KEY_UNIQUE (1).");
            DimensionIsVisible = new RowsetDefinition.Column("DIMENSION_IS_VISIBLE", RowsetDefinition.Type.Boolean, (Enumeration)null, false, false, "A Boolean that indicates whether the parent dimension is visible.");
            HierarchyIsVisibile = new RowsetDefinition.Column("HIERARCHY_IS_VISIBLE", RowsetDefinition.Type.Boolean, (Enumeration)null, false, false, "A Boolean that indicates whether the hieararchy is visible.");
            HierarchyOrigin = new RowsetDefinition.Column("HIERARCHY_ORIGIN", RowsetDefinition.Type.UnsignedShort, (Enumeration)null, true, true, "A bit mask that determines the source of the hierarchy:\nMD_ORIGIN_USER_DEFINED identifies levels in a user defined hierarchy (0x0000001).\nMD_ORIGIN_ATTRIBUTE identifies levels in an attribute hierarchy (0x0000002).\nMD_ORIGIN_INTERNAL identifies levels in attribute hierarchies that are not enabled (0x0000004).\nMD_ORIGIN_KEY_ATTRIBUTE identifies levels in a key attribute hierarchy (0x0000008).\n");
            DisplayFolder = new RowsetDefinition.Column("HIERARCHY_DISPLAY_FOLDER", RowsetDefinition.Type.String, (Enumeration)null, false, true, "The path to be used when displaying the hierarchy in the user interface. Folder names will be separated by a semicolon (;). Nested folders are indicated by a backslash (\\).");
            CubeSource = new RowsetDefinition.Column("CUBE_SOURCE", RowsetDefinition.Type.UnsignedShort, (Enumeration)null, true, true, "A bitmap with one of the following valid values:\n1 CUBE\n2 DIMENSION\nDefault restriction is a value of 1.");
            HierarchyVisibility = new RowsetDefinition.Column("HIERARCHY_VISIBILITY", RowsetDefinition.Type.UnsignedShort, (Enumeration)null, true, true, "A bitmap with one of the following valid values: 1 Visible, 2 Not visible.");
            HierarchyOrdinal = new RowsetDefinition.Column("HIERARCHY_ORDINAL", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, false, false, "The ordinal number of the hierarchy across all hierarchies of the cube.");
            DimensionIsShared = new RowsetDefinition.Column("DIMENSION_IS_SHARED", RowsetDefinition.Type.Boolean, (Enumeration)null, false, false, "Always returns true.");
            Levels = new RowsetDefinition.Column("LEVELS", RowsetDefinition.Type.Rowset, (Enumeration)null, false, true, "Levels in this hierarchy.");
            ParentChild = new RowsetDefinition.Column("PARENT_CHILD", RowsetDefinition.Type.Boolean, (Enumeration)null, false, true, "Is hierarchy a parent.");
        }
    }

    public static class MdschemaFunctionsRowset extends Rowset {
        private final Functor1<Boolean, String> functionNameCond;
        private static final RowsetDefinition.Column FunctionName;
        private static final RowsetDefinition.Column Description;
        private static final RowsetDefinition.Column ParameterList;
        private static final RowsetDefinition.Column ReturnType;
        private static final RowsetDefinition.Column Origin;
        private static final RowsetDefinition.Column InterfaceName;
        private static final RowsetDefinition.Column LibraryName;
        private static final RowsetDefinition.Column Caption;

        MdschemaFunctionsRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.MDSCHEMA_FUNCTIONS, request, handler);
            this.functionNameCond = this.makeCondition(FunctionName);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException, SQLException {
            XmlaExtra extra = XmlaHandler.getExtra(connection);
            Iterator var5 = RowsetDefinition.catIter(connection, this.catNameCond()).iterator();

            while(var5.hasNext()) {
                Catalog catalog = (Catalog)var5.next();
                Schema schema = (Schema)catalog.getSchemas().get(0);
                List<FunctionDefinition> funDefs = new ArrayList();
                extra.getSchemaFunctionList(funDefs, schema, this.functionNameCond);
                Iterator var9 = funDefs.iterator();

                while(var9.hasNext()) {
                    FunctionDefinition funDef = (FunctionDefinition)var9.next();
                    Row row = new Row();
                    row.set(FunctionName.name, funDef.functionName);
                    row.set(Description.name, funDef.description);
                    row.set(ParameterList.name, funDef.parameterList);
                    row.set(ReturnType.name, funDef.returnType);
                    row.set(Origin.name, funDef.origin);
                    row.set(InterfaceName.name, funDef.interfaceName);
                    row.set(Caption.name, funDef.caption);
                    this.addRow(row, rows);
                }
            }

        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            FunctionName = new RowsetDefinition.Column("FUNCTION_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The name of the function.");
            Description = new RowsetDefinition.Column("DESCRIPTION", RowsetDefinition.Type.String, (Enumeration)null, false, true, "A description of the function.");
            ParameterList = new RowsetDefinition.Column("PARAMETER_LIST", RowsetDefinition.Type.String, (Enumeration)null, false, true, "A comma delimited list of parameters.");
            ReturnType = new RowsetDefinition.Column("RETURN_TYPE", RowsetDefinition.Type.Integer, (Enumeration)null, false, false, "The VARTYPE of the return data type of the function.");
            Origin = new RowsetDefinition.Column("ORIGIN", RowsetDefinition.Type.Integer, (Enumeration)null, true, false, "The origin of the function:  1 for MDX functions.  2 for user-defined functions.");
            InterfaceName = new RowsetDefinition.Column("INTERFACE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The name of the interface for user-defined functions");
            LibraryName = new RowsetDefinition.Column("LIBRARY_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the type library for user-defined functions. NULL for MDX functions.");
            Caption = new RowsetDefinition.Column("CAPTION", RowsetDefinition.Type.String, (Enumeration)null, false, true, "The display caption for the function.");
        }

        public static enum VarType {
            Empty("Uninitialized (default)"),
            Null("Contains no valid data"),
            Integer("Integer subtype"),
            Long("Long subtype"),
            Single("Single subtype"),
            Double("Double subtype"),
            Currency("Currency subtype"),
            Date("Date subtype"),
            String("String subtype"),
            Object("Object subtype"),
            Error("Error subtype"),
            Boolean("Boolean subtype"),
            Variant("Variant subtype"),
            DataObject("DataObject subtype"),
            Decimal("Decimal subtype"),
            Byte("Byte subtype"),
            Array("Array subtype");

            public static RowsetDefinition.MdschemaFunctionsRowset.VarType forCategory(int category) {
                switch(category) {
                    case 0:
                        return Empty;
                    case 1:
                        return Array;
                    case 2:
                    case 3:
                    case 4:
                    case 6:
                    case 8:
                    case 10:
                    case 12:
                    case 13:
                        return Variant;
                    case 5:
                        return Boolean;
                    case 7:
                        return Double;
                    case 9:
                    case 11:
                    case 64:
                        return String;
                    case 14:
                    case 16:
                    case 17:
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                    case 39:
                    case 40:
                    case 41:
                    case 42:
                    case 43:
                    case 44:
                    case 45:
                    case 46:
                    case 47:
                    case 48:
                    case 49:
                    case 50:
                    case 51:
                    case 52:
                    case 53:
                    case 54:
                    case 55:
                    case 56:
                    case 57:
                    case 58:
                    case 59:
                    case 60:
                    case 61:
                    case 62:
                    case 63:
                    default:
                        return Empty;
                    case 15:
                    case 31:
                        return Integer;
                    case 18:
                        return Date;
                }
            }

            private VarType(String description) {
                Util.discard(description);
            }
        }
    }

    static class MdschemaMeasuregroupDimensionsRowset extends Rowset {
        private final Functor1<Boolean, Catalog> catalogNameCond;
        private final Functor1<Boolean, Schema> schemaNameCond;
        private final Functor1<Boolean, Cube> cubeNameCond;
        private final Functor1<Boolean, Dimension> dimensionUnameCond;
        private static final RowsetDefinition.Column CatalogName;
        private static final RowsetDefinition.Column SchemaName;
        private static final RowsetDefinition.Column CubeName;
        private static final RowsetDefinition.Column MeasuregroupName;
        private static final RowsetDefinition.Column MeasuregroupCardinality;
        private static final RowsetDefinition.Column DimensionUniqueName;
        private static final RowsetDefinition.Column DimensionCardinality;
        private static final RowsetDefinition.Column DimensionIsVisible;
        private static final RowsetDefinition.Column DimensionIsFactDimension;
        private static final RowsetDefinition.Column DimensionPath;
        private static final RowsetDefinition.Column DimensionGranularity;

        MdschemaMeasuregroupDimensionsRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.MDSCHEMA_MEASUREGROUP_DIMENSIONS, request, handler);
            this.catalogNameCond = this.makeCondition(RowsetDefinition.CATALOG_NAME_GETTER, CatalogName);
            this.schemaNameCond = this.makeCondition(RowsetDefinition.SCHEMA_NAME_GETTER, SchemaName);
            this.cubeNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, CubeName);
            this.dimensionUnameCond = this.makeCondition(RowsetDefinition.ELEMENT_UNAME_GETTER, DimensionUniqueName);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException, SQLException {
            Iterator var4 = RowsetDefinition.catIter(connection, this.catNameCond(), this.catalogNameCond).iterator();

            while(var4.hasNext()) {
                Catalog catalog = (Catalog)var4.next();
                this.populateCatalog(connection, catalog, rows);
            }

        }

        protected void populateCatalog(OlapConnection connection, Catalog catalog, List<Row> rows) throws XmlaException, SQLException {
            Iterator var4 = Util.filter(catalog.getSchemas(), new Functor1[]{this.schemaNameCond}).iterator();

            while(var4.hasNext()) {
                Schema schema = (Schema)var4.next();
                Iterator var6 = RowsetDefinition.filteredCubes(schema, this.cubeNameCond).iterator();

                while(var6.hasNext()) {
                    Cube cube = (Cube)var6.next();
                    this.populateCube(connection, catalog, cube, rows);
                }
            }

        }

        protected void populateCube(OlapConnection connection, Catalog catalog, Cube cube, List<Row> rows) throws XmlaException, SQLException {
            Iterator var5 = Util.filter(cube.getDimensions(), new Functor1[]{this.dimensionUnameCond}).iterator();

            while(var5.hasNext()) {
                Dimension dimension = (Dimension)var5.next();
                this.populateMeasuregroupDimension(connection, catalog, cube, dimension, rows);
            }

        }

        protected void populateMeasuregroupDimension(OlapConnection connection, Catalog catalog, Cube cube, Dimension dimension, List<Row> rows) throws XmlaException, SQLException {
            String desc = dimension.getDescription();
            if (desc == null) {
                desc = cube.getName() + " Cube - " + dimension.getName() + " Dimension";
            }

            Row row = new Row();
            row.set(CatalogName.name, catalog.getName());
            row.set(SchemaName.name, cube.getSchema().getName());
            row.set(CubeName.name, cube.getName());
            row.set(MeasuregroupName.name, cube.getName());
            row.set(MeasuregroupCardinality.name, "ONE");
            row.set(DimensionUniqueName.name, dimension.getUniqueName());
            row.set(DimensionCardinality.name, "MANY");
            row.set(DimensionIsVisible.name, dimension.isVisible());
            row.set(DimensionIsFactDimension.name, "0");
            row.set(DimensionPath.name, "");
            row.set(DimensionGranularity.name, "");
            this.addRow(row, rows);
        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            CatalogName = new RowsetDefinition.Column("CATALOG_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the database.");
            SchemaName = new RowsetDefinition.Column("SCHEMA_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "Not supported.");
            CubeName = new RowsetDefinition.Column("CUBE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The name of the cube.");
            MeasuregroupName = new RowsetDefinition.Column("MEASUREGROUP_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the measure group.");
            MeasuregroupCardinality = new RowsetDefinition.Column("MEASUREGROUP_CARDINALITY", RowsetDefinition.Type.String, (Enumeration)null, false, true, "The number of instances a measure in the measure group can have for a single dimension member. Possible values include:\nONE\nMANY");
            DimensionUniqueName = new RowsetDefinition.Column("DIMENSION_UNIQUE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The unique name of the dimension.");
            DimensionCardinality = new RowsetDefinition.Column("DIMENSION_CARDINALITY", RowsetDefinition.Type.String, (Enumeration)null, false, true, "The number of instances a dimension member can have for a single instance of a measure group measure.\nPossible values include:\nONE\nMANY");
            DimensionIsVisible = new RowsetDefinition.Column("DIMENSION_IS_VISIBLE", RowsetDefinition.Type.Boolean, (Enumeration)null, false, true, "A Boolean that indicates whether hieararchies in the dimension are visible.\nReturns TRUE if one or more hierarchies in the dimension is visible; otherwise, FALSE.");
            DimensionIsFactDimension = new RowsetDefinition.Column("DIMENSION_IS_FACT_DIMENSION", RowsetDefinition.Type.Boolean, (Enumeration)null, false, true, "");
            DimensionPath = new RowsetDefinition.Column("DIMENSION_PATH", RowsetDefinition.Type.String, (Enumeration)null, false, true, "A list of dimensions for the reference dimension.");
            DimensionGranularity = new RowsetDefinition.Column("DIMENSION_GRANULARITY", RowsetDefinition.Type.String, (Enumeration)null, false, true, "The unique name of the granularity hierarchy.");
        }
    }

    static class MdschemaDimensionsRowset extends Rowset {
        private final Functor1<Boolean, Catalog> catalogNameCond;
        private final Functor1<Boolean, Schema> schemaNameCond;
        private final Functor1<Boolean, Cube> cubeNameCond;
        private final Functor1<Boolean, Dimension> dimensionUnameCond;
        private final Functor1<Boolean, Dimension> dimensionNameCond;
        public static final int MD_DIMTYPE_OTHER = 3;
        public static final int MD_DIMTYPE_MEASURE = 2;
        public static final int MD_DIMTYPE_TIME = 1;
        private static final RowsetDefinition.Column CatalogName;
        private static final RowsetDefinition.Column SchemaName;
        private static final RowsetDefinition.Column CubeName;
        private static final RowsetDefinition.Column DimensionName;
        private static final RowsetDefinition.Column DimensionUniqueName;
        private static final RowsetDefinition.Column DimensionGuid;
        private static final RowsetDefinition.Column DimensionCaption;
        private static final RowsetDefinition.Column DimensionOrdinal;
        private static final RowsetDefinition.Column DimensionType;
        private static final RowsetDefinition.Column DimensionCardinality;
        private static final RowsetDefinition.Column DefaultHierarchy;
        private static final RowsetDefinition.Column Description;
        private static final RowsetDefinition.Column IsVirtual;
        private static final RowsetDefinition.Column IsReadWrite;
        private static final RowsetDefinition.Column DimensionUniqueSettings;
        private static final RowsetDefinition.Column DimensionMasterUniqueName;
        private static final RowsetDefinition.Column DimensionIsVisible;
        private static final RowsetDefinition.Column Hierarchies;

        MdschemaDimensionsRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.MDSCHEMA_DIMENSIONS, request, handler);
            this.catalogNameCond = this.makeCondition(RowsetDefinition.CATALOG_NAME_GETTER, CatalogName);
            this.schemaNameCond = this.makeCondition(RowsetDefinition.SCHEMA_NAME_GETTER, SchemaName);
            this.cubeNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, CubeName);
            this.dimensionUnameCond = this.makeCondition(RowsetDefinition.ELEMENT_UNAME_GETTER, DimensionUniqueName);
            this.dimensionNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, DimensionName);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException, SQLException {
            Iterator var4 = RowsetDefinition.catIter(connection, this.catNameCond(), this.catalogNameCond).iterator();

            while(var4.hasNext()) {
                Catalog catalog = (Catalog)var4.next();
                this.populateCatalog(connection, catalog, rows);
            }

        }

        protected void populateCatalog(OlapConnection connection, Catalog catalog, List<Row> rows) throws XmlaException, SQLException {
            Iterator var4 = Util.filter(catalog.getSchemas(), new Functor1[]{this.schemaNameCond}).iterator();

            while(var4.hasNext()) {
                Schema schema = (Schema)var4.next();
                Iterator var6 = RowsetDefinition.filteredCubes(schema, this.cubeNameCond).iterator();

                while(var6.hasNext()) {
                    Cube cube = (Cube)var6.next();
                    this.populateCube(connection, catalog, cube, rows);
                }
            }

        }

        protected void populateCube(OlapConnection connection, Catalog catalog, Cube cube, List<Row> rows) throws XmlaException, SQLException {
            Iterator var5 = Util.filter(cube.getDimensions(), new Functor1[]{this.dimensionNameCond, this.dimensionUnameCond}).iterator();

            while(var5.hasNext()) {
                Dimension dimension = (Dimension)var5.next();
                if (dimension.isVisible()) {
                    this.populateDimension(connection, catalog, cube, dimension, rows);
                }
            }

        }

        protected void populateDimension(OlapConnection connection, Catalog catalog, Cube cube, Dimension dimension, List<Row> rows) throws XmlaException, SQLException {
            String desc = dimension.getDescription();
            if (desc == null) {
                desc = cube.getName() + " Cube - " + dimension.getName() + " Dimension";
            }

            Row row = new Row();
            row.set(CatalogName.name, catalog.getName());
            row.set(SchemaName.name, cube.getSchema().getName());
            row.set(CubeName.name, cube.getName());
            row.set(DimensionName.name, dimension.getName());
            row.set(DimensionUniqueName.name, dimension.getUniqueName());
            row.set(DimensionCaption.name, dimension.getCaption());
            row.set(DimensionOrdinal.name, cube.getDimensions().indexOf(dimension));
            row.set(DimensionType.name, RowsetDefinition.getDimensionType(dimension));
            Hierarchy firstHierarchy = (Hierarchy)dimension.getHierarchies().get(0);
            NamedList<Level> levels = firstHierarchy.getLevels();
            Level lastLevel = (Level)levels.get(levels.size() - 1);
            int n = XmlaHandler.getExtra(connection).getLevelCardinality(lastLevel);
            row.set(DimensionCardinality.name, n + 1);
            row.set(DefaultHierarchy.name, firstHierarchy.getUniqueName());
            row.set(Description.name, desc);
            row.set(IsVirtual.name, false);
            row.set(IsReadWrite.name, false);
            row.set(DimensionUniqueSettings.name, 0);
            row.set(DimensionIsVisible.name, dimension.isVisible());
            if (this.deep) {
                row.set(Hierarchies.name, new RowsetDefinition.MdschemaHierarchiesRowset(RowsetDefinition.wrapRequest(this.request, Olap4jUtil.mapOf(RowsetDefinition.MdschemaHierarchiesRowset.CatalogName, catalog.getName(), new Object[]{RowsetDefinition.MdschemaHierarchiesRowset.SchemaName, cube.getSchema().getName(), RowsetDefinition.MdschemaHierarchiesRowset.CubeName, cube.getName(), RowsetDefinition.MdschemaHierarchiesRowset.DimensionUniqueName, dimension.getUniqueName()})), this.handler));
            }

            this.addRow(row, rows);
        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            CatalogName = new RowsetDefinition.Column("CATALOG_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the database.");
            SchemaName = new RowsetDefinition.Column("SCHEMA_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "Not supported.");
            CubeName = new RowsetDefinition.Column("CUBE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The name of the cube.");
            DimensionName = new RowsetDefinition.Column("DIMENSION_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The name of the dimension.");
            DimensionUniqueName = new RowsetDefinition.Column("DIMENSION_UNIQUE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The unique name of the dimension.");
            DimensionGuid = new RowsetDefinition.Column("DIMENSION_GUID", RowsetDefinition.Type.UUID, (Enumeration)null, false, true, "Not supported.");
            DimensionCaption = new RowsetDefinition.Column("DIMENSION_CAPTION", RowsetDefinition.Type.String, (Enumeration)null, false, false, "The caption of the dimension.");
            DimensionOrdinal = new RowsetDefinition.Column("DIMENSION_ORDINAL", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, false, false, "The position of the dimension within the cube.");
            DimensionType = new RowsetDefinition.Column("DIMENSION_TYPE", RowsetDefinition.Type.Short, (Enumeration)null, false, false, "The type of the dimension.");
            DimensionCardinality = new RowsetDefinition.Column("DIMENSION_CARDINALITY", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, false, false, "The number of members in the key attribute.");
            DefaultHierarchy = new RowsetDefinition.Column("DEFAULT_HIERARCHY", RowsetDefinition.Type.String, (Enumeration)null, false, false, "A hierarchy from the dimension. Preserved for backwards compatibility.");
            Description = new RowsetDefinition.Column("DESCRIPTION", RowsetDefinition.Type.String, (Enumeration)null, false, true, "A user-friendly description of the dimension.");
            IsVirtual = new RowsetDefinition.Column("IS_VIRTUAL", RowsetDefinition.Type.Boolean, (Enumeration)null, false, true, "Always FALSE.");
            IsReadWrite = new RowsetDefinition.Column("IS_READWRITE", RowsetDefinition.Type.Boolean, (Enumeration)null, false, true, "A Boolean that indicates whether the dimension is write-enabled.");
            DimensionUniqueSettings = new RowsetDefinition.Column("DIMENSION_UNIQUE_SETTINGS", RowsetDefinition.Type.Integer, (Enumeration)null, false, true, "A bitmap that specifies which columns contain unique values if the dimension contains only members with unique names.");
            DimensionMasterUniqueName = new RowsetDefinition.Column("DIMENSION_MASTER_UNIQUE_NAME", RowsetDefinition.Type.String, (Enumeration)null, false, true, "Always NULL.");
            DimensionIsVisible = new RowsetDefinition.Column("DIMENSION_IS_VISIBLE", RowsetDefinition.Type.Boolean, (Enumeration)null, false, true, "Always TRUE.");
            Hierarchies = new RowsetDefinition.Column("HIERARCHIES", RowsetDefinition.Type.Rowset, (Enumeration)null, false, true, "Hierarchies in this dimension.");
        }
    }

    public static class MdschemaCubesRowset extends Rowset {
        private final Functor1<Boolean, Catalog> catalogNameCond;
        private final Functor1<Boolean, Schema> schemaNameCond;
        private final Functor1<Boolean, Cube> cubeNameCond;
        public static final String MD_CUBTYPE_CUBE = "CUBE";
        public static final String MD_CUBTYPE_VIRTUAL_CUBE = "VIRTUAL CUBE";
        private static final RowsetDefinition.Column CatalogName;
        private static final RowsetDefinition.Column SchemaName;
        private static final RowsetDefinition.Column CubeName;
        private static final RowsetDefinition.Column CubeType;
        private static final RowsetDefinition.Column BaseCubeName;
        private static final RowsetDefinition.Column CubeGuid;
        private static final RowsetDefinition.Column CreatedOn;
        private static final RowsetDefinition.Column LastSchemaUpdate;
        private static final RowsetDefinition.Column SchemaUpdatedBy;
        private static final RowsetDefinition.Column LastDataUpdate;
        private static final RowsetDefinition.Column DataUpdatedBy;
        private static final RowsetDefinition.Column IsDrillthroughEnabled;
        private static final RowsetDefinition.Column IsWriteEnabled;
        private static final RowsetDefinition.Column IsLinkable;
        private static final RowsetDefinition.Column IsSqlEnabled;
        private static final RowsetDefinition.Column CubeCaption;
        private static final RowsetDefinition.Column Description;
        private static final RowsetDefinition.Column Dimensions;
        private static final RowsetDefinition.Column Sets;
        private static final RowsetDefinition.Column Measures;
        private static final RowsetDefinition.Column CubeSource;

        MdschemaCubesRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.MDSCHEMA_CUBES, request, handler);
            this.catalogNameCond = this.makeCondition(RowsetDefinition.CATALOG_NAME_GETTER, CatalogName);
            this.schemaNameCond = this.makeCondition(RowsetDefinition.SCHEMA_NAME_GETTER, SchemaName);
            this.cubeNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, CubeName);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException, SQLException {
            Iterator var4 = RowsetDefinition.catIter(connection, this.catNameCond(), this.catalogNameCond).iterator();

            while(var4.hasNext()) {
                Catalog catalog = (Catalog)var4.next();
                Iterator var6 = Util.filter(catalog.getSchemas(), new Functor1[]{this.schemaNameCond}).iterator();

                while(var6.hasNext()) {
                    Schema schema = (Schema)var6.next();
                    Iterator var8 = Util.filter(RowsetDefinition.sortedCubes(schema), new Functor1[]{this.cubeNameCond}).iterator();

                    while(var8.hasNext()) {
                        Cube cube = (Cube)var8.next();
                        if (cube.isVisible()) {
                            String desc = cube.getDescription();
                            if (desc == null) {
                                desc = catalog.getName() + " Schema - " + cube.getName() + " Cube";
                            }

                            Row row = new Row();
                            row.set(CatalogName.name, catalog.getName());
                            row.set(SchemaName.name, schema.getName());
                            row.set(CubeName.name, cube.getName());
                            XmlaExtra extra = XmlaHandler.getExtra(connection);
                            row.set(CubeType.name, extra.getCubeType(cube));
                            row.set(IsDrillthroughEnabled.name, true);
                            row.set(IsWriteEnabled.name, false);
                            row.set(IsLinkable.name, false);
                            row.set(IsSqlEnabled.name, false);
                            row.set(CubeCaption.name, cube.getCaption());
                            row.set(Description.name, desc);
                            row.set(CubeSource.name, 1);
                            Format formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            String formattedDate = formatter.format(extra.getSchemaLoadDate(schema));
                            row.set(LastSchemaUpdate.name, formattedDate);
                            formattedDate = formatter.format(new Date());
                            row.set(LastDataUpdate.name, formattedDate);
                            if (this.deep) {
                                row.set(Dimensions.name, new RowsetDefinition.MdschemaDimensionsRowset(RowsetDefinition.wrapRequest(this.request, Olap4jUtil.mapOf(RowsetDefinition.MdschemaDimensionsRowset.CatalogName, catalog.getName(), new Object[]{RowsetDefinition.MdschemaDimensionsRowset.SchemaName, schema.getName(), RowsetDefinition.MdschemaDimensionsRowset.CubeName, cube.getName()})), this.handler));
                                row.set(Sets.name, new RowsetDefinition.MdschemaSetsRowset(RowsetDefinition.wrapRequest(this.request, Olap4jUtil.mapOf(RowsetDefinition.MdschemaSetsRowset.CatalogName, catalog.getName(), new Object[]{RowsetDefinition.MdschemaSetsRowset.SchemaName, schema.getName(), RowsetDefinition.MdschemaSetsRowset.CubeName, cube.getName()})), this.handler));
                                row.set(Measures.name, new RowsetDefinition.MdschemaMeasuresRowset(RowsetDefinition.wrapRequest(this.request, Olap4jUtil.mapOf(RowsetDefinition.MdschemaMeasuresRowset.CatalogName, catalog.getName(), new Object[]{RowsetDefinition.MdschemaMeasuresRowset.SchemaName, schema.getName(), RowsetDefinition.MdschemaMeasuresRowset.CubeName, cube.getName()})), this.handler));
                            }

                            this.addRow(row, rows);
                        }
                    }
                }
            }

        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            CatalogName = new RowsetDefinition.Column("CATALOG_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the catalog to which this cube belongs.");
            SchemaName = new RowsetDefinition.Column("SCHEMA_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the schema to which this cube belongs.");
            CubeName = new RowsetDefinition.Column("CUBE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "Name of the cube.");
            CubeType = new RowsetDefinition.Column("CUBE_TYPE", RowsetDefinition.Type.String, (Enumeration)null, true, false, "Cube type.");
            BaseCubeName = new RowsetDefinition.Column("BASE_CUBE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the source cube if this cube is a perspective cube.");
            CubeGuid = new RowsetDefinition.Column("CUBE_GUID", RowsetDefinition.Type.UUID, (Enumeration)null, false, true, "Cube type.");
            CreatedOn = new RowsetDefinition.Column("CREATED_ON", RowsetDefinition.Type.DateTime, (Enumeration)null, false, true, "Date and time of cube creation.");
            LastSchemaUpdate = new RowsetDefinition.Column("LAST_SCHEMA_UPDATE", RowsetDefinition.Type.DateTime, (Enumeration)null, false, true, "Date and time of last schema update.");
            SchemaUpdatedBy = new RowsetDefinition.Column("SCHEMA_UPDATED_BY", RowsetDefinition.Type.String, (Enumeration)null, false, true, "User ID of the person who last updated the schema.");
            LastDataUpdate = new RowsetDefinition.Column("LAST_DATA_UPDATE", RowsetDefinition.Type.DateTime, (Enumeration)null, false, true, "Date and time of last data update.");
            DataUpdatedBy = new RowsetDefinition.Column("DATA_UPDATED_BY", RowsetDefinition.Type.String, (Enumeration)null, false, true, "User ID of the person who last updated the data.");
            IsDrillthroughEnabled = new RowsetDefinition.Column("IS_DRILLTHROUGH_ENABLED", RowsetDefinition.Type.Boolean, (Enumeration)null, false, false, "Describes whether DRILLTHROUGH can be performed on the members of a cube");
            IsWriteEnabled = new RowsetDefinition.Column("IS_WRITE_ENABLED", RowsetDefinition.Type.Boolean, (Enumeration)null, false, false, "Describes whether a cube is write-enabled");
            IsLinkable = new RowsetDefinition.Column("IS_LINKABLE", RowsetDefinition.Type.Boolean, (Enumeration)null, false, false, "Describes whether a cube can be used in a linked cube");
            IsSqlEnabled = new RowsetDefinition.Column("IS_SQL_ENABLED", RowsetDefinition.Type.Boolean, (Enumeration)null, false, false, "Describes whether or not SQL can be used on the cube");
            CubeCaption = new RowsetDefinition.Column("CUBE_CAPTION", RowsetDefinition.Type.String, (Enumeration)null, false, true, "The caption of the cube.");
            Description = new RowsetDefinition.Column("DESCRIPTION", RowsetDefinition.Type.String, (Enumeration)null, false, true, "A user-friendly description of the dimension.");
            Dimensions = new RowsetDefinition.Column("DIMENSIONS", RowsetDefinition.Type.Rowset, (Enumeration)null, false, true, "Dimensions in this cube.");
            Sets = new RowsetDefinition.Column("SETS", RowsetDefinition.Type.Rowset, (Enumeration)null, false, true, "Sets in this cube.");
            Measures = new RowsetDefinition.Column("MEASURES", RowsetDefinition.Type.Rowset, (Enumeration)null, false, true, "Measures in this cube.");
            CubeSource = new RowsetDefinition.Column("CUBE_SOURCE", RowsetDefinition.Type.Integer, (Enumeration)null, true, true, "A bitmap with one of these valid values:\n\n1 CUBE\n\n2 DIMENSION");
        }
    }

    static class MdschemaActionsRowset extends Rowset {
        private static final RowsetDefinition.Column CatalogName;
        private static final RowsetDefinition.Column SchemaName;
        private static final RowsetDefinition.Column CubeName;
        private static final RowsetDefinition.Column ActionName;
        private static final RowsetDefinition.Column Coordinate;
        private static final RowsetDefinition.Column CoordinateType;

        MdschemaActionsRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.MDSCHEMA_ACTIONS, request, handler);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException {
        }

        static {
            CatalogName = new RowsetDefinition.Column("CATALOG_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the catalog to which this action belongs.");
            SchemaName = new RowsetDefinition.Column("SCHEMA_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the schema to which this action belongs.");
            CubeName = new RowsetDefinition.Column("CUBE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The name of the cube to which this action belongs.");
            ActionName = new RowsetDefinition.Column("ACTION_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The name of the action.");
            Coordinate = new RowsetDefinition.Column("COORDINATE", RowsetDefinition.Type.String, (Enumeration)null, true, false, (String)null);
            CoordinateType = new RowsetDefinition.Column("COORDINATE_TYPE", RowsetDefinition.Type.Integer, (Enumeration)null, true, false, (String)null);
        }
    }

    static class DbschemaTablesInfoRowset extends Rowset {
        private static final RowsetDefinition.Column TableCatalog;
        private static final RowsetDefinition.Column TableSchema;
        private static final RowsetDefinition.Column TableName;
        private static final RowsetDefinition.Column TableType;
        private static final RowsetDefinition.Column TableGuid;
        private static final RowsetDefinition.Column Bookmarks;
        private static final RowsetDefinition.Column BookmarkType;
        private static final RowsetDefinition.Column BookmarkDataType;
        private static final RowsetDefinition.Column BookmarkMaximumLength;
        private static final RowsetDefinition.Column BookmarkInformation;
        private static final RowsetDefinition.Column TableVersion;
        private static final RowsetDefinition.Column Cardinality;
        private static final RowsetDefinition.Column Description;
        private static final RowsetDefinition.Column TablePropId;

        DbschemaTablesInfoRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.DBSCHEMA_TABLES_INFO, request, handler);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException, OlapException {
            Iterator var4 = RowsetDefinition.catIter(connection, this.catNameCond()).iterator();

            while(var4.hasNext()) {
                Catalog catalog = (Catalog)var4.next();
                Schema schema = (Schema)catalog.getSchemas().get(0);
                Iterator var7 = RowsetDefinition.sortedCubes(schema).iterator();

                while(var7.hasNext()) {
                    Cube cube = (Cube)var7.next();
                    String cubeName = cube.getName();
                    String desc = cube.getDescription();
                    if (desc == null) {
                        desc = catalog.getName() + " - " + cubeName + " Cube";
                    }

                    int cardinality = 1000000;
                    String version = "null";
                    Row row = new Row();
                    row.set(TableCatalog.name, catalog.getName());
                    row.set(TableName.name, cubeName);
                    row.set(TableType.name, "TABLE");
                    row.set(Bookmarks.name, false);
                    row.set(TableVersion.name, version);
                    row.set(Cardinality.name, cardinality);
                    row.set(Description.name, desc);
                    this.addRow(row, rows);
                }
            }

        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            TableCatalog = new RowsetDefinition.Column("TABLE_CATALOG", RowsetDefinition.Type.String, (Enumeration)null, true, true, "Catalog name. NULL if the provider does not support catalogs.");
            TableSchema = new RowsetDefinition.Column("TABLE_SCHEMA", RowsetDefinition.Type.String, (Enumeration)null, true, true, "Unqualified schema name. NULL if the provider does not support schemas.");
            TableName = new RowsetDefinition.Column("TABLE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "Table name.");
            TableType = new RowsetDefinition.Column("TABLE_TYPE", RowsetDefinition.Type.String, (Enumeration)null, true, false, "Table type. One of the following or a provider-specific value: ALIAS, TABLE, SYNONYM, SYSTEM TABLE, VIEW, GLOBAL TEMPORARY, LOCAL TEMPORARY, EXTERNAL TABLE, SYSTEM VIEW");
            TableGuid = new RowsetDefinition.Column("TABLE_GUID", RowsetDefinition.Type.UUID, (Enumeration)null, false, true, "GUID that uniquely identifies the table. Providers that do not use GUIDs to identify tables should return NULL in this column.");
            Bookmarks = new RowsetDefinition.Column("BOOKMARKS", RowsetDefinition.Type.Boolean, (Enumeration)null, false, false, "Whether this table supports bookmarks. Allways is false.");
            BookmarkType = new RowsetDefinition.Column("BOOKMARK_TYPE", RowsetDefinition.Type.Integer, (Enumeration)null, false, true, "Default bookmark type supported on this table.");
            BookmarkDataType = new RowsetDefinition.Column("BOOKMARK_DATATYPE", RowsetDefinition.Type.UnsignedShort, (Enumeration)null, false, true, "The indicator of the bookmark's native data type.");
            BookmarkMaximumLength = new RowsetDefinition.Column("BOOKMARK_MAXIMUM_LENGTH", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, false, true, "Maximum length of the bookmark in bytes.");
            BookmarkInformation = new RowsetDefinition.Column("BOOKMARK_INFORMATION", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, false, true, "A bitmask specifying additional information about bookmarks over the rowset. ");
            TableVersion = new RowsetDefinition.Column("TABLE_VERSION", RowsetDefinition.Type.Long, (Enumeration)null, false, true, "Version number for this table or NULL if the provider does not support returning table version information.");
            Cardinality = new RowsetDefinition.Column("CARDINALITY", RowsetDefinition.Type.UnsignedLong, (Enumeration)null, false, false, "Cardinality (number of rows) of the table.");
            Description = new RowsetDefinition.Column("DESCRIPTION", RowsetDefinition.Type.String, (Enumeration)null, false, true, "Human-readable description of the table.");
            TablePropId = new RowsetDefinition.Column("TABLE_PROPID", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, false, true, "Property ID of the table. Return null.");
        }
    }

    static class DbschemaSourceTablesRowset extends Rowset {
        private static final RowsetDefinition.Column TableCatalog;
        private static final RowsetDefinition.Column TableSchema;
        private static final RowsetDefinition.Column TableName;
        private static final RowsetDefinition.Column TableType;

        DbschemaSourceTablesRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.DBSCHEMA_SOURCE_TABLES, request, handler);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException, OlapException, SQLException {
            RolapConnection rolapConnection = ((MondrianOlap4jConnection)connection).getMondrianConnection();
            Connection sqlConnection = rolapConnection.getDataSource().getConnection();
            DatabaseMetaData databaseMetaData = sqlConnection.getMetaData();
            String[] tableTypeRestriction = null;
            List<String> tableTypeRestrictionList = this.getRestriction(TableType);
            if (tableTypeRestrictionList != null) {
                tableTypeRestriction = (String[])tableTypeRestrictionList.toArray(new String[0]);
            }

            ResultSet resultSet = databaseMetaData.getTables((String)null, (String)null, (String)null, tableTypeRestriction);

            while(resultSet.next()) {
                String tableCatalog = resultSet.getString("TABLE_CAT");
                String tableSchema = resultSet.getString("TABLE_SCHEM");
                String tableName = resultSet.getString("TABLE_NAME");
                String tableType = resultSet.getString("TABLE_TYPE");
                Row row = new Row();
                row.set(TableCatalog.name, tableCatalog);
                row.set(TableSchema.name, tableSchema);
                row.set(TableName.name, tableName);
                row.set(TableType.name, tableType);
                this.addRow(row, rows);
            }

        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            TableCatalog = new RowsetDefinition.Column("TABLE_CATALOG", RowsetDefinition.Type.String, (Enumeration)null, true, true, "Catalog name. NULL if the provider does not support catalogs.");
            TableSchema = new RowsetDefinition.Column("TABLE_SCHEMA", RowsetDefinition.Type.String, (Enumeration)null, true, true, "Unqualified schema name. NULL if the provider does not support schemas.");
            TableName = new RowsetDefinition.Column("TABLE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "Table name.");
            TableType = new RowsetDefinition.Column("TABLE_TYPE", RowsetDefinition.Type.StringSometimesArray, (Enumeration)null, true, false, "Table type. One of the following or a provider-specific value: ALIAS, TABLE, SYNONYM, SYSTEM TABLE, VIEW, GLOBAL TEMPORARY, LOCAL TEMPORARY, EXTERNAL TABLE, SYSTEM VIEW");
        }
    }

    static class DbschemaTablesRowset extends Rowset {
        private final Functor1<Boolean, Catalog> tableCatalogCond;
        private final Functor1<Boolean, Cube> tableNameCond;
        private final Functor1<Boolean, String> tableTypeCond;
        private static final RowsetDefinition.Column TableCatalog;
        private static final RowsetDefinition.Column TableSchema;
        private static final RowsetDefinition.Column TableName;
        private static final RowsetDefinition.Column TableType;
        private static final RowsetDefinition.Column TableGuid;
        private static final RowsetDefinition.Column Description;
        private static final RowsetDefinition.Column TablePropId;
        private static final RowsetDefinition.Column DateCreated;
        private static final RowsetDefinition.Column DateModified;

        DbschemaTablesRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.DBSCHEMA_TABLES, request, handler);
            this.tableCatalogCond = this.makeCondition(RowsetDefinition.CATALOG_NAME_GETTER, TableCatalog);
            this.tableNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, TableName);
            this.tableTypeCond = this.makeCondition(TableType);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException, OlapException {
            Iterator var4 = RowsetDefinition.catIter(connection, this.catNameCond(), this.tableCatalogCond).iterator();

            label53:
            while(var4.hasNext()) {
                Catalog catalog = (Catalog)var4.next();
                Schema schema = (Schema)catalog.getSchemas().get(0);
                Iterator var8 = Util.filter(RowsetDefinition.sortedCubes(schema), new Functor1[]{this.tableNameCond}).iterator();

                label51:
                while(true) {
                    Cube cube;
                    do {
                        if (!var8.hasNext()) {
                            continue label53;
                        }

                        cube = (Cube)var8.next();
                        String desc = cube.getDescription();
                        if (desc == null) {
                            desc = catalog.getName() + " - " + cube.getName() + " Cube";
                        }

                        if ((Boolean)this.tableTypeCond.apply("TABLE")) {
                            Row row = new Row();
                            row.set(TableCatalog.name, catalog.getName());
                            row.set(TableName.name, cube.getName());
                            row.set(TableType.name, "TABLE");
                            row.set(Description.name, desc);
                            this.addRow(row, rows);
                        }
                    } while(!(Boolean)this.tableTypeCond.apply("SYSTEM TABLE"));

                    Iterator var11 = cube.getDimensions().iterator();

                    while(true) {
                        Dimension dimension;
                        do {
                            if (!var11.hasNext()) {
                                continue label51;
                            }

                            dimension = (Dimension)var11.next();
                        } while(dimension.getDimensionType() == org.olap4j.metadata.Dimension.Type.MEASURE);

                        Iterator var13 = dimension.getHierarchies().iterator();

                        while(var13.hasNext()) {
                            Hierarchy hierarchy = (Hierarchy)var13.next();
                            this.populateHierarchy(cube, hierarchy, rows);
                        }
                    }
                }
            }

        }

        private void populateHierarchy(Cube cube, Hierarchy hierarchy, List<Row> rows) {
            Iterator var4 = hierarchy.getLevels().iterator();

            while(var4.hasNext()) {
                Level level = (Level)var4.next();
                this.populateLevel(cube, hierarchy, level, rows);
            }

        }

        private void populateLevel(Cube cube, Hierarchy hierarchy, Level level, List<Row> rows) {
            String schemaName = cube.getSchema().getName();
            String cubeName = cube.getName();
            String hierarchyName = RowsetDefinition.getHierarchyName(hierarchy);
            String levelName = level.getName();
            String tableName = cubeName + ':' + hierarchyName + ':' + levelName;
            String desc = level.getDescription();
            if (desc == null) {
                desc = schemaName + " - " + cubeName + " Cube - " + hierarchyName + " Hierarchy - " + levelName + " Level";
            }

            Row row = new Row();
            row.set(TableCatalog.name, schemaName);
            row.set(TableName.name, tableName);
            row.set(TableType.name, "SYSTEM TABLE");
            row.set(Description.name, desc);
            this.addRow(row, rows);
        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            TableCatalog = new RowsetDefinition.Column("TABLE_CATALOG", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The name of the catalog to which this object belongs.");
            TableSchema = new RowsetDefinition.Column("TABLE_SCHEMA", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the cube to which this object belongs.");
            TableName = new RowsetDefinition.Column("TABLE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The name of the object, if TABLE_TYPE is TABLE.");
            TableType = new RowsetDefinition.Column("TABLE_TYPE", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The type of the table. TABLE indicates the object is a measure group. SYSTEM TABLE indicates the object is a dimension.");
            TableGuid = new RowsetDefinition.Column("TABLE_GUID", RowsetDefinition.Type.UUID, (Enumeration)null, false, true, "Not supported.");
            Description = new RowsetDefinition.Column("DESCRIPTION", RowsetDefinition.Type.String, (Enumeration)null, false, true, "A human-readable description of the object.");
            TablePropId = new RowsetDefinition.Column("TABLE_PROPID", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, false, true, "Not supported.");
            DateCreated = new RowsetDefinition.Column("DATE_CREATED", RowsetDefinition.Type.DateTime, (Enumeration)null, false, true, "Not supported.");
            DateModified = new RowsetDefinition.Column("DATE_MODIFIED", RowsetDefinition.Type.DateTime, (Enumeration)null, false, true, "The date the object was last modified.");
        }
    }

    static class DbschemaSchemataRowset extends Rowset {
        private final Functor1<Boolean, Catalog> catalogNameCond;
        private static final RowsetDefinition.Column CatalogName;
        private static final RowsetDefinition.Column SchemaName;
        private static final RowsetDefinition.Column SchemaOwner;

        DbschemaSchemataRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.DBSCHEMA_SCHEMATA, request, handler);
            this.catalogNameCond = this.makeCondition(RowsetDefinition.CATALOG_NAME_GETTER, CatalogName);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException, OlapException {
            Iterator var4 = RowsetDefinition.catIter(connection, this.catalogNameCond, this.catNameCond()).iterator();

            while(var4.hasNext()) {
                Catalog catalog = (Catalog)var4.next();
                Iterator var6 = catalog.getSchemas().iterator();

                while(var6.hasNext()) {
                    Schema schema = (Schema)var6.next();
                    Row row = new Row();
                    row.set(CatalogName.name, catalog.getName());
                    row.set(SchemaName.name, schema.getName());
                    row.set(SchemaOwner.name, "");
                    this.addRow(row, rows);
                }
            }

        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            CatalogName = new RowsetDefinition.Column("CATALOG_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The provider-specific data type name.");
            SchemaName = new RowsetDefinition.Column("SCHEMA_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The indicator of the data type.");
            SchemaOwner = new RowsetDefinition.Column("SCHEMA_OWNER", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The length of a non-numeric column. If the data type is numeric, this is the upper bound on the maximum precision of the data type.");
        }
    }

    static class DbschemaProviderTypesRowset extends Rowset {
        private final Functor1<Boolean, Integer> dataTypeCond;
        private static final RowsetDefinition.Column TypeName;
        private static final RowsetDefinition.Column DataType;
        private static final RowsetDefinition.Column ColumnSize;
        private static final RowsetDefinition.Column LiteralPrefix;
        private static final RowsetDefinition.Column LiteralSuffix;
        private static final RowsetDefinition.Column IsNullable;
        private static final RowsetDefinition.Column CaseSensitive;
        private static final RowsetDefinition.Column Searchable;
        private static final RowsetDefinition.Column UnsignedAttribute;
        private static final RowsetDefinition.Column FixedPrecScale;
        private static final RowsetDefinition.Column AutoUniqueValue;
        private static final RowsetDefinition.Column IsLong;
        private static final RowsetDefinition.Column BestMatch;

        DbschemaProviderTypesRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.DBSCHEMA_PROVIDER_TYPES, request, handler);
            this.dataTypeCond = this.makeCondition(DataType);
        }

        protected boolean needConnection() {
            return false;
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException {
            Integer dt = DBType.I4.xmlaOrdinal();
            Row row;
            if ((Boolean)this.dataTypeCond.apply(dt)) {
                row = new Row();
                row.set(TypeName.name, DBType.I4.userName);
                row.set(DataType.name, dt);
                row.set(ColumnSize.name, 8);
                row.set(IsNullable.name, true);
                row.set(Searchable.name, (Object)null);
                row.set(UnsignedAttribute.name, false);
                row.set(FixedPrecScale.name, false);
                row.set(AutoUniqueValue.name, false);
                row.set(IsLong.name, false);
                row.set(BestMatch.name, true);
                this.addRow(row, rows);
            }

            dt = DBType.R8.xmlaOrdinal();
            if ((Boolean)this.dataTypeCond.apply(dt)) {
                row = new Row();
                row.set(TypeName.name, DBType.R8.userName);
                row.set(DataType.name, dt);
                row.set(ColumnSize.name, 16);
                row.set(IsNullable.name, true);
                row.set(Searchable.name, (Object)null);
                row.set(UnsignedAttribute.name, false);
                row.set(FixedPrecScale.name, false);
                row.set(AutoUniqueValue.name, false);
                row.set(IsLong.name, false);
                row.set(BestMatch.name, true);
                this.addRow(row, rows);
            }

            dt = DBType.CY.xmlaOrdinal();
            if ((Boolean)this.dataTypeCond.apply(dt)) {
                row = new Row();
                row.set(TypeName.name, DBType.CY.userName);
                row.set(DataType.name, dt);
                row.set(ColumnSize.name, 8);
                row.set(IsNullable.name, true);
                row.set(Searchable.name, (Object)null);
                row.set(UnsignedAttribute.name, false);
                row.set(FixedPrecScale.name, false);
                row.set(AutoUniqueValue.name, false);
                row.set(IsLong.name, false);
                row.set(BestMatch.name, true);
                this.addRow(row, rows);
            }

            dt = DBType.BOOL.xmlaOrdinal();
            if ((Boolean)this.dataTypeCond.apply(dt)) {
                row = new Row();
                row.set(TypeName.name, DBType.BOOL.userName);
                row.set(DataType.name, dt);
                row.set(ColumnSize.name, 1);
                row.set(IsNullable.name, true);
                row.set(Searchable.name, (Object)null);
                row.set(UnsignedAttribute.name, false);
                row.set(FixedPrecScale.name, false);
                row.set(AutoUniqueValue.name, false);
                row.set(IsLong.name, false);
                row.set(BestMatch.name, true);
                this.addRow(row, rows);
            }

            dt = DBType.I8.xmlaOrdinal();
            if ((Boolean)this.dataTypeCond.apply(dt)) {
                row = new Row();
                row.set(TypeName.name, DBType.I8.userName);
                row.set(DataType.name, dt);
                row.set(ColumnSize.name, 16);
                row.set(IsNullable.name, true);
                row.set(Searchable.name, (Object)null);
                row.set(UnsignedAttribute.name, false);
                row.set(FixedPrecScale.name, false);
                row.set(AutoUniqueValue.name, false);
                row.set(IsLong.name, false);
                row.set(BestMatch.name, true);
                this.addRow(row, rows);
            }

            dt = DBType.WSTR.xmlaOrdinal();
            if ((Boolean)this.dataTypeCond.apply(dt)) {
                row = new Row();
                row.set(TypeName.name, DBType.WSTR.userName);
                row.set(DataType.name, dt);
                row.set(ColumnSize.name, 255);
                row.set(LiteralPrefix.name, "\"");
                row.set(LiteralSuffix.name, "\"");
                row.set(IsNullable.name, true);
                row.set(CaseSensitive.name, false);
                row.set(Searchable.name, (Object)null);
                row.set(FixedPrecScale.name, false);
                row.set(AutoUniqueValue.name, false);
                row.set(IsLong.name, false);
                row.set(BestMatch.name, true);
                this.addRow(row, rows);
            }

        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            TypeName = new RowsetDefinition.Column("TYPE_NAME", RowsetDefinition.Type.String, (Enumeration)null, false, false, "The provider-specific data type name.");
            DataType = new RowsetDefinition.Column("DATA_TYPE", RowsetDefinition.Type.UnsignedShort, (Enumeration)null, true, false, "The indicator of the data type.");
            ColumnSize = new RowsetDefinition.Column("COLUMN_SIZE", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, false, false, "The length of a non-numeric column. If the data type is numeric, this is the upper bound on the maximum precision of the data type.");
            LiteralPrefix = new RowsetDefinition.Column("LITERAL_PREFIX", RowsetDefinition.Type.String, (Enumeration)null, false, true, "The character or characters used to prefix a literal of this type in a text command.");
            LiteralSuffix = new RowsetDefinition.Column("LITERAL_SUFFIX", RowsetDefinition.Type.String, (Enumeration)null, false, true, "The character or characters used to suffix a literal of this type in a text command.");
            IsNullable = new RowsetDefinition.Column("IS_NULLABLE", RowsetDefinition.Type.Boolean, (Enumeration)null, false, true, "A Boolean that indicates whether the data type is nullable. NULL-- indicates that it is not known whether the data type is nullable.");
            CaseSensitive = new RowsetDefinition.Column("CASE_SENSITIVE", RowsetDefinition.Type.Boolean, (Enumeration)null, false, true, "A Boolean that indicates whether the data type is a characters type and case-sensitive.");
            Searchable = new RowsetDefinition.Column("SEARCHABLE", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, false, true, "An integer indicating how the data type can be used in searches if the provider supports ICommandText; otherwise, NULL.");
            UnsignedAttribute = new RowsetDefinition.Column("UNSIGNED_ATTRIBUTE", RowsetDefinition.Type.Boolean, (Enumeration)null, false, true, "A Boolean that indicates whether the data type is unsigned.");
            FixedPrecScale = new RowsetDefinition.Column("FIXED_PREC_SCALE", RowsetDefinition.Type.Boolean, (Enumeration)null, false, true, "A Boolean that indicates whether the data type has a fixed precision and scale.");
            AutoUniqueValue = new RowsetDefinition.Column("AUTO_UNIQUE_VALUE", RowsetDefinition.Type.Boolean, (Enumeration)null, false, true, "A Boolean that indicates whether the data type is autoincrementing.");
            IsLong = new RowsetDefinition.Column("IS_LONG", RowsetDefinition.Type.Boolean, (Enumeration)null, false, true, "A Boolean that indicates whether the data type is a binary large object (BLOB) and has very long data.");
            BestMatch = new RowsetDefinition.Column("BEST_MATCH", RowsetDefinition.Type.Boolean, (Enumeration)null, true, true, "A Boolean that indicates whether the data type is a best match.");
        }
    }

    static class DbschemaColumnsRowset extends Rowset {
        private final Functor1<Boolean, Catalog> tableCatalogCond;
        private final Functor1<Boolean, Cube> tableNameCond;
        private final Functor1<Boolean, String> columnNameCond;
        private static final RowsetDefinition.Column TableCatalog;
        private static final RowsetDefinition.Column TableSchema;
        private static final RowsetDefinition.Column TableName;
        private static final RowsetDefinition.Column ColumnName;
        private static final RowsetDefinition.Column OrdinalPosition;
        private static final RowsetDefinition.Column ColumnHasDefault;
        private static final RowsetDefinition.Column ColumnFlags;
        private static final RowsetDefinition.Column IsNullable;
        private static final RowsetDefinition.Column DataType;
        private static final RowsetDefinition.Column CharacterMaximumLength;
        private static final RowsetDefinition.Column CharacterOctetLength;
        private static final RowsetDefinition.Column NumericPrecision;
        private static final RowsetDefinition.Column NumericScale;

        DbschemaColumnsRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.DBSCHEMA_COLUMNS, request, handler);
            this.tableCatalogCond = this.makeCondition(RowsetDefinition.CATALOG_NAME_GETTER, TableCatalog);
            this.tableNameCond = this.makeCondition(RowsetDefinition.ELEMENT_NAME_GETTER, TableName);
            this.columnNameCond = this.makeCondition(ColumnName);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException, OlapException {
            Iterator var4 = RowsetDefinition.catIter(connection, this.catNameCond(), this.tableCatalogCond).iterator();

            while(var4.hasNext()) {
                Catalog catalog = (Catalog)var4.next();
                Schema schema = (Schema)catalog.getSchemas().get(0);
                boolean emitInvisibleMembers = XmlaUtil.shouldEmitInvisibleMembers(this.request);
                int ordinalPosition = 1;
                Iterator var10 = Util.filter(RowsetDefinition.sortedCubes(schema), new Functor1[]{this.tableNameCond}).iterator();

                while(var10.hasNext()) {
                    Cube cube = (Cube)var10.next();
                    Iterator var12 = cube.getDimensions().iterator();

                    while(var12.hasNext()) {
                        Dimension dimension = (Dimension)var12.next();

                        Hierarchy hierarchy;
                        for(Iterator var14 = dimension.getHierarchies().iterator(); var14.hasNext(); ordinalPosition = this.populateHierarchy(cube, hierarchy, ordinalPosition, rows)) {
                            hierarchy = (Hierarchy)var14.next();
                        }
                    }

                    List<Measure> rms = cube.getMeasures();

                    for(int k = 1; k < rms.size(); ++k) {
                        Measure member = (Measure)rms.get(k);
                        Boolean visible = (Boolean)member.getPropertyValue(StandardMemberProperty.$visible);
                        if (visible == null) {
                            visible = true;
                        }

                        if (emitInvisibleMembers || visible) {
                            String memberName = member.getName();
                            String columnName = "Measures:" + memberName;
                            if ((Boolean)this.columnNameCond.apply(columnName)) {
                                Row row = new Row();
                                row.set(TableCatalog.name, catalog.getName());
                                row.set(TableName.name, cube.getName());
                                row.set(ColumnName.name, columnName);
                                row.set(OrdinalPosition.name, ordinalPosition++);
                                row.set(ColumnHasDefault.name, false);
                                row.set(ColumnFlags.name, 0);
                                row.set(IsNullable.name, false);
                                row.set(DataType.name, DBType.R8.xmlaOrdinal());
                                row.set(NumericPrecision.name, 16);
                                row.set(NumericScale.name, 255);
                                this.addRow(row, rows);
                            }
                        }
                    }
                }
            }

        }

        private int populateHierarchy(Cube cube, Hierarchy hierarchy, int ordinalPosition, List<Row> rows) {
            String schemaName = cube.getSchema().getName();
            String cubeName = cube.getName();
            String hierarchyName = hierarchy.getName();
            if (hierarchy.hasAll()) {
                Row row = new Row();
                row.set(TableCatalog.name, schemaName);
                row.set(TableName.name, cubeName);
                row.set(ColumnName.name, hierarchyName + ":(All)!NAME");
                row.set(OrdinalPosition.name, ordinalPosition++);
                row.set(ColumnHasDefault.name, false);
                row.set(ColumnFlags.name, 0);
                row.set(IsNullable.name, false);
                row.set(DataType.name, DBType.WSTR.xmlaOrdinal());
                row.set(CharacterMaximumLength.name, 0);
                row.set(CharacterOctetLength.name, 0);
                this.addRow(row, rows);
                row = new Row();
                row.set(TableCatalog.name, schemaName);
                row.set(TableName.name, cubeName);
                row.set(ColumnName.name, hierarchyName + ":(All)!UNIQUE_NAME");
                row.set(OrdinalPosition.name, ordinalPosition++);
                row.set(ColumnHasDefault.name, false);
                row.set(ColumnFlags.name, 0);
                row.set(IsNullable.name, false);
                row.set(DataType.name, DBType.WSTR.xmlaOrdinal());
                row.set(CharacterMaximumLength.name, 0);
                row.set(CharacterOctetLength.name, 0);
                this.addRow(row, rows);
            }

            Level level;
            for(Iterator var10 = hierarchy.getLevels().iterator(); var10.hasNext(); ordinalPosition = this.populateLevel(cube, hierarchy, level, ordinalPosition, rows)) {
                level = (Level)var10.next();
            }

            return ordinalPosition;
        }

        private int populateLevel(Cube cube, Hierarchy hierarchy, Level level, int ordinalPosition, List<Row> rows) {
            String schemaName = cube.getSchema().getName();
            String cubeName = cube.getName();
            String hierarchyName = hierarchy.getName();
            String levelName = level.getName();
            Row row = new Row();
            row.set(TableCatalog.name, schemaName);
            row.set(TableName.name, cubeName);
            row.set(ColumnName.name, hierarchyName + ':' + levelName + "!NAME");
            row.set(OrdinalPosition.name, ordinalPosition++);
            row.set(ColumnHasDefault.name, false);
            row.set(ColumnFlags.name, 0);
            row.set(IsNullable.name, false);
            row.set(DataType.name, DBType.WSTR.xmlaOrdinal());
            row.set(CharacterMaximumLength.name, 0);
            row.set(CharacterOctetLength.name, 0);
            this.addRow(row, rows);
            row = new Row();
            row.set(TableCatalog.name, schemaName);
            row.set(TableName.name, cubeName);
            row.set(ColumnName.name, hierarchyName + ':' + levelName + "!UNIQUE_NAME");
            row.set(OrdinalPosition.name, ordinalPosition++);
            row.set(ColumnHasDefault.name, false);
            row.set(ColumnFlags.name, 0);
            row.set(IsNullable.name, false);
            row.set(DataType.name, DBType.WSTR.xmlaOrdinal());
            row.set(CharacterMaximumLength.name, 0);
            row.set(CharacterOctetLength.name, 0);
            this.addRow(row, rows);
            NamedList<Property> props = level.getProperties();

            for(Iterator var12 = props.iterator(); var12.hasNext(); this.addRow(row, rows)) {
                Property prop = (Property)var12.next();
                String propName = prop.getName();
                row = new Row();
                row.set(TableCatalog.name, schemaName);
                row.set(TableName.name, cubeName);
                row.set(ColumnName.name, hierarchyName + ':' + levelName + '!' + propName);
                row.set(OrdinalPosition.name, ordinalPosition++);
                row.set(ColumnHasDefault.name, false);
                row.set(ColumnFlags.name, 0);
                row.set(IsNullable.name, false);
                DBType dbType = RowsetDefinition.getDBTypeFromProperty(prop);
                row.set(DataType.name, dbType.xmlaOrdinal());
                switch(prop.getDatatype()) {
                    case STRING:
                        row.set(CharacterMaximumLength.name, 0);
                        row.set(CharacterOctetLength.name, 0);
                        break;
                    case INTEGER:
                    case UNSIGNED_INTEGER:
                    case DOUBLE:
                        row.set(NumericPrecision.name, 16);
                        row.set(NumericScale.name, 255);
                        break;
                    case BOOLEAN:
                        row.set(NumericPrecision.name, 255);
                        row.set(NumericScale.name, 255);
                        break;
                    default:
                        row.set(CharacterMaximumLength.name, 0);
                        row.set(CharacterOctetLength.name, 0);
                }
            }

            return ordinalPosition;
        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            TableCatalog = new RowsetDefinition.Column("TABLE_CATALOG", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The name of the Database.");
            TableSchema = new RowsetDefinition.Column("TABLE_SCHEMA", RowsetDefinition.Type.String, (Enumeration)null, true, true, (String)null);
            TableName = new RowsetDefinition.Column("TABLE_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The name of the cube.");
            ColumnName = new RowsetDefinition.Column("COLUMN_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The name of the attribute hierarchy or measure.");
            OrdinalPosition = new RowsetDefinition.Column("ORDINAL_POSITION", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, false, false, "The position of the column, beginning with 1.");
            ColumnHasDefault = new RowsetDefinition.Column("COLUMN_HAS_DEFAULT", RowsetDefinition.Type.Boolean, (Enumeration)null, false, true, "Not supported.");
            ColumnFlags = new RowsetDefinition.Column("COLUMN_FLAGS", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, false, false, "A DBCOLUMNFLAGS bitmask indicating column properties.");
            IsNullable = new RowsetDefinition.Column("IS_NULLABLE", RowsetDefinition.Type.Boolean, (Enumeration)null, false, false, "Always returns false.");
            DataType = new RowsetDefinition.Column("DATA_TYPE", RowsetDefinition.Type.UnsignedShort, (Enumeration)null, false, false, "The data type of the column. Returns a string for dimension columns and a variant for measures.");
            CharacterMaximumLength = new RowsetDefinition.Column("CHARACTER_MAXIMUM_LENGTH", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, false, true, "The maximum possible length of a value within the column.");
            CharacterOctetLength = new RowsetDefinition.Column("CHARACTER_OCTET_LENGTH", RowsetDefinition.Type.UnsignedInteger, (Enumeration)null, false, true, "The maximum possible length of a value within the column, in bytes, for character or binary columns.");
            NumericPrecision = new RowsetDefinition.Column("NUMERIC_PRECISION", RowsetDefinition.Type.UnsignedShort, (Enumeration)null, false, true, "The maximum precision of the column for numeric data types other than DBTYPE_VARNUMERIC.");
            NumericScale = new RowsetDefinition.Column("NUMERIC_SCALE", RowsetDefinition.Type.Short, (Enumeration)null, false, true, "The number of digits to the right of the decimal point for DBTYPE_DECIMAL, DBTYPE_NUMERIC, DBTYPE_VARNUMERIC. Otherwise, this is NULL.");
        }
    }

    static class DbschemaCatalogsRowset extends Rowset {
        private final Functor1<Boolean, Catalog> catalogNameCond;
        private static final RowsetDefinition.Column CatalogName;
        private static final RowsetDefinition.Column Description;
        private static final RowsetDefinition.Column Roles;
        private static final RowsetDefinition.Column DateModified;

        DbschemaCatalogsRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.DBSCHEMA_CATALOGS, request, handler);
            this.catalogNameCond = this.makeCondition(RowsetDefinition.CATALOG_NAME_GETTER, CatalogName);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException, SQLException {
            Iterator var4 = RowsetDefinition.catIter(connection, this.catalogNameCond).iterator();

            while(var4.hasNext()) {
                Catalog catalog = (Catalog)var4.next();
                Iterator var6 = catalog.getSchemas().iterator();

                while(var6.hasNext()) {
                    Schema schema = (Schema)var6.next();
                    Row row = new Row();
                    row.set(CatalogName.name, catalog.getName());
                    row.set(Description.name, "No description available");
                    StringBuilder buf = new StringBuilder(100);
                    List<String> roleNames = XmlaHandler.getExtra(connection).getSchemaRoleNames(schema);
                    RowsetDefinition.serialize(buf, roleNames);
                    row.set(Roles.name, buf.toString());
                    Format formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    String formattedDate = formatter.format(new Date());
                    row.set(DateModified.name, formattedDate);
                    this.addRow(row, rows);
                }
            }

        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            CatalogName = new RowsetDefinition.Column("CATALOG_NAME", RowsetDefinition.Type.String, (Enumeration)null, true, false, "Catalog name. Cannot be NULL.");
            Description = new RowsetDefinition.Column("DESCRIPTION", RowsetDefinition.Type.String, (Enumeration)null, false, false, "Human-readable description of the catalog.");
            Roles = new RowsetDefinition.Column("ROLES", RowsetDefinition.Type.String, (Enumeration)null, false, false, "A comma delimited list of roles to which the current user belongs. An asterisk (*) is included as a role if the current user is a server or database administrator. Username is appended to ROLES if one of the roles uses dynamic security.");
            DateModified = new RowsetDefinition.Column("DATE_MODIFIED", RowsetDefinition.Type.DateTime, (Enumeration)null, false, true, "The date that the catalog was last modified.");
        }
    }

    static class DiscoverXmlMetadataRowset extends Rowset {
        private final Functor1<Boolean, Catalog> catalogNameCond;
        private static final RowsetDefinition.Column METADATA;
        private static final RowsetDefinition.Column ObjectType;
        private static final RowsetDefinition.Column DatabaseID;

        DiscoverXmlMetadataRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.DISCOVER_XML_METADATA, request, handler);
            this.catalogNameCond = this.makeCondition(RowsetDefinition.CATALOG_NAME_GETTER, DatabaseID);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException {
            String objectType = this.getRestrictionValueAsString(ObjectType);
            if (objectType != null && objectType.equals("Database")) {
                try {
                    MondrianServer mondrianServer = MondrianServer.forConnection(((MondrianOlap4jConnection)connection).getMondrianConnection());
                    Repository repository = mondrianServer.getRepository();
                    if (repository instanceof FileRepository) {
                        FileRepository fileRepository = (FileRepository)repository;
                        String repositoryContent = fileRepository.getContent();
                        Row row = new Row();
                        row.set(METADATA.name, repositoryContent);
                        this.addRow(row, rows);
                    }
                } catch (OlapException var15) {
                    throw new RuntimeException(var15);
                }
            } else if (objectType != null && objectType.equals("Schema")) {
                try {
                    RolapConnection rolapConnection = ((MondrianOlap4jConnection)connection).getMondrianConnection();
                    MondrianServer mondrianServer = MondrianServer.forConnection(rolapConnection);
                    Repository repository = mondrianServer.getRepository();
                    Iterator var8 = RowsetDefinition.catIter(connection, this.catalogNameCond).iterator();
                    if (var8.hasNext()) {
                        Catalog catalog = (Catalog)var8.next();
                        Map<String, RolapSchema> schemas = repository.getRolapSchemas(rolapConnection, catalog.getDatabase().getName(), catalog.getName());
                        String catalogStr = null;
                        if (schemas != null && schemas.size() > 0) {
                            String catalogUrl = ((RolapSchema)((Entry)schemas.entrySet().iterator().next()).getValue()).getInternalConnection().getCatalogName();
                            catalogStr = Util.readVirtualFileAsString(catalogUrl);
                        }

                        Row row = new Row();
                        row.set(METADATA.name, catalogStr);
                        this.addRow(row, rows);
                    }
                } catch (OlapException var13) {
                    throw new RuntimeException(var13);
                } catch (IOException var14) {
                    throw new RuntimeException(var14);
                }
            }

        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            METADATA = new RowsetDefinition.Column("METADATA", RowsetDefinition.Type.String, (Enumeration)null, false, false, "An XML document that describes the object requested by the restriction.");
            ObjectType = new RowsetDefinition.Column("ObjectType", RowsetDefinition.Type.String, (Enumeration)null, true, true, "Can be Database or Schema. If Databes - return Datasources.xml. If Schema returns schema xml file by DatabaseId.");
            DatabaseID = new RowsetDefinition.Column("DatabaseID", RowsetDefinition.Type.String, (Enumeration)null, true, true, (String)null);
        }
    }

    static class DiscoverLiteralsRowset extends Rowset {
        private static final RowsetDefinition.Column LiteralName;
        private static final RowsetDefinition.Column LiteralValue;
        private static final RowsetDefinition.Column LiteralInvalidChars;
        private static final RowsetDefinition.Column LiteralInvalidStartingChars;
        private static final RowsetDefinition.Column LiteralMaxLength;
        private static final RowsetDefinition.Column LiteralNameEnumValue;

        DiscoverLiteralsRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.DISCOVER_LITERALS, request, handler);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException {
            this.populate(Literal.class, rows, new Comparator<Literal>() {
                public int compare(Literal o1, Literal o2) {
                    return o1.name().compareTo(o2.name());
                }
            });
        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            LiteralName = new RowsetDefinition.Column("LiteralName", RowsetDefinition.Type.StringSometimesArray, (Enumeration)null, true, false, "The name of the literal described in the row.\nExample: DBLITERAL_LIKE_PERCENT");
            LiteralValue = new RowsetDefinition.Column("LiteralValue", RowsetDefinition.Type.String, (Enumeration)null, false, true, "Contains the actual literal value.\nExample, if LiteralName is DBLITERAL_LIKE_PERCENT and the percent character (%) is used to match zero or more characters in a LIKE clause, this column's value would be \"%\".");
            LiteralInvalidChars = new RowsetDefinition.Column("LiteralInvalidChars", RowsetDefinition.Type.String, (Enumeration)null, false, true, "The characters, in the literal, that are not valid.\nFor example, if table names can contain anything other than a numeric character, this string would be \"0123456789\".");
            LiteralInvalidStartingChars = new RowsetDefinition.Column("LiteralInvalidStartingChars", RowsetDefinition.Type.String, (Enumeration)null, false, true, "The characters that are not valid as the first character of the literal. If the literal can start with any valid character, this is null.");
            LiteralMaxLength = new RowsetDefinition.Column("LiteralMaxLength", RowsetDefinition.Type.Integer, (Enumeration)null, false, true, "The maximum number of characters in the literal. If there is no maximum or the maximum is unknown, the value is ?1.");
            LiteralNameEnumValue = new RowsetDefinition.Column("LiteralNameEnumValue", RowsetDefinition.Type.Integer, (Enumeration)null, false, true, "");
        }
    }

    static class DiscoverKeywordsRowset extends Rowset {
        private static final RowsetDefinition.Column Keyword;

        DiscoverKeywordsRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.DISCOVER_KEYWORDS, request, handler);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException {
            MondrianServer mondrianServer = MondrianServer.forId((String)null);
            Iterator var5 = mondrianServer.getKeywords().iterator();

            while(var5.hasNext()) {
                String keyword = (String)var5.next();
                Row row = new Row();
                row.set(Keyword.name, keyword);
                this.addRow(row, rows);
            }

        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            Keyword = new RowsetDefinition.Column("Keyword", RowsetDefinition.Type.StringSometimesArray, (Enumeration)null, true, false, "A list of all the keywords reserved by a provider.\nExample: AND");
        }
    }

    static class DiscoverEnumeratorsRowset extends Rowset {
        private static final RowsetDefinition.Column EnumName;
        private static final RowsetDefinition.Column EnumDescription;
        private static final RowsetDefinition.Column EnumType;
        private static final RowsetDefinition.Column ElementName;
        private static final RowsetDefinition.Column ElementDescription;
        private static final RowsetDefinition.Column ElementValue;

        DiscoverEnumeratorsRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.DISCOVER_ENUMERATORS, request, handler);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException {
            List<Enumeration> enumerators = getEnumerators();
            Iterator var5 = enumerators.iterator();

            while(var5.hasNext()) {
                Enumeration enumerator = (Enumeration)var5.next();
                List<? extends Enum> values = enumerator.getValues();

                Row row;
                for(Iterator var8 = values.iterator(); var8.hasNext(); this.addRow(row, rows)) {
                    Enum<?> value = (Enum)var8.next();
                    row = new Row();
                    row.set(EnumName.name, enumerator.name);
                    row.set(EnumDescription.name, enumerator.description);
                    row.set(EnumType.name, "string");
                    String name = value instanceof XmlaConstant ? ((XmlaConstant)value).xmlaName() : value.name();
                    row.set(ElementName.name, name);
                    String description = value instanceof XmlaConstant ? ((XmlaConstant)value).getDescription() : (value instanceof EnumWithDesc ? ((EnumWithDesc)value).getDescription() : null);
                    if (description != null) {
                        row.set(ElementDescription.name, description);
                    }

                    switch(enumerator.type) {
                        case String:
                        case StringArray:
                            continue;
                    }

                    int ordinal = value instanceof XmlaConstant && ((XmlaConstant)value).xmlaOrdinal() != -1 ? ((XmlaConstant)value).xmlaOrdinal() : value.ordinal();
                    row.set(ElementValue.name, ordinal);
                }
            }

        }

        private static List<Enumeration> getEnumerators() {
            SortedSet<Enumeration> enumeratorSet = new TreeSet(new Comparator<Enumeration>() {
                public int compare(Enumeration o1, Enumeration o2) {
                    return o1.name.compareTo(o2.name);
                }
            });
            RowsetDefinition[] var1 = (RowsetDefinition[])RowsetDefinition.class.getEnumConstants();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                RowsetDefinition rowsetDefinition = var1[var3];
                RowsetDefinition.Column[] var5 = rowsetDefinition.columnDefinitions;
                int var6 = var5.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    RowsetDefinition.Column column = var5[var7];
                    if (column.enumeration != null) {
                        enumeratorSet.add(column.enumeration);
                    }
                }
            }

            return new ArrayList(enumeratorSet);
        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            EnumName = new RowsetDefinition.Column("EnumName", RowsetDefinition.Type.StringArray, (Enumeration)null, true, false, "The name of the enumerator that contains a set of values.");
            EnumDescription = new RowsetDefinition.Column("EnumDescription", RowsetDefinition.Type.String, (Enumeration)null, false, true, "A localizable description of the enumerator.");
            EnumType = new RowsetDefinition.Column("EnumType", RowsetDefinition.Type.String, (Enumeration)null, false, false, "The data type of the Enum values.");
            ElementName = new RowsetDefinition.Column("ElementName", RowsetDefinition.Type.String, (Enumeration)null, false, false, "The name of one of the value elements in the enumerator set.\nExample: TDP");
            ElementDescription = new RowsetDefinition.Column("ElementDescription", RowsetDefinition.Type.String, (Enumeration)null, false, true, "A localizable description of the element (optional).");
            ElementValue = new RowsetDefinition.Column("ElementValue", RowsetDefinition.Type.String, (Enumeration)null, false, true, "The value of the element.\nExample: 01");
        }
    }

    static class DiscoverPropertiesRowset extends Rowset {
        private final Functor1<Boolean, PropertyDefinition> propNameCond;
        private String properetyCatalog = null;
        private static final RowsetDefinition.Column PropertyName;
        private static final RowsetDefinition.Column PropertyDescription;
        private static final RowsetDefinition.Column PropertyType;
        private static final RowsetDefinition.Column PropertyAccessType;
        private static final RowsetDefinition.Column IsRequired;
        private static final RowsetDefinition.Column Value;

        DiscoverPropertiesRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.DISCOVER_PROPERTIES, request, handler);
            this.propNameCond = this.makeCondition(RowsetDefinition.PROPDEF_NAME_GETTER, PropertyName);
            if (request.getProperties().containsKey(PropertyDefinition.Catalog.name())) {
                this.properetyCatalog = (String)request.getProperties().get(PropertyDefinition.Catalog.name());
            }

        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException, OlapException {
            PropertyDefinition[] var4 = (PropertyDefinition[])PropertyDefinition.class.getEnumConstants();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                PropertyDefinition propertyDefinition = var4[var6];
                if ((Boolean)this.propNameCond.apply(propertyDefinition)) {
                    Row row = new Row();
                    row.set(PropertyName.name, propertyDefinition.name());
                    row.set(PropertyDescription.name, propertyDefinition.description);
                    row.set(PropertyType.name, propertyDefinition.type.getName());
                    row.set(PropertyAccessType.name, propertyDefinition.access);
                    row.set(IsRequired.name, false);
                    String propertyValue = "";
                    if (propertyDefinition.name().equals(PropertyDefinition.Catalog.name())) {
                        List<Catalog> catalogs = connection.getOlapCatalogs();
                        if (this.properetyCatalog != null) {
                            Iterator var11 = catalogs.iterator();

                            while(var11.hasNext()) {
                                Catalog currentCatalog = (Catalog)var11.next();
                                if (currentCatalog.getName().equals(this.properetyCatalog)) {
                                    propertyValue = currentCatalog.getName();
                                    break;
                                }
                            }
                        } else if (catalogs.size() > 0) {
                            propertyValue = ((Catalog)catalogs.get(0)).getName();
                        }
                    } else {
                        propertyValue = propertyDefinition.value;
                    }

                    row.set(Value.name, propertyValue);
                    this.addRow(row, rows);
                }
            }

        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            PropertyName = new RowsetDefinition.Column("PropertyName", RowsetDefinition.Type.StringSometimesArray, (Enumeration)null, true, false, "The name of the property.");
            PropertyDescription = new RowsetDefinition.Column("PropertyDescription", RowsetDefinition.Type.String, (Enumeration)null, false, false, "A localizable text description of the property.");
            PropertyType = new RowsetDefinition.Column("PropertyType", RowsetDefinition.Type.String, (Enumeration)null, false, false, "The XML data type of the property.");
            PropertyAccessType = new RowsetDefinition.Column("PropertyAccessType", RowsetDefinition.Type.EnumString, Enumeration.ACCESS, false, false, "Access for the property. The value can be Read, Write, or ReadWrite.");
            IsRequired = new RowsetDefinition.Column("IsRequired", RowsetDefinition.Type.Boolean, (Enumeration)null, false, false, "True if a property is required, false if it is not required.");
            Value = new RowsetDefinition.Column("Value", RowsetDefinition.Type.String, (Enumeration)null, false, false, "The current value of the property.");
        }
    }

    static class DiscoverSchemaRowsetsRowset extends Rowset {
        private static final RowsetDefinition.Column SchemaName;
        private static final RowsetDefinition.Column SchemaGuid;
        private static final RowsetDefinition.Column RestrictionsMask;
        private static final RowsetDefinition.Column Restrictions;
        private static final RowsetDefinition.Column Description;

        public DiscoverSchemaRowsetsRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.DISCOVER_SCHEMA_ROWSETS, request, handler);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException {
            RowsetDefinition[] rowsetDefinitions = (RowsetDefinition[])((RowsetDefinition[])RowsetDefinition.class.getEnumConstants()).clone();
            Arrays.sort(rowsetDefinitions, new Comparator<RowsetDefinition>() {
                public int compare(RowsetDefinition o1, RowsetDefinition o2) {
                    return o1.name().compareTo(o2.name());
                }
            });
            List<String> restrictionSchemaNames = null;
            if (this.restrictions.containsKey(SchemaName.name)) {
                restrictionSchemaNames = (List)this.restrictions.get(SchemaName.name);
            }

            RowsetDefinition[] var6 = rowsetDefinitions;
            int var7 = rowsetDefinitions.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                RowsetDefinition rowsetDefinition = var6[var8];
                if (restrictionSchemaNames == null || restrictionSchemaNames.contains(rowsetDefinition.name())) {
                    Row row = new Row();
                    row.set(SchemaName.name, rowsetDefinition.name());
                    row.set(SchemaGuid.name, rowsetDefinition.schemaGuid);
                    row.set(Restrictions.name, this.getRestrictions(rowsetDefinition));
                    String desc = rowsetDefinition.getDescription();
                    row.set(Description.name, desc == null ? "" : desc);
                    this.addRow(row, rows);
                }
            }

        }

        private List<XmlElement> getRestrictions(RowsetDefinition rowsetDefinition) {
            List<XmlElement> restrictionList = new ArrayList();
            RowsetDefinition.Column[] columns = (RowsetDefinition.Column[])rowsetDefinition.columnDefinitions.clone();
            Arrays.sort(columns, new Comparator<RowsetDefinition.Column>() {
                public int compare(RowsetDefinition.Column c1, RowsetDefinition.Column c2) {
                    return Integer.compare(c1.restrictionOrder, c2.restrictionOrder);
                }
            });
            RowsetDefinition.Column[] var4 = columns;
            int var5 = columns.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                RowsetDefinition.Column column = var4[var6];
                if (column.restriction) {
                    restrictionList.add(new XmlElement(Restrictions.name, (Object[])null, new XmlElement[]{new XmlElement("Name", (Object[])null, column.name), new XmlElement("Type", (Object[])null, column.getColumnType())}));
                }
            }

            return restrictionList;
        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            SchemaName = new RowsetDefinition.Column("SchemaName", RowsetDefinition.Type.StringArray, (Enumeration)null, true, false, "The name of the schema/request. This returns the values in the RequestTypes enumeration, plus any additional types supported by the provider. The provider defines rowset structures for the additional types");
            SchemaGuid = new RowsetDefinition.Column("SchemaGuid", RowsetDefinition.Type.UUID, (Enumeration)null, false, true, "The GUID of the schema.");
            RestrictionsMask = new RowsetDefinition.Column("RestrictionsMask", RowsetDefinition.Type.UnsignedLong, (Enumeration)null, false, true, "");
            Restrictions = new RowsetDefinition.Column("Restrictions", RowsetDefinition.Type.Array, (Enumeration)null, false, false, "An array of the restrictions suppoted by provider. An example follows this table.");
            Description = new RowsetDefinition.Column("Description", RowsetDefinition.Type.String, (Enumeration)null, false, false, "A localizable description of the schema");
        }
    }

    static class DiscoverDatasourcesRowset extends Rowset {
        private static final RowsetDefinition.Column DataSourceName;
        private static final RowsetDefinition.Column DataSourceDescription;
        private static final RowsetDefinition.Column URL;
        private static final RowsetDefinition.Column DataSourceInfo;
        private static final RowsetDefinition.Column ProviderName;
        private static final RowsetDefinition.Column ProviderType;
        private static final RowsetDefinition.Column AuthenticationMode;
        private static final RowsetDefinition.Column[] columns;

        public DiscoverDatasourcesRowset(XmlaRequest request, XmlaHandler handler) {
            super(RowsetDefinition.DISCOVER_DATASOURCES, request, handler);
        }

        public void populateImpl(XmlaResponse response, OlapConnection connection, List<Row> rows) throws XmlaException, SQLException {
            if (this.needConnection()) {
                XmlaExtra extra = XmlaHandler.getExtra(connection);
                Iterator var5 = extra.getDataSources(connection).iterator();

                while(var5.hasNext()) {
                    Map<String, Object> ds = (Map)var5.next();
                    Row row = new Row();
                    RowsetDefinition.Column[] var8 = columns;
                    int var9 = var8.length;

                    for(int var10 = 0; var10 < var9; ++var10) {
                        RowsetDefinition.Column column = var8[var10];
                        row.set(column.name, ds.get(column.name));
                    }

                    this.addRow(row, rows);
                }
            } else {
                Row row = new Row();
                Map<String, Object> map = this.handler.connectionFactory.getPreConfiguredDiscoverDatasourcesResponse();
                RowsetDefinition.Column[] var14 = columns;
                int var15 = var14.length;

                for(int var16 = 0; var16 < var15; ++var16) {
                    RowsetDefinition.Column column = var14[var16];
                    row.set(column.name, map.get(column.name));
                }

                this.addRow(row, rows);
            }

        }

        protected boolean needConnection() {
            return this.handler.connectionFactory.getPreConfiguredDiscoverDatasourcesResponse() == null;
        }

        protected void setProperty(PropertyDefinition propertyDef, String value) {
            switch(propertyDef) {
                default:
                    super.setProperty(propertyDef, value);
                case Content:
            }
        }

        static {
            DataSourceName = new RowsetDefinition.Column("DataSourceName", RowsetDefinition.Type.String, (Enumeration)null, true, false, "The name of the data source, such as FoodMart 2000.");
            DataSourceDescription = new RowsetDefinition.Column("DataSourceDescription", RowsetDefinition.Type.String, (Enumeration)null, false, true, "A description of the data source, as entered by the publisher.");
            URL = new RowsetDefinition.Column("URL", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The unique path that shows where to invoke the XML for Analysis methods for that data source.");
            DataSourceInfo = new RowsetDefinition.Column("DataSourceInfo", RowsetDefinition.Type.String, (Enumeration)null, false, true, "A string containing any additional information required to connect to the data source. This can include the Initial Catalog property or other information for the provider.\nExample: \"Provider=MSOLAP;Data Source=Local;\"");
            ProviderName = new RowsetDefinition.Column("ProviderName", RowsetDefinition.Type.String, (Enumeration)null, true, true, "The name of the provider behind the data source.\nExample: \"MSDASQL\"");
            ProviderType = new RowsetDefinition.Column("ProviderType", RowsetDefinition.Type.EnumerationArray, Enumeration.PROVIDER_TYPE, true, false, true, "The types of data supported by the provider. May include one or more of the following types. Example follows this table.\nTDP: tabular data provider.\nMDP: multidimensional data provider.\nDMP: data mining provider. A DMP provider implements the OLE DB for Data Mining specification.");
            AuthenticationMode = new RowsetDefinition.Column("AuthenticationMode", RowsetDefinition.Type.EnumString, Enumeration.AUTHENTICATION_MODE, true, false, "Specification of what type of security mode the data source uses. Values can be one of the following:\nUnauthenticated: no user ID or password needs to be sent.\nAuthenticated: User ID and Password must be included in the information required for the connection.\nIntegrated: the data source uses the underlying security to determine authorization, such as Integrated Security provided by Microsoft Internet Information Services (IIS).");
            columns = new RowsetDefinition.Column[]{DataSourceName, DataSourceDescription, URL, DataSourceInfo, ProviderName, ProviderType, AuthenticationMode};
        }
    }

    static class Column {
        static final boolean RESTRICTION = true;
        static final boolean NOT_RESTRICTION = false;
        static final boolean REQUIRED = false;
        static final boolean OPTIONAL = true;
        static final boolean ONE_MAX = false;
        static final boolean UNBOUNDED = true;
        final String name;
        final RowsetDefinition.Type type;
        final Enumeration enumeration;
        final String description;
        final boolean restriction;
        final boolean nullable;
        final boolean unbounded;
        final int restrictionOrder;

        Column(String name, RowsetDefinition.Type type, Enumeration enumeratedType, boolean restriction, boolean nullable, String description) {
            this(name, type, enumeratedType, restriction, 0, nullable, false, description);
        }

        Column(String name, RowsetDefinition.Type type, Enumeration enumeratedType, boolean restriction, int restrictionOrder, boolean nullable, String description) {
            this(name, type, enumeratedType, restriction, restrictionOrder, nullable, false, description);
        }

        Column(String name, RowsetDefinition.Type type, Enumeration enumeratedType, boolean restriction, boolean nullable, boolean unbounded, String description) {
            this(name, type, enumeratedType, restriction, 0, nullable, unbounded, description);
        }

        Column(String name, RowsetDefinition.Type type, Enumeration enumeratedType, boolean restriction, int restrictionOrder, boolean nullable, boolean unbounded, String description) {
            assert type != null;

            assert (type == RowsetDefinition.Type.Enumeration || type == RowsetDefinition.Type.EnumerationArray || type == RowsetDefinition.Type.EnumString) == (enumeratedType != null);

            assert description == null || description.indexOf(13) == -1;

            this.name = name;
            this.type = type;
            this.enumeration = enumeratedType;
            this.description = description;
            this.restriction = restriction;
            this.nullable = nullable;
            this.unbounded = unbounded;
            this.restrictionOrder = restrictionOrder;
        }

        protected Object get(Object row) {
            return this.getFromAccessor(row);
        }

        protected final Object getFromField(Object row) {
            try {
                String javaFieldName = this.name.substring(0, 1).toLowerCase() + this.name.substring(1);
                Field field = row.getClass().getField(javaFieldName);
                return field.get(row);
            } catch (NoSuchFieldException var4) {
                throw Util.newInternal(var4, "Error while accessing rowset column " + this.name);
            } catch (SecurityException var5) {
                throw Util.newInternal(var5, "Error while accessing rowset column " + this.name);
            } catch (IllegalAccessException var6) {
                throw Util.newInternal(var6, "Error while accessing rowset column " + this.name);
            }
        }

        protected final Object getFromAccessor(Object row) {
            try {
                String javaMethodName = "get" + this.name;
                java.lang.reflect.Method method = row.getClass().getMethod(javaMethodName);
                return method.invoke(row);
            } catch (SecurityException var4) {
                throw Util.newInternal(var4, "Error while accessing rowset column " + this.name);
            } catch (IllegalAccessException var5) {
                throw Util.newInternal(var5, "Error while accessing rowset column " + this.name);
            } catch (NoSuchMethodException var6) {
                throw Util.newInternal(var6, "Error while accessing rowset column " + this.name);
            } catch (InvocationTargetException var7) {
                throw Util.newInternal(var7, "Error while accessing rowset column " + this.name);
            }
        }

        public String getColumnType() {
            return this.type.isEnum() ? this.enumeration.type.columnType : this.type.columnType;
        }
    }

    static enum Type {
        String("xsd:string"),
        StringArray("xsd:string"),
        Array("xsd:string"),
        Enumeration("xsd:string"),
        EnumerationArray("xsd:string"),
        EnumString("xsd:string"),
        Boolean("xsd:boolean"),
        StringSometimesArray("xsd:string"),
        Integer("xsd:int"),
        UnsignedInteger("xsd:unsignedInt"),
        Double("xsd:double"),
        DateTime("xsd:dateTime"),
        Rowset((String)null),
        Short("xsd:short"),
        UUID("uuid"),
        UnsignedShort("xsd:unsignedShort"),
        Long("xsd:long"),
        UnsignedLong("xsd:unsignedLong");

        public final String columnType;

        private Type(String columnType) {
            this.columnType = columnType;
        }

        boolean isEnum() {
            return this == Enumeration || this == EnumerationArray || this == EnumString;
        }

        String getName() {
            return this == String ? "string" : this.name();
        }
    }
}