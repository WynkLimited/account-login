package com.wynk.service;

import com.wynk.constants.JsonKeyNames;
import com.wynk.constants.MusicConstants;
import com.wynk.dto.SubscriptionIntentDTO;
import com.wynk.dto.WCFSubscription;
import com.wynk.music.WCFServiceType;
import com.wynk.notification.MusicAdminNotification;
import com.wynk.subscription.WCFSubscriptionPack;
import com.wynk.user.dto.User;
import com.wynk.utils.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aakash on 01/08/17.
 */

@Service
public class SubscriptionIntentService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionIntentService.class.getCanonicalName());

    @Autowired
    private WCFUtils wcfUtils;

    public static List<Integer> adsSongIntervalConfig = new ArrayList<>();

    static {
        adsSongIntervalConfig.add(50);
        adsSongIntervalConfig.add(80);
        adsSongIntervalConfig.add(90);
    }

    public JSONObject getSubscriptionIntent(){

        JSONObject subscriptionIntentObject = new JSONObject();

        List<SubscriptionIntentDTO> offlineDownloadCardList = SubscriptionIntentUtils.getOfflineDownloadCards();
        if(!CollectionUtils.isEmpty(offlineDownloadCardList)){
            JSONArray offlineDownloadJsonArray = new JSONArray();
            for(SubscriptionIntentDTO subscriptionIntentDTO : offlineDownloadCardList){
                offlineDownloadJsonArray.add(getSubscriptionIntentPopUp(subscriptionIntentDTO));
            }
            subscriptionIntentObject.put(JsonKeyNames.OFFLINE_INTENT_PAYLOAD,offlineDownloadJsonArray);
        }

        subscriptionIntentObject.put(JsonKeyNames.FUP_INTENT_PAYLOAD,getFUPIntent());
        subscriptionIntentObject.put(JsonKeyNames.DOWNLOAD_INTENT_PAYLOAD,getDownloadIntent());

        List<SubscriptionIntentDTO> adsCardList = SubscriptionIntentUtils.getRemoveAdsSubscriptionCards();
        if (!CollectionUtils.isEmpty(adsCardList)) {
            JSONArray adsCardJsonArray = new JSONArray();
            for(SubscriptionIntentDTO subscriptionIntentDTO : adsCardList){
                adsCardJsonArray.add(getSubscriptionIntentPopUp(subscriptionIntentDTO));
            }
            subscriptionIntentObject.put(JsonKeyNames.ADS_INTENT_PAYLOAD,adsCardJsonArray);
        }

        return subscriptionIntentObject;
    }

    public JSONArray getFUPIntent(){
        JSONArray fupIntentJsonArray = null;

        List<SubscriptionIntentDTO> fupIntentList = SubscriptionIntentUtils.getFupLimitIntentCards();
        if(!CollectionUtils.isEmpty(fupIntentList)){
            fupIntentJsonArray = new JSONArray();
            for(SubscriptionIntentDTO subscriptionIntentDTO : fupIntentList){
                fupIntentJsonArray.add(getSubscriptionIntentPopUp(subscriptionIntentDTO));
            }
        }

        return fupIntentJsonArray;
    }

    public JSONArray getDownloadIntent(){
        JSONArray downloadIntentArray = null;
        List<SubscriptionIntentDTO> downloadIntentList = SubscriptionIntentUtils.getUnsubscribedDownloadCards();
        if(!CollectionUtils.isEmpty(downloadIntentList)){
            downloadIntentArray = new JSONArray();
            for(SubscriptionIntentDTO subscriptionIntentDTO : downloadIntentList){
                downloadIntentArray.add(getSubscriptionIntentPopUp(subscriptionIntentDTO));
            }
        }

        return downloadIntentArray;
    }

    public static JSONObject getSubscriptionIntentPopUp(SubscriptionIntentDTO subscriptionIntentDTO){
        MusicAdminNotification registerNotification = new MusicAdminNotification();
        registerNotification.setNotificationId(String.valueOf(Utils.getRandomNumber(10000)));
        registerNotification.setTitle(subscriptionIntentDTO.getHeader());
        registerNotification.setText(subscriptionIntentDTO.getSubHeader());
        registerNotification.setIconUrl(subscriptionIntentDTO.getIconUrl());
        registerNotification.setProcessed(false);
        registerNotification.setDeleted(false);
        JSONObject CARD_JSON = registerNotification.getRichAndroidMessageJsonObject();
        CARD_JSON.put("aok", "Okay!");

        return CARD_JSON;
    }

    public static JSONObject getRemoveAdsIntentConfig(){
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(JsonKeyNames.ADS_FMF_COUNT, MusicConstants.FMF_SONGS_ADS_COUNT);
        JSONArray jsonArray = new JSONArray();
        for(Integer songCountInterval : adsSongIntervalConfig){
            jsonArray.add(songCountInterval);
        }

        if(jsonArray.size() > 0){
            jsonObject.put(JsonKeyNames.ADS_SONG_PLAYED_COUNT,jsonArray);
        }

        return jsonObject;
    }
}
