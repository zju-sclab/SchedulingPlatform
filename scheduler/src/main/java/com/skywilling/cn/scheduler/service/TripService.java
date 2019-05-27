package com.skywilling.cn.scheduler.service;

import com.skywilling.cn.common.exception.CarNotAliveException;
import com.skywilling.cn.common.exception.CarNotExistsException;
import com.skywilling.cn.common.exception.IllegalRideException;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.scheduler.model.Route;
import com.skywilling.cn.scheduler.model.Trip;

import java.util.Date;
import java.util.List;

public interface TripService {
    /**
     * 停止正在行驶的行程
     * @param tripId
     * @return
     */
    boolean stopTrip(String tripId);

    /**
     * 提交行程请求，进行路径规划后，发送给Commond模块
     * @param vin
     * @param parkName
     * @param goal
     * @return
     * @throws CarNotExistsException
     * @throws CarNotAliveException
     * @throws IllegalRideException
     */
    String submitTrip(String vin, String parkName, String from, String goal, double velocity, double acceleration)
            throws CarNotExistsException, CarNotAliveException, IllegalRideException;

    Trip submitTrjTrip(String vin, String parkName, String goal) throws CarNotExistsException,
            CarNotAliveException, IllegalRideException;

    Trip updateRoute(AutonomousCarInfo carInfo, Route route);

    Trip get(String id);

    Trip getLiveTripBy(String vin);

    public List<Trip> query(int page, int size);

    public List<Trip> queryBy(String vin, int page, int size);

    public List<Trip> queryBy(String vin, Date start, Date end, int page, int size);




}
