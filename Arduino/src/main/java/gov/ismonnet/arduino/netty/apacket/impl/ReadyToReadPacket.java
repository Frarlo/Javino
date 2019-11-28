package gov.ismonnet.arduino.netty.apacket.impl;

import gov.ismonnet.arduino.netty.apacket.APacket;
import gov.ismonnet.arduino.netty.apacket.APacketParser;
import gov.ismonnet.shared.netty.CustomByteBuf;

public class ReadyToReadPacket implements APacket {

    public static final byte ID = 0;

    private ReadyToReadPacket() {}

    @Override
    public String toString() {
        return "ReadyToReadPacket{}";
    }

    public static final APacketParser PARSER = (CustomByteBuf buf) -> new ReadyToReadPacket();
}
