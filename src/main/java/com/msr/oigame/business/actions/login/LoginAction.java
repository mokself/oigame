package com.msr.oigame.business.actions.login;

import com.msr.oigame.business.entity.User;
import com.msr.oigame.business.repository.UserRepository;
import com.msr.oigame.core.protocol.MessageCmdCode;
import com.msr.oigame.core.skeleton.FlowContext;
import com.msr.oigame.core.skeleton.annotation.Action;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LoginAction {
    private final UserRepository userRepo;

    @Action(MessageCmdCode.login)
    public User login(FlowContext ctx, String key) {
        User user = userRepo.findByKey(key);
        if (user == null) {
            user = new User();
            user.setKey(key);
        }
        user.setLastLoginIp(ctx.getUserSession().getIp());
        userRepo.save(user);
        ctx.login(user.getId());
        return user;
    }
}
