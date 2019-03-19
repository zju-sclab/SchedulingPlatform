package com.skywilling.cn.web;

import com.github.pagehelper.PageInfo;
import com.skywilling.cn.livemap.service.ParkService;
import com.skywilling.cn.manager.car.model.CarDynamic;
import com.skywilling.cn.manager.car.service.impl.CarDynamicServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WebApplication.class})
public class WebApplicationTests {

    @Autowired
    CarDynamicServiceImpl carDynamicService;
    @Autowired
    ParkService parkService;

    @Test
    public void mysqlTest() {
        String vin="00000000112417008";
        CarDynamic query = carDynamicService.query(vin);
        System.out.println();
    }

//    @Test
    public void parktest(){
        parkService.queryByName("sss");
    }

}
