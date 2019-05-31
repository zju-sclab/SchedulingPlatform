package com.skywilling.cn.scheduler.core.trajectoryalgorithm;

import com.skywilling.cn.common.model.RoutePoint;
import com.skywilling.cn.common.model.Triple;
import com.skywilling.cn.scheduler.model.StaticStation;
import com.skywilling.cn.scheduler.service.TrjPlanService;

import java.util.List;

/**
 * 则通: (-31,0)
 * 教九: (-37,101)
 * 曹楼: (-82,125)
 * 生仪: (-52,145)
 * 教九后门: (-14,1.6)
 * 则通左路口: (-31,0)
 * 则通后路口: (25.8,48.7)
 */

public class test {
    public static void main(String[] args){
        TrjPlanService ser= new GlobalTrajPlanner();
        StaticStation start = new StaticStation();
        StaticStation target = new StaticStation();
        start.setPoint(-0.01,-0.01,0,0,0,0,1.0);
        target.setPoint(-82,125,0,0,0,0,0);
        Triple<List<String>, List<Double>, List<RoutePoint>> res = ser.createTrajectory(start,target);
        System.out.println(res.first);
        System.out.println(res.second);
//        int cnt = 0;
//        for(RoutePoint p: res.third){
//            System.out.print(cnt+" ");
//            System.out.println(p);
//            cnt++;
//        }
        System.out.println("\n\n\n");
        start.setPoint(-14,1.6,0,0,0,0,1.0);
        target.setPoint(-52,145,0,0,0,0,0);
        Triple<List<String>, List<Double>, List<RoutePoint>> ress = ser.createTrajectory(start,target);
        System.out.println(ress.first);
        System.out.println(ress.second);
    }
}
