/* Decompiler 3058ms, total 6626ms, lines 1133 */
package mondrian.rolap;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import javax.sql.DataSource;
import mondrian.olap.Access;
import mondrian.olap.Annotation;
import mondrian.olap.CacheControl;
import mondrian.olap.Exp;
import mondrian.olap.Formula;
import mondrian.olap.FunTable;
import mondrian.olap.Hierarchy;
import mondrian.olap.Id;
import mondrian.olap.Level;
import mondrian.olap.Member;
import mondrian.olap.MondrianProperties;
import mondrian.olap.MondrianServer;
import mondrian.olap.NamedSet;
import mondrian.olap.OlapElement;
import mondrian.olap.Role;
import mondrian.olap.RoleImpl;
import mondrian.olap.Schema;
import mondrian.olap.SchemaReader;
import mondrian.olap.Syntax;
import mondrian.olap.Util;
import mondrian.olap.FunTable.Builder;
import mondrian.olap.Id.NameSegment;
import mondrian.olap.Id.Quoting;
import mondrian.olap.Id.Segment;
import mondrian.olap.MondrianDef.CalculatedMember;
import mondrian.olap.MondrianDef.Cube;
import mondrian.olap.MondrianDef.CubeDimension;
import mondrian.olap.MondrianDef.CubeGrant;
import mondrian.olap.MondrianDef.Dimension;
import mondrian.olap.MondrianDef.DimensionGrant;
import mondrian.olap.MondrianDef.DimensionUsage;
import mondrian.olap.MondrianDef.HierarchyGrant;
import mondrian.olap.MondrianDef.MemberGrant;
import mondrian.olap.MondrianDef.Parameter;
import mondrian.olap.MondrianDef.Relation;
import mondrian.olap.MondrianDef.RoleUsage;
import mondrian.olap.MondrianDef.SchemaGrant;
import mondrian.olap.MondrianDef.Script;
import mondrian.olap.MondrianDef.UserDefinedFunction;
import mondrian.olap.MondrianDef.VirtualCube;
import mondrian.olap.Role.RollupPolicy;
import mondrian.olap.Util.PropertyList;
import mondrian.olap.fun.FunTableImpl;
import mondrian.olap.fun.GlobalFunTable;
import mondrian.olap.fun.Resolver;
import mondrian.olap.fun.UdfResolver;
import mondrian.olap.fun.UdfResolver.ClassUdfFactory;
import mondrian.olap.fun.UdfResolver.UdfFactory;
import mondrian.olap.type.MemberType;
import mondrian.olap.type.NumericType;
import mondrian.olap.type.StringType;
import mondrian.olap.type.Type;
import mondrian.resource.MondrianResource;
import mondrian.rolap.aggmatcher.AggTableManager;
import mondrian.spi.DataSourceChangeListener;
import mondrian.spi.Dialect;
import mondrian.spi.DialectManager;
import mondrian.spi.impl.Scripts;
import mondrian.spi.impl.Scripts.ScriptDefinition;
import mondrian.spi.impl.Scripts.ScriptLanguage;
import mondrian.util.ByteString;
import mondrian.util.ClassResolver;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eigenbase.xom.DOMWrapper;
import org.eigenbase.xom.ElementDef;
import org.eigenbase.xom.Parser;
import org.eigenbase.xom.XOMException;
import org.eigenbase.xom.XOMUtil;
import org.olap4j.impl.Olap4jUtil;
import org.olap4j.mdx.IdentifierSegment;

public class RolapSchema implements Schema {
    static final Logger LOGGER = LogManager.getLogger(RolapSchema.class);
    private static final Set<Access> schemaAllowed;
    private static final Set<Access> cubeAllowed;
    private static final Set<Access> dimensionAllowed;
    private static final Set<Access> hierarchyAllowed;
    private static final Set<Access> memberAllowed;
    private String name;
    private RolapConnection internalConnection;
    private final Map<String, RolapCube> mapNameToCube;
    private final Map<String, MemberReader> mapSharedHierarchyToReader;
    private final Map<String, RolapHierarchy> mapSharedHierarchyNameToHierarchy;
    private Role defaultRole;
    private ByteString md5Bytes;
    private AggTableManager aggTableManager;
    final SchemaKey key;
    private final Map<String, Role> mapNameToRole;
    private final Map<String, NamedSet> mapNameToSet;
    private FunTable funTable;
    private mondrian.olap.MondrianDef.Schema xmlSchema;
    final List<RolapSchemaParameter> parameterList;
    private Date schemaLoadDate;
    private DataSourceChangeListener dataSourceChangeListener;
    private final List<Exception> warningList;
    private Map<String, Annotation> annotationMap;
    private final String id;
    private RolapSchema.RolapStarRegistry rolapStarRegistry;
    final RolapNativeRegistry nativeRegistry;

    private RolapSchema(SchemaKey key, PropertyList connectInfo, DataSource dataSource, ByteString md5Bytes, boolean useContentChecksum) {
        this.mapNameToCube = new HashMap();
        this.mapSharedHierarchyToReader = new HashMap();
        this.mapSharedHierarchyNameToHierarchy = new HashMap();
        this.mapNameToRole = new HashMap();
        this.mapNameToSet = new HashMap();
        this.parameterList = new ArrayList();
        this.warningList = new ArrayList();
        this.rolapStarRegistry = new RolapSchema.RolapStarRegistry();
        this.nativeRegistry = new RolapNativeRegistry();
        this.id = Util.generateUuidString();
        this.key = key;
        this.md5Bytes = md5Bytes;
        if (useContentChecksum && md5Bytes == null) {
            throw new AssertionError();
        } else {
            this.defaultRole = Util.createRootRole(this);
            MondrianServer internalServer = MondrianServer.forId((String)null);
            this.internalConnection = new RolapConnection(internalServer, connectInfo, this, dataSource);
            internalServer.removeConnection(this.internalConnection);
            internalServer.removeStatement(this.internalConnection.getInternalStatement());
            this.aggTableManager = new AggTableManager(this);
            this.dataSourceChangeListener = this.createDataSourceChangeListener(connectInfo);
        }
    }

    public RolapSchema(SchemaKey key, ByteString md5Bytes, String catalogUrl, String catalogStr, PropertyList connectInfo, DataSource dataSource) {
        this(key, connectInfo, dataSource, md5Bytes, md5Bytes != null);
        this.load(catalogUrl, catalogStr, connectInfo);

        assert this.md5Bytes != null;

    }

    @Deprecated
    RolapSchema(SchemaKey key, ByteString md5Bytes, RolapConnection internalConnection) {
        this.mapNameToCube = new HashMap();
        this.mapSharedHierarchyToReader = new HashMap();
        this.mapSharedHierarchyNameToHierarchy = new HashMap();
        this.mapNameToRole = new HashMap();
        this.mapNameToSet = new HashMap();
        this.parameterList = new ArrayList();
        this.warningList = new ArrayList();
        this.rolapStarRegistry = new RolapSchema.RolapStarRegistry();
        this.nativeRegistry = new RolapNativeRegistry();
        this.id = Util.generateUuidString();
        this.key = key;
        this.md5Bytes = md5Bytes;
        this.defaultRole = Util.createRootRole(this);
        this.internalConnection = internalConnection;
    }

    protected void flushSegments() {
        RolapConnection internalConnection = this.getInternalConnection();
        if (internalConnection != null) {
            CacheControl cc = internalConnection.getCacheControl((PrintWriter)null);
            Iterator var3 = this.getCubeList().iterator();

            while(var3.hasNext()) {
                RolapCube cube = (RolapCube)var3.next();
                cc.flush(cc.createMeasuresRegion(cube));
            }
        }

    }

    protected void flushJdbcSchema() {
        if (this.aggTableManager != null) {
            this.aggTableManager.finalCleanUp();
            this.aggTableManager = null;
        }

    }

    protected void finalCleanUp() {
        this.flushSegments();
        this.flushJdbcSchema();
    }

    protected void finalize() throws Throwable {
        try {
            super.finalize();
            this.flushJdbcSchema();
        } catch (Throwable var2) {
            LOGGER.info(MondrianResource.instance().FinalizerErrorRolapSchema.baseMessage, var2);
        }

    }

    public boolean equals(Object o) {
        if (!(o instanceof RolapSchema)) {
            return false;
        } else {
            RolapSchema other = (RolapSchema)o;
            return other.key.equals(this.key);
        }
    }

    public int hashCode() {
        return this.key.hashCode();
    }

    protected Logger getLogger() {
        return LOGGER;
    }

    @Deprecated
    protected void load(String catalogUrl, String catalogStr) {
        this.load(catalogUrl, catalogStr, new PropertyList());
    }

    protected void load(String catalogUrl, String catalogStr, PropertyList connectInfo) {
        try {
            Parser xmlParser = XOMUtil.createDefaultParser();
            DOMWrapper def;
            if (catalogStr == null) {
                InputStream in = null;

                try {
                    in = Util.readVirtualFile(catalogUrl);
                    def = xmlParser.parse(in);
                } finally {
                    if (in != null) {
                        in.close();
                    }

                }

                if (this.getLogger().isDebugEnabled() || this.md5Bytes == null) {
                    try {
                        catalogStr = Util.readVirtualFileAsString(catalogUrl);
                    } catch (IOException var13) {
                        this.getLogger().debug("RolapSchema.load: ex=" + var13);
                        catalogStr = "?";
                    }
                }

                if (this.getLogger().isDebugEnabled()) {
                    this.getLogger().debug("RolapSchema.load: content: \n" + catalogStr);
                }
            } else {
                if (this.getLogger().isDebugEnabled()) {
                    this.getLogger().debug("RolapSchema.load: catalogStr: \n" + catalogStr);
                }

                def = xmlParser.parse(catalogStr);
            }

            if (this.md5Bytes == null) {
                assert catalogStr != null;

                this.md5Bytes = new ByteString(Util.digestMd5(catalogStr));
            }

            this.checkSchemaVersion(def);
            this.xmlSchema = new mondrian.olap.MondrianDef.Schema(def);
            if (this.getLogger().isDebugEnabled()) {
                StringWriter sw = new StringWriter(4096);
                PrintWriter pw = new PrintWriter(sw);
                pw.println("RolapSchema.load: dump xmlschema");
                this.xmlSchema.display(pw, 2);
                pw.flush();
                this.getLogger().debug(sw.toString());
            }

            this.load(this.xmlSchema);
        } catch (XOMException var15) {
            throw Util.newError(var15, "while parsing catalog " + catalogUrl);
        } catch (FileSystemException var16) {
            throw Util.newError(var16, "while parsing catalog " + catalogUrl);
        } catch (IOException var17) {
            throw Util.newError(var17, "while parsing catalog " + catalogUrl);
        }

        this.aggTableManager.initialize(connectInfo);
        this.setSchemaLoadDate();
    }

    private void checkSchemaVersion(DOMWrapper schemaDom) {
        String schemaVersion = schemaDom.getAttribute("metamodelVersion");
        if (schemaVersion == null) {
            if (this.hasMondrian4Elements(schemaDom)) {
                schemaVersion = "4.x";
            } else {
                schemaVersion = "3.x";
            }
        }

        String[] versionParts = schemaVersion.split("\\.");
        String schemaMajor = versionParts.length > 0 ? versionParts[0] : "";
        String serverSchemaVersion = Integer.toString(MondrianServer.forId((String)null).getSchemaVersion());
        if (serverSchemaVersion.compareTo(schemaMajor) < 0) {
            String errorMsg = "Schema version '" + schemaVersion + "' is later than schema version '3.x' supported by this version of Mondrian";
            throw Util.newError(errorMsg);
        }
    }

    private boolean hasMondrian4Elements(DOMWrapper schemaDom) {
        DOMWrapper[] var2 = schemaDom.getChildren();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            DOMWrapper child = var2[var4];
            if ("PhysicalSchema".equals(child.getTagName())) {
                return true;
            }

            if ("Cube".equals(child.getTagName())) {
                DOMWrapper[] var6 = child.getChildren();
                int var7 = var6.length;

                for(int var8 = 0; var8 < var7; ++var8) {
                    DOMWrapper grandchild = var6[var8];
                    if ("MeasureGroups".equals(grandchild.getTagName())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void setSchemaLoadDate() {
        this.schemaLoadDate = new Date();
    }

    public Date getSchemaLoadDate() {
        return this.schemaLoadDate;
    }

    public List<Exception> getWarnings() {
        return Collections.unmodifiableList(this.warningList);
    }

    public Role getDefaultRole() {
        return this.defaultRole;
    }

    public mondrian.olap.MondrianDef.Schema getXMLSchema() {
        return this.xmlSchema;
    }

    public String getName() {
        Util.assertPostcondition(this.name != null, "return != null");
        Util.assertPostcondition(this.name.length() > 0, "return.length() > 0");
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public SchemaKey getKey() {
        return this.key;
    }

    public Map<String, Annotation> getAnnotationMap() {
        return this.annotationMap;
    }

    public Dialect getDialect() {
        DataSource dataSource = this.getInternalConnection().getDataSource();
        return DialectManager.createDialect(dataSource, (Connection)null);
    }

    private void load(mondrian.olap.MondrianDef.Schema xmlSchema) {
        this.name = xmlSchema.name;
        if (this.name != null && !this.name.equals("")) {
            this.annotationMap = RolapHierarchy.createAnnotationMap(xmlSchema.annotations);
            Map<String, UdfFactory> mapNameToUdf = new HashMap();
            UserDefinedFunction[] var3 = xmlSchema.userDefinedFunctions;
            int var4 = var3.length;

            int var5;
            for(var5 = 0; var5 < var4; ++var5) {
                UserDefinedFunction udf = var3[var5];
                ScriptDefinition scriptDef = toScriptDef(udf.script);
                this.defineFunction(mapNameToUdf, udf.name, udf.className, scriptDef);
            }

            RolapSchema.RolapSchemaFunctionTable funTable = new RolapSchema.RolapSchemaFunctionTable(mapNameToUdf.values());
            funTable.init();
            this.funTable = funTable;
            Dimension[] var16 = xmlSchema.dimensions;
            var5 = var16.length;

            int var19;
            for(var19 = 0; var19 < var5; ++var19) {
                Dimension xmlDimension = var16[var19];
                if (xmlDimension.foreignKey != null) {
                    throw MondrianResource.instance().PublicDimensionMustNotHaveForeignKey.ex(xmlDimension.name);
                }
            }

            Set<String> parameterNames = new HashSet();
            Parameter[] var18 = xmlSchema.parameters;
            var19 = var18.length;

            int var25;
            for(var25 = 0; var25 < var19; ++var25) {
                Parameter xmlParameter = var18[var25];
                String name = xmlParameter.name;
                if (!parameterNames.add(name)) {
                    throw MondrianResource.instance().DuplicateSchemaParameter.ex(name);
                }

                Object type;
                if (xmlParameter.type.equals("String")) {
                    type = new StringType();
                } else if (xmlParameter.type.equals("Numeric")) {
                    type = new NumericType();
                } else {
                    type = new MemberType((mondrian.olap.Dimension)null, (Hierarchy)null, (Level)null, (Member)null);
                }

                String description = xmlParameter.description;
                boolean modifiable = xmlParameter.modifiable;
                String defaultValue = xmlParameter.defaultValue;
                RolapSchemaParameter param = new RolapSchemaParameter(this, name, defaultValue, description, (Type)type, modifiable);
                Util.discard(param);
            }

            Cube[] var20 = xmlSchema.cubes;
            var19 = var20.length;

            RolapCube cube;
            for(var25 = 0; var25 < var19; ++var25) {
                Cube xmlCube = var20[var25];
                if (xmlCube.isEnabled()) {
                    cube = new RolapCube(this, xmlSchema, xmlCube, true);
                    Util.discard(cube);
                }
            }

            VirtualCube[] var21 = xmlSchema.virtualCubes;
            var19 = var21.length;

            for(var25 = 0; var25 < var19; ++var25) {
                VirtualCube xmlVirtualCube = var21[var25];
                if (xmlVirtualCube.isEnabled()) {
                    cube = new RolapCube(this, xmlSchema, xmlVirtualCube, true);
                    Util.discard(cube);
                }
            }

            mondrian.olap.MondrianDef.NamedSet[] var22 = xmlSchema.namedSets;
            var19 = var22.length;

            for(var25 = 0; var25 < var19; ++var25) {
                mondrian.olap.MondrianDef.NamedSet xmlNamedSet = var22[var25];
                this.mapNameToSet.put(xmlNamedSet.name, this.createNamedSet(xmlNamedSet));
            }

            mondrian.olap.MondrianDef.Role[] var24 = xmlSchema.roles;
            var19 = var24.length;

            for(var25 = 0; var25 < var19; ++var25) {
                mondrian.olap.MondrianDef.Role xmlRole = var24[var25];
                Role role = this.createRole(xmlRole);
                this.mapNameToRole.put(xmlRole.name, role);
            }

            if (xmlSchema.defaultRole != null) {
                Role role = this.lookupRole(xmlSchema.defaultRole);
                if (role == null) {
                    this.error("Role '" + xmlSchema.defaultRole + "' not found", this.locate(xmlSchema, "defaultRole"));
                } else {
                    this.defaultRole = role;
                }
            }

        } else {
            throw Util.newError("<Schema> name must be set");
        }
    }

    static ScriptDefinition toScriptDef(Script script) {
        if (script == null) {
            return null;
        } else {
            ScriptLanguage language = ScriptLanguage.lookup(script.language);
            if (language == null) {
                throw Util.newError("Invalid script language '" + script.language + "'");
            } else {
                return new ScriptDefinition(script.cdata, language);
            }
        }
    }

    RolapSchema.XmlLocation locate(ElementDef node, String attributeName) {
        return null;
    }

    void error(String message, RolapSchema.XmlLocation xmlLocation) {
        RuntimeException ex = new RuntimeException(message);
        if (this.internalConnection != null && "true".equals(this.internalConnection.getProperty(RolapConnectionProperties.Ignore.name()))) {
            this.warningList.add(ex);
        } else {
            throw ex;
        }
    }

    private NamedSet createNamedSet(mondrian.olap.MondrianDef.NamedSet xmlNamedSet) {
        String formulaString = xmlNamedSet.getFormula();

        Exp exp;
        try {
            exp = this.getInternalConnection().parseExpression(formulaString);
        } catch (Exception var5) {
            throw MondrianResource.instance().NamedSetHasBadFormula.ex(xmlNamedSet.name, var5);
        }

        Formula formula = new Formula(new Id(new NameSegment(xmlNamedSet.name, Quoting.UNQUOTED)), exp);
        return formula.getNamedSet();
    }

    private Role createRole(mondrian.olap.MondrianDef.Role xmlRole) {
        if (xmlRole.union != null) {
            return this.createUnionRole(xmlRole);
        } else {
            RoleImpl role = new RoleImpl();
            SchemaGrant[] var3 = xmlRole.schemaGrants;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                SchemaGrant schemaGrant = var3[var5];
                this.handleSchemaGrant(role, schemaGrant);
            }

            role.makeImmutable();
            return role;
        }
    }

    Role createUnionRole(mondrian.olap.MondrianDef.Role xmlRole) {
        if (xmlRole.schemaGrants != null && xmlRole.schemaGrants.length > 0) {
            throw MondrianResource.instance().RoleUnionGrants.ex();
        } else {
            RoleUsage[] usages = xmlRole.union.roleUsages;
            List<Role> roleList = new ArrayList(usages.length);
            RoleUsage[] var4 = usages;
            int var5 = usages.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                RoleUsage roleUsage = var4[var6];
                Role role = (Role)this.mapNameToRole.get(roleUsage.roleName);
                if (role == null) {
                    throw MondrianResource.instance().UnknownRole.ex(roleUsage.roleName);
                }

                roleList.add(role);
            }

            return RoleImpl.union(roleList);
        }
    }

    void handleSchemaGrant(RoleImpl role, SchemaGrant schemaGrant) {
        role.grant(this, this.getAccess(schemaGrant.access, schemaAllowed));
        CubeGrant[] var3 = schemaGrant.cubeGrants;
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            CubeGrant cubeGrant = var3[var5];
            this.handleCubeGrant(role, cubeGrant);
        }

    }

    void handleCubeGrant(RoleImpl role, CubeGrant cubeGrant) {
        RolapCube cube = this.lookupCube(cubeGrant.cube);
        if (cube == null) {
            throw Util.newError("Unknown cube '" + cubeGrant.cube + "'");
        } else {
            role.grant(cube, this.getAccess(cubeGrant.access, cubeAllowed));
            SchemaReader reader = cube.getSchemaReader((Role)null);
            DimensionGrant[] var5 = cubeGrant.dimensionGrants;
            int var6 = var5.length;

            int var7;
            for(var7 = 0; var7 < var6; ++var7) {
                DimensionGrant grant = var5[var7];
                mondrian.olap.Dimension dimension = (mondrian.olap.Dimension)this.lookup(cube, reader, 2, grant.dimension);
                role.grant(dimension, this.getAccess(grant.access, dimensionAllowed));
            }

            HierarchyGrant[] var10 = cubeGrant.hierarchyGrants;
            var6 = var10.length;

            for(var7 = 0; var7 < var6; ++var7) {
                HierarchyGrant hierarchyGrant = var10[var7];
                this.handleHierarchyGrant(role, cube, reader, hierarchyGrant);
            }

        }
    }

    void handleHierarchyGrant(RoleImpl role, RolapCube cube, SchemaReader reader, HierarchyGrant grant) {
        Hierarchy hierarchy = (Hierarchy)this.lookup(cube, reader, 3, grant.hierarchy);
        Access hierarchyAccess = this.getAccess(grant.access, hierarchyAllowed);
        Level topLevel = this.findLevelForHierarchyGrant(cube, reader, hierarchyAccess, grant.topLevel, "topLevel");
        Level bottomLevel = this.findLevelForHierarchyGrant(cube, reader, hierarchyAccess, grant.bottomLevel, "bottomLevel");
        RollupPolicy rollupPolicy;
        if (grant.rollupPolicy != null) {
            try {
                rollupPolicy = RollupPolicy.valueOf(grant.rollupPolicy.toUpperCase());
            } catch (IllegalArgumentException var17) {
                throw Util.newError("Illegal rollupPolicy value '" + grant.rollupPolicy + "'");
            }
        } else {
            rollupPolicy = RollupPolicy.FULL;
        }

        role.grant(hierarchy, hierarchyAccess, topLevel, bottomLevel, rollupPolicy);
        boolean ignoreInvalidMembers = MondrianProperties.instance().IgnoreInvalidMembers.get();
        int membersRejected = 0;
        if (grant.memberGrants.length > 0) {
            if (hierarchyAccess != Access.CUSTOM) {
                throw Util.newError("You may only specify <MemberGrant> if <Hierarchy> has access='custom'");
            }

            MemberGrant[] var12 = grant.memberGrants;
            int var13 = var12.length;

            for(int var14 = 0; var14 < var13; ++var14) {
                MemberGrant memberGrant = var12[var14];
                Member member = reader.withLocus().getMemberByUniqueName(Util.parseIdentifier(memberGrant.member), !ignoreInvalidMembers);
                if (member == null) {
                    assert ignoreInvalidMembers;

                    ++membersRejected;
                } else {
                    if (member.getHierarchy() != hierarchy) {
                        throw Util.newError("Member '" + member + "' is not in hierarchy '" + hierarchy + "'");
                    }

                    role.grant(member, this.getAccess(memberGrant.access, memberAllowed));
                }
            }
        }

        if (membersRejected > 0 && grant.memberGrants.length == membersRejected) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Rolling back grants of Hierarchy '" + hierarchy.getUniqueName() + "' to NONE, because it contains no valid restricted members");
            }

            role.grant(hierarchy, Access.NONE, (Level)null, (Level)null, rollupPolicy);
        }

    }

    private <T extends OlapElement> T lookup(RolapCube cube, SchemaReader reader, int category, String id) {
        List<Segment> segments = Util.parseIdentifier(id);
        return (T) reader.lookupCompound(cube, segments, true, category);
    }

    private Level findLevelForHierarchyGrant(RolapCube cube, SchemaReader schemaReader, Access hierarchyAccess, String name, String desc) {
        if (name == null) {
            return null;
        } else if (hierarchyAccess != Access.CUSTOM) {
            throw Util.newError("You may only specify '" + desc + "' if access='custom'");
        } else {
            return (Level)this.lookup(cube, schemaReader, 4, name);
        }
    }

    private Access getAccess(String accessString, Set<Access> allowed) {
        Access access = Access.valueOf(accessString.toUpperCase());
        if (allowed.contains(access)) {
            return access;
        } else {
            throw Util.newError("Bad value access='" + accessString + "'");
        }
    }

    public mondrian.olap.Dimension createDimension(mondrian.olap.Cube cube, String xml) {
        Object xmlDimension;
        try {
            Parser xmlParser = XOMUtil.createDefaultParser();
            DOMWrapper def = xmlParser.parse(xml);
            String tagName = def.getTagName();
            if (tagName.equals("Dimension")) {
                xmlDimension = new Dimension(def);
            } else {
                if (!tagName.equals("DimensionUsage")) {
                    throw new XOMException("Got <" + tagName + "> when expecting <Dimension> or <DimensionUsage>");
                }

                xmlDimension = new DimensionUsage(def);
            }
        } catch (XOMException var7) {
            throw Util.newError(var7, "Error while adding dimension to cube '" + cube + "' from XML [" + xml + "]");
        }

        return ((RolapCube)cube).createDimension((CubeDimension)xmlDimension, this.xmlSchema);
    }

    public mondrian.olap.Cube createCube(String xml) {
        try {
            Parser xmlParser = XOMUtil.createDefaultParser();
            DOMWrapper def = xmlParser.parse(xml);
            String tagName = def.getTagName();
            RolapCube cube;
            mondrian.olap.MondrianDef.Schema xmlSchema;
            if (tagName.equals("Cube")) {
                xmlSchema = new mondrian.olap.MondrianDef.Schema();
                Cube xmlDimension = new Cube(def);
                cube = new RolapCube(this, xmlSchema, xmlDimension, false);
            } else {
                if (!tagName.equals("VirtualCube")) {
                    throw new XOMException("Got <" + tagName + "> when expecting <Cube>");
                }

                xmlSchema = this.getXMLSchema();
                VirtualCube xmlDimension = new VirtualCube(def);
                cube = new RolapCube(this, xmlSchema, xmlDimension, false);
            }

            return cube;
        } catch (XOMException var8) {
            throw Util.newError(var8, "Error while creating cube from XML [" + xml + "]");
        }
    }

    public static List<RolapSchema> getRolapSchemas() {
        return RolapSchemaPool.instance().getRolapSchemas();
    }

    public static boolean cacheContains(RolapSchema rolapSchema) {
        return RolapSchemaPool.instance().contains(rolapSchema);
    }

    public mondrian.olap.Cube lookupCube(String cube, boolean failIfNotFound) {
        RolapCube mdxCube = this.lookupCube(cube);
        if (mdxCube == null && failIfNotFound) {
            throw MondrianResource.instance().MdxCubeNotFound.ex(cube);
        } else {
            return mdxCube;
        }
    }

    protected RolapCube lookupCube(String cubeName) {
        return (RolapCube)this.mapNameToCube.get(Util.normalizeName(cubeName));
    }

    protected CalculatedMember lookupXmlCalculatedMember(String calcMemberName, String cubeName) {
        Cube[] var3 = this.xmlSchema.cubes;
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Cube cube = var3[var5];
            if (Util.equalName(cube.name, cubeName)) {
                CalculatedMember[] var7 = cube.calculatedMembers;
                int var8 = var7.length;

                for(int var9 = 0; var9 < var8; ++var9) {
                    CalculatedMember xmlCalcMember = var7[var9];
                    if (Util.equalName(calcMemberFqName(xmlCalcMember), calcMemberName)) {
                        return xmlCalcMember;
                    }
                }
            }
        }

        return null;
    }

    public static String calcMemberFqName(CalculatedMember xmlCalcMember) {
        return xmlCalcMember.dimension != null ? Util.makeFqName(Util.quoteMdxIdentifier(xmlCalcMember.dimension), xmlCalcMember.name) : Util.makeFqName(xmlCalcMember.hierarchy, xmlCalcMember.name);
    }

    public List<RolapCube> getCubesWithStar(RolapStar star) {
        List<RolapCube> list = new ArrayList();
        Iterator var3 = this.mapNameToCube.values().iterator();

        while(var3.hasNext()) {
            RolapCube cube = (RolapCube)var3.next();
            if (star == cube.getStar()) {
                list.add(cube);
            }
        }

        return list;
    }

    protected void addCube(RolapCube cube) {
        this.mapNameToCube.put(Util.normalizeName(cube.getName()), cube);
    }

    public boolean removeCube(String cubeName) {
        RolapCube cube = (RolapCube)this.mapNameToCube.remove(Util.normalizeName(cubeName));
        return cube != null;
    }

    public mondrian.olap.Cube[] getCubes() {
        Collection<RolapCube> cubes = this.mapNameToCube.values();
        return (mondrian.olap.Cube[])cubes.toArray(new RolapCube[cubes.size()]);
    }

    public List<RolapCube> getCubeList() {
        return new ArrayList(this.mapNameToCube.values());
    }

    public Hierarchy[] getSharedHierarchies() {
        Collection<RolapHierarchy> hierarchies = this.mapSharedHierarchyNameToHierarchy.values();
        return (Hierarchy[])hierarchies.toArray(new RolapHierarchy[hierarchies.size()]);
    }

    RolapHierarchy getSharedHierarchy(String name) {
        return (RolapHierarchy)this.mapSharedHierarchyNameToHierarchy.get(name);
    }

    public NamedSet getNamedSet(String name) {
        return (NamedSet)this.mapNameToSet.get(name);
    }

    public NamedSet getNamedSet(IdentifierSegment segment) {
        Iterator var2 = this.mapNameToSet.entrySet().iterator();

        Entry entry;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            entry = (Entry)var2.next();
        } while(!Util.matches(segment, (String)entry.getKey()));

        return (NamedSet)entry.getValue();
    }

    public Role lookupRole(String role) {
        return (Role)this.mapNameToRole.get(role);
    }

    public Set<String> roleNames() {
        return this.mapNameToRole.keySet();
    }

    public FunTable getFunTable() {
        return this.funTable;
    }

    public mondrian.olap.Parameter[] getParameters() {
        return (mondrian.olap.Parameter[])this.parameterList.toArray(new mondrian.olap.Parameter[this.parameterList.size()]);
    }

    private void defineFunction(Map<String, UdfFactory> mapNameToUdf, final String name, String className, final ScriptDefinition script) {
        if (className == null && script == null) {
            throw Util.newError("Must specify either className attribute or Script element");
        } else if (className != null && script != null) {
            throw Util.newError("Must not specify both className attribute and Script element");
        } else {
            UdfFactory udfFactory;
            if (className != null) {
                try {
                    Class<mondrian.spi.UserDefinedFunction> klass = ClassResolver.INSTANCE.forName(className, true);
                    udfFactory = new ClassUdfFactory(klass, name);
                } catch (ClassNotFoundException var7) {
                    throw MondrianResource.instance().UdfClassNotFound.ex(name, className);
                }
            } else {
                udfFactory = new UdfFactory() {
                    public mondrian.spi.UserDefinedFunction create() {
                        return Scripts.userDefinedFunction(script, name);
                    }
                };
            }

            this.validateFunction((UdfFactory)udfFactory);
            UdfFactory existingUdf = (UdfFactory)mapNameToUdf.get(name);
            if (existingUdf != null) {
                throw MondrianResource.instance().UdfDuplicateName.ex(name);
            } else {
                mapNameToUdf.put(name, udfFactory);
            }
        }
    }

    private void validateFunction(UdfFactory udfFactory) {
        mondrian.spi.UserDefinedFunction udf = udfFactory.create();
        String udfName = udf.getName();
        if (udfName != null && !udfName.equals("")) {
            String description = udf.getDescription();
            Util.discard(description);
            Type[] parameterTypes = udf.getParameterTypes();

            Type returnType;
            for(int i = 0; i < parameterTypes.length; ++i) {
                returnType = parameterTypes[i];
                if (returnType == null) {
                    throw Util.newInternal("Invalid user-defined function '" + udfName + "': parameter type #" + i + " is null");
                }
            }

            String[] reservedWords = udf.getReservedWords();
            Util.discard(reservedWords);
            returnType = udf.getReturnType(parameterTypes);
            if (returnType == null) {
                throw Util.newInternal("Invalid user-defined function '" + udfName + "': return type is null");
            } else {
                Syntax syntax = udf.getSyntax();
                if (syntax == null) {
                    throw Util.newInternal("Invalid user-defined function '" + udfName + "': syntax is null");
                }
            }
        } else {
            throw Util.newInternal("User-defined function defined by class '" + udf.getClass() + "' has empty name");
        }
    }

    synchronized MemberReader createMemberReader(String sharedName, RolapHierarchy hierarchy, String memberReaderClass) {
        MemberReader reader;
        if (sharedName != null) {
            reader = (MemberReader)this.mapSharedHierarchyToReader.get(sharedName);
            if (reader == null) {
                reader = this.createMemberReader(hierarchy, memberReaderClass);
                if (!this.mapSharedHierarchyNameToHierarchy.containsKey(sharedName)) {
                    this.mapSharedHierarchyNameToHierarchy.put(sharedName, hierarchy);
                }
            }
        } else {
            reader = this.createMemberReader(hierarchy, memberReaderClass);
        }

        return reader;
    }

    private MemberReader createMemberReader(RolapHierarchy hierarchy, String memberReaderClass) {
        mondrian.olap.Dimension dimension;
        if (memberReaderClass != null) {
            Object e2;
            try {
                dimension = null;
                Class<?> clazz = ClassResolver.INSTANCE.forName(memberReaderClass, true);
                Constructor<?> constructor = clazz.getConstructor(RolapHierarchy.class, Properties.class);
                Object o = constructor.newInstance(hierarchy, dimension);
                if (o instanceof MemberReader) {
                    return (MemberReader)o;
                }

                if (o instanceof MemberSource) {
                    return new CacheMemberReader((MemberSource)o);
                }

                throw Util.newInternal("member reader class " + clazz + " does not implement " + MemberSource.class);
            } catch (ClassNotFoundException var8) {
                e2 = var8;
            } catch (NoSuchMethodException var9) {
                e2 = var9;
            } catch (InstantiationException var10) {
                e2 = var10;
            } catch (IllegalAccessException var11) {
                e2 = var11;
            } catch (InvocationTargetException var12) {
                e2 = var12;
            }

            throw Util.newInternal((Throwable)e2, "while instantiating member reader '" + memberReaderClass);
        } else {
            SqlMemberSource source = new SqlMemberSource(hierarchy);
            dimension = hierarchy.getDimension();
            if (dimension.isHighCardinality()) {
                LOGGER.warn(MondrianResource.instance().HighCardinalityInDimension.str(dimension.getUniqueName()));
                LOGGER.debug("High cardinality for " + dimension);
                return new NoCacheMemberReader(source);
            } else {
                LOGGER.debug("Normal cardinality for " + hierarchy.getDimension());
                return (MemberReader)(MondrianProperties.instance().DisableCaching.get() ? new NoCacheMemberReader(source) : new SmartMemberReader(source));
            }
        }
    }

    public SchemaReader getSchemaReader() {
        return (new RolapSchemaReader(this.defaultRole, this)).withLocus();
    }

    private DataSourceChangeListener createDataSourceChangeListener(PropertyList connectInfo) {
        DataSourceChangeListener changeListener = null;
        String dataSourceChangeListenerStr = connectInfo.get(RolapConnectionProperties.DataSourceChangeListener.name());
        if (!Util.isEmpty(dataSourceChangeListenerStr)) {
            try {
                changeListener = (DataSourceChangeListener)ClassResolver.INSTANCE.instantiateSafe(dataSourceChangeListenerStr, new Object[0]);
            } catch (Exception var5) {
                throw Util.newError(var5, "loading DataSourceChangeListener " + dataSourceChangeListenerStr);
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("RolapSchema.createDataSourceChangeListener: create datasource change listener \"" + dataSourceChangeListenerStr);
            }
        }

        return changeListener;
    }

    public ByteString getChecksum() {
        return this.md5Bytes;
    }

    public RolapConnection getInternalConnection() {
        return this.internalConnection;
    }

    RolapStar makeRolapStar(Relation fact) {
        DataSource dataSource = this.getInternalConnection().getDataSource();
        return new RolapStar(this, dataSource, fact);
    }

    public RolapSchema.RolapStarRegistry getRolapStarRegistry() {
        return this.rolapStarRegistry;
    }

    public RolapStar getStar(String factTableName) {
        return this.getStar(RolapUtil.makeRolapStarKey(factTableName));
    }

    public RolapStar getStar(List<String> starKey) {
        return this.getRolapStarRegistry().getStar(starKey);
    }

    public Collection<RolapStar> getStars() {
        return this.getRolapStarRegistry().getStars();
    }

    RolapNativeRegistry getNativeRegistry() {
        return this.nativeRegistry;
    }

    public DataSourceChangeListener getDataSourceChangeListener() {
        return this.dataSourceChangeListener;
    }

    public void setDataSourceChangeListener(DataSourceChangeListener dataSourceChangeListener) {
        this.dataSourceChangeListener = dataSourceChangeListener;
    }

    static {
        schemaAllowed = Olap4jUtil.enumSetOf(Access.NONE, new Access[]{Access.ALL, Access.ALL_DIMENSIONS, Access.CUSTOM});
        cubeAllowed = Olap4jUtil.enumSetOf(Access.NONE, new Access[]{Access.ALL, Access.CUSTOM});
        dimensionAllowed = Olap4jUtil.enumSetOf(Access.NONE, new Access[]{Access.ALL, Access.CUSTOM});
        hierarchyAllowed = Olap4jUtil.enumSetOf(Access.NONE, new Access[]{Access.ALL, Access.CUSTOM});
        memberAllowed = Olap4jUtil.enumSetOf(Access.NONE, new Access[]{Access.ALL});
    }

    private interface XmlLocation {
    }

    static class RolapSchemaFunctionTable extends FunTableImpl {
        private final List<UdfFactory> udfFactoryList;

        RolapSchemaFunctionTable(Collection<UdfFactory> udfs) {
            this.udfFactoryList = new ArrayList(udfs);
        }

        public void defineFunctions(Builder builder) {
            FunTable globalFunTable = GlobalFunTable.instance();
            Iterator var3 = globalFunTable.getReservedWords().iterator();

            while(var3.hasNext()) {
                String reservedWord = (String)var3.next();
                builder.defineReserved(reservedWord);
            }

            var3 = globalFunTable.getResolvers().iterator();

            while(var3.hasNext()) {
                Resolver resolver = (Resolver)var3.next();
                builder.define(resolver);
            }

            var3 = this.udfFactoryList.iterator();

            while(var3.hasNext()) {
                UdfFactory udfFactory = (UdfFactory)var3.next();
                builder.define(new UdfResolver(udfFactory));
            }

        }
    }

    public class RolapStarRegistry {
        private final Map<List<String>, RolapStar> stars = new HashMap();

        RolapStarRegistry() {
        }

        synchronized RolapStar getOrCreateStar(Relation fact) {
            List<String> rolapStarKey = RolapUtil.makeRolapStarKey(fact);
            RolapStar star = (RolapStar)this.stars.get(rolapStarKey);
            if (star == null) {
                star = RolapSchema.this.makeRolapStar(fact);
                this.stars.put(rolapStarKey, star);
                MondrianServer.forConnection(RolapSchema.this.internalConnection).getAggregationManager().getCacheMgr(RolapSchema.this.internalConnection).loadCacheForStar(star);
            }

            return star;
        }

        synchronized RolapStar getStar(List<String> starKey) {
            return (RolapStar)this.stars.get(starKey);
        }

        synchronized Collection<RolapStar> getStars() {
            return this.stars.values();
        }
    }
}