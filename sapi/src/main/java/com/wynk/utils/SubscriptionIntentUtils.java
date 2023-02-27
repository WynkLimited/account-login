package com.wynk.utils;

import com.wynk.constants.SubscriptionIntentEnum;
import com.wynk.dto.SubscriptionIntentDTO;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aakash on 31/07/17.
 */
public class SubscriptionIntentUtils {

    private static Map<Integer,SubscriptionIntentDTO> subscriptionIntentDTOMap = new HashMap<>();
    private static Map<SubscriptionIntentEnum,List<Integer>> subscriptionIntentListMap = new HashMap<>();
    private static List<SubscriptionIntentDTO> removeAdsSubscriptionCards = new ArrayList<>();
    private static List<SubscriptionIntentDTO> fupLimitIntentCards = new ArrayList<>();
    private static List<SubscriptionIntentDTO> unsubscribedDownloadCards = new ArrayList<>();
    private static List<SubscriptionIntentDTO> offlineDownloadCards = new ArrayList<>();
    private static List<SubscriptionIntentDTO> fmfExpireCards = new ArrayList<>();

    static {
        loadIntentMap();
        loadAllCardDetails();
        loadIntentCards();
    }

    public static void loadIntentMap(){

        List removeAdsCard = new ArrayList();
        removeAdsCard.add(1);
        removeAdsCard.add(2);
        removeAdsCard.add(3);
        subscriptionIntentListMap.put(SubscriptionIntentEnum.REMOVE_ADS, removeAdsCard);

        List fupLimitCard = new ArrayList();
        fupLimitCard.add(3);
        fupLimitCard.add(2);
        fupLimitCard.add(1);
        subscriptionIntentListMap.put(SubscriptionIntentEnum.FUP_LIMIT, fupLimitCard);

        List unsubscribedDownload = new ArrayList();
        unsubscribedDownload.add(2);
        unsubscribedDownload.add(3);
        unsubscribedDownload.add(1);
        subscriptionIntentListMap.put(SubscriptionIntentEnum.UNSUBSCRIBED_DOWNLOAD,unsubscribedDownload);

        List offlineDownload = new ArrayList();
        offlineDownload.add(4);
        subscriptionIntentListMap.put(SubscriptionIntentEnum.OFFLINE_DOWNLOADS,offlineDownload);

        List fmfExpire = new ArrayList();
        fmfExpire.add(5);
        subscriptionIntentListMap.put(SubscriptionIntentEnum.FMF_EXPIRE,fmfExpire);
    }

    public static void loadAllCardDetails(){
        subscriptionIntentDTOMap.put(1,new SubscriptionIntentDTO(1,"Ads-free music",
                "Listen to unlimited music without ads interrupting you!","1"));
        subscriptionIntentDTOMap.put(2,new SubscriptionIntentDTO(2,"Take your music offline",
                "Download songs to your device and listen to them without using any data!","3"));
        subscriptionIntentDTOMap.put(3,new SubscriptionIntentDTO(3,"Listen to Unlimited Songs",
                "Listen to as many songs as you want, without any restrictions!","2"));
        subscriptionIntentDTOMap.put(4,new SubscriptionIntentDTO(4,"Oops! Subscription required!",
                "You need an active subscription to play your downloaded songs offline.","0"));
        subscriptionIntentDTOMap.put(5,new SubscriptionIntentDTO(5,"Unlimited ad-free music",
                "Listen to as many songs as you want, online or offline, without any ads!","0"));

    }

    public static void loadIntentCards(){
        for(SubscriptionIntentEnum subscriptionIntentEnum : SubscriptionIntentEnum.values()){
            switch (subscriptionIntentEnum){
                case REMOVE_ADS:           setCards(subscriptionIntentEnum,removeAdsSubscriptionCards);
                                           break;
                case FUP_LIMIT:            setCards(subscriptionIntentEnum,fupLimitIntentCards);
                                           break;
                case UNSUBSCRIBED_DOWNLOAD:setCards(subscriptionIntentEnum,unsubscribedDownloadCards);
                                           break;
                case OFFLINE_DOWNLOADS:    setCards(subscriptionIntentEnum,offlineDownloadCards);
                                           break;
                case FMF_EXPIRE:           setCards(subscriptionIntentEnum,fmfExpireCards);
                                           break;
            }
        }
    }

    public static void setCards(SubscriptionIntentEnum subscriptionIntentEnum,List cards){
        List<Integer> cardIntentIdList = subscriptionIntentListMap.get(subscriptionIntentEnum);
        if(!CollectionUtils.isEmpty(cardIntentIdList)){
             for(Integer cardId : cardIntentIdList) {
                 cards.add(subscriptionIntentDTOMap.get(cardId));
             }
        }
    }

    public static List<SubscriptionIntentDTO> getRemoveAdsSubscriptionCards() {
        return removeAdsSubscriptionCards;
    }

    public static List<SubscriptionIntentDTO> getFupLimitIntentCards() {
        return fupLimitIntentCards;
    }

    public static List<SubscriptionIntentDTO> getUnsubscribedDownloadCards() {
        return unsubscribedDownloadCards;
    }

    public static List<SubscriptionIntentDTO> getOfflineDownloadCards() {
        return offlineDownloadCards;
    }

    public static List<SubscriptionIntentDTO> getFmfExpireCards() {
        return fmfExpireCards;
    }
}
