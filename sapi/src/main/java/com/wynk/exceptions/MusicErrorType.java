package com.wynk.exceptions;

import org.apache.http.HttpStatus;
import org.slf4j.Marker;

import static world.ignite.common.logging.LoggingMarkers.APPLICATION_INVALID_USECASE;

/**
 * @author : Kunal Sharma
 * @since : 05/10/22, Wednesday
 **/
public enum MusicErrorType implements MusicError {


    MUS999(
            "Oops something went wrong",
            "Oops something went wrong",
            HttpStatus.SC_INTERNAL_SERVER_ERROR,
            APPLICATION_INVALID_USECASE);

    private String errorTitle;

    /**
     * The error msg.
     */
    private String errorMsg;

    /**
     * The http response status.
     */
    private int httpResponseStatusCode;

    private Marker marker;


    MusicErrorType(String errorTitle, String errorMsg, int httpResponseStatus, Marker marker) {
        this.errorTitle = errorTitle;
        this.errorMsg = errorMsg;
        this.httpResponseStatusCode = httpResponseStatus;
        this.marker = marker;
    }

    @Override
    public String getErrorCode() {
        return this.name();
    }

    @Override
    public String getErrorTitle() {
        return errorTitle;
    }

    @Override
    public String getErrorMessage() {
        return errorMsg;
    }


    @Override
    public int getHttpResponseStatusCode() {
        return httpResponseStatusCode;
    }

    @Override
    public Marker getMarker() {
        return marker;
    }
}