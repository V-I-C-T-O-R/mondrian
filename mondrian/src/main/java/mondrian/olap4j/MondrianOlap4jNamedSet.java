/* Decompiler 28ms, total 377ms, lines 69 */
package mondrian.olap4j;

import java.sql.SQLException;
import mondrian.olap.OlapElement;
import mondrian.olap.OlapElement.LocalizedProperty;
import org.olap4j.impl.Named;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.NamedSet;

public class MondrianOlap4jNamedSet extends MondrianOlap4jMetadataElement implements NamedSet, Named {
    private final MondrianOlap4jCube olap4jCube;
    private mondrian.olap.NamedSet namedSet;

    MondrianOlap4jNamedSet(MondrianOlap4jCube olap4jCube, mondrian.olap.NamedSet namedSet) {
        this.olap4jCube = olap4jCube;
        this.namedSet = namedSet;
    }

    public Cube getCube() {
        return this.olap4jCube;
    }

    public ParseTreeNode getExpression() {
        MondrianOlap4jConnection olap4jConnection = this.olap4jCube.olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData.olap4jConnection;
        return olap4jConnection.toOlap4j(this.namedSet.getExp());
    }

    public mondrian.olap.NamedSet getNamedSet() {
        return this.namedSet;
    }

    public String getName() {
        return this.namedSet.getName();
    }

    public String getUniqueName() {
        return this.namedSet.getUniqueName();
    }

    public String getCaption() {
        return this.namedSet.getLocalized(LocalizedProperty.CAPTION, this.olap4jCube.olap4jSchema.getLocale());
    }

    public String getDescription() {
        return this.namedSet.getLocalized(LocalizedProperty.DESCRIPTION, this.olap4jCube.olap4jSchema.getLocale());
    }

    public boolean isVisible() {
        return this.namedSet.isVisible();
    }

    protected OlapElement getOlapElement() {
        return this.namedSet;
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