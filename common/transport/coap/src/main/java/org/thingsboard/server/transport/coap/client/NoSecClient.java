package org.thingsboard.server.transport.coap.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.elements.DtlsEndpointContext;
import org.eclipse.californium.elements.EndpointContext;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.thingsboard.common.util.ThingsBoardThreadFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoSecClient {

    private ExecutorService executor = Executors.newFixedThreadPool(1, ThingsBoardThreadFactory.forName(getClass().getSimpleName()));
    private CoapClient coapClient;

    public NoSecClient(String host, int port, String accessToken, String clientKeys, String sharedKeys) throws URISyntaxException {
        URI uri = new URI(getFutureUrl(host, port, accessToken, clientKeys, sharedKeys));
        this.coapClient = new CoapClient(uri);
    }

    public void test() {
        executor.submit(() -> {
            try {
                while (!Thread.interrupted()) {
                    CoapResponse response = null;
                    try {
                        response = coapClient.get();
                    } catch (ConnectorException | IOException e) {
                        System.err.println("Error occurred while sending request: " + e);
                        System.exit(-1);
                    }
                    if (response != null) {
                        EndpointContext context = response.advanced().getSourceContext();
                        Principal identity = context.getPeerIdentity();
                        if (identity != null) {
                            System.out.println(context.getPeerIdentity());
                        } else {
                            System.out.println("anonymous");
                        }
                        System.out.println(context.get(DtlsEndpointContext.KEY_CIPHER));
                        System.out.println(Utils.prettyPrint(response));
                    } else {
                        System.out.println("No response received.");
                    }
                    Thread.sleep(5000);
                }
            } catch (Exception e) {
                System.out.println("Error occurred while sending COAP requests.");
            }
        });
    }

    private String getFutureUrl(String host, Integer port, String accessToken, String clientKeys, String sharedKeys) {
        return "coap://" + host + ":" + port + "/api/v1/" + accessToken + "/attributes?clientKeys=" + clientKeys + "&sharedKeys=" + sharedKeys;
    }

    public static void main(String[] args) throws URISyntaxException {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String accessToken = args[2];
        String clientKeys = args[3];
        String sharedKeys = args[4];

        NoSecClient client = new NoSecClient(host, port, accessToken, clientKeys, sharedKeys);
        client.test();
    }
}
