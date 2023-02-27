package com.wynk.dto;

import com.wynk.utils.ObjectUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Subscription {

    protected String id;

    protected String msisdn;

    protected int     productId;

    protected long    lastSubscribedTimestamp;

    protected boolean isSuspended;

    protected boolean isDeprovisioned;

    protected long    deprovisioningTimestamp;

    protected boolean autoRenewalOff;
    
    protected boolean isUnsubscribed;
    
    protected long    unsubscribeTimestamp;
    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int i) {
        this.productId = i;
    }

    public long getLastSubscribedTimestamp() {
        return lastSubscribedTimestamp;
    }

    public void setLastSubscribedTimestamp(long lastSubscribedTimestamp) {
        this.lastSubscribedTimestamp = lastSubscribedTimestamp;
    }

    public boolean isSuspended() {
        return isSuspended;
    }

    public void setSuspended(boolean isSuspended) {
        this.isSuspended = isSuspended;
    }

    public boolean isDeprovisioned() {
        return isDeprovisioned;
    }

    public void setDeprovisioned(boolean isDeprovisioned) {
        this.isDeprovisioned = isDeprovisioned;
    }

    public long getDeprovisioningTimestamp() {
        return deprovisioningTimestamp;
    }

    public void setDeprovisioningTimestamp(long deprovisioningTimestamp) {
        this.deprovisioningTimestamp = deprovisioningTimestamp;
    }

    public boolean isAutoRenewalOff() {
        return autoRenewalOff;
    }

    public void setAutoRenewalOff(boolean autoRenewalOff) {
        this.autoRenewalOff = autoRenewalOff;
    }

	public boolean isUnsubscribed() {
		return isUnsubscribed;
	}

	public void setUnsubscribed(boolean isUnsubscribed) {
		this.isUnsubscribed = isUnsubscribed;
	}

	public long getUnsubscribeTimestamp() {
		return unsubscribeTimestamp;
	}

	public void setUnsubscribeTimestamp(long unsubscribeTimestamp) {
		this.unsubscribeTimestamp = unsubscribeTimestamp;
	}
	
	public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }
    
    public void fromJsonObject(JSONObject jsonObj) {
     
        if (jsonObj.get("msisdn") != null) {
            setMsisdn((String) jsonObj.get("msisdn"));
        }
        if (jsonObj.get("productId") != null) {
            setProductId(ObjectUtils.getNumber(jsonObj.get("productId"), 0).intValue());
        }
        if (jsonObj.get("lastSubscribedTimestamp") != null) {
            setLastSubscribedTimestamp(ObjectUtils.getNumber(jsonObj.get("lastSubscribedTimestamp"), 0).longValue());
        }
        if (jsonObj.get("deprovisioningTimestamp") != null) {
            setDeprovisioningTimestamp(ObjectUtils.getNumber(jsonObj.get("deprovisioningTimestamp"), 0).longValue());
        }
        if (jsonObj.get("unsubscribeTimestamp") != null) {
            setUnsubscribeTimestamp(ObjectUtils.getNumber(jsonObj.get("unsubscribeTimestamp"), 0).longValue());
        }
        if (jsonObj.get("isSuspended") != null) {
            setSuspended(Boolean.valueOf(jsonObj.get("isSuspended").toString()));
        }
        if (jsonObj.get("isDeprovisioned") != null) {
            setDeprovisioned(Boolean.valueOf(jsonObj.get("isDeprovisioned").toString()));
        }
        if (jsonObj.get("autoRenewalOff") != null) {
            setAutoRenewalOff(Boolean.valueOf(jsonObj.get("autoRenewalOff").toString()));
        }
        if (jsonObj.get("isUnsubscribed") != null) {
            setUnsubscribed(Boolean.valueOf(jsonObj.get("isUnsubscribed").toString()));
        }
    }

}
