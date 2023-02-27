package com.wynk.solace.dto;

public class SolaceNumberData {

    private String si;

    private String circle_id;

    private String lob;

    private String is_wynk;

    private String subscribercode;

    public SolaceNumberData(String subscribercode, String lob, String msisdn, String is_wynk, String circle_id) {
        this.circle_id = circle_id;
        this.si = msisdn;
        this.subscribercode = subscribercode;
        this.lob = lob;
        this.is_wynk = is_wynk;
    }

    public String getSubscribercode() {
        return subscribercode;
    }

    public String getLob() {
        return lob;
    }

    public String getSi() {
        return si;
    }

    public String getIs_wynk() {
        return is_wynk;
    }

    public String getCircle_id() {
        return circle_id;
    }


}
