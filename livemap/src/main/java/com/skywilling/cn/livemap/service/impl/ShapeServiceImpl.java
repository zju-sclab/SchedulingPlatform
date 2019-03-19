package com.skywilling.cn.livemap.service.impl;


import com.skywilling.cn.common.config.redis.RedisDao;
import com.skywilling.cn.livemap.core.StaticLaneShapFactory;
import com.skywilling.cn.livemap.exception.ParkNameEmptyException;
import com.skywilling.cn.livemap.model.Park;
import com.skywilling.cn.livemap.model.ShapeMap;
import com.skywilling.cn.livemap.service.ParkService;
import com.skywilling.cn.livemap.service.ShapeMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ShapeServiceImpl implements ShapeMapService {
    private static final String PREFIX="shapeMap";
    @Autowired
    RedisDao redisDao;
    @Autowired
    ParkService parkService;

    @Override
    public ShapeMap query(String parkName)  {
        Object read = redisDao.read(PREFIX + parkName);
        if(read==null){
            Park park = parkService.queryByName(parkName);
            if (park != null && park.getShapeFileUrl() != null) {
                StaticLaneShapFactory factory = new StaticLaneShapFactory();
                ShapeMap shapeMap = factory.create(park.getShapeFileUrl());
                shapeMap.setParkName(parkName);
                save(shapeMap);
                return shapeMap;
            }else {
                try {
                    throw new ParkNameEmptyException();
                } catch (ParkNameEmptyException e) {
                    e.printStackTrace();
                }
            }
        }
        return (ShapeMap)  read;
    }

    @Override
    public void save(ShapeMap map) {
        redisDao.save(PREFIX+map.getParkName(),map);
    }

}
