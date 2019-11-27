package gov.ismonnet.arduino.netty.cpacket;

import gov.ismonnet.shared.netty.CustomByteBuf;

public interface CPacket {
    byte getID();

    void writePacket(CustomByteBuf buf);
}
