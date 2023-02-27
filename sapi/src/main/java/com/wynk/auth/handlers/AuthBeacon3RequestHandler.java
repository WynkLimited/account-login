package com.wynk.auth.handlers;

import com.wynk.auth.Auth;
import com.wynk.common.PortalException;
import com.wynk.handlers.IUrlRequestHandler;
import com.wynk.server.HttpResponseService;
import com.wynk.service.AccountService;
import com.wynk.user.dto.User;
import com.wynk.utils.HTTPUtils;
import com.wynk.utils.JsonUtils;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.ServerCookieEncoder;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller("/music/account/v1/.*")
public class AuthBeacon3RequestHandler implements IUrlRequestHandler {

  private Logger logger = LoggerFactory.getLogger(getClass().getCanonicalName());

  @Autowired private Auth auth;

  @Autowired private AccountService accountService;

  @Override
  public HttpResponse handleRequest(String requestUri, String requestPayload, HttpRequest request)
      throws PortalException {
    if (request.getMethod() == HttpMethod.POST) {
      if (requestUri.matches("/music/account/v1/login.*")) {
        String becanSecret = "";
        String origin = request.headers().get("X-bsy-cip");
        try {
          becanSecret =
              Arrays.stream(origin.split("\\."))
                  .map(s -> s.substring(1, 3))
                  .collect(Collectors.joining());
        } catch (Exception ingnored) {
          return HTTPUtils.createResponse("{}", "application/json", HttpResponseStatus.OK);
        }
        String cachedDeviceId = auth.getCachedDidFromBecanSecret(becanSecret);
        // returning in case did is not found in the redis
        if (StringUtils.isEmpty(becanSecret)) {
          return HTTPUtils.createResponse("{}", "application/json", HttpResponseStatus.OK);
        }
        Map<String, String> totps = auth.generateTotp(cachedDeviceId);
        HttpResponse response = loginOrCreateWapUser(requestPayload, request, totps);
        return response == null ? HttpResponseService.createOKResponse("") : response;
      }
    } else if (request.getMethod() == HttpMethod.OPTIONS) {
      return HTTPUtils.createResponse("{}", "application/json", HttpResponseStatus.OK);
    }
    return HttpResponseService.createResponse(
        AccountService.createErrorResponse("400", "Bad request").toJSONString(),
        HttpResponseStatus.BAD_REQUEST);
  }

  private HttpResponse loginOrCreateWapUser(
      String requestPayload, HttpRequest request, Map<String, String> totps)
      throws PortalException {

    if (StringUtils.isBlank(requestPayload) || totps == null) {
      // payload is empty early return
      return null;
    }
    JSONObject requestObj = JsonUtils.getJsonObjectFromString(requestPayload);

    if (requestObj == null) {
      // could not parse objects so return null
      return null;
    }

    JSONObject responseJsonObj = JsonUtils.getJsonObjectFromString(requestPayload);
    responseJsonObj.putAll(totps);
    User user = accountService.findOrCreateWapUser(responseJsonObj);

    String token = null;
    if (!StringUtils.isBlank(user.getToken())) {
      token = user.getToken();
      // user.setToken(null);
    }
    String responseJson = null;
    try {
      JSONObject userProfile = user.toUserProfile();
      //totpDeviceId
      userProfile.put("dt", user.getDevices().get(0).getTotpDeviceId());
      //totpKey
      userProfile.put("kt", user.getDevices().get(0).getTotpDeviceKey());
      userProfile.put("server_timestamp", System.currentTimeMillis());
      responseJson = userProfile.toJSONString();
    } catch (Exception e) {
      logger.info("error creating json from user");
    }

    if (!StringUtils.isBlank(responseJson)) {
      HttpResponse response = HttpResponseService.createOKResponse(responseJson);

      if (!StringUtils.isEmpty(token)) {
        logger.info("setting token in cookie and encoding");
        Cookie tokenCookie = new DefaultCookie("tokenCookie", token);
        tokenCookie.setMaxAge(365 * 24 * 60 * 60);
        response.headers().set("Set-Cookie", ServerCookieEncoder.encode(tokenCookie));
      }
      return response;

    } else {
      HttpResponse response =
          HttpResponseService.createResponse(responseJson, HttpResponseStatus.BAD_REQUEST);
      return response;
    }
  }
}
