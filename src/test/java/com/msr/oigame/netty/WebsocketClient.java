package com.msr.oigame.netty;

import com.msr.oigame.core.protocol.BaseMessage;
import com.msr.oigame.netty.handler.codec.WebSocketMessageCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Slf4j
public class WebsocketClient {
    private final URI uri;
    private final WebsocketClientHandler websocketClientHandler;

    @Getter
    public Channel channel;

    private final List<ChannelHandler> customChannelHandlers = new ArrayList<>();

    public WebsocketClient(String host, int port) {
        this("ws://" + host + ":" + port);
    }

    public WebsocketClient(int port) {
        this("localhost", port);
    }

    public WebsocketClient(String url) {
        this(URI.create(url));
    }

    public WebsocketClient(URI uri) {
        this.uri = uri;
        this.websocketClientHandler = new WebsocketClientHandler(WebSocketClientHandshakerFactory.
                newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));
    }

    public WebsocketClient addChannelHandler(ChannelHandler handler) {
        customChannelHandlers.add(handler);
        return this;
    }

    public void addMsgListener(Consumer<BaseMessage> listener) {
        websocketClientHandler.addMsgListener(listener);
    }

    public CompletableFuture<BaseMessage> receive() {
        return websocketClientHandler.receive();
    }

    @SneakyThrows
    public void connect() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
         .channel(NioSocketChannel.class)
         .handler(new ChannelInitializer<SocketChannel>() {

             @Override
             public void initChannel(SocketChannel ch) throws Exception {
                 ch.pipeline().addLast(
                         new HttpClientCodec(),
                         new HttpObjectAggregator(65536),
                         new WebSocketMessageCodec(),                            // websocket编解码
                         websocketClientHandler
                 );
                for (ChannelHandler handler : customChannelHandlers) {
                    ch.pipeline().addLast(handler);
                }
             }
         });

        this.channel = bootstrap.connect(uri.getHost(), uri.getPort()).sync().channel();
        websocketClientHandler.handshakeFuture().sync();
    }

    public void send(Object message) {
        this.channel.writeAndFlush(message);
    }

    public void close() {
        this.channel.close();
    }
}
