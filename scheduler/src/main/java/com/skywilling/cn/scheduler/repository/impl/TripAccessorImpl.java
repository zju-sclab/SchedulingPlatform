package com.skywilling.cn.scheduler.repository.impl;

import com.skywilling.cn.common.enums.Enumerations;
import com.skywilling.cn.scheduler.model.Trip;
import com.skywilling.cn.scheduler.repository.TripAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class TripAccessorImpl implements TripAccessor {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  public void save(Trip trip) {
    mongoTemplate.save(trip);
  }

  @Override
  public void update(String tripId, Map<String, Object> updates) {
    Update update = new Update();
    updates.forEach(update::pull);
    mongoTemplate.updateFirst(Query.query(Criteria.where("generateTripId").is(tripId)), update, Trip.class);
  }

  @Override
  public Trip find(String tripId) {
    return mongoTemplate.findById(tripId, Trip.class);
  }

  @Override
  public Trip findLastedBy(String vin) {
    Query query = new Query();
    query.addCriteria(Criteria.where("vin").is(vin));
    query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "startTime")));
    List<Trip> rides = mongoTemplate.find(query, Trip.class);
    return rides.stream().findFirst().orElse(null);
  }

  @Override
  public List<Trip> query(int page, int size) {
    Query query = new Query();
    query.with(new Sort(new Order(Direction.DESC, "startTime"))).with(new PageRequest(page, size));
    return mongoTemplate.find(query, Trip.class);
  }

  @Override
  public List<Trip> queryByJobStatus(Enumerations.JobStatusType jobStatusType, int page, int size) {
    Query query = new Query();
    query.addCriteria(Criteria.where("status").is(jobStatusType));
    query.with(new Sort(new Order(Direction.DESC, "startTime"))).with(new PageRequest(page, size));
    return mongoTemplate.find(query, Trip.class);
  }

  @Override
  public List<Trip> queryBy(String vin, int page,int size) {
    Query query = new Query();
    query.addCriteria(Criteria.where("vin").is(vin));
    query.with(new Sort(new Order(Direction.DESC, "startTime"))).with(new PageRequest(page, size));
    return mongoTemplate.find(query, Trip.class);

  }

  @Override
  public List<Trip> queryBy(String vin, Date start, Date end, int page, int size) {
    Query query = new Query();
    query.addCriteria(Criteria.where("vin").is(vin))
        .addCriteria(Criteria.where("startTime").gte(start))
        .addCriteria(Criteria.where("startTime").lte(end));
    query.with(new Sort(new Order(Direction.DESC, "startTime"))).with(new PageRequest(page, size));
    return mongoTemplate.find(query, Trip.class);
  }

}
