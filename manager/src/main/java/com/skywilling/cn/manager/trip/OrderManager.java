package com.skywilling.cn.manager.trip;

import com.skywilling.cn.common.model.StationOrder;
import lombok.Data;
import org.apache.kafka.common.metrics.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class OrderManager {
    //放置订单的信息 在进行的订单
    private ConcurrentHashMap<String, StationOrder> stationMap = new ConcurrentHashMap<>();
    List<StationOrder> allOrders = new ArrayList<>();


    public boolean putOrder(String vin, StationOrder stationOrder){
        if(stationMap.get(vin)==null){
            stationOrder.setStatus(1);
            stationMap.put(vin, stationOrder);
            return true;
        }else{
            return false;
        }
    }

    //更新订单的信息
    public boolean updateOrderStatus(String vin, int status){
        if(stationMap.get(vin)!=null){

            return true;
        }else{
            return false;
        }
    }
}
