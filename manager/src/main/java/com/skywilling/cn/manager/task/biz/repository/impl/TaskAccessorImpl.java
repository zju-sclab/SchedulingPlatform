package com.skywilling.cn.manager.task.biz.repository.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.skywilling.cn.common.config.redis.RedisDao;
import com.skywilling.cn.manager.task.biz.repository.TaskAccessor;
import com.skywilling.cn.manager.task.model.AutoTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

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
    //int second = calendar.get(Calendar.SECOND);
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
  public List<AutoTask> getAllTasks() throws JsonProcessingException {
    Set<String> keys = redisDao.keys("TASK*");
    List<AutoTask> tasks = new ArrayList<>();
    for(String key:keys){
      String str = (String)redisDao.read(key);
      AutoTask autoTask = JSONObject.parseObject(str, AutoTask.class);
      tasks.add(autoTask);
//      ObjectMapper objectMapper = new ObjectMapper()
//              .registerModule(new ParameterNamesModule())
//              .registerModule(new Jdk8Module())
//              .registerModule(new JavaTimeModule());
//      Object obj = redisDao.read(key);
//      String jsonContent =  objectMapper.writeValueAsString(obj);
//      AutoTask autoTask = objectMapper.readValue(jsonContent, AutoTask.class);
      //tasks.add((AutoTask)redisDao.read(key));
      //tasks.add(autoTask);
    }
    return tasks;
  }

  @Override
  public List<AutoTask> getTasks(int status, int page, int size) {
    return null;
  }
}
