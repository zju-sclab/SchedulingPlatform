package com.skywilling.cn.monitor.listener;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.common.model.BasicCarResponse;
import com.skywilling.cn.common.model.GeoLocation;
import com.skywilling.cn.common.model.Position;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class GpsInfoListener extends BasicListener {
    private static final Logger LOG = LoggerFactory.getLogger(GpsInfoListener.class);
    @Autowired
    ListenerMap listenerMap;
    @Autowired
    CarDynamicService carDynamicService;

    @Override
    @PostConstruct
    public void init() {
        listenerMap.addListener(TypeField.GPS_INFO.getDesc(), this);
    }

    @Override
    public BasicCarResponse process(String vin, String body) {
        // process当中处理gps信息
        LOG.info("xxxxxxxxxxxxx into gps listener");
        GeoLocation geoLocation = JSONObject.parseObject(body, GeoLocation.class);

        Position pos = new Position();
        pos.setX(geoLocation.getLongitude());

        pos.setY(geoLocation.getLatitude());
        pos.setZ(geoLocation.getAltitude());
        LOG.info(pos.toString());
        carDynamicService.updateLocation(vin, pos);
        return new BasicCarResponse(0, new Object());
    }
}
