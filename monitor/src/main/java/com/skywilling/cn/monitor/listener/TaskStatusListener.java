package com.skywilling.cn.monitor.listener;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.common.model.BasicCarResponse;
import com.skywilling.cn.manager.task.model.AutoTask;
import com.skywilling.cn.manager.task.service.TaskService;
import com.skywilling.cn.monitor.model.DTO.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class TaskStatusListener extends BasicListener {
    @Autowired
    ListenerMap listenerMap;

    @Autowired
    TaskService taskService;

    @Override
    @PostConstruct
    public void init() {
        listenerMap.addListener(TypeField.TASK_REAL_TIME.getDesc(), this);
    }

    @Override
    public BasicCarResponse process(String vin, boolean result, String body) {

        TaskInfo taskInfo = JSONObject.parseObject(body, TaskInfo.class);
        AutoTask autoTask = taskService.getTaskById(taskInfo.getTaskId());
        autoTask.setStatus(taskInfo.getStatus());
        taskService.update(autoTask);
        return null;
    }
}
