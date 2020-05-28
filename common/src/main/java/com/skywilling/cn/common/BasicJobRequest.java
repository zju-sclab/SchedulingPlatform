package com.skywilling.cn.common;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.common.enums.Enumerations;


public class BasicJobRequest {
    String vin;
    int templetId;
    Enumerations.JobType jobType;
    JSONObject paramObject;

    public BasicJobRequest(JSONObject object) {
        this.vin = object.getString("vin");
        this.templetId = object.getInteger("templetId");
        String jobTypeCode = object.getString("jobTypeCode");
        switch (jobTypeCode) {
            case "fileSync":
                this.jobType = Enumerations.JobType.FILE_SYNC;
                break;
            case "autoDrive":
                this.jobType = Enumerations.JobType.AUTO_DRIVE;
                break;
            case "manDrive":
                this.jobType = Enumerations.JobType.MANUAL_DRIVE;
                break;
            default:
                break;
        }
        this.paramObject = object.getJSONObject("paramObject");
    }

}
