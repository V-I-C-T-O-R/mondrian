/* Decompiler 626ms, total 1344ms, lines 914 */
package mondrian.olap4j;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import mondrian.mdx.DimensionExpr;
import mondrian.mdx.HierarchyExpr;
import mondrian.mdx.LevelExpr;
import mondrian.mdx.MemberExpr;
import mondrian.mdx.ResolvedFunCall;
import mondrian.olap.Cube;
import mondrian.olap.Dimension;
import mondrian.olap.DriverManager;
import mondrian.olap.Exp;
import mondrian.olap.Formula;
import mondrian.olap.Hierarchy;
import mondrian.olap.Id;
import mondrian.olap.Level;
import mondrian.olap.Literal;
import mondrian.olap.Member;
import mondrian.olap.MemberProperty;
import mondrian.olap.MondrianException;
import mondrian.olap.MondrianServer;
import mondrian.olap.NamedSet;
import mondrian.olap.Query;
import mondrian.olap.QueryAxis;
import mondrian.olap.QueryCanceledException;
import mondrian.olap.QueryTimeoutException;
import mondrian.olap.ResourceLimitExceededException;
import mondrian.olap.Role;
import mondrian.olap.RoleImpl;
import mondrian.olap.Schema;
import mondrian.olap.Util;
import mondrian.olap.Util.PropertyList;
import mondrian.olap.fun.MondrianEvaluationException;
import mondrian.olap.type.BooleanType;
import mondrian.olap.type.CubeType;
import mondrian.olap.type.DecimalType;
import mondrian.olap.type.DimensionType;
import mondrian.olap.type.HierarchyType;
import mondrian.olap.type.LevelType;
import mondrian.olap.type.MemberType;
import mondrian.olap.type.NullType;
import mondrian.olap.type.NumericType;
import mondrian.olap.type.SetType;
import mondrian.olap.type.StringType;
import mondrian.olap.type.SymbolType;
import mondrian.olap.type.TupleType;
import mondrian.rolap.RolapConnection;
import mondrian.rolap.RolapMeasure;
import mondrian.rolap.RolapSchema;
import mondrian.rolap.ScenarioImpl;
import mondrian.rolap.SchemaContentKey;
import mondrian.spi.CatalogLocator;
import mondrian.util.Bug;
import mondrian.xmla.XmlaHandler.XmlaExtra;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.olap4j.Cell;
import org.olap4j.OlapConnection;
import org.olap4j.OlapDatabaseMetaData;
import org.olap4j.OlapException;
import org.olap4j.OlapStatement;
import org.olap4j.PreparedOlapStatement;
import org.olap4j.Scenario;
import org.olap4j.impl.AbstractNamedList;
import org.olap4j.impl.NamedListImpl;
import org.olap4j.impl.Olap4jUtil;
import org.olap4j.impl.UnmodifiableArrayList;
import org.olap4j.mdx.AxisNode;
import org.olap4j.mdx.CallNode;
import org.olap4j.mdx.CubeNode;
import org.olap4j.mdx.DimensionNode;
import org.olap4j.mdx.HierarchyNode;
import org.olap4j.mdx.IdentifierNode;
import org.olap4j.mdx.IdentifierSegment;
import org.olap4j.mdx.LevelNode;
import org.olap4j.mdx.LiteralNode;
import org.olap4j.mdx.MemberNode;
import org.olap4j.mdx.ParseRegion;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.mdx.ParseTreeWriter;
import org.olap4j.mdx.PropertyValueNode;
import org.olap4j.mdx.SelectNode;
import org.olap4j.mdx.Syntax;
import org.olap4j.mdx.WithMemberNode;
import org.olap4j.mdx.parser.MdxParser;
import org.olap4j.mdx.parser.MdxParserFactory;
import org.olap4j.mdx.parser.MdxValidator;
import org.olap4j.mdx.parser.impl.DefaultMdxParserImpl;
import org.olap4j.metadata.Catalog;
import org.olap4j.metadata.Database;
import org.olap4j.metadata.NamedList;
import org.olap4j.metadata.Database.AuthenticationMode;
import org.olap4j.metadata.Database.ProviderType;
import org.olap4j.type.Type;

public abstract class MondrianOlap4jConnection implements OlapConnection {
    private static final Logger LOGGER;
    final MondrianOlap4jConnection.Helper helper = new MondrianOlap4jConnection.Helper();
    private RolapConnection mondrianConnection;
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    final Map<SchemaContentKey, MondrianOlap4jSchema> schemaMap = new HashMap();
    private final MondrianOlap4jDatabaseMetaData olap4jDatabaseMetaData;
    private static final String CONNECT_STRING_PREFIX = "jdbc:mondrian:";
    private static final String ENGINE_CONNECT_STRING_PREFIX = "jdbc:mondrian:engine:";
    final Factory factory;
    final MondrianOlap4jDriver driver;
    private String roleName;
    private List<String> roleNames = Collections.emptyList();
    private boolean autoCommit;
    private boolean readOnly;
    boolean preferList;
    final MondrianServer mondrianServer;
    private final MondrianOlap4jSchema olap4jSchema;
    private final NamedList<MondrianOlap4jDatabase> olap4jDatabases;

    public MondrianOlap4jSchema getMondrianOlap4jSchema() {
        return this.olap4jSchema;
    }

    MondrianOlap4jConnection(Factory factory, MondrianOlap4jDriver driver, String url, Properties info) throws SQLException {
        assert "jdbc:mondrian:engine:".startsWith("jdbc:mondrian:");

        this.factory = factory;
        this.driver = driver;
        String x;
        if (url.startsWith("jdbc:mondrian:engine:")) {
            x = url.substring("jdbc:mondrian:engine:".length());
        } else {
            if (!url.startsWith("jdbc:mondrian:")) {
                throw new AssertionError("does not start with 'jdbc:mondrian:'");
            }

            x = url.substring("jdbc:mondrian:".length());
        }

        PropertyList list = Util.parseConnectString(x);
        Map<String, String> map = Util.toMap(info);
        Iterator var8 = map.entrySet().iterator();

        while(var8.hasNext()) {
            Entry<String, String> entry = (Entry)var8.next();
            list.put((String)entry.getKey(), (String)entry.getValue());
        }

        this.mondrianConnection = (RolapConnection)DriverManager.getConnection(list, (CatalogLocator)null);
        this.olap4jDatabaseMetaData = factory.newDatabaseMetaData(this, this.mondrianConnection);
        this.mondrianServer = MondrianServer.forConnection(this.mondrianConnection);
        CatalogFinder catalogFinder = (CatalogFinder)this.mondrianServer;
        NamedList<MondrianOlap4jCatalog> olap4jCatalogs = new NamedListImpl();
        this.olap4jDatabases = new NamedListImpl();
        List<Map<String, Object>> dbpropsMaps = this.mondrianServer.getDatabases(this.mondrianConnection);
        if (dbpropsMaps.size() != 1) {
            throw new AssertionError();
        } else {
            Map<String, Object> dbpropsMap = (Map)dbpropsMaps.get(0);
            StringTokenizer st = new StringTokenizer(String.valueOf(dbpropsMap.get("ProviderType")), ",");
            ArrayList pTypes = new ArrayList();

            while(st.hasMoreTokens()) {
                pTypes.add(ProviderType.valueOf(st.nextToken()));
            }

            st = new StringTokenizer(String.valueOf(dbpropsMap.get("AuthenticationMode")), ",");
            ArrayList aModes = new ArrayList();

            while(st.hasMoreTokens()) {
                aModes.add(AuthenticationMode.valueOf(st.nextToken()));
            }

            MondrianOlap4jDatabase database = new MondrianOlap4jDatabase(this, olap4jCatalogs, String.valueOf(dbpropsMap.get("DataSourceName")), String.valueOf(dbpropsMap.get("DataSourceDescription")), String.valueOf(dbpropsMap.get("ProviderName")), String.valueOf(dbpropsMap.get("URL")), String.valueOf(dbpropsMap.get("DataSourceInfo")), pTypes, aModes);
            this.olap4jDatabases.add(database);
            Iterator var16 = catalogFinder.getCatalogNames(this.mondrianConnection).iterator();

            while(var16.hasNext()) {
                String catalogName = (String)var16.next();

                Map schemaMap;
                try {
                    schemaMap = catalogFinder.getRolapSchemas(this.mondrianConnection, catalogName);
                } catch (Exception var20) {
                    LOGGER.warn("Can't get Rolap Schemas for catalog:" + catalogName + ". Skipping...", var20);
                    continue;
                }

                olap4jCatalogs.add(new MondrianOlap4jCatalog(this.olap4jDatabaseMetaData, catalogName, database, schemaMap));
            }

            this.olap4jSchema = this.toOlap4j((Schema)this.mondrianConnection.getSchema());
        }
    }

    static boolean acceptsURL(String url) {
        return url.startsWith("jdbc:mondrian:");
    }

    public OlapStatement createStatement() {
        MondrianOlap4jStatement statement = this.factory.newStatement(this);
        this.mondrianServer.addStatement(statement);
        return statement;
    }

    public ScenarioImpl createScenario() throws OlapException {
        return this.getMondrianConnection().createScenario();
    }

    public void setScenario(Scenario scenario) throws OlapException {
        this.getMondrianConnection().setScenario(scenario);
    }

    public Scenario getScenario() throws OlapException {
        return this.getMondrianConnection().getScenario();
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String nativeSQL(String sql) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.autoCommit = autoCommit;
    }

    public boolean getAutoCommit() throws SQLException {
        return this.autoCommit;
    }

    public void commit() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void rollback() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void close() throws SQLException {
        if (!this.isClosed.get()) {
            this.mondrianConnection.close();
            this.isClosed.set(true);
        }

    }

    public boolean isClosed() throws SQLException {
        return this.isClosed.get();
    }

    public OlapDatabaseMetaData getMetaData() {
        return this.olap4jDatabaseMetaData;
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        this.readOnly = readOnly;
    }

    public boolean isReadOnly() throws SQLException {
        return this.readOnly;
    }

    public void setSchema(String schemaName) throws OlapException {
    }

    public String getSchema() throws OlapException {
        return this.olap4jSchema.getName();
    }

    public org.olap4j.metadata.Schema getOlapSchema() throws OlapException {
        return this.olap4jSchema;
    }

    public NamedList<org.olap4j.metadata.Schema> getOlapSchemas() throws OlapException {
        return this.getOlapCatalog().getSchemas();
    }

    public void setCatalog(String catalogName) throws OlapException {
    }

    public String getCatalog() throws OlapException {
        return this.olap4jSchema.olap4jCatalog.getName();
    }

    public Catalog getOlapCatalog() throws OlapException {
        return this.olap4jSchema.olap4jCatalog;
    }

    public NamedList<Catalog> getOlapCatalogs() throws OlapException {
        return this.getOlapDatabase().getCatalogs();
    }

    public void setDatabase(String databaseName) throws OlapException {
    }

    public String getDatabase() throws OlapException {
        return this.getOlapDatabase().getName();
    }

    public Database getOlapDatabase() throws OlapException {
        return (Database)this.olap4jDatabases.get(0);
    }

    public NamedList<Database> getOlapDatabases() throws OlapException {
        return Olap4jUtil.cast(this.olap4jDatabases);
    }

    public void setTransactionIsolation(int level) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getTransactionIsolation() throws SQLException {
        return 0;
    }

    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void clearWarnings() throws SQLException {
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setHoldability(int holdability) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getHoldability() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Savepoint setSavepoint() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return iface.cast(this);
        } else if (iface.isInstance(this.mondrianConnection)) {
            return iface.cast(this.mondrianConnection);
        } else if (iface == XmlaExtra.class) {
            return iface.cast(MondrianOlap4jExtra.INSTANCE);
        } else {
            throw this.helper.createException("does not implement '" + iface + "'");
        }
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this) || iface.isInstance(this.mondrianConnection);
    }

    public PreparedOlapStatement prepareOlapStatement(String mdx) throws OlapException {
        MondrianOlap4jPreparedStatement preparedStatement = this.factory.newPreparedStatement(mdx, this);
        this.mondrianServer.addStatement(preparedStatement);
        return preparedStatement;
    }

    public MdxParserFactory getParserFactory() {
        return new MdxParserFactory() {
            public MdxParser createMdxParser(OlapConnection connection) {
                return new DefaultMdxParserImpl();
            }

            public MdxValidator createMdxValidator(OlapConnection connection) {
                return new MondrianOlap4jConnection.MondrianOlap4jMdxValidator(connection);
            }
        };
    }

    MondrianOlap4jCube toOlap4j(Cube cube) {
        MondrianOlap4jSchema schema = this.toOlap4j(cube.getSchema());
        return new MondrianOlap4jCube(cube, schema);
    }

    MondrianOlap4jDimension toOlap4j(Dimension dimension) {
        return dimension == null ? null : new MondrianOlap4jDimension(this.toOlap4j(dimension.getSchema()), dimension);
    }

    synchronized MondrianOlap4jSchema toOlap4j(Schema schema) {
        MondrianOlap4jSchema olap4jSchema = (MondrianOlap4jSchema)this.schemaMap.get(((RolapSchema)schema).getKey().getKey());
        if (olap4jSchema == null) {
            throw new RuntimeException("schema not registered: " + schema);
        } else {
            return olap4jSchema;
        }
    }

    Type toOlap4j(mondrian.olap.type.Type type) {
        if (type instanceof BooleanType) {
            return new org.olap4j.type.BooleanType();
        } else if (type instanceof CubeType) {
            Cube mondrianCube = ((CubeType)type).getCube();
            return new org.olap4j.type.CubeType(this.toOlap4j(mondrianCube));
        } else if (type instanceof DecimalType) {
            DecimalType decimalType = (DecimalType)type;
            return new org.olap4j.type.DecimalType(decimalType.getPrecision(), decimalType.getScale());
        } else if (type instanceof DimensionType) {
            DimensionType dimensionType = (DimensionType)type;
            return new org.olap4j.type.DimensionType(this.toOlap4j(dimensionType.getDimension()));
        } else if (type instanceof HierarchyType) {
            return new org.olap4j.type.BooleanType();
        } else if (type instanceof LevelType) {
            return new org.olap4j.type.BooleanType();
        } else if (type instanceof MemberType) {
            MemberType memberType = (MemberType)type;
            return new org.olap4j.type.MemberType(this.toOlap4j(memberType.getDimension()), this.toOlap4j(memberType.getHierarchy()), this.toOlap4j(memberType.getLevel()), this.toOlap4j(memberType.getMember()));
        } else if (type instanceof NullType) {
            return new org.olap4j.type.NullType();
        } else if (type instanceof NumericType) {
            return new org.olap4j.type.NumericType();
        } else if (type instanceof SetType) {
            SetType setType = (SetType)type;
            return new org.olap4j.type.SetType(this.toOlap4j(setType.getElementType()));
        } else if (type instanceof StringType) {
            return new org.olap4j.type.StringType();
        } else if (type instanceof TupleType) {
            TupleType tupleType = (TupleType)type;
            Type[] types = this.toOlap4j(tupleType.elementTypes);
            return new org.olap4j.type.TupleType(types);
        } else if (type instanceof SymbolType) {
            return new org.olap4j.type.SymbolType();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    MondrianOlap4jMember toOlap4j(Member member) {
        if (member == null) {
            return null;
        } else if (member instanceof RolapMeasure) {
            RolapMeasure measure = (RolapMeasure)member;
            return new MondrianOlap4jMeasure(this.toOlap4j(member.getDimension().getSchema()), measure);
        } else {
            return new MondrianOlap4jMember(this.toOlap4j(member.getDimension().getSchema()), member);
        }
    }

    MondrianOlap4jLevel toOlap4j(Level level) {
        return level == null ? null : new MondrianOlap4jLevel(this.toOlap4j(level.getDimension().getSchema()), level);
    }

    MondrianOlap4jHierarchy toOlap4j(Hierarchy hierarchy) {
        return hierarchy == null ? null : new MondrianOlap4jHierarchy(this.toOlap4j(hierarchy.getDimension().getSchema()), hierarchy);
    }

    Type[] toOlap4j(mondrian.olap.type.Type[] mondrianTypes) {
        Type[] types = new Type[mondrianTypes.length];

        for(int i = 0; i < types.length; ++i) {
            types[i] = this.toOlap4j(mondrianTypes[i]);
        }

        return types;
    }

    NamedList<MondrianOlap4jMember> toOlap4j(final List<Member> memberList) {
        return new AbstractNamedList<MondrianOlap4jMember>() {
            public String getName(Object olap4jMember) {
                return ((MondrianOlap4jMember)olap4jMember).getName();
            }

            public MondrianOlap4jMember get(int index) {
                return MondrianOlap4jConnection.this.toOlap4j((Member)memberList.get(index));
            }

            public int size() {
                return memberList.size();
            }
        };
    }

    MondrianOlap4jNamedSet toOlap4j(Cube cube, NamedSet namedSet) {
        return namedSet == null ? null : new MondrianOlap4jNamedSet(this.toOlap4j(cube), namedSet);
    }

    ParseTreeNode toOlap4j(Exp exp) {
        return (new MondrianOlap4jConnection.MondrianToOlap4jNodeConverter(this)).toOlap4j(exp);
    }

    SelectNode toOlap4j(Query query) {
        return (new MondrianOlap4jConnection.MondrianToOlap4jNodeConverter(this)).toOlap4j(query);
    }

    public void setLocale(Locale locale) {
        this.mondrianConnection.setLocale(locale);
    }

    public Locale getLocale() {
        return this.mondrianConnection.getLocale();
    }

    public void setRoleName(String roleName) throws OlapException {
        if (roleName == null) {
            RolapConnection connection1 = this.getMondrianConnection();
            Role role = Util.createRootRole(connection1.getSchema());

            assert role != null;

            this.roleName = roleName;
            this.roleNames = Collections.emptyList();
            connection1.setRole(role);
        } else {
            this.setRoleNames(Collections.singletonList(roleName));
        }

    }

    public void setRoleNames(List<String> roleNames) throws OlapException {
        RolapConnection connection1 = this.getMondrianConnection();
        List<Role> roleList = new ArrayList();
        Iterator var4 = roleNames.iterator();

        while(var4.hasNext()) {
            String roleName = (String)var4.next();
            if (roleName == null) {
                throw new NullPointerException("null role name");
            }

            Role role = connection1.getSchema().lookupRole(roleName);
            if (role == null) {
                throw this.helper.createException("Unknown role '" + roleName + "'");
            }

            roleList.add(role);
        }

        Role role;
        switch(roleList.size()) {
            case 0:
                throw this.helper.createException("Empty list of role names");
            case 1:
                role = (Role)roleList.get(0);
                this.roleName = (String)roleNames.get(0);
                this.roleNames = Collections.singletonList(this.roleName);
                break;
            default:
                role = RoleImpl.union(roleList);
                this.roleNames = Collections.unmodifiableList(new ArrayList(roleNames));
                this.roleName = this.roleNames.toString();
        }

        connection1.setRole(role);
    }

    public String getRoleName() {
        return this.roleName;
    }

    public List<String> getRoleNames() {
        return this.roleNames;
    }

    public List<String> getAvailableRoleNames() throws OlapException {
        return UnmodifiableArrayList.of(this.getMondrianConnection().getSchema().roleNames());
    }

    public void setPreferList(boolean preferList) {
        this.preferList = preferList;
    }

    RolapConnection getMondrianConnection2() throws RuntimeException {
        try {
            return this.getMondrianConnection();
        } catch (OlapException var2) {
            throw new RuntimeException(var2);
        }
    }

    public RolapConnection getMondrianConnection() throws OlapException {
        RolapConnection connection1 = this.mondrianConnection;
        if (connection1 == null) {
            throw this.helper.createException("Connection is closed.");
        } else {
            return connection1;
        }
    }

    static {
        Bug.olap4jUpgrade("Make this class package-protected when we upgrade to olap4j 2.0. The setRoleNames method will then be available through the olap4j API");
        LOGGER = LogManager.getLogger(MondrianOlap4jConnection.class);
    }

    private static class MondrianToOlap4jNodeConverter {
        private final MondrianOlap4jConnection olap4jConnection;

        MondrianToOlap4jNodeConverter(MondrianOlap4jConnection olap4jConnection) {
            this.olap4jConnection = olap4jConnection;
        }

        public SelectNode toOlap4j(Query query) {
            List<IdentifierNode> list = Collections.emptyList();
            return new SelectNode((ParseRegion)null, this.toOlap4j(query.getFormulas()), this.toOlap4j(query.getAxes()), new CubeNode((ParseRegion)null, this.olap4jConnection.toOlap4j(query.getCube())), query.getSlicerAxis() == null ? null : this.toOlap4j(query.getSlicerAxis()), list);
        }

        private AxisNode toOlap4j(QueryAxis axis) {
            return new AxisNode((ParseRegion)null, axis.isNonEmpty(), org.olap4j.Axis.Factory.forOrdinal(axis.getAxisOrdinal().logicalOrdinal()), this.toOlap4j(axis.getDimensionProperties()), this.toOlap4j(axis.getSet()));
        }

        private List<IdentifierNode> toOlap4j(Id[] dimensionProperties) {
            List<IdentifierNode> list = new ArrayList();
            Id[] var3 = dimensionProperties;
            int var4 = dimensionProperties.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Id property = var3[var5];
                list.add(toOlap4j(property));
            }

            return list;
        }

        private ParseTreeNode toOlap4j(Exp exp) {
            if (exp instanceof Id) {
                Id id = (Id)exp;
                return toOlap4j(id);
            } else if (exp instanceof ResolvedFunCall) {
                ResolvedFunCall call = (ResolvedFunCall)exp;
                return this.toOlap4j(call);
            } else if (exp instanceof DimensionExpr) {
                DimensionExpr dimensionExpr = (DimensionExpr)exp;
                return new DimensionNode((ParseRegion)null, this.olap4jConnection.toOlap4j(dimensionExpr.getDimension()));
            } else if (exp instanceof HierarchyExpr) {
                HierarchyExpr hierarchyExpr = (HierarchyExpr)exp;
                return new HierarchyNode((ParseRegion)null, this.olap4jConnection.toOlap4j(hierarchyExpr.getHierarchy()));
            } else if (exp instanceof LevelExpr) {
                LevelExpr levelExpr = (LevelExpr)exp;
                return new LevelNode((ParseRegion)null, this.olap4jConnection.toOlap4j(levelExpr.getLevel()));
            } else if (exp instanceof MemberExpr) {
                MemberExpr memberExpr = (MemberExpr)exp;
                return new MemberNode((ParseRegion)null, this.olap4jConnection.toOlap4j(memberExpr.getMember()));
            } else if (exp instanceof Literal) {
                Literal literal = (Literal)exp;
                Object value = literal.getValue();
                if (literal.getCategory() == 11) {
                    return LiteralNode.createSymbol((ParseRegion)null, (String)literal.getValue());
                } else if (value instanceof Number) {
                    Number number = (Number)value;
                    BigDecimal bd = bigDecimalFor(number);
                    return LiteralNode.createNumeric((ParseRegion)null, bd, false);
                } else if (value instanceof String) {
                    return LiteralNode.createString((ParseRegion)null, (String)value);
                } else if (value == null) {
                    return LiteralNode.createNull((ParseRegion)null);
                } else {
                    throw new RuntimeException("unknown literal " + literal);
                }
            } else {
                throw Util.needToImplement(exp.getClass());
            }
        }

        private static BigDecimal bigDecimalFor(Number number) {
            if (number instanceof BigDecimal) {
                return (BigDecimal)number;
            } else if (number instanceof BigInteger) {
                return new BigDecimal((BigInteger)number);
            } else if (number instanceof Integer) {
                return new BigDecimal((Integer)number);
            } else if (number instanceof Double) {
                return new BigDecimal((Double)number);
            } else if (number instanceof Float) {
                return new BigDecimal((double)(Float)number);
            } else if (number instanceof Long) {
                return new BigDecimal((Long)number);
            } else if (number instanceof Short) {
                return new BigDecimal((Short)number);
            } else {
                return number instanceof Byte ? new BigDecimal((Byte)number) : new BigDecimal(number.doubleValue());
            }
        }

        private ParseTreeNode toOlap4j(ResolvedFunCall call) {
            CallNode callNode = new CallNode((ParseRegion)null, call.getFunName(), this.toOlap4j(call.getSyntax()), this.toOlap4j(Arrays.asList(call.getArgs())));
            if (call.getType() != null) {
                callNode.setType(this.olap4jConnection.toOlap4j(call.getType()));
            }

            return callNode;
        }

        private List<ParseTreeNode> toOlap4j(List<Exp> exprList) {
            List<ParseTreeNode> result = new ArrayList();
            Iterator var3 = exprList.iterator();

            while(var3.hasNext()) {
                Exp expr = (Exp)var3.next();
                result.add(this.toOlap4j(expr));
            }

            return result;
        }

        private Syntax toOlap4j(mondrian.olap.Syntax syntax) {
            return Syntax.valueOf(syntax.name());
        }

        private List<AxisNode> toOlap4j(QueryAxis[] axes) {
            ArrayList<AxisNode> axisList = new ArrayList();
            QueryAxis[] var3 = axes;
            int var4 = axes.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                QueryAxis axis = var3[var5];
                axisList.add(this.toOlap4j(axis));
            }

            return axisList;
        }

        private List<ParseTreeNode> toOlap4j(Formula[] formulas) {
            List<ParseTreeNode> list = new ArrayList();
            Formula[] var3 = formulas;
            int var4 = formulas.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Formula formula = var3[var5];
                if (formula.isMember()) {
                    List<PropertyValueNode> memberPropertyList = new ArrayList();
                    Object[] var8 = formula.getChildren();
                    int var9 = var8.length;

                    for(int var10 = 0; var10 < var9; ++var10) {
                        Object child = var8[var10];
                        if (child instanceof MemberProperty) {
                            MemberProperty memberProperty = (MemberProperty)child;
                            memberPropertyList.add(new PropertyValueNode((ParseRegion)null, memberProperty.getName(), this.toOlap4j(memberProperty.getExp())));
                        }
                    }

                    list.add(new WithMemberNode((ParseRegion)null, toOlap4j(formula.getIdentifier()), this.toOlap4j(formula.getExpression()), memberPropertyList));
                }
            }

            return list;
        }

        private static IdentifierNode toOlap4j(Id id) {
            List<IdentifierSegment> list = Util.toOlap4j(id.getSegments());
            return new IdentifierNode((IdentifierSegment[])list.toArray(new IdentifierSegment[list.size()]));
        }
    }

    private static class MondrianOlap4jMdxValidator implements MdxValidator {
        private final MondrianOlap4jConnection connection;

        public MondrianOlap4jMdxValidator(OlapConnection connection) {
            this.connection = (MondrianOlap4jConnection)connection;
        }

        public SelectNode validateSelect(SelectNode selectNode) throws OlapException {
            try {
                StringWriter sw = new StringWriter();
                selectNode.unparse(new ParseTreeWriter(new PrintWriter(sw)));
                String mdx = sw.toString();
                Query query = this.connection.mondrianConnection.parseQuery(mdx);
                query.resolve();
                return this.connection.toOlap4j(query);
            } catch (MondrianException var5) {
                throw this.connection.helper.createException((String)"Validation error", (Throwable)var5);
            }
        }
    }

    static class Helper {
        OlapException createException(String msg) {
            return new OlapException(msg);
        }

        OlapException createException(Cell context, String msg) {
            OlapException exception = new OlapException(msg);
            exception.setContext(context);
            return exception;
        }

        OlapException createException(Cell context, String msg, Throwable cause) {
            OlapException exception = this.createException(msg, cause);
            exception.setContext(context);
            return exception;
        }

        OlapException createException(String msg, Throwable cause) {
            String sqlState = this.deduceSqlState(cause);

            assert !Bug.olap4jUpgrade("use OlapException(String, String, Throwable) ctor");

            OlapException e = new OlapException(msg, sqlState);
            e.initCause(cause);
            return e;
        }

        private String deduceSqlState(Throwable cause) {
            if (cause == null) {
                return null;
            } else if (cause instanceof ResourceLimitExceededException) {
                return "ResourceLimitExceeded";
            } else if (cause instanceof QueryTimeoutException) {
                return "QueryTimeout";
            } else if (cause instanceof MondrianEvaluationException) {
                return "EvaluationException";
            } else {
                return cause instanceof QueryCanceledException ? "QueryCanceledException" : null;
            }
        }

        public OlapException toOlapException(SQLException e) {
            return e instanceof OlapException ? (OlapException)e : new OlapException((String)null, e);
        }
    }
}