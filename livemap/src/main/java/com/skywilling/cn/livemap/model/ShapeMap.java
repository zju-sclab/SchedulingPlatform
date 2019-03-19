package com.skywilling.cn.livemap.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class ShapeMap {
   private String parkName;
   private ConcurrentHashMap<String,LaneShape> laneshapeMap=new ConcurrentHashMap<>();

   public void addLaneShape(LaneShape laneShape){
      laneshapeMap.putIfAbsent(laneShape.getLaneName(),laneShape);
   }

   public List<LaneShape> query(List<String> lanes){
      List<LaneShape> list=new ArrayList<>();
      for(String name:lanes){
         list.add(laneshapeMap.get(name));
      }
      return list;
   }

   public LaneShape getLaneShape(String laneName){
      return laneshapeMap.get(laneName);
   }
}
