package com.skywilling.cn.scheduler.core;

import com.skywilling.cn.common.exception.NoAvailableActionFoundException;
import com.skywilling.cn.common.model.Node;
import com.skywilling.cn.common.model.Node_Json;
import com.skywilling.cn.livemap.model.*;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.ShapeService;
import com.skywilling.cn.manager.car.model.Action;
import com.skywilling.cn.scheduler.model.Route;
import com.skywilling.cn.common.model.RoutePoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActionScheduler {

    @Autowired
    ShapeService shapeService;
    @Autowired
    MapService mapService;

    /**
     * 组装A-->B的路径的轨迹点
     */
    public List<Action> convertToAction(String parkName, Route route)throws NoAvailableActionFoundException  {

        LiveMap liveMap =  mapService.getMap(parkName);
        if (liveMap == null) return new ArrayList<>();

        List<LiveLane> liveLanes = new ArrayList<>(route.getLiveLanes());
        List<Action> actions = new ArrayList<>(liveLanes.size());

        for (LiveLane lane: liveLanes) {
            LaneShape laneShape = shapeService.query(parkName,lane.getName());
            List<RoutePoint> lidarPoints = laneShape.getPath();
            Action action = new Action();
            if (lidarPoints == null|| lidarPoints.size() == 0) {
                throw new NoAvailableActionFoundException();
            }
            Node from = lane.getFrom();
            Node to = lane.getTo();
            action.setLaneName(lane.getName());
            action.setOutset(new Node_Json(liveMap.getNodeMap().get(from.getName())));
            action.setGoal(new Node_Json(liveMap.getNodeMap().get(to.getName())));
            action.setPoints(laneShape.getPath());
            action.setV(lane.getV());
            /** request type LIDAR */
            action.setType("LIDAR");
            actions.add(action);
        }
        return actions;
    }
    /**
     * 组装A-->B的路径的轨迹点
     */
    public Action convertToActionByLidarPoints(String parkName, Route route, List<RoutePoint>routePoints)
                        throws NoAvailableActionFoundException  {

        LiveMap liveMap =  mapService.getMap(parkName);
        if (liveMap == null) return null;

        Action action = new Action();
        if (routePoints == null|| routePoints.size() == 0) {
            throw new NoAvailableActionFoundException();
        }
        Node from = route.getFrom();
        Node to = route.getTo();
        action.setOutset(new Node_Json(from));
        action.setGoal(new Node_Json(to));
        action.setPoints(routePoints);
        action.setType("LIDAR");
        return action;
    }

    /**
     * 到某个站点的任务实现 终点的信息在route里面
     */
    public Action convertToStationAction(String parkName, Route route)
            throws NoAvailableActionFoundException  {
        LiveMap liveMap =  mapService.getMap(parkName);
        if (liveMap == null){
            return null;
        }
        Action action = new Action();
        Node from = route.getFrom();
        Node to = route.getTo();
        action.setOutset(new Node_Json(from));
        action.setGoal(new Node_Json(to));
        action.setType("STATION");
        return action;
    }

}
