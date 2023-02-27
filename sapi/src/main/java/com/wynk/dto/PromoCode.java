package com.wynk.dto;

import com.wynk.constants.MusicSubscriptionPackConstants;
import com.wynk.utils.ObjectUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PromoCode {
    String id;
    long startDate;
    int promoCodeValidity;
    int productId;
    boolean isUsed;
    String msisdn;
    String source;
    int maxUseCount = 1;
    int currentUseCount = 0;
    List<String> msisdns;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    
    public String toJson() {
        JSONObject jsonObject = toJsonObject();
        return jsonObject.toString();
    }
    
    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("id", getId());
        jsonObj.put("startDate", getStartDate());
        jsonObj.put("productId", getProductId());
        jsonObj.put("isUsed", isUsed());
        jsonObj.put("promoCodeValidity", getPromoCodeValidity());
        jsonObj.put("msisdn", getMsisdn());
        
        JSONArray msisdnArray = new JSONArray();
        if(getMsisdns() != null) {
            for(int i = 0; i < getMsisdns().size(); i++) {
                String msisdn = getMsisdns().get(i);
                msisdnArray.add(msisdn);
            }
        }
        jsonObj.put("msisdns", msisdnArray);
        jsonObj.put("source", getSource());
        jsonObj.put("currentUseCount", getCurrentUseCount());
        jsonObj.put("maxUseCount", getMaxUseCount());
        return jsonObj;
    }
    
 

    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }
    
    public void fromJsonObject(JSONObject jsonObj) {
        if(jsonObj.get("id") != null) {
            setId((String)jsonObj.get("id"));
        }
        if(jsonObj.get("startDate") != null) {
            setStartDate((long)jsonObj.get("startDate"));
        }
        if(jsonObj.get("productId") != null) {
            setProductId(ObjectUtils.getNumber(jsonObj.get("productId"), 0).intValue());
        }
        if(jsonObj.get("isUsed") != null) {
            setUsed((Boolean)jsonObj.get("isUsed"));
        }
        if(jsonObj.get("promoCodeValidity") != null) {
            setPromoCodeValidity(ObjectUtils.getNumber(jsonObj.get("promoCodeValidity"), 0).intValue());
        }
        if(jsonObj.get("msisdn") != null) {
            setMsisdn((String)jsonObj.get("msisdn"));
        }
        
        if(jsonObj.get("msisdns") != null) {
            JSONArray msisdnArray = (JSONArray) jsonObj.get("msisdns");
            List<String> msisdns = new ArrayList<>();
            if(msisdnArray != null) {
                for(int k = 0; k < msisdnArray.size(); k++) {
                    String msisdnObj = (String) msisdnArray.get(k);
                    msisdns.add((String)msisdnObj);
                }
                setMsisdns(msisdns);
            }
        }
        if(jsonObj.get("source") != null) {
            setSource((String)jsonObj.get("source"));
        }
        if(jsonObj.get("maxUseCount") != null) {
            setMaxUseCount(ObjectUtils.getNumber(jsonObj.get("maxUseCount"), 1).intValue());
        }
        if(jsonObj.get("currentUseCount") != null) {
            setCurrentUseCount(ObjectUtils.getNumber(jsonObj.get("currentUseCount"), 0).intValue());
        }
        
    }

    public boolean isUsed() {
        return isUsed;
    }
    
    public void setUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public long getStartDate() {
        return startDate;
    }
    
    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }
    
    public int getPromoCodeValidity() {
        return promoCodeValidity;
    }

    public void setPromoCodeValidity(int promoCodeValidity) {
        this.promoCodeValidity = promoCodeValidity;
    }

    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public int getMaxUseCount() {
    	return maxUseCount;
    }
    
    public void setMaxUseCount(int maxUseCount) {
    	this.maxUseCount = maxUseCount;
    }
    
    public int getCurrentUseCount() {
    	return currentUseCount;
    }
    
    public void setCurrentUseCount(int currentUseCount) {
    	this.currentUseCount = currentUseCount;
    }
    
    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }
    public List<String> getMsisdns() {
        return msisdns;
    }

    public void setMsisdns(List<String> msisdns) {
        this.msisdns = msisdns;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public void addMsisdn(String msisdn) {
    	if (msisdns == null)
    		msisdns = new ArrayList<String>();
    	msisdns.add(msisdn);
    	setMsisdns(msisdns);
    }
    
    @Override
    public String toString() {
        String plan = "Wynk Plus";
        switch(productId) {
            case MusicSubscriptionPackConstants.PROMO_CODE_MUSIC_PACK_ID_29 : plan = "Wynk Plus"; break;
            case MusicSubscriptionPackConstants.PROMO_CODE_MUSIC_PACK_ID_129 : plan = "Wynk Freedom"; break;
        }
        
        return id + "," +  getFormattedStartDate() + "," + getFormattedEndDate() + "," + plan;
    }
    
    public String getFormattedStartDate() {
        return formatter.format(getStartDate());
    }
    
    public String getFormattedEndDate() {
     
        long finalDate = getStartDate() + ((long)getPromoCodeValidity()*24*60*60*1000);
        return formatter.format(finalDate);
    }
}
