package com.skywilling.cn.livemap.service;


import com.skywilling.cn.livemap.model.LaneShape;
import com.skywilling.cn.livemap.model.ShapeMap;

import java.util.List;


public interface ShapeMapService {


    List<LaneShape> query(String parkName,List<String> lanes);

    void create(String parkName);

    void save(LaneShape laneShape);





}
