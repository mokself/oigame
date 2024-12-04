package com.msr.oigame.netty;

import com.msr.oigame.core.protocol.BaseMessage;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class WebsocketClientHandler extends ChannelInboundHandlerAdapter {
    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;

    private final List<Consumer<BaseMessage>> msgListeners = new ArrayList<>();
    private final Queue<CompletableFuture<BaseMessage>> msgFutures = new ArrayDeque<>();

    public WebsocketClientHandler(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(ctx.channel(), (FullHttpResponse) msg);
            handshakeFuture.setSuccess();
            return;
        }
        if (msg instanceof BaseMessage) {
            msgListeners.forEach(listener -> listener.accept((BaseMessage) msg));
            while (!msgFutures.isEmpty()) {
                msgFutures.poll().complete((BaseMessage) msg);
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        handshaker.handshake(ctx.channel());
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    public void addMsgListener(Consumer<BaseMessage> listener) {
        msgListeners.add(listener);
    }

    public CompletableFuture<BaseMessage> receive() {
        CompletableFuture<BaseMessage> future = new CompletableFuture<>();
        msgFutures.add(future);
        return future;
    }
}
