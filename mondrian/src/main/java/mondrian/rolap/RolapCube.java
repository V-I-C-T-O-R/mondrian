/* Decompiler 3927ms, total 5396ms, lines 2390 */
package mondrian.rolap;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import mondrian.calc.Calc;
import mondrian.calc.ExpCompiler;
import mondrian.mdx.MdxVisitorImpl;
import mondrian.mdx.MemberExpr;
import mondrian.mdx.ResolvedFunCall;
import mondrian.olap.Access;
import mondrian.olap.Annotation;
import mondrian.olap.CacheControl;
import mondrian.olap.CubeBase;
import mondrian.olap.Dimension;
import mondrian.olap.DimensionType;
import mondrian.olap.Evaluator;
import mondrian.olap.Exp;
import mondrian.olap.Formula;
import mondrian.olap.Hierarchy;
import mondrian.olap.Id;
import mondrian.olap.Level;
import mondrian.olap.MatchType;
import mondrian.olap.Member;
import mondrian.olap.MemberProperty;
import mondrian.olap.MondrianException;
import mondrian.olap.MondrianProperties;
import mondrian.olap.NameResolver;
import mondrian.olap.OlapElement;
import mondrian.olap.Parameter;
import mondrian.olap.Property;
import mondrian.olap.Query;
import mondrian.olap.QueryAxis;
import mondrian.olap.QueryPart;
import mondrian.olap.Role;
import mondrian.olap.SchemaReader;
import mondrian.olap.SetBase;
import mondrian.olap.Util;
import mondrian.olap.Id.NameSegment;
import mondrian.olap.Id.Quoting;
import mondrian.olap.Id.Segment;
import mondrian.olap.MondrianDef.Action;
import mondrian.olap.MondrianDef.Annotations;
import mondrian.olap.MondrianDef.CalculatedMember;
import mondrian.olap.MondrianDef.CalculatedMemberProperty;
import mondrian.olap.MondrianDef.Column;
import mondrian.olap.MondrianDef.Cube;
import mondrian.olap.MondrianDef.CubeDimension;
import mondrian.olap.MondrianDef.DimensionUsage;
import mondrian.olap.MondrianDef.DrillThroughAction;
import mondrian.olap.MondrianDef.DrillThroughAttribute;
import mondrian.olap.MondrianDef.DrillThroughColumn;
import mondrian.olap.MondrianDef.DrillThroughMeasure;
import mondrian.olap.MondrianDef.Expression;
import mondrian.olap.MondrianDef.InlineTable;
import mondrian.olap.MondrianDef.Join;
import mondrian.olap.MondrianDef.Measure;
import mondrian.olap.MondrianDef.NamedSet;
import mondrian.olap.MondrianDef.Relation;
import mondrian.olap.MondrianDef.RelationOrJoin;
import mondrian.olap.MondrianDef.Schema;
import mondrian.olap.MondrianDef.VirtualCube;
import mondrian.olap.MondrianDef.VirtualCubeMeasure;
import mondrian.olap.MondrianDef.WritebackAttribute;
import mondrian.olap.MondrianDef.WritebackColumn;
import mondrian.olap.MondrianDef.WritebackMeasure;
import mondrian.olap.MondrianDef.WritebackTable;
import mondrian.olap.NameResolver.Namespace;
import mondrian.olap.fun.FunDefBase;
import mondrian.resource.MondrianResource;
import mondrian.rolap.BitKey.Factory;
import mondrian.rolap.RolapCubeHierarchy.CacheRolapCubeHierarchyMemberReader;
import mondrian.rolap.RolapHierarchy.RolapCalculatedMeasure;
import mondrian.rolap.RolapStar.Condition;
import mondrian.rolap.RolapStar.Table;
import mondrian.rolap.aggmatcher.ExplicitRules.Group;
import mondrian.rolap.cache.SoftSmartCache;
import mondrian.rolap.format.FormatterCreateContext;
import mondrian.rolap.format.FormatterFactory;
import mondrian.rolap.format.FormatterCreateContext.Builder;
import mondrian.server.Locus;
import mondrian.server.Statement;
import mondrian.spi.CellFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eigenbase.xom.DOMWrapper;
import org.eigenbase.xom.Parser;
import org.eigenbase.xom.XOMException;
import org.eigenbase.xom.XOMUtil;
import org.olap4j.mdx.IdentifierNode;
import org.olap4j.mdx.IdentifierSegment;

public class RolapCube extends CubeBase {
    private static final Logger LOGGER = LogManager.getLogger(RolapCube.class);
    private final RolapSchema schema;
    private final Map<String, Annotation> annotationMap;
    private final RolapHierarchy measuresHierarchy;
    final Relation fact;
    private SchemaReader schemaReader;
    private final List<Formula> calculatedMemberList;
    private final SoftSmartCache<Role, List<Member>> roleToAccessibleCalculatedMembers;
    private final List<Formula> namedSetList;
    private final List<HierarchyUsage> hierarchyUsages;
    private RolapStar star;
    private Group aggGroup;
    private final Map<Hierarchy, HierarchyUsage> firstUsageMap;
    private RolapCubeUsages cubeUsages;
    RolapBaseCubeMeasure factCountMeasure;
    final List<RolapHierarchy> hierarchyList;
    private boolean loadInProgress;
    private Map<RolapLevel, RolapCubeLevel> virtualToBaseMap;
    final BitKey closureColumnBitKey;
    final List<RolapAction> actionList;
    final List<RolapWritebackTable> writebackTableList;
    private List<RolapCube> baseCubes;

    private RolapCube(RolapSchema schema, Schema xmlSchema, String name, boolean visible, String caption, String description, boolean isCache, Relation fact, CubeDimension[] dimensions, boolean load, Map<String, Annotation> annotationMap) {
        super(name, caption, visible, description, new RolapDimension[dimensions.length + 1]);
        this.calculatedMemberList = new ArrayList();
        this.roleToAccessibleCalculatedMembers = new SoftSmartCache();
        this.namedSetList = new ArrayList();
        this.firstUsageMap = new HashMap();
        this.hierarchyList = new ArrayList();
        this.loadInProgress = false;
        this.virtualToBaseMap = new HashMap();
        this.actionList = new ArrayList();
        this.writebackTableList = new ArrayList();

        assert annotationMap != null;

        this.schema = schema;
        this.annotationMap = annotationMap;
        this.caption = caption;
        this.fact = fact;
        this.hierarchyUsages = new ArrayList();
        if (!this.isVirtual()) {
            this.star = schema.getRolapStarRegistry().getOrCreateStar(fact);
            if (!isCache) {
                this.star.setCacheAggregations(isCache);
            }
        }

        if (this.getLogger().isDebugEnabled()) {
            if (this.isVirtual()) {
                this.getLogger().debug("RolapCube<init>: virtual cube=" + this.name);
            } else {
                this.getLogger().debug("RolapCube<init>: cube=" + this.name);
            }
        }

        RolapDimension measuresDimension = new RolapDimension(schema, "Measures", (String)null, true, (String)null, DimensionType.MeasuresDimension, false, Collections.emptyMap());
        this.dimensions[0] = measuresDimension;
        this.measuresHierarchy = measuresDimension.newHierarchy((String)null, false, (RolapHierarchy)null);
        this.hierarchyList.add(this.measuresHierarchy);
        if (!Util.isEmpty(xmlSchema.measuresCaption)) {
            measuresDimension.setCaption(xmlSchema.measuresCaption);
            this.measuresHierarchy.setCaption(xmlSchema.measuresCaption);
        }

        for(int i = 0; i < dimensions.length; ++i) {
            CubeDimension xmlCubeDimension = dimensions[i];
            if (xmlCubeDimension.highCardinality) {
                LOGGER.warn(MondrianResource.instance().HighCardinalityInDimension.str(xmlCubeDimension.getName()));
            }

            RolapCubeDimension dimension = this.getOrCreateDimension(xmlCubeDimension, schema, xmlSchema, i + 1, this.hierarchyList);
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("RolapCube<init>: dimension=" + dimension.getName());
            }

            this.dimensions[i + 1] = dimension;
            if (!this.isVirtual()) {
                this.createUsages(dimension, xmlCubeDimension);
            }

            this.registerDimension(dimension);
        }

        if (!this.isVirtual()) {
            this.closureColumnBitKey = Factory.makeBitKey(this.star.getColumnCount());
        } else {
            this.closureColumnBitKey = null;
        }

        schema.addCube(this);
    }

    RolapCube(RolapSchema schema, Schema xmlSchema, Cube xmlCube, boolean load) {
        this(schema, xmlSchema, xmlCube.name, xmlCube.visible, xmlCube.caption, xmlCube.description, xmlCube.cache, (Relation)RolapUtil.processRelation(xmlSchema, xmlCube.fact), xmlCube.dimensions, load, RolapHierarchy.createAnnotationMap(xmlCube.annotations));
        if (this.fact == null) {
            throw Util.newError("Must specify fact table of cube '" + this.getName() + "'");
        } else if (this.fact.getAlias() == null) {
            throw Util.newError("Must specify alias for fact table of cube '" + this.getName() + "'");
        } else {
            RolapLevel measuresLevel = this.measuresHierarchy.newMeasuresLevel();
            List<RolapMember> measureList = new ArrayList(xmlCube.measures.length);
            Member defaultMeasure = null;

            for(int i = 0; i < xmlCube.measures.length; ++i) {
                RolapBaseCubeMeasure measure = this.createMeasure(xmlCube, measuresLevel, i, xmlCube.measures[i]);
                measureList.add(measure);
                if (Util.equalName(measure.getName(), xmlCube.defaultMeasure)) {
                    defaultMeasure = measure;
                }

                if (measure.getAggregator() == RolapAggregator.Count) {
                    this.factCountMeasure = measure;
                }
            }

            boolean writebackEnabled = false;
            Iterator var30 = this.hierarchyList.iterator();

            while(var30.hasNext()) {
                RolapHierarchy hierarchy = (RolapHierarchy)var30.next();
                if (ScenarioImpl.isScenario(hierarchy)) {
                    writebackEnabled = true;
                }
            }

            if (this.factCountMeasure == null) {
                Measure xmlMeasure = new Measure();
                xmlMeasure.aggregator = "count";
                xmlMeasure.name = "Fact Count";
                xmlMeasure.visible = false;
                mondrian.olap.MondrianDef.Annotation internalUsage = new mondrian.olap.MondrianDef.Annotation();
                internalUsage.name = "Internal Use";
                internalUsage.cdata = "For internal use";
                Annotations annotations = new Annotations();
                annotations.array = new mondrian.olap.MondrianDef.Annotation[1];
                annotations.array[0] = internalUsage;
                xmlMeasure.annotations = annotations;
                this.factCountMeasure = this.createMeasure(xmlCube, measuresLevel, measureList.size(), xmlMeasure);
                measureList.add(this.factCountMeasure);
            }

            this.setMeasuresHierarchyMemberReader(new CacheMemberReader(new MeasureMemberSource(this.measuresHierarchy, measureList)));
            this.measuresHierarchy.setDefaultMember(defaultMeasure);
            this.init(xmlCube.dimensions);
            this.init(xmlCube, measureList);
            this.setMeasuresHierarchyMemberReader(new CacheMemberReader(new MeasureMemberSource(this.measuresHierarchy, measureList)));
            this.checkOrdinals(xmlCube.name, measureList);
            this.loadAggGroup(xmlCube);
            Action[] var32 = xmlCube.actions;
            int var35 = var32.length;

            int var16;
            Member currentMeasure;
            int var36;
            for(var36 = 0; var36 < var35; ++var36) {
                Action action = var32[var36];
                if (action instanceof DrillThroughAction) {
                    DrillThroughAction drillThroughAction = (DrillThroughAction)action;
                    List<RolapDrillThroughColumn> columns = new ArrayList();
                    DrillThroughColumn[] var15 = drillThroughAction.columns;
                    var16 = var15.length;

                    for(int var17 = 0; var17 < var16; ++var17) {
                        DrillThroughColumn drillThroughColumn = var15[var17];
                        String name;
                        if (drillThroughColumn instanceof DrillThroughAttribute) {
                            DrillThroughAttribute drillThroughAttribute = (DrillThroughAttribute)drillThroughColumn;
                            name = drillThroughAttribute.name;
                            Dimension dimension = null;
                            Hierarchy hierarchy = null;
                            Level level = null;
                            Dimension[] var24 = this.getDimensions();
                            int var25 = var24.length;

                            int var26;
                            for(var26 = 0; var26 < var25; ++var26) {
                                Dimension currntDimension = var24[var26];
                                if (currntDimension.getName().equals(drillThroughAttribute.dimension)) {
                                    dimension = currntDimension;
                                    break;
                                }
                            }

                            if (dimension == null) {
                                throw Util.newError("Error while creating DrillThrough  action. Dimension '" + drillThroughAttribute.dimension + "' not found");
                            }

                            if (drillThroughAttribute.hierarchy != null && !drillThroughAttribute.hierarchy.equals("")) {
                                Hierarchy[] var56 = dimension.getHierarchies();
                                var25 = var56.length;

                                for(var26 = 0; var26 < var25; ++var26) {
                                    Hierarchy currentHierarchy = var56[var26];
                                    String herarchyName = ((RolapCubeHierarchy)currentHierarchy).getSubName();
                                    if (herarchyName == null) {
                                        herarchyName = currentHierarchy.getName();
                                    }

                                    if (herarchyName.equals(drillThroughAttribute.hierarchy)) {
                                        hierarchy = currentHierarchy;
                                        break;
                                    }
                                }

                                if (hierarchy == null) {
                                    throw Util.newError("Error while creating DrillThrough  action. Hierarchy '" + drillThroughAttribute.hierarchy + "' not found");
                                }

                                if (drillThroughAttribute.level != null && !drillThroughAttribute.level.equals("")) {
                                    Level[] var58 = hierarchy.getLevels();
                                    var25 = var58.length;

                                    for(var26 = 0; var26 < var25; ++var26) {
                                        Level currentLevel = var58[var26];
                                        if (currentLevel.getName().equals(drillThroughAttribute.level)) {
                                            level = currentLevel;
                                            break;
                                        }
                                    }

                                    if (level == null) {
                                        throw Util.newError("Error while creating DrillThrough  action. Level '" + drillThroughAttribute.level + "' not found");
                                    }
                                }
                            }

                            columns.add(new RolapDrillThroughAttribute(name, dimension, hierarchy, level));
                        } else if (drillThroughColumn instanceof DrillThroughMeasure) {
                            DrillThroughMeasure drillThroughMeasure = (DrillThroughMeasure)drillThroughColumn;
                            name = drillThroughMeasure.name;
                            currentMeasure = null;
                            Iterator var22 = this.getMeasures().iterator();

                            while(var22.hasNext()) {
                                Member currntMeasure = (Member)var22.next();
                                if (currntMeasure.getName().equals(drillThroughMeasure.measure)) {
                                    currentMeasure = currntMeasure;
                                    break;
                                }
                            }

                            if (currentMeasure == null) {
                                throw Util.newError("Error while creating DrillThrough  action. Measure '" + drillThroughMeasure.name + "' not found");
                            }

                            columns.add(new RolapDrillThroughMeasure(name, currentMeasure));
                        }
                    }

                    RolapDrillThroughAction rolapDrillThroughAction = new RolapDrillThroughAction(drillThroughAction.name, drillThroughAction.caption, drillThroughAction.description, drillThroughAction._default != null && drillThroughAction._default, columns);
                    this.actionList.add(rolapDrillThroughAction);
                }
            }

            WritebackTable[] var34 = xmlCube.writebacks;
            var35 = var34.length;

            for(var36 = 0; var36 < var35; ++var36) {
                WritebackTable writebackTable = var34[var36];
                List<RolapWritebackColumn> columns = new ArrayList();
                WritebackColumn[] var39 = writebackTable.columns;
                int var42 = var39.length;

                for(var16 = 0; var16 < var42; ++var16) {
                    WritebackColumn writebackColumn = var39[var16];
                    if (writebackColumn instanceof WritebackAttribute) {
                        WritebackAttribute writebackAttribute = (WritebackAttribute)writebackColumn;
                        Dimension dimension = null;
                        Dimension[] var50 = this.getDimensions();
                        int var54 = var50.length;

                        for(int var55 = 0; var55 < var54; ++var55) {
                            Dimension currentDimension = var50[var55];
                            if (currentDimension.getName().equals(writebackAttribute.dimension)) {
                                dimension = currentDimension;
                                break;
                            }
                        }

                        if (dimension == null) {
                            throw Util.newError("Error while creating `WritebackTable`. Dimension '" + writebackAttribute.dimension + "' not found");
                        }

                        columns.add(new RolapWritebackAttribute(dimension, writebackAttribute.column));
                    } else if (writebackColumn instanceof WritebackMeasure) {
                        WritebackMeasure writebackMeasure = (WritebackMeasure)writebackColumn;
                        Member measure = null;
                        Iterator var49 = this.getMeasures().iterator();

                        while(var49.hasNext()) {
                            currentMeasure = (Member)var49.next();
                            if (currentMeasure.getName().equals(writebackMeasure.name)) {
                                measure = currentMeasure;
                                break;
                            }
                        }

                        if (measure == null) {
                            throw Util.newError("Error while creating DrillThrough  action. Measure '" + writebackMeasure.name + "' not found");
                        }

                        columns.add(new RolapWritebackMeasure(measure, writebackMeasure.column));
                    }
                }

                RolapWritebackTable rolapWritebackTable = new RolapWritebackTable(writebackTable.name, writebackTable.schema, columns);
                this.writebackTableList.add(rolapWritebackTable);
            }

        }
    }

    private RolapBaseCubeMeasure createMeasure(Cube xmlCube, RolapLevel measuresLevel, int ordinal, Measure xmlMeasure) {
        Object measureExp;
        if (xmlMeasure.column != null) {
            if (xmlMeasure.measureExp != null) {
                throw MondrianResource.instance().BadMeasureSource.ex(xmlCube.name, xmlMeasure.name);
            }

            measureExp = new Column(this.fact.getAlias(), xmlMeasure.column);
        } else if (xmlMeasure.measureExp != null) {
            measureExp = xmlMeasure.measureExp;
        } else {
            if (!xmlMeasure.aggregator.equals("count")) {
                throw MondrianResource.instance().BadMeasureSource.ex(xmlCube.name, xmlMeasure.name);
            }

            measureExp = null;
        }

        String aggregator = xmlMeasure.aggregator;
        if (aggregator.equals("distinct count")) {
            aggregator = RolapAggregator.DistinctCount.getName();
        }

        RolapBaseCubeMeasure measure = new RolapBaseCubeMeasure(this, (RolapMember)null, measuresLevel, xmlMeasure.name, xmlMeasure.caption, xmlMeasure.description, xmlMeasure.formatString, (Expression)measureExp, aggregator, xmlMeasure.datatype, RolapHierarchy.createAnnotationMap(xmlMeasure.annotations));
        FormatterCreateContext formatterContext = (new Builder(measure.getUniqueName())).formatterDef(xmlMeasure.cellFormatter).formatterAttr(xmlMeasure.formatter).build();
        CellFormatter cellFormatter = FormatterFactory.instance().createCellFormatter(formatterContext);
        if (cellFormatter != null) {
            measure.setFormatter(cellFormatter);
        }

        if (!Util.isEmpty(xmlMeasure.caption)) {
            measure.setProperty(Property.CAPTION.name, xmlMeasure.caption);
        }

        Boolean visible = xmlMeasure.visible;
        if (visible == null) {
            visible = Boolean.TRUE;
        }

        measure.setProperty(Property.VISIBLE.name, visible);
        measure.setProperty(Property.DISPLAY_FOLDER.name, xmlMeasure.displayFolder);
        measure.setProperty(Property.BACK_COLOR.name, xmlMeasure.backColor);
        List<String> propNames = new ArrayList();
        List<String> propExprs = new ArrayList();
        this.validateMemberProps(xmlMeasure.memberProperties, propNames, propExprs, xmlMeasure.name);

        for(int j = 0; j < propNames.size(); ++j) {
            String propName = (String)propNames.get(j);
            Object propExpr = propExprs.get(j);
            measure.setProperty(propName, propExpr);
            if (propName.equals(Property.MEMBER_ORDINAL.name) && propExpr instanceof String) {
                String expr = (String)propExpr;
                if (expr.startsWith("\"") && expr.endsWith("\"")) {
                    try {
                        ordinal = Integer.valueOf(expr.substring(1, expr.length() - 1));
                    } catch (NumberFormatException var18) {
                        Util.discard(var18);
                    }
                }
            }
        }

        measure.setOrdinal(ordinal);
        return measure;
    }

    private void setMeasuresHierarchyMemberReader(MemberReader memberReader) {
        this.measuresHierarchy.setMemberReader(memberReader);
        this.schemaReader = null;
    }

    RolapCube(RolapSchema schema, Schema xmlSchema, VirtualCube xmlVirtualCube, boolean load) {
        this(schema, xmlSchema, xmlVirtualCube.name, xmlVirtualCube.visible, xmlVirtualCube.caption, xmlVirtualCube.description, true, (Relation)null, xmlVirtualCube.dimensions, load, RolapHierarchy.createAnnotationMap(xmlVirtualCube.annotations));
        RolapLevel measuresLevel = this.measuresHierarchy.newMeasuresLevel();
        List<RolapVirtualCubeMeasure> origMeasureList = new ArrayList();
        List<CalculatedMember> origCalcMeasureList = new ArrayList();
        RolapCube.CubeComparator cubeComparator = new RolapCube.CubeComparator();
        Map<RolapCube, List<CalculatedMember>> calculatedMembersMap = new TreeMap(cubeComparator);
        Member defaultMeasure = null;
        this.cubeUsages = new RolapCubeUsages(xmlVirtualCube.cubeUsage);
        HashMap<String, VirtualCubeMeasure> measureHash = new HashMap();
        VirtualCubeMeasure[] var12 = xmlVirtualCube.measures;
        int var13 = var12.length;

        RolapCube rolapCube;
        List calculatedMembers;
        for(int var14 = 0; var14 < var13; ++var14) {
            VirtualCubeMeasure xmlMeasure = var12[var14];
            measureHash.put(xmlMeasure.name, xmlMeasure);
            rolapCube = schema.lookupCube(xmlMeasure.cubeName);
            if (rolapCube == null) {
                throw Util.newError("Cube '" + xmlMeasure.cubeName + "' not found");
            }

            calculatedMembers = rolapCube.getMeasures();
            boolean found = false;
            boolean isDefaultMeasureFound = false;
            Iterator var20 = calculatedMembers.iterator();

            while(var20.hasNext()) {
                Member cubeMeasure = (Member)var20.next();
                if (cubeMeasure.getUniqueName().equals(xmlMeasure.name)) {
                    if (cubeMeasure.getName().equalsIgnoreCase(xmlVirtualCube.defaultMeasure)) {
                        defaultMeasure = cubeMeasure;
                        isDefaultMeasureFound = true;
                    }

                    found = true;
                    if (cubeMeasure instanceof RolapCalculatedMember) {
                        CalculatedMember calcMember = schema.lookupXmlCalculatedMember(xmlMeasure.name, xmlMeasure.cubeName);
                        if (calcMember == null) {
                            throw Util.newInternal("Could not find XML Calculated Member '" + xmlMeasure.name + "' in XML cube '" + xmlMeasure.cubeName + "'");
                        }

                        List<CalculatedMember> memberList = (List)calculatedMembersMap.get(rolapCube);
                        if (memberList == null) {
                            memberList = new ArrayList();
                        }

                        ((List)memberList).add(calcMember);
                        origCalcMeasureList.add(calcMember);
                        calculatedMembersMap.put(rolapCube, memberList);
                    } else {
                        RolapVirtualCubeMeasure virtualCubeMeasure = new RolapVirtualCubeMeasure((RolapMember)null, measuresLevel, (RolapStoredMeasure)cubeMeasure, RolapHierarchy.createAnnotationMap(xmlMeasure.annotations));
                        Boolean visible = xmlMeasure.visible;
                        if (visible == null) {
                            visible = Boolean.TRUE;
                        }

                        virtualCubeMeasure.setProperty(Property.VISIBLE.name, visible);
                        virtualCubeMeasure.setProperty(Property.CAPTION.name, cubeMeasure.getCaption());
                        origMeasureList.add(virtualCubeMeasure);
                        if (isDefaultMeasureFound) {
                            defaultMeasure = virtualCubeMeasure;
                        }
                    }
                    break;
                }
            }

            if (!found) {
                throw Util.newInternal("could not find measure '" + xmlMeasure.name + "' in cube '" + xmlMeasure.cubeName + "'");
            }
        }

        this.init(xmlVirtualCube.dimensions);
        List<RolapVirtualCubeMeasure> modifiedMeasureList = new ArrayList(origMeasureList);
        Iterator var25 = calculatedMembersMap.keySet().iterator();

        while(var25.hasNext()) {
            Object o = var25.next();
            RolapCube baseCube = (RolapCube)o;
            List<CalculatedMember> xmlCalculatedMemberList = (List)calculatedMembersMap.get(baseCube);
            Query queryExp = this.resolveCalcMembers(xmlCalculatedMemberList, Collections.emptyList(), baseCube, false);
            RolapCube.MeasureFinder measureFinder = new RolapCube.MeasureFinder(this, baseCube, measuresLevel);
            queryExp.accept(measureFinder);
            modifiedMeasureList.addAll(measureFinder.getMeasuresFound());
        }

        List<CalculatedMember> xmlCalculatedMemberList = new ArrayList();
        Iterator var28 = calculatedMembersMap.keySet().iterator();

        while(var28.hasNext()) {
            Object o = var28.next();
            rolapCube = (RolapCube)o;
            xmlCalculatedMemberList.addAll((Collection)calculatedMembersMap.get(rolapCube));
        }

        xmlCalculatedMemberList.addAll(Arrays.asList(xmlVirtualCube.calculatedMembers));
        this.setMeasuresHierarchyMemberReader(new CacheMemberReader(new MeasureMemberSource(this.measuresHierarchy, Util.cast(modifiedMeasureList))));
        this.createCalcMembersAndNamedSets(xmlCalculatedMemberList, Arrays.asList(xmlVirtualCube.namedSets), new ArrayList(), new ArrayList(), this, false);
        Map<String, RolapCalculatedMeasure> calcMeasuresWithBaseCube = new HashMap();
        Iterator var32 = calculatedMembersMap.keySet().iterator();

        while(var32.hasNext()) {
            rolapCube = (RolapCube)var32.next();
            calculatedMembers = (List)calculatedMembersMap.get(rolapCube);
            Iterator var41 = calculatedMembers.iterator();

            while(var41.hasNext()) {
                CalculatedMember calculatedMember = (CalculatedMember)var41.next();
                List<Member> measures = rolapCube.getMeasures();
                Iterator var48 = measures.iterator();

                while(var48.hasNext()) {
                    Member measure = (Member)var48.next();
                    if (measure instanceof RolapCalculatedMeasure) {
                        RolapCalculatedMeasure calculatedMeasure = (RolapCalculatedMeasure)measure;
                        if (calculatedMember.name.equals(calculatedMeasure.getKey())) {
                            calculatedMeasure.setBaseCube(rolapCube);
                            calcMeasuresWithBaseCube.put(calculatedMeasure.getUniqueName(), calculatedMeasure);
                        }
                    }
                }
            }
        }

        this.setMeasuresHierarchyMemberReader(new CacheMemberReader(new MeasureMemberSource(this.measuresHierarchy, Util.cast(origMeasureList))));
        this.measuresHierarchy.setDefaultMember((Member)defaultMeasure);
        List<CalculatedMember> xmlVirtualCubeCalculatedMemberList = Arrays.asList(xmlVirtualCube.calculatedMembers);
        ArrayList finalMeasureMembers;
        Iterator var38;
        Formula formula;
        if (!this.vcHasAllCalcMembers(origCalcMeasureList, xmlVirtualCubeCalculatedMemberList)) {
            finalMeasureMembers = new ArrayList(this.calculatedMemberList);
            this.calculatedMemberList.clear();
            var38 = finalMeasureMembers.iterator();

            while(var38.hasNext()) {
                formula = (Formula)var38.next();
                if (!this.findOriginalMembers(formula, origCalcMeasureList, this.calculatedMemberList)) {
                    this.findOriginalMembers(formula, xmlVirtualCubeCalculatedMemberList, this.calculatedMemberList);
                }
            }
        }

        Iterator var37 = this.calculatedMemberList.iterator();

        while(var37.hasNext()) {
            Formula calcMember = (Formula)var37.next();
            if (calcMember.getName().equalsIgnoreCase(xmlVirtualCube.defaultMeasure)) {
                this.measuresHierarchy.setDefaultMember(calcMember.getMdxMember());
                break;
            }
        }

        finalMeasureMembers = new ArrayList();
        var38 = origMeasureList.iterator();

        while(var38.hasNext()) {
            RolapVirtualCubeMeasure measure = (RolapVirtualCubeMeasure)var38.next();
            finalMeasureMembers.add(measure);
        }

        RolapMember calcMeasure;
        for(var38 = this.calculatedMemberList.iterator(); var38.hasNext(); finalMeasureMembers.add(calcMeasure)) {
            formula = (Formula)var38.next();
            calcMeasure = (RolapMember)formula.getMdxMember();
            if (calcMeasure instanceof RolapCalculatedMeasure && calcMeasuresWithBaseCube.containsKey(calcMeasure.getUniqueName())) {
                ((RolapCalculatedMeasure)calcMeasure).setBaseCube(((RolapCalculatedMeasure)calcMeasuresWithBaseCube.get(calcMeasure.getUniqueName())).getBaseCube());
            }

            VirtualCubeMeasure xmlMeasure = (VirtualCubeMeasure)measureHash.get(calcMeasure.getUniqueName());
            if (xmlMeasure != null) {
                Boolean visible = xmlMeasure.visible;
                if (visible != null) {
                    calcMeasure.setProperty(Property.VISIBLE.name, visible);
                }
            }
        }

        this.setMeasuresHierarchyMemberReader(new CacheMemberReader(new MeasureMemberSource(this.measuresHierarchy, Util.cast(finalMeasureMembers))));
    }

    private boolean vcHasAllCalcMembers(List<CalculatedMember> origCalcMeasureList, List<CalculatedMember> xmlVirtualCubeCalculatedMemberList) {
        return this.calculatedMemberList.size() == origCalcMeasureList.size() + xmlVirtualCubeCalculatedMemberList.size();
    }

    private boolean findOriginalMembers(Formula formula, List<CalculatedMember> xmlCalcMemberList, List<Formula> calcMemberList) {
        Iterator var4 = xmlCalcMemberList.iterator();

        CalculatedMember xmlCalcMember;
        Hierarchy hierarchy;
        do {
            if (!var4.hasNext()) {
                return false;
            }

            xmlCalcMember = (CalculatedMember)var4.next();
            hierarchy = null;
            if (xmlCalcMember.dimension != null) {
                Dimension dimension = this.lookupDimension(new NameSegment(xmlCalcMember.dimension, Quoting.UNQUOTED));
                if (dimension != null && dimension.getHierarchy() != null) {
                    hierarchy = dimension.getHierarchy();
                }
            } else if (xmlCalcMember.hierarchy != null) {
                hierarchy = this.lookupHierarchy(new NameSegment(xmlCalcMember.hierarchy, Quoting.UNQUOTED), true);
            }
        } while(!formula.getName().equals(xmlCalcMember.name) || !formula.getMdxMember().getHierarchy().equals(hierarchy));

        calcMemberList.add(formula);
        return true;
    }

    protected Logger getLogger() {
        return LOGGER;
    }

    public Map<String, Annotation> getAnnotationMap() {
        return this.annotationMap;
    }

    public boolean hasAggGroup() {
        return this.aggGroup != null;
    }

    public Group getAggGroup() {
        return this.aggGroup;
    }

    void loadAggGroup(Cube xmlCube) {
        this.aggGroup = Group.make(this, xmlCube);
    }

    private RolapCubeDimension getOrCreateDimension(CubeDimension xmlCubeDimension, RolapSchema schema, Schema xmlSchema, int dimensionOrdinal, List<RolapHierarchy> cubeHierarchyList) {
        RolapDimension dimension = null;
        if (xmlCubeDimension instanceof DimensionUsage) {
            DimensionUsage usage = (DimensionUsage)xmlCubeDimension;
            RolapHierarchy sharedHierarchy = schema.getSharedHierarchy(usage.source);
            if (sharedHierarchy != null) {
                dimension = (RolapDimension)sharedHierarchy.getDimension();
            }
        }

        if (dimension == null) {
            mondrian.olap.MondrianDef.Dimension xmlDimension = xmlCubeDimension.getDimension(xmlSchema);
            dimension = new RolapDimension(schema, this, xmlDimension, xmlCubeDimension);
        }

        return new RolapCubeDimension(this, dimension, xmlCubeDimension, xmlCubeDimension.name, dimensionOrdinal, cubeHierarchyList, xmlCubeDimension.highCardinality);
    }

    private void init(Cube xmlCube, List<RolapMember> memberList) {
        List<Formula> formulaList = new ArrayList();
        this.createCalcMembersAndNamedSets(Arrays.asList(xmlCube.calculatedMembers), Arrays.asList(xmlCube.namedSets), memberList, formulaList, this, true);
    }

    private void checkOrdinals(String cubeName, List<RolapMember> measures) {
        Map<Integer, String> ordinals = new HashMap();
        Iterator var4 = measures.iterator();

        while(var4.hasNext()) {
            RolapMember measure = (RolapMember)var4.next();
            Integer ordinal = measure.getOrdinal();
            if (ordinals.containsKey(ordinal)) {
                throw MondrianResource.instance().MeasureOrdinalsNotUnique.ex(cubeName, ordinal.toString(), (String)ordinals.get(ordinal), measure.getUniqueName());
            }

            ordinals.put(ordinal, measure.getUniqueName());
        }

    }

    private void createCalcMembersAndNamedSets(List<CalculatedMember> xmlCalcMembers, List<NamedSet> xmlNamedSets, List<RolapMember> memberList, List<Formula> formulaList, RolapCube cube, boolean errOnDups) {
        Query queryExp = this.resolveCalcMembers(xmlCalcMembers, xmlNamedSets, cube, errOnDups);
        if (queryExp != null) {
            Util.assertTrue(queryExp.getFormulas().length == xmlCalcMembers.size() + xmlNamedSets.size());

            int i;
            for(i = 0; i < xmlCalcMembers.size(); ++i) {
                this.postCalcMember(xmlCalcMembers, i, queryExp, memberList);
            }

            for(i = 0; i < xmlNamedSets.size(); ++i) {
                this.postNamedSet(xmlNamedSets, xmlCalcMembers.size(), i, queryExp, formulaList);
            }

        }
    }

    private Query resolveCalcMembers(List<CalculatedMember> xmlCalcMembers, List<NamedSet> xmlNamedSets, RolapCube cube, boolean errOnDups) {
        if (xmlCalcMembers.size() == 0 && xmlNamedSets.size() == 0) {
            return null;
        } else {
            StringBuilder buf = new StringBuilder(256);
            buf.append("WITH").append(Util.nl);
            Set<String> fqNames = new LinkedHashSet();

            for(int i = 0; i < xmlCalcMembers.size(); ++i) {
                this.preCalcMember(xmlCalcMembers, i, buf, cube, errOnDups, fqNames);
            }

            Set<String> nameSet = new HashSet();
            Iterator var8 = this.namedSetList.iterator();

            while(var8.hasNext()) {
                Formula namedSet = (Formula)var8.next();
                nameSet.add(namedSet.getName());
            }

            var8 = xmlNamedSets.iterator();

            while(var8.hasNext()) {
                NamedSet xmlNamedSet = (NamedSet)var8.next();
                this.preNamedSet(xmlNamedSet, nameSet, buf);
            }

            buf.append("SELECT FROM ").append(cube.getUniqueName());
            final String queryString = buf.toString();

            try {
                final RolapConnection conn = this.schema.getInternalConnection();
                return (Query)Locus.execute(conn, "RolapCube.resolveCalcMembers", new mondrian.server.Locus.Action<Query>() {
                    public Query execute() {
                        Query queryExp = conn.parseQuery(queryString);
                        queryExp.resolve();
                        return queryExp;
                    }
                });
            } catch (Exception var10) {
                throw MondrianResource.instance().UnknownNamedSetHasBadFormula.ex(this.getName(), var10);
            }
        }
    }

    private void postNamedSet(List<NamedSet> xmlNamedSets, int offset, int i, Query queryExp, List<Formula> formulaList) {
        NamedSet xmlNamedSet = (NamedSet)xmlNamedSets.get(i);
        Util.discard(xmlNamedSet);
        Formula formula = queryExp.getFormulas()[offset + i];
        SetBase namedSet = (SetBase)formula.getNamedSet();
        if (xmlNamedSet.caption != null && xmlNamedSet.caption.length() > 0) {
            namedSet.setCaption(xmlNamedSet.caption);
        }

        if (xmlNamedSet.description != null && xmlNamedSet.description.length() > 0) {
            namedSet.setDescription(xmlNamedSet.description);
        }

        if (xmlNamedSet.displayFolder != null && xmlNamedSet.displayFolder.length() > 0) {
            namedSet.setDisplayFolder(xmlNamedSet.displayFolder);
        }

        namedSet.setAnnotationMap(RolapHierarchy.createAnnotationMap(xmlNamedSet.annotations));
        this.namedSetList.add(formula);
        formulaList.add(formula);
    }

    private void preNamedSet(NamedSet xmlNamedSet, Set<String> nameSet, StringBuilder buf) {
        if (!nameSet.add(xmlNamedSet.name)) {
            throw MondrianResource.instance().NamedSetNotUnique.ex(xmlNamedSet.name, this.getName());
        } else {
            buf.append("SET ").append(Util.makeFqName(xmlNamedSet.name)).append(Util.nl).append(" AS ");
            Util.singleQuoteString(xmlNamedSet.getFormula(), buf);
            buf.append(Util.nl);
        }
    }

    private void postCalcMember(List<CalculatedMember> xmlCalcMembers, int i, Query queryExp, List<RolapMember> memberList) {
        CalculatedMember xmlCalcMember = (CalculatedMember)xmlCalcMembers.get(i);
        Formula formula = queryExp.getFormulas()[i];
        this.calculatedMemberList.add(formula);
        RolapMember member = (RolapMember)formula.getMdxMember();
        Boolean visible = xmlCalcMember.visible;
        if (visible == null) {
            visible = Boolean.TRUE;
        }

        member.setProperty(Property.VISIBLE.name, visible);
        member.setProperty(Property.DISPLAY_FOLDER.name, xmlCalcMember.displayFolder);
        if (xmlCalcMember.caption != null && xmlCalcMember.caption.length() > 0) {
            member.setProperty(Property.CAPTION.name, xmlCalcMember.caption);
        }

        if (xmlCalcMember.description != null && xmlCalcMember.description.length() > 0) {
            member.setProperty(Property.DESCRIPTION.name, xmlCalcMember.description);
        }

        if (xmlCalcMember.getFormatString() != null && xmlCalcMember.getFormatString().length() > 0) {
            member.setProperty(Property.FORMAT_STRING.name, xmlCalcMember.getFormatString());
        }

        RolapMember member1 = RolapUtil.strip(member);
        ((RolapCalculatedMember)member1).setAnnotationMap(RolapHierarchy.createAnnotationMap(xmlCalcMember.annotations));
        memberList.add(member);
    }

    private void preCalcMember(List<CalculatedMember> xmlCalcMembers, int j, StringBuilder buf, RolapCube cube, boolean errOnDup, Set<String> fqNames) {
        CalculatedMember xmlCalcMember = (CalculatedMember)xmlCalcMembers.get(j);
        if (xmlCalcMember.hierarchy != null && xmlCalcMember.dimension != null) {
            throw MondrianResource.instance().CalcMemberHasBothDimensionAndHierarchy.ex(xmlCalcMember.name, this.getName());
        } else {
            Hierarchy hierarchy = null;
            String dimName = null;
            if (xmlCalcMember.dimension != null) {
                dimName = xmlCalcMember.dimension;
                Dimension dimension = this.lookupDimension(new NameSegment(xmlCalcMember.dimension, Quoting.UNQUOTED));
                if (dimension != null) {
                    hierarchy = dimension.getHierarchy();
                }
            } else if (xmlCalcMember.hierarchy != null) {
                dimName = xmlCalcMember.hierarchy;
                hierarchy = (Hierarchy)this.getSchemaReader().withLocus().lookupCompound(this, Util.parseIdentifier(dimName), false, 3);
            }

            if (hierarchy == null) {
                throw MondrianResource.instance().CalcMemberHasBadDimension.ex(dimName, xmlCalcMember.name, this.getName());
            } else {
                String parentFqName;
                if (xmlCalcMember.parent != null) {
                    parentFqName = xmlCalcMember.parent;
                } else {
                    parentFqName = hierarchy.getUniqueNameSsas();
                }

                if (!hierarchy.getDimension().isMeasures()) {
                    OlapElement parent = Util.lookupCompound(this.getSchemaReader().withLocus(), this, Util.parseIdentifier(parentFqName), false, 0);
                    if (parent == null) {
                        throw MondrianResource.instance().CalcMemberHasUnknownParent.ex(parentFqName, xmlCalcMember.name, this.getName());
                    }

                    if (parent.getHierarchy() != hierarchy) {
                        throw MondrianResource.instance().CalcMemberHasDifferentParentAndHierarchy.ex(xmlCalcMember.name, this.getName(), hierarchy.getUniqueName());
                    }
                }

                String fqName = Util.makeFqName(parentFqName, xmlCalcMember.name);

                for(int i = 0; i < this.calculatedMemberList.size(); ++i) {
                    Formula formula = (Formula)this.calculatedMemberList.get(i);
                    if (formula.getName().equals(xmlCalcMember.name) && formula.getMdxMember().getHierarchy().equals(hierarchy)) {
                        if (errOnDup) {
                            throw MondrianResource.instance().CalcMemberNotUnique.ex(fqName, this.getName());
                        }

                        this.calculatedMemberList.remove(i);
                        --i;
                    }
                }

                if (!fqNames.add(fqName)) {
                    throw MondrianResource.instance().CalcMemberNotUnique.ex(fqName, this.getName());
                } else {
                    CalculatedMemberProperty[] xmlProperties = xmlCalcMember.memberProperties;
                    List<String> propNames = new ArrayList();
                    List<String> propExprs = new ArrayList();
                    this.validateMemberProps(xmlProperties, propNames, propExprs, xmlCalcMember.name);
                    int measureCount = cube.measuresHierarchy.getMemberReader().getMemberCount();

                    assert fqName.startsWith("[");

                    buf.append("MEMBER ").append(fqName).append(Util.nl).append("  AS ");
                    Util.singleQuoteString(xmlCalcMember.getFormula(), buf);
                    if (xmlCalcMember.cellFormatter != null) {
                        if (xmlCalcMember.cellFormatter.className != null) {
                            propNames.add(Property.CELL_FORMATTER.name);
                            propExprs.add(Util.quoteForMdx(xmlCalcMember.cellFormatter.className));
                        }

                        if (xmlCalcMember.cellFormatter.script != null) {
                            if (xmlCalcMember.cellFormatter.script.language != null) {
                                propNames.add(Property.CELL_FORMATTER_SCRIPT_LANGUAGE.name);
                                propExprs.add(Util.quoteForMdx(xmlCalcMember.cellFormatter.script.language));
                            }

                            propNames.add(Property.CELL_FORMATTER_SCRIPT.name);
                            propExprs.add(Util.quoteForMdx(xmlCalcMember.cellFormatter.script.cdata));
                        }
                    }

                    assert propNames.size() == propExprs.size();

                    this.processFormatStringAttribute(xmlCalcMember, buf);

                    for(int i = 0; i < propNames.size(); ++i) {
                        String name = (String)propNames.get(i);
                        String expr = (String)propExprs.get(i);
                        buf.append(",").append(Util.nl);
                        expr = this.removeSurroundingQuotesIfNumericProperty(name, expr);
                        buf.append(name).append(" = ").append(expr);
                    }

                    buf.append(",").append(Util.nl);
                    Util.quoteMdxIdentifier(Property.MEMBER_SCOPE.name, buf);
                    buf.append(" = 'CUBE'");
                    if (!propNames.contains(Property.MEMBER_ORDINAL.getName())) {
                        buf.append(",").append(Util.nl).append(Property.MEMBER_ORDINAL).append(" = ").append(measureCount + j);
                    }

                    buf.append(Util.nl);
                }
            }
        }
    }

    private String removeSurroundingQuotesIfNumericProperty(String name, String expr) {
        Property prop = Property.lookup(name, false);
        return prop != null && prop.getType().isNumeric() && this.isSurroundedWithQuotes(expr) && expr.length() > 2 ? expr.substring(1, expr.length() - 1) : expr;
    }

    private boolean isSurroundedWithQuotes(String expr) {
        return expr.startsWith("\"") && expr.endsWith("\"");
    }

    void processFormatStringAttribute(CalculatedMember xmlCalcMember, StringBuilder buf) {
        if (xmlCalcMember.formatString != null) {
            buf.append(",").append(Util.nl).append(Property.FORMAT_STRING.name).append(" = ").append(Util.quoteForMdx(xmlCalcMember.formatString));
        }

    }

    private void validateMemberProps(CalculatedMemberProperty[] xmlProperties, List<String> propNames, List<String> propExprs, String memberName) {
        if (xmlProperties != null) {
            CalculatedMemberProperty[] var5 = xmlProperties;
            int var6 = xmlProperties.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                CalculatedMemberProperty xmlProperty = var5[var7];
                if (xmlProperty.expression == null && xmlProperty.value == null) {
                    throw MondrianResource.instance().NeitherExprNorValueForCalcMemberProperty.ex(xmlProperty.name, memberName, this.getName());
                }

                if (xmlProperty.expression != null && xmlProperty.value != null) {
                    throw MondrianResource.instance().ExprAndValueForMemberProperty.ex(xmlProperty.name, memberName, this.getName());
                }

                propNames.add(xmlProperty.name);
                if (xmlProperty.expression != null) {
                    propExprs.add(xmlProperty.expression);
                } else {
                    propExprs.add(Util.quoteForMdx(xmlProperty.value));
                }
            }

        }
    }

    public RolapSchema getSchema() {
        return this.schema;
    }

    public mondrian.olap.NamedSet[] getNamedSets() {
        mondrian.olap.NamedSet[] namedSetsArray = new mondrian.olap.NamedSet[this.namedSetList.size()];

        for(int i = 0; i < this.namedSetList.size(); ++i) {
            namedSetsArray[i] = ((Formula)this.namedSetList.get(i)).getNamedSet();
        }

        return namedSetsArray;
    }

    public synchronized SchemaReader getSchemaReader() {
        if (this.schemaReader == null) {
            this.schemaReader = new RolapCube.RolapCubeSchemaReader(Util.createRootRole(this.schema));
        }

        return this.schemaReader;
    }

    public SchemaReader getSchemaReader(Role role) {
        return (SchemaReader)(role == null ? this.getSchemaReader() : new RolapCube.RolapCubeSchemaReader(role));
    }

    CubeDimension lookup(CubeDimension[] xmlDimensions, String name) {
        CubeDimension[] var3 = xmlDimensions;
        int var4 = xmlDimensions.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            CubeDimension cd = var3[var5];
            if (name.equals(cd.name)) {
                return cd;
            }
        }

        return null;
    }

    private void init(CubeDimension[] xmlDimensions) {
        Dimension[] var2 = this.dimensions;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Dimension dimension1 = var2[var4];
            RolapDimension dimension = (RolapDimension)dimension1;
            dimension.init(this.lookup(xmlDimensions, dimension.getName()));
        }

        this.register();
    }

    private void register() {
        if (!this.isVirtual()) {
            List<RolapBaseCubeMeasure> storedMeasures = new ArrayList();
            Iterator var2 = this.getMeasures().iterator();

            while(var2.hasNext()) {
                Member measure = (Member)var2.next();
                if (measure instanceof RolapBaseCubeMeasure) {
                    storedMeasures.add((RolapBaseCubeMeasure)measure);
                }
            }

            RolapStar star = this.getStar();
            Table table = star.getFactTable();
            Iterator var4 = storedMeasures.iterator();

            while(var4.hasNext()) {
                RolapBaseCubeMeasure storedMeasure = (RolapBaseCubeMeasure)var4.next();
                table.makeMeasure(storedMeasure);
            }

        }
    }

    public boolean isCacheAggregations() {
        return this.isVirtual() || this.star.isCacheAggregations();
    }

    public void setCacheAggregations(boolean cache) {
        if (!this.isVirtual()) {
            this.star.setCacheAggregations(cache);
        }

    }

    public void clearCachedAggregations() {
        this.clearCachedAggregations(false);
    }

    public void clearCachedAggregations(boolean forced) {
        if (this.isVirtual()) {
            Iterator var2 = this.schema.getStars().iterator();

            while(var2.hasNext()) {
                RolapStar star1 = (RolapStar)var2.next();
                star1.clearCachedAggregations(forced);
            }
        } else {
            this.star.clearCachedAggregations(forced);
        }

    }

    public RolapStar getStar() {
        return this.star;
    }

    private void createUsages(RolapCubeDimension dimension, CubeDimension xmlCubeDimension) {
        RolapCubeHierarchy[] hierarchies = (RolapCubeHierarchy[])dimension.getHierarchies();
        if (hierarchies.length == 1) {
            this.createUsage(hierarchies[0], xmlCubeDimension);
        } else {
            int cnt;
            if (xmlCubeDimension instanceof DimensionUsage && ((DimensionUsage)xmlCubeDimension).level != null) {
                DimensionUsage du = (DimensionUsage)xmlCubeDimension;
                cnt = 0;
                RolapCubeHierarchy[] var12 = hierarchies;
                int var13 = hierarchies.length;

                for(int var8 = 0; var8 < var13; ++var8) {
                    RolapCubeHierarchy hierarchy = var12[var8];
                    if (this.getLogger().isDebugEnabled()) {
                        this.getLogger().debug("RolapCube<init>: hierarchy=" + hierarchy.getName());
                    }

                    RolapLevel joinLevel = (RolapLevel)Util.lookupHierarchyLevel(hierarchy, du.level);
                    if (joinLevel != null) {
                        this.createUsage(hierarchy, xmlCubeDimension);
                        ++cnt;
                    }
                }

                if (cnt == 0) {
                    this.createUsage(hierarchies[0], xmlCubeDimension);
                }
            } else {
                RolapCubeHierarchy[] var4 = hierarchies;
                cnt = hierarchies.length;

                for(int var6 = 0; var6 < cnt; ++var6) {
                    RolapCubeHierarchy hierarchy = var4[var6];
                    if (this.getLogger().isDebugEnabled()) {
                        this.getLogger().debug("RolapCube<init>: hierarchy=" + hierarchy.getName());
                    }

                    this.createUsage(hierarchy, xmlCubeDimension);
                }
            }
        }

    }

    synchronized void createUsage(RolapCubeHierarchy hierarchy, CubeDimension cubeDim) {
        HierarchyUsage usage = new HierarchyUsage(this, hierarchy, cubeDim);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("RolapCube.createUsage: cube=" + this.getName() + ", hierarchy=" + hierarchy.getName() + ", usage=" + usage);
        }

        Iterator var4 = this.hierarchyUsages.iterator();

        HierarchyUsage hierUsage;
        do {
            if (!var4.hasNext()) {
                if (this.getLogger().isDebugEnabled()) {
                    this.getLogger().debug("RolapCube.createUsage: register " + usage);
                }

                this.hierarchyUsages.add(usage);
                return;
            }

            hierUsage = (HierarchyUsage)var4.next();
        } while(!hierUsage.equals(usage));

        this.getLogger().warn("RolapCube.createUsage: duplicate " + hierUsage);
    }

    private synchronized HierarchyUsage getUsageByName(String name) {
        Iterator var2 = this.hierarchyUsages.iterator();

        HierarchyUsage hierUsage;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            hierUsage = (HierarchyUsage)var2.next();
        } while(!hierUsage.getFullName().equals(name));

        return hierUsage;
    }

    public synchronized HierarchyUsage[] getUsages(Hierarchy hierarchy) {
        String name = hierarchy.getName();
        if (!name.equals(hierarchy.getDimension().getName()) && MondrianProperties.instance().SsasCompatibleNaming.get()) {
            name = hierarchy.getDimension().getName() + "." + name;
        }

        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("RolapCube.getUsages: name=" + name);
        }

        HierarchyUsage hierUsage = null;
        List<HierarchyUsage> list = null;
        Iterator var5 = this.hierarchyUsages.iterator();

        while(var5.hasNext()) {
            HierarchyUsage hu = (HierarchyUsage)var5.next();
            if (hu.getHierarchyName().equals(name)) {
                if (list != null) {
                    if (this.getLogger().isDebugEnabled()) {
                        this.getLogger().debug("RolapCube.getUsages: add list HierarchyUsage.name=" + hu.getName());
                    }

                    list.add(hu);
                } else if (hierUsage == null) {
                    hierUsage = hu;
                } else {
                    list = new ArrayList();
                    if (this.getLogger().isDebugEnabled()) {
                        this.getLogger().debug("RolapCube.getUsages: add list hierUsage.name=" + hierUsage.getName() + ", hu.name=" + hu.getName());
                    }

                    list.add(hierUsage);
                    list.add(hu);
                    hierUsage = null;
                }
            }
        }

        if (hierUsage != null) {
            return new HierarchyUsage[]{hierUsage};
        } else if (list != null) {
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("RolapCube.getUsages: return list");
            }

            return (HierarchyUsage[])list.toArray(new HierarchyUsage[list.size()]);
        } else {
            return new HierarchyUsage[0];
        }
    }

    synchronized HierarchyUsage getFirstUsage(Hierarchy hier) {
        HierarchyUsage hierarchyUsage = (HierarchyUsage)this.firstUsageMap.get(hier);
        if (hierarchyUsage == null) {
            HierarchyUsage[] hierarchyUsages = this.getUsages(hier);
            if (hierarchyUsages.length != 0) {
                hierarchyUsage = hierarchyUsages[0];
                this.firstUsageMap.put(hier, hierarchyUsage);
            }
        }

        return hierarchyUsage;
    }

    private synchronized HierarchyUsage[] getUsagesBySource(String source) {
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("RolapCube.getUsagesBySource: source=" + source);
        }

        HierarchyUsage hierUsage = null;
        List<HierarchyUsage> list = null;
        Iterator var4 = this.hierarchyUsages.iterator();

        while(var4.hasNext()) {
            HierarchyUsage hu = (HierarchyUsage)var4.next();
            String s = hu.getSource();
            if (s != null && s.equals(source)) {
                if (list != null) {
                    if (this.getLogger().isDebugEnabled()) {
                        this.getLogger().debug("RolapCube.getUsagesBySource: add list HierarchyUsage.name=" + hu.getName());
                    }

                    list.add(hu);
                } else if (hierUsage == null) {
                    hierUsage = hu;
                } else {
                    list = new ArrayList();
                    if (this.getLogger().isDebugEnabled()) {
                        this.getLogger().debug("RolapCube.getUsagesBySource: add list hierUsage.name=" + hierUsage.getName() + ", hu.name=" + hu.getName());
                    }

                    list.add(hierUsage);
                    list.add(hu);
                    hierUsage = null;
                }
            }
        }

        if (hierUsage != null) {
            return new HierarchyUsage[]{hierUsage};
        } else if (list != null) {
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("RolapCube.getUsagesBySource: return list");
            }

            return (HierarchyUsage[])list.toArray(new HierarchyUsage[list.size()]);
        } else {
            return new HierarchyUsage[0];
        }
    }

    void registerDimension(RolapCubeDimension dimension) {
        RolapStar star = this.getStar();
        Hierarchy[] hierarchies = dimension.getHierarchies();
        Hierarchy[] var4 = hierarchies;
        int var5 = hierarchies.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Hierarchy hierarchy1 = var4[var6];
            RolapHierarchy hierarchy = (RolapHierarchy)hierarchy1;
            RelationOrJoin relation = hierarchy.getRelation();
            if (relation != null) {
                RolapCubeLevel[] levels = (RolapCubeLevel[])hierarchy.getLevels();
                HierarchyUsage[] hierarchyUsages = this.getUsages(hierarchy);
                if (hierarchyUsages.length == 0) {
                    if (this.getLogger().isDebugEnabled()) {
                        StringBuilder buf = new StringBuilder(64);
                        buf.append("RolapCube.registerDimension: ");
                        buf.append("hierarchyUsages == null for cube=\"");
                        buf.append(this.name);
                        buf.append("\", hierarchy=\"");
                        buf.append(hierarchy.getName());
                        buf.append("\"");
                        this.getLogger().debug(buf.toString());
                    }
                } else {
                    HierarchyUsage[] var12 = hierarchyUsages;
                    int var13 = hierarchyUsages.length;

                    for(int var14 = 0; var14 < var13; ++var14) {
                        HierarchyUsage hierarchyUsage = var12[var14];
                        String usagePrefix = hierarchyUsage.getUsagePrefix();
                        Table table = star.getFactTable();
                        String levelName = hierarchyUsage.getLevelName();
                        Object relationTmp2;
                        if (relation instanceof Join) {
                            relationTmp2 = relation;
                            relation = reorder((RelationOrJoin)relation, levels);
                            if (relation == null && this.getLogger().isDebugEnabled()) {
                                this.getLogger().debug("RolapCube.registerDimension: after reorder relation==null");
                                this.getLogger().debug("RolapCube.registerDimension: reorder relationTmp1=" + format((RelationOrJoin)relationTmp2));
                            }
                        }

                        relationTmp2 = relation;
                        if (levelName != null) {
                            RolapLevel level = RolapLevel.lookupLevel(levels, levelName);
                            if (level == null) {
                                StringBuilder buf = new StringBuilder(64);
                                buf.append("For cube \"");
                                buf.append(this.getName());
                                buf.append("\" and HierarchyUsage [");
                                buf.append(hierarchyUsage);
                                buf.append("], there is no level with given");
                                buf.append(" level name \"");
                                buf.append(levelName);
                                buf.append("\"");
                                throw Util.newInternal(buf.toString());
                            }

                            if (relation instanceof Join) {
                                RolapLevel childLevel = (RolapLevel)level.getChildLevel();
                                if (childLevel != null) {
                                    String tableName = childLevel.getTableName();
                                    if (tableName != null) {
                                        relation = snip((RelationOrJoin)relation, tableName);
                                        if (relation == null && this.getLogger().isDebugEnabled()) {
                                            this.getLogger().debug("RolapCube.registerDimension: after snip relation==null");
                                            this.getLogger().debug("RolapCube.registerDimension: snip relationTmp2=" + format((RelationOrJoin)relationTmp2));
                                        }
                                    }
                                }
                            }
                        }

                        if (!((RelationOrJoin)relation).equals(table.getRelation())) {
                            if (hierarchyUsage.getForeignKey() == null) {
                                throw MondrianResource.instance().HierarchyMustHaveForeignKey.ex(hierarchy.getName(), this.getName());
                            }

                            Column column = new Column(table.getAlias(), hierarchyUsage.getForeignKey());
                            Condition joinCondition = new Condition(column, hierarchyUsage.getJoinExp());
                            if (hierarchy.getXmlHierarchy() != null && hierarchy.getXmlHierarchy().primaryKeyTable != null && relation instanceof Join && ((Join)relation).right instanceof mondrian.olap.MondrianDef.Table && ((mondrian.olap.MondrianDef.Table)((Join)relation).right).getAlias() != null && ((mondrian.olap.MondrianDef.Table)((Join)relation).right).getAlias().equals(hierarchy.getXmlHierarchy().primaryKeyTable)) {
                                Join newRelation = new Join();
                                newRelation.left = ((Join)relation).right;
                                newRelation.right = ((Join)relation).left;
                                newRelation.leftAlias = ((Join)relation).getRightAlias();
                                newRelation.rightAlias = ((Join)relation).getLeftAlias();
                                newRelation.leftKey = ((Join)relation).rightKey;
                                newRelation.rightKey = ((Join)relation).leftKey;
                                relation = newRelation;
                            }

                            table = table.addJoin(this, (RelationOrJoin)relation, joinCondition);
                        }

                        mondrian.rolap.RolapStar.Column parentColumn = null;
                        int var23;
                        RolapCubeLevel level;
                        RolapCubeLevel[] var29;
                        int var32;
                        if (levelName != null) {
                            var29 = levels;
                            var32 = levels.length;

                            for(var23 = 0; var23 < var32; ++var23) {
                                level = var29[var23];
                                if (level.getKeyExp() != null) {
                                    parentColumn = this.makeColumns(table, level, parentColumn, usagePrefix);
                                }

                                if (levelName.equals(level.getName())) {
                                    break;
                                }
                            }
                        } else {
                            var29 = levels;
                            var32 = levels.length;

                            for(var23 = 0; var23 < var32; ++var23) {
                                level = var29[var23];
                                if (level.getKeyExp() != null) {
                                    parentColumn = this.makeColumns(table, level, parentColumn, usagePrefix);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    protected mondrian.rolap.RolapStar.Column makeColumns(Table table, RolapCubeLevel level, mondrian.rolap.RolapStar.Column parentColumn, String usagePrefix) {
        String tableName = level.getTableName();
        if (tableName != null) {
            if (table.getAlias().equals(tableName)) {
                parentColumn = table.makeColumns(this, level, parentColumn, usagePrefix);
            } else if (table.equalsTableName(tableName)) {
                parentColumn = table.makeColumns(this, level, parentColumn, usagePrefix);
            } else {
                Table t = table.findAncestor(tableName);
                if (t != null) {
                    parentColumn = t.makeColumns(this, level, parentColumn, usagePrefix);
                } else {
                    this.getLogger().warn("RolapCube.makeColumns: for cube \"" + this.getName() + "\" the Level \"" + level.getName() + "\" has a table name attribute \"" + tableName + "\" but the associated RolapStar does not have a table with that name.");
                    parentColumn = table.makeColumns(this, level, parentColumn, usagePrefix);
                }
            }
        } else {
            parentColumn = table.makeColumns(this, level, parentColumn, usagePrefix);
        }

        return parentColumn;
    }

    private static String format(RelationOrJoin relation) {
        StringBuilder buf = new StringBuilder();
        format(relation, buf, "");
        return buf.toString();
    }

    private static void format(RelationOrJoin relation, StringBuilder buf, String indent) {
        if (relation instanceof mondrian.olap.MondrianDef.Table) {
            mondrian.olap.MondrianDef.Table table = (mondrian.olap.MondrianDef.Table)relation;
            buf.append(indent);
            buf.append(table.name);
            if (table.alias != null) {
                buf.append('(');
                buf.append(table.alias);
                buf.append(')');
            }

            buf.append(Util.nl);
        } else {
            Join join = (Join)relation;
            String subindent = indent + "  ";
            buf.append(indent);
            buf.append(join.getLeftAlias());
            buf.append('.');
            buf.append(join.leftKey);
            buf.append('=');
            buf.append(join.getRightAlias());
            buf.append('.');
            buf.append(join.rightKey);
            buf.append(Util.nl);
            format(join.left, buf, subindent);
            format(join.right, buf, indent);
        }

    }

    public boolean shouldIgnoreUnrelatedDimensions(String baseCubeName) {
        return this.cubeUsages != null && this.cubeUsages.shouldIgnoreUnrelatedDimensions(baseCubeName);
    }

    public List<RolapHierarchy> getHierarchies() {
        return this.hierarchyList;
    }

    public boolean isLoadInProgress() {
        return this.loadInProgress || this.getSchema().getSchemaLoadDate() == null;
    }

    private static RelationOrJoin reorder(RelationOrJoin relation, RolapLevel[] levels) {
        if (levels.length < 2) {
            return relation;
        } else {
            Map<String, RolapCube.RelNode> nodeMap = new HashMap();

            for(int i = 0; i < levels.length; ++i) {
                RolapLevel level = levels[i];
                if (!level.isAll()) {
                    String tableName = level.getTableName();
                    if (tableName == null) {
                        return relation;
                    }

                    RolapCube.RelNode rnode = new RolapCube.RelNode(tableName, i);
                    nodeMap.put(tableName, rnode);
                }
            }

            if (!validateNodes(relation, nodeMap)) {
                return relation;
            } else {
                relation = copy(relation);
                leftToRight(relation, nodeMap);
                topToBottom(relation);
                return relation;
            }
        }
    }

    private static boolean validateNodes(RelationOrJoin relation, Map<String, RolapCube.RelNode> map) {
        if (relation instanceof Relation) {
            Relation table = (Relation)relation;
            RolapCube.RelNode relNode = RolapCube.RelNode.lookup(table, map);
            return relNode != null;
        } else if (!(relation instanceof Join)) {
            throw Util.newInternal("bad relation type " + relation);
        } else {
            Join join = (Join)relation;
            return validateNodes(join.left, map) && validateNodes(join.right, map);
        }
    }

    private static int leftToRight(RelationOrJoin relation, Map<String, RolapCube.RelNode> map) {
        if (relation instanceof Relation) {
            Relation table = (Relation)relation;
            RolapCube.RelNode relNode = RolapCube.RelNode.lookup(table, map);
            relNode.table = table;
            return relNode.depth;
        } else if (relation instanceof Join) {
            Join join = (Join)relation;
            int leftDepth = leftToRight(join.left, map);
            int rightDepth = leftToRight(join.right, map);
            if (rightDepth > leftDepth) {
                String leftAlias = join.leftAlias;
                String leftKey = join.leftKey;
                RelationOrJoin left = join.left;
                join.leftAlias = join.rightAlias;
                join.leftKey = join.rightKey;
                join.left = join.right;
                join.rightAlias = leftAlias;
                join.rightKey = leftKey;
                join.right = left;
            }

            return leftDepth;
        } else {
            throw Util.newInternal("bad relation type " + relation);
        }
    }

    private static void topToBottom(RelationOrJoin relation) {
        Join jleft;
        if (!(relation instanceof mondrian.olap.MondrianDef.Table) && relation instanceof Join) {
            for(Join join = (Join)relation; join.left instanceof Join; join.leftKey = jleft.leftKey) {
                jleft = (Join)join.left;
                join.right = new Join(join.leftAlias, join.leftKey, jleft.right, join.rightAlias, join.rightKey, join.right);
                join.left = jleft.left;
                join.rightAlias = jleft.rightAlias;
                join.rightKey = jleft.rightKey;
                join.leftAlias = jleft.leftAlias;
            }
        }

    }

    private static RelationOrJoin copy(RelationOrJoin relation) {
        if (relation instanceof mondrian.olap.MondrianDef.Table) {
            mondrian.olap.MondrianDef.Table table = (mondrian.olap.MondrianDef.Table)relation;
            return new mondrian.olap.MondrianDef.Table(table);
        } else if (relation instanceof InlineTable) {
            InlineTable table = (InlineTable)relation;
            return new InlineTable(table);
        } else if (relation instanceof Join) {
            Join join = (Join)relation;
            RelationOrJoin left = copy(join.left);
            RelationOrJoin right = copy(join.right);
            return new Join(join.leftAlias, join.leftKey, left, join.rightAlias, join.rightKey, right);
        } else {
            throw Util.newInternal("bad relation type " + relation);
        }
    }

    private static RelationOrJoin snip(RelationOrJoin relation, String tableName) {
        if (!(relation instanceof mondrian.olap.MondrianDef.Table)) {
            if (relation instanceof Join) {
                Join join = (Join)relation;
                RelationOrJoin left = snip(join.left, tableName);
                if (left == null) {
                    return join.right;
                } else {
                    join.left = left;
                    RelationOrJoin right = snip(join.right, tableName);
                    if (right == null) {
                        return join.left;
                    } else {
                        join.right = right;
                        return join;
                    }
                }
            } else {
                throw Util.newInternal("bad relation type " + relation);
            }
        } else {
            mondrian.olap.MondrianDef.Table table = (mondrian.olap.MondrianDef.Table)relation;
            return table.alias != null && table.alias.equals(tableName) ? null : (table.name.equals(tableName) ? null : table);
        }
    }

    public Member[] getMembersForQuery(String query, List<Member> calcMembers) {
        throw new UnsupportedOperationException();
    }

    public RolapHierarchy getTimeHierarchy(String funName) {
        Iterator var2 = this.hierarchyList.iterator();

        RolapHierarchy hierarchy;
        do {
            if (!var2.hasNext()) {
                throw MondrianResource.instance().NoTimeDimensionInCube.ex(funName);
            }

            hierarchy = (RolapHierarchy)var2.next();
        } while(hierarchy.getDimension().getDimensionType() != DimensionType.TimeDimension);

        return hierarchy;
    }

    public Set<Dimension> nonJoiningDimensions(Member[] tuple) {
        Set<Dimension> otherDims = new HashSet();
        Member[] var3 = tuple;
        int var4 = tuple.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Member member = var3[var5];
            if (!member.isCalculated()) {
                otherDims.add(member.getDimension());
            }
        }

        return this.nonJoiningDimensions((Set)otherDims);
    }

    public Set<Dimension> nonJoiningDimensions(Set<Dimension> otherDims) {
        Dimension[] baseCubeDimensions = this.getDimensions();
        Set<String> baseCubeDimNames = new HashSet();
        Dimension[] var4 = baseCubeDimensions;
        int var5 = baseCubeDimensions.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Dimension baseCubeDimension = var4[var6];
            baseCubeDimNames.add(baseCubeDimension.getUniqueName());
        }

        Set<Dimension> nonJoiningDimensions = new HashSet();
        Iterator var9 = otherDims.iterator();

        while(var9.hasNext()) {
            Dimension otherDim = (Dimension)var9.next();
            if (!baseCubeDimNames.contains(otherDim.getUniqueName())) {
                nonJoiningDimensions.add(otherDim);
            }
        }

        return nonJoiningDimensions;
    }

    public List<Member> getMeasures() {
        Level measuresLevel = this.dimensions[0].getHierarchies()[0].getLevels()[0];
        return this.getSchemaReader().getLevelMembers(measuresLevel, true);
    }

    public RelationOrJoin getFact() {
        return this.fact;
    }

    public boolean isVirtual() {
        return this.fact == null;
    }

    RolapMeasure getFactCountMeasure() {
        return this.factCountMeasure;
    }

    RolapMeasure getAtomicCellCountMeasure() {
        return this.factCountMeasure;
    }

    RolapHierarchy findBaseCubeHierarchy(RolapHierarchy hierarchy) {
        for(int i = 0; i < this.getDimensions().length; ++i) {
            Dimension dimension = this.getDimensions()[i];
            if (dimension.getName().equals(hierarchy.getDimension().getName())) {
                for(int j = 0; j < dimension.getHierarchies().length; ++j) {
                    Hierarchy hier = dimension.getHierarchies()[j];
                    if (hier.getName().equals(hierarchy.getName())) {
                        return (RolapHierarchy)hier;
                    }
                }
            }
        }

        return null;
    }

    public RolapCubeLevel findBaseCubeLevel(RolapLevel level) {
        if (this.virtualToBaseMap.containsKey(level)) {
            return (RolapCubeLevel)this.virtualToBaseMap.get(level);
        } else {
            String levelDimName = level.getDimension().getName();
            String levelHierName = level.getHierarchy().getName();
            boolean isClosure = false;
            String closDimName = null;
            String closHierName = null;
            if (levelDimName.endsWith("$Closure")) {
                isClosure = true;
                closDimName = levelDimName.substring(0, levelDimName.length() - 8);
                closHierName = levelHierName.substring(0, levelHierName.length() - 8);
            }

            Dimension[] var7 = this.getDimensions();
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                Dimension dimension = var7[var9];
                String dimensionName = dimension.getName();
                if (dimensionName.equals(levelDimName) || isClosure && dimensionName.equals(closDimName)) {
                    Hierarchy[] var12 = dimension.getHierarchies();
                    int var13 = var12.length;

                    for(int var14 = 0; var14 < var13; ++var14) {
                        Hierarchy hier = var12[var14];
                        String hierarchyName = hier.getName();
                        if (hierarchyName.equals(levelHierName) || isClosure && hierarchyName.equals(closHierName)) {
                            if (isClosure) {
                                RolapCubeLevel baseLevel = ((RolapCubeLevel)hier.getLevels()[1]).getClosedPeer();
                                this.virtualToBaseMap.put(level, baseLevel);
                                return baseLevel;
                            }

                            Level[] var17 = hier.getLevels();
                            int var18 = var17.length;

                            for(int var19 = 0; var19 < var18; ++var19) {
                                Level lvl = var17[var19];
                                if (lvl.getName().equals(level.getName())) {
                                    RolapCubeLevel baseLevel = (RolapCubeLevel)lvl;
                                    this.virtualToBaseMap.put(level, baseLevel);
                                    return baseLevel;
                                }
                            }
                        }
                    }
                }
            }

            return null;
        }
    }

    RolapCubeDimension createDimension(CubeDimension xmlCubeDimension, Schema xmlSchema) {
        RolapCubeDimension dimension = this.getOrCreateDimension(xmlCubeDimension, this.schema, xmlSchema, this.dimensions.length, this.hierarchyList);
        if (!this.isVirtual()) {
            this.createUsages(dimension, xmlCubeDimension);
        }

        this.registerDimension(dimension);
        dimension.init(xmlCubeDimension);
        this.dimensions = (Dimension[])Util.append(this.dimensions, dimension);
        return dimension;
    }

    public OlapElement lookupChild(SchemaReader schemaReader, Segment s) {
        return this.lookupChild(schemaReader, s, MatchType.EXACT);
    }

    public OlapElement lookupChild(SchemaReader schemaReader, Segment s, MatchType matchType) {
        if (!(s instanceof NameSegment)) {
            return null;
        } else {
            NameSegment nameSegment = (NameSegment)s;
            String status = null;
            OlapElement oe = null;
            if (matchType == MatchType.EXACT_SCHEMA) {
                oe = super.lookupChild(schemaReader, nameSegment, MatchType.EXACT_SCHEMA);
            } else {
                oe = super.lookupChild(schemaReader, nameSegment, MatchType.EXACT);
            }

            if (oe == null) {
                HierarchyUsage[] usages = this.getUsagesBySource(nameSegment.name);
                if (usages.length > 0) {
                    StringBuilder buf = new StringBuilder(64);
                    buf.append("RolapCube.lookupChild: ");
                    buf.append("In cube \"");
                    buf.append(this.getName());
                    buf.append("\" use of unaliased Dimension name \"");
                    buf.append(nameSegment);
                    if (usages.length == 1) {
                        buf.append("\" rather than the alias name ");
                        buf.append("\"");
                        buf.append(usages[0].getName());
                        buf.append("\" ");
                        this.getLogger().error(buf.toString());
                        throw new MondrianException(buf.toString());
                    }

                    buf.append("\" rather than one of the alias names ");
                    HierarchyUsage[] var9 = usages;
                    int var10 = usages.length;

                    for(int var11 = 0; var11 < var10; ++var11) {
                        HierarchyUsage usage = var9[var11];
                        buf.append("\"");
                        buf.append(usage.getName());
                        buf.append("\" ");
                    }

                    this.getLogger().error(buf.toString());
                    throw new MondrianException(buf.toString());
                }
            }

            if (this.getLogger().isDebugEnabled()) {
                if (!nameSegment.matches("Measures")) {
                    HierarchyUsage hierUsage = this.getUsageByName(nameSegment.name);
                    if (hierUsage == null) {
                        status = "hierUsage == null";
                    } else {
                        status = "hierUsage == " + (hierUsage.isShared() ? "shared" : "not shared");
                    }
                }

                StringBuilder buf = new StringBuilder(64);
                buf.append("RolapCube.lookupChild: ");
                buf.append("name=");
                buf.append(this.getName());
                buf.append(", childname=");
                buf.append(nameSegment);
                if (status != null) {
                    buf.append(", status=");
                    buf.append(status);
                }

                if (oe == null) {
                    buf.append(" returning null");
                } else {
                    buf.append(" returning elementname=").append(oe.getName());
                }

                this.getLogger().debug(buf.toString());
            }

            return oe;
        }
    }

    public Hierarchy getMeasuresHierarchy() {
        return this.measuresHierarchy;
    }

    public List<RolapMember> getMeasuresMembers() {
        return this.measuresHierarchy.getMemberReader().getMembers();
    }

    public Formula createNamedSet(String xml) {
        NamedSet xmlNamedSet;
        try {
            Parser xmlParser = XOMUtil.createDefaultParser();
            DOMWrapper def = xmlParser.parse(xml);
            String tagName = def.getTagName();
            if (!tagName.equals("NamedSet")) {
                throw new XOMException("Got <" + tagName + "> when expecting <NamedSet>");
            }

            xmlNamedSet = new NamedSet(def);
        } catch (XOMException var10) {
            throw Util.newError(var10, "Error while creating named set from XML [" + xml + "]");
        }

        Formula var12;
        try {
            this.loadInProgress = true;
            List<Formula> setList = new ArrayList();
            this.createCalcMembersAndNamedSets(Collections.emptyList(), Collections.singletonList(xmlNamedSet), new ArrayList(), setList, this, true);

            assert setList.size() == 1;

            var12 = (Formula)setList.get(0);
        } finally {
            this.loadInProgress = false;
        }

        return var12;
    }

    public Member createCalculatedMember(String xml) {
        CalculatedMember xmlCalcMember;
        try {
            Parser xmlParser = XOMUtil.createDefaultParser();
            DOMWrapper def = xmlParser.parse(xml);
            String tagName = def.getTagName();
            if (!tagName.equals("CalculatedMember")) {
                throw new XOMException("Got <" + tagName + "> when expecting <CalculatedMember>");
            }

            xmlCalcMember = new CalculatedMember(def);
        } catch (XOMException var10) {
            throw Util.newError(var10, "Error while creating calculated member from XML [" + xml + "]");
        }

        Member var12;
        try {
            this.loadInProgress = true;
            List<RolapMember> memberList = new ArrayList();
            this.createCalcMembersAndNamedSets(Collections.singletonList(xmlCalcMember), Collections.emptyList(), memberList, new ArrayList(), this, true);

            assert memberList.size() == 1;

            var12 = (Member)memberList.get(0);
        } finally {
            this.loadInProgress = false;
        }

        return var12;
    }

    RolapMember createCalculatedMember(RolapHierarchy hierarchy, String name, Calc calc) {
        List<Segment> segmentList = new ArrayList();
        segmentList.addAll(Util.parseIdentifier(hierarchy.getUniqueName()));
        segmentList.add(new NameSegment(name));
        Formula formula = new Formula(new Id(segmentList), createDummyExp(calc), new MemberProperty[0]);
        Statement statement = this.schema.getInternalConnection().getInternalStatement();

        RolapMember var8;
        try {
            Query query = new Query(statement, this, new Formula[]{formula}, new QueryAxis[0], (QueryAxis)null, new QueryPart[0], new Parameter[0], false);
            query.createValidator().validate(formula);
            this.calculatedMemberList.add(formula);
            var8 = (RolapMember)formula.getMdxMember();
        } finally {
            statement.close();
        }

        return var8;
    }

    public void createNamedSet(Formula formula) {
        Statement statement = this.schema.getInternalConnection().getInternalStatement();

        try {
            Query query = new Query(statement, this, new Formula[]{formula}, new QueryAxis[0], (QueryAxis)null, new QueryPart[0], new Parameter[0], false);
            query.createValidator().validate(formula);
            this.namedSetList.add(formula);
        } finally {
            statement.close();
        }

    }

    public RolapMember createCalculatedMember(Formula formula) {
        Statement statement = this.schema.getInternalConnection().getInternalStatement();

        RolapMember var4;
        try {
            Query query = new Query(statement, this, new Formula[]{formula}, new QueryAxis[0], (QueryAxis)null, new QueryPart[0], new Parameter[0], false);
            query.createValidator().validate(formula);
            this.calculatedMemberList.add(formula);
            var4 = (RolapMember)formula.getMdxMember();
        } finally {
            statement.close();
        }

        return var4;
    }

    static Exp createDummyExp(final Calc calc) {
        return new ResolvedFunCall(new FunDefBase("dummy", (String)null, "fn") {
            public Calc compileCall(ResolvedFunCall call, ExpCompiler compiler) {
                return calc;
            }
        }, new Exp[0], calc.getType());
    }

    public List<RolapCube> getBaseCubes() {
        if (this.baseCubes == null) {
            this.baseCubes = findBaseCubes(this);
        }

        return this.baseCubes;
    }

    private static List<RolapCube> findBaseCubes(RolapCube cube) {
        if (!cube.isVirtual()) {
            return Collections.singletonList(cube);
        } else {
            List<RolapCube> cubesList = new ArrayList();
            Set<RolapCube> cubes = new TreeSet(new RolapCube.CubeComparator());
            Iterator var3 = cube.getMeasures().iterator();

            while(var3.hasNext()) {
                Member member = (Member)var3.next();
                if (member instanceof RolapStoredMeasure) {
                    cubes.add(((RolapStoredMeasure)member).getCube());
                } else if (member instanceof RolapCalculatedMeasure) {
                    RolapCube baseCube = ((RolapCalculatedMeasure)member).getBaseCube();
                    if (baseCube != null) {
                        cubes.add(baseCube);
                    }
                }
            }

            cubesList.addAll(cubes);
            return cubesList;
        }
    }

    public void flushCache(RolapConnection rolapConnection) {
        CacheControl cacheControl = rolapConnection.getCacheControl((PrintWriter)null);
        cacheControl.flush(cacheControl.createMeasuresRegion(this));
        Iterator var3 = this.hierarchyList.iterator();

        while(var3.hasNext()) {
            RolapHierarchy rolapHierarchy = (RolapHierarchy)var3.next();
            if (rolapHierarchy instanceof RolapCubeHierarchy) {
                RolapCubeHierarchy rolapCubeHierarchy = (RolapCubeHierarchy)rolapHierarchy;
                MemberReader memberReader = rolapCubeHierarchy.getMemberReader();
                if (memberReader instanceof CacheRolapCubeHierarchyMemberReader) {
                    CacheRolapCubeHierarchyMemberReader crhmr = (CacheRolapCubeHierarchyMemberReader)memberReader;
                    ((MemberCacheHelper)crhmr.getMemberCache()).flushCache();
                    crhmr.getRolapCubeMemberCacheHelper().flushCache();
                }

                RolapHierarchy sharedRolapHierarchy = rolapCubeHierarchy.getRolapHierarchy();
                memberReader = sharedRolapHierarchy.getMemberReader();
                if (memberReader instanceof SmartMemberReader) {
                    SmartMemberReader smartMemberReader = (SmartMemberReader)memberReader;
                    MemberCacheHelper memberCacheHelper = (MemberCacheHelper)smartMemberReader.getMemberCache();
                    memberCacheHelper.flushCache();
                }
            }
        }

    }

    public RolapDrillThroughAction getDefaultDrillThroughAction() {
        Iterator var1 = this.actionList.iterator();

        while(var1.hasNext()) {
            RolapAction action = (RolapAction)var1.next();
            if (action instanceof RolapDrillThroughAction) {
                RolapDrillThroughAction rolapDrillThroughAction = (RolapDrillThroughAction)action;
                if (rolapDrillThroughAction.getIsDefault()) {
                    return rolapDrillThroughAction;
                }
            }
        }

        return null;
    }

    public static class CubeComparator implements Comparator<RolapCube> {
        public int compare(RolapCube c1, RolapCube c2) {
            return c1.getName().compareTo(c2.getName());
        }
    }

    private class MeasureFinder extends MdxVisitorImpl {
        private RolapCube virtualCube;
        private RolapCube baseCube;
        private RolapLevel measuresLevel;
        private List<RolapVirtualCubeMeasure> measuresFound;
        private List<RolapCalculatedMember> calcMembersSeen;

        public MeasureFinder(RolapCube virtualCube, RolapCube baseCube, RolapLevel measuresLevel) {
            this.virtualCube = virtualCube;
            this.baseCube = baseCube;
            this.measuresLevel = measuresLevel;
            this.measuresFound = new ArrayList();
            this.calcMembersSeen = new ArrayList();
        }

        public Object visit(MemberExpr memberExpr) {
            Member member = memberExpr.getMember();
            if (member instanceof RolapCalculatedMember) {
                if (this.calcMembersSeen.contains(member)) {
                    return null;
                } else {
                    RolapCalculatedMember calcMember = (RolapCalculatedMember)member;
                    Formula formula = calcMember.getFormula();
                    if (!this.calcMembersSeen.contains(calcMember)) {
                        this.calcMembersSeen.add(calcMember);
                    }

                    formula.accept(this);
                    this.virtualCube.setMeasuresHierarchyMemberReader(new CacheMemberReader(new MeasureMemberSource(this.virtualCube.measuresHierarchy, Util.cast(this.measuresFound))));
                    CalculatedMember xmlCalcMember = RolapCube.this.schema.lookupXmlCalculatedMember(calcMember.getUniqueName(), this.baseCube.name);
                    RolapCube.this.createCalcMembersAndNamedSets(Collections.singletonList(xmlCalcMember), Collections.emptyList(), new ArrayList(), new ArrayList(), this.virtualCube, false);
                    return null;
                }
            } else {
                if (member instanceof RolapBaseCubeMeasure) {
                    RolapBaseCubeMeasure baseMeasure = (RolapBaseCubeMeasure)member;
                    RolapVirtualCubeMeasure virtualCubeMeasure = new RolapVirtualCubeMeasure((RolapMember)null, this.measuresLevel, baseMeasure, Collections.emptyMap());
                    if (!this.measuresFound.contains(virtualCubeMeasure)) {
                        this.measuresFound.add(virtualCubeMeasure);
                    }
                }

                return null;
            }
        }

        public List<RolapVirtualCubeMeasure> getMeasuresFound() {
            return this.measuresFound;
        }
    }

    private class RolapCubeSchemaReader extends RolapSchemaReader implements Namespace {
        public RolapCubeSchemaReader(Role role) {
            super(role, RolapCube.this.schema);

            assert role != null : "precondition: role != null";

        }

        public List<Member> getLevelMembers(Level level, boolean includeCalculated) {
            return this.getLevelMembers(level, includeCalculated, (Evaluator)null);
        }

        public List<Member> getLevelMembers(Level level, boolean includeCalculated, Evaluator context) {
            List<Member> members = super.getLevelMembers(level, false, context);
            if (includeCalculated) {
                members = Util.addLevelCalculatedMembers(this, level, members);
            }

            return members;
        }

        public Member getCalculatedMember(List<Segment> nameParts) {
            String uniqueName = Util.implode(nameParts);
            Iterator var3 = RolapCube.this.calculatedMemberList.iterator();

            Formula formula;
            String formulaUniqueName;
            do {
                if (!var3.hasNext()) {
                    return null;
                }

                formula = (Formula)var3.next();
                formulaUniqueName = formula.getMdxMember().getUniqueName();
            } while(!formulaUniqueName.equals(uniqueName) || !this.getRole().canAccess(formula.getMdxMember()));

            return formula.getMdxMember();
        }

        public mondrian.olap.NamedSet getNamedSet(List<Segment> segments) {
            if (segments.size() == 1) {
                Segment segment = (Segment)segments.get(0);
                Iterator var3 = RolapCube.this.namedSetList.iterator();

                while(var3.hasNext()) {
                    Formula namedSet = (Formula)var3.next();
                    if (segment.matches(namedSet.getName())) {
                        return namedSet.getNamedSet();
                    }
                }
            }

            return super.getNamedSet(segments);
        }

        public List<Member> getCalculatedMembers(Hierarchy hierarchy) {
            ArrayList<Member> list = new ArrayList();
            if (this.getRole().getAccess(hierarchy) == Access.NONE) {
                return list;
            } else {
                Iterator var3 = this.getCalculatedMembers().iterator();

                while(var3.hasNext()) {
                    Member member = (Member)var3.next();
                    if (member.getHierarchy().equals(hierarchy)) {
                        list.add(member);
                    }
                }

                return list;
            }
        }

        public List<Member> getCalculatedMembers(Level level) {
            List<Member> list = new ArrayList();
            if (this.getRole().getAccess(level) == Access.NONE) {
                return list;
            } else {
                Iterator var3 = this.getCalculatedMembers().iterator();

                while(var3.hasNext()) {
                    Member member = (Member)var3.next();
                    if (member.getLevel().equals(level)) {
                        list.add(member);
                    }
                }

                return list;
            }
        }

        public List<Member> getCalculatedMembers() {
            List<Member> list = new ArrayList();
            Iterator var2 = RolapCube.this.calculatedMemberList.iterator();

            while(var2.hasNext()) {
                Formula formula = (Formula)var2.next();
                Member member = formula.getMdxMember();
                if (this.getRole().canAccess(member)) {
                    list.add(member);
                }
            }

            return list;
        }

        public SchemaReader withoutAccessControl() {
            assert this.getClass() == RolapCube.RolapCubeSchemaReader.class : "Derived class " + this.getClass() + " must override method";

            return RolapCube.this.getSchemaReader();
        }

        public Member getMemberByUniqueName(List<Segment> uniqueNameParts, boolean failIfNotFound, MatchType matchType) {
            Member member = (Member)this.lookupCompound(RolapCube.this, uniqueNameParts, failIfNotFound, 6, matchType);
            if (member == null) {
                assert !failIfNotFound;

                return null;
            } else if (this.getRole().canAccess(member)) {
                return member;
            } else if (failIfNotFound) {
                throw Util.newElementNotFoundException(6, new IdentifierNode(Util.toOlap4j(uniqueNameParts)));
            } else {
                return null;
            }
        }

        public mondrian.olap.Cube getCube() {
            return RolapCube.this;
        }

        public List<Namespace> getNamespaces() {
            List<Namespace> list = new ArrayList();
            list.add(this);
            list.addAll(this.schema.getSchemaReader().getNamespaces());
            return list;
        }

        public OlapElement lookupChild(OlapElement parent, IdentifierSegment segment, MatchType matchType) {
            return this.lookupChild(parent, segment);
        }

        public OlapElement lookupChild(OlapElement parent, IdentifierSegment segment) {
            Iterator var3 = RolapCube.this.calculatedMemberList.iterator();

            Formula formula;
            while(var3.hasNext()) {
                formula = (Formula)var3.next();
                if (NameResolver.matches(formula, parent, segment)) {
                    return formula.getMdxMember();
                }
            }

            if (parent == RolapCube.this) {
                var3 = RolapCube.this.namedSetList.iterator();

                while(var3.hasNext()) {
                    formula = (Formula)var3.next();
                    if (Util.matches(segment, formula.getName())) {
                        return formula.getNamedSet();
                    }
                }
            }

            return null;
        }
    }

    private static class RelNode {
        private int depth;
        private String alias;
        private Relation table;

        private static RolapCube.RelNode lookup(Relation table, Map<String, RolapCube.RelNode> map) {
            if (table instanceof mondrian.olap.MondrianDef.Table) {
                RolapCube.RelNode relNode = (RolapCube.RelNode)map.get(((mondrian.olap.MondrianDef.Table)table).name);
                if (relNode != null) {
                    return relNode;
                }
            }

            return (RolapCube.RelNode)map.get(table.getAlias());
        }

        RelNode(String alias, int depth) {
            this.alias = alias;
            this.depth = depth;
        }
    }
}