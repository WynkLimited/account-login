package com.wynk.user.dto;

import com.wynk.utils.MusicUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 */
public class UserPurchase implements Comparable {
    private String contentId;
    private String sourceId;
    private String contentType;
    private String price;
    private long timestamp;
    private boolean deleted;
    private int downloadCount = 0;
    private long lastDownloadTime;

    public UserPurchase() {
        super();
    }

    public UserPurchase(String contentId, String sourceId, String contentType, String price, long timestamp,
            int downloadCount) {
        this.contentId = contentId;
        this.sourceId = sourceId;
        this.contentType = contentType;
        this.price = price;
        this.timestamp = timestamp;
        this.downloadCount = downloadCount;
    }


    public String getId() {
        return contentId;
    }

    public void setContentId(String id) {
        this.contentId = id;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public long getLastDownloadTime() {
        return lastDownloadTime;
    }

    public void setLastDownloadTime(long lastDownloadTime) {
        this.lastDownloadTime = lastDownloadTime;
    }

    public void updateDownloadStatus(boolean status)
    {
        if(status)
        {
            setLastDownloadTime( System.currentTimeMillis());
            setDownloadCount((getDownloadCount()+1));
        }
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("id", getId());
        if(!StringUtils.isEmpty(getSourceId()))
            jsonObj.put("sourceId", getSourceId());
        if(!StringUtils.isEmpty(getContentType()))
            jsonObj.put("contentType", getContentType());
        if(!StringUtils.isEmpty(getPrice()))
            jsonObj.put("price", getPrice());
        jsonObj.put("timestamp", getTimestamp());
        jsonObj.put("deleted", isDeleted());
        jsonObj.put("downloadCount", getDownloadCount());
        if(getLastDownloadTime() > 0)
            jsonObj.put("lastDownloadTime", getLastDownloadTime());

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
        if(jsonObj.get("sourceId") != null)
            setSourceId((String)jsonObj.get("sourceId") );
        if(jsonObj.get("contentType") != null)
            setContentType((String) jsonObj.get("contentType"));
        if(jsonObj.get("price") != null)
            setPrice((String) jsonObj.get("price"));
        if(jsonObj.get("timestamp") != null)
        {
            long timestamp1 = ((Number) jsonObj.get("timestamp")).longValue();
            setTimestamp(MusicUtils.getTimestampInMS(timestamp1));
        }
        if(jsonObj.get("deleted") != null)
            setDeleted((Boolean) jsonObj.get("deleted"));
        if(jsonObj.get("downloadCount") != null)
            setDownloadCount(((Number) jsonObj.get("downloadCount")).intValue());
        if(jsonObj.get("lastDownloadTime") != null)
            setTimestamp(((Number) jsonObj.get("lastDownloadTime")).longValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPurchase)) return false;

        UserPurchase that = (UserPurchase) o;

        if (!contentId.equals(that.contentId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = contentId.hashCode();
        return result;
    }

    @Override
    public int compareTo(Object o) {
        if(!(o instanceof UserPurchase))
            return -1;

        if(getTimestamp() > ((UserPurchase)o).getTimestamp())
            return -1;
        if(getTimestamp() < ((UserPurchase)o).getTimestamp())
            return 1;

        return 0;
    }

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
