/* Decompiler 40ms, total 554ms, lines 54 */
package mondrian.parser;

import mondrian.olap.Exp;
import mondrian.olap.FunTable;
import mondrian.olap.MondrianException;
import mondrian.olap.QueryPart;
import mondrian.olap.Util;
import mondrian.olap.Parser.FactoryImpl;
import mondrian.parser.MdxParserValidator.QueryPartFactory;
import mondrian.server.Statement;

public class JavaccParserValidatorImpl implements MdxParserValidator {
    private final QueryPartFactory factory;

    public JavaccParserValidatorImpl() {
        this(new FactoryImpl());
    }

    public JavaccParserValidatorImpl(QueryPartFactory factory) {
        this.factory = factory;
    }

    public QueryPart parseInternal(Statement statement, String queryString, boolean debug, FunTable funTable, boolean strictValidation) {
        MdxParserImpl mdxParser = new MdxParserImpl(this.factory, statement, queryString, debug, funTable, strictValidation);

        try {
            return mdxParser.statementEof();
        } catch (ParseException var8) {
            throw this.convertException(queryString, var8);
        }
    }

    public Exp parseExpression(Statement statement, String queryString, boolean debug, FunTable funTable) {
        MdxParserImpl mdxParser = new MdxParserImpl(this.factory, statement, queryString, debug, funTable, false);

        try {
            return mdxParser.expressionEof();
        } catch (ParseException var7) {
            throw this.convertException(queryString, var7);
        }
    }

    private RuntimeException convertException(String queryString, ParseException pe) {
        Object e;
        if (pe.getMessage().startsWith("Encountered ")) {
            e = new MondrianException("Syntax error at line " + pe.currentToken.next.beginLine + ", column " + pe.currentToken.next.beginColumn + ", token '" + pe.currentToken.next.image + "'");
        } else {
            e = pe;
        }

        return Util.newError((Throwable)e, "While parsing " + queryString);
    }
}