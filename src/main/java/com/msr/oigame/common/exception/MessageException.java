package com.msr.oigame.common.exception;

/**
 * MessageException
 *
 * @author morley
 * 2022/1/25 9:38
 */
public class MessageException extends RuntimeException {
    public MessageException() {
        super("消息处理异常");
    }

    public MessageException(String message) {
        super(message);
    }

    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageException(Throwable cause) {
        super(cause);
    }

    protected MessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}