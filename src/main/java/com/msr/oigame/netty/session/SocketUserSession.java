package com.msr.oigame.netty.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class SocketUserSession extends AbstractUserSession {

    public SocketUserSession(Channel channel) {
        super(channel);
    }

    public ChannelFuture writeAndFlush(Object message) {
        return this.channel.writeAndFlush(message);
    }
}
