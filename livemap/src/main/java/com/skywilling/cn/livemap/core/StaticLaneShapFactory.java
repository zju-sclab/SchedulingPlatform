package com.skywilling.cn.livemap.core;

import com.skywilling.cn.common.enums.DriveMethod;
import com.skywilling.cn.common.model.Coordinate;
import com.skywilling.cn.manager.car.model.Instruction;
import com.skywilling.cn.livemap.factory.Factory;
import com.skywilling.cn.livemap.model.LaneShape;
import com.skywilling.cn.livemap.model.ShapeMap;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于读取高清地图的具体轨迹点
 */
public class StaticLaneShapFactory implements Factory<ShapeMap> {
    private static final Logger LOG = LoggerFactory.getLogger(StaticLaneShapFactory.class);

    private Document parse(String path) throws DocumentException, MalformedURLException {
        URL url = new URL(path);
        SAXReader saxReader = new SAXReader();
        return saxReader.read(url);
    }
    /**
     *
     * <line>
     *   <trajectory>
     *
     *   </trajectory>
     *   <instructions>
     *
     *   </instructions>
     *   <site fromQr="" fromBaseX="" fromBaseZ="" toQr="" toBaseX="" toBaseZ="" scene="" curveRadius="" curveAngle="" speed="" shift="">
     *   </site>
     * </line>
     * */


    private List<Coordinate> loadTrj(Element pathRoot) {
        if (pathRoot == null) {
            return null;
        }
        List<Element> points = pathRoot.elements();
        ArrayList<Coordinate> coordinates = new ArrayList<>(points.size());
        for (Element e : points) {
            Coordinate p = new Coordinate();
            p.setX(Double.valueOf(e.attributeValue("x")));
            p.setY(Double.valueOf(e.attributeValue("y")));
            coordinates.add(p);
        }
        return coordinates;
    }

    private List<Instruction> loadInstructions(Element instRoot) {
        if (instRoot == null) {
            return null;
        }
        List<Element> records = instRoot.elements();
        ArrayList<Instruction> insts = new ArrayList<>(records.size());
        for (Element e: records) {
            Instruction instruction = new Instruction();
            instruction.setMotor(Double.valueOf(e.attributeValue("motor")));
            instruction.setServo(Double.valueOf(e.attributeValue("servo")));
            instruction.setShift(Integer.valueOf(e.attributeValue("shift")));
            insts.add(instruction);
        }
        return insts;
    }

    private void loadLaneShapes(ShapeMap shapeMap, Document document) {
        List<Node> links = document.selectNodes("//region/link");
        for (Node link: links) {
            Element element = (Element) link;
            LaneShape laneShape = new LaneShape();
            laneShape.setFrom(element.attributeValue("from", ""));
            laneShape.setTo(element.attributeValue("to", ""));
            laneShape.setId(element.attributeValue("id", ""));
            laneShape.setPriority(Integer.valueOf(element.attributeValue("priority", "1")));
            String supported = element.attributeValue("supported", "");
            String[] methods = supported.split(",");
            for (String method: methods) {
                DriveMethod type = DriveMethod.toDriveMehtod(method);
                if (type != null) {
                    if (type == DriveMethod.TRJ){
                        List<Coordinate> ways = loadTrj(element.element("path"));
                        if (ways != null) {
                            laneShape.setPath(ways);
                            laneShape.addSupport(type);
                        }
                    }
                    if (type == DriveMethod.LANE) {
                        laneShape.addSupport(type);
                    }
                    if (type == DriveMethod.REPLAY) {
                        List<Instruction> instructions = loadInstructions(element.element("instructions"));
                        if (instructions != null) {
                            laneShape.setInstructions(instructions);
                            laneShape.addSupport(type);
                        }
                    }
                    if (type == DriveMethod.SITE) {
                        laneShape.addSupport(DriveMethod.SITE);
                    }
                }
            }
            shapeMap.addLaneShape(laneShape);
        }
    }

    @Override
    public ShapeMap create(String url) {
        try {
            Document document = parse(url);
            ShapeMap shapeMap = new ShapeMap();
            loadLaneShapes(shapeMap, document);
            return shapeMap;
        } catch (DocumentException e) {
            LOG.warn(String.format("create ShapeContainer documentException error, file: {}", url));
        } catch (MalformedURLException e) {
            LOG.warn(String.format("create ShapeContainer MalformedURLException error, url: %s", url));
        }
        return null;
    }
}
