package br.com.facio.wso2.handler.model;

import com.newrelic.api.agent.ExtendedRequest;
import com.newrelic.api.agent.HeaderType;
import java.util.Enumeration;
import org.apache.http.HttpRequest;

/**
 *
 * @author fabiano
 */
public class RequestImpl extends ExtendedRequest {
    private String method = "UNKNOWN";
    private String oriUri = "";
    
    public RequestImpl(HttpRequest req) {
        try {
            method = req.getRequestLine().getMethod().toUpperCase();
            oriUri = req.getRequestLine().getUri();
        } catch(Exception e) {/*ignore*/}
    }

    @Override
    public String getMethod() {
        return method;
    }

    public String getRequestURI() {
        return oriUri;
    }

    public String getRemoteUser() {
        return "";
    }

    public Enumeration getParameterNames() {
        return null;
    }

    public String[] getParameterValues(String string) {
        return null;
    }

    public Object getAttribute(String string) {
        return null;
    }

    public String getCookieValue(String string) {
        return "";
    }

    public HeaderType getHeaderType() {
        return null;
    }

    public String getHeader(String string) {
        return "";
    }
    
}
