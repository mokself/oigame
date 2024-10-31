package com.msr.oigame.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson的ObjectMapper配置
 */
@Configuration
public class JacksonConfig {
    private static final ObjectMapper DEFAULT_INSTANCE = new ObjectMapper();

    /*
     * 初始化默认ObjectMapper
     */
    static {
        // JDK8时间序列化
        DEFAULT_INSTANCE.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        DEFAULT_INSTANCE.registerModule(new JavaTimeModule());

        // 忽略在JSON字符串中存在但Java对象实际没有的属性
        DEFAULT_INSTANCE.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Long转String
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        DEFAULT_INSTANCE.registerModule(simpleModule);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return DEFAULT_INSTANCE;
    }

    public static ObjectMapper getObjectMapper() {
        return DEFAULT_INSTANCE;
    }
}
