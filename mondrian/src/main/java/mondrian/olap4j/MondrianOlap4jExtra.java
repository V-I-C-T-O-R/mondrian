/* Decompiler 246ms, total 1926ms, lines 310 */
package mondrian.olap4j;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import mondrian.olap.Annotated;
import mondrian.olap.Annotation;
import mondrian.olap.Category;
import mondrian.olap.FunTable;
import mondrian.olap.HierarchyBase;
import mondrian.olap.MondrianServer;
import mondrian.olap.Property;
import mondrian.olap.Query;
import mondrian.olap.SchemaReader;
import mondrian.olap.Util;
import mondrian.olap.Util.Functor1;
import mondrian.olap.Util.PropertyList;
import mondrian.olap.fun.FunInfo;
import mondrian.rolap.RolapAggregator;
import mondrian.rolap.RolapConnection;
import mondrian.rolap.RolapConnectionProperties;
import mondrian.rolap.RolapCube;
import mondrian.rolap.RolapLevel;
import mondrian.rolap.RolapMemberBase;
import mondrian.rolap.RolapSchema;
import mondrian.xmla.RowsetDefinition.MdschemaFunctionsRowset.VarType;
import mondrian.xmla.XmlaHandler.XmlaExtra;
import mondrian.xmla.XmlaHandler.XmlaExtra.FunctionDefinition;
import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.OlapConnection;
import org.olap4j.OlapException;
import org.olap4j.OlapStatement;
import org.olap4j.OlapWrapper;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Member;
import org.olap4j.metadata.MetadataElement;
import org.olap4j.metadata.Schema;
import org.olap4j.metadata.Level.Type;

class MondrianOlap4jExtra implements XmlaExtra {
    static final MondrianOlap4jExtra INSTANCE = new MondrianOlap4jExtra();

    public ResultSet executeDrillthrough(OlapStatement olapStatement, String mdx, boolean advanced, String tabFields, int[] rowCountSlot) throws SQLException {
        return ((MondrianOlap4jStatement)olapStatement).executeQuery2(mdx, advanced, tabFields, rowCountSlot);
    }

    public void setPreferList(OlapConnection connection) {
        ((MondrianOlap4jConnection)connection).setPreferList(true);
    }

    public Date getSchemaLoadDate(Schema schema) {
        return ((MondrianOlap4jSchema)schema).schema.getSchemaLoadDate();
    }

    public int getLevelCardinality(Level level) throws OlapException {
        if (level instanceof MondrianOlap4jLevel) {
            MondrianOlap4jLevel olap4jLevel = (MondrianOlap4jLevel)level;
            SchemaReader schemaReader = olap4jLevel.olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData.olap4jConnection.getMondrianConnection().getSchemaReader().withLocus();
            return schemaReader.getLevelCardinality(olap4jLevel.level, true, true);
        } else {
            return level.getCardinality();
        }
    }

    public void getSchemaFunctionList(List<FunctionDefinition> funDefs, Schema schema, Functor1<Boolean, String> functionFilter) {
        FunTable funTable = ((MondrianOlap4jSchema)schema).schema.getFunTable();
        StringBuilder buf = new StringBuilder(50);
        Iterator var6 = funTable.getFunInfoList().iterator();

        while(true) {
            while(true) {
                FunInfo fi;
                Boolean passes;
                do {
                    label41:
                    do {
                        while(var6.hasNext()) {
                            fi = (FunInfo)var6.next();
                            switch(fi.getSyntax()) {
                                case Empty:
                                case Internal:
                                case Parentheses:
                                    break;
                                default:
                                    passes = (Boolean)functionFilter.apply(fi.getName());
                                    continue label41;
                            }
                        }

                        return;
                    } while(passes == null);
                } while(!passes);

                int[][] paramCategories = fi.getParameterCategories();
                int[] returnCategories = fi.getReturnCategories();
                String description = fi.getDescription();
                if (description != null) {
                    description = Util.replace(fi.getDescription(), "\r", "");
                }

                if (paramCategories != null && paramCategories.length != 0) {
                    for(int i = 0; i < paramCategories.length; ++i) {
                        int[] pc = paramCategories[i];
                        int returnCategory = returnCategories[i];
                        buf.setLength(0);

                        for(int j = 0; j < pc.length; ++j) {
                            int v = pc[j];
                            if (j > 0) {
                                buf.append(", ");
                            }

                            buf.append(Category.instance.getDescription(v & 31));
                        }

                        VarType varType = VarType.forCategory(returnCategory);
                        funDefs.add(new FunctionDefinition(fi.getName(), description, buf.toString(), varType.ordinal(), 1, "", fi.getName()));
                    }
                } else {
                    funDefs.add(new FunctionDefinition(fi.getName(), description, "(none)", 1, 1, "", fi.getName()));
                }
            }
        }
    }

    public int getHierarchyCardinality(Hierarchy hierarchy) throws OlapException {
        MondrianOlap4jHierarchy olap4jHierarchy = (MondrianOlap4jHierarchy)hierarchy;
        SchemaReader schemaReader = olap4jHierarchy.olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData.olap4jConnection.getMondrianConnection().getSchemaReader().withLocus();
        return RolapMemberBase.getHierarchyCardinality(schemaReader, olap4jHierarchy.hierarchy);
    }

    public int getHierarchyStructure(Hierarchy hierarchy) {
        MondrianOlap4jHierarchy olap4jHierarchy = (MondrianOlap4jHierarchy)hierarchy;
        return ((HierarchyBase)olap4jHierarchy.hierarchy).isRagged() ? 1 : 0;
    }

    public boolean isHierarchyParentChild(Hierarchy hierarchy) {
        Level nonAllFirstLevel = (Level)hierarchy.getLevels().get(0);
        if (nonAllFirstLevel.getLevelType() == Type.ALL) {
            nonAllFirstLevel = (Level)hierarchy.getLevels().get(1);
        }

        MondrianOlap4jLevel olap4jLevel = (MondrianOlap4jLevel)nonAllFirstLevel;
        return ((RolapLevel)olap4jLevel.level).isParentChild();
    }

    public String getMeasureDisplayFolder(Member member) {
        MondrianOlap4jMeasure olap4jMeasure = (MondrianOlap4jMeasure)member;
        return olap4jMeasure.getDisplayFolder();
    }

    public int getMeasureAggregator(Member member) {
        MondrianOlap4jMeasure olap4jMeasure = (MondrianOlap4jMeasure)member;
        Object aggProp = olap4jMeasure.member.getPropertyValue(Property.AGGREGATION_TYPE.name);
        if (aggProp == null) {
            return 127;
        } else {
            RolapAggregator agg = (RolapAggregator)aggProp;
            if (agg == RolapAggregator.Sum) {
                return 1;
            } else if (agg == RolapAggregator.Count) {
                return 2;
            } else if (agg == RolapAggregator.Min) {
                return 3;
            } else if (agg == RolapAggregator.Max) {
                return 4;
            } else {
                return agg == RolapAggregator.Avg ? 5 : 0;
            }
        }
    }

    public void checkMemberOrdinal(Member member) throws OlapException {
        if (member.getOrdinal() == -1) {
            MondrianOlap4jMember olap4jMember = (MondrianOlap4jMember)member;
            SchemaReader schemaReader = olap4jMember.olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData.olap4jConnection.getMondrianConnection().getSchemaReader().withLocus();
            RolapMemberBase.setOrdinals(schemaReader, olap4jMember.member);
        }

    }

    public boolean shouldReturnCellProperty(CellSet cellSet, org.olap4j.metadata.Property cellProperty, boolean evenEmpty) {
        MondrianOlap4jCellSet olap4jCellSet = (MondrianOlap4jCellSet)cellSet;
        Query query = olap4jCellSet.query;
        return evenEmpty && query.isCellPropertyEmpty() || query.hasCellProperty(cellProperty.getName());
    }

    public List<String> getSchemaRoleNames(Schema schema) {
        MondrianOlap4jSchema olap4jSchema = (MondrianOlap4jSchema)schema;
        return new ArrayList(((RolapSchema)olap4jSchema.schema).roleNames());
    }

    public String getSchemaId(Schema schema) {
        return ((MondrianOlap4jSchema)schema).schema.getId();
    }

    public String getCubeType(Cube cube) {
        return cube instanceof MondrianOlap4jCube && ((RolapCube)((MondrianOlap4jCube)cube).cube).isVirtual() ? "VIRTUAL CUBE" : "CUBE";
    }

    public boolean isLevelUnique(Level level) {
        MondrianOlap4jLevel olap4jLevel = (MondrianOlap4jLevel)level;
        return olap4jLevel.level instanceof RolapLevel && ((RolapLevel)olap4jLevel.level).isUnique();
    }

    public List<org.olap4j.metadata.Property> getLevelProperties(Level level) {
        MondrianOlap4jLevel olap4jLevel = (MondrianOlap4jLevel)level;
        return olap4jLevel.getProperties(false);
    }

    public boolean isPropertyInternal(org.olap4j.metadata.Property property) {
        MondrianOlap4jProperty olap4jProperty = (MondrianOlap4jProperty)property;
        return olap4jProperty.property.isInternal();
    }

    public List<Map<String, Object>> getDataSources(OlapConnection connection) throws OlapException {
        MondrianOlap4jConnection olap4jConnection = (MondrianOlap4jConnection)connection;
        MondrianServer server = MondrianServer.forConnection(olap4jConnection.getMondrianConnection());
        List<Map<String, Object>> databases = server.getDatabases(olap4jConnection.getMondrianConnection());
        Iterator var5 = databases.iterator();

        while(var5.hasNext()) {
            Map<String, Object> db = (Map)var5.next();
            String dsi = (String)db.get("DataSourceInfo");
            if (dsi == null) {
                break;
            }

            PropertyList pl = Util.parseConnectString(dsi);
            boolean removed = pl.remove(RolapConnectionProperties.Jdbc.name());
            removed |= pl.remove(RolapConnectionProperties.JdbcUser.name());
            removed |= pl.remove(RolapConnectionProperties.JdbcPassword.name());
            if (removed) {
                db.put("DataSourceInfo", pl.toString());
            }
        }

        return databases;
    }

    public Map<String, Object> getAnnotationMap(MetadataElement element) throws SQLException {
        if (element instanceof OlapWrapper) {
            OlapWrapper wrapper = (OlapWrapper)element;
            if (wrapper.isWrapperFor(Annotated.class)) {
                Annotated annotated = (Annotated)wrapper.unwrap(Annotated.class);
                Map<String, Object> map = new HashMap();
                Iterator var5 = annotated.getAnnotationMap().entrySet().iterator();

                while(var5.hasNext()) {
                    Entry<String, Annotation> entry = (Entry)var5.next();
                    map.put((String)entry.getKey(), ((Annotation)entry.getValue()).getValue());
                }

                return map;
            }
        }

        return Collections.emptyMap();
    }

    public boolean canDrillThrough(Cell cell) {
        return ((MondrianOlap4jCell)cell).cell.canDrillThrough();
    }

    public int getDrillThroughCount(Cell cell) {
        return ((MondrianOlap4jCell)cell).cell.getDrillThroughCount();
    }

    public void flushSchemaCache(OlapConnection conn) throws OlapException {
        try {
            RolapConnection rConn = (RolapConnection)conn.unwrap(RolapConnection.class);
            rConn.getCacheControl((PrintWriter)null).flushSchema(rConn.getSchema());
        } catch (SQLException var3) {
            throw new OlapException(var3);
        }
    }

    public Object getMemberKey(Member m) throws OlapException {
        try {
            return ((RolapMemberBase)((MondrianOlap4jMember)m).unwrap(RolapMemberBase.class)).getKey();
        } catch (SQLException var3) {
            throw new OlapException(var3);
        }
    }

    public Object getOrderKey(Member m) throws OlapException {
        try {
            return ((RolapMemberBase)((MondrianOlap4jMember)m).unwrap(RolapMemberBase.class)).getOrderKey();
        } catch (SQLException var3) {
            throw new OlapException(var3);
        }
    }

    public String getLevelDataType(Level level) {
        MondrianOlap4jLevel olap4jLevel = (MondrianOlap4jLevel)level;
        return olap4jLevel.level instanceof RolapLevel ? ((RolapLevel)olap4jLevel.level).getDatatype().name() : null;
    }
}