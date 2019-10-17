package com.skywilling.cn.connection.infrastructure.client;


import com.skywilling.cn.connection.model.CarClient;
import com.skywilling.cn.connection.model.Packet;
import io.netty.channel.Channel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ClientService {

  boolean isAlive(String vin);

  void open(String vin, Channel channel);

  void close(String vin);

  void chooseRemote(String vin);

  CarClient getRemote();

  CompletableFuture<Packet> sendRequest(Packet packet);

  CompletableFuture<Packet> sendCommand(CarClient carClient,Packet packet);

  List<String> getAllClients();
}
