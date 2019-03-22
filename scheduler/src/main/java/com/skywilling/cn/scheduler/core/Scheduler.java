package com.skywilling.cn.scheduler.core;

import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.livemap.model.Node;
import com.skywilling.cn.scheduler.model.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
}
