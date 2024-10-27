package com.msr.oigame.core.skeleton;

import java.lang.reflect.Method;

public record ActionCommand(int cmd, Object target, Method method) {
}
