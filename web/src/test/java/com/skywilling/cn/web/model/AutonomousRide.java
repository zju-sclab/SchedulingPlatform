package com.skywilling.cn.web.model;


import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.print.attribute.standard.JobState;
import java.io.Serializable;
import java.util.Date;

@Data
public class AutonomousRide implements Serializable {

  private static final long serialVersionUID = 1l;

  private String vin;
  @Id
  private String jobId;

  private String source;

  private String destination;

  private Date gmtCreate;

  private Date gmtModified;

  private JobState jobState;

  //private DrivingState drivingState;


}
