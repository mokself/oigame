package com.msr.oigame.core.protocol;

import io.netty.buffer.ByteBuf;

public interface BaseMessage {

    ByteBuf getData();
}
