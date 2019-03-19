package com.skywilling.cn.common.utils;

import com.skywilling.cn.common.model.Coordinate;
import org.apache.commons.lang3.StringUtils;

public class GPSUtils {

  public static double toDegree(double v) {
    double ret = (int) (v / 100);
    double lowv = v - ret * 100;
    ret = ret + lowv / 60;
    return ret;
  }

  /**
   * gga format: "$gpgga,time,latitude,north/south,longitude,east/west, ..." refer to
   * @link{http://baike.baidu.com/item/GPGGA/8818840}
   */
  public static Coordinate parsegga(String line) {
    String[] vec = line.trim().split(",");
    double latitude = Double.valueOf(vec[2]);
    double longitude = Double.valueOf(vec[4]);

    latitude = toDegree(latitude);
    longitude = toDegree(longitude);

    if (!StringUtils.equals("N", vec[3])) {
      latitude = -latitude;
    }
    if (!StringUtils.equals("E", vec[5])) {
      longitude = -longitude;
    }
    int valid = Integer.valueOf(vec[6]);
    if (valid == 4 || valid == 5) {
      return GPSUtils.gps2Mecator(longitude, latitude);
    }
    return null;
  }

  public static Coordinate gps2Mecator(double longitude, double latitude) {
    Coordinate coordinate = new Coordinate();
    coordinate.setX(longitude);
    coordinate.setY(latitude);
    return coordinate;
//todo: ignore gps2Mecator
//    coordinate.setX(longitude * 20037508.34 / 180);
//    double y = Math.log(Math.tan((90 + latitude) * Math.PI / 360)) / (Math.PI / 180);
//    y = y * 20037508.34 / 180;
//    coordinate.setY(y);
//    return coordinate;
  }

  public static double norm2(Coordinate s, Coordinate d) {
    double dx = s.getX() - d.getX();
    double dy = s.getY() - d.getY();
    return Math.sqrt(dx * dx + dy * dy);
  }
}
