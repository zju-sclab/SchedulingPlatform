package com.skywilling.cn.manager.car.service;


import com.skywilling.cn.livemap.model.Point;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;

public interface CarGeoService {

    /**
     * 获得离车辆最近的车辆
     */

    AutonomousCarInfo getNearestCar(String vin);

    /**
     * 获得离站点最近的车辆
     */
    AutonomousCarInfo getNearestCar(Point point);

    /**
     * 获得同一车道的车辆
     */

}
