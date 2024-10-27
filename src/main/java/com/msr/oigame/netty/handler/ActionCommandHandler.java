package com.msr.oigame.netty.handler;

import com.msr.oigame.core.codec.DataCodec;
import com.msr.oigame.core.codec.ExternalMessageCodec;
import com.msr.oigame.core.protocol.BaseMessage;
import com.msr.oigame.core.protocol.ExternalMessage;
import com.msr.oigame.core.protocol.GameErrEnum;
import com.msr.oigame.core.skeleton.ActionCommand;
import com.msr.oigame.core.skeleton.FlowContext;
import com.msr.oigame.core.skeleton.annotation.Action;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ChannelHandler.Sharable
@Component
@RequiredArgsConstructor
public class ActionCommandHandler extends SimpleChannelInboundHandler<BaseMessage> implements ApplicationListener<ContextRefreshedEvent> {
    private final ApplicationContext applicationContext;
    private Map<Integer, ActionCommand> actionMap;

    /**
     * spring初始化后加载所有的ActionCommand
     */
    @Override
    public void onApplicationEvent(@Nonnull ContextRefreshedEvent event) {
        load();
    }

    public void load() {
        Map<Integer, ActionCommand> actionMap = new HashMap<>();
        String[] candidateBeanNames = applicationContext.getBeanNamesForType(Object.class);
        for (String beanName : candidateBeanNames) {
            Object bean = applicationContext.getBean(beanName);
            Class<?> type = bean.getClass();
            MethodIntrospector.selectMethods(type,
                    (MethodIntrospector.MetadataLookup<ActionCommand>) method -> {
                        try {
                            Action action = AnnotatedElementUtils.findMergedAnnotation(method, Action.class);
                            if (action == null) {
                                return null;
                            }
                            return new ActionCommand(action.value(), bean, method);
                        } catch (Throwable ex) {
                            if (log.isDebugEnabled()) {
                                log.error("Invalid mapping on handler class [{}]: {}", type.getName(), method, ex);
                            }
                            return null;
                        }
                    }
            ).forEach((key, value) -> actionMap.put(value.cmd(), value));
        }
        this.actionMap = actionMap;
    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseMessage msg) throws Exception {
        ActionCommand actionCommand = actionMap.get(msg.getCmd());
        if (actionCommand == null) {
            log.warn("请求到未定义处理器的命令, cmd: {}", msg.getCmd());
            ExternalMessage rejectMessage = ExternalMessageCodec.encodeMsg(msg.getCmd(), GameErrEnum.CMD_ERROR.getCode());
            ctx.writeAndFlush(rejectMessage);
            return;
        }

        // 解析参数
        Parameter[] parameters = actionCommand.method().getParameters();
        FlowContext flowContext = new FlowContext();
        Object[] params = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> type = parameter.getType();

            // flow 上下文
            if (type.isAssignableFrom(FlowContext.class)) {
                params[i] = flowContext;
                continue;
            }

            // 消息基类
            if (type.isAssignableFrom(BaseMessage.class)) {
                params[i] = msg;
                continue;
            }

            // 其他消息会尝试进行转换
            params[i] = DataCodec.decode(msg, type);

            // TODO JSR380验证
        }

        Object result = actionCommand.method().invoke(actionCommand.target(), params);
        if (result != null) {
            if (result instanceof BaseMessage) {
                ctx.writeAndFlush(result);
            } else {
                ByteBuf encodeResponse = DataCodec.encode(new Object[]{actionCommand.cmd(), result});
                ctx.writeAndFlush(encodeResponse);
            }
        }
    }
}
