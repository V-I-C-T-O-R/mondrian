/* Decompiler 78ms, total 574ms, lines 154 */
package mondrian.server;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import mondrian.olap.MondrianProperties;
import mondrian.olap.MondrianServer;
import mondrian.rolap.RolapSchema;
import mondrian.rolap.RolapSchemaPool;
import mondrian.rolap.agg.SegmentCacheManager;
import mondrian.rolap.agg.SegmentCacheWorker;
import mondrian.xmla.XmlaException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.olap4j.OlapException;
import org.olap4j.Scenario;

public class Session {
    private static final Logger LOGGER = LogManager.getLogger(Session.class);
    private static final long expireSeconds = 86400; //24小时后过期
    static final Map<String, Session> sessions = new HashMap();
    static Timer timer = new Timer(true);
    static TimerTask timerTask = new TimerTask() {
        public void run() {
            List<String> toRemove = new ArrayList();
            Iterator var2 = Session.sessions.entrySet().iterator();

            while(var2.hasNext()) {
                Entry<String, Session> entry = (Entry)var2.next();
                Session session = (Session)entry.getValue();
                Duration duration = Duration.between(session.checkInTime, LocalDateTime.now());
                if (duration.getSeconds() > Session.expireSeconds) {
                    toRemove.add((String)entry.getKey());
                }
            }

            var2 = toRemove.iterator();

            while(var2.hasNext()) {
                String sessionId = (String)var2.next();
                Session.closeInternal(sessionId);
            }

        }
    };
    String sessionId;
    LocalDateTime checkInTime = null;
    private SegmentCacheManager segmentCacheManager = null;
    private Scenario scenario = null;

    Session(String sessionId) {
        this.sessionId = sessionId;
    }

    public static Session create(String sessionId) throws OlapException {
        if (sessions.containsKey(sessionId)) {
            throw new XmlaException("XMLAnalysisError", "0xc10c000a", "Session with id \"" + sessionId + "\" already exists.", new OlapException("Session with id \"" + sessionId + "\" already exists."));
        } else {
            Session session = new Session(sessionId);
            sessions.put(sessionId, session);
            session.checkInTime = LocalDateTime.now();
            return session;
        }
    }

    public static Session getWithoutCheck(String sessionId) {
        return (Session)sessions.get(sessionId);
    }

    public static Session get(String sessionId) throws OlapException {
        if (!sessions.containsKey(sessionId)) {
            throw new XmlaException("XMLAnalysisError", "0xc10c000a", "Session with id \"" + sessionId + "\" does not exists.", new OlapException("Session with id \"" + sessionId + "\" does not exist"));
        } else {
            return (Session)sessions.get(sessionId);
        }
    }

    public static void checkIn(String sessionId) throws OlapException {
        Session session = get(sessionId);
        session.checkInTime = LocalDateTime.now();
    }

    static void closeInternal(String sessionId) {
        List<RolapSchema> rolapSchemas = RolapSchemaPool.instance().getRolapSchemas();
        Iterator var2 = rolapSchemas.iterator();

        while(var2.hasNext()) {
            RolapSchema rolapSchema = (RolapSchema)var2.next();
            String rolapSchemaSessionId = rolapSchema.getInternalConnection().getConnectInfo().get("sessionId");
            if (sessionId.equals(rolapSchemaSessionId)) {
                RolapSchemaPool.instance().remove(rolapSchema);
            }
        }

        Session session = (Session)sessions.get(sessionId);
        shutdownCacheManager(session);
        sessions.remove(sessionId);
    }

    static void shutdownCacheManager(Session session) {
        if (session.segmentCacheManager != null) {
            session.segmentCacheManager.shutdown();
            Iterator var1 = session.segmentCacheManager.segmentCacheWorkers.iterator();

            while(var1.hasNext()) {
                SegmentCacheWorker worker = (SegmentCacheWorker)var1.next();
                worker.shutdown();
            }
        }

    }

    public static void shutdown() {
        Iterator var0 = sessions.entrySet().iterator();

        while(var0.hasNext()) {
            Entry<String, Session> entry = (Entry)var0.next();
            shutdownCacheManager((Session)entry.getValue());
        }

    }

    public static void close(String sessionId) throws OlapException {
        Session session = get(sessionId);
        closeInternal(sessionId);
    }

    public SegmentCacheManager getOrCreateSegmentCacheManager(MondrianServer server) {
        if (this.segmentCacheManager == null) {
            this.segmentCacheManager = new SegmentCacheManager(server);
        }

        return this.segmentCacheManager;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public Scenario getScenario() {
        return this.scenario;
    }

    static {
        timer.scheduleAtFixedRate(timerTask, 0L, 60000L);
    }
}