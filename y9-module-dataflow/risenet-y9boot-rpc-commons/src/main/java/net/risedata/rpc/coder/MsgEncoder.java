package net.risedata.rpc.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.risedata.rpc.model.Msg;

import java.io.Serializable;

/**
 * 对于发出的消息进行编码
 */
@ChannelHandler.Sharable
public class MsgEncoder extends MessageToByteEncoder<Msg> implements Serializable {
    private boolean aBoolean = false;

    @Override
    public boolean isSharable() {
        if (!aBoolean) {
            aBoolean = true;
            return false;
        }
        return true;
    }
    @Override
    protected void encode(ChannelHandlerContext ctx, Msg msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getLength());
        out.writeBytes(msg.getMsg());
    }
}
