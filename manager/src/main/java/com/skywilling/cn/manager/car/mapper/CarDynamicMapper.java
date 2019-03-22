package com.skywilling.cn.manager.car.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.skywilling.cn.manager.car.model.CarDynamic;

import java.util.List;

@Mapper
public interface CarDynamicMapper {

  void save(CarDynamic carDynamic);

  void delete(String vin);

  void update(CarDynamic carDynamic);

  void bindPark(@Param("parkId") int parkId, @Param("vins") List<String> vins);

  void unbindPark(@Param("parkId") int parkId, @Param("vins") List<String> vins);

  CarDynamic query(@Param("vin") String vin);

  List<CarDynamic> queryBy(CarDynamic carDynamic);

  int checkBind(List<String> vins);

  int getTotalNumberByPark(Integer parkId);

  List<CarDynamic> queryByPark(@Param("parkId") int parkId);

  List<CarDynamic> queryFreeByPark(@Param("parkId") int parkId);

  List<CarDynamic> queryUnbound();

  List<CarDynamic> querySimulationCar();

  List<CarDynamic> queryRealCar();

  // vin 模糊查询
  List<String> queryVins(String vin);
}
