package com.skywilling.cn.livemap.service.impl;

import com.skywilling.cn.common.model.Order;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.livemap.model.LiveOrder;
import com.skywilling.cn.livemap.service.OrderService;
import com.skywilling.cn.livemap.util.CacheManager;
import com.sun.tools.corba.se.idl.constExpr.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName OrderServiceImpl
 * Author  Lin
 * Date 2019/6/10 17:30
 **/

public class OrderServiceImpl implements OrderService {
    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);
    private static final String PREFIX = "order_";
    private static ConcurrentHashMap<String, LiveOrder> orders = new ConcurrentHashMap<>();
    @Override
    public void put(String username, Order order,String parkName) {
        LiveOrder orderMap = orders.get(parkName);
        if(orderMap == null){
            LiveOrder new_order = new LiveOrder();
            try {
                new_order.getCacheManager().put(username,order);
            } catch (Exception e) {
                e.printStackTrace();
            }
            orders.put(PREFIX+parkName, new_order);
        }
    }

    @Override
    public LiveOrder getLiveOrder(String parkName) {
        LiveOrder liveOrder = orders.get(PREFIX + parkName);
        return liveOrder;
    }

    @Override
    public void createLiveOrder(String parkName) {

    }

    @Override
    public Order get(String username, String parkName) {
         LiveOrder liveOrder = getLiveOrder(parkName);
         return (Order)liveOrder.getCacheManager().get(username);
    }

    public static void main(String []args){
        OrderServiceImpl o = new OrderServiceImpl();
        String parkName = "yuquanxiaoqu3";
        o.put("l",new Order(),parkName);
        o.put("Lis",new Order(),parkName);
        o.put("ll",new Order(),parkName);
        System.out.println(orders);
        LiveOrder liveOrder = o.getLiveOrder(parkName);
        CacheManager cacheManager = liveOrder.getCacheManager();
        cacheManager.init(1);
        Object o1 = cacheManager.get("l");
        Object o2 = cacheManager.get("ll");
        Object o3 = cacheManager.get("Lis");
        System.out.println(o1+"" +o2+""+o3);
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         o1 = cacheManager.get("l");
         o2 = cacheManager.get("ll");
         o3 = cacheManager.get("Lis");
        System.out.println(o1+"" +o2+""+o3);
    }
}
