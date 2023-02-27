package com.wynk.auth.handlers;

import com.wynk.auth.Auth;
import com.wynk.common.PortalException;
import com.wynk.handlers.IUrlRequestHandler;
import com.wynk.server.HttpResponseService;
import com.wynk.service.AccountService;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller("/webassets.*")
public class AuthBeacon1RequestHandler implements IUrlRequestHandler {

  @Autowired private Auth auth;

  @Override
  public HttpResponse handleRequest(String requestUri, String requestPayload, HttpRequest request)
      throws PortalException {
    if (request.getMethod().equals(HttpMethod.GET)) {
      int imageNumber = auth.resolveBeaconKey(request.uri());
      return auth.returnImageResponse(imageNumber);
    }
    return HttpResponseService.createResponse(
        AccountService.createErrorResponse("400", "Bad request").toJSONString(),
        HttpResponseStatus.BAD_REQUEST);
  }
}
