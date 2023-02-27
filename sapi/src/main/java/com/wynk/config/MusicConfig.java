package com.wynk.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MusicConfig {


    private boolean enableMusic;
    private String galaxyFreeSongPurchaseCount;
    private String galaxyOfferExpiryDate;
    private boolean enableRedirection;
    private String apiBaseUrl;
    private String graphBaseUrl;
    private String apiSecureBaseUrl;
    private String playStreamingUrl;
    private String playStreamingEndpoint;
    private String operatorApiBaseUrl;
    private String opApiBaseUrl;
    private String htPurchaseUrl;
    private String wcfBaseUrl;
    private String wcfBaseUnSecureUrl;
    private String twssUrl;
    private String smsgw;
    private String smsapi;
    private boolean useLocalSMSSender;
    private boolean enableAnalytics;
    private boolean enableRE;
    private boolean enableNDS;
    private boolean enableNotifications;
    private boolean enableTestNotifications;
    private boolean enableStatsD;
    private boolean enableGeoBlocking;
    private boolean enablePackages;
    private String analyticsbucketname;
    private int itemsPerPage;
    private String encryptionKey;
    private String awsEndPoint;
    private String platformBaseUrl;
    private String baseFeUrl;
    private boolean enableSE;
    private boolean enableGoogleNow;
    private boolean gcmMultiCast;
    private boolean updateGCMDeviceKey;

    private int smsSenderMaxPoolSize;
    private String apnsCertFile;
    private String apnsDevCertFile;
    private boolean enableProdApnsCert;
    private String wapSalt;
    private String newWapSalt;
    private String appSalt;
    private boolean wynkContestEnabled;
    private boolean ibmReconFileProcessing;

    //FUP
    private boolean fupEnabled;
    private int streamingFUPLimit;
    private int wapStreamingLimit;

    private int airtel99Limit;

    //ITunes subs
    private String iTunesAirtelSubsProductId;
    private String iTunesNonAirtelSubsProductId;

    private String iTunesAirtelSubsPackUserProductId;
    private String iTunesNonAirtelSubsPackUserProductId;


    private String iTunesApiUrl;
    private String iTunesPassword;

    private boolean subsDBUpdateByApi;

    private int notificationThreadPoolSize;
    private int twilioThreadPoolSize;

    // twilio - ivrs details
    private String twilioBaseUrl;
    private String twilioId;
    private String twilioPin;
    private String twilioCampaignId;

    private boolean disableProactiveFeedback;

    private String musicServers;

    // FeaturedOff
    private boolean featuredOff;

    private boolean featuredOff2G;

    //Inactive Users Notification
    private boolean unrInactivityNotification;

    private Boolean musicSixMonthsOffer;

    //ADHM
    private boolean showADHMHamburgerMenu;
    private boolean showADHMPopUp;

    private boolean freeDataOffer;

    private boolean enableFPKafkaConsumer;

    private boolean showOnDeviceHamburgerMenu;

    private String cmsPythonBaseUrl;

    private String samsungSDKKey;

    private boolean enableIpl;
    private Integer mp3AkamaiCloudFrontRatio;
    private Integer mp4AkamaiCloudFrontRatio;
    private Integer mp3PerformaceTestAkamaiCloudRatio;
    private Integer mp4PerformanceTestAkamaiCloudRatio;



    private String followCountApiUrl;

    private boolean searchVoiceEnable;

    private Boolean musicOfferNonAirtel;

    private Boolean musicOfferIdea;

    private String segmentationURL;


    // Kafka
    private String statsQueue;
    private String adtechQueue;
    private String tpNotifyQueue;

    private String zookeeper;
    private String brokerList;
    private String kafkaProducerType;
    private String fpKafkaQueue;



    // wynk kafka
    private String videosQueue;
    private String gamesQueue;
    private String intentQueue;

    private String clearCacheQueue;
    private String clearCmsCacheQueue;

    // kinesis
    private String wcfThanksStream;


    private boolean airtelStoreEnable = false;
    private boolean enableNdsBaseUrl;
    private String ndsClient;
    private boolean enableDelimiterKafkaConsumer;
    private String fpDelimiterKafkaQueue;

    private Boolean prerollEnabled;
    private Boolean nativeAdEnabled;
    private String wcfNDSCache;

    //sync lyrics
    private Boolean enableLyrics;

    //IOS APP
    private String iosBsyExt;
    private String wcfBaseApiUrl;
    private String cronEnabled;

    private boolean appSidePackagesShuffling;

 // International roaming
    private long intlRoamingCycle;
    private long intlRoamingQuota;
    private long intlRoamingExpiryNotif;
    private boolean intlRoamingEnabled;
    private boolean intlRoamingFreeTierCheck;
    private int intlRoamingAndroidVersionsToUpgrade;
    private int intlRoamingiOsVersionsToUpgrade;
    private int trialProductId;
    private int trialAgainProductId;
    private int bundledTrialProductId;
    private int bundledTrialAgainProductId;

    private String wcfWebViewBaseUrl;

    private String basicSMSHashCode;

    // twitter consumer credentials
    private String twitterKey;
    private String twitterSecret;
    private boolean enableKinesisConsumer;

    //wcf payment
    private String wcfPayBaseUrl;
    private String wcfPaySqsUrl;
    private String wcfPaySqsRegion;
    private String staticWebViewSecretKey;

    //auth
    private boolean enableAuth;

    //wcf endpoints
    private String wcfOfferUrl;
    private String wcfIdentificationUrl;
    private String wcfAllProductsUrl;
    private String wcfAllPlansUrl;
    private String wcfAllOffersUrl;

    private String wcfApiAppId;
    private String wcfApiSecretKey;
    private String wcfSmsDomain;
    private String wcfSmsEndpoint;
    private final Map<String, String> saltMap = new HashMap<>();
    public String getSaltValue(String key) {
        return saltMap.get(key);
    }
    public void createSaltValueMap(String saltStrings) {
        String[] saltArray = saltStrings.split(",");
        Arrays.stream(saltArray).map(saltKeyAndValue -> saltKeyAndValue.split(":")).
                forEach(keyAndVal -> saltMap.put(keyAndVal[0].trim(), keyAndVal[1]));
    }

    public String getWcfSmsDomain() {
        return wcfSmsDomain;
    }

    public void setWcfSmsDomain(String wcfSmsDomain) {
        this.wcfSmsDomain = wcfSmsDomain;
    }

    public String getWcfSmsEndpoint() {
        return wcfSmsEndpoint;
    }

    public void setWcfSmsEndpoint(String wcfSmsEndpoint) {
        this.wcfSmsEndpoint = wcfSmsEndpoint;
    }

    public String getWcfApiAppId() {
        return wcfApiAppId;
    }

    public void setWcfApiAppId(String wcfApiAppId) {
        this.wcfApiAppId = wcfApiAppId;
    }

    public String getWcfApiSecretKey() {
        return wcfApiSecretKey;
    }

    public void setWcfApiSecretKey(String wcfApiSecretKey) {
        this.wcfApiSecretKey = wcfApiSecretKey;
    }

    public String getWcfAllOffersUrl() {
      return wcfAllOffersUrl;
    }

    public void setWcfAllOffersUrl(String wcfAllOffersUrl) {
      this.wcfAllOffersUrl = wcfAllOffersUrl;
    }

    public String getWcfOfferUrl() {
      return wcfOfferUrl;
    }

    public void setWcfOfferUrl(String wcfOfferUrl) {
      this.wcfOfferUrl = wcfOfferUrl;
    }

    public String getWcfIdentificationUrl() {
      return wcfIdentificationUrl;
    }

    public void setWcfIdentificationUrl(String wcfIdentificationUrl) {
      this.wcfIdentificationUrl = wcfIdentificationUrl;
    }

    public String getWcfAllProductsUrl() {
      return wcfAllProductsUrl;
    }

    public void setWcfAllProductsUrl(String wcfAllProductsUrl) {
      this.wcfAllProductsUrl = wcfAllProductsUrl;
    }

    public String getWcfAllPlansUrl() {
      return wcfAllPlansUrl;
    }

    public void setWcfAllPlansUrl(String wcfAllPlansUrl) {
      this.wcfAllPlansUrl = wcfAllPlansUrl;
    }
    
    public String getStaticWebViewSecretKey() {
        return staticWebViewSecretKey;
    }

    public void setStaticWebViewSecretKey(String staticWebViewSecretKey) {
        this.staticWebViewSecretKey = staticWebViewSecretKey;
    }

    public boolean isEnableAuth(){
        return enableAuth;
    }

    public void setEnableAuth(boolean enableAuth) {
        this.enableAuth = enableAuth;
    }

    public void setEnableKinesisConsumer(boolean enableKinesisConsumer) {
      this.enableKinesisConsumer = enableKinesisConsumer;
    }

    public String getWcfWebViewBaseUrl() {
        return wcfWebViewBaseUrl;
    }

    public void setWcfWebViewBaseUrl(String wcfWebViewBaseUrl) {
        this.wcfWebViewBaseUrl = wcfWebViewBaseUrl;
    }

    public String getExternalHandlerAuthenicationCode() {
        return externalHandlerAuthenicationCode;
    }

    public void setExternalHandlerAuthenicationCode(String externalHandlerAuthenicationCode) {
        this.externalHandlerAuthenicationCode = externalHandlerAuthenicationCode;
    }

    private String externalHandlerAuthenicationCode;


    // Ads disable A/B Experiment
    private String adsDisableExperimentId;

    public String getAdsDisableExperimentId() {
        return adsDisableExperimentId;
    }

    public void setAdsDisableExperimentId(String adsDisableExperimentId) {
        this.adsDisableExperimentId = adsDisableExperimentId;
    }

    public boolean isSearchVoiceEnable() {
        return searchVoiceEnable;
    }

    public void setSearchVoiceEnable(boolean searchVoiceEnable) {
        this.searchVoiceEnable = searchVoiceEnable;
    }

    public void setFreeDataOffer(boolean freeDataOffer) {
        this.freeDataOffer = freeDataOffer;
    }

    public String getGraphBaseUrl() {
        return graphBaseUrl;
    }

    public void setGraphBaseUrl(String graphBaseUrl) {
        this.graphBaseUrl = graphBaseUrl;
    }

    private String appSMSHashCode;

    public String getAppSMSHashCode() {
        return appSMSHashCode;
    }

    public void setAppSMSHashCode(String appSMSHashCode) {
        this.appSMSHashCode = appSMSHashCode;
    }

    public int getBundledTrialProductId() {
    return bundledTrialProductId;
  }
  public void setBundledTrialProductId(int bundledTrialProductId) {
    this.bundledTrialProductId = bundledTrialProductId;
  }

  public int getBundledTrialAgainProductId() {
    return bundledTrialAgainProductId;
  }

  public void setBundledTrialAgainProductId(int bundledTrialAgainProductId) {
    this.bundledTrialAgainProductId = bundledTrialAgainProductId;
  }

  public int getTrialProductId() {
    return trialProductId;
  }

  public void setTrialProductId(int trialProductId) {
    this.trialProductId = trialProductId;
  }

  public int getTrialAgainProductId() {
    return trialAgainProductId;
  }

  public void setTrialAgainProductId(int trialAgainProductId) {
    this.trialAgainProductId = trialAgainProductId;
  }

    public Boolean getEnableLyrics() {
        return enableLyrics;
    }

    public void setEnableLyrics(Boolean enableLyrics) {
        this.enableLyrics = enableLyrics;
    }

  public String getFollowCountApiUrl() {
        return followCountApiUrl;
    }

    public void setFollowCountApiUrl(String followCountApiUrl) {
        this.followCountApiUrl = followCountApiUrl;
    }

    public boolean isAppSidePackagesShuffling() {
		return appSidePackagesShuffling;
	}

	public void setAppSidePackagesShuffling(boolean appSidePackagesShuffling) {
		this.appSidePackagesShuffling = appSidePackagesShuffling;
	}

    public Boolean getPrerollEnabled() {
        return prerollEnabled;
    }

    public void setPrerollEnabled(Boolean prerollEnabled) {
        this.prerollEnabled = prerollEnabled;
    }

    public Boolean getNativeAdEnabled() {
        return nativeAdEnabled;
    }

    public void setNativeAdEnabled(Boolean nativeAdEnabled) {
        this.
            nativeAdEnabled = nativeAdEnabled;
    }

    public void setEnableDelimiterKafkaConsumer(boolean enableDelimiterKafkaConsumer) {
        this.enableDelimiterKafkaConsumer = enableDelimiterKafkaConsumer;
    }

    public void setFpDelimiterKafkaQueue(String fpDelimiterKafkaQueue) {
        this.fpDelimiterKafkaQueue = fpDelimiterKafkaQueue;
    }

	public void setIntentQueue(String intentQueue) {
		this.intentQueue = intentQueue;
	}

	public void setVideosQueue(String videosQueue) {
		this.videosQueue = videosQueue;
	}

	public void setGamesQueue(String gamesQueue) {
		this.gamesQueue = gamesQueue;
	}

    public void setStatsQueue(String statsQueue) {
        this.statsQueue = statsQueue;
    }

    public void setMusicOfferNonAirtel(Boolean musicOfferNonAirtel) {
        this.musicOfferNonAirtel = musicOfferNonAirtel;
    }

    public void setEnableProdApnsCert(boolean enableProdApnsCert) {
        this.enableProdApnsCert = enableProdApnsCert;
    }

    public void setMusicSixMonthsOffer(Boolean musicSixMonthsOffer) {
        this.musicSixMonthsOffer = musicSixMonthsOffer;
    }

    public void setiTunesAirtelSubsPackUserProductId(String iTunesAirtelSubsPackUserProductId) {
        this.iTunesAirtelSubsPackUserProductId = iTunesAirtelSubsPackUserProductId;
    }

    public void setiTunesNonAirtelSubsPackUserProductId(String iTunesNonAirtelSubsPackUserProductId) {
        this.iTunesNonAirtelSubsPackUserProductId = iTunesNonAirtelSubsPackUserProductId;
    }

    public boolean isShowADHMHamburgerMenu() {
        return showADHMHamburgerMenu;
    }

    public void setShowADHMHamburgerMenu(boolean showADHMHamburgerMenu) {
        this.showADHMHamburgerMenu = showADHMHamburgerMenu;
    }

    public int getTwilioThreadPoolSize() {
        return twilioThreadPoolSize;
    }

    public void setTwilioThreadPoolSize(int twilioThreadPoolSize) {
        this.twilioThreadPoolSize = twilioThreadPoolSize;
    }

    public String getTwilioCampaignId() {
        return twilioCampaignId;
    }

    public void setTwilioCampaignId(String twilioCampaignId) {
        this.twilioCampaignId = twilioCampaignId;
    }

    public String getTwilioBaseUrl() {
        return twilioBaseUrl;
    }

    public void setTwilioBaseUrl(String twilioBaseUrl) {
        this.twilioBaseUrl = twilioBaseUrl;
    }

    public String getTwilioId() {
        return twilioId;
    }

    public void setTwilioId(String twilioId) {
        this.twilioId = twilioId;
    }

    public String getTwilioPin() {
        return twilioPin;
    }

    public void setTwilioPin(String twilioPin) {
        this.twilioPin = twilioPin;
    }

    public void setNotificationThreadPoolSize(int notificationThreadPoolSize) {
        this.notificationThreadPoolSize = notificationThreadPoolSize;
    }

    public boolean isEnablePackages() {
        return enablePackages;
    }

    public void setEnablePackages(boolean enablePackages) {
        this.enablePackages = enablePackages;
    }

    public boolean isEnableGeoBlocking() {
        return enableGeoBlocking;
    }

    public void setEnableGeoBlocking(boolean enableGeoBlocking) {
        this.enableGeoBlocking = enableGeoBlocking;
    }

    public boolean isEnableMusic() {
        return enableMusic;
    }

    public void setEnableMusic(boolean enableMusic) {
        this.enableMusic = enableMusic;
    }

    public String getPlatformBaseUrl() {
        return platformBaseUrl;
    }

	public void setPlatformBaseUrl(String platformBaseUrl) {
        this.platformBaseUrl = platformBaseUrl;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public void setEnableRedirection(boolean enableRedirection) {
        this.enableRedirection = enableRedirection;
    }

    public void setHTPurchaseUrl(String htPurchaseUrl) {
        this.htPurchaseUrl = htPurchaseUrl;
    }

    public void setTwssUrl(String twssUrl) {
        this.twssUrl = twssUrl;
    }

    public String getSmsgw() {
        return smsgw;
    }

    public void setSmsgw(String smsgw) {
        this.smsgw = smsgw;
    }

    public boolean isUseLocalSMSSender() {
        return useLocalSMSSender;
    }

    public void setUseLocalSMSSender(boolean useLocalSMSSender) {
        this.useLocalSMSSender = useLocalSMSSender;
    }

    public void setEnableAnalytics(boolean enableAnalytics) {
        this.enableAnalytics = enableAnalytics;
    }

    public void setEnableRE(boolean enableRE) {
        this.enableRE = enableRE;
    }

    public boolean isEnableNDS() {
        return enableNDS;
    }

    public void setEnableNDS(boolean enableNDS) {
        this.enableNDS = enableNDS;
    }
    public String getAnalyticsbucketname() {
        return analyticsbucketname;
    }

    public void setAnalyticsbucketname(String analyticsbucketname) {
        this.analyticsbucketname = analyticsbucketname;
    }

	public void setItemsPerPage(int itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}


	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

    public String getAwsEndPoint() {
        return awsEndPoint;
    }

    public void setAwsEndPoint(String awsEndPoint) {
        this.awsEndPoint = awsEndPoint;
    }

    public void setEnableNotifications(boolean enableNotifications) {
        this.enableNotifications = enableNotifications;
    }
    public void setEnableTestNotifications(boolean enableTestNotifications) {
        this.enableTestNotifications = enableTestNotifications;
    }


	public String getBaseFeUrl() {
		return baseFeUrl;
	}


	public void setBaseFeUrl(String baseFeUrl) {
		this.baseFeUrl = baseFeUrl;
	}


	public boolean isFupEnabled() {
		return fupEnabled;
	}


	public void setFupEnabled(boolean fupEnabled) {
		this.fupEnabled = fupEnabled;
	}


	public int getStreamingFUPLimit() {
		return streamingFUPLimit;
	}


	public void setStreamingFUPLimit(int streamingFUPLimit) {
		this.streamingFUPLimit = streamingFUPLimit;
	}

	public int getAirtel99Limit() {
		return airtel99Limit;
	}


	public void setAirtel99Limit(int airtel99Limit) {
		this.airtel99Limit = airtel99Limit;
	}

    public void setEnableStatsD(boolean enableStatsD) {
        this.enableStatsD = enableStatsD;
    }

    public void setEnableSE(boolean enableSE) {
        this.enableSE = enableSE;
    }

    public String getOpApiBaseUrl() {
        return opApiBaseUrl;
    }

    public void setOpApiBaseUrl(String opApiBaseUrl) {
        this.opApiBaseUrl = opApiBaseUrl;
    }

    public String getApnsCertFile() {
        return apnsCertFile;
    }

    public void setApnsCertFile(String apnsCertFile) {
        this.apnsCertFile = apnsCertFile;
    }

    public String getApnsDevCertFile() {
        return apnsDevCertFile;
    }

    public void setApnsDevCertFile(String apnsDevCertFile) {
        this.apnsDevCertFile = apnsDevCertFile;
    }

	public void setiTunesAirtelSubsProductId(String iTunesAirtelSubsProductId) {
		this.iTunesAirtelSubsProductId = iTunesAirtelSubsProductId;
	}

	public void setiTunesNonAirtelSubsProductId(String iTunesNonAirtelSubsProductId) {
		this.iTunesNonAirtelSubsProductId = iTunesNonAirtelSubsProductId;
	}

	public void setiTunesApiUrl(String iTunesApiUrl) {
		this.iTunesApiUrl = iTunesApiUrl;
	}

	public void setiTunesPassword(String iTunesPassword) {
		this.iTunesPassword = iTunesPassword;
	}

	public void setSubsDBUpdateByApi(boolean subsDBUpdateByApi) {
		this.subsDBUpdateByApi = subsDBUpdateByApi;
	}

	public int getSmsSenderMaxPoolSize() {
		return smsSenderMaxPoolSize;
	}

	public void setSmsSenderMaxPoolSize(int smsSenderMaxPoolSize) {
		this.smsSenderMaxPoolSize = smsSenderMaxPoolSize;
	}

    public boolean isDisableProactiveFeedback() {
        return disableProactiveFeedback;
    }

    public void setDisableProactiveFeedback(boolean disableProactiveFeedback) {
        this.disableProactiveFeedback = disableProactiveFeedback;
    }

    public void setMusicServers(String musicServers) {
        this.musicServers = musicServers;
    }

    public void setOperatorApiBaseUrl(String operatorApiBaseUrl) {
        this.operatorApiBaseUrl = operatorApiBaseUrl;
    }

    public void setEnableGoogleNow(boolean enableGoogleNow) {
        this.enableGoogleNow = enableGoogleNow;
    }

	public void setFeaturedOff(boolean featuredOff) {
		this.featuredOff = featuredOff;
	}

    public void setGcmMultiCast(boolean gcmMultiCast) {
        this.gcmMultiCast = gcmMultiCast;
    }

    public void setUpdateGCMDeviceKey(boolean updateGCMDeviceKey) {
        this.updateGCMDeviceKey = updateGCMDeviceKey;
    }

	public String getWapSalt() {
		return wapSalt;
	}

	public void setWapSalt(String wapSalt) {
		this.wapSalt = wapSalt;
	}

	public String getAppSalt() {
		return appSalt;
	}

	public void setAppSalt(String appSalt) {
		this.appSalt = appSalt;
	}

    public String getNewWapSalt() {
        return newWapSalt;
    }

    public void setNewWapSalt(String newWapSalt) {
        this.newWapSalt = newWapSalt;
    }

    public void setWynkContestEnabled(boolean wynkContestEnabled) {
        this.wynkContestEnabled = wynkContestEnabled;
    }

    public void setUnrInactivityNotification(boolean unrInactivityNotification) {
        this.unrInactivityNotification = unrInactivityNotification;
    }

    public void setIbmReconFileProcessing(boolean ibmReconFileProcessing) {
        this.ibmReconFileProcessing = ibmReconFileProcessing;
    }

	public String getSmsapi() {
		return smsapi;
	}

	public void setSmsapi(String smsapi) {
		this.smsapi = smsapi;
	}

	public void setFeaturedOff2G(boolean featuredOff2G) {
		this.featuredOff2G = featuredOff2G;
	}

    public void setShowADHMPopUp(boolean showADHMPopUp) {
        this.showADHMPopUp = showADHMPopUp;
    }

  public String getPlayStreamingUrl() {
    return playStreamingUrl;
  }

  public void setPlayStreamingUrl(String playStreamingUrl) {
    this.playStreamingUrl = playStreamingUrl;
  }

  public String getPlayStreamingEndpoint() {
    return playStreamingEndpoint;
  }

  public void setPlayStreamingEndpoint(String playStreamingEndpoint) {
    this.playStreamingEndpoint = playStreamingEndpoint;
  }

  public void setApiSecureBaseUrl(String apiSecureBaseUrl) {
		this.apiSecureBaseUrl = apiSecureBaseUrl;
	}

    public void setFpKafkaQueue(String fpKafkaQueue) {
        this.fpKafkaQueue = fpKafkaQueue;
    }

    public void setEnableFPKafkaConsumer(boolean enableFPKafkaConsumer) {
        this.enableFPKafkaConsumer = enableFPKafkaConsumer;
    }

    public void setCmsPythonBaseUrl(String cmsPythonBaseUrl) {
        this.cmsPythonBaseUrl = cmsPythonBaseUrl;
    }

	public void setMusicOfferIdea(Boolean musicOfferIdea) {
		this.musicOfferIdea = musicOfferIdea;
	}

    public boolean isShowOnDeviceHamburgerMenu() {
        return showOnDeviceHamburgerMenu;
    }

    public void setShowOnDeviceHamburgerMenu(boolean showOnDeviceHamburgerMenu) {
        this.showOnDeviceHamburgerMenu = showOnDeviceHamburgerMenu;
    }

    public String getWcfBaseUrl() {
        return wcfBaseUrl;
    }

    public void setWcfBaseUrl(String wcfBaseUrl) {
        this.wcfBaseUrl = wcfBaseUrl;
    }

	public String getAdtechQueue() {
		return adtechQueue;
	}

	public void setAdtechQueue(String adtechQueue) {
		this.adtechQueue = adtechQueue;
	}
    public String getSegmentationURL() {
        return segmentationURL;
    }

    public void setSegmentationURL(String segmentationURL) {
        this.segmentationURL = segmentationURL;
    }

    public void setGalaxyFreeSongPurchaseCount(String galaxyFreeSongPurchaseCount) {
        this.galaxyFreeSongPurchaseCount = galaxyFreeSongPurchaseCount;
    }

    public void setGalaxyOfferExpiryDate(String galaxyOfferExpiryDate) {
        this.galaxyOfferExpiryDate = galaxyOfferExpiryDate;
    }

    public boolean isAirtelStoreEnable() {
        return airtelStoreEnable;
    }

    public boolean isEnableNdsBaseUrl() {
        return enableNdsBaseUrl;
    }

    public String getNdsClient() {
        return ndsClient;
    }

    public String getWcfNDSCache() {
        return wcfNDSCache;
    }

    public void setWcfNDSCache(String wcfNDSCache) {
        this.wcfNDSCache = wcfNDSCache;
    }

    public String getSamsungSDKKey() {
		return samsungSDKKey;
	}

	public void setSamsungSDKKey(String samsungSDKKey) {
		this.samsungSDKKey = samsungSDKKey;
	}

    public String getIosBsyExt() {
        return iosBsyExt;
    }

    public void setIosBsyExt(String iosBsyExt) {
        this.iosBsyExt = iosBsyExt;
    }

    public String getWcfBaseApiUrl() {
        return wcfBaseApiUrl;
    }

    public void setWcfBaseApiUrl(String wcfBaseApiUrl) {
        this.wcfBaseApiUrl = wcfBaseApiUrl;
    }

    public void setCronEnabled(String cronEnabled) {
        this.cronEnabled = cronEnabled;
    }

	public void setClearCacheQueue(String clearCacheQueue) {
		this.clearCacheQueue = clearCacheQueue;
	}

	public void setClearCmsCacheQueue(String clearCmsCacheQueue) {
		this.clearCmsCacheQueue = clearCmsCacheQueue;
	}
	public boolean isEnableIpl() {
		return enableIpl;
	}

	public void setEnableIpl(boolean enableIpl) {
		this.enableIpl = enableIpl;
	}

    public long getIntlRoamingCycle() {
        return intlRoamingCycle;
    }

    public void setIntlRoamingCycle(long intlRoamingCycle) {
        this.intlRoamingCycle = intlRoamingCycle;
    }

    public long getIntlRoamingQuota() {
        return intlRoamingQuota;
    }

    public void setIntlRoamingQuota(long intlRoamingQuota) {
        this.intlRoamingQuota = intlRoamingQuota;
    }

    public long getIntlRoamingExpiryNotif() {
        return intlRoamingExpiryNotif;
    }

    public void setIntlRoamingExpiryNotif(long intlRoamingExpiryNotif) {
        this.intlRoamingExpiryNotif = intlRoamingExpiryNotif;
    }

    public boolean isIntlRoamingEnabled() {
        return intlRoamingEnabled;
    }

    public void setIntlRoamingEnabled(boolean intlRoamingEnabled) {
        this.intlRoamingEnabled = intlRoamingEnabled;
    }

    public boolean isIntlRoamingFreeTierCheck() {
        return intlRoamingFreeTierCheck;
    }

    public void setIntlRoamingFreeTier(boolean intlRoamingFreeTier) {
        this.intlRoamingFreeTierCheck = intlRoamingFreeTier;
    }

    public void setTpNotifyQueue(String tpNotifyQueue) {
        this.tpNotifyQueue = tpNotifyQueue;
    }

    public int getIntlRoamingAndroidVersionsToUpgrade() {
        return intlRoamingAndroidVersionsToUpgrade;
    }

    public void setIntlRoamingAndroidVersionsToUpgrade(int intlRoamingAndroidVersionsToUpgrade) {
        this.intlRoamingAndroidVersionsToUpgrade = intlRoamingAndroidVersionsToUpgrade;
    }

    public int getIntlRoamingIosVersionsToUpgrade() {
        return intlRoamingiOsVersionsToUpgrade;
    }

    public void setIntlRoamingiOsVersionsToUpgrade(int intlRoamingiOsVersionsToUpgrade) {
        this.intlRoamingiOsVersionsToUpgrade = intlRoamingiOsVersionsToUpgrade;
    }

	public int getWapStreamingLimit() {
		return wapStreamingLimit;
	}

	public void setWapStreamingLimit(int wapStreamingLimit) {
		this.wapStreamingLimit = wapStreamingLimit;
	}

    public Integer getMp3AkamaiCloudFrontRatio() {
        return mp3AkamaiCloudFrontRatio;
    }

    public void setMp3AkamaiCloudFrontRatio(Integer mp3AkamaiCloudFrontRatio) {
        this.mp3AkamaiCloudFrontRatio = mp3AkamaiCloudFrontRatio;
    }

    public Integer getMp4AkamaiCloudFrontRatio() {
        return mp4AkamaiCloudFrontRatio;
    }

    public void setMp4AkamaiCloudFrontRatio(Integer mp4AkamaiCloudFrontRatio) {
        this.mp4AkamaiCloudFrontRatio = mp4AkamaiCloudFrontRatio;
    }

    public Integer getMp3PerformaceTestAkamaiCloudRatio() {
        return mp3PerformaceTestAkamaiCloudRatio;
    }

    public void setMp3PerformaceTestAkamaiCloudRatio(Integer mp3PerformaceTestAkamaiCloudRatio) {
        this.mp3PerformaceTestAkamaiCloudRatio = mp3PerformaceTestAkamaiCloudRatio;
    }

    public Integer getMp4PerformanceTestAkamaiCloudRatio() {
        return mp4PerformanceTestAkamaiCloudRatio;
    }

    public void setMp4PerformanceTestAkamaiCloudRatio(Integer mp4PerformanceTestAkamaiCloudRatio) {
        this.mp4PerformanceTestAkamaiCloudRatio = mp4PerformanceTestAkamaiCloudRatio;
    }

    public String getBasicSMSHashCode() {
        return basicSMSHashCode;
    }

    public void setBasicSMSHashCode(String basicSMSHashCode) {
        this.basicSMSHashCode = basicSMSHashCode;
    }

    public String getWcfThanksStream() {
        return wcfThanksStream;
    }

    public void setWcfThanksStream(String wcfThanksStream) {
        this.wcfThanksStream = wcfThanksStream;
    }

    public String getTwitterKey() {
        return twitterKey;
    }

    public void setTwitterKey(String twitterKey) {
        this.twitterKey = twitterKey;
    }

    public String getTwitterSecret() {
        return twitterSecret;
    }

    public void setTwitterSecret(String twitterSecret) {
        this.twitterSecret = twitterSecret;
    }

    public String getWcfPayBaseUrl() {
        return wcfPayBaseUrl;
    }

    public void setWcfPayBaseUrl(String wcfPayBaseUrl) {
        this.wcfPayBaseUrl = wcfPayBaseUrl;
    }

    public String getWcfPaySqsUrl() {
        return wcfPaySqsUrl;
    }

    public void setWcfPaySqsUrl(String wcfPaySqsUrl) {
        this.wcfPaySqsUrl = wcfPaySqsUrl;
    }

    public String getWcfPaySqsRegion() {
        return wcfPaySqsRegion;
    }

    public void setWcfPaySqsRegion(String wcfPaySqsRegion) {
        this.wcfPaySqsRegion = wcfPaySqsRegion;
    }
}
