package gov.ismonnet.arduino.netty.cpacket.impl;

import gov.ismonnet.arduino.netty.cpacket.CPacket;
import gov.ismonnet.shared.netty.CustomByteBuf;

public class StartCommPacket implements CPacket {

    public static final byte ID = 0;

    @Override
    public byte getID() {
        return ID;
    }

    @Override
    public void writePacket(CustomByteBuf buf) {
    }

    @Override
    public String toString() {
        return "StartCommPacket{}";
    }
}
