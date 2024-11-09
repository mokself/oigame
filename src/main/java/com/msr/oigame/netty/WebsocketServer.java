package com.msr.oigame.netty;

import com.msr.oigame.config.ServerConfig;
import com.msr.oigame.netty.handler.*;
import com.msr.oigame.netty.handler.codec.WebSocketMessageCodec;
import com.msr.oigame.netty.loopgroup.GroupChannelOption;
import com.msr.oigame.netty.loopgroup.GroupChannelOptionForLinux;
import com.msr.oigame.netty.loopgroup.GroupChannelOptionForMac;
import com.msr.oigame.netty.loopgroup.GroupChannelOptionForOther;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.NetUtil;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebsocketServer implements CommandLineRunner, DisposableBean {
    private final ServerConfig serverConfig;
    private final ActionCommandHandler actionCommandHandler;

    private Channel serverChannel;

    @Override
    public void run(String... args) {
        new Thread(this::start).start();
    }

    @Override
    public void destroy() {
        serverChannel.close();
        log.info("netty服务已停止");
    }

    public void start() {
        Instant startTime = Instant.now();
        log.info("websocket服务启动中...");
        GroupChannelOption groupChannelOption = createGroupChannelOption();
        EventLoopGroup boss = groupChannelOption.bossGroup();
        EventLoopGroup worker = groupChannelOption.workerGroup();
        Class<? extends ServerChannel> channelClass = groupChannelOption.channelClass();

        ServerBootstrap server = new ServerBootstrap()
                .group(boss, worker)
                .channel(channelClass)
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
                    protected void initChannel(@Nonnull SocketChannel ch) {
                        WebSocketServerProtocolConfig config = WebSocketServerProtocolConfig.newBuilder()
                                .websocketPath(serverConfig.getWebsocketPath())
                                .maxFramePayloadLength(1024 * 1024)
                                .checkStartsWith(true)
                                .allowExtensions(true)
                                .build();
                        ServerConfig.Idle propertiesIdle = serverConfig.getIdle();

                        ch.pipeline().addLast(
                                new HttpServerCodec(),                                  // http编解码器
                                new HttpObjectAggregator(64 * 1024),    // http消息聚合器
                                new WebSocketServerCompressionHandler(),                // websocket数据压缩
                                new WebSocketServerProtocolHandler(config),             // websocket协议处理器
                                new WebSocketMessageCodec(),                            // websocket编解码
                                new UserSessionHandler(),                               // 会话处理
                                new HttpRealIpHandler(),                                // 获取真实ip
                                new IdleStateHandler(                                   // netty心跳检测
                                        propertiesIdle.getReaderIdleTime(),
                                        propertiesIdle.getWriterIdleTime(),
                                        propertiesIdle.getAllIdleTime(),
                                        propertiesIdle.getTimeUnit()
                                ),
                                new SocketIdleHandler(serverConfig),                    // 心跳处理
                                AccessAuthHandler.INSTANCE,                             // 认证处理
                                actionCommandHandler                                    // 业务处理
                        );
                    }
                });

        try {
            serverChannel = server.bind(serverConfig.getPort()).sync().channel();
            Duration interval = Duration.between(startTime, Instant.now());
            log.info("netty服务启动完成, ws://{}{}:{} 耗时: {}ms", NetUtil.LOCALHOST4.getHostAddress(), serverConfig.getWebsocketPath(), serverConfig.getPort(), interval.toMillis());
            serverChannel.closeFuture().sync();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    private GroupChannelOption createGroupChannelOption() {
        // 根据环境自动选择，开发者也可以重写此方法，做些自定义
        GroupChannelOption groupChannelOption;

        // 根据系统内核来优化
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains("linux")) {
            // linux
            groupChannelOption = new GroupChannelOptionForLinux();
        } else if (osName.toLowerCase().contains("mac")) {
            // mac
            groupChannelOption = new GroupChannelOptionForMac();
        } else {
            // other system
            groupChannelOption = new GroupChannelOptionForOther();
        }

        return groupChannelOption;
    }
}
