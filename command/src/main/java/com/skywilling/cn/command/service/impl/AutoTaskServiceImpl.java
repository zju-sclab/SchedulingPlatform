package com.skywilling.cn.command.service.impl;


import com.skywilling.cn.command.biz.AutoServiceBiz;
import com.skywilling.cn.command.service.AutoTaskService;
import com.skywilling.cn.manager.car.enumeration.CarState;
import com.skywilling.cn.manager.car.enumeration.TaskState;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.task.model.AutoTask;
import com.skywilling.cn.manager.task.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * 稳定性考虑，如果发送失败需要重复发送等
 */

@Service
public class AutoTaskServiceImpl implements AutoTaskService {
    private final Logger logger = LoggerFactory.getLogger(AutoTaskServiceImpl.class);


    @Autowired
    AutoServiceBiz autoServiceBiz;
    @Autowired
    AutoCarInfoService autoCarInfoService;
    @Autowired
    TaskService taskService;

    /** 正式提交并启动自动驾驶任务 */
    @Override
    public CompletableFuture<Boolean> submit(AutoTask autoTask){
        AutonomousCarInfo car = autoCarInfoService.get(autoTask.getVin());
        if (car.getState() != CarState.FREE.getState()) {
            return null;
        }
        autoTask.setStatus(TaskState.SUBMITTING.getCode());
        taskService.update(autoTask);
        //这里才发送数据
        CompletableFuture<Boolean> future = autoServiceBiz.fireLidarAutonomous(autoTask);
        checkResult(autoTask,future);
        return future;
    }

    /** 开始预热任务,目前直接跳过预热开启任务 */
    @Override
    public CompletableFuture<Boolean> start(AutoTask autoTask) {
        AutonomousCarInfo car = autoCarInfoService.get(autoTask.getVin());
        if (car.getState() != CarState.FREE.getState()) {
            return null;
        }
        autoTask.setStatus(TaskState.PREPARING.getCode());
        String taskId = taskService.save(autoTask);
        car.setTaskId(taskId);
        autoCarInfoService.save(car);
        CompletableFuture<Boolean> future =autoServiceBiz.prepareAutonomous(autoTask);
        checkResult(autoTask,future);
        return future;
    }


    @Override
    public CompletableFuture<Boolean> resume(String taskId) {
        return null;
    }

    /** 停止任务 */
    @Override
    public CompletableFuture<Boolean> stopCar(String vin) {
        CompletableFuture<Boolean> future = autoServiceBiz.killAutonomous(vin);
        future.whenComplete((result, t) -> {
            if (t != null||Boolean.FALSE.equals(result)) {
                throw new RuntimeException("stop car error");
            }
        });
        return future;
    }


    @Override
    public CompletableFuture<Boolean> intervening(String taskId) {
        return null;
    }
    /** 判断是否成功发送到车端 */
    private void checkResult(AutoTask task,CompletableFuture<Boolean> future){
        future.whenComplete((result, t) -> {
            if (t != null||Boolean.FALSE.equals(result)) {
                logger.error("send task failed,vin={},taskId={}",task.getVin(),task.getTaskId());
                task.setStatus(TaskState.REJECTED.getCode());
                taskService.update(task);
            }
        });
    }

}
