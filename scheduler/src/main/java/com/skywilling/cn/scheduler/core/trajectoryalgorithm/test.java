package com.skywilling.cn.scheduler.core.trajectoryalgorithm;

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
        List<RoutePoint> res = ser.createTrajectory(start,target);
        System.out.println(res);
    }
}
