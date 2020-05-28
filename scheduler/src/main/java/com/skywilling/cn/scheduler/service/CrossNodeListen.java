package com.skywilling.cn.scheduler.service;


import com.skywilling.cn.manager.car.model.AutonomousCarInfo;

public interface CrossNodeListen {

  void inComingJunction(String vin, String laneId, String junctionName);

  void outGoingJunction(String vin, String junctionName);

  //void OnArrivingStation(AutonomousCarInfo carInfo, String statonName);

}
