package com.wynk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class MoEngageEvent {
  private String eventName;
  private Map<String, String> attributes;
}
