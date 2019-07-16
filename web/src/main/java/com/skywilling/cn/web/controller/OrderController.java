package com.skywilling.cn.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.common.enums.ResultType;
import com.skywilling.cn.common.model.BasicResponse;
import com.skywilling.cn.common.model.Order;
import com.skywilling.cn.common.model.Plan;

import com.skywilling.cn.livemap.model.LivePlan;
import com.skywilling.cn.livemap.service.PlanService;
import com.skywilling.cn.livemap.util.CacheManager;
import com.skywilling.cn.scheduler.service.TripService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javafx.beans.binding.ObjectExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    PlanService planService;
    @Autowired
    TripService tripService;
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
