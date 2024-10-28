package com.msr.oigame.netty.handler.codec;

import com.msr.oigame.core.codec.DataCodec;
import com.msr.oigame.core.protocol.BaseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

public class WebSocketMessageCodec extends MessageToMessageCodec<BinaryWebSocketFrame, BaseMessage> {

    protected void encode(ChannelHandlerContext ctx, BaseMessage message, List<Object> out) {
        byte[] bytes = DataCodec.encode(message);
        ByteBuf byteBuf = ctx.alloc().buffer(bytes.length);
        byteBuf.writeBytes(bytes);
        BinaryWebSocketFrame socketFrame = new BinaryWebSocketFrame(byteBuf);
        out.add(socketFrame);
    }

    protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame binary, List<Object> out) {
        byte[] bytes = ByteBufUtil.getBytes(binary.content());
        BaseMessage message = DataCodec.decode(bytes, BaseMessage.class);
        out.add(message);
    }
}
