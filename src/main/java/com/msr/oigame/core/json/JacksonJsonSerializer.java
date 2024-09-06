package com.msr.oigame.core.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JacksonJsonSerializer implements JsonSerializer {

    private ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public String serialize(Object data) {
        return getObjectMapper().writeValueAsString(data);
    }

    private ObjectMapper getObjectMapper() {
        if (this.objectMapper == null) {
            // 默认objectMapper
            this.objectMapper = new ObjectMapper();
        }
        return this.objectMapper;
    }
}
