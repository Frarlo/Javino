import gov.ismonnet.arduino.ArduinoClient;
import gov.ismonnet.arduino.ReceiveThread;

import java.net.DatagramSocket;
        import java.net.SocketException;

public class ArduinoMain {

    public static void main(String[] args) throws SocketException {
        ArduinoClient client = new ArduinoClient();

        Thread th = new ReceiveThread(
                () -> {
                    return client.read();
                },
                toSend -> client.send(toSend));
    }
}
