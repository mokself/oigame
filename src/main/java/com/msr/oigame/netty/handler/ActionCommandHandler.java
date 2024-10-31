package com.msr.oigame.netty.handler;

import com.msr.oigame.common.exception.MsgException;
import com.msr.oigame.core.codec.DataCodec;
import com.msr.oigame.core.protocol.BaseMessage;
import com.msr.oigame.core.protocol.GameErrEnum;
import com.msr.oigame.core.protocol.MessageFactory;
import com.msr.oigame.core.skeleton.ActionCommand;
import com.msr.oigame.core.skeleton.FlowContext;
import com.msr.oigame.core.skeleton.annotation.Action;
import com.msr.oigame.netty.session.UserSessionManager;
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
import java.nio.charset.StandardCharsets;
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
        ActionCommand actionCommand = actionMap.get(msg.cmd());
        if (actionCommand == null) {
            log.warn("请求到未定义处理器的命令, cmd: {}", msg.cmd());
            BaseMessage rejectMessage = MessageFactory.employError(msg, GameErrEnum.CMD_ERROR);
            ctx.writeAndFlush(rejectMessage);
            return;
        }

        // 解析参数
        Parameter[] parameters = actionCommand.method().getParameters();
        FlowContext flowContext = new FlowContext(UserSessionManager.getUserSession(ctx), actionCommand, msg);
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
            try {
                params[i] = DataCodec.decode(msg.data().toString().getBytes(StandardCharsets.UTF_8), type);
            } catch (Exception e) {
                log.error("参数解析失败, cmd: {}, 尝试将消息体转换为: [{}]类型时出错; 参数位置: [{}] 消息处理器: [{}]",
                        msg.cmd(), type.getName(), i, actionCommand.method(), e);
                throw e;
            }

            // TODO JSR380验证
        }

        try {
            Object result = actionCommand.method().invoke(actionCommand.target(), params);
            if (result != null) {
                if (result instanceof BaseMessage) {
                    ctx.writeAndFlush(result);
                } else {
                    BaseMessage message = MessageFactory.createMessage(msg.cmd(), result);
                    ctx.writeAndFlush(message);
                }
            }
        } catch (MsgException e) {
            BaseMessage errorMessage = MessageFactory.employError(msg, e.getGameErrEnum());
            ctx.writeAndFlush(errorMessage);
        } catch (Exception e) {
            log.error("处理消息时发生异常, cmd: {}, 消息处理器: [{}]", msg.cmd(), actionCommand.method(), e);
            BaseMessage errorMessage = MessageFactory.employError(msg, GameErrEnum.SERVER_ERROR);
            ctx.writeAndFlush(errorMessage);
        }
    }
}
