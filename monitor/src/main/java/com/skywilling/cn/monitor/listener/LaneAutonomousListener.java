package com.skywilling.cn.monitor.listener;

import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.common.model.BasicCarResponse;
import com.skywilling.cn.common.model.RoutePoint;
import com.skywilling.cn.common.model.Triple;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
//import com.skywilling.cn.scheduler.model.StaticStation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
//import com.skywilling.cn.scheduler.service.TrjPlanService;

import java.util.List;


@Component
public class LaneAutonomousListener extends BasicListener{
    @Autowired
    ListenerMap listenerMap;
    @Autowired
    AutoCarInfoService autoCarInfoService;
//    @Autowired
//    TrjPlanService trjPlanService;

    @Override
    @PostConstruct
    public void init() {
        listenerMap.addListener(TypeField.FIRE_LANE_AUTONOMOUS.getDesc(), this);
    }

    @Override
    public BasicCarResponse process(String vin, String body) {
        //通过service来进行服务
//        StaticStation start = new StaticStation();
//        StaticStation target = new StaticStation();
//        // x y z坐标和四元数
//        start.setPoint(-0.01,-0.01,0,0,0,0,1.0);
//        target.setPoint(-82,125,0,0,0,0,0);
//        Triple<List<String>, List<Double>, List<RoutePoint>> res = ser.createTrajectory(start,target);
        return new BasicCarResponse(0, null);
    }
}
