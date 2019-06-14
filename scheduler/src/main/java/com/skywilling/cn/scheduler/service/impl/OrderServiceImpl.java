package com.skywilling.cn.scheduler.service.impl;

import com.skywilling.cn.common.model.*;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.scheduler.core.trajectoryalgorithm.GlobalTrajPlanner;
import com.skywilling.cn.scheduler.model.StaticStation;
import com.skywilling.cn.scheduler.service.OrderService;
import com.skywilling.cn.scheduler.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName OrderServiceImpl
 * Author  Lin
 * Date 2019/6/10 10:15
 **/
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    GlobalTrajPlanner globalTrajPlanner;
    @Autowired
    MapService mapService;
    @Autowired
    TripService tripService;

    @Override
    public List<Plan> createPlanByOrders(Order[] order) {
        List<Plan> plans = new ArrayList<>();
        for(int i = 0; i < order.length; i++){
            Plan plan = createPlanByOrder(order[i]);
            plans.add(plan);
        }
        return plans;
    }

    @Override
    public Plan createPlanByOrder(Order order) {
        StaticStation s = new StaticStation();
        StaticStation e = new StaticStation();
        Node s_node = mapService.getNode(order.getOutset(),order.getParkName());
        Node e_node = mapService.getNode(order.getDestination(),order.getParkName());
        StaticStation s_station = tripService.createStaticPointByNode(s_node);
        StaticStation e_station = tripService.createStaticPointByNode(e_node);
        Triple<List<String>,List<Double>,List<RoutePoint>> re = globalTrajPlanner.createTrajectory(s_station,e_station);
        Plan plan = new Plan(order,re.second,re.first);
        return plan;
    }

}
