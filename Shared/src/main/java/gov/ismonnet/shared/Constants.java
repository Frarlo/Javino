package gov.ismonnet.shared;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Constants {

    public static final int SEVER_MAIN_RECEIVE_PORT = 4444;
    public static final InetAddress IP_ADDRESS;

    static {
        try {
            IP_ADDRESS = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new AssertionError("There is no localhost address");
        }
    }

    private Constants() {}
}
