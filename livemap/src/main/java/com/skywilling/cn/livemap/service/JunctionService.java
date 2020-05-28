package com.skywilling.cn.livemap.service;

import com.skywilling.cn.livemap.model.LiveJunction;


public interface JunctionService {
    LiveJunction get(String parkName, String junctionName);
    void addJunction(String parkName, LiveJunction junction);
}
