package com.skywilling.cn.scheduler.utils;

import com.skywilling.cn.common.model.Coordinate;

public class FunctionUtils {
  public static double square(double x) {
    return x * x;
  }
  public static double distance(Coordinate p, Coordinate q) {
    return Math.sqrt(square(p.getX() - q.getX()) + square(p.getY() - q.getY()));
  }
  public static double distance(double x_1, double y_1, double x_2, double y_2) {
    return Math.sqrt(square(x_1 - x_2) + square(y_1 - y_2));
  }
}
