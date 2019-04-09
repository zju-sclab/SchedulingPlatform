package com.skywilling.cn.web.model.view;

import com.skywilling.cn.park.model.Node;

import java.io.Serializable;

public class CarView implements Serializable {

  private static final long serialVersionUID = -8876768690242539518L;
  private String vin;
  private String parkName;
  private Node node;
  private int state;
  private int healthState;

  public String getVin() {
    return vin;
  }

  public void setVin(String vin) {
    this.vin = vin;
  }

  public String getParkName() {
    return parkName;
  }

  public void setParkName(String parkName) {
    this.parkName = parkName;
  }

  public int getHealthState() {
    return healthState;
  }

  public Node getNode() {
    return node;
  }

  public void setNode(Node node) {
    this.node = node;
  }

  public int getState() {
    return state;
  }

  public void setState(int state) {
    this.state = state;
  }

  public int getHealthState(Integer bodyStatus) {
    return healthState;
  }

  public void setHealthState(int healthState) {
    this.healthState = healthState;
  }

}
