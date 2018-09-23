package br.com.facio.wso2.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.synapse.AbstractSynapseHandler;
import org.apache.synapse.MessageContext;

/**
 *
 * @author fabianocp
 */
public class LoggingHandler extends AbstractSynapseHandler {
    private static Log LOG = LogFactory.getLog(LoggingHandler.class);


    public boolean handleRequestInFlow(MessageContext synCtx) {
        LOG.info("begin In Flux ...");
        LOG.info("handleRequestInFlow.: " + endpointInfo(synCtx));
        return true;
    }

    public boolean handleRequestOutFlow(MessageContext synCtx) {
        LOG.info("end In Flux.");
        LOG.info("handleRequestOutFlow.: " + endpointInfo(synCtx));
        return true;
    }

    public boolean handleResponseInFlow(MessageContext synCtx) {
        LOG.info("begin Out Flux ...");
        LOG.info("handleResponseInFlow.: " + endpointInfo(synCtx));
        return true;
    }

    public boolean handleResponseOutFlow(MessageContext synCtx) {
        LOG.info("end Out Flux.");
        LOG.info("handleResponseOutFlow.: " + endpointInfo(synCtx));
        return true;
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
