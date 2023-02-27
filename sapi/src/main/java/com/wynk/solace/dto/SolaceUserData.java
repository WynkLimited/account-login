package com.wynk.solace.dto;

import com.wynk.solace.CircleSolaceMapping;

public class SolaceUserData {
    private String msisdn;
    private long timestamp;
    private String uId;
    private CircleSolaceMapping circle;
    private String userType;

    public String getMsisdn() {
        return msisdn;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getuId() {
        return uId;
    }

    public CircleSolaceMapping getCircle() {
        return circle;
    }

    public String getUserType() {
        return userType;
    }

    public static class BuilderClass {
        private String msisdn;
        private long timestamp;
        private String uId;
        private CircleSolaceMapping circle;
        private String userType;

        public BuilderClass msisdn(String msisdn) {
            this.msisdn = msisdn;
            return this;
        }

        public BuilderClass timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public BuilderClass uId(String uId) {
            this.uId = uId;
            return this;
        }

        public BuilderClass userType(String userType) {
            this.userType = userType;
            return this;
        }

        public BuilderClass circle(CircleSolaceMapping circle) {
            this.circle = circle;
            return this;
        }

        public SolaceUserData build() {
            return new SolaceUserData(this);
        }


    }

    SolaceUserData(BuilderClass builderClass) {
        this.msisdn = builderClass.msisdn;
        this.circle = builderClass.circle;
        this.timestamp = builderClass.timestamp;
        this.uId = builderClass.uId;
        this.userType = builderClass.userType;
    }

    @Override
    public String toString() {
        return "SolaceUser [msisdn=" + msisdn + ", circle=" + circle + ", userType=" + userType + ", timestamp=" + timestamp + ", uId=" + uId + "]";
    }

}
