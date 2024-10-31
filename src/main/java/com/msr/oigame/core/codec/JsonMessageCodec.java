package com.msr.oigame.core.codec;

import com.msr.oigame.util.JsonUtil;
import lombok.SneakyThrows;

public class JsonMessageCodec implements MessageCodec {

    /**
     * 将数据对象编码成字节数组
     *
     * @param data 数据对象
     * @return bytes
     */
    @SneakyThrows
    @Override
    public byte[] encode(Object data) {
        return JsonUtil.getObjectMapper().writeValueAsBytes(data);
    }

    /**
     * 将字节数组解码成消息对象
     * @return 消息对象
     */
    @SneakyThrows
    @Override
    public <T> T decode(byte[] data, Class<T> type) {
        return JsonUtil.getObjectMapper().readValue(data, type);
    }
}
