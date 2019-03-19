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
  /**
   * task Id
   */
  @Id
  private String taskId;
  /**
   * task status, refer to {@link com.skywilling.cn.autonomous.common.TaskState}
   */
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


  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getPercentage() {
    return percentage;
  }

  public void setPercentage(int percentage) {
    this.percentage = percentage;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }
}
