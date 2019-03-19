package com.skywilling.cn.monitor.model.DTO;

import java.io.Serializable;
import java.util.List;

public class RunInfo implements Serializable {

  private static final long serialVersionUID = 6927688126995953819L;
  private String runId;
  private List<String> nodeList;

  public String getRunId() {
    return runId;
  }

  public void setRunId(String runId) {
    this.runId = runId;
  }

  public List<String> getNodeList() {
    return nodeList;
  }

  public void setNodeList(List<String> nodeList) {
    this.nodeList = nodeList;
  }
}
