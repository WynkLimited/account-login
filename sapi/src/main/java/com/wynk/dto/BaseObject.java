package com.wynk.dto;

import org.json.simple.JSONObject;

/**
 * Created with IntelliJ IDEA. User: bhuvangupta Date: 20/09/12 Time: 11:01 PM To change this
 * template use File | Settings | File Templates.
 */
public abstract class BaseObject {

    // byte[] byts = Base64.decodeBase64(redisIdOrUID);
    // query.put("_id", new ObjectId(byts));

    private String oid;
    private boolean isDeleted;
    private String createUserId;
    private String modifiedUserId;
    private long    creationDate;
    private long    lastUpdated;
    
    public String getId() {
        return oid;
    }

    public void setId(String id) {
        oid = id;
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("_id", getId());
        jsonObj.put("deleted", isDeleted());
        if(getCreationDate() > 0)
            jsonObj.put("cdate", getCreationDate());
        if(getLastUpdated() > 0)
            jsonObj.put("lastUpdated", getLastUpdated());
        if(getCreateUserId() != null)
            jsonObj.put("createuserid", getCreateUserId());
        if(getModifiedUserId() != null)
            jsonObj.put("modifieduserid", getModifiedUserId());

        return jsonObj;
    }

    public void fromJsonObject(JSONObject jsonObj) {
        Object idobj = jsonObj.get("_id");
        if(idobj instanceof String) {
            setId((String) idobj);
        }
        else if(idobj instanceof JSONObject) {
            setId((String) ((JSONObject) jsonObj.get("_id")).get("$oid"));
        }
        if(jsonObj.get("deleted") != null) {
            setDeleted((Boolean) jsonObj.get("deleted"));
        }
        if(jsonObj.get("cdate") != null) {
            long time = (Long) jsonObj.get("cdate");
            setCreationDate(time);
        }
        if(jsonObj.get("lastUpdated") != null) {
            long time = (Long) jsonObj.get("lastUpdated");
            setLastUpdated(time);
        }
        setCreateUserId((String) jsonObj.get("createuserid"));
        setModifiedUserId((String) jsonObj.get("modifieduserid"));
    }

    public abstract String toJson() throws Exception;

    public abstract void fromJson(String json) throws Exception;

    @Override
    public String toString() {
        try {
            return getClass().getName() + ":" + toJson();
        }
        catch (Exception e) {
            e.printStackTrace();
            return super.toString();
        }
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getModifiedUserId() {
        return modifiedUserId;
    }

    public void setModifiedUserId(String modifiedUserId) {
        this.modifiedUserId = modifiedUserId;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}
