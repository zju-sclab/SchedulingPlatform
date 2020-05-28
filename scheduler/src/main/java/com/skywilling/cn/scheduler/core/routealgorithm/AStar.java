package com.skywilling.cn.scheduler.core.routealgorithm;

import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.common.model.Node;
import com.skywilling.cn.scheduler.core.RouteLogic;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class AStar extends BaseRouteAlgorithm {
    @Autowired
    RouteLogic routeLogic;

    @PostConstruct
    public void init() {
        routeLogic.addAlgorithm("Astar", this);
    }

    /*
     f(n)=g(n)+h(n)
     */
    @Data
    private class Element {
        //路径规划原先用的LiveJuction现在改成基于Node
        Node junction;
        double f = 0;

        Element(Node junction) {
            this.junction = junction;
        }
    }

    private Comparator<Element> comparator = new Comparator<Element>() {
        @Override
        public int compare(Element o1, Element o2) {
            if (o1.getF() >= o2.getF()) return -1;
            else return 1;
        }
    };

    @Override
    public List<LiveLane> process(LiveMap map, Node from, Node to) {
        PriorityQueue<Element> frontier = new PriorityQueue<>(comparator);
        Set<Element> closeList = new HashSet<>();
        HashMap<Element, Double> costSoFar = new HashMap<>();
        HashMap<Node, Node> comesFrom = new HashMap<>();
        frontier.add(new Element(from));


        while (!frontier.isEmpty()) {
            Element curElement = frontier.poll();
            closeList.add(curElement);
            if (curElement.getJunction().equals(to)) {
                return findpath(map, comesFrom, to, from);
            }

            for (String laneName : curElement.getJunction().getLanesStart()) {
                Node next = map.getLaneMap().get(laneName).getTo();
                Element nextElement = new Element(next);
                //已经判断，则跳过
                if (closeList.contains(nextElement)) {
                    continue;
                }
                //f(n)=g(n)+h(n)
                Double newCost = getcost(map, laneName) + heuristic(map, curElement.getJunction(), next);
                if (costSoFar.containsKey(curElement)) {
                    newCost += costSoFar.get(curElement);
                }

                if ((!frontier.contains(nextElement)) || !costSoFar.containsKey(nextElement) || newCost < costSoFar.get(nextElement)) {
                    costSoFar.put(nextElement, newCost);
                    nextElement.setF(newCost);
                    frontier.add(nextElement);
                    comesFrom.put(next, curElement.getJunction());
                }
            }
        }
        return null;
    }

    private List<LiveLane> findpath(LiveMap map, HashMap<Node, Node> comesFrom, Node to, Node from) {

        List<LiveLane> lanes = new ArrayList<>();

        while (!to.getName().equals(from.getName())) {
            Node parent = comesFrom.get(to);
            for (String laneName : parent.getLanesStart()) {
                LiveLane lane = map.getLaneMap().get(laneName);
                if (lane.getTo().equals(to)) {
                    to = parent;
                    lanes.add(lane);
                    break;
                }
            }
        }
        return lanes;
    }

    /*
    曼哈顿距离
     */
    private double heuristic(LiveMap map, Node current, Node to) {

        double h = Math.abs(to.getX() - current.getX()) + Math.abs(to.getY() - current.getY());

        return h;
    }

    private double getcost(LiveMap map, String laneName) {

        LiveLane lane = map.getLaneMap().get(laneName);
        double cost = lane.getLength() / lane.getV();
        return cost;

    }
}
