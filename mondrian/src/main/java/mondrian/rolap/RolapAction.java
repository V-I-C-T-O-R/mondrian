/* Decompiler 30ms, total 501ms, lines 26 */
package mondrian.rolap;

public abstract class RolapAction {
    private final String name;
    private final String caption;
    private final String description;

    public RolapAction(String name, String caption, String description) {
        this.name = name;
        this.caption = caption;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public String getCaption() {
        return this.caption;
    }

    public String getDescription() {
        return this.description;
    }
}