package com.wynk.user.dto;

import com.wynk.utils.MusicUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Ankit Srivastava
 */
public class UserRental implements Comparable {

    private String contentId;
    private long   timestamp;

    public UserRental() {
        super();
    }

    public UserRental(String contentId, long timestamp) {
        this.contentId = contentId;
        this.timestamp = timestamp;
    }

    public String getId() {
        return contentId;
    }

    public void setContentId(String id) {
        this.contentId = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("id", getId());
        jsonObj.put("timestamp", getTimestamp());
        return jsonObj;
    }

    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }

    public void fromJsonObject(JSONObject jsonObj) {
        Object idobj = jsonObj.get("id");
        if(idobj instanceof String) {
            setContentId((String) idobj);
        }
        if(jsonObj.get("timestamp") != null) {
            long timestamp1 = ((Number) jsonObj.get("timestamp")).longValue();
            setTimestamp(MusicUtils.getTimestampInMS(timestamp1));
        }
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(!(o instanceof UserRental))
            return false;

        UserRental that = (UserRental) o;
        if(!contentId.equals(that.contentId))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        if(StringUtils.isNotBlank(contentId))
            return contentId.hashCode();
        else
            return Long.hashCode(timestamp);
    }

    @Override
    public int compareTo(Object o) {
        if(!(o instanceof UserRental))
            return -1;

        if(getTimestamp() > ((UserRental) o).getTimestamp())
            return -1;
        if(getTimestamp() < ((UserRental) o).getTimestamp())
            return 1;

        return 0;
    }
}
