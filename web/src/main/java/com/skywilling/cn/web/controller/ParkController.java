package com.skywilling.cn.web.controller;


import com.github.pagehelper.PageInfo;
import com.skywilling.cn.common.enums.ResultType;
import com.skywilling.cn.common.model.BasicResponse;
import com.skywilling.cn.common.model.Coordinate;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.livemap.model.LiveStation;
import com.skywilling.cn.livemap.model.Park;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.ParkService;
import com.skywilling.cn.livemap.service.StationService;
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
@RequestMapping(value = "/api/v1")
public class ParkController {

    @Autowired
    private ParkService parkService;

    @Autowired
    private MapService mapService;

    @Autowired
    private CarDynamicService carDynamicService;

    @Autowired
    private RouteService routeService;

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
                //清理cache里的旧缓存值
                //regionService.clearCache(park.getName());
                return BasicResponse.buildResponse(ResultType.SUCCESS, null);
            } else {
                return BasicResponse.buildResponse(ResultType.SUCCESS, "Map或者Shape文件不存在!");
            }
        }

    }


    /**
     * 添加园区
     */
    @RequestMapping(value = "/park/add", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BasicResponse addPark(@RequestParam("parkId") int parkId, @RequestParam("mapUrl") String mapUrl,
                                 @RequestParam("shapeUrl") String shapeUrl, HttpServletRequest request) {
        Park park = parkService.query(parkId);
        if (park != null) {
            return BasicResponse.buildResponse(ResultType.FAILED, "该园区已经存在!");
        } else {
            park = new Park();
            park.setId(parkId);
            File mapFile = new File(mapUrl);
            File shapeFile = new File(shapeUrl);
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
     * 查询全部园区信息
     */
    @RequestMapping(value = "/parks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public BasicResponse getWebParks(@RequestParam("page") int page,
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

        List<LiveStation> stations = new ArrayList<>();
        stations.addAll(mapService.getMap(park.getName()).getStationMap().values());

        return BasicResponse.buildResponse(ResultType.SUCCESS, stations);
    }

    /**
     *  根据园区名和站点名查找站点信息的地图接口
     */
    @RequestMapping(value = "/park/{parkId}/station/{stationName}", method = RequestMethod.GET)
    public BasicResponse getStation(@PathVariable("parkId") int parkId,
                                    @PathVariable("stationName") String stationName) {

        Park park = parkService.query(parkId);
        if (park == null) {
            return BasicResponse.buildResponse(ResultType.FAILED, "指定园区不存在!");
        }
       // Node node = regionService.getNodeById(park.getName(), stationId);
        LiveStation liveStation = mapService.getMap(park.getName()).getStationMap().get(stationName);
        return BasicResponse.buildResponse(ResultType.SUCCESS, liveStation);
    }
    @RequestMapping(value = "/park/{parkId}/car", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BasicResponse addCar(@PathVariable("parkId") int parkId, @RequestBody String vin){
        Boolean result  = carDynamicService.bindPark(parkId, vin);
        if(result){

            return BasicResponse.buildResponse(ResultType.SUCCESS, "添加成功!");
        }
        return BasicResponse.buildResponse(ResultType.FAILED, "添加失败!");
    }

    /**
     * 园区添加车辆，即将车辆与园区进行绑定
     */
    @RequestMapping(value = "/park/{parkId}/cars", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BasicResponse addCars(@PathVariable("parkId") int parkId,
                                 @RequestBody List<String> vins) {
        boolean b = carDynamicService.bindPark(parkId, vins);
        if (b) {
            return BasicResponse.buildResponse(ResultType.SUCCESS, "添加成功!");
        }
        return BasicResponse.buildResponse(ResultType.FAILED, "添加失败!");
    }

    /**
     * 分页查询与园区绑定的车辆
     */
    @RequestMapping(value = "/park/{parkId}/cars", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public BasicResponse getAllCarsByPark(@PathVariable("parkId") int parkId,
                                          @RequestParam("page") int page,
                                          @RequestParam("size") int size) {

        PageInfo<CarDynamic> pageInfo = carDynamicService.queryByPark(parkId, page, size);
        PageView pageView = ViewBuilder.build(pageInfo);
        return BasicResponse.buildResponse(ResultType.SUCCESS, pageView);
    }

    /**
     * 将车辆与园区解绑
     */
    @RequestMapping(value = "/park/{parkId}/cars/unbind", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BasicResponse unbind(@PathVariable("parkId") int parkId,
                                @RequestBody List<String> vins) {
        try {
            boolean result = carDynamicService.unbindPark(parkId, vins);
            if(result)
                return BasicResponse.buildResponse(ResultType.SUCCESS,"解绑成功!");
            else
                return BasicResponse.buildResponse(ResultType.FAILED, "解绑失败!");
        } catch (Exception e) {
            return BasicResponse.buildResponse(ResultType.FAILED, e.getMessage());
        }
    }

    @RequestMapping(value = "/park/{parkId}/cars/free", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BasicResponse getFreeCarsByPark(@PathVariable("parkId") int parkId,
                                           @RequestParam("page") int page,
                                           @RequestParam("size") int size) {
        Park park = parkService.query(parkId);
        if (park == null) {
            return BasicResponse.buildResponse(ResultType.FAILED, "该园区不存在!");
        }
        String parkName = park.getName();
        LiveMap liveMap = mapService.getMap(parkName);

        PageInfo<CarDynamic> pageInfo = carDynamicService.queryFreeByPark(parkId, page, size);
        List<CarView> carViews = pageInfo.getList().stream().map(carDynamic -> {
            CarView carView = new CarView();
            carView.setVin(carDynamic.getVin());
            carView.setParkName(park.getName());
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
