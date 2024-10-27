package com.msr.oigame.core.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msr.oigame.core.json.JacksonJsonSerializer;
import com.msr.oigame.core.json.JsonSerializer;
import com.msr.oigame.core.json.JsonSerializerFactory;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;

@Getter
public class JsonMessage implements BaseMessage {
    private static JsonSerializer jsonSerializer;

    private final int cmd;
    /** 响应码: 0:成功, 其他表示有错误 */
    private final int responseStatus;
    private final String json;
    private final ByteBuf data;

    public JsonMessage(ByteBuf data) {
        this.cmd = data.readInt();
        this.responseStatus = data.readInt();
        this.json = data.readCharSequence(data.readableBytes(), StandardCharsets.UTF_8).toString();
        this.data = data;
    }

    private JsonSerializer getJsonSerializer() {
        if (jsonSerializer == null) {
            jsonSerializer = JsonSerializerFactory.getJsonSerializer();
        }
        return jsonSerializer;
    }

    @SneakyThrows
    public <T> T deserialize(Class<T> clazz) {
        JsonSerializer serializer = getJsonSerializer();
        if (serializer instanceof JacksonJsonSerializer jacksonJsonSerializer) {
            ObjectMapper objectMapper = jacksonJsonSerializer.getObjectMapper();
            return objectMapper.readValue(json, clazz);
        }
        throw new UnsupportedOperationException("unsupported json serializer [" + serializer.getClass().getName() + "]");
    }
}
