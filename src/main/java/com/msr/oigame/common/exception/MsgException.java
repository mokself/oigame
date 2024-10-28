package com.msr.oigame.common.exception;

import com.msr.oigame.core.protocol.GameErrEnum;
import lombok.Getter;

/**
 * 协议异常消息
 */
@Getter
public class MsgException extends RuntimeException {
    final int msgCode;
    GameErrEnum gameErrEnum;

    public MsgException(int msgCode, String message) {
        super(message);
        this.msgCode = msgCode;
    }

    public MsgException(GameErrEnum gameErrEnum) {
        super(gameErrEnum.getMsg());
        this.msgCode = gameErrEnum.getCode();
        this.gameErrEnum = gameErrEnum;
    }
}
