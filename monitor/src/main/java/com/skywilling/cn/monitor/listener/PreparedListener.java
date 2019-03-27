package com.skywilling.cn.monitor.listener;

import com.rabbitmq.client.AMQP;
import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.common.exception.CarNotExistsException;
import com.skywilling.cn.common.model.BasicCarResponse;
import com.skywilling.cn.manager.car.enumeration.CarState;
import com.skywilling.cn.manager.car.enumeration.TaskState;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.task.model.AutoTask;
import com.skywilling.cn.manager.task.service.TaskService;
import com.skywilling.cn.monitor.model.DTO.ACK;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class PreparedListener extends BasicListener {
    @Autowired
    ListenerMap listenerMap;
    @Autowired
    AutoCarInfoService autoCarInfoService;
    @Autowired
    TaskService taskService;


    @Override
    @PostConstruct
    public void init() {
        listenerMap.addListener(TypeField.PREPARE_FIRE.getDesc(), this);
    }

    @Override
    public BasicCarResponse process(String vin, boolean result, String body) {
        //判断是否准备好，是则开始正式发送任务，否则放弃
        AutoTask autoTask = null;
        try {
            autoTask = taskService.getCurrentTask(vin);
            return onPrepared(autoTask, result);

        } catch (CarNotExistsException e) {
            e.printStackTrace();
        }

        return null;
    }

    private BasicCarResponse onPrepared(AutoTask autoTask, boolean success) {
        if (!success) {
            autoTask.setStatus(TaskState.REJECTED.getCode());
            taskService.update(autoTask);
            AutonomousCarInfo car = autoCarInfoService.get(autoTask.getVin());
            if (car != null) {
                car.setState(CarState.FREE.getState());
                autoCarInfoService.save(car);
            }
            return null;
        } else {
            return new BasicCarResponse(ACK.COMMAND.getCode(), autoTask);
        }
    }

}
