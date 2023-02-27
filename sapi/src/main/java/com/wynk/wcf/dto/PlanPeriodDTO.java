package com.wynk.wcf.dto;

import java.util.concurrent.TimeUnit;
import lombok.Data;

@Data
public class PlanPeriodDTO {

  private int validity;
  private TimeUnit timeUnit;
}
