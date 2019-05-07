package com.skywilling.cn.scheduler.repository;

import com.skywilling.cn.common.enums.Enumerations;
import com.skywilling.cn.scheduler.model.Trip;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TripAccessor {

    void save(Trip trip);

    void update(String tripId, Map<String, Object> updates);

    Trip find(String tripId);

    Trip findLastedBy(String vin);

    List<Trip> query(int page, int size);

    List<Trip> queryByJobStatus(Enumerations.JobStatusType jobStatusType, int page, int size);

    List<Trip> queryBy(String vin, int page, int size);

    List<Trip> queryBy(String vin, Date start, Date end, int page, int size);

}
