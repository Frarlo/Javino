package gov.ismonnet.arduino.serial;

import gov.ismonnet.arduino.netty.apacket.APacket;
import gov.ismonnet.arduino.netty.apacket.impl.PressButtonPacket;
import gov.ismonnet.arduino.netty.cpacket.CPacket;
import gov.ismonnet.arduino.netty.cpacket.impl.LedPacket;
import gov.ismonnet.shared.Commands;

public class PacketsToCommandsConverterImpl implements PacketsToCommandsConverter {

    @Override
    public Commands convert(APacket packet) {
        if(packet instanceof PressButtonPacket)
            return Commands.PRESS_BUTTON;
        throw new AssertionError("Packet -> command conversion not implemented (" + packet + ")");
    }

    @Override
    public CPacket convert(Commands commands) {
        if(commands == Commands.TURN_OFF_LED)
            return new LedPacket(false);
        if(commands == Commands.TURN_ON_LED)
            return new LedPacket(true);
        throw new AssertionError("Command -> packet conversion not implemented (" + commands + ")");
    }
}
