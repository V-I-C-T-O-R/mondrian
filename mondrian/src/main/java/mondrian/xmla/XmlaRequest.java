/* Decompiler 1ms, total 615ms, lines 31 */
package mondrian.xmla;

import java.util.Map;
import org.olap4j.metadata.XmlaConstants.Method;

public interface XmlaRequest {
    Method getMethod();

    Map<String, String> getProperties();

    Map<String, Object> getRestrictions();

    String getStatement();

    String getRoleName();

    String getRequestType();

    boolean isDrillThrough();

    String getUsername();

    String getPassword();

    String getSessionId();

    String getAuthenticatedUser();

    String[] getAuthenticatedUserGroups();
}