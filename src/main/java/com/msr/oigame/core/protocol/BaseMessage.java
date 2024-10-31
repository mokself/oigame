package com.msr.oigame.core.protocol;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.msr.oigame.common.adapter.jackson.PrimitiveStringDeserializer;

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
        @JsonDeserialize(using = PrimitiveStringDeserializer.class)
        Object data
) { }
