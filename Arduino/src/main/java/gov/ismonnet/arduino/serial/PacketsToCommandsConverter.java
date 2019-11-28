package gov.ismonnet.arduino.serial;

import gov.ismonnet.arduino.netty.apacket.APacket;
import gov.ismonnet.arduino.netty.cpacket.CPacket;
import gov.ismonnet.shared.Commands;

public interface PacketsToCommandsConverter {

    Commands convert(APacket packet);

    CPacket convert(Commands commands);
}
