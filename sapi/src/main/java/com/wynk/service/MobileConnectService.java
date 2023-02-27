package com.wynk.service;

import com.google.gson.Gson;
import com.wynk.common.UserEventType;
import com.wynk.config.MusicConfig;
import com.wynk.server.ChannelContext;
import com.wynk.user.dto.UserActivityEvent;
import com.wynk.utils.HttpClient;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class MobileConnectService {

  @Autowired
  private MusicService musicService;

  private static final Logger mactivityLogger = LoggerFactory.getLogger("mactivityanalytics");

  private static final Logger logger =
      LoggerFactory.getLogger(MobileConnectService.class.getCanonicalName());
  private static final int CONNECTION_TIMEOUT_MILLIS = 3000;
  private static final String REDIRECT_URL = "http://localhost:8080/wynk/callback";
  private static final String CLIENT_KEY = "x-ab918f40-f0d3-466f-b587-7bf1dcff3b70";
  private static final String CLIENT_SECRET = "x-bd7aae7a-fb2a-468a-8b1f-3cdddb397bec";
  private static final String AUTH_URL =
      "http://india.gateway.wso2telco.com/authorize/v1/%s/oauth2/authorize";

  public String getAuthUrl(String operator) {
    String state = getRandomString();
    String authUrl =
        String.format(AUTH_URL, operator)
            + "?client_id="
            + getClientKey()
            + "&scope=openid+mc_attr_vm_share&response_type=code&redirect_uri="
            + REDIRECT_URL
            + "&acr_values=2&nonce="
            + state
            + "&state="
            + state;
    logger.info("Auth url : " + authUrl);
    return authUrl;
  }

  public String getRandomString() {
    Random rand = new Random();
    int n = rand.nextInt(100000) + 1;
    return n + "";
  }

  public static String getClientKey() {
    return CLIENT_KEY;
  }

  public static String getClientSecret() {
    return CLIENT_SECRET;
  }

  Gson gson = new Gson();

  @Autowired MusicConfig musicConfig;

  String mobileConnectBaseUrl = "https://india.mconnect.wso2telco.com";

  public Map<String, String> getHeaderMap() throws Exception {
    Map<String, String> headerMap = new HashMap<>();
    headerMap.put("content-type", "application/x-www-form-urlencoded");
    String encoding =
        Base64.getEncoder()
            .encodeToString((getClientKey() + ":" + getClientSecret()).getBytes("UTF-8"));
    headerMap.put("Authorization", "Basic " + encoding);
    return headerMap;
  }

  public String getUserToken(String operator, String authcode) throws Exception {
    String tokenUrl = String.format("/token/v1/%s/oauth2/token", operator);
    String url = "https://india.gateway.wso2telco.com" + tokenUrl;
    Map<String, String> payload = new HashMap<>();
    payload.put("grant_type", "authorization_code");
    payload.put("redirect_uri", URLEncoder.encode(REDIRECT_URL, "UTF-8"));
    payload.put("code", authcode);
    String data = "";
    for (Map.Entry<String, String> header : payload.entrySet()) {
      data = data.concat(header.getKey() + "=" + header.getValue() + "&");
    }
    data = data.substring(0, data.length() - 1);
    logger.info("request data for url : " + url + " : " + data);
    createTokenRequestEvent(authcode);
    Long start = System.currentTimeMillis();
    String responseBody = HttpClient.postDataWithTimeOut(url, data, getHeaderMap(), CONNECTION_TIMEOUT_MILLIS);
    logger.info("time taken in getUserToken : " + (System.currentTimeMillis() - start));
    String token = parseAuthCode(responseBody);
    createTokenReceivedEvent(authcode, token, "", "");
    return token;
  }

  private void createTokenRequestEvent(String authcode) {
    UserActivityEvent event = new UserActivityEvent();
    event.setType(UserEventType.MOBILE_CONNECT);
    event.setTimestamp(System.currentTimeMillis());
    event.setId("token_request");
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("authcode", authcode);
    event.setMeta(jsonObject);
    String eventLog = musicService.createEventLog(event, ChannelContext.getRequest());
    mactivityLogger.info(eventLog);
  }

  private void createTokenReceivedEvent(String authcode, String token, String error, String description) {
    UserActivityEvent event = new UserActivityEvent();
    event.setType(UserEventType.MOBILE_CONNECT);
    event.setTimestamp(System.currentTimeMillis());
    event.setId("token_received");
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("authcode", authcode);
    jsonObject.put("access_token", token);
    jsonObject.put("error", error);
    jsonObject.put("error_description", description);
    event.setMeta(jsonObject);
    String eventLog = musicService.createEventLog(event, ChannelContext.getRequest());
    mactivityLogger.info(eventLog);
  }

  private String parseAuthCode(String responseBody) {
    Map map = gson.fromJson(responseBody, Map.class);
    return map.get("access_token").toString();
  }

  private String parseMSISDN(String responseBody) {
    Map map = gson.fromJson(responseBody, Map.class);
    return map.get("device_msisdn").toString();
  }

  public String getUserInfo(String authToken) throws Exception {
    String userInfoUrl = "/oauth2/userinfo?schema=openid";
    String url = mobileConnectBaseUrl.concat(userInfoUrl);
    Map<String, String> headerMap = new HashMap<>();
    headerMap.put("authorization", "Bearer " + authToken);
    Long start = System.currentTimeMillis();
    String responseBody = HttpClient.getContent(url, CONNECTION_TIMEOUT_MILLIS, headerMap);
    logger.info("time taken in getUserToken : " + (System.currentTimeMillis() - start));
    return parseMSISDN(responseBody);
  }

  public String getMSISDN(String operator, String authcode) {
    String msisdn = null;
    try {
      String userToken = getUserToken(operator, authcode);
      msisdn = getUserInfo(userToken);
      logger.info("Received msisdn : " + msisdn);
      if (msisdn.length() < 13 && msisdn.startsWith("91")) msisdn = "+" + msisdn;
    } catch (Exception e) {
      logger.error("Exception in getMSISDN : ", e);
      String trace = ExceptionUtils.getStackTrace(e);
      createTokenReceivedEvent(authcode, "", e.getMessage(), trace.substring(0, trace.length() < 100 ? trace.length() : 99));
      return null;
    }
    return msisdn;
  }
}
