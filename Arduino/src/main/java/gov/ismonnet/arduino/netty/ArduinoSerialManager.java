package gov.ismonnet.arduino.netty;

import gov.ismonnet.arduino.netty.apacket.APacket;
import gov.ismonnet.arduino.netty.apacket.impl.PrintPacket;
import gov.ismonnet.arduino.netty.apacket.impl.RtrPacket;
import gov.ismonnet.arduino.netty.apacket.parser.APacketParser;
import gov.ismonnet.arduino.netty.cpacket.CPacket;
import gov.ismonnet.arduino.netty.cpacket.CPacketEncoder;
import gov.ismonnet.arduino.netty.cpacket.impl.EndCommPacket;
import gov.ismonnet.arduino.netty.cpacket.impl.StartCommPacket;
import gov.ismonnet.arduino.netty.cpacket.impl.StopReadingPacket;
import gov.ismonnet.arduino.netty.purejavacomm.PureJavaCommChannel;
import gov.ismonnet.arduino.netty.purejavacomm.PureJavaCommChannelConfig;
import gov.ismonnet.arduino.netty.purejavacomm.PureJavaCommChannelOption;
import gov.ismonnet.arduino.netty.purejavacomm.PureJavaCommDeviceAddress;
import gov.ismonnet.shared.CustomByteBuf;
import gov.ismonnet.shared.netty.charstuffing.CharStuffingDecoder;
import gov.ismonnet.shared.netty.charstuffing.CharStuffingEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ArduinoSerialManager {

    private static final Logger LOGGER = LogManager.getLogger(ArduinoSerialManager.class);
    private static final Map<Byte, APacketParser> PACKET_PARSERS;

    static {
        final Map<Byte, APacketParser> temp = new HashMap<>();
        temp.put(RtrPacket.ID, RtrPacket.PARSER);
        temp.put(PrintPacket.ID, PrintPacket.PARSER);
        PACKET_PARSERS = Collections.unmodifiableMap(temp);
    }

    private final String port;
    private final Bootstrap bootstrap;

    private EventLoopGroup group;
    private ChannelFuture future;

    private final Map<CPacket, ChannelPromise> storedPackets;

    public ArduinoSerialManager(final String port) {
        this.port = port;

        storedPackets = new ConcurrentHashMap<>();
        bootstrap = new Bootstrap()
                .channel(PureJavaCommChannel.class)
                .option(PureJavaCommChannelOption.BAUD_RATE, PureJavaCommChannelConfig.Baudrate.B115200)
                .option(PureJavaCommChannelOption.WAIT_TIME, 500)
                .handler(new ArduinoChannelHandler());
    }

    public void start() {
        group = new OioEventLoopGroup();
        future = bootstrap
                .group(group)
                .connect(new PureJavaCommDeviceAddress(port))
                .syncUninterruptibly();

        sendPacketDirect(new StartCommPacket()).syncUninterruptibly();
        sendPacketDirect(new StopReadingPacket()).syncUninterruptibly();
    }

    public void stop() {
        sendPacket(new EndCommPacket()).syncUninterruptibly();
        future.channel().close().syncUninterruptibly();
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

                                if(packet instanceof RtrPacket)
                                    sendStoredPackets();
                                // else
                                // TODO: we got the packet. And now?
                            } catch(Exception e) {
                                LOGGER.error("Couldn't decode packet with ID {}", packetId, e);
                            }
                        }
                    }
            );
        }
    }
}
