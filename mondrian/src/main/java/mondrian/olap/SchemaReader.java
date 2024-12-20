/* Decompiler 7ms, total 1067ms, lines 109 */
package mondrian.olap;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import mondrian.calc.Calc;
import mondrian.olap.Id.NameSegment;
import mondrian.olap.Id.Segment;
import mondrian.olap.NameResolver.Namespace;
import mondrian.rolap.RolapSchema;

public interface SchemaReader {
    RolapSchema getSchema();

    Role getRole();

    List<Dimension> getCubeDimensions(Cube var1);

    List<Hierarchy> getDimensionHierarchies(Dimension var1);

    List<Member> getHierarchyRootMembers(Hierarchy var1);

    int getChildrenCountFromCache(Member var1);

    int getLevelCardinality(Level var1, boolean var2, boolean var3);

    Member substitute(Member var1);

    List<Member> getMemberChildren(Member var1);

    List<Member> getMemberChildren(Member var1, Evaluator var2);

    List<Member> getMemberChildren(List<Member> var1);

    List<Member> getMemberChildren(List<Member> var1, Evaluator var2);

    void getParentChildContributingChildren(Member var1, Hierarchy var2, List<Member> var3);

    Member getMemberParent(Member var1);

    void getMemberAncestors(Member var1, List<Member> var2);

    int getMemberDepth(Member var1);

    Member getMemberByUniqueName(List<Segment> var1, boolean var2, MatchType var3);

    Member getMemberByUniqueName(List<Segment> var1, boolean var2);

    OlapElement lookupCompound(OlapElement var1, List<Segment> var2, boolean var3, int var4, MatchType var5);

    OlapElement lookupCompound(OlapElement var1, List<Segment> var2, boolean var3, int var4);

    Member getCalculatedMember(List<Segment> var1);

    NamedSet getNamedSet(List<Segment> var1);

    void getMemberRange(Level var1, Member var2, Member var3, List<Member> var4);

    Member getLeadMember(Member var1, int var2);

    int compareMembersHierarchically(Member var1, Member var2);

    OlapElement getElementChild(OlapElement var1, Segment var2, MatchType var3);

    OlapElement getElementChild(OlapElement var1, Segment var2);

    List<Member> getLevelMembers(Level var1, boolean var2);

    List<Member> getLevelMembers(Level var1, boolean var2, Evaluator var3);

    List<Member> getLevelMembers(Level var1, Evaluator var2);

    List<Level> getHierarchyLevels(Hierarchy var1);

    Member getHierarchyDefaultMember(Hierarchy var1);

    boolean isDrillable(Member var1);

    boolean isVisible(Member var1);

    Cube[] getCubes();

    List<Member> getCalculatedMembers(Hierarchy var1);

    List<Member> getCalculatedMembers(Level var1);

    List<Member> getCalculatedMembers();

    Member lookupMemberChildByName(Member var1, Segment var2, MatchType var3);

    List<Member> lookupMemberChildrenByNames(Member var1, List<NameSegment> var2, MatchType var3);

    NativeEvaluator getNativeSetEvaluator(FunDef var1, Exp[] var2, Evaluator var3, Calc var4);

    Parameter getParameter(String var1);

    DataSource getDataSource();

    SchemaReader withoutAccessControl();

    Cube getCube();

    SchemaReader withLocus();

    List<Namespace> getNamespaces();

    Map<? extends Member, Access> getMemberChildrenWithDetails(Member var1, Evaluator var2);
}