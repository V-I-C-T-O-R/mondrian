/* Decompiler 2708ms, total 3756ms, lines 2897 */
package mondrian.xmla;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import javax.sql.DataSource;
import mondrian.mdx.QueryPrintWriter;
import mondrian.mdx.UnresolvedFunCall;
import mondrian.olap.CalculatedFormula;
import mondrian.olap.DmvQuery;
import mondrian.olap.DrillThrough;
import mondrian.olap.Exp;
import mondrian.olap.Formula;
import mondrian.olap.Id;
import mondrian.olap.Literal;
import mondrian.olap.MondrianProperties;
import mondrian.olap.MondrianServer;
import mondrian.olap.QueryPart;
import mondrian.olap.Refresh;
import mondrian.olap.TransactionCommand;
import mondrian.olap.Update;
import mondrian.olap.Util;
import mondrian.olap.Id.NameSegment;
import mondrian.olap.MondrianDef.Role;
import mondrian.olap.MondrianDef.RoleMember;
import mondrian.olap.MondrianDef.Schema;
import mondrian.olap.TransactionCommand.Command;
import mondrian.olap.Update.UpdateClause;
import mondrian.olap.Util.Functor1;
import mondrian.olap4j.IMondrianOlap4jProperty;
import mondrian.olap4j.MondrianOlap4jCell;
import mondrian.olap4j.MondrianOlap4jConnection;
import mondrian.olap4j.MondrianOlap4jMember;
import mondrian.rolap.RolapConnection;
import mondrian.rolap.RolapCube;
import mondrian.rolap.RolapDrillThroughAction;
import mondrian.rolap.RolapDrillThroughColumn;
import mondrian.rolap.RolapSchema;
import mondrian.rolap.SqlStatement;
import mondrian.server.FileRepository;
import mondrian.server.Locus;
import mondrian.server.Repository;
import mondrian.server.Session;
import mondrian.server.Statement;
import mondrian.server.Locus.Action;
import mondrian.util.ByteString;
import mondrian.util.CompositeList;
import mondrian.xmla.Enumeration.ResponseMimeType;
import mondrian.xmla.Rowset.Row;
import mondrian.xmla.XmlaUtil.ElementNameEncoder;
import mondrian.xmla.impl.DefaultSaxWriter;
import mondrian.xmla.impl.DefaultXmlaRequest;
import mondrian.xmla.impl.DmvXmlaRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.olap4j.AllocationPolicy;
import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.CellSetAxisMetaData;
import org.olap4j.OlapConnection;
import org.olap4j.OlapException;
import org.olap4j.OlapStatement;
import org.olap4j.Position;
import org.olap4j.PreparedOlapStatement;
import org.olap4j.Scenario;
import org.olap4j.impl.Olap4jUtil;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Database;
import org.olap4j.metadata.Datatype;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Member;
import org.olap4j.metadata.MetadataElement;
import org.olap4j.metadata.Property;
import org.olap4j.metadata.Level.Type;
import org.olap4j.metadata.Property.ContentType;
import org.olap4j.metadata.Property.StandardCellProperty;
import org.olap4j.metadata.Property.StandardMemberProperty;
import org.olap4j.metadata.Property.TypeFlag;
import org.olap4j.metadata.XmlaConstants.AxisFormat;
import org.olap4j.metadata.XmlaConstants.Content;
import org.olap4j.metadata.XmlaConstants.Format;
import org.olap4j.metadata.XmlaConstants.Method;
import org.xml.sax.SAXException;

public class XmlaHandler {
    private static final Logger LOGGER = LogManager.getLogger(XmlaHandler.class);
    private static final String JDBC_USER = "user";
    private static final String JDBC_PASSWORD = "password";
    public static final String JDBC_LOCALE = "locale";
    final XmlaHandler.ConnectionFactory connectionFactory;
    private final String prefix;
    private static final String EMPTY_ROW_SET_XML_SCHEMA;
    private static final String MD_DATA_SET_XML_SCHEMA;
    private static final String EMPTY_MD_DATA_SET_XML_SCHEMA;
    private static final String NS_XML_SQL = "urn:schemas-microsoft-com:xml-sql";
    public static final String XSD_BOOLEAN = "xsd:boolean";
    public static final String XSD_STRING = "xsd:string";
    public static final String XSD_UNSIGNED_INT = "xsd:unsignedInt";
    public static final String XSD_BYTE = "xsd:byte";
    public static final byte XSD_BYTE_MAX_INCLUSIVE = 127;
    public static final byte XSD_BYTE_MIN_INCLUSIVE = -128;
    public static final String XSD_SHORT = "xsd:short";
    public static final short XSD_SHORT_MAX_INCLUSIVE = 32767;
    public static final short XSD_SHORT_MIN_INCLUSIVE = -32768;
    public static final String XSD_INT = "xsd:int";
    public static final int XSD_INT_MAX_INCLUSIVE = Integer.MAX_VALUE;
    public static final int XSD_INT_MIN_INCLUSIVE = Integer.MIN_VALUE;
    public static final String XSD_LONG = "xsd:long";
    public static final long XSD_LONG_MAX_INCLUSIVE = Long.MAX_VALUE;
    public static final long XSD_LONG_MIN_INCLUSIVE = Long.MIN_VALUE;
    public static final String XSD_DOUBLE = "xsd:double";
    public static final String XSD_FLOAT = "xsd:float";
    public static final String XSD_DECIMAL = "xsd:decimal";
    public static final String XSD_INTEGER = "xsd:integer";
    private ArrayList<XmlaRequest> currentRequests = new ArrayList();

    public static XmlaHandler.XmlaExtra getExtra(OlapConnection connection) {
        try {
            XmlaHandler.XmlaExtra extra = (XmlaHandler.XmlaExtra)connection.unwrap(XmlaHandler.XmlaExtra.class);
            if (extra != null) {
                return extra;
            }
        } catch (SQLException var3) {
        } catch (UndeclaredThrowableException var4) {
            Throwable cause = var4.getCause();
            if (cause instanceof InvocationTargetException) {
                cause = cause.getCause();
            }

            if (!(cause instanceof SQLException)) {
                throw var4;
            }
        }

        return new XmlaHandler.XmlaExtraImpl();
    }

    public OlapConnection getConnection(XmlaRequest request, Map<String, String> propMap) throws OlapException {
        String sessionId = request.getSessionId();
        if (sessionId == null) {
            sessionId = "<no_session>";
        }

        LOGGER.debug("Creating new connection for user [" + request.getUsername() + "] and session [" + sessionId + "]");
        Properties props = new Properties();
        props.put("sessionId", sessionId);
        Iterator var5 = propMap.entrySet().iterator();

        while(var5.hasNext()) {
            Entry<String, String> entry = (Entry)var5.next();
            props.put(entry.getKey(), entry.getValue());
        }

        if (request.getUsername() != null) {
            props.put("user", request.getUsername());
        }

        if (request.getPassword() != null) {
            props.put("password", request.getPassword());
        }

        String databaseName = (String)request.getProperties().get(PropertyDefinition.DataSourceInfo.name());
        String catalogName = (String)request.getProperties().get(PropertyDefinition.Catalog.name());
        if (catalogName == null && request.getMethod() == Method.DISCOVER && request.getRestrictions().containsKey(StandardMemberProperty.CATALOG_NAME.name())) {
            Object restriction = request.getRestrictions().get(StandardMemberProperty.CATALOG_NAME.name());
            if (!(restriction instanceof List)) {
                throw Util.newInternal("unexpected restriction type: " + restriction.getClass());
            }

            List requiredValues = (List)restriction;
            catalogName = String.valueOf(requiredValues.get(0));
        }

        OlapConnection connection = this.getConnection(databaseName, catalogName, request.getRoleName(), props);
        ArrayList<String> authenticatedUserAndGroups = new ArrayList();
        if (request.getAuthenticatedUser() != null) {
            authenticatedUserAndGroups.add(request.getAuthenticatedUser());
        }

        if (request.getAuthenticatedUserGroups() != null) {
            authenticatedUserAndGroups.addAll(Arrays.asList(request.getAuthenticatedUserGroups()));
        }

        if (authenticatedUserAndGroups.size() > 0) {
            ArrayList<String> roles = new ArrayList();
            RolapSchema rolapSchema = ((MondrianOlap4jConnection)connection).getMondrianConnection().getSchema();
            Schema xmlSchema = rolapSchema.getXMLSchema();
            Role[] var12 = xmlSchema.roles;
            int var13 = var12.length;

            for(int var14 = 0; var14 < var13; ++var14) {
                Role role = var12[var14];
                RoleMember[] var16 = role.members;
                int var17 = var16.length;

                for(int var18 = 0; var18 < var17; ++var18) {
                    RoleMember roleMember = var16[var18];
                    boolean inRole = false;
                    if (roleMember.name != null) {
                        String roleMemberName = roleMember.name.trim().toLowerCase(Locale.ROOT);
                        Iterator var22 = authenticatedUserAndGroups.iterator();

                        while(var22.hasNext()) {
                            String aRole = (String)var22.next();
                            if (roleMemberName.equals(aRole.toLowerCase(Locale.ROOT))) {
                                roles.add(role.name);
                                inRole = true;
                                break;
                            }
                        }
                    }

                    if (inRole) {
                        break;
                    }
                }
            }

            if (roles.size() > 0) {
                ((MondrianOlap4jConnection)connection).setRoleNames(roles);
            }
        }

        String localeIdentifier = (String)request.getProperties().get("LocaleIdentifier");
        Locale locale = XmlaUtil.convertToLocale(localeIdentifier);
        if (locale != null) {
            connection.setLocale(locale);
        }

        Session session = Session.getWithoutCheck(sessionId);
        if (session != null) {
            connection.setScenario(session.getScenario());
        }

        return connection;
    }

    public static boolean isValidXsdInt(long l) {
        return l <= 2147483647L && l >= -2147483648L;
    }

    private static String computeXsd(XmlaHandler.SetType setType) {
        StringWriter sw = new StringWriter();
        SaxWriter writer = new DefaultSaxWriter(new PrintWriter(sw), 3);
        writeDatasetXmlSchema(writer, setType);
        writer.flush();
        return sw.toString();
    }

    private static String computeEmptyXsd(XmlaHandler.SetType setType) {
        StringWriter sw = new StringWriter();
        SaxWriter writer = new DefaultSaxWriter(new PrintWriter(sw), 3);
        writeEmptyDatasetXmlSchema(writer, setType);
        writer.flush();
        return sw.toString();
    }

    public XmlaHandler(XmlaHandler.ConnectionFactory connectionFactory, String prefix) {
        assert prefix != null;

        this.connectionFactory = connectionFactory;
        this.prefix = prefix;
    }

    public void process(XmlaRequest request, XmlaResponse response) throws XmlaException {
        Method method = request.getMethod();
        long start = System.currentTimeMillis();
        switch(method) {
            case DISCOVER:
                this.discover(request, response);
                break;
            case EXECUTE:
                this.execute(request, response);
                break;
            default:
                throw new XmlaException("Client", "00HSBB02", "XMLA SOAP bad method", new IllegalArgumentException("Unsupported XML/A method: " + method));
        }

        if (LOGGER.isDebugEnabled()) {
            long end = System.currentTimeMillis();
            LOGGER.debug("XmlaHandler.process: time = " + (end - start));
            LOGGER.debug("XmlaHandler.process: " + Util.printMemory());
        }

    }

    private void checkFormat(XmlaRequest request) throws XmlaException {
        Map<String, String> properties = request.getProperties();
        if (request.isDrillThrough()) {
            Format format = getFormat(request, (Format)null);
            if (format != Format.Tabular) {
                throw new XmlaException("Client", "00HSBE02", "XMLA Drill Through format error", new UnsupportedOperationException("<Format>: only 'Tabular' allowed when drilling through"));
            }
        } else {
            String formatName = (String)properties.get(PropertyDefinition.Format.name());
            if (formatName != null) {
                Format format = getFormat(request, (Format)null);
                if (format != Format.Multidimensional && format != Format.Tabular && format != Format.Native) {
                    throw new UnsupportedOperationException("<Format>: only 'Multidimensional', 'Tabular' and 'Native' currently supported");
                }
            }

            String axisFormatName = (String)properties.get(PropertyDefinition.AxisFormat.name());
            if (axisFormatName != null) {
                AxisFormat axisFormat = (AxisFormat)Util.lookup(AxisFormat.class, axisFormatName, null);
                if (axisFormat != AxisFormat.TupleFormat) {
                    throw new UnsupportedOperationException("<AxisFormat>: only 'TupleFormat' currently supported");
                }
            }
        }

    }

    private void checkedCanceled(XmlaRequest request) {
        String canceled = (String)request.getProperties().get("CANCELED");
        if (canceled != null && canceled.equals("true")) {
            throw new XmlaException("Client", "3238658121", "", new Exception("The query was canceled by user."));
        }
    }

    private void execute(XmlaRequest request, XmlaResponse response) throws XmlaException {
        DefaultXmlaRequest defaultXmlaRequest = (DefaultXmlaRequest)request;
        this.currentRequests.add(request);
        Map<String, String> properties = request.getProperties();
        ResponseMimeType responseMimeType = getResponseMimeType(request);
        String contentName = (String)properties.get(PropertyDefinition.Content.name());
        Content content = (Content)Util.lookup(Content.class, contentName, responseMimeType == ResponseMimeType.JSON ? Content.Data : Content.DEFAULT);
        Object result = null;

        try {
            OlapConnection connection;
            RolapConnection rolapConnection;
            boolean rowset;
            if (defaultXmlaRequest.getCommand().toUpperCase().equals("CANCEL")) {
                try {
                    connection = this.getConnection(request, Collections.emptyMap());
                    rolapConnection = ((MondrianOlap4jConnection)connection).getMondrianConnection();
                    Iterator var84 = this.currentRequests.iterator();

                    while(var84.hasNext()) {
                        XmlaRequest xmlaRequest = (XmlaRequest)var84.next();
                        if (xmlaRequest.getSessionId().equals(rolapConnection.getConnectInfo().get("sessionId"))) {
                            ((DefaultXmlaRequest)xmlaRequest).setProperty("CANCELED", "true");
                        }
                    }

                    MondrianServer mondrianServer = MondrianServer.forConnection(rolapConnection);
                    String sessionId = rolapConnection.getConnectInfo().get("sessionId");
                    Iterator var98 = mondrianServer.getStatements(sessionId).iterator();

                    label910:
                    while(true) {
                        if (!var98.hasNext()) {
                            var98 = this.currentRequests.iterator();

                            while(true) {
                                if (!var98.hasNext()) {
                                    break label910;
                                }

                                XmlaRequest xmlaRequest = (XmlaRequest)var98.next();
                                if (xmlaRequest.getSessionId().equals(sessionId)) {
                                    ((DefaultXmlaRequest)xmlaRequest).setProperty("CANCELED", "true");
                                }
                            }
                        }

                        Statement statement = (Statement)var98.next();
                        if (statement.getMondrianConnection().getConnectInfo().get("sessionId").equals(rolapConnection.getConnectInfo().get("sessionId"))) {
                            statement.cancel();
                        }
                    }
                } catch (OlapException var76) {
                    throw new XmlaException("Client", "3238658121", "DOM parse errors occur", var76);
                } catch (SQLException var77) {
                    throw new XmlaException("Client", "3238658121", "DOM parse errors occur", var77);
                }
            } else {
                String tupleString;
                String key;
                String newKey;
                if (defaultXmlaRequest.getCommand().toUpperCase().equals("ALTER")) {
                    boolean validate = "true".equals(defaultXmlaRequest.getProperties().get("Validate"));
                    rowset = "Database".equals(defaultXmlaRequest.getProperties().get("ObjectType"));
                    boolean alterSchema = "Schema".equals(defaultXmlaRequest.getProperties().get("ObjectType"));
                    if (rowset) {
                        OlapConnection connection1 = this.getConnection(request, Collections.emptyMap());
                        RolapConnection rolapConnection1 = ((MondrianOlap4jConnection)connection1).getMondrianConnection();
                        MondrianServer mondrianServer = MondrianServer.forConnection(rolapConnection1);
                        Repository repository = mondrianServer.getRepository();
                        if (repository instanceof FileRepository) {
                            FileRepository fileRepository = (FileRepository)repository;
                            newKey = ((DefaultXmlaRequest)request).getObjectDefinition();
                            fileRepository.setContent(newKey);
                        }
                    } else if (alterSchema) {
                        ServerObject serverObject = ((DefaultXmlaRequest)request).getServerObject();
                        if (serverObject != null) {
                            OlapConnection connection1 = this.getConnection((String)null, serverObject.getDatabaseID(), (String)null);

                            try {
                                RolapConnection rolapConnection1 = ((MondrianOlap4jConnection)connection1).getMondrianConnection();
                                String catalogUrl = rolapConnection1.getCatalogName();
                                key = ((DefaultXmlaRequest)request).getObjectDefinition();
                                RolapSchema prevSchema = rolapConnection1.getSchema();
                                if (validate) {
                                    List<XmlaHandler.Column> columns = new ArrayList();
                                    columns.add(new XmlaHandler.Column("Text", 12, 0));
                                    ArrayList rows = new ArrayList();

                                    try {
                                        new RolapSchema(prevSchema.getKey(), (ByteString)null, catalogUrl, key, rolapConnection1.getConnectInfo(), (DataSource)null);
                                    } catch (RuntimeException var70) {
                                        rows.add(new Object[]{var70.getMessage()});
                                    }

                                    result = new XmlaHandler.TabularRowSet(columns, rows);
                                } else {
                                    new RolapSchema(prevSchema.getKey(), (ByteString)null, catalogUrl, key, rolapConnection1.getConnectInfo(), (DataSource)null);
                                    tupleString = URI.create(catalogUrl).getPath();
                                    BufferedWriter out = new BufferedWriter(new FileWriter(tupleString));

                                    try {
                                        out.write(key);
                                    } finally {
                                        out.close();
                                    }
                                }
                            } catch (OlapException var71) {
                                throw new XmlaException("Client", "3238658121", "DOM parse errors occur", var71);
                            } catch (IOException var72) {
                                throw new XmlaException("Client", "3238658121", "DOM parse errors occur", var72);
                            }
                        }
                    }
                } else if (defaultXmlaRequest.getCommand().toUpperCase().equals("STATEMENT")) {
                    connection = this.getConnection(request, Collections.emptyMap());
                    rolapConnection = ((MondrianOlap4jConnection)connection).getMondrianConnection();
                    String mdx = request.getStatement();
                    QueryPart queryPart = null;
                    if (mdx != null && !mdx.isEmpty()) {
                        queryPart = rolapConnection.parseStatement(mdx);
                    }

                    if (queryPart instanceof DrillThrough) {
                        result = this.executeDrillThroughQuery(request);
                    } else if (queryPart instanceof CalculatedFormula) {
                        CalculatedFormula calculatedFormula = (CalculatedFormula)queryPart;
                        Formula formula = calculatedFormula.getFormula();
                        RolapSchema schema = rolapConnection.getSchema();
                        RolapCube cube = (RolapCube)schema.lookupCube(calculatedFormula.getCubeName(), true);
                        if (formula.isMember()) {
                            cube.createCalculatedMember(formula);
                        } else {
                            cube.createNamedSet(formula);
                        }
                    } else {
                        Iterator var100;
                        if (queryPart instanceof DmvQuery) {
                            DmvQuery dmvQuery = (DmvQuery)queryPart;
                            HashMap<String, String> upperCaseProperties = new HashMap();

                            for(var100 = request.getProperties().keySet().iterator(); var100.hasNext(); upperCaseProperties.put(newKey, (String)request.getProperties().get(key))) {
                                key = (String)var100.next();
                                newKey = null;
                                if (key != null) {
                                    newKey = key.toUpperCase();
                                }
                            }

                            HashMap<String, Object> restrictions = new HashMap();
                            if (upperCaseProperties.containsKey(PropertyDefinition.Catalog.name().toUpperCase())) {
                                List<String> restriction = new ArrayList();
                                restriction.add((String)request.getProperties().get("Catalog"));
                                restrictions.put(StandardMemberProperty.CATALOG_NAME.name(), restriction);
                            }

                            DmvXmlaRequest dmvXmlaRequest = new DmvXmlaRequest(restrictions, request.getProperties(), request.getRoleName(), dmvQuery.getTableName().toUpperCase(), request.getUsername(), request.getPassword(), request.getSessionId());
                            this.executeDmvQuery(dmvXmlaRequest, response, dmvQuery.getTableName().toUpperCase(), dmvQuery.getWhereExpression(), defaultXmlaRequest.getParameters());
                            return;
                        }

                        RolapSchema schema;
                        if (queryPart instanceof Refresh) {
                            Refresh refresh = (Refresh)queryPart;
                            schema = rolapConnection.getSchema();
                            RolapCube cube = (RolapCube)schema.lookupCube(refresh.getCubeName(), true);
                            cube.flushCache(rolapConnection);
                        } else if (queryPart instanceof Update) {
                            Update update = (Update)queryPart;
                            schema = rolapConnection.getSchema();
                            var100 = update.getUpdateClauses().iterator();

                            while(var100.hasNext()) {
                                UpdateClause updateClause = (UpdateClause)var100.next();
                                StringWriter sw = new StringWriter();
                                PrintWriter pw = new QueryPrintWriter(sw);
                                updateClause.getTupleExp().unparse(pw);
                                tupleString = sw.toString();
                                PreparedOlapStatement pstmt = connection.prepareOlapStatement("SELECT " + tupleString + " ON 0 FROM " + update.getCubeName() + " CELL PROPERTIES CELL_ORDINAL");
                                CellSet cellSet = pstmt.executeQuery();
                                CellSetAxis axis = (CellSetAxis)cellSet.getAxes().get(0);
                                if (axis.getPositionCount() == 0) {
                                }

                                if (axis.getPositionCount() == 1) {
                                }

                                Cell writeBackCell = cellSet.getCell(Arrays.asList(0));
                                sw = new StringWriter();
                                pw = new QueryPrintWriter(sw);
                                updateClause.getValueExp().unparse(pw);
                                String valueString = sw.toString();
                                pstmt = connection.prepareOlapStatement("WITH MEMBER [Measures].[m1] AS " + valueString + " SELECT [Measures].[m1] ON 0 FROM " + update.getCubeName() + " CELL PROPERTIES VALUE");
                                cellSet = pstmt.executeQuery();
                                Cell cell = cellSet.getCell(Arrays.asList(0));
                                Double doubleValue = cell.getDoubleValue();
                                writeBackCell.setValue(doubleValue, AllocationPolicy.EQUAL_ALLOCATION, new Object[0]);
                            }
                        } else if (queryPart instanceof TransactionCommand) {
                            TransactionCommand transactionCommand = (TransactionCommand)queryPart;
                            String sessionId = request.getSessionId();
                            Session session = Session.get(sessionId);
                            if (transactionCommand.getCommand() == Command.BEGIN) {
                                Scenario scenario = connection.createScenario();
                                session.setScenario(scenario);
                            } else if (transactionCommand.getCommand() == Command.ROLLBACK) {
                                session.setScenario((Scenario)null);
                            } else if (transactionCommand.getCommand() == Command.COMMIT) {
                                session.setScenario((Scenario)null);
                            }
                        } else {
                            this.checkedCanceled(request);
                            result = this.executeQuery(request);
                        }
                    }
                }
            }

            this.checkedCanceled(request);
            SaxWriter writer = response.getWriter();
            writer.startDocument();
            writer.startElement("ExecuteResponse", new Object[]{"xmlns", "urn:schemas-microsoft-com:xml-analysis"});
            writer.startElement("return");
            rowset = request.isDrillThrough() || Format.Tabular.name().equals(request.getProperties().get(PropertyDefinition.Format.name()));
            writer.startElement("root", new Object[]{"xmlns", result == null ? "urn:schemas-microsoft-com:xml-analysis:empty" : (rowset ? "urn:schemas-microsoft-com:xml-analysis:rowset" : "urn:schemas-microsoft-com:xml-analysis:mddataset"), "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema", "xmlns:EX", "urn:schemas-microsoft-com:xml-analysis:exception"});
            switch(content) {
                case Schema:
                case SchemaData:
                    if (result != null) {
                        ((XmlaHandler.QueryResult)result).metadata(writer);
                    }
            }

            try {
                switch(content) {
                    case SchemaData:
                    case Data:
                    case DataOmitDefaultSlicer:
                    case DataIncludeDefaultSlicer:
                        if (result != null) {
                            ((XmlaHandler.QueryResult)result).unparse(writer);
                        }
                }
            } catch (XmlaException var73) {
                throw var73;
            } catch (Throwable var74) {
                throw new XmlaException("Server", "00HSBE03", "XMLA Execute unparse results error", var74);
            } finally {
                writer.endElement();
                writer.endElement();
                writer.endElement();
            }

            this.checkedCanceled(request);
            writer.endDocument();
        } catch (OlapException var78) {
            throw new XmlaException("Client", "3238658121", "DOM parse errors occur", var78);
        } finally {
            this.currentRequests.remove(request);
            if (result != null) {
                try {
                    ((XmlaHandler.QueryResult)result).close();
                } catch (SQLException var68) {
                }
            }

        }

    }

    static void writeDatasetXmlSchema(SaxWriter writer, XmlaHandler.SetType settype) {
        String setNsXmla = settype == XmlaHandler.SetType.ROW_SET ? "urn:schemas-microsoft-com:xml-analysis:rowset" : "urn:schemas-microsoft-com:xml-analysis:mddataset";
        writer.startElement("xsd:schema", new Object[]{"xmlns:xsd", "http://www.w3.org/2001/XMLSchema", "targetNamespace", setNsXmla, "xmlns", setNsXmla, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance", "xmlns:sql", "urn:schemas-microsoft-com:xml-sql", "elementFormDefault", "qualified"});
        writer.startElement("xsd:complexType", new Object[]{"name", "MemberType"});
        writer.startElement("xsd:sequence");
        writer.element("xsd:element", new Object[]{"name", "UName", "type", "xsd:string"});
        writer.element("xsd:element", new Object[]{"name", "Caption", "type", "xsd:string"});
        writer.element("xsd:element", new Object[]{"name", "LName", "type", "xsd:string"});
        writer.element("xsd:element", new Object[]{"name", "LNum", "type", "xsd:unsignedInt"});
        writer.element("xsd:element", new Object[]{"name", "DisplayInfo", "type", "xsd:unsignedInt"});
        writer.startElement("xsd:sequence", new Object[]{"maxOccurs", "unbounded", "minOccurs", 0});
        writer.element("xsd:any", new Object[]{"processContents", "lax", "maxOccurs", "unbounded"});
        writer.endElement();
        writer.endElement();
        writer.element("xsd:attribute", new Object[]{"name", "Hierarchy", "type", "xsd:string"});
        writer.endElement();
        writer.startElement("xsd:complexType", new Object[]{"name", "PropType"});
        writer.element("xsd:attribute", new Object[]{"name", "name", "type", "xsd:string"});
        writer.endElement();
        writer.startElement("xsd:complexType", new Object[]{"name", "TupleType"});
        writer.startElement("xsd:sequence", new Object[]{"maxOccurs", "unbounded"});
        writer.element("xsd:element", new Object[]{"name", "Member", "type", "MemberType"});
        writer.endElement();
        writer.endElement();
        writer.startElement("xsd:complexType", new Object[]{"name", "MembersType"});
        writer.startElement("xsd:sequence", new Object[]{"maxOccurs", "unbounded"});
        writer.element("xsd:element", new Object[]{"name", "Member", "type", "MemberType"});
        writer.endElement();
        writer.element("xsd:attribute", new Object[]{"name", "Hierarchy", "type", "xsd:string"});
        writer.endElement();
        writer.startElement("xsd:complexType", new Object[]{"name", "TuplesType"});
        writer.startElement("xsd:sequence", new Object[]{"maxOccurs", "unbounded"});
        writer.element("xsd:element", new Object[]{"name", "Tuple", "type", "TupleType"});
        writer.endElement();
        writer.endElement();
        writer.startElement("xsd:complexType", new Object[]{"name", "CrossProductType"});
        writer.startElement("xsd:sequence");
        writer.startElement("xsd:choice", new Object[]{"minOccurs", 0, "maxOccurs", "unbounded"});
        writer.element("xsd:element", new Object[]{"name", "Members", "type", "MembersType"});
        writer.element("xsd:element", new Object[]{"name", "Tuples", "type", "TuplesType"});
        writer.endElement();
        writer.endElement();
        writer.element("xsd:attribute", new Object[]{"name", "Size", "type", "xsd:unsignedInt"});
        writer.endElement();
        writer.startElement("xsd:complexType", new Object[]{"name", "OlapInfo"});
        writer.startElement("xsd:sequence");
        writer.startElement("xsd:element", new Object[]{"name", "CubeInfo"});
        writer.startElement("xsd:complexType");
        writer.startElement("xsd:sequence");
        writer.startElement("xsd:element", new Object[]{"name", "Cube", "maxOccurs", "unbounded"});
        writer.startElement("xsd:complexType");
        writer.startElement("xsd:sequence");
        writer.element("xsd:element", new Object[]{"name", "CubeName", "type", "xsd:string"});
        writer.endElement();
        writer.endElement();
        writer.endElement();
        writer.endElement();
        writer.endElement();
        writer.endElement();
        writer.startElement("xsd:element", new Object[]{"name", "AxesInfo"});
        writer.startElement("xsd:complexType");
        writer.startElement("xsd:sequence");
        writer.startElement("xsd:element", new Object[]{"name", "AxisInfo", "maxOccurs", "unbounded"});
        writer.startElement("xsd:complexType");
        writer.startElement("xsd:sequence");
        writer.startElement("xsd:element", new Object[]{"name", "HierarchyInfo", "minOccurs", 0, "maxOccurs", "unbounded"});
        writer.startElement("xsd:complexType");
        writer.startElement("xsd:sequence");
        writer.startElement("xsd:sequence", new Object[]{"maxOccurs", "unbounded"});
        writer.element("xsd:element", new Object[]{"name", "UName", "type", "PropType"});
        writer.element("xsd:element", new Object[]{"name", "Caption", "type", "PropType"});
        writer.element("xsd:element", new Object[]{"name", "LName", "type", "PropType"});
        writer.element("xsd:element", new Object[]{"name", "LNum", "type", "PropType"});
        writer.element("xsd:element", new Object[]{"name", "DisplayInfo", "type", "PropType", "minOccurs", 0, "maxOccurs", "unbounded"});
        writer.endElement();
        writer.startElement("xsd:sequence");
        writer.element("xsd:any", new Object[]{"processContents", "lax", "minOccurs", 0, "maxOccurs", "unbounded"});
        writer.endElement();
        writer.endElement();
        writer.element("xsd:attribute", new Object[]{"name", "name", "type", "xsd:string", "use", "required"});
        writer.endElement();
        writer.endElement();
        writer.endElement();
        writer.element("xsd:attribute", new Object[]{"name", "name", "type", "xsd:string"});
        writer.endElement();
        writer.endElement();
        writer.endElement();
        writer.endElement();
        writer.endElement();
        writer.startElement("xsd:element", new Object[]{"name", "CellInfo"});
        writer.startElement("xsd:complexType");
        writer.startElement("xsd:sequence");
        writer.startElement("xsd:sequence", new Object[]{"minOccurs", 0, "maxOccurs", "unbounded"});
        writer.startElement("xsd:choice");
        writer.element("xsd:element", new Object[]{"name", "Value", "type", "PropType"});
        writer.element("xsd:element", new Object[]{"name", "FmtValue", "type", "PropType"});
        writer.element("xsd:element", new Object[]{"name", "BackColor", "type", "PropType"});
        writer.element("xsd:element", new Object[]{"name", "ForeColor", "type", "PropType"});
        writer.element("xsd:element", new Object[]{"name", "FontName", "type", "PropType"});
        writer.element("xsd:element", new Object[]{"name", "FontSize", "type", "PropType"});
        writer.element("xsd:element", new Object[]{"name", "FontFlags", "type", "PropType"});
        writer.element("xsd:element", new Object[]{"name", "FormatString", "type", "PropType"});
        writer.element("xsd:element", new Object[]{"name", "NonEmptyBehavior", "type", "PropType"});
        writer.element("xsd:element", new Object[]{"name", "SolveOrder", "type", "PropType"});
        writer.element("xsd:element", new Object[]{"name", "Updateable", "type", "PropType"});
        writer.element("xsd:element", new Object[]{"name", "Visible", "type", "PropType"});
        writer.element("xsd:element", new Object[]{"name", "Expression", "type", "PropType"});
        writer.endElement();
        writer.endElement();
        writer.startElement("xsd:sequence", new Object[]{"maxOccurs", "unbounded", "minOccurs", 0});
        writer.element("xsd:any", new Object[]{"processContents", "lax", "maxOccurs", "unbounded"});
        writer.endElement();
        writer.endElement();
        writer.endElement();
        writer.endElement();
        writer.endElement();
        writer.endElement();
        writer.startElement("xsd:complexType", new Object[]{"name", "Axes"});
        writer.startElement("xsd:sequence", new Object[]{"maxOccurs", "unbounded"});
        writer.startElement("xsd:element", new Object[]{"name", "Axis"});
        writer.startElement("xsd:complexType");
        writer.startElement("xsd:choice", new Object[]{"minOccurs", 0, "maxOccurs", "unbounded"});
        writer.element("xsd:element", new Object[]{"name", "CrossProduct", "type", "CrossProductType"});
        writer.element("xsd:element", new Object[]{"name", "Tuples", "type", "TuplesType"});
        writer.element("xsd:element", new Object[]{"name", "Members", "type", "MembersType"});
        writer.endElement();
        writer.element("xsd:attribute", new Object[]{"name", "name", "type", "xsd:string"});
        writer.endElement();
        writer.endElement();
        writer.endElement();
        writer.endElement();
        writer.startElement("xsd:complexType", new Object[]{"name", "CellData"});
        writer.startElement("xsd:sequence");
        writer.startElement("xsd:element", new Object[]{"name", "Cell", "minOccurs", 0, "maxOccurs", "unbounded"});
        writer.startElement("xsd:complexType");
        writer.startElement("xsd:sequence", new Object[]{"maxOccurs", "unbounded"});
        writer.startElement("xsd:choice");
        writer.element("xsd:element", new Object[]{"name", "Value"});
        writer.element("xsd:element", new Object[]{"name", "FmtValue", "type", "xsd:string"});
        writer.element("xsd:element", new Object[]{"name", "BackColor", "type", "xsd:unsignedInt"});
        writer.element("xsd:element", new Object[]{"name", "ForeColor", "type", "xsd:unsignedInt"});
        writer.element("xsd:element", new Object[]{"name", "FontName", "type", "xsd:string"});
        writer.element("xsd:element", new Object[]{"name", "FontSize", "type", "xsd:unsignedShort"});
        writer.element("xsd:element", new Object[]{"name", "FontFlags", "type", "xsd:unsignedInt"});
        writer.element("xsd:element", new Object[]{"name", "FormatString", "type", "xsd:string"});
        writer.element("xsd:element", new Object[]{"name", "NonEmptyBehavior", "type", "xsd:unsignedShort"});
        writer.element("xsd:element", new Object[]{"name", "SolveOrder", "type", "xsd:unsignedInt"});
        writer.element("xsd:element", new Object[]{"name", "Updateable", "type", "xsd:unsignedInt"});
        writer.element("xsd:element", new Object[]{"name", "Visible", "type", "xsd:unsignedInt"});
        writer.element("xsd:element", new Object[]{"name", "Expression", "type", "xsd:string"});
        writer.endElement();
        writer.endElement();
        writer.element("xsd:attribute", new Object[]{"name", "CellOrdinal", "type", "xsd:unsignedInt", "use", "required"});
        writer.endElement();
        writer.endElement();
        writer.endElement();
        writer.endElement();
        writer.startElement("xsd:element", new Object[]{"name", "root"});
        writer.startElement("xsd:complexType");
        writer.startElement("xsd:sequence", new Object[]{"maxOccurs", "unbounded"});
        writer.element("xsd:element", new Object[]{"name", "OlapInfo", "type", "OlapInfo"});
        writer.element("xsd:element", new Object[]{"name", "Axes", "type", "Axes"});
        writer.element("xsd:element", new Object[]{"name", "CellData", "type", "CellData"});
        writer.endElement();
        writer.endElement();
        writer.endElement();
        writer.endElement();
    }

    static void writeEmptyDatasetXmlSchema(SaxWriter writer, XmlaHandler.SetType setType) {
        String setNsXmla = "urn:schemas-microsoft-com:xml-analysis:rowset";
        writer.startElement("xsd:schema", new Object[]{"xmlns:xsd", "http://www.w3.org/2001/XMLSchema", "targetNamespace", setNsXmla, "xmlns", setNsXmla, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance", "xmlns:sql", "urn:schemas-microsoft-com:xml-sql", "elementFormDefault", "qualified"});
        writer.element("xsd:element", new Object[]{"name", "root"});
        writer.endElement();
    }

    private XmlaHandler.QueryResult executeDrillThroughQuery(XmlaRequest request) throws XmlaException {
        this.checkFormat(request);
        Map<String, String> properties = request.getProperties();
        String tabFields = (String)properties.get(PropertyDefinition.TableFields.name());
        if (tabFields != null && tabFields.length() == 0) {
            tabFields = null;
        }

        String advancedFlag = (String)properties.get(PropertyDefinition.AdvancedFlag.name());
        boolean advanced = Boolean.parseBoolean(advancedFlag);
        boolean enableRowCount = MondrianProperties.instance().EnableTotalCount.booleanValue();
        int[] rowCountSlot = enableRowCount ? new int[]{0} : null;
        OlapConnection connection = null;
        OlapStatement statement = null;
        ResultSet resultSet = null;

        XmlaHandler.TabularRowSet var35;
        try {
            connection = this.getConnection(request, Collections.emptyMap());
            statement = connection.createStatement();
            resultSet = getExtra(connection).executeDrillthrough(statement, request.getStatement(), advanced, tabFields, rowCountSlot);
            int rowCount = enableRowCount ? rowCountSlot[0] : -1;
            RolapDrillThroughAction rolapDrillThroughAction = (RolapDrillThroughAction)SqlStatement.DrillThroughResults.get(resultSet.getStatement());
            XmlaHandler.TabularRowSet tabularRowSet = new XmlaHandler.TabularRowSet(resultSet, rowCount);
            if (rolapDrillThroughAction != null) {
                List<RolapDrillThroughColumn> rolapDrillThroughColumns = rolapDrillThroughAction.getColumns();

                for(int i = 0; i < rolapDrillThroughColumns.size(); ++i) {
                    tabularRowSet.setColumnName(i, ((RolapDrillThroughColumn)rolapDrillThroughColumns.get(i)).getName());
                }
            }

            var35 = tabularRowSet;
        } catch (XmlaException var31) {
            throw var31;
        } catch (SQLException var32) {
            throw new XmlaException("Server", "00HSBF02", "XMLA Drill Through SQL error", Util.newError(var32, "Error in drill through"));
        } catch (RuntimeException var33) {
            throw new XmlaException("Server", "00HSBF02", "XMLA Drill Through SQL error", var33);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException var30) {
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException var29) {
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException var28) {
                }
            }

        }

        return var35;
    }

    private static String sqlToXsdType(int sqlType, int scale) {
        switch(sqlType) {
            case -6:
            case 4:
            case 5:
                return "xsd:int";
            case -5:
                return "xsd:integer";
            case 2:
            case 3:
                if (scale == 0) {
                    return "xsd:int";
                }

                return "xsd:decimal";
            case 6:
            case 8:
                return "xsd:double";
            case 91:
            case 92:
            case 93:
                return "xsd:string";
            default:
                return "xsd:string";
        }
    }

    private XmlaHandler.QueryResult executeQuery(XmlaRequest request) throws XmlaException, OlapException {
        String mdx = request.getStatement();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("mdx: \"" + mdx + "\"");
        }

        if (mdx != null && mdx.length() != 0) {
            this.checkFormat(request);
            OlapConnection connection = null;
            PreparedOlapStatement statement = null;
            CellSet cellSet = null;
            boolean success = false;

            Object var11;
            try {
                connection = this.getConnection(request, Collections.emptyMap());
                getExtra(connection).setPreferList(connection);

                try {
                    statement = connection.prepareOlapStatement(mdx);
                } catch (XmlaException var29) {
                    throw var29;
                } catch (Exception var30) {
                    throw new XmlaException("Client", "00HSBD01", "XMLA MDX parse failed", var30);
                }

                try {
                    cellSet = statement.executeQuery();
                    Format format = getFormat(request, Format.Native);
                    Content content = getContent(request);
                    ResponseMimeType responseMimeType = getResponseMimeType(request);
                    Object dataSet;
                    if (format != Format.Multidimensional && format != Format.Native) {
                        dataSet = new XmlaHandler.MDDataSet_Tabular(cellSet);
                    } else {
                        dataSet = new XmlaHandler.MDDataSet_Multidimensional(cellSet, connection, content != Content.DataIncludeDefaultSlicer, responseMimeType == ResponseMimeType.JSON);
                    }

                    success = true;
                    var11 = dataSet;
                } catch (XmlaException var31) {
                    throw var31;
                } catch (Exception var32) {
                    throw new XmlaException("Server", "00HSBD02", "XMLA MDX execute failed", var32);
                }
            } finally {
                if (!success) {
                    if (cellSet != null) {
                        try {
                            cellSet.close();
                        } catch (SQLException var28) {
                        }
                    }

                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (SQLException var27) {
                        }
                    }

                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException var26) {
                        }
                    }
                }

            }

            return (XmlaHandler.QueryResult)var11;
        } else {
            return null;
        }
    }

    private static Format getFormat(XmlaRequest request, Format defaultValue) {
        String formatName = (String)request.getProperties().get(PropertyDefinition.Format.name());
        return (Format)Util.lookup(Format.class, formatName, defaultValue);
    }

    private static Content getContent(XmlaRequest request) {
        String contentName = (String)request.getProperties().get(PropertyDefinition.Content.name());
        return (Content)Util.lookup(Content.class, contentName, Content.DEFAULT);
    }

    private static ResponseMimeType getResponseMimeType(XmlaRequest request) {
        ResponseMimeType mimeType = (ResponseMimeType)ResponseMimeType.MAP.get(request.getProperties().get(PropertyDefinition.ResponseMimeType.name()));
        if (mimeType == null) {
            mimeType = ResponseMimeType.SOAP;
        }

        return mimeType;
    }

    private void executeDmvQuery(DmvXmlaRequest dmvXmlaRequest, XmlaResponse response, String rowsetName, Exp whereExpression, Map<String, String> parameters) throws XmlaException {
        RowsetDefinition rowsetDefinition = RowsetDefinition.valueOf(rowsetName);
        Rowset rowset = rowsetDefinition.getRowset(dmvXmlaRequest, this);
        SaxWriter writer = response.getWriter();
        writer.startDocument();
        writer.startElement("DiscoverResponse", new Object[]{"xmlns", "urn:schemas-microsoft-com:xml-analysis"});
        writer.startElement("return");
        writer.startElement("root", new Object[]{"xmlns", "urn:schemas-microsoft-com:xml-analysis:rowset", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema", "xmlns:EX", "urn:schemas-microsoft-com:xml-analysis:exception"});
        rowset.rowsetDefinition.writeRowsetXmlSchema(writer);

        try {
            List<Row> rows = new ArrayList();
            rowset.populate(response, (OlapConnection)null, rows);
            Comparator<Row> comparator = rowsetDefinition.getComparator();
            if (comparator != null) {
                Collections.sort(rows, comparator);
            }

            writer.startSequence((String)null, "row");
            Iterator var11 = rows.iterator();

            while(true) {
                if (!var11.hasNext()) {
                    writer.endSequence();
                    break;
                }

                Row row = (Row)var11.next();
                if (this.isCompatable(row, whereExpression, parameters)) {
                    rowset.emit(row, response);
                }
            }
        } catch (XmlaException var21) {
            throw var21;
        } catch (Throwable var22) {
            throw new XmlaException("Server", "00HSBE02", "XMLA Discover unparse results error", var22);
        } finally {
            try {
                writer.endElement();
                writer.endElement();
                writer.endElement();
            } catch (Throwable var20) {
            }

        }

        writer.endDocument();
    }

    private boolean isCompatable(Row row, Exp exp, Map<String, String> parameters) {
        if (exp == null) {
            return true;
        } else if (exp instanceof UnresolvedFunCall) {
            UnresolvedFunCall unresolvedFunCall = (UnresolvedFunCall)exp;
            String functionName = unresolvedFunCall.getFunName();
            byte var10 = -1;
            switch(functionName.hashCode()) {
                case 61:
                    if (functionName.equals("=")) {
                        var10 = 2;
                    }
                    break;
                case 1922:
                    if (functionName.equals("<>")) {
                        var10 = 3;
                    }
                    break;
                case 2531:
                    if (functionName.equals("OR")) {
                        var10 = 1;
                    }
                    break;
                case 64951:
                    if (functionName.equals("AND")) {
                        var10 = 0;
                    }
            }

            Object o1;
            Object o2;
            boolean result;
            switch(var10) {
                case 0:
                    return this.isCompatable(row, unresolvedFunCall.getArgs()[0], parameters) && this.isCompatable(row, unresolvedFunCall.getArgs()[1], parameters);
                case 1:
                    return this.isCompatable(row, unresolvedFunCall.getArgs()[0], parameters) || this.isCompatable(row, unresolvedFunCall.getArgs()[1], parameters);
                case 2:
                    o1 = this.getValue(row, unresolvedFunCall.getArgs()[0], parameters);
                    o2 = this.getValue(row, unresolvedFunCall.getArgs()[1], parameters);
                    result = o1 == null && o2 == null || o1 != null && o2 != null && o1.equals(o2);
                    return result;
                case 3:
                    o1 = this.getValue(row, unresolvedFunCall.getArgs()[0], parameters);
                    o2 = this.getValue(row, unresolvedFunCall.getArgs()[1], parameters);
                    result = (o1 != null || o2 != null) && (o1 == null || o2 == null || !o1.equals(o2));
                    return result;
                default:
                    return true;
            }
        } else if (!(exp instanceof Id)) {
            return true;
        } else {
            Object value = this.getValue(row, exp, parameters);
            return value != null && value.equals("true");
        }
    }

    private Object getValue(Row row, Exp exp, Map<String, String> parameters) {
        if (exp instanceof Id) {
            String columnName = ((NameSegment)((Id)exp).getElement(0)).getName();
            Object value;
            if (columnName.startsWith("@")) {
                columnName = columnName.substring(1);
                value = null;
                if (parameters.containsKey(columnName)) {
                    value = parameters.get(columnName);
                }

                return value;
            } else {
                value = row.get(columnName);
                if (value != null) {
                    value = value.toString();
                }

                return value;
            }
        } else if (exp instanceof Literal) {
            Object value = ((Literal)exp).getValue();
            if (value != null) {
                value = value.toString();
            }

            return value;
        } else {
            return null;
        }
    }

    private void discover(XmlaRequest request, XmlaResponse response) throws XmlaException {
        RowsetDefinition rowsetDefinition = RowsetDefinition.valueOf(request.getRequestType());
        Rowset rowset = rowsetDefinition.getRowset(request, this);
        Format format = getFormat(request, Format.Tabular);
        if (format != Format.Tabular) {
            throw new XmlaException("Client", "00HSBE01", "XMLA Discover format error", new UnsupportedOperationException("<Format>: only 'Tabular' allowed in Discover method type"));
        } else {
            Content content = getContent(request);
            SaxWriter writer = response.getWriter();
            writer.startDocument();
            writer.startElement("DiscoverResponse", new Object[]{"xmlns", "urn:schemas-microsoft-com:xml-analysis"});
            writer.startElement("return");
            writer.startElement("root", new Object[]{"xmlns", "urn:schemas-microsoft-com:xml-analysis:rowset", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema", "xmlns:EX", "urn:schemas-microsoft-com:xml-analysis:exception"});
            switch(content) {
                case Schema:
                case SchemaData:
                    rowset.rowsetDefinition.writeRowsetXmlSchema(writer);
            }

            try {
                switch(content) {
                    case SchemaData:
                    case Data:
                        rowset.unparse(response);
                }
            } catch (XmlaException var17) {
                throw var17;
            } catch (Throwable var18) {
                throw new XmlaException("Server", "00HSBE02", "XMLA Discover unparse results error", var18);
            } finally {
                try {
                    writer.endElement();
                    writer.endElement();
                    writer.endElement();
                } catch (Throwable var16) {
                }

            }

            writer.endDocument();
        }
    }

    protected OlapConnection getConnection(String catalog, String schema, String role) throws XmlaException {
        return this.getConnection(catalog, schema, role, new Properties());
    }

    protected OlapConnection getConnection(String catalog, String schema, String role, Properties props) throws XmlaException {
        try {
            return this.connectionFactory.getConnection(catalog, schema, role, props);
        } catch (SecurityException var6) {
            throw new XmlaException("Client", "00HSBC02", "XMLA connection with role must be authenticated", var6);
        } catch (SQLException var7) {
            throw new XmlaException("Client", "00HSBC01", "XMLA connection datasource not found", var7);
        }
    }

    private static String createCsv(Iterable<? extends Object> iterable) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for(Iterator var3 = iterable.iterator(); var3.hasNext(); first = false) {
            Object o = var3.next();
            if (!first) {
                sb.append(',');
            }

            sb.append(o);
        }

        return sb.toString();
    }

    static {
        EMPTY_ROW_SET_XML_SCHEMA = computeEmptyXsd(XmlaHandler.SetType.ROW_SET);
        MD_DATA_SET_XML_SCHEMA = computeXsd(XmlaHandler.SetType.MD_DATA_SET);
        EMPTY_MD_DATA_SET_XML_SCHEMA = computeEmptyXsd(XmlaHandler.SetType.MD_DATA_SET);
    }

    public interface ConnectionFactory {
        OlapConnection getConnection(String var1, String var2, String var3, Properties var4) throws SQLException;

        Map<String, Object> getPreConfiguredDiscoverDatasourcesResponse();
    }

    private static class XmlaExtraImpl implements XmlaHandler.XmlaExtra {
        private XmlaExtraImpl() {
        }

        public ResultSet executeDrillthrough(OlapStatement olapStatement, String mdx, boolean advanced, String tabFields, int[] rowCountSlot) throws SQLException {
            return olapStatement.executeQuery(mdx);
        }

        public void setPreferList(OlapConnection connection) {
        }

        public Date getSchemaLoadDate(org.olap4j.metadata.Schema schema) {
            return new Date();
        }

        public int getLevelCardinality(Level level) {
            return level.getCardinality();
        }

        public void getSchemaFunctionList(List<XmlaHandler.XmlaExtra.FunctionDefinition> funDefs, org.olap4j.metadata.Schema schema, Functor1<Boolean, String> functionFilter) {
        }

        public int getHierarchyCardinality(Hierarchy hierarchy) {
            int cardinality = 0;

            Level level;
            for(Iterator var3 = hierarchy.getLevels().iterator(); var3.hasNext(); cardinality += level.getCardinality()) {
                level = (Level)var3.next();
            }

            return cardinality;
        }

        public int getHierarchyStructure(Hierarchy hierarchy) {
            return 0;
        }

        public boolean isHierarchyParentChild(Hierarchy hierarchy) {
            return false;
        }

        public int getMeasureAggregator(Member member) {
            return 0;
        }

        public String getMeasureDisplayFolder(Member member) {
            return "";
        }

        public void checkMemberOrdinal(Member member) {
        }

        public boolean shouldReturnCellProperty(CellSet cellSet, Property cellProperty, boolean evenEmpty) {
            return true;
        }

        public List<String> getSchemaRoleNames(org.olap4j.metadata.Schema schema) {
            return Collections.emptyList();
        }

        public String getSchemaId(org.olap4j.metadata.Schema schema) {
            return schema.getName();
        }

        public String getCubeType(Cube cube) {
            return "CUBE";
        }

        public boolean isLevelUnique(Level level) {
            return false;
        }

        public List<Property> getLevelProperties(Level level) {
            return level.getProperties();
        }

        public boolean isPropertyInternal(Property property) {
            return property instanceof StandardMemberProperty && ((StandardMemberProperty)property).isInternal() || property instanceof StandardCellProperty && ((StandardCellProperty)property).isInternal();
        }

        public List<Map<String, Object>> getDataSources(OlapConnection connection) throws OlapException {
            Database olapDb = connection.getOlapDatabase();
            String modes = XmlaHandler.createCsv(olapDb.getAuthenticationModes());
            String providerTypes = XmlaHandler.createCsv(olapDb.getProviderTypes());
            return Collections.singletonList(Olap4jUtil.mapOf("DataSourceName", olapDb.getName(), new Object[]{"DataSourceDescription", olapDb.getDescription(), "URL", olapDb.getURL(), "DataSourceInfo", olapDb.getDataSourceInfo(), "ProviderName", olapDb.getProviderName(), "ProviderType", providerTypes, "AuthenticationMode", modes}));
        }

        public Map<String, Object> getAnnotationMap(MetadataElement element) {
            return Collections.emptyMap();
        }

        public boolean canDrillThrough(Cell cell) {
            return false;
        }

        public int getDrillThroughCount(Cell cell) {
            return -1;
        }

        public void flushSchemaCache(OlapConnection conn) {
        }

        public Object getMemberKey(Member m) throws OlapException {
            return m.getPropertyValue(StandardMemberProperty.MEMBER_KEY);
        }

        public Object getOrderKey(Member m) throws OlapException {
            return m.getOrdinal();
        }

        // $FF: synthetic method
        XmlaExtraImpl(Object x0) {
            this();
        }
    }

    public interface XmlaExtra {
        ResultSet executeDrillthrough(OlapStatement var1, String var2, boolean var3, String var4, int[] var5) throws SQLException;

        void setPreferList(OlapConnection var1);

        Date getSchemaLoadDate(org.olap4j.metadata.Schema var1);

        int getLevelCardinality(Level var1) throws OlapException;

        void getSchemaFunctionList(List<XmlaHandler.XmlaExtra.FunctionDefinition> var1, org.olap4j.metadata.Schema var2, Functor1<Boolean, String> var3);

        int getHierarchyCardinality(Hierarchy var1) throws OlapException;

        int getHierarchyStructure(Hierarchy var1);

        boolean isHierarchyParentChild(Hierarchy var1);

        int getMeasureAggregator(Member var1);

        String getMeasureDisplayFolder(Member var1);

        void checkMemberOrdinal(Member var1) throws OlapException;

        boolean shouldReturnCellProperty(CellSet var1, Property var2, boolean var3);

        List<String> getSchemaRoleNames(org.olap4j.metadata.Schema var1);

        String getSchemaId(org.olap4j.metadata.Schema var1);

        String getCubeType(Cube var1);

        boolean isLevelUnique(Level var1);

        List<Property> getLevelProperties(Level var1);

        boolean isPropertyInternal(Property var1);

        List<Map<String, Object>> getDataSources(OlapConnection var1) throws OlapException;

        Map<String, Object> getAnnotationMap(MetadataElement var1) throws SQLException;

        boolean canDrillThrough(Cell var1);

        int getDrillThroughCount(Cell var1);

        void flushSchemaCache(OlapConnection var1) throws OlapException;

        Object getMemberKey(Member var1) throws OlapException;

        Object getOrderKey(Member var1) throws OlapException;

        default String getLevelDataType(Level level) {
            return null;
        }

        public static class FunctionDefinition {
            public final String functionName;
            public final String description;
            public final String parameterList;
            public final int returnType;
            public final int origin;
            public final String interfaceName;
            public final String caption;

            public FunctionDefinition(String functionName, String description, String parameterList, int returnType, int origin, String interfaceName, String caption) {
                this.functionName = functionName;
                this.description = description;
                this.parameterList = parameterList;
                this.returnType = returnType;
                this.origin = origin;
                this.interfaceName = interfaceName;
                this.caption = caption;
            }
        }
    }

    private static class IntList extends AbstractList<Integer> {
        private final int[] ints;

        IntList(int[] ints) {
            this.ints = ints;
        }

        public Integer get(int index) {
            return this.ints[index];
        }

        public int size() {
            return this.ints.length;
        }
    }

    static class MDDataSet_Tabular extends XmlaHandler.MDDataSet {
        private final boolean empty;
        private final int[] pos;
        private final List<Integer> posList;
        private final int axisCount;
        private int cellOrdinal;
        private static final List<Property> MemberCaptionIdArray;
        private final Member[] members;
        private final XmlaHandler.ColumnHandler[] columnHandlers;

        public MDDataSet_Tabular(CellSet cellSet) {
            super(cellSet);
            List<CellSetAxis> axes = cellSet.getAxes();
            this.axisCount = axes.size();
            this.pos = new int[this.axisCount];
            this.posList = new XmlaHandler.IntList(this.pos);
            boolean empty = false;
            int dimensionCount = 0;

            for(int i = axes.size() - 1; i > 0; --i) {
                CellSetAxis axis = (CellSetAxis)axes.get(i);
                if (axis.getPositions().size() == 0) {
                    empty = true;
                } else {
                    dimensionCount += ((Position)axis.getPositions().get(0)).getMembers().size();
                }
            }

            this.empty = empty;
            Level[] levels = new Level[dimensionCount];
            List<XmlaHandler.ColumnHandler> columnHandlerList = new ArrayList();
            int memberOrdinal = 0;
            int jj;
            Iterator var13;
            if (!empty) {
                for(int i = axes.size() - 1; i > 0; --i) {
                    CellSetAxis axis = (CellSetAxis)axes.get(i);
                    int z0 = memberOrdinal;
                    List<Position> positions = axis.getPositions();
                    jj = 0;

                    for(var13 = positions.iterator(); var13.hasNext(); ++jj) {
                        Position position = (Position)var13.next();
                        memberOrdinal = z0;

                        for(Iterator var15 = position.getMembers().iterator(); var15.hasNext(); ++memberOrdinal) {
                            Member member = (Member)var15.next();
                            if (jj == 0 || member.getLevel().getDepth() > levels[memberOrdinal].getDepth()) {
                                levels[memberOrdinal] = member.getLevel();
                            }
                        }
                    }

                    List<Property> dimProps = axis.getAxisMetaData().getProperties();
                    if (dimProps.size() == 0) {
                        dimProps = MemberCaptionIdArray;
                    }

                    for(int j = z0; j < memberOrdinal; ++j) {
                        Level level = levels[j];

                        for(int k = 0; k <= level.getDepth(); ++k) {
                            Level level2 = (Level)level.getHierarchy().getLevels().get(k);
                            if (level2.getLevelType() != Type.ALL) {
                                Iterator var18 = dimProps.iterator();

                                while(var18.hasNext()) {
                                    Property dimProp = (Property)var18.next();
                                    columnHandlerList.add(new XmlaHandler.MemberColumnHandler(dimProp, level2, j));
                                }
                            }
                        }
                    }
                }
            }

            this.members = new Member[memberOrdinal + 1];
            if (axes.size() > 0) {
                CellSetAxis columnsAxis = (CellSetAxis)axes.get(0);
                Iterator var23 = columnsAxis.getPositions().iterator();

                while(var23.hasNext()) {
                    Position position = (Position)var23.next();
                    String name = null;
                    jj = 0;

                    for(var13 = position.getMembers().iterator(); var13.hasNext(); ++jj) {
                        Member member = (Member)var13.next();
                        if (jj == 0) {
                            name = member.getUniqueName();
                        } else {
                            name = name + "." + member.getUniqueName();
                        }
                    }

                    columnHandlerList.add(new XmlaHandler.CellColumnHandler(name));
                }
            }

            this.columnHandlers = (XmlaHandler.ColumnHandler[])columnHandlerList.toArray(new XmlaHandler.ColumnHandler[columnHandlerList.size()]);
        }

        public void metadata(SaxWriter writer) {
            writer.startElement("xsd:schema", new Object[]{"xmlns:xsd", "http://www.w3.org/2001/XMLSchema", "targetNamespace", "urn:schemas-microsoft-com:xml-analysis:rowset", "xmlns", "urn:schemas-microsoft-com:xml-analysis:rowset", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance", "xmlns:sql", "urn:schemas-microsoft-com:xml-sql", "elementFormDefault", "qualified"});
            writer.startElement("xsd:element", new Object[]{"name", "root"});
            writer.startElement("xsd:complexType");
            writer.startElement("xsd:sequence");
            writer.element("xsd:element", new Object[]{"maxOccurs", "unbounded", "minOccurs", 0, "name", "row", "type", "row"});
            writer.endElement();
            writer.endElement();
            writer.endElement();
            writer.startElement("xsd:simpleType", new Object[]{"name", "uuid"});
            writer.startElement("xsd:restriction", new Object[]{"base", "xsd:string"});
            writer.element("xsd:pattern", new Object[]{"value", "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"});
            writer.endElement();
            writer.endElement();
            writer.startElement("xsd:complexType", new Object[]{"name", "row"});
            writer.startElement("xsd:sequence");
            XmlaHandler.ColumnHandler[] var2 = this.columnHandlers;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                XmlaHandler.ColumnHandler columnHandler = var2[var4];
                columnHandler.metadata(writer);
            }

            writer.endElement();
            writer.endElement();
            writer.endElement();
        }

        public void unparse(SaxWriter writer) throws SAXException, OlapException {
            if (!this.empty) {
                this.cellData(writer);
            }
        }

        private void cellData(SaxWriter writer) throws SAXException, OlapException {
            this.cellOrdinal = 0;
            this.iterate(writer);
        }

        private void iterate(SaxWriter writer) throws SAXException, OlapException {
            switch(this.axisCount) {
                case 0:
                    this.emitCell(writer, this.cellSet.getCell(this.posList));
                    return;
                default:
                    this.iterate(writer, this.axisCount - 1, 0);
            }
        }

        private void iterate(SaxWriter writer, int axis, int xxx) throws OlapException {
            List<Position> positions = ((CellSetAxis)this.cellSet.getAxes().get(axis)).getPositions();
            int axisLength = axis == 0 ? 1 : positions.size();

            for(int i = 0; i < axisLength; ++i) {
                Position position = (Position)positions.get(i);
                int ho = xxx;
                List<Member> members = position.getMembers();

                for(int j = 0; j < members.size() && ho < this.members.length; ++ho) {
                    this.members[ho] = (Member)position.getMembers().get(j);
                    ++j;
                }

                ++this.cellOrdinal;
                Util.discard(this.cellOrdinal);
                if (axis >= 2) {
                    this.iterate(writer, axis - 1, ho);
                } else {
                    writer.startElement("row");
                    this.pos[axis] = i;
                    this.pos[0] = 0;
                    XmlaHandler.ColumnHandler[] var14 = this.columnHandlers;
                    int var11 = var14.length;

                    for(int var12 = 0; var12 < var11; ++var12) {
                        XmlaHandler.ColumnHandler columnHandler = var14[var12];
                        if (columnHandler instanceof XmlaHandler.MemberColumnHandler) {
                            columnHandler.write(writer, (Cell)null, this.members);
                        } else if (columnHandler instanceof XmlaHandler.CellColumnHandler) {
                            columnHandler.write(writer, this.cellSet.getCell(this.posList), (Member[])null);
                            int var10002 = this.pos[0]++;
                        }
                    }

                    writer.endElement();
                }
            }

        }

        private void emitCell(SaxWriter writer, Cell cell) throws OlapException {
            ++this.cellOrdinal;
            Util.discard(this.cellOrdinal);
            Object cellValue = cell.getValue();
            if (cellValue != null) {
                writer.startElement("row");
                XmlaHandler.ColumnHandler[] var4 = this.columnHandlers;
                int var5 = var4.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    XmlaHandler.ColumnHandler columnHandler = var4[var6];
                    columnHandler.write(writer, cell, this.members);
                }

                writer.endElement();
            }
        }

        static {
            MemberCaptionIdArray = Collections.singletonList(StandardMemberProperty.MEMBER_CAPTION);
        }
    }

    static class MemberColumnHandler extends XmlaHandler.ColumnHandler {
        private final Property property;
        private final Level level;
        private final int memberOrdinal;

        public MemberColumnHandler(Property property, Level level, int memberOrdinal) {
            super(level.getUniqueName() + "." + Util.quoteMdxIdentifier(property.getName()));
            this.property = property;
            this.level = level;
            this.memberOrdinal = memberOrdinal;
        }

        public void metadata(SaxWriter writer) {
            writer.element("xsd:element", new Object[]{"minOccurs", 0, "name", this.encodedName, "sql:field", this.name, "type", "xsd:string"});
        }

        public void write(SaxWriter writer, Cell cell, Member[] members) throws OlapException {
            Member member = members[this.memberOrdinal];
            int depth = this.level.getDepth();
            if (member.getDepth() >= depth) {
                while(member.getDepth() > depth) {
                    member = member.getParentMember();
                }

                Object propertyValue = member.getPropertyValue(this.property);
                if (propertyValue != null) {
                    writer.startElement(this.encodedName);
                    writer.characters(propertyValue.toString());
                    writer.endElement();
                }
            }
        }
    }

    static class CellColumnHandler extends XmlaHandler.ColumnHandler {
        CellColumnHandler(String name) {
            super(name);
        }

        public void metadata(SaxWriter writer) {
            writer.element("xsd:element", new Object[]{"minOccurs", 0, "name", this.encodedName, "sql:field", this.name});
        }

        public void write(SaxWriter writer, Cell cell, Member[] members) {
            if (!cell.isNull()) {
                Object value = cell.getValue();
                String dataType = (String)cell.getPropertyValue(StandardCellProperty.DATATYPE);
                XmlaHandler.ValueInfo vi = new XmlaHandler.ValueInfo(dataType, value);
                String valueType = vi.valueType;
                value = vi.value;
                boolean isDecimal = vi.isDecimal;
                String valueString = value.toString();
                writer.startElement(this.encodedName, new Object[]{"xsi:type", valueType});
                if (isDecimal) {
                    valueString = XmlaUtil.normalizeNumericString(valueString);
                }

                writer.characters(valueString);
                writer.endElement();
            }
        }
    }

    abstract static class ColumnHandler {
        protected final String name;
        protected final String encodedName;

        protected ColumnHandler(String name) {
            this.name = name;
            this.encodedName = ElementNameEncoder.INSTANCE.encode(name);
        }

        abstract void write(SaxWriter var1, Cell var2, Member[] var3) throws OlapException;

        abstract void metadata(SaxWriter var1);
    }

    static class MDDataSet_Multidimensional extends XmlaHandler.MDDataSet {
        private List<Hierarchy> slicerAxisHierarchies;
        private final boolean omitDefaultSlicerInfo;
        private final boolean json;
        private ElementNameEncoder encoder;
        private XmlaHandler.XmlaExtra extra;
        private OlapConnection connection;
        private MondrianOlap4jConnection mondrianOlap4jConnection;
        private List<String> queryCellPropertyNames;
        private static boolean DEFAULT_BOOLEAN;
        private static byte DEFAULT_BYTE;
        private static short DEFAULT_SHORT;
        private static int DEFAULT_INT;
        private static long DEFAULT_LONG;
        private static float DEFAULT_FLOAT;
        private static double DEFAULT_DOUBLE;

        protected MDDataSet_Multidimensional(CellSet cellSet, OlapConnection connection, boolean omitDefaultSlicerInfo, boolean json) throws SQLException {
            super(cellSet);
            this.encoder = ElementNameEncoder.INSTANCE;
            this.queryCellPropertyNames = new ArrayList();
            this.omitDefaultSlicerInfo = omitDefaultSlicerInfo;
            this.json = json;
            this.mondrianOlap4jConnection = (MondrianOlap4jConnection)cellSet.getStatement().getConnection();
            this.connection = connection;
            this.extra = XmlaHandler.getExtra(cellSet.getStatement().getConnection());
            boolean matchCase = MondrianProperties.instance().CaseSensitive.get();
            Statement statement = (Statement)cellSet.getStatement();
            QueryPart[] var7 = statement.getQuery().getCellProperties();
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                QueryPart queryPart = var7[var9];
                mondrian.olap.CellProperty cellProperty = (mondrian.olap.CellProperty)queryPart;
                mondrian.olap.Property property = mondrian.olap.Property.lookup(cellProperty.toString(), matchCase);
                String propertyName = ((NameSegment)Util.parseIdentifier(cellProperty.toString()).get(0)).name;
                this.queryCellPropertyNames.add(propertyName);
            }

            if (this.queryCellPropertyNames.size() == 0) {
                this.queryCellPropertyNames.add("VALUE");
                this.queryCellPropertyNames.add("FORMATTED_VALUE");
            }

        }

        public void unparse(SaxWriter writer) throws SAXException, OlapException, SQLException {
            this.olapInfo(writer);
            this.axes(writer);
            this.cellData(writer);
        }

        public void metadata(SaxWriter writer) {
            writer.verbatim(XmlaHandler.MD_DATA_SET_XML_SCHEMA);
        }

        private void olapInfo(SaxWriter writer) throws OlapException {
            Cube cube = this.cellSet.getMetaData().getCube();
            writer.startElement("OlapInfo");
            writer.startElement("CubeInfo");
            writer.startElement("Cube");
            writer.textElement("CubeName", cube.getName());
            java.text.Format formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String formattedDate = formatter.format(this.extra.getSchemaLoadDate(this.mondrianOlap4jConnection.getMondrianOlap4jSchema()));
            writer.startElement("LastDataUpdate", new Object[]{"xmlns", "http://schemas.microsoft.com/analysisservices/2003/engine"});
            writer.characters(formattedDate);
            writer.endElement();
            writer.startElement("LastSchemaUpdate", new Object[]{"xmlns", "http://schemas.microsoft.com/analysisservices/2003/engine"});
            writer.characters(formattedDate);
            writer.endElement();
            writer.endElement();
            writer.endElement();
            writer.startSequence("AxesInfo", "AxisInfo");
            List<CellSetAxis> axes = this.cellSet.getAxes();
            List<Hierarchy> axisHierarchyList = new ArrayList();

            for(int i = 0; i < axes.size(); ++i) {
                List<Hierarchy> hiers = this.axisInfo(writer, (CellSetAxis)axes.get(i), "Axis" + i);
                axisHierarchyList.addAll(hiers);
            }

            CellSetAxis slicerAxis = this.cellSet.getFilterAxis();
            Object hierarchies;
            if (this.omitDefaultSlicerInfo) {
                hierarchies = this.axisInfo(writer, slicerAxis, "SlicerAxis");
            } else {
                List<Dimension> unseenDimensionList = new ArrayList(cube.getDimensions());
                Iterator var10 = axisHierarchyList.iterator();

                while(var10.hasNext()) {
                    Hierarchy hier1 = (Hierarchy)var10.next();
                    unseenDimensionList.remove(hier1.getDimension());
                }

                hierarchies = new ArrayList();
                var10 = unseenDimensionList.iterator();

                while(var10.hasNext()) {
                    Dimension dimension = (Dimension)var10.next();
                    Iterator var12 = dimension.getHierarchies().iterator();

                    while(var12.hasNext()) {
                        Hierarchy hierarchy = (Hierarchy)var12.next();
                        ((List)hierarchies).add(hierarchy);
                    }
                }

                writer.startElement("AxisInfo", new Object[]{"name", "SlicerAxis"});
                this.writeHierarchyInfo(writer, (List)hierarchies, this.getProps(slicerAxis.getAxisMetaData()));
                writer.endElement();
            }

            this.slicerAxisHierarchies = (List)hierarchies;
            writer.endSequence();
            writer.startElement("CellInfo");

            XmlaHandler.MDDataSet.CellProperty cellProperty;
            ArrayList values;
            for(Iterator var16 = this.queryCellPropertyNames.iterator(); var16.hasNext(); writer.element(cellProperty.getAlias(), values.toArray())) {
                String cellPropertyName = (String)var16.next();
                if (cellPropertyName != null) {
                    cellPropertyName = cellPropertyName.toUpperCase();
                }

                cellProperty = (XmlaHandler.MDDataSet.CellProperty)cellPropertyMap.get(cellPropertyName);
                values = new ArrayList();
                values.add("name");
                values.add(cellPropertyName);
                if (cellProperty != null && cellProperty.getXsdType() != null) {
                    values.add("type");
                    values.add(cellProperty.getXsdType());
                }
            }

            writer.endElement();
            writer.endElement();
        }

        private List<Hierarchy> axisInfo(SaxWriter writer, CellSetAxis axis, String axisName) {
            writer.startElement("AxisInfo", new Object[]{"name", axisName});
            List<Property> props = new ArrayList(this.getProps(axis.getAxisMetaData()));
            Iterator<Position> it = axis.getPositions().iterator();
            Object hierarchies;
            if (it.hasNext()) {
                Position position = (Position)it.next();
                hierarchies = new ArrayList();
                Iterator var8 = position.getMembers().iterator();

                while(var8.hasNext()) {
                    Member member = (Member)var8.next();
                    ((List)hierarchies).add(member.getHierarchy());
                }
            } else {
                hierarchies = axis.getAxisMetaData().getHierarchies();
            }

            props.removeIf((prop) -> {
                return !this.isValidProp(axis.getPositions(), prop);
            });
            this.writeHierarchyInfo(writer, (List)hierarchies, props);
            writer.endElement();
            return (List)hierarchies;
        }

        private boolean isValidProp(List<Position> positions, Property prop) {
            if (!(prop instanceof IMondrianOlap4jProperty)) {
                return true;
            } else {
                Iterator var3 = positions.iterator();

                Position pos;
                do {
                    if (!var3.hasNext()) {
                        return false;
                    }

                    pos = (Position)var3.next();
                } while(!pos.getMembers().stream().anyMatch((member) -> {
                    return Objects.nonNull(this.getHierarchyProperty(member, prop));
                }));

                return true;
            }
        }

        private void writeHierarchyInfo(SaxWriter writer, List<Hierarchy> hierarchies, List<Property> props) {
            writer.startSequence((String)null, "HierarchyInfo");
            Iterator var4 = hierarchies.iterator();

            while(var4.hasNext()) {
                Hierarchy hierarchy = (Hierarchy)var4.next();
                writer.startElement("HierarchyInfo", new Object[]{"name", hierarchy.getUniqueName()});
                Iterator var6 = props.iterator();

                while(var6.hasNext()) {
                    Property prop = (Property)var6.next();
                    if (prop instanceof IMondrianOlap4jProperty) {
                        this.writeProperty(writer, hierarchy, prop);
                    } else {
                        this.writeElement(writer, hierarchy, prop);
                    }
                }

                writer.endElement();
            }

            writer.endSequence();
        }

        private void writeProperty(SaxWriter writer, Hierarchy hierarchy, Property prop) {
            IMondrianOlap4jProperty currentProperty = (IMondrianOlap4jProperty)prop;
            String thisHierarchyName = hierarchy.getName();
            String thatHierarchiName = currentProperty.getLevel().getHierarchy().getName();
            if (thisHierarchyName.equals(thatHierarchiName)) {
                this.writeElement(writer, hierarchy, prop);
            }

        }

        private void writeElement(SaxWriter writer, Hierarchy hierarchy, Property prop) {
            String encodedProp = this.encoder.encode(prop.getName());
            Object[] attributes = this.getAttributes(prop, hierarchy);
            writer.element(encodedProp, attributes);
        }

        private Object[] getAttributes(Property prop, Hierarchy hierarchy) {
            Property longProp = (Property)longProps.get(prop.getName());
            if (longProp == null) {
                longProp = prop;
            }

            List<Object> values = new ArrayList();
            values.add("name");
            values.add(hierarchy.getUniqueName() + "." + Util.quoteMdxIdentifier(longProp.getName()));
            if (!(longProp instanceof IMondrianOlap4jProperty)) {
                values.add("type");
                values.add(this.getXsdType(longProp));
            }

            return values.toArray();
        }

        private String getXsdType(Property property) {
            Datatype datatype = property.getDatatype();
            switch(datatype) {
                case UNSIGNED_INTEGER:
                    return mondrian.xmla.RowsetDefinition.Type.UnsignedInteger.columnType;
                case DOUBLE:
                    return mondrian.xmla.RowsetDefinition.Type.Double.columnType;
                case LARGE_INTEGER:
                    return mondrian.xmla.RowsetDefinition.Type.Long.columnType;
                case INTEGER:
                    return mondrian.xmla.RowsetDefinition.Type.Integer.columnType;
                case BOOLEAN:
                    return mondrian.xmla.RowsetDefinition.Type.Boolean.columnType;
                default:
                    return mondrian.xmla.RowsetDefinition.Type.String.columnType;
            }
        }

        private Object getDefaultValue(Property property) {
            Datatype datatype = property.getDatatype();
            switch(datatype) {
                case UNSIGNED_INTEGER:
                    return DEFAULT_INT;
                case DOUBLE:
                    return DEFAULT_DOUBLE;
                case LARGE_INTEGER:
                    return DEFAULT_LONG;
                case INTEGER:
                    return DEFAULT_INT;
                case BOOLEAN:
                    return DEFAULT_BOOLEAN;
                default:
                    return null;
            }
        }

        private void axes(SaxWriter writer) throws OlapException, SQLException {
            writer.startSequence("Axes", "Axis");
            List<CellSetAxis> axes = this.cellSet.getAxes();

            for(int i = 0; i < axes.size(); ++i) {
                CellSetAxis axis = (CellSetAxis)axes.get(i);
                List<Property> props = this.getProps(axis.getAxisMetaData());
                this.axis(writer, axis, props, "Axis" + i);
            }

            CellSetAxis slicerAxis = this.cellSet.getFilterAxis();
            if (this.omitDefaultSlicerInfo) {
                if (this.slicerAxisHierarchies.size() > 0) {
                    this.axis(writer, slicerAxis, this.getProps(slicerAxis.getAxisMetaData()), "SlicerAxis");
                }
            } else {
                List<Hierarchy> hierarchies = this.slicerAxisHierarchies;
                writer.startElement("Axis", new Object[]{"name", "SlicerAxis"});
                writer.startSequence("Tuples", "Tuple");
                writer.startSequence("Tuple", "Member");
                Map<String, Integer> memberMap = new HashMap();
                List<Position> slicerPositions = slicerAxis.getPositions();
                Member member;
                if (slicerPositions != null && slicerPositions.size() > 0) {
                    Position pos0 = (Position)slicerPositions.get(0);
                    int i = 0;
                    Iterator var10 = pos0.getMembers().iterator();

                    while(var10.hasNext()) {
                        member = (Member)var10.next();
                        memberMap.put(member.getHierarchy().getName(), i++);
                    }
                }

                List<Member> slicerMembers = slicerPositions.isEmpty() ? Collections.emptyList() : ((Position)slicerPositions.get(0)).getMembers();
                Iterator var19 = hierarchies.iterator();

                while(var19.hasNext()) {
                    Hierarchy hierarchy = (Hierarchy)var19.next();
                    member = hierarchy.getDefaultMember();
                    Integer indexPosition = (Integer)memberMap.get(hierarchy.getName());
                    Member positionMember;
                    if (indexPosition != null) {
                        positionMember = (Member)slicerMembers.get(indexPosition);
                    } else {
                        positionMember = null;
                    }

                    Iterator var13 = slicerMembers.iterator();

                    while(var13.hasNext()) {
                        Member slicerMember = (Member)var13.next();
                        if (slicerMember.getHierarchy().equals(hierarchy)) {
                            member = slicerMember;
                            break;
                        }
                    }

                    if (member != null) {
                        if (positionMember != null) {
                            this.writeMember(writer, positionMember, (Position)null, (Position)slicerPositions.get(0), indexPosition, this.getProps(slicerAxis.getAxisMetaData()));
                        } else {
                            this.slicerAxis(writer, member, this.getProps(slicerAxis.getAxisMetaData()));
                        }
                    } else {
                        XmlaHandler.LOGGER.warn("Can not create SlicerAxis: null default member for Hierarchy " + hierarchy.getUniqueName());
                    }
                }

                writer.endSequence();
                writer.endSequence();
                writer.endElement();
            }

            writer.endSequence();
        }

        private List<Property> getProps(CellSetAxisMetaData queryAxis) {
            return (List)(queryAxis == null ? defaultProps : CompositeList.of(new List[]{defaultProps, queryAxis.getProperties()}));
        }

        private void axis(SaxWriter writer, CellSetAxis axis, List<Property> props, String axisName) throws OlapException, SQLException {
            writer.startElement("Axis", new Object[]{"name", axisName});
            writer.startSequence("Tuples", "Tuple");
            HashMap<Level, HashSet<mondrian.olap.Member>> levelMembers = new HashMap();
            Iterator var6 = axis.getPositions().iterator();

            Iterator var8;
            Level level;
            while(var6.hasNext()) {
                Position p = (Position)var6.next();

                Member member;
                for(var8 = p.getMembers().iterator(); var8.hasNext(); ((HashSet)levelMembers.get(level)).add(((MondrianOlap4jMember)member).getOlapMember())) {
                    member = (Member)var8.next();
                    level = member.getLevel();
                    if (!levelMembers.containsKey(level)) {
                        levelMembers.put(level, new HashSet());
                    }
                }
            }

            RolapConnection rolapConnection = ((MondrianOlap4jConnection)this.connection).getMondrianConnection();
            final Statement statement = (Statement)this.cellSet.getStatement();
            var8 = levelMembers.entrySet().iterator();

            while(var8.hasNext()) {
                Entry<Level, HashSet<mondrian.olap.Member>> entry = (Entry)var8.next();
                level = (Level)entry.getKey();
                final ArrayList<mondrian.olap.Member> members = new ArrayList((Collection)entry.getValue());
                if (members.size() > 0 && ((mondrian.olap.Member)members.get(0)).getLevel().getChildLevel() != null && !((mondrian.olap.Member)members.get(0)).getLevel().isAll()) {
                    Locus.execute(rolapConnection, "MondrianOlap4jMember.getChildMembers", new Action<List<mondrian.olap.Member>>() {
                        public List<mondrian.olap.Member> execute() {
                            return statement.getQuery().getSchemaReader(true).getMemberChildren(members);
                        }
                    });
                }
            }

            List<Position> positions = axis.getPositions();
            Iterator<Position> pit = positions.iterator();
            Position prevPosition = null;
            Position position = pit.hasNext() ? (Position)pit.next() : null;

            for(Position nextPosition = pit.hasNext() ? (Position)pit.next() : null; position != null; nextPosition = pit.hasNext() ? (Position)pit.next() : null) {
                writer.startSequence("Tuple", "Member");
                int k = 0;
                Iterator var14 = position.getMembers().iterator();

                while(var14.hasNext()) {
                    Member member = (Member)var14.next();
                    this.writeMember(writer, member, prevPosition, nextPosition, k++, props);
                }

                writer.endSequence();
                prevPosition = position;
                position = nextPosition;
            }

            writer.endSequence();
            writer.endElement();
        }

        private void writeMember(SaxWriter writer, Member member, Position prevPosition, Position nextPosition, int k, List<Property> props) throws OlapException {
            writer.startElement("Member", new Object[]{"Hierarchy", member.getHierarchy().getUniqueName()});
            Iterator var7 = props.iterator();

            while(true) {
                Property prop;
                Object value;
                Property longProp;
                while(true) {
                    if (!var7.hasNext()) {
                        writer.endElement();
                        return;
                    }

                    prop = (Property)var7.next();
                    value = null;
                    longProp = longProps.get(prop.getName()) != null ? (Property)longProps.get(prop.getName()) : prop;
                    if (longProp == StandardMemberProperty.DISPLAY_INFO) {
                        Integer childrenCard = (Integer)member.getPropertyValue(StandardMemberProperty.CHILDREN_CARDINALITY);
                        value = this.calculateDisplayInfo(prevPosition, nextPosition, member, k, childrenCard);
                        break;
                    }

                    if (longProp == StandardMemberProperty.DEPTH) {
                        value = member.getDepth();
                        break;
                    }

                    if (longProp instanceof IMondrianOlap4jProperty) {
                        IMondrianOlap4jProperty currentProperty = (IMondrianOlap4jProperty)longProp;
                        String thisHierarchyName = member.getHierarchy().getName();
                        String thatHierarchyName = currentProperty.getLevel().getHierarchy().getName();
                        if (!thisHierarchyName.equals(thatHierarchyName)) {
                            continue;
                        }

                        value = this.getHierarchyProperty(member, longProp);
                        break;
                    }

                    value = member.getPropertyValue(longProp);
                    break;
                }

                if (longProp != prop && value == null) {
                    value = this.getDefaultValue(prop);
                }

                if (value != null) {
                    if (longProp instanceof IMondrianOlap4jProperty) {
                        writer.startElement(this.encoder.encode(prop.getName()), new Object[]{"xsi:type", this.getXsdType(prop)});
                        writer.characters(value);
                        writer.endElement();
                    } else {
                        writer.textElement(this.encoder.encode(prop.getName()), value);
                    }
                }
            }
        }

        private Object getHierarchyProperty(Member member, Property longProp) {
            IMondrianOlap4jProperty currentProperty = (IMondrianOlap4jProperty)longProp;
            String thisHierarchyName = member.getHierarchy().getName();
            String thatHierarchyName = currentProperty.getLevel().getHierarchy().getName();
            if (thisHierarchyName.equals(thatHierarchyName)) {
                try {
                    return member.getPropertyValue(currentProperty);
                } catch (OlapException var7) {
                    throw new XmlaException("Server", "00HSBB09", "XMLA SOAP bad Discover or Execute PropertyList element", var7);
                }
            } else {
                return null;
            }
        }

        private void slicerAxis(SaxWriter writer, Member member, List<Property> props) throws OlapException {
            writer.startElement("Member", new Object[]{"Hierarchy", member.getHierarchy().getUniqueName()});
            Iterator var4 = props.iterator();

            while(var4.hasNext()) {
                Property prop = (Property)var4.next();
                Property longProp = (Property)longProps.get(prop.getName());
                if (longProp == null) {
                    longProp = prop;
                }

                Object value;
                if (longProp == StandardMemberProperty.DISPLAY_INFO) {
                    Integer childrenCard = (Integer)member.getPropertyValue(StandardMemberProperty.CHILDREN_CARDINALITY);
                    int displayInfo = '\uffff' & childrenCard;
                    value = displayInfo;
                } else if (longProp == StandardMemberProperty.DEPTH) {
                    value = member.getDepth();
                } else {
                    value = member.getPropertyValue(longProp);
                }

                if (value == null) {
                    value = this.getDefaultValue(prop);
                }

                if (value != null) {
                    writer.textElement(this.encoder.encode(prop.getName()), value);
                }
            }

            writer.endElement();
        }

        private int calculateDisplayInfo(Position prevPosition, Position nextPosition, Member currentMember, int memberOrdinal, int childrenCount) {
            int displayInfo = '\uffff' & childrenCount;
            if (nextPosition != null) {
                Member nextMember = (Member)nextPosition.getMembers().get(memberOrdinal);
                this.parentUniqueName(nextMember);
                if (currentMember.equals(nextMember.getParentMember())) {
                    displayInfo |= 65536;
                }
            }

            if (prevPosition != null) {
                String currentParentUName = this.parentUniqueName(currentMember);
                Member prevMember = (Member)prevPosition.getMembers().get(memberOrdinal);
                String prevParentUName = this.parentUniqueName(prevMember);
                if (currentParentUName != null && currentParentUName.equals(prevParentUName)) {
                    displayInfo |= 131072;
                }
            }

            return displayInfo;
        }

        private String parentUniqueName(Member member) {
            Member parent = member.getParentMember();
            return parent == null ? null : parent.getUniqueName();
        }

        private void cellData(SaxWriter writer) {
            writer.startSequence("CellData", "Cell");
            int axisCount = this.cellSet.getAxes().size();
            List<Integer> pos = new ArrayList();

            for(int i = 0; i < axisCount; ++i) {
                pos.add(-1);
            }

            int[] cellOrdinal = new int[]{0};
            int axisOrdinal = axisCount - 1;
            this.recurse(writer, pos, axisOrdinal, cellOrdinal);
            writer.endSequence();
        }

        private void recurse(SaxWriter writer, List<Integer> pos, int axisOrdinal, int[] cellOrdinal) {
            if (axisOrdinal < 0) {
                int var10006 = cellOrdinal[0];
                int var10003 = cellOrdinal[0];
                cellOrdinal[0] = var10006 + 1;
                this.emitCell(writer, pos, var10003);
            } else {
                CellSetAxis axis = (CellSetAxis)this.cellSet.getAxes().get(axisOrdinal);
                List<Position> positions = axis.getPositions();
                int i = 0;

                for(int n = positions.size(); i < n; ++i) {
                    pos.set(axisOrdinal, i);
                    this.recurse(writer, pos, axisOrdinal - 1, cellOrdinal);
                }
            }

        }

        private void emitCell(SaxWriter writer, List<Integer> pos, int ordinal) {
            Cell cell = this.cellSet.getCell(pos);
            Boolean allPropertyIsEmpty = true;
            Iterator var6 = this.queryCellPropertyNames.iterator();

            String propertyName;
            while(var6.hasNext()) {
                propertyName = (String)var6.next();
                if (((MondrianOlap4jCell)cell).getRolapCell().getPropertyValue(propertyName) != null) {
                    allPropertyIsEmpty = false;
                    break;
                }
            }

            if (!cell.isNull() || !allPropertyIsEmpty || ordinal == 0) {
                writer.startElement("Cell", new Object[]{"CellOrdinal", ordinal});
                var6 = this.queryCellPropertyNames.iterator();

                while(true) {
                    Object value;
                    do {
                        while(true) {
                            do {
                                do {
                                    if (!var6.hasNext()) {
                                        writer.endElement();
                                        return;
                                    }

                                    propertyName = (String)var6.next();
                                    if (propertyName != null) {
                                        propertyName = propertyName.toUpperCase();
                                    }
                                } while(propertyName.equals("CELL_ORDINAL"));

                                MondrianOlap4jCell mondrianOlap4jCell = (MondrianOlap4jCell)cell;
                                value = mondrianOlap4jCell.getRolapCell().getPropertyValue(propertyName);
                            } while(value == null);

                            if (!this.json && StandardCellProperty.VALUE.getName().equals(propertyName)) {
                                break;
                            }

                            writer.textElement(((XmlaHandler.MDDataSet.CellProperty)cellPropertyMap.get(propertyName)).getAlias(), value);
                        }
                    } while(cell.isNull());

                    String dataType = (String)cell.getPropertyValue(StandardCellProperty.DATATYPE);
                    XmlaHandler.ValueInfo vi = new XmlaHandler.ValueInfo((String)null, value);
                    String valueType = vi.valueType;
                    String valueString;
                    if (vi.value instanceof Double && (Double)vi.value == Double.POSITIVE_INFINITY) {
                        valueString = "INF";
                    } else if (vi.isDecimal) {
                        valueString = XmlaUtil.normalizeNumericString(vi.value.toString());
                    } else {
                        valueString = vi.value.toString();
                    }

                    writer.startElement(((XmlaHandler.MDDataSet.CellProperty)cellPropertyMap.get(propertyName)).getAlias(), new Object[]{"xsi:type", valueType});
                    writer.characters(valueString);
                    writer.endElement();
                }
            }
        }
    }

    abstract static class MDDataSet implements XmlaHandler.QueryResult {
        protected final CellSet cellSet;
        protected static Map<String, XmlaHandler.MDDataSet.CellProperty> cellPropertyMap = new HashMap<String, XmlaHandler.MDDataSet.CellProperty>() {
            {
                this.put("CELL_ORDINAL", new XmlaHandler.MDDataSet.CellProperty("CELL_ORDINAL", "CellOrdinal", "xsd:unsignedInt"));
                this.put("VALUE", new XmlaHandler.MDDataSet.CellProperty("VALUE", "Value", (String)null));
                this.put("FORMATTED_VALUE", new XmlaHandler.MDDataSet.CellProperty("FORMATTED_VALUE", "FmtValue", "xsd:string"));
                this.put("FORMAT_STRING", new XmlaHandler.MDDataSet.CellProperty("FORMAT_STRING", "FormatString", "xsd:string"));
                this.put("LANGUAGE", new XmlaHandler.MDDataSet.CellProperty("LANGUAGE", "Language", "xsd:unsignedInt"));
                this.put("BACK_COLOR", new XmlaHandler.MDDataSet.CellProperty("BACK_COLOR", "BackColor", "xsd:unsignedInt"));
                this.put("FORE_COLOR", new XmlaHandler.MDDataSet.CellProperty("FORE_COLOR", "ForeColor", "xsd:unsignedInt"));
                this.put("FONT_FLAGS", new XmlaHandler.MDDataSet.CellProperty("FONT_FLAGS", "FontFlags", "xsd:int"));
            }
        };
        protected static final List<Property> defaultProps;
        protected static final Map<String, StandardMemberProperty> longProps;

        protected MDDataSet(CellSet cellSet) {
            this.cellSet = cellSet;
        }

        public void close() throws SQLException {
            this.cellSet.getStatement().getConnection().close();
        }

        private static Property rename(final Property property, final String name) {
            return new Property() {
                public Datatype getDatatype() {
                    return property.getDatatype();
                }

                public Set<TypeFlag> getType() {
                    return property.getType();
                }

                public ContentType getContentType() {
                    return property.getContentType();
                }

                public String getName() {
                    return name;
                }

                public String getUniqueName() {
                    return property.getUniqueName();
                }

                public String getCaption() {
                    return property.getCaption();
                }

                public String getDescription() {
                    return property.getDescription();
                }

                public boolean isVisible() {
                    return property.isVisible();
                }
            };
        }

        static {
            defaultProps = Arrays.asList(rename(StandardMemberProperty.MEMBER_UNIQUE_NAME, "UName"), rename(StandardMemberProperty.MEMBER_CAPTION, "Caption"), rename(StandardMemberProperty.LEVEL_UNIQUE_NAME, "LName"), rename(StandardMemberProperty.LEVEL_NUMBER, "LNum"), rename(StandardMemberProperty.DISPLAY_INFO, "DisplayInfo"));
            longProps = new HashMap();
            longProps.put("UName", StandardMemberProperty.MEMBER_UNIQUE_NAME);
            longProps.put("Caption", StandardMemberProperty.MEMBER_CAPTION);
            longProps.put("LName", StandardMemberProperty.LEVEL_UNIQUE_NAME);
            longProps.put("LNum", StandardMemberProperty.LEVEL_NUMBER);
            longProps.put("DisplayInfo", StandardMemberProperty.DISPLAY_INFO);
        }

        static class CellProperty {
            String name;
            String alias;
            String xsdType;

            public CellProperty(String name, String alias, String xsdType) {
                this.name = name;
                this.alias = alias;
                this.xsdType = xsdType;
            }

            public String getName() {
                return this.name;
            }

            public String getAlias() {
                return this.alias;
            }

            public String getXsdType() {
                return this.xsdType;
            }
        }
    }

    static class TabularRowSet implements XmlaHandler.QueryResult {
        private List<XmlaHandler.Column> columns = new ArrayList();
        private List<Object[]> rows;
        private int totalCount;

        public TabularRowSet(ResultSet rs, int totalCount) throws SQLException {
            this.totalCount = totalCount;
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();

            for(int i = 0; i < columnCount; ++i) {
                this.columns.add(new XmlaHandler.Column(md.getColumnLabel(i + 1), md.getColumnType(i + 1), md.getScale(i + 1)));
            }

            this.rows = new ArrayList();

            while(rs.next()) {
                Object[] row = new Object[columnCount];

                for(int i = 0; i < columnCount; ++i) {
                    row[i] = rs.getObject(i + 1);
                }

                this.rows.add(row);
            }

        }

        public TabularRowSet(Map<String, List<String>> tableFieldMap, List<String> tableList) {
            Iterator var3 = tableList.iterator();

            while(var3.hasNext()) {
                String tableName = (String)var3.next();
                List<String> fieldNames = (List)tableFieldMap.get(tableName);
                Iterator var6 = fieldNames.iterator();

                while(var6.hasNext()) {
                    String fieldName = (String)var6.next();
                    this.columns.add(new XmlaHandler.Column(tableName + "." + fieldName, 12, 0));
                }
            }

            this.rows = new ArrayList();
            Object[] row = new Object[this.columns.size()];

            for(int k = 0; k < row.length; ++k) {
                row[k] = k;
            }

            this.rows.add(row);
        }

        public TabularRowSet(List<XmlaHandler.Column> columns, List<Object[]> rows) {
            if (columns != null) {
                this.columns = columns;
            }

            if (rows != null) {
                this.rows = rows;
                this.totalCount = this.rows.size();
            }

        }

        public void close() {
        }

        public void unparse(SaxWriter writer) throws SAXException {
            if (this.totalCount >= 0) {
                String countStr = Integer.toString(this.totalCount);
                writer.startElement("row");
                Iterator var3 = this.columns.iterator();

                while(var3.hasNext()) {
                    XmlaHandler.Column column = (XmlaHandler.Column)var3.next();
                    writer.startElement(column.encodedName);
                    writer.characters(countStr);
                    writer.endElement();
                }

                writer.endElement();
            }

            Iterator var7 = this.rows.iterator();

            while(var7.hasNext()) {
                Object[] row = (Object[])var7.next();
                writer.startElement("row");

                for(int i = 0; i < row.length; ++i) {
                    Object value = row[i];
                    if (value != null) {
                        writer.startElement(((XmlaHandler.Column)this.columns.get(i)).encodedName, new Object[]{"xsi:type", ((XmlaHandler.Column)this.columns.get(i)).xsdType});
                        if (value == null) {
                            writer.characters("null");
                        } else {
                            if (value instanceof BigDecimal) {
                                value = ((BigDecimal)value).setScale(4, RoundingMode.HALF_EVEN);
                            }

                            String valueString = value.toString();
                            if (value instanceof Number) {
                                valueString = XmlaUtil.normalizeNumericString(valueString);
                            }

                            writer.characters(valueString);
                        }

                        writer.endElement();
                    }
                }

                writer.endElement();
            }

        }

        public void metadata(SaxWriter writer) {
            writer.startElement("xsd:schema", new Object[]{"xmlns:xsd", "http://www.w3.org/2001/XMLSchema", "targetNamespace", "urn:schemas-microsoft-com:xml-analysis:rowset", "xmlns", "urn:schemas-microsoft-com:xml-analysis:rowset", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance", "xmlns:sql", "urn:schemas-microsoft-com:xml-sql", "elementFormDefault", "qualified"});
            writer.startElement("xsd:element", new Object[]{"name", "root"});
            writer.startElement("xsd:complexType");
            writer.startElement("xsd:sequence");
            writer.element("xsd:element", new Object[]{"maxOccurs", "unbounded", "minOccurs", 0, "name", "row", "type", "row"});
            writer.endElement();
            writer.endElement();
            writer.endElement();
            writer.startElement("xsd:simpleType", new Object[]{"name", "uuid"});
            writer.startElement("xsd:restriction", new Object[]{"base", "xsd:string"});
            writer.element("xsd:pattern", new Object[]{"value", "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"});
            writer.endElement();
            writer.endElement();
            writer.startElement("xsd:complexType", new Object[]{"name", "row"});
            writer.startElement("xsd:sequence");
            Iterator var2 = this.columns.iterator();

            while(var2.hasNext()) {
                XmlaHandler.Column column = (XmlaHandler.Column)var2.next();
                writer.element("xsd:element", new Object[]{"minOccurs", 0, "name", column.encodedName, "sql:field", column.name, "type", column.xsdType});
            }

            writer.endElement();
            writer.endElement();
            writer.endElement();
        }

        public void setColumnName(int index, String name) {
            ((XmlaHandler.Column)this.columns.get(index)).setName(name);
        }
    }

    static class Column {
        private String name;
        private String encodedName;
        private final String xsdType;

        Column(String name, int type, int scale) {
            this.setName(name);
            this.xsdType = XmlaHandler.sqlToXsdType(type, scale);
        }

        public void setName(String name) {
            this.name = name;
            this.encodedName = ElementNameEncoder.INSTANCE.encode(name);
        }
    }

    private interface QueryResult {
        void unparse(SaxWriter var1) throws SAXException, OlapException, SQLException;

        void close() throws SQLException;

        void metadata(SaxWriter var1);
    }

    static class ValueInfo {
        String valueType;
        Object value;
        boolean isDecimal;

        static String getValueTypeHint(String dataType) {
            if (dataType != null) {
                return dataType.equals("Integer") ? "xsd:int" : (dataType.equals("Numeric") ? "xsd:double" : "xsd:string");
            } else {
                return null;
            }
        }

        ValueInfo(String dataType, Object inputValue) {
            String valueTypeHint = getValueTypeHint(dataType);
            long lval;
            BigDecimal bd2;
            BigInteger bi;
            BigDecimal bd;
            double dval;
            if (valueTypeHint != null) {
                if (valueTypeHint.equals("xsd:string")) {
                    this.valueType = valueTypeHint;
                    this.value = inputValue;
                    this.isDecimal = false;
                } else if (valueTypeHint.equals("xsd:int")) {
                    if (inputValue instanceof Integer) {
                        this.valueType = valueTypeHint;
                        this.value = inputValue;
                        this.isDecimal = false;
                    } else if (inputValue instanceof Byte) {
                        this.valueType = "xsd:byte";
                        this.value = inputValue;
                        this.isDecimal = false;
                    } else if (inputValue instanceof Short) {
                        this.valueType = "xsd:short";
                        this.value = inputValue;
                        this.isDecimal = false;
                    } else if (inputValue instanceof Long) {
                        lval = (Long)inputValue;
                        this.setValueAndType(lval);
                    } else if (inputValue instanceof BigInteger) {
                        bi = (BigInteger)inputValue;
                        lval = bi.longValue();
                        if (bi.equals(BigInteger.valueOf(lval))) {
                            this.setValueAndType(lval);
                        } else {
                            this.valueType = "xsd:integer";
                            this.value = inputValue;
                            this.isDecimal = false;
                        }
                    } else if (inputValue instanceof Float) {
                        Float f = (Float)inputValue;
                        lval = f.longValue();
                        if (f.equals(new Float((float)lval))) {
                            this.setValueAndType(lval);
                        } else {
                            this.valueType = "xsd:float";
                            this.value = inputValue;
                            this.isDecimal = true;
                        }
                    } else if (inputValue instanceof Double) {
                        Double d = (Double)inputValue;
                        lval = d.longValue();
                        if (d.equals(new Double((double)lval))) {
                            this.setValueAndType(lval);
                        } else {
                            this.valueType = "xsd:double";
                            this.value = inputValue;
                            this.isDecimal = true;
                        }
                    } else if (inputValue instanceof BigDecimal) {
                        bd = (BigDecimal)inputValue;

                        try {
                            lval = bd.longValue();
                            this.setValueAndType(lval);
                        } catch (ArithmeticException var11) {
                            try {
                                bi = bd.toBigIntegerExact();
                                this.valueType = "xsd:integer";
                                this.value = bi;
                                this.isDecimal = false;
                            } catch (ArithmeticException var10) {
                                this.valueType = "xsd:decimal";
                                this.value = inputValue;
                                this.isDecimal = true;
                            }
                        }
                    } else if (inputValue instanceof Number) {
                        this.value = ((Number)inputValue).longValue();
                        this.valueType = valueTypeHint;
                        this.isDecimal = false;
                    } else {
                        this.valueType = valueTypeHint;
                        this.value = inputValue;
                        this.isDecimal = false;
                    }
                } else if (valueTypeHint.equals("xsd:double")) {
                    if (inputValue instanceof Double) {
                        this.valueType = valueTypeHint;
                        this.value = inputValue;
                        this.isDecimal = true;
                    } else if (!(inputValue instanceof Byte) && !(inputValue instanceof Short) && !(inputValue instanceof Integer) && !(inputValue instanceof Long)) {
                        if (inputValue instanceof Float) {
                            this.value = inputValue;
                            this.valueType = "xsd:float";
                            this.isDecimal = true;
                        } else if (inputValue instanceof BigDecimal) {
                            bd = (BigDecimal)inputValue;
                            dval = bd.doubleValue();

                            try {
                                bd2 = Util.makeBigDecimalFromDouble(dval);
                                if (bd.compareTo(bd2) == 0) {
                                    this.valueType = "xsd:double";
                                    this.value = dval;
                                } else {
                                    this.valueType = "xsd:decimal";
                                    this.value = inputValue;
                                }
                            } catch (NumberFormatException var9) {
                                this.valueType = "xsd:decimal";
                                this.value = inputValue;
                            }

                            this.isDecimal = true;
                        } else if (inputValue instanceof BigInteger) {
                            bi = (BigInteger)inputValue;
                            lval = bi.longValue();
                            if (bi.equals(BigInteger.valueOf(lval))) {
                                this.setValueAndType(lval);
                            } else {
                                this.valueType = "xsd:integer";
                                this.value = inputValue;
                                this.isDecimal = true;
                            }
                        } else if (inputValue instanceof Number) {
                            this.value = ((Number)inputValue).doubleValue();
                            this.valueType = valueTypeHint;
                            this.isDecimal = true;
                        } else {
                            this.valueType = valueTypeHint;
                            this.value = inputValue;
                            this.isDecimal = true;
                        }
                    } else {
                        this.value = ((Number)inputValue).doubleValue();
                        this.valueType = valueTypeHint;
                        this.isDecimal = true;
                    }
                }
            } else if (inputValue instanceof String) {
                this.valueType = "xsd:string";
                this.value = inputValue;
                this.isDecimal = false;
            } else if (inputValue instanceof Integer) {
                this.valueType = "xsd:int";
                this.value = inputValue;
                this.isDecimal = false;
            } else if (inputValue instanceof Byte) {
                Byte b = (Byte)inputValue;
                this.valueType = "xsd:byte";
                this.value = b.intValue();
                this.isDecimal = false;
            } else if (inputValue instanceof Short) {
                Short s = (Short)inputValue;
                this.valueType = "xsd:short";
                this.value = s.intValue();
                this.isDecimal = false;
            } else if (inputValue instanceof Long) {
                this.setValueAndType((Long)inputValue);
            } else if (inputValue instanceof BigInteger) {
                bi = (BigInteger)inputValue;
                lval = bi.longValue();
                if (bi.equals(BigInteger.valueOf(lval))) {
                    this.setValueAndType(lval);
                } else {
                    this.valueType = "xsd:integer";
                    this.value = inputValue;
                    this.isDecimal = false;
                }
            } else if (inputValue instanceof Float) {
                this.valueType = "xsd:float";
                this.value = inputValue;
                this.isDecimal = true;
            } else if (inputValue instanceof Double) {
                this.valueType = "xsd:double";
                this.value = inputValue;
                this.isDecimal = true;
            } else if (inputValue instanceof BigDecimal) {
                bd = (BigDecimal)inputValue;
                dval = bd.doubleValue();

                try {
                    bd2 = Util.makeBigDecimalFromDouble(dval);
                    if (bd.compareTo(bd2) == 0) {
                        this.valueType = "xsd:double";
                        this.value = dval;
                    } else {
                        this.valueType = "xsd:decimal";
                        this.value = inputValue;
                    }
                } catch (NumberFormatException var8) {
                    this.valueType = "xsd:decimal";
                    this.value = inputValue;
                }

                this.isDecimal = true;
            } else if (inputValue instanceof Number) {
                this.value = ((Number)inputValue).longValue();
                this.valueType = "xsd:long";
                this.isDecimal = false;
            } else if (inputValue instanceof Boolean) {
                this.value = inputValue;
                this.valueType = "xsd:boolean";
                this.isDecimal = false;
            } else {
                this.valueType = "xsd:string";
                this.value = inputValue;
                this.isDecimal = false;
            }

        }

        private void setValueAndType(long lval) {
            if (!XmlaHandler.isValidXsdInt(lval)) {
                this.valueType = "xsd:long";
                this.value = lval;
            } else {
                this.valueType = "xsd:int";
                this.value = (int)lval;
            }

            this.isDecimal = false;
        }
    }

    private static enum SetType {
        ROW_SET,
        MD_DATA_SET;
    }
}