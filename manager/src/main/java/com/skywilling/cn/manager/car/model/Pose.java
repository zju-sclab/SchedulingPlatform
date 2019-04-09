package com.skywilling.cn.manager.car.model;

import com.skywilling.cn.livemap.model.Point;
import lombok.Data;

import java.io.Serializable;

@Data
public class Pose implements Serializable {

  private static final long serialVersionUID = -5769357827644001273L;
  private Point point;
  private Orientation orientation;


}
