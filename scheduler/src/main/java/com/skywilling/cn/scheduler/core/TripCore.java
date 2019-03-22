package com.skywilling.cn.scheduler.core;



import com.skywilling.cn.command.service.AutoTaskService;
import com.skywilling.cn.common.exception.IllegalTaskException;
import com.skywilling.cn.common.exception.park.NoAvailableActionFoundException;
import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.manager.car.enumeration.TaskState;
import com.skywilling.cn.manager.car.model.Action;
import com.skywilling.cn.manager.task.model.AutoTask;
import com.skywilling.cn.scheduler.common.TripStatus;
import com.skywilling.cn.scheduler.model.Route;
import com.skywilling.cn.scheduler.model.Trip;
import com.skywilling.cn.scheduler.repository.TripAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.List;

@Service
public class TripCore {

  @Autowired
  AutoTaskService autoTaskService;
  @Autowired
  TripAccessor tripAccessor;
  @Autowired
  ActionScheduler actionScheduler;
  @Autowired
  MapService mapService;



  public String generateTripId(String vin) {
    return String.format("%s_%s", System.currentTimeMillis(), vin);
  }

  /**
   * 终止行程
   * @param trip
   */
  public void kill(Trip trip) {

    autoTaskService.stopCar(trip.getVin());
    trip.setStatus(TripStatus.KILLING.getCode());
    tripAccessor.save(trip);
  }

  /**
   * 将规划好路径的行程包装成Task，并向车辆发送
   * @param trip
   */
  public void submitTrip(Trip trip) throws NoAvailableActionFoundException, IllegalTaskException {
    if(trip.getStatus()>= TripStatus.FINISHED.getCode()){
      throw new IllegalTaskException();
    }

    Route subRoute=createSubRoute(trip);
    AutoTask task=createTask(trip,subRoute,trip.getParkName());
    autoTaskService.start(task);
    trip.getTaskIds().add(task.getTaskId());
    tripAccessor.save(trip);
  }



  protected AutoTask createTask(Trip trip, Route route, String parkName)
          throws NoAvailableActionFoundException {
    AutoTask task = new AutoTask();
    List<Action> actions = actionScheduler.convertToAction(parkName, route);
    task.setGmtCreate(Instant.now());
    task.setGmtModified(Instant.now());
    task.setVin(trip.getVin());
    task.setFrom(route.getFrom().getName());
    task.setTo(route.getTo().getName());
    task.setVelocity(1.5);
    task.setAcceleration(0.5);
    task.setStatus(TaskState.INITIAL.getCode());
    task.setRideId(trip.getId());
    task.setAction(actions);
    return task;
  }

  protected Route createSubRoute(Trip trip) {
    Route route=trip.getRoute();
    List<LiveLane> lanes=route.getLiveLanes().subList(trip.getStart(),trip.getEnd());
    Route subRoute=new Route();
    subRoute.setLiveLanes(lanes);
    subRoute.setFrom(mapService.getMap(trip.getParkName()).getJunctionMap().get(lanes.get(0).getFrom()));
    subRoute.setTo(mapService.getMap(trip.getParkName()).getJunctionMap().get(lanes.get(lanes.size()-1).getTo()));
    return subRoute;
  }

}
