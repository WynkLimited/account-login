package com.wynk.exceptions;

import org.slf4j.Marker;

/**
 * @author : Kunal Sharma
 * @since : 13/10/22, Thursday
 **/
public interface MusicError {


    public String getErrorCode();

    public String getErrorTitle();

    public String getErrorMessage();

    public int getHttpResponseStatusCode();

    public Marker getMarker();
}
