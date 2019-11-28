package gov.ismonnet.arduino.netty;

import gov.ismonnet.arduino.netty.apacket.APacket;
import gov.ismonnet.arduino.netty.apacket.APacketParser;
import gov.ismonnet.arduino.netty.apacket.impl.PressButtonPacket;
import gov.ismonnet.arduino.netty.apacket.impl.PrintPacket;
import gov.ismonnet.arduino.netty.apacket.impl.ReadyToReadPacket;
import gov.ismonnet.arduino.netty.cpacket.CPacket;
import gov.ismonnet.arduino.netty.cpacket.CPacketEncoder;
import gov.ismonnet.arduino.netty.cpacket.impl.EndCommPacket;
import gov.ismonnet.arduino.netty.cpacket.impl.StartCommPacket;
import gov.ismonnet.arduino.netty.cpacket.impl.StopReadingPacket;
import gov.ismonnet.arduino.netty.purejavacomm.PureJavaCommChannel;
import gov.ismonnet.arduino.netty.purejavacomm.PureJavaCommChannelConfig;
import gov.ismonnet.arduino.netty.purejavacomm.PureJavaCommChannelOption;
import gov.ismonnet.arduino.netty.purejavacomm.PureJavaCommDeviceAddress;
import gov.ismonnet.shared.netty.CustomByteBuf;
import gov.ismonnet.shared.netty.charstuffing.CharStuffingDecoder;
import gov.ismonnet.shared.netty.charstuffing.CharStuffingEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ArduinoSerialManager {

    // Constants

    private static final Logger LOGGER = LogManager.getLogger(ArduinoSerialManager.class);

    private static final int SHUTDOWN_TIMEOUT = 5000;
    private static final Map<Byte, APacketParser> PACKET_PARSERS;

    static {
        final Map<Byte, APacketParser> temp = new HashMap<>();
        temp.put(ReadyToReadPacket.ID, ReadyToReadPacket.PARSER);
        temp.put(PrintPacket.ID, PrintPacket.PARSER);
        temp.put(PressButtonPacket.ID, PressButtonPacket.PARSER);
        PACKET_PARSERS = Collections.unmodifiableMap(temp);
    }

    // Attributes

    private final String port;
    private final Bootstrap bootstrap;

    private EventLoopGroup group;
    private ChannelFuture future;

    private final Map<CPacket, ChannelPromise> storedPackets;
    private final Set<PacketListener> listeners;

    public ArduinoSerialManager(final String port) {
        this.port = port;

        storedPackets = new ConcurrentHashMap<>();
        bootstrap = new Bootstrap()
                .channel(PureJavaCommChannel.class)
                .option(PureJavaCommChannelOption.BAUD_RATE, PureJavaCommChannelConfig.Baudrate.B115200)
                .option(PureJavaCommChannelOption.WAIT_TIME, 500)
                .handler(new ArduinoChannelHandler());
        listeners = new HashSet<>();
    }

    public void start() {
        LOGGER.trace("Binding on serial port {}...", port);
        group = new OioEventLoopGroup();
        future = bootstrap
                .group(group)
                .connect(new PureJavaCommDeviceAddress(port))
                .syncUninterruptibly();

        LOGGER.trace("Sending StartComm packet...");
        sendPacketDirect(new StartCommPacket()).syncUninterruptibly();
        LOGGER.trace("Sending StopReading packet...");
        sendPacketDirect(new StopReadingPacket()).syncUninterruptibly();

        LOGGER.trace("Started Arduino Serial");
    }

    public void stop() {
        sendPacket(new EndCommPacket()).syncUninterruptibly();

        LOGGER.trace("Shutting down arduino serial");
        future.channel().close().awaitUninterruptibly(SHUTDOWN_TIMEOUT);
        group.shutdownGracefully();
    }

    public ChannelFuture sendPacket(CPacket msg) {
        final ChannelPromise promise = future.channel().newPromise();
        storedPackets.put(msg, promise);
        return promise;
    }

    private ChannelFuture sendPacketDirect(CPacket msg) {
        LOGGER.trace("Sending packet {}", msg);
        return future.channel().writeAndFlush(msg);
    }

    private void sendStoredPackets() {
        for(Map.Entry<CPacket, ChannelPromise> entry : storedPackets.entrySet()) {
            final CPacket toSend = entry.getKey();
            final ChannelPromise promise = entry.getValue();

            try {
                sendPacketDirect(toSend).syncUninterruptibly();
                promise.trySuccess();
            } catch (Throwable e) {
                promise.tryFailure(e);
            }
        }
        sendPacketDirect(new StopReadingPacket()).awaitUninterruptibly();
    }

    private class ArduinoChannelHandler extends ChannelInitializer<PureJavaCommChannel> {
        @Override
        public void initChannel(PureJavaCommChannel ch) {
            ch.pipeline().addLast(
                    new CharStuffingEncoder(),
                    new CPacketEncoder(),
                    new CharStuffingDecoder(),
                    new SimpleChannelInboundHandler<ByteBuf>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
                            final CustomByteBuf msg0 = new CustomByteBuf(msg);

                            final byte packetId = msg0.readByte();
                            final APacketParser packetParser = PACKET_PARSERS.get(packetId);

                            try {
                                if(packetParser == null)
                                    throw new RuntimeException("There is no parser for the given ID (" + packetId+ ')');
                                final APacket packet = packetParser.parse(msg0);
                                LOGGER.trace("Received packet {}", packet);

                                if(packet instanceof ReadyToReadPacket)
                                    sendStoredPackets();
                                 else
                                     fire(packet);
                            } catch(Exception e) {
                                LOGGER.error("Couldn't decode packet with ID {}", packetId, e);
                            }
                        }
                    }
            );
        }
    }

    public void register(PacketListener listener) {
        listeners.add(listener);
    }

    public void unregister(PacketListener listener) {
        listeners.remove(listener);
    }

    private void fire(APacket packet) {
        listeners.forEach(l -> l.receive(packet));
    }
}
