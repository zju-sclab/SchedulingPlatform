package com.skywilling.cn.web.model.view;

import com.skywilling.cn.common.model.Node;
import lombok.Data;

import java.io.Serializable;

@Data
public class CarView implements Serializable {
  private static final long serialVersionUID = -8876768690242539518L;
  private String vin;
  private String parkName;
  private Node node;
  private int state;
  private int healthState;
}
