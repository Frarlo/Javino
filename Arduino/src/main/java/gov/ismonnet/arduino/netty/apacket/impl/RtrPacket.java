package gov.ismonnet.arduino.netty.apacket.impl;

import gov.ismonnet.shared.netty.CustomByteBuf;
import gov.ismonnet.arduino.netty.apacket.APacket;
import gov.ismonnet.arduino.netty.apacket.parser.APacketParser;

public class RtrPacket implements APacket {

    public static final byte ID = 0;

    private RtrPacket() {}

    public static final APacketParser PARSER = (CustomByteBuf buf) -> new RtrPacket();
}
