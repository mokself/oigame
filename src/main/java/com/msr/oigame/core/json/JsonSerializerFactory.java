package com.msr.oigame.core.json;

import org.springframework.util.ClassUtils;

public class JsonSerializerFactory {

    public static JsonSerializer getJsonSerializer() {
        if (ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", null)) {
            return new JacksonJsonSerializer();
        }
        return null;
    }
}
