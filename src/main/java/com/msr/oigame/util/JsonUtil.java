package com.msr.oigame.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;

public class JsonUtil {
    @Getter
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    @SneakyThrows
    public static String toJson(Object data) {
        return objectMapper.writeValueAsString(data);
    }

    @SneakyThrows
    public static <T> T parseJson(String json, Class<T> type) {
        return objectMapper.readValue(json, type);
    }

    @SneakyThrows
    public static <T> T parseJson(String json, TypeReference<T> type) {
        return objectMapper.readValue(json, type);
    }
}
