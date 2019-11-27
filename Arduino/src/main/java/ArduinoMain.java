import gov.ismonnet.arduino.ArduinoClient;
import gov.ismonnet.arduino.ReceiveThread;
import gov.ismonnet.shared.Shared;

import java.io.IOException;
import java.net.DatagramSocket;
        import java.net.SocketException;

public class ArduinoMain {

    public static void main(String[] args) throws SocketException {
        ArduinoClient client = new ArduinoClient(Shared.IP_ADDRESS, Shared.ARDUINO_MAIN_RECEIVE_PORT);


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
