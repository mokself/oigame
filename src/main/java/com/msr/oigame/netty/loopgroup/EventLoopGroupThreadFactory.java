package com.msr.oigame.netty.loopgroup;

import com.msr.oigame.common.concurrent.DaemonThreadFactory;

import java.util.concurrent.ThreadFactory;

/**
 * @author 渔民小镇
 * @date 2023-02-18
 */
final class EventLoopGroupThreadFactory {

    /**
     * 业务 ThreadFactory
     *
     * @return worker ThreadFactory
     */
    static ThreadFactory workerThreadFactory() {
        return new DaemonThreadFactory("oigame:netty-server-worker");
    }

    /**
     * 连接 ThreadFactory
     *
     * @return boss ThreadFactory
     */
    static ThreadFactory bossThreadFactory() {
        return new DaemonThreadFactory("oigame:netty-server-boss");
    }
}
