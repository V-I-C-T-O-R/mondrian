/* Decompiler 2ms, total 564ms, lines 46 */
package mondrian.server;

import java.sql.SQLException;
import mondrian.olap.Query;
import mondrian.olap.SchemaReader;
import mondrian.rolap.RolapConnection;
import mondrian.rolap.RolapSchema;
import mondrian.spi.ProfileHandler;

public interface Statement {
    void close();

    SchemaReader getSchemaReader();

    RolapSchema getSchema();

    RolapConnection getMondrianConnection();

    Object getProperty(String var1);

    Query getQuery();

    void setQuery(Query var1);

    void enableProfiling(ProfileHandler var1);

    ProfileHandler getProfileHandler();

    void setQueryTimeoutMillis(long var1);

    long getQueryTimeoutMillis();

    /** @deprecated */
    void checkCancelOrTimeout();

    void cancel() throws SQLException;

    Execution getCurrentExecution();

    void end(Execution var1);

    void start(Execution var1);

    long getId();
}