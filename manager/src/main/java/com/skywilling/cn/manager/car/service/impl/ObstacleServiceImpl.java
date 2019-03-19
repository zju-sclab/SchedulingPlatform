package com.skywilling.cn.manager.car.service.impl;

import com.skywilling.cn.manager.car.model.ObstacleInfo;
import com.skywilling.cn.manager.car.service.ObstacleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import java.util.List;


public class ObstacleServiceImpl implements ObstacleService {

    private static String COLLECTION_NAME="OBSTACLE_GEO";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(ObstacleInfo obstacleInfo) {
        mongoTemplate.insert(obstacleInfo,COLLECTION_NAME);
    }

    @Override
    public ObstacleInfo select(String obstacleId) {
        return mongoTemplate.findById(obstacleId,ObstacleInfo.class);
    }

    @Override
    public List<ObstacleInfo> queryAll() {
        return null;
    }
}
