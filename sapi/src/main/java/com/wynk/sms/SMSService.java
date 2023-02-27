package com.wynk.sms;

import com.wynk.common.PortalException;
import com.wynk.config.MusicConfig;
import com.wynk.utils.HttpClient;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by bhuvangupta on 04/02/14.
 */
@Service("smsService")
@ManagedResource(objectName = "com.bsb.jmx:name=smsService", description = "SMS Service Stats")
public class SMSService {

    private static final Logger logger          = LoggerFactory.getLogger(SMSService.class.getCanonicalName());

    @Autowired
    private MusicConfig musicConfig;

    private AbstractSMSSender   smsSender;

    private ExecutorService executorService = null;
    
    @Autowired
    private AirtelSMSSender airtelSMSSender;

    @PostConstruct
    private void init() {
        if(musicConfig.isUseLocalSMSSender()) {
            if(musicConfig.getSmsgw().equalsIgnoreCase("comviva"))
                smsSender = new ComvivaSMSSender();
            else
                smsSender = airtelSMSSender;
        }
        else {
            executorService = Executors.newFixedThreadPool(musicConfig.getSmsSenderMaxPoolSize());
        }
    }

    @PreDestroy
    private void destroy() {
        if(null != smsSender) {
            smsSender.shutdown();
        }
        if(null != executorService) {
            executorService.shutdown();
        }
    }
    
    public void sendWynkMessageWithSMSAPi(String msisdn, String message, Boolean useDND, String priority, Boolean nineToNine,String countryCode) {
    	// In case of lapu sometimes it sends null message
    	if (message != null) {
            sendMessageWithSMSAPi(msisdn, message, "WYNK", useDND, priority, nineToNine,0,countryCode);
    	}
    }

    public void sendWynkMessageWithSMSAPiWithRetryCount(String msisdn, String message, Boolean useDND, String priority, Boolean nineToNine,Integer retryCount,String countryCode) {
        // In case of lapu sometimes it sends null message
        if (message != null) {
            sendMessageWithSMSAPi(msisdn, message, "WYNK", useDND, priority, nineToNine,retryCount,countryCode);
        }
    }
    
    public void sendMessageWithSMSAPi(String msisdn, String message, String source, Boolean useDND, String priority, Boolean nineToNine,Integer retryCount,String countryCode) {
    	String SMS_API_URL = musicConfig.getSmsapi();
    	JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", message);
        jsonObject.put("msisdn", msisdn);
        jsonObject.put("source", source);
        jsonObject.put("priority", priority);
        jsonObject.put("nineToNine", nineToNine);
        jsonObject.put("useDnd", useDND);
        jsonObject.put("retryCount",retryCount);
        jsonObject.put("countryCode",countryCode);
        HttpClient.postJsonData(SMS_API_URL, jsonObject.toJSONString());
    }

    public void sendMessageWithSMSAPI(JSONObject requestPlayload) {
        String SMS_API_URL = musicConfig.getSmsapi();
        HttpClient.postJsonData(SMS_API_URL, requestPlayload.toJSONString());
    }

    public void sendMessage(String msisdn, String fromShortCode, String message, Boolean useDND) throws PortalException {
        if(musicConfig.isUseLocalSMSSender())
            smsSender.sendMessage(msisdn, fromShortCode, message, useDND);
        else {
            executorService.submit(new SmsSender(msisdn, fromShortCode, message, useDND));
        }
    }

    public void sendMessage(String msisdn, String fromShortCode, String message) throws PortalException {
        sendMessage(msisdn, fromShortCode, message, true);
    }

    public void sendActualMessage(String msisdn, String fromShortCode, String message, Boolean useDND) throws PortalException {
        JSONObject smsMessage = new JSONObject();
        smsMessage.put("msisdn", msisdn);
        smsMessage.put("shortCode", fromShortCode);
        smsMessage.put("message", message);
        smsMessage.put("useDND", useDND);
        String smsUrl = musicConfig.getSmsgw();
        HttpClient.postData(smsUrl, null, null, false, "text/json", smsMessage.toJSONString(), 10000);
    }

    class SmsSender implements Callable {

        private String msisdn;
        private String shortCode;
        private String message;
        private Boolean useDND;

        SmsSender(String _msisdn, String _shortCode, String _message, Boolean _useDND) {
            msisdn = _msisdn;
            shortCode = _shortCode;
            message = _message;
            useDND = _useDND;
        }

        @Override
        public Object call() throws Exception {
            sendActualMessage(msisdn, shortCode, message, useDND);
            return null;
        }
    }

    @ManagedAttribute(description = "Connection Pool Stats")
    public String getConnectionPoolStats() {
        String response = StringUtils.EMPTY;
        if(null != smsSender) {
            response = smsSender.getConnectionPoolStats();
        }
        return response;
    }

    @ManagedAttribute(description = "Thread Pool Stats")
    public String getThreadPoolStats() {
        String response = StringUtils.EMPTY;
        if(null != smsSender) {
            response = smsSender.getThreadPoolStats();
        }
        return response;
    }

    @ManagedAttribute(description = "Response Code Stats")
    public String getResponseCodeStats() {
        String response = StringUtils.EMPTY;
        if(null != smsSender) {
            response = smsSender.getResponseCodeStats();
        }
        return response;
    }
    
    @ManagedAttribute(description = "Today's Response Code Stats")
    public String getResponseCodeStatsForToday() {
        String response = StringUtils.EMPTY;
        if(null != smsSender) {
            response = smsSender.getTodayResponseCodeStats();
        }
        return response;
    }
}
