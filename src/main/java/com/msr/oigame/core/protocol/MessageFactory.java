package com.msr.oigame.core.protocol;

public class MessageFactory {
    public static final byte[] EMPTY_DATA = null;

    public static BaseMessage createMessage(int cmd, Object data) {
        return new BaseMessage(cmd, 0, data);
    }

    public static BaseMessage createIdleMessage() {
        return new BaseMessage(MessageCmdCode.idle, 0, EMPTY_DATA);
    }
    public static BaseMessage employError(BaseMessage msg, GameErrEnum err) {
        return new BaseMessage(msg.cmd(), err.getCode(), EMPTY_DATA);
    }

    public static BaseMessage employError(int cmd, GameErrEnum err) {
        return new BaseMessage(cmd, err.getCode(), EMPTY_DATA);
    }
}
