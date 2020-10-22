package com.skywilling.cn.manager.task.service.impl;


import com.skywilling.cn.common.exception.CarNotExistsException;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.task.biz.repository.TaskAccessor;
import com.skywilling.cn.manager.task.model.AutoTask;
import com.skywilling.cn.manager.task.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

  private static final Logger LOG = LoggerFactory.getLogger(TaskServiceImpl.class);

  @Autowired
  private AutoCarInfoService autoCarInfoService;

  @Autowired
  TaskAccessor taskAccessor;

  @Override
  public Integer queryStatus(String taskId) {
    AutoTask autoTask = taskAccessor.getById(taskId);
    if (autoTask == null) {
      return null;
    }
    return autoTask.getStatus();
  }

  @Override
  public AutoTask getTaskById(String taskId) {
    return taskAccessor.getById(taskId);
  }

  @Override
  public List<AutoTask> getTasks(int status, int page, int size) {
    return taskAccessor.getTasks(status, page, size);
  }

  @Override
  public List<AutoTask> getAllTasks() {
    return taskAccessor.getAllTasks();
  }

  @Override
  public AutoTask getCurrentTask(String vin) throws CarNotExistsException {
    AutonomousCarInfo car = autoCarInfoService.get(vin);
    if (car == null) {
      throw new CarNotExistsException(vin);
    }
    if (car.getTaskId() == null) {
      return null;
    }
    return taskAccessor.getById(car.getTaskId());
  }

  @Override
  public String save(AutoTask autoTask) {
    return taskAccessor.save(autoTask);
  }

  @Override
  public void update(AutoTask autoTask) {
    taskAccessor.update(autoTask);
  }


}
