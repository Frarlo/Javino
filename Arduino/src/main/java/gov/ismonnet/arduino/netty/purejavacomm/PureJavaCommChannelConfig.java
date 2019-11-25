package gov.ismonnet.arduino.netty.purejavacomm;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import purejavacomm.SerialPort;

/**
 * A configuration class for JavaSerial device connections.
 *
 * <h3>Available options</h3>
 *
 * In addition to the options provided by {@link ChannelConfig},
 * {@link DefaultPureJavaCommChannelConfig} allows the following options in the option map:
 *
 * <table border="1" cellspacing="0" cellpadding="6">
 * <tr>
 * <th>Name</th><th>Associated setter method</th>
 * </tr><tr>
 * <td>{@link PureJavaCommChannelOption#BAUD_RATE}</td><td>{@link #setBaudrate(Baudrate)}</td>
 * </tr><tr>
 * <td>{@link PureJavaCommChannelOption#STOP_BITS}</td><td>{@link #setStopbits(Stopbits)}</td>
 * </tr><tr>
 * <td>{@link PureJavaCommChannelOption#DATA_BITS}</td><td>{@link #setDatabits(Databits)}</td>
 * </tr><tr>
 * <td>{@link PureJavaCommChannelOption#PARITY_BIT}</td><td>{@link #setParitybit(Paritybit)}</td>
 * </tr><tr>
 * <td>{@link PureJavaCommChannelOption#WAIT_TIME}</td><td>{@link #setWaitTimeMillis(int)}</td>
 * </tr>
 * </table>
 */
public interface PureJavaCommChannelConfig extends ChannelConfig {
    enum Baudrate {
        /**
         * Not available in Windows
         */
        B0(0),
        /**
         * Not available in Windows
         */
        B50(50),
        /**
         * Not available in Windows
         */
        B75(75),
        B110(110),
        /**
         * Not available in Windows
         */
        B134(134),
        /**
         * Not available in Windows
         */
        B150(150),
        B200(200),
        B300(300),
        B600(600),
        B1200(1200),
        /**
         * Not available in Windows
         */
        B1800(1800),
        B2400(2400),
        B4800(4800),
        B9600(9600),
        B19200(19200),
        B38400(38400),
        B115200(115200)
        ;

        private final int value;

        Baudrate(int value){
            this.value = value;
        }

        public int value(){
            return value;
        }

        public static Baudrate valueOf(int value) {
            for (Baudrate baudrate : Baudrate.values()) {
                if (baudrate.value == value) {
                    return baudrate;
                }
            }
            throw new IllegalArgumentException("unknown " + Baudrate.class.getSimpleName() + " value: " + value);
        }
    }

    enum Stopbits {
        /**
         * 1 stop bit will be sent at the end of every character
         */
        STOPBITS_1(SerialPort.STOPBITS_1),
        /**
         * 2 stop bits will be sent at the end of every character
         */
        STOPBITS_2(SerialPort.STOPBITS_2),
        /**
         * 1.5 stop bits will be sent at the end of every character
         */
        STOPBITS_1_5(SerialPort.STOPBITS_1_5);

        private final int value;

        Stopbits(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        public static Stopbits valueOf(int value) {
            for (Stopbits stopbit : Stopbits.values()) {
                if (stopbit.value == value) {
                    return stopbit;
                }
            }
            throw new IllegalArgumentException("unknown " + Stopbits.class.getSimpleName() + " value: " + value);
        }
    }

    enum Databits {
        /**
         * 5 data bits will be used for each character (ie. Baudot code)
         */
        DATABITS_5(SerialPort.DATABITS_5),
        /**
         * 6 data bits will be used for each character
         */
        DATABITS_6(SerialPort.DATABITS_6),
        /**
         * 7 data bits will be used for each character (ie. ASCII)
         */
        DATABITS_7(SerialPort.DATABITS_7),
        /**
         * 8 data bits will be used for each character (ie. binary data)
         */
        DATABITS_8(SerialPort.DATABITS_8);

        private final int value;

        Databits(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        public static Databits valueOf(int value) {
            for (Databits databit : Databits.values()) {
                if (databit.value == value) {
                    return databit;
                }
            }
            throw new IllegalArgumentException("unknown " + Databits.class.getSimpleName() + " value: " + value);
        }
    }

    enum Paritybit {
        /**
         * No parity bit will be sent with each data character at all
         */
        NONE(SerialPort.PARITY_NONE),
        /**
         * An odd parity bit will be sent with each data character, ie. will be set
         * to 1 if the data character contains an even number of bits set to 1.
         */
        ODD(SerialPort.PARITY_ODD),
        /**
         * An even parity bit will be sent with each data character, ie. will be set
         * to 1 if the data character contains an odd number of bits set to 1.
         */
        EVEN(SerialPort.PARITY_EVEN),
        /**
         * A mark parity bit (ie. always 1) will be sent with each data character
         */
        MARK(SerialPort.PARITY_MARK),
        /**
         * A space parity bit (ie. always 0) will be sent with each data character
         */
        SPACE(SerialPort.PARITY_SPACE);

        private final int value;

        Paritybit(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        public static Paritybit valueOf(int value) {
            for (Paritybit paritybit : Paritybit.values()) {
                if (paritybit.value == value) {
                    return paritybit;
                }
            }
            throw new IllegalArgumentException("unknown " + Paritybit.class.getSimpleName() + " value: " + value);
        }
    }
    /**
     * Sets the baud rate (ie. bits per second) for communication with the serial device.
     * The baud rate will include bits for framing (in the form of stop bits and parity),
     * such that the effective data rate will be lower than this value.
     *
     * @param baudrate The baud rate (in bits per second)
     */
    PureJavaCommChannelConfig setBaudrate(Baudrate baudrate);

    /**
     * Sets the number of stop bits to include at the end of every character to aid the
     * serial device in synchronising with the data.
     *
     * @param stopbits The number of stop bits to use
     */
    PureJavaCommChannelConfig setStopbits(Stopbits stopbits);

    /**
     * Sets the number of data bits to use to make up each character sent to the serial
     * device.
     *
     * @param databits The number of data bits to use
     */
    PureJavaCommChannelConfig setDatabits(Databits databits);

    /**
     * Sets the type of parity bit to be used when communicating with the serial device.
     *
     * @param paritybit The type of parity bit to be used
     */
    PureJavaCommChannelConfig setParitybit(Paritybit paritybit);

    /**
     * @return The configured baud rate, defaulting to 115200 if unset
     */
    Baudrate getBaudrate();

    /**
     * @return The configured stop bits, defaulting to {@link Stopbits#STOPBITS_1} if unset
     */
    Stopbits getStopbits();

    /**
     * @return The configured data bits, defaulting to {@link Databits#DATABITS_8} if unset
     */
    Databits getDatabits();

    /**
     * @return The configured parity bit, defaulting to {@link Paritybit#NONE} if unset
     */
    Paritybit getParitybit();

    /**
     * @return The number of milliseconds to wait between opening the serial port and
     *     initialising.
     */
    int getWaitTimeMillis();

    /**
     * Sets the time to wait after opening the serial port and before sending it any
     * configuration information or data. A value of 0 indicates that no waiting should
     * occur.
     *
     * @param waitTimeMillis The number of milliseconds to wait, defaulting to 0 (no
     *     wait) if unset
     * @throws IllegalArgumentException if the supplied value is &lt; 0
     */
    PureJavaCommChannelConfig setWaitTimeMillis(int waitTimeMillis);

    /**
     * Sets the maximal time (in ms) to block while try to read from the serial port. Default is 1000ms
     */
    PureJavaCommChannelConfig setReadTimeout(int readTimeout);

    /**
     * Return the maximal time (in ms) to block and wait for something to be ready to read.
     */
    int getReadTimeout();

    @Override
    PureJavaCommChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis);

    @Override
    @Deprecated
    PureJavaCommChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead);

    @Override
    PureJavaCommChannelConfig setWriteSpinCount(int writeSpinCount);

    @Override
    PureJavaCommChannelConfig setAllocator(ByteBufAllocator allocator);

    @Override
    PureJavaCommChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator);

    @Override
    PureJavaCommChannelConfig setAutoRead(boolean autoRead);

    @Override
    PureJavaCommChannelConfig setAutoClose(boolean autoClose);

    @Override
    PureJavaCommChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark);

    @Override
    PureJavaCommChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark);

    @Override
    PureJavaCommChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark);

    @Override
    PureJavaCommChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator);

}
