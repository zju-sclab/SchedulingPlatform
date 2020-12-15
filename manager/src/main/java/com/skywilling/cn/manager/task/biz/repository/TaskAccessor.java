package com.skywilling.cn.manager.task.biz.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.skywilling.cn.manager.task.model.AutoTask;

import java.util.List;

public interface TaskAccessor {
  String save(AutoTask autoTask);
  AutoTask getById(String taskId);
  void update(AutoTask autoTask);

  List<AutoTask> getTasks(int status, int page, int size);
  List<AutoTask> getAllTasks() throws JsonProcessingException;
}
