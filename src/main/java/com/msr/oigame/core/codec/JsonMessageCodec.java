package com.msr.oigame.core.codec;

import com.msr.oigame.core.json.JsonSerializer;
import com.msr.oigame.core.json.JsonSerializerFactory;
import com.msr.oigame.core.protocol.BaseMessage;
import com.msr.oigame.core.protocol.ExternalMessage;
import com.msr.oigame.core.protocol.JsonMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JsonMessageCodec implements MessageCodec {

    private static final JsonSerializer jsonSerializer;

    static {
        jsonSerializer = JsonSerializerFactory.getJsonSerializer();
    }

    /**
     * 将数据对象编码成字节数组
     *
     * @param data 数据对象
     * @return bytes
     */
    @Override
    public ByteBuf encode(Object data) {
        if (data instanceof Map<?, ?> map) {
            int cmd = (int) map.get("cmd");
            int responseStatus = map.containsValue("responseStatus") ? (int) map.get("responseStatus") : 0;
            return encodeMsg(cmd, responseStatus, map).getData();
        }
        throw new UnsupportedOperationException();
    }

    public static ExternalMessage encodeMsg(int cmd, int responseStatus, Object data) {
        // 创建一个堆缓冲区的byteBuf
        ByteBuf byteBuf = Unpooled.buffer(ExternalMessage.headerLength);
        // 写入指令
        byteBuf.writeInt(cmd);
        byteBuf.writeInt(responseStatus);
        // 写入数据
        String jsonStr = jsonSerializer.serialize(data);
        byteBuf.writeCharSequence(jsonStr, StandardCharsets.UTF_8);
        return new ExternalMessage(byteBuf);
    }

    /**
     * 将netty字节缓冲解码成消息对象
     * @param data netty字节缓冲
     * @return 消息对象
     */
    @Override
    public BaseMessage decode(ByteBuf data) {
        return new JsonMessage(data);
    }
}
