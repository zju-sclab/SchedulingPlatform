package com.skywilling.cn.web;

import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.common.model.Pose;
import com.skywilling.cn.common.model.RoutePoint;
import com.skywilling.cn.common.model.Triple;
import com.skywilling.cn.connection.infrastructure.client.ClientService;
import com.skywilling.cn.connection.model.Packet;
import com.skywilling.cn.connection.service.RequestDispatcher;
import com.skywilling.cn.connection.service.RequestSender;
import com.skywilling.cn.livemap.model.*;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.ParkService;
import com.skywilling.cn.livemap.service.impl.ShapeServiceImpl;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.model.CarDynamic;
import com.skywilling.cn.manager.car.repository.AutoCarInfoGeoAccessor;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.car.service.CarInfoService;
import com.skywilling.cn.manager.car.service.impl.CarDynamicServiceImpl;
import com.skywilling.cn.scheduler.core.trajectoryalgorithm.GlobalTrajPlanner;
import com.skywilling.cn.scheduler.model.Route;
import com.skywilling.cn.scheduler.model.StaticStation;
import com.skywilling.cn.scheduler.model.Trip;
import com.skywilling.cn.scheduler.repository.impl.TripAccessorImpl;
import com.skywilling.cn.scheduler.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

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

    /**
     * Netty
     */
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
     * 查找园区测试
     */
    @Test
    public void parkQuerytest(){
        Park park1 = parkService.queryByName("sss");
        Park park2 = parkService.query(14);
        System.out.println(park2);
    }

    /**
     * 新建园区测试
     */
    @Test
    public void parkAdd(){
        Park park = new Park();
        park.setMapFileUrl("D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map\\map2.xml");
        park.setShapeFileUrl("D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map\\shape\\");
        park.setImgUrl("D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map");
        park.setName("yuquanxiaoqu3");
        parkService.save(park);
    }

    /**
     * 修改园区信息
     */
    @Test
    public void updatepark(){
        Park park = parkService.queryByName("yuquanxiaoqu3");
        park.setMapFileUrl("file:\\"+ "D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map\\map2.xml");
        park.setShapeFileUrl("D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map\\shape\\");
        park.setImgUrl("D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map");
        System.out.println(park.getId());
        parkService.update(park);
    }

    @Test
    public void mapAddTest(){
        List<LiveMap> liveMap = mapService.getAllMaps();
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

    @Test
    public void mapPrintTest(){
        String parkName = "yuquanxiaoqu";
        LiveMap liveMap = mapService.getMap(parkName);
        System.out.println(liveMap);
    }

    @Test
    public void MongoInsertAndSaveTest(){
        AutonomousCarInfo autonomousCarInfo = new AutonomousCarInfo();
        autonomousCarInfo.setStation("zetong");
        autonomousCarInfo.setVin("00012345");
        autonomousCarInfo.setVelocity(5);
        autonomousCarInfo.setLane("lane_1");
        autonomousCarInfo.setStation("zetong");
        Pose pose = new Pose();
        //pose.setOrientation(new Orientation());
        //pose.setPoint(new Point());
        autonomousCarInfo.setPose(pose);
        //autonomousCarInfo.setPosition(new GeoJsonPoint(new Point(pose.getPoint().getX(), pose.getPoint().getY())));
        autonomousCarInfo.setTripId("00112345");
        autonomousCarInfo.setTaskId("0x122356");
        autonomousCarInfo.setGear(2);
        autonomousCarInfo.setState(-1);
        //测试redis、MongoDB的插入，一起插入
        autoCarInfoService.save(autonomousCarInfo);
    }
    @Test
    public void MongofindByLaneTest(){

        //测试redis的查找
        AutonomousCarInfo a = autoCarInfoService.get("00012345");
        System.out.println(a);
        //测试mongodb的查找
        List<AutonomousCarInfo> mongocars = autoCarInfoGeoAccessor.getByLane("lane_1");
        System.out.println(mongocars);
        List<AutonomousCarInfo> mongocars2 = autoCarInfoGeoAccessor.findByDist(new GeoJsonPoint(0,0),0.01);
        //System.out.println(mongocars2);
    }
    @Test
    public void MongoRemoveAndFindAllTest(){

        //autoCarInfoGeoAccessor.remove("lane", "lane_1");
        List<AutonomousCarInfo> all = autoCarInfoGeoAccessor.getAll();
        autoCarInfoGeoAccessor.remove("vin","00012345");
        for(AutonomousCarInfo autonomousCarInfo : all){
            autonomousCarInfo.setFromLane("lane_0");
            autonomousCarInfo.setLane("cross_0");
            autoCarInfoService.save(autonomousCarInfo);
        }
        AutonomousCarInfo a = autoCarInfoService.get("00000000112417002");
        List<AutonomousCarInfo> b = autoCarInfoGeoAccessor.getAll();
    }
    @Test
    public void TripSaveTest(){
        Trip trip = new Trip();
        trip.setStartStation(new LiveStation());
        trip.setEndStation(new LiveStation());
        tripAccessor.save(trip);
    }
    @Test
    public void UtilTest(){
        String[] str = new String[]{"2","2,3,4","2,3"};
        List<String[]> res_split = new ArrayList<>();
        for(String s :  str){
            res_split.add(s.split(","));
        }
        for(int i = 0; i < res_split.size(); i++){
            for(int j = 0; j < res_split.get(i).length; j++){
                System.out.print(res_split.get(i)[j] + " ");
            }
            System.out.println("end");
        }
    }
    /**
     * 全局规划测试
     */
    @Test
    public void routeServiceTest(){
        String from = "caolou";
        String to = "shengyi";
        String parkName = "yuquanxiaoqu2";
        /** input : yuquanxiaoqu 2
         * output: lane_3 3 caolou shengyi 1 [lane_3] [lane_3] */
        Route route = routeService.navigate(parkName, from, to);
        for(LiveLane liveLane: route.getLiveLanes()){
            System.out.println(liveLane.getName() + " "+ liveLane.getId()+ " " + liveLane.getFrom().getName()
                    + " " +
                   liveLane.getTo().getName() + " " + liveLane.getPriority() + " "+ liveLane.getFrom().getLanesStart()
                    + " " + liveLane.getTo().getLanesEnd());
        }

    }

    @Test
    public void NettyTest(){
        CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();
        Packet.Builder builder = new Packet.Builder();
        JSONObject s = new JSONObject();
        //18位Id
        Packet packet = builder.buildRequest("S000000000000000095", TypeField.valueOf(0x21)).buildBody(s)
                .build();
        CompletableFuture<Packet> respFuture = clientService.sendRequest(packet);
        if (respFuture == null) {
            throw new NullPointerException();
        }
        respFuture.whenComplete(new RequestSender.PacketConsumer(resultFuture));
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
        StaticStation start = new StaticStation();
        StaticStation target = new StaticStation();
        start.setPoint(1.16,-0.29,0,0,0,0,0);
        target.setPoint(-37,101,0,0,0,0,0);
        Triple<List<String>, List<Double>, List<RoutePoint>> res = ser.createTrajectory(start,target);
        System.out.println(res.first);
        System.out.println(res.second);
        System.out.println(res.third);
    }

    @Test
    public void ScheduleTest(){
        mapService.upDateReqLockMap();
        scheduleService.checkAllClient();
    }

}
