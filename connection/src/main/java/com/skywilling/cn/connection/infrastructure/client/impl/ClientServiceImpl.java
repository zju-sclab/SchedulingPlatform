package com.skywilling.cn.connection.infrastructure.client.impl;

import com.skywilling.cn.connection.infrastructure.client.ClientService;
import com.skywilling.cn.connection.infrastructure.client.ClientPromise;
import com.skywilling.cn.connection.model.CarClient;
import com.skywilling.cn.connection.model.Packet;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

@Service
public class ClientServiceImpl implements ClientService, ClientPromise {

  private static final Logger LOG = LoggerFactory.getLogger(ClientServiceImpl.class);
  private ConcurrentHashMap<String, CarClient> carPool = new ConcurrentHashMap<>();

  @Override
  public void open(String vin, Channel channel) {
    CarClient carClient = new CarClient(vin, channel);
    carPool.put(vin, carClient);
  }

  @Override
  public void close(String vin) {
    CarClient carClient = carPool.remove(vin);
    carClient.close();
  }

  @Override
  public boolean isAlive(String vin) {
    return carPool.containsKey(vin);
  }

  @Override
  public CompletableFuture<Packet> sendRequest(Packet packet) {
    CompletableFuture<Packet> response = new CompletableFuture<>();
    try {
      CarClient carClient = carPool.getOrDefault(packet.getVin(), null);
      Channel channel = carClient.getChannel();
      if (carClient == null || channel == null || packet == null) {
        return null;
      }
      packet.setRequestId(carClient.getNextRequestId());
      channel.writeAndFlush(packet);
      carClient.addAsyncResult(packet.getRequestId(), response);
    } catch (NullPointerException e) {
      response.completeExceptionally(e);
      return null;
    }
    return response;
  }

  @Override
  public void receivedResponse(Packet packet) {
    complete(packet.getVin(), packet);
  }

  private void complete(String vin, Packet response) {
    try {
      CarClient carClient = carPool.getOrDefault(vin, null);
      CompletableFuture<Packet> rspFuture = carClient.removeAsyncResult(response.getRequestId());
      rspFuture.complete(response);
    } catch (Exception e) {
    }
  }

  private void completeExceptionally(String vin, int requestId, Throwable throwable) {
    try {
      CarClient carClient = carPool.getOrDefault(vin, null);
      CompletableFuture<Packet> rspFuture = carClient.removeAsyncResult(requestId);
      rspFuture.completeExceptionally(throwable);
    } catch (Exception e) {
    }
  }

  /**
   * TODO:添加计时,未在规定时间内回复则认为是掉线
   * @param vin
   * @param requestId
   */
  @Override
  public void onRequestTimeout(String vin, int requestId) {
    completeExceptionally(vin, requestId, new TimeoutException());
  }

  public void shutdown() {
    Collection<CarClient> carClientCollection = carPool.values();
    for (CarClient carClient : carClientCollection) {
      carClient.close();
    }
    carPool.clear();
  }
}
