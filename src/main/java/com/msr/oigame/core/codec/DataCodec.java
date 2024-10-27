package com.msr.oigame.core.codec;

import com.msr.oigame.core.protocol.BaseMessage;
import com.msr.oigame.core.protocol.ExternalMessage;
import com.msr.oigame.core.protocol.JsonMessage;
import io.netty.buffer.ByteBuf;
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
     * @param data 业务参数 (指的是请求端的请求参数)
     * @return bytes
     */
    public static ByteBuf encode(Object data) {
        return dataCodec.encode(data);
    }

    /**
     * 将字节数组解码成对象
     *
     * @param data       业务参数 (指的是请求端的请求参数)
     * @return 业务参数
     */
    public static BaseMessage decode(ByteBuf data) {
        return dataCodec.decode(data);
    }

    public static <T> T decode(BaseMessage msg, Class<T> type) {
        if (msg instanceof ExternalMessage externalMessage) {
            return externalMessage.readObject(type);
        } else if (msg instanceof JsonMessage jsonMessage) {
            return jsonMessage.deserialize(type);
        }
        throw new UnsupportedOperationException("unsupported message type [" + msg.getClass().getName() + "]");
    }
}
