package com.skywilling.cn.connection.model;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.common.model.BasicCarResponse;
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
  public String toString(){
    String str = "";
    str += " type: " + Integer.toString(type);
    str += " ack: " + Integer.toString(ack);
    str += " requestId: " + Integer.toString(requestId);
    str += " vin: " + vin;
    str += " data: " + data;
    return str;
  }

  public int getType(){
    return type;
  }

  public String getVin(){
    return vin;
  }

  public String getData(){
    return data;
  }


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
      packet.version = "0.2";
      return this;
    }

    public Builder buildResponse(Packet packet, BasicCarResponse response){
      this.packet.type = packet.type;
      this.packet.requestId = packet.requestId;
      this.packet.vin = packet.getVin();
      this.packet.version = packet.getVersion();
      this.packet.ack = response.getCode();
      this.packet.data = JSONObject.toJSONString(response.getAttach());
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
      packet.version = "0.2";
      return this;
    }

    public Packet build() {
      return packet;
    }
  }
}
