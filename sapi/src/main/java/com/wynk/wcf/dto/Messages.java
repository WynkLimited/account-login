package com.wynk.wcf.dto;

import java.util.List;
import lombok.Data;

@Data
public class Messages {

  private Message activation;
  private Message deactivation;
  private Message renewal;
  private Message download;
  private List<ReminderMessage> reminder;
}
