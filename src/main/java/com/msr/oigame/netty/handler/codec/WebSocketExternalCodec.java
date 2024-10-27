package com.msr.oigame.netty.handler.codec;

import com.msr.oigame.core.codec.DataCodec;
import com.msr.oigame.core.protocol.BaseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

public class WebSocketExternalCodec extends MessageToMessageCodec<BinaryWebSocketFrame, BaseMessage> {

    protected void encode(ChannelHandlerContext ctx, BaseMessage message, List<Object> out) {
        ByteBuf byteBuf = message.getData();
        BinaryWebSocketFrame socketFrame = new BinaryWebSocketFrame(byteBuf);
        out.add(socketFrame);
    }

    protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame binary, List<Object> out) {
        BaseMessage message = DataCodec.decode(binary.content());
        out.add(message);
    }
}
