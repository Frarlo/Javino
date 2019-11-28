import gov.ismonnet.client.ClientManager;
import gov.ismonnet.client.GuiClient;
import gov.ismonnet.shared.Constants;

import java.net.SocketException;

public class ClientMain  {


    public static void main(String[] args) throws SocketException {
        ClientManager cm = new ClientManager( Constants.SERVER_IP_ADDRESS,Constants.SEVER_PORT);
        cm.connect();

        GuiClient gui = new GuiClient(cm);
        cm.add(gui);

        //Serve per stoppare tutto <3
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                cm.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

}
