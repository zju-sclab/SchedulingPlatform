package com.skywilling.cn.livemap.service.impl;


import com.skywilling.cn.common.config.redis.RedisDao;
import com.skywilling.cn.livemap.core.StaticLaneShapFactory;
import com.skywilling.cn.livemap.exception.ParkNameEmptyException;
import com.skywilling.cn.livemap.model.LaneShape;
import com.skywilling.cn.livemap.model.Park;
import com.skywilling.cn.livemap.service.ParkService;
import com.skywilling.cn.livemap.service.ShapeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ShapeServiceImpl implements ShapeService {
    private static final String PREFIX = "shapeMap_";
    @Autowired
    RedisDao redisDao;
    @Autowired
    ParkService parkService;
    @Autowired
    StaticLaneShapFactory staticLaneShapFactory;

    @Override
    public LaneShape query(String parkName, String laneName) {


        Object read = redisDao.read(PREFIX + parkName + laneName);
        if (read == null) {
            Park park = parkService.queryByName(parkName);
            checkAndCreate(parkName, park);
            read = redisDao.read(PREFIX + parkName + laneName);
        }
        return (LaneShape) read;
    }

    @Override
    public void create(String parkName) {
        Park park = parkService.queryByName(parkName);
        checkAndCreate(parkName, park);
    }

    @Override
    public void save(LaneShape laneShape) {
        redisDao.save(PREFIX + laneShape.getParkName() + laneShape.getName(), laneShape);
    }


    private void checkAndCreate(String parkName, Park park) {
        if (park != null && park.getShapeFileUrl() != null) {
            staticLaneShapFactory.create(parkName, park.getShapeFileUrl());

        } else {
            try {
                throw new ParkNameEmptyException();
            } catch (ParkNameEmptyException e) {
                e.printStackTrace();
            }
        }
    }
}
