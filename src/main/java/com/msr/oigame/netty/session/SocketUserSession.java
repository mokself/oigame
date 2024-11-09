package com.msr.oigame.netty.session;

import com.msr.oigame.core.protocol.BaseMessage;
import com.msr.oigame.util.StrUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

public class SocketUserSession extends AbstractUserSession {
    public static final AttributeKey<String> realIpKey = AttributeKey.valueOf("realIp");

    public SocketUserSession(Channel channel) {
        super(channel);
    }

    public ChannelFuture writeAndFlush(BaseMessage message) {
        return this.channel.writeAndFlush(message);
    }

    public String getIp() {
        String realIp = getChannel().attr(realIpKey).get();
        if (StrUtil.isEmpty(realIp)) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress)this.channel.remoteAddress();
            return inetSocketAddress.getHostString();
        } else {
            return realIp;
        }
    }
}
