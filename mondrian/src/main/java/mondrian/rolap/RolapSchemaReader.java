/* Decompiler 664ms, total 2752ms, lines 585 */
package mondrian.rolap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import mondrian.calc.Calc;
import mondrian.calc.DummyExp;
import mondrian.calc.ExpCompiler;
import mondrian.calc.impl.AbstractCalc;
import mondrian.calc.impl.GenericCalc;
import mondrian.olap.Access;
import mondrian.olap.Cube;
import mondrian.olap.Dimension;
import mondrian.olap.Evaluator;
import mondrian.olap.Exp;
import mondrian.olap.FunDef;
import mondrian.olap.Hierarchy;
import mondrian.olap.Level;
import mondrian.olap.Literal;
import mondrian.olap.MatchType;
import mondrian.olap.Member;
import mondrian.olap.MondrianProperties;
import mondrian.olap.NameResolver;
import mondrian.olap.NamedSet;
import mondrian.olap.NativeEvaluator;
import mondrian.olap.OlapElement;
import mondrian.olap.Parameter;
import mondrian.olap.ParameterImpl;
import mondrian.olap.Role;
import mondrian.olap.SchemaReader;
import mondrian.olap.Util;
import mondrian.olap.Id.NameSegment;
import mondrian.olap.Id.Segment;
import mondrian.olap.NameResolver.Namespace;
import mondrian.olap.Parameter.Scope;
import mondrian.olap.Role.HierarchyAccess;
import mondrian.olap.type.StringType;
import mondrian.rolap.RolapCubeHierarchy.RolapCubeHierarchyMemberReader;
import mondrian.rolap.RolapHierarchy.LimitedRollupMember;
import mondrian.rolap.RolapNativeSet.SchemaReaderWithMemberReaderAvailable;
import mondrian.rolap.sql.MemberChildrenConstraint;
import mondrian.rolap.sql.TupleConstraint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eigenbase.util.property.Property;
import org.olap4j.mdx.IdentifierSegment;

public class RolapSchemaReader implements SchemaReader, SchemaReaderWithMemberReaderAvailable, Namespace {
    protected final Role role;
    private final Map<Hierarchy, MemberReader> hierarchyReaders = new ConcurrentHashMap();
    protected final RolapSchema schema;
    private final SqlConstraintFactory sqlConstraintFactory = SqlConstraintFactory.instance();
    private static final Logger LOGGER = LogManager.getLogger(RolapSchemaReader.class);

    RolapSchemaReader(Role role, RolapSchema schema) {
        assert role != null : "precondition: role != null";

        assert schema != null;

        this.role = role;
        this.schema = schema;
    }

    public Role getRole() {
        return this.role;
    }

    public List<Member> getHierarchyRootMembers(Hierarchy hierarchy) {
        HierarchyAccess hierarchyAccess = this.role.getAccessDetails(hierarchy);
        Level[] levels = hierarchy.getLevels();
        Level firstLevel;
        if (hierarchyAccess == null) {
            firstLevel = levels[0];
        } else {
            firstLevel = levels[hierarchyAccess.getTopLevelDepth()];
        }

        return this.getLevelMembers(firstLevel, true);
    }

    public MemberReader getMemberReader(Hierarchy hierarchy) {
        MemberReader memberReader = (MemberReader)this.hierarchyReaders.get(hierarchy);
        if (memberReader == null) {
            synchronized(this) {
                memberReader = (MemberReader)this.hierarchyReaders.get(hierarchy);
                if (memberReader == null) {
                    memberReader = ((RolapHierarchy)hierarchy).createMemberReader(this.role);
                    this.hierarchyReaders.put(hierarchy, memberReader);
                }
            }
        }

        return memberReader;
    }

    public Member substitute(Member member) {
        MemberReader memberReader = this.getMemberReader(member.getHierarchy());
        return memberReader.substitute((RolapMember)member);
    }

    public void getMemberRange(Level level, Member startMember, Member endMember, List<Member> list) {
        this.getMemberReader(level.getHierarchy()).getMemberRange((RolapLevel)level, (RolapMember)startMember, (RolapMember)endMember, Util.cast(list));
    }

    public int compareMembersHierarchically(Member m1, Member m2) {
        RolapMember member1 = (RolapMember)m1;
        RolapMember member2 = (RolapMember)m2;
        RolapHierarchy hierarchy = member1.getHierarchy();
        Util.assertPrecondition(hierarchy == m2.getHierarchy());
        return this.getMemberReader(hierarchy).compare(member1, member2, true);
    }

    public Member getMemberParent(Member member) {
        return this.getMemberReader(member.getHierarchy()).getMemberParent((RolapMember)member);
    }

    public int getMemberDepth(Member member) {
        HierarchyAccess hierarchyAccess = this.role.getAccessDetails(member.getHierarchy());
        int depth;
        if (hierarchyAccess != null) {
            depth = member.getLevel().getDepth();
            int topLevelDepth = hierarchyAccess.getTopLevelDepth();
            return depth - topLevelDepth;
        } else if (!((RolapLevel)member.getLevel()).isParentChild()) {
            return member.getLevel().getDepth();
        } else {
            depth = 0;

            for(Member m = member.getParentMember(); m != null; m = m.getParentMember()) {
                ++depth;
            }

            return depth;
        }
    }

    public List<Member> getMemberChildren(Member member) {
        return this.getMemberChildren((Member)member, (Evaluator)null);
    }

    public List<Member> getMemberChildren(Member member, Evaluator context) {
        MemberChildrenConstraint constraint = this.sqlConstraintFactory.getMemberChildrenConstraint(context);
        List<RolapMember> memberList = this.internalGetMemberChildren(member, constraint);
        return Util.cast(memberList);
    }

    private List<RolapMember> internalGetMemberChildren(Member member, MemberChildrenConstraint constraint) {
        List<RolapMember> children = new ArrayList();
        Hierarchy hierarchy = member.getHierarchy();
        MemberReader memberReader = this.getMemberReader(hierarchy);
        memberReader.getMemberChildren((RolapMember)member, children, constraint);
        return children;
    }

    public void getParentChildContributingChildren(Member dataMember, Hierarchy hierarchy, List<Member> list) {
        List<RolapMember> rolapMemberList = Util.cast(list);
        list.add(dataMember);
        ((RolapHierarchy)hierarchy).getMemberReader().getMemberChildren((RolapMember)dataMember, rolapMemberList);
    }

    public int getChildrenCountFromCache(Member member) {
        Hierarchy hierarchy = member.getHierarchy();
        MemberReader memberReader = this.getMemberReader(hierarchy);
        List list;
        if (memberReader instanceof RolapCubeHierarchyMemberReader) {
            list = ((RolapCubeHierarchyMemberReader)memberReader).getRolapCubeMemberCacheHelper().getChildrenFromCache((RolapMember)member, (MemberChildrenConstraint)null);
            return list == null ? -1 : list.size();
        } else if (memberReader instanceof SmartMemberReader) {
            list = ((SmartMemberReader)memberReader).getMemberCache().getChildrenFromCache((RolapMember)member, (MemberChildrenConstraint)null);
            return list == null ? -1 : list.size();
        } else if (!(memberReader instanceof MemberCache)) {
            return -1;
        } else {
            list = ((MemberCache)memberReader).getChildrenFromCache((RolapMember)member, (MemberChildrenConstraint)null);
            return list == null ? -1 : list.size();
        }
    }

    private int getLevelCardinalityFromCache(Level level) {
        Hierarchy hierarchy = level.getHierarchy();
        MemberReader memberReader = this.getMemberReader(hierarchy);
        if (memberReader instanceof RolapCubeHierarchyMemberReader) {
            MemberCacheHelper cache = ((RolapCubeHierarchyMemberReader)memberReader).getRolapCubeMemberCacheHelper();
            if (cache == null) {
                return Integer.MIN_VALUE;
            } else {
                List<RolapMember> list = cache.getLevelMembersFromCache((RolapLevel)level, (TupleConstraint)null);
                return list == null ? Integer.MIN_VALUE : list.size();
            }
        } else {
            List list;
            if (memberReader instanceof SmartMemberReader) {
                list = ((SmartMemberReader)memberReader).getMemberCache().getLevelMembersFromCache((RolapLevel)level, (TupleConstraint)null);
                return list == null ? Integer.MIN_VALUE : list.size();
            } else if (memberReader instanceof MemberCache) {
                list = ((MemberCache)memberReader).getLevelMembersFromCache((RolapLevel)level, (TupleConstraint)null);
                return list == null ? Integer.MIN_VALUE : list.size();
            } else {
                return Integer.MIN_VALUE;
            }
        }
    }

    public int getLevelCardinality(Level level, boolean approximate, boolean materialize) {
        if (!this.role.canAccess(level)) {
            return 1;
        } else {
            int rowCount = Integer.MIN_VALUE;
            if (approximate) {
                rowCount = level.getApproxRowCount();
            }

            if (rowCount == Integer.MIN_VALUE) {
                rowCount = this.getLevelCardinalityFromCache(level);
            }

            if (rowCount == Integer.MIN_VALUE && materialize) {
                MemberReader memberReader = this.getMemberReader(level.getHierarchy());
                rowCount = memberReader.getLevelMemberCount((RolapLevel)level);
                ((RolapLevel)level).setApproxRowCount(rowCount);
            }

            return rowCount;
        }
    }

    public List<Member> getMemberChildren(List<Member> members) {
        return this.getMemberChildren((List)members, (Evaluator)null);
    }

    public List<Member> getMemberChildren(List<Member> members, Evaluator context) {
        if (members.size() == 0) {
            return Collections.emptyList();
        } else {
            MemberChildrenConstraint constraint = this.sqlConstraintFactory.getMemberChildrenConstraint(context);
            Hierarchy hierarchy = ((Member)members.get(0)).getHierarchy();
            MemberReader memberReader = this.getMemberReader(hierarchy);
            List<RolapMember> rolapMemberList = Util.cast(members);
            List<RolapMember> children = new ArrayList();
            memberReader.getMemberChildren(rolapMemberList, children, constraint);
            return Util.cast(children);
        }
    }

    public void getMemberAncestors(Member member, List<Member> ancestorList) {
        for(Member parentMember = this.getMemberParent(member); parentMember != null; parentMember = this.getMemberParent(parentMember)) {
            ancestorList.add(parentMember);
        }

    }

    public Cube getCube() {
        throw new UnsupportedOperationException();
    }

    public SchemaReader withoutAccessControl() {
        assert this.getClass() == RolapSchemaReader.class : "Subclass " + this.getClass() + " must override";

        return this.role == this.schema.getDefaultRole() ? this : new RolapSchemaReader(this.schema.getDefaultRole(), this.schema);
    }

    public OlapElement getElementChild(OlapElement parent, Segment name) {
        return this.getElementChild(parent, name, MatchType.EXACT);
    }

    public OlapElement getElementChild(OlapElement parent, Segment name, MatchType matchType) {
        return parent.lookupChild(this, name, matchType);
    }

    public final Member getMemberByUniqueName(List<Segment> uniqueNameParts, boolean failIfNotFound) {
        return this.getMemberByUniqueName(uniqueNameParts, failIfNotFound, MatchType.EXACT);
    }

    public Member getMemberByUniqueName(List<Segment> uniqueNameParts, boolean failIfNotFound, MatchType matchType) {
        return null;
    }

    public OlapElement lookupCompound(OlapElement parent, List<Segment> names, boolean failIfNotFound, int category) {
        return this.lookupCompound(parent, names, failIfNotFound, category, MatchType.EXACT);
    }

    public final OlapElement lookupCompound(OlapElement parent, List<Segment> names, boolean failIfNotFound, int category, MatchType matchType) {
        return MondrianProperties.instance().SsasCompatibleNaming.get() ? (new NameResolver()).resolve(parent, Util.toOlap4j(names), failIfNotFound, category, matchType, this.getNamespaces()) : this.lookupCompoundInternal(parent, names, failIfNotFound, category, matchType);
    }

    public final OlapElement lookupCompoundInternal(OlapElement parent, List<Segment> names, boolean failIfNotFound, int category, MatchType matchType) {
        return Util.lookupCompound(this, parent, names, failIfNotFound, category, matchType);
    }

    public List<Namespace> getNamespaces() {
        return Collections.singletonList(this);
    }

    public OlapElement lookupChild(OlapElement parent, IdentifierSegment segment) {
        return this.lookupChild(parent, segment, MatchType.EXACT);
    }

    public OlapElement lookupChild(OlapElement parent, IdentifierSegment segment, MatchType matchType) {
        OlapElement element = this.getElementChild(parent, Util.convert(segment), matchType);
        if (element != null) {
            return (OlapElement)element;
        } else {
            if (parent instanceof Cube) {
                element = this.schema.getNamedSet(segment);
            }

            return (OlapElement)element;
        }
    }

    public Member lookupMemberChildByName(Member parent, Segment childName, MatchType matchType) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("looking for child \"" + childName + "\" of " + parent);
        }

        assert !(parent instanceof LimitedRollupMember);

        try {
            MemberChildrenConstraint constraint;
            if (childName instanceof NameSegment && matchType.isExact()) {
                constraint = this.sqlConstraintFactory.getChildByNameConstraint((RolapMember)parent, (NameSegment)childName);
            } else {
                constraint = this.sqlConstraintFactory.getMemberChildrenConstraint((Evaluator)null);
            }

            List<RolapMember> children = this.internalGetMemberChildren(parent, constraint);
            if (children.size() > 0) {
                return RolapUtil.findBestMemberMatch(children, (RolapMember)parent, ((RolapMember)children.get(0)).getLevel(), childName, matchType);
            }
        } catch (NumberFormatException var6) {
            LOGGER.debug("NumberFormatException in lookupMemberChildByName for parent = \"" + parent + "\", childName=\"" + childName + "\", exception: " + var6.getMessage());
        }

        return null;
    }

    public List<Member> lookupMemberChildrenByNames(Member parent, List<NameSegment> childNames, MatchType matchType) {
        MemberChildrenConstraint constraint = this.sqlConstraintFactory.getChildrenByNamesConstraint((RolapMember)parent, childNames);
        List<RolapMember> children = this.internalGetMemberChildren(parent, constraint);
        List<Member> childMembers = new ArrayList();
        childMembers.addAll(children);
        return childMembers;
    }

    public Member getCalculatedMember(List<Segment> nameParts) {
        return null;
    }

    public NamedSet getNamedSet(List<Segment> nameParts) {
        if (nameParts.size() != 1) {
            return null;
        } else if (!(nameParts.get(0) instanceof NameSegment)) {
            return null;
        } else {
            String name = ((NameSegment)nameParts.get(0)).name;
            return this.schema.getNamedSet(name);
        }
    }

    public Member getLeadMember(Member member, int n) {
        MemberReader memberReader = this.getMemberReader(member.getHierarchy());
        return memberReader.getLeadMember((RolapMember)member, n);
    }

    public List<Member> getLevelMembers(Level level, boolean includeCalculated) {
        return this.getLevelMembers(level, includeCalculated, (Evaluator)null);
    }

    public List<Member> getLevelMembers(Level level, boolean includeCalculated, Evaluator context) {
        List<Member> members = this.getLevelMembers(level, context);
        if (!includeCalculated) {
            members = SqlConstraintUtils.removeCalculatedMembers(members);
        }

        return members;
    }

    public List<Member> getLevelMembers(Level level, Evaluator context) {
        TupleConstraint constraint = this.sqlConstraintFactory.getLevelMembersConstraint(context, new Level[]{level});
        MemberReader memberReader = this.getMemberReader(level.getHierarchy());
        List<RolapMember> membersInLevel = memberReader.getMembersInLevel((RolapLevel)level, constraint);
        return Util.cast(membersInLevel);
    }

    public List<Dimension> getCubeDimensions(Cube cube) {
        assert cube != null;

        List<Dimension> dimensions = new ArrayList();
        Dimension[] var3 = cube.getDimensions();
        int var4 = var3.length;
        int var5 = 0;

        while(var5 < var4) {
            Dimension dimension = var3[var5];
            switch(this.role.getAccess(dimension)) {
                default:
                    dimensions.add(dimension);
                case NONE:
                    ++var5;
            }
        }

        return dimensions;
    }

    public List<Hierarchy> getDimensionHierarchies(Dimension dimension) {
        assert dimension != null;

        List<Hierarchy> hierarchies = new ArrayList();
        Hierarchy[] var3 = dimension.getHierarchies();
        int var4 = var3.length;
        int var5 = 0;

        while(var5 < var4) {
            Hierarchy hierarchy = var3[var5];
            switch(this.role.getAccess(hierarchy)) {
                default:
                    hierarchies.add(hierarchy);
                case NONE:
                    ++var5;
            }
        }

        return hierarchies;
    }

    public List<Level> getHierarchyLevels(Hierarchy hierarchy) {
        assert hierarchy != null;

        HierarchyAccess hierarchyAccess = this.role.getAccessDetails(hierarchy);
        Level[] levels = hierarchy.getLevels();
        if (hierarchyAccess == null) {
            return Arrays.asList(levels);
        } else {
            Level topLevel = levels[hierarchyAccess.getTopLevelDepth()];
            Level bottomLevel = levels[hierarchyAccess.getBottomLevelDepth()];
            List<Level> restrictedLevels = Arrays.asList(levels).subList(topLevel.getDepth(), bottomLevel.getDepth() + 1);

            assert restrictedLevels.size() >= 1 : "postcondition";

            return restrictedLevels;
        }
    }

    public Member getHierarchyDefaultMember(Hierarchy hierarchy) {
        assert hierarchy != null;

        return (Member)(this.role.getAccess(hierarchy) == Access.NONE ? hierarchy.getDefaultMember() : this.getMemberReader(hierarchy).getDefaultMember());
    }

    public boolean isDrillable(Member member) {
        RolapLevel level = (RolapLevel)member.getLevel();
        if (level.getParentExp() != null) {
            return this.getMemberChildren(member).size() > 0;
        } else {
            Level childLevel = level.getChildLevel();
            return childLevel != null && this.role.getAccess(childLevel) != Access.NONE;
        }
    }

    public boolean isVisible(Member member) {
        return !member.isHidden() && this.role.canAccess(member);
    }

    public Cube[] getCubes() {
        List<RolapCube> cubes = this.schema.getCubeList();
        List<Cube> visibleCubes = new ArrayList(cubes.size());
        Iterator var3 = cubes.iterator();

        while(var3.hasNext()) {
            Cube cube = (Cube)var3.next();
            if (this.role.canAccess(cube)) {
                visibleCubes.add(cube);
            }
        }

        return (Cube[])visibleCubes.toArray(new Cube[visibleCubes.size()]);
    }

    public List<Member> getCalculatedMembers(Hierarchy hierarchy) {
        return Collections.emptyList();
    }

    public List<Member> getCalculatedMembers(Level level) {
        return Collections.emptyList();
    }

    public List<Member> getCalculatedMembers() {
        return Collections.emptyList();
    }

    public NativeEvaluator getNativeSetEvaluator(FunDef fun, Exp[] args, Evaluator evaluator, Calc calc) {
        RolapEvaluator revaluator = (RolapEvaluator)AbstractCalc.simplifyEvaluator(calc, evaluator);
        return evaluator.nativeEnabled() ? this.schema.getNativeRegistry().createEvaluator(revaluator, fun, args) : null;
    }

    public Parameter getParameter(String name) {
        Iterator var2 = this.schema.parameterList.iterator();

        RolapSchemaParameter parameter;
        do {
            if (!var2.hasNext()) {
                List<Property> propertyList = MondrianProperties.instance().getPropertyList();
                Iterator var6 = propertyList.iterator();

                Property property;
                do {
                    if (!var6.hasNext()) {
                        return null;
                    }

                    property = (Property)var6.next();
                } while(!property.getPath().equals(name));

                return new RolapSchemaReader.SystemPropertyParameter(name, false);
            }

            parameter = (RolapSchemaParameter)var2.next();
        } while(!Util.equalName(parameter.getName(), name));

        return parameter;
    }

    public DataSource getDataSource() {
        return this.schema.getInternalConnection().getDataSource();
    }

    public RolapSchema getSchema() {
        return this.schema;
    }

    public SchemaReader withLocus() {
        return RolapUtil.locusSchemaReader(this.schema.getInternalConnection(), this);
    }

    public Map<? extends Member, Access> getMemberChildrenWithDetails(Member member, Evaluator evaluator) {
        MemberChildrenConstraint constraint = this.sqlConstraintFactory.getMemberChildrenConstraint(evaluator);
        Hierarchy hierarchy = member.getHierarchy();
        MemberReader memberReader = this.getMemberReader(hierarchy);
        ArrayList<RolapMember> memberChildren = new ArrayList();
        return memberReader.getMemberChildren((RolapMember)member, memberChildren, constraint);
    }

    private static class SystemPropertyParameter extends ParameterImpl {
        private final boolean system;
        private final Property propertyDefinition;

        public SystemPropertyParameter(String name, boolean system) {
            super(name, Literal.nullValue, "System property '" + name + "'", new StringType());
            this.system = system;
            this.propertyDefinition = system ? null : MondrianProperties.instance().getPropertyDefinition(name);
        }

        public Scope getScope() {
            return Scope.System;
        }

        public boolean isModifiable() {
            return false;
        }

        public Calc compile(ExpCompiler compiler) {
            return new GenericCalc(new DummyExp(this.getType())) {
                public Calc[] getCalcs() {
                    return new Calc[0];
                }

                public Object evaluate(Evaluator evaluator) {
                    if (SystemPropertyParameter.this.system) {
                        String name = SystemPropertyParameter.this.getName();
                        return System.getProperty(name);
                    } else {
                        return SystemPropertyParameter.this.propertyDefinition.stringValue();
                    }
                }
            };
        }
    }
}