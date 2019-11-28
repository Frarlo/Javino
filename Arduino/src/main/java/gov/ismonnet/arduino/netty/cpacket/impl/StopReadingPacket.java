package gov.ismonnet.arduino.netty.cpacket.impl;

import gov.ismonnet.shared.netty.CustomByteBuf;
import gov.ismonnet.arduino.netty.cpacket.CPacket;

public class StopReadingPacket implements CPacket {

    public static final byte ID = 3;
    
    @Override
    public byte getID() {
        return ID;
    }

    @Override
    public void writePacket(CustomByteBuf buf) {
    }
}
