package com.skywilling.cn.livemap.service.impl;

import com.skywilling.cn.common.model.Plan;
import com.skywilling.cn.livemap.model.LivePlan;
import com.skywilling.cn.livemap.service.PlanService;
import com.skywilling.cn.livemap.util.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName PlanServiceImpl
 * Author  Lin
 * Date 2019/6/10 17:30
 **/
@Service
public class PlanServiceImpl implements PlanService {
    private static final Logger LOG = LoggerFactory.getLogger(PlanService.class);
    private  static final String PREFIX = "plans_";
    private  ConcurrentHashMap<String, LivePlan> plans = new ConcurrentHashMap<>();
    @Override
    public void put(String username, Plan plan,String parkName) {
        LivePlan plan_map = getLivePlan(parkName);
        try {
            plan_map.getCacheManager().put(username,plan);
        } catch (Exception e) {
           LOG.info(e.getMessage());
        }
        plans.put(PREFIX+parkName, plan_map);
    }

    @Override
    public LivePlan getLivePlan(String parkName) {
        LivePlan livePlan = this.plans.get(PREFIX + parkName);
        if(livePlan == null){
            livePlan = createLivePlan(parkName);
        }
        return livePlan;
    }

    @Override
    public LivePlan createLivePlan(String parkName) {
        LivePlan livePlan = new LivePlan();
        this.plans.put(PREFIX + parkName, livePlan);
        return livePlan;
    }

    @Override
    public Plan get(String username, String parkName) {
         LivePlan livePlan = getLivePlan(parkName);
         return (Plan)livePlan.getCacheManager().get(username);
    }

    public static void main(String []args){
        PlanServiceImpl o = new PlanServiceImpl();
        String parkName = "yuquanxiaoqu3";
        o.put("l",new Plan(),parkName);
        o.put("Lis",new Plan(),parkName);
        o.put("ll",new Plan(),parkName);
        //System.out.println(plans);
        LivePlan livePlan = o.getLivePlan(parkName);
        CacheManager cacheManager = livePlan.getCacheManager();
        cacheManager.init(1);
        Object o1 = cacheManager.get("l");
        Object o2 = cacheManager.get("ll");
        Object o3 = cacheManager.get("Lis");
        System.out.println(o1+"" +o2+""+o3);
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
           LOG.info(e.getMessage());
        }
         o1 = cacheManager.get("l");
         o2 = cacheManager.get("ll");
         o3 = cacheManager.get("Lis");
        System.out.println(o1+"" +o2+""+o3);
    }
}
