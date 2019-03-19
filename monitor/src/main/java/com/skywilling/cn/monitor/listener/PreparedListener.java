package com.skywilling.cn.monitor.listener;

import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.common.exception.CarNotExistsException;
import com.skywilling.cn.manager.car.enumeration.CarState;
import com.skywilling.cn.manager.car.enumeration.TaskState;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.task.model.AutoTask;
import com.skywilling.cn.manager.task.service.AutoTaskService;
import com.skywilling.cn.manager.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;

public class PreparedListener extends BasicListener {
    @Autowired
    ListenerMap listenerMap;
    @Autowired
    AutoCarInfoService autoCarInfoService;
    @Autowired
    TaskService taskService;
    @Autowired
    AutoTaskService autoTaskService;

    @Override
    @PostConstruct
    public void init() {
        listenerMap.addListener(TypeField.PREPARE_FIRE.getDesc(), this);
    }

    @Override
    public boolean process(String vin, boolean result, String body) {
        //判断是否准备好，是则开始正式发送任务，放弃
        AutoTask autoTask = null;
        try {
            autoTask = taskService.getCurrentTask(vin);
            onPrepared(autoTask, result);
            return true;
        } catch (CarNotExistsException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void onPrepared(AutoTask autoTask, boolean success) {
        if (!success) {
            autoTask.setStatus(TaskState.REJECTED.getCode());
            taskService.update(autoTask);
            AutonomousCarInfo car = autoCarInfoService.get(autoTask.getVin());
            if (car != null) {
                car.setState(CarState.FREE.getState());
                autoCarInfoService.save(car);
            }
        } else if (success) {
            autoTask.setStatus(TaskState.SUBMITTING.getCode());
            taskService.update(autoTask);
            CompletableFuture<Boolean> future = autoTaskService.submit(autoTask);
            future.whenComplete((result, t) -> {
                if (t != null) {
                    autoTask.setStatus(TaskState.REJECTED.getCode());
                    taskService.update(autoTask);
                }
            });
        }
    }

}
