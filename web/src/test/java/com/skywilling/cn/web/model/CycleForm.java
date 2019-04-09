package com.skywilling.cn.web.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CycleForm implements Serializable {

  private static final long serialVersionUID = -572398572345L;
  private String vin;
  private List<String> stations;
  private String parkName;
  private long waitingSeconds;
  private int cycleNumber;

}
