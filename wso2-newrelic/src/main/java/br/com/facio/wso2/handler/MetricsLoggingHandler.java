package br.com.facio.wso2.handler;

import br.com.facio.wso2.handler.model.TransactionESBInfo;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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
    private static String TRANSACTION_ESB_INFO = "transactionESBInfoSynapse";

    public boolean handleRequestInFlow(MessageContext synCtx) {
        LOG.info("#-----> Begin IN Flux - handleRequestInFlow.: " + endpointInfo(synCtx));
        TransactionESBInfo info = new TransactionESBInfo(synCtx);
        synCtx.setProperty(TRANSACTION_ESB_INFO, info);
        return true;
    }

    public boolean handleRequestOutFlow(MessageContext synCtx) {
        LOG.info("#-----> End IN Flux - handleRequestOutFlow.: " + endpointInfo(synCtx));
        return true;
    }

    public boolean handleResponseInFlow(MessageContext synCtx) {
        LOG.info("#-----> Begin OUT Flux - handleResponseInFlow.: " + endpointInfo(synCtx));
        TransactionESBInfo transaction = findTransactionESB4ThisMessage(synCtx);
        transaction.insertResponseInFlowId(synCtx);
        return true;
    }

    public boolean handleResponseOutFlow(MessageContext synCtx) {
        LOG.info("#-----> End Out Flux - handleResponseOutFlow.: " + endpointInfo(synCtx));
        finalizeTransactionESBAndLog(synCtx);
        return true;
    }

    private void finalizeTransactionESBAndLog(MessageContext synCtx) {
        completeStatistcsAndLog(synCtx, findTransactionESB4ThisMessage(synCtx));
    }

    private TransactionESBInfo findTransactionESB4ThisMessage(MessageContext synCtx) {
        TransactionESBInfo transaction = null;
        Object property = synCtx.getProperty(TRANSACTION_ESB_INFO);
        if (isTransactionESBInfo(property)) {
            transaction = (TransactionESBInfo)property;
        } else {
            LOG.error("We dont HAVE Transaction ESB Info. Im sorry no statistcs for you !!!!!!");
        }
        
        return transaction;
    }

    private static boolean isTransactionESBInfo(Object property) {
        return (property != null) && (property  instanceof TransactionESBInfo);
    }

    private void completeStatistcsAndLog(MessageContext synCtx, TransactionESBInfo transactionESBInfo) {
        transactionESBInfo.finishAndLogTransactionInfo(synCtx);
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

}
