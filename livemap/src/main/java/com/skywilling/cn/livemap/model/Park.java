package com.skywilling.cn.livemap.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Park implements Serializable, Comparable {

  private static final long serialVersionUID = -896649440578530717L;
  private Integer id;
  private String name;

  private String zh;

  private String imgUrl;
  //min longitude
  private Double minx;
  //min latitude
  private Double miny;
  private Double maxx;
  private Double maxy;

  private String mapFileUrl;

  private String shapeFileUrl;

  private String province;

  private String city;

  private String area;

  private Date gmtModify;

  private Date gmtCreate;

  @Override
  public int compareTo(Object o) {
    Park park = (Park)o;
    return this.getId().compareTo(park.getId());
  }
}
