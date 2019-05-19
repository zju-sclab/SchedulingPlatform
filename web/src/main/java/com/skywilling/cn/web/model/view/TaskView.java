package com.skywilling.cn.web.model.view;

import com.skywilling.cn.manager.car.model.Action;
import com.skywilling.cn.manager.task.model.AutoTask;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class TaskView implements Serializable {

  private static final long serialVersionUID = -123412341234L;

  private String taskId;
  private String rideId;
  private String vin;
  private int status;
  private Instant gmtCreate;
  private Instant gmtModified;
  private double velocity;
  private double acceleration;
  private int offset;
  private int percentage;
  private List<String> stations;

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public String getRideId() {
    return rideId;
  }

  public void setRideId(String rideId) {
    this.rideId = rideId;
  }

  public String getVin() {
    return vin;
  }

  public void setVin(String vin) {
    this.vin = vin;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public Instant getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Instant gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public Instant getGmtModified() {
    return gmtModified;
  }

  public void setGmtModified(Instant gmtModified) {
    this.gmtModified = gmtModified;
  }

  public double getVelocity() {
    return velocity;
  }

  public void setVelocity(double velocity) {
    this.velocity = velocity;
  }

  public double getAcceleration() {
    return acceleration;
  }

  public void setAcceleration(double acceleration) {
    this.acceleration = acceleration;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public int getPercentage() {
    return percentage;
  }

  public void setPercentage(int percentage) {
    this.percentage = percentage;
  }

  public List<String> getStations() {
    return stations;
  }

  public void setStations(List<String> stations) {
    this.stations = stations;
  }

  public static TaskView getFrom(AutoTask task) {
    TaskView taskView = new TaskView();
    taskView.setTaskId(task.getTaskId());
    taskView.setPercentage(task.getPercentage());
    taskView.setOffset(task.getOffset());
    taskView.setStatus(task.getStatus());
    taskView.setVin(task.getVin());
    taskView.setAcceleration(task.getAcceleration());
    taskView.setGmtCreate(task.getGmtCreate());
    taskView.setGmtModified(task.getGmtModified());
    taskView.setRideId(task.getRideId());
    taskView.setVelocity(task.getVelocity());
    int size = task.getAction().size();
    List<String> stations = new ArrayList<>();
    for (Action action : task.getAction()) {
      stations.add(action.getGoal().getName());
    }
    stations.add(task.getAction().get(size - 1).getGoal().getName());
    taskView.setStations(stations);
    return taskView;
  }
}
