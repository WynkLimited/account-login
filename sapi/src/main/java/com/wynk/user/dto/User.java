package com.wynk.user.dto;

import com.wynk.common.Language;
import com.wynk.constants.JsonKeyNames;
import com.wynk.constants.MusicConstants;
import com.wynk.dto.WCFSubscription;
import com.wynk.music.constants.MusicContentLanguage;
import com.wynk.music.constants.MusicPackType;
import com.wynk.musicpacks.FUPPack;
import com.wynk.musicpacks.MusicPack;
import com.wynk.utils.JsonUtils;
import com.wynk.utils.MusicBuildUtils;
import com.wynk.utils.MusicUtils;
import com.wynk.utils.ObjectUtils;
import com.wynk.utils.Utils;
import com.wynk.wcf.dto.UserSubscription;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class User {

    private static final Logger logger = LoggerFactory.getLogger(User.class.getCanonicalName());

    private String id;
    private String uid;
    private String msisdn;
    private String countryId;
    private String name;
    private String lname;
    private String email;
    private String lang;
    private String gender;                   // m,f
    private long creationDate;
    private long lastActivityDate;
    private boolean isActive = true;
    private String circle;
    private String songQuality = "a";              // l,m,h
    private String avatar;
    private Boolean autoRenewal = null;
    private boolean notifications = true;
    private String fbToken;

    private UserDob dob;

    private String iTunesSubscription;

    private List<String> contentLanguages;

    private List<String> onboardingLanguages;

    private Set<String> podcastCategories;

    //    private List<String> userSelectedContentLanguages;
    private String preferredLanguage;

    private List<UserDevice> devices;
    private List<UserFavorite> favorites;
    private List<UserPurchase> downloads;
    private List<UserRental> rentals;
    private List<UserFavorite> follows;

    // For fair use policy
    private int streamedCount;
    private int rentalsCount;

    private LinkedList<Integer> currentOfferIds;

    private String operator;
    private int source;
    private Integer platform;

    private boolean autoPlaylists = true;

    private String downloadQuality = "hd";
    private long lastAutoRenewalOffSettingTimestamp;

    private FUPPack fupPack;
    private String token;
    private String userType;
    private String referrer;
    private int galaxyPurchaseCount;
    private WCFSubscription subscription;
    private UserSubscription userSubscription;

    private Long ndsTS;
    private long expireAt;

    // flag to distinguish users for we set language explicitly
    private boolean isSystemGeneratedContentLang = true;

    private List<String> basicSelectedArtist;
    private List<String> basicSelectedPlaylist;
    private List<String> basicContentLanguage;
    private boolean basicHasManuallySelectedLangauge = false;

    private String vasDND;

    private Boolean isDeleted;

    public Boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public List<String> getOnboardingLanguages() {
        return onboardingLanguages;
    }

    public void setOnboardingLanguages(List<String> onboardingLanguages) {
        this.onboardingLanguages = onboardingLanguages;
    }

    public Long getNdsTS() {
        return ndsTS;
    }

    public void setNdsTS(Long ndsTS) {
        this.ndsTS = ndsTS;
    }

    public String getVasDND() {
        return vasDND;
    }

    public void setVasDND(String vasDND) {
        this.vasDND = vasDND;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public LinkedList<Integer> getCurrentOfferIds() {
        if (currentOfferIds != null) {
            return currentOfferIds;
        }
        return new LinkedList<>();
    }

    public void setCurrentOfferIds(LinkedList<Integer> currentOfferIds) {
        this.currentOfferIds = currentOfferIds;
    }

    public void setAutoPlaylists(boolean autoPlaylists) {
        this.autoPlaylists = autoPlaylists;
    }

    public int getGalaxyPurchaseCount() {
        return galaxyPurchaseCount;
    }

    public void setGalaxyPurchaseCount(int galaxyPurchaseCount) {
        this.galaxyPurchaseCount = galaxyPurchaseCount;
    }

    public boolean isSystemGeneratedContentLang() {
        return isSystemGeneratedContentLang;
    }

    public void setSystemGeneratedContentLang(boolean isSystemGeneratedContentLang) {
        this.isSystemGeneratedContentLang = isSystemGeneratedContentLang;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public FUPPack getFupPack() {
        return fupPack;
    }

    public void setFupPack(FUPPack fupPack) {
        this.fupPack = fupPack;
    }

    private HashMap<String, MusicPack> packs;

    public HashMap<String, MusicPack> getPacks() {
        return packs;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public void setPacks(HashMap<String, MusicPack> packs) {
        this.packs = packs;
    }

    public void addPack(String packType, MusicPack pack) {
        if (packs == null)
            packs = new HashMap<String, MusicPack>();
        packs.put(packType, pack);

    }

    public List<String> getBasicSelectedArtist() {
        return basicSelectedArtist;
    }

    public void setBasicSelectedArtist(List<String> basicSelectedArtist) {
        this.basicSelectedArtist = basicSelectedArtist;
    }

    public List<String> getBasicSelectedPlaylist() {
        return basicSelectedPlaylist;
    }

    public void setBasicSelectedPlaylist(List<String> basicSelectedPlaylist) {
        this.basicSelectedPlaylist = basicSelectedPlaylist;
    }

    public List<String> getBasicContentLanguage() {
        return basicContentLanguage;
    }

    public void setBasicContentLanguage(List<String> basicContentLanguage) {
        this.basicContentLanguage = basicContentLanguage;
    }

    public boolean isBasicHasManuallySelectedLangauge() {
        return basicHasManuallySelectedLangauge;
    }

    public void setBasicHasManuallySelectedLangauge(boolean basicHasManuallySelectedLangauge) {
        this.basicHasManuallySelectedLangauge = basicHasManuallySelectedLangauge;
    }

    public void addPack(String packType, long creationTime, long packValidity) {
        if (packType.equalsIgnoreCase(FUPPack.class.getSimpleName())) {
            FUPPack fupPack = new FUPPack(creationTime, packValidity);
            addPack(fupPack.getPackType(), fupPack);
        }
    }

    public String getDownloadQuality() {
        return downloadQuality;
    }

    public void setDownloadQuality(String downloadQuality) {
        this.downloadQuality = downloadQuality;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getCircle() {
        return circle;
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.lname = name.toLowerCase();
    }


    public UserDob getDob() {
        return dob;
    }

    public void setDob(UserDob dob) {
        this.dob = dob;
    }

    public String getLowercaseUsername() {
        return lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSongQuality() {
        return songQuality;
    }

    public void setSongQuality(String songQuality) {
        this.songQuality = songQuality;
    }

    public Boolean isAutoRenewal() {
        return autoRenewal;
    }

    public void setAutoRenewal(Boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(long lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<UserDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<UserDevice> devices) {
        this.devices = devices;
    }

    public List<UserFavorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<UserFavorite> favorites) {
        this.favorites = favorites;
    }

    public List<UserFavorite> getFollows() {
        return follows;
    }

    public List<UserPurchase> getDownloads() {
        return downloads;
    }

    public void setDownloads(List<UserPurchase> downloads) {
        this.downloads = downloads;
    }

    public List<UserRental> getRentals() {
        return rentals;
    }

    public void setRentals(List<UserRental> rentals) {
        this.rentals = rentals;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

//    public List<String> getActualContentLanguages() {
//        if(getUserSelectedContentLanguages() != null && getUserSelectedContentLanguages().size()>0 )
//            return getUserSelectedContentLanguages();
//        else
//            return getContentLanguages();
//    }

    public List<String> getContentLanguages() {
        return contentLanguages;
    }

    public void setContentLanguages(List<String> contentLanguages) {
        this.contentLanguages = contentLanguages;
    }

    public List<String> getSelectedLanguages() {
        if (!CollectionUtils.isEmpty(onboardingLanguages))
            return onboardingLanguages;
        return contentLanguages;
    }

    public Set<String> getPodcastCategories() {
        return podcastCategories;
    }

    public void setPodcastCategories(Set<String> podcastCategories) {
        this.podcastCategories = podcastCategories;
    }

//    public List<String> getUserSelectedContentLanguages() {
//        return userSelectedContentLanguages;
//    }
//
//    public void setUserSelectedContentLanguages(List<String> userSelectedContentLanguages) {
//        this.userSelectedContentLanguages = userSelectedContentLanguages;
//    }

    public String getFbToken() {
        return fbToken;
    }

    public void setFbToken(String fbToken) {
        this.fbToken = fbToken;
    }

    public String getiTunesSubscription() {
        return iTunesSubscription;
    }

    public void setiTunesSubscription(String itunesSubscription) {
        this.iTunesSubscription = itunesSubscription;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }

    public String toJson() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put(UserEntityKey.id, getId());
        jsonObj.put(UserEntityKey.uid, getUid());
        jsonObj.put(UserEntityKey.creationDate, getCreationDate());
        jsonObj.put(UserEntityKey.lastActivityDate, getLastActivityDate());
        jsonObj.put(UserEntityKey.msisdn, getMsisdn());
        jsonObj.put(UserEntityKey.name, getName());
        jsonObj.put(UserEntityKey.lname, getLowercaseUsername());
        jsonObj.put(UserEntityKey.email, getEmail());
        jsonObj.put(UserEntityKey.lang, getLang());
        jsonObj.put(UserEntityKey.preferredLang, getPreferredLanguage());
        jsonObj.put(UserEntityKey.gender, getGender());
        jsonObj.put(UserEntityKey.operator, getOperator());
        jsonObj.put(UserEntityKey.token, getToken());
        jsonObj.put(UserEntityKey.circle, getCircle());
        jsonObj.put(UserEntityKey.notifications, true);
        jsonObj.put(UserEntityKey.autoPlaylists, isCreatePlaylistsForDls());
        jsonObj.put(UserEntityKey.songQuality, getSongQuality());
        jsonObj.put(UserEntityKey.downloadQuality, getDownloadQuality());
        jsonObj.put(UserEntityKey.source, getSource());
        jsonObj.put(UserEntityKey.lastAutoRenewalOffSettingTimestamp, getLastAutoRenewalOffSettingTimestamp());
        jsonObj.put(UserEntityKey.isSystemGeneratedContentLang, isSystemGeneratedContentLang);
        jsonObj.put(UserEntityKey.autoRenewal, isAutoRenewal());
        jsonObj.put(UserEntityKey.countryId,getCountryId());
        jsonObj.put(UserEntityKey.WynkBasicKeys.basicHasManuallySelectedLang, isBasicHasManuallySelectedLangauge());
        jsonObj.put(UserEntityKey.ndsTS, getNdsTS());
        if (getDob() != null)
            jsonObj.put(UserEntityKey.Dob.dob, getDob().toJsonObject());
        if (getExpireAt() > 0)
            jsonObj.put(UserEntityKey.expireAt, getExpireAt());

        if (getBasicContentLanguage() != null)
            jsonObj.put(UserEntityKey.WynkBasicKeys.basicContentLanguages, Utils.convertToJSONArray(getBasicContentLanguage()));


        if (getBasicSelectedArtist() != null)
            jsonObj.put(UserEntityKey.WynkBasicKeys.basicSelectedArtist, Utils.convertToJSONArray(getBasicSelectedArtist()));


        if (getBasicSelectedPlaylist() != null)
            jsonObj.put(UserEntityKey.WynkBasicKeys.basicSelectedPlaylist, Utils.convertToJSONArray(getBasicSelectedPlaylist()));

        if (getContentLanguages() != null && contentLanguages.size() > 0)
            jsonObj.put(UserEntityKey.contentLanguages, Utils.convertToJSONArray(getContentLanguages()));
//        if (getUserSelectedContentLanguages() != null && getUserSelectedContentLanguages().size() > 0)
//            jsonObj.put(UserEntityKey.contentLanguages, Utils.convertToJSONArray(getUserSelectedContentLanguages()));
        if (getOnboardingLanguages() != null && !onboardingLanguages.isEmpty())
            jsonObj.put(UserEntityKey.onboardingLanguages, Utils.convertToJSONArray(getOnboardingLanguages()));
        if (getPodcastCategories() != null && podcastCategories.size() > 0)
            jsonObj.put(UserEntityKey.podcastCategories, Utils.convertToJSONArray(getPodcastCategories()));

        if (getCurrentOfferIds() != null && getCurrentOfferIds().size() > 0) {
            jsonObj.put(UserEntityKey.currentOfferIds, Utils.convertIntegerListToJSONArrayGeneric(getCurrentOfferIds()));
        }
        if (!StringUtils.isEmpty(getVasDND()))
            jsonObj.put(UserEntityKey.vasDND, getVasDND());
        if (!StringUtils.isEmpty(getUserType()))
            jsonObj.put(UserEntityKey.userType, getUserType());
        if (!StringUtils.isEmpty(getFbToken()))
            jsonObj.put(UserEntityKey.fbToken, getFbToken());
        if (getPlatform() != null)
            jsonObj.put(UserEntityKey.platform, getPlatform());
        if (!StringUtils.isEmpty(getiTunesSubscription()))
            jsonObj.put(UserEntityKey.iTunesSubscription, getiTunesSubscription());
        if (!StringUtils.isEmpty(getAvatar()))
            jsonObj.put(UserEntityKey.avatar, getAvatar());
        if (getSubscription() != null) {
            jsonObj.put(UserEntityKey.subscription, getSubscription().toJsonObject());
        }
        if (getUserSubscription() != null) {
            try{
                jsonObj.put(UserEntityKey.userSubscription, JsonUtils.getJsonObjectFromString(getUserSubscription().toJson()));
            }catch (Exception e){
                logger.error("Error while converting user subs object : {}", e);
            }
        }
        JSONArray deviceArray = new JSONArray();
        if (getDevices() != null) {
            for (int i = 0; i < getDevices().size(); i++) {
                UserDevice userDevice = getDevices().get(i);
                deviceArray.add(userDevice.toJsonObject());
            }
        }
        jsonObj.put(UserEntityKey.devices, deviceArray);

        JSONObject packsObject = new JSONObject();
        if (getPacks() != null) {
            Iterator<String> iterator = getPacks().keySet().iterator();
            while (iterator.hasNext()) {
                String packType = (String) iterator.next();
                MusicPack pack = (MusicPack) getPacks().get(packType);
                packsObject.put(packType, pack.toJsonObject());
            }
        }
        jsonObj.put(UserEntityKey.packs, packsObject);
        jsonObj.put(UserEntityKey.isDeleted, isDeleted());
        return jsonObj.toString();
    }

    public void loadUserProfile(JSONObject jsonObj) {
        if (StringUtils.isNotBlank(getUid()))
            jsonObj.put("uid", getUid());
        if (StringUtils.isNotBlank(getName()))
            jsonObj.put("name", getName());
        if (StringUtils.isNotBlank(getEmail()))
            jsonObj.put("email", getEmail());
        if (StringUtils.isNotBlank(getLang()))
            jsonObj.put("lang", getLang());
        else{
            jsonObj.put("lang", Language.ENGLISH.getId());
        }
        if (!StringUtils.isEmpty(getGender()))
            jsonObj.put("gender", getGender());
        if (!StringUtils.isEmpty(getOperator()))
            jsonObj.put("carrier", getOperator());
        jsonObj.put("token", getToken());
        if(getDob() != null){
            JSONObject dobObj = getDob().toJsonObject();
            if (dobObj.size() > 0)
                jsonObj.put("dob", dobObj);
        }
        if (StringUtils.isNotBlank(getSongQuality())) {
            jsonObj.put("songQuality", getSongQuality());
        } else {
            jsonObj.put("songQuality", "a");
        }
        if (StringUtils.isNotBlank(getDownloadQuality())) {
            jsonObj.put("downloadQuality", getDownloadQuality());
        } else {
            jsonObj.put("downloadQuality", "h");
        }

        // first checking if onboardingLangs are present, otherwise falling back to legacy contentLanguages
        JSONArray contentLangsArray = null;
        if (getOnboardingLanguages() != null && !onboardingLanguages.isEmpty()) {
            contentLangsArray =  Utils.convertToJSONArray(getOnboardingLanguages());
        } else if (getContentLanguages() != null) {
            contentLangsArray = Utils.convertToJSONArray(getContentLanguages());
        }
        if (contentLangsArray != null) {
            if (!MusicBuildUtils.isSrilankaSupported())
                contentLangsArray.remove(MusicContentLanguage.SINHALESE.getId());
            if (contentLangsArray.contains(MusicContentLanguage.HARYANVI.getId())) {
                int index = contentLangsArray.indexOf(MusicContentLanguage.HARYANVI.getId());
                contentLangsArray.remove(MusicContentLanguage.HARYANVI.getId());
                contentLangsArray.add(index, MusicConstants.HARYANVI_ON_APP);
            }
            jsonObj.put("selectedContentLangs", contentLangsArray);
        }

        jsonObj.put("isSystemGeneratedContentLang", isSystemGeneratedContentLang);

        if (!StringUtils.isEmpty(getAvatar()))
            jsonObj.put("avatar", getAvatar());

        if (null != isAutoRenewal())
            jsonObj.put("autoRenewal", isAutoRenewal());
        jsonObj.put("lastAutoRenewalOffSettingTimestamp", getLastAutoRenewalOffSettingTimestamp());
        jsonObj.put("notifications", isNotifications());

        if (StringUtils.isNotBlank(getMsisdn()))
            jsonObj.put("isRegistered", true);
        else
            jsonObj.put("isRegistered", false);

        jsonObj.put("autoPlaylists", isCreatePlaylistsForDls());

        if (Objects.nonNull(isDeleted()))
            jsonObj.put(UserEntityKey.isDeleted, isDeleted());

        // TODO - Temporarily Logging user profile
        // logger.info("loadUserProfile - " + jsonObj.toJSONString());
    }

    public JSONObject toUserProfile() {
        JSONObject jsonObj = new JSONObject();
        loadUserProfile(jsonObj);
        return jsonObj;
    }

    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }

    private void fromJsonObject(JSONObject jsonObj) {
        Object idobj = jsonObj.get("_id");
        if (idobj instanceof String) {
            setId((String) idobj);
        } else if (idobj instanceof JSONObject) {
            setId((String) ((JSONObject) jsonObj.get("_id")).get("$oid"));
        }
        setUid((String) jsonObj.get(UserEntityKey.uid));
        if (jsonObj.get(UserEntityKey.msisdn) != null)
            setMsisdn((String) jsonObj.get(UserEntityKey.msisdn));
        if(jsonObj.get(UserEntityKey.countryId)!=null)
            setCountryId((String)jsonObj.get(UserEntityKey.countryId));
        if (jsonObj.get(UserEntityKey.name) != null)
            setName((String) jsonObj.get(UserEntityKey.name));
        if (jsonObj.get(UserEntityKey.email) != null)
            setEmail((String) jsonObj.get(UserEntityKey.email));
        if (jsonObj.get(UserEntityKey.lang) != null)
            setLang((String) jsonObj.get(UserEntityKey.lang));
        if (jsonObj.get(UserEntityKey.preferredLang) != null)
            setPreferredLanguage((String) jsonObj.get(UserEntityKey.preferredLang));
        if (jsonObj.get(UserEntityKey.gender) != null)
            setGender(((String) jsonObj.get(UserEntityKey.gender)));
        if (jsonObj.get(UserEntityKey.songQuality) != null)
            setSongQuality(((String) jsonObj.get(UserEntityKey.songQuality)));
        if (jsonObj.get(UserEntityKey.downloadQuality) != null)
            setDownloadQuality(((String) jsonObj.get(UserEntityKey.downloadQuality)));
        if (jsonObj.get(UserEntityKey.operator) != null) {
            Object o = jsonObj.get(UserEntityKey.operator);
            if(o instanceof String)
                setOperator(((String) jsonObj.get(UserEntityKey.operator)));

        }
        if (jsonObj.get(UserEntityKey.token) != null)
            setToken(((String) jsonObj.get(UserEntityKey.token)));
        if (jsonObj.get(UserEntityKey.circle) != null)
            setCircle((String) jsonObj.get(UserEntityKey.circle));
        if (jsonObj.get(UserEntityKey.avatar) != null)
            setAvatar(((String) jsonObj.get(UserEntityKey.avatar)));
        if (jsonObj.get(UserEntityKey.fbToken) != null) {
            if(jsonObj.get(UserEntityKey.fbToken) instanceof String)
            {
                setFbToken((String) jsonObj.get(UserEntityKey.fbToken));
            }
            else if(jsonObj.get(UserEntityKey.fbToken) instanceof Long){
                long fbt = (Long) jsonObj.get(UserEntityKey.fbToken);
                setFbToken(Long.toString(fbt));
            }
        }
        if (jsonObj.get(UserEntityKey.notifications) != null)
            setNotifications((Boolean) jsonObj.get(UserEntityKey.notifications));

        if (jsonObj.get(UserEntityKey.autoPlaylists) != null) {
            if(jsonObj.get(UserEntityKey.autoPlaylists) instanceof Boolean)
                setAutoPlaylists((Boolean) jsonObj.get(UserEntityKey.autoPlaylists));
            else if(jsonObj.get(UserEntityKey.autoPlaylists) instanceof String)
                setAutoPlaylists(Boolean.parseBoolean((String)jsonObj.get(UserEntityKey.autoPlaylists)));
        }

        if (jsonObj.get(UserEntityKey.platform) != null)
            setPlatform(((Number) jsonObj.get(UserEntityKey.platform)).intValue());
        else
            setPlatform(MusicUtils.getPlatfromIdFromUid(getUid()));

        if (jsonObj.get(UserEntityKey.creationDate) != null) {
            Object o = jsonObj.get(UserEntityKey.creationDate);
            if(o instanceof Long) {
                long time = (Long) jsonObj.get(UserEntityKey.creationDate);
                setCreationDate(time);
            }
        }
        if (jsonObj.get(UserEntityKey.ndsTS) != null) {
            Object o = jsonObj.get(UserEntityKey.ndsTS);
            if(o instanceof Long) {
                long time = (Long) jsonObj.get(UserEntityKey.ndsTS);
                setNdsTS(time);
            }
        }
        if (jsonObj.get(UserEntityKey.lastActivityDate) != null) {
            Object o = jsonObj.get(UserEntityKey.lastActivityDate);
            if(o instanceof Long) {
                long time = (Long) jsonObj.get(UserEntityKey.lastActivityDate);
                setLastActivityDate(time);
            }
        }
//        if (jsonObj.get(UserEntityKey.expireAt) != null) {
//            String expireAtString = (String)jsonObj.get(UserEntityKey.expireAt);
//            long time = (Long) jsonObj.get(UserEntityKey.expireAt);
//            setExpireAt(time);
//        }
        if (jsonObj.get(UserEntityKey.iTunesSubscription) != null)
            setiTunesSubscription((String) jsonObj.get(UserEntityKey.iTunesSubscription));

        if (jsonObj.get(UserEntityKey.currentOfferIds) != null) {
            try {
                Object object = jsonObj.get(UserEntityKey.currentOfferIds);
                List<Integer> ids = Utils.convertToIntegerList((JSONArray) jsonObj.get(UserEntityKey.currentOfferIds));
                setCurrentOfferIds(new LinkedList<Integer>(ids));
            } catch (Exception e) {
                logger.error("Error while converting coids list from : " + jsonObj.get(UserEntityKey.currentOfferIds), e);
            }
        }

        if (jsonObj.get(UserEntityKey.contentLanguages) != null) {
            try {
                Object object = jsonObj.get(UserEntityKey.contentLanguages);
                if (object instanceof String) {
                    List<String> strings = JsonUtils.stringToList((String) object, String[].class);
                    setContentLanguages(strings);
                } else {
                    setContentLanguages(Utils.convertToStringList((JSONArray) jsonObj.get(UserEntityKey.contentLanguages)));
                }
//                setSystemGeneratedContentLang(true);
            } catch (Exception e) {
                logger.error("Error while converting clang list from : " + jsonObj.get(UserEntityKey.contentLanguages), e);
            }
        }

        if (jsonObj.get(UserEntityKey.onboardingLanguages) != null) {
            try {
                Object obj = jsonObj.get(UserEntityKey.onboardingLanguages);
                if (obj instanceof String) {
                    List<String> strings = JsonUtils.stringToList((String) obj, String[].class);
                    setOnboardingLanguages(strings);
                } else {
                    setOnboardingLanguages(Utils.convertToStringList((JSONArray) jsonObj.get(UserEntityKey.onboardingLanguages)));
                }
            } catch (Exception e) {
                logger.error("Error while converting oblang list from : " + jsonObj.get(UserEntityKey.onboardingLanguages), e);
            }
        }

        //do the same for onboarding Lang

        if (jsonObj.get(UserEntityKey.podcastCategories) != null) {
            try {
                Object object = jsonObj.get(UserEntityKey.podcastCategories);
                if (object instanceof String) {
                    List<String> strings = JsonUtils.stringToList((String) object, String[].class);
                    setPodcastCategories(new HashSet<String>(strings));
                } else {
                    setPodcastCategories(new HashSet<String>(Utils.convertToStringList((JSONArray) jsonObj.get(UserEntityKey.podcastCategories))));
                }
            } catch (Exception e) {
                logger.error("Error while converting podcast_categories list from : " + jsonObj.get(UserEntityKey.podcastCategories), e);
            }
        }

        if (jsonObj.get(UserEntityKey.Dob.dob) != null) {
            try {
                String dobObject = jsonObj.get(UserEntityKey.Dob.dob).toString();
                UserDob dob = new UserDob();
                dob.fromJson(dobObject);
                setDob(dob);
            } catch (Throwable th) {
                logger.error("Error in Parsing the date of birth json String");
            }
        }

        if (jsonObj.get(UserEntityKey.devices) != null) {
            JSONArray deviceArray = (JSONArray) jsonObj.get(UserEntityKey.devices);
            List<UserDevice> devices = new ArrayList<>();
            if (deviceArray != null) {
                for (int k = 0; k < deviceArray.size(); k++) {
                    JSONObject deviceObj = (JSONObject) deviceArray.get(k);
                    UserDevice userDevice = new UserDevice();
                        userDevice.fromJsonObject(deviceObj,false);
                    devices.add(userDevice);
                }
                setDevices(devices);
            }
        }

        if (jsonObj.get(JsonKeyNames.STREAMED_COUNT) instanceof Number) {
            setStreamedCount(((Number) jsonObj.get(JsonKeyNames.STREAMED_COUNT)).intValue());
        }
        if (jsonObj.get(JsonKeyNames.RENTALS_COUNT) instanceof Number) {
            setRentalsCount(((Number) jsonObj.get(JsonKeyNames.RENTALS_COUNT)).intValue());
        }

        if (jsonObj.get(UserEntityKey.userType) != null)
            setUserType((String) jsonObj.get(UserEntityKey.userType));

        if (jsonObj.get(UserEntityKey.vasDND) != null)
            setVasDND((String) jsonObj.get(UserEntityKey.vasDND));

        if (jsonObj.get(UserEntityKey.subscription) != null) {
            try {
                String subscriptionObject = jsonObj.get(UserEntityKey.subscription).toString();
                WCFSubscription wcfSubscription = new WCFSubscription();
                wcfSubscription.fromJson(subscriptionObject);
                setSubscription(wcfSubscription);
            } catch (Throwable th) {
                logger.error("Error in Parsing the json String");
            }
        }

        if (jsonObj.get(UserEntityKey.source) != null)
            setSource(((Number) jsonObj.get(UserEntityKey.source)).intValue());

        if (jsonObj.get(UserEntityKey.lastAutoRenewalOffSettingTimestamp) != null) {
            long time = (Long) jsonObj.get(UserEntityKey.lastAutoRenewalOffSettingTimestamp);
            setLastAutoRenewalOffSettingTimestamp(time);
        }
        if (jsonObj.get(UserEntityKey.autoRenewal) != null)
            setAutoRenewal((Boolean) jsonObj.get(UserEntityKey.autoRenewal));

        if (jsonObj.get(UserEntityKey.isSystemGeneratedContentLang) != null)
            setSystemGeneratedContentLang((Boolean) jsonObj.get(UserEntityKey.isSystemGeneratedContentLang));

        if (jsonObj.get(UserEntityKey.WynkBasicKeys.basicHasManuallySelectedLang) != null)
            setBasicHasManuallySelectedLangauge((Boolean) jsonObj.get(UserEntityKey.WynkBasicKeys.basicHasManuallySelectedLang));

        if (jsonObj.get(UserEntityKey.WynkBasicKeys.basicContentLanguages) != null) {
            try {
                    setBasicContentLanguage(Utils.convertToStringList((JSONArray) jsonObj.get(UserEntityKey.WynkBasicKeys.basicContentLanguages)));
            } catch (Exception e) {
                logger.error("Error while converting basic clang list to String list for uid : " + getUid(), e);
            }
        }

        if (jsonObj.get(UserEntityKey.WynkBasicKeys.basicSelectedArtist) != null) {
            try {
                setBasicSelectedArtist(Utils.convertToStringList((JSONArray) jsonObj.get(UserEntityKey.WynkBasicKeys.basicSelectedArtist)));
            } catch (Exception e) {
                logger.error("Error while converting basic selected artists list to String list for uid : " + getUid(), e);
            }
        } 
        
        if (jsonObj.get(UserEntityKey.WynkBasicKeys.basicSelectedPlaylist) != null) {
            try {
                setBasicSelectedPlaylist(Utils.convertToStringList((JSONArray) jsonObj.get(UserEntityKey.WynkBasicKeys.basicSelectedPlaylist)));
            } catch (Exception e) {
                logger.error("Error while converting basic selected playlists list to String list for uid : " + getUid() , e);
            }
        }

        if (jsonObj.get(UserEntityKey.packs) != null) {
            JSONObject packsObj = (JSONObject) jsonObj.get(UserEntityKey.packs);

            if (packsObj != null) {
                LinkedHashMap<String, MusicPack> packsMap = new LinkedHashMap<String, MusicPack>();
                for (MusicPackType packType : MusicPackType.values()) {
                    String type = packType.getType();
                    JSONObject packJson = (JSONObject) packsObj.get(type);

                    if (packJson == null)
                        continue;

                    // TODO - Refactor this 
                    if (type.equalsIgnoreCase(FUPPack.class.getSimpleName())) {
                        FUPPack musicPack = new FUPPack();
                        musicPack.fromJsonObject(packJson);
                        setFupPack(musicPack);
                        packsMap.put(type, musicPack);
                    }
                }
                setPacks(packsMap);
            }
        }

        if (ObjectUtils.notEmpty(jsonObj.get(UserEntityKey.userSubscription))) {
            try {
                String subscriptionObject = jsonObj.get(UserEntityKey.userSubscription).toString();
                UserSubscription wcfSubscription = new UserSubscription();
                wcfSubscription = wcfSubscription.fromJson(subscriptionObject);
                setUserSubscription(wcfSubscription);
            } catch (Throwable th) {
                logger.error("Error in Parsing the user subscription json String");
            }
        }

        if (jsonObj.get(UserEntityKey.isDeleted) != null) {
            Object o = jsonObj.get(UserEntityKey.isDeleted);
            if (o instanceof Boolean) {
                Boolean isDeleted = (Boolean) jsonObj.get(UserEntityKey.isDeleted);
                setDeleted(isDeleted);
            }
        }

    }

    public UserDevice getActiveDevice() {
        if (CollectionUtils.isEmpty(getDevices()))
            return null;

        for (int i = 0; i < getDevices().size(); i++) {
            UserDevice userDevice = getDevices().get(i);
            if (StringUtils.isEmpty(userDevice.getDeviceId()))
                continue;

            if (userDevice.isActive())
                return userDevice;
        }
        return null;
    }

    public UserDevice getDevice(String deviceId) {
        if (CollectionUtils.isEmpty(getDevices()))
            return null;
        for (int i = 0; i < getDevices().size(); i++) {
            UserDevice userDevice = getDevices().get(i);
            if (StringUtils.isEmpty(userDevice.getDeviceId()))
                continue;

            if (userDevice.getDeviceId().equalsIgnoreCase(deviceId))
                return userDevice;
        }
        return null;

    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int getStreamedCount() {
        return streamedCount;
    }

    public void setStreamedCount(int streamedCount) {
        this.streamedCount = streamedCount;
    }

    public int getRentalsCount() {
        return rentalsCount;
    }

    public void setRentalsCount(int rentalsCount) {
        this.rentalsCount = rentalsCount;
    }

    public boolean isCreatePlaylistsForDls() {
        return autoPlaylists;
    }

    public long getLastAutoRenewalOffSettingTimestamp() {
        return lastAutoRenewalOffSettingTimestamp;
    }

    public void setLastAutoRenewalOffSettingTimestamp(
            long lastAutoRenewalOffSettingTimestamp) {
        this.lastAutoRenewalOffSettingTimestamp = lastAutoRenewalOffSettingTimestamp;
    }


    public String getToken() {
        return token;
    }


    public void setToken(String token) {
        this.token = token;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public WCFSubscription getSubscription() {
        return subscription;
    }

    public void setSubscription(WCFSubscription subscription) {
        this.subscription = subscription;
    }

    public UserSubscription getUserSubscription() {
        return userSubscription;
    }

    public void setUserSubscription(UserSubscription subscription) {
        this.userSubscription = subscription;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", countryId='" + countryId + '\'' +
                ", name='" + name + '\'' +
                ", lname='" + lname + '\'' +
                ", email='" + email + '\'' +
                ", lang='" + lang + '\'' +
                ", gender='" + gender + '\'' +
                ", creationDate=" + creationDate +
                ", lastActivityDate=" + lastActivityDate +
                ", isActive=" + isActive +
                ", circle='" + circle + '\'' +
                ", songQuality='" + songQuality + '\'' +
                ", avatar='" + avatar + '\'' +
                ", autoRenewal=" + autoRenewal +
                ", notifications=" + notifications +
                ", fbToken='" + fbToken + '\'' +
                ", dob=" + dob +
                ", iTunesSubscription='" + iTunesSubscription + '\'' +
                ", contentLanguages=" + contentLanguages +
                ", onboardingLanguages=" + onboardingLanguages +
                ", podcastCategories=" + podcastCategories +
                ", preferredLanguage='" + preferredLanguage + '\'' +
                ", devices=" + devices +
                ", favorites=" + favorites +
                ", downloads=" + downloads +
                ", rentals=" + rentals +
                ", follows=" + follows +
                ", streamedCount=" + streamedCount +
                ", rentalsCount=" + rentalsCount +
                ", currentOfferIds=" + currentOfferIds +
                ", operator='" + operator + '\'' +
                ", source=" + source +
                ", platform=" + platform +
                ", autoPlaylists=" + autoPlaylists +
                ", downloadQuality='" + downloadQuality + '\'' +
                ", lastAutoRenewalOffSettingTimestamp=" + lastAutoRenewalOffSettingTimestamp +
                ", fupPack=" + fupPack +
                ", token='" + token + '\'' +
                ", userType='" + userType + '\'' +
                ", referrer='" + referrer + '\'' +
                ", galaxyPurchaseCount=" + galaxyPurchaseCount +
                ", subscription=" + subscription +
                ", userSubscription=" + userSubscription +
                ", ndsTS=" + ndsTS +
                ", expireAt=" + expireAt +
                ", isSystemGeneratedContentLang=" + isSystemGeneratedContentLang +
                ", basicSelectedArtist=" + basicSelectedArtist +
                ", basicSelectedPlaylist=" + basicSelectedPlaylist +
                ", basicContentLanguage=" + basicContentLanguage +
                ", basicHasManuallySelectedLangauge=" + basicHasManuallySelectedLangauge +
                ", vasDND='" + vasDND + '\'' +
                "isDeleted=" + isDeleted +
                ", packs=" + packs +
                '}';
    }
}
