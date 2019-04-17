package com.skywilling.cn.connection.service;

import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.connection.infrastructure.client.ClientService;
import com.skywilling.cn.connection.model.Packet;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;

public class RequestSenderTest {
    @Autowired
    ClientService clientService;
    @Test
    public void sendRequest() {
        CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();
        Packet.Builder builder = new Packet.Builder();
        JSONObject s = new JSONObject();
       Packet packet = builder.buildRequest(String.valueOf(232312), TypeField.valueOf(0x21)).buildBody(s)
                .build();
        CompletableFuture<Packet> respFuture = clientService.sendRequest(packet);
        if (respFuture == null) {
            throw new NullPointerException();
        }
        respFuture.whenComplete(new RequestSender.PacketConsumer(resultFuture));
    }
}