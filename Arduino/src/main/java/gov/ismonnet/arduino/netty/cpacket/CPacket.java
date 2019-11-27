package gov.ismonnet.arduino.netty.cpacket;

import gov.ismonnet.shared.CustomByteBuf;

public interface CPacket {
    byte getID();

    void writePacket(CustomByteBuf buf);
}
