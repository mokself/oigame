package com.msr.oigame.netty.session;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class AbstractUserSession {

    protected final Channel channel;

    @Setter
    private Long userId;

    public AbstractUserSession(Channel channel) {
        this.channel = channel;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof AbstractUserSession)) {
            return false;
        }

        return channel == object;
    }

    @Override
    public int hashCode() {
        return channel.hashCode();
    }
}
