package com.skywilling.cn.manager.task.service;

import com.skywilling.cn.common.exception.CarNotExistsException;
import com.skywilling.cn.manager.task.model.AutoTask;

import java.util.List;


/***
 * Trajectory task is designed to following the given trajectory, and move from a to b.
 *  In this layer, we want to achieve the goal that control the car move from a to b with specified trajectoy or lane.
 *  For Trajectory task, following states is available:
 *    INITIAL:    submit to service, not sent to car yet.
 *    SUBMITTED:  send trajectory to car and waiting to response.
 *    RUNNING:    the car is moving.
 *    OBSTACLE_INTERRUPTED:  task is interrupted by obstacles.
 *    INTERVENTION:   try to intervention.
 *    KILLING:    server send kill command. next status is killed.
 *    RESUMING:   server sends resume to car and waiting to response. next status is running.
 *    KILLED:    the task is aborted.
 *    FINISHED:    the task reaches the goal.
 *    FINAL_SAVING:
 * */

public interface TaskService {

  Integer queryStatus(String taskId);

  AutoTask getTaskById(String taskId);

  List<AutoTask> getTasks(int status, int page, int size);

  AutoTask getCurrentTask(String vin) throws CarNotExistsException;

  String save(AutoTask autoTask);

  void update(AutoTask autoTask);

}
