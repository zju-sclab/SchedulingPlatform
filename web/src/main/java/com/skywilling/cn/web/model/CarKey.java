package com.skywilling.cn.web.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
public class CarKey implements Serializable {

  private static final long serialVersionUID = 1l;
  @Id
  private String vin;
  private String key;
  private String verifyCode;
  private boolean isLocked;

}
