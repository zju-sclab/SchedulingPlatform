package com.skywilling.cn.manager.car.model;

import lombok.Data;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Date;

@Repository
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
  /**
   * 所在站点
   */
  private String  station     ;
  /**
   * 所在lane
   */
  private String lane;

  private Double velocity = 0.0;
  private Double wheelAngle = 0.0;
  private Double gear = 0.0;
  private Double energy;

  private Integer isValid     ;
  private Integer status = -1;

  private Double  latitude    ;
  private Double  longitude   ;
  private Double  altitude = 0.0;

  private Double roll = 0.0;
  private Double pitch = 0.0;
  private Double yaw = 0.0;

  private Date    gmtModify   ;
  private Date    gmtCreate   ;

}
