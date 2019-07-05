package com.skywilling.cn.manager.car.service.impl;

import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.model.SiteExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ClassName SiteExtDao
 * Author  Lin
 * Date 2019/5/30 16:57
 **/

@Repository
public class SiteExtDao {

    @Autowired
    private MongoTemplate mongoTemplate;


    public List<SiteExt> findPointInPolygon(GeoJsonPolygon geoJsonPolygon, String collectionName) {
        Query query = new Query(Criteria.where("location").within(geoJsonPolygon));
        List<SiteExt> list = mongoTemplate.find(query, SiteExt.class,collectionName);
        return list;
    }

    public void insert(SiteExt siteExt, String collectionName) {
        mongoTemplate.insert(siteExt,collectionName);
    }

    public void insertBatch(List<SiteExt> list, String collectionName) {

        // BulkMode.UNORDERED:表示并行处理，遇到错误时能继续执行不影响其他操作；
        // BulkMode.ORDERED：表示顺序执行，遇到错误时会停止所有执行
        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, collectionName);
        ops.insert(list);
        // 执行操作
        ops.execute();
    }


    public List<SiteExt> findAll(String collectionName) {
        Query query=new Query();
        return mongoTemplate.find(query, SiteExt.class, collectionName);
    }

    public List<SiteExt> findByDist(GeoJsonPoint geoJsonPoint, double dis) {

        Criteria geoCriteria = Criteria.where("location").nearSphere(geoJsonPoint).maxDistance(dis);
        Query query = Query.query(geoCriteria);
        return  mongoTemplate.find(query,SiteExt.class);
    }

    public SiteExt nearVehicle(GeoJsonPoint point) {
        Criteria geoCriteria = Criteria.where("location").nearSphere(point);
        Query query= Query.query(geoCriteria);
        query.with(PageRequest.of(0,1));
        return mongoTemplate.find(query,SiteExt.class).get(0);
    }
}
