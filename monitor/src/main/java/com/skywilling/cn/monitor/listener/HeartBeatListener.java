package com.skywilling.cn.monitor.listener;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.monitor.model.DTO.TerminalInfo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;


public class HeartBeatListener extends BasicListener {

    @Autowired
    ListenerMap listenerMap;
    @Autowired
    AutoCarInfoService autoCarInfoService;

    @Override
    @PostConstruct
    public void init(){
        listenerMap.addListener(TypeField.HEARTBEAT.getDesc(),this);
    }

    @Override
    public boolean process(String vin, String body){
        AutonomousCarInfo car = autoCarInfoService.getOrCreate(vin);
        if (car == null) {
            return false;
        }
        TerminalInfo terminalInfo=JSONObject.parseObject(body,TerminalInfo.class);
        car.setVelocity(terminalInfo.getV());
        car.setWheelAngle(terminalInfo.getWheelAngle());
        car.setGear(terminalInfo.getGear());
        car.setPose(terminalInfo.getPose());
        car.setLocation(terminalInfo.getPose());
        car.setNodes(terminalInfo.getNodes());
        car.setTimestamp(terminalInfo.getTimestamp());
        car.setLane(terminalInfo.getLane());
        autoCarInfoService.save(car);

        return true;
    }
}
