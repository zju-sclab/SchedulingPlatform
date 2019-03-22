package com.skywilling.cn.scheduler.service;

import com.skywilling.cn.common.enums.Enumerations;
import com.skywilling.cn.common.exception.CarNotAliveException;
import com.skywilling.cn.common.exception.CarNotExistsException;
import com.skywilling.cn.common.exception.IllegalRideException;
import com.skywilling.cn.scheduler.common.TripStatus;
import com.skywilling.cn.scheduler.model.Route;
import com.skywilling.cn.scheduler.model.Trip;

import java.util.List;

/**
 * Trip is the core concept in scheduler,  so it should be closed-loop in the ride service. client
 * should only provide essential parameters. Modify operations are not allowed in other modules.
 * RideService is the service interface for client, so it should check parameters' information.
 */
public interface RideService {

  List<Trip> findBy(String runId);

  List<Trip> queryByJobStatus(Enumerations.JobStatusType jobStatusType, int page, int size);

  /**
   * or provide builder class and return generateTripId. if car is not alive, CarNotAliveException will be
   * thrown. if car does not exists, CarNotExistsException respectively.
   */
  String submit(String vin, String parkName, String goal, double velocity, double acceleration,
                boolean usingDefaultSpeed) throws CarNotExistsException, CarNotAliveException, IllegalRideException;

  boolean stopRide(String rideId);

  /**
   * todo add interface for create ride with specified stations.
   */
  /*
  * query interface.
  * */
  Trip findById(String rideId);

  Route getRouteBy(String rideId);

  Trip getLiveRideBy(String vin);

  Trip getLatestRide(String vin, TripStatus tripStatus);

  // query ride in [offset, offset + number)
  List<Trip> queryByStatus(TripStatus tripStatus, int offset, int number);

  public List<Trip> query(int page, int size);

  public List<Trip> queryBy(String vin, int page, int size);

  public List<Trip> queryBy(String vin, String start, String end, int page, int size);


}
