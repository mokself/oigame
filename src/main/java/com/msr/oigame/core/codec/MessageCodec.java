package com.msr.oigame.core.codec;

import com.msr.oigame.core.protocol.BaseMessage;
import io.netty.buffer.ByteBuf;

public interface MessageCodec {
    /**
     * 将数据对象编码成字节数组
     *
     * @param data 数据对象
     * @return bytes
     */
    ByteBuf encode(Object data);

    /**
     * 将netty字节缓冲解码成消息对象
     * @param data netty字节缓冲
     * @return 消息对象
     */
    BaseMessage decode(ByteBuf data);

    /**
     * 编解码名
     *
     * @return 编解码名
     */
    default String codecName() {
        return this.getClass().getSimpleName();
    }
}
