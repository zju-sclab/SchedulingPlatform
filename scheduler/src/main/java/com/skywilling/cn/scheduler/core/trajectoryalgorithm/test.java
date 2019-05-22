package com.skywilling.cn.scheduler.core.trajectoryalgorithm;

import com.skywilling.cn.common.model.RoutePoint;
import com.skywilling.cn.common.model.Triple;
import com.skywilling.cn.common.model.Tuple;
import com.skywilling.cn.scheduler.model.*;
import com.skywilling.cn.scheduler.service.TrjPlanService;
import java.util.List;

public class test {
    public static void main(String[] args){
        TrjPlanService ser= new GlobalTrajPlanner();
        StaticStation start = new StaticStation();
        StaticStation target = new StaticStation();
        start.setPoint(0,0,0,0,0,0,0);
        target.setPoint(20,50,0,0,0,0,0);
        Triple<List<String>, List<Double>, List<RoutePoint>> res = ser.createTrajectory(start,target);
        System.out.println(res.first);
        System.out.println(res.second);
        System.out.println(res.third);
    }
}
