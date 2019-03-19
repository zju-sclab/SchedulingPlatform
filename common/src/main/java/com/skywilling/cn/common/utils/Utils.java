package com.skywilling.cn.common.utils;

import com.skywilling.cn.common.model.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class Utils {
  public static List<Point> toPoints(List<Coordinate> coordinateList) {
    List<Point> points = new ArrayList<>(coordinateList.size());
    for (Coordinate coordinate: coordinateList) {
      Point point = new Point();
      point.setX(coordinate.getX());
      point.setY(coordinate.getY());
      points.add(point);
    }
    return points;
  }
}
