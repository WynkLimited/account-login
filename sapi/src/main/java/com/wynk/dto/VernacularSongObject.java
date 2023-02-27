package com.wynk.dto;

 import com.wynk.utils.Utils;
 import org.apache.commons.lang.StringUtils;
 import org.json.simple.JSONArray;
 import org.json.simple.JSONObject;
 import org.springframework.util.CollectionUtils;

 import java.util.ArrayList;
 import java.util.List;

public class VernacularSongObject extends VernacularAlbumObject {
    private List<IdNameType> singers;
    private IdNameType                 director;
    private List<IdNameType>           lyricists;
    private List<IdNameType>           artists;
    private List<IdNameType>           actors;
    private String album;

    public List<IdNameType> getSingers() {
        return singers;
    }

    public void setSingers(List<IdNameType> singers) {
        this.singers = singers;
    }

    public IdNameType getDirector() {
        return director;
    }

    public void setDirector(IdNameType director) {
        this.director = director;
    }

    public List<IdNameType> getLyricists() {
        return lyricists;
    }

    public void setLyricists(List<IdNameType> lyricists) {
        this.lyricists = lyricists;
    }

    public List<IdNameType> getArtists() {
        return artists;
    }

    public void setArtists(List<IdNameType> artists) {
        this.artists = artists;
    }

    public List<IdNameType> getActors() {
        return actors;
    }

    public void setActors(List<IdNameType> actors) {
        this.actors = actors;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }


    public JSONObject toJson(){
        JSONObject jsonObj = super.toJson();

        if(!StringUtils.isEmpty(getAlbum()))
            jsonObj.put("album", getAlbum());

        if(!CollectionUtils.isEmpty(singers)) {
            jsonObj.put("singers", Utils.convertToIDNJSONArray(singers));
        }
        if(!CollectionUtils.isEmpty(lyricists)) {
            jsonObj.put("lyricists", Utils.convertToIDNJSONArray(lyricists));
        }
        if(!CollectionUtils.isEmpty(artists)) {
            jsonObj.put("artists", Utils.convertToIDNJSONArray(artists));
        }
        if(director != null) {
            jsonObj.put("director", director.toJsonObject());
        }
        if(!CollectionUtils.isEmpty(actors)) {
            jsonObj.put("actors", Utils.convertToIDNJSONArray(actors));
        }
        return jsonObj;

    }

    public void fromJsonObject(JSONObject jsonObj) {
        super.fromJsonObject(jsonObj);

        if(jsonObj.get("album") instanceof String) {
            setAlbum((String) jsonObj.get("album"));
        }
        if(jsonObj.get("singers") instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) jsonObj.get("singers");
            List<IdNameType> idNameTypes = new ArrayList<>();
            for(Object object : jsonArray){
                IdNameType idNameType = new IdNameType();
                idNameType.fromJsonObject((JSONObject)object);
                idNameTypes.add(idNameType);
            }
            setSingers(idNameTypes);
        }
        if(jsonObj.get("lyricists") instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) jsonObj.get("lyricists");
            List<IdNameType> idNameTypes = new ArrayList<>();
            for(Object object : jsonArray){
                IdNameType idNameType = new IdNameType();
                idNameType.fromJsonObject((JSONObject)object);
                idNameTypes.add(idNameType);
            }
            setLyricists(idNameTypes);
        }
        if(jsonObj.get("artists") instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) jsonObj.get("artists");
            List<IdNameType> idNameTypes = new ArrayList<>();
            for(Object object : jsonArray){
                IdNameType idNameType = new IdNameType();
                idNameType.fromJsonObject((JSONObject)object);
                idNameTypes.add(idNameType);
            }
            setArtists(idNameTypes);
        }
        if(jsonObj.get("actors") instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) jsonObj.get("actors");
            List<IdNameType> idNameTypes = new ArrayList<>();
            for(Object object : jsonArray){
                IdNameType idNameType = new IdNameType();
                idNameType.fromJsonObject((JSONObject)object);
                idNameTypes.add(idNameType);
            }
            setActors(idNameTypes);
        }
        if(jsonObj.get("director") instanceof String) {
            JSONObject str = (JSONObject) jsonObj.get("director");
            IdNameType idNameType = new IdNameType();
            idNameType.fromJsonObject(str);
            setDirector(idNameType);
        }

    }
}
