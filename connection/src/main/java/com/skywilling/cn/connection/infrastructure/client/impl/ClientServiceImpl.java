package com.skywilling.cn.connection.infrastructure.client.impl;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.connection.infrastructure.client.ClientPromise;
import com.skywilling.cn.connection.infrastructure.client.ClientService;
import com.skywilling.cn.connection.model.CarClient;
import com.skywilling.cn.connection.model.Packet;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@Service
public class ClientServiceImpl implements ClientService, ClientPromise {
  private static final Logger LOG = LoggerFactory.getLogger(ClientServiceImpl.class);
  /** 内存Mao保存车id到车端链接的映射 */
  private ConcurrentHashMap<String, CarClient> carPool = new ConcurrentHashMap<>();
  /** 打开一个新的车端链接 */
  private  CarClient remoteTarget = null;

  @Override
  public void open(String vin, Channel channel) {
    CarClient carClient = new CarClient(vin, channel);
    carPool.put(vin, carClient);
  }
  /** 关闭一个车端链接 */
  @Override
  public void close(String vin) {
    if(isAlive(vin)){
        CarClient carClient = carPool.get(vin);
        carClient.close();
        carPool.remove(vin);
        if(remoteTarget.getVin() == vin){
            //失去链接之后 我们的远程遥控链接也需要关闭
            remoteTarget = null;
        }
    }
  }

    @Override
    public void chooseRemote(String vin) {
        remoteTarget = carPool.getOrDefault(vin, null);
    }

    @Override
    public CarClient getRemote() {
        return remoteTarget;
    }

  /** 判断链接是否存活 */
  @Override
  public boolean isAlive(String vin) {
    return carPool.containsKey(vin);
  }


  /** 按照协议发送消息到车端 */
  @Override
  public CompletableFuture<Packet> sendRequest(Packet packet) {

      CompletableFuture<Packet> response = new CompletableFuture<>();
      try {
          //这里指定TCP的消息通道为vin对用的Socket链接
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

    /** 按照协议发送消息到车端 */
    @Override
    public CompletableFuture<Packet> sendCommand(CarClient carClient,Packet packet) {

        CompletableFuture<Packet> response = new CompletableFuture<>();
        try {
            //这里指定TCP的消息通道为vin对用的Socket链接

//            String js = packet.getData();
//            LOG.warn(js);
//            JSONObject jsonObject = JSONObject.parseObject(js);
//            LOG.warn(jsonObject.getString("target"));
//            String vin = jsonObject.getString("target");

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




  /** 查找所有的车端链接 */
  @Override
  public List<String> getAllClients() {
      ArrayList<String> clients = new ArrayList<>();
      carPool.keySet().stream().forEach(s -> clients.add(s));
      return clients;
  }

  @Override
  public void receivedResponse(Packet packet) {
      complete(packet.getVin(), packet);
  }

  private void complete(String vin, Packet response) {

     try{
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
    //if(isAlive())
    completeExceptionally(vin, requestId, new TimeoutException());
  }
  /** 关闭所有链接 */
  public void shutDownAll() {
      Collection<CarClient> carClientCollection = carPool.values();
      for (CarClient carClient : carClientCollection) {
        carClient.close();
      }
      carPool.clear();
  }
}
