/* Decompiler 205ms, total 1557ms, lines 249 */
package mondrian.olap4j;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mondrian.olap.OlapElement;
import mondrian.rolap.RolapConnection;
import mondrian.rolap.RolapMeasure;
import mondrian.server.Locus;
import mondrian.server.Locus.Action;
import org.olap4j.OlapException;
import org.olap4j.impl.AbstractNamedList;
import org.olap4j.impl.Named;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Member;
import org.olap4j.metadata.NamedList;
import org.olap4j.metadata.Property;
import org.olap4j.metadata.Member.Type;
import org.olap4j.metadata.Property.StandardMemberProperty;

public class MondrianOlap4jMember extends MondrianOlap4jMetadataElement implements Member, Named {
    final mondrian.olap.Member member;
    final MondrianOlap4jSchema olap4jSchema;

    MondrianOlap4jMember(MondrianOlap4jSchema olap4jSchema, mondrian.olap.Member mondrianMember) {
        assert mondrianMember != null;

        assert mondrianMember instanceof RolapMeasure == (this instanceof MondrianOlap4jMeasure);

        this.olap4jSchema = olap4jSchema;
        this.member = mondrianMember;
    }

    public boolean equals(Object obj) {
        return obj instanceof MondrianOlap4jMember && this.member.equals(((MondrianOlap4jMember)obj).member);
    }

    public int hashCode() {
        return this.member.hashCode();
    }

    public String toString() {
        return this.getUniqueName();
    }

    public NamedList<MondrianOlap4jMember> getChildMembers() throws OlapException {
        final RolapConnection conn = this.olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData.olap4jConnection.getMondrianConnection();
        final List<mondrian.olap.Member> children = (List)Locus.execute(conn, "MondrianOlap4jMember.getChildMembers", new Action<List<mondrian.olap.Member>>() {
            public List<mondrian.olap.Member> execute() {
                return conn.getSchemaReader().getMemberChildren(MondrianOlap4jMember.this.member);
            }
        });
        return new AbstractNamedList<MondrianOlap4jMember>() {
            public String getName(Object member) {
                return ((MondrianOlap4jMember)member).getName();
            }

            public MondrianOlap4jMember get(int index) {
                return new MondrianOlap4jMember(MondrianOlap4jMember.this.olap4jSchema, (mondrian.olap.Member)children.get(index));
            }

            public int size() {
                return children.size();
            }
        };
    }

    public int getChildMemberCount() throws OlapException {
        final RolapConnection conn = this.olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData.olap4jConnection.getMondrianConnection();
        return (Integer)Locus.execute(conn, "MondrianOlap4jMember.getChildMemberCount", new Action<Integer>() {
            public Integer execute() {
                return conn.getSchemaReader().getMemberChildren(MondrianOlap4jMember.this.member).size();
            }
        });
    }

    public MondrianOlap4jMember getParentMember() {
        final mondrian.olap.Member parentMember = this.member.getParentMember();
        if (parentMember == null) {
            return null;
        } else {
            final RolapConnection conn = this.olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData.olap4jConnection.getMondrianConnection2();
            boolean isVisible = (Boolean)Locus.execute(conn, "MondrianOlap4jMember.getParentMember", new Action<Boolean>() {
                public Boolean execute() {
                    return conn.getSchemaReader().isVisible(parentMember);
                }
            });
            return !isVisible ? null : new MondrianOlap4jMember(this.olap4jSchema, parentMember);
        }
    }

    public Level getLevel() {
        return new MondrianOlap4jLevel(this.olap4jSchema, this.member.getLevel());
    }

    public Hierarchy getHierarchy() {
        return new MondrianOlap4jHierarchy(this.olap4jSchema, this.member.getHierarchy());
    }

    public Dimension getDimension() {
        return new MondrianOlap4jDimension(this.olap4jSchema, this.member.getDimension());
    }

    public Type getMemberType() {
        return Type.valueOf(this.member.getMemberType().name());
    }

    public boolean isAll() {
        return this.member.isAll();
    }

    public boolean isChildOrEqualTo(Member member) {
        throw new UnsupportedOperationException();
    }

    public boolean isCalculated() {
        return this.getMemberType() == Type.FORMULA;
    }

    public int getSolveOrder() {
        return this.member.getSolveOrder();
    }

    public ParseTreeNode getExpression() {
        throw new UnsupportedOperationException();
    }

    public List<Member> getAncestorMembers() {
        List<Member> list = new ArrayList();

        for(MondrianOlap4jMember m = this.getParentMember(); m != null; m = m.getParentMember()) {
            list.add(m);
        }

        return list;
    }

    public boolean isCalculatedInQuery() {
        return this.member.isCalculatedInQuery();
    }

    public Object getPropertyValue(Property property) {
        return this.member.getPropertyValue(property.getName());
    }

    public String getPropertyFormattedValue(Property property) {
        return this.member.getPropertyFormattedValue(property.getName());
    }

    public void setProperty(Property property, Object value) throws OlapException {
        this.member.setProperty(property.getName(), value);
    }

    public NamedList<Property> getProperties() {
        return this.getLevel().getProperties();
    }

    public int getOrdinal() {
        Number ordinal = (Number)this.member.getPropertyValue(StandardMemberProperty.MEMBER_ORDINAL.getName());
        return ordinal.intValue();
    }

    public boolean isHidden() {
        return this.member.isHidden();
    }

    public int getDepth() {
        return this.member.getDepth();
    }

    public Member getDataMember() {
        mondrian.olap.Member dataMember = this.member.getDataMember();
        return dataMember == null ? null : new MondrianOlap4jMember(this.olap4jSchema, dataMember);
    }

    public String getName() {
        return this.member.getName();
    }

    public String getUniqueName() {
        return this.member.getUniqueName();
    }

    public String getCaption() {
        return this.member.getCaption();
    }

    public String getDescription() {
        return this.member.getDescription();
    }

    public boolean isVisible() {
        return (Boolean)this.member.getPropertyValue(mondrian.olap.Property.VISIBLE.getName());
    }

    public OlapElement getOlapElement() {
        return this.member;
    }

    public mondrian.olap.Member getOlapMember() {
        return this.member;
    }
}