package com.wynk.dto;

import com.wynk.constants.SubscriptionChannelPhases;
import com.wynk.utils.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by Aakash on 05/09/17.
 */
public class AccountPaymentResponseDTO {

    private String url;
    private Boolean showOtpScreen;
    private Boolean openWebView;
    private Boolean openPopUp;
    private String title;
    private String subTitle;
    private String buttonColour;
    private String buttonText;
    private String titleColour;
    private String subTitleColour;
    private String httpMethod;
    private String paymentName;
    private String iconUrl;
    private String requestBodyMandatoryParameter;
    private String requestBodyOptionalParameter;
    private Boolean isNativePayment;
    private String pricePoint;
    private String billingText;
    private Boolean isAutoRenewal;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getShowOtpScreen() {
        return showOtpScreen;
    }

    public void setShowOtpScreen(Boolean showOtpScreen) {
        this.showOtpScreen = showOtpScreen;
    }

    public Boolean getOpenWebView() {
        return openWebView;
    }

    public void setOpenWebView(Boolean openWebView) {
        this.openWebView = openWebView;
    }

    public Boolean getOpenPopUp() {
        return openPopUp;
    }

    public void setOpenPopUp(Boolean openPopUp) {
        this.openPopUp = openPopUp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getButtonColour() {
        return buttonColour;
    }

    public void setButtonColour(String buttonColour) {
        this.buttonColour = buttonColour;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getTitleColour() {
        return titleColour;
    }

    public void setTitleColour(String titleColour) {
        this.titleColour = titleColour;
    }

    public String getSubTitleColour() {
        return subTitleColour;
    }

    public void setSubTitleColour(String subTitleColour) {
        this.subTitleColour = subTitleColour;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getRequestBodyMandatoryParameter() {
        return requestBodyMandatoryParameter;
    }

    public void setRequestBodyMandatoryParameter(String requestBodyMandatoryParameter) {
        this.requestBodyMandatoryParameter = requestBodyMandatoryParameter;
    }

    public Boolean getNativePayment() {
        return isNativePayment;
    }

    public void setNativePayment(Boolean nativePayment) {
        isNativePayment = nativePayment;
    }

    public String getRequestBodyOptionalParameter() {
        return requestBodyOptionalParameter;
    }

    public void setRequestBodyOptionalParameter(String requestBodyOptionalParameter) {
        this.requestBodyOptionalParameter = requestBodyOptionalParameter;
    }

    public String getPricePoint() {
        return pricePoint;
    }

    public void setPricePoint(String pricePoint) {
        this.pricePoint = pricePoint;
    }

    public String getBillingText() {
        return billingText;
    }

    public void setBillingText(String billingText) {
        this.billingText = billingText;
    }

    public Boolean getAutoRenewal() {
        return isAutoRenewal;
    }

    public void setAutoRenewal(Boolean autoRenewal) {
        isAutoRenewal = autoRenewal;
    }

    public JSONObject toJsonObject(){
        JSONObject jsonObject = new JSONObject();
        if(getUrl() != null){
            jsonObject.put("url",getUrl());
        }

        jsonObject.put("showOtpScreen",Boolean.FALSE);
        if(getShowOtpScreen() != null){
            jsonObject.put("showOtpScreen",getShowOtpScreen());
        }

        jsonObject.put("openWebView",Boolean.FALSE);
        if(getOpenWebView() != null){
            jsonObject.put("openWebView",getOpenWebView());
        }

        jsonObject.put("openPopUp",Boolean.FALSE);
        if(getOpenWebView() != null){
            jsonObject.put("openPopUp",getOpenPopUp());
        }

        if(StringUtils.isNotBlank(getTitle())){
            jsonObject.put("title",getTitle());
        }

        if(StringUtils.isNotBlank(getSubTitle())){
            jsonObject.put("subTitle",getSubTitle());
        }

        if(StringUtils.isNotBlank(getButtonColour())){
            jsonObject.put("buttonColour",getButtonColour());
        }

        if(StringUtils.isNotBlank(getButtonText())){
            jsonObject.put("buttonText",getButtonText());
        }

        if(StringUtils.isNotBlank(getTitleColour())){
            jsonObject.put("titleColour",getTitleColour());
        }

        if(StringUtils.isNotBlank(getSubTitleColour())){
            jsonObject.put("subTitleColour",getSubTitleColour());
        }

        if(StringUtils.isNotBlank(getHttpMethod())){
            jsonObject.put("httpMethod",getHttpMethod());
        }
        if(StringUtils.isNotBlank(getIconUrl())){
            jsonObject.put("iconUrl",getIconUrl());
        }
        if(StringUtils.isNotBlank(getPaymentName())){
            jsonObject.put("paymentName",getPaymentName());
        }
        if(StringUtils.isNotBlank(getRequestBodyMandatoryParameter())){
            String requestBodyParameter = getRequestBodyMandatoryParameter();
            JSONObject bodyJson = new JSONObject();

            if (StringUtils.isNotBlank(requestBodyParameter)) {
                String[] requestMandatoryBodyParameterArray = requestBodyParameter.split("\\|");
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < requestMandatoryBodyParameterArray.length; i++) {
                    jsonArray.add(requestMandatoryBodyParameterArray[i]);
                }
                if(jsonArray.size() > 0){
                    bodyJson.put("mandatory",jsonArray);
                }
            }

            requestBodyParameter = getRequestBodyOptionalParameter();

            if (StringUtils.isNotBlank(requestBodyParameter)) {
                String[] requestMandatoryBodyParameterArray = requestBodyParameter.split("\\|");
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < requestMandatoryBodyParameterArray.length; i++) {
                    jsonArray.add(requestMandatoryBodyParameterArray[i]);
                }
                if(jsonArray.size() > 0){
                    bodyJson.put("optional",jsonArray);
                }
            }

            if(bodyJson.size() > 0){
                jsonObject.put("parameters",bodyJson);
            }
        }
        if(getNativePayment() != null){
            jsonObject.put("isNativePayment",getNativePayment());
        }
        if(getPricePoint() != null){
            jsonObject.put("pricePoint",getPricePoint());
        }
        if(getBillingText() != null){
            jsonObject.put("billingText",getBillingText());
        }
        if(getAutoRenewal() != null){
            jsonObject.put("isAutoRenewal",getAutoRenewal());
        }
        return jsonObject;
    }
}
