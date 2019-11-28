package gov.ismonnet.arduino.netty.apacket.impl;

import gov.ismonnet.arduino.netty.apacket.APacket;
import gov.ismonnet.arduino.netty.apacket.APacketParser;
import gov.ismonnet.shared.netty.CustomByteBuf;

public class PressButtonPacket implements APacket {

    public static final byte ID = 2;

    private PressButtonPacket() {}

    public static final APacketParser PARSER = (CustomByteBuf buf) -> new PressButtonPacket();
}
