package com.msr.oigame.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msr.oigame.config.JacksonConfig;
import lombok.SneakyThrows;

public class JsonUtil {

    public static ObjectMapper getObjectMapper() {
        return JacksonConfig.getObjectMapper();
    }

    @SneakyThrows
    public static String toJson(Object data) {
        return JacksonConfig.getObjectMapper().writeValueAsString(data);
    }

    @SneakyThrows
    public static <T> T parseJson(String json, Class<T> type) {
        return JacksonConfig.getObjectMapper().readValue(json, type);
    }

    @SneakyThrows
    public static <T> T parseJson(String json, TypeReference<T> type) {
        return JacksonConfig.getObjectMapper().readValue(json, type);
    }
}
