package com.skywilling.cn.manager.car.service;


import com.skywilling.cn.common.exception.CarNotExistsException;
import com.skywilling.cn.livemap.model.Point;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.model.ModuleInfo;

import java.util.List;

public interface AutoCarInfoService {

  void save(AutonomousCarInfo autonomousCarInfo);

  Point getPosition(String vin);


  boolean isConnected(String vin) throws CarNotExistsException;

  String getTaskId(String vin);

  AutonomousCarInfo get(String vin);

  AutonomousCarInfo getOrCreate(String vin);
}
