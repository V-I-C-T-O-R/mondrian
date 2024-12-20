/* Decompiler 9ms, total 483ms, lines 30 */
package mondrian.olap;

import java.io.PrintWriter;

public class TransactionCommand extends QueryPart {
    private final TransactionCommand.Command command;

    TransactionCommand(TransactionCommand.Command command) {
        this.command = command;
    }

    public void unparse(PrintWriter pw) {
        pw.print(this.command.name() + "TRANSACTION");
    }

    public Object[] getChildren() {
        return new Object[]{this.command};
    }

    public TransactionCommand.Command getCommand() {
        return this.command;
    }

    public static enum Command {
        BEGIN,
        COMMIT,
        ROLLBACK;
    }
}