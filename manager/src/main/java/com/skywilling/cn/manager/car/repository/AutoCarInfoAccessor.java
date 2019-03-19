package com.skywilling.cn.manager.car.repository;


import com.skywilling.cn.manager.car.model.AutonomousCarInfo;

public interface AutoCarInfoAccessor {

  void save(AutonomousCarInfo car);

  AutonomousCarInfo get(String vin);

  AutonomousCarInfo getOrCreate(String vin);
}
