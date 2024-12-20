/* Decompiler 460ms, total 1070ms, lines 856 */
package mondrian.olap;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java_cup.runtime.Symbol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Scanner {
    private static final Logger LOGGER = LogManager.getLogger(Scanner.class);
    protected int nextChar;
    private int[] lookaheadChars = new int[16];
    private int firstLookaheadChar = 0;
    private int lastLookaheadChar = 0;
    private Hashtable<String, Integer> m_resWordsTable;
    private int iMaxResword;
    private String[] m_aResWords;
    protected boolean debug;
    private List<Integer> lines;
    private int iChar;
    private int iPrevChar;
    private int previousSymbol;
    private boolean inFormula;
    private static final String[][] commentDelim = new String[][]{{"//", null}, {"--", null}, {"/*", "*/"}};
    private static final boolean allowNestedComments = true;
    private static final BigDecimal BigDecimalZero = BigDecimal.valueOf(0L);

    Scanner(boolean debug) {
        this.debug = debug;
    }

    public static boolean getNestedCommentsState() {
        return true;
    }

    public static String[][] getCommentDelimiters() {
        return commentDelim;
    }

    private void advance() throws IOException {
        if (this.firstLookaheadChar == this.lastLookaheadChar) {
            this.nextChar = this.getChar();
        } else {
            this.nextChar = this.lookaheadChars[this.firstLookaheadChar++];
            if (this.firstLookaheadChar == this.lastLookaheadChar) {
                this.firstLookaheadChar = 0;
                this.lastLookaheadChar = 0;
            }
        }

        if (this.nextChar == 10) {
            this.lines.add(this.iChar);
        }

        ++this.iChar;
    }

    private int lookahead() throws IOException {
        return this.lookahead(1);
    }

    private int lookahead(int n) throws IOException {
        if (n == 0) {
            return this.nextChar;
        } else {
            if (n > this.lastLookaheadChar - this.firstLookaheadChar) {
                int len = this.lastLookaheadChar - this.firstLookaheadChar;
                if (n + this.firstLookaheadChar > this.lookaheadChars.length) {
                    int[] t;
                    if (n > this.lookaheadChars.length) {
                        t = new int[n * 2];
                    } else {
                        t = this.lookaheadChars;
                    }

                    System.arraycopy(this.lookaheadChars, this.firstLookaheadChar, t, 0, len);
                    this.lookaheadChars = t;
                    this.firstLookaheadChar = 0;
                    this.lastLookaheadChar = len;
                }

                while(n > this.lastLookaheadChar - this.firstLookaheadChar) {
                    this.lookaheadChars[this.lastLookaheadChar++] = this.getChar();
                }
            }

            return this.lookaheadChars[n - 1 + this.firstLookaheadChar];
        }
    }

    protected int getChar() throws IOException {
        return System.in.read();
    }

    public void init() throws IOException {
        this.initReswords();
        this.lines = new ArrayList();
        this.iChar = this.iPrevChar = 0;
        this.advance();
    }

    void getLocation(Symbol symbol, int[] loc) {
        int iTarget = symbol.left;
        int iLine = -1;
        int iLineEnd = 0;

        int iLineStart;
        do {
            ++iLine;
            iLineStart = iLineEnd;
            iLineEnd = Integer.MAX_VALUE;
            if (iLine < this.lines.size()) {
                iLineEnd = (Integer)this.lines.get(iLine);
            }
        } while(iLineEnd < iTarget);

        loc[0] = iLine;
        loc[1] = iTarget - iLineStart;
    }

    private Symbol trace(Symbol s) {
        if (this.debug) {
            String name = null;
            if (s.sym < this.m_aResWords.length) {
                name = this.m_aResWords[s.sym];
            }

            LOGGER.error("Scanner returns #" + s.sym + (name == null ? "" : ":" + name) + (s.value == null ? "" : "(" + s.value.toString() + ")"));
        }

        return s;
    }

    private void initResword(int id, String s) {
        this.m_resWordsTable.put(s, id);
        if (id > this.iMaxResword) {
            this.iMaxResword = id;
        }

    }

    private void initReswords() {
        this.m_resWordsTable = new Hashtable();
        this.iMaxResword = 0;
        this.initResword(2, "AND");
        this.initResword(3, "AS");
        this.initResword(4, "AXIS");
        this.initResword(6, "CAST");
        this.initResword(5, "CASE");
        this.initResword(7, "CELL");
        this.initResword(8, "CHAPTERS");
        this.initResword(9, "COLUMNS");
        this.initResword(10, "DIMENSION");
        this.initResword(11, "DRILLTHROUGH");
        this.initResword(12, "ELSE");
        this.initResword(13, "EMPTY");
        this.initResword(14, "END");
        this.initResword(15, "EXPLAIN");
        this.initResword(16, "FIRSTROWSET");
        this.initResword(17, "FOR");
        this.initResword(18, "FROM");
        this.initResword(20, "IS");
        this.initResword(19, "IN");
        this.initResword(21, "MATCHES");
        this.initResword(22, "MAXROWS");
        this.initResword(23, "MEMBER");
        this.initResword(24, "NON");
        this.initResword(25, "NOT");
        this.initResword(26, "NULL");
        this.initResword(27, "ON");
        this.initResword(28, "OR");
        this.initResword(29, "PAGES");
        this.initResword(30, "PLAN");
        this.initResword(31, "PROPERTIES");
        this.initResword(32, "RETURN");
        this.initResword(33, "ROWS");
        this.initResword(34, "SECTIONS");
        this.initResword(35, "SELECT");
        this.initResword(36, "SET");
        this.initResword(37, "THEN");
        this.initResword(38, "WHEN");
        this.initResword(39, "WHERE");
        this.initResword(41, "WITH");
        this.initResword(40, "XOR");
        this.m_aResWords = new String[this.iMaxResword + 1];

        String s;
        int i;
        for(Enumeration e = this.m_resWordsTable.keys(); e.hasMoreElements(); this.m_aResWords[i] = s) {
            Object o = e.nextElement();
            s = (String)o;
            i = (Integer)this.m_resWordsTable.get(s);
        }

    }

    public String lookupReserved(int i) {
        return this.m_aResWords[i];
    }

    private Symbol makeSymbol(int id, Object o) {
        int iPrevPrevChar = this.iPrevChar;
        this.iPrevChar = this.iChar;
        this.previousSymbol = id;
        return this.trace(new Symbol(id, iPrevPrevChar, this.iChar, o));
    }

    private Symbol makeNumber(BigDecimal mantissa, int exponent) {
        BigDecimal d = mantissa.movePointRight(exponent);
        return this.makeSymbol(62, d);
    }

    private Symbol makeId(String s, boolean quoted, boolean ampersand) {
        return this.makeSymbol(quoted && ampersand ? 65 : (quoted ? 64 : 63), s);
    }

    private Symbol makeRes(int i) {
        return this.makeSymbol(i, this.m_aResWords[i]);
    }

    private Symbol makeToken(int i, String s) {
        return this.makeSymbol(i, s);
    }

    private Symbol makeString(String s) {
        if (this.inFormula) {
            this.inFormula = false;
            return this.makeSymbol(67, s);
        } else {
            return this.makeSymbol(66, s);
        }
    }

    private void skipToEOL() throws IOException {
        while(this.nextChar != -1 && this.nextChar != 10) {
            this.advance();
        }

    }

    private void skipComment(String startDelim, String endDelim) throws IOException {
        int depth = 1;

        int x;
        for(x = 0; x < startDelim.length(); ++x) {
            this.advance();
        }

        label43:
        do {
            while(this.nextChar != -1) {
                if (this.checkForSymbol(endDelim)) {
                    for(x = 0; x < endDelim.length(); ++x) {
                        this.advance();
                    }

                    --depth;
                    continue label43;
                }

                if (!this.checkForSymbol(startDelim)) {
                    this.advance();
                } else {
                    for(x = 0; x < startDelim.length(); ++x) {
                        this.advance();
                    }

                    ++depth;
                }
            }

            return;
        } while(depth != 0);

    }

    private void searchForComments() throws IOException {
        boolean foundComment;
        do {
            foundComment = false;
            String[][] var2 = commentDelim;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String[] aCommentDelim = var2[var4];
                if (this.checkForSymbol(aCommentDelim[0])) {
                    if (aCommentDelim[1] == null) {
                        foundComment = true;
                        this.skipToEOL();
                    } else {
                        foundComment = true;
                        this.skipComment(aCommentDelim[0], aCommentDelim[1]);
                    }
                }
            }
        } while(foundComment);

    }

    private boolean checkForSymbol(String symb) throws IOException {
        for(int x = 0; x < symb.length(); ++x) {
            if (symb.charAt(x) != this.lookahead(x)) {
                return false;
            }
        }

        return true;
    }

    public Symbol next_token() throws IOException {
        boolean ampersandId = false;

        while(true) {
            this.searchForComments();
            StringBuilder id;
            switch(this.nextChar) {
                case -1:
                    return this.makeToken(0, "EOF");
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 11:
                case 12:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 35:
                case 37:
                case 59:
                case 63:
                case 64:
                case 92:
                case 93:
                case 94:
                case 96:
                default:
                    if (this.nextChar > 65535 || !Character.isWhitespace((char)this.nextChar)) {
                        throw new RuntimeException("Unexpected character '" + (char)this.nextChar + "'");
                    }
                case 9:
                case 10:
                case 13:
                case 32:
                    this.iPrevChar = this.iChar;
                    this.advance();
                    break;
                case 33:
                    this.advance();
                    return this.makeToken(44, "!");
                case 34:
                    id = new StringBuilder();

                    while(true) {
                        this.advance();
                        switch(this.nextChar) {
                            case -1:
                                return this.makeString(id.toString());
                            case 34:
                                this.advance();
                                if (this.nextChar != 34) {
                                    return this.makeString(id.toString());
                                }

                                id.append('"');
                                break;
                            default:
                                id.append((char)this.nextChar);
                        }
                    }
                case 36:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                case 72:
                case 73:
                case 74:
                case 75:
                case 76:
                case 77:
                case 78:
                case 79:
                case 80:
                case 81:
                case 82:
                case 83:
                case 84:
                case 85:
                case 86:
                case 87:
                case 88:
                case 89:
                case 90:
                case 95:
                case 97:
                case 98:
                case 99:
                case 100:
                case 101:
                case 102:
                case 103:
                case 104:
                case 105:
                case 106:
                case 107:
                case 108:
                case 109:
                case 110:
                case 111:
                case 112:
                case 113:
                case 114:
                case 115:
                case 116:
                case 117:
                case 118:
                case 119:
                case 120:
                case 121:
                case 122:
                    id = new StringBuilder();

                    while(true) {
                        id.append((char)this.nextChar);
                        this.advance();
                        switch(this.nextChar) {
                            case 36:
                            case 48:
                            case 49:
                            case 50:
                            case 51:
                            case 52:
                            case 53:
                            case 54:
                            case 55:
                            case 56:
                            case 57:
                            case 65:
                            case 66:
                            case 67:
                            case 68:
                            case 69:
                            case 70:
                            case 71:
                            case 72:
                            case 73:
                            case 74:
                            case 75:
                            case 76:
                            case 77:
                            case 78:
                            case 79:
                            case 80:
                            case 81:
                            case 82:
                            case 83:
                            case 84:
                            case 85:
                            case 86:
                            case 87:
                            case 88:
                            case 89:
                            case 90:
                            case 95:
                            case 97:
                            case 98:
                            case 99:
                            case 100:
                            case 101:
                            case 102:
                            case 103:
                            case 104:
                            case 105:
                            case 106:
                            case 107:
                            case 108:
                            case 109:
                            case 110:
                            case 111:
                            case 112:
                            case 113:
                            case 114:
                            case 115:
                            case 116:
                            case 117:
                            case 118:
                            case 119:
                            case 120:
                            case 121:
                            case 122:
                                break;
                            case 37:
                            case 38:
                            case 39:
                            case 40:
                            case 41:
                            case 42:
                            case 43:
                            case 44:
                            case 45:
                            case 46:
                            case 47:
                            case 58:
                            case 59:
                            case 60:
                            case 61:
                            case 62:
                            case 63:
                            case 64:
                            case 91:
                            case 92:
                            case 93:
                            case 94:
                            case 96:
                            default:
                                String strId = id.toString();
                                Integer i = (Integer)this.m_resWordsTable.get(strId.toUpperCase());
                                if (i == null) {
                                    return this.makeId(strId, false, false);
                                }

                                return this.makeRes(i);
                        }
                    }
                case 38:
                    this.advance();
                    if (this.nextChar != 91) {
                        return this.makeToken(68, "&");
                    }

                    ampersandId = true;
                case 91:
                    id = new StringBuilder();

                    while(true) {
                        this.advance();
                        switch(this.nextChar) {
                            case -1:
                                if (ampersandId) {
                                    ampersandId = false;
                                    return this.makeId(id.toString(), true, true);
                                }

                                return this.makeId(id.toString(), true, false);
                            case 93:
                                this.advance();
                                if (this.nextChar != 93) {
                                    if (ampersandId) {
                                        ampersandId = false;
                                        return this.makeId(id.toString(), true, true);
                                    }

                                    return this.makeId(id.toString(), true, false);
                                }

                                id.append(']');
                                break;
                            default:
                                id.append((char)this.nextChar);
                        }
                    }
                case 39:
                    if (this.previousSymbol == 3) {
                        this.inFormula = true;
                    }

                    id = new StringBuilder();

                    while(true) {
                        this.advance();
                        switch(this.nextChar) {
                            case -1:
                                return this.makeString(id.toString());
                            case 39:
                                this.advance();
                                if (this.nextChar != 39) {
                                    return this.makeString(id.toString());
                                }

                                id.append('\'');
                                break;
                            default:
                                id.append((char)this.nextChar);
                        }
                    }
                case 40:
                    this.advance();
                    return this.makeToken(54, "(");
                case 41:
                    this.advance();
                    return this.makeToken(60, ")");
                case 42:
                    this.advance();
                    return this.makeToken(43, "*");
                case 43:
                    this.advance();
                    return this.makeToken(58, "+");
                case 44:
                    this.advance();
                    return this.makeToken(46, ",");
                case 45:
                    this.advance();
                    return this.makeToken(56, "-");
                case 46:
                    switch(this.lookahead()) {
                        case 48:
                        case 49:
                        case 50:
                        case 51:
                        case 52:
                        case 53:
                        case 54:
                        case 55:
                        case 56:
                        case 57:
                            break;
                        default:
                            this.advance();
                            return this.makeToken(48, ".");
                    }
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                    BigDecimal n = BigDecimalZero;
                    int digitCount = 0;
                    int exponent = 0;
                    boolean positive = true;
                    BigDecimal mantissa = BigDecimalZero;
                    Scanner.State state = Scanner.State.leftOfPoint;

                    label149:
                    while(true) {
                        switch(this.nextChar) {
                            case 43:
                            case 45:
                                if (state == Scanner.State.inExponent && digitCount == 0) {
                                    positive = !positive;
                                    this.advance();
                                    break;
                                }
                            case 44:
                            case 47:
                            case 58:
                            case 59:
                            case 60:
                            case 61:
                            case 62:
                            case 63:
                            case 64:
                            case 65:
                            case 66:
                            case 67:
                            case 68:
                            case 70:
                            case 71:
                            case 72:
                            case 73:
                            case 74:
                            case 75:
                            case 76:
                            case 77:
                            case 78:
                            case 79:
                            case 80:
                            case 81:
                            case 82:
                            case 83:
                            case 84:
                            case 85:
                            case 86:
                            case 87:
                            case 88:
                            case 89:
                            case 90:
                            case 91:
                            case 92:
                            case 93:
                            case 94:
                            case 95:
                            case 96:
                            case 97:
                            case 98:
                            case 99:
                            case 100:
                            default:
                                break label149;
                            case 46:
                                switch(state) {
                                    case leftOfPoint:
                                        state = Scanner.State.rightOfPoint;
                                        mantissa = n;
                                        n = BigDecimalZero;
                                        digitCount = 0;
                                        positive = true;
                                        this.advance();
                                        continue;
                                    case rightOfPoint:
                                        mantissa = mantissa.add(n.movePointRight(-digitCount));
                                        return this.makeNumber(mantissa, exponent);
                                    case inExponent:
                                        if (!positive) {
                                            n = n.negate();
                                        }

                                        exponent = n.intValue();
                                        return this.makeNumber(mantissa, exponent);
                                    default:
                                        continue;
                                }
                            case 48:
                            case 49:
                            case 50:
                            case 51:
                            case 52:
                            case 53:
                            case 54:
                            case 55:
                            case 56:
                            case 57:
                                n = n.movePointRight(1);
                                n = n.add(BigDecimal.valueOf((long)(this.nextChar - 48)));
                                ++digitCount;
                                this.advance();
                                break;
                            case 69:
                            case 101:
                                switch(state) {
                                    case leftOfPoint:
                                        mantissa = n;
                                        break;
                                    case inExponent:
                                        if (!positive) {
                                            n = n.negate();
                                        }

                                        exponent = n.intValue();
                                        return this.makeNumber(mantissa, exponent);
                                    default:
                                        mantissa = mantissa.add(n.movePointRight(-digitCount));
                                }

                                digitCount = 0;
                                n = BigDecimalZero;
                                positive = true;
                                this.advance();
                                state = Scanner.State.inExponent;
                        }
                    }

                    switch(state) {
                        case leftOfPoint:
                            mantissa = n;
                            break;
                        case rightOfPoint:
                            mantissa = mantissa.add(n.movePointRight(-digitCount));
                            break;
                        default:
                            if (!positive) {
                                n = n.negate();
                            }

                            exponent = n.intValue();
                    }

                    return this.makeNumber(mantissa, exponent);
                case 47:
                    this.advance();
                    return this.makeToken(61, "/");
                case 58:
                    this.advance();
                    return this.makeToken(45, ":");
                case 60:
                    this.advance();
                    switch(this.nextChar) {
                        case 61:
                            this.advance();
                            return this.makeToken(53, "<=");
                        case 62:
                            this.advance();
                            return this.makeToken(57, "<>");
                        default:
                            return this.makeToken(55, "<");
                    }
                case 61:
                    this.advance();
                    return this.makeToken(49, "=");
                case 62:
                    this.advance();
                    switch(this.nextChar) {
                        case 61:
                            this.advance();
                            return this.makeToken(50, ">=");
                        default:
                            return this.makeToken(51, ">");
                    }
                case 123:
                    this.advance();
                    return this.makeToken(52, "{");
                case 124:
                    this.advance();
                    switch(this.nextChar) {
                        case 124:
                            this.advance();
                            return this.makeToken(47, "||");
                        default:
                            return this.makeToken(68, "|");
                    }
                case 125:
                    this.advance();
                    return this.makeToken(59, "}");
            }
        }
    }

    private static enum State {
        leftOfPoint,
        rightOfPoint,
        inExponent;
    }
}