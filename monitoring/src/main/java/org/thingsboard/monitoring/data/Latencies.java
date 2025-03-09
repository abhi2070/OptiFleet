
package org.thingsboard.monitoring.data;

public class Latencies {

    public static final String WS_UPDATE = "wsUpdate";
    public static final String WS_CONNECT = "wsConnect";
    public static final String LOG_IN = "logIn";

    public static String request(String key) {
        return String.format("%sRequest", key);
    }

    public static String wsUpdate(String key) {
        return String.format("%sWsUpdate", key);
    }

}
