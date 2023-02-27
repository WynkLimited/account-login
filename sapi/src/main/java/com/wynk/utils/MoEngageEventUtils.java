package com.wynk.utils;

import com.wynk.common.MoEngageConstants;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class MoEngageEventUtils {
  private static HttpHeaders getBasicHeadersMoEvent() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", MoEngageConstants.CONTENT_TYPE);
    headers.set("Authorization", MoEngageConstants.AUTHORISATION_MOENGAGE_EVENT_PROD);
    headers.set("MOE-APPKEY", MoEngageConstants.MOE_APP_KEY);
    return headers;
  }

  public static HttpEntity getBasicHttpEntityMoEvent(String payload) {
    HttpHeaders header = getBasicHeadersMoEvent();
    return new HttpEntity<>(payload, header);
  }
}
