package com.msr.oigame.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LogoutEvent extends ApplicationEvent {
    private final long userId;
    public LogoutEvent(long userId) {
        super(userId);
        this.userId = userId;
    }
}
