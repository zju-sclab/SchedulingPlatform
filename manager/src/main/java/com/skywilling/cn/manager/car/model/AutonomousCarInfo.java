package com.skywilling.cn.manager.car.model;



import com.skywilling.cn.manager.car.enumeration.CarState;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Data
@Repository
@Document(collection = "autonomousCarInfo")
@CompoundIndexes({
        @CompoundIndex(name = "location_index", def = "{position': '2dsphere'}"),
})
public class AutonomousCarInfo implements Serializable {

  private static final long serialVersionUID = 23412341209L;
  @Id
  private String vin;
  @Field
  private String tripId;
  @Field
  private int state;
  @Field
  private String taskId;
  @Field
  private double velocity;
  @Field
  private double wheelAngle;
  @Field
  private double gear;
  @Field
  private Pose pose;

  @Field String station;
  @Field
  private String lane;
  @Field
  private List<ModuleInfo> nodes;
  @Field
  private long timestamp;

  /*
  便于直接使用geohash索引
   */
  @Field
  private GeoJsonPoint location;

  public void setPose(Pose pose){
    this.pose=pose;
    this.location=new GeoJsonPoint(pose.getPoint().getX(),pose.getPoint().getY());
  }


  public AutonomousCarInfo() {

  }

  public AutonomousCarInfo(String vin) {
    this.vin = vin;
    this.tripId = null;
  }


  public void setLocation(Pose pose){
    this.location=new GeoJsonPoint(pose.getPoint().getX(),pose.getPoint().getY());
  }

  public boolean isConnected() {
    return this.state != CarState.LOST.getState();
  }
}
