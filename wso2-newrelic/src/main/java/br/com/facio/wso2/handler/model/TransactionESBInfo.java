package br.com.facio.wso2.handler.model;

import com.newrelic.api.agent.Agent;
import com.newrelic.api.agent.NewRelic;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;

/**
 *
 * @author fabianocp
 */
public class TransactionESBInfo {
    private static String ESB_HOSTNAME = "localhost";    
    private static Log LOG = LogFactory.getLog(TransactionESBInfo.class);
    private static String TRANSACTION_ENDPOINT_CLIENT_TO = "endpointClientTO";
    private static String TRANSACTION_ENDPOINT_CLIENT_FROM = "endpointClientFROM";
    private static String TRANSACTION_ENDPOINT_SERVER_TO = "endpointServerTO";
    private static String TRANSACTION_ENDPOINT_SERVER_FROM = "endpointServerFROM";
    static {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            LOG.info("localHost.: " + localHost.toString());
            String hostName = localHost.getHostName();
            LOG.info("hostName.: " + hostName);
            ESB_HOSTNAME = hostName;
        } catch (Exception ex) {
            LOG.error("Failed to find HOSTNAME ESB. Use default \"localhost\"", ex);
        }
    }

    private Map<String, Object> eventAttributes;
    private long startCurrentTimeMillis = 0;

    public TransactionESBInfo(MessageContext synCtx) {
        startCurrentTimeMillis = System.currentTimeMillis();
        eventAttributes = new HashMap<String, Object>();
        if (synCtx == null) {
            LOG.error("MessageContext IS NULL. Verify WSO2 configuration !!!!!!");
            return;
        }

        eventAttributes.put(TRANSACTION_ENDPOINT_CLIENT_TO, endpointTO(synCtx));
        eventAttributes.put(TRANSACTION_ENDPOINT_CLIENT_FROM, endpointFROM(synCtx));
        eventAttributes.put("host", ESB_HOSTNAME);
        eventAttributes.put("appName", "WSO2_ESB");
        eventAttributes.put("type", "Transaction");
        eventAttributes.put("transactionType", "Web");
        eventAttributes.put("transactionSubType", "Inside Synapse ESB");
        eventAttributes.put("originalMessageId", synCtx.getMessageID());
    }
    
    public void finishAndLogTransactionInfo(MessageContext synCtx) {
        finish(synCtx);
        logTransactionInfo();
    }
    
    public void finishAndSendToNewRelicInsights(MessageContext synCtx) {
        finish(synCtx);
        Agent agent = NewRelic.getAgent();  
        agent.getInsights().recordCustomEvent("Inside_Synapse_ESB", eventAttributes);  
    }
    
    public void insertResponseInFlowId(MessageContext synCtx) {
        eventAttributes.put("responseMessageInFlowId", synCtx.getMessageID());
    }

    private void finish(MessageContext synCtx) {
        
        if (synCtx == null) {
            LOG.error("MessageContext is NULL !!!!!");
            return;
        }
        
        long duration = 0;
        if (startCurrentTimeMillis != 0) {
            duration = System.currentTimeMillis() - startCurrentTimeMillis;
        } else {
            LOG.error("Failed to get Synapse ESB Duration! I think that this transaction skipped \"handleRequestInFlow() - phase()\". Is this possible ?");
        }
        eventAttributes.put("duration", duration);
        eventAttributes.put(TRANSACTION_ENDPOINT_SERVER_TO, endpointTO(synCtx));
        eventAttributes.put(TRANSACTION_ENDPOINT_SERVER_FROM, endpointFROM(synCtx));
        eventAttributes.put("responseMessageId", synCtx.getMessageID());
        eventAttributes.put("isFaultResponse", synCtx.isFaultResponse());
        if (synCtx.isFaultResponse()) {
            eventAttributes.put("faultEndpointTO", faultEndpointTO(synCtx));
        }
    }

    private void logTransactionInfo() {
        String transactionInfo = "========= TransactionInfo ============ \r\n";
        for (Map.Entry<String, Object> entry : eventAttributes.entrySet()) {
            transactionInfo += entry.getKey() + "=" + entry.getValue() + "\r\n";
        }

        transactionInfo += "============= end ===========\r\n";

        LOG.info(transactionInfo);
    }

    private String faultEndpointTO(MessageContext synCtx) {
        EndpointReference to = synCtx.getFaultTo();
        String toEndpoint = "";
        if (to != null) {
            toEndpoint = to.getAddress();
        }
        return toEndpoint;
    }
    
    private String endpointFROM(MessageContext synCtx) {
        EndpointReference from = synCtx.getFrom();
        String fromEndpoint = "";
        if (from != null) {
            fromEndpoint = from.getAddress();
        }
        return fromEndpoint;
    }

    private String endpointTO(MessageContext synCtx) {
        EndpointReference to = synCtx.getTo();
        String toEndpoint = "";
        if (to != null) {
            toEndpoint = to.getAddress();
        }
        return toEndpoint;
    }
}
