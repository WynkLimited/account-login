package com.wynk.music.constants;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bhuvangupta on 17/12/13.
 */
public enum MusicContentType {

    SONG, ALBUM , PLAYLIST, USERPLAYLIST, PACKAGE, VIDEO, HELLOTUNE,
    ARTIST, MOOD, GENRE, RADIO, USERPACKAGE,SHORTURL, COMPILATION,CONTENT,ADHM_PLAYLIST,NAVIGATION,ONDEVICE_SONGS,CONCERT,SHAREDPLAYLIST;

    private static Map<String, MusicContentType> contentNameToTypeMapping = new HashMap<>();

    static {
        for (MusicContentType ctype : MusicContentType.values()) {
            contentNameToTypeMapping.put(ctype.name().toLowerCase(), ctype);
        }
    }

    public static MusicContentType getContentTypeId(String name) {
        if(StringUtils.isEmpty(name))
            return null;

        MusicContentType musicContentType = contentNameToTypeMapping.get(name.toLowerCase());
        if(musicContentType == null)
            return null;
        return musicContentType;
    }

    public String getCmsDataType()
    {
        if(name().equalsIgnoreCase("album") ||name().equalsIgnoreCase("playlist"))
            return "album";
        else if(name().equalsIgnoreCase("song"))
            return "content";
        else
            return null;
    }

    public String getShortUrlType()
    {
       if(this == MusicContentType.SONG)
            return "01";
       else if(this == MusicContentType.ALBUM)
           return "02";
       else if(this == MusicContentType.PLAYLIST)
            return "03";
       else if(this == MusicContentType.USERPLAYLIST)
            return "04";
       else if(this == MusicContentType.RADIO)
            return "05";
       else if(this == MusicContentType.ARTIST)
            return "06";
       else if(this == MusicContentType.PACKAGE)
            return "07";
       else if(this == MusicContentType.MOOD)
           return "08";
       else if(this == MusicContentType.COMPILATION)
           return "09";
       else if(this == MusicContentType.CONTENT)
           return "10";
       else if(this == MusicContentType.ADHM_PLAYLIST)
           return "11";
       else
           return "00";
    }

    public static MusicContentType getContentTypeForShortUrlType(String shortUrlType)
    {
         switch (shortUrlType)
         {
             case "01" :
                 return SONG;
             case "02" :
                 return ALBUM;
             case "03" :
                 return PLAYLIST;
             case "04" :
                 return USERPLAYLIST;
             case "05" :
                 return RADIO;
             case "06" :
                 return ARTIST;
             case "07" :
                 return PACKAGE;
             case "08" :
                return MOOD;
             case "09" :
                 return COMPILATION;
             case "10" :
                 return CONTENT;
             case "11" :
                return ADHM_PLAYLIST;
             default:
                 return null;

         }
    }
}
