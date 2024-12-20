/* Decompiler 116ms, total 667ms, lines 127 */
package mondrian.olap;

import java.util.List;
import java.util.Map;
import mondrian.olap.Id.Segment;
import mondrian.olap.type.MemberType;
import mondrian.olap.type.SetType;
import mondrian.olap.type.TupleType;
import mondrian.olap.type.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetBase extends OlapElementBase implements NamedSet {
    private static final Logger LOGGER = LogManager.getLogger(SetBase.class);
    private String name;
    private Map<String, Annotation> annotationMap;
    private String description;
    private final String uniqueName;
    private Exp exp;
    private boolean validated;
    private String displayFolder;

    SetBase(String name, String caption, String description, Exp exp, boolean validated, Map<String, Annotation> annotationMap) {
        this.name = name;
        this.annotationMap = annotationMap;
        this.caption = caption;
        this.description = description;
        this.exp = exp;
        this.validated = validated;
        this.uniqueName = "[" + name + "]";
    }

    public Map<String, Annotation> getAnnotationMap() {
        return this.annotationMap;
    }

    public String getNameUniqueWithinQuery() {
        return System.identityHashCode(this) + "";
    }

    public boolean isDynamic() {
        return false;
    }

    public Object clone() {
        return new SetBase(this.name, this.caption, this.description, this.exp.clone(), this.validated, this.annotationMap);
    }

    protected Logger getLogger() {
        return LOGGER;
    }

    public String getUniqueName() {
        return this.uniqueName;
    }

    public String getName() {
        return this.name;
    }

    public String getQualifiedName() {
        return null;
    }

    public String getDescription() {
        return this.description;
    }

    public String getDisplayFolder() {
        return this.displayFolder;
    }

    public List<Hierarchy> getHierarchies() {
        return ((SetType)this.exp.getType()).getHierarchies();
    }

    public Hierarchy getHierarchy() {
        return this.exp.getType().getHierarchy();
    }

    public Dimension getDimension() {
        return this.getHierarchy().getDimension();
    }

    public OlapElement lookupChild(SchemaReader schemaReader, Segment s, MatchType matchType) {
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDisplayFolder(String displayFolder) {
        this.displayFolder = displayFolder;
    }

    public void setAnnotationMap(Map<String, Annotation> annotationMap) {
        this.annotationMap = annotationMap;
    }

    public Exp getExp() {
        return this.exp;
    }

    public NamedSet validate(Validator validator) {
        if (!this.validated) {
            this.exp = validator.validate(this.exp, false);
            this.validated = true;
        }

        return this;
    }

    public Type getType() {
        Type type = this.exp.getType();
        if (type instanceof MemberType || type instanceof TupleType) {
            type = new SetType((Type)type);
        }

        return (Type)type;
    }
}