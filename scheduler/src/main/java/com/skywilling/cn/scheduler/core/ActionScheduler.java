package com.skywilling.cn.scheduler.core;

import com.skywilling.cn.common.exception.park.NoAvailableActionFoundException;
import com.skywilling.cn.common.model.LidarPoint;
import com.skywilling.cn.livemap.model.*;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.ShapeMapService;
import com.skywilling.cn.manager.car.model.Action;
import com.skywilling.cn.scheduler.model.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActionScheduler {

    @Autowired
    ShapeMapService shapeMapService;
    @Autowired
    MapService mapService;

    /**
     * 规划A--》B的路径
     */
    public List<Action> convertToAction(String parkName, Route route)throws NoAvailableActionFoundException  {

        LiveMap liveMap =  mapService.getMap(parkName);
        if (liveMap == null) return new ArrayList<>();

        List<LiveLane> liveLanes = new ArrayList<>(route.getLiveLanes());
        List<Action> actions = new ArrayList<>(liveLanes.size());

        for (LiveLane lane: liveLanes) {
            LaneShape laneShape = shapeMapService.query(parkName,lane.getName());
            List<LidarPoint> lidarPoints = laneShape.getPath();
            Action action = new Action();
            if (lidarPoints == null|| lidarPoints.size() == 0) {
                throw new NoAvailableActionFoundException();
            }
            Node from = lane.getFrom();
            Node to = lane.getTo();
            action.setLaneName(lane.getName());
            action.setOutset(liveMap.getNodeMap().get(from.getName()));
            action.setGoal(liveMap.getNodeMap().get(to.getName()));
            action.setPoints(laneShape.getPath());
            action.setV(lane.getV());
            /** request type LIDAR */
            action.setType("LIDAR");
            actions.add(action);
        }
        return actions;
    }

}
