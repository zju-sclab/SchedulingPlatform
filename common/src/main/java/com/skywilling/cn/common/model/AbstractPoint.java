package com.skywilling.cn.common.model;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public abstract class AbstractPoint implements Serializable{
  protected static final long serialVersionUID = -3245643463L;

  @Override
  public String toString() {
    return JSONObject.toJSONString(this);
  }
}
