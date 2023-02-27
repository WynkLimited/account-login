package com.wynk.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wynk.common.MoEngageConstants;
import com.wynk.dto.MoEngageEvent;
import com.wynk.dto.MoEngageEventRequest;
import com.wynk.enums.MoEngageEventAttributes;
import com.wynk.utils.LogstashLoggerUtils;
import com.wynk.utils.MoEngageEventUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MoEngageEventService {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  RestTemplate restTemplate;

  public void sendEvent(MoEngageEventRequest moEngageEventRequest) {
    JsonObject payload = moEngageEventModelPayload(moEngageEventRequest);
    String res = "";
    try {
      res =
          restTemplate.postForObject(
              MoEngageConstants.MOENGAGE_EVENT_URL_PROD,
              MoEngageEventUtils.getBasicHttpEntityMoEvent(payload.toString()),
              String.class);
      JSONParser parser = new JSONParser();
      JSONObject obj = (JSONObject) parser.parse(res);
      String requestStatus = obj.get("status").toString();
      String responseMsg = obj.get("message").toString();
      moEngageEventRequest
          .getEventList()
          .forEach(
              i -> {
                LogstashLoggerUtils.moEngageEventLog(
                    moEngageEventRequest.getUid(),
                    payload.toString(),
                    MoEngageConstants.MOENGAGE_EVENT_URL_PROD,
                    responseMsg,
                    !requestStatus.equalsIgnoreCase(MoEngageConstants.SUCCESS),
                    i.getEventName(),
                    i.getAttributes().get(MoEngageEventAttributes.CurrentPlanID.name()));
              });
    } catch (Exception e) {
      moEngageEventRequest
          .getEventList()
          .forEach(
              i -> {
                LogstashLoggerUtils.moEngageEventLog(
                    moEngageEventRequest.getUid(),
                    payload.toString(),
                    MoEngageConstants.MOENGAGE_EVENT_URL_PROD,
                    e.toString(),
                    true,
                    i.getEventName(),
                    i.getAttributes().get(MoEngageEventAttributes.CurrentPlanID.name()));
              });
    }
  }

  private static JsonObject moEngageEventModelPayload(MoEngageEventRequest moEngageEventRequest) {
    JsonObject payload = new JsonObject();
    payload.addProperty("type", "event");
    payload.addProperty("customer_id", moEngageEventRequest.getUid());
    JsonArray actions = new JsonArray();
    for (MoEngageEvent moEngageEvent : moEngageEventRequest.getEventList()) {
      JsonObject eventObject = new JsonObject();
      JsonObject attributes = new JsonObject();
      for (String key : moEngageEvent.getAttributes().keySet()) {
        attributes.addProperty(key, moEngageEvent.getAttributes().get(key));
      }
      eventObject.addProperty("action", moEngageEvent.getEventName());
      eventObject.add("attributes", attributes);
      actions.add(eventObject);
    }
    payload.add("actions", actions);
    return payload;
  }
}
