/* Decompiler 181ms, total 913ms, lines 221 */
package mondrian.rolap;

import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import mondrian.olap.Access;
import mondrian.olap.Member;
import mondrian.olap.Id.Segment;
import mondrian.rolap.TupleReader.MemberBuilder;
import mondrian.rolap.sql.MemberChildrenConstraint;
import mondrian.rolap.sql.TupleConstraint;

public abstract class SubstitutingMemberReader extends DelegatingMemberReader {
    private final MemberBuilder memberBuilder = new SubstitutingMemberReader.SubstitutingMemberBuilder();

    SubstitutingMemberReader(MemberReader memberReader) {
        super(memberReader);
    }

    private List<RolapMember> desubstitute(List<RolapMember> members) {
        List<RolapMember> list = new ArrayList(members.size());
        Iterator var3 = members.iterator();

        while(var3.hasNext()) {
            RolapMember member = (RolapMember)var3.next();
            list.add(this.desubstitute(member));
        }

        return list;
    }

    private List<RolapMember> substitute(List<RolapMember> members) {
        List<RolapMember> list = new ArrayList(members.size());
        Iterator var3 = members.iterator();

        while(var3.hasNext()) {
            RolapMember member = (RolapMember)var3.next();
            list.add(this.substitute(member));
        }

        return list;
    }

    public RolapMember getLeadMember(RolapMember member, int n) {
        return this.substitute(this.memberReader.getLeadMember(this.desubstitute(member), n));
    }

    public List<RolapMember> getMembersInLevel(RolapLevel level) {
        return this.substitute(this.memberReader.getMembersInLevel(level));
    }

    public void getMemberRange(RolapLevel level, RolapMember startMember, RolapMember endMember, List<RolapMember> list) {
        this.memberReader.getMemberRange(level, this.desubstitute(startMember), this.desubstitute(endMember), new SubstitutingMemberReader.SubstitutingMemberList(list));
    }

    public int compare(RolapMember m1, RolapMember m2, boolean siblingsAreEqual) {
        return this.memberReader.compare(this.desubstitute(m1), this.desubstitute(m2), siblingsAreEqual);
    }

    public RolapHierarchy getHierarchy() {
        return this.memberReader.getHierarchy();
    }

    public boolean setCache(MemberCache cache) {
        throw new UnsupportedOperationException();
    }

    public List<RolapMember> getMembers() {
        throw new UnsupportedOperationException();
    }

    public List<RolapMember> getRootMembers() {
        return this.substitute(this.memberReader.getRootMembers());
    }

    public void getMemberChildren(RolapMember parentMember, List<RolapMember> children) {
        this.memberReader.getMemberChildren(this.desubstitute(parentMember), new SubstitutingMemberReader.SubstitutingMemberList(children));
    }

    public void getMemberChildren(List<RolapMember> parentMembers, List<RolapMember> children) {
        this.memberReader.getMemberChildren(this.desubstitute(parentMembers), new SubstitutingMemberReader.SubstitutingMemberList(children));
    }

    public int getMemberCount() {
        return this.memberReader.getMemberCount();
    }

    public RolapMember lookupMember(List<Segment> uniqueNameParts, boolean failIfNotFound) {
        return this.substitute(this.memberReader.lookupMember(uniqueNameParts, failIfNotFound));
    }

    public Map<? extends Member, Access> getMemberChildren(RolapMember member, List<RolapMember> children, MemberChildrenConstraint constraint) {
        return this.memberReader.getMemberChildren(this.desubstitute(member), new SubstitutingMemberReader.SubstitutingMemberList(children), constraint);
    }

    public Map<? extends Member, Access> getMemberChildren(List<RolapMember> parentMembers, List<RolapMember> children, MemberChildrenConstraint constraint) {
        return this.memberReader.getMemberChildren(this.desubstitute(parentMembers), new SubstitutingMemberReader.SubstitutingMemberList(children), constraint);
    }

    public List<RolapMember> getMembersInLevel(RolapLevel level, TupleConstraint constraint) {
        return this.substitute(this.memberReader.getMembersInLevel(level, constraint));
    }

    public RolapMember getDefaultMember() {
        return this.substitute(this.memberReader.getDefaultMember());
    }

    public RolapMember getMemberParent(RolapMember member) {
        return this.substitute(this.memberReader.getMemberParent(this.desubstitute(member)));
    }

    public MemberBuilder getMemberBuilder() {
        return this.memberBuilder;
    }

    // $FF: synthetic method
    // $FF: bridge method
    public int getLevelMemberCount(RolapLevel var1) {
        return super.getLevelMemberCount(var1);
    }

    // $FF: synthetic method
    // $FF: bridge method
    public RolapMember getMemberByKey(RolapLevel var1, List var2) {
        return super.getMemberByKey(var1, var2);
    }

    // $FF: synthetic method
    // $FF: bridge method
    public RolapMember desubstitute(RolapMember var1) {
        return super.desubstitute(var1);
    }

    // $FF: synthetic method
    // $FF: bridge method
    public RolapMember substitute(RolapMember var1) {
        return super.substitute(var1);
    }

    private class SubstitutingMemberBuilder implements MemberBuilder {
        private SubstitutingMemberBuilder() {
        }

        public MemberCache getMemberCache() {
            return SubstitutingMemberReader.this.memberReader.getMemberBuilder().getMemberCache();
        }

        public Object getMemberCacheLock() {
            return SubstitutingMemberReader.this.memberReader.getMemberBuilder().getMemberCacheLock();
        }

        public RolapMember makeMember(RolapMember parentMember, RolapLevel childLevel, Object value, Object captionValue, boolean parentChild, SqlStatement stmt, Object key, int column) throws SQLException {
            return SubstitutingMemberReader.this.substitute(SubstitutingMemberReader.this.memberReader.getMemberBuilder().makeMember(SubstitutingMemberReader.this.desubstitute(parentMember), childLevel, value, captionValue, parentChild, stmt, key, column));
        }

        public RolapMember allMember() {
            return SubstitutingMemberReader.this.substitute(SubstitutingMemberReader.this.memberReader.getHierarchy().getAllMember());
        }

        // $FF: synthetic method
        SubstitutingMemberBuilder(Object x1) {
            this();
        }
    }

    class SubstitutingMemberList extends AbstractList<RolapMember> {
        private final List<RolapMember> list;

        SubstitutingMemberList(List<RolapMember> list) {
            this.list = list;
        }

        public RolapMember get(int index) {
            return SubstitutingMemberReader.this.desubstitute((RolapMember)this.list.get(index));
        }

        public int size() {
            return this.list.size();
        }

        public RolapMember set(int index, RolapMember element) {
            return SubstitutingMemberReader.this.desubstitute((RolapMember)this.list.set(index, SubstitutingMemberReader.this.substitute(element)));
        }

        public void add(int index, RolapMember element) {
            this.list.add(index, SubstitutingMemberReader.this.substitute(element));
        }

        public RolapMember remove(int index) {
            return (RolapMember)this.list.remove(index);
        }
    }
}