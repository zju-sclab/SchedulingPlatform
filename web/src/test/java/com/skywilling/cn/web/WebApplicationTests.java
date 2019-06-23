package com.skywilling.cn.web;

import com.skywilling.cn.common.model.Pose;
import com.skywilling.cn.common.model.RoutePoint;
import com.skywilling.cn.common.model.Triple;
import com.skywilling.cn.connection.infrastructure.client.ClientService;
import com.skywilling.cn.connection.service.RequestDispatcher;
import com.skywilling.cn.connection.service.RequestSender;
import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.livemap.model.Park;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.ParkService;
import com.skywilling.cn.livemap.service.impl.ShapeServiceImpl;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.model.CarDynamic;
import com.skywilling.cn.manager.car.model.SiteExt;
import com.skywilling.cn.manager.car.repository.AutoCarInfoGeoAccessor;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.car.service.CarInfoService;
import com.skywilling.cn.manager.car.service.impl.CarDynamicServiceImpl;
import com.skywilling.cn.manager.car.service.impl.SiteExtDao;
import com.skywilling.cn.scheduler.core.trajectoryalgorithm.GlobalTrajPlanner;
import com.skywilling.cn.scheduler.model.Route;
import com.skywilling.cn.scheduler.model.StaticStation;
import com.skywilling.cn.scheduler.repository.impl.TripAccessorImpl;
import com.skywilling.cn.scheduler.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/* import org.springframework.data.geo.Point; */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WebApplication.class})
public class WebApplicationTests {
    /**
     * car info  动态存入内存 静态存入Mysql
     */
    @Autowired
    CarDynamicServiceImpl carDynamicService;
    @Autowired
    AutoCarInfoService autoCarInfoService;
    @Autowired
    CarInfoService carInfoService;
    /**
     * 地图测试，园区信息存入数据库，地图存入内存
     */
    @Autowired
    ParkService parkService;
    @Autowired
    MapService mapService;
    @Autowired
    ShapeServiceImpl shapeService;
    @Autowired
    GlobalTrajPlanner ser;
    @Autowired
    RequestSender requestSender;
    @Autowired
    RequestDispatcher requestDispatcher;

    /**
     * 调度测试，规划路径，路口调度
     */
    @Autowired
    private RouteService routeService;
    @Autowired
    private CrossNodeListen crossNodeListen;
    @Autowired
    TripService tripService;
    @Autowired
    NodeLockService nodeLockService;
    @Autowired
    private SiteExtDao siteExtDao;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    AutoCarInfoGeoAccessor autoCarInfoGeoAccessor;
    @Autowired
    ClientService clientService;
    @Autowired
    TripAccessorImpl tripAccessor;
    @Autowired
    ScheduleService scheduleService;

    /**
     * 数据库测试
     */
    @Test
    public void mysqlTest() {
        String vin="00000000112417008";
        CarDynamic query = carDynamicService.query(vin);
        System.out.println(query);
    }

    /**
     * 新建园区测试
     */
    @Test
    public void parkAdd(){
        Park park = new Park();
        park.setMapFileUrl("file:\\"+"D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map\\map2.xml");
        park.setShapeFileUrl("D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map\\shape\\");
        park.setImgUrl("D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map");
        park.setName("yuquanxiaoqu3");
        if(parkService.queryByName("yuquanxiaoqu3")==null) parkService.save(park);
    }

    /**
     * 修改园区信息
     */
    @Test
    public void updatepark(){
        Park park = parkService.queryByName("yuquanxiaoqu3");
        park.setMapFileUrl("file:\\"+ System.getProperty("user.dir")+"\\src\\main\\resources\\config\\dev\\map2.xml");
        park.setShapeFileUrl("D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map\\shape\\");
        park.setImgUrl("D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map");
        System.out.println(park.getId());
        parkService.update(park);
        Park park2 = parkService.query(park.getId());
        System.out.println(park2);
    }


    /**
     * 解析Map和Shape测试
     */
    @Test
    public void mapTest(){
        String parkName = "yuquanxiaoqu3";
        Park park = parkService.queryByName(parkName);
        LiveMap map = mapService.getMap(park.getName());
        System.out.println(map);
    }


    //@Test
    public void TestGeoJsonPoints(){
        GeoJsonPoint geoJsonPoint1 = new GeoJsonPoint(new Point(113.330908,23.155678));
        SiteExt siteExt1 = new SiteExt("A",geoJsonPoint1);
        GeoJsonPoint geoJsonPoint2 = new GeoJsonPoint(new Point(113.33831,23.137335));
        SiteExt siteExt2 = new SiteExt("B",geoJsonPoint2);

        siteExtDao.insert(siteExt1,"SiteExt");
        siteExtDao.insert(siteExt2,"SiteExt");
    }
    @Test
    public void TestGeoPolgy(){
        Point p1 = new Point(113.314882,23.163055);
        Point p2 = new Point(113.355845,23.167042);
        Point p3 = new Point(113.370289,23.149564);
        Point p4 = new Point(113.356779,23.129758);
        Point p5 = new Point(113.338238,23.13913);
        Point p6 = new Point(113.330979,23.124706);
        Point p7 = new Point(113.313588,23.140858);
        Point p8 = new Point(113.323865,23.158204);
        Point p9 = new Point(113.314882,23.163055);
        List<Point> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        list.add(p5);
        list.add(p6);
        list.add(p7);
        list.add(p8);
        list.add(p9);
       //用9个点围成一个区域，首尾两个点p1和p9要相同，才能构成一个区域
        GeoJsonPolygon geoJsonPolygon = new GeoJsonPolygon(list);
       //传入区域和数据库表名
        List<SiteExt> pointInPolygon = siteExtDao.findPointInPolygon(geoJsonPolygon,"SiteExt");
        pointInPolygon.forEach(
                p -> {
                    System.out.println(p.getLocation());
                }
        );
        SiteExt near = siteExtDao.nearVehicle(new GeoJsonPoint(113.330907,23.155675));
        System.out.println(near);
        List<SiteExt> res = siteExtDao.findByDist(new GeoJsonPoint(new Point(113.33,23.14)),2);
        System.out.println(res);
    }


    @Test
    public void MongoRemoveAndFindAllTest(){
        //autoCarInfoGeoAccessor.remove("lane", "lane_1");
        List<AutonomousCarInfo> all = autoCarInfoGeoAccessor.getAll();
        System.out.println(all);
        autoCarInfoGeoAccessor.remove("vin","00012345");
        autoCarInfoGeoAccessor.remove("vin","00000000112417002");
        List<String> cars = new LinkedList<>();
        cars.add("00000000112417002");
        for(String vin :cars){
            AutonomousCarInfo car = new AutonomousCarInfo(vin);
            car.setVin(vin);
            car.setPose(new Pose());
            car.setFromLane("lane_0");
            car.setLane("cross_0");
            autoCarInfoService.save(car);
        }
        AutonomousCarInfo a = autoCarInfoService.get("00000000112417002");
        System.out.println(a);
        List<AutonomousCarInfo> b = autoCarInfoGeoAccessor.getAll();
        System.out.println(b);
    }

    /**全局规划测试*/
    public void routeServiceTest(){
        String from = "caolou";String to = "shengyi";
        String parkName = "yuquanxiaoqu2";
        /** input : yuquanxiaoqu 2  output: lane_3 3 caolou shengyi 1 [lane_3] [lane_3] */
        Route route = routeService.navigate(parkName, from, to);
        for(LiveLane liveLane: route.getLiveLanes()){
            System.out.println("lane_name: " + liveLane.getName() + " lane_id: "+ liveLane.getId()+ " from: " +
                    liveLane.getFrom().getName() + " to: " + liveLane.getTo().getName() + " weight: " + liveLane.getPriority() + " ");
        }
    }

    @Test
    public void FileIOTestinDoc(){
        //String file_dir = "doc/Map/map.xml";
        try {
            File path = new File(ResourceUtils.getURL("classpath:").getPath());
            if(!path.exists()) path = new File("");
            System.out.println("class path:"+path.getAbsolutePath());

            File upload = new File(path.getAbsoluteFile(),"doc/Map/lanes");
            if(!upload.exists()) upload.mkdirs();
            System.out.println("upload ur:"+upload.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void GlobalPlanTest(){
        StaticStation start = new StaticStation(1.16,-0.29,0,0,0,0,0);
        StaticStation target = new StaticStation(-37,101,0,0,0,0,0);;
        Triple<List<String>, List<Double>, List<RoutePoint>> res = ser.createTrajectory(start,target);
        System.out.println(res.first);
        System.out.println(res.second);
        System.out.println(res.third);
    }

}
