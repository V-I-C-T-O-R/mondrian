/* Decompiler 143ms, total 974ms, lines 216 */
package mondrian.olap;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import mondrian.calc.Calc;
import mondrian.olap.Id.NameSegment;
import mondrian.olap.Id.Segment;
import mondrian.olap.NameResolver.Namespace;
import mondrian.rolap.RolapSchema;
import mondrian.rolap.RolapUtil;

public abstract class DelegatingSchemaReader implements SchemaReader {
    protected final SchemaReader schemaReader;

    protected DelegatingSchemaReader(SchemaReader schemaReader) {
        this.schemaReader = schemaReader;
    }

    public RolapSchema getSchema() {
        return this.schemaReader.getSchema();
    }

    public Role getRole() {
        return this.schemaReader.getRole();
    }

    public Cube getCube() {
        return this.schemaReader.getCube();
    }

    public List<Dimension> getCubeDimensions(Cube cube) {
        return this.schemaReader.getCubeDimensions(cube);
    }

    public List<Hierarchy> getDimensionHierarchies(Dimension dimension) {
        return this.schemaReader.getDimensionHierarchies(dimension);
    }

    public List<Member> getHierarchyRootMembers(Hierarchy hierarchy) {
        return this.schemaReader.getHierarchyRootMembers(hierarchy);
    }

    public Member getMemberParent(Member member) {
        return this.schemaReader.getMemberParent(member);
    }

    public Member substitute(Member member) {
        return this.schemaReader.substitute(member);
    }

    public List<Member> getMemberChildren(Member member) {
        return this.schemaReader.getMemberChildren(member);
    }

    public List<Member> getMemberChildren(List<Member> members) {
        return this.schemaReader.getMemberChildren(members);
    }

    public void getParentChildContributingChildren(Member dataMember, Hierarchy hierarchy, List<Member> list) {
        this.schemaReader.getParentChildContributingChildren(dataMember, hierarchy, list);
    }

    public int getMemberDepth(Member member) {
        return this.schemaReader.getMemberDepth(member);
    }

    public final Member getMemberByUniqueName(List<Segment> uniqueNameParts, boolean failIfNotFound) {
        return this.getMemberByUniqueName(uniqueNameParts, failIfNotFound, MatchType.EXACT);
    }

    public Member getMemberByUniqueName(List<Segment> uniqueNameParts, boolean failIfNotFound, MatchType matchType) {
        return this.schemaReader.getMemberByUniqueName(uniqueNameParts, failIfNotFound, matchType);
    }

    public final OlapElement lookupCompound(OlapElement parent, List<Segment> names, boolean failIfNotFound, int category) {
        return this.lookupCompound(parent, names, failIfNotFound, category, MatchType.EXACT);
    }

    public final OlapElement lookupCompound(OlapElement parent, List<Segment> names, boolean failIfNotFound, int category, MatchType matchType) {
        return MondrianProperties.instance().SsasCompatibleNaming.get() ? (new NameResolver()).resolve(parent, Util.toOlap4j(names), failIfNotFound, category, matchType, this.getNamespaces()) : this.lookupCompoundInternal(parent, names, failIfNotFound, category, matchType);
    }

    public List<Namespace> getNamespaces() {
        return this.schemaReader.getNamespaces();
    }

    public OlapElement lookupCompoundInternal(OlapElement parent, List<Segment> names, boolean failIfNotFound, int category, MatchType matchType) {
        return this.schemaReader.lookupCompound(parent, names, failIfNotFound, category, matchType);
    }

    public Member getCalculatedMember(List<Segment> nameParts) {
        return this.schemaReader.getCalculatedMember(nameParts);
    }

    public NamedSet getNamedSet(List<Segment> nameParts) {
        return this.schemaReader.getNamedSet(nameParts);
    }

    public void getMemberRange(Level level, Member startMember, Member endMember, List<Member> list) {
        this.schemaReader.getMemberRange(level, startMember, endMember, list);
    }

    public Member getLeadMember(Member member, int n) {
        return this.schemaReader.getLeadMember(member, n);
    }

    public int compareMembersHierarchically(Member m1, Member m2) {
        return this.schemaReader.compareMembersHierarchically(m1, m2);
    }

    public OlapElement getElementChild(OlapElement parent, Segment name) {
        return this.getElementChild(parent, name, MatchType.EXACT);
    }

    public OlapElement getElementChild(OlapElement parent, Segment name, MatchType matchType) {
        return this.schemaReader.getElementChild(parent, name, matchType);
    }

    public List<Member> getLevelMembers(Level level, boolean includeCalculated, Evaluator context) {
        return this.schemaReader.getLevelMembers(level, includeCalculated, context);
    }

    public List<Member> getLevelMembers(Level level, boolean includeCalculated) {
        return this.getLevelMembers(level, includeCalculated, (Evaluator)null);
    }

    public List<Level> getHierarchyLevels(Hierarchy hierarchy) {
        return this.schemaReader.getHierarchyLevels(hierarchy);
    }

    public Member getHierarchyDefaultMember(Hierarchy hierarchy) {
        return this.schemaReader.getHierarchyDefaultMember(hierarchy);
    }

    public boolean isDrillable(Member member) {
        return this.schemaReader.isDrillable(member);
    }

    public boolean isVisible(Member member) {
        return this.schemaReader.isVisible(member);
    }

    public Cube[] getCubes() {
        return this.schemaReader.getCubes();
    }

    public List<Member> getCalculatedMembers(Hierarchy hierarchy) {
        return this.schemaReader.getCalculatedMembers(hierarchy);
    }

    public List<Member> getCalculatedMembers(Level level) {
        return this.schemaReader.getCalculatedMembers(level);
    }

    public List<Member> getCalculatedMembers() {
        return this.schemaReader.getCalculatedMembers();
    }

    public int getChildrenCountFromCache(Member member) {
        return this.schemaReader.getChildrenCountFromCache(member);
    }

    public int getLevelCardinality(Level level, boolean approximate, boolean materialize) {
        return this.schemaReader.getLevelCardinality(level, approximate, materialize);
    }

    public List<Member> getLevelMembers(Level level, Evaluator context) {
        return this.schemaReader.getLevelMembers(level, context);
    }

    public List<Member> getMemberChildren(Member member, Evaluator context) {
        return this.schemaReader.getMemberChildren(member, context);
    }

    public List<Member> getMemberChildren(List<Member> members, Evaluator context) {
        return this.schemaReader.getMemberChildren(members, context);
    }

    public void getMemberAncestors(Member member, List<Member> ancestorList) {
        this.schemaReader.getMemberAncestors(member, ancestorList);
    }

    public Member lookupMemberChildByName(Member member, Segment memberName, MatchType matchType) {
        return this.schemaReader.lookupMemberChildByName(member, memberName, matchType);
    }

    public List<Member> lookupMemberChildrenByNames(Member parent, List<NameSegment> childNames, MatchType matchType) {
        return this.schemaReader.lookupMemberChildrenByNames(parent, childNames, matchType);
    }

    public NativeEvaluator getNativeSetEvaluator(FunDef fun, Exp[] args, Evaluator evaluator, Calc calc) {
        return this.schemaReader.getNativeSetEvaluator(fun, args, evaluator, calc);
    }

    public Parameter getParameter(String name) {
        return this.schemaReader.getParameter(name);
    }

    public DataSource getDataSource() {
        return this.schemaReader.getDataSource();
    }

    public SchemaReader withoutAccessControl() {
        return this.schemaReader.withoutAccessControl();
    }

    public SchemaReader withLocus() {
        return RolapUtil.locusSchemaReader(this.schemaReader.getSchema().getInternalConnection(), this);
    }

    public Map<? extends Member, Access> getMemberChildrenWithDetails(Member member, Evaluator evaluator) {
        return this.schemaReader.getMemberChildrenWithDetails(member, evaluator);
    }
}