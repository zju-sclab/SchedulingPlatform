package com.skywilling.cn.manager.car.model;

import com.skywilling.cn.common.model.AbstractPoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instruction extends AbstractPoint {
  private double motor;
  private double servo;
  private int shift;
}
