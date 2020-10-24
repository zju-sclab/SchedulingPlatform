package com.skywilling.cn.manager.task.biz.repository.impl;

import com.skywilling.cn.common.config.redis.RedisDao;
import com.skywilling.cn.manager.task.biz.repository.TaskAccessor;
import com.skywilling.cn.manager.task.model.AutoTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

@Repository
public class TaskAccessorImpl implements TaskAccessor {

  private static final String VIN_PREFIX = "TASK";

  @Autowired
  private RedisDao redisDao;

  static String getKey(String id) {
    return String.format("%s_%s", VIN_PREFIX, id);
  }

  static String getId(String vin) {
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);
    return String.format("%02d%02d%02d%02d%02d_%s", year, month, day, hour, minute, vin.substring(8));
  }

  @Override
  public String save(AutoTask autoTask) {
    String taskId = getId(autoTask.getVin());
    autoTask.setTaskId(taskId);
    redisDao.save(getKey(taskId), autoTask);
    return taskId;
  }

  @Override
  public AutoTask getById(String id) {
    return (AutoTask) redisDao.read(getKey(id));
  }

  @Override
  public void update(AutoTask autoTask) {
    if (redisDao.exists(getKey(autoTask.getTaskId()))) {
      redisDao.save(getKey(autoTask.getTaskId()), autoTask);
    }
  }

  @Override
  public List<AutoTask> getAllTasks() {
    Set<String> keys = redisDao.keys("TASK1*");
    List<AutoTask> tasks = new ArrayList<>();
    for(String key:keys){
      tasks.add((AutoTask)redisDao.read(key));
    }
    return tasks;
  }

  @Override
  public List<AutoTask> getTasks(int status, int page, int size) {
    return null;
  }
}
