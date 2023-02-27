package com.wynk.music.dto;

import com.wynk.constants.MusicConstants;
import com.wynk.dto.AppScreenMapping;
import com.wynk.dto.BaseObject;
import com.wynk.dto.IdNameType;
import com.wynk.music.MusicCMSDataFetcher;
import com.wynk.music.constants.*;
import com.wynk.utils.MusicUtils;
import com.wynk.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.wynk.constants.JsonKeyNames.*;

/**
 * Created by bhuvangupta on 16/12/13.
 */
public abstract class MusicMetadata extends BaseObject {

    private static Logger logger = LoggerFactory.getLogger(MusicMetadata.class.getCanonicalName());


    private String title;
    private String subTitle;
    private boolean showPlayIcon;
    private int duration; //for songs/albums : in seconds
    private String contentLanguage;

    private MusicContentType type;

    //based on the device type
    private String thumbnailUrl; //50x50
    private String smallImage;   //120x120
    private String mediumImage;  //240x240
    private String largeImage;   //?
    private String originalImage;
    private String newRedesignFeaturedImage = "";
    private String newRedesignPlaylistImage = "";

    private boolean exclusive;

   /* private MusicGenre genre;
    private String mood;*/

    private String cpId;
    private long validTill;
    private long releaseDate;
    private String publishedYear;

    private int playCount;
    private int likes;
    private int shares;
    private String shortUrl;
    private String basicShortUrl;

    private MusicSongStatus downloaded = MusicSongStatus.NONE;
    private MusicSongStatus rented = MusicSongStatus.NONE;
    private boolean liked = false;

    private long downloadedTimestamp;
    private long rentedTimestamp;
    private long likedTimestamp;

    private String purchaseUrl;
    private String downloadUrl;
    private String downloadPrice;

    private double rank;
    private double score;
    private double packageRank;
    private double popularityRank;
    private double boost;
    public String publisher;
    
    private AppScreenMapping appScreenMapping;
    
    public MusicPackageType packageType;

    private String minimalSetEtag;

    private MusicContentState contentState;
    private String newRedesignFeaturedTitle = "";
    private String newRedesignFeaturedSubTitle = "";
    private boolean playIcon = true;

    public String getNewRedesignPlaylistImage() {
        return newRedesignPlaylistImage;
    }

    public void setNewRedesignPlaylistImage(String newRedesignPlaylistImage) {
        this.newRedesignPlaylistImage = newRedesignPlaylistImage;
    }


    public String getNewRedesignFeaturedTitle() {
        return newRedesignFeaturedTitle;
    }

    public void setNewRedesignFeaturedTitle(String newRedesignFeaturedTitle) {
        this.newRedesignFeaturedTitle = newRedesignFeaturedTitle;
    }

    public String getNewRedesignFeaturedSubTitle() {
        return newRedesignFeaturedSubTitle;
    }

    public void setNewRedesignFeaturedSubTitle(String newRedesignFeaturedSubTitle) {
        this.newRedesignFeaturedSubTitle = newRedesignFeaturedSubTitle;
    }

    public boolean isPlayIcon() {
        return playIcon;
    }

    public void setPlayIcon(boolean playIcon) {
        this.playIcon = playIcon;
    }
	public void setScore(double solrScore) {
		this.score = solrScore;
	}

	public void setPackageRank(double packageRank) {
		this.packageRank = packageRank;
	}

	public void setPopularityRank(double popularityRank) {
		this.popularityRank = popularityRank;
	}

	public void setBoost(double boost) {
		this.boost = boost;
	}

	public String getTitle() {
        return title;
    }

  public String getSubTitle() {
    return subTitle;
  }

  public boolean getShowPlayIcon() {
    return showPlayIcon;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setSubTitle(String subTitle) {
    this.subTitle = subTitle;
  }

  public void setShowPlayIcon(boolean showPlayIcon) {
    this.showPlayIcon = showPlayIcon;
  }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getContentLanguage() {
        return contentLanguage;
    }

    public void setContentLanguage(String contentLanguage) {
        this.contentLanguage = contentLanguage;
    }

    public MusicContentType getType() {
        return type;
    }

    public void setType(MusicContentType type) {
        this.type = type;
    }

    public String getOriginalImage() {
        return originalImage;
    }

    
    // TODO - PLEASE REMOVE THIS HACK AFTER 07/09/16
    public void setOriginalImage(String originalImage) {
    	if(StringUtils.isBlank(originalImage) || originalImage.equalsIgnoreCase("http://s3-ap-southeast-1.amazonaws.com/bsy/wynk/wynk_noimg.png"))
    		originalImage = "http://s3-ap-southeast-1.amazonaws.com/bsy/wynk/wynk_noimg_2.png";
    	
        this.originalImage = originalImage;
    }

    public String getNewRedesignFeaturedImage() {
        return newRedesignFeaturedImage;
    }

    public void setNewRedesignFeaturedImage(String newRedesignFeaturedImage) {
        this.newRedesignFeaturedImage = newRedesignFeaturedImage;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getLargeImage() {
        return largeImage;
    }

    public void setLargeImage(String largeImage) {
        this.largeImage = largeImage;
    }

    public String getMediumImage() {
        return mediumImage;
    }

    public void setMediumImage(String mediumImage) {
        this.mediumImage = mediumImage;
    }

    public String getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(String smallImage) {
        this.smallImage = smallImage;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }


    public String getCpId() {
        return cpId;
    }

    public void setCpId(String cpId) {
        this.cpId = cpId;
    }

    public long getValidTill() {
        return validTill;
    }

    public void setValidTill(long validTill) {
        this.validTill = validTill;
    }

    public long getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(long releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(String publishedYear) {
        this.publishedYear = publishedYear;
    }


    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }
    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public MusicSongStatus getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(MusicSongStatus downloaded) {
        this.downloaded = downloaded;
    }

    public MusicSongStatus getRented() {
        return rented;
    }

    public String getPurchaseUrl() {
        return purchaseUrl;
    }

    public void setPurchaseUrl(String purchaseUrl) {
        this.purchaseUrl = purchaseUrl;
    }

    public boolean getLiked() {
        return liked;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
        if(StringUtils.isNotBlank(shortUrl)) {
            setBasicShortUrl(shortUrl.replace(MusicConstants.shortUrlDomain, MusicConstants.basicShortUrlDomain));
        }
    }

    public String getBasicShortUrl() {
        return basicShortUrl;
    }

    public void setBasicShortUrl(String basicShortUrl) {
        this.basicShortUrl = basicShortUrl;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getMinimalSetEtag() {
        return minimalSetEtag;
    }

    public void setMinimalSetEtag(String minimalSetEtag) {
        this.minimalSetEtag = minimalSetEtag;
    }

    public MusicContentState getContentState() {
        return contentState;
    }

    public void setContentState(MusicContentState contentState) {
        this.contentState = contentState;
    }

    public JSONObject toCompressedJsonObject(List<String> keys) {
        if(keys == null)
            return toJsonObject();
        else
        {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(ID, getId());
            try {
                Field[] fields = MusicMetadata.class.getDeclaredFields();
                for(Field f : fields) {
                    String name = f.getName();
                    if(keys.contains(name))
                    {
                        Object object = f.get(this);
                        if(object == null)
                            continue;
                        
                        if(object instanceof MusicGenre)
                        {
                            MusicGenre genre = (MusicGenre) object;
                            if(genre != MusicGenre.OTHERS && !MusicUtils.isInvalidString(genre.getName()))
                                jsonObj.put(name,genre.toJsonObject());
                        }
                        //TODO - Moods123
                        /*else if(object instanceof MusicMood)
                        {
                            MusicMood mood = (MusicMood) object;
                            if(mood != MusicMood.UNKNOWN && !MusicUtils.isInvalidString(mood.getName()))
                                jsonObj.put(name,mood.toJsonObject());
                        }*/
                        else if(object instanceof MusicSongStatus)
                        {
                            MusicSongStatus status = (MusicSongStatus) object;
                            jsonObj.put(name,status.getStatus());
                        }
                        else
                        {
                            jsonObj.put(name,f.get(this));
                        }
                    }
                }
            }
            catch (Exception e) {
                logger.error("Error creating toCompressed JSON Object : "+e.getMessage(),e);
            }
            return jsonObj;
        }
    }
    
    public long getRentedTimestamp() {
        return rentedTimestamp;
    }

    public long getDownloadedTimestamp() {
        return downloadedTimestamp;
    }

    public long getLikedTimestamp() {
        return likedTimestamp;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDownloadPrice() {
        return downloadPrice;
    }

    public void setDownloadPrice(String downloadPrice) {
        this.downloadPrice = downloadPrice;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

    @Override
    public JSONObject toJsonObject() {
        JSONObject jsonObj = super.toJsonObject();
        jsonObj.remove("_id");
        jsonObj.put(ID, getId());
        if(!StringUtils.isEmpty(getTitle()))
            jsonObj.put(TITLE,getTitle());
        if(getType() != null) {
        	jsonObj.put(TYPE,getType().name());
        }
        if(getPackageType() != null) {
            jsonObj.put(MUSIC_PACKAGE_TYPE, getPackageType().name());
        }
        if(getDuration() > 0)
            jsonObj.put(DURATION,getDuration());
        if(!StringUtils.isEmpty(getContentLanguage()))
            jsonObj.put(ITEM_CONTENT_LANGUAGE,getContentLanguage());
        if(!StringUtils.isEmpty(getThumbnailUrl()))
        {
            jsonObj.put(THUMBNAIL,getThumbnailUrl());
        }

        if(getSubTitle() != null) {
          jsonObj.put(SUBTITLE, getSubTitle());
        }

        jsonObj.put(SHOW_PLAY_ICON, getShowPlayIcon());

        if(!StringUtils.isEmpty(getOriginalImage()))
            jsonObj.put(ORIGINAL_IMAGE,getOriginalImage());

        if(!StringUtils.isEmpty(getMediumImage()))
            jsonObj.put(MEDIUM_IMAGE,getMediumImage());

        if(!StringUtils.isEmpty(getSmallImage()))
            jsonObj.put(SMALL_IMAGE,getSmallImage());

        if(!StringUtils.isEmpty(getLargeImage()))
            jsonObj.put(LARGE_IMAGE,getLargeImage());

        if(!StringUtils.isEmpty(getNewRedesignFeaturedImage()))
            jsonObj.put(REDESIGN_FEATURED_IMAGE,getNewRedesignFeaturedImage());

        if(!StringUtils.isEmpty(getNewRedesignPlaylistImage()))
            jsonObj.put(REDESIGN_PLAYLIST_IMAGE,getNewRedesignPlaylistImage());


        jsonObj.put("playIcon",isPlayIcon());

        /*if(jsonObj.get("playIcon") instanceof String) {
            String  playIcon = (String) jsonObj.get("playIcon");
            if(playIcon.equals("true"))
                setPlayIcon(true);
            else
                setPlayIcon(false);
        }*/

        jsonObj.put("newRedesignFeaturedTitle", getNewRedesignFeaturedTitle());
        jsonObj.put("newRedesignFeaturedSubTitle",getNewRedesignFeaturedSubTitle());

//        if(isExclusive())
            jsonObj.put(EXCLUSIVE,isExclusive());
       /* if(getGenre()!= null && (getGenre() != MusicGenre.OTHERS) && !MusicUtils.isInvalidString(getGenre().getName()))
            jsonObj.put(GENRE,getGenre().toJsonObject());

        if(!StringUtils.isEmpty(getMood())) {
            JSONObject obj = new JSONObject();
            obj.put(ID, MusicUtils.getMoodsHashedId(getMood()));
            obj.put(TITLE, getMood());
            jsonObj.put(MOOD,obj);
        }*/

        if(getDownloadUrl() != null)
            jsonObj.put("downloadUrl",getDownloadUrl());
        if(getDownloadPrice() != null)
            jsonObj.put("downloadPrice",getDownloadPrice());

        if(getPurchaseUrl() != null)
            jsonObj.put(PURCHASE_URL,getPurchaseUrl());

        if(getPublisher() != null)
        {
            jsonObj.put(PUBLISHER,getPublisher());
            if(StringUtils.isNotEmpty(getPublisher()))
                jsonObj.put(LABEL,getPublisher());
        }

        if(getMinimalSetEtag() != null)
            jsonObj.put(MIN_SET_ETAG, getMinimalSetEtag());

//        if(getLikes() > 0)
//        {
//            jsonObj.put(LIKES,getLikes());
//            jsonObj.put(LIKES_LABEL,Utils.getCountLabel(getLikes())+" Likes");
//        }
//        if(getShares() > 0)
//        {
//            jsonObj.put(SHARES,getShares());
//            jsonObj.put(SHARES_LABEL,Utils.getCountLabel(getShares())+" Shares");
//
//        }
//        if(getPlayCount() > 0)
//        {
//            jsonObj.put(PLAYS,getPlayCount());
//            jsonObj.put(PLAYS_LABEL,Utils.getCountLabel(getPlayCount())+" Plays");
//
//        }

        if(!StringUtils.isEmpty(getCpId()))
            jsonObj.put(CP_ID,getCpId());
        if(getValidTill() > 0)
            jsonObj.put(VALID_TILL,getValidTill());
        if(getReleaseDate() > 0)
            jsonObj.put(RELEASE_DATE,getReleaseDate());
        
        if (getAppScreenMapping() != null)
            jsonObj.put("appScreenMapping",getAppScreenMapping().toJsonObject());


        if(getPublishedYear() != null)
            jsonObj.put(PUBLISHED_YEAR,getPublishedYear());

        if(getDownloaded() != null)
        {
            jsonObj.put(DOWNLOADED,getDownloaded().getStatus ());
            jsonObj.put(DOWNLOADED_TS,getDownloadedTimestamp());
        }

        if(getRented() != null)
        {
            jsonObj.put(RENTED,getRented().getStatus ());
            jsonObj.put(RENTED_TS,getRentedTimestamp());
        }

        jsonObj.put(LIKED,getLiked());
        jsonObj.put(LIKED_TS,getLikedTimestamp());

        String shortUrl1 = getShortUrl();
//        if(StringUtils.isEmpty(shortUrl1))
//            shortUrl1 = MusicUtils.generateShortUrl(getId(), getType());
        if(!StringUtils.isEmpty(shortUrl1)) {
            jsonObj.put(SHORT_URL, shortUrl1);
            jsonObj.put(BASIC_SHORT_URL,shortUrl1.replace(MusicConstants.shortUrlDomain, MusicConstants.basicShortUrlDomain));
        }

        if(getRank() > 0)
            jsonObj.put(RANK, rank);

        if(getAppScreenMapping() != null) {
            jsonObj.put("appScreenMapping", getAppScreenMapping().toJsonObject());
        }

        if(getContentState() != null)
            jsonObj.put("contentState", getContentState().name());

        jsonObj.put(LAST_UPDATED,getLastUpdated());

        return jsonObj;
    }

    public void fromJsonObject(JSONObject jsonObj)
    {
        super.fromJsonObject(jsonObj);
        if(jsonObj.get(ID) != null) {
            setId((String) jsonObj.get(ID));
        }
        if(jsonObj.get(TITLE) != null) {
            setTitle((String) jsonObj.get(TITLE));
            setPackageType(MusicPackageType.getPackageType(getTitle()));
            
        }
        if(jsonObj.get(TYPE) != null) {
            setType(MusicContentType.valueOf ((String) jsonObj.get(TYPE)));
            setAppScreenMapping(ContentTypeToScreenEnum.getScreenMappingFromContentType(getType()));
        }

        if(jsonObj.get(DURATION) != null) {
            setDuration(((Number) jsonObj.get(DURATION)).intValue());
        }

      if(jsonObj.get(SUBTITLE) != null) {
        setSubTitle((String) jsonObj.get(SUBTITLE));
      }

      if(jsonObj.get(SHOW_PLAY_ICON) != null) {
        setShowPlayIcon((boolean) jsonObj.get(SHOW_PLAY_ICON));
      }

        if(jsonObj.get(ITEM_CONTENT_LANGUAGE) != null) {
            setContentLanguage((String) jsonObj.get(ITEM_CONTENT_LANGUAGE));
        }
        if(jsonObj.get(CONTENT_LANGUAGE) != null) {
            setContentLanguage((String) jsonObj.get(CONTENT_LANGUAGE));
        }

        if(jsonObj.get(THUMBNAIL) != null) {
            setThumbnailUrl((String) jsonObj.get(THUMBNAIL));
        }

        if(jsonObj.get(ORIGINAL_IMAGE) != null) {
            setOriginalImage((String) jsonObj.get(ORIGINAL_IMAGE));
        }

        if(jsonObj.get(LARGE_IMAGE) != null) {
            setLargeImage((String) jsonObj.get(LARGE_IMAGE));
        }

        if(jsonObj.get(MEDIUM_IMAGE) != null) {
            setMediumImage((String) jsonObj.get(MEDIUM_IMAGE));
        }

        if(jsonObj.get(SMALL_IMAGE) != null) {
            setSmallImage((String) jsonObj.get(SMALL_IMAGE));
        }
        if(jsonObj.get(REDESIGN_FEATURED_IMAGE) != null) {
            setNewRedesignFeaturedImage((String) jsonObj.get(REDESIGN_FEATURED_IMAGE));
        }
        if(jsonObj.get(REDESIGN_PLAYLIST_IMAGE) != null) {
            setNewRedesignPlaylistImage((String) jsonObj.get(REDESIGN_PLAYLIST_IMAGE));
        }
        if(jsonObj.get("playIcon") instanceof String) {
            String  playIcon = (String) jsonObj.get("playIcon");
            if(playIcon.equals("true"))
                setPlayIcon(true);
            else
                setPlayIcon(false);
        }
        if(jsonObj.get("playIcon") instanceof Boolean) {
                setPlayIcon((Boolean)jsonObj.get("playIcon"));
        }
        if(jsonObj.get("newRedesignFeaturedTitle") != null) {
            setNewRedesignFeaturedTitle((String) jsonObj.get("newRedesignFeaturedTitle"));
        }
        if(jsonObj.get("newRedesignFeaturedSubTitle") != null) {
            setNewRedesignFeaturedSubTitle((String) jsonObj.get("newRedesignFeaturedSubTitle"));
        }
        if(jsonObj.get(EXCLUSIVE) != null) {
            setExclusive((Boolean) jsonObj.get(EXCLUSIVE));
        }
       /* if(jsonObj.get(GENRE) != null) {
            Object genre = jsonObj.get(GENRE);
            if(genre instanceof String)
                setGenre(MusicGenre.getGenreByName((String) jsonObj.get(GENRE)));
            else if(genre instanceof JSONObject)
                setGenre(MusicGenre.getGenreByName((String) ((JSONObject) jsonObj.get(GENRE)).get("title")));
            else
                setGenre((MusicGenre) jsonObj.get(GENRE));
        }
        if(jsonObj.get(MOOD) != null) {
            Object mood = jsonObj.get(MOOD);
            if(mood instanceof String)
                setMood((String) jsonObj.get(MOOD));
            else if(mood instanceof JSONObject)
                setMood(((String) ((JSONObject) jsonObj.get(MOOD)).get("title")));
            else
                setMood((String) jsonObj.get(MOOD));
        }*/

        if(jsonObj.get("downloadPrice") != null) {
            setDownloadPrice((String) jsonObj.get("downloadPrice"));
        }
        if(jsonObj.get("downloadUrl") != null) {
            setDownloadUrl((String) jsonObj.get("downloadUrl"));
        }
        if(jsonObj.get(PURCHASE_URL) != null) {
            setPurchaseUrl((String) jsonObj.get(PURCHASE_URL));
        }
        if(jsonObj.get(MIN_SET_ETAG) != null) {
            setMinimalSetEtag((String) jsonObj.get(MIN_SET_ETAG));
        }


        if(jsonObj.get(CP_ID) != null) {
            setCpId((String) jsonObj.get(CP_ID));
        }

        if(jsonObj.get(VALID_TILL) != null) {
            setValidTill(((Number) jsonObj.get(VALID_TILL)).longValue());
        }

        if(jsonObj.get(RELEASE_DATE) != null) {
            setReleaseDate(((Number) jsonObj.get(RELEASE_DATE)).longValue());
        }
        

        if(jsonObj.get(PUBLISHED_YEAR) != null) {
            setPublishedYear((String) jsonObj.get(PUBLISHED_YEAR));
        }
        if(jsonObj.get(PUBLISHER) != null) {
            setPublisher((String) jsonObj.get(PUBLISHER));
        }

       /* if(jsonObj.get(DOWNLOADED) != null) {
            setDownloaded(MusicSongStatus.getSongStatus(((Number) jsonObj.get(DOWNLOADED)).intValue()));

            if(jsonObj.get(DOWNLOADED_TS) != null)
                setDownloadedTimestamp(((Number)jsonObj.get(DOWNLOADED_TS)).longValue());
        }*/

       /* if(jsonObj.get(RENTED) != null) {
            setRented(MusicSongStatus.getSongStatus(((Number) jsonObj.get(RENTED)).intValue()));
            if(jsonObj.get(RENTED_TS) != null)
                setRentedTimestamp(((Number) jsonObj.get(RENTED_TS)).longValue());
        }

        if(jsonObj.get(LIKED) != null) {
            Boolean likd = (Boolean) jsonObj.get(LIKED);
            setLiked((likd != null)?likd : false);
            if(jsonObj.get(LIKED_TS) != null)
                setLikedTimestamp(((Number) jsonObj.get(LIKED_TS)).longValue());
        }*/
        if(jsonObj.get(SHORT_URL) != null)
        {
            setShortUrl((String)jsonObj.get(SHORT_URL));
        }

        if(jsonObj.get(RANK) != null)
        {
            setRank(((Number)jsonObj.get(RANK)).doubleValue());
        }
        if(jsonObj.get("appScreenMapping") != null)
        {
            AppScreenMapping screenMapping = new AppScreenMapping();
            screenMapping.fromJsonObject((JSONObject)jsonObj.get("appScreenMapping"));
            setAppScreenMapping(screenMapping);
        }

        Object object = jsonObj.get("contentState");
        if(object != null)
            setContentState(MusicContentState.valueOf((String)object));
        else
            setContentState(MusicContentState.LIVE);


    }

    
    public MusicPackageType getPackageType() {
        return packageType;
    }

    
    public void setPackageType(MusicPackageType packageType) {
        this.packageType = packageType;
    }

    public static List<IdNameType> convertToIdNameTypeList(MusicContentType type,List<String> strList)
    {
        if(strList == null || strList.size() == 0)
            return null;

        List<String> uniqueIdNameList = MusicUtils.getUniqueArtistList(null,strList);
        List<IdNameType> objList = new ArrayList<>();
        for (int i = 0; i < uniqueIdNameList.size(); i++) {
            String obj = uniqueIdNameList.get(i);
            IdNameType idObj = new IdNameType();
            //obj = Utils.removeQuotes(obj);
            idObj.setId(obj);
            idObj.setName(obj);
            idObj.setType(type.name());
            if(MusicCMSDataFetcher.getCuratedArtistMap().keySet().contains(idObj.getId())){
                idObj.setCurated(MusicCMSDataFetcher.getCuratedArtistMap().get(idObj.getId()).isCurated());
                idObj.setUrl(MusicCMSDataFetcher.getCuratedArtistMap().get(idObj.getId()).getUrl()); //done
            }
            else idObj.setCurated(false);

            if(!StringUtils.isEmpty(idObj.getName()))
                objList.add(idObj);
        }
        return objList;
    }


    public static List<IdNameType> convertToIdNameTypeList(JSONArray jsonArray)
    {
        if(jsonArray == null || jsonArray.size() == 0)
            return null;

        Set<String> uniqueArtists = new HashSet<>();
        List<IdNameType> artistsList = new ArrayList<>();
        Set<IdNameType> objList = new HashSet<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = (JSONObject)jsonArray.get(i);
            IdNameType idObj = new IdNameType();
            idObj.fromJsonObject(obj);
            idObj.setName(MusicUtils.normalizeArtistName(idObj.getName()));
            if(!StringUtils.isEmpty(idObj.getName()) && !uniqueArtists.contains(idObj.getName().toLowerCase())) {
                objList.add(idObj);
                uniqueArtists.add(idObj.getName().toLowerCase());
            }
            idObj.setType(MusicContentType.ARTIST.name());
            objList.add(idObj);
        }
        artistsList = new ArrayList(objList);

        return artistsList;
    }


    static List<IdNameType> getArtistList(Object actorObj) {
        List<IdNameType> actorList = null;
        if(actorObj instanceof JSONArray)
        {
            actorList = convertToIdNameTypeList(MusicContentType.ARTIST,
                    Utils.convertToStringList((JSONArray)actorObj));
        }
        else if(actorObj instanceof String)
            actorList = convertToIdNameTypeList(MusicContentType.ARTIST,
                    Utils.convertToStringList((String) actorObj));
        return actorList;
    }

    public static JSONArray convertFromIdNameTypeList(List<IdNameType> idNameList)
    {
        if(idNameList == null || idNameList.size() == 0)
            return null;

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < idNameList.size(); i++) {
            IdNameType idObj = idNameList.get(i);
            if(MusicUtils.isInvalidString(idObj.getName()))
                continue;
            JSONObject jobj = new JSONObject();
            jobj.put(ID,idObj.getId().toLowerCase());
            jobj.put(TITLE,idObj.getName());
            jobj.put(TYPE,idObj.getType());
            jobj.put(IS_CURATED,idObj.isCurated());
            if(idObj.getUrl() != null)
                jobj.put(SMALL_IMAGE,idObj.getUrl());
            if(!StringUtils.isEmpty(idObj.getName()))
                jsonArray.add(jobj);
            if(!StringUtils.isEmpty(idObj.getPackageId()))
                jobj.put(PACKAGE_ID,idObj.getPackageId());

        }
        return jsonArray;
    }


	public AppScreenMapping getAppScreenMapping() {
		return appScreenMapping;
	}

	public void setAppScreenMapping(AppScreenMapping appScreenMapping) {
		this.appScreenMapping = appScreenMapping;
	}

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MusicMetadata that = (MusicMetadata) o;

        if(getId() != that.getId())
            return false;
        if (type != that.type)
            return false;

        return true;
    }

    public JSONObject getMinimalSetJSONObject() {
        return new JSONObject();
    }

}
