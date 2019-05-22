package com.skywilling.cn.livemap.core;

import com.skywilling.cn.common.model.Node;
import com.skywilling.cn.livemap.factory.Factory;
import com.skywilling.cn.livemap.model.*;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

@Service
public class StaticMapFactory implements Factory<LiveMap> {


    private Document parse(String xmlUrl) throws DocumentException, MalformedURLException {
        //URL url = new URL(xmlUrl);
        SAXReader reader = new SAXReader();
        return reader.read(xmlUrl);
    }

    private void loadNode(Document document, LiveMap liveMap) {
        Element root = document.getRootElement();
        Element locations = root.element("nodes");
        Iterator<Element> iterator = locations.elementIterator();
        while (iterator.hasNext()) {
            Element next = iterator.next();
            if (next.attributeValue("type").equals("station")) {
                LiveStation station = new LiveStation();

                station.setX(Double.valueOf(next.attributeValue("x")));
                station.setY(Double.valueOf(next.attributeValue("y")));
                station.setId(Integer.valueOf(next.attributeValue("id")));
                station.setName(next.attributeValue("name"));

                //更新LiveMap的LiveStation集合
                liveMap.getStationMap().putIfAbsent(station.getName(), station);
                //更新LiveMap的Node集合
                liveMap.getNodeMap().putIfAbsent(station.getName(), station);

            }
            else if (next.attributeValue("type").equals("junction")) {
                LiveJunction junction = new LiveJunction();

                junction.setX(Double.valueOf(next.attributeValue("x")));
                junction.setY(Double.valueOf(next.attributeValue("y")));
                junction.setId(Integer.valueOf(next.attributeValue("id")));
                junction.setName(next.attributeValue("name"));

                //更新LiveMap的LiveJunction集合
                liveMap.getJunctionMap().putIfAbsent(junction.getName(), junction);
                //更新LiveMap的Node集合
                liveMap.getNodeMap().putIfAbsent(junction.getName(), junction);
            }

        }
    }

/*    private void readNode(Node node, Element next) {
        node.setId(next.attributeValue("id"));
        node.setName(next.attributeValue("name"));
        node.setZh(next.attributeValue("zh"));
        node.setX(Double.valueOf(next.attributeValue("x", "0.0")));
        node.setY(Double.valueOf(next.attributeValue("y", "0.0")));
    }*/

    /**
     *  从XML解析的文档加载地图到LiveMap
     */
    private void loadLanes(Document document, LiveMap liveMap) {
        Element root = document.getRootElement();
        Element lanes = root.element("lanes");
        Iterator<Element> iterator = lanes.elementIterator();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            LiveLane lane = new LiveLane();

            //设置唯一ID
            lane.setId(Integer.valueOf(element.attributeValue("id")));

            //设置唯一名字
            lane.setName(element.attributeValue("name"));
            //LiveLane起点Junction
            String fromName = element.attributeValue("from");
            //设置lane的起点
            lane.setFrom(liveMap.getNodeMap().get(fromName));

            //更新起点的JunctionMap的start
            Node node = liveMap.getNodeMap().get(fromName);
            //获取start集合
            List<String> set = node.getLanesStart();
            //当前路段加入到start集合
            set.add(element.attributeValue("name"));

            //更新LiveLane的终点
            String toName = element.attributeValue("to");
            lane.setTo(liveMap.getNodeMap().get(toName));

            //更新起点的JunctionMap的End
            Node node2 = liveMap.getNodeMap().get(toName);
            //获取end集合
            List<String> set2 = node2.getLanesEnd();
            //加入end集合
            set2.add(element.attributeValue("name"));

            //更新路长
            lane.setLength(Double.valueOf(element.attributeValue("length", "0.0")));
            //设置中文名
            lane.setZh(element.attributeValue("zh"));
            //更新最大速度
            lane.setV(Double.valueOf(element.attributeValue("v", "0.0")));
            //更新调度行使权的权重
            lane.setPriority(Integer.valueOf(element.attributeValue("priority", "1")));
            //更新LiveMap的Lane
            liveMap.getLaneMap().putIfAbsent(lane.getName(), lane);
        }
    }

    /**
     * 根据Map.xml构建拓扑地图，拓扑地图仅仅路段和站点的基本信息
     */
    @Override
    public LiveMap create(String parkName, String mapUrl) {
        LiveMap liveMap = new LiveMap();
        try {
            Document document = parse(mapUrl);
            liveMap.setParkName(parkName);
            loadNode(document, liveMap);
            loadLanes(document, liveMap);
            return liveMap;
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
