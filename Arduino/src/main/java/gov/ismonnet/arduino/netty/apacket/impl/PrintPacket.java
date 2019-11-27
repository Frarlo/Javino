package gov.ismonnet.arduino.netty.apacket.impl;

import gov.ismonnet.shared.netty.CustomByteBuf;
import gov.ismonnet.arduino.netty.apacket.APacket;
import gov.ismonnet.arduino.netty.apacket.parser.APacketParser;

import java.nio.charset.StandardCharsets;

public class PrintPacket implements APacket {

    public static final byte ID = 1;

    private final String toPrint;

    private PrintPacket(String toPrint) {
        this.toPrint = toPrint;
        //TODO: remove debug statement
        System.out.println("[Arduino] " + toPrint);
    }

    public String getToPrint() {
        return toPrint;
    }

    public static final APacketParser PARSER = (CustomByteBuf buf) -> {
        final String msg = buf.toString(StandardCharsets.UTF_8);
        return new PrintPacket(msg);
    };
}
