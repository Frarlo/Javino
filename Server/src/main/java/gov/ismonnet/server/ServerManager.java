package gov.ismonnet.server;

import gov.ismonnet.shared.UdpUtils;

import java.io.IOException;
import java.net.*;

public class ServerManager {

    private final int receivePort;

    private DatagramSocket receiveSocket;
    private Thread receiveThread;

    public ServerManager(int receivePort) {
        this.receivePort = receivePort;
    }

    public void bind() throws SocketException {

        receiveSocket = new DatagramSocket(receivePort);
        receiveThread = new Thread(() -> {



        });

        receiveThread.start();
    }

    public void send(String msg) throws IOException {
        receiveSocket.send(UdpUtils.getPacketToSend(msg, ip, receivePort));
    }

    public String read() throws IOException {

        byte[] buffer = new byte[256];

        DatagramPacket pkt = new DatagramPacket(buffer, buffer.length);
        receiveSocket.receive(pkt);

        return UdpUtils.getInfoReceivedPacket(pkt);
    }
}
