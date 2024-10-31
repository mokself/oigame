package com.msr.oigame.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LoginEvent extends ApplicationEvent {
    private final long userId;
    public LoginEvent(long userId) {
        super(userId);
        this.userId = userId;
    }
}
