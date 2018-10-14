package br.com.facio.wso2.nhttp;

import br.com.facio.wso2.handler.model.RequestImpl;
import br.com.facio.wso2.handler.model.ResponseImpl;
import com.newrelic.api.agent.Agent;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.NHttpServerConnection;
import org.apache.http.protocol.HttpContext;
import org.apache.synapse.transport.http.conn.Scheme;
import org.apache.synapse.transport.nhttp.ListenerContext;
import org.apache.synapse.transport.nhttp.ServerHandler;
import org.apache.synapse.transport.nhttp.util.NhttpMetricsCollector;

/**
 *
 * @author fabiano
 */
public class NewRelicServerHandler extends ServerHandler {

    private static final String SERVER_TOKEN = "NEWRELIC_SERVERHANDLER_TOKEN";

    public NewRelicServerHandler(ConfigurationContext cfgCtx, Scheme scheme, ListenerContext listenerContext, NhttpMetricsCollector metrics) {
        super(cfgCtx, scheme, listenerContext, metrics);
    }

    @Override
    @Trace(dispatcher = true, metricName = "ServerHandler")
    public void requestReceived(NHttpServerConnection conn) {
        final Token token = NewRelic.getAgent().getTransaction().getToken();
        conn.getContext().setAttribute(SERVER_TOKEN, token);
        super.requestReceived(conn);
    }

    @Override
    @Trace(async = true)
    public void outputReady(NHttpServerConnection conn, ContentEncoder encoder) {
        
        if (encoder.isCompleted()) {
            Token token = linkAsync(conn);
            instrumentAndCallOriginalServerHandler(conn, encoder, token);
        } else {
            super.outputReady(conn, encoder);
        }
    }

    private void instrumentAndCallOriginalServerHandler(NHttpServerConnection conn, ContentEncoder encoder, Token token) {
        HttpRequest request = conn.getHttpRequest();
        HttpResponse response = conn.getHttpResponse();
        
        try {
            super.outputReady(conn, encoder);
            NewRelic.setRequestAndResponse(new RequestImpl(request), new ResponseImpl(response));
        } finally {
            if (token != null) {
                token.expire();
            }
        }
    }

    private Token linkAsync(NHttpServerConnection conn) {
        HttpContext context = conn.getContext();
        Token token = (Token) context.getAttribute(SERVER_TOKEN);
        if (token != null) {
            token.link();
        }
        return token;
    }

}
