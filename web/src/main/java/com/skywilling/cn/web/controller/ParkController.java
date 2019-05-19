package com.skywilling.cn.web.controller;


import com.github.pagehelper.PageInfo;
import com.skywilling.cn.common.enums.ResultType;
import com.skywilling.cn.common.model.BasicResponse;
import com.skywilling.cn.common.model.Coordinate;
import com.skywilling.cn.livemap.core.StaticMapFactory;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.livemap.model.LiveStation;
import com.skywilling.cn.livemap.model.Node;
import com.skywilling.cn.livemap.model.Park;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.ParkService;
import com.skywilling.cn.livemap.service.ShapeMapService;
import com.skywilling.cn.livemap.service.StationService;
import com.skywilling.cn.livemap.service.impl.ShapeServiceImpl;
import com.skywilling.cn.manager.car.enumeration.UseStatus;
import com.skywilling.cn.manager.car.model.CarDynamic;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import com.skywilling.cn.scheduler.service.RouteService;
import com.skywilling.cn.web.model.view.CarView;
import com.skywilling.cn.web.model.view.PageView;
import com.skywilling.cn.web.utils.ViewBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/api/v2")
public class ParkController {

    @Autowired
    private ParkService parkService;

    @Autowired
    private MapService mapService;

    @Autowired
    private CarDynamicService carDynamicService;

    @Autowired
    private ShapeMapService shapeService;

    @Autowired
    private StationService stationService;



/*  @RequestMapping(value = "/park/updateBasicInfo", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public BasicResponse updateRegion(ParkBasicParam basicParam) {
    try {
      parkService.updateBasic(basicParam);
      return BasicResponse.buildResponse(ResultType.SUCCESS, null);
    }catch (Exception e) {
      return BasicResponse.buildResponse(ResultType.FAILED, e.getClass());
    }
  }*/

    /**
     * 更新园区的地图信息
     */
    @RequestMapping(value = "/park/updateRegion", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BasicResponse updateRegion(@RequestParam("parkId") int parkId, @RequestParam("mapUrl") String mapUrl,
                                      @RequestParam("shapeUrl") String shapeUrl, HttpServletRequest request) {
        Park park = parkService.query(parkId);
        if (park == null) {
            return BasicResponse.buildResponse(ResultType.FAILED, "园区不存在!");
        } else {
            File mapFile = new File(mapUrl);
            File shapeFile = new File(shapeUrl);
            if (mapFile.exists() && mapFile.isFile() && shapeFile.exists() && shapeFile.isDirectory()) {
                parkService.updateRegion(parkId, mapUrl, shapeUrl);
                String parkName = park.getName();
                //创建Live Map
                LiveMap liveMap = new StaticMapFactory().create(parkName, park.getMapFileUrl());
                //创建map
                mapService.addMap(liveMap);
                //创建shape Map
                shapeService.create(parkName);
                return BasicResponse.buildResponse(ResultType.SUCCESS, null);
            } else {
                return BasicResponse.buildResponse(ResultType.SUCCESS, "Map或者Shape文件不存在!");
            }
        }

    }


    /**
     * 根据名字和地图路径来添加园区
     */
    @RequestMapping(value = "/park/add", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BasicResponse addPark(@RequestParam("parkName")String parkName, @RequestParam("mapUrl") String mapUrl,
                                 @RequestParam("shapeUrl") String shapeUrl, HttpServletRequest request) {

        Park park = parkService.queryByName(parkName);
        if (park != null) {
            return BasicResponse.buildResponse(ResultType.FAILED, "该园区已经存在!");
        } else {
            park = new Park();
            park.setName(parkName);
            //park.setId(parkId); 自增主键
            File mapFile = new File(mapUrl);
            File shapeFile = new File(shapeUrl);
            park.setImgUrl(mapUrl);
            if (mapFile.exists() && mapFile.isFile() && shapeFile.exists() && shapeFile.isDirectory()) {
                park.setMapFileUrl(mapUrl);
                park.setShapeFileUrl(shapeUrl);
                parkService.save(park);

                return BasicResponse.buildResponse(ResultType.SUCCESS, "创建园区成功!");
            } else {
                return BasicResponse.buildResponse(ResultType.SUCCESS, "创建园区的map或者shape文件不存在!");
            }
        }
    }
    /**根据id来删除园区和地图*/
    @RequestMapping(value = "/park/{parkId}/delete", method = RequestMethod.POST)
    public BasicResponse delete(@PathVariable("parkId") int parkId) {

        try {
            int numberByPark = carDynamicService.getTotalNumberByPark(parkId);
            if (numberByPark > 0) {
                return BasicResponse.buildResponse(ResultType.FAILED, "该园区尚有绑定车辆,请先解绑车辆!");
            }
            else{
                parkService.delete(parkId);
                return BasicResponse.buildResponse(ResultType.SUCCESS, "删除园区成功!");
            }

        } catch (Exception e) {
            return BasicResponse.buildResponse(ResultType.FAILED, e.getMessage());
        }
    }

    /**
     * 根据Id查询园区信息
     */
    @RequestMapping(value = "/park/{parkId}", method = RequestMethod.GET)
    public BasicResponse getPark(@PathVariable("parkId") int parkId) {

        try {
            Park park = parkService.query(parkId);
            return BasicResponse.buildResponse(ResultType.SUCCESS, park);
        } catch (Exception e) {
            return BasicResponse.buildResponse(ResultType.FAILED, e.getMessage());
        }
    }
    /**
     * 根据Name查询园区信息
     */
    @RequestMapping(value = "/park/{parkName}", method = RequestMethod.GET)
    public BasicResponse getPark(@PathVariable("parkName") String parkName) {

        try {
            Park park = parkService.queryByName(parkName);
            return BasicResponse.buildResponse(ResultType.SUCCESS, park);
        } catch (Exception e) {
            return BasicResponse.buildResponse(ResultType.FAILED, e.getMessage());
        }
    }

    /**
     * 查询全部园区信息
     */
    @RequestMapping(value = "/parks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public BasicResponse getWebParks(@RequestParam("page") int page  ,
                                     @RequestParam("size") int size) {

        try {
            PageInfo<Park> parkPageInfo = parkService.query( page, size);
            PageView pageView = ViewBuilder.build(parkPageInfo);
            return BasicResponse.buildResponse(ResultType.SUCCESS, pageView);
        } catch (Exception e) {
            return BasicResponse.buildResponse(ResultType.FAILED, e.getMessage());
        }
    }

    /**
     * Park仅表示园区，Map表示园区的地图
     */
    @RequestMapping(value = "/park/{parkId}/stations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public BasicResponse getStations(@PathVariable("parkId") int parkId) {

        Park park = parkService.query(parkId);
        if (park == null) {
            return BasicResponse.buildResponse(ResultType.FAILED, "指定园区不存在");
        }
        //查询LiveMap的Node节点返回前端
        List<Node> stations = new ArrayList<>();
        stations.addAll(mapService.getMap(park.getName()).getNodeMap().values());

        return BasicResponse.buildResponse(ResultType.SUCCESS, stations);
    }

    /**
     *  根据园区名和站点名查找站点信息的地图接口
     */
    @RequestMapping(value = "/park/{parkId}/station/{stationName}", method = RequestMethod.GET)
    public BasicResponse getStation(@PathVariable("parkId") int parkId,@PathVariable("stationName")String stationName){

        Park park = parkService.query(parkId);
        if (park == null) {
            return BasicResponse.buildResponse(ResultType.FAILED, "指定园区不存在!");
        }
       // Node node = regionService.getNodeById(park.getName(), stationId);
        LiveStation liveStation = mapService.getMap(park.getName()).getStationMap().get(stationName);
        return BasicResponse.buildResponse(ResultType.SUCCESS, liveStation);
    }

    /** 绑定车辆到指定园区 */
    //@RequestBody用于注解到非x-www-form-urlencoded的前端输入,一般是json,xml类型
    //consumes参数,指定处理请求的 提交内容类型 （Content-Type），例如 application/json, text/html
    @RequestMapping(value = "/park/{parkId}/car", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BasicResponse addCar(@PathVariable("parkId") int parkId, @RequestParam("vin")String vin){

        Boolean result  = carDynamicService.bindPark(parkId, vin);
        if(result){

            return BasicResponse.buildResponse(ResultType.SUCCESS, "添加成功!");
        }
        return BasicResponse.buildResponse(ResultType.FAILED, "添加失败!");
    }

    /**
     * 园区批量添加车辆
     */
    @RequestMapping(value = "/park/{parkId}/cars", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BasicResponse addCars(@PathVariable("parkId") int parkId, @RequestBody List<String> vins) {

        boolean res = carDynamicService.bindPark(parkId, vins);
        if (res) {
            return BasicResponse.buildResponse(ResultType.SUCCESS, "添加成功!");
        }
        return BasicResponse.buildResponse(ResultType.FAILED, "添加失败!");
    }

    /**
     * 分页查询与园区绑定的车辆
     */
    //produces指定返回的内容类型 ，仅当request请求头中的(Accept)类型中包含该指定类型才返回
    @RequestMapping(value = "/park/{parkId}/carsByPage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public BasicResponse getAllCarsByPark(@PathVariable("parkId") int parkId,
                                          @RequestParam("page") int page,
                                          @RequestParam("size") int size) {

        PageInfo<CarDynamic> pageInfo = carDynamicService.queryByPark(parkId, page, size);
        PageView pageView = ViewBuilder.build(pageInfo);
        return BasicResponse.buildResponse(ResultType.SUCCESS, pageView);
    }
    //直接根据园区id查询
    @RequestMapping(value = "/park/{parkId}/cars", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public BasicResponse getAllCarsByPark(@PathVariable("parkId") int parkId) {

        PageInfo<CarDynamic> pageInfo = carDynamicService.queryByPark(parkId, 1, 10);
        PageView pageView = ViewBuilder.build(pageInfo);
        return BasicResponse.buildResponse(ResultType.SUCCESS, pageView);
    }

    /**
     * 将单个车辆与园区解绑
     */
    @RequestMapping(value = "/park/{parkId}/car/unbind", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BasicResponse unbind(@PathVariable("parkId") int parkId, @RequestParam("vin")String vin) {

        try {
            boolean re = carDynamicService.unbindPark(parkId, vin);
            if(re)
                return BasicResponse.buildResponse(ResultType.SUCCESS,"解绑成功!");
            else
                return BasicResponse.buildResponse(ResultType.FAILED, "解绑失败!");
        } catch (Exception e) {
            return BasicResponse.buildResponse(ResultType.FAILED, e.getMessage());
        }
    }
    /**
     * 批量解绑
     */
/*    @RequestMapping(value = "/park/{parkId}/cars/unbindAll", method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BasicResponse unbind(@PathVariable("parkId") int parkId, @RequestBody List<String> vins) {

        try {
            boolean result = carDynamicService.unbindPark(parkId, vins);
            if(result)
                return BasicResponse.buildResponse(ResultType.SUCCESS,"解绑成功!");
            else
                return BasicResponse.buildResponse(ResultType.FAILED, "解绑失败!");
        } catch (Exception e) {
            return BasicResponse.buildResponse(ResultType.FAILED, e.getMessage());
        }
    }*/
    /** 查询空闲的停止不动的车辆 */
    @RequestMapping(value = "/park/{parkId}/cars/free", method = RequestMethod.GET)
    public BasicResponse getFreeCarsByPark(@PathVariable("parkId") int parkId) {
        Park park = parkService.query(parkId);
        if (park == null) {
            return BasicResponse.buildResponse(ResultType.FAILED, "该园区不存在!");
        }
        String parkName = park.getName();
        PageInfo<CarDynamic> pageInfo = carDynamicService.queryFreeByPark(parkId, 1, 10);
        List<CarView> carViews = pageInfo.getList().stream().map(carDynamic -> {
            CarView carView = new CarView();
            carView.setVin(carDynamic.getVin());
            carView.setParkName(park.getName());
            //查询车辆当前停止的节点所在位置
            LiveStation s  = stationService.getStation(parkName, carDynamic.getStation());
            carView.setNode(s);
            carView.setState(UseStatus.FREE.getCode());
            carView.setHealthState(carDynamic.getBodyStatus());
            return carView;
        }).collect(Collectors.toList());
        PageView pageView = ViewBuilder.build(pageInfo);
        pageView.setList(carViews);
        return BasicResponse.buildResponse(ResultType.SUCCESS, pageView);
    }
}
