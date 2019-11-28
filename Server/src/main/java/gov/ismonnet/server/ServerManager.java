package gov.ismonnet.server;

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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerManager {

    // Constants

    private static final Logger LOGGER = LogManager.getLogger(ServerManager.class);
    private static final int SHUTDOWN_TIMEOUT = 5000;

    // Attributes

    private final int receivePort;
    private final Set<ClientInformation> clients = new HashSet<>();

    private final AtomicInteger ledState = new AtomicInteger(0);

    private DatagramSocket socket;
    private Thread receiveThread;

    private DatagramPacket packet;
    private byte[] bufferIn;

    public ServerManager(int receivePort) {
        this.receivePort = receivePort;
    }

    public void bind() throws SocketException {
        LOGGER.trace("Binding socket on port {}...", receivePort);
        socket = new DatagramSocket(receivePort);

        bufferIn = new byte[256];
        packet = new DatagramPacket(bufferIn, bufferIn.length);

        receiveThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    socket.receive(packet);

                    final String msg = UdpUtils.getInfoReceivedPacket(packet);
                    final Commands cmd = Commands.fromString(msg);
                    if (cmd == null) {
                        LOGGER.error("Invalid command received {}", msg);
                        continue;
                    }

                    // TODO: handle diff packets
                    LOGGER.trace("Received command {}", packet);
                    sendToAll(cmd);
                }
            } catch (Exception e) {
                LOGGER.fatal("Unhandled exception when receiving commands", e);
            }
        });

        LOGGER.trace("Starting receive thread...");
        receiveThread.start();

        LOGGER.trace("Server started.");
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
        sendToAll(Commands.DISCONNECT);
        LOGGER.trace("Closing socket...");
        socket.close();

        LOGGER.trace("Server stopped.");
    }

    private void sendToAll(Commands cmd) {
        clients.forEach(client -> send(cmd, client));
    }

    private void send(Commands cmd, ClientInformation client) throws UncheckedIOException {
        LOGGER.trace("Sending command {} to {}", cmd, client);

        try {
            final String msg = cmd.getToSend();
            socket.send(UdpUtils.getPacketToSend(msg, client.getClientIp(), client.getClientPort()));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private void connectClient(InetAddress newClientIp, int newClientPort) {
        final ClientInformation client = new ClientInformation(newClientIp, newClientPort);
        clients.add(client);
        LOGGER.trace("New connection {}", client);

        final boolean state = ledState.get() % 2 != 0;
        send(state ? Commands.TURN_ON_LED : Commands.TURN_OFF_LED, client);
    }

    private void disconnectClient(ClientInformation info) {
        clients.remove(info);
        LOGGER.trace("Closed connection {}", info);
    }

    static class ClientInformation {

        private final InetAddress clientIp;
        private final int clientPort;

        ClientInformation(final InetAddress ip, final int port) {
            this.clientPort = port;
            this.clientIp = ip;
        }

        InetAddress getClientIp() {
            return clientIp;
        }

        int getClientPort() {
            return clientPort;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClientInformation that = (ClientInformation) o;
            return clientPort == that.clientPort &&
                    clientIp.equals(that.clientIp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clientIp, clientPort);
        }

        @Override
        public String toString() {
            return "ClientInformation{" +
                    "clientIp=" + clientIp +
                    ", clientPort=" + clientPort +
                    '}';
        }
    }
}
