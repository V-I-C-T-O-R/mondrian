/* Decompiler 955ms, total 1899ms, lines 1417 */
package mondrian.rolap;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.SoftReference;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import javax.sql.DataSource;
import mondrian.olap.Member;
import mondrian.olap.MondrianException;
import mondrian.olap.MondrianProperties;
import mondrian.olap.Util;
import mondrian.olap.MondrianDef.Expression;
import mondrian.olap.MondrianDef.InlineTable;
import mondrian.olap.MondrianDef.Join;
import mondrian.olap.MondrianDef.KeyExpression;
import mondrian.olap.MondrianDef.Relation;
import mondrian.olap.MondrianDef.RelationOrJoin;
import mondrian.olap.MondrianDef.View;
import mondrian.olap.Util.GcIterator;
import mondrian.resource.MondrianResource;
import mondrian.rolap.BitKey.Factory;
import mondrian.rolap.RolapAggregationManager.PinSet;
import mondrian.rolap.SqlStatement.Type;
import mondrian.rolap.agg.Aggregation;
import mondrian.rolap.agg.AggregationKey;
import mondrian.rolap.agg.CellRequest;
import mondrian.rolap.agg.SegmentWithData;
import mondrian.rolap.agg.AggregationManager.PinSetImpl;
import mondrian.rolap.aggmatcher.AggStar;
import mondrian.rolap.sql.SqlQuery;
import mondrian.server.Locus;
import mondrian.spi.DataSourceChangeListener;
import mondrian.spi.Dialect;
import mondrian.spi.Dialect.Datatype;
import org.apache.commons.collections.map.ReferenceMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RolapStar {
    private static final Logger LOGGER = LogManager.getLogger(RolapStar.class);
    private final RolapSchema schema;
    private DataSource dataSource;
    private final RolapStar.Table factTable;
    private int columnCount;
    private final List<RolapStar.Column> columnList = new ArrayList();
    private final Dialect sqlQueryDialect;
    private boolean cacheAggregations = true;
    private final List<AggStar> aggStars = new LinkedList();
    private DataSourceChangeListener changeListener;
    private RolapStar.StarNetworkNode factNode;
    private Map<String, RolapStar.StarNetworkNode> nodeLookup = new HashMap();
    private final RolapStatisticsCache statisticsCache;
    private final ThreadLocal<RolapStar.Bar> localBars = new ThreadLocal<RolapStar.Bar>() {
        protected RolapStar.Bar initialValue() {
            return new RolapStar.Bar();
        }
    };

    RolapStar(RolapSchema schema, DataSource dataSource, Relation fact) {
        this.schema = schema;
        this.dataSource = dataSource;
        this.factTable = new RolapStar.Table(this, fact, (RolapStar.Table)null, (RolapStar.Condition)null);
        this.factNode = new RolapStar.StarNetworkNode((RolapStar.StarNetworkNode)null, this.factTable.alias, (Relation)null, (String)null, (String)null);
        this.sqlQueryDialect = schema.getDialect();
        this.changeListener = schema.getDataSourceChangeListener();
        this.statisticsCache = new RolapStatisticsCache(this);
    }

    public Object getCellFromCache(CellRequest request, PinSet pinSet) {
        AggregationKey aggregationKey = new AggregationKey(request);
        RolapStar.Bar bar = (RolapStar.Bar)this.localBars.get();
        Iterator var5 = GcIterator.over(bar.segmentRefs).iterator();

        while(var5.hasNext()) {
            SegmentWithData segment = (SegmentWithData)var5.next();
            if (segment.getConstrainedColumnsBitKey().equals(request.getConstrainedColumnsBitKey()) && segment.matches(aggregationKey, request.getMeasure())) {
                Object o = segment.getCellValue(request.getSingleValues());
                if (o != null) {
                    if (pinSet != null) {
                        ((PinSetImpl)pinSet).add(segment);
                    }

                    return o;
                }
            }
        }

        return null;
    }

    public Object getCellFromAllCaches(CellRequest request, RolapConnection rolapConnection) {
        Object result = this.getCellFromCache(request, (PinSet)null);
        return result != null ? result : this.getCellFromExternalCache(request, rolapConnection);
    }

    private Object getCellFromExternalCache(CellRequest request, RolapConnection rolapConnection) {
        SegmentWithData segment = Locus.peek().getServer().getAggregationManager().getCacheMgr(rolapConnection).peek(request);
        return segment == null ? null : segment.getCellValue(request.getSingleValues());
    }

    public void register(SegmentWithData segment) {
        ((RolapStar.Bar)this.localBars.get()).segmentRefs.add(new SoftReference(segment));
    }

    public RolapStatisticsCache getStatisticsCache() {
        return this.statisticsCache;
    }

    protected RelationOrJoin cloneRelation(Relation rel, String possibleName) {
        if (rel instanceof mondrian.olap.MondrianDef.Table) {
            mondrian.olap.MondrianDef.Table tbl = (mondrian.olap.MondrianDef.Table)rel;
            return new mondrian.olap.MondrianDef.Table(tbl, possibleName);
        } else if (rel instanceof View) {
            View view = (View)rel;
            View newView = new View(view);
            newView.alias = possibleName;
            return newView;
        } else if (rel instanceof InlineTable) {
            InlineTable inlineTable = (InlineTable)rel;
            InlineTable newInlineTable = new InlineTable(inlineTable);
            newInlineTable.alias = possibleName;
            return newInlineTable;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public RelationOrJoin getUniqueRelation(RelationOrJoin rel, String factForeignKey, String primaryKey, String primaryKeyTable) {
        return this.getUniqueRelation(this.factNode, rel, factForeignKey, primaryKey, primaryKeyTable);
    }

    private RelationOrJoin getUniqueRelation(RolapStar.StarNetworkNode parent, RelationOrJoin relOrJoin, String foreignKey, String joinKey, String joinKeyTable) {
        if (relOrJoin == null) {
            return null;
        } else if (relOrJoin instanceof Relation) {
            int val = 0;
            Relation rel = (Relation)relOrJoin;
            String newAlias = joinKeyTable != null ? joinKeyTable : rel.getAlias();

            while(true) {
                RolapStar.StarNetworkNode node = (RolapStar.StarNetworkNode)this.nodeLookup.get(newAlias);
                if (node == null) {
                    if (val != 0) {
                        rel = (Relation)this.cloneRelation(rel, newAlias);
                    }

                    node = new RolapStar.StarNetworkNode(parent, newAlias, rel, foreignKey, joinKey);
                    this.nodeLookup.put(newAlias, node);
                    return rel;
                }

                if (node.isCompatible(parent, rel, foreignKey, joinKey)) {
                    return node.origRel;
                }

                StringBuilder var10000 = (new StringBuilder()).append(rel.getAlias()).append("_");
                ++val;
                newAlias = var10000.append(val).toString();
            }
        } else if (!(relOrJoin instanceof Join)) {
            return null;
        } else {
            Join join = (Join)relOrJoin;
            if (join.left instanceof Join) {
                throw MondrianResource.instance().IllegalLeftDeepJoin.ex();
            } else {
                RelationOrJoin left;
                RelationOrJoin right;
                if (join.getLeftAlias().equals(joinKeyTable)) {
                    left = this.getUniqueRelation(parent, join.left, foreignKey, joinKey, joinKeyTable);
                    parent = (RolapStar.StarNetworkNode)this.nodeLookup.get(((Relation)left).getAlias());
                    right = this.getUniqueRelation(parent, join.right, join.leftKey, join.rightKey, join.getRightAlias());
                } else {
                    if (!join.getRightAlias().equals(joinKeyTable)) {
                        throw new MondrianException("failed to match primary key table to join tables");
                    }

                    right = this.getUniqueRelation(parent, join.right, foreignKey, joinKey, joinKeyTable);
                    parent = (RolapStar.StarNetworkNode)this.nodeLookup.get(((Relation)right).getAlias());
                    left = this.getUniqueRelation(parent, join.left, join.rightKey, join.leftKey, join.getLeftAlias());
                }

                if (join.left != left || join.right != right) {
                    join = new Join(left instanceof Relation ? ((Relation)left).getAlias() : null, join.leftKey, left, right instanceof Relation ? ((Relation)right).getAlias() : null, join.rightKey, right);
                }

                return join;
            }
        }
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    private int nextColumnCount() {
        return this.columnCount++;
    }

    private int decrementColumnCount() {
        return this.columnCount--;
    }

    public void prepareToLoadAggregates() {
        this.aggStars.clear();
    }

    public void addAggStar(AggStar aggStar) {
        long size = aggStar.getSize();
        ListIterator lit = this.aggStars.listIterator();

        AggStar as;
        do {
            if (!lit.hasNext()) {
                this.aggStars.add(aggStar);
                return;
            }

            as = (AggStar)lit.next();
        } while(as.getSize() < size);

        lit.previous();
        lit.add(aggStar);
    }

    void clearAggStarList() {
        this.aggStars.clear();
    }

    public void reOrderAggStarList() {
        List<AggStar> oldList = new ArrayList(this.aggStars);
        this.aggStars.clear();
        Iterator var2 = oldList.iterator();

        while(var2.hasNext()) {
            AggStar aggStar = (AggStar)var2.next();
            this.addAggStar(aggStar);
        }

    }

    public List<AggStar> getAggStars() {
        return this.aggStars;
    }

    public RolapStar.Table getFactTable() {
        return this.factTable;
    }

    public SqlQuery getSqlQuery() {
        return new SqlQuery(this.getSqlQueryDialect());
    }

    public Dialect getSqlQueryDialect() {
        return this.sqlQueryDialect;
    }

    void setCacheAggregations(boolean cacheAggregations) {
        this.cacheAggregations = cacheAggregations;
        this.clearCachedAggregations(false);
    }

    boolean isCacheAggregations() {
        return this.cacheAggregations;
    }

    boolean isCacheDisabled() {
        return MondrianProperties.instance().DisableCaching.get();
    }

    void clearCachedAggregations(boolean forced) {
        if (forced || !this.cacheAggregations || this.isCacheDisabled()) {
            if (LOGGER.isDebugEnabled()) {
                StringBuilder buf = new StringBuilder(100);
                buf.append("RolapStar.clearCachedAggregations: schema=");
                buf.append(this.schema.getName());
                buf.append(", star=");
                buf.append(this.getFactTable().getAlias());
                LOGGER.debug(buf.toString());
            }

            ((RolapStar.Bar)this.localBars.get()).aggregations.clear();
            ((RolapStar.Bar)this.localBars.get()).segmentRefs.clear();
        }

    }

    public Aggregation lookupOrCreateAggregation(AggregationKey aggregationKey) {
        Aggregation aggregation = this.lookupSegment(aggregationKey);
        if (aggregation != null) {
            return aggregation;
        } else {
            aggregation = new Aggregation(aggregationKey);
            ((RolapStar.Bar)this.localBars.get()).aggregations.put(aggregationKey, aggregation);
            if (this.cacheAggregations && !this.isCacheDisabled() && this.changeListener != null) {
                Util.discard(this.changeListener.isAggregationChanged(aggregationKey));
            }

            return aggregation;
        }
    }

    public Aggregation lookupSegment(AggregationKey aggregationKey) {
        return (Aggregation)((RolapStar.Bar)this.localBars.get()).aggregations.get(aggregationKey);
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public static RolapStar.Measure getStarMeasure(Member member) {
        return (RolapStar.Measure)((RolapStoredMeasure)member).getStarMeasure();
    }

    public RolapStar.Column[] lookupColumns(String tableAlias, String columnName) {
        RolapStar.Table table = this.factTable.findDescendant(tableAlias);
        return table == null ? null : table.lookupColumns(columnName);
    }

    public RolapStar.Column lookupColumn(String tableAlias, String columnName) {
        RolapStar.Table table = this.factTable.findDescendant(tableAlias);
        return table == null ? null : table.lookupColumn(columnName);
    }

    public BitKey getBitKey(String[] tableAlias, String[] columnName) {
        BitKey bitKey = Factory.makeBitKey(this.getColumnCount());

        for(int i = 0; i < tableAlias.length; ++i) {
            RolapStar.Column starColumn = this.lookupColumn(tableAlias[i], columnName[i]);
            if (starColumn != null) {
                bitKey.set(starColumn.getBitPosition());
            }
        }

        return bitKey;
    }

    public List<String> getAliasList() {
        List<String> aliasList = new ArrayList();
        if (this.factTable != null) {
            collectAliases(aliasList, this.factTable);
        }

        return aliasList;
    }

    private static void collectAliases(List<String> aliasList, RolapStar.Table table) {
        aliasList.add(table.getAlias());
        Iterator var2 = table.children.iterator();

        while(var2.hasNext()) {
            RolapStar.Table child = (RolapStar.Table)var2.next();
            collectAliases(aliasList, child);
        }

    }

    public static void collectColumns(Collection<RolapStar.Column> columnList, RolapStar.Table table, mondrian.olap.MondrianDef.Column joinColumn) {
        if (joinColumn == null) {
            columnList.addAll(table.columnList);
        }

        Iterator var3 = table.children.iterator();

        while(true) {
            RolapStar.Table child;
            do {
                if (!var3.hasNext()) {
                    return;
                }

                child = (RolapStar.Table)var3.next();
            } while(joinColumn != null && !child.getJoinCondition().left.equals(joinColumn));

            collectColumns(columnList, child, (mondrian.olap.MondrianDef.Column)null);
        }
    }

    private boolean containsColumn(String tableName, String columnName) {
        Connection jdbcConnection;
        try {
            jdbcConnection = this.dataSource.getConnection();
        } catch (SQLException var18) {
            throw Util.newInternal(var18, "Error while creating connection from data source");
        }

        boolean var6;
        try {
            DatabaseMetaData metaData = jdbcConnection.getMetaData();
            ResultSet columns = metaData.getColumns((String)null, (String)null, tableName, columnName);
            var6 = columns.next();
        } catch (SQLException var16) {
            throw Util.newInternal("Error while retrieving metadata for table '" + tableName + "', column '" + columnName + "'");
        } finally {
            try {
                jdbcConnection.close();
            } catch (SQLException var15) {
            }

        }

        return var6;
    }

    private void addColumn(RolapStar.Column c) {
        this.columnList.add(c.getBitPosition(), c);
    }

    public RolapStar.Column getColumn(int bitPos) {
        return (RolapStar.Column)this.columnList.get(bitPos);
    }

    public RolapSchema getSchema() {
        return this.schema;
    }

    public String generateSql(List<RolapStar.Column> columnList, List<String> columnNameList) {
        SqlQuery query = new SqlQuery(this.sqlQueryDialect, true);
        query.addFrom(this.factTable.relation, this.factTable.relation.getAlias(), false);
        int k = -1;
        Iterator var5 = columnList.iterator();

        while(var5.hasNext()) {
            RolapStar.Column column = (RolapStar.Column)var5.next();
            ++k;
            column.table.addToFrom(query, false, true);
            String columnExpr = column.generateExprString(query);
            if (column instanceof RolapStar.Measure) {
                RolapStar.Measure measure = (RolapStar.Measure)column;
                columnExpr = measure.getAggregator().getExpression(columnExpr);
            }

            String columnName = (String)columnNameList.get(k);
            String alias = query.addSelect(columnExpr, (Type)null, columnName);
            if (!(column instanceof RolapStar.Measure)) {
                query.addGroupBy(columnExpr, alias);
            }
        }

        return query.toString().trim();
    }

    public String toString() {
        StringWriter sw = new StringWriter(256);
        PrintWriter pw = new PrintWriter(sw);
        this.print(pw, "", true);
        pw.flush();
        return sw.toString();
    }

    public void print(PrintWriter pw, String prefix, boolean structure) {
        if (structure) {
            pw.print(prefix);
            pw.println("RolapStar:");
            String subprefix = prefix + "  ";
            this.factTable.print(pw, subprefix);
            Iterator var5 = this.getAggStars().iterator();

            while(var5.hasNext()) {
                AggStar aggStar = (AggStar)var5.next();
                aggStar.print(pw, subprefix);
            }
        }

    }

    public DataSourceChangeListener getChangeListener() {
        return this.changeListener;
    }

    public void setChangeListener(DataSourceChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public static class ColumnComparator implements Comparator<RolapStar.Column> {
        public static RolapStar.ColumnComparator instance = new RolapStar.ColumnComparator();

        private ColumnComparator() {
        }

        public int compare(RolapStar.Column o1, RolapStar.Column o2) {
            int result = o1.getName().compareTo(o2.getName());
            if (result == 0) {
                result = o1.getTable().getAlias().compareTo(o2.getTable().getAlias());
            }

            return result;
        }
    }

    public static class AliasReplacer {
        private final String oldAlias;
        private final String newAlias;

        public AliasReplacer(String oldAlias, String newAlias) {
            this.oldAlias = oldAlias;
            this.newAlias = newAlias;
        }

        private RolapStar.Condition visit(RolapStar.Condition condition) {
            if (condition == null) {
                return null;
            } else {
                return this.newAlias.equals(this.oldAlias) ? condition : new RolapStar.Condition(this.visit(condition.left), this.visit(condition.right));
            }
        }

        public Expression visit(Expression expression) {
            if (expression == null) {
                return null;
            } else if (this.newAlias.equals(this.oldAlias)) {
                return expression;
            } else if (expression instanceof mondrian.olap.MondrianDef.Column) {
                mondrian.olap.MondrianDef.Column column = (mondrian.olap.MondrianDef.Column)expression;
                return new mondrian.olap.MondrianDef.Column(this.visit(column.table), column.name);
            } else {
                throw Util.newInternal("need to implement " + expression);
            }
        }

        private String visit(String table) {
            return table.equals(this.oldAlias) ? this.newAlias : table;
        }
    }

    public static class Condition {
        private static final Logger LOGGER = LogManager.getLogger(RolapStar.Condition.class);
        private final Expression left;
        private final Expression right;
        RolapStar.Table table;

        Condition(Expression left, Expression right) {
            assert left != null;

            assert right != null;

            if (!(left instanceof mondrian.olap.MondrianDef.Column)) {
                LOGGER.debug("Condition.left NOT Column: " + left.getClass().getName());
            }

            this.left = left;
            this.right = right;
        }

        public Expression getLeft() {
            return this.left;
        }

        public String getLeft(SqlQuery query) {
            return this.left.getExpression(query);
        }

        public Expression getRight() {
            return this.right;
        }

        public String getRight(SqlQuery query) {
            return this.right.getExpression(query);
        }

        public String toString(SqlQuery query) {
            return this.left.getExpression(query) + " = " + this.right.getExpression(query);
        }

        public int hashCode() {
            return this.left.hashCode() ^ this.right.hashCode();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof RolapStar.Condition)) {
                return false;
            } else {
                RolapStar.Condition that = (RolapStar.Condition)obj;
                return this.left.equals(that.left) && this.right.equals(that.right);
            }
        }

        public String toString() {
            StringWriter sw = new StringWriter(256);
            PrintWriter pw = new PrintWriter(sw);
            this.print(pw, "");
            pw.flush();
            return sw.toString();
        }

        public void print(PrintWriter pw, String prefix) {
            SqlQuery sqlQueuy = this.table.getSqlQuery();
            pw.print(prefix);
            pw.println("Condition:");
            String subprefix = prefix + "  ";
            pw.print(subprefix);
            pw.print("left=");
            if (this.left instanceof mondrian.olap.MondrianDef.Column) {
                mondrian.olap.MondrianDef.Column c = (mondrian.olap.MondrianDef.Column)this.left;
                RolapStar.Column col = this.table.star.getFactTable().lookupColumn(c.name);
                if (col != null) {
                    pw.print(" (");
                    pw.print(col.getBitPosition());
                    pw.print(") ");
                }
            }

            pw.println(this.left.getExpression(sqlQueuy));
            pw.print(subprefix);
            pw.print("right=");
            pw.println(this.right.getExpression(sqlQueuy));
        }
    }

    public static class Table {
        private final RolapStar star;
        private final Relation relation;
        private final List<RolapStar.Column> columnList;
        private final RolapStar.Table parent;
        private List<RolapStar.Table> children;
        private final RolapStar.Condition joinCondition;
        private final String alias;

        private Table(RolapStar star, Relation relation, RolapStar.Table parent, RolapStar.Condition joinCondition) {
            this.star = star;
            this.relation = relation;
            this.alias = this.chooseAlias();
            this.parent = parent;
            RolapStar.AliasReplacer aliasReplacer = new RolapStar.AliasReplacer(relation.getAlias(), this.alias);
            this.joinCondition = aliasReplacer.visit(joinCondition);
            if (this.joinCondition != null) {
                this.joinCondition.table = this;
            }

            this.columnList = new ArrayList();
            this.children = Collections.emptyList();
            Util.assertTrue(parent == null == (joinCondition == null));
        }

        public RolapStar.Condition getJoinCondition() {
            return this.joinCondition;
        }

        public RolapStar.Table getParentTable() {
            return this.parent;
        }

        private void addColumn(RolapStar.Column column) {
            this.columnList.add(column);
        }

        private void collectColumns(BitKey bitKey, List<RolapStar.Column> list) {
            Iterator var3 = this.getColumns().iterator();

            while(var3.hasNext()) {
                RolapStar.Column column = (RolapStar.Column)var3.next();
                if (bitKey.get(column.getBitPosition())) {
                    list.add(column);
                }
            }

            var3 = this.getChildren().iterator();

            while(var3.hasNext()) {
                RolapStar.Table table = (RolapStar.Table)var3.next();
                table.collectColumns(bitKey, list);
            }

        }

        public RolapStar.Column[] lookupColumns(String columnName) {
            List<RolapStar.Column> l = new ArrayList();
            Iterator var3 = this.getColumns().iterator();

            while(var3.hasNext()) {
                RolapStar.Column column = (RolapStar.Column)var3.next();
                if (column.getExpression() instanceof mondrian.olap.MondrianDef.Column) {
                    mondrian.olap.MondrianDef.Column columnExpr = (mondrian.olap.MondrianDef.Column)column.getExpression();
                    if (columnExpr.name.equals(columnName)) {
                        l.add(column);
                    }
                } else if (column.getExpression() instanceof KeyExpression) {
                    KeyExpression columnExpr = (KeyExpression)column.getExpression();
                    if (columnExpr.toString().equals(columnName)) {
                        l.add(column);
                    }
                }
            }

            return (RolapStar.Column[])l.toArray(new RolapStar.Column[l.size()]);
        }

        public RolapStar.Column lookupColumn(String columnName) {
            Iterator var2 = this.getColumns().iterator();

            while(var2.hasNext()) {
                RolapStar.Column column = (RolapStar.Column)var2.next();
                if (column.getExpression() instanceof mondrian.olap.MondrianDef.Column) {
                    mondrian.olap.MondrianDef.Column columnExpr = (mondrian.olap.MondrianDef.Column)column.getExpression();
                    if (columnExpr.name.equals(columnName)) {
                        return column;
                    }
                } else if (column.getExpression() instanceof KeyExpression) {
                    KeyExpression columnExpr = (KeyExpression)column.getExpression();
                    if (columnExpr.toString().equals(columnName)) {
                        return column;
                    }
                } else if (column.getName().equals(columnName)) {
                    return column;
                }
            }

            return null;
        }

        public RolapStar.Column lookupColumnByExpression(Expression xmlExpr) {
            Iterator var2 = this.getColumns().iterator();

            RolapStar.Column column;
            do {
                if (!var2.hasNext()) {
                    return null;
                }

                column = (RolapStar.Column)var2.next();
            } while(column instanceof RolapStar.Measure || !column.getExpression().equals(xmlExpr));

            return column;
        }

        public boolean containsColumn(RolapStar.Column column) {
            return this.getColumns().contains(column);
        }

        public RolapStar.Measure lookupMeasureByName(String cubeName, String name) {
            Iterator var3 = this.getColumns().iterator();

            while(var3.hasNext()) {
                RolapStar.Column column = (RolapStar.Column)var3.next();
                if (column instanceof RolapStar.Measure) {
                    RolapStar.Measure measure = (RolapStar.Measure)column;
                    if (measure.getName().equals(name) && measure.getCubeName().equals(cubeName)) {
                        return measure;
                    }
                }
            }

            return null;
        }

        RolapStar getStar() {
            return this.star;
        }

        private SqlQuery getSqlQuery() {
            return this.getStar().getSqlQuery();
        }

        public Relation getRelation() {
            return this.relation;
        }

        private String chooseAlias() {
            List<String> aliasList = this.star.getAliasList();
            int i = 0;

            while(true) {
                String candidateAlias = this.relation.getAlias();
                if (i > 0) {
                    candidateAlias = candidateAlias + "_" + i;
                }

                if (!aliasList.contains(candidateAlias)) {
                    return candidateAlias;
                }

                ++i;
            }
        }

        public String getAlias() {
            return this.alias;
        }

        public String getTableName() {
            if (this.relation instanceof mondrian.olap.MondrianDef.Table) {
                mondrian.olap.MondrianDef.Table t = (mondrian.olap.MondrianDef.Table)this.relation;
                return t.name;
            } else {
                return null;
            }
        }

        synchronized void makeMeasure(RolapBaseCubeMeasure measure) {
            RolapStar.Measure starMeasure = new RolapStar.Measure(measure.getName(), measure.getCube().getName(), measure.getAggregator(), this, measure.getMondrianDefExpression(), measure.getDatatype());
            measure.setStarMeasure(starMeasure);
            if (this.containsColumn((RolapStar.Column)starMeasure)) {
                this.star.decrementColumnCount();
            } else {
                this.addColumn(starMeasure);
            }

        }

        synchronized RolapStar.Column makeColumns(RolapCube cube, RolapCubeLevel level, RolapStar.Column parentColumn, String usagePrefix) {
            RolapStar.Column nameColumn = null;
            if (level.getNameExp() != null) {
                nameColumn = this.makeColumnForLevelExpr(cube, level, level.getName(), level.getNameExp(), Datatype.String, (Type)null, (RolapStar.Column)null, (RolapStar.Column)null, (String)null);
            }

            String name = level.getNameExp() == null ? level.getName() : level.getName() + " (Key)";
            RolapStar.Column column = this.makeColumnForLevelExpr(cube, level, name, level.getKeyExp(), level.getDatatype(), level.getInternalType(), nameColumn, parentColumn, usagePrefix);
            if (column != null) {
                level.setStarKeyColumn(column);
            }

            return column;
        }

        private RolapStar.Column makeColumnForLevelExpr(RolapCube cube, RolapLevel level, String name, Expression xmlExpr, Datatype datatype, Type internalType, RolapStar.Column nameColumn, RolapStar.Column parentColumn, String usagePrefix) {
            RolapStar.Table table = this;
            if (xmlExpr instanceof mondrian.olap.MondrianDef.Column) {
                mondrian.olap.MondrianDef.Column xmlColumn = (mondrian.olap.MondrianDef.Column)xmlExpr;
                String tableName = xmlColumn.table;
                table = this.findAncestor(tableName);
                if (table == null) {
                    throw Util.newError("Level '" + level.getUniqueName() + "' of cube '" + this + "' is invalid: table '" + tableName + "' is not found in current scope" + Util.nl + ", star:" + Util.nl + this.getStar());
                }

                RolapStar.AliasReplacer aliasReplacer = new RolapStar.AliasReplacer(tableName, table.getAlias());
                xmlExpr = aliasReplacer.visit(xmlExpr);
            }

            RolapStar.Column c = this.lookupColumnByExpression(xmlExpr);
            RolapStar.Column column;
            if (c != null && !c.equals(nameColumn)) {
                column = c;
            } else {
                column = new RolapStar.Column(name, table, xmlExpr, datatype, internalType, nameColumn, parentColumn, usagePrefix, level.getApproxRowCount(), this.star.nextColumnCount());
                this.addColumn(column);
            }

            return column;
        }

        synchronized RolapStar.Table addJoin(RolapCube cube, RelationOrJoin relationOrJoin, RolapStar.Condition joinCondition) {
            RolapStar.Table leftTable;
            if (relationOrJoin instanceof Relation) {
                Relation relation = (Relation)relationOrJoin;
                leftTable = this.findChild(relation, joinCondition);
                if (leftTable == null) {
                    leftTable = new RolapStar.Table(this.star, relation, this, joinCondition);
                    if (this.children.isEmpty()) {
                        this.children = new ArrayList();
                    }

                    this.children.add(leftTable);
                }

                return leftTable;
            } else if (relationOrJoin instanceof Join) {
                Join join = (Join)relationOrJoin;
                leftTable = this.addJoin(cube, join.left, joinCondition);
                String leftAlias = join.leftAlias;
                if (leftAlias == null) {
                    leftAlias = ((Relation)join.left).getAlias();
                    if (leftAlias == null) {
                        throw Util.newError("missing leftKeyAlias in " + relationOrJoin);
                    }
                }

                assert leftTable.findAncestor(leftAlias) == leftTable;

                leftAlias = leftTable.getAlias();
                String rightAlias = join.rightAlias;
                if (rightAlias == null) {
                    if (join.right instanceof Join) {
                        Join joinright = (Join)join.right;
                        rightAlias = ((Relation)joinright.left).getAlias();
                    } else {
                        rightAlias = ((Relation)join.right).getAlias();
                    }

                    if (rightAlias == null) {
                        throw Util.newError("missing rightKeyAlias in " + relationOrJoin);
                    }
                }

                joinCondition = new RolapStar.Condition(new mondrian.olap.MondrianDef.Column(leftAlias, join.leftKey), new mondrian.olap.MondrianDef.Column(rightAlias, join.rightKey));
                RolapStar.Table rightTable = leftTable.addJoin(cube, join.right, joinCondition);
                return rightTable;
            } else {
                throw Util.newInternal("bad relation type " + relationOrJoin);
            }
        }

        public RolapStar.Table findChild(Relation relation, RolapStar.Condition joinCondition) {
            Iterator var3 = this.getChildren().iterator();

            while(var3.hasNext()) {
                RolapStar.Table child = (RolapStar.Table)var3.next();
                if (child.relation.equals(relation)) {
                    RolapStar.Condition condition = joinCondition;
                    if (!Util.equalName(relation.getAlias(), child.alias)) {
                        RolapStar.AliasReplacer aliasReplacer = new RolapStar.AliasReplacer(relation.getAlias(), child.alias);
                        condition = aliasReplacer.visit(joinCondition);
                    }

                    if (child.joinCondition.equals(condition)) {
                        return child;
                    }
                }
            }

            return null;
        }

        public RolapStar.Table findDescendant(String seekAlias) {
            if (this.getAlias().equals(seekAlias)) {
                return this;
            } else {
                Iterator var2 = this.getChildren().iterator();

                RolapStar.Table found;
                do {
                    if (!var2.hasNext()) {
                        return null;
                    }

                    RolapStar.Table child = (RolapStar.Table)var2.next();
                    found = child.findDescendant(seekAlias);
                } while(found == null);

                return found;
            }
        }

        public RolapStar.Table findAncestor(String tableName) {
            for(RolapStar.Table t = this; t != null; t = t.parent) {
                if (t.relation.getAlias().equals(tableName)) {
                    return t;
                }
            }

            return null;
        }

        public boolean equalsTableName(String tableName) {
            if (this.relation instanceof mondrian.olap.MondrianDef.Table) {
                mondrian.olap.MondrianDef.Table mt = (mondrian.olap.MondrianDef.Table)this.relation;
                if (mt.name.equals(tableName)) {
                    return true;
                }
            }

            return false;
        }

        public void addToFrom(SqlQuery query, boolean failIfExists, boolean joinToParent) {
            Util.assertTrue(this.parent == null == (this.joinCondition == null));
            if (joinToParent) {
                if (this.parent != null) {
                    this.parent.addToFrom(query, failIfExists, joinToParent);
                }

                if (this.joinCondition != null) {
                    query.addWhere(this.joinCondition.toString(query));
                }
            }

            query.addFrom(this.relation, this.alias, failIfExists);
        }

        public List<RolapStar.Table> getChildren() {
            return this.children;
        }

        public List<RolapStar.Column> getColumns() {
            return this.columnList;
        }

        public RolapStar.Table findTableWithLeftJoinCondition(String columnName) {
            Iterator var2 = this.getChildren().iterator();

            while(var2.hasNext()) {
                RolapStar.Table child = (RolapStar.Table)var2.next();
                RolapStar.Condition condition = child.joinCondition;
                if (condition != null && condition.left instanceof mondrian.olap.MondrianDef.Column) {
                    mondrian.olap.MondrianDef.Column mcolumn = (mondrian.olap.MondrianDef.Column)condition.left;
                    if (mcolumn.name.equals(columnName)) {
                        return child;
                    }
                }
            }

            return null;
        }

        public RolapStar.Table findTableWithLeftCondition(Expression left) {
            Iterator var2 = this.getChildren().iterator();

            while(var2.hasNext()) {
                RolapStar.Table child = (RolapStar.Table)var2.next();
                RolapStar.Condition condition = child.joinCondition;
                if (condition != null && condition.left instanceof mondrian.olap.MondrianDef.Column) {
                    mondrian.olap.MondrianDef.Column mcolumn = (mondrian.olap.MondrianDef.Column)condition.left;
                    if (mcolumn.equals(left)) {
                        return child;
                    }
                }
            }

            return null;
        }

        public boolean isFunky() {
            return this.relation == null;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof RolapStar.Table)) {
                return false;
            } else {
                RolapStar.Table other = (RolapStar.Table)obj;
                return this.getAlias().equals(other.getAlias());
            }
        }

        public int hashCode() {
            return this.getAlias().hashCode();
        }

        public String toString() {
            StringWriter sw = new StringWriter(256);
            PrintWriter pw = new PrintWriter(sw);
            this.print(pw, "");
            pw.flush();
            return sw.toString();
        }

        public void print(PrintWriter pw, String prefix) {
            pw.print(prefix);
            pw.println("Table:");
            String subprefix = prefix + "  ";
            pw.print(subprefix);
            pw.print("alias=");
            pw.println(this.getAlias());
            if (this.relation != null) {
                pw.print(subprefix);
                pw.print("relation=");
                pw.println(this.relation);
            }

            pw.print(subprefix);
            pw.println("Columns:");
            String subsubprefix = subprefix + "  ";
            Iterator var5 = this.getColumns().iterator();

            while(var5.hasNext()) {
                RolapStar.Column column = (RolapStar.Column)var5.next();
                column.print(pw, subsubprefix);
                pw.println();
            }

            if (this.joinCondition != null) {
                this.joinCondition.print(pw, subprefix);
            }

            var5 = this.getChildren().iterator();

            while(var5.hasNext()) {
                RolapStar.Table child = (RolapStar.Table)var5.next();
                child.print(pw, subprefix);
            }

        }

        public boolean containsColumn(String columnName) {
            return this.relation instanceof Relation ? this.star.containsColumn(this.relation.getAlias(), columnName) : false;
        }

        // $FF: synthetic method
        Table(RolapStar x0, Relation x1, RolapStar.Table x2, RolapStar.Condition x3, Object x4) {
            this(x0, x1, x2, x3);
        }
    }

    public static class Measure extends RolapStar.Column {
        private final String cubeName;
        private final RolapAggregator aggregator;

        public Measure(String name, String cubeName, RolapAggregator aggregator, RolapStar.Table table, Expression expression, Datatype datatype) {
            super(name, table, expression, datatype, null);
            this.cubeName = cubeName;
            this.aggregator = aggregator;
        }

        public RolapAggregator getAggregator() {
            return this.aggregator;
        }

        public boolean equals(Object o) {
            if (!(o instanceof RolapStar.Measure)) {
                return false;
            } else {
                RolapStar.Measure that = (RolapStar.Measure)o;
                if (!super.equals(that)) {
                    return false;
                } else if (!this.cubeName.equals(that.cubeName)) {
                    return false;
                } else {
                    return that.aggregator == this.aggregator;
                }
            }
        }

        public int hashCode() {
            int h = super.hashCode();
            h = Util.hash(h, this.aggregator);
            return h;
        }

        public void print(PrintWriter pw, String prefix) {
            SqlQuery sqlQuery = this.getSqlQuery();
            pw.print(prefix);
            pw.print(this.getName());
            pw.print(" (");
            pw.print(this.getBitPosition());
            pw.print("): ");
            pw.print(this.aggregator.getExpression(this.getExpression() == null ? null : this.generateExprString(sqlQuery)));
        }

        public String getCubeName() {
            return this.cubeName;
        }
    }

    public static class Column {
        public static final Comparator<RolapStar.Column> COMPARATOR = new Comparator<RolapStar.Column>() {
            public int compare(RolapStar.Column object1, RolapStar.Column object2) {
                return Util.compare(object1.getBitPosition(), object2.getBitPosition());
            }
        };
        private final RolapStar.Table table;
        private final Expression expression;
        private final Datatype datatype;
        private final Type internalType;
        private final String name;
        private final RolapStar.Column parentColumn;
        private final String usagePrefix;
        private final RolapStar.Column nameColumn;
        private boolean isNameColumn;
        private final int bitPosition;
        private AtomicLong approxCardinality;

        private Column(String name, RolapStar.Table table, Expression expression, Datatype datatype) {
            this(name, table, expression, datatype, (Type)null, (RolapStar.Column)null, (RolapStar.Column)null, (String)null, Integer.MIN_VALUE, table.star.nextColumnCount());
        }

        private Column(String name, RolapStar.Table table, Expression expression, Datatype datatype, Type internalType, RolapStar.Column nameColumn, RolapStar.Column parentColumn, String usagePrefix, int approxCardinality, int bitPosition) {
            this.approxCardinality = new AtomicLong(Long.MIN_VALUE);
            this.name = name;
            this.table = table;
            this.expression = expression;

            assert expression == null || expression.getGenericExpression() != null;

            this.datatype = datatype;
            this.internalType = internalType;
            this.bitPosition = bitPosition;
            this.nameColumn = nameColumn;
            this.parentColumn = parentColumn;
            this.usagePrefix = usagePrefix;
            this.approxCardinality.set((long)approxCardinality);
            if (nameColumn != null) {
                nameColumn.isNameColumn = true;
            }

            if (table != null) {
                table.star.addColumn(this);
            }

        }

        protected Column(Datatype datatype) {
            this((String)null, (RolapStar.Table)null, (Expression)null, datatype, (Type)null, (RolapStar.Column)null, (RolapStar.Column)null, (String)null, Integer.MIN_VALUE, 0);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof RolapStar.Column)) {
                return false;
            } else {
                RolapStar.Column other = (RolapStar.Column)obj;
                return other.table == this.table && Util.equals(other.expression, this.expression) && other.datatype == this.datatype && other.name.equals(this.name);
            }
        }

        public int hashCode() {
            int h = this.name.hashCode();
            h = Util.hash(h, this.table);
            return h;
        }

        public String getName() {
            return this.name;
        }

        public int getBitPosition() {
            return this.bitPosition;
        }

        public RolapStar getStar() {
            return this.table.star;
        }

        public RolapStar.Table getTable() {
            return this.table;
        }

        public SqlQuery getSqlQuery() {
            return this.getTable().getStar().getSqlQuery();
        }

        public RolapStar.Column getNameColumn() {
            return this.nameColumn;
        }

        public RolapStar.Column getParentColumn() {
            return this.parentColumn;
        }

        public String getUsagePrefix() {
            return this.usagePrefix;
        }

        public boolean isNameColumn() {
            return this.isNameColumn;
        }

        public Expression getExpression() {
            return this.expression;
        }

        public String generateExprString(SqlQuery query) {
            return this.getExpression().getExpression(query);
        }

        public long getCardinality() {
            if (this.approxCardinality.get() < 0L) {
                this.approxCardinality.set(this.table.star.getStatisticsCache().getColumnCardinality(this.table.relation, this.expression, this.approxCardinality.get()));
            }

            return this.approxCardinality.get();
        }

        public static String createInExpr(final String expr, StarColumnPredicate predicate, Datatype datatype, SqlQuery sqlQuery) {
            RolapStar.Column column = new RolapStar.Column(datatype) {
                public String generateExprString(SqlQuery query) {
                    return expr;
                }
            };
            predicate = predicate.cloneWithColumn(column);
            StringBuilder buf = new StringBuilder(64);
            predicate.toSql(sqlQuery, buf);
            return buf.toString();
        }

        public String toString() {
            StringWriter sw = new StringWriter(256);
            PrintWriter pw = new PrintWriter(sw);
            this.print(pw, "");
            pw.flush();
            return sw.toString();
        }

        public void print(PrintWriter pw, String prefix) {
            SqlQuery sqlQuery = this.getSqlQuery();
            pw.print(prefix);
            pw.print(this.getName());
            pw.print(" (");
            pw.print(this.getBitPosition());
            pw.print("): ");
            pw.print(this.generateExprString(sqlQuery));
        }

        public Datatype getDatatype() {
            return this.datatype;
        }

        public String getDatatypeString(Dialect dialect) {
            SqlQuery query = new SqlQuery(dialect);
            query.addFrom(this.table.star.factTable.relation, this.table.star.factTable.alias, false);
            query.addFrom(this.table.relation, this.table.alias, false);
            query.addSelect(this.expression.getExpression(query), (Type)null);
            String sql = query.toString();
            Connection jdbcConnection = null;

            String var11;
            try {
                jdbcConnection = this.table.star.dataSource.getConnection();
                PreparedStatement pstmt = jdbcConnection.prepareStatement(sql);
                ResultSetMetaData resultSetMetaData = pstmt.getMetaData();

                assert resultSetMetaData.getColumnCount() == 1;

                String type = resultSetMetaData.getColumnTypeName(1);
                int precision = resultSetMetaData.getPrecision(1);
                int scale = resultSetMetaData.getScale(1);
                if (type.equals("DOUBLE")) {
                    precision = 0;
                }

                String typeString;
                if (precision == 0) {
                    typeString = type;
                } else if (scale == 0) {
                    typeString = type + "(" + precision + ")";
                } else {
                    typeString = type + "(" + precision + ", " + scale + ")";
                }

                pstmt.close();
                jdbcConnection.close();
                jdbcConnection = null;
                var11 = typeString;
            } catch (SQLException var20) {
                throw Util.newError(var20, "Error while deriving type of column " + this.toString());
            } finally {
                if (jdbcConnection != null) {
                    try {
                        jdbcConnection.close();
                    } catch (SQLException var19) {
                    }
                }

            }

            return var11;
        }

        public Type getInternalType() {
            return this.internalType;
        }

        // $FF: synthetic method
        Column(String x0, RolapStar.Table x1, Expression x2, Datatype x3, Object x4) {
            this(x0, x1, x2, x3);
        }

        // $FF: synthetic method
        Column(String x0, RolapStar.Table x1, Expression x2, Datatype x3, Type x4, RolapStar.Column x5, RolapStar.Column x6, String x7, int x8, int x9, Object x10) {
            this(x0, x1, x2, x3, x4, x5, x6, x7, x8, x9);
        }
    }

    private static class StarNetworkNode {
        private RolapStar.StarNetworkNode parent;
        private Relation origRel;
        private String foreignKey;
        private String joinKey;

        private StarNetworkNode(RolapStar.StarNetworkNode parent, String alias, Relation origRel, String foreignKey, String joinKey) {
            this.parent = parent;
            this.origRel = origRel;
            this.foreignKey = foreignKey;
            this.joinKey = joinKey;
        }

        private boolean isCompatible(RolapStar.StarNetworkNode compatibleParent, Relation rel, String compatibleForeignKey, String compatibleJoinKey) {
            return this.parent == compatibleParent && this.origRel.getClass().equals(rel.getClass()) && this.foreignKey.equals(compatibleForeignKey) && this.joinKey.equals(compatibleJoinKey);
        }

        // $FF: synthetic method
        StarNetworkNode(RolapStar.StarNetworkNode x0, String x1, Relation x2, String x3, String x4, Object x5) {
            this(x0, x1, x2, x3, x4);
        }
    }

    public static class Bar {
        private final Map<AggregationKey, Aggregation> aggregations = new ReferenceMap(2, 2);
        private final List<SoftReference<SegmentWithData>> segmentRefs = new ArrayList();
    }
}