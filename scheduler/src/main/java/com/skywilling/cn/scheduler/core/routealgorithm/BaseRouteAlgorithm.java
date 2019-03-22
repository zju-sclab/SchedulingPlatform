package com.skywilling.cn.scheduler.core.routealgorithm;

import com.skywilling.cn.livemap.model.LiveJunction;
import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.livemap.model.LiveMap;

import java.util.List;

public abstract class BaseRouteAlgorithm {

    public  abstract List<LiveLane> process(LiveMap map, LiveJunction from, LiveJunction to);
}
