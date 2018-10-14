package br.com.facio.wso2.handler.model;

import com.newrelic.api.agent.ExtendedResponse;
import com.newrelic.api.agent.HeaderType;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

/**
 *
 * @author fabiano
 */
public class ResponseImpl extends ExtendedResponse {
    private int status;
    private String statusMessage;
    public ResponseImpl(HttpResponse resp) {
        try {
            status = resp.getStatusLine().getStatusCode();
            statusMessage = resp.getStatusLine().getReasonPhrase();
        } catch(Exception e) {/*ignore*/}
    }

    @Override
    public long getContentLength() {
        return 0;
    }

    public int getStatus() throws Exception {
        return status;
    }

    public String getStatusMessage() throws Exception {
        return statusMessage;
    }

    public String getContentType() {
        return null;
    }

    public HeaderType getHeaderType() {
        return null;
    }

    public void setHeader(String string, String string1) {
    }
    
}
