package com.skywilling.cn.monitor.listener;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.common.model.BasicCarResponse;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;

import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import com.skywilling.cn.monitor.model.DTO.TerminalInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * ClassName TerminalInfoListener
 * Author  Lin
 * Date 2019/5/16 21:32
 **/

@Component
public class TerminalInfoListener extends BasicListener {
    @Autowired
    ListenerMap listenerMap;
    @Autowired
    AutoCarInfoService autoCarInfoService;
    @Autowired
    CarDynamicService carDynamicService;

    @Override
    @PostConstruct
    public void init() {
        listenerMap.addListener(TypeField.TERMINAL_INFO.getDesc(), this);
    }

    @Override
    public BasicCarResponse process(String vin, String body) {
        AutonomousCarInfo car = autoCarInfoService.getOrCreate(vin);
        //判断是否登陆的依据是redis是否存有该车信息,登陆逻辑会添加车辆vin到redis
        if (car== null) {
            return null;
        }
        //车端上传的终端信息，包括速度，转角，挡位，姿态（位置，方向），ros nodes，时间戳
        TerminalInfo terminalInfo = JSONObject.parseObject(body, TerminalInfo.class);
        car.setVelocity(terminalInfo.getV());
        car.setWheelAngle(terminalInfo.getWheelAngle());
        car.setGear(terminalInfo.getGear());
        car.setPose(terminalInfo.getPose());
        car.setTimestamp(terminalInfo.getTimestamp());
        car.setRosNodes(terminalInfo.getNodes());
        //car.setGeoJsonPoint(geoJsonPoint);
        //异步存入redis
        autoCarInfoService.save(car);
        //然后异步取redis数据判断，不然会变成同步操作，判断reids数据再做调度
        return new BasicCarResponse(0, new Object());
    }
}
