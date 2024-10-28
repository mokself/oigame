package com.msr.oigame.core.protocol;

import com.msr.oigame.core.codec.DataCodec;

public class MessageFactory {
    public static final byte[] EMPTY_BYTES = new byte[0];

    public static BaseMessage createMessage(int cmd, Object data) {
        byte[] bytes = DataCodec.encode(data);
        return new BaseMessage(cmd, 0, bytes);
    }

    public static BaseMessage createIdleMessage() {
        return new BaseMessage(MessageCmdCode.idle, 0, EMPTY_BYTES);
    }
    public static BaseMessage employError(BaseMessage msg, GameErrEnum err) {
        return new BaseMessage(msg.cmd(), err.getCode(), EMPTY_BYTES);
    }

    public static BaseMessage employError(int cmd, GameErrEnum err) {
        return new BaseMessage(cmd, err.getCode(), EMPTY_BYTES);
    }
}
