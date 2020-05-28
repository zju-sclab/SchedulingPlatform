package com.skywilling.cn.monitor.listener;


import com.skywilling.cn.common.model.BasicCarResponse;
import com.skywilling.cn.manager.car.enumeration.CarState;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.monitor.model.DTO.ACK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class FailureListener extends BasicListener {
    @Autowired
    ListenerMap listenerMap;
    @Autowired
    AutoCarInfoService autoCarInfoService;

    @Override
    @PostConstruct
    public void init() {
        listenerMap.addListener(ACK.FAILURE.getDesc(), this);
    }

    @Override
    public BasicCarResponse process(String vin, String body) {
        AutonomousCarInfo car = autoCarInfoService.getOrCreate(vin);
        car.setState(CarState.LOST.getState());
        car.setRosNodes(null);
        car.setTaskId(null);
        autoCarInfoService.save(car);
        return null;
    }
}
