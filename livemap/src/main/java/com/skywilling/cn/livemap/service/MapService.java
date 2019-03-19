package com.skywilling.cn.livemap.service;

import com.skywilling.cn.livemap.model.LiveJunction;
import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.livemap.model.LiveStation;


public interface MapService {
    LiveMap getMap(String parkName);
    void save(LiveMap map);
}
