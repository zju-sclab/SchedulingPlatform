package com.skywilling.cn.manager.car.repository;


import com.skywilling.cn.common.model.AutoCarRequest;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;

public interface AutoCarInfoGeoAccessor {

    /**
     * 保存
     */
    void save(AutonomousCarInfo autonomousCarInfo);

    /**
     * 找到同一个lane中的所有车辆
     */
    List<AutonomousCarInfo> getByLane(String lane);

    /**
     * 找到某个节点规定距离内的所有车辆
     */
    List<AutonomousCarInfo> findByDist(GeoJsonPoint geoJsonPoint, double dis);

    /**
     * 找到离某个节点最近的车辆
     */

    AutonomousCarInfo nearVehicle(GeoJsonPoint point);

    void remove(String field, String value);

    List<AutonomousCarInfo>  getAll();

    void insert(AutonomousCarInfo autonomousCarInfo);

    void saveReq(AutoCarRequest autoCarRequest);

    List<AutoCarRequest> getAllReq();


}
