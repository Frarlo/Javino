package gov.ismonnet.arduino.netty.cpacket.impl;

import gov.ismonnet.arduino.netty.cpacket.CPacket;
import gov.ismonnet.shared.netty.CustomByteBuf;

public class LedPacket implements CPacket {

    public static final byte ID = 4;

    private final boolean state;

    public LedPacket(boolean state) {
        this.state = state;
    }

    public boolean isState() {
        return state;
    }

    @Override
    public byte getID() {
        return ID;
    }

    @Override
    public void writePacket(CustomByteBuf buf) {
        buf.writeBoolean(state);
    }

    @Override
    public String toString() {
        return "LedPacket{" +
                "state=" + state +
                '}';
    }
}
