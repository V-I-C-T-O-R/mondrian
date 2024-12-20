/* Decompiler 154ms, total 1687ms, lines 200 */
package mondrian.xmla;

import org.olap4j.metadata.XmlaConstant;

public interface XmlaConstants {
    String NS_SOAP_ENV_1_1 = "http://schemas.xmlsoap.org/soap/envelope/";
    String NS_SOAP_ENC_1_1 = "http://schemas.xmlsoap.org/soap/encoding/";
    String NS_SOAP_ENV_1_2 = "http://www.w3.org/2003/05/soap-envelope";
    String NS_SOAP_ENC_1_2 = "http://www.w3.org/2003/05/soap-encoding";
    String NS_XSD = "http://www.w3.org/2001/XMLSchema";
    String NS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    String NS_XMLA = "urn:schemas-microsoft-com:xml-analysis";
    String NS_XMLA_MDDATASET = "urn:schemas-microsoft-com:xml-analysis:mddataset";
    String NS_XMLA_EMPTY = "urn:schemas-microsoft-com:xml-analysis:empty";
    String NS_XMLA_ROWSET = "urn:schemas-microsoft-com:xml-analysis:rowset";
    String NS_SQL = "urn:schemas-microsoft-com:xml-sql";
    String NS_XMLA_EX = "urn:schemas-microsoft-com:xml-analysis:exception";
    String NS_SOAP_SECEXT = "http://schemas.xmlsoap.org/ws/2002/04/secext";
    String SOAP_PREFIX = "SOAP-ENV";
    String NS_AS_ENGINE = "http://schemas.microsoft.com/analysisservices/2003/engine";
    String SOAP_MUST_UNDERSTAND_ATTR = "mustUnderstand";
    String XMLA_BEGIN_SESSION = "BeginSession";
    String XMLA_SESSION = "Session";
    String XMLA_END_SESSION = "EndSession";
    String XMLA_SESSION_ID = "SessionId";
    String XMLA_SECURITY = "Security";
    String CONTEXT_ROLE_NAME = "role_name";
    String CONTEXT_MIME_TYPE = "language";
    String CONTEXT_XMLA_SESSION_ID = "session_id";
    String CONTEXT_XMLA_USERNAME = "username";
    String CONTEXT_XMLA_PASSWORD = "password";
    String CONTEXT_XMLA_AUTHENTICATED_USER = "AuthenticatedUser";
    String CONTEXT_XMLA_AUTHENTICATED_USER_GROUPS = "AuthenticatedUserGroups";
    String CONTEXT_XMLA_SESSION_STATE = "SessionState";
    String CONTEXT_XMLA_SESSION_STATE_BEGIN = "SessionStateBegin";
    String CONTEXT_XMLA_SESSION_STATE_WITHIN = "SessionStateWithin";
    String CONTEXT_XMLA_SESSION_STATE_END = "SessionStateEnd";
    String MONDRIAN_NAMESPACE = "http://mondrian.sourceforge.net";
    String FAULT_NS_PREFIX = "XA";
    String FAULT_ACTOR = "Mondrian";
    String VERSION_MISSMATCH_FAULT_FC = "VersionMismatch";
    String MUST_UNDERSTAND_FAULT_FC = "MustUnderstand";
    String CLIENT_FAULT_FC = "Client";
    String SERVER_FAULT_FC = "Server";
    String FAULT_FC_PREFIX = "Mondrian";
    String FAULT_FS_PREFIX = "The Mondrian XML: ";
    String SIE_REQUEST_STATE_CODE = "00SIEA01";
    String SIE_REQUEST_STATE_FAULT_FS = "Servlet initialization error";
    String USM_REQUEST_STATE_CODE = "00USMA01";
    String USM_REQUEST_STATE_FAULT_FS = "Request input method invoked at illegal time";
    String USM_REQUEST_INPUT_CODE = "00USMA02";
    String USM_REQUEST_INPUT_FAULT_FS = "Request input Exception occurred";
    String USM_DOM_FACTORY_CODE = "00USMB01";
    String USM_DOM_FACTORY_FAULT_FS = "DocumentBuilder cannot be created which satisfies the configuration requested";
    String USM_DOM_PARSE_IO_CODE = "00USMC01";
    String USM_DOM_PARSE_IO_FAULT_FS = "DOM parse IO errors occur";
    String USM_DOM_PARSE_CODE = "00USMC02";
    String USM_DOM_PARSE_FAULT_FS = "DOM parse errors occur";
    String USM_UNKNOWN_CODE = "00USMU01";
    String USM_UNKNOWN_FAULT_FS = "Unknown error unmarshalling soap message";
    String CHH_CODE = "00CHHA01";
    String CHH_FAULT_FS = "Error in Callback processHttpHeader";
    String CHH_AUTHORIZATION_CODE = "00CHHA02";
    String CHH_AUTHORIZATION_FAULT_FS = "Error in Callback processHttpHeader Authorization";
    String CPREA_CODE = "00CPREA01";
    String CPREA_FAULT_FS = "Error in Callback PreAction";
    String HSH_MUST_UNDERSTAND_CODE = "00HSHA01";
    String HSH_MUST_UNDERSTAND_FAULT_FS = "SOAP Header must understand element not recognized";
    String HSH_BAD_SESSION_ID_CODE = "00HSHB01";
    String HSH_BAD_SESSION_ID_FAULT_FS = "Bad Session Id, re-start session";
    String HSH_UNKNOWN_CODE = "00HSHU01";
    String HSH_UNKNOWN_FAULT_FS = "Unknown error handle soap header";
    String HSB_BAD_SOAP_BODY_CODE = "00HSBA01";
    String HSB_BAD_SOAP_BODY_FAULT_FS = "SOAP Body not correctly formed";
    String HSB_PROCESS_CODE = "00HSBB01";
    String HSB_PROCESS_FAULT_FS = "XMLA SOAP Body processing error";
    String HSB_BAD_METHOD_CODE = "00HSBB02";
    String HSB_BAD_METHOD_FAULT_FS = "XMLA SOAP bad method";
    String HSB_BAD_METHOD_NS_CODE = "00HSBB03";
    String HSB_BAD_METHOD_NS_FAULT_FS = "XMLA SOAP bad method namespace";
    String HSB_BAD_REQUEST_TYPE_CODE = "00HSBB04";
    String HSB_BAD_REQUEST_TYPE_FAULT_FS = "XMLA SOAP bad Discover RequestType element";
    String HSB_BAD_RESTRICTIONS_CODE = "00HSBB05";
    String HSB_BAD_RESTRICTIONS_FAULT_FS = "XMLA SOAP bad Discover Restrictions element";
    String HSB_BAD_PROPERTIES_CODE = "00HSBB06";
    String HSB_BAD_PROPERTIES_FAULT_FS = "XMLA SOAP bad Discover or Execute Properties element";
    String HSB_BAD_COMMAND_CODE = "00HSBB07";
    String HSB_BAD_COMMAND_FAULT_FS = "XMLA SOAP bad Execute Command element";
    String HSB_BAD_RESTRICTION_LIST_CODE = "00HSBB08";
    String HSB_BAD_RESTRICTION_LIST_FAULT_FS = "XMLA SOAP too many Discover RestrictionList element";
    String HSB_BAD_PROPERTIES_LIST_CODE = "00HSBB09";
    String HSB_BAD_PROPERTIES_LIST_FAULT_FS = "XMLA SOAP bad Discover or Execute PropertyList element";
    String HSB_BAD_STATEMENT_CODE = "00HSBB10";
    String HSB_BAD_STATEMENT_FAULT_FS = "XMLA SOAP bad Execute Statement element";
    String HSB_BAD_NON_NULLABLE_COLUMN_CODE = "00HSBB16";
    String HSB_BAD_NON_NULLABLE_COLUMN_FAULT_FS = "XMLA SOAP non-nullable column";
    String HSB_CONNECTION_DATA_SOURCE_CODE = "00HSBC01";
    String HSB_CONNECTION_DATA_SOURCE_FAULT_FS = "XMLA connection datasource not found";
    String HSB_ACCESS_DENIED_CODE = "00HSBC02";
    String HSB_ACCESS_DENIED_FAULT_FS = "XMLA connection with role must be authenticated";
    String HSB_PARSE_QUERY_CODE = "00HSBD01";
    String HSB_PARSE_QUERY_FAULT_FS = "XMLA MDX parse failed";
    String HSB_EXECUTE_QUERY_CODE = "00HSBD02";
    String HSB_EXECUTE_QUERY_FAULT_FS = "XMLA MDX execute failed";
    String HSB_DISCOVER_FORMAT_CODE = "00HSBE01";
    String HSB_DISCOVER_FORMAT_FAULT_FS = "XMLA Discover format error";
    String HSB_DRILL_THROUGH_FORMAT_CODE = "00HSBE02";
    String HSB_DRILL_THROUGH_FORMAT_FAULT_FS = "XMLA Drill Through format error";
    String HSB_DISCOVER_UNPARSE_CODE = "00HSBE02";
    String HSB_DISCOVER_UNPARSE_FAULT_FS = "XMLA Discover unparse results error";
    String HSB_EXECUTE_UNPARSE_CODE = "00HSBE03";
    String HSB_EXECUTE_UNPARSE_FAULT_FS = "XMLA Execute unparse results error";
    String HSB_DRILL_THROUGH_NOT_ALLOWED_CODE = "00HSBF01";
    String HSB_DRILL_THROUGH_NOT_ALLOWED_FAULT_FS = "XMLA Drill Through not allowed";
    String HSB_DRILL_THROUGH_SQL_CODE = "00HSBF02";
    String HSB_DRILL_THROUGH_SQL_FAULT_FS = "XMLA Drill Through SQL error";
    String HSB_UNKNOWN_CODE = "00HSBU01";
    String HSB_UNKNOWN_FAULT_FS = "Unknown error handle soap body";
    String CPOSTA_CODE = "00CPOSTA01";
    String CPOSTA_FAULT_FS = "Error in Callback PostAction";
    String MSM_UNKNOWN_CODE = "00MSMU01";
    String MSM_UNKNOWN_FAULT_FS = "Unknown error marshalling soap message";
    String UNKNOWN_ERROR_CODE = "00UE001";
    String UNKNOWN_ERROR_FAULT_FS = "Internal Error";

    public static enum Literal implements XmlaConstant {
        CATALOG_NAME(2, (String)null, 24, ".", "0123456789", 2, "A catalog name in a text command."),
        CATALOG_SEPARATOR(3, ".", 0, (String)null, (String)null, 3, (String)null),
        COLUMN_ALIAS(5, (String)null, -1, "'\"[]", "0123456789", 5, (String)null),
        COLUMN_NAME(6, (String)null, -1, ".", "0123456789", 6, (String)null),
        CORRELATION_NAME(7, (String)null, -1, "'\"[]", "0123456789", 7, (String)null),
        CUBE_NAME(21, (String)null, -1, ".", "0123456789", 21, (String)null),
        DIMENSION_NAME(22, (String)null, -1, ".", "0123456789", 22, (String)null),
        HIERARCHY_NAME(23, (String)null, -1, ".", "0123456789", 23, (String)null),
        LEVEL_NAME(24, (String)null, -1, ".", "0123456789", 24, (String)null),
        MEMBER_NAME(25, (String)null, -1, ".", "0123456789", 25, (String)null),
        PROCEDURE_NAME(14, (String)null, -1, ".", "0123456789", 14, (String)null),
        PROPERTY_NAME(26, (String)null, -1, ".", "0123456789", 26, (String)null),
        QUOTE(15, "[", -1, (String)null, (String)null, 15, "The character used in a text command as the opening quote for quoting identifiers that contain special characters."),
        QUOTE_SUFFIX(28, "]", -1, (String)null, (String)null, 28, "The character used in a text command as the closing quote for quoting identifiers that contain special characters. 1.x providers that use the same character as the prefix and suffix may not return this literal value and can set the lt member of the DBLITERAL structure to DBLITERAL_INVALID if requested."),
        TABLE_NAME(17, (String)null, -1, ".", "0123456789", 17, (String)null),
        TEXT_COMMAND(18, (String)null, -1, (String)null, (String)null, 18, "A text command, such as an SQL statement."),
        USER_NAME(19, (String)null, 0, (String)null, (String)null, 19, (String)null);

        private int xmlaOrdinal;
        private final String literalValue;
        private final int literalMaxLength;
        private final String literalInvalidChars;
        private final String literalInvalidStartingChars;
        private final int literalNameEnumValue;
        private final String description;

        private Literal(int xmlaOrdinal, String literalValue, int literalMaxLength, String literalInvalidChars, String literalInvalidStartingChars, int literalNameEnumValue, String description) {
            this.xmlaOrdinal = xmlaOrdinal;
            this.literalValue = literalValue;
            this.literalMaxLength = literalMaxLength;
            this.literalInvalidChars = literalInvalidChars;
            this.literalInvalidStartingChars = literalInvalidStartingChars;
            this.literalNameEnumValue = literalNameEnumValue;
            this.description = description;
        }

        public String getLiteralName() {
            return this.xmlaName();
        }

        public String getLiteralValue() {
            return this.literalValue;
        }

        public String getLiteralInvalidChars() {
            return this.literalInvalidChars;
        }

        public String getLiteralInvalidStartingChars() {
            return this.literalInvalidStartingChars;
        }

        public int getLiteralMaxLength() {
            return this.literalMaxLength;
        }

        public int getLiteralNameEnumValue() {
            return this.literalNameEnumValue;
        }

        public String xmlaName() {
            return "DBLITERAL_" + this.name();
        }

        public String getDescription() {
            return this.description;
        }

        public int xmlaOrdinal() {
            return this.xmlaOrdinal;
        }
    }
}