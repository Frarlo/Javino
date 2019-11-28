package gov.ismonnet.shared;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Constants {

    public static final int SEVER_PORT = 4444;
    public static final InetAddress SERVER_IP_ADDRESS;

    static {
        try {
            SERVER_IP_ADDRESS = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new AssertionError("There is no localhost address");
        }
    }

    private Constants() {}
}
