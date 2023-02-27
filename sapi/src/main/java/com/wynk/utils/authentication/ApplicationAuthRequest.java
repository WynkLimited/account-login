package com.wynk.utils.authentication;

/**
 * @author : Kunal Sharma
 * @since : 23/07/22, Saturday
 **/

public class ApplicationAuthRequest {

    private String method;
    private String requestUri;
    private String requestPayload;
    private long requestTimestamp;
    private String signature;
    private String appId;

    public ApplicationAuthRequest(String method, String requestUri, String requestPayload, long requestTimestamp, String signature, String appId) {
        this.method = method;
        this.requestUri = requestUri;
        this.requestPayload = requestPayload;
        this.requestTimestamp = requestTimestamp;
        this.signature = signature;
        this.appId = appId;
    }

    public ApplicationAuthRequest() {

    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(String requestPayload) {
        this.requestPayload = requestPayload;
    }

    public long getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(long requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public ApplicationAuthRequest _method(String method) {
        this.method = method;
        return this;
    }

    public ApplicationAuthRequest _requestUri(String requestUri) {
        this.requestUri = requestUri;
        return this;
    }

    public ApplicationAuthRequest _requestPayload(String requestPayload) {
        this.requestPayload = requestPayload;
        return this;
    }

    public ApplicationAuthRequest _requestTimestamp(long requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
        return this;
    }

    public ApplicationAuthRequest _signature(String signature) {
        this.signature = signature;
        return this;
    }

    public ApplicationAuthRequest _appId(String appId) {
        this.appId = appId;
        return this;
    }


    @Override
    public String toString() {
        return "ApplicationAuthRequest [method=" + method + "," +
                " requestUri=" + requestUri +
                ", requestPayload=" + requestPayload +
                ", requestTimestamp=" + requestTimestamp +
                ", signature=" + signature +
                ", appId=" + appId + "]";
    }


}
