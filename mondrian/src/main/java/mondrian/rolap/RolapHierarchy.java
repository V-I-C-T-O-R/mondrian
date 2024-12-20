/* Decompiler 751ms, total 1468ms, lines 894 */
package mondrian.rolap;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import mondrian.calc.Calc;
import mondrian.calc.DummyExp;
import mondrian.calc.ExpCompiler;
import mondrian.calc.ListCalc;
import mondrian.calc.TupleList;
import mondrian.calc.impl.AbstractListCalc;
import mondrian.calc.impl.ConstantCalc;
import mondrian.calc.impl.UnaryTupleList;
import mondrian.calc.impl.ValueCalc;
import mondrian.mdx.HierarchyExpr;
import mondrian.mdx.ResolvedFunCall;
import mondrian.mdx.UnresolvedFunCall;
import mondrian.olap.Access;
import mondrian.olap.Annotation;
import mondrian.olap.Dimension;
import mondrian.olap.DimensionType;
import mondrian.olap.Evaluator;
import mondrian.olap.Exp;
import mondrian.olap.Formula;
import mondrian.olap.HierarchyBase;
import mondrian.olap.LevelType;
import mondrian.olap.MatchType;
import mondrian.olap.Member;
import mondrian.olap.MondrianProperties;
import mondrian.olap.OlapElement;
import mondrian.olap.Property;
import mondrian.olap.Role;
import mondrian.olap.SchemaReader;
import mondrian.olap.Syntax;
import mondrian.olap.Util;
import mondrian.olap.Validator;
import mondrian.olap.Id.NameSegment;
import mondrian.olap.Id.Quoting;
import mondrian.olap.Id.Segment;
import mondrian.olap.Member.MemberType;
import mondrian.olap.MondrianDef.Annotations;
import mondrian.olap.MondrianDef.Closure;
import mondrian.olap.MondrianDef.Column;
import mondrian.olap.MondrianDef.CubeDimension;
import mondrian.olap.MondrianDef.DimensionUsage;
import mondrian.olap.MondrianDef.Expression;
import mondrian.olap.MondrianDef.Hierarchy;
import mondrian.olap.MondrianDef.InlineTable;
import mondrian.olap.MondrianDef.Join;
import mondrian.olap.MondrianDef.Level;
import mondrian.olap.MondrianDef.Relation;
import mondrian.olap.MondrianDef.RelationOrJoin;
import mondrian.olap.MondrianDef.VirtualCubeDimension;
import mondrian.olap.OlapElement.LocalizedProperty;
import mondrian.olap.Role.HierarchyAccess;
import mondrian.olap.Role.RollupPolicy;
import mondrian.olap.fun.BuiltinFunTable;
import mondrian.olap.fun.FunDefBase;
import mondrian.olap.fun.FunUtil;
import mondrian.olap.fun.AggregateFunDef.AggregateCalc;
import mondrian.olap.type.NumericType;
import mondrian.olap.type.SetType;
import mondrian.resource.MondrianResource;
import mondrian.rolap.RestrictedMemberReader.MultiCardinalityDefaultMember;
import mondrian.rolap.RolapLevel.HideMemberCondition;
import mondrian.rolap.RolapResult.CellFormatterValueFormatter;
import mondrian.rolap.RolapResult.ValueFormatter;
import mondrian.rolap.RolapStar.Condition;
import mondrian.rolap.RolapStar.Table;
import mondrian.rolap.SqlStatement.Type;
import mondrian.rolap.SubstitutingMemberReader.SubstitutingMemberList;
import mondrian.rolap.format.FormatterCreateContext;
import mondrian.rolap.format.FormatterFactory;
import mondrian.rolap.format.FormatterCreateContext.Builder;
import mondrian.rolap.sql.MemberChildrenConstraint;
import mondrian.rolap.sql.SqlQuery;
import mondrian.spi.CellFormatter;
import mondrian.spi.Dialect.Datatype;
import mondrian.util.UnionIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RolapHierarchy extends HierarchyBase {
    private static final Logger LOGGER = LogManager.getLogger(RolapHierarchy.class);
    private MemberReader memberReader;
    protected Hierarchy xmlHierarchy;
    private String memberReaderClass;
    protected RelationOrJoin relation;
    private Member defaultMember;
    private String defaultMemberName;
    private RolapHierarchy.RolapNullMember nullMember;
    private String sharedHierarchyName;
    private String uniqueKeyLevelName;
    private Exp aggregateChildrenExpression;
    protected final RolapLevel nullLevel;
    private RolapMemberBase allMember;
    private static final String ALL_LEVEL_CARDINALITY = "1";
    private final Map<String, Annotation> annotationMap;
    final RolapHierarchy closureFor;
    protected String displayFolder;

    RolapHierarchy(RolapDimension dimension, String subName, String caption, boolean visible, String description, String displayFolder, boolean hasAll, RolapHierarchy closureFor, Map<String, Annotation> annotationMap) {
        super(dimension, subName, caption, visible, description, hasAll);
        this.displayFolder = null;
        this.displayFolder = displayFolder;
        this.annotationMap = annotationMap;
        this.allLevelName = "(All)";
        this.allMemberName = subName == null || !MondrianProperties.instance().SsasCompatibleNaming.get() && !this.name.equals(subName + "." + subName) ? "All " + this.name + "s" : "All " + subName + "s";
        this.closureFor = closureFor;
        if (hasAll) {
            this.levels = new RolapLevel[1];
            this.levels[0] = new RolapLevel(this, this.allLevelName, (String)null, true, (String)null, 0, (Expression)null, (Expression)null, (Expression)null, (Expression)null, (Expression)null, (String)null, (Closure)null, RolapProperty.emptyArray, 6, (Datatype)null, (Type)null, HideMemberCondition.Never, LevelType.Regular, "", Collections.emptyMap());
        } else {
            this.levels = new RolapLevel[0];
        }

        this.nullLevel = new RolapLevel(this, this.allLevelName, (String)null, true, (String)null, 0, (Expression)null, (Expression)null, (Expression)null, (Expression)null, (Expression)null, (String)null, (Closure)null, RolapProperty.emptyArray, 6, (Datatype)null, (Type)null, HideMemberCondition.Never, LevelType.Null, "", Collections.emptyMap());
    }

    RolapHierarchy(RolapCube cube, RolapDimension dimension, Hierarchy xmlHierarchy, CubeDimension xmlCubeDimension) {
        this(dimension, xmlHierarchy.name, xmlHierarchy.caption, xmlHierarchy.visible, xmlHierarchy.description, xmlHierarchy.displayFolder, xmlHierarchy.hasAll, (RolapHierarchy)null, createAnnotationMap(xmlHierarchy.annotations));

        assert !(this instanceof RolapCubeHierarchy);

        this.xmlHierarchy = xmlHierarchy;
        RelationOrJoin xmlHierarchyRelation = RolapUtil.processRelation(((RolapSchema)dimension.getSchema()).getXMLSchema(), xmlHierarchy.relation);
        if (xmlHierarchy.relation == null && xmlHierarchy.memberReaderClass == null && cube != null) {
            if (cube.isVirtual()) {
                String cubeName = ((VirtualCubeDimension)xmlCubeDimension).cubeName;
                RolapCube sourceCube = cube.getSchema().lookupCube(cubeName);
                if (sourceCube != null) {
                    xmlHierarchyRelation = sourceCube.getFact();
                }
            } else {
                xmlHierarchyRelation = cube.getFact();
            }
        }

        this.relation = xmlHierarchyRelation;
        if (xmlHierarchyRelation instanceof InlineTable) {
            this.relation = RolapUtil.convertInlineTableToRelation((InlineTable)xmlHierarchyRelation, this.getRolapSchema().getDialect());
        }

        this.memberReaderClass = xmlHierarchy.memberReaderClass;
        this.uniqueKeyLevelName = xmlHierarchy.uniqueKeyLevelName;
        if (xmlHierarchy.allMemberName != null) {
            this.allMemberName = xmlHierarchy.allMemberName;
        }

        if (xmlHierarchy.allLevelName != null) {
            this.allLevelName = xmlHierarchy.allLevelName;
        }

        RolapLevel allLevel = new RolapLevel(this, this.allLevelName, (String)null, true, (String)null, 0, (Expression)null, (Expression)null, (Expression)null, (Expression)null, (Expression)null, (String)null, (Closure)null, RolapProperty.emptyArray, 6, (Datatype)null, (Type)null, HideMemberCondition.Never, LevelType.Regular, "1", Collections.emptyMap());
        allLevel.init(xmlCubeDimension);
        this.allMember = new RolapMemberBase((RolapMember)null, allLevel, RolapUtil.sqlNullValue, this.allMemberName, MemberType.ALL);
        if (xmlHierarchy.allMemberCaption != null && xmlHierarchy.allMemberCaption.length() > 0) {
            this.allMember.setCaption(xmlHierarchy.allMemberCaption);
        }

        this.allMember.setOrdinal(0);
        if (xmlHierarchy.levels.length == 0) {
            throw MondrianResource.instance().HierarchyHasNoLevels.ex(this.getUniqueName());
        } else {
            Set<String> levelNameSet = new HashSet();
            Level[] var8 = xmlHierarchy.levels;
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                Level level = var8[var10];
                if (!levelNameSet.add(level.name)) {
                    throw MondrianResource.instance().HierarchyLevelNamesNotUnique.ex(this.getUniqueName(), level.name);
                }
            }

            int i;
            if (this.hasAll) {
                this.levels = new RolapLevel[xmlHierarchy.levels.length + 1];
                this.levels[0] = allLevel;

                for(i = 0; i < xmlHierarchy.levels.length; ++i) {
                    Level xmlLevel = xmlHierarchy.levels[i];
                    if (xmlLevel.getKeyExp() == null && xmlHierarchy.memberReaderClass == null) {
                        throw MondrianResource.instance().LevelMustHaveNameExpression.ex(xmlLevel.name);
                    }

                    this.levels[i + 1] = new RolapLevel(this, i + 1, xmlLevel);
                }
            } else {
                this.levels = new RolapLevel[xmlHierarchy.levels.length];

                for(i = 0; i < xmlHierarchy.levels.length; ++i) {
                    this.levels[i] = new RolapLevel(this, i, xmlHierarchy.levels[i]);
                }
            }

            if (xmlCubeDimension instanceof DimensionUsage) {
                String sharedDimensionName = ((DimensionUsage)xmlCubeDimension).source;
                this.sharedHierarchyName = sharedDimensionName;
                if (this.subName != null) {
                    this.sharedHierarchyName = this.sharedHierarchyName + "." + this.subName;
                }
            } else {
                this.sharedHierarchyName = null;
            }

            if (xmlHierarchyRelation != null && xmlHierarchy.memberReaderClass != null) {
                throw MondrianResource.instance().HierarchyMustNotHaveMoreThanOneSource.ex(this.getUniqueName());
            } else {
                if (!Util.isEmpty(xmlHierarchy.caption)) {
                    this.setCaption(xmlHierarchy.caption);
                }

                this.defaultMemberName = xmlHierarchy.defaultMember;
            }
        }
    }

    public static Map<String, Annotation> createAnnotationMap(Annotations annotations) {
        if (annotations != null && annotations.array != null && annotations.array.length != 0) {
            Map<String, Annotation> map = new LinkedHashMap();
            mondrian.olap.MondrianDef.Annotation[] var2 = annotations.array;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                mondrian.olap.MondrianDef.Annotation annotation = var2[var4];
                final String name = annotation.name;
                final String value = annotation.cdata;
                map.put(annotation.name, new Annotation() {
                    public String getName() {
                        return name;
                    }

                    public Object getValue() {
                        return value;
                    }
                });
            }

            return map;
        } else {
            return Collections.emptyMap();
        }
    }

    protected Logger getLogger() {
        return LOGGER;
    }

    public String getDisplayFolder() {
        return this.displayFolder;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof RolapHierarchy)) {
            return false;
        } else {
            RolapHierarchy that = (RolapHierarchy)o;
            if (this.sharedHierarchyName != null && that.sharedHierarchyName != null) {
                return this.sharedHierarchyName.equals(that.sharedHierarchyName) && this.getUniqueName().equals(that.getUniqueName());
            } else {
                return false;
            }
        }
    }

    protected int computeHashCode() {
        return super.computeHashCode() ^ (this.sharedHierarchyName == null ? 0 : this.sharedHierarchyName.hashCode());
    }

    void init(CubeDimension xmlDimension) {
        if (this.memberReader == null) {
            this.memberReader = this.getRolapSchema().createMemberReader(this.sharedHierarchyName, this, this.memberReaderClass);
        }

        mondrian.olap.Level[] var2 = this.levels;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            mondrian.olap.Level level = var2[var4];
            ((RolapLevel)level).init(xmlDimension);
        }

        if (this.defaultMemberName != null) {
            List uniqueNameParts;
            if (this.defaultMemberName.contains("[")) {
                uniqueNameParts = Util.parseIdentifier(this.defaultMemberName);
            } else {
                uniqueNameParts = Collections.singletonList(new NameSegment(this.defaultMemberName, Quoting.UNQUOTED));
            }

            this.defaultMember = (Member)Util.lookupCompound(this.getRolapSchema().getSchemaReader(), this, uniqueNameParts, false, 6, MatchType.EXACT);
            if (this.defaultMember == null) {
                this.defaultMember = (Member)Util.lookupCompound(this.getRolapSchema().getSchemaReader(), new RolapHierarchy.DummyElement(), uniqueNameParts, false, 6, MatchType.EXACT);
            }

            if (this.defaultMember == null) {
                throw Util.newInternal("Can not find Default Member with name \"" + this.defaultMemberName + "\" in Hierarchy \"" + this.getName() + "\"");
            }
        }

    }

    void setMemberReader(MemberReader memberReader) {
        this.memberReader = memberReader;
    }

    MemberReader getMemberReader() {
        return this.memberReader;
    }

    public Map<String, Annotation> getAnnotationMap() {
        return this.annotationMap;
    }

    RolapLevel newMeasuresLevel() {
        RolapLevel level = new RolapLevel(this, "MeasuresLevel", (String)null, true, (String)null, this.levels.length, (Expression)null, (Expression)null, (Expression)null, (Expression)null, (Expression)null, (String)null, (Closure)null, RolapProperty.emptyArray, 0, (Datatype)null, (Type)null, HideMemberCondition.Never, LevelType.Regular, "", Collections.emptyMap());
        this.levels = (mondrian.olap.Level[])Util.append(this.levels, level);
        return level;
    }

    Relation getUniqueTable() {
        if (this.relation instanceof Relation) {
            return (Relation)this.relation;
        } else if (this.relation instanceof Join) {
            return null;
        } else {
            throw Util.newInternal("hierarchy's relation is a " + this.relation.getClass());
        }
    }

    boolean tableExists(String tableName) {
        return this.relation != null && getTable(tableName, this.relation) != null;
    }

    Relation getTable(String tableName) {
        return this.relation == null ? null : getTable(tableName, this.relation);
    }

    private static Relation getTable(String tableName, RelationOrJoin relationOrJoin) {
        if (relationOrJoin instanceof Relation) {
            Relation relation = (Relation)relationOrJoin;
            return relation.getAlias().equals(tableName) ? relation : null;
        } else {
            Join join = (Join)relationOrJoin;
            Relation rel = getTable(tableName, join.left);
            return rel != null ? rel : getTable(tableName, join.right);
        }
    }

    public RolapSchema getRolapSchema() {
        return (RolapSchema)this.dimension.getSchema();
    }

    public RelationOrJoin getRelation() {
        return this.relation;
    }

    public Hierarchy getXmlHierarchy() {
        return this.xmlHierarchy;
    }

    public Member getDefaultMember() {
        if (this.defaultMember == null) {
            List<RolapMember> rootMembers = this.memberReader.getRootMembers();
            SchemaReader schemaReader = this.getRolapSchema().getSchemaReader();
            List<RolapMember> calcMemberList = Util.cast(schemaReader.getCalculatedMembers(this.getLevels()[0]));
            Iterator var4 = UnionIterator.over(new Collection[]{rootMembers, calcMemberList}).iterator();

            while(var4.hasNext()) {
                RolapMember rootMember = (RolapMember)var4.next();
                if (!rootMember.isHidden()) {
                    this.defaultMember = rootMember;
                    break;
                }
            }

            if (this.defaultMember == null) {
                throw MondrianResource.instance().InvalidHierarchyCondition.ex(this.getUniqueName());
            }
        }

        return this.defaultMember;
    }

    public Member getNullMember() {
        if (this.nullMember == null) {
            this.nullMember = new RolapHierarchy.RolapNullMember(this.nullLevel);
        }

        return this.nullMember;
    }

    public RolapMember getAllMember() {
        return this.allMember;
    }

    public Member createMember(Member parent, mondrian.olap.Level level, String name, Formula formula) {
        if (formula == null) {
            return new RolapMemberBase((RolapMember)parent, (RolapLevel)level, name);
        } else {
            return (Member)(level.getDimension().isMeasures() ? new RolapHierarchy.RolapCalculatedMeasure((RolapMember)parent, (RolapLevel)level, name, formula) : new RolapCalculatedMember((RolapMember)parent, (RolapLevel)level, name, formula));
        }
    }

    String getAlias() {
        return this.getName();
    }

    public String getSharedHierarchyName() {
        return this.sharedHierarchyName;
    }

    void addToFromInverse(SqlQuery query, Expression expression) {
        if (this.relation == null) {
            throw Util.newError("cannot add hierarchy " + this.getUniqueName() + " to query: it does not have a <Table>, <View> or <Join>");
        } else {
            boolean failIfExists = false;
            RelationOrJoin subRelation = this.relation;
            if (this.relation instanceof Join && expression != null) {
                subRelation = relationSubsetInverse(this.relation, expression.getTableAlias());
            }

            query.addFrom(subRelation, (String)null, false);
        }
    }

    void addToFrom(SqlQuery query, Expression expression) {
        if (this.relation == null) {
            throw Util.newError("cannot add hierarchy " + this.getUniqueName() + " to query: it does not have a <Table>, <View> or <Join>");
        } else {
            query.registerRootRelation(this.relation);
            boolean failIfExists = false;
            RelationOrJoin subRelation = this.relation;
            if (this.relation instanceof Join && expression != null) {
                subRelation = relationSubset(this.relation, expression.getTableAlias());
                if (subRelation == null) {
                    subRelation = this.relation;
                }
            }

            query.addFrom(subRelation, expression == null ? null : expression.getTableAlias(), false);
        }
    }

    void addToFrom(SqlQuery query, Table table) {
        if (this.getRelation() == null) {
            throw Util.newError("cannot add hierarchy " + this.getUniqueName() + " to query: it does not have a <Table>, <View> or <Join>");
        } else {
            boolean failIfExists = false;
            RelationOrJoin subRelation = null;
            if (table != null) {
                subRelation = lookupRelationSubset(this.getRelation(), table);
            }

            if (subRelation == null) {
                subRelation = this.getRelation();
            }

            boolean tableAdded = query.addFrom(subRelation, table != null ? table.getAlias() : null, false);
            Condition joinCondition;
            if (tableAdded && table != null) {
                do {
                    joinCondition = table.getJoinCondition();
                    if (joinCondition != null) {
                        query.addWhere(joinCondition);
                    }

                    table = table.getParentTable();
                } while(joinCondition != null);
            }

        }
    }

    private static RelationOrJoin relationSubsetInverse(RelationOrJoin relation, String alias) {
        if (relation instanceof Relation) {
            Relation table = (Relation)relation;
            return table.getAlias().equals(alias) ? relation : null;
        } else if (relation instanceof Join) {
            Join join = (Join)relation;
            RelationOrJoin leftRelation = relationSubsetInverse(join.left, alias);
            return (RelationOrJoin)(leftRelation == null ? relationSubsetInverse(join.right, alias) : join);
        } else {
            throw Util.newInternal("bad relation type " + relation);
        }
    }

    private static RelationOrJoin relationSubset(RelationOrJoin relation, String alias) {
        if (relation instanceof Relation) {
            Relation table = (Relation)relation;
            return table.getAlias().equals(alias) ? relation : null;
        } else if (relation instanceof Join) {
            Join join = (Join)relation;
            RelationOrJoin rightRelation = relationSubset(join.right, alias);
            return (RelationOrJoin)(rightRelation == null ? relationSubset(join.left, alias) : (MondrianProperties.instance().FilterChildlessSnowflakeMembers.get() ? join : rightRelation));
        } else {
            throw Util.newInternal("bad relation type " + relation);
        }
    }

    private static RelationOrJoin lookupRelationSubset(RelationOrJoin relation, Table targetTable) {
        if (relation instanceof mondrian.olap.MondrianDef.Table) {
            mondrian.olap.MondrianDef.Table table = (mondrian.olap.MondrianDef.Table)relation;
            return table.name.equals(targetTable.getTableName()) ? relation : null;
        } else if (relation instanceof Join) {
            Join join = (Join)relation;
            RelationOrJoin rightRelation = lookupRelationSubset(join.right, targetTable);
            return (RelationOrJoin)(rightRelation == null ? lookupRelationSubset(join.left, targetTable) : join);
        } else {
            return null;
        }
    }

    MemberReader createMemberReader(Role role) {
        Access access = role.getAccess(this);
        switch(access) {
            case NONE:
                role.getAccess(this);
                throw Util.newInternal("Illegal access to members of hierarchy " + this);
            case ALL:
                return (MemberReader)(this.isRagged() ? new SmartRestrictedMemberReader(this.getMemberReader(), role) : this.getMemberReader());
            case CUSTOM:
                final HierarchyAccess hierarchyAccess = role.getAccessDetails(this);
                RollupPolicy rollupPolicy = hierarchyAccess.getRollupPolicy();
                final NumericType returnType = new NumericType();
                switch(rollupPolicy) {
                    case FULL:
                        return new SmartRestrictedMemberReader(this.getMemberReader(), role);
                    case PARTIAL:
                        mondrian.olap.type.Type memberType1 = new mondrian.olap.type.MemberType(this.getDimension(), this, (mondrian.olap.Level)null, (Member)null);
                        SetType setType = new SetType(memberType1);
                        ListCalc listCalc = new AbstractListCalc(new DummyExp(setType), new Calc[0]) {
                            public TupleList evaluateList(Evaluator evaluator) {
                                return new UnaryTupleList(RolapHierarchy.this.getLowestMembersForAccess(evaluator, hierarchyAccess, (Map)null));
                            }

                            public boolean dependsOn(mondrian.olap.Hierarchy hierarchy) {
                                return true;
                            }
                        };
                        final Calc partialCalc = new RolapHierarchy.LimitedRollupAggregateCalc(returnType, listCalc);
                        Exp partialExp = new ResolvedFunCall(new FunDefBase("$x", "x", "In") {
                            public Calc compileCall(ResolvedFunCall call, ExpCompiler compiler) {
                                return partialCalc;
                            }

                            public void unparse(Exp[] args, PrintWriter pw) {
                                pw.print("$RollupAccessibleChildren()");
                            }
                        }, new Exp[0], returnType);
                        return new RolapHierarchy.LimitedRollupSubstitutingMemberReader(this.getMemberReader(), role, hierarchyAccess, partialExp);
                    case HIDDEN:
                        Exp hiddenExp = new ResolvedFunCall(new FunDefBase("$x", "x", "In") {
                            public Calc compileCall(ResolvedFunCall call, ExpCompiler compiler) {
                                return new ConstantCalc(returnType, (Object)null);
                            }

                            public void unparse(Exp[] args, PrintWriter pw) {
                                pw.print("$RollupAccessibleChildren()");
                            }
                        }, new Exp[0], returnType);
                        return new RolapHierarchy.LimitedRollupSubstitutingMemberReader(this.getMemberReader(), role, hierarchyAccess, hiddenExp);
                    default:
                        throw Util.unexpected(rollupPolicy);
                }
            default:
                throw Util.badValue(access);
        }
    }

    List<Member> getLowestMembersForAccess(Evaluator evaluator, HierarchyAccess hAccess, Map<Member, Access> membersWithAccess) {
        if (membersWithAccess == null) {
            membersWithAccess = FunUtil.getNonEmptyMemberChildrenWithDetails(evaluator, ((RolapEvaluator)evaluator).getExpanding());
        }

        boolean goesLower = false;
        Iterator var5 = membersWithAccess.keySet().iterator();

        while(var5.hasNext()) {
            Member member = (Member)var5.next();
            Access access = (Access)membersWithAccess.get(member);
            if (access == null) {
                access = hAccess.getAccess(member);
            }

            if (access != Access.ALL) {
                goesLower = true;
                break;
            }
        }

        if (!goesLower) {
            return new ArrayList(membersWithAccess.keySet());
        } else {
            Map<Member, Access> newMap = new HashMap();
            Iterator var13 = membersWithAccess.keySet().iterator();

            while(var13.hasNext()) {
                Member member = (Member)var13.next();
                int savepoint = evaluator.savepoint();

                try {
                    evaluator.setContext(member);
                    newMap.putAll(FunUtil.getNonEmptyMemberChildrenWithDetails(evaluator, member));
                } finally {
                    evaluator.restore(savepoint);
                }
            }

            return this.getLowestMembersForAccess(evaluator, hAccess, newMap);
        }
    }

    public boolean isRagged() {
        mondrian.olap.Level[] var1 = this.levels;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            mondrian.olap.Level level = var1[var3];
            if (((RolapLevel)level).getHideMemberCondition() != HideMemberCondition.Never) {
                return true;
            }
        }

        return false;
    }

    synchronized Exp getAggregateChildrenExpression() {
        if (this.aggregateChildrenExpression == null) {
            UnresolvedFunCall fc = new UnresolvedFunCall("$AggregateChildren", Syntax.Internal, new Exp[]{new HierarchyExpr(this)});
            Validator validator = Util.createSimpleValidator(BuiltinFunTable.instance());
            this.aggregateChildrenExpression = fc.accept(validator);
        }

        return this.aggregateChildrenExpression;
    }

    RolapDimension createClosedPeerDimension(RolapLevel src, Closure clos, CubeDimension xmlDimension) {
        RolapDimension peerDimension = new RolapDimension(this.dimension.getSchema(), this.dimension.getName() + "$Closure", (String)null, true, "Closure dimension for parent-child hierarchy " + this.getName(), DimensionType.StandardDimension, this.dimension.isHighCardinality(), Collections.emptyMap());
        RolapHierarchy peerHier = peerDimension.newHierarchy((String)null, true, this);
        peerHier.allMemberName = this.getAllMemberName();
        peerHier.allMember = (RolapMemberBase)this.getAllMember();
        peerHier.allLevelName = this.getAllLevelName();
        peerHier.sharedHierarchyName = this.getSharedHierarchyName();
        Join join = new Join();
        peerHier.relation = join;
        join.left = clos.table;
        join.leftKey = clos.parentColumn;
        join.right = this.relation;
        join.rightKey = clos.childColumn;
        int index = peerHier.levels.length;
        int flags = src.getFlags() & -5;
        Expression keyExp = new Column(clos.table.name, clos.parentColumn);
        RolapLevel level = new RolapLevel(peerHier, "Closure", this.caption, true, this.description, index++, keyExp, (Expression)null, (Expression)null, (Expression)null, (Expression)null, (String)null, (Closure)null, RolapProperty.emptyArray, flags | 4, src.getDatatype(), (Type)null, src.getHideMemberCondition(), src.getLevelType(), "", Collections.emptyMap());
        peerHier.levels = (mondrian.olap.Level[])Util.append(peerHier.levels, level);
        flags = src.getFlags() | 4;
        keyExp = new Column(clos.table.name, clos.childColumn);
        RolapLevel sublevel = new RolapLevel(peerHier, "Item", (String)null, true, (String)null, index++, keyExp, (Expression)null, (Expression)null, (Expression)null, (Expression)null, (String)null, (Closure)null, RolapProperty.emptyArray, flags, src.getDatatype(), src.getInternalType(), src.getHideMemberCondition(), src.getLevelType(), "", Collections.emptyMap());
        peerHier.levels = (mondrian.olap.Level[])Util.append(peerHier.levels, sublevel);
        return peerDimension;
    }

    public void setDefaultMember(Member defaultMember) {
        if (defaultMember != null) {
            this.defaultMember = defaultMember;
        }

    }

    public String getUniqueKeyLevelName() {
        return this.uniqueKeyLevelName;
    }

    public int getOrdinalInCube() {
        assert this.dimension.isMeasures();

        return 0;
    }

    private class DummyElement implements OlapElement {
        private DummyElement() {
        }

        public String getUniqueName() {
            throw new UnsupportedOperationException();
        }

        public String getName() {
            return "$";
        }

        public String getDescription() {
            throw new UnsupportedOperationException();
        }

        public OlapElement lookupChild(SchemaReader schemaReader, Segment s, MatchType matchType) {
            if (!(s instanceof NameSegment)) {
                return null;
            } else {
                NameSegment nameSegment = (NameSegment)s;
                if (Util.equalName(nameSegment.name, RolapHierarchy.this.dimension.getName())) {
                    return RolapHierarchy.this.dimension;
                } else {
                    return !MondrianProperties.instance().SsasCompatibleNaming.get() && Util.equalName(nameSegment.name, RolapHierarchy.this.dimension.getName() + "." + RolapHierarchy.this.subName) ? RolapHierarchy.this : null;
                }
            }
        }

        public String getQualifiedName() {
            throw new UnsupportedOperationException();
        }

        public String getCaption() {
            throw new UnsupportedOperationException();
        }

        public mondrian.olap.Hierarchy getHierarchy() {
            throw new UnsupportedOperationException();
        }

        public Dimension getDimension() {
            throw new UnsupportedOperationException();
        }

        public boolean isVisible() {
            throw new UnsupportedOperationException();
        }

        public String getLocalized(LocalizedProperty prop, Locale locale) {
            throw new UnsupportedOperationException();
        }

        // $FF: synthetic method
        DummyElement(Object x1) {
            this();
        }
    }

    public static class LimitedRollupAggregateCalc extends AggregateCalc {
        public LimitedRollupAggregateCalc(mondrian.olap.type.Type returnType, ListCalc listCalc) {
            super(new DummyExp(returnType), listCalc, new ValueCalc(new DummyExp(returnType)));
        }
    }

    private static class LimitedRollupSubstitutingMemberReader extends SubstitutingMemberReader {
        private final HierarchyAccess hierarchyAccess;
        private final Exp exp;

        public LimitedRollupSubstitutingMemberReader(MemberReader memberReader, Role role, HierarchyAccess hierarchyAccess, Exp exp) {
            super(new SmartRestrictedMemberReader(memberReader, role));
            this.hierarchyAccess = hierarchyAccess;
            this.exp = exp;
        }

        public Map<? extends Member, Access> getMemberChildren(RolapMember member, List<RolapMember> memberChildren, MemberChildrenConstraint constraint) {
            return this.memberReader.getMemberChildren(member, new SubstitutingMemberList(memberChildren), constraint);
        }

        public Map<? extends Member, Access> getMemberChildren(List<RolapMember> parentMembers, List<RolapMember> children, MemberChildrenConstraint constraint) {
            return this.memberReader.getMemberChildren(parentMembers, new SubstitutingMemberList(children), constraint);
        }

        public RolapMember substitute(RolapMember member, Access access) {
            if (member != null && member instanceof MultiCardinalityDefaultMember) {
                return new RolapHierarchy.LimitedRollupMember((RolapCubeMember)((MultiCardinalityDefaultMember)member).member.getParentMember(), this.exp, this.hierarchyAccess);
            } else if (member != null && (access == Access.CUSTOM || this.hierarchyAccess.hasInaccessibleDescendants(member))) {
                if (member instanceof RolapHierarchy.LimitedRollupMember) {
                    member = ((RolapHierarchy.LimitedRollupMember)member).member;
                }

                return new RolapHierarchy.LimitedRollupMember((RolapCubeMember)member, this.exp, this.hierarchyAccess);
            } else {
                return member;
            }
        }

        public RolapMember substitute(RolapMember member) {
            return member == null ? null : this.substitute(member, this.hierarchyAccess.getAccess(member));
        }

        public RolapMember desubstitute(RolapMember member) {
            return member instanceof RolapHierarchy.LimitedRollupMember ? ((RolapHierarchy.LimitedRollupMember)member).member : member;
        }
    }

    public static class LimitedRollupMember extends RolapCubeMember {
        public final RolapMember member;
        private final Exp exp;
        final HierarchyAccess hierarchyAccess;

        LimitedRollupMember(RolapCubeMember member, Exp exp, HierarchyAccess hierarchyAccess) {
            super(member.getParentMember(), member.getRolapMember(), member.getLevel());
            this.hierarchyAccess = hierarchyAccess;

            assert !(member instanceof RolapHierarchy.LimitedRollupMember);

            this.member = member;
            this.exp = exp;
        }

        public boolean equals(Object o) {
            return o instanceof RolapHierarchy.LimitedRollupMember && ((RolapHierarchy.LimitedRollupMember)o).member.equals(this.member);
        }

        public int hashCode() {
            return this.member.hashCode();
        }

        public Exp getExpression() {
            return this.exp;
        }

        protected boolean computeCalculated(MemberType memberType) {
            return true;
        }

        public boolean isCalculated() {
            return false;
        }

        public boolean isEvaluated() {
            return true;
        }

        public RolapMember getSourceMember() {
            return this.member;
        }
    }

    protected class RolapCalculatedMeasure extends RolapCalculatedMember implements RolapMeasure {
        private ValueFormatter cellFormatter;

        public RolapCalculatedMeasure(RolapMember parent, RolapLevel level, String name, Formula formula) {
            super(parent, level, name, formula);
        }

        public synchronized void setProperty(String name, Object value) {
            String language;
            if (name.equals(Property.CELL_FORMATTER.getName())) {
                language = (String)value;
                FormatterCreateContext formatterContext = (new Builder(this.getUniqueName())).formatterAttr(language).build();
                this.setCellFormatter(FormatterFactory.instance().createCellFormatter(formatterContext));
            }

            if (name.equals(Property.CELL_FORMATTER_SCRIPT.name)) {
                language = (String)this.getPropertyValue(Property.CELL_FORMATTER_SCRIPT_LANGUAGE.name);
                String scriptText = (String)value;
                FormatterCreateContext formatterContextx = (new Builder(this.getUniqueName())).script(scriptText, language).build();
                this.setCellFormatter(FormatterFactory.instance().createCellFormatter(formatterContextx));
            }

            super.setProperty(name, value);
        }

        public ValueFormatter getFormatter() {
            return this.cellFormatter;
        }

        private void setCellFormatter(CellFormatter cellFormatter) {
            if (cellFormatter != null) {
                this.cellFormatter = new CellFormatterValueFormatter(cellFormatter);
            }

        }
    }

    static class RolapNullMember extends RolapMemberBase {
        RolapNullMember(RolapLevel level) {
            super((RolapMember)null, level, RolapUtil.sqlNullValue, RolapUtil.mdxNullLiteral(), MemberType.NULL);

            assert level != null;

        }
    }
}