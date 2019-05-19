package com.skywilling.cn.common.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Pose implements Serializable {

  private static final long serialVersionUID = -5769357827644001273L;
  private Point point;
  private Orientation orientation;
  public Pose(){}
  public Pose(Point point, Orientation orientation){ this.point = point; this.orientation = orientation;}
}
