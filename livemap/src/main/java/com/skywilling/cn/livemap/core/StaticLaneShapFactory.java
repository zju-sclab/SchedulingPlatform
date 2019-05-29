package com.skywilling.cn.livemap.core;
import com.skywilling.cn.common.model.Orientation;
import com.skywilling.cn.common.model.Position;
import com.skywilling.cn.common.model.RoutePoint;
import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.livemap.factory.Factory;
import com.skywilling.cn.livemap.model.LaneShape;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.ShapeService;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
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
    ShapeService shapeService;
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
            if (tempList[i].isDirectory())
                files.add(tempList[i].toString());
        }
        return files;
    }

    /**
     * 读取所有的line和cross文件,返回路段数组
     */
    public List<LaneShape> readCSVFiles(String parkName, List<String> files) {

        List<LaneShape> laneShapes = new ArrayList<>();
        BufferedReader br;
        String line;
        for (String filename : files) {
            LaneShape laneShape = new LaneShape();
            //根据去除绝对路径后的文件名得到lane_id
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
                List<RoutePoint> allLidarPoint = new ArrayList<>();
                while ((line = br.readLine()) != null) {
                    String[] lineSplit = line.trim().split(",");
                    Position position = new Position(lineSplit[0], lineSplit[1], lineSplit[2]);
                    Orientation orientation = new Orientation(lineSplit[3], lineSplit[4], lineSplit[5], lineSplit[6]);
                    RoutePoint lidarPoint = new RoutePoint();
                    lidarPoint.setPosition(position);
                    lidarPoint.setOrientation(orientation);
                    allLidarPoint.add(lidarPoint);
                }
                laneShape.setPath(allLidarPoint);
                laneShapes.add(laneShape);
                //关闭流同时刷新缓冲区
                br.close();
            } catch (IOException e) {
                LOG.warn(e.getMessage());
            }
        }
        return laneShapes;
    }
    /**
     * 根据dir文件夹下存放的csv文件加载Lane信息
     */
    private void loadLaneShapes(String parkName, String dir)  {
        List<String> filesnames ;
        filesnames = getAllFiles(dir);
        List<LaneShape> laneShapes = readCSVFiles(parkName, filesnames);
        for (LaneShape laneShape : laneShapes) {
            laneShape.setParkName(parkName);
            shapeService.save(laneShape);
        }
    }
    /**
     * 构建路段的语义信息
     */
    @Override
    public Boolean create(String parkName, String shapeUrl) {
            //Document document = parse(Shapeurl);
            //String parkName2 = String.valueOf(document.getRootElement().attribute("name"));
            loadLaneShapes(parkName, shapeUrl);
            return true;
    }
}
