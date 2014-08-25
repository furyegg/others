package com.lombardrisk.xbrl.render;

import org.jboss.resteasy.client.ClientRequest;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

/**
 * Created by Cesar on 10/06/2014.
 */
public class RenderXbrlTest {
    @Test
    public void testRenderExcel() throws Exception {
        ClientRequest clientRequest = new ClientRequest("https://svcs.sandbox.paypal.com/AdaptivePayments/Pay");
        clientRequest.header("X-PAYPAL-SECURITY-USERID", "Sandbox-Caller-User-Id");
        clientRequest.header("X-PAYPAL-SECURITY-PASSWORD", "Sandbox-Caller-User-Id");
        clientRequest.header("X-PAYPAL-SECURITY-SIGNATURE", "Sandbox-Caller-User-Id");


        clientRequest.header("X-PAYPAL-APPLICATION-ID", "APP-80W284485P519543T");

        clientRequest.header("X-PAYPAL-REQUEST-DATA-FORMAT", "JSON");
        clientRequest.header("X-PAYPAL-RESPONSE-DATA-FORMAT", "JSON");

        String payload = "{\n" +
                "\"actionType\":\"PAY\",    // Specify the payment action\n" +
                "\"currencyCode\":\"USD\",  // The currency of the payment\n" +
                "\"receiverList\":{\"receiver\":[{\n" +
                "\"amount\":\"1.00\",                    // The payment amount\n" +
                "\"email\":\"Sandbox-Receiver-eMail\"}]  // The payment Receiver's email address\n" +
                "},\n" +
                "\n" +
                "// Where the Sender is redirected to after approving a successful payment\n" +
                "\"returnUrl\":\"http://Payment-Success-URL\",\n" +
                "\n" +
                "// Where the Sender is redirected to upon a canceled payment\n" +
                "\"cancelUrl\":\"http://Payment-Cancel-URL\",\n" +
                "\"requestEnvelope\":{\n" +
                "\"errorLanguage\":\"en_US\",    // Language used to display errors\n" +
                "\"detailLevel\":\"ReturnAll\"   // Error detail level\n" +
                "}\n" +
                "}";
        clientRequest.body(MediaType.APPLICATION_JSON_TYPE, payload);
        String response = clientRequest.postTarget(String.class);
        System.out.println(response);
    }
}
