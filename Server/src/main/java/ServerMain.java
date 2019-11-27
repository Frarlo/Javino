import gov.ismonnet.server.ServerManager;
import gov.ismonnet.shared.Constants;

import java.net.DatagramSocket;
import java.net.SocketException;

public class ServerMain {
    public static void main(String[] args) throws SocketException {
        ServerManager server = new ServerManager(Constants.SEVER_MAIN_RECEIVE_PORT);
        server.bind();
    }
}
