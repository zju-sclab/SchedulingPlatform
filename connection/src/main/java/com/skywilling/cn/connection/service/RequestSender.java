package com.skywilling.cn.connection.service;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.connection.infrastructure.client.ClientService;
import com.skywilling.cn.connection.model.ACK;
import com.skywilling.cn.connection.model.Packet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Component
public class RequestSender {
    @Autowired
    ClientService clientService;

    static class PacketConsumer implements BiConsumer<Packet, Throwable> {

        CompletableFuture<Boolean> thenFuture;

        PacketConsumer(CompletableFuture<Boolean> thenFuture) {
            this.thenFuture = thenFuture;
        }

        @Override
        public void accept(Packet packet, Throwable throwable) {
            if (throwable != null) {
                thenFuture.completeExceptionally(throwable);
            } else {
                if (packet.getAck() == ACK.SUCCESS.getCode()) {
                    thenFuture.complete(true);
                } else {
                    thenFuture.complete(false);
                }
            }
        }
    }

    /**
     * 发送该数据到车端，指定vin所在的socket链接
     */
    public CompletableFuture<Boolean> sendRequest(String vin, TypeField typeField, JSONObject body) {

        CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();
        Packet.Builder builder = new Packet.Builder();
        Packet packet = builder.buildRequest(vin, typeField).buildBody(body).build();
        //调用车端链接的发送函数，指定vin对应的TCP链接
        CompletableFuture<Packet> respFuture = clientService.sendRequest(packet);
        if (respFuture == null) {
            throw new NullPointerException();
        }
        respFuture.whenComplete(new PacketConsumer(resultFuture));
        return resultFuture;
    }


}
