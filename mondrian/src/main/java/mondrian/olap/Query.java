/* Decompiler 1429ms, total 2390ms, lines 1787 */
package mondrian.olap;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import mondrian.calc.Calc;
import mondrian.calc.CalcWriter;
import mondrian.calc.ExpCompiler;
import mondrian.calc.ResultStyle;
import mondrian.calc.ExpCompiler.Factory;
import mondrian.mdx.HierarchyExpr;
import mondrian.mdx.LevelExpr;
import mondrian.mdx.MdxVisitor;
import mondrian.mdx.MdxVisitorImpl;
import mondrian.mdx.MemberExpr;
import mondrian.mdx.NamedSetExpr;
import mondrian.mdx.ParameterExpr;
import mondrian.mdx.ResolvedFunCall;
import mondrian.mdx.UnresolvedFunCall;
import mondrian.olap.AxisOrdinal.StandardAxisOrdinal;
import mondrian.olap.Id.NameSegment;
import mondrian.olap.Id.Segment;
import mondrian.olap.NameResolver.Namespace;
import mondrian.olap.OlapElement.LocalizedProperty;
import mondrian.olap.Parameter.Scope;
import mondrian.olap.fun.ParameterFunDef;
import mondrian.olap.type.MemberType;
import mondrian.olap.type.SetType;
import mondrian.olap.type.StringType;
import mondrian.olap.type.TupleType;
import mondrian.olap.type.Type;
import mondrian.olap.type.TypeUtil;
import mondrian.resource.MondrianResource;
import mondrian.rolap.RolapConnectionProperties;
import mondrian.rolap.RolapCube;
import mondrian.rolap.RolapEvaluator;
import mondrian.rolap.RolapMember;
import mondrian.rolap.RolapUtil;
import mondrian.rolap.RolapHierarchy.LimitedRollupMember;
import mondrian.server.Execution;
import mondrian.server.Locus;
import mondrian.server.Statement;
import mondrian.server.Locus.Action;
import mondrian.spi.ProfileHandler;
import mondrian.util.ArrayStack;
import org.apache.commons.collections.collection.CompositeCollection;
import org.olap4j.impl.IdentifierParser;
import org.olap4j.mdx.IdentifierSegment;

public class Query extends QueryPart {
  private Formula[] formulas;
  public QueryAxis[] axes;
  private QueryAxis slicerAxis;
  private final List<Parameter> parameters;
  private final Map<String, Parameter> parametersByName;
  private final QueryPart[] cellProps;
  private final Cube cube;
  private Subcube subcube;
  private final Statement statement;
  public Calc[] axisCalcs;
  public Calc slicerCalc;
  Set<FunDef> alertedNonNativeFunDefs;
  private Set<Member> measuresMembers;
  private boolean nativeCrossJoinVirtualCube;
  private List<RolapCube> baseCubes;
  private boolean strictValidation;
  private ResultStyle resultStyle;
  private Map<String, Object> evalCache;
  private final List<Query.ScopedNamedSet> scopedNamedSets;
  private boolean ownStatement;
  public HashMap<Hierarchy, Calc> subcubeHierarchyCalcs;
  public HashMap<Hierarchy, HashMap<Member, Member>> subcubeHierarchies;

  public Query(Statement statement, Formula[] formulas, QueryAxis[] axes, String cubeName, QueryAxis slicerAxis, QueryPart[] cellProps, boolean strictValidation) {
    this(statement, Util.lookupCube(statement.getSchemaReader(), cubeName, true), formulas, new Subcube(cubeName, (Subcube)null, new QueryAxis[0], (QueryAxis)null), axes, slicerAxis, cellProps, new Parameter[0], strictValidation);
  }

  public Query(Statement statement, Formula[] formulas, QueryAxis[] axes, Subcube subcube, QueryAxis slicerAxis, QueryPart[] cellProps, boolean strictValidation) {
    this(statement, Util.lookupCube(statement.getSchemaReader(), subcube.getCubeName(), true), formulas, subcube, axes, slicerAxis, cellProps, new Parameter[0], strictValidation);
  }

  public Query(Statement statement, Cube mdxCube, Formula[] formulas, QueryAxis[] axes, QueryAxis slicerAxis, QueryPart[] cellProps, Parameter[] parameters, boolean strictValidation) {
    this(statement, mdxCube, formulas, (Subcube)null, axes, slicerAxis, cellProps, parameters, strictValidation);
  }

  public Query(Statement statement, Cube mdxCube, Formula[] formulas, Subcube subcube, QueryAxis[] axes, QueryAxis slicerAxis, QueryPart[] cellProps, Parameter[] parameters, boolean strictValidation) {
    this.parameters = new ArrayList();
    this.parametersByName = new HashMap();
    this.resultStyle = Util.Retrowoven ? ResultStyle.LIST : ResultStyle.ITERABLE;
    this.evalCache = new HashMap();
    this.scopedNamedSets = new ArrayList();
    this.subcubeHierarchyCalcs = new HashMap();
    this.subcubeHierarchies = new HashMap();
    this.statement = statement;
    this.cube = mdxCube;
    this.formulas = formulas;
    this.subcube = subcube;
    this.axes = axes;
    this.normalizeAxes();
    this.slicerAxis = slicerAxis;
    this.cellProps = cellProps;
    this.parameters.addAll(Arrays.asList(parameters));
    this.measuresMembers = new HashSet();
    this.nativeCrossJoinVirtualCube = true;
    this.strictValidation = strictValidation;
    this.alertedNonNativeFunDefs = new HashSet();
    statement.setQuery(this);
    this.resolve();
    if (RolapUtil.PROFILE_LOGGER.isDebugEnabled() && statement.getProfileHandler() == null) {
      statement.enableProfiling(new ProfileHandler() {
        public void explain(String plan, QueryTiming timing) {
          if (timing != null) {
            plan = plan + "\n" + timing;
          }

          RolapUtil.PROFILE_LOGGER.debug(plan);
        }
      });
    }

  }

  /** @deprecated */
  public void setQueryTimeoutMillis(long queryTimeoutMillis) {
    this.statement.setQueryTimeoutMillis(queryTimeoutMillis);
  }

  public QueryPart[] getCellProperties() {
    return this.cellProps;
  }

  public boolean hasCellProperty(String propertyName) {
    QueryPart[] var2 = this.cellProps;
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      QueryPart cellProp = var2[var4];
      if (((CellProperty)cellProp).isNameEquals(propertyName)) {
        return true;
      }
    }

    return false;
  }

  public boolean isCellPropertyEmpty() {
    return this.cellProps.length == 0;
  }

  public void addFormula(Id id, Exp exp) {
    this.addFormula(new Formula(false, id, exp, new MemberProperty[0], (Member)null, (NamedSet)null));
  }

  public void addFormula(Id id, Exp exp, MemberProperty[] memberProperties) {
    this.addFormula(new Formula(true, id, exp, memberProperties, (Member)null, (NamedSet)null));
  }

  public void addFormula(Formula formula) {
    this.formulas = (Formula[])Util.append(this.formulas, formula);
    this.resolve();
  }

  public void addFormulas(Formula... additions) {
    this.formulas = (Formula[])Util.appendArrays(this.formulas, new Formula[][]{additions});
    this.resolve();
  }

  public Validator createValidator() {
    return this.createValidator(this.statement.getSchema().getFunTable(), false, new HashMap());
  }

  public Validator createValidator(Map<QueryPart, QueryPart> resolvedIdentifiers) {
    return this.createValidator(this.statement.getSchema().getFunTable(), false, resolvedIdentifiers);
  }

  public Validator createValidator(FunTable functionTable, boolean alwaysResolveFunDef) {
    return new Query.QueryValidator(functionTable, alwaysResolveFunDef, this, new HashMap());
  }

  public Validator createValidator(FunTable functionTable, boolean alwaysResolveFunDef, Map<QueryPart, QueryPart> resolvedIdentifiers) {
    return new Query.QueryValidator(functionTable, alwaysResolveFunDef, this, resolvedIdentifiers);
  }

  /** @deprecated */
  public Query safeClone() {
    return this.clone();
  }

  public Query clone() {
    return new Query(this.statement, this.cube, Formula.cloneArray(this.formulas), QueryAxis.cloneArray(this.axes), this.slicerAxis == null ? null : (QueryAxis)this.slicerAxis.clone(), this.cellProps, (Parameter[])this.parameters.toArray(new Parameter[this.parameters.size()]), this.strictValidation);
  }

  public Connection getConnection() {
    return this.statement.getMondrianConnection();
  }

  /** @deprecated */
  public void cancel() {
    try {
      this.statement.cancel();
    } catch (SQLException var2) {
      throw new RuntimeException(var2);
    }
  }

  /** @deprecated */
  public void checkCancelOrTimeout() {
    Execution execution0 = this.statement.getCurrentExecution();
    if (execution0 != null) {
      execution0.checkCancelOrTimeout();
    }
  }

  /** @deprecated */
  public long getQueryStartTime() {
    Execution currentExecution = this.statement.getCurrentExecution();
    return currentExecution == null ? 0L : currentExecution.getStartTime();
  }

  public boolean shouldAlertForNonNative(FunDef funDef) {
    return this.alertedNonNativeFunDefs.add(funDef);
  }

  private void normalizeAxes() {
    for(int i = 0; i < this.axes.length; ++i) {
      AxisOrdinal correctOrdinal = StandardAxisOrdinal.forLogicalOrdinal(i);
      if (this.axes[i].getAxisOrdinal() != correctOrdinal) {
        for(int j = i + 1; j < this.axes.length; ++j) {
          if (this.axes[j].getAxisOrdinal() == correctOrdinal) {
            QueryAxis temp = this.axes[i];
            this.axes[i] = this.axes[j];
            this.axes[j] = temp;
            break;
          }
        }
      }
    }

  }

  public void resolve() {
    this.createFormulaElements();
    Map<QueryPart, QueryPart> resolvedIdentifiers = (new IdBatchResolver(this)).resolve();
    Validator validator = this.createValidator(resolvedIdentifiers);
    this.resolve(validator);
    Evaluator evaluator = RolapUtil.createEvaluator(this.statement);
    ExpCompiler compiler = this.createCompiler(evaluator, validator, Collections.singletonList(this.resultStyle));
    this.compile(compiler);
  }

  private void createFormulaElements() {
    if (this.formulas != null) {
      Formula[] var1 = this.formulas;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
        Formula formula = var1[var3];
        formula.createElement(this);
      }
    }

  }

  public boolean ignoreInvalidMembers() {
    boolean var10000;
    label25: {
      MondrianProperties props = MondrianProperties.instance();
      boolean load = ((RolapCube)this.getCube()).isLoadInProgress();
      if (!this.strictValidation) {
        if (load) {
          if (props.IgnoreInvalidMembers.get()) {
            break label25;
          }
        } else if (props.IgnoreInvalidMembersDuringQuery.get()) {
          break label25;
        }
      }

      var10000 = false;
      return var10000;
    }

    var10000 = true;
    return var10000;
  }

  public void setResultStyle(ResultStyle resultStyle) {
    switch(resultStyle) {
      case ITERABLE:
        this.resultStyle = Util.Retrowoven ? ResultStyle.LIST : ResultStyle.ITERABLE;
        break;
      case LIST:
      case MUTABLE_LIST:
        this.resultStyle = resultStyle;
        break;
      default:
        throw ResultStyleException.generateBadType(ResultStyle.ITERABLE_LIST_MUTABLELIST, resultStyle);
    }

  }

  public ResultStyle getResultStyle() {
    return this.resultStyle;
  }

  private void compile(ExpCompiler compiler) {
    if (this.subcube != null) {
      Iterator var2 = ((RolapCube)this.getCube()).getHierarchies().iterator();

      while(var2.hasNext()) {
        Hierarchy hierarchy = (Hierarchy)var2.next();
        Level[] levels = hierarchy.getLevels();
        Level lastLevel = levels[levels.length - 1];
        LevelExpr levelExpr = new LevelExpr(lastLevel);
        Exp levelMembers = new UnresolvedFunCall("AllMembers", Syntax.Property, new Exp[]{levelExpr});
        Exp resultExp = null;
        List<Exp> subcubeAxisExps = this.subcube.getAxisExps();
        ArrayList<Exp> hierarchyExps = new ArrayList();

        Exp hierarchyExp;
        for(int j = 0; j < subcubeAxisExps.size(); ++j) {
          hierarchyExp = (Exp)subcubeAxisExps.get(j);
          hierarchyExp = hierarchyExp.accept(compiler.getValidator());
          Hierarchy[] subcubeAxisHierarchies = this.collectHierarchies(hierarchyExp);
          if (Arrays.asList(subcubeAxisHierarchies).contains(hierarchy)) {
            hierarchyExps.add(hierarchyExp);
          }
        }

        Iterator var27 = hierarchyExps.iterator();

        while(var27.hasNext()) {
          hierarchyExp = (Exp)var27.next();
          Exp prevExp;
          if (resultExp == null) {
            prevExp = levelMembers;
          } else {
            prevExp = resultExp;
          }

          Exp axisInBracesExp = new UnresolvedFunCall("{}", Syntax.Braces, new Exp[]{hierarchyExp});
          if (hierarchyExps.size() > 1) {
            resultExp = new UnresolvedFunCall("Exists", Syntax.Function, new Exp[]{prevExp, axisInBracesExp});
          } else {
            resultExp = axisInBracesExp;
          }
        }

        if (resultExp != null) {
          HierarchyExpr hierarchyExpr = new HierarchyExpr(hierarchy);
          Exp hierarchyAllMembersExp = new UnresolvedFunCall("AllMembers", Syntax.Property, new Exp[]{hierarchyExpr});
          resultExp = new UnresolvedFunCall("Exists", Syntax.Function, new Exp[]{hierarchyAllMembersExp, resultExp});
          resultExp = resultExp.accept(compiler.getValidator());
          Calc calc = compiler.compileList(resultExp);
          this.subcubeHierarchyCalcs.put(hierarchy, calc);
        }
      }

      new HashMap();
    }

    if (this.formulas != null) {
      Formula[] var15 = this.formulas;
      int var17 = var15.length;

      for(int var19 = 0; var19 < var17; ++var19) {
        Formula formula = var15[var19];
        formula.compile();
      }
    }

    if (this.axes != null) {
      this.axisCalcs = new Calc[this.axes.length];

      for(int i = 0; i < this.axes.length; ++i) {
//        Exp prevSet = this.axes[i].getSet();
//        if (this.axes[i].isNonEmpty()) {
//          ArrayList<Exp> memberExps = new ArrayList();
//          Iterator var22 = this.measuresMembers.iterator();
//
//          while(var22.hasNext()) {
//            Member member = (Member)var22.next();
//            memberExps.add(new MemberExpr(member));
//          }
//
//          Exp[] nonEmptyArgs;
//          if (memberExps.size() > 0) {
//            Exp set2Exp = new UnresolvedFunCall("{}", Syntax.Braces, (Exp[])memberExps.toArray(new Exp[memberExps.size()]));
//            nonEmptyArgs = new Exp[]{prevSet, set2Exp};
//          } else {
//            nonEmptyArgs = new Exp[]{prevSet};
//          }
//
//          this.axes[i].setSet((new UnresolvedFunCall("NonEmpty", Syntax.Function, nonEmptyArgs)).accept(compiler.getValidator()));
//        }
//
//        this.axisCalcs[i] = this.axes[i].compile(compiler, this.resultStyle);
//        this.axes[i].setSet(prevSet);
        // 修改为同官方9.3.0.0版本逻辑一致
        this.axisCalcs[i] = this.axes[i].compile(compiler, this.resultStyle );
      }
    }

    if (this.slicerAxis != null) {
      this.slicerCalc = this.slicerAxis.compile(compiler, this.resultStyle);
    }

  }

  public void resolve(Validator validator) {
    this.parameters.clear();
    this.parametersByName.clear();
    this.accept(new Query.ParameterFinder());
    this.accept(new Query.AliasedExpressionFinder());
    int seekOrdinal;
    int useCount;
    if (this.formulas != null) {
      Formula[] var2 = this.formulas;
      seekOrdinal = var2.length;

      for(useCount = 0; useCount < seekOrdinal; ++useCount) {
        Formula formula = var2[useCount];
        validator.validate(formula);
      }
    }

    QueryAxis axis;
    if (this.axes != null) {
      Set<Integer> axisNames = new HashSet();
      QueryAxis[] var11 = this.axes;
      useCount = var11.length;

      int var14;
      for(var14 = 0; var14 < useCount; ++var14) {
        axis = var11[var14];
        validator.validate(axis);
        if (!axisNames.add(axis.getAxisOrdinal().logicalOrdinal())) {
          throw MondrianResource.instance().DuplicateAxis.ex(axis.getAxisName());
        }
      }

      seekOrdinal = StandardAxisOrdinal.COLUMNS.logicalOrdinal();
      QueryAxis[] var13 = this.axes;
      var14 = var13.length;

      for(int var15 = 0; var15 < var14; ++var15) {
        QueryAxis var10000 = var13[var15];
        if (!axisNames.contains(seekOrdinal)) {
          AxisOrdinal axisName = StandardAxisOrdinal.forLogicalOrdinal(seekOrdinal);
          throw MondrianResource.instance().NonContiguousAxis.ex(seekOrdinal, axisName.name());
        }

        ++seekOrdinal;
      }
    }

    if (this.slicerAxis != null) {
      this.slicerAxis.validate(validator);
    }

    Iterator var10 = ((RolapCube)this.getCube()).getHierarchies().iterator();

    Hierarchy hierarchy;
    do {
      if (!var10.hasNext()) {
        return;
      }

      hierarchy = (Hierarchy)var10.next();
      useCount = 0;
      Iterator var16 = this.allAxes().iterator();

      while(var16.hasNext()) {
        axis = (QueryAxis)var16.next();
        if (axis.getSet().getType().usesHierarchy(hierarchy, true)) {
          ++useCount;
        }
      }
    } while(useCount <= 1);

    throw MondrianResource.instance().HierarchyInIndependentAxes.ex(hierarchy.getUniqueName());
  }

  public void explain(PrintWriter pw) {
    boolean profiling = this.getStatement().getProfileHandler() != null;
    CalcWriter calcWriter = new CalcWriter(pw, profiling);
    Formula[] var4 = this.formulas;
    int var5 = var4.length;

    int var6;
    for(var6 = 0; var6 < var5; ++var6) {
      Formula formula = var4[var6];
      formula.getMdxMember();
    }

    if (this.slicerCalc != null) {
      pw.println("Axis (FILTER):");
      this.slicerCalc.accept(calcWriter);
      pw.println();
    }

    int i = -1;
    QueryAxis[] var10 = this.axes;
    var6 = var10.length;

    for(int var11 = 0; var11 < var6; ++var11) {
      QueryAxis axis = var10[var11];
      ++i;
      pw.println("Axis (" + axis.getAxisName() + "):");
      this.axisCalcs[i].accept(calcWriter);
      pw.println();
    }

    pw.flush();
  }

  private Collection<QueryAxis> allAxes() {
    return (Collection)(this.slicerAxis == null ? Arrays.asList(this.axes) : new CompositeCollection(new Collection[]{Collections.singletonList(this.slicerAxis), Arrays.asList(this.axes)}));
  }

  public void unparse(PrintWriter pw) {
    int i;
    if (this.formulas != null) {
      for(i = 0; i < this.formulas.length; ++i) {
        if (i == 0) {
          pw.print("with ");
        } else {
          pw.print("  ");
        }

        this.formulas[i].unparse(pw);
        pw.println();
      }
    }

    pw.print("select ");
    if (this.axes != null) {
      for(i = 0; i < this.axes.length; ++i) {
        this.axes[i].unparse(pw);
        if (i < this.axes.length - 1) {
          pw.println(",");
          pw.print("  ");
        } else {
          pw.println();
        }
      }
    }

    if (this.subcube != null) {
      pw.print("from ");
      this.subcube.unparse(pw);
    }

    if (this.slicerAxis != null) {
      pw.print("where ");
      this.slicerAxis.unparse(pw);
      pw.println();
    }

  }

  public String toString() {
    this.resolve();
    return Util.unparse(this);
  }

  public Object[] getChildren() {
    List<QueryPart> list = new ArrayList();
    list.addAll(Arrays.asList(this.axes));
    if (this.slicerAxis != null) {
      list.add(this.slicerAxis);
    }

    list.addAll(Arrays.asList(this.formulas));
    return list.toArray();
  }

  public QueryAxis getSlicerAxis() {
    return this.slicerAxis;
  }

  public void setSlicerAxis(QueryAxis axis) {
    this.slicerAxis = axis;
  }

  public void addLevelToAxis(AxisOrdinal axis, Level level) {
    assert axis != null;

    this.axes[axis.logicalOrdinal()].addLevel(level);
  }

  private Hierarchy[] collectHierarchies(Exp queryPart) {
    Type exprType = queryPart.getType();
    if (exprType instanceof SetType) {
      exprType = ((SetType)exprType).getElementType();
    }

    if (!(exprType instanceof TupleType)) {
      return new Hierarchy[]{this.getTypeHierarchy(exprType)};
    } else {
      Type[] types = ((TupleType)exprType).elementTypes;
      ArrayList<Hierarchy> hierarchyList = new ArrayList();
      Type[] var5 = types;
      int var6 = types.length;

      for(int var7 = 0; var7 < var6; ++var7) {
        Type type = var5[var7];
        hierarchyList.add(this.getTypeHierarchy(type));
      }

      return (Hierarchy[])hierarchyList.toArray(new Hierarchy[hierarchyList.size()]);
    }
  }

  private Hierarchy getTypeHierarchy(Type type) {
    Hierarchy hierarchy = type.getHierarchy();
    if (hierarchy != null) {
      return hierarchy;
    } else {
      Dimension dimension = type.getDimension();
      return dimension != null ? dimension.getHierarchy() : null;
    }
  }

  public void setParameter(final String parameterName, final Object value) {
    if (this.parameters.isEmpty()) {
      this.resolve();
    }

    final Parameter param = this.getSchemaReader(false).getParameter(parameterName);
    if (param == null) {
      throw MondrianResource.instance().UnknownParameter.ex(parameterName);
    } else if (!param.isModifiable()) {
      throw MondrianResource.instance().ParameterIsNotModifiable.ex(parameterName, param.getScope().name());
    } else {
      Object value2 = Locus.execute(new Execution(this.statement, 0L), "Query.quickParse", new Action<Object>() {
        public Object execute() {
          return Query.quickParse(parameterName, param.getType(), value, Query.this);
        }
      });
      param.setValue(value2);
    }
  }

  private static Object quickParse(String parameterName, Type type, Object value, Query query) throws NumberFormatException {
    int category = TypeUtil.typeToCategory(type);
    switch(category) {
      case 6:
        if (value == null) {
          if (type.getHierarchy() != null) {
            value = type.getHierarchy().getNullMember();
          } else if (type.getDimension() != null) {
            value = type.getDimension().getHierarchy().getNullMember();
          }
        }

        if (value instanceof String) {
          value = Util.parseIdentifier((String)value);
        }

        List olap4jSegmentList;
        if (value instanceof List && Util.canCast((List)value, Segment.class)) {
          olap4jSegmentList = Util.cast((List)value);
          OlapElement olapElement = Util.lookup(query, olap4jSegmentList);
          if (olapElement instanceof Member) {
            value = olapElement;
          }
        }

        if (value instanceof List && Util.canCast((List)value, IdentifierSegment.class)) {
          olap4jSegmentList = Util.cast((List)value);
          List<Segment> segmentList = Util.convert(olap4jSegmentList);
          OlapElement olapElement = Util.lookup(query, segmentList);
          if (olapElement instanceof Member) {
            value = olapElement;
          }
        }

        if (value instanceof Member && type.isInstance(value)) {
          return value;
        }

        throw Util.newInternal("Invalid value '" + value + "' for parameter '" + parameterName + "', type " + type);
      case 7:
        if (!(value instanceof Number) && value != null) {
          if (value instanceof String) {
            String s = (String)value;

            try {
              return new Integer(s);
            } catch (NumberFormatException var12) {
              return new Double(s);
            }
          }

          throw Util.newInternal("Invalid value '" + value + "' for parameter '" + parameterName + "', type " + type);
        }

        return value;
      case 8:
        if (value instanceof String) {
          value = IdentifierParser.parseIdentifierList((String)value);
        }

        if (!(value instanceof List)) {
          throw Util.newInternal("Invalid value '" + value + "' for parameter '" + parameterName + "', type " + type);
        } else {
          List<Member> expList = new ArrayList();
          List list = (List)value;
          SetType setType = (SetType)type;
          Type elementType = setType.getElementType();
          Iterator var9 = list.iterator();

          while(var9.hasNext()) {
            Object o = var9.next();
            if (o != null) {
              Member member = (Member)quickParse(parameterName, elementType, o, query);
              expList.add(member);
            }
          }

          return expList;
        }
      case 9:
        if (value == null) {
          return null;
        }

        return value.toString();
      default:
        throw Category.instance.badValue(category);
    }
  }

  public void swapAxes() {
    if (this.axes.length == 2) {
      Exp e0 = this.axes[0].getSet();
      boolean nonEmpty0 = this.axes[0].isNonEmpty();
      Exp e1 = this.axes[1].getSet();
      boolean nonEmpty1 = this.axes[1].isNonEmpty();
      this.axes[1].setSet(e0);
      this.axes[1].setNonEmpty(nonEmpty0);
      this.axes[0].setSet(e1);
      this.axes[0].setNonEmpty(nonEmpty1);
    }

  }

  public Parameter[] getParameters() {
    return (Parameter[])this.parameters.toArray(new Parameter[this.parameters.size()]);
  }

  public Cube getCube() {
    return this.cube;
  }

  public SchemaReader getSchemaReader(boolean accessControlled) {
    Role role;
    if (accessControlled) {
      role = this.getConnection().getRole();
    } else {
      role = null;
    }

    SchemaReader cubeSchemaReader = this.cube.getSchemaReader(role);
    return new Query.QuerySchemaReader(cubeSchemaReader, this);
  }

  public Member lookupMemberFromCache(String memberUniqueName) {
    Iterator var2 = this.getDefinedMembers().iterator();

    Member member;
    do {
      if (!var2.hasNext()) {
        return null;
      }

      member = (Member)var2.next();
    } while(!Util.equalName(member.getUniqueName(), memberUniqueName) && !Util.equalName(this.getUniqueNameWithoutAll(member), memberUniqueName));

    return member;
  }

  private String getUniqueNameWithoutAll(Member member) {
    Member parentMember = member.getParentMember();
    return parentMember != null && !parentMember.isAll() ? Util.makeFqName(this.getUniqueNameWithoutAll(parentMember), member.getName()) : Util.makeFqName(member.getHierarchy(), member.getName());
  }

  private NamedSet lookupNamedSet(Segment segment) {
    if (!(segment instanceof NameSegment)) {
      return null;
    } else {
      NameSegment nameSegment = (NameSegment)segment;
      Formula[] var3 = this.formulas;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
        Formula formula = var3[var5];
        if (!formula.isMember() && formula.getElement() != null && formula.getName().equals(nameSegment.getName())) {
          return (NamedSet)formula.getElement();
        }
      }

      return null;
    }
  }

  public Query.ScopedNamedSet createScopedNamedSet(String name, QueryPart scope, Exp expr) {
    Query.ScopedNamedSet scopedNamedSet = new Query.ScopedNamedSet(name, scope, expr);
    this.scopedNamedSets.add(scopedNamedSet);
    return scopedNamedSet;
  }

  Query.ScopedNamedSet lookupScopedNamedSet(List<Segment> nameParts, ArrayStack<QueryPart> scopeList) {
    if (nameParts.size() != 1) {
      return null;
    } else if (!(nameParts.get(0) instanceof NameSegment)) {
      return null;
    } else {
      String name = ((NameSegment)nameParts.get(0)).getName();
      Query.ScopedNamedSet bestScopedNamedSet = null;
      int bestScopeOrdinal = -1;
      Iterator var6 = this.scopedNamedSets.iterator();

      while(var6.hasNext()) {
        Query.ScopedNamedSet scopedNamedSet = (Query.ScopedNamedSet)var6.next();
        if (Util.equalName(scopedNamedSet.name, name)) {
          int scopeOrdinal = scopeList.indexOf(scopedNamedSet.scope);
          if (scopeOrdinal > bestScopeOrdinal) {
            bestScopedNamedSet = scopedNamedSet;
            bestScopeOrdinal = scopeOrdinal;
          }
        }
      }

      return bestScopedNamedSet;
    }
  }

  public Formula[] getFormulas() {
    return this.formulas;
  }

  public QueryAxis[] getAxes() {
    return this.axes;
  }

  public void removeFormula(String uniqueName, boolean failIfUsedInQuery) {
    Formula formula = this.findFormula(uniqueName);
    if (failIfUsedInQuery && formula != null) {
      OlapElement mdxElement = formula.getElement();
      Walker walker = new Walker(this);

      while(walker.hasMoreElements()) {
        Object queryElement = walker.nextElement();
        if (queryElement.equals(mdxElement)) {
          String formulaType = formula.isMember() ? MondrianResource.instance().CalculatedMember.str() : MondrianResource.instance().CalculatedSet.str();
          int i = 0;
          Object parent = walker.getAncestor(i);

          for(Object grandParent = walker.getAncestor(i + 1); parent != null && grandParent != null; grandParent = walker.getAncestor(i + 1)) {
            if (grandParent instanceof Query) {
              if (parent instanceof Axis) {
                throw MondrianResource.instance().MdxCalculatedFormulaUsedOnAxis.ex(formulaType, uniqueName, ((QueryAxis)parent).getAxisName());
              }

              if (parent instanceof Formula) {
                String parentFormulaType = ((Formula)parent).isMember() ? MondrianResource.instance().CalculatedMember.str() : MondrianResource.instance().CalculatedSet.str();
                throw MondrianResource.instance().MdxCalculatedFormulaUsedInFormula.ex(formulaType, uniqueName, parentFormulaType, ((Formula)parent).getUniqueName());
              }

              throw MondrianResource.instance().MdxCalculatedFormulaUsedOnSlicer.ex(formulaType, uniqueName);
            }

            ++i;
            parent = walker.getAncestor(i);
          }

          throw MondrianResource.instance().MdxCalculatedFormulaUsedInQuery.ex(formulaType, uniqueName, Util.unparse(this));
        }
      }
    }

    List<Formula> formulaList = new ArrayList();
    Formula[] var13 = this.formulas;
    int var14 = var13.length;

    for(int var15 = 0; var15 < var14; ++var15) {
      Formula formula1 = var13[var15];
      if (!formula1.getUniqueName().equalsIgnoreCase(uniqueName)) {
        formulaList.add(formula1);
      }
    }

    this.formulas = (Formula[])formulaList.toArray(new Formula[formulaList.size()]);
  }

  public boolean canRemoveFormula(String uniqueName) {
    Formula formula = this.findFormula(uniqueName);
    if (formula == null) {
      return false;
    } else {
      OlapElement mdxElement = formula.getElement();
      Walker walker = new Walker(this);

      Object queryElement;
      do {
        if (!walker.hasMoreElements()) {
          return true;
        }

        queryElement = walker.nextElement();
        if (queryElement instanceof MemberExpr && ((MemberExpr)queryElement).getMember().equals(mdxElement)) {
          return false;
        }
      } while(!(queryElement instanceof NamedSetExpr) || !((NamedSetExpr)queryElement).getNamedSet().equals(mdxElement));

      return false;
    }
  }

  public Formula findFormula(String uniqueName) {
    Formula[] var2 = this.formulas;
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      Formula formula = var2[var4];
      if (formula.getUniqueName().equalsIgnoreCase(uniqueName)) {
        return formula;
      }
    }

    return null;
  }

  public void renameFormula(String uniqueName, String newName) {
    Formula formula = this.findFormula(uniqueName);
    if (formula == null) {
      throw MondrianResource.instance().MdxFormulaNotFound.ex("formula", uniqueName, Util.unparse(this));
    } else {
      formula.rename(newName);
    }
  }

  List<Member> getDefinedMembers() {
    List<Member> definedMembers = new ArrayList();
    Formula[] var2 = this.formulas;
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      Formula formula = var2[var4];
      if (formula.isMember() && formula.getElement() != null && this.getConnection().getRole().canAccess(formula.getElement())) {
        definedMembers.add((Member)formula.getElement());
      }
    }

    return definedMembers;
  }

  public void setAxisShowEmptyCells(int axis, boolean showEmpty) {
    if (axis >= this.axes.length) {
      throw MondrianResource.instance().MdxAxisShowSubtotalsNotSupported.ex(axis);
    } else {
      this.axes[axis].setNonEmpty(!showEmpty);
    }
  }

  public Hierarchy[] getMdxHierarchiesOnAxis(AxisOrdinal axis) {
    if (axis.logicalOrdinal() >= this.axes.length) {
      throw MondrianResource.instance().MdxAxisShowSubtotalsNotSupported.ex(axis.logicalOrdinal());
    } else {
      QueryAxis queryAxis = axis.isFilter() ? this.slicerAxis : this.axes[axis.logicalOrdinal()];
      return this.collectHierarchies(queryAxis.getSet());
    }
  }

  public Hierarchy[] getMdxHierarchiesOnAxis(QueryAxis axis) {
    return axis == null ? new Hierarchy[0] : this.collectHierarchies(axis.getSet());
  }

  public Calc compileExpression(Exp exp, boolean scalar, ResultStyle resultStyle) {
    this.statement.setQuery(this);
    Evaluator evaluator = RolapEvaluator.create(this.statement);
    Validator validator = this.createValidator();
    List<ResultStyle> resultStyleList = Collections.singletonList(resultStyle != null ? resultStyle : this.resultStyle);
    ExpCompiler compiler = this.createCompiler(evaluator, validator, resultStyleList);
    return scalar ? compiler.compileScalar(exp, false) : compiler.compile(exp);
  }

  public ExpCompiler createCompiler() {
    this.statement.setQuery(this);
    Evaluator evaluator = RolapEvaluator.create(this.statement);
    Validator validator = this.createValidator();
    return this.createCompiler(evaluator, validator, Collections.singletonList(this.resultStyle));
  }

  private ExpCompiler createCompiler(Evaluator evaluator, Validator validator, List<ResultStyle> resultStyleList) {
    ExpCompiler compiler = Factory.getExpCompiler(evaluator, validator, resultStyleList);
    int expDeps = MondrianProperties.instance().TestExpDependencies.get();
    ProfileHandler profileHandler = this.statement.getProfileHandler();
    if (profileHandler != null) {
      compiler = RolapUtil.createProfilingCompiler(compiler);
    } else if (expDeps > 0) {
      compiler = RolapUtil.createDependencyTestingCompiler(compiler);
    }

    return compiler;
  }

  public void addMeasuresMembers(OlapElement olapElement) {
    if (olapElement instanceof Member) {
      Member member = (Member)olapElement;
      if (member.isMeasure()) {
        this.measuresMembers.add(member);
      }
    }

  }

  public Set<Member> getMeasuresMembers() {
    return Collections.unmodifiableSet(this.measuresMembers);
  }

  public void setVirtualCubeNonNativeCrossJoin() {
    this.nativeCrossJoinVirtualCube = false;
  }

  public boolean nativeCrossJoinVirtualCube() {
    return this.nativeCrossJoinVirtualCube;
  }

  public void setBaseCubes(List<RolapCube> baseCubes) {
    this.baseCubes = baseCubes;
  }

  public List<RolapCube> getBaseCubes() {
    return this.baseCubes;
  }

  public Object accept(MdxVisitor visitor) {
    Object o = visitor.visit(this);
    if (visitor.shouldVisitChildren()) {
      Formula[] var3 = this.formulas;
      int var4 = var3.length;

      int var5;
      for(var5 = 0; var5 < var4; ++var5) {
        Formula formula = var3[var5];
        formula.accept(visitor);
      }

      QueryAxis[] var7 = this.axes;
      var4 = var7.length;

      for(var5 = 0; var5 < var4; ++var5) {
        QueryAxis axis = var7[var5];
        axis.accept(visitor);
      }

      if (this.slicerAxis != null) {
        this.slicerAxis.accept(visitor);
      }
    }

    return o;
  }

  public void putEvalCache(String key, Object value) {
    this.evalCache.put(key, value);
  }

  public Object getEvalCache(String key) {
    return this.evalCache.get(key);
  }

  public void clearEvalCache() {
    this.evalCache.clear();
  }

  /** @deprecated */
  public void close() {
    if (this.ownStatement) {
      this.statement.close();
    }

  }

  public Statement getStatement() {
    return this.statement;
  }

  public void setOwnStatement(boolean ownStatement) {
    this.ownStatement = ownStatement;
  }

  public void replaceSubcubeMembers() {
    QueryAxis[] var1 = this.axes;
    int var2 = var1.length;

    int var3;
    Exp exp;
    for(var3 = 0; var3 < var2; ++var3) {
      QueryAxis queryAxis = var1[var3];
      exp = queryAxis.getSet();
      queryAxis.setSet(this.replaceSubcubeMember(exp));
    }

    if (this.slicerAxis != null) {
      exp = this.slicerAxis.getSet();
      this.slicerAxis.setSet(this.replaceSubcubeMember(exp));
    }

    Formula[] var7 = this.formulas;
    var2 = var7.length;

    for(var3 = 0; var3 < var2; ++var3) {
      Formula formula = var7[var3];
      exp = formula.getExpression();
      if (exp != null) {
        formula.setExpression(this.replaceSubcubeMember(exp));
      }

      exp = formula.getExpression();
      if (exp != null) {
        formula.setExpression(this.replaceSubcubeMember(exp));
      }
    }

  }

  private List<Member> getSubcubeMembers(List<Member> members, boolean addNullMember) {
    ArrayList<Member> newMembers = new ArrayList();
    Iterator var4 = members.iterator();

    while(var4.hasNext()) {
      Member sourceMember = (Member)var4.next();
      Member subcubeMember = this.getSubcubeMember(sourceMember, addNullMember);
      if (subcubeMember != null) {
        newMembers.add(subcubeMember);
      }
    }

    return newMembers;
  }

  private List<Member> getRolapMembers(List<Member> members) {
    ArrayList<Member> newMembers = new ArrayList();
    Iterator var3 = members.iterator();

    while(var3.hasNext()) {
      Member sourceMember = (Member)var3.next();
      newMembers.add(this.getRolapMember(sourceMember));
    }

    return newMembers;
  }

  private Member getRolapMember(Member member) {
    return (Member)(member != null && member instanceof LimitedRollupMember ? ((LimitedRollupMember)member).getSourceMember() : member);
  }

  private Member getSubcubeMember(Member member, boolean addNullMember) {
    Hierarchy hierarchy = ((RolapMember)member).getHierarchy();
    if (this.subcubeHierarchies.containsKey(hierarchy)) {
      HashMap<Member, Member> subcubeMembers = (HashMap)this.subcubeHierarchies.get(hierarchy);
      if (subcubeMembers.containsKey(member)) {
        return (Member)subcubeMembers.get(member);
      } else {
        return addNullMember ? hierarchy.getNullMember() : null;
      }
    } else {
      return member;
    }
  }

  private Exp replaceSubcubeMember(Exp exp) {
    if (exp instanceof MemberExpr) {
      MemberExpr memberExpr = (MemberExpr)exp;
      Member subcubeMember = this.getSubcubeMember(memberExpr.getMember(), true);
      return new MemberExpr(subcubeMember);
    } else {
      if (exp instanceof ResolvedFunCall) {
        ResolvedFunCall resolvedFunCall = (ResolvedFunCall)exp;

        for(int i = 0; i < resolvedFunCall.getArgs().length; ++i) {
          resolvedFunCall.getArgs()[i] = this.replaceSubcubeMember(resolvedFunCall.getArgs()[i]);
        }
      }

      return exp;
    }
  }

  private class AliasedExpressionFinder extends MdxVisitorImpl {
    private AliasedExpressionFinder() {
    }

    public Object visit(QueryAxis queryAxis) {
      this.registerAlias(queryAxis, queryAxis.getSet());
      return super.visit(queryAxis);
    }

    public Object visit(UnresolvedFunCall call) {
      this.registerAliasArgs(call);
      return super.visit(call);
    }

    public Object visit(ResolvedFunCall call) {
      this.registerAliasArgs(call);
      return super.visit(call);
    }

    private void registerAliasArgs(FunCall call) {
      Exp[] var2 = call.getArgs();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
        Exp exp = var2[var4];
        this.registerAlias((QueryPart)call, exp);
      }

    }

    private void registerAlias(QueryPart parent, Exp exp) {
      if (exp instanceof FunCall) {
        FunCall call2 = (FunCall)exp;
        if (call2.getSyntax() == Syntax.Infix && call2.getFunName().equals("AS")) {
          assert call2.getArgCount() == 2;

          if (call2.getArg(1) instanceof Id) {
            Id id = (Id)call2.getArg(1);
            Query.this.createScopedNamedSet(((NameSegment)id.getSegments().get(0)).getName(), parent, call2.getArg(0));
          } else if (call2.getArg(1) instanceof NamedSetExpr) {
            NamedSetExpr set = (NamedSetExpr)call2.getArg(1);
            Query.this.createScopedNamedSet(set.getNamedSet().getName(), parent, call2.getArg(0));
          }
        }
      }

    }

    // $FF: synthetic method
    AliasedExpressionFinder(Object x1) {
      this();
    }
  }

  private class ParameterFinder extends MdxVisitorImpl {
    private ParameterFinder() {
    }

    public Object visit(ParameterExpr parameterExpr) {
      Parameter parameter = parameterExpr.getParameter();
      if (!Query.this.parameters.contains(parameter)) {
        Query.this.parameters.add(parameter);
        Query.this.parametersByName.put(parameter.getName(), parameter);
      }

      return null;
    }

    public Object visit(UnresolvedFunCall call) {
      if (call.getFunName().equals("Parameter")) {
        String parameterName = ParameterFunDef.getParameterName(call.getArgs());
        if (Query.this.parametersByName.get(parameterName) != null) {
          throw MondrianResource.instance().ParameterDefinedMoreThanOnce.ex(parameterName);
        }

        Type type = ParameterFunDef.getParameterType(call.getArgs());
        Parameter parameter = new ParameterImpl(parameterName, Literal.nullValue, (String)null, type);
        Query.this.parameters.add(parameter);
        Query.this.parametersByName.put(parameterName, parameter);
      }

      return null;
    }

    // $FF: synthetic method
    ParameterFinder(Object x1) {
      this();
    }
  }

  public static class ScopedNamedSet implements NamedSet {
    private final String name;
    private final QueryPart scope;
    private Exp expr;

    private ScopedNamedSet(String name, QueryPart scope, Exp expr) {
      this.name = name;
      this.scope = scope;
      this.expr = expr;
    }

    public String getName() {
      return this.name;
    }

    public String getNameUniqueWithinQuery() {
      return System.identityHashCode(this) + "";
    }

    public boolean isDynamic() {
      return true;
    }

    public Exp getExp() {
      return this.expr;
    }

    public void setExp(Exp expr) {
      this.expr = expr;
    }

    public void setName(String newName) {
      throw new UnsupportedOperationException();
    }

    public Type getType() {
      return this.expr.getType();
    }

    public Map<String, Annotation> getAnnotationMap() {
      return Collections.emptyMap();
    }

    public NamedSet validate(Validator validator) {
      Exp newExpr = this.expr.accept(validator);
      Type type = newExpr.getType();
      if (type instanceof MemberType || type instanceof TupleType) {
        newExpr = (new UnresolvedFunCall("{}", Syntax.Braces, new Exp[]{newExpr})).accept(validator);
      }

      this.expr = newExpr;
      return this;
    }

    public String getUniqueName() {
      return this.name;
    }

    public String getDescription() {
      throw new UnsupportedOperationException();
    }

    public OlapElement lookupChild(SchemaReader schemaReader, Segment s, MatchType matchType) {
      throw new UnsupportedOperationException();
    }

    public String getQualifiedName() {
      throw new UnsupportedOperationException();
    }

    public String getCaption() {
      throw new UnsupportedOperationException();
    }

    public boolean isVisible() {
      throw new UnsupportedOperationException();
    }

    public Hierarchy getHierarchy() {
      throw new UnsupportedOperationException();
    }

    public Dimension getDimension() {
      throw new UnsupportedOperationException();
    }

    public String getLocalized(LocalizedProperty prop, Locale locale) {
      throw new UnsupportedOperationException();
    }

    // $FF: synthetic method
    ScopedNamedSet(String x0, QueryPart x1, Exp x2, Object x3) {
      this(x0, x1, x2);
    }
  }

  private static class ScopedSchemaReader extends DelegatingSchemaReader implements Namespace {
    private final Query.QueryValidator queryValidator;
    private final boolean accessControlled;

    private ScopedSchemaReader(Query.QueryValidator queryValidator, boolean accessControlled) {
      super(queryValidator.getQuery().getSchemaReader(accessControlled));
      this.queryValidator = queryValidator;
      this.accessControlled = accessControlled;
    }

    public SchemaReader withoutAccessControl() {
      return !this.accessControlled ? this : new Query.ScopedSchemaReader(this.queryValidator, false);
    }

    public List<Namespace> getNamespaces() {
      List<Namespace> list = new ArrayList();
      list.add(this);
      list.addAll(super.getNamespaces());
      return list;
    }

    public OlapElement lookupCompoundInternal(OlapElement parent, List<Segment> names, boolean failIfNotFound, int category, MatchType matchType) {
      switch(category) {
        case 0:
        case 8:
          Query.ScopedNamedSet namedSet = this.queryValidator.getQuery().lookupScopedNamedSet(names, this.queryValidator.getScopeStack());
          if (namedSet != null) {
            return namedSet;
          }
        default:
          return super.lookupCompoundInternal(parent, names, failIfNotFound, category, matchType);
      }
    }

    public OlapElement lookupChild(OlapElement parent, IdentifierSegment segment, MatchType matchType) {
      return this.lookupChild(parent, segment);
    }

    public OlapElement lookupChild(OlapElement parent, IdentifierSegment segment) {
      return !(parent instanceof Cube) ? null : this.queryValidator.getQuery().lookupScopedNamedSet(Collections.singletonList(Util.convert(segment)), this.queryValidator.getScopeStack());
    }

    // $FF: synthetic method
    ScopedSchemaReader(Query.QueryValidator x0, boolean x1, Object x2) {
      this(x0, x1);
    }
  }

  private static class QueryValidator extends ValidatorImpl {
    private final boolean alwaysResolveFunDef;
    private Query query;
    private final SchemaReader schemaReader;

    public QueryValidator(FunTable functionTable, boolean alwaysResolveFunDef, Query query, Map<QueryPart, QueryPart> resolvedIdentifiers) {
      super(functionTable, resolvedIdentifiers);
      this.alwaysResolveFunDef = alwaysResolveFunDef;
      this.query = query;
      this.schemaReader = new Query.ScopedSchemaReader(this, true);
    }

    public SchemaReader getSchemaReader() {
      return this.schemaReader;
    }

    protected void defineParameter(Parameter param) {
      String name = param.getName();
      this.query.parameters.add(param);
      this.query.parametersByName.put(name, param);
    }

    public Query getQuery() {
      return this.query;
    }

    public boolean alwaysResolveFunDef() {
      return this.alwaysResolveFunDef;
    }

    public ArrayStack<QueryPart> getScopeStack() {
      return this.stack;
    }
  }

  private static class ConnectionParameterImpl extends ParameterImpl {
    public ConnectionParameterImpl(String name, Literal defaultValue) {
      super(name, defaultValue, "Connection property", new StringType());
    }

    public Scope getScope() {
      return Scope.Connection;
    }

    public void setValue(Object value) {
      throw MondrianResource.instance().ParameterIsNotModifiable.ex(this.getName(), this.getScope().name());
    }
  }

  private static class QuerySchemaReader extends DelegatingSchemaReader implements Namespace {
    private final Query query;

    public QuerySchemaReader(SchemaReader cubeSchemaReader, Query query) {
      super(cubeSchemaReader);
      this.query = query;
    }

    public SchemaReader withoutAccessControl() {
      return new Query.QuerySchemaReader(this.schemaReader.withoutAccessControl(), this.query);
    }

    public Member getMemberByUniqueName(List<Segment> uniqueNameParts, boolean failIfNotFound, MatchType matchType) {
      String uniqueName = Util.implode(uniqueNameParts);
      Member member = this.query.lookupMemberFromCache(uniqueName);
      if (member == null) {
        member = this.schemaReader.getMemberByUniqueName(uniqueNameParts, failIfNotFound, matchType);
      }

      if (!failIfNotFound && member == null) {
        return null;
      } else {
        return this.getRole().canAccess(member) ? member : null;
      }
    }

    public List<Member> getLevelMembers(Level level, boolean includeCalculated) {
      return this.getLevelMembers(level, includeCalculated, (Evaluator)null);
    }

    public List<Member> getLevelMembers(Level level, Evaluator context) {
      return this.getLevelMembers(level, false, context);
    }

    public List<Member> getLevelMembers(Level level, boolean includeCalculated, Evaluator context) {
      List<Member> members = super.getLevelMembers(level, false, context);
      if (includeCalculated) {
        members = Util.addLevelCalculatedMembers(this, level, (List)members);
      }

      Hierarchy hierarchy = level.getHierarchy();
      if (this.query.subcubeHierarchies.containsKey(hierarchy)) {
        ArrayList<Member> newMembers = new ArrayList();
        HashMap<Member, Member> subcubeMembers = (HashMap)this.query.subcubeHierarchies.get(hierarchy);

        for(int i = 0; i < ((List)members).size(); ++i) {
          Member sourceMember = (Member)((List)members).get(i);
          if (subcubeMembers.containsKey(sourceMember)) {
            newMembers.add((Member)subcubeMembers.get(sourceMember));
          }
        }

        members = newMembers;
      }

      return (List)members;
    }

    public List<Member> getMemberChildren(Member member) {
      Member rolapMember = this.query.getRolapMember(member);
      return this.query.getSubcubeMembers(super.getMemberChildren(rolapMember), false);
    }

    public List<Member> getMemberChildren(List<Member> members) {
      List<Member> rolapMembers = this.query.getRolapMembers(members);
      return this.query.getSubcubeMembers(super.getMemberChildren(rolapMembers), false);
    }

    public List<Member> getMemberChildren(Member member, Evaluator context) {
      Member rolapMember = this.query.getRolapMember(member);
      return this.query.getSubcubeMembers(super.getMemberChildren(rolapMember, context), false);
    }

    public List<Member> getMemberChildren(List<Member> members, Evaluator context) {
      List<Member> rolapMembers = this.query.getRolapMembers(members);
      return this.query.getSubcubeMembers(super.getMemberChildren(rolapMembers, context), false);
    }

    public Map<? extends Member, Access> getMemberChildrenWithDetails(Member member, Evaluator evaluator) {
      Member rolapMember = this.query.getRolapMember(member);
      Map<?, Access> sourceMembers = super.getMemberChildrenWithDetails(rolapMember, evaluator);
      HashMap<Member, Access> newMembers = new HashMap();
      Iterator var6 = sourceMembers.entrySet().iterator();

      while(var6.hasNext()) {
        Entry<Member, Access> entry = (Entry)var6.next();
        Member subcubeMember = this.query.getSubcubeMember((Member)entry.getKey(), false);
        if (subcubeMember != null) {
          newMembers.put(subcubeMember, (Access)entry.getValue());
        }
      }

      return newMembers;
    }

    public Member getCalculatedMember(List<Segment> nameParts) {
      Formula[] var2 = this.query.formulas;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
        Formula formula = var2[var4];
        if (formula.isMember()) {
          Member member = (Member)formula.getElement();
          if (member != null && Util.matches(member, nameParts) && this.query.getConnection().getRole().canAccess(member)) {
            return member;
          }
        }
      }

      return null;
    }

    public List<Member> getCalculatedMembers(Hierarchy hierarchy) {
      List<Member> result = new ArrayList();
      List<Member> calculatedMembers = super.getCalculatedMembers(hierarchy);
      result.addAll(calculatedMembers);
      Iterator var4 = this.query.getDefinedMembers().iterator();

      while(var4.hasNext()) {
        Member member = (Member)var4.next();
        if (member.getHierarchy().equals(hierarchy)) {
          result.add(member);
        }
      }

      return result;
    }

    public List<Member> getCalculatedMembers(Level level) {
      List<Member> hierarchyMembers = this.getCalculatedMembers(level.getHierarchy());
      List<Member> result = new ArrayList();
      Iterator var4 = hierarchyMembers.iterator();

      while(var4.hasNext()) {
        Member member = (Member)var4.next();
        if (member.getLevel().equals(level)) {
          result.add(member);
        }
      }

      return result;
    }

    public List<Member> getCalculatedMembers() {
      return this.query.getDefinedMembers();
    }

    public OlapElement getElementChild(OlapElement parent, Segment s) {
      return this.getElementChild(parent, s, MatchType.EXACT);
    }

    public OlapElement getElementChild(OlapElement parent, Segment s, MatchType matchType) {
      OlapElement mdxElement = this.schemaReader.getElementChild(parent, s, matchType);
      if (mdxElement != null) {
        return mdxElement;
      } else if (!(s instanceof NameSegment)) {
        return null;
      } else {
        String name = ((NameSegment)s).getName();
        Formula[] var6 = this.query.formulas;
        int var7 = var6.length;

        for(int var8 = 0; var8 < var7; ++var8) {
          Formula formula = var6[var8];
          if (!formula.isMember()) {
            Id id = formula.getIdentifier();
            if (id.getSegments().size() == 1 && ((Segment)id.getSegments().get(0)).matches(name)) {
              return formula.getNamedSet();
            }
          }
        }

        return mdxElement;
      }
    }

    public OlapElement lookupCompoundInternal(OlapElement parent, List<Segment> names, boolean failIfNotFound, int category, MatchType matchType) {
      if (matchType == MatchType.EXACT) {
        OlapElement oe = this.lookupCompound(parent, names, failIfNotFound, category, MatchType.EXACT_SCHEMA);
        if (oe != null) {
          return oe;
        }
      }

      switch(category) {
        case 0:
        case 6:
          if (parent == this.query.cube) {
            Member calculatedMember = this.getCalculatedMember(names);
            if (calculatedMember != null) {
              return calculatedMember;
            }
          }
        default:
          switch(category) {
            case 0:
            case 8:
              if (parent == this.query.cube) {
                NamedSet namedSet = this.getNamedSet(names);
                if (namedSet != null) {
                  return namedSet;
                }
              }
            default:
              OlapElement olapElement = super.lookupCompoundInternal(parent, names, failIfNotFound, category, matchType);
              if (olapElement instanceof Member) {
                Member member = (Member)olapElement;
                Formula formula = (Formula)member.getPropertyValue(Property.FORMULA.name);
                if (formula != null) {
                  Formula formulaClone = (Formula)formula.clone();
                  formulaClone.createElement(this.query);
                  formulaClone.accept(this.query.createValidator());
                  olapElement = formulaClone.getMdxMember();
                }
              }

              return (OlapElement)olapElement;
          }
      }
    }

    public NamedSet getNamedSet(List<Segment> nameParts) {
      return nameParts.size() != 1 ? null : this.query.lookupNamedSet((Segment)nameParts.get(0));
    }

    public Parameter getParameter(String name) {
      Iterator var2 = this.query.parameters.iterator();

      Parameter parameter;
      do {
        if (!var2.hasNext()) {
          if (Util.lookup(RolapConnectionProperties.class, name) != null) {
            Object value = this.query.statement.getProperty(name);
            Literal defaultValue = Literal.createString(String.valueOf(value));
            return new Query.ConnectionParameterImpl(name, defaultValue);
          }

          return super.getParameter(name);
        }

        parameter = (Parameter)var2.next();
      } while(!parameter.getName().equals(name));

      return parameter;
    }

    public OlapElement lookupChild(OlapElement parent, IdentifierSegment segment, MatchType matchType) {
      return this.lookupChild(parent, segment);
    }

    public OlapElement lookupChild(OlapElement parent, IdentifierSegment segment) {
      Formula[] var3 = this.query.getFormulas();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
        Formula formula = var3[var5];
        if (NameResolver.matches(formula, parent, segment)) {
          return formula.getElement();
        }
      }

      OlapElement parentOlapElement = parent;
      if (parent != null && parent instanceof RolapMember) {
        parentOlapElement = this.query.getRolapMember((RolapMember)parent);
      }

      OlapElement child = null;
      Iterator var9 = this.getNamespaces().iterator();

      while(var9.hasNext()) {
        Namespace namespace = (Namespace)var9.next();
        if (namespace != this) {
          child = namespace.lookupChild((OlapElement)parentOlapElement, segment);
          if (child != null) {
            break;
          }
        }
      }

      return child != null && child instanceof RolapMember ? this.query.getSubcubeMember((RolapMember)child, true) : null;
    }

    public Member getHierarchyDefaultMember(Hierarchy hierarchy) {
      Member member = super.getHierarchyDefaultMember(hierarchy);
      return this.query.getSubcubeMember(member, true);
    }

    public List<Namespace> getNamespaces() {
      List<Namespace> list = new ArrayList();
      list.add(this);
      list.addAll(super.getNamespaces());
      return list;
    }
  }
}