package com.skywilling.cn.scheduler.core;

import com.skywilling.cn.livemap.model.LiveJunction;
import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.scheduler.core.routealgorithm.BaseRouteAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class RouteLogic {
    HashMap<String, BaseRouteAlgorithm> routeAlgorithmHashMap=new HashMap<>();

    @Value("route.algorithm")
    private String algorithm;

   public List<LiveLane> routePlanning(LiveMap map, LiveJunction from, LiveJunction to) {

       BaseRouteAlgorithm baseRouteAlgorithm = routeAlgorithmHashMap.get(algorithm);
       if(baseRouteAlgorithm !=null){
           return baseRouteAlgorithm.process(map, from, to);
       }
       return null;
   }

   public void addAlgorithm(String name, BaseRouteAlgorithm algorithm){
       this.routeAlgorithmHashMap.putIfAbsent(name,algorithm);
   }


}
