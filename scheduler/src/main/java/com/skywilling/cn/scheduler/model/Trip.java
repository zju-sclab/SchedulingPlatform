package com.skywilling.cn.scheduler.model;

import com.skywilling.cn.livemap.model.LiveStation;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Document(collection = "ride")
@Data
public class Trip implements Serializable {

    private static final long serialVersionUID = 6650672244872735531L;
    @Id
    private String id;
    private String vin;
    private Route route;
    private Long startTime;
    private Long endTime = 0L;
    private String parkName;
    private LiveStation startStation;
    private LiveStation endStation;
    private List<String> taskIds = new ArrayList<>();
    /**
     * start用于标志走到route的第几个路段
     */
    private int start;
    private int end;
    /**
     * 状态，TripStatus为具体状态类型
     */
    private int status;

    public Trip(){}

    public Trip(String vin, String tripId, Route route) {
        this.vin = vin;
        this.id = tripId;
        this.route = route;
        this.parkName = route.getParkName();
        this.startTime = System.currentTimeMillis();
        this.start = 0;
        this.end = route.getLiveLanes().size();
    }

}
