package com.skywilling.cn.connection.service;

import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.common.model.BasicCarResponse;
import com.skywilling.cn.connection.infrastructure.client.ClientPromise;
import com.skywilling.cn.connection.infrastructure.client.ClientService;
import com.skywilling.cn.connection.model.ACK;
import com.skywilling.cn.connection.model.Packet;
import com.skywilling.cn.connection.model.ProtocolField;
import com.skywilling.cn.monitor.listener.ListenerMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 请求分排器,通道中主要包含三种数据，一是请求，二是回复，三是链接断开
 */
@Component
public class RequestDispatcher {

    @Value("${netty.dispatch.threads}")
    private int threadNum;
    private ExecutorService executorService;

    @Autowired
    ClientService clientService;
    @Autowired
    ClientPromise clientPromise;
    @Autowired
    ListenerMap listenerMap;

    public RequestDispatcher() {
        //分配固定大小度的线程池多线程地处理请求的分派
        executorService = Executors.newFixedThreadPool(30);
    }

    /**
     * 分发，需要分发的请求有请求和回复
     */
    public void dispatch(final ChannelHandlerContext ctx, Packet packet) {

        executorService.submit(() -> {
            BasicCarResponse carResponse;
            if (ACK.COMMAND.getCode() == packet.getAck()) {
                carResponse = commandHandler(ctx, packet); //请求包
            } else {
                carResponse = responseHandler(packet); //回复包
            }
            if (carResponse != null) {
                Packet.Builder builder = new Packet.Builder();
                ctx.writeAndFlush(builder.buildResponse(packet, carResponse).build());
            }
        });
    }

    public BasicCarResponse commandHandler(final ChannelHandlerContext ctx, Packet packet) {
        TypeField typeField = TypeField.valueOf(packet.getType());
        if (TypeField.LOGIN == typeField) {
            return loginHandler(ctx, packet);
        }
        //Type 21 == Ox15
        else if (typeField != null) {
            //login, logout, heartbeat ,registration,terminalInfo..
            return listenerMap.getListener(typeField.getDesc()).process(packet.getVin(), packet.getData());
        }
        return null;
    }


    private BasicCarResponse loginHandler(final ChannelHandlerContext ctx, Packet packet) {
        String vin = packet.getVin();
        BasicCarResponse process = listenerMap.getListener(TypeField.LOGIN.getDesc()).process(vin, packet.getData());
        if (ACK.SUCCESS.getCode() == process.getCode()) {
            //save channel and vin
            AttributeKey<String> key = AttributeKey.valueOf(ProtocolField.VIN.getField());
            Attribute<String> vinAttr = ctx.channel().attr(key);
            vinAttr.setIfAbsent(vin);
            clientService.open(vin, ctx.channel());
        }
        return process;
    }

    public BasicCarResponse responseHandler(Packet packet) {
        boolean result = ACK.getResult(packet.getAck());
        TypeField typeField = TypeField.valueOf(packet.getType());
        if (typeField != null) {
            clientPromise.receivedResponse(packet);
            BasicCarResponse process = listenerMap.getListener(typeField.getDesc()).process(packet.getVin(), result, packet.getData());
            return process;
        }
        return null;
    }


    public void onLostConnection(final ChannelHandlerContext ctx) {
        AttributeKey<String> key = AttributeKey.valueOf(ProtocolField.VIN.getField());
        Attribute<String> vin = ctx.channel().attr(key);
        if (StringUtils.isNotEmpty(vin.get())) {
            clientService.close(vin.get());
            listenerMap.getListener(ACK.FAILURE.getDesc()).process(vin.get(), null);
        }
        ctx.close();
    }

}
