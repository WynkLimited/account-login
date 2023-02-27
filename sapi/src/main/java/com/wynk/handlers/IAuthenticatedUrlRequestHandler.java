package com.wynk.handlers;

import com.wynk.common.PortalException;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Created by bhuvangupta on 13/01/14.
 */
public interface IAuthenticatedUrlRequestHandler {

    public boolean authenticate(String requestUri, String requestPayload, HttpRequest request) throws PortalException;
}
