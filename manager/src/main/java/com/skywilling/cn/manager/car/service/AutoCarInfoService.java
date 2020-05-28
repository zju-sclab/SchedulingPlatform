package com.skywilling.cn.manager.car.service;


import com.skywilling.cn.common.exception.CarNotExistsException;
import com.skywilling.cn.common.model.Position;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.model.ModuleInfo;

import java.util.List;

public interface AutoCarInfoService {

  void save(AutonomousCarInfo autonomousCarInfo);

  Position getPosition(String vin);


  boolean isConnected(String vin);

  String getTaskId(String vin);

  AutonomousCarInfo get(String vin);

  AutonomousCarInfo getOrCreate(String vin);

  List<ModuleInfo> getAllNodesInfo(String vin);
}
