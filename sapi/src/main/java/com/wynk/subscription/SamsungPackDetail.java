package com.wynk.subscription;

import com.wynk.dto.DefaultPackType;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by Aakash on 17/02/17.
 */
public class SamsungPackDetail extends WCFSubscriptionPack{

    private static Map<Integer,WCFSubscriptionPack> samsungMusicProductMapping = new HashMap();

    // Create Map for PackId - > <Map of Payment Method, ProductId>
    private static Map<Integer,Map<String,Integer>> wcfPackMapping = new HashMap();

    private static WCFSubscriptionPack defaultPackDetail = null;

    private static WCFSubscriptionPack defaultRecommendedPackDetail = null;

    private static Long lastUpdatedTimestamp = null;

    static {
        defaultPackDetail = new WCFPackDetail();
        defaultPackDetail.setTitle("100 Free Streams every 30 days");
        defaultPackDetail.setPrice(0);
        defaultPackDetail.setValidity(30);
        defaultPackDetail.setPopUpMessage("100 Free Streams every 30 days");
        defaultPackDetail.setShowAds(Boolean.TRUE);
        defaultPackDetail.setIOSPack(Boolean.FALSE);

        WCFProductInfo activeEnglish = new WCFProductInfo();
        activeEnglish.setTitleColour("#333333");
        activeEnglish.setInfoColour("#000000");
        activeEnglish.setSubTitleColour("#333333");
        activeEnglish.setStatusMessageColour("#32CD32");
        activeEnglish.setTitle("100 Free Streams every 30 days");
        activeEnglish.setSubTitle("Free");
        activeEnglish.setStatusMessage("Active");
        List<String> infoMessageNonAirtelEn = new ArrayList<>();
        infoMessageNonAirtelEn.add("Stream up to 100 songs online");
        infoMessageNonAirtelEn.add("If you finish listening to 100 songs and you don’t want to wait for the 30 days to get over, you can subscribe to our recommended plan");
        activeEnglish.setInfo(infoMessageNonAirtelEn);

        defaultPackDetail.setActive_en(activeEnglish);
        defaultPackDetail.setInactive_en(activeEnglish);

        WCFProductInfo activeHindi = new WCFProductInfo();
        activeHindi.setTitleColour("#333333");
        activeHindi.setSubTitleColour("#333333");
        activeEnglish.setInfoColour("#000000");
        activeHindi.setStatusMessageColour("#32CD32");
        activeHindi.setTitle("100 नि:शुल्क धाराओं हर 30 दिन");
        activeHindi.setSubTitle("नि:शुल्क");
        activeHindi.setStatusMessage("एक्टिव");
        List<String> infoMessageNonAirtelHi = new ArrayList<>();
        infoMessageNonAirtelHi.add("100 गाने ऑनलाइन स्ट्रीम करें");
        infoMessageNonAirtelHi.add("अगर आप १०० से अधिक गाने सुनना चाहते हैं तो हमारे संस्तुत प्लान पर सबस्क्राइब करें");
        activeHindi.setInfo(infoMessageNonAirtelHi);

        defaultPackDetail.setActive_hi(activeHindi);
        defaultPackDetail.setInactive_hi(activeHindi);
    }

    public static void setLastUpdatedTimestamp(Long lastUpdatedTimestamp) {
        SamsungPackDetail.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    public static Long getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    public static Integer getProductId(Integer packId, String paymentMethod){
        Integer productId = null;
        if(wcfPackMapping.get(packId) != null){
            productId = wcfPackMapping.get(packId).get(paymentMethod);
        }
        return productId;
    }

    public static WCFSubscriptionPack getDefaultPackDetail(Boolean isAirtelUser){
        return defaultPackDetail;
    }

    public static void setDefaultPack(String type, WCFSubscriptionPack wcfSubscriptionPack){
        defaultRecommendedPackDetail = wcfSubscriptionPack;
    }

    public static WCFSubscriptionPack getDefaultRecommendedPackDetail(String osSystem) {
        return defaultRecommendedPackDetail;
    }

    public static void initialisePackDetail(Map<Integer, WCFSubscriptionPack> newWCFSubscriptionPack,Map<Integer,Map<String,Integer>> newSamsungMusicPackMapping,SortedSet<Integer> newPrioritySet){
        samsungMusicProductMapping = newWCFSubscriptionPack;
        wcfPackMapping = newSamsungMusicPackMapping;
    }

    public static Map<Integer,WCFSubscriptionPack> getSamsungMusicProductMapping(){
        return samsungMusicProductMapping;
    }

    public static WCFSubscriptionPack getSamsungMusicPackDetailByProductId(Integer productId){
        if(productId == null){
            return null;
        }
        return samsungMusicProductMapping.get(productId);
    }
}
