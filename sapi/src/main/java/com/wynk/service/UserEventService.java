package com.wynk.service;

import com.wynk.adtech.AdUtils;
import com.wynk.common.UserEventType;
import com.wynk.config.MusicConfig;
import com.wynk.constants.JsonKeyNames;
import com.wynk.constants.MusicConstants;
import com.wynk.dto.ThirdPartyNotifyDTO;
import com.wynk.server.ChannelContext;
import com.wynk.user.dto.UserActivityEvent;
import com.wynk.user.dto.UserDevice;
import com.wynk.utils.EncryptUtils;
import com.wynk.utils.MusicUtils;
import com.wynk.utils.Utils;
import com.wynk.wcf.WCFApisUtils;
import com.wynk.wcf.dto.FeatureType;
import com.wynk.wcf.dto.UserSubscription;
import com.wynk.wcf.dto.UserSubscription.ProductMeta;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class UserEventService {
	
    private static final Logger mactivityLogger               = LoggerFactory.getLogger("mactivityanalytics");
    private static final Logger eventServiceLogger            = LoggerFactory.getLogger("eventservicelog");
    private static final Logger moengageServiceLogger         = LoggerFactory.getLogger("moengageservicelog");
	private static final Logger logger               = LoggerFactory
            .getLogger(UserEventService.class.getCanonicalName());

    ExecutorService executor = Executors.newFixedThreadPool(10);
    public ExecutorService mainExecutorService             = null;


    @Autowired
    private MusicService musicService;
    
    @Autowired
    private MusicConfig config;
    
    @Autowired
    private KafkaProducer kafkaProducerManager;

    @Autowired
    private WCFApisUtils wcfApisUtils;

    @PostConstruct
    public void init() {
        try {
            mainExecutorService = new ThreadPoolExecutor(2, 10, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(10000), new ThreadPoolExecutor.DiscardOldestPolicy());
        }
        catch (Exception e) {
            logger.info("Error loading executorService : ", e.getMessage(), e);
        }
    }
	
	public void addUserCreationEvent(String uid) {
		 UserActivityEvent event = new UserActivityEvent();
	        event.setType(UserEventType.NEW_USER_NOT_REGISTERED);
	        event.setTimestamp(System.currentTimeMillis());
	        event.setUid(uid);
	        if(ChannelContext.getUser() != null ){
	            event.setLang(ChannelContext.getUser().getLang());
	        }
	        if(!MusicUtils.isDevEnv(ChannelContext.getRequest())) {
	            if (event != null) 
	            {
	                String eventLog = musicService.createEventLog(event, ChannelContext.getRequest());
	                sendDataToKafka(eventLog);
	            }
	        }
    }
    
    public void addUserDeviceEvent(String uid, UserDevice device) {
          UserActivityEvent event = new UserActivityEvent();
          event.setType(UserEventType.USER_DEVICE_ADD);
          event.setTimestamp(System.currentTimeMillis());
          JSONObject meta = new JSONObject();
          try {
          	meta.put(JsonKeyNames.DEVICEID, device.getDeviceId());
          	meta.put(JsonKeyNames.OPERATOR, device.getOperator());
          	meta.put(JsonKeyNames.OS, device.getOs());
          	meta.put(JsonKeyNames.DEVICETYPE, device.getDeviceType() );
          	meta.put(JsonKeyNames.DEVICEKEY, device.getDeviceKey());
          	meta.put(JsonKeyNames.DEVICE_RESOLUTION, device.getResolution());
          	meta.put(JsonKeyNames.DEVICE_REGIS_DATE, device.getRegistrationDate());
          	meta.put(JsonKeyNames.DEVICE_IMEI, device.getImeiNumber());
          	meta.put(JsonKeyNames.DEVICE_AD_ID, device.getAdvertisingId() );
          	meta.put(JsonKeyNames.DEVICE_IS_ACTIVE, device.isActive() );
          	meta.put(JsonKeyNames.DEVICE_APP_VER, device.getAppVersion() );
          	meta.put(JsonKeyNames.DEVICE_BUILD_NOS, device.getBuildNumber() );
          	meta.put(JsonKeyNames.DEVICE_LAST_UPDATE, device.getDeviceKeyLastUpdateTime() );
  		} catch (Exception e) {
  			logger.error("Error while encrypting msisdn");
  		}
          event.setMeta(meta);
          if(!MusicUtils.isDevEnv(ChannelContext.getRequest())) {
              if (event != null) //If Valid event is there, then only log it. In few cases event is null and NPE is there
              {
                  String eventLog = musicService.createEventLog(event, uid, device, ChannelContext.getRequest());
                  sendDataToKafka(eventLog);
              }
          }
    }
    
    public void addUserDeviceRemovedEvent(String uid, UserDevice device) {
          UserActivityEvent event = new UserActivityEvent();
          event.setType(UserEventType.USER_DEVICE_REMOVED);
          event.setTimestamp(System.currentTimeMillis());
          event.setUid(uid);
          JSONObject meta = new JSONObject();
          try {
          	meta.put(JsonKeyNames.DEVICEID, device.getDeviceId());
          	meta.put(JsonKeyNames.EXISTING_UID, uid);
  		} catch (Exception e) {
  			logger.error("Error while encrypting msisdn");
  		}
          event.setMeta(meta);
          if(!MusicUtils.isDevEnv(ChannelContext.getRequest())) {
              if (event != null) //If Valid event is there, then only log it. In few cases event is null and NPE is there
              {
                  String eventLog = musicService.createEventLog(event, uid, device, ChannelContext.getRequest());
                  sendDataToKafka(eventLog);
              }
          }
    }
    
    public void addUserUpdateEvent(String uid,String did, String attribute, String updatedValue) {
          UserActivityEvent event = new UserActivityEvent();
          event.setType(UserEventType.USER_PROFILE_UPDATE);
          event.setTimestamp(System.currentTimeMillis());
          JSONObject meta = new JSONObject();
          try {
          	meta.put(attribute, updatedValue);
          	meta.put(JsonKeyNames.DEVICEID, did);
  		} catch (Exception e) {
  			logger.error("Error while encrypting msisdn");
  		}
          event.setMeta(meta);
          if(!MusicUtils.isDevEnv(ChannelContext.getRequest())) {
              if (event != null) //If Valid event is there, then only log it. In few cases event is null and NPE is there
              {
                  String eventLog = musicService.createEventLog(event, ChannelContext.getRequest());
                  sendDataToKafka(eventLog);
              }
          }
    }
    
    public void addUserDeviceUpdateEvent(String uid,UserDevice device, String attribute, String updatedValue) {
        UserActivityEvent event = new UserActivityEvent();
        event.setType(UserEventType.USER_DEVICE_UPDATE);
        event.setTimestamp(System.currentTimeMillis());
        JSONObject meta = new JSONObject();
        try {
        	meta.put(attribute, updatedValue);
        	meta.put(JsonKeyNames.DEVICEID, device.getDeviceId());
		} catch (Exception e) {
			logger.error("Error while encrypting msisdn");
		}
        event.setMeta(meta);
        if(!MusicUtils.isDevEnv(ChannelContext.getRequest())) {
            if (event != null) //If Valid event is there, then only log it. In few cases event is null and NPE is there
            {
                String eventLog = musicService.createEventLog(event, uid, device, ChannelContext.getRequest());
                sendDataToKafka(eventLog);
            }
        }
    }
    
    public void addRegisteredEvent()
    {
        // Registered event
        UserActivityEvent event = new UserActivityEvent();
        event.setType(UserEventType.REGISTERED);
        event.setTimestamp(System.currentTimeMillis());
        if(ChannelContext.getUser() != null ){
            event.setLang(ChannelContext.getUser().getLang());
        }
        JSONObject meta = new JSONObject();
        try {
			meta.put(JsonKeyNames.MSISDN, EncryptUtils.encrypt(Utils.normalizePhoneNumber(ChannelContext.getMsisdn()),
                    config.getEncryptionKey()));
		} catch (Exception e) {
			logger.error("Error while encrypting msisdn");
		}
        event.setMeta(meta);
        if(!MusicUtils.isDevEnv(ChannelContext.getRequest())) {
            if (event != null) //If Valid event is there, then only log it. In few cases event is null and NPE is there
            {
                String eventLog = musicService.createEventLog(event, ChannelContext.getRequest());
                sendDataToKafka(eventLog);
                mactivityLogger.info(eventLog);
            }
        }
    }

    public void addSubscriptionTypeEvent(String uid, UserSubscription userSubscription, Boolean cron){
        UserActivityEvent event = new UserActivityEvent();
        event.setType(UserEventType.USER_SUBSCRIPTION_TYPE);
        event.setTimestamp(System.currentTimeMillis());
        JSONObject completeMeta = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        List<ProductMeta> productMetasProdIds = new LinkedList<>();
        productMetasProdIds.addAll(userSubscription.getProdIds());
        try{
          for (ProductMeta productMeta : productMetasProdIds) {
            JSONObject meta = new JSONObject();
            meta.put(JsonKeyNames.PRODUCT_ID, productMeta.getProdId());
            meta.put(JsonKeyNames.EXPIRE_TIMESTAMP,productMeta.getEts());
            meta.put(JsonKeyNames.IS_PREMIUM_ACCOUNT, AdUtils.isPremiumAccount(userSubscription,wcfApisUtils.getFeature(
                FeatureType.DOWNLOADS,userSubscription)));
            jsonArray.add(meta);
          }

          completeMeta.put(JsonKeyNames.PRODUCTS_META,jsonArray);
        }catch (Exception ex){
            logger.error("Exception in User Subscription Event for the uid {}",uid);
        }
        event.setMeta(completeMeta);
        if(!cron) {
            if (!MusicUtils.isDevEnv(ChannelContext.getRequest())) {
                if (event != null) //If Valid event is there, then only log it. In few cases event is null and NPE is there
                {
                    String eventLog = musicService.createEventLog(event, ChannelContext.getRequest());
                    sendDataToKafka(eventLog);
                }
            }
        }else  {
            if (event != null) //If Valid event is there, then only log it. In few cases event is null and NPE is there
            {
                String eventLog = musicService.createEventLog(event,uid, new UserDevice());
                sendDataToKafka(eventLog);
            }
        }
    }

    public void addThirdPartyNotifyEvent(ThirdPartyNotifyDTO thirdPartyNotifyDTO){
        if(thirdPartyNotifyDTO == null || StringUtils.isBlank(thirdPartyNotifyDTO.getUid())){
            return;
        }

        String eventLog = thirdPartyNotifyDTO.toJsonObject().toJSONString();
        sendDataToTPNotifyKafka(eventLog);
    }

    private void sendDataToKafka(String kafkaData) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                logger.info("Adding Adtech Targeting event to kafka : " + kafkaData);
                String partitionKey = String.valueOf(Utils.getRandomNumber(MusicConstants.ADTECH_KAFKA_PARTITION) - 1) ;
                KeyedMessage<String, String> data = new KeyedMessage<String, String>(config.getAdtechQueue(), partitionKey, kafkaData);
                try{
                    kafkaProducerManager.send(data);
                } catch (Exception e){
                    logger.error("Exception in sending data to Adtech Targeting kafka topic: ", e);
                }
            }
        });

        try {
            mainExecutorService.submit(thread);
        }catch (RejectedExecutionException e){
            logger.error("Exception in sending data to Adtech Targeting executor service: ", e);
        }

    }
    
    private void sendDataToTPNotifyKafka(String kafkaData) {
        //logger.info("Adding Third Party event to kafka :: " + kafkaData);
        //String partitionKey = String.valueOf(Utils.getRandomNumber(MusicConstants.MOENGAGE_KAFKA_PARTITION) - 1) ;
        //KeyedMessage<String, String> data = new KeyedMessage<String, String>(config.getTpNotifyQueue(),partitionKey, kafkaData);
        //kafkaProducerManager.send(data);
        moengageServiceLogger.info(kafkaData);
    }
    
}