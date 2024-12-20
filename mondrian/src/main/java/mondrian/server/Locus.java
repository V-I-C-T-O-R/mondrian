/* Decompiler 55ms, total 510ms, lines 78 */
package mondrian.server;

import mondrian.olap.MondrianServer;
import mondrian.rolap.RolapConnection;
import mondrian.util.ArrayStack;

public class Locus {
    public final Execution execution;
    public final String message;
    public final String component;
    private static final ThreadLocal<ArrayStack<Locus>> THREAD_LOCAL = new ThreadLocal<ArrayStack<Locus>>() {
        protected ArrayStack<Locus> initialValue() {
            return new ArrayStack();
        }
    };

    public Locus(Execution execution, String component, String message) {
        assert execution != null;

        this.execution = execution;
        this.component = component;
        this.message = message;
    }

    public static void pop(Locus locus) {
        Locus pop = (Locus)((ArrayStack)THREAD_LOCAL.get()).pop();

        assert locus == pop;

    }

    public static void push(Locus locus) {
        ((ArrayStack)THREAD_LOCAL.get()).push(locus);
    }

    public static Locus peek() {
        return (Locus)((ArrayStack)THREAD_LOCAL.get()).peek();
    }

    public static boolean isEmpty() {
        return ((ArrayStack)THREAD_LOCAL.get()).isEmpty();
    }

    public static <T> T execute(RolapConnection connection, String component, Locus.Action<T> action) {
        Statement statement = connection.getInternalStatement();
        Execution execution = new Execution(statement, 0L);
        return execute(execution, component, action);
    }

    public static <T> T execute(Execution execution, String component, Locus.Action<T> action) {
        Locus locus = new Locus(execution, component, (String)null);
        push(locus);

        T var4;
        try {
            var4 = action.execute();
        } finally {
            pop(locus);
        }

        return var4;
    }

    public final MondrianServer getServer() {
        return this.execution.statement.getMondrianConnection().getServer();
    }

    public interface Action<T> {
        T execute();
    }
}