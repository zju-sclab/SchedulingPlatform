package com.skywilling.cn.scheduler.repository;

import com.skywilling.cn.common.enums.Enumerations;
import com.skywilling.cn.scheduler.model.Trip;

import java.util.List;
import java.util.Map;

public interface TripAccessor {

    void save(Trip trip);

    void update(String tripId, Map<String, Object> updates);

    Trip find(String tripId);

    List<Trip> query(int page, int size);

    List<Trip> queryByJobStatus(Enumerations.JobStatusType jobStatusType, int page, int size);

    List<Trip> queryBy(String vin, int page, int size);

    List<Trip> queryBy(String vin, String start, String end, int page, int size);

}
