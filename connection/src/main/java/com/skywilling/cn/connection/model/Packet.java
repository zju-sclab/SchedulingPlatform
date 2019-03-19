package com.skywilling.cn.connection.model;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.common.enums.TypeField;
import lombok.Data;

import java.io.Serializable;

@Data
public class Packet implements Serializable {

  private static final long serialVersionUID = -3241431L;
  private String version;
  private int type;
  private int ack;
  private String vin;
  private String data;
  private int requestId;


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Packet packet = (Packet) o;

    if (type != packet.type) {
      return false;
    }
    if (ack != packet.ack) {
      return false;
    }
    if (!version.equals(packet.version)) {
      return false;
    }
    if (!vin.equals(packet.vin)) {
      return false;
    }
    return data.equals(packet.data);
  }

  @Override
  public int hashCode() {
    int result = version.hashCode();
    result = 31 * result + type;
    result = 31 * result + ack;
    result = 31 * result + vin.hashCode();
    if (data != null) {
      result = 31 * result + data.hashCode();
    }
    return result;
  }

  public static class Builder {

    Packet packet = new Packet();

    public Builder buildResponse(String vin, TypeField typeField, ACK ack, int requestId) {
      packet.vin = vin;
      packet.type = typeField.getType();
      packet.ack = ack.getCode();
      packet.requestId = requestId;
      packet.version = "0.1";
      return this;
    }

    public Builder buildBody(Object data) {
      packet.data = JSONObject.toJSONString(data);
      return this;
    }

    public Builder buildBody(String data) {
      packet.data = data;
      return this;
    }

    public Builder buildRequest(String vin, TypeField typeField) {
      packet.vin = vin;
      packet.type = typeField.getType();
      packet.ack = ACK.COMMAND.getCode();
      packet.version = "0.1";
      return this;
    }

    public Packet build() {
      return packet;
    }
  }
}
