package com.msr.oigame.core.protocol;

import io.netty.buffer.ByteBuf;

public interface BaseMessage {

    /**
     * 消息指令，也可以叫路由
     */
    int getCmd();

    /**
     * 消息体数据
     */
    ByteBuf getData();

    /** 响应码: 0:成功, 其他表示有错误 */
    int getResponseStatus();
}
