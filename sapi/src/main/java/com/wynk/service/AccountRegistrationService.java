package com.wynk.service;

import com.wynk.auth.Auth;
import com.wynk.common.Circle;
import com.wynk.common.Country;
import com.wynk.common.ExceptionTypeEnum;
import com.wynk.common.Language;
import com.wynk.common.OperatorCircle;
import com.wynk.config.MusicConfig;
import com.wynk.constants.Constants;
import com.wynk.constants.JsonKeyNames;
import com.wynk.constants.MusicConstants;
import com.wynk.constants.WCFChannelEnum;
import com.wynk.db.ShardedRedisServiceManager;
import com.wynk.dto.LoginCreateAccountDTO;
import com.wynk.dto.NdsUserInfo;
import com.wynk.dto.SimInfo;
import com.wynk.dto.WCFRegistrationChannel;
import com.wynk.exceptions.OTPAuthorizationException;
import com.wynk.music.CircleInfoMccMncCache;
import com.wynk.music.WCFServiceType;
import com.wynk.music.constants.MusicContentLanguage;
import com.wynk.music.constants.MusicNotificationType;
import com.wynk.music.dto.MusicPlatformType;
import com.wynk.music.service.MusicContentService;
import com.wynk.musicpacks.FUPPack;
import com.wynk.musicpacks.MusicLanguagesMappings;
import com.wynk.notification.NotificationsText;
import com.wynk.server.ChannelContext;
import com.wynk.service.api.NdsUserInfoApiService;
import com.wynk.sms.SMSService;
import com.wynk.solace.listeners.events.UserCreationEvent;
import com.wynk.solace.publishers.MusicUserEventPublisher;
import com.wynk.user.dto.User;
import com.wynk.user.dto.UserDevice;
import com.wynk.user.dto.UserFavorite;
import com.wynk.user.dto.UserPurchase;
import com.wynk.utils.EncryptUtils;
import com.wynk.utils.HTTPUtils;
import com.wynk.utils.JsonUtils;
import com.wynk.utils.LogstashLoggerUtils;
import com.wynk.utils.MusicBuildUtils;
import com.wynk.utils.MusicDeviceUtils;
import com.wynk.utils.MusicUtils;
import com.wynk.utils.UserDeviceUtils;
import com.wynk.utils.Utils;
import com.wynk.utils.WCFUtils;
import com.wynk.wcf.WCFApisConstants;
import com.wynk.wcf.WCFApisService;
import com.wynk.wcf.dto.DeviceType;
import com.wynk.wcf.dto.MsisdnIdentificationRequest;
import com.wynk.wcf.dto.MsisdnIdentificationResponse;
import com.wynk.wcf.dto.UserMobilityInfo;
import com.wynk.wcf.dto.UserSubscription;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.wynk.common.Country.getCountryByCountryCode;
import static com.wynk.service.AccountService.AIRTEL;
import static com.wynk.utils.MusicUtils.getDeviceRedisBucketId;

/**
 * Created by Aakash on 07/07/17.
 */
@Service
public class AccountRegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(AccountRegistrationService.class.getCanonicalName());

    private static final Logger mDuplicateUidLogger = LoggerFactory.getLogger("mduplicateuids");

    @Autowired
    private OtpService otpService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private WCFService wcfService;

    @Autowired
    private WCFUtils wcfUtils;

    @Autowired
    private Auth auth;

    @Autowired
    private MusicContentService musicContentService;

    @Autowired
    private MusicService musicService;

    @Autowired
    private UserEventService userEventService;

    @Autowired
    private MusicConfig musicConfig;

    @Autowired
    private SMSService smsService;

    @Autowired
    private NdsUserInfoApiService ndsUserInfoApiService;

    @Autowired
    private CircleInfoMccMncCache circleInfoMccMncCache;

    @Autowired
    private ShardedRedisServiceManager userPersistantRedisServiceManager;

    @Autowired
    private MyAccountService myAccountService;

    @Autowired
    private MobileConnectService mobileConnectService;

    @Autowired
    private KafkaProducer kafkaProducerManager;

    @Autowired private GeoDBService geoDBService;

    @Autowired
    private MusicUserEventPublisher eventPublisher;

    @Autowired
    private WCFApisService wcfApisService;

    ExecutorService executor = Executors.newFixedThreadPool(10);

    private static String DEVICE_KEY = "deviceId";
    private static String USER_AGENT = "userAgent";

    public static final String TOKEN_LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final int INIT_TOKEN_SIZE = 8;

    private static final Boolean solaceOn = true;

    public String validateOtp(String jsonMsisdn, String otpPinFromApp, Boolean isFourDigitPin,Country country) throws OTPAuthorizationException {
        return validateOtp(jsonMsisdn, otpPinFromApp, isFourDigitPin, country, Constants.EMPTY_STRING);
    }
    public String validateOtp(String jsonMsisdn, String otpPinFromApp, Boolean isFourDigitPin,Country country, String intent) throws OTPAuthorizationException {
        logger.info("Inside validateOtp function");
        if (StringUtils.isEmpty(intent)) {
            intent = Constants.LOGIN;
        }

        if (MusicUtils.isTestSIM(jsonMsisdn)) {
            logger.info("Validate Otp Success for Test Msisdn {}", jsonMsisdn);
            return jsonMsisdn;
        }
        Language userLang = Language.getLanguageById(ChannelContext.getLang());

        if(StringUtils.isBlank(jsonMsisdn)){
            return null;
        }
        else{
            if (null == otpPinFromApp){
                HashMap<String, String> logExtras = new HashMap();
                logExtras.put("invalidOtp", otpPinFromApp);
                LogstashLoggerUtils.createStandardLog("MUSIC_REGISTRATION", "", jsonMsisdn, "", logExtras);
                JSONObject jsonObject = JsonUtils.createErrorResponse("BSY004", NotificationsText.getLangNotificationWithParams(MusicNotificationType.OTP_INVALID_TITLE, userLang,
                        null), NotificationsText.getLangNotificationWithParams(MusicNotificationType.OTP_INVALID_MESSAGE, userLang, null));
                throw new OTPAuthorizationException(jsonObject);
            }
        }

        jsonMsisdn = Utils.normalizePhoneNumber(jsonMsisdn,country);
        String otpPinFromRedis = otpService.getPin(jsonMsisdn, isFourDigitPin, intent);
        if (StringUtils.isBlank(otpPinFromRedis)) {
            logger.info("Otp Pin From Redis is Blank for msisdn {}", jsonMsisdn);
            JSONObject jsonObject = JsonUtils.createErrorResponse("BSY005", NotificationsText.getLangNotificationWithParams(MusicNotificationType.OTP_EXPIRED_TITLE, userLang,
                    null), NotificationsText.getLangNotificationWithParams(MusicNotificationType.OTP_EXPIRED_MESSAGE, userLang, null));
            throw new OTPAuthorizationException(jsonObject);
        }

        Boolean isValidPin = otpService.validatePin(jsonMsisdn, otpPinFromApp, otpPinFromRedis);

        if (!isValidPin) {
            HashMap<String, String> logExtras = new HashMap();
            logExtras.put("invalidOtp", otpPinFromApp);
            LogstashLoggerUtils.createStandardLog("MUSIC_REGISTRATION", "", jsonMsisdn, "", logExtras);
            JSONObject jsonObject = JsonUtils.createErrorResponse("BSY004", NotificationsText.getLangNotificationWithParams(MusicNotificationType.OTP_INVALID_TITLE, userLang,
                    null), NotificationsText.getLangNotificationWithParams(MusicNotificationType.OTP_INVALID_MESSAGE, userLang, null));
            throw new OTPAuthorizationException(jsonObject);
        }
        //removing otp post successful validation
        otpService.expireOtp(jsonMsisdn, isFourDigitPin, intent);
        HashMap<String, String> logExtras = new HashMap();
        logExtras.put("valid otp found", otpPinFromApp);
        LogstashLoggerUtils.createStandardLog("MUSIC_REGISTRATION", "", jsonMsisdn, "", logExtras);
        logger.info("exiting validateOtp function");
        return jsonMsisdn;
    }

    public String getAuthenticatedMsisdn(Boolean isByPassOtp ,Boolean isSamsungGTMDevice, JSONObject requestPayload, Boolean isFourDigitPin, UserDevice userDevice,boolean isEncrypted) throws OTPAuthorizationException {
        String jsonMsisdn = null;
        try {
            if (requestPayload.get("msisdn")!=null) {
                jsonMsisdn = isEncrypted ? EncryptUtils
                    .decrypt_256((String) requestPayload.get("msisdn"),
                        EncryptUtils.getDeviceKey())
                    : (String) requestPayload.get("msisdn");
            }
        } catch (Exception e) {
            logger.error("Error while decrypting msisdn",e);
        }
        String countryCode=(String) requestPayload.get("countryCode");
        Country country=Country.getCountryByCountryCode(countryCode);
        if (isSamsungGTMDevice) {
            return Utils.normalizePhoneNumber(jsonMsisdn,country);
        }

        if (isByPassOtp) {
            logger.info("By passing otp check , msisdn so far is {}", jsonMsisdn);
            return jsonMsisdn;
        }

        String otpPinFromApp = (String) requestPayload.get("otp");
        return validateOtp(jsonMsisdn, otpPinFromApp, isFourDigitPin,country);
    }

    public LoginCreateAccountDTO initialiseUser(LoginCreateAccountDTO loginCreateAccountDTO, MusicPlatformType platform) {
        User appUser = new User();

        String uuid = loginCreateAccountDTO.getUuid();
        appUser.setUid(uuid);
        appUser.setCreationDate(System.currentTimeMillis());
        appUser.setLastActivityDate(System.currentTimeMillis());
        appUser.setPlatform(platform.getPlatform());

        UserMobilityInfo wcfOperatorInfo = loginCreateAccountDTO.getUserMobilityInfo();
        String msisdn = loginCreateAccountDTO.getMsisdn();
        logger.info("Msisdn found so far in initialise user is {}", msisdn);

        if (wcfOperatorInfo != null && StringUtils.isNotBlank(wcfOperatorInfo.getOperator())) {
            appUser.setOperator(wcfOperatorInfo.getOperator());
        }

        if (StringUtils.isNotBlank(msisdn)) {
            appUser.setMsisdn(msisdn);
        }

        if (wcfOperatorInfo != null && StringUtils.isNotBlank(wcfOperatorInfo.getCircleShortName())) {
            appUser.setCircle(wcfOperatorInfo.getCircleShortName());
        }

        if (wcfOperatorInfo != null && StringUtils.isNotBlank(wcfOperatorInfo.getUserType())) {
            appUser.setUserType(wcfOperatorInfo.getUserType());
        }
        //sri lanka
        if (wcfOperatorInfo != null) {
            logger.info("Where wcfOperatorInfo is not null");
            //nds info circle
            if (StringUtils.isNotBlank(wcfOperatorInfo.getCircle()) && wcfOperatorInfo.getCircle().equalsIgnoreCase(Circle.SRILANKA.name())) {
                appUser.setCountryId(Country.SRILANKA.getCountryId());
                appUser.setCircle(Circle.SRILANKA.getCircleId());
            } else if (MusicUtils.isSriLankaMsisdn(msisdn)) {
                appUser.setCountryId(Country.SRILANKA.getCountryId());
                appUser.setCircle(Circle.SRILANKA.getCircleId());
            } else if (StringUtils.isEmpty(msisdn)) {   //unregistered user
                if (StringUtils.isNotEmpty(wcfOperatorInfo.getCountryCodeMcc()) && wcfOperatorInfo.getCountryCodeMcc().equals(Country.SRILANKA.getCountryCode())) {
                    appUser.setCountryId(Country.SRILANKA.getCountryId());
                    appUser.setCircle(Circle.SRILANKA.getCircleId());
                } else if (StringUtils.isNotEmpty(wcfOperatorInfo.getCountryCodeIP()) && wcfOperatorInfo.getCountryCodeIP().equalsIgnoreCase(Country.SRILANKA.getIsoCode())) {
                    appUser.setCountryId(Country.SRILANKA.getCountryId());
                    appUser.setCircle(Circle.SRILANKA.getCircleId());
                }
            } else if (StringUtils.isNotEmpty(msisdn)) {
                logger.info("Here country Id is of India");
                appUser.setCountryId(Country.INDIA.getCountryId());
            }
        }

        if (MusicUtils.isSingaporeMsisdn(msisdn)) {
            appUser.setCountryId(Country.SINGAPORE.getCountryId());
            appUser.setCircle(Circle.SINGAPORE.getCircleId());
            logger.info("Circle and Country set for Singapore");
        }
        List<String> userContentList = accountService.getContentLanguageList(appUser, wcfOperatorInfo, true);
        appUser.setContentLanguages(userContentList);

        appUser.addPack(FUPPack.class.getSimpleName(), System.currentTimeMillis(), MusicConstants.MUSIC_FUP_VALIDITY_IN_MILLIS);

        loginCreateAccountDTO.setAppUser(appUser);
        UserDevice userDevice = loginCreateAccountDTO.getCurrentDevice();

        int clientType = ChannelContext.getClientType();

        // try getting client type from device os, in cases where header doesn't come specially for backward compatibility.
        if (clientType == 0) {
            clientType = ChannelContext.getClientTypeFromDeviceOs(userDevice.getOs());
        }
        appUser.setSource(clientType);

        String token = generateTokenForMsisdn(uuid);
        appUser.setToken(token);
//        appUser.setLang(loginCreateAccountDTO.getApplang());

        loginCreateAccountDTO.setAppUser(appUser);
        return loginCreateAccountDTO;
    }

    public User createUserFromConfigCall(HttpRequest request, JSONObject simInfo,boolean isEnrcypted) {
        try {
            UserDevice userDevice = new UserDevice();
            if(simInfo != null)
                userDevice.fromJson(simInfo.toJSONString(),isEnrcypted);

            userDevice.setDeviceId(MusicDeviceUtils.getDeviceId());
            userDevice.setBuildNumber(MusicDeviceUtils.getBuildNumber());
            userDevice.setOs(MusicDeviceUtils.getOS());
            userDevice.setOsVersion(MusicDeviceUtils.getOSVersion());
            userDevice.setAppVersion(MusicDeviceUtils.getAppVersion());
            userDevice.setUid(ChannelContext.getUid());

            String msisdnStr = ChannelContext.getMsisdn();
            MusicPlatformType platform = MusicPlatformType.WYNK_APP;

            String msisdn = null;
            platform = UserDeviceUtils.getUserPlatform(msisdn, userDevice.getDeviceId(), request);
            User appUser = new User();
            appUser.setCreationDate(System.currentTimeMillis());
            appUser.setLastActivityDate(System.currentTimeMillis());
            appUser.setPlatform(platform.getPlatform());
            appUser.setUid(ChannelContext.getUid());

            appUser.addPack(FUPPack.class.getSimpleName(), System.currentTimeMillis(), MusicConstants.MUSIC_FUP_VALIDITY_IN_MILLIS);

            int clientType = ChannelContext.getClientType();

            // try getting client type from device os, in cases where header doesn't come specially for backward compatibility.
            if (clientType == 0) {
                clientType = ChannelContext.getClientTypeFromDeviceOs(userDevice.getOs());
            }
            appUser.setSource(clientType);

            List<UserDevice> deviceList = new ArrayList<>();
            deviceList.add(userDevice);
            appUser.setDevices(deviceList);

            String token = generateTokenForMsisdn(appUser.getUid());
            appUser.setToken(token);
            accountService.createUserWithoutSendingEvents(appUser);
            return appUser;
        } catch (Exception e) {
            logger.error("Error creating user from config call, Error = "+e.getMessage()+e);
            e.printStackTrace();
            return null;
        }
    }

    //duid -> device uid
    private String generateTokenForMsisdn(String uid) {
        String ukey = uid;

        String token = null;
        token = accountService.getTokenForMsisdn(ukey, false);
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        int size = INIT_TOKEN_SIZE;
        if (StringUtils.isBlank(token)) {
            boolean isUidExistsForToken = false;
            do {
                token = RandomStringUtils.random(size, TOKEN_LETTERS);
                isUidExistsForToken = accountService.isTokenInUse(token);
                size++;
            } while (isUidExistsForToken);
            accountService.updateTokenInUser(uid, token);
        }
        accountService.setMTokenCache(ukey, token);
        return token;
    }

    public JSONObject  loginOrCreateAccount(boolean byPassOtp , String xMsisdn, Boolean isSamsungGTMDevice, JSONObject requestPayload, Boolean isFourDigitPin, HttpRequest request, String requestUri,boolean isEncrypted) {
        return loginOrCreateAccount(byPassOtp,xMsisdn, isSamsungGTMDevice, requestPayload, isFourDigitPin, request, null, requestUri,isEncrypted);
    }

    public JSONObject  loginOrCreateAccount(String xMsisdn, Boolean isSamsungGTMDevice, JSONObject requestPayload, Boolean isFourDigitPin, HttpRequest request, String requestUri,boolean isEncrypted) {
        return loginOrCreateAccount(xMsisdn, isSamsungGTMDevice, requestPayload, isFourDigitPin, request, null, requestUri,isEncrypted);
    }

    public JSONObject loginOrCreateAccount(String xMsisdn, Boolean isSamsungGTMDevice, JSONObject requestPayload, Boolean isFourDigitPin, HttpRequest request, String s2sMsisdn, String requestUri, boolean isEncrypted) {
        return loginOrCreateAccount(false, xMsisdn, isSamsungGTMDevice, requestPayload, isFourDigitPin, request, s2sMsisdn, requestUri, isEncrypted);
    }

    public JSONObject loginOrCreateAccount(boolean byPassOtp,String xMsisdn, Boolean isSamsungGTMDevice, JSONObject requestPayload, Boolean isFourDigitPin, HttpRequest request, String s2sMsisdn, String requestUri,boolean isEncrypted) {
        logger.info("Login or Create Account for the request {}", requestPayload);

        Boolean isRegistered = Boolean.FALSE;
        Boolean migrateDeviceUidData = Boolean.FALSE;
        Boolean isAutoRegistration = Boolean.FALSE;

//        String lang = Language.ENGLISH.getId();;
//        try{
//            lang = HTTPUtils.getStringParameter(HTTPUtils.getUrlParameters(requestUri), "lang");
//        }
//        catch(PortalException e){
//            logger.error("Error parsing appLang query string parameter - ",e.getMessage(),e);
//        }

        MsisdnIdentificationResponse msisdnIdentificationResponse = null;
        JSONObject response = new JSONObject();
        Boolean msisdnDetected = Boolean.FALSE;
        UserDevice userDevice = new UserDevice();
        String msisdn = null;

        if (requestPayload == null) {
            logger.info("Request Body for Registration is Null for request {}", request);
            return JsonUtils.createErrorResponse("", "NULL PAYLOAD : " + requestPayload);
        }

        Boolean autoRegister = true;
        if (requestPayload.get("autoregister") != null) {
            autoRegister = (Boolean) requestPayload.get("autoregister");
            // We dont wasn't to autoregister based on msisdn header
            if (autoRegister) {
                if (!checkMsidnHeaderForAcccountSync(requestPayload)) {
                    response = new JSONObject();
                    response.put("error", "Msisdn headers didn't match");
                    return response;
                }
            } else {
                msisdn = null;
                // Clearing context set during authentication
                ChannelContext.unset();
                ChannelContext.setRequest(request);
            }
        }

        Boolean isRetryCall = false;
        if (requestPayload.get("retryCount") != null) {
            isRetryCall = true;
        }

        Boolean isMConnectTest = false;
        if (requestPayload.get("isMConnectTest") != null) {
            isMConnectTest = (Boolean) requestPayload.get("isMConnectTest");
        }

        LoginCreateAccountDTO loginCreateAccountDTO = new LoginCreateAccountDTO();
        try {
            //msisdn decrypted
            msisdn = getAuthenticatedMsisdn(byPassOtp,isSamsungGTMDevice, requestPayload, isFourDigitPin, userDevice,isEncrypted);
            if (msisdn != null) loginCreateAccountDTO.setWcfRegistrationChannel(WCFChannelEnum.OTP);
        } catch (OTPAuthorizationException ex) {
            return ex.getJsonObject();
        }
        String msisdnDetectedFromOtherSource = null;
        try {
            //imsi and imei decrypted
            userDevice.fromAppJsonObject(requestPayload,isEncrypted);
            logger.info("Build number : " + userDevice.getBuildNumber());
            userDevice.setOsVersion(MusicDeviceUtils.getOSVersion(null));
            if (userDevice != null) {
                ChannelContext.setOscontext(userDevice.getOs());
                ChannelContext.setBuildnumbercontext(userDevice.getBuildNumber());
            }
            MusicPlatformType platform = UserDeviceUtils.getUserPlatform(StringUtils.isNotBlank(msisdn) ? msisdn : xMsisdn, userDevice.getDeviceId(), request);
            String mobileConnectAuthCode = (String) requestPayload.get("mobileConnectAuthCode");
            if (StringUtils.isBlank(msisdn) && ((StringUtils.isNotBlank(xMsisdn) || !isRetryCall)) && mobileConnectAuthCode == null && !isMConnectTest && StringUtils.isBlank(s2sMsisdn)) {
                JSONObject headerJSONObject = HTTPUtils.getHeaderJSON(request);
                String clientIps = null;
                if (MusicConstants.REG_BROADBAND_ENABLE && MusicBuildUtils.isChangeNumberBuildSupported()) {
                    clientIps = headerJSONObject.get("X-Forwarded-For") != null ? headerJSONObject.get("X-Forwarded-For").toString() : null;
                }
                msisdnIdentificationResponse = getMsisdnIdentificationResponse(msisdnIdentificationResponse, userDevice, xMsisdn);
                if (msisdnIdentificationResponse != null && msisdnIdentificationResponse.getData() != null) {
                    msisdnDetectedFromOtherSource = msisdnIdentificationResponse.getData().getMsisdn();
                    logger.info("Received msisdn from wcf is {}",msisdnDetectedFromOtherSource);
                    if (StringUtils.isNotBlank(msisdnDetectedFromOtherSource)) {
                        LogstashLoggerUtils.createAccessLogLite("NetworkEnrichedAutoLogin", "", "");
                    }
                }
//                msisdnDetectedFromOtherSource = msisdnIdentificationResponse != null ? msisdnIdentificationResponse.getData().getMsisdn() : null;
                if (StringUtils.isBlank(msisdnDetectedFromOtherSource)) {
                    msisdnDetectedFromOtherSource = getUniqueMsisdnFromDeviceId(userDevice.getDeviceId());
                }
            }

            if (StringUtils.isNotBlank(msisdnDetectedFromOtherSource)) {
                msisdn = msisdnDetectedFromOtherSource;
                isAutoRegistration = Boolean.TRUE;
            }

            if (StringUtils.isNotBlank(s2sMsisdn)) {
                msisdn = s2sMsisdn;
                isAutoRegistration = Boolean.TRUE;
            }

            if (!isAutoRegistration) {
                JSONObject mobileConnect = new JSONObject();
                mobileConnect.put("is_powered_by_mobile_connect", false);
                String operator = null;
                try {
                    operator = getOperatorFromMCCMNC(userDevice);
                } catch (Exception e) {
                  logger.error("error while getting operator", e);
                }
                if (operator == null) {
                    mobileConnect.put("operator", null);
                    mobileConnect.put("authUrl", null);
                    mobileConnect.put("isCallRequired", false);
                }
                else if (mobileConnectAuthCode == null) {
                    mobileConnect.put("operator", operator);
                    mobileConnect.put("authUrl", mobileConnectService.getAuthUrl(operator));
                    mobileConnect.put("isCallRequired", true);
                }
                else {
                    String mobileConnectServiceMSISDN = mobileConnectService.getMSISDN(operator, mobileConnectAuthCode);
                    if (mobileConnectServiceMSISDN != null) {
                        msisdn = mobileConnectServiceMSISDN;
                        isAutoRegistration = Boolean.TRUE;
                        mobileConnect.put("is_powered_by_mobile_connect", true);
                    }
                    mobileConnect.put("operator", null);
                    mobileConnect.put("authUrl", null);
                    mobileConnect.put("isCallRequired", false);
                }
                response.put("mobileConnect", mobileConnect);
            }

            msisdnDetected = StringUtils.isNotBlank(msisdn);
            platform = UserDeviceUtils.getUserPlatform(msisdn, userDevice.getDeviceId(), request);
            String uuid = UserDeviceUtils.generateUUID(msisdn, userDevice.getDeviceId(), ChannelContext.getRequest(), platform);
            if (StringUtils.isEmpty(uuid)) {
                logger.error("[ALERT] NULL UUID : " + requestPayload);
                return JsonUtils.createErrorResponse("BSY012", "NULL UUID : " + requestPayload);
            }

            User deviceUser = ChannelContext.getUser();
            User appUser = accountService.getUserFromDB(uuid);
            logger.info("getting operator info");
            UserMobilityInfo wcfOperatorInfo = getOperatorInfoDetail(musicConfig.isEnableNDS(), msisdn, msisdnIdentificationResponse);
            String operatorNameFromApp = (String) requestPayload.get("carrier");
            // TODO - We already have circle info from either WCF or NDS, do not want to use CicleInfoCatalog so no need for below line
            //wcfOperatorInfo = getWcfOperatorInfoFromCircleInfo(msisdn,wcfOperatorInfo,operatorNameFromApp);
            logger.info("got operator info");
            if (wcfOperatorInfo != null) {
                wcfOperatorInfo.setCountryCodeIP(MusicUtils.getFirstIPCountry(geoDBService,request));
            }
            getOperatorInfoFromMNCANDMCC(userDevice, wcfOperatorInfo);
            long initialLAD = System.currentTimeMillis();
            if (StringUtils.isNotBlank(uuid) && StringUtils.isNotBlank(ChannelContext.getUid()) && !uuid.equalsIgnoreCase(ChannelContext.getUid())) {
                if (deviceUser != null) {
                    if (appUser != null) {
                        logger.info("MSISDN based UID found : " + uuid);
                        initialLAD = appUser.getLastActivityDate();
                        appUser.setLastActivityDate(System.currentTimeMillis());

                        // In case of user registration id is changed hence update the user details in google now db
                        // TODO - Removing Google now update
                        //authService.updateUserIdInfo(ChannelContext.getUid(), appUser.getUid());
                    }
                    if (StringUtils.isBlank(deviceUser.getMsisdn())) {
                        migrateDeviceUidData = Boolean.TRUE;
                    }
                }
            }
            Boolean isNewRegistration = Boolean.FALSE;
            loginCreateAccountDTO.setUpdatePlayList(Boolean.FALSE);
            loginCreateAccountDTO.setUpdateUserinCache(Boolean.FALSE);
            loginCreateAccountDTO.setUserMobilityInfo(wcfOperatorInfo);
            loginCreateAccountDTO.setCurrentDevice(userDevice);
            loginCreateAccountDTO.setMsisdn(msisdn);
            loginCreateAccountDTO.setUuid(uuid);
            loginCreateAccountDTO.setAppUser(appUser);
//            loginCreateAccountDTO.setApplang(lang);
            if (msisdnIdentificationResponse != null && msisdnIdentificationResponse.getData() != null) {
                loginCreateAccountDTO.setWcfRegistrationChannel(msisdnIdentificationResponse.getData().getChannel());
            }

            if (appUser == null) {
                logger.info("User is null , so new registration");
                isNewRegistration = Boolean.TRUE;
                createUser(loginCreateAccountDTO, platform, migrateDeviceUidData, isNewRegistration, userDevice, msisdn);
                loginCreateAccountDTO.getAppUser().loadUserProfile(response);
                //totpDeviceId
                response.put("dt",loginCreateAccountDTO.getCurrentDevice().getTotpDeviceId());
                //totpKey
                response.put("kt",loginCreateAccountDTO.getCurrentDevice().getTotpDeviceKey());
                response.put(MusicConstants.NEW_USER, true);
            } else {
                updateAndGetUserCountryIfNotExist(appUser, true);
                generateTokenForMsisdn(appUser.getUid());
                userDevice.setUid(appUser.getUid());

                loginCreateAccountDTO.setCurrentDevice(userDevice);
                loginCreateAccountDTO.setIsSimulator(Boolean.FALSE);

                loginCreateAccountDTO = initialiseDevice(loginCreateAccountDTO, migrateDeviceUidData, Boolean.FALSE);
                //totpDeviceId
                response.put("dt",loginCreateAccountDTO.getCurrentDevice().getTotpDeviceId());
                //totpKey
                response.put("kt",loginCreateAccountDTO.getCurrentDevice().getTotpDeviceKey());
                response.put(JsonKeyNames.INITIAL_LAD, initialLAD);
                List<UserDevice> deviceList = loginCreateAccountDTO.getUserDeviceList();
                if (!CollectionUtils.isEmpty(deviceList)) {
                    loginCreateAccountDTO.getAppUser().setDevices(deviceList);
                }
                loginCreateAccountDTO = initialiseUserAppDetails(migrateDeviceUidData, loginCreateAccountDTO, isNewRegistration);
                if (migrateDeviceUidData && ChannelContext.getUser() != null) {
                    musicService.updateUserPlaylistToNewUser(ChannelContext.getUid(), loginCreateAccountDTO.getAppUser().getUid());
                    musicService.updateADHMPlaylistToNewUser(ChannelContext.getUid(), loginCreateAccountDTO.getAppUser().getUid());
                    accountService.purgeData(migrateDeviceUidData, loginCreateAccountDTO);
                }

                // Handling for removing expiry time from Mongo in case the unregistered user comes back within 30 days.
                if(ChannelContext.getUser() == null){
                    accountService.removeExpireAtField(appUser.getUid());
                }

                if (!loginCreateAccountDTO.getIsSimulator()) {
                    int source = loginCreateAccountDTO.getAppUser().getSource();
                    if (source == 0) {
                        source = ChannelContext.getClientType();

                        // try getting client type from device os, in cases where header doesn't come specially for backward compatibility.
                        if (source == 0) {
                            source = ChannelContext.getClientTypeFromDeviceOs(userDevice.getOs());
                        }
                        loginCreateAccountDTO.getAppUser().setSource(source);
                    }

                    if (wcfOperatorInfo != null) {
                        loginCreateAccountDTO.getAppUser().setUserType(wcfOperatorInfo.getUserType());
                    }

                    if (CollectionUtils.isEmpty(loginCreateAccountDTO.getAppUser().getContentLanguages())) {
                        List<String> contentLanguages = accountService.getContentLanguageList(loginCreateAccountDTO.getAppUser(), wcfOperatorInfo, isNewRegistration);
                        logger.info("Pulled list is getContentLanguageList  : {}", contentLanguages);
                        if (!CollectionUtils.isEmpty(contentLanguages)) {
                            loginCreateAccountDTO.getAppUser().setContentLanguages(contentLanguages);
                            logger.info("After pulling found list was empty so nothing to set");
                        }
                        if (appUser.getCircle() != null && wcfOperatorInfo != null && wcfOperatorInfo.isCircleDetectedFromMccMnc()) {
                            List<MusicContentLanguage> defaultLanguages = musicContentService.getDefaultLanguageByCircle(appUser.getCircle());
                            logger.info("Getting default language : {},  based on circle : {}",defaultLanguages, appUser.getCircle());
                            contentLanguages = Utils.ConvertContentLangListToStringList(defaultLanguages);

                            logger.info("Finally prepared list is :{}",contentLanguages);
                        }

                        logger.info("Going to update user content lang : {} with uid : {}", contentLanguages, loginCreateAccountDTO.getAppUser().getUid());

                        accountService.updateContentLangsForUser(loginCreateAccountDTO.getAppUser().getUid(), contentLanguages);
                    }
//                    appUser.setLang(loginCreateAccountDTO.getApplang());
                    accountService.updateDevicesForUid(loginCreateAccountDTO.getAppUser().getUid(), deviceList, source, loginCreateAccountDTO.getAppUser().getUserType(), loginCreateAccountDTO.getApplang());
                    loginCreateAccountDTO.setUpdateUserinCache(Boolean.TRUE);
                }

                appUser = loginCreateAccountDTO.getAppUser();
                if (loginCreateAccountDTO.getUpdateUserinCache() != null && loginCreateAccountDTO.getUpdateUserinCache()) {
                    User updatedUser = accountService.updateUserInCache(appUser.getUid(), false);
                    if (updatedUser != null)
                        appUser = updatedUser;
                }

                //populate the response object
                appUser.loadUserProfile(response);
                accountService.addUserSelectedLanguagesIfEmpty(response);

                Object selectedContentLang = response.get("contentLang");
                if (selectedContentLang != null) {
                    response.put("selectedContentLangs", selectedContentLang);
                }

                if (musicService.getStatsMonitoring() != null) {
                    musicService.getStatsMonitoring().increment("wynk.user.update");
                }

            }

            appUser = loginCreateAccountDTO.getAppUser();
            userDevice = loginCreateAccountDTO.getCurrentDevice();

            try {
                // Trigger UserCreationEvent for solace implementation.
                logger.info("User is new registration: {} , and is AirtelUser : {} , with user Id: {}", isNewRegistration, accountService.isAirtelUser(appUser), appUser.getUid());

                if (solaceOn && isNewRegistration == Boolean.TRUE && accountService.isAirtelUser(appUser)) {
                    UserCreationEvent userCreationEvent = new UserCreationEvent(UserCreationEvent.class.getCanonicalName(), appUser);
                    logger.info("Publishing event for new user with uid : {}", appUser.getUid());
                    eventPublisher.publish(userCreationEvent);

                }
            }
            catch (Exception e){
                logger.error("Exception occured while firing solace event : " + e);
            }

            if(isAutoRegistration) {
                LogstashLoggerUtils.autoLoginLogger(appUser.getUid(), appUser.getMsisdn());
            }

            removeAndUpdateDeviceIDUIDMapping(userDevice, appUser, uuid);

            if (StringUtils.isNotBlank(msisdn)) {
                String encryptionKey = appUser.getUid().substring(0, 16);
                response.put(JsonKeyNames.MSISDN, EncryptUtils.encrypt_256(msisdn, encryptionKey));
            }

            response.put("token", appUser.getToken());
            //response.put("msisdn",appUser.getMsisdn());
            response.put("md", msisdnDetected);
            response.put("uid", appUser.getUid());

            response.put("dupd", false);

            response.put("purge", false);

            response.put("server_timestamp", System.currentTimeMillis());

            String operator = appUser.getOperator();

            if (StringUtils.isNotBlank(appUser.getMsisdn()))
                isRegistered = true;

            //Code added to update the msisdn for corrupt users to allow them to log in
            String otpPinFromApp = (String) requestPayload.get("otp");
            if (StringUtils.isNotEmpty(msisdn) && !isRegistered && StringUtils.isNotEmpty(otpPinFromApp)) {
                appUser.setMsisdn(msisdn);
                accountService.updateMsisdnInUser(appUser.getUid(),appUser.getMsisdn());
                accountService.removeUserFromCache(appUser.getUid());
                isRegistered = true;
            }

            // Setting user data in context
            ChannelContext.setUser(appUser);
            ChannelContext.setUid(appUser.getUid());
            accountService.setDeviceDetailsInContext(appUser, ChannelContext.getRequest());

            response.put("isRegistered", isRegistered);
            response.put("changeMobile", isAutoRegistration);

            if (!StringUtils.isEmpty(operator))
                response.put("carrier", operator);

            String circleShortName = wcfOperatorInfo != null ? wcfOperatorInfo.getCircleShortName() : "";
            List<Language> appLangs = MusicLanguagesMappings.getLanguagesForCircle(circleShortName);
            JSONArray langArray = new JSONArray();
            for (int i = 0; i < appLangs.size(); i++) {
                Language lng = appLangs.get(i);
                langArray.add(lng.getId());
            }
            response.put("circleLang", langArray);

            String circle = appUser.getCircle();
            Boolean doesVersionSupportNewLangs = MusicBuildUtils.doesVersionSupportNewLangs();

            List<MusicContentLanguage> contentLangs = musicContentService.getLanguageOrderByCircle(circle);

            JSONArray contentLangsArray = new JSONArray();
            if (!CollectionUtils.isEmpty(contentLangs)) {
                for (int i = 0; i < contentLangs.size(); i++) {
                    MusicContentLanguage lng = contentLangs.get(i);
                    if (!doesVersionSupportNewLangs) {
                        if (MusicContentLanguage.isLangSupportedInOldAppVersions(lng.getId()))
                            contentLangsArray.add(lng.getId());
                    } else
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

            if (doesVersionSupportNewLangs)
                response.put("autoPlaylists", appUser.isCreatePlaylistsForDls());
            if (StringUtils.isNotBlank(appUser.getMsisdn())) {
                String emailInDb = "";
                try {
                    emailInDb = appUser.getEmail();
                } catch (Exception ignored) {
                    emailInDb = "";
                }
                if (appUser.getDevices() != null && appUser.getDevices().size() > 0) {
                    UserDevice device = appUser.getDevices().get(0);
                    LogstashLoggerUtils.userDumpLogs(appUser.getUid(),appUser.getMsisdn(),appUser.getCircle(),appUser.getOperator(),appUser.getName(),appUser.getCreationDate(),appUser.getLastActivityDate(),device.getOs(),device.getAppVersion(),device.getOsVersion(),device.getDeviceType(),Utils.getDobInMillis(appUser),emailInDb, appUser.getCountryId());
                }else
                {
                    LogstashLoggerUtils.userDumpLogs(appUser.getUid(),appUser.getMsisdn(),appUser.getCircle(),appUser.getOperator(),appUser.getName(),appUser.getCreationDate(),appUser.getLastActivityDate(),null,null,null,null ,Utils.getDobInMillis(appUser),emailInDb, appUser.getCountryId());
                }
            }

            return response;
        } catch (Exception e) {
            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e,
                    ExceptionTypeEnum.CODE.name(),
                    ChannelContext.getUid(),
                    "AccountService.findOrCreate",
                    "Error in creating account for MSISDN: " + msisdn);
            logger.error("Error creating account for msisdn : " + msisdn + ", requestPayload" + requestPayload + ". Error : " + e.getMessage() +  e);
            e.printStackTrace();
            return JsonUtils.createErrorResponse("BSY002", e.getMessage());
        }
    }

    /**
     * For Syncing account with autodetected msisdn we need to make sure current msisdn header and old msisdn detected are same.
     *
     * @param requestPayload
     * @return
     */
    private boolean checkMsidnHeaderForAcccountSync(JSONObject requestPayload) {
        if (requestPayload.containsKey("syncmsisdn")) {
            String syncMsisdn = (String) requestPayload.get("syncmsisdn");
            String key = ChannelContext.getUid().substring(0, 16);
            try {
                syncMsisdn = Utils.normalizePhoneNumber(EncryptUtils.decrypt_256(syncMsisdn, key));
                String msisdn = Utils.normalizePhoneNumber(UserDeviceUtils.getMsisdn(ChannelContext.getRequest()));
                if (syncMsisdn.equals(msisdn))
                    return true;
            } catch (Exception e) {
                logger.error("Error while decrypting syncmsisdn", e);
            }
        }
        return false;
    }

    private void sendWynkMoviesSMS(String msisdn) {
        smsService.sendWynkMessageWithSMSAPi(Utils.normalizePhoneNumber(msisdn), MusicConstants.WYNK_MOVIES_SMS, false, "MEDIUM", false,"+91");
    }

    public LoginCreateAccountDTO initialiseDevice(LoginCreateAccountDTO loginCreateAccountDTO, Boolean migrateDeviceUidData, Boolean isNewRegistration) {

        User appUser = loginCreateAccountDTO.getAppUser();
        if (appUser == null) {
            return loginCreateAccountDTO;
        }

        List<UserDevice> userDeviceList = new ArrayList<>();
        List<UserDevice> existingDeviceList = appUser.getDevices();

        UserDevice userDevice = loginCreateAccountDTO.getCurrentDevice();
        userDevice.setUid(appUser.getUid());

        if (StringUtils.isNotBlank(appUser.getMsisdn())) {
            userDevice.setMsisdn(appUser.getMsisdn());
        }

        WCFRegistrationChannel wcfRegistrationChannel = UserDeviceUtils.getRegistrationChannelBody(loginCreateAccountDTO.getWcfRegistrationChannel(), null);
        if (wcfRegistrationChannel != null) {
            userDevice.setRegistrationChannel(wcfRegistrationChannel);
        }

        if (migrateDeviceUidData && ChannelContext.getUser() != null) {
            String duplicateId = ChannelContext.getUser().getUid() + "," + appUser.getUid();
            sendDataToKafka(duplicateId);
            logger.info("duplicate ids : " + duplicateId);
            try {
                String key = getDateKey();
                userPersistantRedisServiceManager.saddWithExpire(key, duplicateId, 7 * 24 * 3600);
            } catch (Exception e) {
              logger.error("error writing duplicate id to redis : id : " + duplicateId, e);
            }
            if (ChannelContext.getUser().getUid().endsWith("0")) {
                LogstashLoggerUtils.registeredUidInUnregisteredBugLogger(
                        ChannelContext.getUser().toString(),
                        appUser.toString(),
                        Objects.requireNonNull(ChannelContext.getRequest()).toString());
            }
            mDuplicateUidLogger.info(duplicateId);
        }
        userDevice = accountService.getUpdatedDevice(userDevice, false, appUser);
        if (userDevice.getDeviceType().contains("Simulator")){
            loginCreateAccountDTO.setIsSimulator(Boolean.TRUE);
            if(CollectionUtils.isEmpty(existingDeviceList)){
                userDeviceList.add(userDevice);
            }
        }else{
            if (!CollectionUtils.isEmpty(existingDeviceList)) {
                Boolean deviceExists = Boolean.FALSE;
                for (UserDevice existingDevice : existingDeviceList) {
                    if (StringUtils.equalsIgnoreCase(existingDevice.getDeviceId(),userDevice.getDeviceId())) {
                        deviceExists = Boolean.TRUE;
                        logger.info("Updating device : " + userDevice.getDeviceId() + " , UID : " + appUser.getUid());
                        if (!userDevice.getAppVersion().equalsIgnoreCase(existingDevice.getAppVersion())) {
                            userEventService.addUserDeviceUpdateEvent(appUser.getUid(), userDevice, JsonKeyNames.DEVICE_APP_VER, userDevice.getAppVersion());
                        }

                        if (StringUtils.isNotEmpty(userDevice.getDeviceKey())) {
                            existingDevice.setDeviceKey(userDevice.getDeviceKey());
                            existingDevice.setDeviceKeyLastUpdateTime(System.currentTimeMillis());
                        }

                        existingDevice.setAppVersion(userDevice.getAppVersion());
                        existingDevice.setAppId(userDevice.getAppId());
                        existingDevice.setUid(appUser.getUid());
                        if (wcfRegistrationChannel != null) {
                            Boolean isVerifiedBefore = existingDevice.getRegistrationChannel() != null ? existingDevice.getRegistrationChannel().getVerifiedUser() : null;
                            if (isVerifiedBefore != null) {
                                isVerifiedBefore = isVerifiedBefore || wcfRegistrationChannel.getVerifiedUser();
                                wcfRegistrationChannel.setVerifiedUser(isVerifiedBefore);
                            }

                            existingDevice.setRegistrationChannel(wcfRegistrationChannel);
                        }

                        if(StringUtils.isEmpty(existingDevice.getTotpDeviceId()))
                        {
                            Map<String, String> totps = auth.generateTotp(userDevice.getDeviceId());
                            if (totps != null) {
                                existingDevice.setTotpDeviceKey(totps.get("totpKey"));
                                existingDevice.setTotpDeviceId(totps.get("totpDeviceId"));
                                userDevice.setTotpDeviceId(totps.get("totpDeviceId"));
                                userDevice.setTotpDeviceKey(totps.get("totpKey"));
                            }
                        }
                    }
                    userDeviceList.add(existingDevice);
                }
                if(!deviceExists){
                    Map<String, String> totps = auth.generateTotp(userDevice.getDeviceId());
                    if (!CollectionUtils.isEmpty(totps)) {
                        userDevice.setTotpDeviceKey(totps.get("totpKey"));
                        userDevice.setTotpDeviceId(totps.get("totpDeviceId"));
                    }
                    logger.info("Adding Device [NEW]: " + userDevice.getDeviceId() + " to UID : " + appUser.getUid());
                    userDeviceList.add(userDevice);
                }
            } else {
                logger.info("Adding First Device : " + userDevice.getDeviceId() + " to UID : " + appUser.getUid());
                Map<String, String> totps = auth.generateTotp(userDevice.getDeviceId());
                    if (!CollectionUtils.isEmpty(totps)) {
                        userDevice.setTotpDeviceKey(totps.get("totpKey"));
                        userDevice.setTotpDeviceId(totps.get("totpDeviceId"));
                    }
                userDeviceList.add(userDevice);
                if(!isNewRegistration) {
                    userEventService.addUserDeviceEvent(appUser.getUid(), userDevice);
                }
            }
        }
        loginCreateAccountDTO.setUserDeviceList(userDeviceList);
        loginCreateAccountDTO.setCurrentDevice(userDevice);
        return loginCreateAccountDTO;
    }

    private void sendDataToKafka(String kafkaData) {
        Thread thread = new Thread(new Runnable(){

            @Override
            public void run() {
                logger.info("Adding duplicate uids event to kafka : " + kafkaData);
                String partitionKey = String.valueOf(Utils.getRandomNumber(MusicConstants.ADTECH_KAFKA_PARTITION) - 1) ;
                KeyedMessage<String, String> data = new
                        KeyedMessage<String, String>("uids", partitionKey, kafkaData);
                try{
                    kafkaProducerManager.send(data);
                } catch (Exception e){
                    logger.error("Exception in sending data to Adtech Targeting kafka topic: ", e);
                }
            }
        });
        executor.submit(thread);
    }

    private String getDateKey() {
        TimeZone timeZone = TimeZone.getTimeZone("IST");
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(new Date());
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.format(calendar.getTime());
    }

    public UserMobilityInfo getOperatorInfoDetail(Boolean isEnableNDS, String msisdn, MsisdnIdentificationResponse msisdnIdentificationResponse) throws Exception {
        if (msisdnIdentificationResponse != null &&
                msisdnIdentificationResponse.getData() != null && msisdnIdentificationResponse.getData().getOperatorDetails() != null) {
            return msisdnIdentificationResponse.getData().getOperatorDetails().getNdsUserInfo();
        } else if (isEnableNDS && StringUtils.isNotBlank(msisdn)) {
            NdsUserInfo ndsUserInfo = ndsUserInfoApiService.getNdsUserInfoFromWCFCache(msisdn);
            String ndsCircle = ndsUserInfo.getCircle();
            if (!StringUtils.isEmpty(ndsCircle) && !ndsCircle.equalsIgnoreCase("unknown")) {
                UserMobilityInfo wcfOperatorInfoFromNDS = new UserMobilityInfo();
                wcfOperatorInfoFromNDS.setCircle(ndsCircle);
                wcfOperatorInfoFromNDS.setOperator(AIRTEL);
                wcfOperatorInfoFromNDS.setUserType(ndsUserInfo.getUserType().name());
                return wcfOperatorInfoFromNDS;
            }
        }
        return new UserMobilityInfo();
    }

    public void getOperatorInfoFromMNCANDMCC(UserDevice userDevice, UserMobilityInfo wcfOperatorInfo) {
        Boolean isCircleDetectedFromMccMnc = Boolean.FALSE;
        String circleShortName = null;
        String operatorName = null;

        //Now App will be sending mcc,mnc  info .  use that info and get circle and operator name
        if (userDevice.getSimInfo() != null && userDevice.getSimInfo().size() > 0) {
            //In case of single sim phone
            if (userDevice.getSimInfo().size() == 1) {
                SimInfo simInfo = userDevice.getSimInfo().get(0);
                //operatorName = simInfo.getCarrier();
                String mcc = simInfo.getMcc();
                String mnc = simInfo.getMnc();
                if (StringUtils.isNotBlank(mnc) && StringUtils.isNotBlank(mcc)) {
                    OperatorCircle operatorCircleInfo = circleInfoMccMncCache.getOperatorCircleInfo(mcc + mnc);
                    if (operatorCircleInfo != null) {
                        circleShortName = operatorCircleInfo.getCirle().getCircleId();
                        operatorName = operatorCircleInfo.getOperator();
                        isCircleDetectedFromMccMnc = true;
                    }
                } else if (StringUtils.isNotBlank(simInfo.getImsiNumber())) {
                    //If mcc and mnc are not there then fall back to imsi
                    OperatorCircle operatorCircleInfo = circleInfoMccMncCache.getOperatorCircleInfoFromImsi(simInfo.getImsiNumber());
                    if (operatorCircleInfo != null) {
                        circleShortName = operatorCircleInfo.getCirle().getCircleId();
                        operatorName = operatorCircleInfo.getOperator();
                        isCircleDetectedFromMccMnc = true;
                    }
                }
            } else {
                //In case of dual sim phone check if mcc and mnc of both sim is same
                HashSet<String> mccSet = new HashSet<String>();
                HashSet<String> mncSet = new HashSet<String>();
                HashSet<String> imsiSet = new HashSet<String>();
                boolean isValidMccMncs = true;
                boolean isValidImsis = true;
                for (SimInfo simInfo : userDevice.getSimInfo()) {
                    if (StringUtils.isNotBlank(simInfo.getMcc()) && StringUtils.isNotBlank(simInfo.getMnc())) {
                        mccSet.add(simInfo.getMcc());
                        mncSet.add(simInfo.getMnc());
                    } else {
                        isValidMccMncs = false;
                    }

                    if (StringUtils.isNotBlank(simInfo.getImsiNumber())) {
                        imsiSet.add(simInfo.getImsiNumber());
                    } else {
                        isValidImsis = false;
                    }

                }
                //In case mcc and mnc of both sim is same use that mcc and mnc
                if (isValidMccMncs && mccSet.size() == 1 && mncSet.size() == 1) {
                    String mcc = (String) mccSet.toArray()[0];
                    String mnc = (String) mncSet.toArray()[0];
                    OperatorCircle operatorCircleInfo = circleInfoMccMncCache.getOperatorCircleInfo(mcc + mnc);
                    if (operatorCircleInfo != null) {
                        circleShortName = operatorCircleInfo.getCirle().getCircleId();
                        operatorName = operatorCircleInfo.getOperator();
                        isCircleDetectedFromMccMnc = true;
                    }
                } else if (isValidImsis && imsiSet.size() == 1) {
                    //if mcc and mnc are not valid then fall back to imsi
                    String imsi = (String) imsiSet.toArray()[0];
                    OperatorCircle operatorCircleInfo = circleInfoMccMncCache.getOperatorCircleInfoFromImsi(imsi);
                    if (operatorCircleInfo != null) {
                        circleShortName = operatorCircleInfo.getCirle().getCircleId();
                        operatorName = operatorCircleInfo.getOperator();
                        isCircleDetectedFromMccMnc = true;
                    }
                }
            }
        }

        if (isCircleDetectedFromMccMnc && wcfOperatorInfo != null) {
            wcfOperatorInfo.setCircleDetectedFromMccMnc(isCircleDetectedFromMccMnc);
            wcfOperatorInfo.setCircleShortName(circleShortName);
            //Commenting for now
            //wcfOperatorInfo.setOperator(operatorName);
        }
        //detect country from mcc . Ony first sim
        if (userDevice.getSimInfo() != null && userDevice.getSimInfo().size() > 0){
            SimInfo simInfo = userDevice.getSimInfo().get(0);
            String mcc = simInfo.getMcc();
            if(StringUtils.isNotEmpty(mcc)){
                if(mcc.equals("413") && wcfOperatorInfo != null){
                    wcfOperatorInfo.setCountryCodeMcc(Country.SRILANKA.getCountryCode());
                }
            }
        }
    }

    public String getOperatorFromMCCMNC(UserDevice userDevice) {
        logger.info("In getOperatorFromMCCMNC : userDevice is : " + userDevice);
        String operatorName = null;
        if (userDevice.getSimInfo() != null && !userDevice.getSimInfo().isEmpty()) {
            SimInfo simInfo = userDevice.getSimInfo().get(0);
            String mcc = simInfo.getMcc();
            String mnc = simInfo.getMnc();
            if (StringUtils.isNotBlank(mnc) && StringUtils.isNotBlank(mcc)) {
                OperatorCircle operatorCircleInfo = circleInfoMccMncCache.getOperatorCircleInfo(mcc + mnc);
                if (operatorCircleInfo != null) {
                    operatorName = operatorCircleInfo.getOperator();
                }
            }
        }
        if (operatorName != null) {
            if (operatorName.toLowerCase().contains("vodafone")) return "vodafone";
            else if (operatorName.toLowerCase().contains("idea")) return "idea";
            else if (operatorName.toLowerCase().contains("airtel")) return "airtel";
            else return operatorName.toLowerCase();
        }
        else return null;
    }

    public LoginCreateAccountDTO initialiseUserAppDetails(Boolean migrateDeviceUidData, LoginCreateAccountDTO loginCreateAccountDTO, Boolean isNewRegistration) {
        User appUser = loginCreateAccountDTO.getAppUser();

        if (migrateDeviceUidData && ChannelContext.getUser() != null) {

            if (isNewRegistration) {
                if (!CollectionUtils.isEmpty(ChannelContext.getUser().getContentLanguages())) {
                    appUser.setContentLanguages(ChannelContext.getUser().getContentLanguages());
                }
                if (!CollectionUtils.isEmpty(ChannelContext.getUser().getOnboardingLanguages())) {
                    appUser.setOnboardingLanguages(ChannelContext.getUser().getOnboardingLanguages());
                }
                // handling for basic
                if(!CollectionUtils.isEmpty(ChannelContext.getUser().getBasicContentLanguage())){
                    appUser.setBasicContentLanguage(ChannelContext.getUser().getBasicContentLanguage());
                }
                if(!CollectionUtils.isEmpty(ChannelContext.getUser().getBasicSelectedArtist())){
                    appUser.setBasicSelectedArtist(ChannelContext.getUser().getBasicSelectedArtist());
                }
                if(!CollectionUtils.isEmpty(ChannelContext.getUser().getBasicSelectedPlaylist())){
                    appUser.setBasicSelectedPlaylist(ChannelContext.getUser().getBasicSelectedPlaylist());
                }
                appUser.setBasicHasManuallySelectedLangauge(ChannelContext.getUser().isBasicHasManuallySelectedLangauge());
                appUser.setSongQuality(ChannelContext.getUser().getSongQuality());
                appUser.setLang(loginCreateAccountDTO.getApplang());
            }

            List<UserFavorite> listFavorites = new ArrayList<>();
            if (!CollectionUtils.isEmpty(appUser.getFavorites()) && !CollectionUtils.isEmpty(ChannelContext.getUser().getFavorites())) {
                List<UserFavorite> currentUserFavorites = ChannelContext.getUser().getFavorites();
                List<UserFavorite> appUserFavorites = appUser.getFavorites();
                Set<UserFavorite> favorites = new HashSet<>();
                favorites.addAll(currentUserFavorites);
                favorites.addAll(appUserFavorites);
                listFavorites.addAll(favorites);
                Collections.sort(listFavorites);
            } else if (!CollectionUtils.isEmpty(appUser.getFavorites())) {
                listFavorites.addAll(appUser.getFavorites());
            } else if (!CollectionUtils.isEmpty(ChannelContext.getUser().getFavorites())) {
                listFavorites.addAll(ChannelContext.getUser().getFavorites());
            }
            appUser.setFavorites(listFavorites);

            if (!isNewRegistration) {
                //accountService.updateFavoritesForUser(appUser,listFavorites);
            }

            List<UserPurchase> userPurchaseList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(appUser.getDownloads()) && !CollectionUtils.isEmpty(ChannelContext.getUser().getDownloads())) {
                List<UserPurchase> currentUserPurchase = ChannelContext.getUser().getDownloads();
                List<UserPurchase> appUserPurchases = appUser.getDownloads();

                Set<UserPurchase> purchases = new HashSet<>();
                purchases.addAll(currentUserPurchase);
                purchases.addAll(appUserPurchases);
                userPurchaseList.addAll(purchases);
                Collections.sort(userPurchaseList);
            } else if (!CollectionUtils.isEmpty(appUser.getRentals())) {
                userPurchaseList.addAll(appUser.getDownloads());
            } else if (!CollectionUtils.isEmpty(ChannelContext.getUser().getRentals())) {
                userPurchaseList.addAll(ChannelContext.getUser().getDownloads());
            }
            appUser.setDownloads(userPurchaseList);

            if (!isNewRegistration) {
                // accountService.updatePurchaseForUser(appUser,userPurchaseList);
            }
        }
        loginCreateAccountDTO.setAppUser(appUser);
        return loginCreateAccountDTO;
    }

    private static String getRedisDeviceIdKey(String uid) {
        return String.format(MusicConstants.REDIS_DEVICEID_HASH, getDeviceRedisBucketId(uid));
    }

    public UserSubscription reRegistration(String msisdnStr, WCFServiceType wcfServiceType) {
        return myAccountService.updateAndProvisionSubscription(msisdnStr, wcfServiceType);
    }

    private MsisdnIdentificationResponse getMsisdnIdentificationResponse(MsisdnIdentificationResponse wcfMsisdnIdentificationResponseBody,
        UserDevice userDevice, String xMsisdn) {
        MsisdnIdentificationRequest msisdnIdentificationRequest = MsisdnIdentificationRequest.builder()
            .appId(WCFApisConstants.APPID_MOBILITY)
            .appVersion(userDevice.getAppVersion())
            .deviceType(DeviceType.PHONE.name)
            .buildNo(userDevice.getBuildNumber())
            .os(userDevice.getOs())
            .deviceId(userDevice.getDeviceId())
            .dthCustID("")
            .service(WCFServiceType.WYNK_MUSIC.getServiceName())
            .xMsisdn(xMsisdn)
            .imsi(wcfUtils.getImsiInfo(userDevice)).build();

        if(StringUtils.isNotBlank(msisdnIdentificationRequest.getXMsisdn()) || !CollectionUtils.isEmpty(msisdnIdentificationRequest.getImsi())){
            wcfMsisdnIdentificationResponseBody = wcfApisService.getMsisdnResponse(msisdnIdentificationRequest);
        }
        return wcfMsisdnIdentificationResponseBody;
    }

    public void createUser(LoginCreateAccountDTO loginCreateAccountDTO, MusicPlatformType platform, Boolean migrateDeviceUidData, Boolean isNewRegistration, UserDevice userDevice, String msisdn) throws Exception {
        logger.info("Creating user ");
        loginCreateAccountDTO = initialiseUser(loginCreateAccountDTO, platform);
        if (!loginCreateAccountDTO.getAppUser().getUid().endsWith("1")) {
            loginCreateAccountDTO = initialiseDevice(loginCreateAccountDTO, migrateDeviceUidData, isNewRegistration);
            List<UserDevice> userDeviceList = loginCreateAccountDTO.getUserDeviceList();
            if (!CollectionUtils.isEmpty(userDeviceList)) {
                loginCreateAccountDTO.getAppUser().setDevices(userDeviceList);
            }
            loginCreateAccountDTO = initialiseUserAppDetails(migrateDeviceUidData, loginCreateAccountDTO, isNewRegistration);
            accountService.createUser(loginCreateAccountDTO.getAppUser());
            if (!MusicUtils.isSamsungDevice()) {
                //send SMS to download Wynk Movies for Android and iOS device
                if (StringUtils.isNotEmpty(msisdn) && msisdn.startsWith(Country.INDIA.getCountryCode())) {
                    sendWynkMoviesSMS(msisdn);
                }
            }
            if (migrateDeviceUidData && ChannelContext.getUser() != null) {
                musicService.updateUserPlaylistToNewUser(ChannelContext.getUid(), loginCreateAccountDTO.getAppUser().getUid());
                accountService.purgeData(migrateDeviceUidData, loginCreateAccountDTO);
            }

            HashMap<String, String> logExtras = new HashMap();
            logExtras.put("status", "registering new user successful");
            LogstashLoggerUtils.createStandardLog("MUSIC_REGISTRATION", loginCreateAccountDTO.getAppUser().getUid(), msisdn, userDevice.getDeviceId(), logExtras);

            accountService.sendEventOnRegistration(loginCreateAccountDTO.getAppUser(), userDevice);
        }
        logger.info("exiting Creating user ");
    }

    public void removeAndUpdateDeviceIDUIDMapping(UserDevice userDevice, User appUser, String uuid) {
        // Remove device from other UIDs and assign device to current UID except in case of Samsung SDK - platform=5
        try {
            if (userPersistantRedisServiceManager != null && StringUtils.isNotBlank(userDevice.getDeviceId()) && appUser.getPlatform() != 5) {
                String currentUidForThisDevice = UserDeviceUtils.generateUUID(null, userDevice.getDeviceId(), null, MusicPlatformType.WYNK_DEVICE_BASED);
                String uidFromCache = userPersistantRedisServiceManager.hget(getRedisDeviceIdKey(userDevice.getDeviceId()), userDevice.getDeviceId());
                if (StringUtils.isNotBlank(uidFromCache))
                    currentUidForThisDevice = uidFromCache;

                Boolean previousUserRegistered = Boolean.FALSE;
                if (!currentUidForThisDevice.endsWith("2"))
                    previousUserRegistered = Boolean.TRUE;

                if (!StringUtils.equalsIgnoreCase(appUser.getUid(), currentUidForThisDevice)) {
                    // FOUND EXISTING UID
                    logger.info("Removing Device : " + userDevice.getDeviceId() + " from existing uid : " + currentUidForThisDevice);
                    accountService.removeDeviceForUidAndPurgeUser(userDevice, currentUidForThisDevice, previousUserRegistered);
                    userEventService.addUserDeviceRemovedEvent(currentUidForThisDevice, userDevice);
                }
                Boolean currentUserRegistered = Boolean.FALSE;
                if (appUser.getUid().endsWith("0"))
                    currentUserRegistered = Boolean.TRUE;

                if (currentUserRegistered) {
                    logger.info("Setting in persistent redis - key = " + userDevice.getDeviceId() + ", Value = " + appUser.getUid());
                    userPersistantRedisServiceManager.hset(getRedisDeviceIdKey(userDevice.getDeviceId()), userDevice.getDeviceId(), appUser.getUid());
                }
                else{
                    userPersistantRedisServiceManager.hdel(getRedisDeviceIdKey(userDevice.getDeviceId()), userDevice.getDeviceId());
                }
            }
        } catch (Exception e) {
            logger.error("Error removing device from existing user ", e.getMessage(), e);
            LogstashLoggerUtils.createFatalExceptionLogWithMessage(e,
                    ExceptionTypeEnum.INFRA.REDIS.name(),
                    "",
                    "AccountService.findOrCreate",
                    "Error in getting data from userRedisServiceManager");
        }
    }

    public String getUniqueMsisdnFromDeviceId(String deviceId) {
        String uniqueUUid = null;

        if (!MusicBuildUtils.isChangeNumberBuildSupported()) {
            return null;
        }

        try {
            uniqueUUid = userPersistantRedisServiceManager.hget(getRedisDeviceIdKey(deviceId), deviceId);
        } catch (Exception e) {
            logger.error("Error in getting data from userRedisServiceManager", e);
            LogstashLoggerUtils.createFatalExceptionLogWithMessage(e,
                    ExceptionTypeEnum.INFRA.REDIS.name(),
                    "",
                    "AccountService.findOrCreate",
                    "Error in getting data from userRedisServiceManager");
        }

        if (uniqueUUid == null) {
            return null;
        }

        User user = accountService.getUserFromUid(uniqueUUid);
        if (user == null || StringUtils.isBlank(user.getMsisdn())) {
            return null;
        }

        Long lastInactiveTime = user.getLastActivityDate();
        Long currentTime = System.currentTimeMillis();

        if (currentTime > ((MusicConstants.LAST_INACTIVE_IN_SECONDS * 1000) + lastInactiveTime)) {
            return null;
        }

        List<UserDevice> userDeviceList = user.getDevices();
        if (!CollectionUtils.isEmpty(userDeviceList)) {
            if (userDeviceList.size() == 1) {
                UserDevice userDevice = userDeviceList.get(0);
                if (userDevice.getDeviceId().equals(deviceId)) {
                    return user.getMsisdn();
                }
            }
        }

        return null;
    }

    public String updateAndGetUserCountryIfNotExist(User appUser, boolean isDbAndCacheRefreshNeeded) {
        String updatedCountry = null;
        String normalizeMsisdnToCheck = Utils.normalizePhoneNumber(appUser.getMsisdn());
        if (appUser != null && StringUtils.isBlank(appUser.getCountryId()) && Objects.nonNull(normalizeMsisdnToCheck) && normalizeMsisdnToCheck.length() > 3
                && appUser.getUid() != null &&  appUser.getUid().endsWith("0")) {
            Country country = getCountryByCountryCode(normalizeMsisdnToCheck.substring(0, 3));
            appUser.setCountryId(country.getCountryId());
            updatedCountry = country.getCountryId();

            if (isDbAndCacheRefreshNeeded) {
                boolean isUpdatedInDb = accountService.updateUserCountryInDB(appUser.getUid(), country.getCountryId());
                if (isUpdatedInDb) {
                    logger.info("Country updated to {} for user id {} from null", updatedCountry, appUser.getUid());
                    accountService.updateUserInCache(appUser.getUid(), false);
                }
            }

        }
        return updatedCountry;
    }
}
