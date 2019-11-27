package gov.ismonnet.shared;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class UDPManager {

    public static DatagramPacket getPacketToSend(String msg, InetAddress receiverIP, int receiverPort){

        return new DatagramPacket(msg.getBytes(), msg.getBytes().length, receiverIP, receiverPort);
    }

    public static String getInfoReceivedPacket(DatagramPacket received){

        return new String(received.getData()).substring(0, received.getLength());
    }

}
