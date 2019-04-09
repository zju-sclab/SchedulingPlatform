package com.skywilling.cn.manager.task.model;

import com.skywilling.cn.common.model.LidarPoint;
import com.skywilling.cn.manager.car.model.Action;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "task")
public class AutoTask implements Serializable{
  private static final long serialVersionUID = 2233388390744700817L;
  @Id
  private String taskId;
  @Field
  private String rideId;
  @Field
  private String vin;
  @Field
  private String from;
  @Field
  private String to;
  @Field
  private int status;
  @Field
  private Instant gmtCreate;
  @Field
  private Instant gmtModified;
  @Field
  private double velocity;
  @Field
  private double acceleration;
  @Field
  private List<LidarPoint> action;
  @Field
  private int offset;
  @Field
  private int percentage;


}


