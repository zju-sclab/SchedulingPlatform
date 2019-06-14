package com.skywilling.cn.scheduler.service;

import com.skywilling.cn.common.model.Order;
import com.skywilling.cn.common.model.Plan;

import java.util.List;

public interface OrderService {
    List<Plan> createPlanByOrders(Order[] order);
    Plan createPlanByOrder(Order order);
}
