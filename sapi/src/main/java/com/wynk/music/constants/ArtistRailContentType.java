package com.wynk.music.constants;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ArtistRailContentType {

    TOP_SONGS, CONCERTS_RAIL, RELATED_PLAYLIST, All_SONGS_RAIL, RELATED_ALBUM, SIMILAR_ARTISTS, UPDATE_RAIL, BIOGRAPHY, SOCIAL_LINKS_RAIL;

    private static Map<String, ArtistRailContentType> contentNameToTypeMapping = new HashMap<>();

    static {
        for (ArtistRailContentType ctype : ArtistRailContentType.values()) {
            contentNameToTypeMapping.put(ctype.name().toLowerCase(), ctype);
        }
    }

    public static ArtistRailContentType getContentTypeId(String name) {
        if(StringUtils.isEmpty(name))
            return null;

        ArtistRailContentType artistRailContentType = contentNameToTypeMapping.get(name.toLowerCase());
        if(artistRailContentType == null)
            return null;
        return artistRailContentType;
    }

    public static List<String> defaultListOfPosition(){
        List<String> positionRail = new ArrayList<>();
        for(ArtistRailContentType artistRailContentType : ArtistRailContentType.values()){
            positionRail.add(artistRailContentType.name());
        }
        return positionRail;
    }
}
