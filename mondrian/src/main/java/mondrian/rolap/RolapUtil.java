/* Decompiler 486ms, total 820ms, lines 579 */
package mondrian.rolap;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import javax.sql.DataSource;
import mondrian.calc.ExpCompiler;
import mondrian.olap.Evaluator;
import mondrian.olap.Formula;
import mondrian.olap.MatchType;
import mondrian.olap.Member;
import mondrian.olap.MondrianException;
import mondrian.olap.MondrianProperties;
import mondrian.olap.NativeEvaluationUnsupportedException;
import mondrian.olap.SchemaReader;
import mondrian.olap.Util;
import mondrian.olap.Id.NameSegment;
import mondrian.olap.Id.Quoting;
import mondrian.olap.Id.Segment;
import mondrian.olap.MondrianDef.InlineTable;
import mondrian.olap.MondrianDef.Relation;
import mondrian.olap.MondrianDef.RelationOrJoin;
import mondrian.olap.MondrianDef.Row;
import mondrian.olap.MondrianDef.Schema;
import mondrian.olap.MondrianDef.SchemaView;
import mondrian.olap.MondrianDef.Table;
import mondrian.olap.MondrianDef.Value;
import mondrian.olap.MondrianDef.View;
import mondrian.olap.Util.Functor1;
import mondrian.olap.fun.FunUtil;
import mondrian.resource.MondrianResource;
import mondrian.rolap.RolapDependencyTestingEvaluator.DteCompiler;
import mondrian.rolap.RolapHierarchy.LimitedRollupMember;
import mondrian.rolap.RolapProfilingEvaluator.ProfilingEvaluatorCompiler;
import mondrian.rolap.RolapStar.Column;
import mondrian.rolap.SqlStatement.Type;
import mondrian.server.Execution;
import mondrian.server.Locus;
import mondrian.spi.Dialect;
import mondrian.util.ClassResolver;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eigenbase.util.property.StringProperty;
import org.eigenbase.xom.XOMException;

public class RolapUtil {
    public static final Logger MDX_LOGGER = LogManager.getLogger("mondrian.mdx");
    public static final Logger SQL_LOGGER = LogManager.getLogger("mondrian.sql");
    public static final Logger MONITOR_LOGGER = LogManager.getLogger("mondrian.server.monitor");
    public static final Logger PROFILE_LOGGER = LogManager.getLogger("mondrian.profile");
    static final Logger LOGGER = LogManager.getLogger(RolapUtil.class);
    public static final Object valueNotReadyException = new Double(0.0D);
    private static RolapUtil.ExecuteQueryHook queryHook = null;
    public static final Comparable<?> sqlNullValue;
    public static final Comparator ROLAP_COMPARATOR;
    private static String mdxNullLiteral;
    public static final String sqlNullLiteral = "null";
    private static final Set<String> loadedDrivers;

    public static Functor1<Void, Statement> getDefaultCallback(final Locus locus) {
        return new Functor1<Void, Statement>() {
            public Void apply(Statement stmt) {
                locus.execution.registerStatement(locus, stmt);
                return null;
            }
        };
    }

    public static SchemaReader locusSchemaReader(RolapConnection connection, final SchemaReader schemaReader) {
        mondrian.server.Statement statement = connection.getInternalStatement();
        Execution execution = new Execution(statement, 0L);
        final Locus locus = new Locus(execution, "Schema reader", (String)null);
        return (SchemaReader)Proxy.newProxyInstance(SchemaReader.class.getClassLoader(), new Class[]{SchemaReader.class}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Locus.push(locus);

                Object var4;
                try {
                    var4 = method.invoke(schemaReader, args);
                } catch (InvocationTargetException var8) {
                    throw var8.getCause();
                } finally {
                    Locus.pop(locus);
                }

                return var4;
            }
        });
    }

    public static synchronized RolapUtil.ExecuteQueryHook getHook() {
        return queryHook;
    }

    public static synchronized void setHook(RolapUtil.ExecuteQueryHook hook) {
        queryHook = hook;
    }

    public static String mdxNullLiteral() {
        if (mdxNullLiteral == null) {
            reloadNullLiteral();
        }

        return mdxNullLiteral;
    }

    public static void reloadNullLiteral() {
        mdxNullLiteral = MondrianProperties.instance().NullMemberRepresentation.get();
    }

    static RolapMember[] toArray(List<RolapMember> v) {
        return v.isEmpty() ? new RolapMember[0] : (RolapMember[])v.toArray(new RolapMember[v.size()]);
    }

    static RolapMember lookupMember(MemberReader reader, List<Segment> uniqueNameParts, boolean failIfNotFound) {
        RolapMember member = lookupMemberInternal(uniqueNameParts, (RolapMember)null, reader, failIfNotFound);
        if (member != null) {
            return member;
        } else {
            List<RolapMember> rootMembers = reader.getRootMembers();
            if (rootMembers.size() == 1) {
                RolapMember rootMember = (RolapMember)rootMembers.get(0);
                if (rootMember.isAll()) {
                    member = lookupMemberInternal(uniqueNameParts, rootMember, reader, failIfNotFound);
                }
            }

            return member;
        }
    }

    private static RolapMember lookupMemberInternal(List<Segment> segments, RolapMember member, MemberReader reader, boolean failIfNotFound) {
        Iterator var4 = segments.iterator();

        while(var4.hasNext()) {
            Segment segment = (Segment)var4.next();
            if (!(segment instanceof NameSegment)) {
                break;
            }

            NameSegment nameSegment = (NameSegment)segment;
            Object children;
            if (member == null) {
                children = reader.getRootMembers();
            } else {
                children = new ArrayList();
                reader.getMemberChildren(member, (List)children);
                member = null;
            }

            Iterator var8 = ((List)children).iterator();

            while(var8.hasNext()) {
                RolapMember child = (RolapMember)var8.next();
                if (child.getName().equals(nameSegment.name)) {
                    member = child;
                    break;
                }
            }

            if (member == null) {
                break;
            }
        }

        if (member == null && failIfNotFound) {
            throw MondrianResource.instance().MdxCantFindMember.ex(Util.implode(segments));
        } else {
            return member;
        }
    }

    public static SqlStatement executeQuery(DataSource dataSource, String sql, Locus locus) {
        return executeQuery(dataSource, sql, (List)null, 0, 0, locus, -1, -1, getDefaultCallback(locus));
    }

    public static SqlStatement executeQuery(DataSource dataSource, String sql, List<Type> types, int maxRowCount, int firstRowOrdinal, Locus locus, int resultSetType, int resultSetConcurrency, Functor1<Void, Statement> callback) {
        SqlStatement stmt = new SqlStatement(dataSource, sql, types, maxRowCount, firstRowOrdinal, locus, resultSetType, resultSetConcurrency, callback == null ? getDefaultCallback(locus) : callback);
        stmt.execute();
        return stmt;
    }

    public static void alertNonNative(String functionName, String reason) throws NativeEvaluationUnsupportedException {
        String alertMsg = "Unable to use native SQL evaluation for '" + functionName + "'; reason:  " + reason;
        StringProperty alertProperty = MondrianProperties.instance().AlertNativeEvaluationUnsupported;
        String alertValue = alertProperty.get();
        if (alertValue.equalsIgnoreCase(Level.WARN.toString())) {
            LOGGER.warn(alertMsg);
        } else if (alertValue.equalsIgnoreCase(Level.ERROR.toString())) {
            LOGGER.error(alertMsg);
            throw MondrianResource.instance().NativeEvaluationUnsupported.ex(functionName);
        }

    }

    public static synchronized void loadDrivers(String jdbcDrivers) {
        StringTokenizer tok = new StringTokenizer(jdbcDrivers, ",");

        while(tok.hasMoreTokens()) {
            String jdbcDriver = tok.nextToken();
            if (loadedDrivers.add(jdbcDriver)) {
                try {
                    ClassResolver.INSTANCE.forName(jdbcDriver, true);
                    LOGGER.info("Mondrian: JDBC driver " + jdbcDriver + " loaded successfully");
                } catch (ClassNotFoundException var4) {
                    LOGGER.warn("Mondrian: Warning: JDBC driver " + jdbcDriver + " not found");
                }
            }
        }

    }

    public static ExpCompiler createDependencyTestingCompiler(ExpCompiler compiler) {
        return new DteCompiler(compiler);
    }

    public static Member findBestMemberMatch(List<? extends Member> members, RolapMember parent, RolapLevel level, Segment searchName, MatchType matchType) {
        if (!(searchName instanceof NameSegment)) {
            return null;
        } else {
            NameSegment nameSegment = (NameSegment)searchName;
            switch(matchType) {
                case FIRST:
                    return (Member)members.get(0);
                case LAST:
                    return (Member)members.get(members.size() - 1);
                default:
                    Member searchMember = level.getHierarchy().createMember(parent, level, nameSegment.name, (Formula)null);
                    Member bestMatch = null;
                    Iterator var8 = members.iterator();

                    while(true) {
                        Member member;
                        int rc;
                        do {
                            label63:
                            do {
                                while(var8.hasNext()) {
                                    member = (Member)var8.next();
                                    if (searchName.quoting == Quoting.KEY && member instanceof RolapMember && ((RolapMember)member).getKey().toString().equals(nameSegment.name)) {
                                        return member;
                                    }

                                    if (matchType.isExact()) {
                                        rc = Util.compareName(member.getName(), nameSegment.name);
                                    } else {
                                        rc = FunUtil.compareSiblingMembers(member, searchMember);
                                    }

                                    if (rc == 0) {
                                        return member;
                                    }

                                    if (matchType == MatchType.BEFORE) {
                                        continue label63;
                                    }

                                    if (matchType == MatchType.AFTER && rc > 0 && (bestMatch == null || FunUtil.compareSiblingMembers(member, bestMatch) < 0)) {
                                        bestMatch = member;
                                    }
                                }

                                if (matchType.isExact()) {
                                    return null;
                                }

                                return bestMatch;
                            } while(rc >= 0);
                        } while(bestMatch != null && FunUtil.compareSiblingMembers(member, bestMatch) <= 0);

                        bestMatch = member;
                    }
            }
        }
    }

    public static Relation convertInlineTableToRelation(InlineTable inlineTable, Dialect dialect) {
        View view = new View();
        view.alias = inlineTable.alias;
        int columnCount = inlineTable.columnDefs.array.length;
        List<String> columnNames = new ArrayList();
        List<String> columnTypes = new ArrayList();

        for(int i = 0; i < columnCount; ++i) {
            columnNames.add(inlineTable.columnDefs.array[i].name);
            columnTypes.add(inlineTable.columnDefs.array[i].type);
        }

        List<String[]> valueList = new ArrayList();
        Row[] var7 = inlineTable.rows.array;
        int var8 = var7.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            Row row = var7[var9];
            String[] values = new String[columnCount];
            Value[] var12 = row.values;
            int var13 = var12.length;

            for(int var14 = 0; var14 < var13; ++var14) {
                Value value = var12[var14];
                int columnOrdinal = columnNames.indexOf(value.column);
                if (columnOrdinal < 0) {
                    throw Util.newError("Unknown column '" + value.column + "'");
                }

                values[columnOrdinal] = value.cdata;
            }

            valueList.add(values);
        }

        view.addCode("generic", dialect.generateInline(columnNames, columnTypes, valueList));
        return view;
    }

    public static RolapMember strip(RolapMember member) {
        return member instanceof RolapCubeMember ? ((RolapCubeMember)member).getRolapMember() : member;
    }

    public static ExpCompiler createProfilingCompiler(ExpCompiler compiler) {
        return new ProfilingEvaluatorCompiler(compiler);
    }

    public static Evaluator createEvaluator(mondrian.server.Statement statement) {
        Execution dummyExecution = new Execution(statement, 0L);
        RolapResult result = new RolapResult(dummyExecution, false);
        return result.getRootEvaluator();
    }

    public static void constraintBitkeyForLimitedMembers(Evaluator evaluator, Member[] members, RolapCube cube, BitKey levelBitKey) {
        Member[] var4 = members;
        int var5 = members.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Member curMember = var4[var6];
            if (curMember instanceof LimitedRollupMember) {
                int savepoint = evaluator.savepoint();

                try {
                    evaluator.setNonEmpty(false);
                    List<Member> lowestMembers = ((RolapHierarchy)curMember.getHierarchy()).getLowestMembersForAccess(evaluator, ((LimitedRollupMember)curMember).hierarchyAccess, FunUtil.getNonEmptyMemberChildrenWithDetails(evaluator, curMember));

                    assert lowestMembers.size() > 0;

                    Member lowMember = (Member)lowestMembers.get(0);

                    do {
                        Column curColumn = ((RolapCubeLevel)lowMember.getLevel()).getBaseStarKeyColumn(cube);
                        if (curColumn != null) {
                            levelBitKey.set(curColumn.getBitPosition());
                        }

                        if (((RolapCubeLevel)lowMember.getLevel()).isUnique()) {
                            break;
                        }

                        lowMember = lowMember.getParentMember();
                    } while(!lowMember.isAll());
                } finally {
                    evaluator.restore(savepoint);
                }
            }
        }

    }

    public static List<String> makeRolapStarKey(Relation fact) {
        List<String> rlStarKey = new ArrayList();
        Table table = null;
        rlStarKey.add(fact.getAlias());
        if (fact instanceof Table) {
            table = (Table)fact;
        }

        if (!Util.isNull(table) && !Util.isNull(table.filter) && !Util.isBlank(table.filter.cdata)) {
            rlStarKey.add(table.filter.dialect);
            rlStarKey.add(table.filter.cdata);
        }

        return Collections.unmodifiableList(rlStarKey);
    }

    public static List<String> makeRolapStarKey(String factTableName) {
        return Collections.unmodifiableList(Arrays.asList(factTableName));
    }

    public static boolean isGroupByNeeded(RolapHierarchy hierarchy, RolapLevel[] levels, int levelDepth) {
        boolean needsGroupBy = false;
        if (hierarchy.getUniqueKeyLevelName() == null) {
            needsGroupBy = true;
        } else {
            boolean foundUniqueKeyLevelName = false;

            for(int i = 0; i <= levelDepth; ++i) {
                RolapLevel lvl = levels[i];
                if (!lvl.isAll()) {
                    if (hierarchy.getUniqueKeyLevelName().equals(lvl.getName())) {
                        foundUniqueKeyLevelName = true;
                    }

                    RolapProperty[] var7 = lvl.getProperties();
                    int var8 = var7.length;

                    for(int var9 = 0; var9 < var8; ++var9) {
                        RolapProperty p = var7[var9];
                        if (!p.dependsOnLevelValue()) {
                            needsGroupBy = true;
                            break;
                        }
                    }

                    if (needsGroupBy) {
                        break;
                    }
                }
            }

            if (!foundUniqueKeyLevelName) {
                needsGroupBy = true;
            }
        }

        return needsGroupBy;
    }

    public static RelationOrJoin processRelation(Schema xmlSchema, RelationOrJoin relation) {
        if (relation instanceof SchemaView) {
            SchemaView schemaView = (SchemaView)relation;
            View[] var3 = xmlSchema.views;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                View view = var3[var5];
                if (view.alias.equals(schemaView.source)) {
                    View newView = null;

                    try {
                        newView = (View)view.deepCopy();
                        newView.alias = schemaView.alias;
                        return newView;
                    } catch (XOMException e){
                        e.printStackTrace();
                    }
                    finally {
                        ;
                    }
                }
            }

            return null;
        } else {
            return relation;
        }
    }

    static {
        sqlNullValue = RolapUtil.RolapUtilComparable.INSTANCE;
        ROLAP_COMPARATOR = new RolapUtil.RolapUtilComparator();
        mdxNullLiteral = null;
        loadedDrivers = new HashSet();
    }

    public interface ExecuteQueryHook {
        void onExecuteQuery(String var1);
    }

    private static class NullWriter extends Writer {
        public void write(char[] cbuf, int off, int len) throws IOException {
        }

        public void flush() throws IOException {
        }

        public void close() throws IOException {
        }
    }

    public static class TeeWriter extends FilterWriter {
        StringWriter buf = new StringWriter();

        public TeeWriter(Writer out) {
            super(out);
        }

        public String toString() {
            return this.buf.toString();
        }

        public Writer getWriter() {
            return this.out;
        }

        public void write(int c) throws IOException {
            super.write(c);
            this.buf.write(c);
        }

        public void write(char[] cbuf) throws IOException {
            super.write(cbuf);
            this.buf.write(cbuf);
        }

        public void write(char[] cbuf, int off, int len) throws IOException {
            super.write(cbuf, off, len);
            this.buf.write(cbuf, off, len);
        }

        public void write(String str) throws IOException {
            super.write(str);
            this.buf.write(str);
        }

        public void write(String str, int off, int len) throws IOException {
            super.write(str, off, len);
            this.buf.write(str, off, len);
        }
    }

    private static final class RolapUtilComparator<T extends Comparable<T>> implements Comparator<T> {
        private RolapUtilComparator() {
        }

        public int compare(T o1, T o2) {
            try {
                return o1.compareTo(o2);
            } catch (ClassCastException var4) {
                if (o2 == RolapUtil.RolapUtilComparable.INSTANCE) {
                    return 1;
                } else {
                    throw new MondrianException(var4);
                }
            }
        }

        // $FF: synthetic method
        RolapUtilComparator(Object x0) {
            this();
        }
    }

    private static final class RolapUtilComparable implements Comparable, Serializable {
        private static final long serialVersionUID = -2595758291465179116L;
        public static final RolapUtil.RolapUtilComparable INSTANCE = new RolapUtil.RolapUtilComparable();

        public String toString() {
            return "#null";
        }

        public int compareTo(Object o) {
            return o == this ? 0 : -1;
        }
    }
}