package com.skywilling.cn.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.command.biz.AutoServiceBiz;
import com.skywilling.cn.common.enums.ResultType;
import com.skywilling.cn.common.model.BasicResponse;
import com.skywilling.cn.common.model.Order;
import com.skywilling.cn.common.model.Plan;

import com.skywilling.cn.common.model.StationOrder;
import com.skywilling.cn.livemap.model.LivePlan;
import com.skywilling.cn.livemap.service.PlanService;
import com.skywilling.cn.livemap.util.CacheManager;
import com.skywilling.cn.manager.trip.OrderManager;
import com.skywilling.cn.scheduler.service.TripService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ClassName OrderController
 * Author  Lin
 * Date 2019/6/10 11:24
 **/
@CrossOrigin
@Api(tags = "订单管理")
@RequestMapping(value = "/api/v2/order/")
@Controller
public class OrderController {
    //存储订单信息
    @Autowired
    OrderManager orderManager;
    @Autowired
    PlanService planService;
    @Autowired
    TripService tripService;
    @Autowired
    AutoServiceBiz autoServiceBiz;
    /**
     * 批量订单
     */
    @ApiOperation("批量订单")
    @RequestMapping(value="/addOrders", method = RequestMethod.POST)
    @ResponseBody
    public BasicResponse addOrders(@RequestParam String username, @RequestParam("startTime")String startTime,
                                   @RequestParam("outset")String source, @RequestParam("goal")String to,
                                   @RequestParam String parkName){
        String[] users = username.split(",");
        String[] startTimes = startTime.split(",");
        String[] outsets = source.split(",");
        String[] goals = to.split(",");
        Order[] orders = new Order[users.length];
        for(int i = 0; i < users.length; i++){
            String[] time = startTimes[i].split(":");
            Order order = new Order(users[i],time[0], time[1],outsets[i],goals[i],parkName);
            orders[i] = order;
        }
        List<Plan> plans = tripService.createPlanByOrders(orders);
        for(int i = 0; i < users.length; i++ ){
            planService.put(users[i],plans.get(i),parkName);
        }
        JSONObject resp = new JSONObject();
        resp.put("parkName",parkName);
        resp.put("plans",plans);
        return BasicResponse.buildResponse(ResultType.SUCCESS, resp);
    }

    @ApiOperation("添加订单")
    @RequestMapping(value="/add", method = RequestMethod.POST)
    @ResponseBody
    public BasicResponse addOrder(Order order, ModelMap map){
        Plan plan = tripService.createPlanByOrder(order);
        planService.put(order.getUsername(),plan,order.getParkName());
        JSONObject resp = new JSONObject();
        resp.put("parkName",order.getParkName());
        resp.put("plans",plan);
        return BasicResponse.buildResponse(ResultType.SUCCESS,resp);
    }

    @ApiOperation("构建新的调度任务")
    @RequestMapping(value="/station/add", method = RequestMethod.POST)
    @ResponseBody
    public BasicResponse addOrder(@RequestParam String vin, @RequestParam String stationName,
                                  @RequestParam double x, @RequestParam double y){
        //创建StationOrder
        //获取当前的时间
        LocalDate now = LocalDate.now();
        int year = now.get(ChronoField.YEAR);
        int month = now.get(ChronoField.MONTH_OF_YEAR);
        int day = now.get(ChronoField.DAY_OF_MONTH);
//        int hour = now.get(ChronoField.HOUR_OF_DAY);
//        int min = now.get(ChronoField.MINUTE_OF_HOUR);
//        int sec = now.get(ChronoField.SECOND_OF_MINUTE);
        String time = Integer.toString(year) + '-' + month + '-' + day;// + '-' + hour + '-' + min + '-' + sec;
        double z = 0.0, pitch = 0.0, roll = 0.0, yaw = 0.0;
        StationOrder stationOrder = new StationOrder(vin, stationName, time, x, y, z, pitch, roll, yaw);
        if(orderManager.putOrder(vin, stationOrder)){
            autoServiceBiz.sendStationInfo(vin, x, y, z, pitch, roll, yaw);
            return BasicResponse.buildResponse(ResultType.SUCCESS, new JSONObject());
        }else{
            return BasicResponse.buildResponse(ResultType.FAILED, new JSONObject());
        }

    }

//    @ApiOperation("查询所有的订单")
//    @RequestMapping(value="/queryAll", method = RequestMethod.GET)
//    @ResponseBody
//    public BasicResponse queryAllOrder(){
//
//    }

    //从已经登录的车辆列表里面取寻找最接近的车辆来构建任务,并且需要从构建好的station里面去寻找位姿
    /*@ApiOperation("构建网约任务")
    @RequestMapping(value = "/addStationOrder",method = RequestMethod.POST)
    @ResponseBody
    public BasicResponse addStationOrder(int stationId){

    }*/

    @ApiOperation("查询路径")
    @RequestMapping(value = "/plan/{parkName}", method = RequestMethod.GET)
    public String queryPlan(ModelMap map, @PathVariable("parkName") String parkName){
        LivePlan livePlan = planService.getLivePlan(parkName);
        Map<Object, CacheManager.CacheEntry> cacheEntryMap = livePlan.getCacheManager().getCacheEntryMap();
        List<Plan> plans = new ArrayList<>();
        for(Object key : cacheEntryMap.keySet()){
            plans.add((Plan) cacheEntryMap.get(key).getValue());
        }
        map.addAttribute("plans",plans);
        return "schedule_view";
    }
}
