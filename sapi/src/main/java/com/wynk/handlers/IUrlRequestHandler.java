package com.wynk.handlers;

import com.wynk.common.PortalException;
import com.wynk.exceptions.OTPAuthorizationException;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * Created with IntelliJ IDEA. User: bhuvangupta Date: 13/10/12 Time: 12:52 AM To change this
 * template use File | Settings | File Templates.
 */
public interface IUrlRequestHandler {

    public HttpResponse handleRequest(String requestUri, String requestPayload, HttpRequest request) throws
            PortalException, OTPAuthorizationException;
}
