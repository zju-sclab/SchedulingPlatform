package com.skywilling.cn.livemap.service.impl;

import com.skywilling.cn.livemap.db.mapper.ParkMapper;
import com.skywilling.cn.livemap.model.Park;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

public class ParkServiceImplTest {
    @Autowired
    private ParkMapper parkMapper;


    @Test
    public void save() {
         Park park = new Park();
         park.setName("xuanzhou");
         park.setMapFileUrl("/doc/Map/map.xml");
         park.setShapeFileUrl("/doc/Map/shape");
         parkMapper.save(park);

    }

    @Test
    public void delete() {
    }

    @Test
    public void update() {
    }

    @Test
    public void updateMap() {
    }

    @Test
    public void updateRegion() {
    }

    @Test
    public void query() {
    }

    @Test
    public void queryByName() {
    }

    @Test
    public void query1() {
    }
}