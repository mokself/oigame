package com.msr.oigame.core.codec;

import com.msr.oigame.core.protocol.BaseMessage;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataCodec {

    /** 业务数据的编解码器 */
    private static MessageCodec dataCodec = new ExternalMessageCodec();

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
}
