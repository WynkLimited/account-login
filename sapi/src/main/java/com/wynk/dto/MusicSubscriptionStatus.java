package com.wynk.dto;

import com.wynk.utils.StringUtils;
import com.wynk.wcf.dto.UserSubscription;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class MusicSubscriptionStatus {

    private MusicSubscriptionState status;

    private int                    daysToExpire;

    private String purchaseUrl;
    
    private String changePlanUrl;

    private long                   expireTimestamp;

    private int                    price;

    private int                    packValidityInDays;

    private int                    songsLimit;

    private boolean                autoRenewalOn;

    private boolean                offerPackAvailed;

    private String message;

    private String notificationMessage;

    private String headerMessage;

    private String buttonText;

    private String statusMessage;

    private String statusMessageColor;

    private int                    productId;
    
    private JSONObject subscriptionInfo;
    
    boolean                        isUnsubscribed;

    private List<String>           info;

    private String                 buttonColour;

    private String                 buttonTextColour;
    
    private String 				subscriptionType;

    private UserSubscription userSubscription;

    private String redirectUrl;

    private String sid;

    private JSONObject subscription_intent;

    private int topOfferId;

    public int getTopOfferId() {
        return topOfferId;
    }

    public void setTopOfferId(int topOfferId) {
        this.topOfferId = topOfferId;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getRedirectUrl(){
        return redirectUrl;
    }

    public void setSubscription_intent(JSONObject subscription_intent) {
        this.subscription_intent = subscription_intent;
    }

    public JSONObject getSubscription_intent() {
        return subscription_intent;
    }

    // wynk 6 months free offer
//    boolean                musicOfferAvailed;
    
//    public boolean isMusicOfferAvailed() {
//        return musicOfferAvailed;
//    }
//    
//    public void setMusicOfferAvailed(boolean musicOfferAvailed) {
//        this.musicOfferAvailed = musicOfferAvailed;
//    }

    public UserSubscription getUserSubscription() {
        return userSubscription;
    }

    public void setUserSubscription(UserSubscription userSubscription) {
        this.userSubscription = userSubscription;
    }
    
    public boolean isUnsubscribed() {
        return isUnsubscribed;
    }

    public void setUnsubscribed(boolean isUnsubscribed) {
        this.isUnsubscribed = isUnsubscribed;
    }

    public String getStatusMessageColor() {
        return statusMessageColor;
    }

    public void setStatusMessageColor(String statusMessageColor) {
        this.statusMessageColor = statusMessageColor;
    }

    public MusicSubscriptionState getState() {
        return status;
    }

    public void setState(MusicSubscriptionState state) {
        this.status = state;
    }

    public int getDaysToExpire() {
        return daysToExpire;
    }

    public void setDaysToExpire(int daysToExpire) {
        this.daysToExpire = daysToExpire;
    }

    public String getPurchaseUrl() {
        return purchaseUrl;
    }

    public void setPurchaseUrl(String purchaseUrl) {
        this.purchaseUrl = purchaseUrl;
    }

    public long getExpireTimestamp() {
        return expireTimestamp;
    }

    public void setExpireTimestamp(long expireTimestamp) {
        this.expireTimestamp = expireTimestamp;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPackValidityInDays() {
        return packValidityInDays;
    }

    public void setPackValidityInDays(int packValidityInDays) {
        this.packValidityInDays = packValidityInDays;
    }

    public int getSongsLimit() {
        return songsLimit;
    }

    public void setSongsLimit(int songsLimit) {
        this.songsLimit = songsLimit;
    }

    public boolean isAutoRenewalOn() {
        return autoRenewalOn;
    }

    public void setAutoRenewalOn(boolean autoRenewalOn) {
        this.autoRenewalOn = autoRenewalOn;
    }

    public boolean isOfferPackAvailed() {
        return offerPackAvailed;
    }

    public void setOfferPackAvailed(boolean freePackAvailed) {
        this.offerPackAvailed = freePackAvailed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getHeaderMessage() {
        return headerMessage;
    }

    public void setHeaderMessage(String headerMessage) {
        this.headerMessage = headerMessage;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getChangePlanUrl() {
        return changePlanUrl;
    }
    
    public void setChangePlanUrl(String changePlanUrl) {
        this.changePlanUrl = changePlanUrl;
    }
    
    public JSONObject getSubscriptionInfo() {
        return subscriptionInfo;
    }
    
    public void setSubscriptionInfo(JSONObject subscriptionInfo) {
        this.subscriptionInfo = subscriptionInfo;
    }

    public List<String> getInfo() {
        return info;
    }

    public void setInfo(List<String> info) {
        this.info = info;
    }

    public String getButtonColour() {
        return buttonColour;
    }

    public void setButtonColour(String buttonColour) {
        this.buttonColour = buttonColour;
    }

    public String getButtonTextColour() {
        return buttonTextColour;
    }

    public void setButtonTextColour(String buttonTextColour) {
        this.buttonTextColour = buttonTextColour;
    }

	public String getSubscriptionType() {
		return subscriptionType;
	}

	public void setSubscriptionType(String subscriptionType) {
		this.subscriptionType = subscriptionType;
	}


	public JSONObject toJsonObject(){
        JSONObject jsonObject = new JSONObject();

        if(getState() != null){
            jsonObject.put("status",getState().toString());
        }
        jsonObject.put("daysToExpire",getDaysToExpire());

        if(getPurchaseUrl() !=null){
            jsonObject.put("purchaseUrl",getPurchaseUrl());
        }
        jsonObject.put("expireTimestamp",getExpireTimestamp());
        jsonObject.put("price",getPrice());
        jsonObject.put("packValidityInDays",getPackValidityInDays());
        jsonObject.put("songsLimit",getSongsLimit());
        jsonObject.put("autoRenewalOn",isAutoRenewalOn());
        jsonObject.put("offerPackAvailed",isOfferPackAvailed());
        if(StringUtils.isNotBlank(getHeaderMessage())) {
            jsonObject.put("headerMessage", getHeaderMessage());
        }
        if(StringUtils.isNotBlank(getStatusMessage())){
            jsonObject.put("statusMessage",getStatusMessage());
        }
        if(StringUtils.isNotBlank(getStatusMessageColor())){
            jsonObject.put("statusMessageColor",getStatusMessageColor());
        }
        jsonObject.put("productId",getProductId());
        jsonObject.put("isUnsubscribed",isUnsubscribed());
        if(StringUtils.isNotBlank(getSubscriptionType())) {
            jsonObject.put("subscriptionType", getSubscriptionType());
        }
        if(!CollectionUtils.isEmpty(getInfo())){
            JSONArray jsonArray = new JSONArray();
            for(String info : getInfo()){
                jsonArray.add(info);
            }
            jsonObject.put("info",jsonArray);
        }
        if (getRedirectUrl() != null){
            jsonObject.put("redirectUrl", getRedirectUrl());
        }
        if (getSid() != null){
            jsonObject.put("sid", getSid());
        }
        if (getSubscription_intent() != null){
            jsonObject.put("subscription_intent", getSubscription_intent());
        }
        return jsonObject;
    }

}
