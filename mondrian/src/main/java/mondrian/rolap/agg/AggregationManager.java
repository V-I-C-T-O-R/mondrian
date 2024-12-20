/* Decompiler 207ms, total 860ms, lines 343 */
package mondrian.rolap.agg;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import mondrian.olap.CacheControl;
import mondrian.olap.MondrianProperties;
import mondrian.olap.MondrianServer;
import mondrian.olap.OlapElement;
import mondrian.olap.Util;
import mondrian.olap.CacheControl.CellRegion;
import mondrian.rolap.BitKey;
import mondrian.rolap.CacheControlImpl;
import mondrian.rolap.GroupingSetsCollector;
import mondrian.rolap.RolapAggregationManager;
import mondrian.rolap.RolapConnection;
import mondrian.rolap.RolapStar;
import mondrian.rolap.StarColumnPredicate;
import mondrian.rolap.StarPredicate;
import mondrian.rolap.RolapAggregationManager.PinSet;
import mondrian.rolap.RolapStar.Column;
import mondrian.rolap.RolapStar.Measure;
import mondrian.rolap.SqlStatement.Type;
import mondrian.rolap.agg.SegmentCacheManager.FlushCommand;
import mondrian.rolap.agg.SegmentCacheManager.FlushResult;
import mondrian.rolap.aggmatcher.AggStar;
import mondrian.server.Locus;
import mondrian.server.Session;
import mondrian.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AggregationManager extends RolapAggregationManager {
    private static final MondrianProperties properties = MondrianProperties.instance();
    private static final Logger LOGGER = LogManager.getLogger(AggregationManager.class);
    public final SegmentCacheManager cacheMgr;
    private MondrianServer server;

    public AggregationManager(MondrianServer server) {
        this.server = server;
        if (properties.EnableCacheHitCounters.get()) {
            LOGGER.error("Property " + properties.EnableCacheHitCounters.getPath() + " is obsolete; ignored.");
        }

        this.cacheMgr = new SegmentCacheManager(server);
    }

    public final Logger getLogger() {
        return LOGGER;
    }

    /** @deprecated */
    public static synchronized AggregationManager instance() {
        return MondrianServer.forId((String)null).getAggregationManager();
    }

    public static void loadAggregation(SegmentCacheManager cacheMgr, int cellRequestCount, List<Measure> measures, Column[] columns, AggregationKey aggregationKey, StarColumnPredicate[] predicates, GroupingSetsCollector groupingSetsCollector, List<Future<Map<Segment, SegmentWithData>>> segmentFutures) {
        RolapStar star = ((Measure)measures.get(0)).getStar();
        Aggregation aggregation = star.lookupOrCreateAggregation(aggregationKey);
        predicates = aggregation.optimizePredicates(columns, predicates);
        aggregation.load(cacheMgr, cellRequestCount, columns, measures, predicates, groupingSetsCollector, segmentFutures);
    }

    public CacheControl getCacheControl(final RolapConnection connection, final PrintWriter pw) {
        return new CacheControlImpl(connection) {
            protected void flushNonUnion(CellRegion region) {
                SegmentCacheManager segmentCacheManager = AggregationManager.this.getCacheMgr(connection);
                FlushResult result = (FlushResult)segmentCacheManager.execute(new FlushCommand(Locus.peek(), segmentCacheManager, region, this));
                List<Future<Boolean>> futures = new ArrayList();
                Iterator var5 = result.tasks.iterator();

                while(var5.hasNext()) {
                    Callable<Boolean> task = (Callable)var5.next();
                    futures.add(segmentCacheManager.cacheExecutor.submit(task));
                }

                var5 = futures.iterator();

                while(var5.hasNext()) {
                    Future<Boolean> future = (Future)var5.next();
                    Util.discard(Util.safeGet(future, "Flush cache"));
                }

            }

            public void flush(CellRegion region) {
                if (pw != null) {
                    pw.println("Cache state before flush:");
                    this.printCacheState(pw, region);
                    pw.println();
                }

                super.flush(region);
                if (pw != null) {
                    pw.println("Cache state after flush:");
                    this.printCacheState(pw, region);
                    pw.println();
                }

            }

            public void trace(String message) {
                if (pw != null) {
                    pw.println(message);
                }

            }

            public boolean isTraceEnabled() {
                return pw != null;
            }
        };
    }

    public Object getCellFromCache(CellRequest request) {
        return this.getCellFromCache(request, (PinSet)null);
    }

    public Object getCellFromCache(CellRequest request, PinSet pinSet) {
        Measure measure = request.getMeasure();
        return measure.getStar().getCellFromCache(request, pinSet);
    }

    public Object getCellFromAllCaches(CellRequest request, RolapConnection rolapConnection) {
        Measure measure = request.getMeasure();
        return measure.getStar().getCellFromAllCaches(request, rolapConnection);
    }

    public String getDrillThroughSql(DrillThroughCellRequest request, StarPredicate starPredicateSlicer, List<OlapElement> fields, boolean countOnly) {
        DrillThroughQuerySpec spec = new DrillThroughQuerySpec(request, starPredicateSlicer, fields, countOnly);
        Pair<String, List<Type>> pair = spec.generateSqlQuery();
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("DrillThroughSQL: " + (String)pair.left + Util.nl);
        }

        return (String)pair.left;
    }

    public static Pair<String, List<Type>> generateSql(GroupingSetsList groupingSetsList, List<StarPredicate> compoundPredicateList) {
        RolapStar star = groupingSetsList.getStar();
        BitKey levelBitKey = groupingSetsList.getDefaultLevelBitKey();
        BitKey measureBitKey = groupingSetsList.getDefaultMeasureBitKey();
        boolean hasCompoundPredicates = false;
        if (compoundPredicateList != null && compoundPredicateList.size() > 0) {
            hasCompoundPredicates = true;
        }

        if (MondrianProperties.instance().UseAggregates.get() && !hasCompoundPredicates) {
            boolean[] rollup = new boolean[]{false};
            AggStar aggStar = findAgg(star, levelBitKey, measureBitKey, rollup);
            if (aggStar != null) {
                if (LOGGER.isDebugEnabled()) {
                    StringBuilder buf = new StringBuilder(256);
                    buf.append("MATCH: ");
                    buf.append(star.getFactTable().getAlias());
                    buf.append(Util.nl);
                    buf.append("   foreign=");
                    buf.append(levelBitKey);
                    buf.append(Util.nl);
                    buf.append("   measure=");
                    buf.append(measureBitKey);
                    buf.append(Util.nl);
                    buf.append("   aggstar=");
                    buf.append(aggStar.getBitKey());
                    buf.append(Util.nl);
                    buf.append("AggStar=");
                    buf.append(aggStar.getFactTable().getName());
                    buf.append(Util.nl);
                    Iterator var9 = aggStar.getFactTable().getColumns().iterator();

                    while(var9.hasNext()) {
                        mondrian.rolap.aggmatcher.AggStar.Table.Column column = (mondrian.rolap.aggmatcher.AggStar.Table.Column)var9.next();
                        buf.append("   ");
                        buf.append(column);
                        buf.append(Util.nl);
                    }

                    LOGGER.debug(buf.toString());
                }

                AggQuerySpec aggQuerySpec = new AggQuerySpec(aggStar, rollup[0], groupingSetsList);
                Pair<String, List<Type>> sql = aggQuerySpec.generateSqlQuery();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("generateSqlQuery: sql=" + (String)sql.left);
                }

                return sql;
            }
        }

        if (LOGGER.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("NO MATCH : ");
            sb.append(star.getFactTable().getAlias());
            sb.append(Util.nl);
            sb.append("Foreign columns bit key=");
            sb.append(levelBitKey);
            sb.append(Util.nl);
            sb.append("Measure bit key=        ");
            sb.append(measureBitKey);
            sb.append(Util.nl);
            sb.append("Agg Stars=[");
            sb.append(Util.nl);
            Iterator var13 = star.getAggStars().iterator();

            while(var13.hasNext()) {
                AggStar aggStar = (AggStar)var13.next();
                sb.append(aggStar.toString());
            }

            sb.append(Util.nl);
            sb.append("]");
            LOGGER.debug(sb.toString());
        }

        SegmentArrayQuerySpec spec = new SegmentArrayQuerySpec(groupingSetsList, compoundPredicateList);
        Pair<String, List<Type>> pair = spec.generateSqlQuery();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("generateSqlQuery: sql=" + (String)pair.left);
        }

        return pair;
    }

    public static AggStar findAgg(RolapStar star, BitKey levelBitKey, BitKey measureBitKey, boolean[] rollup) {
        assert rollup != null;

        BitKey fullBitKey = levelBitKey.or(measureBitKey);
        BitKey expandedLevelBitKey = expandLevelBitKey(star, levelBitKey.copy());
        Iterator var6 = star.getAggStars().iterator();

        while(true) {
            while(true) {
                AggStar aggStar;
                do {
                    if (!var6.hasNext()) {
                        return null;
                    }

                    aggStar = (AggStar)var6.next();
                } while(!aggStar.superSetMatch(fullBitKey));

                boolean isDistinct = measureBitKey.intersects(aggStar.getDistinctMeasureBitKey());
                if (!isDistinct) {
                    rollup[0] = !aggStar.isFullyCollapsed() || aggStar.hasIgnoredColumns() || levelBitKey.isEmpty() || !aggStar.getLevelBitKey().equals(levelBitKey);
                    return aggStar;
                }

                if (aggStar.hasIgnoredColumns()) {
                    LOGGER.info(aggStar.getFactTable().getName() + " cannot be used for distinct-count measures since it has unused or ignored columns.");
                } else {
                    BitKey distinctMeasuresBitKey = measureBitKey.and(aggStar.getDistinctMeasureBitKey());
                    BitSet distinctMeasures = distinctMeasuresBitKey.toBitSet();
                    BitKey combinedLevelBitKey = null;

                    for(int k = distinctMeasures.nextSetBit(0); k >= 0; k = distinctMeasures.nextSetBit(k + 1)) {
                        mondrian.rolap.aggmatcher.AggStar.FactTable.Measure distinctMeasure = aggStar.lookupMeasure(k);
                        BitKey rollableLevelBitKey = distinctMeasure.getRollableLevelBitKey();
                        if (combinedLevelBitKey == null) {
                            combinedLevelBitKey = rollableLevelBitKey;
                        } else {
                            combinedLevelBitKey = combinedLevelBitKey.and(rollableLevelBitKey);
                        }
                    }

                    if (aggStar.hasForeignKeys()) {
                        BitKey fkBitKey = aggStar.getForeignKeyBitKey().copy();
                        Iterator var16 = aggStar.getFactTable().getMeasures().iterator();

                        while(var16.hasNext()) {
                            mondrian.rolap.aggmatcher.AggStar.FactTable.Measure measure = (mondrian.rolap.aggmatcher.AggStar.FactTable.Measure)var16.next();
                            if (measure.isDistinct() && measureBitKey.get(measure.getBitPosition())) {
                                fkBitKey.clear(measure.getBitPosition());
                            }
                        }

                        if (!fkBitKey.isEmpty()) {
                            continue;
                        }
                    }

                    if (aggStar.select(expandedLevelBitKey, combinedLevelBitKey, measureBitKey) && !expandedLevelBitKey.isEmpty()) {
                        rollup[0] = !aggStar.getLevelBitKey().equals(expandedLevelBitKey);
                        return aggStar;
                    }
                }
            }
        }
    }

    private static BitKey expandLevelBitKey(RolapStar star, BitKey levelBitKey) {
        for(int bitPos = levelBitKey.nextSetBit(0); bitPos >= 0; bitPos = levelBitKey.nextSetBit(bitPos + 1)) {
            levelBitKey = setParentsBitKey(star, levelBitKey, bitPos);
        }

        return levelBitKey;
    }

    private static BitKey setParentsBitKey(RolapStar star, BitKey levelBitKey, int bitPos) {
        Column parent = star.getColumn(bitPos).getParentColumn();
        if (parent == null) {
            return levelBitKey;
        } else {
            levelBitKey.set(parent.getBitPosition());
            return setParentsBitKey(star, levelBitKey, parent.getBitPosition());
        }
    }

    public PinSet createPinSet() {
        return new AggregationManager.PinSetImpl();
    }

    public void shutdown() {
        this.cacheMgr.shutdown();
        Iterator var1 = this.cacheMgr.segmentCacheWorkers.iterator();

        while(var1.hasNext()) {
            SegmentCacheWorker worker = (SegmentCacheWorker)var1.next();
            worker.shutdown();
        }

    }

    public SegmentCacheManager getCacheMgr(RolapConnection connection) {
//        if (connection != null && MondrianProperties.instance().EnableSessionCaching.get()) {
//            String sessionId = connection.getConnectInfo().get("sessionId");
//            Session session = Session.getWithoutCheck(sessionId);
//            return session == null ? this.cacheMgr : session.getOrCreateSegmentCacheManager(this.server);
//        } else {
//            return this.cacheMgr;
//        }
        return this.cacheMgr;
    }

    public static class PinSetImpl extends HashSet<Segment> implements PinSet {
    }
}