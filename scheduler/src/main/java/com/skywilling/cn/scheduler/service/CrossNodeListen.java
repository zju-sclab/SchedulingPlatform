package com.skywilling.cn.scheduler.service;


import com.skywilling.cn.manager.car.model.AutonomousCarInfo;

public interface CrossNodeListen {

  void inComingJunction(AutonomousCarInfo carInfo, String junctionName);

  void outGoingJunction(AutonomousCarInfo carInfo, String junctionName);

}
