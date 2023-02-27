package com.wynk.user.dto;

import com.wynk.music.constants.MusicContentType;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Created by bhuvangupta on 01/01/14.
 */
public class UserFavorite implements Comparable {
    private long timestamp;
    private String id;
    private MusicContentType type;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MusicContentType getType() {
        return type;
    }

    public void setType(MusicContentType type) {
        this.type = type;
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("id", getId());
        jsonObj.put("type", getType().name());
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
            setId((String) idobj);
        }
        if(jsonObj.get("type") != null)
            setType(MusicContentType.valueOf(((String)jsonObj.get("type")).toUpperCase()));

        if(jsonObj.get("timestamp") != null)
            setTimestamp(((Number)jsonObj.get("timestamp")).longValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserFavorite)) return false;

        UserFavorite that = (UserFavorite) o;

        if (!id.equals(that.id)) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public int compareTo(Object o) {
        if(!(o instanceof UserFavorite))
            return -1;

        if(getTimestamp() > ((UserFavorite)o).getTimestamp())
            return -1;
        if(getTimestamp() < ((UserFavorite)o).getTimestamp())
            return 1;

        return 0;
    }
}
