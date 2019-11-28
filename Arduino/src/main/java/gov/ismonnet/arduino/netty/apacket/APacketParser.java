package gov.ismonnet.arduino.netty.apacket;

import gov.ismonnet.shared.netty.CustomByteBuf;


public interface APacketParser {
    APacket parse(CustomByteBuf buf) throws Exception;
}
