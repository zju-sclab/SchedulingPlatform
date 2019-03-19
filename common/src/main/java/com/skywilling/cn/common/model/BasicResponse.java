package com.skywilling.cn.common.model;

import com.skywilling.cn.common.enums.ResultType;

public class BasicResponse {

  private int code;
  private String msg;
  private Object attach;

  public BasicResponse(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public static BasicResponse buildResponse(ResultType resultType, Object attach) {
    BasicResponse response = new BasicResponse(resultType.getCode(), resultType.getMsg());
    response.setAttach(attach);
    return response;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public Object getAttach() {
    return attach;
  }

  public void setAttach(Object attach) {
    this.attach = attach;
  }
}
