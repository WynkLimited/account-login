package com.wynk.dto;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.wynk.constants.JsonKeyNames.*;

/**
 *
 */
public class IdNameType {
    private static final org.slf4j.Logger logger               = LoggerFactory
            .getLogger(IdNameType.class.getCanonicalName());

    private String id;
    private String name;
    private String type;
    private boolean isCurated ;
    private String url;
    private String packageId;

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public IdNameType()
    {

    }

    public IdNameType(String id, String name, String type,boolean isCurated,String url,String packageId)
    {
        setId(id);
        setName(name);
        setType(type);
        setCurated(isCurated);
        if(url != null)
            setUrl(url);
        setPackageId(packageId);

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if(id == null)
            return;
        id = id.trim().toLowerCase().replaceAll("[^a-z0-9-_ ]", "").replaceAll("\\s", "-");
        /*if(id.contains(" "))
            id = id.trim().replaceAll(" ","-");*/
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(!StringUtils.isEmpty(name) && name.length() > 0)
        {
            boolean isAlphaN = StringUtils.isAlphanumeric("" + name.charAt(0));
            if(!isAlphaN || name.startsWith("\""))
                name = name.substring(1);
            if(name.endsWith("\""))
                name = name.substring(0,name.length()-1);
            name = WordUtils.capitalize(name);
        }

        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type)
    {
        if(StringUtils.isEmpty(type))
            return;
        type = type.replaceAll(" ","%20");
        this.type = type;
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put(ID, getId().trim().toLowerCase().replaceAll("[^a-z0-9-_ ]", "").replaceAll("\\s", "-"));
        jsonObj.put(TITLE, getName());
        if(!StringUtils.isEmpty(getType()))
            jsonObj.put(TYPE, getType());
        /*if (MusicCMSDataFetcher.getCuratedArtistMap() != null) {
            logger.info("MusicCMSDataFetcher.getCuratedArtistMap()! in idnameType tojson =null");
            if (MusicCMSDataFetcher.getCuratedArtistMap().keySet().contains(getId().toLowerCase())) {
                jsonObj.put(IS_CURATED,true);
                jsonObj.put(SMALL_IMAGE, MusicCMSDataFetcher.getCuratedArtistMap().get(getId().toLowerCase()).getUrl());
            }
            else
                jsonObj.put(IS_CURATED,false);
        }*/
        jsonObj.put(IS_CURATED,isCurated());
        if(!StringUtils.isEmpty(getUrl()))
            jsonObj.put(SMALL_IMAGE, getUrl());
        if(!StringUtils.isEmpty(getPackageId()))
            jsonObj.put(PACKAGE_ID, getUrl());

        return jsonObj;
    }

    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }

    public void fromJsonObject(JSONObject jsonObj) {
        Object idobj = jsonObj.get(ID);

        if(idobj instanceof String) {
            setId((String) idobj);
        }
        if(jsonObj.get(TITLE) != null)
            setName((String) jsonObj.get(TITLE));

        if(jsonObj.get(TYPE) != null)
            setType((String) jsonObj.get(TYPE));

        if(jsonObj.get(IS_CURATED) != null)
            setCurated((Boolean) jsonObj.get(IS_CURATED));

        if(jsonObj.get(SMALL_IMAGE) != null)
            setUrl((String) jsonObj.get(SMALL_IMAGE));
        if(jsonObj.get(PACKAGE_ID) != null)
            setUrl((String) jsonObj.get(PACKAGE_ID));

    }

    public IdNameType clone() {
        return this;
    }

    @Override
    public String toString() {
        return "{"+getId()+":"+getName()+":"+getType()+"}";
    }

    public boolean isCurated() {
        return isCurated;
    }

    public void setCurated(boolean curated) {
        isCurated = curated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdNameType that = (IdNameType) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
