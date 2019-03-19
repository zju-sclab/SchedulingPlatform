package com.skywilling.cn.manager.car.service.impl;


import com.skywilling.cn.common.exception.CarNotExistsException;
import com.skywilling.cn.livemap.model.Point;
import com.skywilling.cn.manager.car.enumeration.CarState;
import com.skywilling.cn.manager.car.enumeration.ModuleType;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.model.ModuleInfo;
import com.skywilling.cn.manager.car.model.Pose;
import com.skywilling.cn.manager.car.repository.AutoCarInfoAccessor;
import com.skywilling.cn.manager.car.repository.AutoCarInfoGeoAccessor;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AutoCarInfoServiceImpl implements AutoCarInfoService {

  @Autowired
  private AutoCarInfoAccessor autoCarInfoAccessor;
  @Autowired
  private AutoCarInfoGeoAccessor autoCarInfoGeoAccessor;

  @Override
  public void save(AutonomousCarInfo autonomousCarInfo) {
    autoCarInfoAccessor.save(autonomousCarInfo);
    autoCarInfoGeoAccessor.save(autonomousCarInfo);
  }

  @Override
  public Point getPosition(String vin) {
    AutonomousCarInfo car = autoCarInfoAccessor.get(vin);
    if (car == null) {
      return null;
    }
    Pose pose = car.getPose();
    if (pose != null) {
      return pose.getPoint();
    }
    return null;
  }

  @Override
  public List<ModuleInfo> getAllNodesInfo(String vin) {
    AutonomousCarInfo car = autoCarInfoAccessor.get(vin);
    if (car == null) {
      return null;
    }
    return car.getNodes();
  }

  @Override
  public List<ModuleInfo> getHardWaresInfo(String vin) {
    AutonomousCarInfo car = autoCarInfoAccessor.get(vin);
    if (car == null) {
      return null;
    }
    return car.getNodes().stream().filter(moduleInfo ->
            moduleInfo.getType() == ModuleType.HARDWARE.getCode()).collect(Collectors.toList());
  }

  @Override
  public boolean isConnected(String vin) throws CarNotExistsException {
    AutonomousCarInfo car = autoCarInfoAccessor.get(vin);
    if (car == null) {
      throw new CarNotExistsException(vin);
    }
    if (car.getState() != CarState.LOST.getState()) {
      return true;
    }
    return false;
  }

  @Override
  public String getTaskId(String vin) {
    AutonomousCarInfo car = autoCarInfoAccessor.get(vin);
    if (car != null) {
      return car.getTaskId();
    }
    return null;
  }

  @Override
  public AutonomousCarInfo get(String vin) {
    return autoCarInfoAccessor.get(vin);
  }

  @Override
  public AutonomousCarInfo getOrCreate(String vin) {
    return autoCarInfoAccessor.getOrCreate(vin);
  }

}
