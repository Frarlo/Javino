import gov.ismonnet.arduino.ArduinoClient;
import gov.ismonnet.arduino.ReceiveThread;
import gov.ismonnet.shared.Constants;

import java.net.SocketException;

public class ArduinoMain {

    public static void main(String[] args) throws SocketException {
        ArduinoClient client = new ArduinoClient(Constants.IP_ADDRESS, Constants.ARDUINO_MAIN_RECEIVE_PORT);


        final Thread th = new ReceiveThread(
                client::read,
                client::send);
        th.start();

        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
