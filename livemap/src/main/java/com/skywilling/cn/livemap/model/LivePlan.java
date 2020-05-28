package com.skywilling.cn.livemap.model;

import com.skywilling.cn.livemap.util.CacheManager;
import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName LivePlan
 * Author  Lin
 * Date 2019/6/10 11:37
 **/

@Data
public class LivePlan implements Serializable {
     private  CacheManager cacheManager = CacheManager.getCacheManagerInstance();

}
