/* Decompiler 2321ms, total 2882ms, lines 3293 */
package mondrian.parser;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import mondrian.mdx.UnresolvedFunCall;
import mondrian.olap.AxisOrdinal;
import mondrian.olap.CellProperty;
import mondrian.olap.Exp;
import mondrian.olap.Formula;
import mondrian.olap.FunTable;
import mondrian.olap.Id;
import mondrian.olap.Literal;
import mondrian.olap.MemberProperty;
import mondrian.olap.Query;
import mondrian.olap.QueryAxis;
import mondrian.olap.QueryPart;
import mondrian.olap.Subcube;
import mondrian.olap.Syntax;
import mondrian.olap.Util;
import mondrian.olap.AxisOrdinal.StandardAxisOrdinal;
import mondrian.olap.Id.KeySegment;
import mondrian.olap.Id.NameSegment;
import mondrian.olap.Id.Quoting;
import mondrian.olap.Id.Segment;
import mondrian.olap.QueryAxis.SubtotalVisibility;
import mondrian.olap.TransactionCommand.Command;
import mondrian.olap.Update.Allocation;
import mondrian.olap.Update.UpdateClause;
import mondrian.parser.MdxParserValidator.QueryPartFactory;
import mondrian.resource.MondrianResource;
import mondrian.server.Statement;

public class MdxParserImpl implements MdxParserImplConstants {
    private QueryPartFactory factory;
    private Statement statement;
    private FunTable funTable;
    private boolean strictValidation;
    private static final MemberProperty[] EmptyMemberPropertyArray = new MemberProperty[0];
    private static final Exp[] EmptyExpArray = new Exp[0];
    private static final Formula[] EmptyFormulaArray = new Formula[0];
    private static final Id[] EmptyIdArray = new Id[0];
    private static final QueryPart[] EmptyQueryPartArray = new QueryPart[0];
    private static final QueryAxis[] EmptyQueryAxisArray = new QueryAxis[0];
    private static final String DQ = "\"";
    private static final String DQDQ = "\"\"";
    public MdxParserImplTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    private int jj_gen;
    private final int[] jj_la1;
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private static int[] jj_la1_2;
    private static int[] jj_la1_3;
    private final MdxParserImpl.JJCalls[] jj_2_rtns;
    private boolean jj_rescan;
    private int jj_gc;
    private final MdxParserImpl.LookaheadSuccess jj_ls;
    private List<int[]> jj_expentries;
    private int[] jj_expentry;
    private int jj_kind;
    private int[] jj_lasttokens;
    private int jj_endpos;

    public MdxParserImpl(QueryPartFactory factory, Statement statement, String queryString, boolean debug, FunTable funTable, boolean strictValidation) {
        this((Reader)(new StringReader(term(queryString))));
        this.factory = factory;
        this.statement = statement;
        this.funTable = funTable;
        this.strictValidation = strictValidation;
    }

    private static String term(String s) {
        return s.endsWith("\n") ? s : s + "\n";
    }

    public void setTabSize(int tabSize) {
        this.jj_input_stream.setTabSize(tabSize);
    }

    Exp recursivelyParseExp(String s) throws ParseException {
        MdxParserImpl parser = new MdxParserImpl(this.factory, this.statement, s, false, this.funTable, this.strictValidation);
        return parser.expression();
    }

    static Id[] toIdArray(List<Id> idList) {
        return idList != null && idList.size() != 0 ? (Id[])idList.toArray(new Id[idList.size()]) : EmptyIdArray;
    }

    static Exp[] toExpArray(List<Exp> expList) {
        return expList != null && expList.size() != 0 ? (Exp[])expList.toArray(new Exp[expList.size()]) : EmptyExpArray;
    }

    static Formula[] toFormulaArray(List<Formula> formulaList) {
        return formulaList != null && formulaList.size() != 0 ? (Formula[])formulaList.toArray(new Formula[formulaList.size()]) : EmptyFormulaArray;
    }

    static MemberProperty[] toMemberPropertyArray(List<MemberProperty> mpList) {
        return mpList != null && mpList.size() != 0 ? (MemberProperty[])mpList.toArray(new MemberProperty[mpList.size()]) : EmptyMemberPropertyArray;
    }

    static QueryPart[] toQueryPartArray(List<QueryPart> qpList) {
        return qpList != null && qpList.size() != 0 ? (QueryPart[])qpList.toArray(new QueryPart[qpList.size()]) : EmptyQueryPartArray;
    }

    static QueryAxis[] toQueryAxisArray(List<QueryAxis> qpList) {
        return qpList != null && qpList.size() != 0 ? (QueryAxis[])qpList.toArray(new QueryAxis[qpList.size()]) : EmptyQueryAxisArray;
    }

    private static String stripQuotes(String s, String prefix, String suffix, String quoted) {
        assert s.startsWith(prefix) && s.endsWith(suffix);

        s = s.substring(prefix.length(), s.length() - suffix.length());
        s = Util.replace(s, quoted, suffix);
        return s;
    }

    private Exp createCall(Exp left, Segment segment, List<Exp> argList) {
        String name = segment instanceof NameSegment ? ((NameSegment)segment).name : null;
        if (argList != null) {
            if (left != null) {
                argList.add(0, left);
                return new UnresolvedFunCall(name, Syntax.Method, toExpArray(argList));
            } else {
                return new UnresolvedFunCall(name, Syntax.Function, toExpArray(argList));
            }
        } else {
            boolean call = false;
            Syntax syntax;
            switch(segment.quoting) {
                case UNQUOTED:
                    syntax = Syntax.Property;
                    call = this.funTable.isProperty(name);
                    break;
                case QUOTED:
                    syntax = Syntax.QuotedProperty;
                    break;
                default:
                    syntax = Syntax.AmpersandQuotedProperty;
            }

            if (left instanceof Id && !call) {
                return ((Id)left).append(segment);
            } else {
                return (Exp)(left == null ? new Id(segment) : new UnresolvedFunCall(name, syntax, new Exp[]{left}));
            }
        }
    }

    public final QueryPart statementEof() throws ParseException {
        QueryPart qp = this.statement();
        this.jj_consume_token(0);
        return qp;
    }

    public final Exp expressionEof() throws ParseException {
        Exp e = this.expression();
        this.jj_consume_token(0);
        return e;
    }

    public final Segment identifier() throws ParseException {
        Object segment;
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 14:
            case 35:
            case 90:
            case 98:
            case 99:
                segment = this.nameSegment();
                break;
            case 100:
            case 101:
                segment = this.keyIdentifier();
                break;
            default:
                this.jj_la1[0] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
        }

        return (Segment)segment;
    }

    public final NameSegment nameSegment() throws ParseException {
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 14:
            case 35:
                String id = this.keyword();
                return new NameSegment(id, Quoting.UNQUOTED);
            case 90:
                this.jj_consume_token(90);
                this.jj_consume_token(98);
                return new NameSegment("@" + this.token.image, Quoting.UNQUOTED);
            case 98:
                this.jj_consume_token(98);
                return new NameSegment(this.token.image, Quoting.UNQUOTED);
            case 99:
                this.jj_consume_token(99);
                return new NameSegment(stripQuotes(this.token.image, "[", "]", "]]"), Quoting.QUOTED);
            default:
                this.jj_la1[1] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
        }
    }

    public final KeySegment keyIdentifier() throws ParseException {
        ArrayList list = new ArrayList();

        while(true) {
            NameSegment key = this.ampId();
            list.add(key);
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 100:
                case 101:
                    break;
                default:
                    this.jj_la1[2] = this.jj_gen;
                    return new KeySegment(list);
            }
        }
    }

    public final NameSegment ampId() throws ParseException {
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 100:
                this.jj_consume_token(100);
                return new NameSegment(stripQuotes(this.token.image, "&[", "]", "]]"), Quoting.QUOTED);
            case 101:
                this.jj_consume_token(101);
                return new NameSegment(this.token.image.substring(1), Quoting.UNQUOTED);
            default:
                this.jj_la1[3] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
        }
    }

    public final String keyword() throws ParseException {
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 14:
                this.jj_consume_token(14);
                return "Dimension";
            case 35:
                this.jj_consume_token(35);
                return "Properties";
            default:
                this.jj_la1[4] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
        }
    }

    public final Id compoundId() throws ParseException {
        List<Segment> list = new ArrayList();
        Segment i = this.identifier();
        list.add(i);

        while(this.jj_2_1(Integer.MAX_VALUE)) {
            this.jj_consume_token(76);
            i = this.identifier();
            list.add(i);
        }

        return new Id(list);
    }

    public final Exp unaliasedExpression() throws ParseException {
        Object x = this.term5();

        while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 32:
                case 54:
                case 73:
                    Exp y;
                    switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 32:
                            this.jj_consume_token(32);
                            y = this.term5();
                            x = new UnresolvedFunCall("OR", Syntax.Infix, new Exp[]{(Exp)x, y});
                            continue;
                        case 54:
                            this.jj_consume_token(54);
                            y = this.term5();
                            x = new UnresolvedFunCall("XOR", Syntax.Infix, new Exp[]{(Exp)x, y});
                            continue;
                        case 73:
                            this.jj_consume_token(73);
                            y = this.term5();
                            x = new UnresolvedFunCall(":", Syntax.Infix, new Exp[]{(Exp)x, y});
                            continue;
                        default:
                            this.jj_la1[6] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                    }
                default:
                    this.jj_la1[5] = this.jj_gen;
                    return (Exp)x;
            }
        }
    }

    public final Exp term5() throws ParseException {
        Object x = this.term4();

        while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 1:
                    this.jj_consume_token(1);
                    Exp y = this.term4();
                    x = new UnresolvedFunCall("AND", Syntax.Infix, new Exp[]{(Exp)x, y});
                    break;
                default:
                    this.jj_la1[7] = this.jj_gen;
                    return (Exp)x;
            }
        }
    }

    public final Exp term4() throws ParseException {
        Exp x;
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 6:
            case 7:
            case 14:
            case 30:
            case 35:
            case 56:
            case 80:
            case 82:
            case 84:
            case 86:
            case 90:
            case 91:
            case 92:
            case 93:
            case 95:
            case 96:
            case 98:
            case 99:
                x = this.term3();
                return x;
            case 29:
                this.jj_consume_token(29);
                x = this.term4();
                return new UnresolvedFunCall("NOT", Syntax.Prefix, new Exp[]{x});
            default:
                this.jj_la1[8] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
        }
    }

    public final Exp term3() throws ParseException {
        Object x = this.term2();

        while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 23:
                case 24:
                case 25:
                case 29:
                case 77:
                case 78:
                case 79:
                case 81:
                case 83:
                case 85:
                    Exp y;
                    switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 77:
                        case 78:
                        case 79:
                        case 81:
                        case 83:
                        case 85:
                            Token op;
                            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 77:
                                    this.jj_consume_token(77);
                                    op = this.token;
                                    break;
                                case 78:
                                    this.jj_consume_token(78);
                                    op = this.token;
                                    break;
                                case 79:
                                    this.jj_consume_token(79);
                                    op = this.token;
                                    break;
                                case 80:
                                case 82:
                                case 84:
                                default:
                                    this.jj_la1[10] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                case 81:
                                    this.jj_consume_token(81);
                                    op = this.token;
                                    break;
                                case 83:
                                    this.jj_consume_token(83);
                                    op = this.token;
                                    break;
                                case 85:
                                    this.jj_consume_token(85);
                                    op = this.token;
                            }

                            y = this.term2();
                            x = new UnresolvedFunCall(op.image, Syntax.Infix, new Exp[]{(Exp)x, y});
                            break;
                        case 80:
                        case 82:
                        case 84:
                        default:
                            this.jj_la1[11] = this.jj_gen;
                            if (this.jj_2_2(2)) {
                                this.jj_consume_token(24);
                                this.jj_consume_token(30);
                                x = new UnresolvedFunCall("IS NULL", Syntax.Postfix, new Exp[]{(Exp)x});
                            } else if (this.jj_2_3(2)) {
                                this.jj_consume_token(24);
                                y = this.term2();
                                x = new UnresolvedFunCall("IS", Syntax.Infix, new Exp[]{(Exp)x, y});
                            } else {
                                switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                    case 24:
                                        this.jj_consume_token(24);
                                        this.jj_consume_token(17);
                                        x = new UnresolvedFunCall("IS EMPTY", Syntax.Postfix, new Exp[]{(Exp)x});
                                        break;
                                    case 25:
                                        this.jj_consume_token(25);
                                        y = this.term2();
                                        x = new UnresolvedFunCall("MATCHES", Syntax.Infix, new Exp[]{(Exp)x, y});
                                        break;
                                    default:
                                        this.jj_la1[12] = this.jj_gen;
                                        if (this.jj_2_4(2)) {
                                            this.jj_consume_token(29);
                                            this.jj_consume_token(25);
                                            y = this.term2();
                                            x = new UnresolvedFunCall("NOT", Syntax.Prefix, new Exp[]{new UnresolvedFunCall("MATCHES", Syntax.Infix, new Exp[]{(Exp)x, y})});
                                        } else {
                                            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                                case 23:
                                                    this.jj_consume_token(23);
                                                    y = this.term2();
                                                    x = new UnresolvedFunCall("IN", Syntax.Infix, new Exp[]{(Exp)x, y});
                                                    break;
                                                case 29:
                                                    this.jj_consume_token(29);
                                                    this.jj_consume_token(23);
                                                    y = this.term2();
                                                    x = new UnresolvedFunCall("NOT", Syntax.Prefix, new Exp[]{new UnresolvedFunCall("IN", Syntax.Infix, new Exp[]{(Exp)x, y})});
                                                    break;
                                                default:
                                                    this.jj_la1[13] = this.jj_gen;
                                                    this.jj_consume_token(-1);
                                                    throw new ParseException();
                                            }
                                        }
                                }
                            }
                    }
                    break;
                default:
                    this.jj_la1[9] = this.jj_gen;
                    return (Exp)x;
            }
        }
    }

    public final Exp term2() throws ParseException {
        Object x = this.term();

        while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 75:
                case 84:
                case 86:
                    Exp y;
                    switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 75:
                            this.jj_consume_token(75);
                            y = this.term();
                            x = new UnresolvedFunCall("||", Syntax.Infix, new Exp[]{(Exp)x, y});
                            continue;
                        case 84:
                            this.jj_consume_token(84);
                            y = this.term();
                            x = new UnresolvedFunCall("-", Syntax.Infix, new Exp[]{(Exp)x, y});
                            continue;
                        case 86:
                            this.jj_consume_token(86);
                            y = this.term();
                            x = new UnresolvedFunCall("+", Syntax.Infix, new Exp[]{(Exp)x, y});
                            continue;
                        default:
                            this.jj_la1[15] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                    }
                default:
                    this.jj_la1[14] = this.jj_gen;
                    return (Exp)x;
            }
        }
    }

    public final Exp term() throws ParseException {
        Object x = this.factor();

        while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 71:
                case 89:
                    Exp y;
                    switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 71:
                            this.jj_consume_token(71);
                            y = this.factor();
                            x = new UnresolvedFunCall("*", Syntax.Infix, new Exp[]{(Exp)x, y});
                            continue;
                        case 89:
                            this.jj_consume_token(89);
                            y = this.factor();
                            x = new UnresolvedFunCall("/", Syntax.Infix, new Exp[]{(Exp)x, y});
                            continue;
                        default:
                            this.jj_la1[17] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                    }
                default:
                    this.jj_la1[16] = this.jj_gen;
                    return (Exp)x;
            }
        }
    }

    public final Exp factor() throws ParseException {
        Exp p;
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 6:
            case 7:
            case 14:
            case 30:
            case 35:
            case 80:
            case 82:
            case 90:
            case 91:
            case 92:
            case 93:
            case 95:
            case 96:
            case 98:
            case 99:
                p = this.primary();
                return p;
            case 56:
                this.jj_consume_token(56);
                p = this.primary();
                return new UnresolvedFunCall("Existing", Syntax.Prefix, new Exp[]{p});
            case 84:
                this.jj_consume_token(84);
                p = this.primary();
                return new UnresolvedFunCall("-", Syntax.Prefix, new Exp[]{p});
            case 86:
                this.jj_consume_token(86);
                p = this.primary();
                return p;
            default:
                this.jj_la1[18] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
        }
    }

    public final Exp primary() throws ParseException {
        Exp e = this.atom();

        while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 76:
                    this.jj_consume_token(76);
                    e = this.segmentOrFuncall(e);
                    break;
                default:
                    this.jj_la1[19] = this.jj_gen;
                    return e;
            }
        }
    }

    public final Exp segmentOrFuncall(Exp left) throws ParseException {
        List<Exp> argList = null;
        Segment segment = this.identifier();
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 82:
                this.jj_consume_token(82);
                if (this.jj_2_5(Integer.MAX_VALUE)) {
                    argList = Collections.emptyList();
                } else {
                    argList = this.expOrEmptyList();
                }

                this.jj_consume_token(88);
                break;
            default:
                this.jj_la1[20] = this.jj_gen;
        }

        return this.createCall(left, segment, argList);
    }

    public final Literal numericLiteral() throws ParseException {
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 91:
                this.jj_consume_token(91);
                return Literal.create(new BigDecimal(this.token.image));
            case 92:
                this.jj_consume_token(92);
                return Literal.create(new BigDecimal(this.token.image));
            case 93:
                this.jj_consume_token(93);
                return Literal.create(new BigDecimal(this.token.image));
            default:
                this.jj_la1[21] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
        }
    }

    public final Exp atom() throws ParseException {
        NameSegment segment;
        List lis;
        Exp e;
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 6:
                e = this.caseExpression();
                return e;
            case 7:
                this.jj_consume_token(7);
                this.jj_consume_token(82);
                e = this.unaliasedExpression();
                this.jj_consume_token(2);
                segment = this.nameSegment();
                this.jj_consume_token(88);
                return new UnresolvedFunCall("CAST", Syntax.Cast, new Exp[]{e, Literal.createSymbol(segment.name)});
            case 14:
            case 35:
            case 90:
            case 98:
            case 99:
                segment = this.nameSegment();

                while(true) {
                    switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 72:
                            this.jj_consume_token(72);
                            segment = this.nameSegment();
                            break;
                        default:
                            this.jj_la1[23] = this.jj_gen;
                            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 82:
                                    this.jj_consume_token(82);
                                    if (this.jj_2_7(Integer.MAX_VALUE)) {
                                        lis = Collections.emptyList();
                                    } else {
                                        lis = this.expOrEmptyList();
                                    }

                                    this.jj_consume_token(88);
                                    break;
                                default:
                                    this.jj_la1[24] = this.jj_gen;
                                    lis = null;
                            }

                            return this.createCall((Exp)null, segment, lis);
                    }
                }
            case 30:
                this.jj_consume_token(30);
                return Literal.nullValue;
            case 80:
                this.jj_consume_token(80);
                if (this.jj_2_6(Integer.MAX_VALUE)) {
                    lis = Collections.emptyList();
                } else {
                    switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 6:
                        case 7:
                        case 14:
                        case 29:
                        case 30:
                        case 35:
                        case 56:
                        case 80:
                        case 82:
                        case 84:
                        case 86:
                        case 90:
                        case 91:
                        case 92:
                        case 93:
                        case 95:
                        case 96:
                        case 98:
                        case 99:
                            lis = this.expList();
                            break;
                        default:
                            this.jj_la1[22] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                    }
                }

                this.jj_consume_token(87);
                return new UnresolvedFunCall("{}", Syntax.Braces, toExpArray(lis));
            case 82:
                this.jj_consume_token(82);
                lis = this.expList();
                this.jj_consume_token(88);
                return new UnresolvedFunCall("()", Syntax.Parentheses, toExpArray(lis));
            case 91:
            case 92:
            case 93:
                e = this.numericLiteral();
                return e;
            case 95:
                this.jj_consume_token(95);
                return Literal.createString(stripQuotes(this.token.image, "'", "'", "''"));
            case 96:
                this.jj_consume_token(96);
                return Literal.createString(stripQuotes(this.token.image, "\"", "\"", "\"\""));
            default:
                this.jj_la1[25] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
        }
    }

    public final Exp caseExpression() throws ParseException {
        List<Exp> list = new ArrayList();
        boolean match = false;
        this.jj_consume_token(6);
        Exp e;
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 6:
            case 7:
            case 14:
            case 29:
            case 30:
            case 35:
            case 56:
            case 80:
            case 82:
            case 84:
            case 86:
            case 90:
            case 91:
            case 92:
            case 93:
            case 95:
            case 96:
            case 98:
            case 99:
                e = this.expression();
                match = true;
                list.add(e);
                break;
            default:
                this.jj_la1[26] = this.jj_gen;
        }

        while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 52:
                    this.jj_consume_token(52);
                    e = this.expression();
                    this.jj_consume_token(44);
                    Exp e2 = this.expression();
                    list.add(e);
                    list.add(e2);
                    break;
                default:
                    this.jj_la1[27] = this.jj_gen;
                    switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 16:
                            this.jj_consume_token(16);
                            e = this.expression();
                            list.add(e);
                            break;
                        default:
                            this.jj_la1[28] = this.jj_gen;
                    }

                    this.jj_consume_token(18);
                    if (match) {
                        return new UnresolvedFunCall("_CaseMatch", Syntax.Case, toExpArray(list));
                    }

                    return new UnresolvedFunCall("_CaseTest", Syntax.Case, toExpArray(list));
            }
        }
    }

    public final Exp expression() throws ParseException {
        Object e = this.unaliasedExpression();

        while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 2:
                    this.jj_consume_token(2);
                    Segment i = this.identifier();
                    Id id = new Id(i);
                    e = new UnresolvedFunCall("AS", Syntax.Infix, new Exp[]{(Exp)e, id});
                    break;
                default:
                    this.jj_la1[29] = this.jj_gen;
                    return (Exp)e;
            }
        }
    }

    public final Exp expressionOrEmpty() throws ParseException {
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 6:
            case 7:
            case 14:
            case 29:
            case 30:
            case 35:
            case 56:
            case 80:
            case 82:
            case 84:
            case 86:
            case 90:
            case 91:
            case 92:
            case 93:
            case 95:
            case 96:
            case 98:
            case 99:
                Exp e = this.expression();
                return e;
            default:
                this.jj_la1[30] = this.jj_gen;
                return new UnresolvedFunCall("", Syntax.Empty, new Exp[0]);
        }
    }

    public final List<Exp> expOrEmptyList() throws ParseException {
        List<Exp> list = new LinkedList();
        Exp e = this.expressionOrEmpty();
        list.add(e);

        while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 74:
                    this.jj_consume_token(74);
                    e = this.expressionOrEmpty();
                    list.add(e);
                    break;
                default:
                    this.jj_la1[31] = this.jj_gen;
                    return list;
            }
        }
    }

    public final List<Exp> expList() throws ParseException {
        List<Exp> list = new LinkedList();
        Exp e = this.expression();
        list.add(e);

        while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 74:
                    this.jj_consume_token(74);
                    e = this.expression();
                    list.add(e);
                    break;
                default:
                    this.jj_la1[32] = this.jj_gen;
                    return list;
            }
        }
    }

    public final QueryPart statement() throws ParseException {
        Object qp;
        if (this.jj_2_8(Integer.MAX_VALUE)) {
            qp = this.dmvSelectStatement();
        } else {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4:
                case 12:
                case 38:
                    qp = this.transactionCommandStatement();
                    break;
                case 10:
                    qp = this.createFormula();
                    break;
                case 15:
                    qp = this.drillthroughStatement();
                    break;
                case 19:
                    qp = this.explainStatement();
                    break;
                case 36:
                    qp = this.refreshStatement();
                    break;
                case 41:
                case 55:
                    qp = this.selectStatement();
                    break;
                case 47:
                    qp = this.updateStatement();
                    break;
                default:
                    this.jj_la1[33] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
            }
        }

        return (QueryPart)qp;
    }

    public final QueryPart selectOrDrillthroughStatement() throws ParseException {
        QueryPart qp = null;
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 15:
                qp = this.drillthroughStatement();
                return qp;
            case 41:
            case 55:
                qp = this.selectStatement();
                return qp;
            default:
                this.jj_la1[34] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
        }
    }

    public final QueryPart dmvSelectStatement() throws ParseException {
        ArrayList columns;
        Exp w;
        columns = new ArrayList();
        w = null;
        this.jj_consume_token(41);
        label47:
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 14:
            case 35:
            case 90:
            case 98:
            case 99:
            case 100:
            case 101:
                Id c = this.compoundId();
                columns.add(c);

                while(true) {
                    switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 74:
                            this.jj_consume_token(74);
                            c = this.compoundId();
                            columns.add(c);
                            break;
                        default:
                            this.jj_la1[35] = this.jj_gen;
                            break label47;
                    }
                }
            default:
                this.jj_la1[36] = this.jj_gen;
        }

        this.jj_consume_token(22);
        this.jj_consume_token(57);
        this.jj_consume_token(76);
        NameSegment tableId = this.nameSegment();
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 53:
                this.jj_consume_token(53);
                w = this.expression();
                break;
            default:
                this.jj_la1[37] = this.jj_gen;
        }

        String tableName = tableId.name;
        List<String> returnList = new ArrayList();
        Iterator var7 = columns.iterator();

        while(var7.hasNext()) {
            Id id = (Id)var7.next();
            returnList.add(((NameSegment)id.getElement(0)).name);
        }

        return this.factory.makeDmvQuery(tableName, returnList, w);
    }

    public final Query selectStatement() throws ParseException {
        ArrayList f;
        Exp w;
        ArrayList a;
        ArrayList cellPropList;
        f = new ArrayList();
        w = null;
        a = new ArrayList();
        cellPropList = new ArrayList();
        label112:
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 55:
                this.jj_consume_token(55);

                while(true) {
                    Formula e;
                    switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 27:
                            e = this.memberSpecification();
                            f.add(e);
                            break;
                        case 43:
                            e = this.setSpecification();
                            f.add(e);
                            break;
                        default:
                            this.jj_la1[38] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                    }

                    switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 27:
                        case 43:
                            break;
                        default:
                            this.jj_la1[39] = this.jj_gen;
                            break label112;
                    }
                }
            default:
                this.jj_la1[40] = this.jj_gen;
        }

        this.jj_consume_token(41);
        label99:
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 6:
            case 7:
            case 14:
            case 28:
            case 29:
            case 30:
            case 35:
            case 56:
            case 80:
            case 82:
            case 84:
            case 86:
            case 90:
            case 91:
            case 92:
            case 93:
            case 95:
            case 96:
            case 98:
            case 99:
                QueryAxis i = this.axisSpecification();
                a.add(i);

                while(true) {
                    switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 74:
                            this.jj_consume_token(74);
                            i = this.axisSpecification();
                            a.add(i);
                            break;
                        default:
                            this.jj_la1[41] = this.jj_gen;
                            break label99;
                    }
                }
            default:
                this.jj_la1[42] = this.jj_gen;
        }

        this.jj_consume_token(22);
        Subcube subcube = this.selectSubcubeClause();
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 53:
                this.jj_consume_token(53);
                w = this.expression();
                break;
            default:
                this.jj_la1[43] = this.jj_gen;
        }

        label88:
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 8:
            case 35:
                switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 8:
                        this.jj_consume_token(8);
                        break;
                    default:
                        this.jj_la1[44] = this.jj_gen;
                }

                this.jj_consume_token(35);
                Id p = this.compoundId();
                cellPropList.add(new CellProperty(p.getSegments()));

                while(true) {
                    switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 74:
                            this.jj_consume_token(74);
                            p = this.compoundId();
                            cellPropList.add(new CellProperty(p.getSegments()));
                            break;
                        default:
                            this.jj_la1[45] = this.jj_gen;
                            break label88;
                    }
                }
            default:
                this.jj_la1[46] = this.jj_gen;
        }

        String cubeName = subcube.getCubeName();
        return this.factory.makeQuery(this.statement, toFormulaArray(f), toQueryAxisArray(a), subcube, w, toQueryPartArray(cellPropList), this.strictValidation);
    }

    public final Subcube selectSubcubeClause() throws ParseException {
        Id c = null;
        List<QueryAxis> a = new ArrayList();
        Subcube subcube = null;
        Exp w = null;
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 14:
            case 35:
            case 90:
            case 98:
            case 99:
            case 100:
            case 101:
                c = this.compoundId();
                break;
            case 82:
                this.jj_consume_token(82);
                this.jj_consume_token(41);
                label60:
                switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 6:
                    case 7:
                    case 14:
                    case 28:
                    case 29:
                    case 30:
                    case 35:
                    case 56:
                    case 80:
                    case 82:
                    case 84:
                    case 86:
                    case 90:
                    case 91:
                    case 92:
                    case 93:
                    case 95:
                    case 96:
                    case 98:
                    case 99:
                        QueryAxis i = this.axisSpecification();
                        a.add(i);

                        while(true) {
                            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 74:
                                    this.jj_consume_token(74);
                                    i = this.axisSpecification();
                                    a.add(i);
                                    break;
                                default:
                                    this.jj_la1[47] = this.jj_gen;
                                    break label60;
                            }
                        }
                    default:
                        this.jj_la1[49] = this.jj_gen;
                        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 71:
                                this.jj_consume_token(71);
                                break;
                            default:
                                this.jj_la1[48] = this.jj_gen;
                        }
                }

                this.jj_consume_token(22);
                subcube = this.selectSubcubeClause();
                switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 53:
                        this.jj_consume_token(53);
                        w = this.expression();
                        break;
                    default:
                        this.jj_la1[50] = this.jj_gen;
                }

                this.jj_consume_token(88);
                break;
            default:
                this.jj_la1[51] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
        }

        String cubeName = null;
        if (c != null) {
            cubeName = ((NameSegment)c.getElement(0)).name;
        }

        QueryAxis slicerAxis = w == null ? null : new QueryAxis(false, w, StandardAxisOrdinal.SLICER, SubtotalVisibility.Undefined, new Id[0]);
        return new Subcube(cubeName, subcube, toQueryAxisArray(a), slicerAxis);
    }

    public final QueryPart createFormula() throws ParseException {
        boolean isMember = true;
        List<MemberProperty> l = new ArrayList();
        this.jj_consume_token(10);
        this.jj_consume_token(42);
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 27:
                this.jj_consume_token(27);
                break;
            case 43:
                this.jj_consume_token(43);
                isMember = false;
                break;
            default:
                this.jj_la1[52] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
        }

        NameSegment cubeId = this.nameSegment();
        this.jj_consume_token(76);
        Id m = this.compoundId();
        this.jj_consume_token(2);
        Exp e = this.formulaExpression();

        while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 74:
                    this.jj_consume_token(74);
                    MemberProperty mp = this.memberPropertyDefinition();
                    l.add(mp);
                    break;
                default:
                    this.jj_la1[53] = this.jj_gen;
                    String cubeName = cubeId.name;
                    Formula formula;
                    if (isMember) {
                        formula = new Formula(m, e, toMemberPropertyArray(l));
                    } else {
                        formula = new Formula(m, e);
                    }

                    return this.factory.makeCalculatedFormula(cubeName, formula);
            }
        }
    }

    public final QueryPart refreshStatement() throws ParseException {
        this.jj_consume_token(36);
        this.jj_consume_token(13);
        Id c = this.compoundId();
        String cubeName = ((NameSegment)c.getElement(0)).name;
        return this.factory.makeRefresh(cubeName);
    }

    public final QueryPart updateStatement() throws ParseException {
        List<UpdateClause> list = new ArrayList();
        this.jj_consume_token(47);
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 13:
                this.jj_consume_token(13);
                break;
            default:
                this.jj_la1[54] = this.jj_gen;
        }

        Id c = this.compoundId();
        this.jj_consume_token(43);
        UpdateClause uc = this.updateClause();
        list.add(uc);

        while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 74:
                    this.jj_consume_token(74);
                    uc = this.updateClause();
                    list.add(uc);
                    break;
                default:
                    this.jj_la1[55] = this.jj_gen;
                    String cubeName = ((NameSegment)c.getElement(0)).name;
                    return this.factory.makeUpdate(cubeName, list);
            }
        }
    }

    public final UpdateClause updateClause() throws ParseException {
        Exp w = null;
        Allocation a = Allocation.NO_ALLOCATION;
        Exp t = this.primary();
        this.jj_consume_token(77);
        Exp v = this.unaliasedExpression();
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 48:
            case 49:
            case 50:
            case 51:
                switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 48:
                        this.jj_consume_token(48);
                        a = Allocation.USE_EQUAL_ALLOCATION;
                        return new UpdateClause(t, v, a, w);
                    case 49:
                        this.jj_consume_token(49);
                        a = Allocation.USE_EQUAL_INCREMENT;
                        return new UpdateClause(t, v, a, w);
                    case 50:
                        this.jj_consume_token(50);
                        a = Allocation.USE_WEIGHTED_ALLOCATION;
                        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 5:
                                this.jj_consume_token(5);
                                w = this.unaliasedExpression();
                                return new UpdateClause(t, v, a, w);
                            default:
                                this.jj_la1[56] = this.jj_gen;
                                return new UpdateClause(t, v, a, w);
                        }
                    case 51:
                        this.jj_consume_token(51);
                        a = Allocation.USE_WEIGHTED_INCREMENT;
                        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 5:
                                this.jj_consume_token(5);
                                w = this.unaliasedExpression();
                                return new UpdateClause(t, v, a, w);
                            default:
                                this.jj_la1[57] = this.jj_gen;
                                return new UpdateClause(t, v, a, w);
                        }
                    default:
                        this.jj_la1[58] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                }
            default:
                this.jj_la1[59] = this.jj_gen;
                return new UpdateClause(t, v, a, w);
        }
    }

    public final QueryPart transactionCommandStatement() throws ParseException {
        Command c;
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 4:
                this.jj_consume_token(4);
                c = Command.BEGIN;
                break;
            case 12:
                this.jj_consume_token(12);
                c = Command.COMMIT;
                break;
            case 38:
                this.jj_consume_token(38);
                c = Command.ROLLBACK;
                break;
            default:
                this.jj_la1[60] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
        }

        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 45:
                this.jj_consume_token(45);
                break;
            case 46:
                this.jj_consume_token(46);
                break;
            default:
                this.jj_la1[61] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
        }

        return this.factory.makeTransactionCommand(c);
    }

    public final Formula memberSpecification() throws ParseException {
        List<MemberProperty> l = new ArrayList();
        this.jj_consume_token(27);
        Id m = this.compoundId();
        this.jj_consume_token(2);
        Exp e = this.formulaExpression();

        while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 74:
                    this.jj_consume_token(74);
                    MemberProperty mp = this.memberPropertyDefinition();
                    l.add(mp);
                    break;
                default:
                    this.jj_la1[62] = this.jj_gen;
                    return new Formula(m, e, toMemberPropertyArray(l));
            }
        }
    }

    public final Exp formulaExpression() throws ParseException {
        if (this.jj_2_9(Integer.MAX_VALUE)) {
            this.jj_consume_token(95);
            return this.recursivelyParseExp(stripQuotes(this.token.image, "'", "'", "''"));
        } else {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 6:
                case 7:
                case 14:
                case 29:
                case 30:
                case 35:
                case 56:
                case 80:
                case 82:
                case 84:
                case 86:
                case 90:
                case 91:
                case 92:
                case 93:
                case 95:
                case 96:
                case 98:
                case 99:
                    Exp e = this.unaliasedExpression();
                    return e;
                default:
                    this.jj_la1[63] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
            }
        }
    }

    public final MemberProperty memberPropertyDefinition() throws ParseException {
        NameSegment id = this.nameSegment();
        this.jj_consume_token(77);
        Exp e = this.expression();
        return new MemberProperty(id.name, e);
    }

    public final Formula setSpecification() throws ParseException {
        this.jj_consume_token(43);
        Id n = this.compoundId();
        this.jj_consume_token(2);
        Exp e = this.formulaExpression();
        return new Formula(n, e);
    }

    public final QueryAxis axisSpecification() throws ParseException {
        boolean nonEmpty = false;
        List<Id> dp = new ArrayList();
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 28:
                this.jj_consume_token(28);
                this.jj_consume_token(17);
                nonEmpty = true;
                break;
            default:
                this.jj_la1[64] = this.jj_gen;
        }

        Exp e;
        e = this.expression();
        label84:
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 14:
            case 35:
                switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 14:
                        this.jj_consume_token(14);
                        break;
                    default:
                        this.jj_la1[65] = this.jj_gen;
                }

                this.jj_consume_token(35);
                Id p = this.compoundId();
                dp.add(p);

                while(true) {
                    switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 74:
                            this.jj_consume_token(74);
                            p = this.compoundId();
                            dp.add(p);
                            break;
                        default:
                            this.jj_la1[66] = this.jj_gen;
                            break label84;
                    }
                }
            default:
                this.jj_la1[67] = this.jj_gen;
        }

        this.jj_consume_token(31);
        Object axis;
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 3:
            case 91:
            case 92:
            case 93:
                Literal n;
                switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 3:
                        this.jj_consume_token(3);
                        this.jj_consume_token(82);
                        n = this.numericLiteral();
                        this.jj_consume_token(88);
                        break;
                    case 91:
                    case 92:
                    case 93:
                        n = this.numericLiteral();
                        break;
                    default:
                        this.jj_la1[68] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                }

                Number number = (Number)n.getValue();
                if (number.doubleValue() < 0.0D || number.doubleValue() != (double)number.intValue()) {
                    throw MondrianResource.instance().InvalidAxis.ex(number.doubleValue());
                }

                axis = StandardAxisOrdinal.forLogicalOrdinal(number.intValue());
                break;
            case 9:
                this.jj_consume_token(9);
                axis = StandardAxisOrdinal.CHAPTERS;
                break;
            case 11:
                this.jj_consume_token(11);
                axis = StandardAxisOrdinal.COLUMNS;
                break;
            case 33:
                this.jj_consume_token(33);
                axis = StandardAxisOrdinal.PAGES;
                break;
            case 39:
                this.jj_consume_token(39);
                axis = StandardAxisOrdinal.ROWS;
                break;
            case 40:
                this.jj_consume_token(40);
                axis = StandardAxisOrdinal.SECTIONS;
                break;
            default:
                this.jj_la1[69] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
        }

        return new QueryAxis(nonEmpty, e, (AxisOrdinal)axis, SubtotalVisibility.Undefined, toIdArray(dp));
    }

    public final QueryPart drillthroughStatement() throws ParseException {
        int m = 0;
        int f = 0;
        List<Exp> rl = null;
        this.jj_consume_token(15);
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 26:
                this.jj_consume_token(26);
                this.jj_consume_token(91);
                m = Integer.valueOf(this.token.image);
                break;
            default:
                this.jj_la1[70] = this.jj_gen;
        }

        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 20:
                this.jj_consume_token(20);
                this.jj_consume_token(91);
                f = Integer.valueOf(this.token.image);
                break;
            default:
                this.jj_la1[71] = this.jj_gen;
        }

        Query s = this.selectStatement();
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 37:
                this.jj_consume_token(37);
                rl = this.returnItemList();
                break;
            default:
                this.jj_la1[72] = this.jj_gen;
        }

        return this.factory.makeDrillThrough(s, m, f, rl);
    }

    public final List<Exp> returnItemList() throws ParseException {
        List<Exp> list = new ArrayList();
        Id i = this.returnItem();
        list.add(i);

        while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 74:
                    this.jj_consume_token(74);
                    i = this.returnItem();
                    list.add(i);
                    break;
                default:
                    this.jj_la1[73] = this.jj_gen;
                    return list;
            }
        }
    }

    public final Id returnItem() throws ParseException {
        Id i = this.compoundId();
        return i;
    }

    public final QueryPart explainStatement() throws ParseException {
        this.jj_consume_token(19);
        this.jj_consume_token(34);
        this.jj_consume_token(21);
        Object qp;
        switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 15:
                qp = this.drillthroughStatement();
                break;
            case 41:
            case 55:
                qp = this.selectStatement();
                break;
            default:
                this.jj_la1[74] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
        }

        return this.factory.makeExplain((QueryPart)qp);
    }

    private boolean jj_2_1(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;

        boolean var3;
        try {
            boolean var2 = !this.jj_3_1();
            return var2;
        } catch (MdxParserImpl.LookaheadSuccess var7) {
            var3 = true;
        } finally {
            this.jj_save(0, xla);
        }

        return var3;
    }

    private boolean jj_2_2(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;

        boolean var3;
        try {
            boolean var2 = !this.jj_3_2();
            return var2;
        } catch (MdxParserImpl.LookaheadSuccess var7) {
            var3 = true;
        } finally {
            this.jj_save(1, xla);
        }

        return var3;
    }

    private boolean jj_2_3(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;

        boolean var3;
        try {
            boolean var2 = !this.jj_3_3();
            return var2;
        } catch (MdxParserImpl.LookaheadSuccess var7) {
            var3 = true;
        } finally {
            this.jj_save(2, xla);
        }

        return var3;
    }

    private boolean jj_2_4(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;

        boolean var3;
        try {
            boolean var2 = !this.jj_3_4();
            return var2;
        } catch (MdxParserImpl.LookaheadSuccess var7) {
            var3 = true;
        } finally {
            this.jj_save(3, xla);
        }

        return var3;
    }

    private boolean jj_2_5(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;

        boolean var3;
        try {
            boolean var2 = !this.jj_3_5();
            return var2;
        } catch (MdxParserImpl.LookaheadSuccess var7) {
            var3 = true;
        } finally {
            this.jj_save(4, xla);
        }

        return var3;
    }

    private boolean jj_2_6(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;

        boolean var3;
        try {
            boolean var2 = !this.jj_3_6();
            return var2;
        } catch (MdxParserImpl.LookaheadSuccess var7) {
            var3 = true;
        } finally {
            this.jj_save(5, xla);
        }

        return var3;
    }

    private boolean jj_2_7(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;

        boolean var3;
        try {
            boolean var2 = !this.jj_3_7();
            return var2;
        } catch (MdxParserImpl.LookaheadSuccess var7) {
            var3 = true;
        } finally {
            this.jj_save(6, xla);
        }

        return var3;
    }

    private boolean jj_2_8(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;

        boolean var3;
        try {
            boolean var2 = !this.jj_3_8();
            return var2;
        } catch (MdxParserImpl.LookaheadSuccess var7) {
            var3 = true;
        } finally {
            this.jj_save(7, xla);
        }

        return var3;
    }

    private boolean jj_2_9(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;

        boolean var3;
        try {
            boolean var2 = !this.jj_3_9();
            return var2;
        } catch (MdxParserImpl.LookaheadSuccess var7) {
            var3 = true;
        } finally {
            this.jj_save(8, xla);
        }

        return var3;
    }

    private boolean jj_3R_118() {
        if (this.jj_3R_119()) {
            return true;
        } else {
            Token xsp;
            do {
                xsp = this.jj_scanpos;
            } while(!this.jj_3R_120());

            this.jj_scanpos = xsp;
            return false;
        }
    }

    private boolean jj_3R_102() {
        if (this.jj_3R_42()) {
            return true;
        } else {
            Token xsp = this.jj_scanpos;
            if (this.jj_3R_109()) {
                this.jj_scanpos = xsp;
            }

            return false;
        }
    }

    private boolean jj_3R_122() {
        return false;
    }

    private boolean jj_3_1() {
        return this.jj_scan_token(76);
    }

    private boolean jj_3R_101() {
        if (this.jj_scan_token(76)) {
            return true;
        } else {
            return this.jj_3R_102();
        }
    }

    private boolean jj_3R_43() {
        if (this.jj_scan_token(76)) {
            return true;
        } else {
            return this.jj_3R_42();
        }
    }

    private boolean jj_3R_47() {
        if (this.jj_3R_54()) {
            return true;
        } else {
            Token xsp;
            do {
                xsp = this.jj_scanpos;
            } while(!this.jj_3R_101());

            this.jj_scanpos = xsp;
            return false;
        }
    }

    private boolean jj_3R_121() {
        return this.jj_3R_37();
    }

    private boolean jj_3R_119() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_121()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_122()) {
                return true;
            }
        }

        return false;
    }

    private boolean jj_3R_31() {
        if (this.jj_3R_42()) {
            return true;
        } else {
            Token xsp;
            do {
                xsp = this.jj_scanpos;
            } while(!this.jj_3R_43());

            this.jj_scanpos = xsp;
            return false;
        }
    }

    private boolean jj_3R_41() {
        if (this.jj_scan_token(56)) {
            return true;
        } else {
            return this.jj_3R_47();
        }
    }

    private boolean jj_3R_46() {
        if (this.jj_scan_token(2)) {
            return true;
        } else {
            return this.jj_3R_42();
        }
    }

    private boolean jj_3R_40() {
        if (this.jj_scan_token(84)) {
            return true;
        } else {
            return this.jj_3R_47();
        }
    }

    private boolean jj_3R_51() {
        return this.jj_scan_token(35);
    }

    private boolean jj_3R_39() {
        if (this.jj_scan_token(86)) {
            return true;
        } else {
            return this.jj_3R_47();
        }
    }

    private boolean jj_3R_37() {
        if (this.jj_3R_45()) {
            return true;
        } else {
            Token xsp;
            do {
                xsp = this.jj_scanpos;
            } while(!this.jj_3R_46());

            this.jj_scanpos = xsp;
            return false;
        }
    }

    private boolean jj_3R_44() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_50()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_51()) {
                return true;
            }
        }

        return false;
    }

    private boolean jj_3R_50() {
        return this.jj_scan_token(14);
    }

    private boolean jj_3R_38() {
        return this.jj_3R_47();
    }

    private boolean jj_3R_30() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_38()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_39()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_40()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_41()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean jj_3R_100() {
        if (this.jj_scan_token(89)) {
            return true;
        } else {
            return this.jj_3R_30();
        }
    }

    private boolean jj_3R_99() {
        if (this.jj_scan_token(71)) {
            return true;
        } else {
            return this.jj_3R_30();
        }
    }

    private boolean jj_3R_89() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_99()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_100()) {
                return true;
            }
        }

        return false;
    }

    private boolean jj_3R_81() {
        return this.jj_scan_token(101);
    }

    private boolean jj_3R_75() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_80()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_81()) {
                return true;
            }
        }

        return false;
    }

    private boolean jj_3R_80() {
        return this.jj_scan_token(100);
    }

    private boolean jj_3R_26() {
        if (this.jj_3R_30()) {
            return true;
        } else {
            Token xsp;
            do {
                xsp = this.jj_scanpos;
            } while(!this.jj_3R_89());

            this.jj_scanpos = xsp;
            return false;
        }
    }

    private boolean jj_3R_113() {
        if (this.jj_scan_token(16)) {
            return true;
        } else {
            return this.jj_3R_37();
        }
    }

    private boolean jj_3R_112() {
        if (this.jj_scan_token(52)) {
            return true;
        } else if (this.jj_3R_37()) {
            return true;
        } else if (this.jj_scan_token(44)) {
            return true;
        } else {
            return this.jj_3R_37();
        }
    }

    private boolean jj_3R_70() {
        return this.jj_3R_75();
    }

    private boolean jj_3R_92() {
        if (this.jj_scan_token(75)) {
            return true;
        } else {
            return this.jj_3R_26();
        }
    }

    private boolean jj_3R_111() {
        return this.jj_3R_37();
    }

    private boolean jj_3R_91() {
        if (this.jj_scan_token(84)) {
            return true;
        } else {
            return this.jj_3R_26();
        }
    }

    private boolean jj_3R_29() {
        if (this.jj_scan_token(53)) {
            return true;
        } else {
            return this.jj_3R_37();
        }
    }

    private boolean jj_3R_55() {
        if (this.jj_3R_70()) {
            return true;
        } else {
            Token xsp;
            do {
                xsp = this.jj_scanpos;
            } while(!this.jj_3R_70());

            this.jj_scanpos = xsp;
            return false;
        }
    }

    private boolean jj_3R_90() {
        if (this.jj_scan_token(86)) {
            return true;
        } else {
            return this.jj_3R_26();
        }
    }

    private boolean jj_3R_83() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_90()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_91()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_92()) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean jj_3R_32() {
        if (this.jj_scan_token(74)) {
            return true;
        } else {
            return this.jj_3R_31();
        }
    }

    private boolean jj_3R_108() {
        return false;
    }

    private boolean jj_3R_74() {
        if (this.jj_scan_token(6)) {
            return true;
        } else {
            Token xsp = this.jj_scanpos;
            if (this.jj_3R_111()) {
                this.jj_scanpos = xsp;
            }

            do {
                xsp = this.jj_scanpos;
            } while(!this.jj_3R_112());

            this.jj_scanpos = xsp;
            xsp = this.jj_scanpos;
            if (this.jj_3R_113()) {
                this.jj_scanpos = xsp;
            }

            return this.jj_scan_token(18);
        }
    }

    private boolean jj_3R_24() {
        if (this.jj_3R_26()) {
            return true;
        } else {
            Token xsp;
            do {
                xsp = this.jj_scanpos;
            } while(!this.jj_3R_83());

            this.jj_scanpos = xsp;
            return false;
        }
    }

    private boolean jj_3R_27() {
        if (this.jj_3R_31()) {
            return true;
        } else {
            Token xsp;
            do {
                xsp = this.jj_scanpos;
            } while(!this.jj_3R_32());

            this.jj_scanpos = xsp;
            return false;
        }
    }

    private boolean jj_3_7() {
        return this.jj_scan_token(88);
    }

    private boolean jj_3R_36() {
        return this.jj_scan_token(99);
    }

    private boolean jj_3R_25() {
        if (this.jj_scan_token(41)) {
            return true;
        } else {
            Token xsp = this.jj_scanpos;
            if (this.jj_3R_27()) {
                this.jj_scanpos = xsp;
            }

            if (this.jj_scan_token(22)) {
                return true;
            } else if (this.jj_scan_token(57)) {
                return true;
            } else if (this.jj_scan_token(76)) {
                return true;
            } else if (this.jj_3R_28()) {
                return true;
            } else {
                xsp = this.jj_scanpos;
                if (this.jj_3R_29()) {
                    this.jj_scanpos = xsp;
                }

                return false;
            }
        }
    }

    private boolean jj_3R_115() {
        return this.jj_3R_118();
    }

    private boolean jj_3R_35() {
        if (this.jj_scan_token(90)) {
            return true;
        } else {
            return this.jj_scan_token(98);
        }
    }

    private boolean jj_3R_114() {
        return false;
    }

    private boolean jj_3R_34() {
        return this.jj_scan_token(98);
    }

    private boolean jj_3R_88() {
        if (this.jj_scan_token(29)) {
            return true;
        } else if (this.jj_scan_token(23)) {
            return true;
        } else {
            return this.jj_3R_24();
        }
    }

    private boolean jj_3R_87() {
        if (this.jj_scan_token(23)) {
            return true;
        } else {
            return this.jj_3R_24();
        }
    }

    private boolean jj_3R_107() {
        if (this.jj_scan_token(82)) {
            return true;
        } else {
            Token xsp = this.jj_scanpos;
            if (this.jj_3R_114()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_115()) {
                    return true;
                }
            }

            return this.jj_scan_token(88);
        }
    }

    private boolean jj_3R_33() {
        return this.jj_3R_44();
    }

    private boolean jj_3R_28() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_33()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_34()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_35()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_36()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean jj_3_4() {
        if (this.jj_scan_token(29)) {
            return true;
        } else if (this.jj_scan_token(25)) {
            return true;
        } else {
            return this.jj_3R_24();
        }
    }

    private boolean jj_3R_106() {
        if (this.jj_scan_token(72)) {
            return true;
        } else {
            return this.jj_3R_28();
        }
    }

    private boolean jj_3R_49() {
        return this.jj_3R_55();
    }

    private boolean jj_3R_86() {
        if (this.jj_scan_token(25)) {
            return true;
        } else {
            return this.jj_3R_24();
        }
    }

    private boolean jj_3R_48() {
        return this.jj_3R_28();
    }

    private boolean jj_3R_85() {
        if (this.jj_scan_token(24)) {
            return true;
        } else {
            return this.jj_scan_token(17);
        }
    }

    private boolean jj_3R_69() {
        if (this.jj_3R_28()) {
            return true;
        } else {
            Token xsp;
            do {
                xsp = this.jj_scanpos;
            } while(!this.jj_3R_106());

            this.jj_scanpos = xsp;
            xsp = this.jj_scanpos;
            if (this.jj_3R_107()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_108()) {
                    return true;
                }
            }

            return false;
        }
    }

    private boolean jj_3R_42() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_48()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_49()) {
                return true;
            }
        }

        return false;
    }

    private boolean jj_3_6() {
        return this.jj_scan_token(87);
    }

    private boolean jj_3_3() {
        if (this.jj_scan_token(24)) {
            return true;
        } else {
            return this.jj_3R_24();
        }
    }

    private boolean jj_3_8() {
        return this.jj_3R_25();
    }

    private boolean jj_3R_68() {
        return this.jj_3R_74();
    }

    private boolean jj_3R_105() {
        return this.jj_3R_103();
    }

    private boolean jj_3_2() {
        if (this.jj_scan_token(24)) {
            return true;
        } else {
            return this.jj_scan_token(30);
        }
    }

    private boolean jj_3R_104() {
        return false;
    }

    private boolean jj_3R_98() {
        return this.jj_scan_token(78);
    }

    private boolean jj_3R_97() {
        return this.jj_scan_token(81);
    }

    private boolean jj_3R_96() {
        return this.jj_scan_token(79);
    }

    private boolean jj_3R_95() {
        return this.jj_scan_token(83);
    }

    private boolean jj_3R_94() {
        return this.jj_scan_token(85);
    }

    private boolean jj_3R_93() {
        return this.jj_scan_token(77);
    }

    private boolean jj_3R_67() {
        if (this.jj_scan_token(80)) {
            return true;
        } else {
            Token xsp = this.jj_scanpos;
            if (this.jj_3R_104()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_105()) {
                    return true;
                }
            }

            return this.jj_scan_token(87);
        }
    }

    private boolean jj_3R_82() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_84()) {
            this.jj_scanpos = xsp;
            if (this.jj_3_2()) {
                this.jj_scanpos = xsp;
                if (this.jj_3_3()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_85()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3R_86()) {
                            this.jj_scanpos = xsp;
                            if (this.jj_3_4()) {
                                this.jj_scanpos = xsp;
                                if (this.jj_3R_87()) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_3R_88()) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean jj_3R_84() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_93()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_94()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_95()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_96()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3R_97()) {
                            this.jj_scanpos = xsp;
                            if (this.jj_3R_98()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return this.jj_3R_24();
    }

    private boolean jj_3R_66() {
        if (this.jj_scan_token(82)) {
            return true;
        } else if (this.jj_3R_103()) {
            return true;
        } else {
            return this.jj_scan_token(88);
        }
    }

    private boolean jj_3R_76() {
        if (this.jj_3R_24()) {
            return true;
        } else {
            Token xsp;
            do {
                xsp = this.jj_scanpos;
            } while(!this.jj_3R_82());

            this.jj_scanpos = xsp;
            return false;
        }
    }

    private boolean jj_3R_65() {
        if (this.jj_scan_token(7)) {
            return true;
        } else if (this.jj_scan_token(82)) {
            return true;
        } else if (this.jj_3R_45()) {
            return true;
        } else if (this.jj_scan_token(2)) {
            return true;
        } else if (this.jj_3R_28()) {
            return true;
        } else {
            return this.jj_scan_token(88);
        }
    }

    private boolean jj_3R_64() {
        return this.jj_scan_token(30);
    }

    private boolean jj_3R_63() {
        return this.jj_3R_73();
    }

    private boolean jj_3R_72() {
        if (this.jj_scan_token(29)) {
            return true;
        } else {
            return this.jj_3R_56();
        }
    }

    private boolean jj_3R_62() {
        return this.jj_scan_token(96);
    }

    private boolean jj_3R_56() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_71()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_72()) {
                return true;
            }
        }

        return false;
    }

    private boolean jj_3R_71() {
        return this.jj_3R_76();
    }

    private boolean jj_3R_54() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_61()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_62()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_63()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_64()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3R_65()) {
                            this.jj_scanpos = xsp;
                            if (this.jj_3R_66()) {
                                this.jj_scanpos = xsp;
                                if (this.jj_3R_67()) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_3R_68()) {
                                        this.jj_scanpos = xsp;
                                        if (this.jj_3R_69()) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean jj_3R_61() {
        return this.jj_scan_token(95);
    }

    private boolean jj_3_9() {
        return this.jj_scan_token(95);
    }

    private boolean jj_3R_57() {
        if (this.jj_scan_token(1)) {
            return true;
        } else {
            return this.jj_3R_56();
        }
    }

    private boolean jj_3R_110() {
        if (this.jj_scan_token(74)) {
            return true;
        } else {
            return this.jj_3R_37();
        }
    }

    private boolean jj_3R_79() {
        return this.jj_scan_token(92);
    }

    private boolean jj_3R_52() {
        if (this.jj_3R_56()) {
            return true;
        } else {
            Token xsp;
            do {
                xsp = this.jj_scanpos;
            } while(!this.jj_3R_57());

            this.jj_scanpos = xsp;
            return false;
        }
    }

    private boolean jj_3R_78() {
        return this.jj_scan_token(91);
    }

    private boolean jj_3_5() {
        return this.jj_scan_token(88);
    }

    private boolean jj_3R_103() {
        if (this.jj_3R_37()) {
            return true;
        } else {
            Token xsp;
            do {
                xsp = this.jj_scanpos;
            } while(!this.jj_3R_110());

            this.jj_scanpos = xsp;
            return false;
        }
    }

    private boolean jj_3R_73() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_77()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_78()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_79()) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean jj_3R_77() {
        return this.jj_scan_token(93);
    }

    private boolean jj_3R_117() {
        return this.jj_3R_118();
    }

    private boolean jj_3R_60() {
        if (this.jj_scan_token(73)) {
            return true;
        } else {
            return this.jj_3R_52();
        }
    }

    private boolean jj_3R_116() {
        return false;
    }

    private boolean jj_3R_59() {
        if (this.jj_scan_token(54)) {
            return true;
        } else {
            return this.jj_3R_52();
        }
    }

    private boolean jj_3R_120() {
        if (this.jj_scan_token(74)) {
            return true;
        } else {
            return this.jj_3R_119();
        }
    }

    private boolean jj_3R_53() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_58()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_59()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_60()) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean jj_3R_58() {
        if (this.jj_scan_token(32)) {
            return true;
        } else {
            return this.jj_3R_52();
        }
    }

    private boolean jj_3R_109() {
        if (this.jj_scan_token(82)) {
            return true;
        } else {
            Token xsp = this.jj_scanpos;
            if (this.jj_3R_116()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_117()) {
                    return true;
                }
            }

            return this.jj_scan_token(88);
        }
    }

    private boolean jj_3R_45() {
        if (this.jj_3R_52()) {
            return true;
        } else {
            Token xsp;
            do {
                xsp = this.jj_scanpos;
            } while(!this.jj_3R_53());

            this.jj_scanpos = xsp;
            return false;
        }
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{16384, 16384, 0, 0, 16384, 0, 0, 2, 1610629312, 595591168, 0, 0, 50331648, 545259520, 0, 0, 0, 0, 1073758400, 0, 0, 0, 1610629312, 0, 0, 1073758400, 1610629312, 0, 65536, 4, 1610629312, 0, 0, 562192, 32768, 0, 16384, 0, 134217728, 134217728, 0, 0, 1879064768, 0, 256, 0, 256, 0, 0, 1879064768, 0, 16384, 134217728, 0, 8192, 0, 32, 32, 0, 0, 4112, 0, 0, 1610629312, 268435456, 16384, 0, 16384, 8, 2568, 67108864, 1048576, 0, 0, 32768};
    }

    private static void jj_la1_init_1() {
        jj_la1_1 = new int[]{8, 8, 0, 0, 8, 4194305, 4194305, 0, 16777224, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16777224, 0, 0, 0, 16777224, 0, 0, 8, 16777224, 1048576, 0, 0, 16777224, 0, 0, 8421968, 8389120, 0, 8, 2097152, 2048, 2048, 8388608, 0, 16777224, 2097152, 0, 0, 8, 0, 0, 16777224, 2097152, 8, 2048, 0, 0, 0, 0, 0, 983040, 983040, 64, 24576, 0, 16777224, 0, 0, 0, 8, 0, 386, 0, 0, 32, 0, 8389120};
    }

    private static void jj_la1_init_2() {
        jj_la1_2 = new int[]{67108864, 67108864, 0, 0, 0, 512, 512, 0, -1135280128, 2809856, 2809856, 2809856, 0, 0, 5244928, 5244928, 33554560, 33554560, -1135280128, 4096, 262144, 939524096, -1135280128, 256, 262144, -1140523008, -1135280128, 0, 0, 0, -1135280128, 1024, 1024, 0, 0, 1024, 67108864, 0, 0, 0, 0, 1024, -1135280128, 0, 0, 1024, 0, 1024, 128, -1135280128, 0, 67371008, 0, 1024, 0, 1024, 0, 0, 0, 0, 0, 0, 1024, -1135280128, 0, 0, 1024, 0, 939524096, 939524096, 0, 0, 0, 1024, 0};
    }

    private static void jj_la1_init_3() {
        jj_la1_3 = new int[]{60, 12, 48, 48, 0, 0, 0, 0, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 0, 0, 0, 13, 0, 0, 13, 13, 0, 0, 0, 13, 0, 0, 0, 0, 0, 60, 0, 0, 0, 0, 0, 13, 0, 0, 0, 0, 0, 0, 13, 0, 60, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    public MdxParserImpl(InputStream stream) {
        this(stream, (String)null);
    }

    public MdxParserImpl(InputStream stream, String encoding) {
        this.jj_la1 = new int[75];
        this.jj_2_rtns = new MdxParserImpl.JJCalls[9];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new MdxParserImpl.LookaheadSuccess();
        this.jj_expentries = new ArrayList();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];

        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        } catch (UnsupportedEncodingException var4) {
            throw new RuntimeException(var4);
        }

        this.token_source = new MdxParserImplTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;

        int i;
        for(i = 0; i < 75; ++i) {
            this.jj_la1[i] = -1;
        }

        for(i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new MdxParserImpl.JJCalls();
        }

    }

    public void ReInit(InputStream stream) {
        this.ReInit(stream, (String)null);
    }

    public void ReInit(InputStream stream, String encoding) {
        try {
            this.jj_input_stream.ReInit(stream, encoding, 1, 1);
        } catch (UnsupportedEncodingException var4) {
            throw new RuntimeException(var4);
        }

        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;

        int i;
        for(i = 0; i < 75; ++i) {
            this.jj_la1[i] = -1;
        }

        for(i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new MdxParserImpl.JJCalls();
        }

    }

    public MdxParserImpl(Reader stream) {
        this.jj_la1 = new int[75];
        this.jj_2_rtns = new MdxParserImpl.JJCalls[9];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new MdxParserImpl.LookaheadSuccess();
        this.jj_expentries = new ArrayList();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new MdxParserImplTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;

        int i;
        for(i = 0; i < 75; ++i) {
            this.jj_la1[i] = -1;
        }

        for(i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new MdxParserImpl.JJCalls();
        }

    }

    public void ReInit(Reader stream) {
        this.jj_input_stream.ReInit(stream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;

        int i;
        for(i = 0; i < 75; ++i) {
            this.jj_la1[i] = -1;
        }

        for(i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new MdxParserImpl.JJCalls();
        }

    }

    public MdxParserImpl(MdxParserImplTokenManager tm) {
        this.jj_la1 = new int[75];
        this.jj_2_rtns = new MdxParserImpl.JJCalls[9];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new MdxParserImpl.LookaheadSuccess();
        this.jj_expentries = new ArrayList();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;

        int i;
        for(i = 0; i < 75; ++i) {
            this.jj_la1[i] = -1;
        }

        for(i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new MdxParserImpl.JJCalls();
        }

    }

    public void ReInit(MdxParserImplTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;

        int i;
        for(i = 0; i < 75; ++i) {
            this.jj_la1[i] = -1;
        }

        for(i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new MdxParserImpl.JJCalls();
        }

    }

    private Token jj_consume_token(int kind) throws ParseException {
        Token oldToken;
        if ((oldToken = this.token).next != null) {
            this.token = this.token.next;
        } else {
            this.token = this.token.next = this.token_source.getNextToken();
        }

        this.jj_ntk = -1;
        if (this.token.kind != kind) {
            this.token = oldToken;
            this.jj_kind = kind;
            throw this.generateParseException();
        } else {
            ++this.jj_gen;
            if (++this.jj_gc > 100) {
                this.jj_gc = 0;

                for(int i = 0; i < this.jj_2_rtns.length; ++i) {
                    for(MdxParserImpl.JJCalls c = this.jj_2_rtns[i]; c != null; c = c.next) {
                        if (c.gen < this.jj_gen) {
                            c.first = null;
                        }
                    }
                }
            }

            return this.token;
        }
    }

    private boolean jj_scan_token(int kind) {
        if (this.jj_scanpos == this.jj_lastpos) {
            --this.jj_la;
            if (this.jj_scanpos.next == null) {
                this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken();
            } else {
                this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next;
            }
        } else {
            this.jj_scanpos = this.jj_scanpos.next;
        }

        if (this.jj_rescan) {
            int i = 0;

            Token tok;
            for(tok = this.token; tok != null && tok != this.jj_scanpos; tok = tok.next) {
                ++i;
            }

            if (tok != null) {
                this.jj_add_error_token(kind, i);
            }
        }

        if (this.jj_scanpos.kind != kind) {
            return true;
        } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            throw this.jj_ls;
        } else {
            return false;
        }
    }

    public final Token getNextToken() {
        if (this.token.next != null) {
            this.token = this.token.next;
        } else {
            this.token = this.token.next = this.token_source.getNextToken();
        }

        this.jj_ntk = -1;
        ++this.jj_gen;
        return this.token;
    }

    public final Token getToken(int index) {
        Token t = this.token;

        for(int i = 0; i < index; ++i) {
            if (t.next != null) {
                t = t.next;
            } else {
                t = t.next = this.token_source.getNextToken();
            }
        }

        return t;
    }

    private int jj_ntk() {
        return (this.jj_nt = this.token.next) == null ? (this.jj_ntk = (this.token.next = this.token_source.getNextToken()).kind) : (this.jj_ntk = this.jj_nt.kind);
    }

    private void jj_add_error_token(int kind, int pos) {
        if (pos < 100) {
            if (pos == this.jj_endpos + 1) {
                this.jj_lasttokens[this.jj_endpos++] = kind;
            } else if (this.jj_endpos != 0) {
                this.jj_expentry = new int[this.jj_endpos];

                for(int i = 0; i < this.jj_endpos; ++i) {
                    this.jj_expentry[i] = this.jj_lasttokens[i];
                }

                Iterator it = this.jj_expentries.iterator();

                label41:
                while(true) {
                    int[] oldentry;
                    do {
                        if (!it.hasNext()) {
                            break label41;
                        }

                        oldentry = (int[])it.next();
                    } while(oldentry.length != this.jj_expentry.length);

                    for(int i = 0; i < this.jj_expentry.length; ++i) {
                        if (oldentry[i] != this.jj_expentry[i]) {
                            continue label41;
                        }
                    }

                    this.jj_expentries.add(this.jj_expentry);
                    break;
                }

                if (pos != 0) {
                    this.jj_lasttokens[(this.jj_endpos = pos) - 1] = kind;
                }
            }

        }
    }

    public ParseException generateParseException() {
        this.jj_expentries.clear();
        boolean[] la1tokens = new boolean[104];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }

        int i;
        int j;
        for(i = 0; i < 75; ++i) {
            if (this.jj_la1[i] == this.jj_gen) {
                for(j = 0; j < 32; ++j) {
                    if ((jj_la1_0[i] & 1 << j) != 0) {
                        la1tokens[j] = true;
                    }

                    if ((jj_la1_1[i] & 1 << j) != 0) {
                        la1tokens[32 + j] = true;
                    }

                    if ((jj_la1_2[i] & 1 << j) != 0) {
                        la1tokens[64 + j] = true;
                    }

                    if ((jj_la1_3[i] & 1 << j) != 0) {
                        la1tokens[96 + j] = true;
                    }
                }
            }
        }

        for(i = 0; i < 104; ++i) {
            if (la1tokens[i]) {
                this.jj_expentry = new int[1];
                this.jj_expentry[0] = i;
                this.jj_expentries.add(this.jj_expentry);
            }
        }

        this.jj_endpos = 0;
        this.jj_rescan_token();
        this.jj_add_error_token(0, 0);
        int[][] exptokseq = new int[this.jj_expentries.size()][];

        for(j = 0; j < this.jj_expentries.size(); ++j) {
            exptokseq[j] = (int[])this.jj_expentries.get(j);
        }

        return new ParseException(this.token, exptokseq, tokenImage);
    }

    public final void enable_tracing() {
    }

    public final void disable_tracing() {
    }

    private void jj_rescan_token() {
        this.jj_rescan = true;

        for(int i = 0; i < 9; ++i) {
            try {
                MdxParserImpl.JJCalls p = this.jj_2_rtns[i];

                do {
                    if (p.gen > this.jj_gen) {
                        this.jj_la = p.arg;
                        this.jj_lastpos = this.jj_scanpos = p.first;
                        switch(i) {
                            case 0:
                                this.jj_3_1();
                                break;
                            case 1:
                                this.jj_3_2();
                                break;
                            case 2:
                                this.jj_3_3();
                                break;
                            case 3:
                                this.jj_3_4();
                                break;
                            case 4:
                                this.jj_3_5();
                                break;
                            case 5:
                                this.jj_3_6();
                                break;
                            case 6:
                                this.jj_3_7();
                                break;
                            case 7:
                                this.jj_3_8();
                                break;
                            case 8:
                                this.jj_3_9();
                        }
                    }

                    p = p.next;
                } while(p != null);
            } catch (MdxParserImpl.LookaheadSuccess var3) {
            }
        }

        this.jj_rescan = false;
    }

    private void jj_save(int index, int xla) {
        MdxParserImpl.JJCalls p;
        for(p = this.jj_2_rtns[index]; p.gen > this.jj_gen; p = p.next) {
            if (p.next == null) {
                p = p.next = new MdxParserImpl.JJCalls();
                break;
            }
        }

        p.gen = this.jj_gen + xla - this.jj_la;
        p.first = this.token;
        p.arg = xla;
    }

    static {
        jj_la1_init_0();
        jj_la1_init_1();
        jj_la1_init_2();
        jj_la1_init_3();
    }

    static final class JJCalls {
        int gen;
        Token first;
        int arg;
        MdxParserImpl.JJCalls next;
    }

    private static final class LookaheadSuccess extends Error {
        private LookaheadSuccess() {
        }

        // $FF: synthetic method
        LookaheadSuccess(Object x0) {
            this();
        }
    }
}