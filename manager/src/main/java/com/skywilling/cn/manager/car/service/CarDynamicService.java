package com.skywilling.cn.manager.car.service;


import com.github.pagehelper.PageInfo;
import com.skywilling.cn.common.exception.CarNotVinException;
import com.skywilling.cn.common.model.Position;
import com.skywilling.cn.manager.car.enumeration.DriveType;
import com.skywilling.cn.manager.car.model.CarDynamic;
import com.skywilling.cn.manager.car.model.ModuleInfo;

import java.util.List;

public interface CarDynamicService {

  void save(CarDynamic carDynamic) throws CarNotVinException;

  void save(List<CarDynamic> carDynamics) throws CarNotVinException;

  void delete(String vin);


  void update(CarDynamic carDynamic) throws CarNotVinException;

  void updateLocation(String vin, Position point);

  void updateModuleStatus(String vin, List<ModuleInfo> moduleInfos);

  boolean markRentedCar(String vin, DriveType driveType);

  boolean markFreeCar(String vin);

  boolean bindPark(int parkId, String vin);

  boolean bindPark(int parkId, List<String> vins);

  boolean unbindPark(int parkId, String vin);

  boolean unbindPark(int parkId, List<String> vins);


  CarDynamic query(String vin);

  List<String> queryVins(String vin);

  PageInfo<CarDynamic> queryBy(CarDynamic carDynamic, int page, int size);

  PageInfo<CarDynamic> queryUnbound(int page, int size);

  PageInfo<CarDynamic> querySimulationCar(int page, int size);

  PageInfo<CarDynamic> queryRealCar(int page, int size);

  PageInfo<CarDynamic> queryByPark(int parkId, int page, int size);

  PageInfo<CarDynamic> queryFreeByPark(int parkId, int page, int size);

  int getTotalNumberByPark(Integer parkId);
}
