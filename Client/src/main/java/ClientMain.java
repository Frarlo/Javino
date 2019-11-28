import gov.ismonnet.client.ClientManager;
import gov.ismonnet.client.GuiClient;
import gov.ismonnet.shared.Constants;

import java.net.SocketException;

public class ClientMain  {
    public static void main(String[] args) throws SocketException {
        ClientManager cm = new ClientManager( Constants.SERVER_IP_ADDRESS,Constants.SEVER_PORT);
        GuiClient gui = new GuiClient(cm);
        cm.add(gui);

        gui.setVisible(true);
        cm.connect();

        //Serve per stoppare tutto <3
        Runtime.getRuntime().addShutdownHook(new Thread(cm::stop));
    }

}
