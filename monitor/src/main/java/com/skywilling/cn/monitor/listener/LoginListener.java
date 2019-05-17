package com.skywilling.cn.monitor.listener;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.common.model.BasicCarResponse;
import com.skywilling.cn.manager.car.enumeration.CarState;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
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
        BasicCarResponse basicCarResponse = new BasicCarResponse(0, new Object());
        return basicCarResponse;
    }
}
