package com.skywilling.cn.monitor.listener;

import com.skywilling.cn.connection.model.ACK;
import com.skywilling.cn.manager.car.enumeration.CarState;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class FailureListener extends BasicListener {
    @Autowired
    ListenerMap listenerMap;
    @Autowired
    AutoCarInfoService autoCarInfoService;

    @Override
    @PostConstruct
    public void init(){
        listenerMap.addListener(ACK.FAILURE.getDesc(),this);
    }

    @Override
    public boolean process(String vin, String body){
        AutonomousCarInfo car = autoCarInfoService.getOrCreate(vin);
        car.setState(CarState.LOST.getState());
        car.setNodes(null);
        car.setTaskId(null);
        autoCarInfoService.save(car);
        return true;
    }
}
