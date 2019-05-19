package com.skywilling.cn.monitor.model.DTO;

import lombok.Data;

@Data
public class TaskInfo  {

    private String taskId;
    private int status;
    private int percentage;

}
