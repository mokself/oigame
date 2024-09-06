package com.msr.oigame.netty;

import com.msr.oigame.netty.codec.WebSocketExternalCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.util.NetUtil;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
public class WebsocketServer {

    private Channel serverChannel;

    @PostConstruct
    public void onApplicationEvent() {
        Instant startTime = Instant.now();
        log.info("websocket服务启动中...");
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap server = new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                // 客户端保持活动连接
                .option(ChannelOption.SO_KEEPALIVE, true)
                /*
                 * BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，
                 * 用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，
                 * 使用默认值100
                 */
                .option(ChannelOption.SO_BACKLOG, 100)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_RCVBUF, 5120)
                .childOption(ChannelOption.SO_SNDBUF, 20480)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(@Nonnull SocketChannel ch) throws Exception {
                        WebSocketServerProtocolConfig config = WebSocketServerProtocolConfig.newBuilder()
//                                .websocketPath(CoreOption.websocketPath)
//                                .maxFramePayloadLength(CoreOption.packageMaxSize)
                                .checkStartsWith(true)
                                .allowExtensions(true)
                                .build();

                        ch.pipeline().addLast(
                                new HttpServerCodec(),
                                new HttpObjectAggregator(64 * 1024),
                                new WebSocketServerCompressionHandler(),
                                new WebSocketServerProtocolHandler(config),
                                new WebSocketExternalCodec()
                        );
                    }
                });

        try {
            serverChannel = server.bind(6000).sync().channel();
            Duration interval = Duration.between(startTime, Instant.now());
            log.info("netty服务启动完成, 连接地址: ws://{}:{} 耗时: {}ms", NetUtil.LOCALHOST.toString(), 6000, interval);

        } catch (InterruptedException e) {
            log.error("netty");
        }
    }

    @PreDestroy
    public void destroy() {
        serverChannel.close();
    }
}
