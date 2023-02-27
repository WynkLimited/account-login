package com.wynk.wcf.dto;

import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.Getter;

@Data
public class ReminderMessage {

  private int duration;
  private TimeUnit timeUnit;
  private boolean sms;
  private boolean appNotification;
}
