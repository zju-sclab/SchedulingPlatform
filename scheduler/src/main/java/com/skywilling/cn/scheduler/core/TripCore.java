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

    /** Generate Trip ID*/
    public String generateTripId(String vin) {
        return String.format("%s_%s", System.currentTimeMillis(), vin);
    }

    /** 终止行程*/
    public void kill(Trip trip) {

        autoTaskService.stopCar(trip.getVin());
        trip.setStatus(TripStatus.KILLING.getCode());
        tripAccessor.save(trip);
    }

    /**将规划好路径的行程包装成Task，并向车辆发送*/
    public void submitTrip(Trip trip) throws NoAvailableActionFoundException, IllegalTaskException {
        //状态时已经完成则抛出异常
        if (trip.getStatus() >= TripStatus.FINISHED.getCode()) {
            throw new IllegalTaskException();
        }
        //创建根route
        Route subRoute = createSubRoute(trip);
        //新建一个task执行
        AutoTask task = createTask(trip, subRoute, trip.getParkName());
        //这里优化了一步，不再预热直接启动自动驾驶，干嘛要prepare?
        autoTaskService.submit(task);
        trip.getTaskIds().add(task.getTaskId());
        //保存Trip状态
        tripAccessor.save(trip);
    }

    /**创建任务*/
    protected AutoTask createTask(Trip trip, Route route, String parkName)
            throws NoAvailableActionFoundException {
        AutoTask task = new AutoTask();
        //这里actions是负责取出雷达点云序列拼装结果
        List<Action> actions = actionScheduler.convertToAction(parkName, route);
        task.setGmtCreate(Instant.now());
        task.setGmtModified(Instant.now());
        task.setVin(trip.getVin());
        task.setFrom(route.getFrom().getName());
        task.setTo(route.getTo().getName());
        //默认速度1.5 加速度0.5
        task.setVelocity(1.5);
        task.setAcceleration(0.5);
        task.setStatus(TaskState.INITIAL.getCode());
        task.setRideId(trip.getId());
        task.setAction(actions);
        return task;
    }

    /**根据Trip还原出原始的从A-->B的Route*/
    protected Route createSubRoute(Trip trip) {
        Route route = trip.getRoute();
        List<LiveLane> lanes = route.getLiveLanes().subList(trip.getStart(), trip.getEnd());
        Route subRoute = new Route();
        subRoute.setLiveLanes(lanes);
        subRoute.setFrom(mapService.getMap(trip.getParkName()).getJunctionMap().get(lanes.get(0).getFrom()));
        subRoute.setTo(mapService.getMap(trip.getParkName()).getJunctionMap().get(lanes.get(lanes.size() - 1).getTo()));
        return subRoute;
    }

}
