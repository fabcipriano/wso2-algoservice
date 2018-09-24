package br.com.facio.wso2.handler.model;

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
            ESB_HOSTNAME = InetAddress.getLocalHost().getHostName();
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

        LOG.info("before in creation eventAttributes=" + eventAttributes);
        eventAttributes.put(TRANSACTION_ENDPOINT_CLIENT_TO, endpointTO(synCtx));
        eventAttributes.put(TRANSACTION_ENDPOINT_CLIENT_FROM, endpointFROM(synCtx));
        eventAttributes.put("host", ESB_HOSTNAME);
        eventAttributes.put("appName", "WSO2_ESB");
        eventAttributes.put("type", "Transaction");
        eventAttributes.put("transactionType", "Web");
        eventAttributes.put("transactionSubType", "Inside Synapse ESB");
        eventAttributes.put("originalMessageId", synCtx.getMessageID());
        LOG.info("after in creation eventAttributes=" + eventAttributes);
    }
    
    public void finishAndLogTransactionInfo(MessageContext synCtx) {
        LOG.info("Before in finishAndLogTransactionInfo eventAttributes=" + eventAttributes);
        finish(synCtx);
        LOG.info("After in finishAndLogTransactionInfo eventAttributes=" + eventAttributes);
        logTransactionInfo();
        LOG.info("After in logTransactionInfo eventAttributes=" + eventAttributes);
    }

    private void finish(MessageContext synCtx) {
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
    }

    private void logTransactionInfo() {
        String transactionInfo = "========= TransactionInfo ============ \r\n";
        for (Map.Entry<String, Object> entry : eventAttributes.entrySet()) {
            transactionInfo += entry.getKey() + "=" + entry.getValue() + "\r\n";
        }

        transactionInfo += "============= end ===========\r\n";

        LOG.info(transactionInfo);
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
