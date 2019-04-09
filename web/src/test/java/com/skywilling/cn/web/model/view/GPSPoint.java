package com.skywilling.cn.web.model.view;

import java.io.Serializable;

public class GPSPoint implements Serializable {

  private static final long serialVersionUID = -2071899999925581898L;
  private Double latitude;
  private Double longitude;

  public GPSPoint() {
  }

  public GPSPoint(Double latitude, Double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }
}
