package com.wynk.subscription;

import com.wynk.common.ExceptionTypeEnum;
import com.wynk.music.WCFServiceType;
import com.wynk.utils.LogstashLoggerUtils;
import com.wynk.utils.WCFUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.*;

/**
 * Created by Aakash on 16/02/17.
 */
public abstract class WCFSubscriptionPack {

    private Integer productId;
    private Integer packId;
    private String packageGroup;
    private String packageType;
    private String title;
    private Integer price;
    private Integer validity;
    private Integer priority;
    private String externalProductId;
    private WCFProductInfo active_en;
    private WCFProductInfo active_hi;
    private WCFProductInfo inactive_en;
    private WCFProductInfo inactive_hi;
    private String popUpMessage;
    private String defaultPackType;
    private String paymentMethod;
    private Boolean isPaidPack;
    private Boolean isAutoRenewal;
    private Boolean showAds;
    private Boolean isIOSPack;
    private Boolean isDeprecated;
    private Boolean isFMFPack;
    private String fullPrice;
    private String discPrice;
    private String carrier;
    private String defaultTitle;
    private String defaultSubTitle;
    private String packValidity;
    private String currency;
    private Boolean downloadAllowed;
    private Boolean htAllowed;

    public Boolean getDownloadAllowed() {
        return downloadAllowed;
    }

    public void setDownloadAllowed(Boolean downloadAllowed) {
        this.downloadAllowed = downloadAllowed;
    }

    public Boolean getHtAllowed() {
        return htAllowed;
    }

    public void setHtAllowed(Boolean hellotunes) {
        this.htAllowed = hellotunes;
    }

    public static Integer getProductId(WCFServiceType wcfServiceType, Integer productId, String paymentMethod){

        switch (wcfServiceType){

            case SAMSUNG_MUSIC:  return SamsungPackDetail.getProductId(productId,paymentMethod);

            default:            return WCFPackDetail.getProductId(productId,paymentMethod);
        }
    }

    public static WCFSubscriptionPack getDefaultPackDetail(WCFServiceType wcfServiceType,Boolean isAirtelUser){

        switch (wcfServiceType){
            case SAMSUNG_MUSIC:  return SamsungPackDetail.getDefaultPackDetail(Boolean.TRUE);

            default:            return WCFPackDetail.getDefaultPackDetail(isAirtelUser);
        }
    }

    public static void setDefaultPack(WCFServiceType wcfServiceType,String type, WCFSubscriptionPack wcfSubscriptionPack){

        switch (wcfServiceType){
            case SAMSUNG_MUSIC: SamsungPackDetail.setDefaultPack(type,wcfSubscriptionPack);
                                break;
            default:            WCFPackDetail.setDefaultPack(type, wcfSubscriptionPack);
        }
    }

    public static WCFSubscriptionPack getDefaultRecommendedPackDetail(WCFServiceType wcfServiceType,String osSystem,String country){

        switch (wcfServiceType){
            case SAMSUNG_MUSIC: return SamsungPackDetail.getDefaultRecommendedPackDetail(osSystem);
            default:            return WCFPackDetail.getDefaultRecommendedPackDetail(osSystem,country);
        }
    }

    public static void setWcfSubscriptionProductMapping(WCFServiceType wcfServiceType,List<WCFSubscriptionPack> wcfSubscriptionPackList){
        Map<Integer, WCFSubscriptionPack> newWCFProductMapping = new HashMap();
        Map<Integer,Map<String,Integer>> newWCFPackMapping = new HashMap();

        SortedSet<Integer> newPrioritySet = new TreeSet<>();

        if (CollectionUtils.isNotEmpty(wcfSubscriptionPackList)) {

            for (WCFSubscriptionPack wcfSubscriptionPack : wcfSubscriptionPackList) {
                newWCFProductMapping.put(wcfSubscriptionPack.getProductId(), wcfSubscriptionPack);
                newPrioritySet.add(wcfSubscriptionPack.getPriority());

                Map<String,Integer> internalWcfPackMapping = null;
                if(newWCFPackMapping.get(wcfSubscriptionPack.getPackId())!= null){
                    internalWcfPackMapping = newWCFPackMapping.get(wcfSubscriptionPack.getPackId());
                }
                else{
                    internalWcfPackMapping = new HashMap();
                }

                internalWcfPackMapping.put(wcfSubscriptionPack.getPaymentMethod(),wcfSubscriptionPack.getProductId());
                newWCFPackMapping.put(wcfSubscriptionPack.getPackId(),internalWcfPackMapping);
            }

            if (newWCFProductMapping.size() > 0 && newPrioritySet.size() >0 && newWCFPackMapping.size() >0 ) {
                switch (wcfServiceType){
                    case SAMSUNG_MUSIC:  SamsungPackDetail.initialisePackDetail(newWCFProductMapping,newWCFPackMapping,newPrioritySet);
                                         break;
                    default:             WCFPackDetail.initialisePackDetail(newWCFProductMapping,newWCFPackMapping,newPrioritySet);
                }
            }
        }
    }

    public static Map<Integer,WCFSubscriptionPack> getWcfSubscriptionProductMapping(WCFServiceType wcfServiceType){
        switch (wcfServiceType){
            case SAMSUNG_MUSIC:   return SamsungPackDetail.getSamsungMusicProductMapping();
            default:             return WCFPackDetail.getWcfProductMapping();
        }
    }

    public static WCFSubscriptionPack getWCFSubscriptionPackDetailByProductId(WCFServiceType wcfServiceType,Integer productId){
        if(productId == null || productId == 0){
            return null;
        }

        WCFSubscriptionPack wcfSubscriptionPack = null;
        switch (wcfServiceType){
            case SAMSUNG_MUSIC:  wcfSubscriptionPack = SamsungPackDetail.getSamsungMusicPackDetailByProductId(productId);
                                 break;
            default:             wcfSubscriptionPack = WCFPackDetail.getWCFPackDetailByProductId(productId);
                                 break;
        }

        if(wcfSubscriptionPack == null){
            LogstashLoggerUtils.createFatalExceptionLogWithMessage(new Exception("ProductId not exist "), ExceptionTypeEnum.THIRD_PARTY.WCF.name(), "",
                    "WCFSubscriptionPack.getWCFSubscriptionPackDetailByProductId", "Exception as Product id not exist " +productId);
        }
        return wcfSubscriptionPack;
    }

    public static void setLastUpdatedTimestamp(WCFServiceType wcfServiceType, Long lastUpdatedTimestamp) {
        switch (wcfServiceType){
            case SAMSUNG_MUSIC:   SamsungPackDetail.setLastUpdatedTimestamp(lastUpdatedTimestamp);
                                  break;
            default:              WCFPackDetail.setLastUpdatedTimestamp(lastUpdatedTimestamp);
        }
    }

    public static Long getLastUpdatedTimestamp(WCFServiceType wcfServiceType) {
        switch (wcfServiceType){
            case SAMSUNG_MUSIC:   return SamsungPackDetail.getLastUpdatedTimestamp();
            default:              return WCFPackDetail.getLastUpdatedTimestamp();
        }
    }

    public static WCFSubscriptionPack getWCFSubscriptionPack(WCFServiceType wcfServiceType) {
        switch (wcfServiceType){
            case SAMSUNG_MUSIC:   return new SamsungPackDetail();
            default:              return new WCFPackDetail();
        }
    }

    public static Comparator<WCFSubscriptionPack> getAscComparatorByPriorityAscByPrice() {
        Comparator comp = new Comparator<WCFSubscriptionPack>(){
            @Override
            public int compare(WCFSubscriptionPack wp1, WCFSubscriptionPack wp2)
            {
                int descPrio = wp1.getPriority().compareTo(wp2.getPriority());

                if(descPrio != 0){
                    return descPrio;
                }
                else{
                    return wp1.getPrice().compareTo(wp2.getPrice());
                }
            }
        };
        return comp;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPackValidity() {
        return packValidity;
    }

    public void setPackValidity(String packValidity) {
        this.packValidity = packValidity;
    }

    public String getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(String fullPrice) {
        this.fullPrice = fullPrice;
    }

    public String getDiscPrice() {
        return discPrice;
    }

    public void setDiscPrice(String discPrice) {
        this.discPrice = discPrice;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getDefaultTitle() {
        return defaultTitle;
    }

    public void setDefaultTitle(String defaultTitle) {
        this.defaultTitle = defaultTitle;
    }

    public String getDefaultSubTitle() {
        return defaultSubTitle;
    }

    public void setDefaultSubTitle(String defaultSubTitle) {
        this.defaultSubTitle = defaultSubTitle;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getPackId() {
        return packId;
    }

    public void setPackId(Integer packId) {
        this.packId = packId;
    }

    public String getPackageGroup() {
        return packageGroup;
    }

    public void setPackageGroup(String packageGroup) {
        this.packageGroup = packageGroup;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getValidity() {
        return validity;
    }

    public void setValidity(Integer validity) {
        this.validity = validity;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getExternalProductId() {
        return externalProductId;
    }

    public void setExternalProductId(String externalProductId) {
        this.externalProductId = externalProductId;
    }

    public WCFProductInfo getActive_en() {
        return active_en;
    }

    public void setActive_en(WCFProductInfo active_en) {
        this.active_en = active_en;
    }

    public WCFProductInfo getActive_hi() {
        return active_hi;
    }

    public void setActive_hi(WCFProductInfo active_hi) {
        this.active_hi = active_hi;
    }

    public WCFProductInfo getInactive_en() {
        return inactive_en;
    }

    public void setInactive_en(WCFProductInfo inactive_en) {
        this.inactive_en = inactive_en;
    }

    public WCFProductInfo getInactive_hi() {
        return inactive_hi;
    }

    public void setInactive_hi(WCFProductInfo inactive_hi) {
        this.inactive_hi = inactive_hi;
    }

    public String getPopUpMessage() {
        return popUpMessage;
    }

    public void setPopUpMessage(String popUpMessage) {
        this.popUpMessage = popUpMessage;
    }

    public String getDefaultPackType() {
        return defaultPackType;
    }

    public void setDefaultPackType(String defaultPackType) {
        this.defaultPackType = defaultPackType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Boolean getPaidPack() {
        return isPaidPack;
    }

    public void setPaidPack(Boolean paidPack) {
        isPaidPack = paidPack;
    }

    public Boolean getAutoRenewal() {
        return isAutoRenewal;
    }

    public void setAutoRenewal(Boolean autoRenewal) {
        isAutoRenewal = autoRenewal;
    }

    public Boolean getShowAds() {
        return showAds;
    }

    public void setShowAds(Boolean showAds) {
        this.showAds = showAds;
    }

    public Boolean getIOSPack() {
        return isIOSPack;
    }

    public void setIOSPack(Boolean IOSPack) {
        isIOSPack = IOSPack;
    }

    public Boolean getDeprecated() {
        return isDeprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        isDeprecated = deprecated;
    }

    public Boolean getFMFPack() {
        return isFMFPack;
    }

    public void setFMFPack(Boolean FMFPack) {
        isFMFPack = FMFPack;
    }

    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }

    public void fromJsonObject(JSONObject jsonObj) throws Exception {

        if(jsonObj.get("productId") != null) {
            setProductId(Integer.parseInt(jsonObj.get("productId").toString()));
        }

        if(jsonObj.get("hierarchy") != null) {
            setPriority(Integer.parseInt(jsonObj.get("hierarchy").toString()));
        }

        if(jsonObj.get("partnerProductId") != null) {
            setPackId(Integer.parseInt(jsonObj.get("partnerProductId").toString()));
        }

        setExternalProductId("");
        if(jsonObj.get("extProductId") != null) {
            setExternalProductId(jsonObj.get("extProductId").toString());
        }

        setPackageGroup("");
        if(jsonObj.get("packageGroup") != null){
            setPackageGroup(jsonObj.get("packageGroup").toString());
        }

        setAutoRenewal(Boolean.FALSE);
        setPackageType("");
        if(jsonObj.get("packageType") != null){
            setPackageType(jsonObj.get("packageType").toString());
            if(getPackageType()!= null && getPackageType().equalsIgnoreCase("Subscription")){
                setAutoRenewal(Boolean.TRUE);
            }
        }

        setDeprecated(Boolean.FALSE);
        if(jsonObj.get("isDeprecated") != null){
            setDeprecated(Boolean.valueOf(jsonObj.get("isDeprecated").toString()));
        }

        setTitle("");
        if(jsonObj.get("title") != null){
            setTitle(jsonObj.get("title").toString());
        }

        setPaidPack(Boolean.TRUE);
        setIOSPack(Boolean.FALSE);
        setPaymentMethod("");
        if(jsonObj.get("paymentMethod") != null){
            setPaymentMethod(jsonObj.get("paymentMethod").toString());
            if(getPaymentMethod().equalsIgnoreCase("FREE")){
                setPaidPack(Boolean.FALSE);
            }
            else if(getPaymentMethod().equalsIgnoreCase("ITUNES")){
                setIOSPack(Boolean.TRUE);
            }
        }

        setShowAds(Boolean.TRUE);
        setFMFPack(Boolean.FALSE);
        setHtAllowed(Boolean.FALSE);
        setDownloadAllowed(Boolean.FALSE);

        if(jsonObj.get("pricing") != null) {
            JSONObject priceObj = (JSONObject) jsonObj.get("pricing");
            setPrice(Integer.parseInt(priceObj.get("price").toString()));
            if(priceObj.get("unit")!=null){
                setCurrency(priceObj.get("unit").toString());
            }
        }

        if(jsonObj.get("packPeriod") != null) {
            JSONObject packPeriodObj = (JSONObject) jsonObj.get("packPeriod");
            setValidity(Integer.parseInt(packPeriodObj.get("validity").toString()));
        }

        setPopUpMessage("");
        if(jsonObj.get("packMetaData")!= null){
            JSONObject packMetaDataObj = (JSONObject) jsonObj.get("packMetaData");

            if(packMetaDataObj.get("uiData")!= null) {
                JSONObject infoPackMetaData = (JSONObject) packMetaDataObj.get("uiData");

                if(infoPackMetaData.get("popUpMessage") != null) {
                    setPopUpMessage((infoPackMetaData.get("popUpMessage").toString()));
                }
                if(infoPackMetaData.get("fullPrice") != null) {
                    setFullPrice((infoPackMetaData.get("fullPrice").toString()));
                }
                if(infoPackMetaData.get("discPrice") != null) {
                    setDiscPrice((infoPackMetaData.get("discPrice").toString()));
                }
                if(infoPackMetaData.get("carrier") != null) {
                    setCarrier((infoPackMetaData.get("carrier").toString()));
                }
                if(infoPackMetaData.get("defaultTitle") != null) {
                    setDefaultTitle((infoPackMetaData.get("defaultTitle").toString()));
                }
                if(infoPackMetaData.get("defaultSubTitle") != null) {
                    setDefaultSubTitle((infoPackMetaData.get("defaultSubTitle").toString()));
                }
                if(infoPackMetaData.get("validity") != null) {
                    setPackValidity((infoPackMetaData.get("validity").toString()));
                }
                if(infoPackMetaData.get("showAds") != null) {
                    String showAdsString = infoPackMetaData.get("showAds").toString();
                    if(StringUtils.isNotBlank(showAdsString) && showAdsString.equalsIgnoreCase("false")){
                        setShowAds(Boolean.FALSE);
                    }
                }

                if(infoPackMetaData.get("isFMFPack") != null) {
                    String isFMFPackString = infoPackMetaData.get("isFMFPack").toString();
                    if(StringUtils.isNotBlank(isFMFPackString) && isFMFPackString.equalsIgnoreCase("true")){
                        setFMFPack(Boolean.TRUE);
                    }
                }

                if(infoPackMetaData.get("defaultPackType") != null){
                    setDefaultPackType(infoPackMetaData.get("defaultPackType").toString());
                }

                if(infoPackMetaData.get("downloadAllowed") != null) {
                    String downloadAllowed = infoPackMetaData.get("downloadAllowed").toString();
                    if(StringUtils.isNotBlank(downloadAllowed) && downloadAllowed.equalsIgnoreCase("true")){
                        setDownloadAllowed(Boolean.TRUE);
                    }
                }

                if(infoPackMetaData.get("htAllowed") != null) {
                    String htAllowed = infoPackMetaData.get("htAllowed").toString();
                    if(StringUtils.isNotBlank(htAllowed) && htAllowed.equalsIgnoreCase("true")){
                        setHtAllowed(Boolean.TRUE);
                    }
                }

                WCFProductInfo activeEn = new WCFProductInfo();
                activeEn.fromJsonObject(infoPackMetaData, "active_en_");
                setActive_en(activeEn);

                WCFProductInfo activeHi = new WCFProductInfo();
                activeHi.fromJsonObject(infoPackMetaData, "active_hi_");
                setActive_hi(activeHi);

                WCFProductInfo inactiveEn = new WCFProductInfo();
                inactiveEn.fromJsonObject(infoPackMetaData, "inactive_en_");
                setInactive_en(inactiveEn);

                WCFProductInfo inactiveHi = new WCFProductInfo();
                inactiveHi.fromJsonObject(infoPackMetaData, "inactive_hi_");
                setInactive_hi(inactiveHi);
            }
        }
    }

    public String toJson(){
        JSONObject jsonObject = toJsonObject();
        return jsonObject.toString();
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();

        if(getProductId() != null){
            jsonObj.put("productId", getProductId());
        }
        if(getPackId() != null){
            jsonObj.put("packId", getPackId());
        }
        if(getPackageGroup() != null){
            jsonObj.put("packageGroup", getPackageGroup());
        }
        if(getPackageType() != null){
            jsonObj.put("packageType", getPackageType());
        }
        if(getTitle() != null){
            jsonObj.put("title", getTitle());
        }
        if(getPrice() != null){
            jsonObj.put("price",getPrice());
        }
        if(getValidity() != null){
            jsonObj.put("validity", getValidity());
        }
        if(StringUtils.isNotEmpty(getCurrency())){
            jsonObj.put("currency",getCurrency());
        }
        if(getPriority() != null){
            jsonObj.put("priority", getPriority());
        }
        if(getExternalProductId() != null){
            jsonObj.put("extProductId", getExternalProductId());
        }
        if(getPopUpMessage() != null){
            jsonObj.put("popUpMessage", getPopUpMessage());
        }
        if(getFullPrice()!=null){
            jsonObj.put("fullPrice",getFullPrice());
        }
        if(getDiscPrice()!=null){
            jsonObj.put("discPrice",getDiscPrice());
        }
        if(getCarrier()!=null){
            jsonObj.put("carrier",getCarrier());
        }
        if(getDefaultTitle()!=null){
            jsonObj.put("defaultTitle",getDefaultTitle());
        }
        if(getDefaultSubTitle()!=null){
            jsonObj.put("defaultSubTitle",getDefaultSubTitle());
        }
        if(getPackValidity()!=null){
            jsonObj.put("packValidity",getPackValidity());
        }
        if(getDefaultPackType() != null){
            jsonObj.put("defaultPackType", getDefaultPackType());
        }
        if(getPaymentMethod() != null){
            jsonObj.put("paymentMethod", getPaymentMethod());
        }
        if(getPaidPack() != null){
            jsonObj.put("isPaidPack", getPaidPack());
        }
        if(getIOSPack() != null){
            jsonObj.put("isIOSPack",getIOSPack());
        }
        if(getShowAds() != null){
            jsonObj.put("showAds",getShowAds());
        }
        if(getFMFPack() != null){
            jsonObj.put("isFMFPack",getFMFPack());
        }
        if(getAutoRenewal() != null){
            jsonObj.put("isAutoRenewal", getAutoRenewal());
        }
        if(getDeprecated() != null){
            jsonObj.put("isDeprecated",getDeprecated());
        }
        if(getActive_en() != null){
            jsonObj.put("active_en",getActive_en().toJsonObject());
        }
        if(getActive_hi() != null){
            jsonObj.put("active_hi",getActive_hi().toJsonObject());
        }
        if(getInactive_en() != null){
            jsonObj.put("inactive_en",getInactive_en().toJsonObject());
        }
        if(getInactive_hi() != null){
            jsonObj.put("inactive_hi",getInactive_hi().toJsonObject());
        }
        return jsonObj;
    }
}
