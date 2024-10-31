package com.msr.oigame.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Data
@Configuration
@ConfigurationProperties("game")
public class ServerConfig {
    private int port;

    private String websocketPath = "";

    private Idle idle = new Idle();

    @Data
    public static class Idle {
        /** all - 心跳时间 */
        private long allIdleTime = 300;
        /** 读 - 心跳时间 */
        private long readerIdleTime = allIdleTime;
        /** 写 - 心跳时间 */
        private long writerIdleTime = allIdleTime;
        /** 心跳时间单位 - 默认秒单位 */
        private TimeUnit timeUnit = TimeUnit.SECONDS;
        /** true : 响应心跳给客户端 */
        private boolean pong = true;
    }
}
