/* Decompiler 909ms, total 3894ms, lines 881 */
package mondrian.rolap;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import mondrian.olap.Access;
import mondrian.olap.Formula;
import mondrian.olap.Level;
import mondrian.olap.Member;
import mondrian.olap.MondrianProperties;
import mondrian.olap.Util;
import mondrian.olap.MondrianDef.Column;
import mondrian.olap.MondrianDef.CubeDimension;
import mondrian.olap.MondrianDef.Join;
import mondrian.olap.MondrianDef.Relation;
import mondrian.olap.MondrianDef.RelationOrJoin;
import mondrian.olap.fun.VisualTotalsFunDef.VisualTotalMember;
import mondrian.rolap.RolapHierarchy.RolapCalculatedMeasure;
import mondrian.rolap.TupleReader.MemberBuilder;
import mondrian.rolap.sql.MemberChildrenConstraint;
import mondrian.rolap.sql.TupleConstraint;
import mondrian.util.UnsupportedList;

public class RolapCubeHierarchy extends RolapHierarchy {
    private final boolean cachingEnabled;
    private final RolapCubeDimension cubeDimension;
    private final RolapHierarchy rolapHierarchy;
    private final RolapCubeLevel currentNullLevel;
    private RolapCubeMember currentNullMember;
    private RolapCubeMember currentAllMember;
    private final RelationOrJoin currentRelation;
    private final RolapCubeHierarchy.RolapCubeHierarchyMemberReader reader;
    private HierarchyUsage usage;
    private final Map<String, String> aliases;
    private RolapCubeMember currentDefaultMember;
    private final int ordinal;
    protected final boolean usingCubeFact;
    private final int removePrefixLength;
    private final RolapCubeLevel[] cubeLevels;

    public RolapCubeHierarchy(RolapCubeDimension cubeDimension, CubeDimension cubeDim, RolapHierarchy rolapHierarchy, String subName, int ordinal) {
        this(cubeDimension, cubeDim, rolapHierarchy, subName, ordinal, (RolapCube)null);
    }

    public RolapCubeHierarchy(RolapCubeDimension cubeDimension, CubeDimension cubeDim, RolapHierarchy rolapHierarchy, String subName, int ordinal, RolapCube factCube) {
        super(cubeDimension, subName, applyPrefix(cubeDim, rolapHierarchy.getCaption()), rolapHierarchy.isVisible(), applyPrefix(cubeDim, rolapHierarchy.getDescription()), rolapHierarchy.getDisplayFolder(), rolapHierarchy.hasAll(), (RolapHierarchy)null, rolapHierarchy.getAnnotationMap());
        this.cachingEnabled = MondrianProperties.instance().EnableRolapCubeMemberCache.get();
        this.aliases = new HashMap();
        this.ordinal = ordinal;
        boolean cubeIsVirtual = cubeDimension.getCube().isVirtual();
        if (!cubeIsVirtual) {
            this.usage = new HierarchyUsage(cubeDimension.getCube(), rolapHierarchy, cubeDim);
        }

        this.rolapHierarchy = rolapHierarchy;
        this.cubeDimension = cubeDimension;
        this.xmlHierarchy = rolapHierarchy.getXmlHierarchy();
        this.currentNullLevel = new RolapCubeLevel(this.nullLevel, this);
        if (factCube == null) {
            factCube = cubeDimension.getCube();
        }

        this.usingCubeFact = factCube == null || factCube.getFact() == null || factCube.getFact().equals(rolapHierarchy.getRelation());
        if (!cubeIsVirtual && !this.usingCubeFact) {
            assert this.usage.getJoinExp() instanceof Column;

            this.currentRelation = this.cubeDimension.getCube().getStar().getUniqueRelation(rolapHierarchy.getRelation(), this.usage.getForeignKey(), ((Column)this.usage.getJoinExp()).getColumnName(), this.usage.getJoinTable().getAlias());
        } else {
            this.currentRelation = rolapHierarchy.getRelation();
        }

        this.extractNewAliases(rolapHierarchy.getRelation(), this.currentRelation);
        this.relation = this.currentRelation;
        this.levels = this.cubeLevels = new RolapCubeLevel[rolapHierarchy.getLevels().length];

        for(int i = 0; i < rolapHierarchy.getLevels().length; ++i) {
            this.cubeLevels[i] = new RolapCubeLevel((RolapLevel)rolapHierarchy.getLevels()[i], this);
            if (i == 0 && rolapHierarchy.getAllMember() != null) {
                RolapCubeLevel allLevel;
                if (this.hasAll()) {
                    allLevel = this.cubeLevels[0];
                } else {
                    allLevel = new RolapCubeLevel(rolapHierarchy.getAllMember().getLevel(), this);
                    allLevel.init(cubeDimension.xmlDimension);
                }

                this.currentAllMember = new RolapAllCubeMember(rolapHierarchy.getAllMember(), allLevel);
            }
        }

        if (this.uniqueName.equals(rolapHierarchy.getUniqueName())) {
            this.removePrefixLength = 0;
        } else {
            this.removePrefixLength = rolapHierarchy.getUniqueName().length();
        }

        if (!cubeDimension.isHighCardinality() && this.cachingEnabled) {
            this.reader = new RolapCubeHierarchy.CacheRolapCubeHierarchyMemberReader();
        } else {
            this.reader = new RolapCubeHierarchy.NoCacheRolapCubeHierarchyMemberReader();
        }

    }

    private static String applyPrefix(CubeDimension cubeDim, String caption) {
        return caption == null ? null : caption;
    }

    public RolapCubeLevel[] getLevels() {
        return this.cubeLevels;
    }

    public String getAllMemberName() {
        return this.rolapHierarchy.getAllMemberName();
    }

    public String getSharedHierarchyName() {
        return this.rolapHierarchy.getSharedHierarchyName();
    }

    public String getAllLevelName() {
        return this.rolapHierarchy.getAllLevelName();
    }

    public boolean isUsingCubeFact() {
        return this.usingCubeFact;
    }

    public String lookupAlias(String origTable) {
        return (String)this.aliases.get(origTable);
    }

    public final RolapHierarchy getRolapHierarchy() {
        return this.rolapHierarchy;
    }

    public final int getOrdinalInCube() {
        return this.ordinal;
    }

    protected void extractNewAliases(RelationOrJoin oldrel, RelationOrJoin newrel) {
        if (oldrel != null || newrel != null) {
            if (oldrel instanceof Relation && newrel instanceof Relation) {
                this.aliases.put(((Relation)oldrel).getAlias(), ((Relation)newrel).getAlias());
            } else {
                if (!(oldrel instanceof Join) || !(newrel instanceof Join)) {
                    throw new UnsupportedOperationException();
                }

                Join oldjoin = (Join)oldrel;
                Join newjoin = (Join)newrel;
                this.extractNewAliases(oldjoin.left, newjoin.left);
                this.extractNewAliases(oldjoin.right, newjoin.right);
            }

        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof RolapCubeHierarchy)) {
            return false;
        } else {
            RolapCubeHierarchy that = (RolapCubeHierarchy)o;
            return this.cubeDimension.equals(that.cubeDimension) && this.getUniqueName().equals(that.getUniqueName());
        }
    }

    protected int computeHashCode() {
        return Util.hash(super.computeHashCode(), this.cubeDimension.cube);
    }

    public Member createMember(Member parent, Level level, String name, Formula formula) {
        RolapLevel rolapLevel = ((RolapCubeLevel)level).getRolapLevel();
        if (formula == null) {
            RolapMember rolapParent = null;
            if (parent != null) {
                rolapParent = ((RolapCubeMember)parent).getRolapMember();
            }

            RolapMember member = new RolapMemberBase(rolapParent, rolapLevel, name);
            return new RolapCubeMember((RolapCubeMember)parent, member, (RolapCubeLevel)level);
        } else if (level.getDimension().isMeasures()) {
            RolapCalculatedMeasure member = new RolapCalculatedMeasure((RolapMember)parent, rolapLevel, name, formula);
            return new RolapCubeMember((RolapCubeMember)parent, member, (RolapCubeLevel)level);
        } else {
            RolapCalculatedMember member = new RolapCalculatedMember((RolapMember)parent, rolapLevel, name, formula);
            return new RolapCubeMember((RolapCubeMember)parent, member, (RolapCubeLevel)level);
        }
    }

    boolean tableExists(String tableName) {
        return this.rolapHierarchy.tableExists(tableName);
    }

    public RelationOrJoin getRelation() {
        return this.currentRelation;
    }

    public final RolapCubeMember getDefaultMember() {
        if (this.currentDefaultMember == null) {
            this.reader.getRootMembers();
            this.currentDefaultMember = this.bootstrapLookup((RolapMember)this.rolapHierarchy.getDefaultMember());
        }

        return this.currentDefaultMember;
    }

    private RolapCubeMember bootstrapLookup(RolapMember rolapMember) {
        RolapCubeMember parent = rolapMember.getParentMember() == null ? null : (rolapMember.getParentMember().isAll() ? this.currentAllMember : this.bootstrapLookup(rolapMember.getParentMember()));
        RolapCubeLevel level = this.cubeLevels[rolapMember.getLevel().getDepth()];
        return this.reader.lookupCubeMember(parent, rolapMember, level);
    }

    public Member getNullMember() {
        if (this.currentNullMember == null) {
            this.currentNullMember = new RolapCubeMember((RolapCubeMember)null, (RolapMember)this.rolapHierarchy.getNullMember(), this.currentNullLevel);
        }

        return this.currentNullMember;
    }

    public RolapCubeMember getAllMember() {
        return this.currentAllMember;
    }

    void setMemberReader(MemberReader memberReader) {
        this.rolapHierarchy.setMemberReader(memberReader);
    }

    MemberReader getMemberReader() {
        return this.reader;
    }

    public void setDefaultMember(Member defaultMeasure) {
        this.rolapHierarchy.setDefaultMember(defaultMeasure);
        RolapCubeLevel level = new RolapCubeLevel((RolapLevel)this.rolapHierarchy.getDefaultMember().getLevel(), this);
        this.currentDefaultMember = new RolapCubeMember((RolapCubeMember)null, (RolapMember)this.rolapHierarchy.getDefaultMember(), level);
    }

    void init(CubeDimension xmlDimension) {
        this.rolapHierarchy.init(xmlDimension);
        super.init(xmlDimension);
    }

    final String convertMemberName(String memberUniqueName) {
        return this.removePrefixLength > 0 && !memberUniqueName.startsWith(this.uniqueName) ? this.uniqueName + memberUniqueName.substring(this.removePrefixLength) : memberUniqueName;
    }

    public final RolapCube getCube() {
        return this.cubeDimension.cube;
    }

    private static RolapCubeMember createAncestorMembers(RolapCubeHierarchy.RolapCubeHierarchyMemberReader memberReader, RolapCubeLevel level, RolapMember member) {
        if (member == null) {
            return null;
        } else {
            RolapCubeMember parent = null;
            if (member.getParentMember() != null) {
                parent = createAncestorMembers(memberReader, level.getParentLevel(), member.getParentMember());
            }

            return memberReader.lookupCubeMember(parent, member, level);
        }
    }

    public static class RolapCubeSqlMemberSource extends SqlMemberSource {
        private final RolapCubeHierarchy.RolapCubeHierarchyMemberReader memberReader;
        private final MemberCacheHelper memberSourceCacheHelper;
        private final Object memberCacheLock;

        public RolapCubeSqlMemberSource(RolapCubeHierarchy.RolapCubeHierarchyMemberReader memberReader, RolapCubeHierarchy hierarchy, MemberCacheHelper memberSourceCacheHelper, Object memberCacheLock) {
            super(hierarchy);
            this.memberReader = memberReader;
            this.memberSourceCacheHelper = memberSourceCacheHelper;
            this.memberCacheLock = memberCacheLock;
        }

        public RolapMember makeMember(RolapMember parentMember, RolapLevel childLevel, Object value, Object captionValue, boolean parentChild, SqlStatement stmt, Object key, int columnOffset) throws SQLException {
            RolapCubeMember parentCubeMember = (RolapCubeMember)parentMember;
            RolapCubeLevel childCubeLevel = (RolapCubeLevel)childLevel;
            RolapMember parent;
            if (parentMember != null) {
                parent = parentCubeMember.getRolapMember();
            } else {
                parent = null;
            }

            RolapMember member = super.makeMember(parent, childCubeLevel.getRolapLevel(), value, captionValue, parentChild, stmt, key, columnOffset);
            return this.memberReader.lookupCubeMember(parentCubeMember, member, childCubeLevel);
        }

        public MemberCache getMemberCache() {
            return this.memberSourceCacheHelper;
        }

        public Object getMemberCacheLock() {
            return this.memberCacheLock;
        }

        public RolapMember allMember() {
            return this.getHierarchy().getAllMember();
        }
    }

    public class NoCacheRolapCubeHierarchyMemberReader extends NoCacheMemberReader implements RolapCubeHierarchy.RolapCubeHierarchyMemberReader {
        protected final RolapCubeHierarchy.RolapCubeSqlMemberSource cubeSource;
        protected MemberCacheHelper rolapCubeCacheHelper = new MemberNoCacheHelper();

        public NoCacheRolapCubeHierarchyMemberReader() {
            super(new SqlMemberSource(RolapCubeHierarchy.this));
            this.cubeSource = new RolapCubeHierarchy.RolapCubeSqlMemberSource(this, RolapCubeHierarchy.this, this.rolapCubeCacheHelper, new MemberNoCacheHelper());
            this.cubeSource.setCache(this.rolapCubeCacheHelper);
        }

        public MemberBuilder getMemberBuilder() {
            return this.cubeSource;
        }

        public MemberCacheHelper getRolapCubeMemberCacheHelper() {
            return this.rolapCubeCacheHelper;
        }

        public List<RolapMember> getRootMembers() {
            return this.getMembersInLevel(RolapCubeHierarchy.this.cubeLevels[0]);
        }

        protected void readMemberChildren(List<RolapMember> parentMembers, List<RolapMember> children, MemberChildrenConstraint constraint) {
            List<RolapMember> rolapChildren = new ArrayList();
            List<RolapMember> rolapParents = new ArrayList();
            Map<String, RolapCubeMember> lookup = new HashMap();
            List<RolapCubeMember> parentRolapCubeMemberList = Util.cast(parentMembers);
            Iterator var8 = parentRolapCubeMemberList.iterator();

            RolapMember currMember;
            while(var8.hasNext()) {
                RolapCubeMember member = (RolapCubeMember)var8.next();
                currMember = member.getRolapMember();
                lookup.put(currMember.getUniqueName(), member);
                rolapParents.add(currMember);
            }

            boolean joinReq = constraint instanceof SqlContextConstraint;
            if (joinReq) {
                super.readMemberChildren(parentMembers, rolapChildren, constraint);
            } else {
                RolapCubeHierarchy.this.rolapHierarchy.getMemberReader().getMemberChildren(rolapParents, rolapChildren, constraint);
            }

            Iterator var15 = rolapChildren.iterator();

            while(var15.hasNext()) {
                currMember = (RolapMember)var15.next();
                RolapCubeMember parent = (RolapCubeMember)lookup.get(currMember.getParentMember().getUniqueName());
                RolapCubeLevel level = parent.getLevel().getChildLevel();
                if (level == null) {
                    level = parent.getLevel();
                }

                RolapCubeMember newmember = this.lookupCubeMember(parent, currMember, level);
                children.add(newmember);
            }

            Map<RolapMember, List<RolapMember>> tempMap = new HashMap();
            Iterator var17 = parentMembers.iterator();

            RolapMember child;
            while(var17.hasNext()) {
                child = (RolapMember)var17.next();
                tempMap.put(child, Collections.emptyList());
            }

            RolapMember var19;
            for(var17 = children.iterator(); var17.hasNext(); var19 = child.getParentMember()) {
                child = (RolapMember)var17.next();

                assert child != null : "child";
            }

        }

        public Map<? extends Member, Access> getMemberChildren(List<RolapMember> parentMembers, List<RolapMember> children, MemberChildrenConstraint constraint) {
            List<RolapMember> missed = new ArrayList();
            Iterator var5 = parentMembers.iterator();

            while(var5.hasNext()) {
                RolapMember parentMember = (RolapMember)var5.next();
                if (!parentMember.isNull()) {
                    missed.add(parentMember);
                }
            }

            if (missed.size() > 0) {
                this.readMemberChildren(missed, children, constraint);
            }

            return Util.toNullValuesMap(children);
        }

        public List<RolapMember> getMembersInLevel(final RolapLevel level, TupleConstraint constraint) {
            List<RolapMember> members = null;
            boolean joinReq = constraint instanceof SqlContextConstraint;
            final List list;
            if (!joinReq) {
                list = RolapCubeHierarchy.this.rolapHierarchy.getMemberReader().getMembersInLevel(((RolapCubeLevel)level).getRolapLevel(), constraint);
            } else {
                list = super.getMembersInLevel(level, constraint);
            }

            return new UnsupportedList<RolapMember>() {
                public RolapMember get(int index) {
                    return this.mutate((RolapMember)list.get(index));
                }

                public int size() {
                    return list.size();
                }

                public Iterator<RolapMember> iterator() {
                    final Iterator<RolapMember> it = list.iterator();
                    return new Iterator<RolapMember>() {
                        public boolean hasNext() {
                            return it.hasNext();
                        }

                        public RolapMember next() {
                            return mutate((RolapMember)it.next());
                        }

                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }

                private RolapMember mutate(RolapMember member) {
                    RolapCubeMember parent = null;
                    if (member.getParentMember() != null) {
                        parent = RolapCubeHierarchy.createAncestorMembers(NoCacheRolapCubeHierarchyMemberReader.this, (RolapCubeLevel)level.getParentLevel(), member.getParentMember());
                    }

                    return NoCacheRolapCubeHierarchyMemberReader.this.lookupCubeMember(parent, member, (RolapCubeLevel)level);
                }
            };
        }

        public RolapCubeMember lookupCubeMember(RolapCubeMember parent, RolapMember member, RolapCubeLevel level) {
            return member.getKey() == RolapUtil.sqlNullValue && member.isAll() ? RolapCubeHierarchy.this.getAllMember() : new RolapCubeMember(parent, member, level);
        }

        public int getMemberCount() {
            return RolapCubeHierarchy.this.rolapHierarchy.getMemberReader().getMemberCount();
        }
    }

    public class CacheRolapCubeHierarchyMemberReader extends SmartMemberReader implements RolapCubeHierarchy.RolapCubeHierarchyMemberReader {
        protected final RolapCubeHierarchy.RolapCubeSqlMemberSource cubeSource;
        protected MemberCacheHelper rolapCubeCacheHelper;
        private final boolean enableCache;

        public CacheRolapCubeHierarchyMemberReader() {
            super(new SqlMemberSource(RolapCubeHierarchy.this));
            this.enableCache = MondrianProperties.instance().EnableRolapCubeMemberCache.get();
            this.rolapCubeCacheHelper = new MemberCacheHelper(RolapCubeHierarchy.this);
            this.cubeSource = new RolapCubeHierarchy.RolapCubeSqlMemberSource(this, RolapCubeHierarchy.this, this.rolapCubeCacheHelper, this.cacheHelper);
            this.cubeSource.setCache(this.getMemberCache());
        }

        public MemberBuilder getMemberBuilder() {
            return this.cubeSource;
        }

        public MemberCacheHelper getRolapCubeMemberCacheHelper() {
            return this.rolapCubeCacheHelper;
        }

        public List<RolapMember> getRootMembers() {
            if (this.rootMembers == null) {
                this.rootMembers = this.getMembersInLevel(RolapCubeHierarchy.this.cubeLevels[0]);
            }

            return this.rootMembers;
        }

        protected void readMemberChildren(List<RolapMember> parentMembers, List<RolapMember> children, MemberChildrenConstraint constraint) {
            List<RolapMember> rolapChildren = new ArrayList();
            List<RolapMember> rolapParents = new ArrayList();
            Map<String, RolapCubeMember> lookup = new HashMap();
            Iterator var7 = parentMembers.iterator();

            RolapMember child;
            while(var7.hasNext()) {
                RolapMember memberx = (RolapMember)var7.next();
                if (!(memberx instanceof VisualTotalMember)) {
                    RolapCubeMember cubeMember = (RolapCubeMember)memberx;
                    child = cubeMember.getRolapMember();
                    lookup.put(child.getUniqueName(), cubeMember);
                    rolapParents.add(child);
                }
            }

            boolean joinReq = constraint instanceof SqlContextConstraint;
            if (joinReq) {
                super.readMemberChildren(parentMembers, rolapChildren, constraint);
            } else {
                RolapCubeHierarchy.this.rolapHierarchy.getMemberReader().getMemberChildren(rolapParents, rolapChildren, constraint);
            }

            Iterator var17 = rolapChildren.iterator();

            while(var17.hasNext()) {
                RolapMember currMember = (RolapMember)var17.next();
                RolapCubeMember parent = (RolapCubeMember)lookup.get(currMember.getParentMember().getUniqueName());
                RolapCubeLevel level = parent.getLevel().getChildLevel();
                if (level == null) {
                    level = parent.getLevel();
                }

                RolapCubeMember newmember = this.lookupCubeMember(parent, currMember, level);
                children.add(newmember);
            }

            Map<RolapMember, List<RolapMember>> tempMap = new HashMap();
            Iterator var20 = parentMembers.iterator();

            while(var20.hasNext()) {
                child = (RolapMember)var20.next();
                tempMap.put(child, Collections.emptyList());
            }

            var20 = children.iterator();

            while(var20.hasNext()) {
                child = (RolapMember)var20.next();

                assert child != null : "child";

                RolapMember parentMember = child.getParentMember();
                List<RolapMember> cacheList = (List)tempMap.get(parentMember);
                if (cacheList != null) {
                    if (cacheList == Collections.EMPTY_LIST) {
                        cacheList = new ArrayList();
                        tempMap.put(parentMember, cacheList);
                    }

                    ((List)cacheList).add(child);
                }
            }

            synchronized(this.cacheHelper) {
                Iterator var22 = tempMap.entrySet().iterator();

                while(var22.hasNext()) {
                    Entry<RolapMember, List<RolapMember>> entry = (Entry)var22.next();
                    RolapMember member = (RolapMember)entry.getKey();
                    if (this.rolapCubeCacheHelper.getChildrenFromCache(member, constraint) == null) {
                        List<RolapMember> cacheListx = (List)entry.getValue();
                        if (this.enableCache) {
                            this.rolapCubeCacheHelper.putChildren(member, constraint, cacheListx);
                        }
                    }
                }

            }
        }

        public Map<? extends Member, Access> getMemberChildren(List<RolapMember> parentMembers, List<RolapMember> children, MemberChildrenConstraint constraint) {
            synchronized(this.cacheHelper) {
                this.checkCacheStatus();
                List<RolapMember> missed = new ArrayList();
                Iterator var6 = parentMembers.iterator();

                while(true) {
                    if (!var6.hasNext()) {
                        if (missed.size() > 0) {
                            this.readMemberChildren(missed, children, constraint);
                        }
                        break;
                    }

                    RolapMember parentMember = (RolapMember)var6.next();
                    List<RolapMember> list = this.rolapCubeCacheHelper.getChildrenFromCache(parentMember, constraint);
                    if (list == null) {
                        if (!parentMember.isNull()) {
                            missed.add(parentMember);
                        }
                    } else {
                        children.addAll(list);
                    }
                }
            }

            return Util.toNullValuesMap(children);
        }

        public List<RolapMember> getMembersInLevel(RolapLevel level, TupleConstraint constraint) {
            synchronized(this.cacheHelper) {
                this.checkCacheStatus();
                List<RolapMember> members = this.rolapCubeCacheHelper.getLevelMembersFromCache(level, constraint);
                if (members != null) {
                    return members;
                } else {
                    boolean joinReq = constraint instanceof SqlContextConstraint;
                    RolapCubeLevel cubeLevel = (RolapCubeLevel)level;
                    List list;
                    if (!joinReq) {
                        list = RolapCubeHierarchy.this.rolapHierarchy.getMemberReader().getMembersInLevel(cubeLevel.getRolapLevel(), constraint);
                    } else {
                        list = super.getMembersInLevel(level, constraint);
                    }

                    List<RolapMember> newlist = new ArrayList();
                    Iterator var9 = list.iterator();

                    while(var9.hasNext()) {
                        RolapMember member = (RolapMember)var9.next();
                        RolapCubeMember cubeMember = this.lookupCubeMemberWithParent(member, cubeLevel);
                        newlist.add(cubeMember);
                    }

                    this.rolapCubeCacheHelper.putLevelMembersInCache(level, constraint, newlist);
                    return newlist;
                }
            }
        }

        private RolapCubeMember lookupCubeMemberWithParent(RolapMember member, RolapCubeLevel cubeLevel) {
            RolapMember parentMember = member.getParentMember();
            RolapCubeMember parentCubeMember;
            if (parentMember == null) {
                parentCubeMember = null;
            } else {
                RolapCubeLevel parentLevel = parentMember.getLevel() == member.getLevel() ? cubeLevel : cubeLevel.getParentLevel();
                parentCubeMember = this.lookupCubeMemberWithParent(parentMember, parentLevel);
            }

            return this.lookupCubeMember(parentCubeMember, member, cubeLevel);
        }

        public RolapMember getMemberByKey(RolapLevel level, List<Comparable> keyValues) {
            synchronized(this.cacheHelper) {
                RolapMember member = super.getMemberByKey(level, keyValues);
                return RolapCubeHierarchy.createAncestorMembers(this, (RolapCubeLevel)level, member);
            }
        }

        public RolapCubeMember lookupCubeMember(RolapCubeMember parent, RolapMember member, RolapCubeLevel level) {
            synchronized(this.cacheHelper) {
                if (member.getKey() == RolapUtil.sqlNullValue && member.isAll()) {
                    return RolapCubeHierarchy.this.getAllMember();
                } else {
                    RolapCubeMember cubeMember;
                    if (this.enableCache) {
                        Object key = this.rolapCubeCacheHelper.makeKey(parent, member.getKey());
                        cubeMember = (RolapCubeMember)this.rolapCubeCacheHelper.getMember(key, false);
                        if (cubeMember == null) {
                            cubeMember = new RolapCubeMember(parent, member, level);
                            this.rolapCubeCacheHelper.putMember(key, cubeMember);
                        } else if (level.hasOrdinalExp()) {
                            this.fixOrdinal(cubeMember, member.getOrdinal());
                        }
                    } else {
                        cubeMember = new RolapCubeMember(parent, member, level);
                    }

                    return cubeMember;
                }
            }
        }

        private void fixOrdinal(RolapCubeMember rlCubeMemberToFix, int ordinalToSet) {
            RolapMember rolapMember = rlCubeMemberToFix.getRolapMember();
            if (rolapMember instanceof RolapMemberBase) {
                ((RolapMemberBase)rolapMember).setOrdinal(ordinalToSet, true);
            }

        }

        public int getMemberCount() {
            return RolapCubeHierarchy.this.rolapHierarchy.getMemberReader().getMemberCount();
        }

        protected void checkCacheStatus() {
            synchronized(this.cacheHelper) {
                if (this.cacheHelper.getChangeListener() != null && this.cacheHelper.getChangeListener().isHierarchyChanged(this.getHierarchy())) {
                    this.cacheHelper.flushCache();
                    this.rolapCubeCacheHelper.flushCache();
                    if (RolapCubeHierarchy.this.rolapHierarchy.getMemberReader() instanceof SmartMemberReader) {
                        SmartMemberReader smartMemberReader = (SmartMemberReader)RolapCubeHierarchy.this.rolapHierarchy.getMemberReader();
                        if (smartMemberReader.getMemberCache() instanceof MemberCacheHelper) {
                            MemberCacheHelper helper = (MemberCacheHelper)smartMemberReader.getMemberCache();
                            helper.flushCache();
                        }
                    }
                }

            }
        }
    }

    public interface RolapCubeHierarchyMemberReader extends MemberReader {
        RolapCubeMember lookupCubeMember(RolapCubeMember var1, RolapMember var2, RolapCubeLevel var3);

        MemberCacheHelper getRolapCubeMemberCacheHelper();
    }
}