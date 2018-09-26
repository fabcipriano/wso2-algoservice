/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.facio.wso2.handler.model;

import java.util.Map;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.synapse.MessageContext;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author fabianocp
 */
@RunWith(MockitoJUnitRunner.class) 
public class TransactionESBInfoTest {

    @Mock
    MessageContext ctx;
    
    @Mock
    EndpointReference to;

    @Mock
    EndpointReference from;
    
    public TransactionESBInfoTest() {
    }

    @Test
    public void testConstructWithNull() {
        try {
            TransactionESBInfo t = new TransactionESBInfo(null);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Must NOT lauch exception");
        }        
    }
    
    @Test
    public void testConstructWithContextOK() {
        prepareContextOK();
        TransactionESBInfo t = new TransactionESBInfo(ctx);
        
        Map<String, Object> eventAttributes = t.getEventAttributes();
        assertEquals(eventAttributes.get("appName"), "WSO2_ESB");
    }

    private void prepareContextOK() {
//        when(to.getAddress()).thenReturn("paraOAlto");
//        when(from.getAddress()).thenReturn("deOnde");
//        when(ctx.getMessageID()).thenReturn("1234");
//        when(ctx.isFaultResponse()).thenReturn(false);
//        when(ctx.getTo()).thenReturn(to);
//        when(ctx.getFrom()).thenReturn(from);
    }
}
