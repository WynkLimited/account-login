package com.wynk.common;


public class PackProviderAuthRequest {

    private String method;
    private String requestUri;
    private String requestPayload;
    private long   requestTimestamp;
    private String signature;
    private String appId;

    public String getMethod() {
        return method;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getRequestPayload() {
        return requestPayload;
    }

    public long getRequestTimestamp() {
        return requestTimestamp;
    }

    public String getSignature() {
        return signature;
    }

    public String getAppId() {
        return appId;
    }

    public static class Builder {

        private String method;
        private String requestUri;
        private String requestPayload;
        private long   requestTimestamp;
        private String signature;
        private String appId;

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder requestUri(String requestUri) {
            this.requestUri = requestUri;
            return this;
        }

        public Builder requestPayload(String requestPayload) {
            this.requestPayload = requestPayload;
            return this;
        }

        public Builder requestTimestamp(long requestTimestamp) {
            this.requestTimestamp = requestTimestamp;
            return this;
        }

        public Builder signature(String signature) {
            this.signature = signature;
            return this;
        }

        public Builder appId(String appId) {
            this.appId = appId;
            return this;
        }

        public PackProviderAuthRequest build() {
            return new PackProviderAuthRequest(this);
        }
    }

    private PackProviderAuthRequest(Builder builder) {
        this.method = builder.method;
        this.requestUri = builder.requestUri;
        this.requestPayload = builder.requestPayload;
        this.requestTimestamp = builder.requestTimestamp;
        this.signature = builder.signature;
        this.appId = builder.appId;
    }

    @Override
    public String toString() {
        return "PackProviderAuthRequest [method=" + method + ", requestUri=" + requestUri + ", requestPayload=" + requestPayload + ", requestTimestamp=" + requestTimestamp + ", signature=" + signature
                + ", appId=" + appId + "]";
    }

}