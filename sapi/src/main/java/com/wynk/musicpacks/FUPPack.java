package com.wynk.musicpacks;

import com.wynk.constants.JsonKeyNames;
import com.wynk.dto.MusicSubscriptionStatus;
import com.wynk.music.WCFServiceType;
import com.wynk.server.ChannelContext;
import com.wynk.user.dto.UserEntityKey;
import com.wynk.utils.*;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.wynk.constants.JsonKeyNames.*;
import static com.wynk.constants.MusicConstants.STREAMING_LIMIT_REACHED_ERROR_CODE;

public class FUPPack extends MusicPack {

    private static final Logger logger = LoggerFactory
            .getLogger(FUPPack.class.getCanonicalName());

    public FUPPack() {
    }

    ;

    public FUPPack(long creationTime, long packValidity) {
        super(creationTime, packValidity);
        setLastFUPResetDate(creationTime);
    }

    private boolean shownFUPWarning = false;
    private boolean shownFUP95Warning = false;

    private int streamedCount;
    private int rentalsCount;

    private long lastFUPResetDate;

    public void setShownFUPWarning(boolean shownFUPWarning) {
        this.shownFUPWarning = shownFUPWarning;
    }

    public void setShownFUP95Warning(boolean shownFUP95Warning) {
        this.shownFUP95Warning = shownFUP95Warning;
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

    public long getLastFUPResetDate() {
        return lastFUPResetDate;
    }

    public void setLastFUPResetDate(long lastFUPResetDate) {
        this.lastFUPResetDate = lastFUPResetDate;
    }

    @Override
    public JSONObject toJsonObject() {
        JSONObject jsonObj = super.toJsonObject();
        jsonObj.put(UserEntityKey.FupPack.shownFUPWarning, shownFUPWarning);
        jsonObj.put(UserEntityKey.FupPack.shownFUP95Warning, shownFUP95Warning);

        jsonObj.put(UserEntityKey.FupPack.streamedCount, streamedCount);
        jsonObj.put(UserEntityKey.FupPack.rentalsCount, rentalsCount);
        jsonObj.put(UserEntityKey.FupPack.lastFUPResetDate, lastFUPResetDate);

        return jsonObj;
    }

    @Override
    public void fromJsonObject(JSONObject jsonObj) {

        super.fromJsonObject(jsonObj);

        if (jsonObj.get(UserEntityKey.FupPack.streamedCount) != null) {
            setStreamedCount(((Number) jsonObj.get(UserEntityKey.FupPack.streamedCount)).intValue());
        }

        if (jsonObj.get(UserEntityKey.FupPack.rentalsCount) != null) {
            setRentalsCount(((Number) jsonObj.get(UserEntityKey.FupPack.rentalsCount)).intValue());
        }

        if (jsonObj.get(UserEntityKey.FupPack.lastFUPResetDate) != null) {
            long lastFUPResetDate = (Long) jsonObj.get(UserEntityKey.FupPack.lastFUPResetDate);
            setLastFUPResetDate(lastFUPResetDate);
        }

        if (jsonObj.get(UserEntityKey.FupPack.shownFUPWarning) != null) {
            setShownFUPWarning((Boolean) jsonObj.get(UserEntityKey.FupPack.shownFUPWarning));
        }

        if (jsonObj.get(UserEntityKey.FupPack.shownFUP95Warning) != null) {
            setShownFUP95Warning((Boolean) jsonObj.get(UserEntityKey.FupPack.shownFUP95Warning));
        }
    }

    @Override
    public String getPackType() {
        return FUPPack.class.getSimpleName();
    }

    @Override
    public Boolean isEnabled() {
        return getMusicConfig().isFupEnabled();
    }

    @Override
    public Boolean checkIfStreamAvailable() {
        boolean status = true;
        int totalCount = getStreamedCount() + getRentalsCount();
        boolean isWifi = UserDeviceUtils.isWifi(ChannelContext.getRequest());
        boolean isAirtelMobileIP = IPRangeUtil.isAirtelMobileIP(ChannelContext.getRequest());
        /* Removing Hotspot check
        if(UserDeviceUtils.isWifi(ChannelContext.getRequest())) {
            isAirtelMobileIP = false;
        }
        */
        boolean isAirtelUser = getAccountService().isUserOperatorAirtel();
        WCFServiceType wcfServiceType = getWcfUtils().getWCFServiceType(UserDeviceUtils.getPlatform());

        MusicSubscriptionStatus subStatus = getWcfService().getMusicSubscriptionStatusFromCache(wcfServiceType.getServiceName(), ChannelContext.getUser(), ChannelContext.getMsisdn(), isAirtelUser, false, null, false, false);

        // Airtel Users get unlimited streaming
        if (!isAirtelUser) {
            if (!getWcfUtils().isUserSubsActive(subStatus)) {
                if (isWAPUser(ChannelContext.getUid())) {
                    if (getStreamedCount() >= getMusicConfig().getWapStreamingLimit()) {
                        status = false;
                    }
                } else {
                    if (getStreamedCount() >= getMusicConfig().getStreamingFUPLimit()) {
                        status = false;
                    }
                }
            }
        }

        return status;
    }

    public boolean isWAPUser(String uid) {
        return uid.endsWith("4");
    }

    @Override
    public Boolean updateStreamingCount(int count) {
        Boolean status = false;
        Map<String, Object> queryValues = new HashMap<>();
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(UID, ChannelContext.getUid());
        logger.info("Current stream count " + getStreamedCount());
        logger.info("Updating to " + getStreamedCount() + count);
        queryValues.put("packs.FUPPack." + UserEntityKey.FupPack.streamedCount, count);
        getMongoUserDBManager().incrementField(USER_COLLECTION, queryParams, queryValues, false);
        return status;
    }

    @Override
    public Boolean isApplicableForStreaming() {
        return true;
    }

    @Override
    public JSONObject getConfigResponseJson(MusicSubscriptionStatus subscriptionStatus) {
        JSONObject jsonObject = new JSONObject();
        int totalAllowed = getMusicConfig().getStreamingFUPLimit();
        int current = 0;
        Boolean fup_limit_status = true;
        int errcode = 0;
        String failureUrl = "";

        int totalCount = getStreamedCount() + getRentalsCount();
        boolean isWifi = UserDeviceUtils.isWifi(ChannelContext.getRequest());
        boolean isAirtelMobileIP = IPRangeUtil.isAirtelMobileIP(ChannelContext.getRequest());
        /* Removing Hotspot check
        if(UserDeviceUtils.isWifi(ChannelContext.getRequest())) {
            isAirtelMobileIP = false;
        }
        */
        boolean isAirtelUser = getAccountService().isUserOperatorAirtel();

        if (!isAirtelUser) {
            if (!getWcfUtils().isUserSubsActive(subscriptionStatus)) {
                fup_limit_status = false;
                current = totalCount;
            }
        }
        errcode = STREAMING_LIMIT_REACHED_ERROR_CODE;

        if (subscriptionStatus != null) {
            failureUrl = subscriptionStatus.getPurchaseUrl();
        }

        jsonObject.put(JsonKeyNames.TOTAL, totalAllowed);
        jsonObject.put(JsonKeyNames.FUP_CURRENT_COUNT, current);
        jsonObject.put(JsonKeyNames.FUP_LIMIT_STATUS, fup_limit_status);

        jsonObject.put("failureUrl", failureUrl);
        return jsonObject;
    }

}
