package com.msr.oigame;

import com.msr.oigame.config.ServerConfig;
import com.msr.oigame.core.protocol.BaseMessage;
import com.msr.oigame.core.protocol.MessageFactory;
import com.msr.oigame.netty.WebsocketClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SpringBootTest
class WebsocketConnectTest {
    private final WebsocketClient client;

    @Autowired
    public WebsocketConnectTest(ServerConfig serverConfig) {
        client = new WebsocketClient(serverConfig.getPort());
    }

    @Test
    @DisplayName("测试连接")
    void testConnect() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<BaseMessage> receive = client.receive();
        client.connect();

        BaseMessage idleMessage = MessageFactory.createIdleMessage();
        client.send(idleMessage);
        BaseMessage message = receive.get(3, TimeUnit.SECONDS);
        System.out.println(message);
        client.close();
    }

}
