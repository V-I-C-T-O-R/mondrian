/* Decompiler 82ms, total 520ms, lines 107 */
package mondrian.olap.type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import mondrian.olap.Dimension;
import mondrian.olap.Hierarchy;
import mondrian.olap.Level;
import mondrian.olap.Util;

public class SetType implements Type {
    private final Type elementType;
    private final String digest;

    public SetType(Type elementType) {
        assert elementType == null || elementType instanceof MemberType || elementType instanceof TupleType;

        this.elementType = elementType;
        this.digest = "SetType<" + elementType + ">";
    }

    public int hashCode() {
        return this.digest.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof SetType) {
            SetType that = (SetType)obj;
            return Util.equals(this.elementType, that.elementType);
        } else {
            return false;
        }
    }

    public String toString() {
        return this.digest;
    }

    public Type getElementType() {
        return this.elementType;
    }

    public boolean usesDimension(Dimension dimension, boolean definitely) {
        return this.elementType == null ? definitely : this.elementType.usesDimension(dimension, definitely);
    }

    public boolean usesHierarchy(Hierarchy hierarchy, boolean definitely) {
        return this.elementType == null ? definitely : this.elementType.usesHierarchy(hierarchy, definitely);
    }

    public List<Hierarchy> getHierarchies() {
        if (this.elementType instanceof TupleType) {
            return ((TupleType)this.elementType).getHierarchies();
        } else {
            ArrayList<Hierarchy> result = new ArrayList();
            result.add(this.getHierarchy());
            return result;
        }
    }

    public Dimension getDimension() {
        return this.elementType == null ? null : this.elementType.getDimension();
    }

    public Hierarchy getHierarchy() {
        return this.elementType == null ? null : this.elementType.getHierarchy();
    }

    public Level getLevel() {
        return this.elementType == null ? null : this.elementType.getLevel();
    }

    public int getArity() {
        return this.elementType.getArity();
    }

    public Type computeCommonType(Type type, int[] conversionCount) {
        if (!(type instanceof SetType)) {
            return null;
        } else {
            SetType that = (SetType)type;
            Type mostGeneralElementType = this.getElementType().computeCommonType(that.getElementType(), conversionCount);
            return mostGeneralElementType == null ? null : new SetType(mostGeneralElementType);
        }
    }

    public boolean isInstance(Object value) {
        if (!(value instanceof List)) {
            return false;
        } else {
            List list = (List)value;
            Iterator var3 = list.iterator();

            Object o;
            do {
                if (!var3.hasNext()) {
                    return true;
                }

                o = var3.next();
            } while(this.elementType.isInstance(o));

            return false;
        }
    }
}