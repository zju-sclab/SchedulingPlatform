package com.skywilling.cn.connection.infrastructure.server.handler;

import com.skywilling.cn.connection.model.Packet;
import com.skywilling.cn.connection.service.RequestDispatcher;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("serverHandler")
@ChannelHandler.Sharable
public class ServerChannelHandlerAdapter extends ChannelInboundHandlerAdapter {
    /**
     * 日志处理
     */
    private Logger logger = LoggerFactory.getLogger(ServerChannelHandlerAdapter.class);
    /**
     * 注入请求分排器
     */
    @Resource
    private RequestDispatcher dispatcher;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        //异常关闭事件
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Packet packet = (Packet) msg;
        //文件读写事件
        dispatcher.dispatch(ctx, packet);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof ChannelInputShutdownEvent) {
            //客户端断线事件
            dispatcher.onLostConnection(ctx);
        }
    }

}
