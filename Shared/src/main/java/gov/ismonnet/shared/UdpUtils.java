package gov.ismonnet.shared;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UdpUtils {

    public static DatagramPacket getPacketToSend(String msg, InetAddress receiverIP, int receiverPort) {
        final byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        return new DatagramPacket(bytes, bytes.length, receiverIP, receiverPort);
    }

    public static String getInfoReceivedPacket(DatagramPacket received) {
        return new String(received.getData(), StandardCharsets.UTF_8)
                .substring(0, received.getLength());
    }

    private UdpUtils() {}
}
