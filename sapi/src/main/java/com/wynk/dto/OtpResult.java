package com.wynk.dto;

/**
 * Created by a1vlqlyy on 23/03/17.
 */
public class OtpResult {

    private String otp;
    private Integer retryCount;
    private Integer otpRequestCount;

    public OtpResult(String otp, Integer retryCount, Integer otpRequestCount) {
        this.otp = otp;
        this.retryCount = retryCount;
        this.otpRequestCount = otpRequestCount;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Integer getOtpRequestCount() {
        return otpRequestCount;
    }

    public void setOtpRequestCount(Integer otpRequestCount) {
        this.otpRequestCount = otpRequestCount;
    }
}
