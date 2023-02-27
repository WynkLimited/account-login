package com.wynk.common;

/**
 * Created with IntelliJ IDEA. User: bhuvangupta Date: 20/09/12 Time: 11:49 PM To change this
 * template use File | Settings | File Templates.
 */
public class PortalException extends Exception {

    public PortalException(String message) {
        super(message);
    }

    public PortalException(Throwable thr) {
        super(thr);
    }

    public PortalException(String errorMsg, Throwable thr) {
        super(errorMsg, thr);
    }
}
