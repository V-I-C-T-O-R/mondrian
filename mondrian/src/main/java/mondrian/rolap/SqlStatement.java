/* Decompiler 459ms, total 1006ms, lines 419 */
package mondrian.rolap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import javax.sql.DataSource;
import mondrian.olap.MondrianProperties;
import mondrian.olap.Util;
import mondrian.olap.Util.Functor1;
import mondrian.resource.MondrianResource;
import mondrian.rolap.RolapUtil.ExecuteQueryHook;
import mondrian.server.Execution;
import mondrian.server.Locus;
import mondrian.server.monitor.SqlStatementEndEvent;
import mondrian.server.monitor.SqlStatementExecuteEvent;
import mondrian.server.monitor.SqlStatementStartEvent;
import mondrian.server.monitor.SqlStatementEvent.Purpose;
import mondrian.spi.Dialect;
import mondrian.spi.DialectManager;
import mondrian.util.Counters;
import mondrian.util.DelegatingInvocationHandler;

public class SqlStatement {
  private static final String TIMING_NAME = "SqlStatement-";
  private static final AtomicLong ID_GENERATOR = new AtomicLong();
  private static final Semaphore querySemaphore;
  private final DataSource dataSource;
  private Connection jdbcConnection;
  private ResultSet resultSet;
  private final String sql;
  private final List<SqlStatement.Type> types;
  private final int maxRows;
  private final int firstRowOrdinal;
  private final Locus locus;
  private final int resultSetType;
  private final int resultSetConcurrency;
  private boolean haveSemaphore;
  public int rowCount;
  private long startTimeMillis;
  private final List<SqlStatement.Accessor> accessors = new ArrayList();
  private SqlStatement.State state;
  private final long id;
  private Functor1<Void, Statement> callback;
  public static HashMap<Statement, RolapDrillThroughAction> DrillThroughResults;

  public SqlStatement(DataSource dataSource, String sql, List<SqlStatement.Type> types, int maxRows, int firstRowOrdinal, Locus locus, int resultSetType, int resultSetConcurrency, Functor1<Void, Statement> callback) {
    this.state = SqlStatement.State.FRESH;
    this.callback = callback;
    this.id = ID_GENERATOR.getAndIncrement();
    this.dataSource = dataSource;
    this.sql = sql;
    this.types = types;
    this.maxRows = maxRows;
    this.firstRowOrdinal = firstRowOrdinal;
    this.locus = locus;
    this.resultSetType = resultSetType;
    this.resultSetConcurrency = resultSetConcurrency;
  }

  public void execute() {
    assert this.state == SqlStatement.State.FRESH : "cannot re-execute";

    this.state = SqlStatement.State.ACTIVE;
    Counters.SQL_STATEMENT_EXECUTE_COUNT.incrementAndGet();
    Counters.SQL_STATEMENT_EXECUTING_IDS.add(this.id);
    String status = "failed";
    Statement statement = null;

    try {
      this.locus.execution.checkCancelOrTimeout();
      this.jdbcConnection = this.dataSource.getConnection();
      querySemaphore.acquire();
      this.haveSemaphore = true;
      if (RolapUtil.SQL_LOGGER.isDebugEnabled()) {
        StringBuilder sqllog = new StringBuilder();
        sqllog.append(this.id).append(": ").append(this.locus.component).append(": executing sql [");
        if (this.sql.indexOf(10) >= 0) {
          sqllog.append("\n");
        }

        sqllog.append(this.sql);
        sqllog.append(']');
        RolapUtil.SQL_LOGGER.debug(sqllog.toString());
      }

      ExecuteQueryHook hook = RolapUtil.getHook();
      if (hook != null) {
        hook.onExecuteQuery(this.sql);
      }

      this.locus.execution.checkCancelOrTimeout();
      long startTimeNanos = System.nanoTime();
      this.startTimeMillis = System.currentTimeMillis();
      if (this.resultSetType >= 0 && this.resultSetConcurrency >= 0) {
        statement = this.jdbcConnection.createStatement(this.resultSetType, this.resultSetConcurrency);
      } else {
        statement = this.jdbcConnection.createStatement();
      }

      if (this.maxRows > 0) {
        statement.setMaxRows(this.maxRows);
      }

      if (this.getPurpose() != Purpose.CELL_SEGMENT) {
        this.locus.execution.registerStatement(this.locus, statement);
      } else if (this.callback != null) {
        this.callback.apply(statement);
      }

      this.locus.getServer().getMonitor().sendEvent(new SqlStatementStartEvent(this.startTimeMillis, this.id, this.locus, this.sql, this.getPurpose(), this.getCellRequestCount()));
      this.resultSet = statement.executeQuery(this.sql);
      this.state = SqlStatement.State.ACTIVE;
      if (this.firstRowOrdinal > 0) {
        if (this.resultSetType == 1003) {
          for(int i = 0; i < this.firstRowOrdinal; ++i) {
            if (!this.resultSet.next()) {
              this.state = SqlStatement.State.DONE;
              break;
            }
          }
        } else if (!this.resultSet.absolute(this.firstRowOrdinal)) {
          this.state = SqlStatement.State.DONE;
        }
      }

      long timeMillis = System.currentTimeMillis();
      long timeNanos = System.nanoTime();
      long executeNanos = timeNanos - startTimeNanos;
      long executeMillis = executeNanos / 1000000L;
      Util.addDatabaseTime(executeMillis);
      status = ", exec " + executeMillis + " ms";
      this.locus.getServer().getMonitor().sendEvent(new SqlStatementExecuteEvent(timeMillis, this.id, this.locus, this.sql, this.getPurpose(), executeNanos));
      this.accessors.clear();
      Iterator var14 = this.guessTypes().iterator();

      while(var14.hasNext()) {
        SqlStatement.Type type = (SqlStatement.Type)var14.next();
        this.accessors.add(this.createAccessor(this.accessors.size(), type));
      }
    } catch (Throwable var19) {
      status = ", failed (" + var19 + ")";
      Util.close((ResultSet)null, statement, (Connection)null);
      throw this.handle(var19);
    } finally {
      RolapUtil.SQL_LOGGER.debug(this.id + ": " + status);
      if (RolapUtil.LOGGER.isDebugEnabled()) {
        RolapUtil.LOGGER.debug(this.locus.component + ": executing sql [" + this.sql + "]" + status);
      }

    }

  }

  public void close() {
    if (this.state != SqlStatement.State.CLOSED) {
      this.state = SqlStatement.State.CLOSED;
      if (this.haveSemaphore) {
        this.haveSemaphore = false;
        querySemaphore.release();
      }

      SQLException ex = Util.close(this.resultSet, (Statement)null, this.jdbcConnection);
      this.resultSet = null;
      this.jdbcConnection = null;
      if (ex != null) {
        throw Util.newError(ex, this.locus.message + "; sql=[" + this.sql + "]");
      } else {
        long endTime = System.currentTimeMillis();
        long totalMs;
        if (this.startTimeMillis == 0L) {
          totalMs = 0L;
        } else {
          totalMs = endTime - this.startTimeMillis;
        }

        String status = this.formatTimingStatus(totalMs, this.rowCount);
        this.locus.execution.getQueryTiming().markFull("SqlStatement-" + this.locus.component, totalMs);
        RolapUtil.SQL_LOGGER.debug(this.id + ": " + status);
        Counters.SQL_STATEMENT_CLOSE_COUNT.incrementAndGet();
        boolean remove = Counters.SQL_STATEMENT_EXECUTING_IDS.remove(this.id);
        status = status + ", ex=" + Counters.SQL_STATEMENT_EXECUTE_COUNT.get() + ", close=" + Counters.SQL_STATEMENT_CLOSE_COUNT.get() + ", open=" + Counters.SQL_STATEMENT_EXECUTING_IDS;
        if (RolapUtil.LOGGER.isDebugEnabled()) {
          RolapUtil.LOGGER.debug(this.locus.component + ": done executing sql [" + this.sql + "]" + status);
        }

        if (!remove) {
          throw new AssertionError("SqlStatement closed that was never executed: " + this.id);
        } else {
          this.locus.getServer().getMonitor().sendEvent(new SqlStatementEndEvent(endTime, this.id, this.locus, this.sql, this.getPurpose(), (long)this.rowCount, false, (Throwable)null));
        }
      }
    }
  }

  String formatTimingStatus(long totalMs, int rowCount) {
    return ", exec+fetch " + totalMs + " ms, " + rowCount + " rows";
  }

  public ResultSet getResultSet() {
    return this.resultSet;
  }

  public RuntimeException handle(Throwable e) {
    RuntimeException runtimeException = Util.newError(e, this.locus.message + "; sql=[" + this.sql + "]");

    try {
      this.close();
    } catch (Throwable var4) {
    }

    return runtimeException;
  }

  private SqlStatement.Accessor createAccessor(int column, SqlStatement.Type type) {
    final int columnPlusOne = column + 1;
    switch(type) {
      case OBJECT:
        return new SqlStatement.Accessor() {
          public Object get() throws SQLException {
            return SqlStatement.this.resultSet.getObject(columnPlusOne);
          }
        };
      case STRING:
        return new SqlStatement.Accessor() {
          public Object get() throws SQLException {
            return SqlStatement.this.resultSet.getString(columnPlusOne);
          }
        };
      case INT:
        return new SqlStatement.Accessor() {
          public Object get() throws SQLException {
            int val = SqlStatement.this.resultSet.getInt(columnPlusOne);
            return val == 0 && SqlStatement.this.resultSet.wasNull() ? null : val;
          }
        };
      case LONG:
        return new SqlStatement.Accessor() {
          public Object get() throws SQLException {
            long val = SqlStatement.this.resultSet.getLong(columnPlusOne);
            return val == 0L && SqlStatement.this.resultSet.wasNull() ? null : val;
          }
        };
      case DOUBLE:
        return new SqlStatement.Accessor() {
          public Object get() throws SQLException {
            double val = SqlStatement.this.resultSet.getDouble(columnPlusOne);
            return val == 0.0D && SqlStatement.this.resultSet.wasNull() ? null : val;
          }
        };
      case DECIMAL:
        return new SqlStatement.Accessor() {
          public Object get() throws SQLException {
            BigDecimal decimal = SqlStatement.this.resultSet.getBigDecimal(columnPlusOne);
            if (decimal == null && SqlStatement.this.resultSet.wasNull()) {
              return null;
            } else {
              double val = SqlStatement.this.resultSet.getBigDecimal(columnPlusOne).doubleValue();
              if (val != Double.NEGATIVE_INFINITY && val != Double.POSITIVE_INFINITY) {
                return val;
              } else {
                throw MondrianResource.instance().JavaDoubleOverflow.ex(SqlStatement.this.resultSet.getMetaData().getColumnName(columnPlusOne));
              }
            }
          }
        };
      default:
        throw Util.unexpected(type);
    }
  }

  public List<SqlStatement.Type> guessTypes() throws SQLException {
    ResultSetMetaData metaData = this.resultSet.getMetaData();
    int columnCount = metaData.getColumnCount();

    assert this.types == null || this.types.size() == columnCount;

    List<SqlStatement.Type> typeList = new ArrayList();

    for(int i = 0; i < columnCount; ++i) {
      SqlStatement.Type suggestedType = this.types == null ? null : (SqlStatement.Type)this.types.get(i);
      RolapSchema schema = this.locus.execution.getMondrianStatement().getMondrianConnection().getSchema();
      Dialect dialect = this.getDialect(schema);
      if (suggestedType != null) {
        typeList.add(suggestedType);
      } else if (dialect != null) {
        typeList.add(dialect.getType(metaData, i));
      } else {
        typeList.add(SqlStatement.Type.OBJECT);
      }
    }

    return typeList;
  }

  protected Dialect getDialect(RolapSchema schema) {
    Dialect dialect = null;
    if (schema != null && schema.getDialect() != null) {
      dialect = schema.getDialect();
    } else {
      dialect = this.createDialect();
    }

    return dialect;
  }

  protected Dialect createDialect() {
    return DialectManager.createDialect(this.dataSource, this.jdbcConnection);
  }

  public List<SqlStatement.Accessor> getAccessors() {
    return this.accessors;
  }

  public ResultSet getWrappedResultSet() {
    return (ResultSet)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{ResultSet.class}, new SqlStatement.MyDelegatingInvocationHandler(this));
  }

  private Purpose getPurpose() {
    return this.locus instanceof SqlStatement.StatementLocus ? ((SqlStatement.StatementLocus)this.locus).purpose : Purpose.OTHER;
  }

  private int getCellRequestCount() {
    return this.locus instanceof SqlStatement.StatementLocus ? ((SqlStatement.StatementLocus)this.locus).cellRequestCount : 0;
  }

  static {
    querySemaphore = new Semaphore(MondrianProperties.instance().QueryLimit.get(), true);
    DrillThroughResults = new HashMap();
  }

  public static class StatementLocus extends Locus {
    private final Purpose purpose;
    private final int cellRequestCount;

    public StatementLocus(Execution execution, String component, String message, Purpose purpose, int cellRequestCount) {
      super(execution, component, message);
      this.purpose = purpose;
      this.cellRequestCount = cellRequestCount;
    }
  }

  private static enum State {
    FRESH,
    ACTIVE,
    DONE,
    CLOSED;
  }

  public static class MyDelegatingInvocationHandler extends DelegatingInvocationHandler {
    private final SqlStatement sqlStatement;

    MyDelegatingInvocationHandler(SqlStatement sqlStatement) {
      this.sqlStatement = sqlStatement;
    }

    protected Object getTarget() throws InvocationTargetException {
      ResultSet resultSet = this.sqlStatement.getResultSet();
      if (resultSet == null) {
        throw new InvocationTargetException(new SQLException("Invalid operation. Statement is closed."));
      } else {
        return resultSet;
      }
    }

    public void close() throws SQLException {
      Statement statment = this.sqlStatement.getResultSet().getStatement();
      this.sqlStatement.close();
      if (SqlStatement.DrillThroughResults.containsKey(statment)) {
        SqlStatement.DrillThroughResults.remove(statment);
      }

    }
  }

  public interface Accessor {
    Object get() throws SQLException;
  }

  public static enum Type {
    OBJECT,
    DOUBLE,
    INT,
    LONG,
    STRING,
    DECIMAL;

    public Object get(ResultSet resultSet, int column) throws SQLException {
      switch(this) {
        case OBJECT:
          return resultSet.getObject(column + 1);
        case STRING:
          return resultSet.getString(column + 1);
        case INT:
          return resultSet.getInt(column + 1);
        case LONG:
          return resultSet.getLong(column + 1);
        case DOUBLE:
          return resultSet.getDouble(column + 1);
        case DECIMAL:
          BigDecimal decimal = resultSet.getBigDecimal(column + 1);
          return decimal == null ? null : decimal.doubleValue();
        default:
          throw Util.unexpected(this);
      }
    }
  }
}