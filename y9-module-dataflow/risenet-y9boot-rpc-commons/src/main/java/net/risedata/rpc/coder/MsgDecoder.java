package net.risedata.rpc.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import net.risedata.rpc.model.Msg;

import java.util.List;

/**
 * 将接收到的消息进行解码
 */
@ChannelHandler.Sharable
public class MsgDecoder extends ReplayingDecoder<Msg> {
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
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        byte[] content = new byte[in.readInt()];
        in.readBytes(content);
        out.add(new Msg(content.length, content));

    }


}
