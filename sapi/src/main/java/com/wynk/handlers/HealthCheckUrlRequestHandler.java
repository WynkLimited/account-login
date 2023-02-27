package com.wynk.handlers;

import com.wynk.common.PortalException;
import com.wynk.utils.HTTPUtils;

import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

/**
 * Created with IntelliJ IDEA.
 * User: dhruva
 * Date: 02/12/14
 * Time: 1:10 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller("/v1/health/.*")
public class HealthCheckUrlRequestHandler implements IUrlRequestHandler {
    private Logger logger = LoggerFactory.getLogger(HealthCheckUrlRequestHandler.class.getCanonicalName());

    private final String jsonContentType = "application/json";

    private boolean serverUp = true;

    @Override
    public HttpResponse handleRequest(String requestUri, String requestPayload, HttpRequest request) throws PortalException {
        logger.info("HealthCheckUrlRequestHandler - Received request : {}", requestUri);
        if (request.getMethod().equals(HttpMethod.GET)) {

            if (requestUri.matches("/v1/health/status.*")) {
                if (serverUp) {
                    return HTTPUtils.createResponse("up", jsonContentType, HttpResponseStatus.OK);
                } else {
                    return HTTPUtils.createResponse("down", jsonContentType, HttpResponseStatus.SERVICE_UNAVAILABLE);
                }
            }
            if (requestUri.matches("/v1/health/config/status.*")) {
                if (serverUp) {
                    return HTTPUtils.createResponse("up", jsonContentType, HttpResponseStatus.OK);
                } else {
                    return HTTPUtils.createResponse("down", jsonContentType, HttpResponseStatus.SERVICE_UNAVAILABLE);
                }
            }
        } else if (request.getMethod().equals(HttpMethod.POST)) {

            if (requestUri.matches("/v1/health/markDown.*")) {
                serverUp = false;
                return HTTPUtils.createResponse("down", jsonContentType, HttpResponseStatus.OK);
            }

            if (requestUri.matches("/v1/health/markUp.*")) {
                serverUp = true;
                return HTTPUtils.createResponse("up", jsonContentType, HttpResponseStatus.OK);
            }
        }

        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
    }
}
