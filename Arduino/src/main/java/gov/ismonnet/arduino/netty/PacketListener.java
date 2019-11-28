package gov.ismonnet.arduino.netty;

import gov.ismonnet.arduino.netty.apacket.APacket;

public interface PacketListener {
    void receive(APacket packet);
}
