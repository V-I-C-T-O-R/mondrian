/* Decompiler 2307ms, total 3052ms, lines 2665 */
package mondrian.parser;

import java.io.IOException;
import java.io.PrintStream;

public class MdxParserImplTokenManager implements MdxParserImplConstants {
    public PrintStream debugStream;
    static final long[] jjbitVec0 = new long[]{-2L, -1L, -1L, -1L};
    static final long[] jjbitVec2 = new long[]{0L, 0L, -1L, -1L};
    static final long[] jjbitVec3 = new long[]{2301339413881290750L, -16384L, 4294967295L, 432345564227567616L};
    static final long[] jjbitVec4 = new long[]{0L, 0L, 0L, -36028797027352577L};
    static final long[] jjbitVec5 = new long[]{274877906943L, 0L, 0L, 0L};
    static final long[] jjbitVec6 = new long[]{0L, -1L, -1L, -1L};
    static final long[] jjbitVec7 = new long[]{-1L, -1L, 65535L, 0L};
    static final long[] jjbitVec8 = new long[]{-1L, -1L, 0L, 0L};
    static final long[] jjbitVec9 = new long[]{70368744177663L, 0L, 0L, 0L};
    static final int[] jjnextStates = new int[]{22, 23, 27, 24, 29, 31, 32, 37, 42, 10, 12, 13, 5, 7, 8, 17, 19, 20, 29, 30, 31, 27, 28, 24, 32, 38, 40, 41, 25, 26};
    public static final String[] jjstrLiteralImages = new String[]{"", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "*", "!", ":", ",", "||", ".", "=", ">=", ">", "{", "<=", "(", "<", "-", "<>", "+", "}", ")", "/", "@", null, null, null, null, null, null, null, null, null, null, null, null, null};
    public static final String[] lexStateNames = new String[]{"DEFAULT", "IN_SINGLE_LINE_COMMENT", "IN_FORMAL_COMMENT", "IN_MULTI_LINE_COMMENT"};
    public static final int[] jjnewLexState = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2, 1, 1, 3, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    static final long[] jjtoToken = new long[]{288230376151711743L, 265214230400L};
    static final long[] jjtoSkip = new long[]{8935141660703064064L, 56L};
    static final long[] jjtoSpecial = new long[]{0L, 56L};
    static final long[] jjtoMore = new long[]{Long.MIN_VALUE, 71L};
    protected SimpleCharStream input_stream;
    private final int[] jjrounds;
    private final int[] jjstateSet;
    private final StringBuilder jjimage;
    private StringBuilder image;
    private int jjimageLen;
    private int lengthOfMatch;
    protected char curChar;
    int curLexState;
    int defaultLexState;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;

    public void setDebugStream(PrintStream ds) {
        this.debugStream = ds;
    }

    private final int jjStopStringLiteralDfa_0(int pos, long active0, long active1) {
        switch(pos) {
            case 0:
                if ((active1 & 33554437L) != 0L) {
                    return 2;
                } else if ((active1 & 4096L) != 0L) {
                    return 44;
                } else {
                    if ((active0 & 288230376151711742L) != 0L) {
                        this.jjmatchedKind = 98;
                        return 15;
                    }

                    return -1;
                }
            case 1:
                if ((active1 & 4L) != 0L) {
                    return 0;
                } else if ((active0 & 288230369684094938L) != 0L) {
                    this.jjmatchedKind = 98;
                    this.jjmatchedPos = 1;
                    return 15;
                } else {
                    if ((active0 & 6467616804L) != 0L) {
                        return 15;
                    }

                    return -1;
                }
            case 2:
                if ((active0 & 270207174273925080L) != 0L) {
                    this.jjmatchedKind = 98;
                    this.jjmatchedPos = 2;
                    return 15;
                } else {
                    if ((active0 & 18023195410169858L) != 0L) {
                        return 15;
                    }

                    return -1;
                }
            case 3:
                if ((active0 & 229551064311586320L) != 0L) {
                    if (this.jjmatchedPos != 3) {
                        this.jjmatchedKind = 98;
                        this.jjmatchedPos = 3;
                    }

                    return 15;
                } else {
                    if ((active0 & 40656109962338760L) != 0L) {
                        return 15;
                    }

                    return -1;
                }
            case 4:
                if ((active0 & 9007207844806672L) != 0L) {
                    return 15;
                } else {
                    if ((active0 & 220614225210957312L) != 0L) {
                        this.jjmatchedKind = 98;
                        this.jjmatchedPos = 4;
                        return 15;
                    }

                    return -1;
                }
            case 5:
                if ((active0 & 220471151126170112L) != 0L) {
                    this.jjmatchedKind = 98;
                    this.jjmatchedPos = 5;
                    return 15;
                } else {
                    if ((active0 & 143074084787200L) != 0L) {
                        return 15;
                    }

                    return -1;
                }
            case 6:
                if ((active0 & 76351496183136768L) != 0L) {
                    this.jjmatchedKind = 98;
                    this.jjmatchedPos = 6;
                    return 15;
                } else {
                    if ((active0 & 144119654943033344L) != 0L) {
                        return 15;
                    }

                    return -1;
                }
            case 7:
                if ((active0 & 72058968427463168L) != 0L) {
                    return 15;
                } else {
                    if ((active0 & 4292527755673600L) != 0L) {
                        this.jjmatchedKind = 98;
                        this.jjmatchedPos = 7;
                        return 15;
                    }

                    return -1;
                }
            case 8:
                if ((active0 & 4292527755657216L) != 0L) {
                    this.jjmatchedKind = 98;
                    this.jjmatchedPos = 8;
                    return 15;
                } else {
                    if ((active0 & 16384L) != 0L) {
                        return 15;
                    }

                    return -1;
                }
            case 9:
                if ((active0 & 4292493395918848L) != 0L) {
                    this.jjmatchedKind = 98;
                    this.jjmatchedPos = 9;
                    return 15;
                } else {
                    if ((active0 & 34359738368L) != 0L) {
                        return 15;
                    }

                    return -1;
                }
            case 10:
                if ((active0 & 4222124650692608L) != 0L) {
                    this.jjmatchedKind = 98;
                    this.jjmatchedPos = 10;
                    return 15;
                } else {
                    if ((active0 & 70368745226240L) != 0L) {
                        return 15;
                    }

                    return -1;
                }
            case 11:
                if ((active0 & 4222124650659840L) != 0L) {
                    this.jjmatchedKind = 98;
                    this.jjmatchedPos = 11;
                    return 15;
                } else {
                    if ((active0 & 32768L) != 0L) {
                        return 15;
                    }

                    return -1;
                }
            case 12:
                if ((active0 & 4222124650659840L) != 0L) {
                    this.jjmatchedKind = 98;
                    this.jjmatchedPos = 12;
                    return 15;
                }

                return -1;
            case 13:
                if ((active0 & 4222124650659840L) != 0L) {
                    this.jjmatchedKind = 98;
                    this.jjmatchedPos = 13;
                    return 15;
                }

                return -1;
            case 14:
                if ((active0 & 4222124650659840L) != 0L) {
                    this.jjmatchedKind = 98;
                    this.jjmatchedPos = 14;
                    return 15;
                }

                return -1;
            case 15:
                if ((active0 & 4222124650659840L) != 0L) {
                    this.jjmatchedKind = 98;
                    this.jjmatchedPos = 15;
                    return 15;
                }

                return -1;
            case 16:
                if ((active0 & 4222124650659840L) != 0L) {
                    this.jjmatchedKind = 98;
                    this.jjmatchedPos = 16;
                    return 15;
                }

                return -1;
            case 17:
                if ((active0 & 4222124650659840L) != 0L) {
                    this.jjmatchedKind = 98;
                    this.jjmatchedPos = 17;
                    return 15;
                }

                return -1;
            case 18:
                if ((active0 & 3659174697238528L) != 0L) {
                    this.jjmatchedKind = 98;
                    this.jjmatchedPos = 18;
                    return 15;
                } else {
                    if ((active0 & 562949953421312L) != 0L) {
                        return 15;
                    }

                    return -1;
                }
            case 19:
                if ((active0 & 281474976710656L) != 0L) {
                    return 15;
                } else {
                    if ((active0 & 3377699720527872L) != 0L) {
                        this.jjmatchedKind = 98;
                        this.jjmatchedPos = 19;
                        return 15;
                    }

                    return -1;
                }
            case 20:
                if ((active0 & 3377699720527872L) != 0L) {
                    this.jjmatchedKind = 98;
                    this.jjmatchedPos = 20;
                    return 15;
                }

                return -1;
            case 21:
                if ((active0 & 1125899906842624L) != 0L) {
                    this.jjmatchedKind = 98;
                    this.jjmatchedPos = 21;
                    return 15;
                } else {
                    if ((active0 & 2251799813685248L) != 0L) {
                        return 15;
                    }

                    return -1;
                }
            default:
                return -1;
        }
    }

    private final int jjStartNfa_0(int pos, long active0, long active1) {
        return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0, active1), pos + 1);
    }

    private int jjStopAtPos(int pos, int kind) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        return pos + 1;
    }

    private int jjMoveStringLiteralDfa0_0() {
        switch(this.curChar) {
            case '!':
                return this.jjStopAtPos(0, 72);
            case '"':
            case '#':
            case '%':
            case '&':
            case '\'':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case ';':
            case '?':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'L':
            case 'Q':
            case 'V':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'g':
            case 'h':
            case 'j':
            case 'k':
            case 'l':
            case 'q':
            case 'v':
            case 'y':
            case 'z':
            default:
                return this.jjMoveNfa_0(3, 0);
            case '$':
                return this.jjMoveStringLiteralDfa1_0(144115188075855872L, 0L);
            case '(':
                return this.jjStopAtPos(0, 82);
            case ')':
                return this.jjStopAtPos(0, 88);
            case '*':
                return this.jjStopAtPos(0, 71);
            case '+':
                return this.jjStopAtPos(0, 86);
            case ',':
                return this.jjStopAtPos(0, 74);
            case '-':
                this.jjmatchedKind = 84;
                return this.jjMoveStringLiteralDfa1_0(0L, 2L);
            case '.':
                return this.jjStartNfaWithStates_0(0, 76, 44);
            case '/':
                this.jjmatchedKind = 89;
                return this.jjMoveStringLiteralDfa1_0(0L, 5L);
            case ':':
                return this.jjStopAtPos(0, 73);
            case '<':
                this.jjmatchedKind = 83;
                return this.jjMoveStringLiteralDfa1_0(0L, 2228224L);
            case '=':
                return this.jjStopAtPos(0, 77);
            case '>':
                this.jjmatchedKind = 79;
                return this.jjMoveStringLiteralDfa1_0(0L, 16384L);
            case '@':
                return this.jjStopAtPos(0, 90);
            case 'A':
            case 'a':
                return this.jjMoveStringLiteralDfa1_0(14L, 0L);
            case 'B':
            case 'b':
                return this.jjMoveStringLiteralDfa1_0(48L, 0L);
            case 'C':
            case 'c':
                return this.jjMoveStringLiteralDfa1_0(16320L, 0L);
            case 'D':
            case 'd':
                return this.jjMoveStringLiteralDfa1_0(49152L, 0L);
            case 'E':
            case 'e':
                return this.jjMoveStringLiteralDfa1_0(72057594038910976L, 0L);
            case 'F':
            case 'f':
                return this.jjMoveStringLiteralDfa1_0(7340032L, 0L);
            case 'I':
            case 'i':
                return this.jjMoveStringLiteralDfa1_0(25165824L, 0L);
            case 'M':
            case 'm':
                return this.jjMoveStringLiteralDfa1_0(234881024L, 0L);
            case 'N':
            case 'n':
                return this.jjMoveStringLiteralDfa1_0(1879048192L, 0L);
            case 'O':
            case 'o':
                return this.jjMoveStringLiteralDfa1_0(6442450944L, 0L);
            case 'P':
            case 'p':
                return this.jjMoveStringLiteralDfa1_0(60129542144L, 0L);
            case 'R':
            case 'r':
                return this.jjMoveStringLiteralDfa1_0(1030792151040L, 0L);
            case 'S':
            case 's':
                return this.jjMoveStringLiteralDfa1_0(16492674416640L, 0L);
            case 'T':
            case 't':
                return this.jjMoveStringLiteralDfa1_0(123145302310912L, 0L);
            case 'U':
            case 'u':
                return this.jjMoveStringLiteralDfa1_0(4362862139015168L, 0L);
            case 'W':
            case 'w':
                return this.jjMoveStringLiteralDfa1_0(49539595901075456L, 0L);
            case 'X':
            case 'x':
                return this.jjMoveStringLiteralDfa1_0(18014398509481984L, 0L);
            case '{':
                return this.jjStopAtPos(0, 80);
            case '|':
                return this.jjMoveStringLiteralDfa1_0(0L, 2048L);
            case '}':
                return this.jjStopAtPos(0, 87);
        }
    }

    private int jjMoveStringLiteralDfa1_0(long active0, long active1) {
        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(0, active0, active1);
            return 1;
        }

        switch(this.curChar) {
            case '*':
                if ((active1 & 4L) != 0L) {
                    return this.jjStartNfaWithStates_0(1, 66, 0);
                }
            case '+':
            case ',':
            case '.':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case ':':
            case ';':
            case '<':
            case '?':
            case '@':
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'G':
            case 'J':
            case 'K':
            case 'Q':
            case 'T':
            case 'V':
            case 'W':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'b':
            case 'c':
            case 'd':
            case 'f':
            case 'g':
            case 'j':
            case 'k':
            case 'q':
            case 't':
            case 'v':
            case 'w':
            case 'z':
            case '{':
            default:
                break;
            case '-':
                if ((active1 & 2L) != 0L) {
                    return this.jjStopAtPos(1, 65);
                }
                break;
            case '/':
                if ((active1 & 1L) != 0L) {
                    return this.jjStopAtPos(1, 64);
                }
                break;
            case '=':
                if ((active1 & 16384L) != 0L) {
                    return this.jjStopAtPos(1, 78);
                }

                if ((active1 & 131072L) != 0L) {
                    return this.jjStopAtPos(1, 81);
                }
                break;
            case '>':
                if ((active1 & 2097152L) != 0L) {
                    return this.jjStopAtPos(1, 85);
                }
                break;
            case 'A':
            case 'a':
                return this.jjMoveStringLiteralDfa2_0(active0, 8690598080L, active1, 0L);
            case 'E':
            case 'e':
                return this.jjMoveStringLiteralDfa2_0(active0, 16698967064848L, active1, 0L);
            case 'H':
            case 'h':
                return this.jjMoveStringLiteralDfa2_0(active0, 13528391068156416L, active1, 0L);
            case 'I':
            case 'i':
                return this.jjMoveStringLiteralDfa2_0(active0, 36028797020028928L, active1, 0L);
            case 'L':
            case 'l':
                return this.jjMoveStringLiteralDfa2_0(active0, 17179934720L, active1, 0L);
            case 'M':
            case 'm':
                return this.jjMoveStringLiteralDfa2_0(active0, 131072L, active1, 0L);
            case 'N':
            case 'n':
                if ((active0 & 8388608L) != 0L) {
                    return this.jjStartNfaWithStates_0(1, 23, 15);
                }

                if ((active0 & 2147483648L) != 0L) {
                    return this.jjStartNfaWithStates_0(1, 31, 15);
                }

                return this.jjMoveStringLiteralDfa2_0(active0, 262146L, active1, 0L);
            case 'O':
            case 'o':
                return this.jjMoveStringLiteralDfa2_0(active0, 18015223950612480L, active1, 0L);
            case 'P':
            case 'p':
                return this.jjMoveStringLiteralDfa2_0(active0, 140737488355328L, active1, 0L);
            case 'R':
            case 'r':
                if ((active0 & 4294967296L) != 0L) {
                    return this.jjStartNfaWithStates_0(1, 32, 15);
                }

                return this.jjMoveStringLiteralDfa2_0(active0, 105587480232960L, active1, 0L);
            case 'S':
            case 's':
                if ((active0 & 4L) != 0L) {
                    return this.jjStartNfaWithStates_0(1, 2, 15);
                }

                if ((active0 & 16777216L) != 0L) {
                    return this.jjStartNfaWithStates_0(1, 24, 15);
                }

                return this.jjMoveStringLiteralDfa2_0(active0, 148337312726515712L, active1, 0L);
            case 'U':
            case 'u':
                return this.jjMoveStringLiteralDfa2_0(active0, 1073750016L, active1, 0L);
            case 'X':
            case 'x':
                return this.jjMoveStringLiteralDfa2_0(active0, 72057594038452232L, active1, 0L);
            case 'Y':
            case 'y':
                if ((active0 & 32L) != 0L) {
                    return this.jjStartNfaWithStates_0(1, 5, 15);
                }
                break;
            case '|':
                if ((active1 & 2048L) != 0L) {
                    return this.jjStopAtPos(1, 75);
                }
        }

        return this.jjStartNfa_0(0, active0, active1);
    }

    private int jjMoveStringLiteralDfa2_0(long old0, long active0, long old1, long active1) {
        if (((active0 &= old0) | active1 & old1) == 0L) {
            return this.jjStartNfa_0(0, old0, old1);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var10) {
                this.jjStopStringLiteralDfa_0(1, active0, 0L);
                return 2;
            }

            switch(this.curChar) {
                case 'A':
                case 'a':
                    return this.jjMoveStringLiteralDfa3_0(active0, 105570296136192L);
                case 'B':
                case 'b':
                    return this.jjMoveStringLiteralDfa3_0(active0, 8192L);
                case 'C':
                case 'c':
                    return this.jjMoveStringLiteralDfa3_0(active0, 1099511627776L);
                case 'D':
                case 'd':
                    if ((active0 & 2L) != 0L) {
                        return this.jjStartNfaWithStates_0(2, 1, 15);
                    } else {
                        if ((active0 & 262144L) != 0L) {
                            return this.jjStartNfaWithStates_0(2, 18, 15);
                        }

                        return this.jjMoveStringLiteralDfa3_0(active0, 140737488355328L);
                    }
                case 'E':
                case 'e':
                    return this.jjMoveStringLiteralDfa3_0(active0, 17750515718816768L);
                case 'F':
                case 'f':
                    return this.jjMoveStringLiteralDfa3_0(active0, 68719476736L);
                case 'G':
                case 'g':
                    return this.jjMoveStringLiteralDfa3_0(active0, 8589934608L);
                case 'I':
                case 'i':
                    return this.jjMoveStringLiteralDfa3_0(active0, 72057594037960712L);
                case 'L':
                case 'l':
                    return this.jjMoveStringLiteralDfa3_0(active0, 2474974906624L);
                case 'M':
                case 'm':
                    return this.jjMoveStringLiteralDfa3_0(active0, 134238208L);
                case 'N':
                case 'n':
                    if ((active0 & 268435456L) != 0L) {
                        return this.jjStartNfaWithStates_0(2, 28, 15);
                    }
                case 'H':
                case 'J':
                case 'K':
                case 'Q':
                case 'U':
                case 'V':
                case 'Z':
                case '[':
                case '\\':
                case ']':
                case '^':
                case '_':
                case '`':
                case 'h':
                case 'j':
                case 'k':
                case 'q':
                case 'u':
                case 'v':
                default:
                    return this.jjStartNfa_0(1, active0, 0L);
                case 'O':
                case 'o':
                    return this.jjMoveStringLiteralDfa3_0(active0, 34363932672L);
                case 'P':
                case 'p':
                    return this.jjMoveStringLiteralDfa3_0(active0, 655360L);
                case 'R':
                case 'r':
                    if ((active0 & 2097152L) != 0L) {
                        return this.jjStartNfaWithStates_0(2, 21, 15);
                    } else {
                        if ((active0 & 18014398509481984L) != 0L) {
                            return this.jjStartNfaWithStates_0(2, 54, 15);
                        }

                        return this.jjMoveStringLiteralDfa3_0(active0, 1048576L);
                    }
                case 'S':
                case 's':
                    return this.jjMoveStringLiteralDfa3_0(active0, 4398046576832L);
                case 'T':
                case 't':
                    if ((active0 & 536870912L) != 0L) {
                        return this.jjStartNfaWithStates_0(2, 29, 15);
                    } else {
                        if ((active0 & 8796093022208L) != 0L) {
                            return this.jjStartNfaWithStates_0(2, 43, 15);
                        }

                        return this.jjMoveStringLiteralDfa3_0(active0, 36028934491471872L);
                    }
                case 'W':
                case 'w':
                    return this.jjMoveStringLiteralDfa3_0(active0, 549755813888L);
                case 'X':
                case 'x':
                    return this.jjMoveStringLiteralDfa3_0(active0, 67108864L);
                case 'Y':
                case 'y':
                    return this.jjMoveStringLiteralDfa3_0(active0, 144115188075855872L);
            }
        }
    }

    private int jjMoveStringLiteralDfa3_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(1, old0, 0L);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var6) {
                this.jjStopStringLiteralDfa_0(2, active0, 0L);
                return 3;
            }

            switch(this.curChar) {
                case 'A':
                case 'a':
                    return this.jjMoveStringLiteralDfa4_0(active0, 140737488356352L);
                case 'B':
                case 'b':
                    return this.jjMoveStringLiteralDfa4_0(active0, 134217728L);
                case 'C':
                case 'c':
                    return this.jjMoveStringLiteralDfa4_0(active0, 33554432L);
                case 'E':
                case 'e':
                    if ((active0 & 64L) != 0L) {
                        return this.jjStartNfaWithStates_0(3, 6, 15);
                    } else if ((active0 & 8192L) != 0L) {
                        return this.jjStartNfaWithStates_0(3, 13, 15);
                    } else {
                        if ((active0 & 65536L) != 0L) {
                            return this.jjStartNfaWithStates_0(3, 16, 15);
                        }

                        return this.jjMoveStringLiteralDfa4_0(active0, 2207613206528L);
                    }
                case 'H':
                case 'h':
                    if ((active0 & 36028797018963968L) != 0L) {
                        return this.jjStartNfaWithStates_0(3, 55, 15);
                    }
                case 'D':
                case 'F':
                case 'G':
                case 'J':
                case 'K':
                case 'O':
                case 'Q':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '[':
                case '\\':
                case ']':
                case '^':
                case '`':
                case 'd':
                case 'f':
                case 'g':
                case 'j':
                case 'k':
                case 'o':
                case 'q':
                default:
                    return this.jjStartNfa_0(2, active0, 0L);
                case 'I':
                case 'i':
                    return this.jjMoveStringLiteralDfa4_0(active0, 16L);
                case 'L':
                case 'l':
                    if ((active0 & 256L) != 0L) {
                        return this.jjStartNfaWithStates_0(3, 8, 15);
                    } else {
                        if ((active0 & 1073741824L) != 0L) {
                            return this.jjStartNfaWithStates_0(3, 30, 15);
                        }

                        return this.jjMoveStringLiteralDfa4_0(active0, 274878464000L);
                    }
                case 'M':
                case 'm':
                    if ((active0 & 4194304L) != 0L) {
                        return this.jjStartNfaWithStates_0(3, 22, 15);
                    }

                    return this.jjMoveStringLiteralDfa4_0(active0, 4096L);
                case 'N':
                case 'n':
                    if ((active0 & 17179869184L) != 0L) {
                        return this.jjStartNfaWithStates_0(3, 34, 15);
                    } else if ((active0 & 17592186044416L) != 0L) {
                        return this.jjStartNfaWithStates_0(3, 44, 15);
                    } else {
                        if ((active0 & 35184372088832L) != 0L) {
                            this.jjmatchedKind = 45;
                            this.jjmatchedPos = 3;
                        } else if ((active0 & 4503599627370496L) != 0L) {
                            return this.jjStartNfaWithStates_0(3, 52, 15);
                        }

                        return this.jjMoveStringLiteralDfa4_0(active0, 70368744177664L);
                    }
                case 'P':
                case 'p':
                    return this.jjMoveStringLiteralDfa4_0(active0, 34359738880L);
                case 'R':
                case 'r':
                    return this.jjMoveStringLiteralDfa4_0(active0, 9007268041326592L);
                case 'S':
                case 's':
                    if ((active0 & 8L) != 0L) {
                        return this.jjStartNfaWithStates_0(3, 3, 15);
                    } else {
                        if ((active0 & 549755813888L) != 0L) {
                            return this.jjStartNfaWithStates_0(3, 39, 15);
                        }

                        return this.jjMoveStringLiteralDfa4_0(active0, 216177180161343488L);
                    }
                case 'T':
                case 't':
                    if ((active0 & 128L) != 0L) {
                        return this.jjStartNfaWithStates_0(3, 7, 15);
                    }

                    return this.jjMoveStringLiteralDfa4_0(active0, 1099511758848L);
                case 'U':
                case 'u':
                    return this.jjMoveStringLiteralDfa4_0(active0, 137438955520L);
                case '_':
                    return this.jjMoveStringLiteralDfa4_0(active0, 4222124650659840L);
            }
        }
    }

    private int jjMoveStringLiteralDfa4_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(2, old0, 0L);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var6) {
                this.jjStopStringLiteralDfa_0(3, active0, 0L);
                return 4;
            }

            switch(this.curChar) {
                case 'A':
                case 'a':
                    return this.jjMoveStringLiteralDfa5_0(active0, 524288L);
                case 'B':
                case 'b':
                    return this.jjMoveStringLiteralDfa5_0(active0, 274877906944L);
                case 'C':
                case 'c':
                    return this.jjMoveStringLiteralDfa5_0(active0, 2199023255552L);
                case 'E':
                case 'e':
                    if ((active0 & 9007199254740992L) != 0L) {
                        return this.jjStartNfaWithStates_0(4, 53, 15);
                    }

                    return this.jjMoveStringLiteralDfa5_0(active0, 844528143564800L);
                case 'H':
                case 'h':
                    return this.jjMoveStringLiteralDfa5_0(active0, 33554432L);
                case 'I':
                case 'i':
                    return this.jjMoveStringLiteralDfa5_0(active0, 5497558142976L);
                case 'L':
                case 'l':
                    return this.jjMoveStringLiteralDfa5_0(active0, 32768L);
                case 'M':
                case 'm':
                    return this.jjMoveStringLiteralDfa5_0(active0, 2048L);
                case 'N':
                case 'n':
                    if ((active0 & 16L) != 0L) {
                        return this.jjStartNfaWithStates_0(4, 4, 15);
                    }

                    return this.jjMoveStringLiteralDfa5_0(active0, 16384L);
                case 'O':
                case 'o':
                    return this.jjMoveStringLiteralDfa5_0(active0, 67108864L);
                case 'R':
                case 'r':
                    return this.jjMoveStringLiteralDfa5_0(active0, 137438953472L);
                case 'S':
                case 's':
                    if ((active0 & 8589934592L) != 0L) {
                        return this.jjStartNfaWithStates_0(4, 33, 15);
                    }

                    return this.jjMoveStringLiteralDfa5_0(active0, 70368744177664L);
                case 'T':
                case 't':
                    return this.jjMoveStringLiteralDfa5_0(active0, 216313519603189248L);
                case 'W':
                case 'w':
                    return this.jjMoveStringLiteralDfa5_0(active0, 3377699720527872L);
                case 'Y':
                case 'y':
                    if ((active0 & 131072L) != 0L) {
                        return this.jjStartNfaWithStates_0(4, 17, 15);
                    }
                case 'D':
                case 'F':
                case 'G':
                case 'J':
                case 'K':
                case 'P':
                case 'Q':
                case 'U':
                case 'V':
                case 'X':
                case 'Z':
                case '[':
                case '\\':
                case ']':
                case '^':
                case '_':
                case '`':
                case 'd':
                case 'f':
                case 'g':
                case 'j':
                case 'k':
                case 'p':
                case 'q':
                case 'u':
                case 'v':
                case 'x':
                default:
                    return this.jjStartNfa_0(3, active0, 0L);
            }
        }
    }

    private int jjMoveStringLiteralDfa5_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(3, old0, 0L);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var6) {
                this.jjStopStringLiteralDfa_0(4, active0, 0L);
                return 5;
            }

            switch(this.curChar) {
                case 'A':
                case 'a':
                    return this.jjMoveStringLiteralDfa6_0(active0, 70643622084608L);
                case 'B':
                case 'C':
                case 'D':
                case 'F':
                case 'G':
                case 'H':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'P':
                case 'U':
                case 'V':
                case 'X':
                case 'Y':
                case 'Z':
                case '[':
                case '\\':
                case ']':
                case '^':
                case '_':
                case '`':
                case 'b':
                case 'c':
                case 'd':
                case 'f':
                case 'g':
                case 'h':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'p':
                case 'u':
                case 'v':
                default:
                    return this.jjStartNfa_0(4, active0, 0L);
                case 'E':
                case 'e':
                    if ((active0 & 1024L) != 0L) {
                        return this.jjStartNfaWithStates_0(5, 10, 15);
                    } else {
                        if ((active0 & 140737488355328L) != 0L) {
                            return this.jjStartNfaWithStates_0(5, 47, 15);
                        }

                        return this.jjMoveStringLiteralDfa6_0(active0, 147492887829938688L);
                    }
                case 'I':
                case 'i':
                    return this.jjMoveStringLiteralDfa6_0(active0, 72057594038452224L);
                case 'N':
                case 'n':
                    if ((active0 & 137438953472L) != 0L) {
                        return this.jjStartNfaWithStates_0(5, 37, 15);
                    }

                    return this.jjMoveStringLiteralDfa6_0(active0, 2048L);
                case 'O':
                case 'o':
                    return this.jjMoveStringLiteralDfa6_0(active0, 5497558138880L);
                case 'Q':
                case 'q':
                    return this.jjMoveStringLiteralDfa6_0(active0, 844424930131968L);
                case 'R':
                case 'r':
                    if ((active0 & 134217728L) != 0L) {
                        return this.jjStartNfaWithStates_0(5, 27, 15);
                    }

                    return this.jjMoveStringLiteralDfa6_0(active0, 34360786944L);
                case 'S':
                case 's':
                    return this.jjMoveStringLiteralDfa6_0(active0, 68719493120L);
                case 'T':
                case 't':
                    if ((active0 & 4096L) != 0L) {
                        return this.jjStartNfaWithStates_0(5, 12, 15);
                    } else {
                        if ((active0 & 2199023255552L) != 0L) {
                            return this.jjStartNfaWithStates_0(5, 41, 15);
                        }

                        return this.jjMoveStringLiteralDfa6_0(active0, 32768L);
                    }
                case 'W':
                case 'w':
                    return this.jjMoveStringLiteralDfa6_0(active0, 67108864L);
            }
        }
    }

    private int jjMoveStringLiteralDfa6_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(4, old0, 0L);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var6) {
                this.jjStopStringLiteralDfa_0(5, active0, 0L);
                return 6;
            }

            switch(this.curChar) {
                case 'C':
                case 'c':
                    return this.jjMoveStringLiteralDfa7_0(active0, 70643622084608L);
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'J':
                case 'K':
                case 'L':
                case 'P':
                case 'Q':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '[':
                case '\\':
                case ']':
                case '^':
                case '_':
                case '`':
                case 'a':
                case 'b':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'j':
                case 'k':
                case 'l':
                case 'p':
                case 'q':
                default:
                    break;
                case 'H':
                case 'h':
                    if ((active0 & 68719476736L) != 0L) {
                        return this.jjStartNfaWithStates_0(6, 36, 15);
                    }

                    return this.jjMoveStringLiteralDfa7_0(active0, 32768L);
                case 'I':
                case 'i':
                    return this.jjMoveStringLiteralDfa7_0(active0, 3377699720544256L);
                case 'M':
                case 'm':
                    if ((active0 & 144115188075855872L) != 0L) {
                        return this.jjStartNfaWithStates_0(6, 57, 15);
                    }
                    break;
                case 'N':
                case 'n':
                    if ((active0 & 524288L) != 0L) {
                        return this.jjStartNfaWithStates_0(6, 19, 15);
                    }

                    if ((active0 & 4398046511104L) != 0L) {
                        return this.jjStartNfaWithStates_0(6, 42, 15);
                    }

                    return this.jjMoveStringLiteralDfa7_0(active0, 72058693549555712L);
                case 'O':
                case 'o':
                    return this.jjMoveStringLiteralDfa7_0(active0, 1048576L);
                case 'R':
                case 'r':
                    return this.jjMoveStringLiteralDfa7_0(active0, 512L);
                case 'S':
                case 's':
                    if ((active0 & 2048L) != 0L) {
                        return this.jjStartNfaWithStates_0(6, 11, 15);
                    }

                    if ((active0 & 33554432L) != 0L) {
                        return this.jjStartNfaWithStates_0(6, 25, 15);
                    }

                    if ((active0 & 67108864L) != 0L) {
                        return this.jjStartNfaWithStates_0(6, 26, 15);
                    }
                    break;
                case 'T':
                case 't':
                    return this.jjMoveStringLiteralDfa7_0(active0, 34359738368L);
                case 'U':
                case 'u':
                    return this.jjMoveStringLiteralDfa7_0(active0, 844424930131968L);
            }

            return this.jjStartNfa_0(5, active0, 0L);
        }
    }

    private int jjMoveStringLiteralDfa7_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(5, old0, 0L);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var6) {
                this.jjStopStringLiteralDfa_0(6, active0, 0L);
                return 7;
            }

            switch(this.curChar) {
                case 'A':
                case 'a':
                    return this.jjMoveStringLiteralDfa8_0(active0, 844424930131968L);
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'H':
                case 'J':
                case 'L':
                case 'M':
                case 'N':
                case 'P':
                case 'Q':
                case 'U':
                case 'V':
                case 'X':
                case 'Y':
                case 'Z':
                case '[':
                case '\\':
                case ']':
                case '^':
                case '_':
                case '`':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'h':
                case 'j':
                case 'l':
                case 'm':
                case 'n':
                case 'p':
                case 'q':
                case 'u':
                case 'v':
                default:
                    break;
                case 'G':
                case 'g':
                    if ((active0 & 72057594037927936L) != 0L) {
                        return this.jjStartNfaWithStates_0(7, 56, 15);
                    }

                    return this.jjMoveStringLiteralDfa8_0(active0, 3377699720527872L);
                case 'I':
                case 'i':
                    return this.jjMoveStringLiteralDfa8_0(active0, 34359738368L);
                case 'K':
                case 'k':
                    if ((active0 & 274877906944L) != 0L) {
                        return this.jjStartNfaWithStates_0(7, 38, 15);
                    }
                    break;
                case 'O':
                case 'o':
                    return this.jjMoveStringLiteralDfa8_0(active0, 16384L);
                case 'R':
                case 'r':
                    return this.jjMoveStringLiteralDfa8_0(active0, 32768L);
                case 'S':
                case 's':
                    if ((active0 & 512L) != 0L) {
                        return this.jjStartNfaWithStates_0(7, 9, 15);
                    }

                    if ((active0 & 1099511627776L) != 0L) {
                        return this.jjStartNfaWithStates_0(7, 40, 15);
                    }
                    break;
                case 'T':
                case 't':
                    return this.jjMoveStringLiteralDfa8_0(active0, 70368744177664L);
                case 'W':
                case 'w':
                    return this.jjMoveStringLiteralDfa8_0(active0, 1048576L);
            }

            return this.jjStartNfa_0(6, active0, 0L);
        }
    }

    private int jjMoveStringLiteralDfa8_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(6, old0, 0L);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var6) {
                this.jjStopStringLiteralDfa_0(7, active0, 0L);
                return 8;
            }

            switch(this.curChar) {
                case 'E':
                case 'e':
                    return this.jjMoveStringLiteralDfa9_0(active0, 34359738368L);
                case 'H':
                case 'h':
                    return this.jjMoveStringLiteralDfa9_0(active0, 3377699720527872L);
                case 'I':
                case 'i':
                    return this.jjMoveStringLiteralDfa9_0(active0, 70368744177664L);
                case 'L':
                case 'l':
                    return this.jjMoveStringLiteralDfa9_0(active0, 844424930131968L);
                case 'N':
                case 'n':
                    if ((active0 & 16384L) != 0L) {
                        return this.jjStartNfaWithStates_0(8, 14, 15);
                    }
                case 'F':
                case 'G':
                case 'J':
                case 'K':
                case 'M':
                case 'P':
                case 'Q':
                case 'R':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '[':
                case '\\':
                case ']':
                case '^':
                case '_':
                case '`':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'f':
                case 'g':
                case 'j':
                case 'k':
                case 'm':
                case 'p':
                case 'q':
                case 'r':
                default:
                    return this.jjStartNfa_0(7, active0, 0L);
                case 'O':
                case 'o':
                    return this.jjMoveStringLiteralDfa9_0(active0, 32768L);
                case 'S':
                case 's':
                    return this.jjMoveStringLiteralDfa9_0(active0, 1048576L);
            }
        }
    }

    private int jjMoveStringLiteralDfa9_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(7, old0, 0L);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var6) {
                this.jjStopStringLiteralDfa_0(8, active0, 0L);
                return 9;
            }

            switch(this.curChar) {
                case 'E':
                case 'e':
                    return this.jjMoveStringLiteralDfa10_0(active0, 1048576L);
                case 'O':
                case 'o':
                    return this.jjMoveStringLiteralDfa10_0(active0, 70368744177664L);
                case 'S':
                case 's':
                    if ((active0 & 34359738368L) != 0L) {
                        return this.jjStartNfaWithStates_0(9, 35, 15);
                    }
                default:
                    return this.jjStartNfa_0(8, active0, 0L);
                case 'T':
                case 't':
                    return this.jjMoveStringLiteralDfa10_0(active0, 3377699720527872L);
                case 'U':
                case 'u':
                    return this.jjMoveStringLiteralDfa10_0(active0, 32768L);
                case '_':
                    return this.jjMoveStringLiteralDfa10_0(active0, 844424930131968L);
            }
        }
    }

    private int jjMoveStringLiteralDfa10_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(8, old0, 0L);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var6) {
                this.jjStopStringLiteralDfa_0(9, active0, 0L);
                return 10;
            }

            switch(this.curChar) {
                case 'A':
                case 'a':
                    return this.jjMoveStringLiteralDfa11_0(active0, 281474976710656L);
                case 'E':
                case 'e':
                    return this.jjMoveStringLiteralDfa11_0(active0, 3377699720527872L);
                case 'G':
                case 'g':
                    return this.jjMoveStringLiteralDfa11_0(active0, 32768L);
                case 'I':
                case 'i':
                    return this.jjMoveStringLiteralDfa11_0(active0, 562949953421312L);
                case 'N':
                case 'n':
                    if ((active0 & 70368744177664L) != 0L) {
                        return this.jjStartNfaWithStates_0(10, 46, 15);
                    }
                    break;
                case 'T':
                case 't':
                    if ((active0 & 1048576L) != 0L) {
                        return this.jjStartNfaWithStates_0(10, 20, 15);
                    }
            }

            return this.jjStartNfa_0(9, active0, 0L);
        }
    }

    private int jjMoveStringLiteralDfa11_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(9, old0, 0L);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var6) {
                this.jjStopStringLiteralDfa_0(10, active0, 0L);
                return 11;
            }

            switch(this.curChar) {
                case 'D':
                case 'd':
                    return this.jjMoveStringLiteralDfa12_0(active0, 3377699720527872L);
                case 'H':
                case 'h':
                    if ((active0 & 32768L) != 0L) {
                        return this.jjStartNfaWithStates_0(11, 15, 15);
                    }
                default:
                    return this.jjStartNfa_0(10, active0, 0L);
                case 'L':
                case 'l':
                    return this.jjMoveStringLiteralDfa12_0(active0, 281474976710656L);
                case 'N':
                case 'n':
                    return this.jjMoveStringLiteralDfa12_0(active0, 562949953421312L);
            }
        }
    }

    private int jjMoveStringLiteralDfa12_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(10, old0, 0L);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var6) {
                this.jjStopStringLiteralDfa_0(11, active0, 0L);
                return 12;
            }

            switch(this.curChar) {
                case 'C':
                case 'c':
                    return this.jjMoveStringLiteralDfa13_0(active0, 562949953421312L);
                case 'L':
                case 'l':
                    return this.jjMoveStringLiteralDfa13_0(active0, 281474976710656L);
                case '_':
                    return this.jjMoveStringLiteralDfa13_0(active0, 3377699720527872L);
                default:
                    return this.jjStartNfa_0(11, active0, 0L);
            }
        }
    }

    private int jjMoveStringLiteralDfa13_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(11, old0, 0L);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var6) {
                this.jjStopStringLiteralDfa_0(12, active0, 0L);
                return 13;
            }

            switch(this.curChar) {
                case 'A':
                case 'a':
                    return this.jjMoveStringLiteralDfa14_0(active0, 1125899906842624L);
                case 'I':
                case 'i':
                    return this.jjMoveStringLiteralDfa14_0(active0, 2251799813685248L);
                case 'O':
                case 'o':
                    return this.jjMoveStringLiteralDfa14_0(active0, 281474976710656L);
                case 'R':
                case 'r':
                    return this.jjMoveStringLiteralDfa14_0(active0, 562949953421312L);
                default:
                    return this.jjStartNfa_0(12, active0, 0L);
            }
        }
    }

    private int jjMoveStringLiteralDfa14_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(12, old0, 0L);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var6) {
                this.jjStopStringLiteralDfa_0(13, active0, 0L);
                return 14;
            }

            switch(this.curChar) {
                case 'C':
                case 'c':
                    return this.jjMoveStringLiteralDfa15_0(active0, 281474976710656L);
                case 'E':
                case 'e':
                    return this.jjMoveStringLiteralDfa15_0(active0, 562949953421312L);
                case 'L':
                case 'l':
                    return this.jjMoveStringLiteralDfa15_0(active0, 1125899906842624L);
                case 'N':
                case 'n':
                    return this.jjMoveStringLiteralDfa15_0(active0, 2251799813685248L);
                default:
                    return this.jjStartNfa_0(13, active0, 0L);
            }
        }
    }

    private int jjMoveStringLiteralDfa15_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(13, old0, 0L);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var6) {
                this.jjStopStringLiteralDfa_0(14, active0, 0L);
                return 15;
            }

            switch(this.curChar) {
                case 'A':
                case 'a':
                    return this.jjMoveStringLiteralDfa16_0(active0, 281474976710656L);
                case 'C':
                case 'c':
                    return this.jjMoveStringLiteralDfa16_0(active0, 2251799813685248L);
                case 'L':
                case 'l':
                    return this.jjMoveStringLiteralDfa16_0(active0, 1125899906842624L);
                case 'M':
                case 'm':
                    return this.jjMoveStringLiteralDfa16_0(active0, 562949953421312L);
                default:
                    return this.jjStartNfa_0(14, active0, 0L);
            }
        }
    }

    private int jjMoveStringLiteralDfa16_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(14, old0, 0L);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var6) {
                this.jjStopStringLiteralDfa_0(15, active0, 0L);
                return 16;
            }

            switch(this.curChar) {
                case 'E':
                case 'e':
                    return this.jjMoveStringLiteralDfa17_0(active0, 562949953421312L);
                case 'O':
                case 'o':
                    return this.jjMoveStringLiteralDfa17_0(active0, 1125899906842624L);
                case 'R':
                case 'r':
                    return this.jjMoveStringLiteralDfa17_0(active0, 2251799813685248L);
                case 'T':
                case 't':
                    return this.jjMoveStringLiteralDfa17_0(active0, 281474976710656L);
                default:
                    return this.jjStartNfa_0(15, active0, 0L);
            }
        }
    }

    private int jjMoveStringLiteralDfa17_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(15, old0, 0L);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var6) {
                this.jjStopStringLiteralDfa_0(16, active0, 0L);
                return 17;
            }

            switch(this.curChar) {
                case 'C':
                case 'c':
                    return this.jjMoveStringLiteralDfa18_0(active0, 1125899906842624L);
                case 'E':
                case 'e':
                    return this.jjMoveStringLiteralDfa18_0(active0, 2251799813685248L);
                case 'I':
                case 'i':
                    return this.jjMoveStringLiteralDfa18_0(active0, 281474976710656L);
                case 'N':
                case 'n':
                    return this.jjMoveStringLiteralDfa18_0(active0, 562949953421312L);
                default:
                    return this.jjStartNfa_0(16, active0, 0L);
            }
        }
    }

    private int jjMoveStringLiteralDfa18_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(16, old0, 0L);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var6) {
                this.jjStopStringLiteralDfa_0(17, active0, 0L);
                return 18;
            }

            switch(this.curChar) {
                case 'A':
                case 'a':
                    return this.jjMoveStringLiteralDfa19_0(active0, 1125899906842624L);
                case 'M':
                case 'm':
                    return this.jjMoveStringLiteralDfa19_0(active0, 2251799813685248L);
                case 'O':
                case 'o':
                    return this.jjMoveStringLiteralDfa19_0(active0, 281474976710656L);
                case 'T':
                case 't':
                    if ((active0 & 562949953421312L) != 0L) {
                        return this.jjStartNfaWithStates_0(18, 49, 15);
                    }
                default:
                    return this.jjStartNfa_0(17, active0, 0L);
            }
        }
    }

    private int jjMoveStringLiteralDfa19_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(17, old0, 0L);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var6) {
                this.jjStopStringLiteralDfa_0(18, active0, 0L);
                return 19;
            }

            switch(this.curChar) {
                case 'E':
                case 'e':
                    return this.jjMoveStringLiteralDfa20_0(active0, 2251799813685248L);
                case 'N':
                case 'n':
                    if ((active0 & 281474976710656L) != 0L) {
                        return this.jjStartNfaWithStates_0(19, 48, 15);
                    }
                default:
                    return this.jjStartNfa_0(18, active0, 0L);
                case 'T':
                case 't':
                    return this.jjMoveStringLiteralDfa20_0(active0, 1125899906842624L);
            }
        }
    }

    private int jjMoveStringLiteralDfa20_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(18, old0, 0L);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var6) {
                this.jjStopStringLiteralDfa_0(19, active0, 0L);
                return 20;
            }

            switch(this.curChar) {
                case 'I':
                case 'i':
                    return this.jjMoveStringLiteralDfa21_0(active0, 1125899906842624L);
                case 'N':
                case 'n':
                    return this.jjMoveStringLiteralDfa21_0(active0, 2251799813685248L);
                default:
                    return this.jjStartNfa_0(19, active0, 0L);
            }
        }
    }

    private int jjMoveStringLiteralDfa21_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(19, old0, 0L);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var6) {
                this.jjStopStringLiteralDfa_0(20, active0, 0L);
                return 21;
            }

            switch(this.curChar) {
                case 'O':
                case 'o':
                    return this.jjMoveStringLiteralDfa22_0(active0, 1125899906842624L);
                case 'T':
                case 't':
                    if ((active0 & 2251799813685248L) != 0L) {
                        return this.jjStartNfaWithStates_0(21, 51, 15);
                    }
                default:
                    return this.jjStartNfa_0(20, active0, 0L);
            }
        }
    }

    private int jjMoveStringLiteralDfa22_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(20, old0, 0L);
        } else {
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var6) {
                this.jjStopStringLiteralDfa_0(21, active0, 0L);
                return 22;
            }

            switch(this.curChar) {
                case 'N':
                case 'n':
                    if ((active0 & 1125899906842624L) != 0L) {
                        return this.jjStartNfaWithStates_0(22, 50, 15);
                    }
                default:
                    return this.jjStartNfa_0(21, active0, 0L);
            }
        }
    }

    private int jjStartNfaWithStates_0(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException var5) {
            return pos + 1;
        }

        return this.jjMoveNfa_0(state, pos + 1);
    }

    private int jjMoveNfa_0(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 44;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;

        while(true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }

            long l;
            if (this.curChar < '@') {
                l = 1L << this.curChar;

                do {
                    --i;
                    switch(this.jjstateSet[i]) {
                        case 0:
                            if (this.curChar == '*') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                            }
                            break;
                        case 1:
                            if ((-140737488355329L & l) != 0L && kind > 63) {
                                kind = 63;
                            }
                            break;
                        case 2:
                            if (this.curChar == '*') {
                                this.jjstateSet[this.jjnewStateCnt++] = 0;
                            }
                            break;
                        case 3:
                            if ((287948901175001088L & l) != 0L) {
                                if (kind > 91) {
                                    kind = 91;
                                }

                                this.jjCheckNAddStates(0, 6);
                            } else if (this.curChar == '&') {
                                this.jjAddStates(7, 8);
                            } else if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(34, 35);
                            } else if (this.curChar == '$') {
                                if (kind > 98) {
                                    kind = 98;
                                }

                                this.jjCheckNAdd(15);
                            } else if (this.curChar == '"') {
                                this.jjCheckNAddStates(9, 11);
                            } else if (this.curChar == '\'') {
                                this.jjCheckNAddStates(12, 14);
                            } else if (this.curChar == '/') {
                                this.jjstateSet[this.jjnewStateCnt++] = 2;
                            }
                            break;
                        case 4:
                        case 6:
                            if (this.curChar == '\'') {
                                this.jjCheckNAddStates(12, 14);
                            }
                            break;
                        case 5:
                            if ((-549755813889L & l) != 0L) {
                                this.jjCheckNAddStates(12, 14);
                            }
                            break;
                        case 7:
                            if (this.curChar == '\'') {
                                this.jjstateSet[this.jjnewStateCnt++] = 6;
                            }
                            break;
                        case 8:
                            if (this.curChar == '\'' && kind > 95) {
                                kind = 95;
                            }
                            break;
                        case 9:
                        case 11:
                            if (this.curChar == '"') {
                                this.jjCheckNAddStates(9, 11);
                            }
                            break;
                        case 10:
                            if ((-17179869185L & l) != 0L) {
                                this.jjCheckNAddStates(9, 11);
                            }
                            break;
                        case 12:
                            if (this.curChar == '"') {
                                this.jjstateSet[this.jjnewStateCnt++] = 11;
                            }
                            break;
                        case 13:
                            if (this.curChar == '"' && kind > 96) {
                                kind = 96;
                            }
                            break;
                        case 14:
                            if (this.curChar == '$') {
                                if (kind > 98) {
                                    kind = 98;
                                }

                                this.jjCheckNAdd(15);
                            }
                            break;
                        case 15:
                            if ((287948969894477824L & l) != 0L) {
                                if (kind > 98) {
                                    kind = 98;
                                }

                                this.jjCheckNAdd(15);
                            }
                        case 16:
                        case 18:
                        case 19:
                        case 20:
                        case 24:
                        case 37:
                        case 39:
                        case 40:
                        case 41:
                        case 42:
                        default:
                            break;
                        case 17:
                            if ((-9217L & l) != 0L) {
                                this.jjAddStates(15, 17);
                            }
                            break;
                        case 21:
                            if ((287948901175001088L & l) != 0L) {
                                if (kind > 91) {
                                    kind = 91;
                                }

                                this.jjCheckNAddStates(0, 6);
                            }
                            break;
                        case 22:
                            if ((287948901175001088L & l) != 0L) {
                                if (kind > 91) {
                                    kind = 91;
                                }

                                this.jjCheckNAdd(22);
                            }
                            break;
                        case 23:
                            if ((287948901175001088L & l) != 0L) {
                                this.jjCheckNAddTwoStates(23, 24);
                            }
                            break;
                        case 25:
                            if ((43980465111040L & l) != 0L) {
                                this.jjCheckNAdd(26);
                            }
                            break;
                        case 26:
                            if ((287948901175001088L & l) != 0L) {
                                if (kind > 92) {
                                    kind = 92;
                                }

                                this.jjCheckNAdd(26);
                            }
                            break;
                        case 27:
                            if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(28, 24);
                            }
                            break;
                        case 28:
                            if ((287948901175001088L & l) != 0L) {
                                this.jjCheckNAddTwoStates(28, 24);
                            }
                            break;
                        case 29:
                            if (this.curChar == '.') {
                                if (kind > 93) {
                                    kind = 93;
                                }

                                this.jjCheckNAdd(30);
                            }
                            break;
                        case 30:
                            if ((287948901175001088L & l) != 0L) {
                                if (kind > 93) {
                                    kind = 93;
                                }

                                this.jjCheckNAdd(30);
                            }
                            break;
                        case 31:
                            if ((287948901175001088L & l) != 0L) {
                                if (kind > 93) {
                                    kind = 93;
                                }

                                this.jjCheckNAddStates(18, 20);
                            }
                            break;
                        case 32:
                            if ((287948901175001088L & l) != 0L) {
                                this.jjCheckNAddStates(21, 24);
                            }
                            break;
                        case 33:
                            if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(34, 35);
                            }
                            break;
                        case 34:
                            if ((287948901175001088L & l) != 0L) {
                                this.jjCheckNAddTwoStates(34, 24);
                            }
                            break;
                        case 35:
                            if ((287948901175001088L & l) != 0L) {
                                if (kind > 93) {
                                    kind = 93;
                                }

                                this.jjCheckNAdd(35);
                            }
                            break;
                        case 36:
                            if (this.curChar == '&') {
                                this.jjAddStates(7, 8);
                            }
                            break;
                        case 38:
                            if ((-9217L & l) != 0L) {
                                this.jjAddStates(25, 27);
                            }
                            break;
                        case 43:
                            if ((287948969894477824L & l) != 0L) {
                                if (kind > 101) {
                                    kind = 101;
                                }

                                this.jjstateSet[this.jjnewStateCnt++] = 43;
                            }
                            break;
                        case 44:
                            if ((287948901175001088L & l) != 0L) {
                                if (kind > 93) {
                                    kind = 93;
                                }

                                this.jjCheckNAdd(35);
                            }

                            if ((287948901175001088L & l) != 0L) {
                                this.jjCheckNAddTwoStates(34, 24);
                            }
                    }
                } while(i != startsAt);
            } else if (this.curChar < 128) {
                l = 1L << (this.curChar & 63);

                do {
                    --i;
                    switch(this.jjstateSet[i]) {
                        case 1:
                            if (kind > 63) {
                                kind = 63;
                            }
                        case 2:
                        case 4:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 11:
                        case 12:
                        case 13:
                        case 21:
                        case 22:
                        case 23:
                        case 25:
                        case 26:
                        case 27:
                        case 28:
                        case 29:
                        case 30:
                        case 31:
                        case 32:
                        case 33:
                        case 34:
                        case 35:
                        case 36:
                        default:
                            break;
                        case 3:
                            if ((576460745995190270L & l) != 0L) {
                                if (kind > 98) {
                                    kind = 98;
                                }

                                this.jjCheckNAdd(15);
                            } else if (this.curChar == '[') {
                                this.jjCheckNAddStates(15, 17);
                            }
                            break;
                        case 5:
                            this.jjAddStates(12, 14);
                            break;
                        case 10:
                            this.jjAddStates(9, 11);
                            break;
                        case 14:
                        case 15:
                            if ((576460745995190270L & l) != 0L) {
                                if (kind > 98) {
                                    kind = 98;
                                }

                                this.jjCheckNAdd(15);
                            }
                            break;
                        case 16:
                            if (this.curChar == '[') {
                                this.jjCheckNAddStates(15, 17);
                            }
                            break;
                        case 17:
                            if ((-536870913L & l) != 0L) {
                                this.jjCheckNAddStates(15, 17);
                            }
                            break;
                        case 18:
                            if (this.curChar == ']') {
                                this.jjCheckNAddStates(15, 17);
                            }
                            break;
                        case 19:
                            if (this.curChar == ']') {
                                this.jjstateSet[this.jjnewStateCnt++] = 18;
                            }
                            break;
                        case 20:
                            if (this.curChar == ']' && kind > 99) {
                                kind = 99;
                            }
                            break;
                        case 24:
                            if ((137438953504L & l) != 0L) {
                                this.jjAddStates(28, 29);
                            }
                            break;
                        case 37:
                            if (this.curChar == '[') {
                                this.jjCheckNAddStates(25, 27);
                            }
                            break;
                        case 38:
                            if ((-536870913L & l) != 0L) {
                                this.jjCheckNAddStates(25, 27);
                            }
                            break;
                        case 39:
                            if (this.curChar == ']') {
                                this.jjCheckNAddStates(25, 27);
                            }
                            break;
                        case 40:
                            if (this.curChar == ']') {
                                this.jjstateSet[this.jjnewStateCnt++] = 39;
                            }
                            break;
                        case 41:
                            if (this.curChar == ']' && kind > 100) {
                                kind = 100;
                            }
                            break;
                        case 42:
                            if ((576460743847706622L & l) != 0L) {
                                if (kind > 101) {
                                    kind = 101;
                                }

                                this.jjCheckNAdd(43);
                            }
                            break;
                        case 43:
                            if ((576460745995190270L & l) != 0L) {
                                if (kind > 101) {
                                    kind = 101;
                                }

                                this.jjCheckNAdd(43);
                            }
                    }
                } while(i != startsAt);
            } else {
                int hiByte = this.curChar >> 8;
                int i1 = hiByte >> 6;
                long l1 = 1L << (hiByte & 63);
                int i2 = (this.curChar & 255) >> 6;
                long l2 = 1L << (this.curChar & 63);

                do {
                    --i;
                    switch(this.jjstateSet[i]) {
                        case 1:
                            if (jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 63) {
                                kind = 63;
                            }
                            break;
                        case 3:
                        case 15:
                            if (jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                if (kind > 98) {
                                    kind = 98;
                                }

                                this.jjCheckNAdd(15);
                            }
                            break;
                        case 5:
                            if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                                this.jjAddStates(12, 14);
                            }
                            break;
                        case 10:
                            if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                                this.jjAddStates(9, 11);
                            }
                            break;
                        case 17:
                            if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                                this.jjAddStates(15, 17);
                            }
                            break;
                        case 38:
                            if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                                this.jjAddStates(25, 27);
                            }
                            break;
                        case 43:
                            if (jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                if (kind > 101) {
                                    kind = 101;
                                }

                                this.jjstateSet[this.jjnewStateCnt++] = 43;
                            }
                    }
                } while(i != startsAt);
            }

            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }

            ++curPos;
            if ((i = this.jjnewStateCnt) == (startsAt = 44 - (this.jjnewStateCnt = startsAt))) {
                return curPos;
            }

            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var13) {
                return curPos;
            }
        }
    }

    private int jjMoveStringLiteralDfa0_3() {
        switch(this.curChar) {
            case '*':
                return this.jjMoveStringLiteralDfa1_3(32L);
            default:
                return 1;
        }
    }

    private int jjMoveStringLiteralDfa1_3(long active1) {
        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException var4) {
            return 1;
        }

        switch(this.curChar) {
            case '/':
                if ((active1 & 32L) != 0L) {
                    return this.jjStopAtPos(1, 69);
                }

                return 2;
            default:
                return 2;
        }
    }

    private int jjMoveStringLiteralDfa0_1() {
        return this.jjMoveNfa_1(0, 0);
    }

    private int jjMoveNfa_1(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 3;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;

        while(true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }

            long l;
            if (this.curChar < '@') {
                l = 1L << this.curChar;

                do {
                    --i;
                    switch(this.jjstateSet[i]) {
                        case 0:
                            if ((9216L & l) != 0L && kind > 67) {
                                kind = 67;
                            }

                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                            }
                            break;
                        case 1:
                            if (this.curChar == '\n' && kind > 67) {
                                kind = 67;
                            }
                            break;
                        case 2:
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                            }
                    }
                } while(i != startsAt);
            } else if (this.curChar < 128) {
                l = 1L << (this.curChar & 63);

                do {
                    --i;
                    switch(this.jjstateSet[i]) {
                    }
                } while(i != startsAt);
            } else {
                int hiByte = this.curChar >> 8;
                int i1 = hiByte >> 6;
                long l1 = 1L << (hiByte & 63);
                int i2 = (this.curChar & 255) >> 6;
                long var11 = 1L << (this.curChar & 63);

                do {
                    --i;
                    switch(this.jjstateSet[i]) {
                    }
                } while(i != startsAt);
            }

            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }

            ++curPos;
            if ((i = this.jjnewStateCnt) == (startsAt = 3 - (this.jjnewStateCnt = startsAt))) {
                return curPos;
            }

            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException var13) {
                return curPos;
            }
        }
    }

    private int jjMoveStringLiteralDfa0_2() {
        switch(this.curChar) {
            case '*':
                return this.jjMoveStringLiteralDfa1_2(16L);
            default:
                return 1;
        }
    }

    private int jjMoveStringLiteralDfa1_2(long active1) {
        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException var4) {
            return 1;
        }

        switch(this.curChar) {
            case '/':
                if ((active1 & 16L) != 0L) {
                    return this.jjStopAtPos(1, 68);
                }

                return 2;
            default:
                return 2;
        }
    }

    private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
        switch(hiByte) {
            case 0:
                return (jjbitVec2[i2] & l2) != 0L;
            default:
                return (jjbitVec0[i1] & l1) != 0L;
        }
    }

    private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2) {
        switch(hiByte) {
            case 0:
                return (jjbitVec4[i2] & l2) != 0L;
            case 45:
                return (jjbitVec5[i2] & l2) != 0L;
            case 48:
                return (jjbitVec6[i2] & l2) != 0L;
            case 49:
                return (jjbitVec7[i2] & l2) != 0L;
            case 51:
                return (jjbitVec8[i2] & l2) != 0L;
            case 61:
                return (jjbitVec9[i2] & l2) != 0L;
            default:
                return (jjbitVec3[i1] & l1) != 0L;
        }
    }

    public MdxParserImplTokenManager(SimpleCharStream stream) {
        this.debugStream = System.out;
        this.jjrounds = new int[44];
        this.jjstateSet = new int[88];
        this.jjimage = new StringBuilder();
        this.image = this.jjimage;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.input_stream = stream;
    }

    public MdxParserImplTokenManager(SimpleCharStream stream, int lexState) {
        this(stream);
        this.SwitchTo(lexState);
    }

    public void ReInit(SimpleCharStream stream) {
        this.jjmatchedPos = this.jjnewStateCnt = 0;
        this.curLexState = this.defaultLexState;
        this.input_stream = stream;
        this.ReInitRounds();
    }

    private void ReInitRounds() {
        this.jjround = -2147483647;

        for(int i = 44; i-- > 0; this.jjrounds[i] = Integer.MIN_VALUE) {
        }

    }

    public void ReInit(SimpleCharStream stream, int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }

    public void SwitchTo(int lexState) {
        if (lexState < 4 && lexState >= 0) {
            this.curLexState = lexState;
        } else {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
    }

    protected Token jjFillToken() {
        String im = jjstrLiteralImages[this.jjmatchedKind];
        String curTokenImage = im == null ? this.input_stream.GetImage() : im;
        int beginLine = this.input_stream.getBeginLine();
        int beginColumn = this.input_stream.getBeginColumn();
        int endLine = this.input_stream.getEndLine();
        int endColumn = this.input_stream.getEndColumn();
        Token t = Token.newToken(this.jjmatchedKind, curTokenImage);
        t.beginLine = beginLine;
        t.endLine = endLine;
        t.beginColumn = beginColumn;
        t.endColumn = endColumn;
        return t;
    }

    public Token getNextToken() {
        Token specialToken = null;
        int curPos = 0;

        label128:
        while(true) {
            Token matchedToken;
            try {
                this.curChar = this.input_stream.BeginToken();
            } catch (IOException var9) {
                this.jjmatchedKind = 0;
                matchedToken = this.jjFillToken();
                matchedToken.specialToken = specialToken;
                return matchedToken;
            }

            this.image = this.jjimage;
            this.image.setLength(0);
            this.jjimageLen = 0;

            while(true) {
                switch(this.curLexState) {
                    case 0:
                        try {
                            this.input_stream.backup(0);

                            while(this.curChar <= ' ' && (4294981120L & 1L << this.curChar) != 0L) {
                                this.curChar = this.input_stream.BeginToken();
                            }
                        } catch (IOException var12) {
                            continue label128;
                        }

                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_0();
                        break;
                    case 1:
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_1();
                        if (this.jjmatchedPos == 0 && this.jjmatchedKind > 70) {
                            this.jjmatchedKind = 70;
                        }
                        break;
                    case 2:
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_2();
                        if (this.jjmatchedPos == 0 && this.jjmatchedKind > 70) {
                            this.jjmatchedKind = 70;
                        }
                        break;
                    case 3:
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_3();
                        if (this.jjmatchedPos == 0 && this.jjmatchedKind > 70) {
                            this.jjmatchedKind = 70;
                        }
                }

                if (this.jjmatchedKind == Integer.MAX_VALUE) {
                    break label128;
                }

                if (this.jjmatchedPos + 1 < curPos) {
                    this.input_stream.backup(curPos - this.jjmatchedPos - 1);
                }

                if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
                    matchedToken = this.jjFillToken();
                    matchedToken.specialToken = specialToken;
                    if (jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = jjnewLexState[this.jjmatchedKind];
                    }

                    return matchedToken;
                }

                if ((jjtoSkip[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
                    if ((jjtoSpecial[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
                        matchedToken = this.jjFillToken();
                        if (specialToken == null) {
                            specialToken = matchedToken;
                        } else {
                            matchedToken.specialToken = specialToken;
                            specialToken = specialToken.next = matchedToken;
                        }

                        this.SkipLexicalActions(matchedToken);
                    } else {
                        this.SkipLexicalActions((Token)null);
                    }

                    if (jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = jjnewLexState[this.jjmatchedKind];
                    }
                    break;
                }

                this.jjimageLen += this.jjmatchedPos + 1;
                if (jjnewLexState[this.jjmatchedKind] != -1) {
                    this.curLexState = jjnewLexState[this.jjmatchedKind];
                }

                curPos = 0;
                this.jjmatchedKind = Integer.MAX_VALUE;

                try {
                    this.curChar = this.input_stream.readChar();
                } catch (IOException var11) {
                    break label128;
                }
            }
        }

        int error_line = this.input_stream.getEndLine();
        int error_column = this.input_stream.getEndColumn();
        String error_after = null;
        boolean EOFSeen = false;

        try {
            this.input_stream.readChar();
            this.input_stream.backup(1);
        } catch (IOException var10) {
            EOFSeen = true;
            error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
            if (this.curChar != '\n' && this.curChar != '\r') {
                ++error_column;
            } else {
                ++error_line;
                error_column = 0;
            }
        }

        if (!EOFSeen) {
            this.input_stream.backup(1);
            error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
        }

        throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
    }

    void SkipLexicalActions(Token matchedToken) {
        switch(this.jjmatchedKind) {
            default:
        }
    }

    private void jjCheckNAdd(int state) {
        if (this.jjrounds[state] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = state;
            this.jjrounds[state] = this.jjround;
        }

    }

    private void jjAddStates(int start, int end) {
        do {
            this.jjstateSet[this.jjnewStateCnt++] = jjnextStates[start];
        } while(start++ != end);

    }

    private void jjCheckNAddTwoStates(int state1, int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }

    private void jjCheckNAddStates(int start, int end) {
        do {
            this.jjCheckNAdd(jjnextStates[start]);
        } while(start++ != end);

    }
}