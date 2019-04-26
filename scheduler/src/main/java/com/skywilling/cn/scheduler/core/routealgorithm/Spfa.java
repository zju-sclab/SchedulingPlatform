package com.skywilling.cn.scheduler.core.routealgorithm;

import com.skywilling.cn.livemap.model.LiveJunction;
import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.scheduler.core.RouteLogic;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class Spfa extends BaseRouteAlgorithm {
    @Autowired
    RouteLogic routeLogic;

    @PostConstruct
    public void init() {
        routeLogic.addAlgorithm("Spfa", this);
    }

    public List<LiveLane> process(LiveMap map, LiveJunction from, LiveJunction to) {
        Queue<String> queue = new ArrayDeque<>();
        queue.add(from.getName());
        Set<String> prev = new HashSet<>();
        List<LiveLane> lanes = new ArrayList<>();
        ConcurrentHashMap<String, LiveJunction> junctionMap = map.getJunctionMap();
        while (!queue.isEmpty()) {
            String cur = queue.poll();
            if (StringUtils.equals(cur, to.getName())) {
                break;
            }

            if (junctionMap != null) {
                for (String laneName : junctionMap.get(cur).getLanesStart()) {
                    if (!prev.contains(cur)) {
                        prev.add(cur);
                        lanes.add(map.getLaneMap().get(laneName));
                        queue.add(map.getLaneMap().get(laneName).getTo().getName());
                    }

                }
            }
        }

        return lanes;
    }
}
