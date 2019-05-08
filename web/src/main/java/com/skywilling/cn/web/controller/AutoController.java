package com.skywilling.cn.web.controller;

import com.skywilling.cn.command.biz.AutoServiceBiz;
import com.skywilling.cn.common.enums.ResultType;
import com.skywilling.cn.common.exception.CarNotAliveException;
import com.skywilling.cn.common.exception.CarNotExistsException;
import com.skywilling.cn.common.exception.IllegalRideException;
import com.skywilling.cn.common.model.BasicResponse;

import com.skywilling.cn.common.model.TaskState;
import com.skywilling.cn.connection.common.exception.TaskNotExistException;
import com.skywilling.cn.livemap.model.Park;
import com.skywilling.cn.livemap.service.ParkService;
import com.skywilling.cn.manager.car.enumeration.DriveType;
import com.skywilling.cn.manager.car.model.CarDynamic;
import com.skywilling.cn.manager.car.model.ModuleInfo;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import com.skywilling.cn.manager.car.service.CarInfoService;
import com.skywilling.cn.manager.task.model.AutoTask;
import com.skywilling.cn.manager.task.service.TaskService;


import com.skywilling.cn.scheduler.model.Trip;
import com.skywilling.cn.scheduler.service.TripService;
import com.skywilling.cn.web.model.RideParam;
import com.skywilling.cn.web.model.view.TaskView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@CrossOrigin
@RequestMapping(value = "/api/v2/auto")
@RestController
public class AutoController {

    private static final Logger LOG = LoggerFactory.getLogger(AutoController.class);

    @Autowired
    private AutoServiceBiz autoServiceBiz;

    @Autowired
    private CarInfoService carInfoService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TripService tripService;

    @Autowired
    private CarDynamicService carDynamicService;

    @Autowired
    private ParkService parkService;

    /**
     * 查询当前车辆任务状态
     */
    @RequestMapping(value = "/car/{vin}/current", method = RequestMethod.GET)
    public BasicResponse currentTask(@PathVariable("vin") String vin) {
        try {
            AutoTask task = taskService.getCurrentTask(vin);
            if (task != null) {
                TaskView taskView = TaskView.getFrom(task);
                return BasicResponse.buildResponse(ResultType.SUCCESS, taskView);
            }
            throw new TaskNotExistException(vin);
        } catch (CarNotExistsException | TaskNotExistException e) {
            return BasicResponse.buildResponse(ResultType.FAILED, e.getMessage());
        }
    }

    /**
     * 查询Task的执行状态
     */
    @RequestMapping(value = "/task/{taskId}/state")
    public BasicResponse taskState(@PathVariable("taskId") String taskId) {
        Integer status = taskService.queryStatus(taskId);
        if (status == null) {
            return BasicResponse.buildResponse(ResultType.FAILED, "Can't find the task");
        }
        return BasicResponse.buildResponse(ResultType.SUCCESS, TaskState.valueOf(status));
    }

    /**
     *   车端开启自动驾驶任务，提交的参数是
     *   private String vin;
     *   private String source;
     *   private String goal;
     *   private boolean usingMapSpeed = false;
     *   private double velocity = DEFAULT_VELOCITY;
     *   private double acceleartion = DEFAULT_ACCELEARTION;
     */
    @RequestMapping(value = "/car/start", method = RequestMethod.POST)
    @ResponseBody
    public BasicResponse startAutonomous(RideParam rideParam) {
        try {
            CarDynamic carDynamic = carDynamicService.query(rideParam.getVin());
//            if (carDynamic == null) {
//                return BasicResponse.buildResponse(ResultType.FAILED, "the car is not exist");
//            }
            Park park = parkService.query(carDynamic.getParkId());
            String rideId = tripService.submitTrip(rideParam.getVin(), park.getName(), rideParam.getFrom(),
                    rideParam.getGoal(), rideParam.getVelocity(), rideParam.getAcc());
            if (rideId != null) {
                return BasicResponse.buildResponse(ResultType.SUCCESS, rideId);
            }
            return BasicResponse.buildResponse(ResultType.FAILED, null);
        } catch (CarNotExistsException e) {
            return BasicResponse.buildResponse(ResultType.CAR_NOT_EXISTS, null);
        } catch (CarNotAliveException e) {
            return BasicResponse.buildResponse(ResultType.CAR_NOT_CONNECTED, null);
        } catch (IllegalRideException e) {
            return BasicResponse.buildResponse(ResultType.FAILED, null);
        }
    }

    /**
     * 车端停止自动驾驶
     */
    @RequestMapping(value = "/car/{vin}/stop", method = RequestMethod.GET)
    @ResponseBody
    public BasicResponse stopAutonomous(@PathVariable(name = "vin") String vin) {
        CompletableFuture<Boolean> asyncResult = autoServiceBiz.killAutonomous(vin);
        List<Trip> trips = tripService.queryBy(vin,1,12);
        try {
            for(Trip trip : trips) {
                if (asyncResult.get()) {
                    if (trip.getTaskIds() == null || trip.getTaskIds().size() == 0)
                        return BasicResponse.buildResponse(ResultType.SUCCESS, null);
                }
                return BasicResponse.buildResponse(ResultType.FAILED, null);
            }
        } catch (InterruptedException | ExecutionException e) {
            LOG.error(e.getMessage());
        }
        return BasicResponse.buildResponse(ResultType.FAILED, null);
    }

    /**
     * 查询车辆的健康状态信息
     */
   /* @RequestMapping(value = "/car/{vin}/health", method = RequestMethod.GET)
    @ResponseBody
    public BasicResponse checkModuleHealth(@PathVariable(name = "vin") String vin) {
        try {
            List<ModuleInfo> moduleInfos = carInfoService.getAllNodesInfo(vin);
            BasicResponse.buildResponse(ResultType.SUCCESS, moduleInfos);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return BasicResponse.buildResponse(ResultType.FAILED, null);
    }*/

    @RequestMapping(value = "car/ride/{rideId}", method = RequestMethod.GET)
    public BasicResponse getRide(@PathVariable("rideId") String rideId) {
        Trip ride = tripService.get(rideId);
        if (ride == null) {
            return BasicResponse.buildResponse(ResultType.FAILED, null);
        }
        return BasicResponse.buildResponse(ResultType.SUCCESS, ride);
    }

    @RequestMapping(value = "/car/rent", method = RequestMethod.POST)
    @ResponseBody
    public BasicResponse fireRide(RideParam rideParam) {
        try {
            CarDynamic carDynamic = carDynamicService.query(rideParam.getVin());
            if (carDynamic == null) {
                return BasicResponse.buildResponse(ResultType.FAILED, "the car isn't exist");
            }
            Park park = parkService.query(carDynamic.getParkId());
            String rideId = tripService.submitTrip(rideParam.getVin(), park.getName(), rideParam.getFrom(),rideParam.getGoal(),
                    rideParam.getVelocity(),
                    rideParam.getAcc());
            if (rideId != null) {
                carDynamicService.markRentedCar(rideParam.getVin(), DriveType.AUTONOMOUS);
                return BasicResponse.buildResponse(ResultType.SUCCESS, rideId);
            }
            return BasicResponse.buildResponse(ResultType.FAILED, null);
        } catch (NullPointerException e) {
            return BasicResponse.buildResponse(ResultType.FAILED, e.getMessage());
        } catch (CarNotAliveException e) {
            return BasicResponse.buildResponse(ResultType.CAR_NOT_CONNECTED, "");
        } catch (CarNotExistsException e) {
            return BasicResponse.buildResponse(ResultType.CAR_NOT_EXISTS, "");
        } catch (IllegalRideException e) {
            return BasicResponse.buildResponse(ResultType.FAILED, null);
        }
    }

    @RequestMapping(value = "/ride/{rideId}", method = RequestMethod.GET)
    @ResponseBody
    public BasicResponse getWholeRideInfo(@PathVariable("rideId") String rideId) {
        Trip ride = tripService.get(rideId);
        if (ride != null) {
            return BasicResponse.buildResponse(ResultType.SUCCESS, ride);
        }
        return BasicResponse.buildResponse(ResultType.FAILED, null);
    }


    @RequestMapping(value = "/ride/{rideId}/stop", method = RequestMethod.POST)
    @ResponseBody
    public BasicResponse stopRide(@PathVariable("rideId") String rideId) {
        Trip ride = tripService.get(rideId);
        if (tripService.stopTrip(rideId)) {
            carDynamicService.markFreeCar(ride.getVin());
            return BasicResponse.buildResponse(ResultType.SUCCESS, null);
        }
        return BasicResponse.buildResponse(ResultType.FAILED, null);
    }

    @RequestMapping(value = "/rides", method = RequestMethod.GET)
    @ResponseBody
    public BasicResponse queryRides(@RequestParam("page") int page, @RequestParam("size") int size) {
        List<Trip> rides = tripService.query(page, size);
        return BasicResponse.buildResponse(ResultType.SUCCESS, rides);
    }

  /*  @RequestMapping(value = "/ride/status/{status}", method = RequestMethod.GET)
    public BasicResponse getRidesByJobStatus(@PathVariable("status") int status,
                                             @RequestParam("page") int page,
                                             @RequestParam("size") int size) {
        try {
            RideStatus rideStatus= RideStatus.valueOf(status);
            List<Trip> rides = tripService.queryByStrus(rideStatus, page, size);
            return BasicResponse.buildResponse(ResultType.SUCCESS, rides);
        } catch (Exception e) {
            LOG.error("getRidesByJobStatus: " + e.getMessage());
            return BasicResponse.buildResponse(ResultType.FAILED, e.getMessage());
        }
    }*/
}
