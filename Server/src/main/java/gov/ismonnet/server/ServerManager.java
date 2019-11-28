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

public class ServerManager {

    private static final Logger LOGGER = LogManager.getLogger(ServerManager.class);
    private static final int SHUTDOWN_TIMEOUT = 5000;

    private final Set<ClientInformation> clients = new HashSet<>();
    private final int receivePort;

    private DatagramSocket receiveSocket;
    private Thread receiveThread;

    private DatagramPacket packet;
    private byte[] bufferIn;

    public ServerManager(int receivePort) {
        this.receivePort = receivePort;
    }

    public void bind() throws SocketException {
        receiveSocket = new DatagramSocket(receivePort);
        packet = new DatagramPacket(bufferIn, bufferIn.length);

        receiveThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    bufferIn = new byte[256];
                    receiveSocket.receive(packet);

                    connectClient(packet.getAddress(), packet.getPort());

                    final String msg = UdpUtils.getInfoReceivedPacket(packet);
                    final Commands cmd = Commands.fromString(msg);
                    if(cmd == null) {
                        LOGGER.warn("Invalid command received {}", msg);
                        continue;
                    }

                    // TODO: handle diff packets
                    sendToAll(cmd);
                }
            } catch (Exception e) {
                LOGGER.fatal("Unhandled exception when receiving packets", e);
            }
        });

        receiveThread.start();
    }

    public void stop() throws Exception {
        receiveThread.interrupt();
        receiveThread.join(SHUTDOWN_TIMEOUT);

        sendToAll(Commands.DISCONNECT);
        receiveSocket.close();
    }

    private void sendToAll(Commands cmd) {
        clients.forEach(client -> send(cmd, client));
    }

    private void send(Commands cmd, ClientInformation client) throws UncheckedIOException {
        try {
            final String msg = cmd.getToSend();
            receiveSocket.send(UdpUtils.getPacketToSend(msg, client.getClientIp(), client.getClientPort()));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private void connectClient(InetAddress newClientIp, int newClientPort) {
        clients.add(new ClientInformation(newClientIp, newClientPort));
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
    }
}
