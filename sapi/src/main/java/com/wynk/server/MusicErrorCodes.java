package com.wynk.server;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Created by bhuvangupta on 26/12/13.
 */
public enum MusicErrorCodes {

    INVALID_METHOD,
    AUTHENTICATION_FAILED,
    INVALID_FORMAT,
    INVALID_PARAMETERS,
    INVALID_API_KEY,
    RATE_LIMIT_EXCEEDED,
    SUSPENDED_USER,
    INTERNAL_SERVER_ERROR,
    TEMPORARY_SERVER_ERROR,
    FORBIDDEN;


    public static HttpResponseStatus getHttpErrorStatus(String errorCode)
    {
        if(errorCode.equalsIgnoreCase(FORBIDDEN.name()))
            return HttpResponseStatus.FORBIDDEN;
        if(errorCode.equalsIgnoreCase(INVALID_METHOD.name()))
            return HttpResponseStatus.BAD_REQUEST;
        if(errorCode.equalsIgnoreCase(AUTHENTICATION_FAILED.name()))
            return HttpResponseStatus.UNAUTHORIZED;
        if(errorCode.equalsIgnoreCase(INVALID_FORMAT.name()))
            return HttpResponseStatus.BAD_REQUEST;
        if(errorCode.equalsIgnoreCase(INVALID_PARAMETERS.name()))
            return HttpResponseStatus.BAD_REQUEST;
        return HttpResponseStatus.INTERNAL_SERVER_ERROR;

    }

}
