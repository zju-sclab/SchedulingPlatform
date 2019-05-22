package com.skywilling.cn.web.controller;

import com.github.pagehelper.PageInfo;
import com.skywilling.cn.common.enums.ResultType;

import com.skywilling.cn.common.exception.CarNotVinException;
import com.skywilling.cn.common.model.BasicResponse;

import com.skywilling.cn.common.model.Position;
import com.skywilling.cn.connection.infrastructure.client.ClientService;
import com.skywilling.cn.common.model.Node;
import com.skywilling.cn.livemap.model.Park;
import com.skywilling.cn.livemap.service.ParkService;
import com.skywilling.cn.manager.car.enumeration.*;
import com.skywilling.cn.manager.car.model.CarDynamic;
import com.skywilling.cn.manager.car.model.ModuleInfo;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import com.skywilling.cn.manager.car.service.CarInfoService;
import com.skywilling.cn.manager.task.model.AutoTask;
import com.skywilling.cn.manager.task.service.TaskService;
import com.skywilling.cn.scheduler.model.Trip;
import com.skywilling.cn.scheduler.service.TripService;
import com.skywilling.cn.web.model.ParkAndCar;
import com.skywilling.cn.web.model.view.CarView;
import com.skywilling.cn.web.model.view.PageView;
import com.skywilling.cn.web.service.LockService;
import com.skywilling.cn.web.utils.ViewBuilder;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CarInfo related queries are placed here.
 */
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v2/carInfo")
@RestController
public class CarInfoController {

  private static final Logger LOG = LoggerFactory.getLogger(CarInfoController.class);
  @Autowired
  ClientService clientService;
  @Autowired
  LockService lockService;


  @Autowired
  CarInfoService carInfoService;

  @Autowired
  CarDynamicService carDynamicService;



  @Autowired
  private ParkService parkService;

  @Autowired
  private TaskService taskService;

  @Autowired
  private TripService tripService;


  /** 查询所有链接的车端 */
  @RequestMapping(value = "/clients", method = RequestMethod.GET)
  public BasicResponse getAutonomousClients(@RequestParam(value = "page", required = true) int page,
                                            @RequestParam(value = "size", required = true) int size){
    return BasicResponse.buildResponse(ResultType.SUCCESS, clientService.getAllClients());
  }

  @RequestMapping(value = "/cars", method = RequestMethod.GET)
  public BasicResponse cars(@RequestParam(value = "parkId", required = false) Integer parkId,
                            @RequestParam(value = "type", required = false) Integer type,
                            @RequestParam(value = "simulation", required = false) Integer simulation,
                            @RequestParam(value = "definitionId", required = false) Integer definitionId,
                            @RequestParam(value = "connect", required = false) Integer connect,
                            @RequestParam(value = "useStatus", required = false) Integer useStatus,
                            HttpServletRequest request) {


        CarDynamic carDynamic = new CarDynamic();
        carDynamic.setParkId(parkId);
        carDynamic.setType(type);
        carDynamic.setSimulation(simulation);
        carDynamic.setDefinitionId(definitionId);
        carDynamic.setConnect(connect);
        carDynamic.setUseStatus(useStatus);
        PageInfo<CarDynamic> pageInfo = carDynamicService.queryBy(carDynamic, 1, 10);
        PageView pageView = ViewBuilder.build(pageInfo);
        return BasicResponse.buildResponse(ResultType.SUCCESS, pageView);
  }

  @RequestMapping(value = "/car/vins", method = RequestMethod.GET)
  public BasicResponse queryVins(@RequestParam(value = "vin", required = false) String vin) {
        try {
          List<String> vins = carInfoService.queryVins(vin);
          return BasicResponse.buildResponse(ResultType.SUCCESS, vins);
        }catch (Exception e) {
          return BasicResponse.buildResponse(ResultType.FAILED, e.getClass());
        }
  }

  @RequestMapping(value = "/car/{vin}/ride", method = RequestMethod.GET)
  public BasicResponse getRide(@PathVariable("vin") String vin) {
        String taskId = carInfoService.getTaskId(vin);
        if (taskId == null) {
          return BasicResponse.buildResponse(ResultType.SUCCESS, "");
        }
        AutoTask task = taskService.getTaskById(taskId);
        if (task == null) {
          return BasicResponse.buildResponse(ResultType.FAILED, "");
        }
        Trip ride = tripService.get(task.getRideId());
        return BasicResponse.buildResponse(ResultType.SUCCESS, ride);
  }

  @RequestMapping(value = "/car/{vin}/rides", method = RequestMethod.GET)
  public BasicResponse getHistoryRides(@PathVariable("vin") String vin,
           @RequestParam(value = "start", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date start,
           @RequestParam(value = "end", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date end) {
        try {
               List<Trip> rides = null;
               if (start == null || end == null) {
               rides = tripService.queryBy(vin, 1, 12);
          }else{
               rides = tripService.queryBy(vin, start, end, 1, 12);
          }
          return BasicResponse.buildResponse(ResultType.SUCCESS, rides);
        } catch (Exception e) {
          return BasicResponse.buildResponse(ResultType.FAILED, null);
        }
  }
  /** add a  car*/
  @RequestMapping(value = "/car/add", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public BasicResponse addCar(@RequestParam(value = "vin", required = false )String vin,
                              @RequestParam(value = "parkId", required = false) Integer parkId,
                              @RequestParam(value = "type", required = false) Integer type,
                              @RequestParam(value = "simulation", required = false) Integer simulation,
                              @RequestParam(value = "definitionId", required = false) Integer definitionId,
                              @RequestParam(value = "connect", required = false) Integer connect,
                              @RequestParam(value = "useStatus", required = false) Integer useStatus,
                              HttpServletRequest request) {
      CarDynamic carDynamic = new CarDynamic();
      carDynamic.setVin(vin);
      carDynamic.setParkId(parkId);
      carDynamic.setType(type);
      carDynamic.setSimulation(simulation);
      carDynamic.setDefinitionId(definitionId);
      carDynamic.setConnect(connect);
      carDynamic.setUseStatus(useStatus);
    try {
      carDynamic.setDriveMode(DriveType.AUTONOMOUS.getCode());
      carDynamicService.save(carDynamic);
      return BasicResponse.buildResponse(ResultType.SUCCESS, carDynamic);
    } catch (CarNotVinException e) {
      return BasicResponse.buildResponse(ResultType.FAILED, e.getMessage());
    }
  }
  /** delete a car*/
  @RequestMapping(value = "/car/{vin}/delete", method = {RequestMethod.DELETE, RequestMethod.POST})
  public BasicResponse deleteCar(@PathVariable("vin") String vin) {
    CarDynamic carDynamic = carDynamicService.query(vin);
    if (carDynamic != null && carDynamic.getParkId() != null) {
      return BasicResponse.buildResponse(ResultType.FAILED, "该车辆已与园区进行绑定，无法删除");
    }
    carDynamicService.delete(vin);
    return BasicResponse.buildResponse(ResultType.SUCCESS, null);
  }

  /**直接更新车辆信息,可以指定园区*/
  @RequestMapping(value = "/car/{vin}/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public BasicResponse updateCar(@PathVariable String vin, CarDynamic carDynamic) {

          if (carDynamic == null || (carDynamic.getParkId() == null && carDynamic.getCarPlate() == null
              && carDynamic.getType() == null && carDynamic.getSimulation() == null)) {
              return BasicResponse.buildResponse(ResultType.FAILED, "the update info is empty");
          }
          carDynamic.setVin(vin);
          try {
               CarDynamic query = carDynamicService.query(carDynamic.getVin());
            if (query == null) {
              return BasicResponse.buildResponse(ResultType.FAILED, "the car is not exists");
            }
              carDynamicService.update(carDynamic);
              return BasicResponse.buildResponse(ResultType.SUCCESS, null);
             } catch (CarNotVinException e) {
              e.printStackTrace();
              return BasicResponse.buildResponse(ResultType.FAILED, e.getMessage());
            } catch (Exception e) {
              return BasicResponse.buildResponse(ResultType.FAILED, e.getMessage());
            }
  }

  /**
   * 获取所有未与园区绑定的车辆
   */
  @RequestMapping(value = "/car/unbound", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public BasicResponse getAllUnboundCar(@RequestParam("page") int page, @RequestParam("size") int size) {

        PageInfo<CarDynamic> pageInfo = carDynamicService.queryUnbound(page, size);
        PageView pageView = ViewBuilder.build(pageInfo);
        return BasicResponse.buildResponse(ResultType.SUCCESS, pageView);
  }

  @RequestMapping(value = "/car/{vin}/position", method = RequestMethod.GET)
  public BasicResponse getPosition(@PathVariable(name = "vin") String vin) {
        try {
              CarDynamic carDynamic = carDynamicService.query(vin);
              if (carDynamic == null) {
                  return BasicResponse.buildResponse(ResultType.FAILED, "the car is not exist");
              }
              Park park = parkService.query(carDynamic.getParkId());
              if (park == null) {
                return BasicResponse.buildResponse(ResultType.FAILED, "the car is not bind to park");
              }
              Position pos = carInfoService.getPosition(vin);
              return BasicResponse.buildResponse(ResultType.SUCCESS, pos);
           } catch (NullPointerException e) {
             return BasicResponse.buildResponse(ResultType.FAILED, null);
        }
  }

  @RequestMapping(value = "/car/carInfos", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public BasicResponse getCarInfos(@RequestBody ParkAndCar parkAndCar) {

        Park park = parkService.query(parkAndCar.getParkId());
        if (park == null) {
          return BasicResponse.buildResponse(ResultType.FAILED, "the park is not exist");
        }
        List<CarView> carViews = parkAndCar.getVins().stream().map((vin) -> {
            CarView carView = new CarView();
            carView.setVin(vin);
            carView.setParkName(park.getName());
            carView.setState(carInfoService.getState(vin));
            Position point = carInfoService.getPosition(vin);
            Node node = new Node();
            node.setX(point.getX());
            node.setY(point.getY());
            carView.setNode(node);
            List<ModuleInfo> nodes = carInfoService.getAllNodesInfo(vin);
            boolean b = nodes.stream().anyMatch(moduleInfo -> moduleInfo.getStatus() != 0);
            carView.setHealthState(b ? 1 : 0);
            return carView;
        }).collect(Collectors.toList());
        return BasicResponse.buildResponse(ResultType.FAILED, carViews);
  }

  @RequestMapping(value = "/car/{vin}/health", method = RequestMethod.GET)
  public BasicResponse getHealthMsg(@PathVariable("vin") String vin) {

          try {
                List<ModuleInfo> moduleInfoList = carInfoService.getAllNodesInfo(vin);
                if (moduleInfoList == null) {
                  return BasicResponse.buildResponse(ResultType.CAR_NOT_EXISTS, null);
                }
                return BasicResponse.buildResponse(ResultType.SUCCESS, moduleInfoList);
          } catch (NullPointerException e) {
                return BasicResponse.buildResponse(ResultType.FAILED, null);
          }
  }

  @RequestMapping(value = "/car/{vin}/carInfo", method = RequestMethod.GET)
  public BasicResponse getCarInfo(@PathVariable("vin") String vin) {

    try {
      CarDynamic latest = carInfoService.get(vin);
      if (latest == null) {
        return BasicResponse.buildResponse(ResultType.CAR_NOT_EXISTS, null);
      }
      return BasicResponse.buildResponse(ResultType.SUCCESS, latest);
    } catch (RuntimeException e) {

    }
    return BasicResponse.buildResponse(ResultType.FAILED, null);
  }

  /**
   * 查询车辆是否是连接状态
   * @param vin 车辆vin码
   */
  @RequestMapping(value = "/car/{vin}/alive", method = RequestMethod.GET)
  public BasicResponse checkIsAlive(@PathVariable(name = "vin") String vin) {
    boolean connected = carInfoService.isConnected(vin);
    if (connected) {
      return BasicResponse.buildResponse(ResultType.SUCCESS, null);
    }
    return BasicResponse.buildResponse(ResultType.FAILED, null);
  }

  /**
   * 设置车辆秘钥
   * @param vin 车辆vin码
   * @param key 秘钥
   */
  @RequestMapping(value = "/car/{id}/key", method = RequestMethod.POST)
  public BasicResponse updateVinKey(@PathVariable(name = "id") String vin, @RequestParam(name = "key") String key) {
      lockService.updateKey(vin, key);
      return BasicResponse.buildResponse(ResultType.SUCCESS, null);
  }


  @RequestMapping(value = "/car/{id}/verifyCode", method = RequestMethod.POST)
  public BasicResponse updateVinVerifyCode(@PathVariable(name = "id") String vin,
                                           @RequestParam(name = "verifyCode") String verifyCode) {
      lockService.updateVerifyCode(vin, verifyCode);
      return BasicResponse.buildResponse(ResultType.SUCCESS, null);
  }

  /**
   * 车辆解锁
   * @param vin     车辆vin码
   * @param data    秘钥或验证码
   * @param type    解锁类型，type=0，使用key; type=1，验证码解锁
   * @param source  解锁请求来源，app或system应用系统
   */
  @RequestMapping(value = "/car/{id}/unlock", method = RequestMethod.POST)
  public BasicResponse unlockCar(@PathVariable(name = "id") String vin, @RequestParam(name = "data") String data,
                                 @RequestParam(name = "type") Integer type, @RequestParam(name = "source") String source) {
      // try to unlock car
      LOG.info("unlockCar", "vin = %s, data = %s, type = %s, source = %s", vin, data, type, source);
      if (type == 0) {
          if (lockService.unlockByKey(vin, data)) {
            return BasicResponse.buildResponse(ResultType.SUCCESS, null);
          }
      }
      if (type == 1) {
          if (lockService.unlockByVerifyCode(vin, data)) {
              //Todo rent car
              //notifyService.rentCar(vin);
              return BasicResponse.buildResponse(ResultType.SUCCESS, null);
          }
      }
      return BasicResponse.buildResponse(ResultType.FAILED, null);
  }

  /**查询锁状态 */
  @RequestMapping(value = "/car/{id}/lock", method = RequestMethod.GET)
  public BasicResponse getVinLockState(@PathVariable(name = "id") String vin) {
        if (lockService.isLocked(vin)) {
          return BasicResponse.buildResponse(ResultType.LOCKED, null);
        }
        return BasicResponse.buildResponse(ResultType.UNLOCKED, null);
  }

  /**
   * 车辆上锁
   * @param vin     车辆vin码
   * @param data    秘钥
   * @param source  来源，app或system
   */
  @RequestMapping(value = "/car/{id}/lock", method = RequestMethod.POST)
  public BasicResponse lockCar(@PathVariable(name = "id") String vin, @RequestParam(name = "data") String data,
                               @RequestParam(name = "source") String source) {
        LOG.info("[lockCar]", "lock car %s from source %s", vin, source);
        lockService.lock(vin);
        if (StringUtils.equals(source, "app")) {
              lockService.updateKey(vin, data);
              //Todo return car
              //notifyService.returnCar(vin);
        }
        return BasicResponse.buildResponse(ResultType.SUCCESS, null);
  }

/*  @RequestMapping(value = "/car/types", method = RequestMethod.GET)
  public BasicResponse getCarTypes() {
        try {
          return BasicResponse.buildResponse(ResultType.SUCCESS, carTypeService.query());
        } catch (Exception e) {
          return BasicResponse.buildResponse(ResultType.FAILED, e.getClass());
        }
  }*/
}
