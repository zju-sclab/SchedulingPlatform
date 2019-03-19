package com.skywilling.cn.manager.task.service.impl;


import com.skywilling.cn.manager.car.enumeration.CarState;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.task.biz.AutoServiceBiz;
import com.skywilling.cn.manager.task.model.AutoTask;
import com.skywilling.cn.manager.task.service.AutoTaskService;
import com.skywilling.cn.manager.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AutoTaskServiceImpl implements AutoTaskService {

    @Autowired
    AutoServiceBiz autoServiceBiz;
    @Autowired
    AutoCarInfoService autoCarInfoService;
    @Autowired
    TaskService taskService;

    @Override
    public String submit(AutoTask autoTask) throws IllegalStateException {
        autoTask.
        AutonomousCarInfo car = autoCarInfoService.get(autoTask.getVin());
        if (car.getState() != CarState.FREE.getState()) {
            return null;
        }
        String taskId = taskService.save(autoTask);
        car.setTaskId(taskId);
        autoCarInfoService.save(car);
        autoServiceBiz.fireAutonomous(autoTask);
        return taskId;
    }


    @Override
    public CompletableFuture<Integer> resume(String taskId) {
        // todo: implement resume protocol.
        return null;
    }

    @Override
    public CompletableFuture<Boolean> stop(String taskId) {
        AutoTask autoTask = taskService.getTaskById(taskId);
        return autoServiceBiz.killAutonomous(autoTask.getVin());
    }

    @Override
    public CompletableFuture<Integer> intervening(String taskId) {
        return null;
    }

}
