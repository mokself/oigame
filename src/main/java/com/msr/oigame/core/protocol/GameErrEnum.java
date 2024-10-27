package com.msr.oigame.core.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameErrEnum {
    /** 1-100 常见链路错误 */
    SYSTEM_ERROR(1, "系统其他错误"),
    PARAM_VALIDATE_ERROR(2, "参数校验错误"),
    CMD_ERROR(3, "路由错误"),
    IDLE_TIMEOUT(4, "心跳超时"),
    VERIFY_IDENTITY_FAILED(5, "请先登录"),
    FORCED_OFFLINE(6, "强制玩家下线"),

    /** 100-1000 服务器内部错误 */
    SERVER_ERROR(101, "服务器错误"),
    OPERATION_FAILED(102, "操作失败"),
    DATA_ERROR(103, "非法数据"),

    /** 1000-* 业务错误 */
    USER_NOT_EXIST(1000, "用户不存在"),
    ACCOUNT_ALREADY_LOGIN(1001, "当前账号已经登录"),
    ;

    private final int code;
    private final String msg;
}
