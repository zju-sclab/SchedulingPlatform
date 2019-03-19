package com.skywilling.cn.manager.car.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CarDynamic implements Serializable {

  private static final long serialVersionUID = 6704319415892113323L;

  private String  vin;
  private String carPlate;
  private Integer parkId;
  private String parkName;
  private Integer definitionId = 1;
  private Integer type;
  private Integer simulation;

  private Integer driveMode   ;
  private Integer useStatus   ;
  private Integer bodyStatus  ;
  private Integer moduleStatus;
  private Integer connect     ;

  private Double  endurance   ;

  private String  station     ;

  private Double velocity = 0.0;
  private Double wheelAngle = 0.0;
  private Double gear = 0.0;

  private Integer isValid     ;
  private String lane;
  private Double  latitude    ;
  private Double  longitude   ;
  private Double  altitude = 0.0;

  private Double roll = 0.0;
  private Double pitch = 0.0;
  private Double yaw = 0.0;
  private Integer status = -1;


  private Date    gmtModify   ;
  private Date    gmtCreate   ;

}
