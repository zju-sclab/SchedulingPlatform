package com.skywilling.cn.manager.car.model;



import com.skywilling.cn.common.model.Pose;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
@Repository
@Document(collection = "autonomousCarInfo")
//复合索引，加复合索引后通过复合索引字段查询将大大提高速度,如@CompoundIndex(name = "age_idx", def = "{'lastName': 1, 'age': -1}")
//lastName和age将作为复合索引age_idx，数字参数指定索引的方向，1为正序，-1为倒序。方向对单键索引和随机存不要紧，但如果你要执行分组和排序操作的时候，它就非常重要了
//空间索引(判断一个点POINT是否在一个区域POLYGON内)
@CompoundIndexes({
        @CompoundIndex(name = "location_index", def = "{'position':'2dsphere'}"),
        @CompoundIndex(name = "lane_station_idx", def = "{'lane':1, 'station':1}"),
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
  @Field
  private String station;
  /** 当前的lane*/
  @Field
  private String fromLane;
  /** 预瞄的lane*/
  @Field
  private String lane;
  @Field
  private long timestamp;
  @Field
  private List<ModuleInfo> RosNodes;
  /**
  * 便于直接使用geohash索引
   * */
/*  private GeoJsonPoint geoJsonPoint;*/


  public AutonomousCarInfo(String vin) {
    this.vin = vin;
    this.tripId = null;
  }
/*  public AutonomousCarInfo(String vin, GeoJsonPoint jsonPoint){
    this.vin = vin;
    this.setGeoJsonPoint(jsonPoint);
  }*/

}
