package com.skywilling.cn.scheduler.core.trajectoryalgorithm;

import com.skywilling.cn.common.model.RoutePoint;
import com.skywilling.cn.common.model.Triple;
import com.skywilling.cn.scheduler.model.StaticStation;
import com.skywilling.cn.scheduler.service.TrjPlanService;

import java.util.List;

public class test {
    public static void main(String[] args){
        TrjPlanService ser= new GlobalTrajPlanner();
        StaticStation start = new StaticStation();
        StaticStation target = new StaticStation();
        start.setPoint(-0.01,-0.01,0,0,0,0,1.0);
        target.setPoint(-37,101,0,0,0,0,0);
        Triple<List<String>, List<Double>, List<RoutePoint>> res = ser.createTrajectory(start,target);
        System.out.println(res.first);
        System.out.println(res.second);
        int cnt = 0;
        for(RoutePoint p: res.third){
            System.out.print(cnt+" ");
            System.out.println(p);
            cnt++;
        }
    }
}
