package com.skywilling.cn.scheduler.core.routealgorithm;

import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.common.model.Node;
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


    //采用Node普通节点做规划，和调度无关
    public List<LiveLane> process(LiveMap map, Node from, Node to) {
        Queue<String> queue = new ArrayDeque<>();
        queue.add(from.getName());
        //prev保存处理过的节点名
        Set<String> prev = new HashSet<>();
        List<LiveLane> lanes = new ArrayList<>();
        //获取拓扑图的节点
        ConcurrentHashMap<String, Node> junctionMap = map.getNodeMap();
        while (!queue.isEmpty()) {
            //获取队列中当前处理的Node的节点名
            String cur = queue.poll();
            //到达终点退出循环
            if (StringUtils.equals(cur, to.getName())) {
                break;
            }
            //判断地图是否存在
            if (junctionMap != null) {
                //判断是否存在当前节点开始的路段
                for (String laneName : junctionMap.get(cur).getLanesStart()) {
                    //判断当前节点是否规划过
                    if (!prev.contains(cur)) {
                        //加入prev
                        prev.add(cur);
                        //加入结果集合
                        lanes.add(map.getLaneMap().get(laneName));
                        //处理当前路段的终点作为下一个节点
                        queue.add(map.getLaneMap().get(laneName).getTo().getName());
                    }

                }
            }
        }
        //返回路段的规划结果
        return lanes;
    }
}
