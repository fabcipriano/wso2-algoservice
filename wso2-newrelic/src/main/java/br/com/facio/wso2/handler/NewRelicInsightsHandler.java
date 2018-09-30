package br.com.facio.wso2.handler;

import br.com.facio.wso2.handler.model.TransactionESBInfo;
import com.newrelic.api.agent.Trace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.AbstractSynapseHandler;
import org.apache.synapse.MessageContext;

/**
 *
 * @author fabianocp
 */
public class NewRelicInsightsHandler extends AbstractSynapseHandler {
    private static Log LOG = LogFactory.getLog(NewRelicInsightsHandler.class);
    private static String TRANSACTION_ESB_INFO = "transactionESBInfoSynapse";

    @Trace  
    public boolean handleRequestInFlow(MessageContext synCtx) {
        TransactionESBInfo info = new TransactionESBInfo(synCtx);
        synCtx.setProperty(TRANSACTION_ESB_INFO, info);
        return true;
    }

    @Trace  
    public boolean handleRequestOutFlow(MessageContext synCtx) {
        return true;
    }

    @Trace  
    public boolean handleResponseInFlow(MessageContext synCtx) {
        TransactionESBInfo transaction = findTransactionESB4ThisMessage(synCtx);
        transaction.insertResponseInFlowId(synCtx);
        return true;
    }

    @Trace  
    public boolean handleResponseOutFlow(MessageContext synCtx) {
        if (LOG.isInfoEnabled()) {
            finalizeTransactionESBAndLog(synCtx);
        }
        finalizeTransactionESBAndSendToNewRelicInsights(synCtx);
        return true;
    }

    private void finalizeTransactionESBAndSendToNewRelicInsights(MessageContext synCtx) {
        completeStatistcsAndSendToNewRelicInsights(synCtx, findTransactionESB4ThisMessage(synCtx));
    }

    private void finalizeTransactionESBAndLog(MessageContext synCtx) {
        completeStatistcsAndLog(synCtx, findTransactionESB4ThisMessage(synCtx));
    }
    
    private void completeStatistcsAndLog(MessageContext synCtx, TransactionESBInfo transactionESBInfo) {
        transactionESBInfo.finishAndLogTransactionInfo(synCtx);
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

    private void completeStatistcsAndSendToNewRelicInsights(MessageContext synCtx, TransactionESBInfo transactionESBInfo) {
        transactionESBInfo.finishAndSendToNewRelicInsights(synCtx);
    }    
    
}
