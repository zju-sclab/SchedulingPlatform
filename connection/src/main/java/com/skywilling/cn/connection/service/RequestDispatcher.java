package com.skywilling.cn.connection.service;

import com.skywilling.cn.connection.infrastructure.client.ClientPromise;
import com.skywilling.cn.connection.infrastructure.client.ClientService;
import com.skywilling.cn.connection.model.ACK;
import com.skywilling.cn.connection.model.Packet;
import com.skywilling.cn.connection.model.ProtocolField;
import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.monitor.listener.ListenerMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 请求分排器,通道中主要包含三种数据，一是请求，二是回复，三是链接断开
 */
@Service
public class RequestDispatcher {

    @Value("${netty.dispatch.threads}")
    private int threadNum;

    private ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
    @Autowired
    ClientService clientService;
    @Autowired
    ClientPromise clientPromise;
    @Autowired
    ListenerMap listenerMap;

    /**
     * 分发，需要分发的请求有请求和回复
     *
     * @param ctx
     * @param packet
     */
    public void dispatch(final ChannelHandlerContext ctx, Packet packet) {

        executorService.submit(() -> {
            if (ACK.COMMAND.getCode() == packet.getAck()) {
                commandHandler(ctx, packet);
                return;
            }
            responseHandler(packet);

        });
    }

    public void commandHandler(final ChannelHandlerContext ctx, Packet packet) {
        TypeField typeField = TypeField.valueOf(packet.getType());
        if (TypeField.LOGIN == typeField) {
            loginHandler(ctx, packet);
            return;
        }

        if (typeField != null) {
            listenerMap.getListener(typeField.getDesc()).
                    process(packet.getVin(), packet.getData());
        }
    }


    private void loginHandler(final ChannelHandlerContext ctx, Packet packet) {
        String vin = packet.getVin();
        ACK ack = ACK.SUCCESS;

        if (listenerMap.getListener(TypeField.LOGIN.getDesc()).process(vin, packet.getData())) {
            //save channel
            AttributeKey<String> key = AttributeKey.valueOf(ProtocolField.VIN.getField());
            Attribute<String> vinAttr = ctx.channel().attr(key);
            vinAttr.setIfAbsent(vin);
            clientService.open(vin, ctx.channel());
        } else {
            ack = ACK.FAILURE;
        }
        //build response
        Packet.Builder builder = new Packet.Builder();
        ctx.writeAndFlush(builder.buildResponse(vin, TypeField.LOGIN, ack, packet.getRequestId()).build());
    }

    public void responseHandler(Packet packet) {
        boolean result = ACK.getResult(packet.getAck());
        TypeField typeField = TypeField.valueOf(packet.getType());

        if (typeField != null) {
            listenerMap.getListener(typeField.getDesc()).
                    process(packet.getVin(), result, packet.getData());
            clientPromise.receivedResponse(packet);
        }
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
