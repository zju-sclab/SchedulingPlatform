package com.skywilling.cn.common.model;

import com.skywilling.cn.common.enums.ResultType;
import lombok.Data;

@Data
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

}
