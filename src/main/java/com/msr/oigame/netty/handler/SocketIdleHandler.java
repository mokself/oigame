package com.msr.oigame.netty.handler;

import com.msr.oigame.config.ServerProperties;
import com.msr.oigame.core.protocol.*;
import com.msr.oigame.netty.session.SocketUserSession;
import com.msr.oigame.netty.session.UserSessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;

/**
 * 心跳处理器
 */
@Slf4j
@ChannelHandler.Sharable
public class SocketIdleHandler extends ChannelInboundHandlerAdapter {

    private final boolean pong;

    public SocketIdleHandler(ServerProperties serverProperties) {
        this.pong = serverProperties.getIdle().isPong();
    }

    @Override
    public void channelRead(@Nonnull ChannelHandlerContext ctx, @Nonnull Object msg) throws Exception {
        BaseMessage message = (BaseMessage) msg;

        int cmd = message.cmd();
        if (cmd != MessageCmdCode.idle) {
            // 不是心跳请求. 交给下一个业务处理 (handler) , 下一个业务指的是你编排 handler 时的顺序
            ctx.fireChannelRead(msg);
            return;
        }

        if (this.pong) {
            BaseMessage idleMessage = MessageFactory.createIdleMessage();
            ctx.writeAndFlush(idleMessage);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent event) {
            SocketUserSession userSession = UserSessionManager.getUserSession(ctx);

            if (userSession != null) {
                IdleState state = event.state();
                if (state == IdleState.READER_IDLE) {
                    /* 读超时 */
                    log.debug("READER_IDLE 读超时");
                } else if (state == IdleState.WRITER_IDLE) {
                    /* 写超时 */
                    log.debug("WRITER_IDLE 写超时");
                } else if (state == IdleState.ALL_IDLE) {
                    /* 总超时 */
                    log.debug("ALL_IDLE 总超时");
                }
                // 发送心跳超时消息
                BaseMessage message = MessageFactory.employError(MessageCmdCode.idle, GameErrEnum.IDLE_TIMEOUT);
                userSession.writeAndFlush(message);
                // 关闭会话
                UserSessionManager.removeUserSession(userSession);
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
