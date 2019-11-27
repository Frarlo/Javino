import gov.ismonnet.shared.Constants;

import java.net.DatagramSocket;
import java.net.SocketException;

public class ServerMain {
    public static void main(String[] args) throws SocketException {
        DatagramSocket receiveSocket = new DatagramSocket(Constants.SEVER_MAIN_RECEIVE_PORT);
        DatagramSocket sendSocket = new DatagramSocket();
    }
}
