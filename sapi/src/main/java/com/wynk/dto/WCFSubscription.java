package com.wynk.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wynk.user.dto.User;
import com.wynk.user.dto.UserEntityKey;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aakashkumar on 21/11/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WCFSubscription {
    private static final Logger logger = LoggerFactory.getLogger(WCFSubscription.class.getCanonicalName());
    private Long expireTS;
    private Long offerTS;
    private Integer productId;
    private List<Integer> recommendedProductId;
    private List<Integer> eligibleOfferProductId;
    private Boolean isSubscribed;
    private Long lastUpdatedTS;
    private Boolean renewalConsent;

    public Boolean getRenewalConsent() {
        return renewalConsent;
    }

    public void setRenewalConsent(Boolean renewalConsent) {
        this.renewalConsent = renewalConsent;
    }

    public Long getExpireTS() {
        return expireTS;
    }

    public void setExpireTS(Long expireTS) {
        this.expireTS = expireTS;
    }


    public Long getOfferTS() {
        return offerTS;
    }

    public void setOfferTS(Long offerTS) {
        this.offerTS = offerTS;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Boolean getSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(Boolean subscribed) {
        isSubscribed = subscribed;
    }

    public List<Integer> getRecommendedProductId() {
        return recommendedProductId;
    }

    public void setRecommendedProductId(List<Integer> recommendedProductId) {
        this.recommendedProductId = recommendedProductId;
    }

    public List<Integer> getEligibleOfferProductId() {
        return eligibleOfferProductId;
    }

    public void setEligibleOfferProductId(List<Integer> eligibleOfferProductId) {
        this.eligibleOfferProductId = eligibleOfferProductId;
    }

    public Long getLastUpdatedTS() {
        return lastUpdatedTS;
    }

    public void setLastUpdatedTS(Long lastUpdatedTS) {
        this.lastUpdatedTS = lastUpdatedTS;
    }

    public void fromJson(String json) throws Exception {
        try {
            JsonElement jsonElement = new JsonParser().parse(json);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            fromJsonObject(jsonObject);
        } catch (Exception ex) {
            logger.error("Error parsing subscription json",ex.getMessage(),ex);
        }
    }

    private void fromJsonObject(JsonObject jsonObj) {

        if (jsonObj.get(UserEntityKey.WCFSubcription.expireTS)!=null && !jsonObj.get(UserEntityKey.WCFSubcription.expireTS).isJsonNull()) {
            setExpireTS(jsonObj.get(UserEntityKey.WCFSubcription.expireTS).getAsLong());
        }
        if (jsonObj.get(UserEntityKey.WCFSubcription.productId)!=null && !jsonObj.get(UserEntityKey.WCFSubcription.productId).isJsonNull()) {
            setProductId(jsonObj.get(UserEntityKey.WCFSubcription.productId).getAsInt());
        }
        if (jsonObj.get(UserEntityKey.WCFSubcription.offerTS)!=null && !jsonObj.get(UserEntityKey.WCFSubcription.offerTS).isJsonNull()) {
            setOfferTS(jsonObj.get(UserEntityKey.WCFSubcription.offerTS).getAsLong());
        }
        if (jsonObj.get(UserEntityKey.WCFSubcription.isSubscribed)!=null && !jsonObj.get(UserEntityKey.WCFSubcription.isSubscribed).isJsonNull()) {
            setSubscribed(jsonObj.get(UserEntityKey.WCFSubcription.isSubscribed).getAsBoolean());
        }
        if (jsonObj.get(UserEntityKey.WCFSubcription.renewalConsent)!=null && !jsonObj.get(UserEntityKey.WCFSubcription.renewalConsent).isJsonNull()) {
            setRenewalConsent(jsonObj.get(UserEntityKey.WCFSubcription.renewalConsent).getAsBoolean());
        }
        if (jsonObj.get(UserEntityKey.WCFSubcription.recommendedProductId)!=null && !jsonObj.get(UserEntityKey.WCFSubcription.recommendedProductId).isJsonNull()) {
            JsonArray arrayObj = null;
            JsonParser jsonParser = new JsonParser();
            arrayObj = (JsonArray) jsonParser.parse(jsonObj.get(UserEntityKey.WCFSubcription.recommendedProductId).toString());

            List<Integer> productIdList = new ArrayList<Integer>();
            for (int i = 0; i < arrayObj.size(); i++) {
                Integer productId = arrayObj.get(i).getAsInt();
                productIdList.add(productId);
            }
            setRecommendedProductId(productIdList);

        }
        if (jsonObj.get(UserEntityKey.WCFSubcription.eligibleOfferProductId)!=null && !jsonObj.get(UserEntityKey.WCFSubcription.eligibleOfferProductId).isJsonNull()) {
            JsonArray arrayObj = null;
            JsonParser jsonParser = new JsonParser();
            arrayObj = (JsonArray) jsonParser.parse(jsonObj.get(UserEntityKey.WCFSubcription.eligibleOfferProductId).toString());

            List<Integer> productIdList = new ArrayList<Integer>();
            for (int i = 0; i < arrayObj.size(); i++) {
                Integer productId = arrayObj.get(i).getAsInt();
                productIdList.add(productId);
            }
            setEligibleOfferProductId(productIdList);
        }
        if (jsonObj.get(UserEntityKey.WCFSubcription.lastUpdatedTS)!=null && !jsonObj.get(UserEntityKey.WCFSubcription.lastUpdatedTS).isJsonNull()) {
            setLastUpdatedTS(jsonObj.get(UserEntityKey.WCFSubcription.lastUpdatedTS).getAsLong());
        }
    }

    public Boolean isNotEqual(WCFSubscription wcfSubscription) {
        if (wcfSubscription == null) {
            return true;
        }

        if ((productId != null && wcfSubscription.getProductId() == null) || (productId == null && wcfSubscription.getProductId() != null)
                || !productId.equals(wcfSubscription.getProductId())) {
            return true;
        }

        if ((expireTS != null && wcfSubscription.getExpireTS() == null) || (expireTS == null && wcfSubscription.getExpireTS() != null) || !expireTS.equals(wcfSubscription.getExpireTS())) {
            return true;
        }

        if ((isSubscribed != null && wcfSubscription.getSubscribed() == null) || (isSubscribed == null && wcfSubscription.getSubscribed() != null) || !isSubscribed.equals(wcfSubscription.getSubscribed())) {
            return true;
        }

        Integer newEligOfferProdSize = wcfSubscription.getEligibleOfferProductId() != null ? wcfSubscription.getEligibleOfferProductId().size() : 0;
        Integer oldEliOfferProdSize = eligibleOfferProductId != null ? eligibleOfferProductId.size() : 0;

        if ((newEligOfferProdSize != oldEliOfferProdSize) ||
                (wcfSubscription.getEligibleOfferProductId() != null && eligibleOfferProductId != null && !eligibleOfferProductId.containsAll(wcfSubscription.getEligibleOfferProductId()))) {
            return true;
        }

        Integer newRecomProdSize = wcfSubscription.getRecommendedProductId() != null ? wcfSubscription.getRecommendedProductId().size() : 0;
        Integer oldRecomProdSize = recommendedProductId != null ? recommendedProductId.size() : 0;


        if ((newRecomProdSize != oldRecomProdSize) ||
                (wcfSubscription.getRecommendedProductId() != null && recommendedProductId != null && !recommendedProductId.containsAll(wcfSubscription.getRecommendedProductId()))) {
            return true;
        }

        return false;
    }

    public String toJson() {
        JSONObject jsonObject = toJsonObject();
        return jsonObject.toString();
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put(UserEntityKey.WCFSubcription.expireTS, getExpireTS());
        jsonObj.put(UserEntityKey.WCFSubcription.offerTS, getOfferTS());
        jsonObj.put(UserEntityKey.WCFSubcription.productId, getProductId());
        jsonObj.put(UserEntityKey.WCFSubcription.recommendedProductId, getRecommendedProductId());
        jsonObj.put(UserEntityKey.WCFSubcription.eligibleOfferProductId, getEligibleOfferProductId());
        jsonObj.put(UserEntityKey.WCFSubcription.isSubscribed, getSubscribed());
        jsonObj.put(UserEntityKey.WCFSubcription.lastUpdatedTS, getLastUpdatedTS());
        jsonObj.put(UserEntityKey.WCFSubcription.renewalConsent,getRenewalConsent());
        return jsonObj;
    }

    @Override
    public String toString() {
        return "WCFSubscription{" +
                "expireTS=" + expireTS +
                ", offerTS=" + offerTS +
                ", productId=" + productId +
                ", recommendedProductId=" + recommendedProductId +
                ", eligibleOfferProductId=" + eligibleOfferProductId +
                ", isSubscribed=" + isSubscribed +
                ", lastUpdatedTS=" + lastUpdatedTS +
                ", renewalConsent=" + renewalConsent +
                '}';
    }
}
