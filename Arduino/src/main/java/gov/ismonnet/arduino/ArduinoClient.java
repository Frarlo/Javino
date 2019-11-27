package gov.ismonnet.arduino;

import gov.ismonnet.shared.UdpUtils;

import java.io.IOException;
import java.net.*;

public class ArduinoClient {

    private DatagramSocket receiveSocket;
    private DatagramSocket sendSocket;
    private InetAddress ip;
    private int receivePort;

    public ArduinoClient(InetAddress ip, int receivePort) throws SocketException {
        this.receiveSocket = new DatagramSocket(receivePort);
        this.sendSocket = new DatagramSocket();
        this.ip = ip;
        this.receivePort = receivePort;
    }

    public void send(String msg) throws IOException {

        sendSocket.send(UdpUtils.getPacketToSend(msg, ip, receivePort));
    }

    public String read() throws IOException {

        byte[] buffer = new byte[256];

        DatagramPacket pkt = new DatagramPacket(buffer, buffer.length);
        receiveSocket.receive(pkt);

        return UdpUtils.getInfoReceivedPacket(pkt);
    }

}
