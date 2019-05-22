package com.skywilling.cn.manager.car.service;



import com.skywilling.cn.common.model.Position;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.model.CarDynamic;
import com.skywilling.cn.manager.car.model.ModuleInfo;

import java.util.List;

public interface CarInfoService {

  Position getPosition(String vin);

  boolean isConnected(String vin);

  int getState(String vin);

  CarDynamic get(String vin);

  AutonomousCarInfo getAutoCarInfo(String vin);

  List<ModuleInfo> getAllNodesInfo(String vin);

  String getTaskId(String vin);

  List<String> queryVins(String vin);

}
