package com.wynk.musicpacks;

import com.wynk.config.MusicConfig;
import com.wynk.db.MongoDBManager;
import com.wynk.db.ShardedRedisServiceManager;
import com.wynk.dto.MusicSubscriptionStatus;
import com.wynk.service.AccountService;
import com.wynk.service.MyAccountService;
import com.wynk.service.WCFService;
import com.wynk.user.dto.UserEntityKey;
import com.wynk.utils.WCFUtils;
import com.wynk.wcf.WCFApisService;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public abstract class MusicPack {

    private long    creationTime;
    private long    packValidity;

    public abstract String getPackType();

    public abstract Boolean isEnabled();

    public abstract Boolean checkIfStreamAvailable();

    public abstract Boolean updateStreamingCount(int count);
    
    public abstract Boolean isApplicableForStreaming();
    
    public abstract JSONObject getConfigResponseJson(MusicSubscriptionStatus subscriptionStatus);

    private MusicConfig                      musicConfig;
    private MongoDBManager                   mongoUserDBManager;
    private AccountService                   accountService;
    private ShardedRedisServiceManager userPersistantRedisServiceManager;
    private WCFService wcfService;
    private MyAccountService myAccountService;
    private WCFUtils wcfUtils;
    private WCFApisService wcfApisService;

    static final String USER_COLLECTION = "users";

    public MusicPack() {
    }

    public MusicPack(long creationTime, long packValidity) {
        this.creationTime = creationTime;
        this.packValidity = packValidity;
    }

    protected MusicConfig getMusicConfig() {
        return musicConfig;
    }

    public void setMusicConfig(MusicConfig musicConfig) {
        this.musicConfig = musicConfig;
    }

    protected MongoDBManager getMongoUserDBManager() {
        return mongoUserDBManager;
    }

    public void setMongoUserDBManager(MongoDBManager mongoUserDBManager) {
        this.mongoUserDBManager = mongoUserDBManager;
    }

    protected AccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
    
    public ShardedRedisServiceManager getUserRedisServiceManager() {
        return userPersistantRedisServiceManager;
    }
    
    public void setUserRedisServiceManager(ShardedRedisServiceManager userPersistantRedisServiceManager) {
        this.userPersistantRedisServiceManager = userPersistantRedisServiceManager;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getPackValidity() {
        return packValidity;
    }

    public void setPackValidity(long packValidity) {
        this.packValidity = packValidity;
    }

    public WCFApisService getWcfService() {
        return wcfApisService;
    }

    public MyAccountService getMyAccountService() {
        return myAccountService;
    }

    public void setMyAccountService(MyAccountService myAccountService) {
        this.myAccountService = myAccountService;
    }

    public WCFUtils getWcfUtils() {
        return wcfUtils;
    }

    public void setWcfUtils(WCFUtils wcfUtils) {
        this.wcfUtils = wcfUtils;
    }

    public void setWcfService(WCFService wcfService) {
        this.wcfService = wcfService;
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put(UserEntityKey.FupPack.creationTime, creationTime);
        jsonObj.put(UserEntityKey.FupPack.packValidity, packValidity);
        return jsonObj;
    }

    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }

    public void fromJsonObject(JSONObject jsonObj) {

        if(jsonObj.get(UserEntityKey.FupPack.creationTime) != null) {
            long creationTime = (Long) jsonObj.get(UserEntityKey.FupPack.creationTime);
            setCreationTime(creationTime);
        }
        
        if(jsonObj.get(UserEntityKey.FupPack.packValidity) != null) {
            long packValidity = (Long) jsonObj.get(UserEntityKey.FupPack.packValidity);
            setPackValidity(packValidity);
        }
    }

}
