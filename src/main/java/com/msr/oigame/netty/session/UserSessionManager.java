package com.msr.oigame.netty.session;

import com.msr.oigame.common.event.LoginEvent;
import com.msr.oigame.common.event.LogoutEvent;
import com.msr.oigame.core.protocol.BaseMessage;
import com.msr.oigame.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.jctools.maps.NonBlockingHashMapLong;

public class UserSessionManager {
    /** 用户 session，与channel是 1:1 的关系 */
    private static final AttributeKey<SocketUserSession> userSessionKey = AttributeKey.valueOf("userSession");

    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * key : 玩家 id
     * value : UserSession
     */
    private static final NonBlockingHashMapLong<SocketUserSession> userIdMap = new NonBlockingHashMapLong<>();

    public static SocketUserSession add(ChannelHandlerContext channelHandlerContext) {
        Channel channel = channelHandlerContext.channel();
        SocketUserSession userSession = new SocketUserSession(channel);
        // channel 中也保存 UserSession 的引用
        channel.attr(userSessionKey).set(userSession);
        channelGroup.add(channel);

        return userSession;
    }

    public static SocketUserSession getUserSession(ChannelHandlerContext channelHandlerContext) {
        Channel channel = channelHandlerContext.channel();
        return channel.attr(userSessionKey).get();
    }

    public static SocketUserSession getUserSession(long userId) {
        return userIdMap.get(userId);
    }

    public static boolean settingUserId(SocketUserSession userSession, long userId) {
        if (userSession == null) {
            return false;
        }

        userSession.setUserId(userId);

        userIdMap.put(userId, userSession);

        // 发送登录事件
        SpringUtil.getContext().publishEvent(new LoginEvent(userId));

        return true;
    }

    public static void removeUserSession(SocketUserSession userSession) {
        if (userSession == null) {
            return;
        }

        Long userId = userSession.getUserId();
        if (userId != null) {
            // 发送离线事件
            SpringUtil.getContext().publishEvent(new LogoutEvent(userId));
            userIdMap.remove(userId);
        }

        Channel channel = userSession.getChannel();
        if (channel != null) {
            channelGroup.remove(channel);
            channel.close();
        }
    }

    public static int countOnline() {
        return channelGroup.size();
    }

    public static void broadcast(BaseMessage msg) {
        channelGroup.writeAndFlush(msg);
    }
}
