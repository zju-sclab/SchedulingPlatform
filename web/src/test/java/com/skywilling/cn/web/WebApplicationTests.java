package com.skywilling.cn.web;

import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.connection.infrastructure.client.ClientService;
import com.skywilling.cn.connection.model.Packet;
import com.skywilling.cn.connection.service.RequestDispatcher;
import com.skywilling.cn.connection.service.RequestSender;
import com.skywilling.cn.livemap.core.StaticMapFactory;
import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.livemap.model.Park;
import com.skywilling.cn.livemap.model.Point;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.ParkService;
import com.skywilling.cn.livemap.service.impl.ShapeServiceImpl;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.model.CarDynamic;
import com.skywilling.cn.manager.car.model.Orientation;
import com.skywilling.cn.manager.car.model.Pose;
import com.skywilling.cn.manager.car.repository.AutoCarInfoGeoAccessor;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.car.service.CarInfoService;
import com.skywilling.cn.manager.car.service.impl.CarDynamicServiceImpl;
import com.skywilling.cn.scheduler.model.Route;
import com.skywilling.cn.scheduler.service.CrossNodeListen;
import com.skywilling.cn.scheduler.service.NodeLockService;
import com.skywilling.cn.scheduler.service.RouteService;
import com.skywilling.cn.scheduler.service.TripService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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


    /**
     * 数据库测试
     */
    @Test
    public void mysqlTest() {
        String vin="00000000112417008";
        CarDynamic query = carDynamicService.query(vin);
        System.out.println();
    }

    /**
     * 查找园区测试
     */
    @Test
    public void parkQuerytest(){
        parkService.queryByName("sss");
    }

    /**
     * 新建园区测试
     */
    @Test
    public void parkAdd(){
        Park park = new Park();
        park.setMapFileUrl("D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map\\map.xml");
        park.setShapeFileUrl("D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map\\shape\\");
        park.setImgUrl("D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map");
        park.setName("yuquanxiaoqu");
        parkService.save(park);
    }

    /**
     * 修改园区信息
     */
    @Test
    public void updatepark(){
        Park park = parkService.queryByName("yuquanxiaoqu");
        park.setMapFileUrl("D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map\\map.xml");
        park.setShapeFileUrl("D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map\\shape\\");
        park.setImgUrl("D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map");
        parkService.update(park);
    }

    @Test
    public void mapAddTest(){
       /* LiveMap liveMap = new LiveMap();
        liveMap.setParkName("xuanzhou");
        mapService.addMap(liveMap);*/
    }

    /**
     * 解析Map和Shape测试
     */
    @Test
    public void mapTest(){
        String parkName = "yuquanxiaoqu";
        Park park = parkService.queryByName(parkName);
        if (park != null&& park.getMapFileUrl() != null && park.getShapeFileUrl() != null) {
            LiveMap m = mapService.getMap(parkName);
            //创建Live Map
            LiveMap liveMap = new StaticMapFactory().create(parkName, park.getMapFileUrl());
            //创建map
            mapService.addMap(liveMap);
            //创建shape Map
            shapeService.create(parkName);
        }

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
        pose.setOrientation(new Orientation());
        pose.setPoint(new Point());
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
        //List<AutonomousCarInfo> mongocars2 = autoCarInfoGeoAccessor.findByDist(new GeoJsonPoint(0,0),0.01);
        //System.out.println(mongocars2);
    }
    @Test
    public void MongoRemoveAndFindAllTest(){

        autoCarInfoGeoAccessor.remove("lane", "lane_1");
        List<AutonomousCarInfo> all = autoCarInfoGeoAccessor.getAll();
        System.out.println(all);
    }

    /**
     * 全局规划测试
     */
    @Test
    public void routeServiceTest(){
        String from = "caolou";
        String to = "shengyi";
        String parkName = "yuquanxiaoqu";
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

}
