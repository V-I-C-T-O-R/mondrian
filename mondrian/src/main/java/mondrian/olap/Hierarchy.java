/* Decompiler 1ms, total 433ms, lines 23 */
package mondrian.olap;

public interface Hierarchy extends OlapElement, Annotated {
    Dimension getDimension();

    Level[] getLevels();

    Member getDefaultMember();

    Member getAllMember();

    Member getNullMember();

    boolean hasAll();

    Member createMember(Member var1, Level var2, String var3, Formula var4);

    /** @deprecated */
    String getUniqueNameSsas();

    String getDisplayFolder();
}