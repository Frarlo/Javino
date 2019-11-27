package gov.ismonnet.server;

import gov.ismonnet.shared.UDPManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServerManager {
    private DatagramSocket receiveSocket;
    private DatagramSocket sendSocket;
    private InetAddress ip;
    private int receivePort;

    public ServerManager(InetAddress ip, int receivePort) throws SocketException {

        receiveSocket = new DatagramSocket(receivePort);
        sendSocket = new DatagramSocket();
        this.ip = ip;
        this.receivePort = receivePort;
    }

    public void send(String msg) throws IOException {

        sendSocket.send(UDPManager.getPacketToSend(msg, ip, receivePort));
    }

    public String read() throws IOException {

        byte[] buffer = new byte[256];

        DatagramPacket pkt = new DatagramPacket(buffer, buffer.length);
        receiveSocket.receive(pkt);

        return UDPManager.getInfoReceivedPacket(pkt);
    }
}
