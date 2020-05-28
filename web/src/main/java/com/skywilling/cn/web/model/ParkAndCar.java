package com.skywilling.cn.web.model;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class ParkAndCar implements Serializable {
  private static final long serialVersionUID = 5369631862241443412L;
  private int parkId;
  private List<String> vins;

}
