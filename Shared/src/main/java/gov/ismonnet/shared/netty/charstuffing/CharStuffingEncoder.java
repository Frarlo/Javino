package gov.ismonnet.shared.netty.charstuffing;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CharStuffingEncoder extends MessageToByteEncoder<ByteBuf> {
    private final byte dle;
    private final byte stx;
    private final byte etx;

    public CharStuffingEncoder() {
        this((byte) 10, (byte) 2, (byte) 3);
    }

    public CharStuffingEncoder(byte dle, byte stx, byte etx) {
        this.dle = dle;
        this.stx = stx;
        this.etx = etx;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        out.writeByte(dle);
        out.writeByte(stx);

        while (msg.isReadable()) {
            final byte b = msg.readByte();
            if(b == dle)
                out.writeByte(dle);
            out.writeByte(b);
        }

        out.writeByte(dle);
        out.writeByte(etx);
    }
}
