package com.wynk.auth.handlers;

import com.wynk.auth.Auth;
import com.wynk.common.PortalException;
import com.wynk.handlers.IUrlRequestHandler;
import com.wynk.server.HttpResponseService;
import com.wynk.service.AccountService;
import com.wynk.utils.HTTPUtils;
import com.wynk.utils.JsonUtils;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller("/health/.*")
public class AuthBeacon2RequestHandler implements IUrlRequestHandler {

  @Autowired private Auth auth;

  @Override
  public HttpResponse handleRequest(String requestUri, String requestPayload, HttpRequest request)
      throws PortalException {
    if (request.getMethod() == HttpMethod.POST) {
      if (requestUri.matches("/health/check.*")) {
        JSONObject reqPayload = JsonUtils.getJsonObjectFromString(requestPayload);
        String becanPart1 = "";
        String becanPart2 = "";
        String becanSecret = "";
        becanPart1 = request.headers().get("bk");
        becanPart2 = (String) reqPayload.get("pid");
        boolean spoofResult = false;
        if (StringUtils.isEmpty(becanPart1) | StringUtils.isEmpty(becanPart2)) {
          spoofResult = true;
        } else {
          becanSecret = auth.genBeaconSecret(becanPart1, becanPart2);
        }
        HttpResponse response =
            HTTPUtils.createResponse(
                "{\"status\":\"up\"}", "application/json", HttpResponseStatus.OK);
        auth.encryptBecanSecretInHeaders(response, spoofResult, becanSecret);
        return response;
      }
    } else if (request.getMethod() == HttpMethod.OPTIONS) {
      return HTTPUtils.createResponse("{}", "application/json", HttpResponseStatus.OK);
    }
    return HttpResponseService.createResponse(
        AccountService.createErrorResponse("400", "Bad request").toJSONString(),
        HttpResponseStatus.BAD_REQUEST);
  }
}
