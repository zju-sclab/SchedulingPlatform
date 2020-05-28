package com.skywilling.cn.monitor.listener;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.common.model.BasicCarResponse;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.monitor.model.DTO.TerminalInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class HeartBeatListener extends BasicListener {

    @Autowired
    ListenerMap listenerMap;
    @Autowired
    AutoCarInfoService autoCarInfoService;

    @Override
    @PostConstruct
    public void init() {
        listenerMap.addListener(TypeField.HEARTBEAT.getDesc(), this);
    }

    @Override
    public BasicCarResponse process(String vin, String body) {
        AutonomousCarInfo car = autoCarInfoService.getOrCreate(vin);
        if (car == null) {
            return null;
        }
        return new BasicCarResponse(0, null);
    }
}
