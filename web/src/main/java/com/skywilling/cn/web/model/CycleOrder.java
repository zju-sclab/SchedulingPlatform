package com.skywilling.cn.web.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Document(collection = "cycle_order")
public class CycleOrder implements Serializable {

  private static final long serialVersionUID = -900456854306L;
  @Id
  private String id;
  @Field
  private String parkName;
  @Field
  private String vin;
  @Field
  private List<String> stations;
  @Field
  private List<String> rideId;
  @Field
  private long waitingSeconds;
  @Field
  private int start;
  @Field
  private int cycleNumber;
  @Field
  private Date gmtCreate;
  @Field
  private Date gmtModified;

  public static class Builder {

    CycleOrder cycleOrder;

    public Builder(String parkName, String vin, List<String> stations, long waitingSeconds) {
      cycleOrder = new CycleOrder();
      cycleOrder.parkName = parkName;
      cycleOrder.vin = vin;
      cycleOrder.stations = stations;
      cycleOrder.waitingSeconds = waitingSeconds;
      cycleOrder.gmtCreate = new Date();
      cycleOrder.gmtModified = new Date();
    }

    public Builder cycleNumber(int n) {
      cycleOrder.cycleNumber = n;
      return this;
    }

    public CycleOrder build() {
      return cycleOrder;
    }
  }

  public String getParkName() {
    return parkName;
  }

  public void setParkName(String parkName) {
    this.parkName = parkName;
  }

  public String getVin() {
    return vin;
  }

  public void setVin(String vin) {
    this.vin = vin;
  }

  public List<String> getStations() {
    return stations;
  }

  public void setStations(List<String> stations) {
    this.stations = stations;
  }

  public List<String> getRideId() {
    return rideId;
  }

  public void setRideId(List<String> rideId) {
    this.rideId = rideId;
  }

  public long getWaitingSeconds() {
    return waitingSeconds;
  }

  public void setWaitingSeconds(long waitingSeconds) {
    this.waitingSeconds = waitingSeconds;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getCycleNumber() {
    return cycleNumber;
  }

  public void setCycleNumber(int cycleNumber) {
    this.cycleNumber = cycleNumber;
  }

  public Date getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public Date getGmtModified() {
    return gmtModified;
  }

  public void setGmtModified(Date gmtModified) {
    this.gmtModified = gmtModified;
  }
}
