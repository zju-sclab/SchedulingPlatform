package com.skywilling.cn.web.service;

import com.skywilling.cn.web.model.AutoJob;
import com.skywilling.cn.web.model.CollectJob;
import com.skywilling.cn.web.model.ManualJob;

public interface JobService {

  String submit(AutoJob autoJob);

  String submit(ManualJob manualJob);

  String submit(CollectJob collectJob);

  boolean stopJob(String vin, String jobId);
}
