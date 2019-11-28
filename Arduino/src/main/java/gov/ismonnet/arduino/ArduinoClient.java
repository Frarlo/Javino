package gov.ismonnet.arduino;

import gov.ismonnet.shared.Commands;
import gov.ismonnet.shared.UdpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ArduinoClient {

    // Constants

    private static final Logger LOGGER = LogManager.getLogger(ArduinoClient.class);

    // Attributes

    private final InetAddress ip;
    private final int receivePort;

    private DatagramSocket socket;

    public ArduinoClient(InetAddress ip, int receivePort) {
        this.ip = ip;
        this.receivePort = receivePort;
    }

    public void connect() throws SocketException {
        LOGGER.trace("Binding socket on port {}...", receivePort);
        socket = new DatagramSocket();

        LOGGER.trace("Arduino client started.");
    }

    public void close() {
        LOGGER.trace("Sending disconnect command...");
        send(Commands.DISCONNECT);
        LOGGER.trace("Closing socket...");
        socket.close();

        LOGGER.trace("Arduino client stopped.");
    }

    public void send(Commands cmd) throws UncheckedIOException {
        try {
            LOGGER.trace("Sending command {}", cmd);
            socket.send(UdpUtils.getPacketToSend(cmd.getToSend(), ip, receivePort));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public Commands read() throws UncheckedIOException {
        try {
            while(true) {
                byte[] buffer = new byte[256];

                DatagramPacket pkt = new DatagramPacket(buffer, buffer.length);
                socket.receive(pkt);

                final String msg = UdpUtils.getInfoReceivedPacket(pkt);
                final Commands cmd = Commands.fromString(msg);
                if (cmd == null) {
                    LOGGER.error("Invalid command received {}", msg);
                    continue;
                }

                return cmd;
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
