package com.skywilling.cn.web.model;

import java.io.Serializable;
import java.util.List;

public class ParkAndCar implements Serializable {

  private static final long serialVersionUID = 5369631862241443412L;
  private int parkId;
  private List<String> vins;

  public int getParkId() {
    return parkId;
  }

  public void setParkId(int parkId) {
    this.parkId = parkId;
  }

  public List<String> getVins() {
    return vins;
  }

  public void setVins(List<String> vins) {
    this.vins = vins;
  }
}
