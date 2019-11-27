import gov.ismonnet.shared.Constants;

import java.net.DatagramSocket;
import java.net.SocketException;

public class ClientMain {
    public static void main(String[] args) throws SocketException {

        DatagramSocket receiveSocket = new DatagramSocket(Constants.CLIENT_MAIN_RECEIVE_PORT);
        DatagramSocket sendSocket = new DatagramSocket();
    }
}
