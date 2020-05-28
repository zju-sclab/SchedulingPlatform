package com.skywilling.cn.connection.model;

import io.netty.channel.Channel;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CarClient {

  private AtomicInteger nextRequestId;
  private final String vin;
  private ConcurrentHashMap<Integer, CompletableFuture<Packet>> responses;

  /**
   * */
  private final Channel channel;

  public CarClient(String vin, Channel channel) {
    this.vin = vin;
    this.channel = channel;
    nextRequestId = new AtomicInteger(0);
    responses = new ConcurrentHashMap<>();
  }

  public int getNextRequestId() {
    return nextRequestId.getAndIncrement();
  }

  public void setRequestId(int requestId) {
    nextRequestId.set(requestId);
  }

  public String getVin() {
    return vin;
  }

  public Channel getChannel() {
    return channel;
  }

  public boolean addAsyncResult(int requestId, CompletableFuture<Packet> response) {
    if (responses.putIfAbsent(requestId, response) != null) {
      response.completeExceptionally(new Throwable("requestId is already used"));
      return true;
    }
    return false;
  }

  public CompletableFuture<Packet> removeAsyncResult(int requestId) {
    return responses.remove(requestId);
  }

  public void close() {
    Collection<CompletableFuture<Packet>> elements = responses.values();
    for (CompletableFuture<Packet>result: elements) {
      result.completeExceptionally(new Throwable("car client closed"));
    }
    responses.clear();
  }
}
