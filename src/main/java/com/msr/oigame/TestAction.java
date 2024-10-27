package com.msr.oigame;

import com.msr.oigame.core.skeleton.annotation.Action;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TestAction {

    @Transactional
    @Action(2)
    public void test1() {

    }

    @Action(1)
    public int test2() {
        return 0;
    }
}
