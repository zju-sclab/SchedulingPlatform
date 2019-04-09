package com.skywilling.cn.web.controller;


import com.skywilling.cn.common.enums.ResultType;
import com.skywilling.cn.common.model.BasicResponse;
import com.skywilling.cn.manager.task.model.AutoTask;
import com.skywilling.cn.manager.task.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/autonomous/task")
@RestController
public class TaskController {

    public static final Logger LOG = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    TaskService taskService;

    @RequestMapping(value = "/{taskId}", method = RequestMethod.GET)
    public BasicResponse getTask(@PathVariable("taskId") String taskId) {
        AutoTask task = taskService.getTaskById(taskId);
        if (task != null) {
            return BasicResponse.buildResponse(ResultType.SUCCESS, task);
        }
        return BasicResponse.buildResponse(ResultType.FAILED, null);
    }

    @RequestMapping(value = "/{status}", method = RequestMethod.GET)
    public BasicResponse getTasks(@PathVariable("status") int status,
                                  @RequestParam("page") int page,
                                  @RequestParam("size") int size) {
        try {
            List<AutoTask> tasks = taskService.getTasks(status, page, size);
            return BasicResponse.buildResponse(ResultType.SUCCESS, tasks);
        } catch (Exception e) {
            return BasicResponse.buildResponse(ResultType.FAILED, e.getMessage());
        }
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }
}
