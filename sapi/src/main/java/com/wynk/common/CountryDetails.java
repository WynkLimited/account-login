package com.wynk.common;

import org.json.simple.JSONObject;

public class CountryDetails {
    private String countryCode;
    private String isoCode;
    private String countryName;
    private Boolean pointPurchaseEnabled;
    private Boolean helloTuneEnabled;
    private Integer mobileNumberMaxLength;
    private String flagUrl;
    private Boolean callVerification;
    private Boolean adEnabled;
    private Boolean podcastEnabled;


    public CountryDetails(String countryCode, String isoCode) {
        this.countryCode = countryCode;
        this.isoCode = isoCode;
    }

    public CountryDetails(String countryCode, String isoCode, String countryName, Boolean pointPurchaseEnabled, Boolean helloTuneEnabled, Integer mobileNumberMaxLength, String flagUrl) {
        this.countryCode = countryCode;
        this.isoCode = isoCode;
        this.countryName = countryName;
        this.pointPurchaseEnabled = pointPurchaseEnabled;
        this.helloTuneEnabled = helloTuneEnabled;
        this.mobileNumberMaxLength = mobileNumberMaxLength;
        this.flagUrl = flagUrl;
    }

    public CountryDetails(String countryCode, String isoCode, String countryName, Boolean pointPurchaseEnabled, Boolean helloTuneEnabled, Integer mobileNumberMaxLength, String flagUrl, Boolean callVerification, Boolean adEnabled, Boolean podcastEnabled) {
        this.countryCode = countryCode;
        this.isoCode = isoCode;
        this.countryName = countryName;
        this.pointPurchaseEnabled = pointPurchaseEnabled;
        this.helloTuneEnabled = helloTuneEnabled;
        this.mobileNumberMaxLength = mobileNumberMaxLength;
        this.flagUrl = flagUrl;
        this.callVerification = callVerification;
        this.adEnabled = adEnabled;
        this.podcastEnabled= podcastEnabled;
    }

    public Boolean getPointPurchaseEnabled() {
        return pointPurchaseEnabled;
    }

    public void setPointPurchaseEnabled(Boolean pointPurchaseEnabled) {
        this.pointPurchaseEnabled = pointPurchaseEnabled;
    }

    public String getFlagUrl() {
        return flagUrl;
    }

    public void setFlagUrl(String flagUrl) {
        this.flagUrl = flagUrl;
    }

    public Boolean getHelloTuneEnabled() {
        return helloTuneEnabled;
    }

    public void setHelloTuneEnabled(Boolean helloTuneEnabled) {
        this.helloTuneEnabled = helloTuneEnabled;
    }

    public Integer getMobileNumberMaxLength() {
        return mobileNumberMaxLength;
    }

    public void setMobileNumberMaxLength(Integer mobileNumberMaxLength) {
        this.mobileNumberMaxLength = mobileNumberMaxLength;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Boolean getCallVerification() {
        return callVerification;
    }

    public void setCallVerification(Boolean callVerification) {
        this.callVerification = callVerification;
    }

    public Boolean getAdEnabled() {
        return adEnabled;
    }

    public void setAdEnabled(Boolean adEnabled) {
        this.adEnabled = adEnabled;
    }

    public Boolean getPodcastEnabled() {
        return podcastEnabled;
    }

    public void setPodcastEnabled(Boolean podcastEnabled) {
        this.podcastEnabled = podcastEnabled;
    }

    public void fromJsonObject(JSONObject json) {
        if (json == null)
            return;
        if (json.get("countryCode") != null) {
            this.setCountryCode(String.valueOf(json.get(countryCode)));
        }
        if (json.get("isoCode") != null) {
            this.setIsoCode(String.valueOf(json.get("isoCode")));
        }
        if (json.get("countryName") != null) {
            this.setCountryName(String.valueOf(json.get("countryName")));
        }
        if (json.get("pointPurchaseEnabled") != null) {
            this.setPointPurchaseEnabled((Boolean) json.get("pointPurchaseEnabled"));
        }
        if (json.get("helloTuneEnabled") != null) {
            this.setHelloTuneEnabled((Boolean) json.get("helloTuneEnabled"));
        }
        if (json.get("mobileNumberMaxLength") != null) {
            this.mobileNumberMaxLength = (Integer) json.get("mobileNumberMaxLength");
        }
        if (json.get("flagUrl") != null) {
            this.setFlagUrl(String.valueOf(json.get("flagUrl")));
        }
        if (json.get("callVerification") != null) {
            this.setCallVerification((Boolean) json.get("callVerification"));
        }
        if (json.get("adEnabled") != null) {
            this.setAdEnabled((Boolean) json.get("adEnabled"));
        }
        if (json.get("podcastEnabled") != null) {
            this.setPodcastEnabled((Boolean) json.get("podcastEnabled"));
        }
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("countryCode", this.countryCode);
        jsonObject.put("isoCode", this.isoCode);
        jsonObject.put("countryName", this.countryName);
        jsonObject.put("pointPurchaseEnabled", this.pointPurchaseEnabled);
        jsonObject.put("helloTuneEnabled", this.helloTuneEnabled);
        jsonObject.put("mobileNumberMaxLength", this.mobileNumberMaxLength);
        jsonObject.put("flagUrl", this.flagUrl);
        jsonObject.put("callVerification", this.callVerification);
        jsonObject.put("adEnabled", this.adEnabled);
        jsonObject.put("podcastEnabled", this.podcastEnabled);
        return jsonObject;
    }
}
