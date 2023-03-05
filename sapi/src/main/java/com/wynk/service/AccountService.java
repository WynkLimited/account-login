package com.wynk.service;

import com.bsb.portal.core.common.AccessMsnInf;
import com.bsb.portal.core.common.CircleCatalog;
import com.bsb.portal.core.common.CircleInf;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.DBObject;
import com.wynk.call.CallService;
import com.wynk.common.Circle;
import com.wynk.common.Country;
import com.wynk.common.ExceptionTypeEnum;
import com.wynk.common.Language;
import com.wynk.common.MobileNetwork;
import com.wynk.common.PortalException;
import com.wynk.common.ScreenCode;
import com.wynk.common.WynkAppType;
import com.wynk.config.MusicConfig;
import com.wynk.constants.Constants;
import com.wynk.constants.JsonKeyNames;
import com.wynk.constants.MusicConstants;
import com.wynk.db.MongoDBManager;
import com.wynk.db.S3StorageService;
import com.wynk.db.ShardedRedisServiceManager;
import com.wynk.dto.BasicUserInfo;
import com.wynk.dto.LoginCreateAccountDTO;
import com.wynk.dto.MusicSubscriptionStatus;
import com.wynk.dto.NdsUserInfo;
import com.wynk.dto.OtpResult;
import com.wynk.dto.ThirdPartyNotifyDTO;
import com.wynk.exceptions.OTPAuthorizationException;
import com.wynk.music.WCFServiceType;
import com.wynk.music.constants.MusicContentLanguage;
import com.wynk.music.constants.MusicPackageType;
import com.wynk.music.constants.MusicSongQualityType;
import com.wynk.music.dto.ModuleNameOrderType;
import com.wynk.music.dto.MusicPlatformType;
import com.wynk.music.service.MusicContentService;
import com.wynk.musicpacks.FUPPack;
import com.wynk.musicpacks.MusicLanguagesMappings;
import com.wynk.notification.ActionOpen;
import com.wynk.notification.MusicAdminNotification;
import com.wynk.notification.MusicNotificationConstants;
import com.wynk.server.ChannelContext;
import com.wynk.server.HttpResponseService;
import com.wynk.service.api.HTApiService;
import com.wynk.service.api.NdsUserInfoApiService;
import com.wynk.service.helper.WcfHelper;
import com.wynk.sms.SMSService;
import com.wynk.solace.CircleSolaceMapping;
import com.wynk.user.dto.User;
import com.wynk.user.dto.UserDevice;
import com.wynk.user.dto.UserEntityKey;
import com.wynk.utils.EncryptUtils;
import com.wynk.utils.IPRangeUtil;
import com.wynk.utils.JsonUtils;
import com.wynk.utils.LogstashLoggerUtils;
import com.wynk.utils.MusicBuildUtils;
import com.wynk.utils.MusicDeviceUtils;
import com.wynk.utils.MusicUtils;
import com.wynk.utils.NotificationUtils;
import com.wynk.utils.ObjectUtils;
import com.wynk.utils.OperatorUtils;
import com.wynk.utils.UserDeviceUtils;
import com.wynk.utils.UserUtils;
import com.wynk.utils.Utils;
import com.wynk.utils.WCFUtils;
import com.wynk.wcf.WCFApisService;
import com.wynk.wcf.WCFApisUtils;
import com.wynk.wcf.dto.Feature;
import com.wynk.wcf.dto.FeatureType;
import com.wynk.wcf.dto.UserMobilityInfo;
import com.wynk.wcf.dto.UserSubscription;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import kafka.javaapi.producer.Producer;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.wynk.constants.MusicConstants.*;
import static com.wynk.constants.MusicSubscriptionPackConstants.DAY;
import static com.wynk.service.OtpService.getClientPerMinuteOtpCount;
import static com.wynk.service.OtpService.getPinExpiryTimeMinutes;
import static com.wynk.utils.EncryptUtils.ENCRYPTION_KEY_FOR_THANKS;

/**
 * Created with IntelliJ IDEA.
 * User: bhuvangupta
 * Date: 12/11/13
 * Time: 12:27 PM
 * To change this template use File | Settings | File Templates.
 */
@Service ("accountService")
public class AccountService {

	public static final String OFFLINE_SUBSCRIPTION_INTENT = "offlineSubscription=true";

    private static final Logger logger               = LoggerFactory.getLogger(AccountService.class.getCanonicalName());

    private static final Logger mDuplicateUidLogger = LoggerFactory.getLogger("mduplicateuids");

    private static final Logger mFUPResetLogger = LoggerFactory.getLogger("mFupReset");

    public static final String AIRTEL = "airtel";
    public static final String OTHER = "other";

    private static final String OTP_MODE = "otpMode";
    private static final String EMPTY_STRING = "";
    private static final String CALL = "call";

    private static final String DEFAULT_ONBOARDING_CIRCLE = "mb";

    private static final Integer MIN_ONBOARDING_DEFAULT_LANGS = 3;

    private static final Integer MAX_OTP_ATTEMPT = getPinExpiryTimeMinutes() * getClientPerMinuteOtpCount();

    private static final String OTP_CALL_MSG = "Hi, This is a call from Wink Music. Your One time pin for Login is %1$s. I repeat, Your O tea pea for Login is %2$s. Thank you.";

    private static final String DELETE_OTP_CALL_MSG = "There has been an initiation of Account deletion request from your Wynk ID. Please enter PIN %1$s to approve the same. We are sad to see you go hope to serve you again soon!";

    //yes, its thread safe. https://groups.google.com/forum/#!topic/google-gson/Vju1HuJJUIE
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private static  final int regPopUpTtl = 3*24*60*60; // 3 days
    private static  String regPopupCode;

    @Autowired
    private MusicConfig musicConfig;

    @Autowired
    private MusicService musicService;

    @Autowired
    private NotificationUtils notificationUtils;

    @Autowired
    private UserEventService userEventService;

    @Autowired
    private ShardedRedisServiceManager userPersistantRedisServiceManager;

    @Autowired
    private ShardedRedisServiceManager musicUserShardRedisServiceManager;

//    @Autowired
//    private RedisServiceManager musicRedisServiceManager;

    @Autowired
    private MongoDBManager mongoUserDBManager;

    @Autowired
    private S3StorageService s3ServiceManager;

    @Autowired
    private SMSService smsService;

    @Autowired
    private NdsUserInfoApiService ndsUserInfoApiService;

    @Autowired
    @Qualifier("userPersistantRedisServiceManager")
    private ShardedRedisServiceManager notificationCodeRedisServiceManager;

    private ExecutorService execService ;

    @Autowired
    private Producer<String, String> kafkaProducerManager;

    @Autowired
    private MusicContentService musicContentService;

    @Autowired
    WCFService wcfService;

    @Autowired
    private MyAccountService myAccountService;

    @Autowired
    private WCFUtils wcfUtils;

    @Autowired
    private WCFApisService wcfApisService;

    @Autowired
    private WCFApisUtils wcfApisUtils;

    @Autowired
    CallService callService;


  @Autowired
    private OtpService otpService;

    @Autowired
    private AccountRegistrationService accountRegistrationService;

    @Value("${wynk.offer.id:9001}")
    private Integer offerId;

    @Value("${wynk.offer.intent.id:8001}")
    private Integer wynkOfferId;

    @Value("${wynk.app.id:e15a521ea60435036cce94d6020112d5}")
    private String appId;

    @Value("${wynk.app.secret:f71e4bd7cb7c2648eae8259e5d6541d4}")
    private String appSecret;

    @Value("${wynk.service.name:music}")
    private String serviceName;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HTApiService htApiService;



    LoadingCache<String,String> tokenLruCache = CacheBuilder.newBuilder()
            .maximumSize(100000)
            .build(
                    new CacheLoader<String, String>() {
                        public String load(String uid)
                                throws Exception {
                        	return getTokenForMsisdn(uid, true);
                        }

                    });

    @PostConstruct
    public void init()
    {
        execService = Executors.newFixedThreadPool(2);
    }

    @Scheduled(cron ="0 01 00 * * *")
    private void rotateLogsAfterMidnight()
    {
        rotateLoggerForDuplicateUids();
        rotateLoggerForFUPLogs();
    }

    public void rotateLoggerForDuplicateUids()
    {
        mDuplicateUidLogger.info(",");
    }
    public void rotateLoggerForFUPLogs()
    {
        mFUPResetLogger.info("-");
    }

    private String getHostname()
    {
        String hostName = "unknown";

        try {
            InetAddress iAddress = InetAddress.getLocalHost();
            hostName = iAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            logger.info("UnknownHostException"+":Cant find the hostname");
        }

        return  hostName;
    }

    @Scheduled(cron ="0 10 00 * * *")
    private void uploadLogs()
    {
        uploadFUPResetLogsToS3();
        uploadDuplicateUIDLogsToS3();
    }

    private void uploadFUPResetLogsToS3()
    {
        try {
            String analyticsbucketname = musicConfig.getAnalyticsbucketname();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            String date = (new SimpleDateFormat("yyyy-MM-dd")).format(cal.getTime());
            String month = (new SimpleDateFormat("yyyy-MM")).format(cal.getTime());

            String hostname = getHostname();
            String filepath = "/data/log/music/";

            String resetFileName = "fup_reset.csv."+date;
            s3ServiceManager.store(analyticsbucketname, "FUP/Reset/"+month+"/"+date+"/"+hostname+"."+resetFileName, new File(filepath+resetFileName));

            String reachedFileName = "fup_reached.csv."+date;
            s3ServiceManager.store(analyticsbucketname, "FUP/Reached/"+month+"/"+date+"/"+hostname+"."+reachedFileName, new File(filepath+reachedFileName));
        }
        catch (Exception e) {
            LogstashLoggerUtils.createFatalExceptionLogWithMessage(e, ExceptionTypeEnum.CODE.name(), "",
                    "AccountService.uploadFUPResetLogsToS3", "Error updating FUP logs to S3");

            logger.error("Error uploading FUP logs to S3 : "+e.getMessage(),e);
        }
    }

    private void uploadDuplicateUIDLogsToS3()
    {
        try {
            String analyticsbucketname = musicConfig.getAnalyticsbucketname();

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            String date = (new SimpleDateFormat("yyyy-MM-dd")).format(cal.getTime());
            String month = (new SimpleDateFormat("yyyy-MM")).format(cal.getTime());

            String hostname = getHostname();
            String filepath = "/data/log/music/";
            String fileName = "duplicate_uids.csv."+date;

            s3ServiceManager.store(analyticsbucketname, "DuplicateUIDMapData/"+month+"/"+date+"/"+hostname+"."+fileName, new File(filepath+fileName));
        }
        catch (Exception e) {

            LogstashLoggerUtils.createCriticalExceptionLog(e,
                    ExceptionTypeEnum.CODE.name(),
                    ChannelContext.getUid(),
                    "AccountService.uploadDuplicateUIDLogsToS3");

            logger.error("Error uploading Duplicate UID logs to S3 : "+e.getMessage(),e);
        }
    }


    @Scheduled(cron ="0 0 * * * *")
    private void refreshRegistrationNotificationCode()
    {
        setRegistrationPopupCode();
    }

    public  String getRegPopupCode() {
        if (StringUtils.isEmpty(regPopupCode)){
            setRegistrationPopupCode();
        }
        return regPopupCode;
    }

    public boolean checkForMusicUserTokenPresence(HttpRequest request) {
        String wapUID = request.headers().get(MusicConstants.MUSIC_HEADER_WAP);
        String userToken = request.headers().get("x-bsy-utkn");
        if(!StringUtils.isEmpty(wapUID) || !StringUtils.isEmpty(userToken)) {
            return true;
        }
        return false;
    }

    public boolean shouldPurgeDataFromOldUser(UserDevice device) {
        if (ChannelContext.getClientTypeFromDeviceOs(device.getOs()) == 2 && device.getBuildNumber() < 53) {
            return false;
        }
        return true;
    }

	public void updateAuthenticatedUsersData(String uid, HttpRequest request) {
		ChannelContext.setUid(uid);
		MDC.put("uid", uid);

		String msisdn = UserDeviceUtils.getMsisdn(request);
		String circleShortName 	= null;
		User appUser 			= getAppUserFromUID(uid, request);

		if (UserDeviceUtils.isWAPUser(uid)) {

		    ChannelContext.setUser(appUser);
		    return;
		}


		if (appUser != null) {
			circleShortName = appUser.getCircle();
			if (StringUtils.isEmpty(ChannelContext.getMsisdn()))
				msisdn = appUser.getMsisdn();
		} else {
			logger.info("No user object in DB for UID : " + ChannelContext.getUid());
		}

		if (!StringUtils.isEmpty(msisdn))
			ChannelContext.setMsisdn(msisdn);
		ChannelContext.setCircle(circleShortName);

		boolean isAirtelMobileIP 	= OperatorUtils.isAirtelMobileIp();
		boolean isAirtelUser 		= OperatorUtils.isAirtelUser();

		boolean updateUserInCache 		= false;
		Map<String, Object> queryParams = new HashMap<>();
		Map<String, Object> queryValues = new HashMap<>();
		String msisdnFromHeaders 		= UserDeviceUtils.getMsisdn(request);

		if (appUser != null && !isAirtelUser && StringUtils.isNotBlank(appUser.getMsisdn())) {
			if (isAirtelMobileIP && appUser.getMsisdn().equals(msisdnFromHeaders)) {

				String isTestHeaderPresent = request.headers().get("x-bsy-test");
				if (StringUtils.isBlank(isTestHeaderPresent) && ChannelContext.getUser() != null) {
					ChannelContext.getUser().setOperator(AIRTEL);
					queryParams.put(UserEntityKey.uid, ChannelContext.getUser().getUid());
					queryValues.put(UserEntityKey.operator, AIRTEL);
					updateUserInCache = true;
					userEventService.addUserUpdateEvent(uid, getDeviceId(), JsonKeyNames.OPERATOR, AIRTEL);
				}

			}
		}
		if (updateUserInCache) {
			boolean setField = mongoUserDBManager.setField(USER_COLLECTION, queryParams, queryValues, false);
			updateUserInCache(ChannelContext.getUser().getUid(), false);
		}
        String etag = request.headers().get("If-None-Match");
        ChannelContext.setEtag(etag);
	}

	public User getAppUserFromUID(String uid, HttpRequest request) {

	    if (UserDeviceUtils.isWAPUser(uid)) {
	        return getWAPUserObjectByUidFromDB(uid);
	    }

		User appUser = null;
		if (ChannelContext.getUser() == null) {
			try {
				appUser = getUserFromUid(uid);
				appUser = setUserInContext(appUser);
				setDeviceDetailsInContext(appUser, request);
			} catch (Exception e) {
				LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e, ExceptionTypeEnum.CODE.name(), uid,
						"AccountService._authenticate", "Error set user object in context " + uid);
				logger.error("Error set user object in context : " + uid + ". Error :" + e.getMessage(), e);
			}
		}
		return appUser;
	}

	public void setDeviceDetailsInContext(User appUser, HttpRequest request)
    {
		Map<String,String> result= MusicDeviceUtils.parseMusicHeaderDID();
        String os = null ;
        String did = null;
        int appBuildNo = -1;
        String appVersion = null;

		if (result.size() == 5) {
			os = result.get(MusicConstants.OS);
			String abn = result.get(MusicConstants.APP_BUILD_NO);
			appVersion = result.get(MusicConstants.APP_VERSION_NO);
			if (abn != null){
				try {
					appBuildNo = Integer.parseInt(abn);
				} catch (NumberFormatException e) {
					logger.info("Error parsing x-bsy-did header - Error - NumberFormatError");
				}
			}
		}
		if(result.size() > 0) {
			did = result.get(MusicConstants.DEVICE_ID);
        }

        if(appUser != null)
        {
        	if(os == null  || appBuildNo == 0 || appVersion == null) {
                UserDevice userDevice = MusicDeviceUtils.getUserDeviceFromDid(appUser, did);
                if(userDevice != null) {
                    if (os == null)
                        os = userDevice.getOs();
                    if (appBuildNo == 0)
                        appBuildNo = userDevice.getBuildNumber();
                    if (appVersion == null)
                        appVersion = userDevice.getAppVersion();
                }
            }
        }
        ChannelContext.setOscontext(os);
        ChannelContext.setAppVersionContext(appVersion);
        ChannelContext.setBuildnumbercontext(appBuildNo);
    }

    private User setUserInContext(User appUser)
            throws Exception
    {
        if(appUser != null)
        {
            if(logger.isDebugEnabled())
                logger.debug("Set user object in Context for : "+appUser.getMsisdn()+","+appUser.getUid());
            ChannelContext.setUser(appUser);
            return appUser;
        }
        return null;
    }

    private static String DEVICE_KEY = "deviceId";
    private static String USER_AGENT = "userAgent";

  private User createWAPUser(String uuid, JSONObject requestObj) {
    User appUser;
    String token = generateTokenForMsisdn(uuid);

    //create if it does not exists
    appUser = new User();
    appUser.setUid(uuid);

    appUser.setCreationDate(System.currentTimeMillis());
    appUser.setLastActivityDate(System.currentTimeMillis());

    List<UserDevice> devices = appUser.getDevices();
    if (devices == null) {
      devices = new ArrayList<>();
    }

    UserDevice device = new UserDevice();

    if (requestObj.get("totpDeviceId") != null) {
      device.setTotpDeviceId((String) requestObj.get("totpDeviceId"));
    }
    if (requestObj.get("totpKey") != null) {
      device.setTotpDeviceKey((String) requestObj.get("totpKey"));
    }

    if (StringUtils.isNotBlank(DEVICE_KEY)) {
      device.setDeviceId((String) requestObj.get(DEVICE_KEY));
    }

    if (requestObj.get("did") != null) {
      device.setDeviceId((String) requestObj.get("did"));
    }

    if (StringUtils.isNotBlank(USER_AGENT)) {
      device.setDeviceKey((String) requestObj.get(USER_AGENT));
    }

    device.setUid(uuid);
    devices.add(device);

    appUser.addPack(FUPPack.class.getSimpleName(), System.currentTimeMillis(), MusicConstants.MUSIC_FUP_VALIDITY_IN_MILLIS);
    appUser.setDevices(devices);

    appUser.setToken(token);
    appUser.setSource(4);

    createWAPUser(appUser);
    logger.info("Successfully created WAP user with device id" + device.getDeviceKey());

    return appUser;
  }

  public User findOrCreateWapUser(JSONObject requestPayload) {
    String deviceId = (String) requestPayload.get(DEVICE_KEY);
    String did = "";
    if (deviceId == null && requestPayload.get("did") == null) {
      logger.info("device id found null not proceding further " + requestPayload);
      return null;
    }
    // new auth did 64 byte
    if (requestPayload.get("did") != null) {
      did = (String) requestPayload.get("did");
    } else {
      did = deviceId;
    }

    // generate uuid from deviceId
    String uuid = UserDeviceUtils.generateUUIDWithExceptionHandling(did, ChannelContext.getRequest());

    if (StringUtils.isBlank(uuid)) {
      logger.info("error generating uuid not proceding further" + requestPayload);
    }

    User wapUser = getUserFromUidForWAP(uuid, true);

    if (wapUser != null) {
      // user found return the user
      return wapUser;
    }

    // user not found have to create one..
    wapUser = createWAPUser(uuid, requestPayload);
    return wapUser;
  }

    public List<String> getContentLanguageList(User appUser, UserMobilityInfo wcfOperatorInfo,Boolean isNewRegistration) {
        List<String> contentLanguageList = new ArrayList<String>();

        logger.info("Before setting up language we have Appuser : {} , wcfOperatorInfo is {}", appUser, wcfOperatorInfo);

        if(isNewRegistration) {
            if ((appUser.getCircle() != null) && (appUser.getCircle().equalsIgnoreCase("TN") || appUser.getCircle().equalsIgnoreCase("CH"))) {
                contentLanguageList.add(MusicContentLanguage.TAMIL.getId());
            }

            if ((appUser.getCircle() != null) && (appUser.getCircle().equalsIgnoreCase("AP"))) {
                contentLanguageList.add(MusicContentLanguage.TELUGU.getId());
            }

            if (appUser.getCircle() != null && wcfOperatorInfo != null && wcfOperatorInfo.isCircleDetectedFromMccMnc()) {
                List<MusicContentLanguage> defaultLanguages = musicContentService.getDefaultLanguageByCircle(appUser.getCircle());
                contentLanguageList = Utils.ConvertContentLangListToStringList(defaultLanguages);
            }
            logger.info("Exting new registration");
        }
        else {
            if (wcfOperatorInfo != null && wcfOperatorInfo.isCircleDetectedFromMccMnc() && StringUtils.isNotBlank(wcfOperatorInfo.getCircleShortName()) && StringUtils.isNotBlank(wcfOperatorInfo.getOperator()) && appUser.getCircle() == null) {
                appUser.setCircle(wcfOperatorInfo.getCircleShortName());
                appUser.setOperator(wcfOperatorInfo.getOperator());
                updateCirlceAndOperatorForUser(appUser, wcfOperatorInfo.getCircleShortName(), wcfOperatorInfo.getOperator());
            }
        }
        logger.info("Returning clang with {}", contentLanguageList);

        return contentLanguageList;
    }

    public void sendEventOnRegistration(User appUser, UserDevice userDevice) {

    	ChannelContext.setUid(appUser.getUid());
        ChannelContext.setUser(appUser);
        if (StringUtils.isNotBlank(userDevice.getImeiNumber())) {
            musicService.fireIMEIReceivedEvent(appUser, userDevice);
        }

        if (musicService.getStatsMonitoring() != null) {
            //musicService.getStatsMonitoring().increment("wynk.user.new");
            musicService.getStatsMonitoring().increment("wynk.user.new." + appUser.getSource());
        }

        String uuid = ChannelContext.getUid();
        if (StringUtils.isNotBlank(appUser.getMsisdn()) && appUser.getSource() != 0) {
            userEventService.addRegisteredEvent();
            userEventService.addUserDeviceEvent(uuid, userDevice);
        }

        if (StringUtils.isBlank(appUser.getMsisdn()) && appUser.getSource() != 0) {
            userEventService.addUserCreationEvent(appUser.getUid());
            userEventService.addUserDeviceEvent(uuid, userDevice);
        }

        if (StringUtils.isNotBlank(userDevice.getDeviceKey())) {
            musicService.addAppInstallEventOnAccountCreation();
        }

    }


    public void purgeData(Boolean migrateDeviceUidData,LoginCreateAccountDTO loginCreateAccountDTO){
        UserDevice currentDevice = loginCreateAccountDTO.getCurrentDevice();
        if (migrateDeviceUidData && ChannelContext.getUser() != null) {
            if (shouldPurgeDataFromOldUser(currentDevice)) {
                //updateFavoritesForUser(ChannelContext.getUser(), new ArrayList<UserFavorite>());
                //updateRentalsForUser(ChannelContext.getUser(), new ArrayList<UserRental>());
            }

            removeUserFromCache(ChannelContext.getUser().getUid());
            musicService.removeUserJourney(ChannelContext.getUser().getUid());
        }
    }

    public void addUserSelectedLanguagesIfEmpty(JSONObject response) {
    	if(response==null || response.isEmpty())
    		return;
    	Map<String,String> oSBuildNo = MusicDeviceUtils.getOSAndBuildNo();
        String os = oSBuildNo.get(MusicConstants.OS);
        String buildNumber = oSBuildNo.get(MusicConstants.APP_BUILD_NO);
        int currentBuildNum = Integer.parseInt(buildNumber);
        boolean isIos = MusicDeviceUtils.isIOSDeviceFromOS(os);
        if(response.get(JsonKeyNames.SELECTED_CONTENT_LANGS)==null || ((JSONArray)response.get(JsonKeyNames.SELECTED_CONTENT_LANGS)).isEmpty()){
        	//Don't add hi/en in case of ios builds older than langSplit because iso doesn't remove hi/en if sent in selectedContentLangs
        	if(!isIos || MusicBuildUtils.isNewerBuildNumber(currentBuildNum, MusicConstants.IOS_LANGSPLIT_BUILD_NUMBER)){
            	List<String> defaultLanguageList = new ArrayList<String>();
               	defaultLanguageList.add(MusicContentLanguage.HINDI.getId());
               	defaultLanguageList.add(MusicContentLanguage.ENGLISH.getId());
               	response.put(JsonKeyNames.SELECTED_CONTENT_LANGS, Utils.convertToJSONArray(defaultLanguageList));
            }
        }
	}

    public UserDevice getUpdatedDevice(UserDevice device, Boolean fireIMEIEvent, User user) {
    	if(user == null)
    		return device;
        List<UserDevice> existingDevices = user.getDevices();
        // Add device token handling here for IOS
        if(!CollectionUtils.isEmpty(existingDevices)) {
            for (int i = 0; i < existingDevices.size(); i++) {
                UserDevice userDevice = existingDevices.get(i);
                if (StringUtils.isEmpty(userDevice.getDeviceId()))
                    continue;
                  if (userDevice.getDeviceId().equals(device.getDeviceId())) {
                    if(StringUtils.isNotBlank(userDevice.getTotpDeviceId()))
                    {
                      device.setTotpDeviceId(userDevice.getTotpDeviceId());
                    }
                    if(StringUtils.isNotBlank(userDevice.getTotpDeviceKey()))
                    {
                      device.setTotpDeviceKey(userDevice.getTotpDeviceKey());
                    }
                    if (StringUtils.isNotBlank(userDevice.getDeviceKey())) {
                        device.setDeviceKey(userDevice.getDeviceKey());
                        device.setDeviceKeyLastUpdateTime(System.currentTimeMillis());
                    }
                    if (StringUtils.isBlank(device.getPreInstallOem())) {
                        device.setPreInstallOem(userDevice.getPreInstallOem());
                    }
                    device.setFbUserId(userDevice.getFbUserId());
                    device.setAdvertisingId(userDevice.getAdvertisingId());
                    if (StringUtils.isNotBlank(device.getImeiNumber())) {
                        if (fireIMEIEvent && !device.getImeiNumber().equalsIgnoreCase(userDevice.getImeiNumber())) {
                            // Fire IMEI event
                            musicService.fireIMEIReceivedEvent(user, device);
                        }
                    }

                }
            }
        }
        return device;
    }

    public boolean isAirtelUser(User user) {
        if (StringUtils.isNotBlank(user.getOperator()) && user.getOperator().toLowerCase().contains("airtel"))
            return true;

        return false;
    }

    public void updateOperator(User user,String operator) {
    	updateOperatorForUid(user.getUid(), operator, null);
        updateUserInCache(user.getUid(), false);
        user.setOperator(operator);
    }

    public boolean updateDevicesForUid(String uuid, List<UserDevice> userDevices) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(UserEntityKey.uid, uuid);
        Map<String, Object> queryValues = new HashMap<>();
        JSONArray deviceArray = new JSONArray();
        for(int i = 0; i < userDevices.size(); i++) {
            UserDevice userDevice = userDevices.get(i);
            deviceArray.add(userDevice.toJsonObject());
        }
        queryValues.put(UserEntityKey.devices, deviceArray);
        boolean setField = mongoUserDBManager.setField(USER_COLLECTION, queryParams, queryValues, false);
        return setField;
    }

    public boolean updateCirlceAndOperatorForUser(User user, String circleId,String operatorName ) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(UserEntityKey.uid, user.getUid());
        Map<String, Object> queryValues = new HashMap<>();
        queryValues.put(UserEntityKey.circle, circleId);
        queryValues.put(UserEntityKey.operator, operatorName);
        boolean setField = mongoUserDBManager.setField(USER_COLLECTION, queryParams, queryValues, false, false);
        return setField;
    }

    public boolean updateContentLangsForUser(String uuid, List<String> contentLangs) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(UserEntityKey.uid, uuid);
        Map<String, Object> queryValues = new HashMap<>();
        queryValues.put(UserEntityKey.contentLanguages, contentLangs);
        boolean setField = mongoUserDBManager.setField(USER_COLLECTION, queryParams, queryValues, false);
        return setField;
    }

    public boolean updateDevicesForUid(String uuid, List<UserDevice> userDevices, int source, String userType, String appLang) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(UserEntityKey.uid, uuid);
        Map<String, Object> queryValues = new HashMap<>();
        JSONArray deviceArray = new JSONArray();
        for(int i = 0; i < userDevices.size(); i++) {
            UserDevice userDevice = userDevices.get(i);
            deviceArray.add(userDevice.toJsonObject());
        }
        queryValues.put(UserEntityKey.devices, deviceArray);
        queryValues.put(UserEntityKey.source, source);
        if(StringUtils.isNotBlank(userType))
            queryValues.put(UserEntityKey.userType, userType);
        if (StringUtils.isNotBlank(appLang))
            queryValues.put(UserEntityKey.lang, appLang);
        boolean setField = mongoUserDBManager.setField(USER_COLLECTION, queryParams, queryValues, false);
        return setField;
    }

    private boolean updateLastActivityForUid(String uuid) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(UserEntityKey.uid, uuid);
        Map<String, Object> queryValues = new HashMap<>();
        queryValues.put(UserEntityKey.lastActivityDate, System.currentTimeMillis());
        boolean setField = mongoUserDBManager.setField(USER_COLLECTION, queryParams, queryValues, false);
        return setField;
    }

    public String getMsisdnByUID() {
        String msisdn = "";
        if(StringUtils.isBlank(ChannelContext.getUid()))
            return msisdn;

        User user = ChannelContext.getUser();
        if(user == null) {
            return msisdn;
        }
        else {
            msisdn = user.getMsisdn();
        }
        if(StringUtils.isNotEmpty(msisdn)) {
            Country country=Country.getCountryByCountryId(user.getCountryId());
            msisdn = Utils.normalizePhoneNumber(msisdn,country);
        }
        return msisdn;
    }

  public DBObject getUserObjectByUid(String uid) {
    if(StringUtils.isBlank(uid))
      return null;
    DBObject userObject;
    Map queryParams = new HashMap<>();
    queryParams.put(UserEntityKey.uid, uid);
    if (UserDeviceUtils.isWAPUser(uid)) {
            userObject = mongoUserDBManager.getObject(WAP_USER_COLLECTION, queryParams);
      } else {
            userObject = mongoUserDBManager.getObject(USER_COLLECTION, queryParams);
      }
      return userObject;
   }

    public void purgeUser(User user){
        long expireAt = System.currentTimeMillis() + (30 * DAY);
        Date expiryDate = new Date(expireAt);

        Map<String, Object> query = new HashMap<>();
        query.put(UserEntityKey.uid, user.getUid());
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(UserEntityKey.expireAt, expiryDate);

        boolean updateDB =  mongoUserDBManager.setField(MusicConstants.USER_COLLECTION, query, valueMap);
        if (updateDB){ // remove from cache
            removeUserFromCache(user.getUid());
        }
    }

    public void removeExpireAtField(String uid){

        Map<String, Object> query = new HashMap<>();
        query.put(UserEntityKey.uid, uid);
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(UserEntityKey.expireAt, "");

        boolean updateDB =  mongoUserDBManager.unsetField(MusicConstants.USER_COLLECTION, query, valueMap);
        if (updateDB){ // remove from cache
            removeUserFromCache(uid);
        }
    }


    public void removeDeviceForUidAndPurgeUser(UserDevice device, String uid, Boolean isRegistered) {
        User appUser = getUserFromUid(uid, false);
        if (appUser == null)
            return;
        List<UserDevice> existingDevices = appUser.getDevices();

        if (!isRegistered) {
            if (CollectionUtils.isEmpty(existingDevices)) {
                purgeUser(appUser);
                return;
            }
            if (existingDevices.size() == 1) {
                UserDevice userDevice = existingDevices.get(0);
                if (userDevice.getDeviceId().equalsIgnoreCase(device.getDeviceId())) {
                    purgeUser(appUser);
                }
            }
        }

        List<UserDevice> userDevices = new ArrayList<>();
        for (int i = 0; i < existingDevices.size(); i++) {
            UserDevice userDevice = existingDevices.get(i);
            if (StringUtils.isEmpty(userDevice.getDeviceId()))
                continue;
            if (!userDevice.getDeviceId().equals(device.getDeviceId())) {
                userDevices.add(userDevice);
            }
        }
        updateDevicesForUid(uid, userDevices);
        removeUserFromCache(uid);
    }

    public static String getRedisUidKey(String uid)
    {
        return String.format(MusicConstants.REDIS_UID_HASH, uid);
    }

    public static String getRedisMTokenKey(String uid)
    {
        return String.format(MusicConstants.MTOKEN_UID_HASH, uid);
    }

    private User getUserFromJson(String userJson) {
        User user = new User();
        try {
            user.fromJson(userJson);
        }
        catch (Exception e) {
            logger.error("unable to cast user from json" + userJson);
            return null;
        }

        if (StringUtils.isBlank(user.getUid())) {

            logger.error("Empty user found returning null" + userJson);
            return null;
        }

        return user;
    }

    private User getUserFromCache(String redisUidKey) {

        String userJson = "";
        try {
            userJson = musicUserShardRedisServiceManager.get(redisUidKey);
        } catch (Exception e) {
            logger.info("Error while fetching user from redis for uid :" + redisUidKey);
            e.printStackTrace();
            return null;
        }

        if (StringUtils.isEmpty(userJson)) {
            logger.info("user not found in cache" + redisUidKey);
            return null;
        }

        return getUserFromJson(userJson);
    }

    private User getWAPUserObjectByUidFromDB(String uid) {

        if (StringUtils.isBlank(uid))
            return null;

        DBObject userObject;

        Map queryParams = new HashMap<>();
        queryParams.put(UserEntityKey.uid, uid);

        userObject = mongoUserDBManager.getObject(WAP_USER_COLLECTION, queryParams);

        if (userObject == null) {
            logger.info("user not found in the db" + uid);
            return null;
        }

        User user = getUserFromJson(userObject.toString());

        return user;
    }

    public User getUserFromUidForWAP(String uid, Boolean writeToCache) {

        String redisUidKey = getRedisUidKey(uid);
        User user = getUserFromCache(redisUidKey);

        if (user != null) {
            logger.info("user found in cache returning  user" + user.getUid());
            return user;
        }

        user = getWAPUserObjectByUidFromDB(uid);

        if (user != null) {
            createUpdateUserInCache(user);
        }
        return user;
    }


	public User getUserFromUid(String uid, Boolean writeToCache)
    {
        String userJson = null;
        JSONObject userJsonObject = null;
        User appUser = null;
        try {
            String redisUidKey = getRedisUidKey(uid);
          if(musicUserShardRedisServiceManager != null)
            {
                try {
                    userJson = musicUserShardRedisServiceManager.get(redisUidKey);
                }
                catch (Exception e) {
                    LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e,
                            ExceptionTypeEnum.INFRA.REDIS.name(),
                            uid,
                            "AccountService.getUserFromUid",
                            "Error fetching user from Redis for UID:" + uid);

                    logger.error("Error fetching user from Redis for UID : "+uid+" ; Error : "+e.getMessage(),e);
                }
                if(!StringUtils.isEmpty(userJson))
                {
                    if(logger.isDebugEnabled())
                        logger.debug("Fetching From Cache - User details for uid : " + uid);

                    try {
                        userJsonObject =  (JSONObject) JSONValue.parseWithException(userJson);
                        appUser = new User();
                        appUser.fromJson(userJson);
                    }
                    catch (Exception e) {
                        logger.info("Error getting user details from cache for user json",e.getMessage(),e);
                    }

                }
            }
            if(userJsonObject == null)
            {
                DBObject userObject = getUserObjectByUid(uid);
                if(userObject != null)
                {
                    logger.info("Fetched user object from db");
                    userJson = userObject.toString();
                    appUser = new User();
                    appUser.fromJson(userJson);
                    if(!StringUtils.isBlank(uid) && writeToCache)
                    {
                        createUpdateUserInCache(appUser);
                    }
                }
            }
        }
        catch (Exception e) {
            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e,
                    ExceptionTypeEnum.CODE.name(),
                    uid,
                    "AccountService.getUserFromUid",
                    "Error fetching user details for  UID" + uid);


            logger.error("Error fetching User details for uid : "+uid+". Error : "+e.getMessage(),e);
        }
        return appUser;

    }

    public User getUserFromUid(String uid) {

        if (UserDeviceUtils.isWAPUser(uid)) {
            return getWAPUserObjectByUidFromDB(uid);
        }

        return getUserFromUid(uid, true);
    }

    public User getUserFromDB(String uid) {
      if (UserDeviceUtils.isWAPUser(uid)) {
            return getWAPUserObjectByUidFromDB(uid);
      }
        removeUserFromCache(uid);
        return getUserFromUid(uid, true);
    }

    public User updateUserInCache(String uid, Boolean updateLastActivity)
    {
        User appUser = null;
        try {
            DBObject userObject = getUserObjectByUid(uid);
            if(userObject != null)
            {
                String userJson = userObject.toString();
                appUser = new User();
                appUser.fromJson(userJson);
                if(updateLastActivity)
                {
                    appUser.setLastActivityDate(System.currentTimeMillis());
                    Map<String,Object> queryParams = new HashMap<>();
                    queryParams.put(UserEntityKey.uid,appUser.getUid());
                    Map<String,Object> queryValues = new HashMap<>();
                    queryValues.put(UserEntityKey.lastActivityDate,appUser.getLastActivityDate());
                    try {
                      if(UserDeviceUtils.isWAPUser(uid)){
                        mongoUserDBManager.setField(WAP_USER_COLLECTION, queryParams,queryValues, false);
                      }else{
                        mongoUserDBManager.setField(USER_COLLECTION, queryParams,queryValues, false);
                      }
                    } catch (Exception e) {
                        LogstashLoggerUtils.createFatalExceptionLogWithMessage(e,
                                ExceptionTypeEnum.INFRA.MONGO.name(),
                                appUser.getUid(),
                                "AccountService.updateUserInCache",
                                "Error updating user in mongo");
                    }
                }
                createUpdateUserInCache(appUser);
            }
        }
        catch (Exception e) {
            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e,
                    ExceptionTypeEnum.CODE.name(),
                    uid,
                    "AccountService.updateUserInCache",
                    "Error updating user in db object");

            logger.error("Error updating User details in Redis for uid : "+uid+". Error : "+e.getMessage(),e);
        }
        return appUser;
    }

    public JSONObject getJsonObjectFromPayload(String requestPayload) throws PortalException {
        JSONObject requestJson = null;
        if(!StringUtils.isEmpty(requestPayload)) {
            try {
                requestJson = (JSONObject) JSONValue.parseWithException(requestPayload);
            }
            catch (ParseException e) {
                LogstashLoggerUtils.createCriticalExceptionLog(e,
                        ExceptionTypeEnum.CODE.name(),
                        ChannelContext.getUid(),
                        "AccountService.getJsonObjectFromPayload");

                return createErrorResponse("BSY001","Error parsing response");
            }
        }
        return requestJson;

    }

    public JSONObject generatePin(JSONObject requestPayload, boolean isFourDigitPin, boolean isEncrypted)
            throws PortalException
    {
        String countryCode=(String) requestPayload.get("countryCode");
        String msisdn = (String) requestPayload.get("msisdn");
        if(msisdn!=null) {
          try {
            msisdn = isEncrypted?EncryptUtils.decrypt_256(msisdn, EncryptUtils.getDeviceKey()):msisdn;
          } catch (Exception e) {
            logger.error("Error while decrypting msisdn", e);
          }
        }

      Country country=Country.getCountryByCountryCode(countryCode);
        if(StringUtils.isEmpty(msisdn))
        {
            return createErrorResponse("BSY001","unable to determine msisdn");
        }
        msisdn = Utils.normalizePhoneNumber(msisdn,country);
        OtpResult otpResult = null;
        String intention = (String) requestPayload.get("intent");
        otpResult = Utils.isIndentTypeValid(intention) ? otpService.generateDeleteOtp(msisdn, isFourDigitPin, false)
                : otpService.generateOtp(msisdn, isFourDigitPin, false);
        String otp = otpResult != null ? otpResult.getOtp():null;
        Integer otpRequestCount = otpResult != null ? otpResult.getOtpRequestCount():0;

        if (!StringUtils.isBlank(otp) && (otpRequestCount <= MAX_OTP_ATTEMPT)) {
            HashMap<String, String> logExtras = new HashMap();
            logExtras.put("generated otp", otp);
            LogstashLoggerUtils.createStandardLog("MUSIC_REGISTRATION", "", msisdn, "", logExtras);
            String otpMsg = prepareMsgBasedOnIndent(otp, intention);
            try {
              // checking if number can be bypassed
                String otpBypass =
                    musicUserShardRedisServiceManager.get(
                        "bypass_" + Utils.getTenDigitMsisdnWithoutCountryCode(msisdn));
                if (StringUtils.isNotBlank(otpBypass)) {
                  logger.info("bypassing msisdn : {} , by users : {}", msisdn, otpBypass);
                  msisdn = Utils.normalizePhoneNumber(otpBypass);
                }
            } catch (Exception e) {
              logger.info("exception while bypassing the msisdn : {}", msisdn);
            }
            String otpMode = (String) requestPayload.getOrDefault(OTP_MODE, EMPTY_STRING);
            logger.info("otpMode for msisdn : {} is {}", msisdn, otpMode);
            if (otpMode.equals(CALL)) {
                try {
                    logger.info("Calling wcf for {}", msisdn);
                    String slowOtp = convertIntoSlowOtpForCall(otp);
                    String callServiceResponse = Utils.isIndentTypeValid(intention) ? callService.requestCall(msisdn, String.format(DELETE_OTP_CALL_MSG, slowOtp,
                            slowOtp), "HIGH") : callService.requestCall(msisdn, String.format(OTP_CALL_MSG, slowOtp,
                            slowOtp), "HIGH");
                    //passing empty as no uid at this pt => before login
                    String logType = Utils.isIndentTypeValid(intention) ? "OTP_DELETE_CALL_SUCCESS_LOGGER" : "OTP_CALL_SUCCESS_LOGGER";
                    LogstashLoggerUtils.createAccessLogLiteV2(logType,
                            String.format("wcf call api response is %s", callServiceResponse), msisdn, "");
                }
                catch (Exception e) {
                    LogstashLoggerUtils.otpCallExceptionLogger(e, msisdn, ExceptionTypeEnum.THIRD_PARTY.WCF.name());
                }
            } else {
                smsService.sendWynkMessageWithSMSAPiWithRetryCount(
                        msisdn,
                        otpMsg,
                        false,
                        "HIGH",
                        false,
                        otpResult.getRetryCount(),
                        country.getCountryCode());
            }
    }

        JSONObject response = new JSONObject();
        response.put("msisdn",msisdn);
        //response.put("otp",otp);

        return response;
    }

    public JSONObject sendSMSWithText(JSONObject requestPayload)
            throws PortalException
    {
        String countryCode=(String) requestPayload.get("countryCode");
        String uid = (String) requestPayload.get("uid");
        if(StringUtils.isEmpty(uid)) {
            return createErrorResponse("BSY001","unable to determine uid");
        }
        String message = (String) requestPayload.get("message");
        if (StringUtils.isNotBlank(message) && message.contains(UNINSTALL_MESSAGE)) {
            String doNotSend = musicUserShardRedisServiceManager.get(UNINSTALL_MESSAGE_REDIS_KEY_PREFIX + uid);
            if (doNotSend != null) {
                JSONObject response = new JSONObject();
                response.put("message", "Duplicate SMS request within 24 hours");
                LogstashLoggerUtils.createAccessLogLite("DUPLICATE_SMS_REQUEST", "", uid);
                return response;
            } else {
                musicUserShardRedisServiceManager.setex(UNINSTALL_MESSAGE_REDIS_KEY_PREFIX + uid, "", 24*60*60);
            }
        }
        User user = getUserFromUid(uid);
        String msisdn = user.getMsisdn();
        Country country=Country.getCountryByCountryCode(countryCode);
        if(StringUtils.isEmpty(msisdn)) {
            return createErrorResponse("BSY001","unable to determine msisdn");
        }
        msisdn = Utils.normalizePhoneNumber(msisdn,country);
        requestPayload.put("msisdn",msisdn);
        requestPayload.put("countryCode",countryCode);
        requestPayload.put("useDnd", Boolean.FALSE);
        requestPayload.remove("uid");
        requestPayload.put("priority", "LOW");
        smsService.sendMessageWithSMSAPI(requestPayload);
        logger.info("SMS Send with requst paylaod" + requestPayload);
        JSONObject response = new JSONObject();
        response.put("msisdn",msisdn);
        musicService.createGCMUninstallLog(user,user.getActiveDevice());
        return response;
    }

    public boolean validateOtp(String msisdn, String otp, String intent, Country country) throws OTPAuthorizationException {
        /*
        using the existing method. It returns the msisdn required in account login flow.
        In case of mismatch it will throw exception.
        So, we don't need the isEmpty check. We can return true.
         */
        String authenticatedMsisdn = accountRegistrationService.validateOtp(msisdn, otp, true, country
                , intent);
        return StringUtils.isNotEmpty(authenticatedMsisdn);
    }

    public JSONObject updateUserProfile(JSONObject requestPayload)
            throws PortalException
    {
        User user = ChannelContext.getUser();
        if(user == null)
        {
            return createErrorResponse("BSY001","No user associated  with uid : "+ChannelContext.getUid());
        }

        String uname = (String) requestPayload.get("name");
        String email = (String) requestPayload.get("email");
        String gender = (String) requestPayload.get("gender");
        JSONObject dob = (JSONObject) requestPayload.get("dob");
        String songQuality = (String) requestPayload.get("songQuality");
        String applang = (String) requestPayload.get("lang");
        JSONArray contentLang = (JSONArray) requestPayload.get("contentLang");
        Boolean autoRenewal = (Boolean) requestPayload.get("autoRenewal");
        Boolean notifications = (Boolean) requestPayload.get("notifications");
        String deviceKey = (String) requestPayload.get("devicekey");
        String downloadQuality = (String) requestPayload.get("downloadQuality");

        if(StringUtils.isNotBlank(songQuality)) {
            try {
                MusicSongQualityType songQualityVal = MusicSongQualityType.valueOf(songQuality);
                songQuality = songQualityVal.name();
            } catch (Exception e) {
                songQuality = "a";
            }
        }

        if(StringUtils.isNotBlank(downloadQuality)) {
            try {
                MusicSongQualityType downloadQualityVal = MusicSongQualityType.valueOf(downloadQuality);
                downloadQuality = downloadQualityVal.name();
            } catch (Exception e) {
                downloadQuality = "h";
            }
        }

        String fbToken = (String) requestPayload.get("fbtoken");

        boolean updateLastActivity = false;
        if(!StringUtils.isEmpty(uname) && uname.length() > 50)
        {
            return createErrorResponse("BSY001","Invalid User name. Max length should be 50 characters");
        }

        if(!StringUtils.isEmpty(email) && !MusicUtils.isValidEmail(email))
        {
            return createErrorResponse("BSY001","Invalid Email");
        }

        if(dob != null && !validateDOB(dob))
        {
            return createErrorResponse("BSY001","Invalid DOB");
        }

        if(!StringUtils.isEmpty(applang) && (Language.getLanguageById(applang) == null))
        {
            return createErrorResponse("BSY001","Invalid Application Language : "+applang);
        }

        Map<String,Object> queryParams = new HashMap<>();
        String uid = user.getUid();
        queryParams.put(UserEntityKey.uid, uid);
        Map<String,Object> queryValues = new HashMap<>();
        if(!StringUtils.isBlank(uname))
            queryValues.put(UserEntityKey.name,uname);
        if(!StringUtils.isBlank(email))
            queryValues.put(UserEntityKey.email,email);
        if(!StringUtils.isBlank(gender) && (gender.equalsIgnoreCase("m") || gender.equalsIgnoreCase("f")))
            queryValues.put(UserEntityKey.gender,gender);
        if(StringUtils.isNotBlank(songQuality))
            queryValues.put(UserEntityKey.songQuality,songQuality);
        if(StringUtils.isNotBlank(downloadQuality))
            queryValues.put(UserEntityKey.downloadQuality,downloadQuality);
        if(!StringUtils.isBlank(applang))
            queryValues.put(UserEntityKey.lang,applang);

        List<String> dbLangs = ChannelContext.getUser().getSelectedLanguages();
        List<String> userLangs = Utils.convertToStringList(contentLang);

        boolean langsIdentical = Utils.equalLists(dbLangs, userLangs);

        if(contentLang != null && !langsIdentical) {
            List<String> onboardingLangs = ChannelContext.getUser().getOnboardingLanguages();
            if (onboardingLangs!= null && !onboardingLangs.isEmpty()) {
                queryValues.put(UserEntityKey.onboardingLanguages, contentLang);
                ChannelContext.getUser().setOnboardingLanguages(Utils.convertToStringList(contentLang));
            }
            else {
                queryValues.put(UserEntityKey.contentLanguages, contentLang);
                ChannelContext.getUser().setContentLanguages(Utils.convertToStringList(contentLang));
            }
            String userLangsString = null;
            if(!CollectionUtils.isEmpty(userLangs))
                userLangsString = Utils.ConvertContentLangStringListToString(userLangs);
            musicService.addMusicPreferenceChangedEvent(userLangsString);
            updateLastActivity = true;
        }

        if(notifications != null)
            queryValues.put(UserEntityKey.notifications,notifications);
        if(!StringUtils.isBlank(fbToken))
            queryValues.put(UserEntityKey.fbToken,fbToken);
        if(queryValues.size() == 0 && null == autoRenewal  && StringUtils.isBlank(deviceKey) && !langsIdentical)
            return createErrorResponse("BSY001","Not sufficient params in the request");


        boolean status = false;
        if(queryValues.size() > 0) {
        	if (UserDeviceUtils.isWAPUser(ChannelContext.getUid())) {
                status = mongoUserDBManager.setField(WAP_USER_COLLECTION,queryParams,queryValues, false);
        	} else {
                status = mongoUserDBManager.setField(USER_COLLECTION,queryParams,queryValues, false);
        	}
            if (queryValues.containsKey(UserEntityKey.contentLanguages) || queryValues.containsKey(UserEntityKey.onboardingLanguages))
            	userEventService.addUserUpdateEvent(uid, getDeviceId(), JsonKeyNames.CONTENT_LANGUAGE, contentLang.toJSONString());
            if (queryValues.containsKey(UserEntityKey.name))
            	userEventService.addUserUpdateEvent(uid, getDeviceId(), JsonKeyNames.NAME,  (String)queryValues.get(UserEntityKey.name));
            if (queryValues.containsKey(UserEntityKey.email))
            	userEventService.addUserUpdateEvent(uid, getDeviceId(), JsonKeyNames.EMAIL ,  (String)queryValues.get(UserEntityKey.email));
            if (queryValues.containsKey(UserEntityKey.gender))
            	userEventService.addUserUpdateEvent(uid, getDeviceId(), JsonKeyNames.GENDER, (String)queryValues.get(UserEntityKey.gender));
            if (queryValues.containsKey(UserEntityKey.songQuality))
            	userEventService.addUserUpdateEvent(uid, getDeviceId(), JsonKeyNames.SONQ_QUAL, (String)queryValues.get(UserEntityKey.songQuality));
        }

        if(!StringUtils.isBlank(deviceKey))
        {
            UserDevice device = MusicDeviceUtils.getUserDevice();
            if(device != null)
            {
                String currentDeviceKey = device.getDeviceKey();
                if(StringUtils.isBlank(currentDeviceKey) || !currentDeviceKey.equalsIgnoreCase(deviceKey)) {
                    device.setDeviceKey(deviceKey);
                    device.setDeviceKeyLastUpdateTime(System.currentTimeMillis());
                    if (WynkAppType.isWynkBasicApp()) {
                        device.setAppType(WynkAppType.BASIC);
                    } else {
                        device.setAppType(WynkAppType.DEFAULT);
                    }
                    updateDevice(device, false);
                    updateLastActivity = true;
                }
            }
        }

        if (contentLang != null && langsIdentical)
        	status = true;

        JSONObject response = null;

        updateUserInCache(uid, updateLastActivity);

        response = getUserProfile();
        if(response == null)
            response = new JSONObject();
        response.put("uid",uid);
        response.put("status",status);
        if (StringUtils.isNotBlank(user.getMsisdn()))
        {
          String emailInDb = "";
          try {
            emailInDb = user.getEmail();
          } catch (Exception ignored) {
          }
          if (user.getDevices() != null && user.getDevices().size() > 0) {
            UserDevice device = user.getDevices().get(0);
            LogstashLoggerUtils.userDumpLogs(user.getUid(),user.getMsisdn(),user.getCircle(),user.getOperator(),user.getName(),user.getCreationDate(),user.getLastActivityDate(),device.getOs(),device.getAppVersion(),device.getOsVersion(),device.getDeviceType(),Utils.getDobInMillis(user),emailInDb, user.getCountryId());
          }else
          {
            LogstashLoggerUtils.userDumpLogs(user.getUid(),user.getMsisdn(),user.getCircle(),user.getOperator(),user.getName(),user.getCreationDate(),user.getLastActivityDate(),null,null,null,null,Utils.getDobInMillis(user),emailInDb, user.getCountryId());
          }
       }
        return response;
    }


    /**
     * TODO : modify this to store avatar on S3 bucket
     */
    public JSONObject updateUserAvatar(JSONObject requestPayload)
            throws PortalException
    {
        User user = ChannelContext.getUser();
        if(user == null)
        {
            return createErrorResponse("BSY001","No user associated with uid : "+ChannelContext.getUid());
        }

        String base64encoded = (String) requestPayload.get("avatar");
        String uid = user.getUid();

        String avatarUrl = "";
        if(!StringUtils.isEmpty(base64encoded))
        {
            InputStream inputStream = new ByteArrayInputStream(Base64.decodeBase64(base64encoded));
            String avtrFileName = "ic" + uid.toLowerCase()+"_"+ System.currentTimeMillis();
            String filename = Base64.encodeBase64URLSafeString(avtrFileName.getBytes());
            String etag = this.s3ServiceManager.store(Constants.S3_BUCKETS.AVATARS, "avatars", filename + "." + "jpg", Utils.getContentType("jpeg"), inputStream);
            avatarUrl = "http://s3-ap-southeast-1.amazonaws.com/"+s3ServiceManager.getBucketName(Constants.S3_BUCKETS.AVATARS)+"/avatars/"+filename+".jpg";
        }

        Map<String,Object> queryParams = new HashMap<>();
        queryParams.put(UserEntityKey.uid, uid);
        Map<String,Object> queryValues = new HashMap<>();
        queryValues.put(UserEntityKey.avatar,avatarUrl);

        boolean status = mongoUserDBManager.setField(USER_COLLECTION,queryParams,queryValues,false);

        JSONObject response = new JSONObject();
        response.put("uid",uid);
        response.put("avatar", avatarUrl);

        updateUserInCache(uid, false);

        return response;
    }

    public JSONObject getUserProfile()
    {
    	return getUserProfile(true);
    }

    public JSONObject getUserProfile(boolean fetchAutoRenewalStatus)
    {
        if(StringUtils.isBlank(ChannelContext.getUid()))
        {
            return null;
        }
        User appUser = getUserFromUid(ChannelContext.getUid());
        if(appUser == null)
        {
            return createErrorResponse("BSY001","unable to determine user");
        }

        try {
            JSONObject response = appUser.toUserProfile();
            addUserSelectedLanguagesIfEmpty(response);
            addContentLangsToResponse(appUser, response);
            addParentPackageTypes(appUser, response);

            // Adding device Keys in response to fix notification issue.
            UserDevice userDevice = MusicDeviceUtils.getUserDevice();
            if(userDevice != null) {
                response.put("deviceKey", userDevice.getDeviceKey());
                response.put("deviceKeyLastUpdateTime", userDevice.getDeviceKeyLastUpdateTime());
            }

            response.put("circle",appUser.getCircle());
            logger.info("Circle : "+ appUser.getCircle()+ " of uid : "+ appUser.getUid());
            return response;
        }
        catch (IllegalArgumentException e) {

        	 LogstashLoggerUtils.createCriticalExceptionLog(e,
                     ExceptionTypeEnum.CODE.name(),
                     ChannelContext.getUid(),
                     "AccountService.getUserProfile");

        	logger.error(e.getMessage(),e);
            appUser.setAutoRenewal(null);
            JSONObject response = appUser.toUserProfile();
            addContentLangsToResponse(appUser, response);
            return response;
        }
        catch (Exception e) {

            LogstashLoggerUtils.createCriticalExceptionLog(e,
                    ExceptionTypeEnum.CODE.name(),
                    ChannelContext.getUid(),
                    "AccountService.getUserProfile");

            logger.error(e.getMessage(),e);
            return createErrorResponse("BSY002",e.getMessage());
        }
    }

    private void addParentPackageTypes(User appUser, JSONObject response) {
    	String circle = appUser.getCircle();
    	List<ModuleNameOrderType> moduleOrderList = musicContentService.getModuleOrderByCircle(circle);
    	if(CollectionUtils.isEmpty(moduleOrderList)){
    		logger.info("[addParentPackageTypes] Module order list empty returning default ordered array ");
			moduleOrderList = new ArrayList<>();
			for(MusicPackageType packageType : MusicPackageType.values())
			{
				moduleOrderList.add(new ModuleNameOrderType(packageType, packageType.getLabel()));
			}
    	}
    	JSONArray jsonArray = new JSONArray();
    	for(ModuleNameOrderType module : moduleOrderList){
    		String packageName = module.getPackageType().name();
    		jsonArray.add(packageName);
    	}

    	long cDate = ChannelContext.getUser().getCreationDate();
        long timeToShowLangCard = cDate + (7 * DAY);
        Boolean toShowLangCard = System.currentTimeMillis() < timeToShowLangCard;
    	if(toShowLangCard){
    	    if(jsonArray.contains(MusicPackageType.LANG_CARD.name())){
    	        jsonArray.remove(MusicPackageType.LANG_CARD.name());
            }
            jsonArray.add(LANG_CARD_POSITION,MusicPackageType.LANG_CARD.name());
        }
    	response.put(JsonKeyNames.PACKAGES_ORDER, jsonArray);
    	boolean appSideShuffling = musicConfig.isAppSidePackagesShuffling();
    	response.put(JsonKeyNames.APP_SIDE_PACKAGE_SHUFFLING, appSideShuffling);

	}

	private void addContentLangsToResponse(User appUser, JSONObject response) {
    	String circle = appUser.getCircle();
    	boolean canUseThisCircle = checkIfAllDetailsExistForCircle(circle);

    	if(!canUseThisCircle){
    		//If all details doesn't exist for circle then fall back to all circle
    		logger.info("Not able to fetch details from 33 for circle : {} . Hence fallback to  all circle",circle);
    		circle = Circle.ALL.getCircleName();
    		canUseThisCircle = checkIfAllDetailsExistForCircle(circle);
    	}
    	List<MusicContentLanguage> contentLangs=null;
    	List<MusicContentLanguage> defaultLanguages=null;
    	List<MusicContentLanguage>  backUpLanguages=null;
    	List<MusicContentLanguage> fullyCurateLanguages = musicContentService.getFullyCuratedLanguagesList();
    	if(canUseThisCircle){
    		 contentLangs = musicContentService.getLanguageOrderByCircle(circle);
    		 defaultLanguages = musicContentService.getDefaultLanguageByCircle(circle);
        	 backUpLanguages = musicContentService.getBackUpLanguageByCircle(circle);
    	}else{
    		//In case we didn't found values for all circle from s3 then use default values
    		logger.info("Not able to fetch details from s3 for circle :{} . Hence using default values for all circle",circle);
    		contentLangs = MusicLanguagesMappings.getContentLanguagesForCircle(Circle.ALL.getCircleId());
    		defaultLanguages = new ArrayList<MusicContentLanguage>();
    		backUpLanguages = new ArrayList<MusicContentLanguage>();
    		defaultLanguages.add(MusicContentLanguage.HINDI);
    		defaultLanguages.add(MusicContentLanguage.ENGLISH);
    		defaultLanguages.add(MusicContentLanguage.PUNJABI);
    		backUpLanguages.add(MusicContentLanguage.HINDI);

    	}


    	if(CollectionUtils.isEmpty(contentLangs)){
    		contentLangs = musicContentService.getLanguageOrderByCircle(circle);
    	}

        Boolean doesVersionSupportNewLangs = MusicBuildUtils.doesVersionSupportNewLangs();
       // MusicLanguagesMappings.getContentLanguagesForCircle(appUser.getCircle());
        JSONArray contentLangsArray = new JSONArray();
        if(contentLangs!=null){
        	for (int i = 0; i < contentLangs.size(); i++) {
                MusicContentLanguage lng = contentLangs.get(i);
                if(!doesVersionSupportNewLangs)
                {
                    if(MusicContentLanguage.isLangSupportedInOldAppVersions(lng.getId()))
                        contentLangsArray.add(lng.getId());
                }
                else
                    contentLangsArray.add(lng.getId());
            }
        }
        if(contentLangsArray.contains(MusicContentLanguage.HARYANVI.getId())) {
            int index = contentLangsArray.indexOf(MusicContentLanguage.HARYANVI.getId());
            contentLangsArray.remove(MusicContentLanguage.HARYANVI.getId());
            contentLangsArray.add(index, MusicConstants.HARYANVI_ON_APP);
        }

        if(!MusicBuildUtils.isSrilankaSupported())
            contentLangsArray.remove(MusicContentLanguage.SINHALESE.getId());


        response.put("contentLang", contentLangsArray);

        JSONArray defaultLangArray = new JSONArray();
        if(defaultLanguages!=null){
        	for (int i = 0; i < defaultLanguages.size(); i++) {
                MusicContentLanguage lng = defaultLanguages.get(i);
                defaultLangArray.add(lng.getId());
            }
        }
        if(defaultLangArray.contains(MusicContentLanguage.HARYANVI.getId())) {
            int index = defaultLangArray.indexOf(MusicContentLanguage.HARYANVI.getId());
            defaultLangArray.remove(MusicContentLanguage.HARYANVI.getId());
            defaultLangArray.add(index, MusicConstants.HARYANVI_ON_APP);
        }
        if(!MusicBuildUtils.isSrilankaSupported())
            defaultLangArray.remove(MusicContentLanguage.SINHALESE.getId());
        response.put("defaultLanguages", defaultLangArray);

        JSONArray backUpLangsArray = new JSONArray();
        if(backUpLanguages!=null){
        	for (int i = 0; i < backUpLanguages.size(); i++) {
                MusicContentLanguage lng = backUpLanguages.get(i);
                backUpLangsArray.add(lng.getId());
            }
        }
        if(backUpLangsArray.contains(MusicContentLanguage.HARYANVI.getId())) {
            int index = backUpLangsArray.indexOf(MusicContentLanguage.HARYANVI.getId());
            backUpLangsArray.remove(MusicContentLanguage.HARYANVI.getId());
            backUpLangsArray.add(index, MusicConstants.HARYANVI_ON_APP);
        }
        if(!MusicBuildUtils.isSrilankaSupported())
            backUpLangsArray.remove(MusicContentLanguage.SINHALESE.getId());
        response.put("backUpLanguage", backUpLangsArray);

        JSONArray fullyCurateLangsArray = new JSONArray();
    	for (int i = 0; i < fullyCurateLanguages.size(); i++) {
            MusicContentLanguage lng = fullyCurateLanguages.get(i);
            fullyCurateLangsArray.add(lng.getId());

        }
        if(fullyCurateLangsArray.contains(MusicContentLanguage.HARYANVI.getId())) {
            int index = fullyCurateLangsArray.indexOf(MusicContentLanguage.HARYANVI.getId());
            fullyCurateLangsArray.remove(MusicContentLanguage.HARYANVI.getId());
            fullyCurateLangsArray.add(index, MusicConstants.HARYANVI_ON_APP);
        }
        if(!MusicBuildUtils.isSrilankaSupported())
            fullyCurateLangsArray.remove(MusicContentLanguage.SINHALESE.getId());
        response.put("fullyCuratedLanguages", fullyCurateLangsArray);
    }

    //This funtion will check if for circle all details exist or not
    private boolean checkIfAllDetailsExistForCircle(String circle) {
    	List<MusicContentLanguage> contentLangs = musicContentService.getLanguageOrderByCircle(circle);
    	List<MusicContentLanguage> defaultLanguages = musicContentService.getDefaultLanguageByCircle(circle);
    	List<MusicContentLanguage>  backUpLanguages = musicContentService.getBackUpLanguageByCircle(circle);
    	if(!CollectionUtils.isEmpty(contentLangs)&&!CollectionUtils.isEmpty(defaultLanguages)&&!CollectionUtils.isEmpty(backUpLanguages))
    		return true;
    	return false;
	}

    public static JSONObject createErrorResponse(String errorcode,String errorMessage)
    {
        JSONObject response = new JSONObject();
        response.put("errorCode",errorcode);
        response.put("error",errorMessage);
        return response;
    }

    public static JSONObject createErrorResponse(String errorcode, String errorTitle, String errorMessage)
    {
        JSONObject response = new JSONObject();
        response.put("errorCode",errorcode);
        response.put("errorTitle",errorTitle);
        response.put("error",errorMessage);
        return response;
    }

    public JSONObject updateDevice(UserDevice device,boolean makeActive)
    {

        User appUser = ChannelContext.getUser();
        String uid = ChannelContext.getUid();

        if(appUser == null)
        {
            return createErrorResponse("BSY001","No user associated with uid : "+uid);
        }

        try {

            List<UserDevice> userDevices = new ArrayList<>();

            List<UserDevice> existingDevices = appUser.getDevices();
            boolean alreadyExists = false;
            for(int i=0;i<existingDevices.size();i++)
            {
                UserDevice userDevice = existingDevices.get(i);
                if(StringUtils.isEmpty(userDevice.getDeviceId()))
                    continue;
                if(userDevice.getDeviceId().equalsIgnoreCase(device.getDeviceId()))
                {
                    alreadyExists = true;
                    userDevice.fromJson(device.toJsonObject().toJSONString(),false);
                    if(makeActive)
                        userDevice.setActive(true);
                }
                else
                {
                    if(makeActive)
                        userDevice.setActive(false);
                }
                userDevices.add(userDevice);
            }

            if(!alreadyExists)
            {
                userDevices.add(device);
                // Commenting this
                // generateTokenForMsisdn(appUser.getUid());
                device.setUid(uid);
            }

            boolean status = updateDevicesForUid(uid, userDevices);

            JSONObject response = new JSONObject();
            response.put("uid",uid);
            response.put("status",status);

            return response;
        }
        catch (Exception e) {

        	 LogstashLoggerUtils.createCriticalExceptionLog(e,
                     ExceptionTypeEnum.CODE.name(),
                     ChannelContext.getUid(),
                     "AccountService.updateDevice");


        	e.printStackTrace();
            return createErrorResponse("BSY002",e.getMessage());
        }

    }

    private boolean validateDOB(JSONObject dob)
    {
        if(dob.get("month") == null || dob.get("day") == null)
            return false;

        int month = ((Number) dob.get("month")).intValue();
        int day = ((Number) dob.get("day")).intValue();

        if(month>12 || month<1 || day>31 || day <1)
            return false;

        return true;

    }


    public static final String TOKEN_LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final int INIT_TOKEN_SIZE = 8;


    //duid -> device uid
    private String generateTokenForMsisdn(String uid)
    {
        String ukey = uid;

        String token = null;
        token = getTokenForMsisdn(ukey, false);
        if(StringUtils.isNotBlank(token)) {
            return token;
        }
        int size = INIT_TOKEN_SIZE;
        if(StringUtils.isBlank(token)) {
            boolean isUidExistsForToken = false;
            do
            {
                token = RandomStringUtils.random(size, TOKEN_LETTERS);
                isUidExistsForToken = isTokenInUse(token);
                size++;
            } while (isUidExistsForToken);
            updateTokenInUser(uid, token);
        }
        setMTokenCache(ukey, token);
        return token;
    }

    public void setMTokenCache(String uid, String token) {
        try {
        	musicUserShardRedisServiceManager.setex(getRedisMTokenKey(uid), token,MusicConstants.REDIS_USER_TOKEN_EXPIRY_DURATION);
        }
        catch (Exception e) {
            LogstashLoggerUtils.createFatalExceptionLogWithMessage(e,
                    ExceptionTypeEnum.INFRA.REDIS.name(),
                    ChannelContext.getUid(),
                    "AccountService.setMTokenCache",
                    "Error setting token in Redis for UID" + uid);

            logger.error("Error setting token in Redis for UID : "+uid+" ; Error : "+e.getMessage(),e);
        }
    }

    public String getMTokenFromCache(String uid) {
        try {
            return musicUserShardRedisServiceManager.get(getRedisMTokenKey(uid));
        }
        catch (Exception e) {
            LogstashLoggerUtils.createFatalExceptionLogWithMessage(e,
                    ExceptionTypeEnum.INFRA.REDIS.name(),
                    ChannelContext.getUid(),
                    "AccountService.getMTokenFromCache",
                    "Error fetching token from Redis for UID" + uid);

            logger.error("Error fetching token from Redis for UID : "+uid+" ; Error : "+e.getMessage(),e);
            return null;
        }
    }

    public void updateTokenInUser(String uid, String token) {
        Map<String,Object> queryParams = new HashMap<>();
        //queryParams.put(MongoDBManager.MONGO_ID,MongoDBManager.getObjectId(appUser.getUid()));
        queryParams.put(UserEntityKey.uid,uid);
        Map<String,Object> queryValues = new HashMap<>();
        queryValues.put(UserEntityKey.token, token);
        mongoUserDBManager.setField(USER_COLLECTION, queryParams,queryValues, false);
    }

    public void updateMsisdnInUser(String uid, String msisdn) {
        Map<String,Object> queryParams = new HashMap<>();
        //queryParams.put(MongoDBManager.MONGO_ID,MongoDBManager.getObjectId(appUser.getUid()));
        queryParams.put(UserEntityKey.uid,uid);
        Map<String,Object> queryValues = new HashMap<>();
        queryValues.put(UserEntityKey.msisdn, msisdn);
        mongoUserDBManager.setField(USER_COLLECTION, queryParams,queryValues, false);
    }


    public String getTokenFromLru(String uid) {
    	try {
			return tokenLruCache.get(uid);
		} catch (Exception e) {
			logger.error("Error while reading tom tokenLRU",e);
			return getTokenForMsisdn(uid, false);
		}
    }

    public String getTokenForMsisdn(String uid, Boolean store)
    {
        String token =  getMTokenFromCache(uid);
        if(StringUtils.isBlank(token)) {
            User user = getUserFromUid(uid);
            if(user != null) {
                token = user.getToken();
                if(StringUtils.isNotBlank(token) && store)
                    setMTokenCache(uid, token);
                else
                    logger.info("Null token for user uid : "+uid);
            }
        }
        return token;
    }

    public boolean isTokenInUse(String token) {
        logger.info("inside isTokenInUse");
        if(StringUtils.isBlank(token)) {
            return true;
        }
        Map queryParams = new HashMap<>();
        queryParams.put(UserEntityKey.token, token);
        Map keys = new HashMap<>();
        //TODO This was EVIL ...
        //long count = mongoUserDBManager.getCount(USER_COLLECTION, queryParams);
//        if(count > 0) {
//            return true;
//        }
        logger.info("exiting isTokenInUse");
        return false;
    }

    public String getMSISDN()
    {
        String msisdnStr = ChannelContext.getMsisdn();
        User user = ChannelContext.getUser();
        if(user != null){
            msisdnStr = user.getMsisdn();
        }

        return msisdnStr;
    }

    public String getXRAT(HttpRequest request)
    {
        int xrat = 0; //0=Unknown,1=Airtel3G,2=Airtel2G,3=MobileData,4=WiFi
        String x_bsy_net = null;
        Map<String,String> networkHeaderMap = UserDeviceUtils.parseNetworkHeader(request.headers().get(
                MusicConstants.MUSIC_HEADER_NET));

        if(networkHeaderMap != null && networkHeaderMap.size() > 0)
            x_bsy_net = networkHeaderMap.get(MusicConstants.NETWORK_TYPE);

        if ((x_bsy_net != null) && (StringUtils.isNotEmpty(x_bsy_net)) && (Integer.parseInt(x_bsy_net) == MobileNetwork.CONNECTION_TYPE_WIFI)){
            xrat = 4;  // 4=WiFi
        }
        else
        {
        	AccessMsnInf userOperatorInfo = ChannelContext.getUserOperatorInfo();
            try {
                xrat = userOperatorInfo.getRatType();  //1=Airtel3G,2=Airtel2G
            } catch (Exception e) {
                logger.info("Error finding x-rat :"+e.getMessage(),e);

                LogstashLoggerUtils.createCriticalExceptionLog(e,
                        ExceptionTypeEnum.CODE.name(),
                        ChannelContext.getUid(),
                        "AccountService.getXRAT");
            }

            if ((xrat == 0) && (x_bsy_net != null) && (StringUtils.isNotEmpty(x_bsy_net)) &&
                    (Integer.parseInt(x_bsy_net) ==  MobileNetwork.CONNECTION_TYPE_MOBILE))
            {
               boolean is2G = UserDeviceUtils.is2G(request,userOperatorInfo);
               xrat = is2G ? 2 : 1;
            }
        }

        return String.valueOf(xrat);
    }

    public String getCircleShortName()
    {
        String circleShortName = null;
        circleShortName = ChannelContext.getCircle();
        if (StringUtils.isBlank(circleShortName) && null != ChannelContext.getCircleCatalog() && null != ChannelContext.getMsisdn())
        {
        	CircleCatalog catalog = ChannelContext.getCircleCatalog();

            try {
            	CircleInf circleInfObj = catalog.getCircleInfObj(ChannelContext.getMsisdn());
                if(circleInfObj != null)
                    circleShortName = circleInfObj.getCircleShortName();
            } catch (Exception e) {
                logger.info("Error finding circle name :"+e.getMessage());

                LogstashLoggerUtils.createFatalExceptionLogWithMessage(e,
                        ExceptionTypeEnum.THIRD_PARTY.DIWALI_LIBRARY.name(),
                        ChannelContext.getUid(),
                        "AccountService.getCircleShortName",
                        "Error finding circle name");
            }
        }

        return  circleShortName;
    }

    public String getNetworkOperator()
    {
        String netOp = "-";
        if (ChannelContext.getUser() == null)
            return netOp;

        netOp = ChannelContext.getUser().getOperator();
        //double checking the net op value. In few cases it coming empty.
        if(StringUtils.isBlank(netOp))
        {
            String ip = ChannelContext.getRequest().headers().get("x-bsy-ip");
            if(IPRangeUtil.isAirtelIPRange(ip,ChannelContext.getRequest())) {
                netOp = AIRTEL;
                return netOp;
            }
            User user = ChannelContext.getUser();
            if(user.getDevices() != null && user.getDevices().size() > 0) {
                UserDevice device = MusicDeviceUtils.getUserDevice();
                if(device != null)
                    netOp =  device.getOperator();
            }

            if(StringUtils.isBlank(netOp))
            {
                if (null != ChannelContext.getCircleCatalog() && null != ChannelContext.getMsisdn())
                {
                	CircleCatalog catalog = ChannelContext.getCircleCatalog();
                    try {
                        netOp = catalog.getCircleInfObj(ChannelContext.getMsisdn()).getOperatorName();
                    } catch (Exception e) {
                        logger.info("Error finding network operator :"+e.getMessage());

                    }
                }
            }
        }

        if(StringUtils.isBlank(netOp))
            netOp = "-";
        return  netOp;
    }

    public String getOS()
    {
        return ChannelContext.getOS();
    }

    public String getDeviceId()
    {
        return ChannelContext.getDeviceId();
    }

    public String getOSVersion()
    {
        String osversion = null;
        if(ChannelContext.getUser() != null)
        {
            User user = ChannelContext.getUser();
            if(user.getDevices() != null && user.getDevices().size() > 0) {
                UserDevice device = MusicDeviceUtils.getUserDevice();
                if(device != null)
                    osversion =  device.getOsVersion();
            }
        }
        return osversion;
    }

    public String getAppVersion()
    {
        return ChannelContext.getAppVersion();
    }

    public boolean isUserOperatorAirtel() {
        User user = ChannelContext.getUser();
        if(user == null) {
            return false;
        }
        String operator = user.getOperator();
        if(StringUtils.isNotEmpty(operator)) {
            operator = StringUtils.lowerCase(operator);
            if(operator.contains(AIRTEL)) {
                return true;
            }
        }
        else {
            return false;

            // DO NOT CHECK FOR OPERATOR INSIDE DEVICES
            /*
            UserDevice device = MusicUtils.getCurrentDevice(user,null);
            if(device != null) {
                operator = StringUtils.lowerCase(device.getOperator());
                if (operator != null && operator.contains(AIRTEL)) {
                    return true;
                }
            }

            List<UserDevice> devices = user.getDevices();
            if(CollectionUtils.isEmpty(devices)) {
                return false;
            }
            for(UserDevice userDevice: devices) {
                if(userDevice == null || StringUtils.isEmpty(userDevice.getOperator())) {
                    continue;
                }
                operator = StringUtils.lowerCase(userDevice.getOperator());
                if(operator != null && operator.contains(AIRTEL)) {
                    return true;
                }
            }
            */
        }
        return false;
    }

    public boolean fupResetRequired(User user, long startTime) {
      if(!musicConfig.isFupEnabled()){
        return false;
      }
      if(user == null || user.getFupPack()== null)
        return false;

      if((user.getFupPack().getLastFUPResetDate() >= startTime))
        return false;

      if(user.getFupPack().getStreamedCount() + user.getFupPack().getRentalsCount() == 0)
        return false;

      String msisdn = user.getMsisdn();
      if(StringUtils.isBlank(msisdn) && user.getFupPack().getLastFUPResetDate() <= startTime) {
        return true;
      }

      Feature streamingFeature = wcfApisUtils.getFeature(FeatureType.STREAMING,user.getUserSubscription());

      if(streamingFeature.getValidTill() >= System.currentTimeMillis() && streamingFeature.getStreamCount() > 0){
        return true;
      }
      return false;
    }

    public void updateUserContext(Boolean updateLastActivity)
    {
        String userId = ChannelContext.getUid();
        if(userId == null)
            return;

        try {
            User user = updateUserInCache(ChannelContext.getUid(), updateLastActivity);
            ChannelContext.setUser(user);
        }
        catch (Exception e) {
            e.printStackTrace();
            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e,
                    ExceptionTypeEnum.CODE.name(),
                    ChannelContext.getUid(),
                    "AccountService.updateUserContext",
                    "Error updating user in context");
            logger.error("Error updating User Context for uid : "+ChannelContext.getUid()+". Error :"+e.getMessage(),e);
        }
    }

  public void resetFUPLimitsForUser(String uid) {
    if(StringUtils.isEmpty(uid)) {
      return;
    }
    Map queryParams = new HashMap<>();
    queryParams.put(UserEntityKey.uid, uid);
    Map<String, Object> fieldValueMap = new HashMap<>();
    fieldValueMap.put("packs.FUPPack."+ UserEntityKey.FupPack.rentalsCount, 0);
    fieldValueMap.put("packs.FUPPack."+ UserEntityKey.FupPack.streamedCount, 0);
    fieldValueMap.put("packs.FUPPack."+ UserEntityKey.FupPack.shownFUPWarning, false);
    fieldValueMap.put("packs.FUPPack."+ UserEntityKey.FupPack.shownFUP95Warning, false);
    fieldValueMap.put("packs.FUPPack."+ UserEntityKey.FupPack.lastFUPResetDate, System.currentTimeMillis());
    if(UserDeviceUtils.isWAPUser(uid)) {
      mongoUserDBManager.setField(WAP_USER_COLLECTION, queryParams, fieldValueMap, false, true);
    }else{
      mongoUserDBManager.setField(USER_COLLECTION, queryParams, fieldValueMap, false, true);
    }
    logger.info("Updated db for user's uid :" + uid);
    removeUserFromCache(uid);
    mFUPResetLogger.info(uid);
  }

  public void updateStreamCount(String uid) {
    if(!musicConfig.isFupEnabled()){
        return;
    }
    User user = ChannelContext.getUser();
    Map queryParams = new HashMap<>();
    queryParams.put(UserEntityKey.uid, uid);
    if (user == null && StringUtils.isEmpty(uid)) {
      return;
    }
    int fupLimit = 200;
    // uid not ending with 0, hence not registered
    boolean isRegisteredUser = uid.endsWith("0") ? Boolean.TRUE : Boolean.FALSE;

    Feature streamingFeature = wcfApisUtils.getFeature(FeatureType.STREAMING,user.getUserSubscription());

    if(isRegisteredUser){
      fupLimit = musicConfig.getStreamingFUPLimit();
    }else if(streamingFeature.getStreamCount() > 0){
      fupLimit = streamingFeature.getStreamCount().intValue();
    }else{
      // not updating stream count
      return;
    }

    if (user.getFupPack() != null
        && user.getFupPack().getStreamedCount() < fupLimit) {
      int recentSongCount =
          musicService
              .getLastNDaysRecentSongCount(uid, fupLimit, user.getFupPack().getLastFUPResetDate())
              .size();
      if ( recentSongCount
          <= user.getFupPack().getStreamedCount()) {
        return;
      }
      Map<String, Object> fieldValueMap = new HashMap<>();
      fieldValueMap.put("packs.FUPPack." + UserEntityKey.FupPack.rentalsCount, user.getFupPack().getRentalsCount());
      fieldValueMap.put("packs.FUPPack." + UserEntityKey.FupPack.streamedCount, recentSongCount);
      fieldValueMap.put("packs.FUPPack." + UserEntityKey.FupPack.shownFUPWarning, false);
      fieldValueMap.put("packs.FUPPack." + UserEntityKey.FupPack.shownFUP95Warning, false);
      fieldValueMap.put(
          "packs.FUPPack." + UserEntityKey.FupPack.lastFUPResetDate,
          user.getFupPack().getLastFUPResetDate());
      if (UserDeviceUtils.isWAPUser(uid)) {
        mongoUserDBManager.setField(WAP_USER_COLLECTION, queryParams, fieldValueMap, false, true);
      } else {
        mongoUserDBManager.setField(USER_COLLECTION, queryParams, fieldValueMap, false, true);
      }
      logger.info("Updated FUPPack for user's uid :" + uid + " with recentSongCount :"+recentSongCount);
    }
  }

    public String getTenDigitMsisdnFromRequest(HttpRequest request) {
        String msisdnStr = getMsisdnByUID();

        if(StringUtils.isEmpty(msisdnStr)) {
            msisdnStr = UserDeviceUtils.getMsisdn(request);
        }

        msisdnStr = Utils.getTenDigitMsisdn(msisdnStr);
        return msisdnStr;
    }

    public String getTenDigitMsisdnFromUser() {
        String msisdnStr = getMsisdnByUID();
        msisdnStr = Utils.getTenDigitMsisdn(msisdnStr);
        return msisdnStr;
    }

    public JSONObject getMsisdnFromUid(String uid) {
        User user = StringUtils.isNotEmpty(uid) ? getUserFromUid(uid,false) : null ;
        JSONObject response = new JSONObject();
        if (Objects.nonNull(user)) {
            response.put("msisdn", Utils.getTenDigitMsisdn(user.getMsisdn()));
            response.put("success", StringUtils.isNotBlank(user.getMsisdn()) ? true : false);
        }else{
            response.put("msisdn", "");
            response.put("success", false);
        }
        return response;
    }

    public User getUserFromContext(String uid) {
        if (StringUtils.isEmpty(uid)) {
            return null;
        }
        return ChannelContext.getUser() != null
                ? ChannelContext.getUser()
                : getUserFromUid(uid, false);
    }

    public List<String> getOnboardingLanguagesFromUid(String uid) {
        User user = StringUtils.isNotEmpty(uid) ? getUserFromUid(uid, false) : null;
        String circle = DEFAULT_ONBOARDING_CIRCLE;
        if (Objects.nonNull(user)) {
            circle = user.getCircle() != null ? user.getCircle() : DEFAULT_ONBOARDING_CIRCLE;
            if (user.getOnboardingLanguages() != null && !user.getOnboardingLanguages().isEmpty()) {
                return user.getOnboardingLanguages();
            } else if (user.getContentLanguages() != null && !user.getContentLanguages().isEmpty()) {
                return user.getContentLanguages();
            }
        }
        List<String> languagesOrderedByCircle = getLanguagesOrderByCircle(circle);
        return languagesOrderedByCircle.subList(0,
                Math.min(languagesOrderedByCircle.size(), MIN_ONBOARDING_DEFAULT_LANGS));
    }

    public List<String> getLanguagesOrderByCircle(String circle) {
        List<String> languagesOrderedByCircle = musicContentService.getLanguageOrderByCircle(circle)
                .stream().map(MusicContentLanguage::getId).collect(Collectors.toList());
        int indexOfHaryanvi = languagesOrderedByCircle.indexOf(MusicContentLanguage.HARYANVI.getId());
        if (indexOfHaryanvi != -1) {
            languagesOrderedByCircle.remove(MusicContentLanguage.HARYANVI.getId());
            languagesOrderedByCircle.add(indexOfHaryanvi, MusicConstants.HARYANVI_ON_APP);
        }
        return languagesOrderedByCircle;
    }

    public JSONObject getCircleFromMsisdn(String msisdn) {
        String uid = null;
        try {
            uid = UserDeviceUtils.generateUUID(msisdn, null, null, MusicPlatformType.WYNK_APP);
        } catch (Exception e) {
            logger.error("Error while generating uid for msisdn : " + msisdn);
        }
        User user = StringUtils.isNotEmpty(uid) ? getUserFromUid(uid,false) : null;
        JSONObject response = new JSONObject();
        if (Objects.nonNull(user)) {
            response.put("circle", CircleSolaceMapping.getCircleMappingByName(user.getCircle()).getCircleName());
            response.put("success", true);
        } else{
            response.put("circle", "");
            response.put("success", false);
        }
        return response;
    }

    public JSONObject isPaidUser(String uid) {
        JSONObject response = new JSONObject();
        boolean isPaidUser = false;
        User user = getUserFromUid(uid);
        if (Objects.nonNull(user) && ObjectUtils.notEmpty(user.getUserSubscription())) {
            isPaidUser = WCFUtils.isPaidUser(user)
                    && wcfApisUtils.getFeature(FeatureType.DOWNLOADS, user.getUserSubscription()).isSubscribed();
            if(StringUtils.isNotBlank(user.getMsisdn())){
                response.put("msisdn", user.getMsisdn());
            }
        }
        response.put("is_paid_music_user", isPaidUser);
        return response;
    }


    public MusicSubscriptionStatus getMusicSubscriptionStatus(HttpRequest request, boolean subStatusCall, String requestUri, Boolean isConfigOrAccount, Boolean fromAccountCall) {

        /*
        boolean isAirtelMobileIP = IPRangeUtil.isAirtelMobileIP(request);
        if (isAirtelMobileIP) {
            String msisdnFromRequest = UserDeviceUtils.getMsisdn(request);
            isAirtelMobileIP = !StringUtils.isEmpty(msisdnFromRequest);
            if (StringUtils.isNotBlank(msisdnStr) && msisdnStr.equals(Utils.getTenDigitMsisdn(msisdnFromRequest)))
                isAirtelUser = true;
        }
         */
        String msisdn = getTenDigitMsisdnFromUser();
        String uid = ChannelContext.getUid();
        MusicSubscriptionStatus musicSubscriptionStatus = null;
        User user = ChannelContext.getUser();
        WCFServiceType wcfServiceType = wcfUtils.getWCFServiceType(UserDeviceUtils.getPlatform());
        try {
            musicSubscriptionStatus =
                    wcfApisService.getMusicSubscriptionStatusFromCache(wcfServiceType.getServiceName(),user,msisdn,null,subStatusCall, requestUri, isConfigOrAccount, fromAccountCall);
        } catch (Exception e) {
            logger.error("Exception occurred while fetching music subscription status" + e);
            return null;
        }

        if (!wcfUtils.isUserSubsActive(musicSubscriptionStatus)
                || musicSubscriptionStatus.getProductId() == MusicConstants.BLACK_LISTED_PRODUCT) {
            String purchaseUrl = musicConfig.getWcfBaseApiUrl()
                    + "displaySubscription?u=" + MusicUtils.encryptAndEncodeParam(musicConfig.getEncryptionKey(), uid);
            musicSubscriptionStatus.setPurchaseUrl(purchaseUrl);
        }

        //TODO remove this log
        logger.info("getMusicSubscriptionStatus" + musicSubscriptionStatus.toJsonObject().toJSONString());
        return musicSubscriptionStatus;
    }

  public void updateSubscriptionForUserId(String userId, Map<String,Object> queryValues,Boolean isUserSubscriptionEvent, UserSubscription allocatedSubscriptionPlan) {
    Map<String, Object> queryParams = new HashMap<>();
    queryParams.put(UserEntityKey.uid, userId);
    boolean updatedinDB = false;
    updatedinDB = mongoUserDBManager.setField(USER_COLLECTION, queryParams, queryValues,false);

    if(updatedinDB){
      updateUserInCache(userId,false);
      if(isUserSubscriptionEvent){
        userEventService.addSubscriptionTypeEvent(userId,allocatedSubscriptionPlan,Boolean.TRUE);
        // Todo - update moengage
        ThirdPartyNotifyDTO thirdPartyNotifyDTO = notificationUtils.getUpdateTPNotifyObjectFromUser(userId,null,allocatedSubscriptionPlan);
        userEventService.addThirdPartyNotifyEvent(thirdPartyNotifyDTO);
      }
    }
    else
      logger.error("Error updating subscription object in DB for uid = " + userId);
  }

    public JSONObject getSubscriptionOfferJson(Boolean isActiveUser,Boolean configOnly,Boolean isChangeMobileRequest) {
        JSONObject jsonObj = new JSONObject();

        String uid = ChannelContext.getUid();
        User user = getUserFromUid(uid);

        if(user == null)
            return jsonObj;

        if(StringUtils.isBlank(user.getMsisdn())) {
        	if (MusicBuildUtils.isRegistrationPopupSupported()) {
                JSONObject notification;
                if (OperatorUtils.isAirtelDevice()) {
                    notification = MusicNotificationConstants.REGISTER_NOTIFICATION_AIRTEL;
                    if(MusicBuildUtils.isHtSupported()) {
                        notification = MusicNotificationConstants.REGISTER_NOTIFICATION_AIRTEL_HT;
                    }
                }
	        	else {
                    notification = MusicNotificationConstants.REGISTER_NOTIFICATION;
                    if(MusicBuildUtils.isHtSupported()){
                        notification = MusicNotificationConstants.REGISTER_NOTIFICATION_HT;
                    }
                }
	        	if (MusicDeviceUtils.isAndroidDevice()){
                    notification.put("id", getRegPopupCode());
                }
	        	return  notification;
        	}
        }

        Boolean isInternalWebViewSupported = MusicBuildUtils.isInternalWebViewNotificationSupported();

        if(!isInternalWebViewSupported)
            return jsonObj;

        if (user.getOperator() == null) {
        	String operator = null;
        	if (MusicUtils.isOnAirtelMobile(ChannelContext.getRequest(), user.getMsisdn()))
        		operator = AIRTEL;

        	if (StringUtils.isBlank(operator)) {
                CircleInf circleInfo = UserDeviceUtils.getCircleInfo(user.getMsisdn());
                if (circleInfo != null) {
                	try {
						operator = circleInfo.getOperatorName();
					} catch (Exception e) {
						logger.error("Could not recieve operator from circle info",e);
					}
                }
                // Do not trust airtel nos from Number series
				if (StringUtils.isNotBlank(operator) && operator.toLowerCase().contains("airtel"))
					operator = null;
        	}

        	if (StringUtils.isNotBlank(operator))
        		updateOperator(user,operator);
        }

        Integer offerProvisionCount = 0;
        String headerText = "You have unlocked the following benefit:";
        String text = "";
/*        if(!ObjectUtils.isEmpty(subscriptionMap)){
            Set<Integer> updatedProdIds = wcfApisUtils.getUpdatedAndNewProdIds(userDBprodIds == null ? null: userDBprodIds.getProdIds(),subscriptionMap.getProdIds());
            for(Integer pdId : updatedProdIds){
                Product prod = wcfApisService.getProduct(pdId);
                if (prod != null) {
                    text += "\n  \u2022 " + prod.getTitle();
                    if (StringUtils.isNotEmpty(text)) {
                        offerProvisionCount++;
                    }
                }
            }
        }*/

        boolean isWebView=false;
        String webViewTarget="";
        String notificationText = "";
        String title = "Congratulations!";
        String notificationId = "";
        String blackListedNotifPrefix = "bundle_trial_";
        ScreenCode screenCode = ScreenCode.NONE;
        if(!configOnly && isActiveUser && offerProvisionCount == 0){
            if(isChangeMobileRequest){
                notificationText = "Your number has been changed successfully.";
            }
            else {
                notificationText = "You have been registered successfully.";
            }
        }
        else if(offerProvisionCount > 0){
            notificationText = headerText.concat(text);
            updateCurrentOfferIdsInDb(user);
        }

        if(StringUtils.isNotBlank(notificationText)) {
            MusicAdminNotification musicAdminNotification = new MusicAdminNotification();
            musicAdminNotification.setNotificationId(String.valueOf(Utils.getRandomNumber(10000)));
            musicAdminNotification.setActionOpen(ActionOpen.ALERT);
            musicAdminNotification.setTargetScreen(screenCode);
            musicAdminNotification.setText(notificationText);
            musicAdminNotification.setTitle(title);
            musicAdminNotification.setProcessed(false);
            musicAdminNotification.setDeleted(false);
            musicAdminNotification.setShowImage(isActiveUser);
            musicAdminNotification.setShowPacks(!isActiveUser);
            if(isWebView) {
                musicAdminNotification.setBrowserUrl(webViewTarget);
            }
            if(StringUtils.isNotBlank(notificationId) && notificationId.startsWith(blackListedNotifPrefix))
            {
                musicAdminNotification.setNotificationId(notificationId);
                musicAdminNotification.setShowImage(!isActiveUser);
                musicAdminNotification.setShowPacks(isActiveUser);
            }
            JSONObject NEW_SUBSCRIPTION_NOTIFICATION = musicAdminNotification.getRichAndroidMessageJsonObject();
            NEW_SUBSCRIPTION_NOTIFICATION.put("aok", "Okay!");
            return NEW_SUBSCRIPTION_NOTIFICATION;
        }
        return jsonObj;
    }


    private String setRegistrationPopupCode(){
        String key = "regPopup";
        try {
            regPopupCode  = notificationCodeRedisServiceManager.get(key);
        }
        catch (Exception ex){
            logger.error("error in fetching registration popup code" );
        }
        if(StringUtils.isEmpty(regPopupCode)){
            String date = new SimpleDateFormat("ddMMyyyy").format(new Date());
            regPopupCode = date;
            notificationCodeRedisServiceManager.setex(key, date, regPopUpTtl);
        }
        return regPopupCode;
    }

    private void updateCurrentOfferIdsInDb(User user) {
        Map<String,Object> queryParams = new HashMap<>();
        queryParams.put(UserEntityKey.uid,user.getUid());
        try {
            updateUser(queryParams,user);
        } catch (Exception e) {
            logger.error(" Error occurred while update current offer ids for user +" + user.getUid(), e);
        }
    }

    public JSONObject getTamilUserNotification() {
		User user = ChannelContext.getUser();
		Boolean isTamilUser = false;
		if ((user != null) &&	(user.getCircle() != null) &&
				(user.getCircle().equalsIgnoreCase("TN") || user.getCircle().equalsIgnoreCase("CH")) &&
				user.getSelectedLanguages() != null &&
				user.getSelectedLanguages().contains("ta") &&
				user.isSystemGeneratedContentLang()) {
			isTamilUser = true;
		}

    	if (isTamilUser) {
    		return MusicNotificationConstants.TAMIL_NOTIFICATION;
        }
    	return null;
    }

    public JSONObject getTeluguUserNotification() {
		User user = ChannelContext.getUser();
		Boolean isTeluguUser = false;
		if ((user != null) &&	(user.getCircle() != null) &&
				(user.getCircle().equalsIgnoreCase("AP")) &&
				user.getSelectedLanguages() != null &&
				user.getSelectedLanguages().contains("te") &&
				user.isSystemGeneratedContentLang()) {
			isTeluguUser = true;
		}

    	if (isTeluguUser) {
    		return MusicNotificationConstants.TELUGU_NOTIFICATION;
        }
    	return null;
    }

    public void createWAPUser(User user) {
        String addObject;
        try {
            addObject = createWAPUserInDb(user);

            if (StringUtils.isNotBlank(addObject))
                createUpdateUserInCache(user);

        }
        catch (Exception e) {
            logger.info("error creating wap user in db " + user.getUid());
        }

    }

    public void createUserWithoutSendingEvents(User user) throws Exception {
        logger.info("entered createUserWithoutSendingEvents with {}", user.toJson());
        String justPreviousUid = wcfService.getPreviousUidBasedOnDeletedVersion(user.getMsisdn(),ChannelContext.getUserDeletedVersionContext());
        String addObject = createUserInDb(user);
        if(StringUtils.isNotBlank(addObject))
            createUpdateUserInCache(user);
        htApiService.migrateHtData(justPreviousUid, user.getUid());
    }

    public void createUser(User user) throws Exception {
        String justPreviousUid = wcfService.getPreviousUidBasedOnDeletedVersion(user.getMsisdn(),ChannelContext.getUserDeletedVersionContext());
        String addObject = createUserInDb(user);
    	Boolean isIOSDevice = MusicDeviceUtils.isIOSDevice();
    	if(StringUtils.isNotBlank(addObject))
    	    createUpdateUserInCache(user);
        ThirdPartyNotifyDTO thirdPartyNotifyDTO = notificationUtils.getUpdateTPNotifyObjectFromUser(user.getUid(),user,user.getUserSubscription());
        userEventService.addThirdPartyNotifyEvent(thirdPartyNotifyDTO);
        htApiService.migrateHtData(justPreviousUid, user.getUid());
    }

    public void updateUser(Map queryParam ,User user) throws Exception {
    	updateUserInDb(user, queryParam);
    	createUpdateUserInCache(user);
    }

    private void updateUserInDb(User user, Map queryParam) throws Exception {
        try {
            mongoUserDBManager.updateObject(USER_COLLECTION, queryParam, user.toJson());
        } catch (Exception e) {
            LogstashLoggerUtils.createFatalExceptionLogWithMessage(e,
                    ExceptionTypeEnum.INFRA.MONGO.name(),
                    ChannelContext.getUid(),
                    "AccountService.updateUserInDb",
                    "Error updating user in db");

        }
	}

    public String createWAPUserInDb(User user) throws Exception {
        String addObject = null;
        try {
            addObject = mongoUserDBManager.addObjectAndGetId(WAP_USER_COLLECTION, user.toJson());

        } catch (Exception e) {
            LogstashLoggerUtils.createFatalExceptionLogWithMessage(e,
                    ExceptionTypeEnum.INFRA.MONGO.name(),
                    ChannelContext.getUid(),
                    "AccountService.createUserInDb",
                    "Error creating user in db");
        }
        return addObject;
    }


	public String createUserInDb(User user) throws Exception {
        logger.info("Creating user in db");
        String addObject = null;
	    try {
	        addObject = mongoUserDBManager.addObjectAndGetId(USER_COLLECTION, user.toJson());
	    } catch (Exception e) {
            LogstashLoggerUtils.createFatalExceptionLogWithMessage(e,
                    ExceptionTypeEnum.INFRA.MONGO.name(),
                    ChannelContext.getUid(),
                    "AccountService.createUserInDb",
                    "Error creating user in db");
	    }
        logger.info("Created user in db");
	    return addObject;
	}

    public void createUpdateUserInCache(User user){
        try {
            if(musicUserShardRedisServiceManager != null) {
            	musicUserShardRedisServiceManager.setex(getRedisUidKey(user.getUid()), user.toJson(), MusicConstants.REDIS_USER_EXPIRY_DURATION);
            }
        }
        catch (Exception e) {

            LogstashLoggerUtils.createFatalExceptionLogWithMessage(e,
                    ExceptionTypeEnum.INFRA.REDIS.name(),
                    user.getUid(),
                    "AccountService.createUpdateUserInCache",
                    "Error updating user in Redis for UID:" + user.getUid());

            logger.error("Error updating user in Redis for UID : "+user.getUid()+" ; Error : "+e.getMessage(),e);
        }
    }

    public void removeUserFromCache(String uid){
        try {
            if(musicUserShardRedisServiceManager != null) {
            	musicUserShardRedisServiceManager.delete(getRedisUidKey(uid));
            }
        }
        catch (Exception e) {

            LogstashLoggerUtils.createFatalExceptionLogWithMessage(e,
                    ExceptionTypeEnum.INFRA.REDIS.name(),
                    uid,
                    "AccountService.removeUserFromCache",
                    "Error deleting user from Redis for UID:" + uid);

            logger.error("Error deleting user from Redis for UID : "+uid+" ; Error : "+e.getMessage(),e);
        }
    }


    public JSONObject createChromecastUser(String msisdn, JSONObject requestPayload)
    {
        try
        {
            boolean msisdnDetected = !StringUtils.isEmpty(msisdn);

            UserDevice device = new UserDevice();
            String username = null;

            //Skipping NDS calls for operatorName and circle as its not required for chromecast user
            String operatorName = "other";

            User appUser = null;

            JSONObject response = new JSONObject();

            String uuid = UserDeviceUtils.generateUUIDForChromecastUser(ChannelContext.getUid());
            if(StringUtils.isEmpty(uuid))
            {
                logger.error("[ALERT] NULL UUID for chromecast user: {}", requestPayload);
                return createErrorResponse("BSY012","NULL UUID : "+requestPayload);
            }

            //check if the chromecast user already exists for a given msisdn or deviceId
            appUser = getUserFromUid(uuid);

            if (appUser == null)
            {
                //create if it does not exists
                appUser = new User();
                appUser.setUid(uuid);
                appUser.setCreationDate(System.currentTimeMillis());
                appUser.setLastActivityDate(System.currentTimeMillis());

                if(StringUtils.isNotBlank(operatorName))
                    appUser.setOperator(operatorName);

                if(!StringUtils.isEmpty(msisdn))
                    appUser.setMsisdn(msisdn);

                if(!StringUtils.isEmpty(username))
                    appUser.setName(username);

                appUser.addPack(FUPPack.class.getSimpleName(), System.currentTimeMillis(), MusicConstants.MUSIC_FUP_VALIDITY_IN_MILLIS);

                List<UserDevice> devices = appUser.getDevices();
                if(devices == null)
                    devices = new ArrayList<>();

                device.setUid(uuid);
                if(!StringUtils.isEmpty(msisdn))
                    device.setMsisdn(msisdn);

                String token = generateTokenForMsisdn(uuid);
                appUser.setToken(token);
                device.setDeviceId(ChannelContext.getDeviceId());

                devices.add(device);


                appUser.setDevices(devices);

                int clientType = ChannelContext.getClientType();
                appUser.setSource(clientType);

                createUser(appUser);
                logger.info("Successfully created chromecast user for msisdn: {}, uuid: {}", msisdn, ChannelContext.getUid());

                if(musicService.getStatsMonitoring() != null) {
                    musicService.getStatsMonitoring().increment("wynk.user.new.chromecast");
                }

            } else  {
                //update the last activity; can be used for purging the inactive user

                if (ChannelContext.getDeviceId() != null) {
                    device = appUser.getDevice(ChannelContext.getDeviceId());
                }

                updateLastActivityForUid(uuid);
                logger.info("Returning the existing chromecast user for msisdn: {}, uuid: {}", msisdn, uuid);
            }

            response.put("msisdn",appUser.getMsisdn());
            response.put("md",msisdnDetected);

            response.put("uid",appUser.getUid());

            if (device != null)
                response.put("token",appUser.getToken());

            response.put("dupd", false);

            response.put("purge", false);

            response.put("carrier", operatorName);
            return response;
        }
        catch (Exception e) {

            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e,
                    ExceptionTypeEnum.CODE.name(),
                    ChannelContext.getUid(),
                    "AccountService.createChromecastUser",
                    "Error creating chromecast for msisdn" + msisdn);

        	logger.error("Error creating chromecast for msisdn : "+msisdn+", uuid: " + ChannelContext.getUid() +"requestPayload: "+requestPayload+". Error : "+e.getMessage(),e);

            return createErrorResponse("BSY002",e.getMessage());
        }
    }

    public boolean updateOperatorForUid(String uuid, String operator, NdsUserInfo userInfo) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("uid", uuid);
        Map<String, Object> queryValues = new HashMap<>();
        queryValues.put("operator", operator);
        userEventService.addUserUpdateEvent(uuid, getDeviceId(), JsonKeyNames.OPERATOR,  operator);

        if (userInfo != null) {
        	if (StringUtils.isNotEmpty(userInfo.getCircle())) {
                if (Circle.getCircleShortName(userInfo.getCircle()) != null) {
                    queryValues.put("circle", Circle.getCircleShortName(userInfo.getCircle()).toUpperCase());
                    userEventService.addUserUpdateEvent(uuid, getDeviceId(), JsonKeyNames.CIRCLE, Circle.getCircleShortName(userInfo.getCircle()).toUpperCase());
                }
            }

            if (userInfo.getUserType() != null) {
                queryValues.put("userType", userInfo.getUserType().name());
            }
        }

        boolean setField = mongoUserDBManager.setField(USER_COLLECTION, queryParams, queryValues, false);
        return setField;
    }

    public void updateUserDeviceInDBAndCache(List<UserDevice> userDevices, User user){
        updateDevicesForUid(user.getUid(), userDevices);
        updateUserContext(false);
    }
     public void updateDeviceInUserDevicesAndDb(UserDevice updatedUserDevice){
         User user = ChannelContext.getUser();
         List<UserDevice> userDevices = user.getDevices();
         List<UserDevice> newUserDevices = new ArrayList<>();
         for (UserDevice userDevice : userDevices){
             if (userDevice.getDeviceId().equalsIgnoreCase(updatedUserDevice.getDeviceId())){
                 try {
                     userDevice.fromJson(updatedUserDevice.toJsonObject().toJSONString(),false);
                 } catch (Exception e) {
                     logger.error("Error in updating device data in DB" + e.getMessage());
                     e.printStackTrace();
                 }
             }
             newUserDevices.add(userDevice);
         }
         updateUserDeviceInDBAndCache(newUserDevices, user);
     }

  public JSONObject validateMusicUser(String uid, String deviceId) throws PortalException {
    JSONObject response = new JSONObject();
    double daysSinceLastActive = 0;
    boolean isUser = false;
    User user = getUserFromUid(uid);
    if (Objects.nonNull(user)) {
      daysSinceLastActive = (double) (System.currentTimeMillis() - user.getLastActivityDate()) / (1000 * 60 * 60 * 24);
      if (daysSinceLastActive < MusicConstants.DAYS_FOR_USER_TO_BE_VALID && Objects.nonNull(user.getDevice(deviceId))) {
        isUser = true;
      }
    }
    logger.info("[validateMusicUser] uid : {} ,daysSinceLastActive : {} ,deviceId :{} ,is_user : {}", uid, daysSinceLastActive, deviceId, isUser);
    response.put("is_user", isUser);
    return response;
  }

  public void purgeOfferCache(String uid) throws PortalException {
    User user = getUserFromUid(uid);
    if (user != null) {
      UserSubscription wcfSubscription = user.getUserSubscription();
      wcfSubscription.setOfferTS(0L);
      Map<String, Object> paramValues =
          Collections.singletonMap(UserEntityKey.userSubscription, JsonUtils.getJsonObjectFromString(wcfSubscription.toJson()));
      updateSubscriptionForUserId(
          uid, paramValues, Boolean.FALSE, wcfSubscription);
      removeUserFromCache(user.getUid());
    }
  }

    private String convertIntoSlowOtpForCall(String otp) {
        try {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < otp.length() - 1; i++) {
                res.append(otp.charAt(i)).append(". ");
            }
            res.append(otp.charAt(otp.charAt(otp.length() - 1)));
            return res.toString();
        } catch (Exception e) {
            logger.error("Exception while conveting otp : {} to slow otp : {}", otp, e.toString());
        }
        return otp;
    }

    private String prepareMsgBasedOnIndent(String otp, String deleteIndent) {
        String otpMsg = null;
        if (Utils.isIndentTypeValid(deleteIndent)) {
            otpMsg = "There has been an initiation of Account deletion request from your Wynk ID. Please enter PIN " + otp + " to approve the same. We are sad to see you go hope to serve you again soon!";
            if (MusicDeviceUtils.isAndroidDevice() || WynkAppType.isWynkBasicApp()) {
                otpMsg = "<#> There has been an initiation of Account deletion request from your Wynk ID. Please enter PIN " + otp + " to approve the same. We are sad to see you go hope to serve you again soon!\n" + musicConfig.getAppSMSHashCode();
            }
        } else {
            otpMsg = "Hi! Your music PIN is " + otp + ". Keep grooving with Wynk :)";
            if (MusicDeviceUtils.isAndroidDevice()) {
                otpMsg = "<#> Hi! Your music PIN is " + otp + ". Keep grooving with Wynk :)\n" + musicConfig.getAppSMSHashCode();
            }
            if (WynkAppType.isWynkBasicApp()) {
                otpMsg = "<#> Hi! Your Wynk Tube PIN is " + otp + ".\n" + musicConfig.getBasicSMSHashCode();
            }
        }
        return otpMsg;
    }

    public HttpResponse deleteUserAccount(String requestPayload, HttpRequest request) {
        JSONObject jsonResponse  = new JSONObject();
        jsonResponse.put("success", Boolean.FALSE);
        String uid = "";
        try {
            String utkn = UserUtils.getHeaderValue(request.headers(), Constants.RequestHeaders.X_BSY_UTKN);
            uid = utkn.split(":")[0];
            validateUserDeleteRequest(uid, jsonResponse);
            String did = UserUtils.getHeaderValue(request.headers(), Constants.RequestHeaders.X_BSY_DID);
            String cid = UserUtils.getHeaderValue(request.headers(), Constants.RequestHeaders.X_BSY_CID);
            User user = getUserFromContext(uid);
            String msisdn = user != null && user.getMsisdn() != null ? user.getMsisdn() : null;
            ChannelContext.setMsisdn(msisdn);
            String body = WcfHelper.prepareBodyForUserConsent(requestPayload);
            logger.info("Prepared body is, {}", body);
            Boolean isSuccess = wcfService.validateUserConsent(body);
            if (isSuccess) {
                removeUserFromCache(uid);
                logger.info("Delete user from redis cache with key uid:{}", uid);
            }
            logger.info("Received status response from wcf is {}",isSuccess);
            jsonResponse.put("success", isSuccess);
            if (isSuccess) {
                logger.info("Going to deactivate HT after consent captured");
                htApiService.deactivateUserHt(uid, msisdn);
            }
        } catch (Exception e) {
            // log and push stats
            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e,
                    ExceptionTypeEnum.CODE.name(),
                    uid,
                    "AccountUrlRequestHandler.deleteUserAccount",
                    "Exception while delete user");

            logger.error("Exception while delete user. Exception: {}, {}", e.getMessage(), e);
        }
        return HttpResponseService.createOKResponse(jsonResponse.toJSONString());
    }

    private void validateIfUserAlreadyDelete(String uid, JSONObject jsonResponse) {
        if (Objects.nonNull(uid)) if (!isUserDeleted(uid)) return;
        jsonResponse.put("reason", "You have already initiated delete request with us");
        throw new RuntimeException("Multiple delete request not allowed");
    }

    public boolean isUserDeleted(String uid) {
        User user = getUserFromDB(uid);
        // If no delete flag found for user or user is Not Deleted in DB.
        if (user != null && (user.isDeleted() == null || !user.isDeleted())) {
            // not deleted
        logger.info("Extracted user from db is : {}", user.toString());
        logger.info("User delete flag status in db is: {}", user.isDeleted());
            return false;
        }
        // already deleted
        return true;
    }


    private void validateUserDeleteRequest(String uid, JSONObject jsonResponse) throws RuntimeException {
        validateIfUserAlreadyDelete(uid, jsonResponse);
        if (isUserAllowed(uid)) {
            return;
        }
        jsonResponse.put("reason", "Your Session expired ! Please retry again.");
        throw new RuntimeException("User delete request is expired");
    }

    public boolean isUserAllowed(String uid) {
        if (Objects.nonNull(uid)) {
            Long fetchedValue = otpService.getTTL(REDIS_USER_UID_KEY + uid);
            logger.info("fetched val from db is  {}", fetchedValue);
            Integer expiryTimeOfKey = (fetchedValue != null && fetchedValue > 0) ? fetchedValue.intValue() : -1;
            logger.info("time left is   {}", expiryTimeOfKey);
            if (expiryTimeOfKey > 0) { // if key exist then  delete key from redis
                userPersistantRedisServiceManager.delete(REDIS_USER_UID_KEY + uid);
                return true;
            }
        }
        return false;
    }

    public boolean updateUserCountryInDB(String uid, Object fieldValue) {
        return updateUserFieldInDb(uid, UserEntityKey.countryId, fieldValue);
    }


    public boolean deleteUserFromDB(String uid, Object fieldValue) {
        return updateUserFieldInDb(uid, UserEntityKey.isDeleted, fieldValue);
    }

    private boolean updateUserFieldInDb(String uid, String fieldName, Object fieldValue) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(UserEntityKey.uid, uid);
        Map<String, Object> queryValues = new HashMap<>();
        queryValues.put(fieldName, fieldValue);
        return mongoUserDBManager.setField(USER_COLLECTION, queryParams, queryValues, false);
    }

    public HttpResponse getLoginInfo(String requestPayload, HttpRequest request) {

        String uri = request.getUri();

        try {
            if (StringUtils.isEmpty(requestPayload))
                return HttpResponseService
                        .createResponse(AccountService.createErrorResponse("BSY007", "Request body can't be empty.").toJSONString(),
                                HttpResponseStatus.NO_CONTENT);

            JSONObject requestBody = getJsonObjectFromPayload(requestPayload);

            if (requestBody != null) {
                String encryptedMsisdn = (String) getJsonObjectFromPayload(requestPayload).get("msisdn");
                String deviceId = (String) getJsonObjectFromPayload(requestPayload).get(DEVICE_ID);

                if (StringUtils.isEmpty(encryptedMsisdn) && StringUtils.isEmpty(deviceId) ) {
                    return HttpResponseService.createResponse(
                            AccountService.createErrorResponse("BSY001", "unable to determine encryptedMsisdn").toJSONString(),
                            HttpResponseStatus.NO_CONTENT);
                }

                String msisdnNonEncrypted = EncryptUtils.decrypt_256(encryptedMsisdn, (ENCRYPTION_KEY_FOR_THANKS+deviceId).substring(0,16));

//                HashSet<String> allowedMsisdn = WcfHelper.allowedMsisdn;
//                logger.info("Allowed set size is {}", allowedMsisdn.size());
//                if (!allowedMsisdn.contains(msisdnNonEncrypted)) {
//
//                    return HttpResponseService
//                            .createResponse(AccountService.createErrorResponse("BSY010", "Mobile number not allowed.").toJSONString(),
//                                    HttpResponseStatus.NO_CONTENT);
//                }

                logger.info("Device id found in thanks request is {}", deviceId);

               requestBody.put("msisdn" ,  msisdnNonEncrypted);
               JSONObject thanksResponse = accountRegistrationService.loginOrCreateAccount(true, msisdnNonEncrypted, false, requestBody, false, request, uri, false);
                if (Objects.nonNull(thanksResponse) && Objects.nonNull(thanksResponse.get("token"))) {
                    String token = (String) thanksResponse.get("token");
                    String encryptedToken = EncryptUtils.encrypt_256(token, (ENCRYPTION_KEY_FOR_THANKS + deviceId).substring(0, 16));
                    thanksResponse.put("token", encryptedToken);
                    musicService.addConfigInfo(thanksResponse, null);
                    logger.info("So far my json object is {}", thanksResponse);
                    BasicUserInfo basicUserInfo = gson.fromJson(thanksResponse.toJSONString(), BasicUserInfo.class);
                    return HttpResponseService.createOKResponse(gson.toJson(basicUserInfo));
                }
            }
        } catch (Exception e) {
            logger.error("Exception occurred in user login {}", e.getMessage(),e);
        }
        return HttpResponseService.createOKResponse(EMPTY_STRING);
    }
}
