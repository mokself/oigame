package com.msr.oigame.common.concurrent;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public final class DaemonThreadFactory implements ThreadFactory {
    private String threadNamePrefix;
    private int threadPriority = 5;
    private ThreadGroup threadGroup;
    private final AtomicInteger threadCount = new AtomicInteger();

    public DaemonThreadFactory(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    @Override
    public Thread newThread(@Nonnull Runnable runnable) {
        Thread thread = new Thread(this.getThreadGroup(), runnable, this.nextThreadName());
        thread.setPriority(this.threadPriority);
        thread.setDaemon(true);
        return thread;
    }

    private String nextThreadName() {
        String format = "%s-%d";
        return String.format(format, this.getThreadNamePrefix(), this.threadCount.incrementAndGet());
    }
}
