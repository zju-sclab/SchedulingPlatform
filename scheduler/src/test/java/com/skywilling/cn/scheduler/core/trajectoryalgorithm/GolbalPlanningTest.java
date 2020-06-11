package com.skywilling.cn.scheduler.core.trajectoryalgorithm;

import org.junit.Test;
import com.skywilling.cn.common.model.RoutePoint;
import com.skywilling.cn.common.model.Triple;
import com.skywilling.cn.scheduler.model.StaticStation;
import com.skywilling.cn.scheduler.service.TrjPlanService;

import static org.junit.Assert.*;
import java.util.List;

public class GolbalPlanningTest {

    @Test
    public void filesgetAllFiles() {
    }

    @Test
    public void readALLFiles() {
    }

    @Test
    public void findNearstPose() {
    }

    @Test
    public void testRouting(){
        try{
            TrjPlanService ser= new GlobalTrajPlanner("DEBUG");
            StaticStation start = new StaticStation();
            StaticStation target = new StaticStation();
            // x y z坐标和四元数
            start.setPoint(80,0.9,0,0,0,0,1.0);
            target.setPoint(0,0,0,0,0,0,0);
            Triple<List<String>, List<Double>, List<RoutePoint>> res = ser.createTrajectory(start,target);
            //cross_8888 8888代表起步阶段,出站
            //cross_9999 9999代表结束阶段,入站
            for(RoutePoint each : res.third){
                System.out.println(each);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}