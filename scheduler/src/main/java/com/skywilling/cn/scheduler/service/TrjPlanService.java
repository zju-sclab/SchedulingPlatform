package com.skywilling.cn.scheduler.service;

import com.skywilling.cn.common.model.RoutePoint;
import com.skywilling.cn.common.model.Triple;
import com.skywilling.cn.scheduler.model.StaticStation;
import org.springframework.stereotype.Service;

import java.util.List;

public interface TrjPlanService {
//    JSONObject createTrajectory(StaticStation start, StaticStation target);
    Triple<List<String>, List<Double>, List<RoutePoint>> createTrajectory(StaticStation start, StaticStation target);
}
