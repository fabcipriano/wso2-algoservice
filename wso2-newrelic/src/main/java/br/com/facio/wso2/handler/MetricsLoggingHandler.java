package br.com.facio.wso2.handler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.synapse.AbstractSynapseHandler;
import org.apache.synapse.MessageContext;

/**
 *
 * @author fabianocp
 */
public class MetricsLoggingHandler extends AbstractSynapseHandler {
    private static Log LOG = LogFactory.getLog(MetricsLoggingHandler.class);
    private static String ESB_HOSTNAME = "localhost";
    private static String TRANSACTION_ESB_INFO = "transactionESBInfoSynapse";

    static {
        try {
            ESB_HOSTNAME = InetAddress.getLocalHost().getHostName();
        } catch (Exception ex) {
            LOG.error("Failed to find HOSTNAME ESB. Use default \"localhost\"", ex);
        }
    }

    public boolean handleRequestInFlow(MessageContext synCtx) {
        LOG.info("-----> Begin IN Flux - handleRequestInFlow.: " + endpointInfo(synCtx));
        TransactionESBInfo info = new TransactionESBInfo(synCtx);
        synCtx.setProperty(TRANSACTION_ESB_INFO, info);
        return true;
    }

    public boolean handleRequestOutFlow(MessageContext synCtx) {
        LOG.info("-----> End IN Flux - handleRequestOutFlow.: " + endpointInfo(synCtx));
        return true;
    }

    public boolean handleResponseInFlow(MessageContext synCtx) {
        LOG.info("-----> Begin OUT Flux - handleResponseInFlow.: " + endpointInfo(synCtx));
        return true;
    }

    public boolean handleResponseOutFlow(MessageContext synCtx) {
        LOG.info("-----> End Out Flux - handleResponseOutFlow.: " + endpointInfo(synCtx));
        finalizeTransactionESBAndLog(synCtx);
        return true;
    }

    private void finalizeTransactionESBAndLog(MessageContext synCtx) {
        Object property = synCtx.getProperty(TRANSACTION_ESB_INFO);
        if (isTransactionESBInfo(property)) {
            completeStatistcsAndLog((MetricsLoggingHandler.TransactionESBInfo)property);
        } else {
            LOG.error("We dont HAVE Transaction ESB Info. Im sorry no statistcs for you !!!!!!");
        }
    }

    private static boolean isTransactionESBInfo(Object property) {
        return (property != null) && (property  instanceof MetricsLoggingHandler.TransactionESBInfo);
    }
    
    private String endpointInfo(MessageContext synCtx) {
        String fromStr = null;
        String toStr = null;
        EndpointReference from = synCtx.getFrom();
        if (from != null) {
            fromStr = from.getAddress();
        }
        EndpointReference to = synCtx.getTo();
        if (to != null) {
            toStr = to.getAddress();
        }
        
        return "from=" + fromStr + "; to=" + toStr;
    }

    private void completeStatistcsAndLog(TransactionESBInfo transactionESBInfo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static class TransactionESBInfo {
        public String TRANSACTION_ENDPOINT_CLIENT_TO = "endpointClientTO";
        public String TRANSACTION_ENDPOINT_CLIENT_FROM = "endpointClientFROM";
        public String TRANSACTION_ENDPOINT_SERVER_TO = "endpointServerTO";
        public String TRANSACTION_ENDPOINT_SERVER_FROM = "endpointServerFROM";
        
        private Map<String, Object> eventAttributes = new HashMap<String, Object>();
        private long startCurrentTimeMillis;
        
        public TransactionESBInfo( MessageContext synCtx ) {
            startCurrentTimeMillis = System.currentTimeMillis();
            if (synCtx == synCtx) {
                LOG.error("MessageContext IS NULL. Verify WSO2 configuration !!!!!!");
                return;
            }
            
            eventAttributes.put(TRANSACTION_ENDPOINT_CLIENT_TO, clientEndpointTO(synCtx));
            eventAttributes.put(TRANSACTION_ENDPOINT_CLIENT_FROM, clientEndpointFROM(synCtx));
            eventAttributes.put("host", ESB_HOSTNAME);
            eventAttributes.put("appName","WSO2_ESB");  
            eventAttributes.put("type","Transaction");  
            eventAttributes.put("transactionType","Web");  
            eventAttributes.put("transactionSubType","Inside Synapse ESB");  
            eventAttributes.put("originalMessageId", synCtx.getMessageID());
        }

        private String clientEndpointFROM(MessageContext synCtx) {
            EndpointReference from = synCtx.getFrom();
            String fromEndpoint = "";
            if (from != null) {
                fromEndpoint = from.getAddress();
            }
            return fromEndpoint;
        }

        private String clientEndpointTO(MessageContext synCtx) {
            EndpointReference to = synCtx.getTo();
            String toEndpoint = "";
            if (to != null) {
                toEndpoint = to.getAddress();
            }
            return toEndpoint;
        }        
    }
}
