package com.skywilling.cn.common.config.redis;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisDao {

  private static final Logger LOG = LoggerFactory.getLogger(RedisDao.class);

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;


  public void save(String key, Object object) {
    ValueOperations<String, Object> vop = redisTemplate.opsForValue();
    vop.set(key, object);
  }

  public boolean exists(String key) {
    if (redisTemplate.hasKey(key) ) {
      return true;
    }
    return false;
  }

  public Object read(String key) {
    ValueOperations<String, Object> valueOper = redisTemplate.opsForValue();
    return valueOper.get(key);
  }

  public void delete(String key) {
    ValueOperations<String, Object> valueOper = redisTemplate.opsForValue();
    RedisOperations<String, Object> RedisOperations = valueOper.getOperations();
    RedisOperations.delete(key);
  }

  public void lpush(String key, Object object) {
    ListOperations<String, Object> listOps = redisTemplate.opsForList();
    listOps.leftPush(key, object);

  }

  public void rpush(String key, Object object) {
    ListOperations<String, Object> listOps = redisTemplate.opsForList();
    listOps.rightPush(key, object);
  }

  public Object lpop(String key) {
    ListOperations<String, Object> listOps = redisTemplate.opsForList();
    Object obj = listOps.leftPop(key);
    return obj;
  }

  public List lrange(String key, long start, long end) {
    ListOperations<String, Object> listOps = redisTemplate.opsForList();
    List list = listOps.range(key, start, end);
    return list;
  }

  public Object lIndex(String key, long index) {
    ListOperations<String, Object> listOps = redisTemplate.opsForList();
    Object obj = listOps.index(key, index);
    return obj;
  }

  public long size(String key) {
    ListOperations<String, Object> listOperations = redisTemplate.opsForList();
    return listOperations.size(key);
  }

  public Set<String> keys(String pattern)
  {
    return redisTemplate.keys(pattern);
  }

  public Boolean expire(String key, final long timeout, final TimeUnit unit) {
    return redisTemplate.expire(key, timeout, unit);
  }

  public Long getExpire(String key, final TimeUnit timeUnit) {
    return redisTemplate.getExpire(key, timeUnit);
  }
}
