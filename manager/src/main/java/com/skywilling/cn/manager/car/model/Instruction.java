package com.skywilling.cn.manager.car.model;

import com.skywilling.cn.common.model.AbstractPoint;
import lombok.Data;

@Data
public class Instruction extends AbstractPoint {
  private double motor;
  private double servo;
  private int shift;
}
