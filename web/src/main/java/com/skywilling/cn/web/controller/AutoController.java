package com.skywilling.cn.web.controller;

import com.alibaba.fastjson.JSONObject;
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
    @RequestMapping(value = "/car/task/{vin}", method = RequestMethod.GET)
    public BasicResponse currentTask(@PathVariable("vin") String vin) {
        try {
            AutoTask task = taskService.getCurrentTask(vin);
            if (task != null) {
                TaskView taskView = TaskView.getFrom(task);
                return BasicResponse.buildResponse(ResultType.SUCCESS, taskView);
            }
            else throw new TaskNotExistException(vin);
        } catch (CarNotExistsException | TaskNotExistException e) {
            return BasicResponse.buildResponse(ResultType.FAILED, e.getMessage());
        }
    }

    /**
     * 查询Task的执行状态
     */
    @RequestMapping(value = "/car/task/{taskId}")
    public BasicResponse taskState(@PathVariable("taskId") String taskId) {
        Integer status = taskService.queryStatus(taskId);
        if (status == null) {
            return BasicResponse.buildResponse(ResultType.FAILED, "Can't find the task");
        }
        return BasicResponse.buildResponse(ResultType.SUCCESS, TaskState.valueOf(status));
    }

    /**
     *   车端开启自动驾驶任务，提交的参数:
     *   vin, source, goal, velocity = DEFAULT_VELOCITY, acc = DEFAULT_ACCELEARTION;
     */
    @RequestMapping(value = "/car/site/start", method = RequestMethod.POST)
    @ResponseBody
    public BasicResponse startAutonomous(RideParam rideParam) {
        try {
            CarDynamic carDynamic = carDynamicService.query(rideParam.getVin());
            /**检查数据库中是否存在车辆   */
            if (carDynamic == null) {
                return BasicResponse.buildResponse(ResultType.FAILED, "the car has not been added to a park");
            }
            Park park = parkService.query(carDynamic.getParkId());
            String rideId = tripService.submitTrip(rideParam.getVin(), park.getName(),
                    rideParam.getFrom(), rideParam.getGoal(),
                    rideParam.getVelocity(), rideParam.getAcc());
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
     *   车端开启定点到达A->B的自动循迹驾驶任务，提交的参数:
     *   vin, goal;
     */
    @RequestMapping(value = "/car/trj/start", method = RequestMethod.POST)
    @ResponseBody
    public BasicResponse startTrjAutonomous(RideParam rideParam) {
        try {
            CarDynamic carDynamic = carDynamicService.query(rideParam.getVin());
            /**检查数据库中是否存在车辆   */
            if (carDynamic == null) {
                return BasicResponse.buildResponse(ResultType.FAILED, "the car has not been added to a park");
            }
            Park park = parkService.query(carDynamic.getParkId());
            Trip trip = tripService.submitTrjTrip(rideParam.getVin(), park.getName(), rideParam.getGoal());
            JSONObject resp = new JSONObject();
            resp.put("parkName",trip.getParkName());
            resp.put("vin",trip.getVin());
            resp.put("lanes",trip.getRoute().getLanes());
            resp.put("times",trip.getRoute().getTimes());
            resp.put("from",trip.getRoute().getFrom().getName());
            resp.put("to",trip.getRoute().getTo().getName());
            return BasicResponse.buildResponse(ResultType.SUCCESS, resp);
        } catch (CarNotExistsException e) {
            return BasicResponse.buildResponse(ResultType.CAR_NOT_EXISTS, e.getMessage());
        } catch (CarNotAliveException e) {
            return BasicResponse.buildResponse(ResultType.CAR_NOT_CONNECTED, e.getMessage());
        } catch (IllegalRideException e) {
            return BasicResponse.buildResponse(ResultType.FAILED, e.getMessage());
        }
    }

    /**
     * 车端停止自动驾驶trj,site 通用
     */
    @RequestMapping(value = "/car/{vin}/stop", method = RequestMethod.GET)
    @ResponseBody
    public BasicResponse stopAutonomous(@PathVariable(name = "vin") String vin) {
        CompletableFuture<Boolean> asyncResult = autoServiceBiz.killAutonomous(vin);
        try {
            if(asyncResult.get()){
                return BasicResponse.buildResponse(ResultType.SUCCESS,("stop successful!"));
            }
        } catch (InterruptedException | ExecutionException e) {
            return BasicResponse.buildResponse(ResultType.FAILED,e.getMessage());
        }
        return BasicResponse.buildResponse(ResultType.FAILED, "car stop failed");
    }

    /**
     * 查询车辆的健康状态信息
     */
    @RequestMapping(value = "/car/{vin}/health", method = RequestMethod.GET)
    @ResponseBody
    public BasicResponse checkModuleHealth(@PathVariable(name = "vin") String vin) {
        try {
            List<ModuleInfo> moduleInfos = carInfoService.getAllNodesInfo(vin);
            BasicResponse.buildResponse(ResultType.SUCCESS, moduleInfos);
        } catch (Exception e) {
            return  BasicResponse.buildResponse(ResultType.FAILED,e.getMessage());
        }
        return BasicResponse.buildResponse(ResultType.FAILED, "failed");
    }

    /**
     * 查询ride(trip）信息
     */
    @RequestMapping(value = "car/ride/{rideId}", method = RequestMethod.GET)
    public BasicResponse getRide(@PathVariable("rideId") String rideId) {
        Trip ride = tripService.get(rideId);
        if (ride == null) {
            return BasicResponse.buildResponse(ResultType.FAILED, "ride is not exist");
        }
        return BasicResponse.buildResponse(ResultType.SUCCESS, ride);
    }

    /**
     * 旧接口
     * 租用空闲车，对于使用中的车不能再使用，只通过数据库查询标志位
     */
    @RequestMapping(value = "/car/rent", method = RequestMethod.POST)
    @ResponseBody
    public BasicResponse fireRide(RideParam rideParam) throws NullPointerException, IllegalRideException {
        try {
            CarDynamic carDynamic = carDynamicService.query(rideParam.getVin());
            if (carDynamic == null) {
                return BasicResponse.buildResponse(ResultType.FAILED, "the car isn't exist");
            }
            Park park = parkService.query(carDynamic.getParkId());
            String rideId = tripService.submitTrip(rideParam.getVin(), park.getName(),
                    rideParam.getFrom(),rideParam.getGoal(),
                    rideParam.getVelocity(),
                    rideParam.getAcc());
            if (rideId != null) {
                carDynamicService.markRentedCar(rideParam.getVin(), DriveType.AUTONOMOUS);
                return BasicResponse.buildResponse(ResultType.SUCCESS, "rent ride start successful");
            }
            return BasicResponse.buildResponse(ResultType.FAILED, "rent failed");
        } catch (CarNotAliveException e) {
            return BasicResponse.buildResponse(ResultType.CAR_NOT_CONNECTED, e.getMessage());
        } catch (CarNotExistsException e) {
            return BasicResponse.buildResponse(ResultType.CAR_NOT_EXISTS, e.getMessage());
        }
    }



    /**
     * 旧接口
     * 借助rideId停止并释放使用中的车辆
     */
    @RequestMapping(value = "/ride/{rideId}/stop", method = RequestMethod.POST)
    @ResponseBody
    public BasicResponse stopRide(@PathVariable("rideId") String rideId) {
        Trip ride = tripService.get(rideId);
        //任务空或者已经正常结束
        if (tripService.stopTrip(rideId)) {
            carDynamicService.markFreeCar(ride.getVin());
            return BasicResponse.buildResponse(ResultType.SUCCESS, "stop ride successful");
        }
        //任务非正常结束
        return BasicResponse.buildResponse(ResultType.FAILED, "stop ride failed because trip not finish");
    }

    /**
     * 查询当前所有的ride（trip）
     */
    @RequestMapping(value = "/rides", method = RequestMethod.GET)
    @ResponseBody
    public BasicResponse queryRides(@RequestParam("page") int page, @RequestParam("size") int size) {
        List<Trip> rides = tripService.query(page, size);
        return BasicResponse.buildResponse(ResultType.SUCCESS, rides);
    }

}
