/* Decompiler 12ms, total 515ms, lines 75 */
package mondrian.xmla.impl;

import java.util.HashMap;
import java.util.Map;
import mondrian.xmla.XmlaRequest;
import org.olap4j.metadata.XmlaConstants.Method;

public class DmvXmlaRequest implements XmlaRequest {
    private HashMap<String, Object> restrictions;
    private HashMap<String, String> properties;
    private String roleName;
    private String requestType;
    private String username;
    private String password;
    private String sessionId;

    public DmvXmlaRequest(Map<String, Object> restrictions, Map<String, String> properties, String roleName, String requestType, String username, String password, String sessionId) {
        this.restrictions = new HashMap(restrictions);
        this.properties = new HashMap(properties);
        this.roleName = roleName;
        this.requestType = requestType;
        this.username = username;
        this.password = password;
        this.sessionId = sessionId;
    }

    public Method getMethod() {
        return Method.DISCOVER;
    }

    public Map<String, String> getProperties() {
        return new HashMap();
    }

    public Map<String, Object> getRestrictions() {
        return this.restrictions;
    }

    public String getStatement() {
        return null;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public String getRequestType() {
        return this.requestType;
    }

    public boolean isDrillThrough() {
        return false;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public String getAuthenticatedUser() {
        return null;
    }

    public String[] getAuthenticatedUserGroups() {
        return null;
    }
}