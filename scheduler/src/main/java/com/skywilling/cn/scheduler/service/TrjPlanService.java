package com.skywilling.cn.scheduler.service;

import com.skywilling.cn.common.model.RoutePoint;
import com.skywilling.cn.scheduler.model.StaticStation;

import java.util.List;

public interface TrjPlanService {
//    JSONObject createTrajectory(StaticStation start, StaticStation target);
    List<RoutePoint> createTrajectory(StaticStation start, StaticStation target);
}
