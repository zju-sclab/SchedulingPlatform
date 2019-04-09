package com.skywilling.cn.livemap.core;

import com.skywilling.cn.common.config.redis.RedisDao;
import com.skywilling.cn.common.model.LidarPoint;
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
    ShapeMapService shapeMapService;


    private Document parse(String path) throws DocumentException, MalformedURLException {
        URL url = new URL(path);
        SAXReader saxReader = new SAXReader();
        return saxReader.read(url);
    }

    /**
     * 获取path目录下的所有文件
     *
     * @param path
     * @return
     */
    public List<String> getAllFiles(String path) {
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
     *
     * @param files
     */
    public List<LaneShape> readALLFiles(List<String> files) {
        List<LaneShape> laneShapes = new ArrayList<>();
        BufferedReader br = null;
        String line;

        List<LidarPoint> allLidarPoint = new ArrayList<>();
        for (String filename : files) {
            LaneShape laneShape = new LaneShape();

            if (filename.startsWith("lane")) laneShape.setType("Lane");
            else laneShape.setType("Curve");

            try {
                br = new BufferedReader(new FileReader(new File(filename)));

                laneShape.setId(laneShape.getType() + br.readLine()); //Id
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



/*
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

    }*/

    /**
     * 根据Laneshape文件存放的文件夹目录加载Lane信息
     * 不再使用xml构建语义地图，现在直接加载csv文件
     * @param parkName
     * @param dir
     */
    private void loadLaneShapes(String parkName, String dir) {

/*        List<Node> lanes = document.selectNodes("//region/lane");
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
        }*/
        List<String> filesnames = getAllFiles(dir);
        List<LaneShape> laneShapes = readALLFiles(filesnames);
        for (LaneShape laneShape : laneShapes) {
            laneShape.setParkName(parkName);
            if (laneShapes != null && laneShapes.size() != 0) {
                shapeMapService.save(laneShape);
            }
        }
    }

    /**
     * 构建点云地图，加载点云数组到路段,丰富路段的语义信息
     */
    @Override
    public Boolean create(String mapUrl, String shapeDir) {
        try {
            Document document = parse(mapUrl);
            String parkName = String.valueOf(document.getRootElement().attribute("id"));
            loadLaneShapes(parkName,shapeDir);
        } catch (DocumentException e) {
            LOG.warn(String.format("create ShapeContainer documentException error, file: {}", mapUrl));
        } catch (MalformedURLException e) {
            LOG.warn(String.format("create ShapeContainer MalformedURLException error, url: %s", mapUrl));
        }
        return null;
    }
}
