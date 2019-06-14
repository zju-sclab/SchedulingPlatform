package com.skywilling.cn.livemap.service;

import com.skywilling.cn.common.model.Plan;
import com.skywilling.cn.livemap.model.LivePlan;

public interface PlanService {
    void put(String username,Plan plan,String parkName);
    LivePlan getLivePlan(String parkName);
    LivePlan createLivePlan(String parkName);
    Plan get(String username,String parkName);
}
