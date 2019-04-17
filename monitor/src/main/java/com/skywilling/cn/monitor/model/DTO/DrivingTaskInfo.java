package com.skywilling.cn.monitor.model.DTO;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

// todo this class only used to store the executed information
// it should not be persisted to storage.
@Document(collection = "driving_task_info")
public class DrivingTaskInfo implements Serializable {
  private static final long serialVersionUID = -3956419855731943291L;

  @Id
  private String taskId;

  @Field
  private int status;

  @Field
  private int offset;

  /**
   * task execute percentage.
   */
  @Field
  private int percentage;

  @Field
  private long timestamp = System.currentTimeMillis();


}
