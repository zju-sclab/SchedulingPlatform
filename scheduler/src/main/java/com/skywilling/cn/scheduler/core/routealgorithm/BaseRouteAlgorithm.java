package com.skywilling.cn.scheduler.core.routealgorithm;

import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.common.model.Node;

import java.util.List;

public abstract class BaseRouteAlgorithm {

    public  abstract List<LiveLane> process(LiveMap map, Node from, Node to);
}
