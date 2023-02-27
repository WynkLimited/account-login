package com.wynk.music.constants;


import com.wynk.utils.MusicUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.wynk.constants.JsonKeyNames.*;

/**
 * Created by bhuvangupta on 17/12/13.
 */
public enum MusicGenre {

    OTHERS("1000","Others","http://s3-ap-southeast-1.amazonaws.com/almusicapp/genres/others.png"),
    DANCE("1001","Dance","http://s3-ap-southeast-1.amazonaws.com/almusicapp/genres/dance.png"),
    ROMANCE("1002","Romance","http://s3-ap-southeast-1.amazonaws.com/almusicapp/genres/romance.png"),
    FUSION("1003","Fusion","http://s3-ap-southeast-1.amazonaws.com/almusicapp/genres/fusion.png"),
    REMIX("1004","Remix","http://s3-ap-southeast-1.amazonaws.com/almusicapp/genres/remix.png"),
    CLASSICAL("1005","Classical","http://s3-ap-southeast-1.amazonaws.com/almusicapp/genres/classical.png"),
    OCCASSIONS("1006","Occassions","http://s3-ap-southeast-1.amazonaws.com/almusicapp/genres/occassions.png"),
    FOLK("1007","Folk","http://s3-ap-southeast-1.amazonaws.com/almusicapp/genres/folk.png"),
    GHAZAL("1008","Gazal","http://s3-ap-southeast-1.amazonaws.com/almusicapp/genres/ghazal.png"),
    INTERNATIONAL("1009","International","http://s3-ap-southeast-1.amazonaws.com/almusicapp/genres/international.png"),
    PATRIOTIC("1010","Patriotic","http://s3-ap-southeast-1.amazonaws.com/almusicapp/genres/patriotic.png"),
    CHILDHOOD("1011","Childhood","http://s3-ap-southeast-1.amazonaws.com/almusicapp/genres/childhood.png"),
    QAWWALI("1012","Qawwali","http://s3-ap-southeast-1.amazonaws.com/almusicapp/genres/qawwali.png"),
    SPIRITUAL("1013","Spiritual","http://s3-ap-southeast-1.amazonaws.com/almusicapp/genres/spiritual.png"),
    SUFI("1014","Sufi","http://s3-ap-southeast-1.amazonaws.com/almusicapp/genres/sufi.png"),
    INSTRUMENTAL("1015","Instrumental","http://s3-ap-southeast-1.amazonaws.com/almusicapp/genres/instrumental.png");

    private String id;
    private String name;
    private String thumbnail;

    private MusicGenre(String id, String name, String thumbnail) {
        this.id = id;
        this.name = name;
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getThumbnail()
    {
        return thumbnail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JSONObject toJsonObject()
    {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put(ID, getId());
        jsonObj.put(TITLE, getName());
        return jsonObj;
    }

    public JSONObject toCompleteJsonObject()
    {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put(ID, getId());
        jsonObj.put(TITLE, getName());
        jsonObj.put(THUMBNAIL, getThumbnail());
        jsonObj.put(TYPE, MusicContentType.GENRE.name());
        jsonObj.put(LARGE_IMAGE, MusicUtils.getLargeResizedImage(getThumbnail()));
        jsonObj.put(SMALL_IMAGE, MusicUtils.getSmallResizedImage(getThumbnail()));
        jsonObj.put(MEDIUM_IMAGE,  MusicUtils.getMediumResizedImage(getThumbnail()));
        return jsonObj;
    }

    private static Map<String, MusicGenre> genreNameIdMapping = new HashMap<String, MusicGenre>();
    private static Map<String, MusicGenre> genreIdToNameMapping = new HashMap<>();

    static {
        for (MusicGenre mood : MusicGenre.values()) {
            genreNameIdMapping.put(mood.name.toLowerCase(), mood);
            genreIdToNameMapping.put(mood.getId(), mood);
        }
    }

    public static MusicGenre getGenreById(String typeId) {
        MusicGenre musicGenreType = genreIdToNameMapping.get(typeId);
        if (musicGenreType != null)
            return musicGenreType;

        return OTHERS; // default
    }

    public static String getGenreId(String name) {
        if(StringUtils.isBlank(name))
            return OTHERS.getId();
        MusicGenre musicGenreType = genreNameIdMapping.get(name.toLowerCase());
        if(musicGenreType == null)
            return OTHERS.getId();
        return musicGenreType.getId();
    }

    public static MusicGenre getGenreByName(String name) {
        if(StringUtils.isBlank(name))
            return OTHERS;

        MusicGenre musicGenreType = genreNameIdMapping.get(name.toLowerCase());
        if(musicGenreType == null)
        {
            //System.out.println("Not found genre : "+name);
            return OTHERS;
        }
        return musicGenreType;
    }


    @Override
    public String toString() {
        return toJsonObject().toJSONString();
    }
}
