package com.msr.oigame.netty.session;

import io.netty.channel.Channel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class AbstractUserSession {

    protected final Channel channel;

    @Setter(AccessLevel.PROTECTED)
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

    @Override
    public String toString() {
        String id;
        if (getUserId() == null) {
            id = "channelId: " + channel.id().asShortText();
        } else {
            id = "userId: " + getUserId();
        }
        return "UserSession{ " + id + " }";
    }
}
