package com.skywilling.cn.manager.task.biz;

import com.alibaba.fastjson.JSONObject;


import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.connection.service.RequestSender;
import com.skywilling.cn.manager.car.enumeration.TaskState;
import com.skywilling.cn.manager.task.biz.repository.TaskAccessor;
import com.skywilling.cn.manager.task.model.AutoTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AutoServiceBiz {

  private static final Logger LOG = LoggerFactory.getLogger(AutoServiceBiz.class);

  @Autowired
  RequestSender requestSender;
  @Autowired
  TaskAccessor taskAccessor;

  public CompletableFuture<Boolean> prepareAutonomous(String vin, AutoTask autoTask) {
    LOG.info("prepareTask taskId {}", autoTask.getTaskId());
    autoTask.setStatus(TaskState.PREPARING.getCode());
    taskAccessor.save(autoTask);
    return requestSender.sendRequest(vin, TypeField.PREPARE_FIRE, new JSONObject());

  }


  public CompletableFuture<Boolean> killAutonomous(String vin) {
    CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();
    return requestSender.sendRequest(vin, TypeField.STOP_AUTONOMOUS,new JSONObject());

  }

  public CompletableFuture<Boolean> fireAutonomous(AutoTask autoTask) {
    String vin = autoTask.getVin();
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("taskId", autoTask.getTaskId());
    jsonObject.put("speed", autoTask.getVelocity());
    jsonObject.put("route", autoTask.getAction());

    return requestSender.sendRequest(vin, TypeField.FIRE_LANE_AUTONOMOUS,jsonObject);

  }
}
