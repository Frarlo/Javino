package gov.ismonnet.client;

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
import java.util.ArrayList;
import java.util.List;

public class ClientManager {

    // Constants

    private static final Logger LOGGER = LogManager.getLogger(ClientManager.class);
    private static final int SHUTDOWN_TIMEOUT = 5000;

    // Attributes

    private final InetAddress ip;
    private final int receivePort;

    private final List<Listener> listeners = new ArrayList<>();

    private DatagramSocket socket;
    private Thread receiveThread;

    public ClientManager(InetAddress ip, int receivePort) {
        this.ip = ip;
        this.receivePort = receivePort;
    }

    public void connect() throws SocketException {
        LOGGER.trace("Binding socket on port {}...", receivePort);
        socket = new DatagramSocket();
        LOGGER.trace("Sending connect command...");
        send(Commands.CONNECT);

        receiveThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    final Commands cmd = read();
                    LOGGER.trace("Received command {}", cmd);

                    if(cmd == Commands.DISCONNECT) {
                        LOGGER.fatal("The server got shut down. Stopping...");
                        System.exit(0);
                    } else {
                        notifyList(cmd);
                    }
                }
            } catch (Exception e) {
                LOGGER.fatal("Unhandled exception when receiving packets", e);
            }
        });

        LOGGER.trace("Starting receive thread...");
        receiveThread.start();

        LOGGER.trace("Client started.");
    }

    public void stop() {
        LOGGER.trace("Interrupting thread...");
        receiveThread.interrupt();
        try {
            receiveThread.join(SHUTDOWN_TIMEOUT);
        } catch (InterruptedException ex) {
            LOGGER.fatal("Couldn't interrupt receive thread");
        }

        LOGGER.trace("Sending disconnect command...");
        send(Commands.DISCONNECT);
        LOGGER.trace("Closing socket...");
        socket.close();

        LOGGER.trace("Client stopped.");
    }

    void send(Commands cmd) throws UncheckedIOException {
        try {
            LOGGER.trace("Sending command {}", cmd);
            socket.send(UdpUtils.getPacketToSend(cmd.getToSend(), ip, receivePort));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private Commands read() throws UncheckedIOException {
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

    //methods for list

    public void add(Listener l) {
        listeners.add(l);
    }

    public void remove(Listener l) {
        listeners.remove(l);
    }

    private void notifyList(Commands cmd) {
        listeners.forEach(listener -> listener.receive(cmd));
    }
}
