package com.skywilling.cn.manager.car.service;



import com.skywilling.cn.manager.car.model.ObstacleInfo;

import java.util.List;

public interface ObstacleService {

    /**
     * 保存障碍物信息
     * @param obstacleInfo
     */
    void save(ObstacleInfo obstacleInfo);

    /**
     * 通过id查询障碍物信息
     */
    ObstacleInfo select(String ObstacleId);

    /**
     * 查找所有Obstacle信息
     */

    List<ObstacleInfo> queryAll();

}
