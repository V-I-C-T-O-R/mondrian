/* Decompiler 15ms, total 702ms, lines 19 */
package mondrian.parser;

import mondrian.olap.Exp;
import mondrian.olap.FunTable;
import mondrian.olap.Parser;
import mondrian.olap.QueryPart;
import mondrian.olap.Parser.FactoryImpl;
import mondrian.server.Statement;

public class MdxParserValidatorImpl implements MdxParserValidator {
    public QueryPart parseInternal(Statement statement, String queryString, boolean debug, FunTable funTable, boolean strictValidation) {
        return (new Parser()).parseInternal(new FactoryImpl(), statement, queryString, debug, funTable, strictValidation);
    }

    public Exp parseExpression(Statement statement, String queryString, boolean debug, FunTable funTable) {
        return (new Parser()).parseExpression(new FactoryImpl(), statement, queryString, debug, funTable);
    }
}