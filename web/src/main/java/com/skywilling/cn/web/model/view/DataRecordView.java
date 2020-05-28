package com.skywilling.cn.web.model.view;

import java.io.Serializable;

public class DataRecordView implements Serializable {

  private static final long serialVersionUID = -7652415968256155790L;
  private String vin;
  private String runId;
  private String url;
  private String type;
  private Long size;

  public String getVin() {
    return vin;
  }

  public void setVin(String vin) {
    this.vin = vin;
  }

  public String getRunId() {
    return runId;
  }

  public void setRunId(String runId) {
    this.runId = runId;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }
}
