package com.skywilling.cn.livemap.model;

import java.io.Serializable;
import java.util.Date;

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

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getZh() {
    return zh;
  }

  public void setZh(String zh) {
    this.zh = zh;
  }

  public String getImgUrl() {
    return imgUrl;
  }

  public void setImgUrl(String imgUrl) {
    this.imgUrl = imgUrl;
  }

  public Double getMinx() {
    return minx;
  }

  public void setMinx(Double minx) {
    this.minx = minx;
  }

  public Double getMiny() {
    return miny;
  }

  public void setMiny(Double miny) {
    this.miny = miny;
  }

  public Double getMaxx() {
    return maxx;
  }

  public void setMaxx(Double maxx) {
    this.maxx = maxx;
  }

  public Double getMaxy() {
    return maxy;
  }

  public void setMaxy(Double maxy) {
    this.maxy = maxy;
  }

  public String getMapFileUrl() {
    return mapFileUrl;
  }

  public void setMapFileUrl(String mapFileUrl) {
    this.mapFileUrl = mapFileUrl;
  }

  public String getShapeFileUrl() {
    return shapeFileUrl;
  }

  public void setShapeFileUrl(String shapeFileUrl) {
    this.shapeFileUrl = shapeFileUrl;
  }

  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public Date getGmtModify() {
    return gmtModify;
  }

  public void setGmtModify(Date gmtModify) {
    this.gmtModify = gmtModify;
  }

  public Date getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  @Override
  public int compareTo(Object o) {
    Park park = (Park)o;
    return this.getId().compareTo(park.getId());
  }
}
