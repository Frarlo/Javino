import gov.ismonnet.client.ClientManager;
import gov.ismonnet.client.GuiClient;
import gov.ismonnet.client.Listener;
import gov.ismonnet.shared.Commands;
import gov.ismonnet.shared.Constants;

import java.net.DatagramSocket;
import java.net.SocketException;

public class ClientMain  {


    public static void main(String[] args) throws SocketException {

        ClientManager cm = new ClientManager( Constants.IP_ADDRESS,Constants.SEVER_MAIN_RECEIVE_PORT);
        cm.connect();

        GuiClient gui = new GuiClient();
        cm.add(gui);




    }

}
