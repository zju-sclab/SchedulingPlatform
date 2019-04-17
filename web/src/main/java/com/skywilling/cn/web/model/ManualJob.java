package com.skywilling.cn.web.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ManualJob implements Serializable {

  private static final long serialVersionUID = 3421342133L;
  private String vin;
  private String parkName;
  private String from;
  private String to;
  // optional
  private String launchFile;

}
