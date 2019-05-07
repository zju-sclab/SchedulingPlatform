package com.skywilling.cn.scheduler.service;


import com.skywilling.cn.livemap.model.LiveJunction;
import com.skywilling.cn.livemap.model.LiveLane;

public interface ScheduleService {

    void checkAllClient();

    void checkLaneTimeWindow(String vin, LiveLane liveLane);

    void checkJunctionLock(String vin, LiveJunction liveJunction, boolean isRealse);

}
