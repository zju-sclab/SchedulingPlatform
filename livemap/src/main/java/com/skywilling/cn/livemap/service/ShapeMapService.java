package com.skywilling.cn.livemap.service;


import com.skywilling.cn.livemap.model.ShapeMap;


public interface ShapeMapService {

    ShapeMap query(String parkName);
    void save(ShapeMap map);





}
