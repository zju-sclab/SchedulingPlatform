package com.skywilling.cn.command.biz;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.connection.service.RequestSender;
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

  /** 预热自动驾驶*/
  @Deprecated
  public CompletableFuture<Boolean> prepareAutonomous(AutoTask autoTask) {
    LOG.info("prepareTask taskId {}", autoTask.getTaskId());
    return requestSender.sendRequest(autoTask.getVin(), TypeField.PREPARE_FIRE, new JSONObject());
  }

  /** 停止自动驾驶*/
  public CompletableFuture<Boolean> killAutonomous(String vin) {
    return requestSender.sendRequest(vin, TypeField.STOP_AUTONOMOUS,new JSONObject());
  }

  /** 启动自动驾驶*/
  public CompletableFuture<Boolean> fireAutonomous(AutoTask autoTask) {
    String vin = autoTask.getVin();
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("taskId", autoTask.getTaskId());
    jsonObject.put("actions", autoTask.getAction());
    return requestSender.sendRequest(vin, TypeField.FIRE_AUTONOMOUS,jsonObject);
  }

  /** 暂停当前任务 */
  public CompletableFuture<Boolean> pauseAutonomous(String vin){
    return requestSender.sendRequest(vin, TypeField.PAUSE_AUTONOMOUS, new JSONObject());
  }

  /** 重启当前任务 */
  public CompletableFuture<Boolean> reStartAutonomous(String vin){
    return requestSender.sendRequest(vin, TypeField.RESTART_AUTONOMOUS, new JSONObject());
  }
}
