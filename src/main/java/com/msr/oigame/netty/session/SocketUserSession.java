package com.msr.oigame.netty.session;

import com.msr.oigame.core.protocol.BaseMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class SocketUserSession extends AbstractUserSession {

    public SocketUserSession(Channel channel) {
        super(channel);
    }

    public ChannelFuture writeAndFlush(BaseMessage message) {
        return this.channel.writeAndFlush(message);
    }
}
