package com.skywilling.cn.web.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class AutoJob implements Serializable {

  private static final long serialVersionUID = 5689324234543L;

  private String vin;
  private String from;
  private String to;
  private String parkName;
  private String launchFile;

}
