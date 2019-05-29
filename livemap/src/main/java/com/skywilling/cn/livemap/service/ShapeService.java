package com.skywilling.cn.livemap.service;


import com.skywilling.cn.livemap.model.LaneShape;
import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.livemap.model.ShapeMap;

import java.util.List;


public interface ShapeService {


    LaneShape query(String parkName, String laneName);

    void create(String parkName);

    void save(LaneShape laneShape);





}
