package com.msr.oigame.core.skeleton.annotation;

import java.lang.annotation.*;

/**
 * 声明一个Action，Action可以处理指定的cmd指令
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Action {
    int value();
}
