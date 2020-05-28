package com.skywilling.cn.livemap.service.impl;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.livemap.service.CarPositionService;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName CarPositionServiceImpl
 * Author  Lin
 * Date 2019/5/5 18:22
 **/

@Service
public class CarPositionServiceImpl implements CarPositionService {
    @Autowired
    MapService mapService;
    @Autowired
    AutoCarInfoService autoCarInfoService;

    /**
     * 查找lane上的所有车
     */
    @Override
    public List<String> findCarsByLaneName(String laneName, String parkName) {
        LiveMap liveMap = mapService.getMap(parkName);
        List<String> cars = liveMap.getLaneToCarMap().get(laneName);
        return cars;
    }

    /**
     * 查找车所在的lane
     */
    @Override
    public String findCarPositionByCarId(String vin, String parkName) {
        LiveMap liveMap = mapService.getMap(parkName);
        String lane = liveMap.getCarMap().get(vin);
        return lane;
    }

}
