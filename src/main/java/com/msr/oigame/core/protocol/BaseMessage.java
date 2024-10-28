package com.msr.oigame.core.protocol;

public record BaseMessage(
        /**
         * 消息指令，也可以叫路由
         */
        int cmd,
        /**
         *  响应码: 0:成功, 其他表示有错误
         */
        int responseStatus,
        /**
         * 消息体数据
         */
        byte[] data
) { }
