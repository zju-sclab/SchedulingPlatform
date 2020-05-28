package com.skywilling.cn.manager.car.service.impl;


import com.skywilling.cn.common.exception.CarNotExistsException;
import com.skywilling.cn.common.model.Position;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.model.CarDynamic;
import com.skywilling.cn.manager.car.model.ModuleInfo;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import com.skywilling.cn.manager.car.service.CarInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarInfoServiceImpl implements CarInfoService {

  @Autowired
  private AutoCarInfoService autoCarInfoService;

  @Autowired
  private CarDynamicService carDynamicService;


  @Override
  public Position getPosition(String vin) {
    Position point = autoCarInfoService.getPosition(vin);
    if (point == null) {
      CarDynamic carDynamic = carDynamicService.query(vin);
      point.setStatus(carDynamic.getIsValid());
      point.setX(carDynamic.getLongitude());
      point.setY(carDynamic.getLatitude());
    }
    return point;
  }

  @Override
  public boolean isConnected(String vin) {
    return autoCarInfoService.isConnected(vin);
  }

  @Override
  public int getState(String vin) {
    AutonomousCarInfo carInfo = autoCarInfoService.get(vin);


    return carInfo.getState();
  }

  @Override
  public CarDynamic get(String vin) {
    return carDynamicService.query(vin);
  }

  @Override
  public AutonomousCarInfo getAutoCarInfo(String vin) {
    return autoCarInfoService.get(vin);
  }

  @Override
  public List<ModuleInfo> getAllNodesInfo(String vin) {
    return autoCarInfoService.getAllNodesInfo(vin);
  }

  @Override
  public String getTaskId(String vin) {
    return autoCarInfoService.getTaskId(vin);
  }

  @Override
  public List<String> queryVins(String vin) {
    return carDynamicService.queryVins(vin);
  }
}
