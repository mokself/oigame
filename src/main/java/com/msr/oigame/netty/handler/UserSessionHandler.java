package com.msr.oigame.netty.handler;

import com.msr.oigame.netty.session.SocketUserSession;
import com.msr.oigame.netty.session.UserSessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class UserSessionHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        UserSessionManager.add(ctx);
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        SocketUserSession userSession = UserSessionManager.getUserSession(ctx);
        UserSessionManager.removeUserSession(userSession);
        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        SocketUserSession userSession = UserSessionManager.getUserSession(ctx);
        UserSessionManager.removeUserSession(userSession);
        ctx.fireExceptionCaught(cause);
    }
}
