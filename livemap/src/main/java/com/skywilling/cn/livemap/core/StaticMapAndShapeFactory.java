package com.skywilling.cn.livemap.core;

import com.skywilling.cn.common.model.LidarPoint;
import com.skywilling.cn.common.model.Node;
import com.skywilling.cn.livemap.model.*;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.ShapeMapService;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ClassName StaticMapAndShapeFactory
 * Author  Lin
 * Date 2019/5/19 21:07
 **/
@Service
public class StaticMapAndShapeFactory {
    private static final Logger LOG = LoggerFactory.getLogger(StaticLaneShapFactory.class);

    @Autowired
    ShapeMapService shapeMapService;
    @Autowired
    MapService mapService;


    private Document parse(String path) throws DocumentException, MalformedURLException {
        URL url = new URL(path);
        SAXReader saxReader = new SAXReader();
        return saxReader.read(url);
    }

    /**
    *  读取固定站点配置
    */
    private void loadNode(Document document, LiveMap liveMap) {
        Element root = document.getRootElement();
        Element locations = root.element("nodes");
        Iterator<Element> iterator = locations.elementIterator();
        while (iterator.hasNext()) {
            Element next = iterator.next();
            /**读取固定站点坐标和id*/
            Node station = new Node();
            station.setX(Double.valueOf(next.attributeValue("x")));
            station.setY(Double.valueOf(next.attributeValue("y")));
            station.setId(Integer.valueOf(next.attributeValue("id")));
            station.setName(next.attributeValue("name"));
            /**更新LiveMap的Node集合，基于ID索引*/
            liveMap.getNodeMap().put(String.valueOf(station.getId()), station);
        }
    }

    /**
     *  读取直线配置
     */
    private void loadLanes(Document document, LiveMap liveMap) {
        Element root = document.getRootElement();
        Element lanes = root.element("lanes");
        Iterator<Element> iterator = lanes.elementIterator();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            loadOneLane(element,liveMap,"lane");
        }
    }

    /**
     *  读取弯道配置
     */
    private void loadCurves(Document document, LiveMap liveMap) {
        Element root = document.getRootElement();
        Element lanes = root.element("curves");
        Iterator<Element> iterator = lanes.elementIterator();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            loadOneLane(element,liveMap,"curve");
        }
    }
    /**组装lane属性*/
    private void loadOneLane(Element element,LiveMap liveMap, String type){
        LiveLane lane = new LiveLane();
        lane.setId(Integer.valueOf(element.attributeValue("id")));
        lane.setName(element.attributeValue("name"));
        lane.setLength(Double.valueOf(element.attributeValue("length", "0.0")));
        lane.setZh(element.attributeValue("zh"));
        lane.setType(type);
        lane.setV(Double.valueOf(element.attributeValue("v", "0.0")));
        lane.setPriority(Double.valueOf(element.attributeValue("priority", "1.0")));
        //更新LiveMap的Lane
        liveMap.getLaneMap().put(String.valueOf(lane.getType()+lane.getId()), lane);
    }
    /**
     * 根据Map.xml构建拓扑地图，拓扑地图仅仅路段和站点的基本信息
     */
    public LiveMap create(String parkName, String mapUrl, String shapeUrl) {
        LiveMap liveMap = new LiveMap();
        try {
            Document document = parse(mapUrl);
            liveMap.setParkName(parkName);
            loadNode(document, liveMap);
            loadLanes(document, liveMap);
            loadCurves(document, liveMap);
            return liveMap;
        } catch (DocumentException | MalformedURLException e) {
           e.printStackTrace();
        }
        return null;
    }
}
