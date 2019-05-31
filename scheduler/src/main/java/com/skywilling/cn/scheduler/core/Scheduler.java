package com.skywilling.cn.scheduler.core;

import com.alibaba.druid.util.StringUtils;
import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.common.model.Node;
import com.skywilling.cn.scheduler.model.Route;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class Scheduler {

  public static final Logger LOG = LoggerFactory.getLogger(Scheduler.class);

  /**
   * split the ride into several segment. each segment is a task. link_0->link_1->link_2->link_3->link_4
   * [0, 1), [1, 3), [3, 5) link_0, link_1->link_2, link_3->link_4
   *
   * for active scheduling.
   */

  List<Integer> split(String regionName, Route route) {
    List<Integer> cuttingPoints = new ArrayList<>();
    // current we simply the implementation of split
    for (int i = 0; i < route.getLiveLanes().size(); ++i) {
      LiveLane lane = route.getLiveLanes().get(i);
      if (shouldStop(regionName, lane.getTo())) {
        cuttingPoints.add(i + 1);
      }
    }
    if (cuttingPoints.size() == 0) {
      cuttingPoints.add(route.getLiveLanes().size());
    }
    return cuttingPoints;
  }

  //todo leave to implementation
  protected boolean shouldStop(String regionName, Node node) {
    return false;
  }

  /**
   * for passive occasions,  try to acquireLock pass rights
   * todo leave to implementation
   */
  public boolean acquireDrivingRights(String vin, String regionName, String nodeName) {
    return false;
  }

  @Data
  public static class NodeLock {
      private String name;
      private String owner;
      private ReentrantLock lock = new ReentrantLock();
      private long startTime;
      private PriorityBlockingQueue<Element> inComingVehicles = new PriorityBlockingQueue<>();

      public NodeLock(String name) {
          this.name = name;
      }

      static class Element implements Comparable<Element> {
          String vin;
          CompletableFuture<Boolean> result;
          double priority;

          public Element(String vin, CompletableFuture<Boolean> result, double priority) {
              this.vin = vin;
              this.result = result;
              this.priority = priority;
          }

          public String getVin() {
              return vin;
          }

          @Override
          public int compareTo(Element o) {
              if (this.priority > o.priority) return 1;
              if (this.priority < o.priority) return -1;
              return 0;
          }
      }

      public boolean hasCar(String vin){
          for(Element x : inComingVehicles){
              if(x.getVin().equals(vin)){
                  return true;
              }
          }
          return false;
      }

      public CompletableFuture<Boolean> acquire(String vin, double priority) {
          CompletableFuture<Boolean> result = new CompletableFuture<>();
          try {

              lock.lock();
              if (owner == null || StringUtils.equals(owner, vin)) {
                  LOG.warn("vin: " + vin + " accquire lock and be the owner ! ");
                  owner = vin;
                  result.complete(true);
              } else {
                  LOG.warn("vin: " + vin + " accquire lock and wait in queue ! ");
                  Element element = new Element(vin, result, priority);
                  inComingVehicles.put(element);
              }
          } finally {
              lock.unlock();
          }
          return result;
      }

      public String release(String vin) {
          if (!owner.equalsIgnoreCase(vin)) {
              LOG.warn("vin: " + vin + " release lock and invalid ! ");
              return null;
          }
          try {
              lock.lock();
              Element front = inComingVehicles.poll();
              if (front != null) {
                  LOG.warn("vin: " + vin + " release lock and notify vin : " + front.vin);
                  owner = front.vin;
                  front.result.complete(true);
              } else {
                  LOG.warn("vin: " + vin + " release lock and notify nobody! ");
                  owner = null;
              }
          } finally {
              lock.unlock();
          }
          return owner;
      }

  }
}
