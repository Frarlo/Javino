package gov.ismonnet.client;

import gov.ismonnet.shared.Commands;
import gov.ismonnet.shared.UdpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ClientManager {

    private static final Logger LOGGER = LogManager.getLogger(ClientManager.class);
    private static final int SHUTDOWN_TIMEOUT = 5000;

    private DatagramSocket socket;
    private Thread receiveThread;

    private final List<Listener> listeners = new ArrayList<>();

    private InetAddress ip;
    private int receivePort;

    public ClientManager(InetAddress ip, int receivePort) {
        this.ip = ip;
        this.receivePort = receivePort;
    }

    public void connect() throws SocketException {
        socket = new DatagramSocket();
        receiveThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    final Commands cmd = read();
                    notifyList(cmd);
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

        send(Commands.DISCONNECT);
        socket.close();
    }

    void send(Commands cmd) throws UncheckedIOException {
        String msg = cmd.getToSend();
        try {
            socket.send(UdpUtils.getPacketToSend(msg, ip, receivePort));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private Commands read() throws UncheckedIOException {
        try {
            byte[] buffer = new byte[256];

            DatagramPacket pkt = new DatagramPacket(buffer, buffer.length);
            socket.receive(pkt);

            return Commands.fromString(UdpUtils.getInfoReceivedPacket(pkt));
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
