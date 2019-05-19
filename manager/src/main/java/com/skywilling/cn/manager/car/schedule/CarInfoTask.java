package com.skywilling.cn.manager.car.schedule;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.common.config.redis.RedisDao;
import com.skywilling.cn.common.exception.CarNotVinException;
import com.skywilling.cn.manager.car.enumeration.ConnectType;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.model.CarDynamic;
import com.skywilling.cn.common.model.Pose;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class CarInfoTask {

  @Autowired
  private CarDynamicService carDynamicService;

  @Autowired
  private AutoCarInfoService autoCarInfoService;

  @Autowired
  private RedisDao redisDao;

  @Autowired
  private RestTemplate restTemplate;

  private Logger logger = LoggerFactory.getLogger(CarInfoTask.class);

  @Scheduled(fixedDelay = 10000)
  public void updateAutoDriveCar() {
    Set<String> keys = redisDao.keys("CAR_*");
    keys.forEach(key -> {
      String vin = key.substring(4);
      AutonomousCarInfo carInfo = autoCarInfoService.get(vin);

      CarDynamic carDynamic = new CarDynamic();
      carDynamic.setVin(vin);
      carDynamic.setConnect(carInfo.getState() == -1 ? 0: 1);
      /*carDynamic.setBodyStatus(carInfo.getNodes()
              .stream()
              .anyMatch(moduleInfo -> ModuleType.HARDWARE.getCode() ==moduleInfo.getType()
                      && moduleInfo.getType() != 0) ? 1 : 0);
      carDynamic.setModuleStatus(carInfo.getNodes()
              .stream()
              .anyMatch(moduleInfo -> ModuleType.SOFTWARE.getCode() ==moduleInfo.getType()
                      && moduleInfo.getType() != 0) ? 1 : 0);
*/
      Pose pose = carInfo.getPose();
      if (pose != null && pose.getPoint() != null) {
        carDynamic.setLatitude(pose.getPoint().getX());
        carDynamic.setLongitude(pose.getPoint().getY());
        carDynamic.setIsValid(pose.getPoint().getStatus());
        carDynamic.setLane(carInfo.getLane());
        carDynamic.setStation(carInfo.getStation());

      }

      try {
        carDynamicService.update(carDynamic);
      } catch (CarNotVinException e) {
        e.printStackTrace();
      }
    });
  }

  @Scheduled(fixedDelay = 10000)
  public void updateRealCar() {
    List<CarDynamic> carDynamics = carDynamicService.queryRealCar(1, Integer.MAX_VALUE).getList();
    logger.info("updateRealCar: " + carDynamics.size());

    carDynamics.forEach(carDynamic -> {
      String url = "http://116.62.112.178:8000/calcifer-web/api/car/basicinfo?vin=" + carDynamic.getVin();
      JSONObject response = restTemplate.getForObject(url, JSONObject.class);
      logger.info(response.toJSONString());

      JSONObject attach = response.getJSONObject("attach");
      if (attach != null) {
        CarDynamic dynamic = new CarDynamic();
        dynamic.setVin(carDynamic.getVin());

        Long recordTime = attach.getLong("time");
        long l = System.currentTimeMillis() - recordTime;
        dynamic.setConnect(l > 5000 ? ConnectType.DISCONNECT.getCode() : ConnectType.CONNECT.getCode());
        dynamic.setBodyStatus(attach.getInteger("carStatus"));
        dynamic.setIsValid(attach.getBoolean("validPosition") ? 1 : 0);
        dynamic.setLatitude(attach.getDouble("latitude"));
        dynamic.setLongitude(attach.getDouble("longitude"));
        dynamic.setLane(attach.getString("Lane"));
        dynamic.setGmtModify(new Date());
        try {
          carDynamicService.update(dynamic);
        } catch (CarNotVinException e) {
          e.printStackTrace();
        }
      }
    });
  }
}
