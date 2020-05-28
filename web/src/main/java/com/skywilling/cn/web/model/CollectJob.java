package com.skywilling.cn.web.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
public class CollectJob implements Serializable {

  private static final long serialVersionUID = 1l;

  private String vin;
  @Id
  private String rideId; // refer to driving job id
  private String host;
  private String port;
  private String wifiSSID;
  private String wifiPassword;


}
