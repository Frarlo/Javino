package gov.ismonnet.arduino;

import gov.ismonnet.shared.Commands;
import gov.ismonnet.shared.UdpUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
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

    public void send(Commands cmd) throws UncheckedIOException {
        String msg = cmd.getToSend();
        try {
            sendSocket.send(UdpUtils.getPacketToSend(msg, ip, receivePort));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public Commands read() throws UncheckedIOException {
        try {
            byte[] buffer = new byte[256];

            DatagramPacket pkt = new DatagramPacket(buffer, buffer.length);
            receiveSocket.receive(pkt);

            return Commands.fromString(UdpUtils.getInfoReceivedPacket(pkt));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

}
