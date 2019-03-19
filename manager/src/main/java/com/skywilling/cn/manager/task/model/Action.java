package com.skywilling.cn.manager.task.model;

import com.skywilling.cn.common.enums.DriveMethod;
import com.skywilling.cn.livemap.model.LiveJunction;
import com.skywilling.cn.livemap.model.Point;
import com.skywilling.cn.manager.car.model.Instruction;
import lombok.Data;


import java.io.Serializable;
import java.util.List;

@Data
public class Action implements Serializable{
  private static final long serialVersionUID = 893452345L;
  private String outset;
  private String goal;
  private double v;
  private DriveMethod type;
  private List<Point> points;
  private List<Instruction> instructions;

}
