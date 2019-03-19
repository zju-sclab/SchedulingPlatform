package com.skywilling.cn.manager.car.service.impl;

import com.github.pagehelper.PageHelper;

import com.github.pagehelper.PageInfo;
import com.skywilling.cn.common.exception.CarNotVinException;
import com.skywilling.cn.livemap.model.Point;
import com.skywilling.cn.manager.car.enumeration.DriveType;
import com.skywilling.cn.manager.car.mapper.CarDynamicMapper;
import com.skywilling.cn.manager.car.model.CarDynamic;
import com.skywilling.cn.manager.car.model.ModuleInfo;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class CarDynamicServiceImpl implements CarDynamicService {

  @Autowired
  private CarDynamicMapper carDynamicMapper;

  @Transactional
  @Override
  public void save(CarDynamic carDynamic) throws CarNotVinException {
    if (carDynamic.getVin() == null) throw new CarNotVinException();
    carDynamicMapper.save(carDynamic);
  }

  @Transactional
  @Override
  public void save(List<CarDynamic> carDynamics) throws CarNotVinException {
    for (CarDynamic carDynamic : carDynamics) {
      if (carDynamic.getVin() == null) throw new CarNotVinException();
      carDynamicMapper.save(carDynamic);
    }
  }

  @Transactional
  @Override
  public void delete(String vin) {
    carDynamicMapper.delete(vin);
  }

  @Transactional
  @Override
  public void update(CarDynamic carDynamic) throws CarNotVinException {
    if (carDynamic.getVin() == null) throw new CarNotVinException();
    carDynamicMapper.update(carDynamic);
  }

  @Override
  public void updateLocation(String vin, Point point) {
    CarDynamic carDynamic = new CarDynamic();
    carDynamic.setVin(vin);
    carDynamic.setLongitude(point.getX());
    carDynamic.setLatitude(point.getY());
    carDynamicMapper.update(carDynamic);
  }

  @Override
  public void updateModuleStatus(String vin, List<ModuleInfo> moduleInfos) {
    boolean match = moduleInfos.stream().anyMatch(moduleInfo -> moduleInfo.getStatus() != 0);
    CarDynamic carDynamic = new CarDynamic();
    carDynamic.setVin(vin);
    carDynamic.setModuleStatus(match ? 1 : 0);
    carDynamicMapper.update(carDynamic);
  }


  @Override
  public CarDynamic query(String vin) {
    return carDynamicMapper.query(vin);
  }

  @Override
  public List<String> queryVins(String vin) {
    return carDynamicMapper.queryVins(vin);
  }

  @Override
  public PageInfo<CarDynamic> queryBy(CarDynamic carDynamic, int page, int size) {
    return PageHelper.startPage(page, size).doSelectPageInfo(() -> carDynamicMapper.queryBy(carDynamic));
  }

  @Override
  public int getTotalNumberByPark(Integer parkId) {
    return carDynamicMapper.getTotalNumberByPark(parkId);
  }

  @Override
  public PageInfo<CarDynamic> queryByPark(int parkId, int page, int size) {
    return PageHelper.startPage(page, size).doSelectPageInfo(() -> carDynamicMapper.queryByPark(parkId));
  }

  @Override
  public PageInfo<CarDynamic> queryFreeByPark(int parkId, int page, int size) {
    return PageHelper.startPage(page, size).doSelectPageInfo(() -> carDynamicMapper.queryFreeByPark(parkId));
  }

  @Override
  public PageInfo<CarDynamic> queryUnbound(int page, int size) {
    return PageHelper.startPage(page, size).doSelectPageInfo(() -> carDynamicMapper.queryUnbound());
  }

  @Override
  public PageInfo<CarDynamic> querySimulationCar(int page, int size) {
    return PageHelper.startPage(page, size).doSelectPageInfo(() -> carDynamicMapper.querySimulationCar());
  }

  @Override
  public PageInfo<CarDynamic> queryRealCar(int page, int size) {
    return PageHelper.startPage(page, size).doSelectPageInfo(() -> carDynamicMapper.queryRealCar());
  }

  @Transactional
  @Override
  public boolean markRentedCar(String vin, DriveType driveType) {
    try {
      CarDynamic dynamic = new CarDynamic();
      dynamic.setVin(vin);
      dynamic.setUseStatus(1);
      dynamic.setDriveMode(driveType.getCode());
      return true;
    } catch (Exception e) {
      return  false;
    }
  }

  @Override
  public boolean markFreeCar(String vin) {
    try {
      CarDynamic dynamic = new CarDynamic();
      dynamic.setVin(vin);
      dynamic.setUseStatus(0);
      return true;
    } catch (Exception e) {
      return  false;
    }
  }

  @Transactional
  @Override
  public boolean bindPark(int parkId, String vin) {
    List<String> vins = Collections.singletonList(vin);
    return bindPark(parkId, vins);
  }

  @Transactional
  @Override
  public boolean bindPark(int parkId, List<String> vins) {
    int count = carDynamicMapper.checkBind(vins);
    if (count > 0) return false;
    carDynamicMapper.bindPark(parkId, vins);
    return true;
  }

  @Transactional
  @Override
  public boolean unbindPark(int parkId, String vin) {
    List<String> vins = Collections.singletonList(vin);
    return unbindPark(parkId, vins);
  }

  @Transactional
  @Override
  public boolean unbindPark(int parkId, List<String> vins) {
    carDynamicMapper.unbindPark(parkId, vins);
    return true;
  }
}
