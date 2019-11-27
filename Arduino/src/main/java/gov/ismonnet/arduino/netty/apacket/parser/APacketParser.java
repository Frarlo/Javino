package gov.ismonnet.arduino.netty.apacket.parser;

import gov.ismonnet.shared.CustomByteBuf;
import gov.ismonnet.arduino.netty.apacket.APacket;


public interface APacketParser {
    APacket parse(CustomByteBuf buf) throws Exception;
}
