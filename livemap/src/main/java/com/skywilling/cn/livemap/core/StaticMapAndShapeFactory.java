package com.skywilling.cn.livemap.core;

import com.skywilling.cn.common.model.LidarPoint;
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
     * 获取path目录下的所有文件
     */
    public List<String> getAllFiles(String path)  {
        ArrayList<String> files = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile())
                files.add(tempList[i].toString());
        }
        return files;
    }

    /**
     * 读取所有的line和cross文件,返回路段数组
     *
     * @param files
     */
    public List<LaneShape> readALLFiles(List<String> files) {

        List<LaneShape> laneShapes = new ArrayList<>();
        BufferedReader br ;
        String line;
        List<LidarPoint> allLidarPoint = new ArrayList<>();
        for (String filename : files) {
            LaneShape laneShape = new LaneShape();

            if (filename.startsWith("lane")) laneShape.setType("Lane");
            else laneShape.setType("Curve");

            try {
                br = new BufferedReader(new FileReader(new File(filename)));

                //laneShape.setId(laneShape.getType() + br.readLine()); //Id
                laneShape.setLength(Double.valueOf(br.readLine()));//长度
                laneShape.setPriority(Integer.valueOf(br.readLine()));//权重

                while ((line = br.readLine()) != null) {
                    String[] lineSplit = line.trim().split(",");
                    LidarPoint lidarPoint = new LidarPoint(
                            lineSplit[0], lineSplit[1], lineSplit[2],
                            lineSplit[3], lineSplit[4], lineSplit[5], lineSplit[6]);
                    allLidarPoint.add(lidarPoint);
                }
                laneShape.setPath(allLidarPoint);
                laneShapes.add(laneShape);
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return laneShapes;
    }


    /**
     * 读取所有的line和cross文件,返回路段数组
     *
     * @param files
     */
    public List<LaneShape> readCSVFiles(String parkName, List<String> files) {

        List<LaneShape> laneShapes = new ArrayList<>();
        BufferedReader br;
        String line;

        for (String filename : files) {
            LaneShape laneShape = new LaneShape();
            /**
             * 根据去除绝对路径后的文件名得到lane_id
             */
            String filename_no_pre = filename.substring(filename.lastIndexOf("\\")+1);
            String lane_id = filename_no_pre.substring(0,1);
            String prefix = "lane_";
            LiveMap liveMap = mapService.getMap(parkName);
            LiveLane liveLane = liveMap.getLaneMap().get(prefix+lane_id);
            laneShape.setParkName(parkName);
            laneShape.setId(Integer.valueOf(lane_id));
            laneShape.setName(prefix+lane_id);
            laneShape.setPriority(liveLane.getPriority());
            laneShape.setFromId(liveLane.getFrom().getName());
            laneShape.setToId(liveLane.getTo().getName());
            laneShape.setLength(liveLane.getLength());
            laneShape.setV(liveLane.getV());

            try {
                br = new BufferedReader(new FileReader(new File(filename)));
                /**
                 *  存储每一个文件的点云集合
                 */
                List<LidarPoint> allLidarPoint = new ArrayList<>();
                while ((line = br.readLine()) != null) {
                    String[] lineSplit = line.trim().split(",");
                    /**
                     * 写入一行标准的点云数据
                     */
                    LidarPoint lidarPoint = new LidarPoint(lineSplit[0], lineSplit[1], lineSplit[2],
                                                           lineSplit[3], lineSplit[4], lineSplit[5], lineSplit[6]);
                    allLidarPoint.add(lidarPoint);
                }
                laneShape.setPath(allLidarPoint);
                laneShapes.add(laneShape);
                /**
                 *  关闭流同时刷新缓冲区
                 */
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return laneShapes;
    }


    /**
     * 根据Laneshape文件存放的文件夹目录加载Lane信息
     * 不再使用xml构建语义地图，现在直接加载csv文件
     * @param parkName
     * @param dir
     */
    private void loadLaneShapes(String parkName, String dir)  {

        List<String> filesnames ;
        filesnames = getAllFiles(dir);
        List<LaneShape> laneShapes = readCSVFiles(parkName, filesnames);
        for (LaneShape laneShape : laneShapes) {
            laneShape.setParkName(parkName);
            shapeMapService.save(laneShape);
        }
    }

    /**读取站点配置*/
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

    /**
     *  读取直线路段配置
     */
    private void loadLanes(Document document, LiveMap liveMap) {
        Element root = document.getRootElement();
        Element lanes = root.element("lanes");
        Iterator<Element> iterator = lanes.elementIterator();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            LiveLane lane = new LiveLane();
            lane.setId(Integer.valueOf(element.attributeValue("id")));
            lane.setName(element.attributeValue("name"));

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
    public LiveMap create(String parkName, String mapUrl, String shapeUrl) {
        LiveMap liveMap = new LiveMap();
        try {
            Document document = parse(mapUrl);
            liveMap.setParkName(parkName);
            loadNode(document, liveMap);
            loadLanes(document, liveMap);
            loadLaneShapes(parkName, shapeUrl);
            return liveMap;
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
