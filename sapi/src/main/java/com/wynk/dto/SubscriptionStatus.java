package com.wynk.dto;

import com.wynk.utils.ObjectUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionStatus {

    MusicSubscriptionState state;
    long                   expireTimestamp;
    boolean                autoRenewalOn;
    boolean                offerPackAvailed;
    
    // wynk 6 months free offer
//    boolean                musicOfferAvailed;
 
    int                    productId;
    boolean                isUnsubscribed;
    private Long bundlePackLastSubscriptionDate;
    private Long lastSmsSentTimestamp;

    ArrayList<SubscriptionStatusChangeEvent> subStatusChangeJourney;

    public MusicSubscriptionState getState() {
        return state;
    }
    
//    public boolean isMusicOfferAvailed() {
//        return musicOfferAvailed;
//    }
//    
//    public void setMusicOfferAvailed(boolean musicOfferAvailed) {
//        this.musicOfferAvailed = musicOfferAvailed;
//    }

    public void setState(MusicSubscriptionState state) {
        this.state = state;
    }

    public long getExpireTimestamp() {
        return expireTimestamp;
    }

    public void setExpireTimestamp(long expiryTimestamp) {
        this.expireTimestamp = expiryTimestamp;
    }

    public boolean isAutoRenewalOn() {
        return autoRenewalOn;
    }

    public void setAutoRenewalOn(boolean autoRenew) {
        this.autoRenewalOn = autoRenew;
    }

    public boolean isOfferPackAvailed() {
        return offerPackAvailed;
    }

    public void setOfferPackAvailed(boolean offerPackAvailed) {
        this.offerPackAvailed = offerPackAvailed;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public boolean isUnsubscribed() {
        return isUnsubscribed;
    }

    public void setUnsubscribed(boolean isUnsubscribed) {
        this.isUnsubscribed = isUnsubscribed;
    }
    
    public Long getBundlePackLastSubscriptionDate() {
        return bundlePackLastSubscriptionDate;
    }
    
    public void setBundlePackLastSubscriptionDate(Long bundlePackLastSubscriptionDate) {
        this.bundlePackLastSubscriptionDate = bundlePackLastSubscriptionDate;
    }

    public Long getLastSmsSentTimestamp() {
        return lastSmsSentTimestamp;
    }
    
    public void setLastSmsSentTimestamp(Long lastSmsSentTimestamp) {
        this.lastSmsSentTimestamp = lastSmsSentTimestamp;
    }
    
    public String toJson() {
        JSONObject jsonObject = toJsonObject();
        return jsonObject.toString();
    }
    
    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        if(getState() != null){
            jsonObj.put("state", getState().name());
        }
        jsonObj.put("expireTimestamp", getExpireTimestamp());
        jsonObj.put("autoRenewal", isAutoRenewalOn());
        jsonObj.put("productId", getProductId());
        jsonObj.put("isUnsubscribed", isUnsubscribed());
        jsonObj.put("offerPackAvailed", isOfferPackAvailed());
//        jsonObj.put("musicOfferAvailed", isMusicOfferAvailed());
        if(getBundlePackLastSubscriptionDate() != null)
            jsonObj.put("bundlePackLastSubscriptionDate", getBundlePackLastSubscriptionDate());
        
        if(getLastSmsSentTimestamp() != null)
            jsonObj.put("lastSmsSentTimestamp", getLastSmsSentTimestamp());
        
        if (getSubStatusChangeJourney() != null) {
            // add state change journey array to the dto
            JSONArray subStatusChangeJourney = new JSONArray();
            List<SubscriptionStatusChangeEvent> journey = getSubStatusChangeJourney();
            if (journey.size() > 5)
            	journey =  journey.subList(journey.size() - 5, journey.size());
            
            for(SubscriptionStatusChangeEvent event: journey) {
                subStatusChangeJourney.add(event.toJsonObject());
            }
            jsonObj.put("subStatusChangeJourney", subStatusChangeJourney);
        }
        
        return jsonObj;
    }
    
    public ArrayList<SubscriptionStatusChangeEvent> getSubStatusChangeJourney() {
        return subStatusChangeJourney;
    }
    
    public void setSubStatusChangeJourney(ArrayList<SubscriptionStatusChangeEvent> subStatusChangeJourney) {
        this.subStatusChangeJourney = subStatusChangeJourney;
    }

    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }
    
    public void fromJsonObject(JSONObject jsonObj) {
        if(jsonObj.get("state") != null) {
            setState(MusicSubscriptionState.valueOf((String)jsonObj.get("state")));
        }
        if(jsonObj.get("offerPackAvailed") != null) {
            setOfferPackAvailed((Boolean)jsonObj.get("offerPackAvailed"));
        }
//        if(jsonObj.get("musicOfferAvailed") != null) {
//            setMusicOfferAvailed((Boolean)jsonObj.get("musicOfferAvailed"));
//        }
        if(jsonObj.get("isUnsubscribed") != null) {
            setUnsubscribed((Boolean)jsonObj.get("isUnsubscribed"));
        }
        if(jsonObj.get("autoRenewal") != null) {
            setAutoRenewalOn((Boolean)jsonObj.get("autoRenewal"));
        }
        if(jsonObj.get("productId") != null) {
            setProductId(ObjectUtils.getNumber(jsonObj.get("productId"), 0).intValue());
        }
        if(jsonObj.get("expireTimestamp") != null) {
            setExpireTimestamp(ObjectUtils.getNumber(jsonObj.get("expireTimestamp"), 0).longValue());
        }
        if(jsonObj.get("bundlePackLastSubscriptionDate") != null) {
            setBundlePackLastSubscriptionDate(ObjectUtils.getNumber(jsonObj.get("bundlePackLastSubscriptionDate"), 0).longValue());
        }
        if(jsonObj.get("lastSmsSentTimestamp") != null) {
            setLastSmsSentTimestamp(ObjectUtils.getNumber(jsonObj.get("lastSmsSentTimestamp"), 0).longValue());
        }
    
        if (jsonObj.get ("subStatusChangeJourney") != null) {
            JSONArray array = (JSONArray)jsonObj.get("subStatusChangeJourney");
            ArrayList<SubscriptionStatusChangeEvent> statusChangeEvents = new ArrayList<SubscriptionStatusChangeEvent>();
            
            if (array != null) {
                for (int i=0; i < array.size(); i++) {
                    JSONObject obj = (JSONObject) array.get(i);
                    SubscriptionStatusChangeEvent event = new SubscriptionStatusChangeEvent();
                    event.fromJsonObject(obj);
                    statusChangeEvents.add(event);
                }
            }
            setSubStatusChangeJourney(statusChangeEvents);
        }
    }
    
    
    public Long getExpireTimestampFromJourney(long timestamp) {
        if (getSubStatusChangeJourney() != null) {
            
            // get the index of the journey where timestamp is greater than the time when the state changed.
            int count = getSubStatusChangeJourney().size() - 1;
            
            for(int i = getSubStatusChangeJourney().size() -1 ; i >=0 ; i--) {
                if (timestamp < getSubStatusChangeJourney().get(i).getCreateTimestamp()) { 
                    count--;
                } else {
                    break;
                }
            }
            if (count == (getSubStatusChangeJourney().size() - 1)) {
                return null;
            } else
                return getSubStatusChangeJourney().get(count + 1).getExpireTimestamp();
        }
        return null;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        SubscriptionStatus other = (SubscriptionStatus) obj;
        
        if(autoRenewalOn != other.autoRenewalOn)
            return false;
        if(isUnsubscribed != other.isUnsubscribed)
            return false;
        if(offerPackAvailed != other.offerPackAvailed)
            return false;
        if(productId != other.productId)
            return false;
        if(state != other.state)
            return false;
        if (Math.abs(expireTimestamp - other.getExpireTimestamp()) > 24*60*60*1000)
            return false;
        return true;
    }

}
