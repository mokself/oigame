package com.msr.oigame.core.codec;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataCodec {

    /** 业务数据的编解码器 */
    private static MessageCodec dataCodec = new JsonMessageCodec();

    /**
     * 将业务参数编码成字节数组
     *
     * @param message 业务参数 (指的是请求端的请求参数)
     * @return bytes
     */
    public static byte[] encode(Object message) {
        return dataCodec.encode(message);
    }

    /**
     * 将字节数组解码成对象
     *
     * @param data       业务参数 (指的是请求端的请求参数)
     * @return 业务参数
     */
    public static <T> T decode(byte[] data, Class<T> type) {
        return dataCodec.decode(data, type);
    }
}
