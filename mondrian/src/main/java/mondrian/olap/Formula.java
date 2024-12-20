/* Decompiler 417ms, total 1014ms, lines 463 */
package mondrian.olap;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import mondrian.mdx.MdxVisitor;
import mondrian.mdx.MdxVisitorImpl;
import mondrian.mdx.MemberExpr;
import mondrian.olap.Id.NameSegment;
import mondrian.olap.Id.Segment;
import mondrian.olap.type.DecimalType;
import mondrian.olap.type.NumericType;
import mondrian.olap.type.Type;
import mondrian.olap.type.TypeUtil;
import mondrian.resource.MondrianResource;
import mondrian.rolap.RolapCalculatedMember;

public class Formula extends QueryPart {
    private final Id id;
    private Exp exp;
    private final MemberProperty[] memberProperties;
    private final boolean isMember;
    private Member mdxMember;
    private NamedSet mdxSet;

    public Formula(Id id, Exp exp) {
        this(false, id, exp, new MemberProperty[0], (Member)null, (NamedSet)null);
        this.createElement((Query)null);
    }

    public Formula(Id id, Exp exp, MemberProperty[] memberProperties) {
        this(true, id, exp, memberProperties, (Member)null, (NamedSet)null);
    }

    Formula(boolean isMember, Id id, Exp exp, MemberProperty[] memberProperties, Member mdxMember, NamedSet mdxSet) {
        this.isMember = isMember;
        this.id = id;
        this.exp = exp;
        this.memberProperties = memberProperties;
        this.mdxMember = mdxMember;
        this.mdxSet = mdxSet;

        assert isMember || mdxMember == null;

        assert !isMember || mdxSet == null;

    }

    public Object clone() {
        return new Formula(this.isMember, this.id, this.exp.clone(), MemberProperty.cloneArray(this.memberProperties), this.mdxMember, this.mdxSet);
    }

    static Formula[] cloneArray(Formula[] x) {
        Formula[] x2 = new Formula[x.length];

        for(int i = 0; i < x.length; ++i) {
            x2[i] = (Formula)x[i].clone();
        }

        return x2;
    }

    void accept(Validator validator) {
        boolean scalar = this.isMember;
        this.exp = validator.validate(this.exp, scalar);
        String id = this.id.toString();
        Type type = this.exp.getType();
        if (this.isMember) {
            if (!TypeUtil.canEvaluate(type)) {
                throw MondrianResource.instance().MdxMemberExpIsSet.ex(this.exp.toString());
            }
        } else if (!TypeUtil.isSet(type)) {
            throw MondrianResource.instance().MdxSetExpNotSet.ex(id);
        }

        MemberProperty[] var5 = this.memberProperties;
        int var6 = var5.length;

        int i;
        MemberProperty memberProperty;
        for(i = 0; i < var6; ++i) {
            memberProperty = var5[i];
            validator.validate(memberProperty);
        }

        if (this.isMember) {
            Exp formatExp = this.getFormatExp(validator);
            if (formatExp != null) {
                this.mdxMember.setProperty(Property.FORMAT_EXP_PARSED.name, formatExp);
                this.mdxMember.setProperty(Property.FORMAT_EXP.name, Util.unparse(formatExp));
            }

            List<MemberProperty> memberPropertyList = new ArrayList(Arrays.asList(this.memberProperties));

            for(i = 0; i < memberPropertyList.size(); ++i) {
                memberProperty = (MemberProperty)memberPropertyList.get(i);
                if (memberProperty.getName().equals(Property.CELL_FORMATTER_SCRIPT_LANGUAGE.name)) {
                    memberPropertyList.remove(i);
                    memberPropertyList.add(0, memberProperty);
                }
            }

            Iterator var13 = memberPropertyList.iterator();

            while(var13.hasNext()) {
                memberProperty = (MemberProperty)var13.next();
                if (!Property.FORMAT_PROPERTIES.contains(memberProperty.getName())) {
                    Exp exp = memberProperty.getExp();
                    if (exp instanceof Literal) {
                        String value = String.valueOf(((Literal)exp).getValue());
                        this.mdxMember.setProperty(memberProperty.getName(), value);
                    }
                }
            }
        }

    }

    void createElement(Query q) {
        List<Segment> segments = this.id.getSegments();
        if (!this.isMember) {
            Util.assertTrue(segments.size() == 1, "set names must not be compound");
            Segment segment0 = (Segment)segments.get(0);
            if (!(segment0 instanceof NameSegment)) {
                throw Util.newError("Calculated member name must not contain member keys");
            }

            this.mdxSet = new SetBase(((NameSegment)segment0).getName(), (String)null, (String)null, this.exp, false, Collections.emptyMap());
        } else {
            if (this.mdxMember != null) {
                return;
            }

            OlapElement mdxElement = q.getCube();
            SchemaReader schemaReader = q.getSchemaReader(false);
            int i = 0;

            while(true) {
                if (i >= segments.size()) {
                    this.mdxMember = (Member)mdxElement;
                    break;
                }

                Segment segment0 = (Segment)segments.get(i);
                if (!(segment0 instanceof NameSegment)) {
                    throw Util.newError("Calculated member name must not contain member keys");
                }

                NameSegment segment = (NameSegment)segment0;
                OlapElement parent = mdxElement;
                mdxElement = null;
                if (i != segments.size() - 1) {
                    mdxElement = schemaReader.getElementChild((OlapElement)parent, segment);
                }

                if (mdxElement == null || i == segments.size() - 1) {
                    Member parentMember = null;
                    Level level;
                    if (parent instanceof Member) {
                        parentMember = (Member)parent;
                        level = parentMember.getLevel().getChildLevel();
                        if (level == null) {
                            throw Util.newError("The '" + segment + "' calculated member cannot be created because its parent is at the lowest level in the " + parentMember.getHierarchy().getUniqueName() + " hierarchy.");
                        }
                    } else {
                        Hierarchy hierarchy;
                        if (parent instanceof Dimension && MondrianProperties.instance().SsasCompatibleNaming.get()) {
                            Dimension dimension = (Dimension)parent;
                            if (dimension.getHierarchies().length == 1) {
                                hierarchy = dimension.getHierarchies()[0];
                            } else {
                                hierarchy = null;
                            }
                        } else {
                            hierarchy = ((OlapElement)parent).getHierarchy();
                        }

                        if (hierarchy == null) {
                            throw MondrianResource.instance().MdxCalculatedHierarchyError.ex(this.id.toString());
                        }

                        level = hierarchy.getLevels()[0];
                    }

                    if (parentMember != null && parentMember.isCalculated()) {
                        throw Util.newError("The '" + parent + "' calculated member cannot be used as a parent of another calculated member.");
                    }

                    Member mdxMember = level.getHierarchy().createMember(parentMember, level, segment.getName(), this);

                    assert mdxMember != null;

                    mdxElement = mdxMember;
                }

                ++i;
            }
        }

    }

    public Object[] getChildren() {
        Object[] children = new Object[1 + this.memberProperties.length];
        children[0] = this.exp;
        System.arraycopy(this.memberProperties, 0, children, 1, this.memberProperties.length);
        return children;
    }

    public void unparse(PrintWriter pw) {
        if (this.isMember) {
            pw.print("member ");
            if (this.mdxMember != null) {
                pw.print(this.mdxMember.getUniqueName());
            } else {
                this.id.unparse(pw);
            }
        } else {
            pw.print("set ");
            this.id.unparse(pw);
        }

        pw.print(" as '");
        this.exp.unparse(pw);
        pw.print("'");
        if (this.memberProperties != null) {
            MemberProperty[] var2 = this.memberProperties;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                MemberProperty memberProperty = var2[var4];
                pw.print(", ");
                memberProperty.unparse(pw);
            }
        }

    }

    public boolean isMember() {
        return this.isMember;
    }

    public NamedSet getNamedSet() {
        return this.mdxSet;
    }

    public Id getIdentifier() {
        return this.id;
    }

    public String getName() {
        return this.isMember ? this.mdxMember.getName() : this.mdxSet.getName();
    }

    public String getCaption() {
        return this.isMember ? this.mdxMember.getCaption() : this.mdxSet.getName();
    }

    void rename(String newName) {
        String oldName = this.getElement().getName();
        List<Segment> segments = this.id.getSegments();

        assert Util.last(segments) instanceof NameSegment;

        assert ((NameSegment)Util.last(segments)).name.equalsIgnoreCase(oldName);

        segments.set(segments.size() - 1, new NameSegment(newName));
        if (this.isMember) {
            this.mdxMember.setName(newName);
        } else {
            this.mdxSet.setName(newName);
        }

    }

    String getUniqueName() {
        return this.isMember ? this.mdxMember.getUniqueName() : this.mdxSet.getUniqueName();
    }

    OlapElement getElement() {
        return (OlapElement)(this.isMember ? this.mdxMember : this.mdxSet);
    }

    public Exp getExpression() {
        return this.exp;
    }

    public Exp setExpression(Exp exp) {
        return this.exp = exp;
    }

    private Exp getMemberProperty(String name) {
        return MemberProperty.get(this.memberProperties, name);
    }

    public Member getMdxMember() {
        return this.mdxMember;
    }

    public Number getSolveOrder() {
        return this.getIntegerMemberProperty(Property.SOLVE_ORDER.name);
    }

    private Number getIntegerMemberProperty(String name) {
        Exp exp = this.getMemberProperty(name);
        return exp != null && exp.getType() instanceof NumericType ? quickEval(exp) : null;
    }

    private static Number quickEval(Exp exp) {
        if (exp instanceof Literal) {
            Literal literal = (Literal)exp;
            Object value = literal.getValue();
            return value instanceof Number ? (Number)value : null;
        } else {
            if (exp instanceof FunCall) {
                FunCall call = (FunCall)exp;
                if (call.getFunName().equals("-") && call.getSyntax() == Syntax.Prefix) {
                    Number number = quickEval(call.getArg(0));
                    if (number == null) {
                        return null;
                    }

                    if (number instanceof Integer) {
                        return -number.intValue();
                    }

                    return -number.doubleValue();
                }
            }

            return null;
        }
    }

    private Exp getFormatExp(Validator validator) {
        Iterator var2 = Property.FORMAT_PROPERTIES.iterator();

        while(var2.hasNext()) {
            String prop = (String)var2.next();
            Exp formatExp = this.getMemberProperty(prop);
            if (formatExp != null) {
                return formatExp;
            }
        }

        Type type = this.exp.getType();
        if (!(type instanceof DecimalType)) {
            if (!this.mdxMember.isMeasure()) {
                return null;
            } else {
                try {
                    this.exp.accept(new Formula.FormatFinder(validator));
                    return null;
                } catch (Formula.FoundOne var5) {
                    return var5.exp;
                }
            }
        } else {
            int scale = ((DecimalType)type).getScale();
            String formatString = "#,##0";
            if (scale > 0) {
                for(formatString = formatString + "."; scale-- > 0; formatString = formatString + "0") {
                }
            }

            return Literal.createString(formatString);
        }
    }

    public void compile() {
    }

    public Object accept(MdxVisitor visitor) {
        Object o = visitor.visit(this);
        if (visitor.shouldVisitChildren()) {
            this.exp.accept(visitor);
        }

        return o;
    }

    private static class FormatFinder extends MdxVisitorImpl {
        private final Validator validator;

        public FormatFinder(Validator validator) {
            this.validator = validator;
        }

        public Object visit(MemberExpr memberExpr) {
            Member member = memberExpr.getMember();
            this.returnFormula(member);
            if (member.isCalculated() && member instanceof RolapCalculatedMember && !this.hasCyclicReference(memberExpr)) {
                Formula formula = ((RolapCalculatedMember)member).getFormula();
                formula.accept(this.validator);
                this.returnFormula(member);
            }

            return super.visit(memberExpr);
        }

        private boolean hasCyclicReference(Exp expr) {
            List<MemberExpr> expList = new ArrayList();
            return this.hasCyclicReference(expr, expList);
        }

        private boolean hasCyclicReference(Exp expr, List<MemberExpr> expList) {
            if (expr instanceof MemberExpr) {
                MemberExpr memberExpr = (MemberExpr)expr;
                if (expList.contains(expr)) {
                    return true;
                }

                expList.add(memberExpr);
                Member member = memberExpr.getMember();
                if (member instanceof RolapCalculatedMember) {
                    RolapCalculatedMember calculatedMember = (RolapCalculatedMember)member;
                    Exp exp1 = calculatedMember.getExpression().accept(this.validator);
                    return this.hasCyclicReference(exp1, expList);
                }
            }

            if (expr instanceof FunCall) {
                FunCall funCall = (FunCall)expr;
                Exp[] exps = funCall.getArgs();

                for(int i = 0; i < exps.length; ++i) {
                    if (this.hasCyclicReference(exps[i], this.cloneForEachBranch(expList))) {
                        return true;
                    }
                }
            }

            return false;
        }

        private List<MemberExpr> cloneForEachBranch(List<MemberExpr> expList) {
            ArrayList<MemberExpr> list = new ArrayList();
            list.addAll(expList);
            return list;
        }

        private void returnFormula(Member member) {
            if (this.getFormula(member) != null) {
                throw new Formula.FoundOne(this.getFormula(member));
            }
        }

        private Exp getFormula(Member member) {
            return (Exp)member.getPropertyValue(Property.FORMAT_EXP_PARSED.name);
        }
    }

    private static class FoundOne extends RuntimeException {
        private final Exp exp;

        public FoundOne(Exp exp) {
            this.exp = exp;
        }
    }
}