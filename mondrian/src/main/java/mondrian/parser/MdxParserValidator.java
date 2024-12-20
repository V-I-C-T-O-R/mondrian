/* Decompiler 1ms, total 501ms, lines 45 */
package mondrian.parser;

import java.util.List;
import mondrian.olap.CalculatedFormula;
import mondrian.olap.DmvQuery;
import mondrian.olap.DrillThrough;
import mondrian.olap.Exp;
import mondrian.olap.Explain;
import mondrian.olap.Formula;
import mondrian.olap.FunTable;
import mondrian.olap.Query;
import mondrian.olap.QueryAxis;
import mondrian.olap.QueryPart;
import mondrian.olap.Refresh;
import mondrian.olap.Subcube;
import mondrian.olap.TransactionCommand;
import mondrian.olap.Update;
import mondrian.olap.TransactionCommand.Command;
import mondrian.olap.Update.UpdateClause;
import mondrian.server.Statement;

public interface MdxParserValidator {
    QueryPart parseInternal(Statement var1, String var2, boolean var3, FunTable var4, boolean var5);

    Exp parseExpression(Statement var1, String var2, boolean var3, FunTable var4);

    public interface QueryPartFactory {
        Query makeQuery(Statement var1, Formula[] var2, QueryAxis[] var3, Subcube var4, Exp var5, QueryPart[] var6, boolean var7);
        Query makeQuery(Statement var1, Formula[] var2, QueryAxis[] var3, String var4, Exp var5, QueryPart[] var6, boolean var7);

        DrillThrough makeDrillThrough(Query var1, int var2, int var3, List<Exp> var4);

        CalculatedFormula makeCalculatedFormula(String var1, Formula var2);

        Explain makeExplain(QueryPart var1);

        Refresh makeRefresh(String var1);

        Update makeUpdate(String var1, List<UpdateClause> var2);

        DmvQuery makeDmvQuery(String var1, List<String> var2, Exp var3);

        TransactionCommand makeTransactionCommand(Command var1);
    }
}