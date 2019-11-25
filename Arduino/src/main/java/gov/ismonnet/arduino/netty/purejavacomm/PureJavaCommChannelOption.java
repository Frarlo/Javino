package gov.ismonnet.arduino.netty.purejavacomm;

import gov.ismonnet.arduino.netty.purejavacomm.PureJavaCommChannelConfig.Baudrate;
import gov.ismonnet.arduino.netty.purejavacomm.PureJavaCommChannelConfig.Databits;
import gov.ismonnet.arduino.netty.purejavacomm.PureJavaCommChannelConfig.Paritybit;
import gov.ismonnet.arduino.netty.purejavacomm.PureJavaCommChannelConfig.Stopbits;
import io.netty.channel.ChannelOption;

/**
 * Option for configuring a serial port connection
 */
public final class PureJavaCommChannelOption<T> extends ChannelOption<T> {

    public static final ChannelOption<Baudrate> BAUD_RATE = valueOf(PureJavaCommChannelOption.class, "BAUD_RATE");
    public static final ChannelOption<Databits> DATA_BITS = valueOf(PureJavaCommChannelOption.class, "DATA_BITS");
    public static final ChannelOption<Stopbits> STOP_BITS = valueOf(PureJavaCommChannelOption.class, "STOP_BITS");
    public static final ChannelOption<Paritybit> PARITY_BIT = valueOf(PureJavaCommChannelOption.class, "PARITY_BIT");

    public static final ChannelOption<Integer> WAIT_TIME = valueOf(PureJavaCommChannelOption.class, "WAIT_TIME");
    public static final ChannelOption<Integer> READ_TIMEOUT = valueOf(PureJavaCommChannelOption.class, "READ_TIMEOUT");

    private PureJavaCommChannelOption() {
        super(null);
    }
}
