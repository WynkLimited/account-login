package com.wynk.dto;

/**
 * Created by Aakash on 20/09/17.
 */
public class WCFCarrierBillingRequestBody {

    private String tid;
    private String msisdn;

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @Override
    public String toString() {
        return "WCFCarrierBillingRequestBody{" +
                "tid='" + tid + '\'' +
                ", msisdn='" + msisdn + '\'' +
                '}';
    }
}
