package gov.ismonnet.shared.netty.charstuffing;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;

import java.util.List;
import java.util.StringJoiner;

public class CharStuffingDecoder extends ByteToMessageDecoder {

    private static final char[] HEX_CODE = "0123456789ABCDEF".toCharArray();

    private final byte dle;
    private final byte stx;
    private final byte etx;

    private boolean isEscaped;

    private boolean isReadingFrame;
    private ByteBuf frame;

    public CharStuffingDecoder() {
        this((byte) 10, (byte) 2, (byte) 3);
    }

    public CharStuffingDecoder(byte dle, byte stx, byte etx) {
        this.dle = dle;
        this.stx = stx;
        this.etx = etx;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] bytes = new byte[in.readableBytes()];
        in.getBytes(in.readerIndex(), bytes, 0, in.readableBytes());

        while(in.isReadable()) {
            final byte b = in.readByte();

            if(!isEscaped) {
                if(b == dle)
                    isEscaped = true;
                else if(isReadingFrame)
                    frame.writeByte(b);
            } else /*if (isEscaped)*/ {
                isEscaped = false;

                if(b == stx && !isReadingFrame) {
                    // Start of the packet
                    isReadingFrame = true;
                    frame = Unpooled.buffer();
                } else if(b == dle && isReadingFrame) {
                    // It's escaped, so unwrap it
                    frame.writeByte(b);
                } else if(b == etx && isReadingFrame) {
                    // End of the packet
                    out.add(frame);
                    isReadingFrame = false;
                    frame = null;
                } else {
                    // We are just going to log it and not throw an exception
                    // as the decoder can easily discard the data and go on
                    isReadingFrame = false;
                    frame = null;

                    throw new DecoderException("Coudln't frame packet (" + in.readerIndex() + ") " + getHex(bytes));
                }
            }
        }
    }

    private static String getHex(byte[] data) {
        final StringJoiner sj = new StringJoiner(", ");
        for (byte b : data)
            sj.add(getHex(b));
        return sj.toString();
    }

    private static String getHex(byte b) {
        return "0x" + HEX_CODE[(b >> 4) & 0xF] + HEX_CODE[(b & 0xF)];
    }
}
