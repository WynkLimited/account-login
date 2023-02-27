package com.wynk.user.dto;

import com.wynk.music.constants.MusicContentType;
import com.wynk.utils.*;
import com.google.gson.Gson;
import com.wynk.dto.BaseObject;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.wynk.constants.JsonKeyNames.*;

/**
 * User: bhuvangupta
 * Date: 25/11/13
 */
public class UserPlaylist extends BaseObject implements Comparable {
    private String playlistId;
    private String ownerId;
    private int          type; //music, video
    private String title;
    private String thumbnailUrl;
    //set containing the id of the tracks.
    private LinkedHashSet<String> tracks;
    private Map<String,Integer> unmatchedSongCountMap;
    private long         lastPlayTime;
    private int          privacyStatus = 0;
    private int          onlineSongCount = 0;

    //id of the album/package from which this playlist is created.
    //this is used only when autoCreated is true;
    private String referenceId;
    public String referenceType;
    private boolean      autoCreated = false;


    public String getReferenceId() {
        return referenceId;
    }
    
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public boolean isAutoCreated() {
        return autoCreated;
    }

    public void setAutoCreated(boolean autoCreated) {
        this.autoCreated = autoCreated;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }


    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public int getPrivacyStatus() {
        return privacyStatus;
    }

    public void setPrivacyStatus(int privacyStatus) {
        this.privacyStatus = privacyStatus;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOnlineSongCount() {
        return onlineSongCount;
    }

    public void setOnlineSongCount(int onlineSongCount) {
        this.onlineSongCount = onlineSongCount;
    }

    public String getLowerCaseTitle() {
        if(title == null)
            return title;
        return title.toLowerCase();
    }

    public void setLowerCaseTitle(String lowerCaseTitle) {
        //not implemented
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public LinkedHashSet<String> getTracks() {
        return tracks;
    }

    public void setTracks(LinkedHashSet<String> tracks) {
        this.tracks = tracks;
    }

    public Map<String, Integer> getUnmatchedSongCountMap() {
        if(unmatchedSongCountMap != null)
            return unmatchedSongCountMap;
        else
            return  new HashMap<String, Integer>();
    }

    public void setUnmatchedSongCountMap(Map<String, Integer> unmatchedSongCountMap) {
        this.unmatchedSongCountMap = unmatchedSongCountMap;
    }

    public boolean addTrack(String trackId)
    {
        if(tracks == null)
            tracks = new LinkedHashSet<>();
        return tracks.add(trackId);
    }

    public boolean addTracks(List<String> trackIds)
    {
        if(trackIds == null || trackIds.size() == 0)
            return false;

        if(tracks == null)
            tracks = new LinkedHashSet<>();
        else
        {
            //add new at the end of the list
            for (Iterator<String> tks = tracks.iterator(); tks.hasNext(); ) {
                String tid = tks.next();
                if(trackIds.contains(tid))
                    tks.remove();
            }
        }

        return tracks.addAll(trackIds);
    }

    public boolean removeTrack(String trackId)
    {
        if(tracks == null)
            return false;
        return tracks.remove(trackId);
    }

    public boolean removeTracks(List<String> trackIds)
    {
        if(trackIds == null || trackIds.size() == 0)
            return false;
        return tracks.removeAll(trackIds);
    }

    public long getLastPlayTime() {
        return lastPlayTime;
    }

    public void setLastPlayTime(long lastPlayTime) {
        this.lastPlayTime = lastPlayTime;
    }

    @Override
    public String toJson()
            throws Exception {
        return toJsonObject().toJSONString();
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObj = super.toJsonObject();
        jsonObj.put(ID, getPlaylistId());
        jsonObj.put(THUMBNAIL, getThumbnailUrl());
        jsonObj.put(TITLE, getTitle());
        jsonObj.put("ltitle", getLowerCaseTitle());
        //jsonObj.put(TYPE, getType());
        jsonObj.put(TYPE, MusicContentType.USERPLAYLIST.name());
        jsonObj.put(OWNERID, getOwnerId());
        if(getTracks() != null)
            jsonObj.put(TRACKS, Utils.getJsonArrayFromStringList(new ArrayList<String>(getTracks())));
        jsonObj.put(PRIVATE, getPrivacyStatus());
        
        if(!StringUtils.isEmpty(getReferenceId()))
            jsonObj.put(REFERENCE_ID, getReferenceId());

        if(!StringUtils.isEmpty(getReferenceType()))
            jsonObj.put(REFERENCE_TYPE, getReferenceType());

        jsonObj.put(AUTO_CREATED, isAutoCreated());

        if(!CollectionUtils.isEmpty(unmatchedSongCountMap)) {
            Gson gson = new Gson();
            jsonObj.put(UNMATCHED_SONG_COUNT, gson.toJsonTree(unmatchedSongCountMap).getAsJsonObject());
        }

        if(getOnlineSongCount() > 0)
            jsonObj.put("onlineCount", getOnlineSongCount());

        return jsonObj;
    }

    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }

    public void fromJsonObject(JSONObject jsonObj) {
        fromJsonObject(jsonObj,false);
    }
    public void fromJsonObject(JSONObject jsonObj,boolean reverseSortTracks) {

        super.fromJsonObject(jsonObj);
        Object idobj = jsonObj.get(ID);
        if(idobj instanceof String) {
            setPlaylistId((String) idobj);
        }
        if(StringUtils.isEmpty(getPlaylistId()))
        {
            idobj = jsonObj.get("_id");
            if(idobj instanceof String) {
                setPlaylistId((String) idobj);
            }
            else if(idobj instanceof JSONObject) {
                setPlaylistId((String) ((JSONObject) jsonObj.get("_id")).get("$oid"));
            }
        }


        Object privateObj = jsonObj.get(PRIVATE);
        if(privateObj != null) {
            if(privateObj instanceof Number)
                setPrivacyStatus(((Number) privateObj).intValue());
            else
                setPrivacyStatus(((Boolean) privateObj).booleanValue() ? 1 : 0);
        }
        if(jsonObj.get(LAST_PLAY_TIME) != null) {
            long time = (Long) jsonObj.get(LAST_PLAY_TIME);
            setLastPlayTime(time);
        }
        if(jsonObj.get(OWNERID) != null)
            setOwnerId((String) jsonObj.get(OWNERID));
        if(jsonObj.get(THUMBNAIL) != null)
            setThumbnailUrl((String) jsonObj.get(THUMBNAIL));
        //if(jsonObj.get(TYPE) != null)
        //    setType(((Long) jsonObj.get(TYPE)).intValue());
        if(jsonObj.get(TITLE) != null)
            setTitle((String) jsonObj.get(TITLE));
        if(jsonObj.get("ltitle") != null)
            setLowerCaseTitle((String) jsonObj.get("ltitle"));
        if(jsonObj.get(TRACKS) != null)
        {
            JSONArray traksJsonArray = (JSONArray) jsonObj.get(TRACKS);

            List<String> traksList = null;
            if(reverseSortTracks)
            {
                traksList = new ArrayList<String>();
                if(traksJsonArray != null) {
                    for(int j = traksJsonArray.size() - 1; j >= 0; j--){
                        String trk = (String) traksJsonArray.get(j);
                        traksList.add(trk);
                    }
                }
            }
            else
            {
                traksList = Utils.getListFromJsonArr(traksJsonArray);
           }
            setTracks(new LinkedHashSet(traksList));
        }
        
        if(jsonObj.get(REFERENCE_ID) != null)
            setReferenceId((String) jsonObj.get(REFERENCE_ID));
        
        if(jsonObj.get(REFERENCE_TYPE) != null)
            setReferenceType((String) jsonObj.get(REFERENCE_TYPE));

        if(jsonObj.get(AUTO_CREATED) != null)
            setAutoCreated((Boolean) jsonObj.get(AUTO_CREATED));

        if(jsonObj.get("onlineCount") != null)
            setOnlineSongCount(((Number)jsonObj.get("onlineCount")).intValue());
        else
            setOnlineSongCount(-1);

        if(jsonObj.get(UNMATCHED_SONG_COUNT) != null) {
            JSONObject contentObj = (JSONObject)jsonObj.get(UNMATCHED_SONG_COUNT);
            unmatchedSongCountMap = new HashMap<>();
            for(Object key: contentObj.keySet()) {
                String id = (String) key;
                Integer value = ((Number) contentObj.get(key)).intValue();
                unmatchedSongCountMap.put(id, value);
            }
        }

    }

    @Override
    public int compareTo(Object o) {
        if(!(o instanceof UserPlaylist))
            return -1;

        if(getLastUpdated() > ((UserPlaylist)o).getLastUpdated())
            return -1;
        if(getLastUpdated() < ((UserPlaylist)o).getLastUpdated())
            return 1;

        return 0;
    }
}
