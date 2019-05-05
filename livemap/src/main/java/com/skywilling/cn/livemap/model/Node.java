package com.skywilling.cn.livemap.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 点，是合流点和停车点的公有属性
 * */

@Data
public class Node implements Serializable{
  private static final long serialVersionUID = -23142345L;

  private int id;
  private String name;
  private double x;
  private double y;
  private String zh;

  private List<String> LanesStart = new ArrayList<>();
  private List<String> LanesEnd = new ArrayList<>();

  public LiveStation toStation() {
    LiveStation station = new LiveStation();
    station.setName(this.name);
    station.setZh(this.zh);
    station.setX(this.x);
    station.setY(this.y);
    return station;
  }

}
