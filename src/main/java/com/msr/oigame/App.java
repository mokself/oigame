package com.msr.oigame;

import com.msr.oigame.core.protocol.MessageCmdCode;
import com.msr.oigame.netty.handler.AccessAuthHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;

@EnableJdbcAuditing
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        setAccessAuthCmd();
    }

    static void setAccessAuthCmd() {
        AccessAuthHandler.INSTANCE.addIgnoreCmd(MessageCmdCode.login);
    }

}
