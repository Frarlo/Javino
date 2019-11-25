package gov.ismonnet.arduino.netty.cpacket;

import gov.ismonnet.shared.CustomByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CPacketEncoder extends MessageToByteEncoder<CPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, CPacket msg, ByteBuf out) throws Exception {
        final CustomByteBuf out0 = new CustomByteBuf(out);

        out0.writeByte(msg.getID());
        msg.writePacket(out0);
    }
}
