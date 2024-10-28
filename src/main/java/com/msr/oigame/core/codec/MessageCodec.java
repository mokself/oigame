package com.msr.oigame.core.codec;

public interface MessageCodec {
    /**
     * 将数据对象编码成字节数组
     *
     * @param data 数据对象
     * @return bytes
     */
    byte[] encode(Object data);

    /**
     * 将netty字节缓冲解码成消息对象
     * @param data netty字节缓冲
     * @return 消息对象
     */
    <T> T decode(byte[] data, Class<T> type);

    /**
     * 编解码名
     *
     * @return 编解码名
     */
    default String codecName() {
        return this.getClass().getSimpleName();
    }
}
