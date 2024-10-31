package com.msr.oigame.business.listener;

import com.msr.oigame.common.event.LoginEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoginListener {

    @EventListener
    public void onLogin(LoginEvent event) {
        log.info("用户登录成功, userId: {}", event.getUserId());
    }
}
