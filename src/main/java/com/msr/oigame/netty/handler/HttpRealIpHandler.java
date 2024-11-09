package com.msr.oigame.netty.handler;

import com.msr.oigame.netty.session.SocketUserSession;
import com.msr.oigame.util.StrUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class HttpRealIpHandler extends ChannelInboundHandlerAdapter {

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest request) {
            HttpHeaders headers = request.headers();
            final String[] checkHeaders = new String[]{"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
            for (String checkHeader : checkHeaders) {
                String ip = headers.get(checkHeader);
                if (!isUnknown(ip)) {
                    String realIp = getMultistageReverseProxyIp(ip);
                    ctx.channel().attr(SocketUserSession.realIpKey).set(realIp);
                    break;
                }
            }
            ctx.pipeline().remove(this);
        }

        super.channelRead(ctx, msg);
    }

    public boolean isUnknown(String checkString) {
        return StrUtil.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
    }

    public String getMultistageReverseProxyIp(String ip) {
        if (ip != null && ip.indexOf(',') > 0) {
            List<String> ips = StrUtil.splitTrim(ip, ",");

            for (String subIp : ips) {
                if (!isUnknown(subIp)) {
                    ip = subIp;
                    break;
                }
            }
        }

        return ip;
    }
}
