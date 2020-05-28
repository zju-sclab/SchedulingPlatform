package com.skywilling.cn.connection.infrastructure.client;


import com.skywilling.cn.connection.model.Packet;

public interface ClientPromise {

  void receivedResponse(Packet packet);
  /**
   *
   * */
  void onRequestTimeout(String vin, int requestId);

}
