package com.wynk.subscription;

import com.wynk.common.Circle;
import com.wynk.common.Country;
import com.wynk.dto.DefaultPackType;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by aakashkumar on 23/11/16.
 */
public class WCFPackDetail extends WCFSubscriptionPack{

    private static Map<Integer,WCFSubscriptionPack> wcfProductMapping = new HashMap();

    // Create Map for PackId - > <Map of Payment Method, ProductId>
    private static Map<Integer,Map<String,Integer>> wcfPackMapping = new HashMap();

    private static SortedSet<Integer> prioritySet = new TreeSet<>();

    private static WCFSubscriptionPack defaultNonAirtelPackDetail = null;

    private static WCFSubscriptionPack defaultAirtelPackDetail = null;

    private static WCFSubscriptionPack defaultRecommendedAndroidPackDetail = null;

    private static WCFSubscriptionPack defaultRecommendedIOSPackDetail = null;

    private static WCFSubscriptionPack defaultSLRecommendedAndroidPackDetail=null;

    private static Long lastUpdatedTimestamp = null;

    static {

        //Set Non Airtel Default Pack
        defaultNonAirtelPackDetail = new WCFPackDetail();
        defaultNonAirtelPackDetail.setTitle("200 Free Streams every 30 days");
        defaultNonAirtelPackDetail.setPrice(0);
        defaultNonAirtelPackDetail.setValidity(30);
        defaultNonAirtelPackDetail.setPopUpMessage("200 Free Streams every 30 days");
        defaultNonAirtelPackDetail.setShowAds(Boolean.TRUE);
        defaultNonAirtelPackDetail.setIOSPack(Boolean.FALSE);

        WCFProductInfo activeNonAirtelEnglish = new WCFProductInfo();
        activeNonAirtelEnglish.setTitleColour("#333333");
        activeNonAirtelEnglish.setInfoColour("#000000");
        activeNonAirtelEnglish.setSubTitleColour("#333333");
        activeNonAirtelEnglish.setStatusMessageColour("#0ad436");
        activeNonAirtelEnglish.setTitle("200 Free Streams every 30 days");
        activeNonAirtelEnglish.setSubTitle("Free");
        activeNonAirtelEnglish.setStatusMessage("Active");
        List<String> infoMessageNonAirtelEn = new ArrayList<>();
        infoMessageNonAirtelEn.add("Stream up to 200 songs online");
        infoMessageNonAirtelEn.add("If you finish listening to 200 songs and you don’t want to wait for the 30 days to get over, you can subscribe to our recommended plan");
        activeNonAirtelEnglish.setInfo(infoMessageNonAirtelEn);

        defaultNonAirtelPackDetail.setActive_en(activeNonAirtelEnglish);
        defaultNonAirtelPackDetail.setInactive_en(activeNonAirtelEnglish);

        WCFProductInfo activeNonAirtelHindi = new WCFProductInfo();
        activeNonAirtelHindi.setTitleColour("#333333");
        activeNonAirtelHindi.setSubTitleColour("#333333");
        activeNonAirtelEnglish.setInfoColour("#000000");
        activeNonAirtelHindi.setStatusMessageColour("#0ad436");
        activeNonAirtelHindi.setTitle("200 मुफ़्त स्ट्रीम्स प्रति 30 दिन");
        activeNonAirtelHindi.setSubTitle("नि:शुल्क");
        activeNonAirtelHindi.setStatusMessage("एक्टिव");
        List<String> infoMessageNonAirtelHi = new ArrayList<>();
        infoMessageNonAirtelHi.add("200 गाने ऑनलाइन स्ट्रीम करें");
        infoMessageNonAirtelHi.add("अगर आप १०० से अधिक गाने सुनना चाहते हैं तो हमारे संस्तुत प्लान पर सबस्क्राइब करें");
        activeNonAirtelHindi.setInfo(infoMessageNonAirtelHi);

        defaultNonAirtelPackDetail.setActive_hi(activeNonAirtelHindi);
        defaultNonAirtelPackDetail.setInactive_hi(activeNonAirtelHindi);

        //Set Airtel Default Pack
        defaultAirtelPackDetail = new WCFPackDetail();
        defaultAirtelPackDetail.setTitle("Unlimited Streams-Free");
        defaultAirtelPackDetail.setPrice(0);
        defaultAirtelPackDetail.setValidity(30);
        defaultAirtelPackDetail.setPopUpMessage("Unlimited Streams");
        defaultAirtelPackDetail.setShowAds(Boolean.TRUE);
        defaultAirtelPackDetail.setIOSPack(Boolean.FALSE);

        WCFProductInfo activeAirtelEnglish = new WCFProductInfo();
        activeAirtelEnglish.setTitleColour("#333333");
        activeAirtelEnglish.setSubTitleColour("#333333");
        activeAirtelEnglish.setStatusMessageColour("#0ad436");
        activeAirtelEnglish.setTitle("Unlimited Streams");
        activeAirtelEnglish.setSubTitle("Free for Airtel users");
        activeAirtelEnglish.setStatusMessage("Active");
        List<String> infoMessageAirtelEn = new ArrayList<>();
        infoMessageAirtelEn.add("This is a special offer for you");
        infoMessageAirtelEn.add("You can also listen to unlimited songs online");
        activeAirtelEnglish.setInfo(infoMessageAirtelEn);

        defaultAirtelPackDetail.setActive_en(activeAirtelEnglish);
        defaultAirtelPackDetail.setInactive_en(activeAirtelEnglish);

        WCFProductInfo activeAirtelHindi = new WCFProductInfo();
        activeAirtelHindi.setTitleColour("#333333");
        activeAirtelHindi.setSubTitleColour("#333333");
        activeAirtelHindi.setStatusMessageColour("#0ad436");
        activeAirtelHindi.setTitle("असीमित स्ट्रीम्स");
        activeAirtelHindi.setSubTitle("एयरटेल उपयोगकर्ताओं के लिए नि:शुल्क");
        activeAirtelHindi.setStatusMessage("एक्टिव");
        List<String> infoMessageAirtelHi = new ArrayList<>();
        infoMessageAirtelHi.add("यह आपके लिए खास ऑफर है");
        infoMessageAirtelHi.add("आप असीमित गाने ऑनलाइन भी सुन सकते हैं");
        activeAirtelHindi.setInfo(infoMessageAirtelHi);

        defaultAirtelPackDetail.setActive_hi(activeAirtelHindi);
        defaultAirtelPackDetail.setInactive_hi(activeAirtelHindi);

    }
    public static String getExternalProductId(int productId){
        return wcfProductMapping.containsKey(productId) ? wcfProductMapping.get(productId).getExternalProductId() : "";
    }

    public static void setLastUpdatedTimestamp(Long lastUpdatedTimestamp) {
        WCFPackDetail.lastUpdatedTimestamp = lastUpdatedTimestamp;
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
        if(isAirtelUser != null && isAirtelUser){
            return defaultAirtelPackDetail;
        }
        return defaultNonAirtelPackDetail;
    }

    public static void setDefaultPack(String type, WCFSubscriptionPack wcfPackDetail){
        if(StringUtils.isNotBlank(type)){
            if(type.equalsIgnoreCase(DefaultPackType.IOS_RECOMMENDED.getPackType())){
                defaultRecommendedIOSPackDetail = wcfPackDetail;
            }
            else if(type.equalsIgnoreCase(DefaultPackType.ANDROID_RECOMMENDED.getPackType())){
                 defaultRecommendedAndroidPackDetail = wcfPackDetail;
            }
            else if(type.equalsIgnoreCase(DefaultPackType.SL_ANDROID_RECOMMENDED.getPackType())){
                defaultSLRecommendedAndroidPackDetail=wcfPackDetail;
            }
        }
    }

    public static WCFSubscriptionPack getDefaultRecommendedPackDetail(String osSystem,String country){
        if(StringUtils.isNotEmpty(country)){
            if(StringUtils.equalsIgnoreCase(country, Country.SRILANKA.getCountryId())){
                return defaultSLRecommendedAndroidPackDetail;
            }
        }
        if(osSystem.equalsIgnoreCase("Android")){
            return defaultRecommendedAndroidPackDetail;
        }
        return defaultRecommendedIOSPackDetail;
    }

    public static void initialisePackDetail(Map<Integer, WCFSubscriptionPack> newWCFProductMapping,Map<Integer,Map<String,Integer>> newWCFPackMapping,SortedSet<Integer> newPrioritySet){
        wcfProductMapping = newWCFProductMapping;
        prioritySet = newPrioritySet;
        wcfPackMapping = newWCFPackMapping;
    }

    public static Map<Integer,WCFSubscriptionPack> getWcfProductMapping(){
        return wcfProductMapping;
    }

    public static WCFSubscriptionPack getWCFPackDetailByProductId(Integer productId){
        if(productId == null){
            return null;
        }
        return wcfProductMapping.get(productId);
    }
}
