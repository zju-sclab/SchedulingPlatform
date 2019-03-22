package com.skywilling.cn.livemap.core;

import com.skywilling.cn.livemap.factory.Factory;
import com.skywilling.cn.livemap.model.*;
import com.skywilling.cn.livemap.service.MapService;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

@Service
public class StaticMapFactory implements Factory<LiveMap> {


    private Document parse(String xmlUrl) throws DocumentException, MalformedURLException {
        URL url = new URL(xmlUrl);
        SAXReader reader = new SAXReader();
        return reader.read(url);
    }

    private void loadNode(Document document, LiveMap liveMap) {
        Element root = document.getRootElement();
        Element locations = root.element("nodes");
        Iterator<Element> iterator = locations.elementIterator();
        while (iterator.hasNext()) {
            Element next = iterator.next();
            if(next.attributeValue("type").equals("station")){
               LiveStation station=new LiveStation();
               readNode(station,next);
               liveMap.getStationMap().putIfAbsent(station.getName(),station);

            }
            if(next.attribute("type").equals("junction")){
                LiveJunction junction=new LiveJunction();
                readNode(junction,next);
                liveMap.getJunctionMap().putIfAbsent(junction.getName(),junction);
            }
        }
    }

    private void readNode(Node node, Element next){
        node.setId(next.attributeValue("id"));
        node.setName(next.attributeValue("name"));
        node.setZh(next.attributeValue("zh"));
        node.setX(Double.valueOf(next.attributeValue("x", "0.0")));
        node.setY(Double.valueOf(next.attributeValue("y", "0.0")));
    }

    private void loadLanes(Document document, LiveMap liveMap) {
        Element root = document.getRootElement();
        Element lanes = root.element("lanes");
        Iterator<Element> iterator = lanes.elementIterator();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            LiveLane lane = new LiveLane();
            lane.setName(element.attributeValue("id"));
            String node=element.attributeValue("from");
            lane.setFrom(liveMap.getJunctionMap().get(node));
            liveMap.getJunctionMap().get(node).getLanesStart().add(lane.getName());
            node=element.attributeValue("to");
            lane.setTo(liveMap.getJunctionMap().get(node));
            liveMap.getJunctionMap().get(node).getLanesEnd().add(lane.getName());
            lane.setLength(Double.valueOf(element.attributeValue("length", "0.0")));
            lane.setZh(element.attributeValue("zh"));
            lane.setV(Double.valueOf(element.attributeValue("v", "0.0")));
            lane.setPriority(Integer.valueOf(element.attributeValue("priority","1")));
            liveMap.getLaneMap().putIfAbsent(lane.getName(),lane);
        }
    }

    @Override
    public LiveMap create(String url) {
        LiveMap liveMap=new LiveMap();
        try {
            Document document = parse(url);
            liveMap.setParkName(String.valueOf(document.getRootElement().attribute("parkName")));
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
