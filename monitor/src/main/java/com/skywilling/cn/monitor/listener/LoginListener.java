package com.skywilling.cn.monitor.listener;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.common.model.BasicCarResponse;
import com.skywilling.cn.manager.car.enumeration.CarState;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.model.CarDynamic;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import com.skywilling.cn.monitor.model.DTO.RunInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class LoginListener extends BasicListener {

    @Autowired
    ListenerMap listenerMap;
    @Autowired
    AutoCarInfoService autoCarInfoService;
    @Autowired
    CarDynamicService carDynamicService;

    @Override
    @PostConstruct
    public void init() {
        listenerMap.addListener(TypeField.LOGIN.getDesc(), this);
    }

    @Override
    public BasicCarResponse process(String vin, String body) {
        AutonomousCarInfo car = autoCarInfoService.getOrCreate(vin);
        car.setState(CarState.FREE.getState());
        RunInfo runInfo = JSONObject.parseObject(body, RunInfo.class);
        car.setTripId(runInfo.getRunId());
        autoCarInfoService.save(car);
/*        CarDynamic carDynamic = carDynamicService.query(vin);
        String parkName = carDynamic.getParkName();
        LiveMap liveMap = mapService.getMap(parkName);
        liveMap.getCarsSet().add(vin);
        *//**车辆刚登陆时的位置存储为空值 *//*
        liveMap.getCarMap().put(vin,"");*/
        BasicCarResponse basicCarResponse = new BasicCarResponse(0, new Object());
        return basicCarResponse;
    }
}
