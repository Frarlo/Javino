import gov.ismonnet.arduino.ArduinoClient;
import gov.ismonnet.arduino.ReceiveThread;
import gov.ismonnet.shared.Constants;

import java.io.IOException;
import java.net.SocketException;

public class ArduinoMain {

    public static void main(String[] args) throws SocketException {
        ArduinoClient client = new ArduinoClient(Constants.IP_ADDRESS, Constants.ARDUINO_MAIN_RECEIVE_PORT);


        Thread th = new ReceiveThread(
                () -> {
                    try {
                        return client.read();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                },
                toSend -> {
                    try {
                        client.send(toSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        th.start();

        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
