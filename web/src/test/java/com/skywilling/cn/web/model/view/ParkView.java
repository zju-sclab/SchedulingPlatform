package com.skywilling.cn.web.model.view;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class ParkView implements Serializable, Comparable {

  private static final long serialVersionUID = 533563833307613234L;
  private Integer id;
  private String name;
  private String zh;
  private String picture;
  private GPSPoint upLeft;
  private GPSPoint upRight;
  private GPSPoint downLeft;
  private GPSPoint downRight;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getZh() {
    return zh;
  }

  public void setZh(String zh) {
    this.zh = zh;
  }

  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }

  public GPSPoint getUpLeft() {
    return upLeft;
  }

  public void setUpLeft(GPSPoint upLeft) {
    this.upLeft = upLeft;
  }

  public GPSPoint getUpRight() {
    return upRight;
  }

  public void setUpRight(GPSPoint upRight) {
    this.upRight = upRight;
  }

  public GPSPoint getDownLeft() {
    return downLeft;
  }

  public void setDownLeft(GPSPoint downLeft) {
    this.downLeft = downLeft;
  }

  public GPSPoint getDownRight() {
    return downRight;
  }

  public void setDownRight(GPSPoint downRight) {
    this.downRight = downRight;
  }

  @Override
  public int compareTo(@NotNull Object o) {
    ParkView parkView = (ParkView) o;
    return this.getId().compareTo(parkView.getId());
  }
}
