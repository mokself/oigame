package com.msr.oigame.common.exception;

/**
 * 试图写入空数据
 *
 * @author morley
 * 2022/1/22 10:40
 */
public class WriteEmptyDataException extends RuntimeException {
    public WriteEmptyDataException() {
    }

    public WriteEmptyDataException(String msg) {
        super(msg);
    }

    public WriteEmptyDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public WriteEmptyDataException(Throwable cause) {
        super(cause);
    }

    public WriteEmptyDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
