package com.wynk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

@Data
@AllArgsConstructor
public class MoEngageEventRequest {
  private String uid;
  private List<MoEngageEvent> eventList;
}
