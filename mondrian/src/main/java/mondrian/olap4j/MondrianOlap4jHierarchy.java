/* Decompiler 131ms, total 602ms, lines 133 */
package mondrian.olap4j;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import mondrian.olap.OlapElement;
import mondrian.olap.SchemaReader;
import mondrian.olap.OlapElement.LocalizedProperty;
import org.olap4j.OlapException;
import org.olap4j.impl.AbstractNamedList;
import org.olap4j.impl.Named;
import org.olap4j.impl.NamedListImpl;
import org.olap4j.impl.Olap4jUtil;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Member;
import org.olap4j.metadata.NamedList;

public class MondrianOlap4jHierarchy extends MondrianOlap4jMetadataElement implements Hierarchy, Named {
    final MondrianOlap4jSchema olap4jSchema;
    final mondrian.olap.Hierarchy hierarchy;

    MondrianOlap4jHierarchy(MondrianOlap4jSchema olap4jSchema, mondrian.olap.Hierarchy hierarchy) {
        this.olap4jSchema = olap4jSchema;
        this.hierarchy = hierarchy;
    }

    public boolean equals(Object obj) {
        return obj instanceof MondrianOlap4jHierarchy && this.hierarchy.equals(((MondrianOlap4jHierarchy)obj).hierarchy);
    }

    public int hashCode() {
        return this.hierarchy.hashCode();
    }

    public Dimension getDimension() {
        return new MondrianOlap4jDimension(this.olap4jSchema, this.hierarchy.getDimension());
    }

    public NamedList<Level> getLevels() {
        NamedList<MondrianOlap4jLevel> list = new NamedListImpl();
        MondrianOlap4jConnection olap4jConnection = this.olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData.olap4jConnection;
        SchemaReader schemaReader = olap4jConnection.getMondrianConnection2().getSchemaReader().withLocus();
        Iterator var4 = schemaReader.getHierarchyLevels(this.hierarchy).iterator();

        while(var4.hasNext()) {
            mondrian.olap.Level level = (mondrian.olap.Level)var4.next();
            list.add(olap4jConnection.toOlap4j(level));
        }

        return Olap4jUtil.cast(list);
    }

    public boolean hasAll() {
        return this.hierarchy.hasAll();
    }

    public Member getDefaultMember() throws OlapException {
        MondrianOlap4jConnection olap4jConnection = this.olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData.olap4jConnection;
        SchemaReader schemaReader = olap4jConnection.getMondrianConnection().getSchemaReader().withLocus();
        return olap4jConnection.toOlap4j(schemaReader.getHierarchyDefaultMember(this.hierarchy));
    }

    public NamedList<Member> getRootMembers() throws OlapException {
        final MondrianOlap4jConnection olap4jConnection = this.olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData.olap4jConnection;
        final List<mondrian.olap.Member> levelMembers = olap4jConnection.getMondrianConnection().getSchemaReader().withLocus().getLevelMembers(this.hierarchy.getLevels()[0], true);
        return new AbstractNamedList<Member>() {
            public String getName(Object member) {
                return ((Member)member).getName();
            }

            public Member get(int index) {
                return olap4jConnection.toOlap4j((mondrian.olap.Member)levelMembers.get(index));
            }

            public int size() {
                return levelMembers.size();
            }
        };
    }

    public String getName() {
        return this.hierarchy.getName();
    }

    public String getUniqueName() {
        return this.hierarchy.getUniqueName();
    }

    public String getCaption() {
        return this.hierarchy.getLocalized(LocalizedProperty.CAPTION, this.olap4jSchema.getLocale());
    }

    public String getDescription() {
        return this.hierarchy.getLocalized(LocalizedProperty.DESCRIPTION, this.olap4jSchema.getLocale());
    }

    public String getDisplayFolder() {
        return this.hierarchy.getDisplayFolder();
    }

    public boolean isVisible() {
        return this.hierarchy.isVisible();
    }

    protected OlapElement getOlapElement() {
        return this.hierarchy;
    }

    public mondrian.olap.Hierarchy getHierarchy() {
        return this.hierarchy;
    }

    // $FF: synthetic method
    // $FF: bridge method
    public boolean isWrapperFor(Class var1) {
        return super.isWrapperFor(var1);
    }

    // $FF: synthetic method
    // $FF: bridge method
    public Object unwrap(Class var1) throws SQLException {
        return super.unwrap(var1);
    }
}