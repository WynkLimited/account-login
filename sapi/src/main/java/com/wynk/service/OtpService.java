package com.wynk.service;

import com.wynk.common.ExceptionTypeEnum;
import com.wynk.constants.Constants;
import com.wynk.constants.JsonKeyNames;
import com.wynk.constants.MusicConstants;
import com.wynk.db.ShardedRedisServiceManager;
import com.wynk.dto.OtpResult;
import com.wynk.server.ChannelContext;
import com.wynk.utils.LogstashLoggerUtils;
import com.wynk.utils.MusicUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Aakash on 07/07/17.
 */
@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class.getCanonicalName());

    private static final String REDIS_PIN_KEY_PREFIX = "pin_";
    private static final String REDIS_PIN_KEY_PREFIX_FOUR_DIGIT = "4pin_";
    private static final String REDIS_PIN_DELETE_KEY_PREFIX_FOUR_DIGIT = "del_4pin_";
    private static final String REDIS_OTP_ATTEMPTS_KEY = "otpgencount_";
    private static final String REDIS_DELETE_OTP_ATTEMPTS_KEY = "otpgendeletecount_";

    @Autowired
    private ShardedRedisServiceManager userPersistantRedisServiceManager;

    private static final int PIN_EXPIRY_TIME=900; //# 15*60, ie 15 mins
    private static final int MAX_VALIDATION_COUNT=5;
    private static final int CLIENT_OTP_INTERVAL_SECS = 30;
    /**
     *We want to limit user to get otp inside a span of 5 mins
     */
    private static final int OTP_WINDOW_SIZE_MINS = 5;

    private static final int OTP_WINDOW_RATE_LIMIT = (OTP_WINDOW_SIZE_MINS * (60/CLIENT_OTP_INTERVAL_SECS)) + 2;

    static JSONObject smsJsonObject = new JSONObject();

    public static int getPinExpiryTimeMinutes() {
        return PIN_EXPIRY_TIME/60;
    }

    public static int getClientPerMinuteOtpCount() {
        return 60/CLIENT_OTP_INTERVAL_SECS;
    }

    static {

        smsJsonObject.put(JsonKeyNames.OTP_SMS_START_INDEX,26);
        smsJsonObject.put(JsonKeyNames.OTP_SMS_END_INDEX,32);
    }
    /**
     * Method to generate otp.
     * @return
     */
    public OtpResult generateOtp(String msisdn, boolean isFourDigitPin, boolean isCall) {

        Integer retryCount = 0;
        Integer otpRequestCount = 0;
        long now = System.currentTimeMillis();
        String redisHashPrefix = REDIS_PIN_KEY_PREFIX;
        if(isFourDigitPin)
            redisHashPrefix = REDIS_PIN_KEY_PREFIX_FOUR_DIGIT;

        String userId = ChannelContext.getUid();
        String otp = null;
        String otpGenAttemptCount = null;
        try {
            otpGenAttemptCount = userPersistantRedisServiceManager.get(REDIS_OTP_ATTEMPTS_KEY+userId);
            otp = userPersistantRedisServiceManager.get(redisHashPrefix+msisdn);
        } catch (Exception e) {
            logger.error("Error while reading OTP in AccountService.generateOtp from userRedisServiceManager",e);
            LogstashLoggerUtils.createFatalExceptionLogWithMessage(e, ExceptionTypeEnum.INFRA.REDIS.name(), "",
                    "AccountService.generateOtp", "Error while reading OTP from userRedisServiceManager");
            return null;
        }

        if (StringUtils.isEmpty(otpGenAttemptCount)) {
            userPersistantRedisServiceManager.setex(
                REDIS_OTP_ATTEMPTS_KEY + userId, "1", MusicConstants.MAX_OTP_ATTEMPTS_EXPIRY);
        } else {
            int attempts = Integer.parseInt(otpGenAttemptCount);
            if (attempts == OTP_WINDOW_RATE_LIMIT) {
                return null;
        } else {
        Long ttlInSec = userPersistantRedisServiceManager.ttl(REDIS_OTP_ATTEMPTS_KEY + userId);
        Integer expirationTimeLeftInSec =
            (ttlInSec != null && ttlInSec > 0) ? ttlInSec.intValue() : -1;
        if (expirationTimeLeftInSec > 0) {
          userPersistantRedisServiceManager.setex(
              REDIS_OTP_ATTEMPTS_KEY + userId, String.valueOf(++attempts), expirationTimeLeftInSec);
        }
      }
    }
        long smsTimestamp = now;
        long callTimestamp = now;

        if(StringUtils.isEmpty(otp)) {
            //generate new
            Random rnd = new Random();
            if(isFourDigitPin)
                otp = ""+(1000 + rnd.nextInt(9000));
            else
                otp = ""+(100000 + rnd.nextInt(900000));
        }
        else
        {
            String[] otpRedisEntries = otp.split(":");
            // For old OTP's
            if (otpRedisEntries.length == 3) {
                otp =  otpRedisEntries[0];
                smsTimestamp = Long.parseLong(otpRedisEntries[1]);
                callTimestamp = Long.parseLong(otpRedisEntries[2]);

                long timestamp = smsTimestamp;
                long buffer = CLIENT_OTP_INTERVAL_SECS * 1000L;
                if (isCall) {
                    timestamp = callTimestamp;
                    buffer = CLIENT_OTP_INTERVAL_SECS * 1000L;
                }

                if ((now - timestamp) <= buffer) {
                    logger.info("OTP call within 15 seconds - "+otp);
                    return null;
                }
            }
            else if(otpRedisEntries.length == 4 || otpRedisEntries.length == 5 || otpRedisEntries.length == 6){
                otp =  otpRedisEntries[0];
                smsTimestamp = Long.parseLong(otpRedisEntries[1]);
                callTimestamp = Long.parseLong(otpRedisEntries[2]);
                retryCount = Integer.parseInt(otpRedisEntries[3]);
                if (otpRedisEntries.length == 6) {
                    otpRequestCount = Integer.parseInt(otpRedisEntries[5]);
                }

                long timestamp = smsTimestamp;
                long buffer = CLIENT_OTP_INTERVAL_SECS * 1000L;
                if (isCall) {
                    timestamp = callTimestamp;
                    buffer = CLIENT_OTP_INTERVAL_SECS * 1000L;
                }

                if ((now - timestamp) <= buffer) {
                    logger.info("OTP call within {} seconds - {}", CLIENT_OTP_INTERVAL_SECS, otp);
                    return null;
                }
            }
            logger.info("OTP found - "+otp);
            retryCount++;
        }
        otpRequestCount++;
        if (isCall) {
            callTimestamp = now;
        } else {
            smsTimestamp = now;
        }

        return setOtpInRedis(otp,msisdn,redisHashPrefix,PIN_EXPIRY_TIME,smsTimestamp,callTimestamp,retryCount,otpRequestCount,0);
    }

    private OtpResult setOtpInRedis(String otp,String msisdn,String redisHashPrefix, int pinExpiry ,long smsTimestamp, long callTimestamp, int retryCount, int otpRequestCount,int validationCount){
        String otpRedisEntryFormat = "<OTP>:<SMS_TIMESTAMP>:<CALL_TIMESTAMP>:<RETRY_COUNT>:<VALIDATION_COUNT>:<OTP_REQUEST_COUNT>";
        String otpRedisEntry = otpRedisEntryFormat;
        otpRedisEntry = otpRedisEntry.replace("<OTP>", otp);
        otpRedisEntry = otpRedisEntry.replace("<SMS_TIMESTAMP>", String.valueOf(smsTimestamp));
        otpRedisEntry = otpRedisEntry.replace("<CALL_TIMESTAMP>", String.valueOf(callTimestamp));
        otpRedisEntry = otpRedisEntry.replace("<RETRY_COUNT>",String.valueOf(retryCount));
        otpRedisEntry = otpRedisEntry.replace("<OTP_REQUEST_COUNT>",String.valueOf(otpRequestCount));
        otpRedisEntry = otpRedisEntry.replace("<VALIDATION_COUNT>",String.valueOf(validationCount));
        Long ttlInSec = userPersistantRedisServiceManager.ttl(redisHashPrefix+msisdn);
        Integer expirationTimeLeftInSec = (ttlInSec != null && ttlInSec > 0) ? ttlInSec.intValue() : pinExpiry;
        try {
            userPersistantRedisServiceManager.setex(redisHashPrefix+msisdn,otpRedisEntry,expirationTimeLeftInSec);
        } catch (Exception e) {
            logger.error("Error while setting OTP in AccountService.generateOtp from userRedisServiceManager",e);
            LogstashLoggerUtils.createFatalExceptionLogWithMessage(e, ExceptionTypeEnum.INFRA.REDIS.name(), "",
                    "AccountService.generateOtp", "Error while setting OTP from userRedisServiceManager");
            return null;
        }
        return new OtpResult(otp,retryCount,otpRequestCount);
    }

    public String getPin(String msisdn, boolean isFourDigitPin, String intent) {
        String pinPrefix = REDIS_PIN_KEY_PREFIX;
        if (isFourDigitPin)
            pinPrefix = REDIS_PIN_KEY_PREFIX_FOUR_DIGIT;
        if (intent.equalsIgnoreCase(Constants.DELETE)) {
            pinPrefix = REDIS_PIN_DELETE_KEY_PREFIX_FOUR_DIGIT;
        }
        String redisKey = pinPrefix + msisdn;
        String storedPin = null;
        try {
            storedPin = userPersistantRedisServiceManager.get(redisKey);
        } catch (Exception e) {
            logger.error("Error while reading OTP in AccountService.getPin from userRedisServiceManager",e);
            LogstashLoggerUtils.createFatalExceptionLogWithMessage(e, ExceptionTypeEnum.INFRA.REDIS.name(), "",
                    "AccountService.getPin", "Error while reading OTP from userRedisServiceManager");
            return null;
        }

        if (storedPin != null) {
            String[] otpRedisEntries = storedPin.split(":");
            // For old OTP's
            if (otpRedisEntries.length == 3 || otpRedisEntries.length == 4) {
                storedPin = otpRedisEntries[0];
            }
            if (otpRedisEntries.length == 5 || otpRedisEntries.length == 6) {
                storedPin = otpRedisEntries[0];
                // Validation check for brute force attack
               int  validationCount =  NumberUtils.toInt(otpRedisEntries[4],0);
                validationCount++;
               if(validationCount >= MAX_VALIDATION_COUNT ){
                   logger.info("Validation count exceeded so expiring otp !!!");
                   //userPersistantRedisServiceManager.delete(redisKey);
                   return null;
                }else{
                   updateValidationCount(redisKey,otpRedisEntries,msisdn,pinPrefix,validationCount);
               }
            }
        }
        return storedPin;
    }

    public void expireOtp(String msisdn, boolean isFourDigitPin, String intent) {
        String pinPrefix = REDIS_PIN_KEY_PREFIX;
        if (isFourDigitPin)
            pinPrefix = REDIS_PIN_KEY_PREFIX_FOUR_DIGIT;
        if (intent.equalsIgnoreCase(Constants.DELETE)) {
            pinPrefix = REDIS_PIN_DELETE_KEY_PREFIX_FOUR_DIGIT;
        }
        String redisKey = pinPrefix + msisdn;
        userPersistantRedisServiceManager.delete(redisKey);
        logger.info("Expiring otp : {}",msisdn);
    }

    private void updateValidationCount(String redisKey,String[] otpRedisEntries,String msisdn,String pinPrefix,int validationCount){
        try {
            logger.info("Updating validation count {} of otp for msisdn {} ", validationCount, msisdn);
            long smsTimestamp = NumberUtils.toLong(otpRedisEntries[1], 0L);
            long callTimestamp = NumberUtils.toLong(otpRedisEntries[2], 0L);
            int retryCount = NumberUtils.toInt(otpRedisEntries[3], 0);
            int otpRequestCount = 0;
            if (otpRedisEntries.length == 6) {
                otpRequestCount = NumberUtils.toInt(otpRedisEntries[5], 0);
            }
            int pinExpiryInSecs = PIN_EXPIRY_TIME;
            Long ttlInSec = userPersistantRedisServiceManager.ttl(redisKey);
            Integer expirationTimeLeftInSec = (ttlInSec != null && ttlInSec > 0) ? ttlInSec.intValue() : PIN_EXPIRY_TIME;
            logger.info("Updating values to : pinExpiryInSecs  : {}, smsTimestamp : {}, callTimestamp : {} ,retryCount : {}, validationCount : {} ", pinExpiryInSecs,smsTimestamp,callTimestamp, retryCount, validationCount);
            setOtpInRedis(otpRedisEntries[0], msisdn, pinPrefix, expirationTimeLeftInSec, smsTimestamp, callTimestamp, retryCount, otpRequestCount, validationCount);
        }catch (Exception e){
            logger.error("Error while updating otp validation count",e);
        }
    }

    public boolean validatePin(String msisdn,String pin, String storedPin)
    {
        if(MusicUtils.isTestSIM(msisdn))
            return true;

        if(storedPin.equalsIgnoreCase(pin))
        {
            // TODO - Remove this log line
            logger.info("SMS FUNNEL - OTP validation success for msisdn : {} , pin : {}",msisdn,pin);
            return true;
        }
        else
        {
            // TODO - Remove this log line
            logger.info("SMS FUNNEL - OTP validation failure for msisdn : {} , entered pin : {} , stored pin : {}",msisdn,pin,storedPin);
        }

        return false;

    }

    public static JSONObject getPinIndex(){
        return smsJsonObject;
    }

    public OtpResult generateDeleteOtp(String msisdn, boolean isFourDigitPin, boolean isCall) {
        validateUserMsisdn(msisdn);
        OtpResult otpResult = null;
        String deleteOtp = null;
        String userId = ChannelContext.getUid();
        long clockStart = System.currentTimeMillis();
        String otpDeleteGenAttemptCount = null;
        String redisDeleteHashPrefix = REDIS_PIN_DELETE_KEY_PREFIX_FOUR_DIGIT;
        try {
            otpDeleteGenAttemptCount = getValueFromDb(REDIS_DELETE_OTP_ATTEMPTS_KEY, userId);
            deleteOtp = getValueFromDb(redisDeleteHashPrefix, msisdn);
            if (isOtpAttemptsOver(userId, otpDeleteGenAttemptCount, REDIS_DELETE_OTP_ATTEMPTS_KEY)) return null;
            otpResult = generateAndSetOtpInDB(deleteOtp, clockStart, isCall, isFourDigitPin, msisdn, redisDeleteHashPrefix);
        } catch (Exception e) {
            logger.error("Error while generateDeleteOtp for msisdn : {}, fourDigitPin : {}, isCall : {} ", msisdn, isFourDigitPin, isCall, e);
            LogstashLoggerUtils.createFatalExceptionLogWithMessage(e, ExceptionTypeEnum.CODE.name(), "",
                    "OtpService.setValueInDb", "Error while generating delete otp from generateDeleteOtp");
        }
        return otpResult;
    }

    private void validateUserMsisdn(String msisdn) {
        if(msisdn != null && msisdn.equalsIgnoreCase(ChannelContext.getMsisdn()))
        {
            return;
        }
        throw new RuntimeException("Incorrect msisdn found");
    }

    private OtpResult generateAndSetOtpInDB(String deleteOtp, long startedClock, boolean isCall, boolean isFourDigitPin, String msisdn, String redisDeleteHashPrefix) {
        long smsTimestamp = startedClock;
        long callTimestamp = startedClock;
        Integer retryCount = 0;
        Integer otpDeleteRequestCount = 0;

        if (StringUtils.isBlank(deleteOtp)) {
            Random rnd = new Random();
            deleteOtp = isFourDigitPin ? "" + (1000 + rnd.nextInt(9000)) : "" + (100000 + rnd.nextInt(900000));
        } else {
            String[] otpRedisEntries = deleteOtp.split(":");
            logger.info("OtpRedisEntries found from db {}", Arrays.toString(otpRedisEntries));
            deleteOtp = otpRedisEntries[0];
            smsTimestamp = Long.parseLong(otpRedisEntries[1]);
            callTimestamp = Long.parseLong(otpRedisEntries[2]);
            retryCount = Integer.parseInt(otpRedisEntries[3]);
            otpDeleteRequestCount = Integer.parseInt(otpRedisEntries[5]);
            boolean isAllowed = isOtpRequestCallValid(deleteOtp, smsTimestamp, callTimestamp, isCall, startedClock);
            if (!isAllowed) return null;
            logger.info("Prepared <DELETE Otp> is {} ", deleteOtp);
            retryCount++;
        }
        otpDeleteRequestCount++;
        if (isCall) {
            callTimestamp = startedClock;
        } else {
            smsTimestamp = startedClock;
        }
        return setOtpInRedis(deleteOtp, msisdn, redisDeleteHashPrefix, PIN_EXPIRY_TIME, smsTimestamp, callTimestamp, retryCount, otpDeleteRequestCount, 0);
    }

    private boolean isOtpRequestCallValid(String deleteOtp, long smsTimestamp, Long callTimestamp, Boolean isCall, long startClock) {
        long timestamp = 0L;
        long buffer = 0L;
        if (isCall) {
            timestamp = callTimestamp;
            buffer = CLIENT_OTP_INTERVAL_SECS * 1000L;
        } else {
            timestamp = smsTimestamp;
            buffer = CLIENT_OTP_INTERVAL_SECS * 1000L;
        }

        logger.info("Found timestamp are buffer :  {} , timestamp : {} , clock start : {} , isCall : {}", buffer, timestamp, startClock, isCall);

        if ((startClock - timestamp) <= buffer) {
            logger.info("OTP call within 15 seconds - " + deleteOtp);
            return false;
        }
        logger.info("Delete Otp's validated successfully");
        return true;
    }

    private boolean isOtpAttemptsOver(String userId, String otpGenAttemptCount, String redisKey) {
        if (StringUtils.isEmpty(otpGenAttemptCount)) {  // first time generation
            setValueInDbWithExpiryTime(redisKey + userId, "1", MusicConstants.MAX_OTP_ATTEMPTS_EXPIRY);
        } else {
            int attempts = Integer.parseInt(otpGenAttemptCount);
            if (attempts == OTP_WINDOW_RATE_LIMIT) {         //  max attempts reached
                return true;
            } else {
                Long fetchedValue = getTTL(redisKey + userId); // if ttl alive if yes then set with time
                Integer expiryTimeOfKey =
                        (fetchedValue != null && fetchedValue > 0) ? fetchedValue.intValue() : -1;
                if (expiryTimeOfKey > 0) {
                    setValueInDbWithExpiryTime(redisKey + userId, String.valueOf(++attempts), expiryTimeOfKey);
                }
            }
        }
        return false;
    }

    private String getValueFromDb(String key, String suffix) {
        String value = null;
        try {
            value = userPersistantRedisServiceManager.get(key+suffix);
        } catch (Exception e) {
            logger.error("Error while reading for key : {} , suffix : {}  in OtpService.getValueFromDb from userRedisServiceManager", key, suffix, e);
            LogstashLoggerUtils.createFatalExceptionLogWithMessage(e, ExceptionTypeEnum.INFRA.REDIS.name(), "",
                    "OtpService.getValueFromDb", "Error while reading from userRedisServiceManager");
        }
        return value;
    }

    public void setValueInDbWithExpiryTime(String key, String value, int expirationTime) {
        try {
            userPersistantRedisServiceManager.setex(key, value, expirationTime);
        } catch (Exception e) {
            logger.error("Error while setting for key : {} , value : {} , expirationTime : {}  in OtpService.setValueInDb from userRedisServiceManager", key, value, expirationTime, e);
            LogstashLoggerUtils.createFatalExceptionLogWithMessage(e, ExceptionTypeEnum.INFRA.REDIS.name(), "",
                    "OtpService.setValueInDb", "Error while reading from userRedisServiceManager");
        }
    }

    public Long getTTL(String key) {
        Long ttlOfKey = null;
        try {
            ttlOfKey = userPersistantRedisServiceManager.ttl(key);
        } catch (Exception e) {
            logger.error("Error in reading <TTL> for key : {} in OtpService.getTTL from userRedisServiceManager", key, e);
            LogstashLoggerUtils.createFatalExceptionLogWithMessage(e, ExceptionTypeEnum.INFRA.REDIS.name(), "",
                    "OtpService.setValueInDb", "Error while reading from userRedisServiceManager");
        }
        return ttlOfKey;
    }
}
