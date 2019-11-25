import gov.ismonnet.shared.Shared;

import java.net.DatagramSocket;
import java.net.SocketException;

public class ServerMain {
    public static void main(String[] args) throws SocketException {
        DatagramSocket receiveSocket = new DatagramSocket(Shared.SEVER_MAIN_RECEIVE_PORT);
        DatagramSocket sendSocket = new DatagramSocket();
    }
}
