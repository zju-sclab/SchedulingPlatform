package com.skywilling.cn.livemap.service;

import com.skywilling.cn.common.model.Order;
import com.skywilling.cn.livemap.model.LiveOrder;

public interface OrderService {
    void put(String username,Order order,String parkName);
    LiveOrder getLiveOrder(String parkName);
    void createLiveOrder(String parkName);
    Order get(String username,String parkName);
}
