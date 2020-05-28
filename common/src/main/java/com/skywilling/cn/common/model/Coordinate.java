package com.skywilling.cn.common.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coordinate extends AbstractPoint {
  private double x;
  private double y;
}
