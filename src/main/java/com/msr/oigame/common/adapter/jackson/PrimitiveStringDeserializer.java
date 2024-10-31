package com.msr.oigame.common.adapter.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.SneakyThrows;

/**
 * 解析任何json数据时都将解析为其原本的字符串形式
 */
public class PrimitiveStringDeserializer extends JsonDeserializer<String> {
    @SneakyThrows
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) {
        return p.readValueAsTree().toString();
    }
}
