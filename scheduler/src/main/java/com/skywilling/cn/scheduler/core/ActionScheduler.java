package com.skywilling.cn.scheduler.core;

import com.skywilling.cn.common.enums.DriveMethod;
import com.skywilling.cn.common.exception.park.NoAvailableActionFoundException;
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
    public List<Action> convertToAction(String parkName, Route route) throws NoAvailableActionFoundException {

        LiveMap liveMap=mapService.getMap(parkName);
        if (liveMap == null) return null;

        List<LiveLane> liveLanes = route.getLiveLanes();
        List<Action> actions = new ArrayList<>(liveLanes.size());

        for (LiveLane lane: liveLanes) {
            Action action = this.toAction(shapeMapService.query(parkName, lane.getName()));
            if (action == null) {
                throw new NoAvailableActionFoundException();
            }
            Node from = lane.getFrom();
            Node to = lane.getTo();
            action.setLaneName(lane.getName());
            action.setFrom(from.getId());
            action.setTo(to.getId());
            action.setV(lane.getV());
            actions.add(action);
        }
        return actions;
    }

    private Action toAction(LaneShape laneShape) {


        return null;
    }
}
