package com.msr.oigame.netty.handler;

import com.msr.oigame.core.codec.ExternalMessageCodec;
import com.msr.oigame.core.protocol.BaseMessage;
import com.msr.oigame.core.protocol.ExternalMessage;
import com.msr.oigame.core.protocol.GameErrEnum;
import com.msr.oigame.netty.session.AbstractUserSession;
import com.msr.oigame.netty.session.UserSessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * 认证处理器
 */
@ChannelHandler.Sharable
public class AccessAuthHandler extends SimpleChannelInboundHandler<BaseMessage> {
    public static final AccessAuthHandler INSTANCE = new AccessAuthHandler();

    private AccessAuthHandler() {}

    /**
     * 忽略认证的命令
     */
    final Set<Integer> ignoreCmds = new HashSet<>();

    /**
     * 拒绝访问的命令
     */
    final Set<Integer> rejectCmds = new HashSet<>();

    public void addIgnoreCmd(int cmd) {
        ignoreCmds.add(cmd);
    }

    public void addRejectCmd(int cmd) {
        rejectCmds.add(cmd);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseMessage msg) throws Exception {
        int cmd = msg.getCmd();
        if (rejectCmds.contains(cmd)) {
            ExternalMessage rejectMessage = ExternalMessageCodec.encodeMsg(cmd, GameErrEnum.CMD_ERROR.getCode());
            ctx.writeAndFlush(rejectMessage);
            return;
        }

        if (!ignoreCmds.contains(cmd)) {
            // 访问了需要登录才能访问的 action
            AbstractUserSession userSession = UserSessionManager.getUserSession(ctx);
            if (userSession.getUserId() == null) {
                ExternalMessage rejectMessage = ExternalMessageCodec.encodeMsg(cmd, GameErrEnum.VERIFY_IDENTITY_FAILED.getCode());
                ctx.writeAndFlush(rejectMessage);
                return;
            }
        }

        // 交给下一个业务处理 (handler) , 下一个业务指的是你编排 handler 时的顺序
        ctx.fireChannelRead(msg);
    }
}
