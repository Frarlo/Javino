package gov.ismonnet.arduino;

import gov.ismonnet.shared.Commands;
import gov.ismonnet.shared.UdpUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.*;

public class ArduinoClient {

    private DatagramSocket socket;
    private InetAddress ip;
    private int receivePort;

    public ArduinoClient(InetAddress ip, int receivePort) {
        this.ip = ip;
        this.receivePort = receivePort;
    }

    public void connect() throws SocketException {
        socket = new DatagramSocket();
    }

    public void close(){
        socket.close();
    }

    public void send(Commands cmd) throws UncheckedIOException {
        String msg = cmd.getToSend();
        try {
            socket.send(UdpUtils.getPacketToSend(msg, ip, receivePort));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public Commands read() throws UncheckedIOException {
        try {
            byte[] buffer = new byte[256];

            DatagramPacket pkt = new DatagramPacket(buffer, buffer.length);
            socket.receive(pkt);

            return Commands.fromString(UdpUtils.getInfoReceivedPacket(pkt));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

}
