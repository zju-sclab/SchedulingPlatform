package com.skywilling.cn.livemap.core;

import com.skywilling.cn.common.config.redis.RedisDao;
import com.skywilling.cn.livemap.model.Point;
import com.skywilling.cn.livemap.factory.Factory;
import com.skywilling.cn.livemap.model.LaneShape;
import com.skywilling.cn.livemap.service.ShapeMapService;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于读取高清地图的具体轨迹点
 */
@Service
public class StaticLaneShapFactory implements Factory<Boolean> {
    private static final Logger LOG = LoggerFactory.getLogger(StaticLaneShapFactory.class);

    @Autowired
    ShapeMapService shapeMapService;


    private Document parse(String path) throws DocumentException, MalformedURLException {
        URL url = new URL(path);
        SAXReader saxReader = new SAXReader();
        return saxReader.read(url);
    }



    private List<Point> loadTrj(Element pathRoot) {
        if (pathRoot == null) {
            return null;
        }
        List<Element> points = pathRoot.elements();
        ArrayList<Point> coordinates = new ArrayList<>(points.size());
        for (Element e : points) {
            Point p = new Point();
            p.setX(Double.valueOf(e.attributeValue("x")));
            p.setY(Double.valueOf(e.attributeValue("y")));
            p.setZ(Double.valueOf(e.attributeValue("z")));
            coordinates.add(p);
        }
        return coordinates;
    }

    private void loadLaneShapes(String parkName, Document document) {

        List<Node> lanes = document.selectNodes("//region/lane");
        for (Node lane : lanes) {
            Element element = (Element) lane;
            LaneShape laneShape = new LaneShape();
            laneShape.setParkName(parkName);
            laneShape.setId(element.attributeValue("id", ""));
            List<Point> ways = loadTrj(element.element("path"));
            if (ways != null) {
                laneShape.setPath(ways);
            }
           shapeMapService.save(laneShape);
        }

    }



    @Override
    public Boolean create(String url) {
        try {
            Document document = parse(url);
            String parkName=String.valueOf(document.getRootElement().attribute("id"));
            loadLaneShapes(parkName, document);
        } catch (DocumentException e) {
            LOG.warn(String.format("create ShapeContainer documentException error, file: {}", url));
        } catch (MalformedURLException e) {
            LOG.warn(String.format("create ShapeContainer MalformedURLException error, url: %s", url));
        }
        return null;
    }
}
