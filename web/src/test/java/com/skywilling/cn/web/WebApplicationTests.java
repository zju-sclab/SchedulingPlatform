package com.skywilling.cn.web;

import com.github.pagehelper.PageInfo;
import com.skywilling.cn.livemap.core.StaticMapFactory;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.livemap.model.LiveStation;
import com.skywilling.cn.livemap.model.Park;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.ParkService;
import com.skywilling.cn.livemap.service.impl.ShapeServiceImpl;
import com.skywilling.cn.manager.car.model.CarDynamic;
import com.skywilling.cn.manager.car.service.impl.CarDynamicServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WebApplication.class})
public class WebApplicationTests {

    @Autowired
    CarDynamicServiceImpl carDynamicService;
    @Autowired
    ParkService parkService;
    @Autowired
    MapService mapService;
    @Autowired
    ShapeServiceImpl shapeService;

    @Test
    public void mysqlTest() {
        String vin="00000000112417008";
        CarDynamic query = carDynamicService.query(vin);
        System.out.println();
    }

    @Test
    public void parktest(){
        parkService.queryByName("sss");
    }


    @Test
    public void parkAdd(){
        Park park = new Park();
        park.setMapFileUrl("D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map\\map.xml");
        park.setShapeFileUrl("D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map\\shape\\");
        park.setImgUrl("D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map");
        park.setName("yuquanxiaoqu");
        parkService.save(park);
    }

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

    @Test
    public void mapTest(){
        String parkName = "yuquanxiaoqu";
        Park park = parkService.queryByName(parkName);
        if (park != null&& park.getMapFileUrl() != null && park.getShapeFileUrl() != null) {
            LiveMap m = mapService.getMap(parkName);
            //读取本地文件
            LiveMap liveMap = new StaticMapFactory().create(parkName, park.getMapFileUrl());
            mapService.addMap(liveMap);
            //读取本地文件夹
            shapeService.create(parkName);
        }

    }

}
