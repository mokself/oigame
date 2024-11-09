package com.msr.oigame.core.skeleton;

import com.msr.oigame.core.protocol.BaseMessage;
import com.msr.oigame.netty.session.SocketUserSession;
import com.msr.oigame.netty.session.UserSessionManager;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FlowContext {
    private final SocketUserSession userSession;
    private final ActionCommand actionCommand;
    private final BaseMessage msg;

    public void login(long userId) {
        UserSessionManager.settingUserId(userSession, userId);
    }
}
