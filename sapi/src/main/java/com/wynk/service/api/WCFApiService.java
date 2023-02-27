package com.wynk.service.api;

import com.google.gson.*;
import com.wynk.common.ExceptionTypeEnum;
import com.wynk.config.MusicConfig;
import com.wynk.constants.MusicConstants;
import com.wynk.dto.*;
import com.wynk.music.WCFServiceType;
import com.wynk.server.ChannelContext;
import com.wynk.user.dto.User;
import com.wynk.utils.*;
import com.wynk.wcf.WCFApisService;
import com.wynk.wcf.dto.UserSubscription;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.aspectj.lang.annotation.DeclarePrecedence;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aakashkumar on 10/11/16.
 */
@Service
@Deprecated
public class WCFApiService {

    private static final Logger logger = LoggerFactory.getLogger(WCFApiService.class.getCanonicalName());

    public static final String X_BSY_ATKN = "x-bsy-atkn";
    public static final String X_BSY_DATE = "x-bsy-date";
    public static final String X_APP_VERSION = "x-app-version";
    public static final String X_BUSY_IMEI = "x-bsy-imei";
    public static final String X_BSY_IMSI ="x-bsy-imsi";
    public static final String X_BSY_LOCATION = "x-bsy-location";
    public static final String X_BSY_NETWORK = "x-bsy-network";
    private static final int CONNECTION_TIMEOUT_MILLIS = 2001;

    @Autowired
    MusicConfig musicConfig;

    @Autowired
    WCFUtils wcfUtils;

    @Autowired
    WCFApisService wcfApisService;

    @Autowired
    private ConfigFile properties;

    Gson gson = new Gson();

    @Deprecated
    public Map<String,String> getHeaderMap(String httpMethod , String requestUri, String requestBody){
        Map<String,String> headerMap = new HashMap<>();

        String timestamp = String.valueOf(System.currentTimeMillis());
        if(StringUtils.isBlank(requestBody)){
            requestBody = "";
        }
        String signature = wcfUtils.createSignature(httpMethod,requestUri,requestBody,timestamp,"ed02d7dbb8f6923409d230e731f2ff70");

        headerMap.put(X_BSY_DATE,timestamp);
        headerMap.put(X_BSY_ATKN,"6915ddabb06cf86324eccd170aa44ea9:"+signature);

        return headerMap;
    }

    @Deprecated
    public WCFSubsInitResponseObject wcfInitialisationCall(String service,WCFSubscribeInitializationObject wcfSubscribeInitializationObject) throws Exception {

        String wcfBaseUrl = musicConfig.getWcfBaseUrl();
        String apiRequestUrl = "/wynk/v1/s2s/subscription/init?service="+service;
        String url = wcfBaseUrl.concat(apiRequestUrl);
        String responseBody = null;
        String json = null;

        long startTime =0;
        try {
            json = gson.toJson(wcfSubscribeInitializationObject);
            Map<String,String> headerMap = getHeaderMap("POST",apiRequestUrl,json);

            startTime = System.currentTimeMillis();
            logger.info("WCF Payment Init Request Url :"+ apiRequestUrl + "with request body" + json + "with header :"+headerMap);
            responseBody = HttpClient.postData(url,json,headerMap,CONNECTION_TIMEOUT_MILLIS);
            logger.info("WCF Payment Init Request Response Body :"+ responseBody + "for request "+json);
        }catch (Exception ex){
            LogstashLoggerUtils.createFatalExceptionLogWithMessage(ex, ExceptionTypeEnum.THIRD_PARTY.WCF.name(), "",
                    "WCFApiService.wcfInitialisationCall", "Exception while WCF init Subscription Call");
            logger.error("Exception occured in Initialization Api Call for request {}", wcfSubscribeInitializationObject);
            throw new Exception("Exception occured in Initialization Api");
        }

        if(responseBody == null){
            logger.error("Null Response for the Wcf Initialization request {}",json);
            throw new Exception("Failure Status code in Initialization Api");
        }

        long apiTimeSpan = System.currentTimeMillis() - startTime;
        logger.info("WCF Initialization call time span : "+ apiTimeSpan);

        try{
            JsonParser jsonParser = new JsonParser();
            JsonObject jo = (JsonObject)jsonParser.parse(responseBody);

            WCFSubsInitResponseObject wcfSubsInitResponseObject = new WCFSubsInitResponseObject();

            if(jo.get("tid") != null) {
                wcfSubsInitResponseObject.setTransactionId(jo.get("tid").getAsString());
            }

            if(jo.get("chargingUrl") != null) {
                wcfSubsInitResponseObject.setRedirectUri(jo.get("chargingUrl").getAsString());
            }

            if(jo.get("wcfProductId") != null) {
                wcfSubsInitResponseObject.setWcfProductId(jo.get("wcfProductId").getAsString());
            }
            wcfSubsInitResponseObject.setCarrierBillingAllow(Boolean.FALSE);
            if(jo.get("isSeEligible") != null){
                wcfSubsInitResponseObject.setCarrierBillingAllow(jo.get("isSeEligible").getAsBoolean());
            }
            return wcfSubsInitResponseObject;
        }catch (Exception ex){
            logger.error("Error in parsing the response Object for the request {} ",wcfSubscribeInitializationObject);
            throw new Exception("Exception occured in parsing response of WCF Initialization Api");
        }
    }

    @Deprecated
    public List<Integer> getRecommnededProductId(String service,String msisdn) throws Exception {
        String apiRequestUrl = "/wynk/v1/s2s/subscription/eligiblepacks?service="+service+"&msisdn="+msisdn;
        String responseBody = null;
        String wcfBaseUrl = musicConfig.getWcfBaseUrl();
        String url = wcfBaseUrl.concat(apiRequestUrl);

        long startTime = System.currentTimeMillis();

        Map<String,String> headerMap = getHeaderMap("GET",apiRequestUrl,null);

        logger.info("WCF Recommended Request Url :"+ apiRequestUrl + "with header :"+headerMap);
        responseBody = HttpClient.getContent(url,CONNECTION_TIMEOUT_MILLIS,headerMap);
        logger.info("WCF Recommended Request Response Body :"+ responseBody + "for msisdb "+msisdn);

        if(responseBody == null){
            throw new RuntimeException("Null Response for the Get Recommended Products call for msisdn " + msisdn);
        }

        long apiTimeSpan = System.currentTimeMillis() - startTime;
        logger.info("WCF Display Product Call time span : "+ apiTimeSpan);

        JsonParser jsonParser=new JsonParser();
        JsonArray arrayObj =(JsonArray)jsonParser.parse(responseBody);

        List<Integer> productIdList = new ArrayList<Integer>();
        for(int i =0; i< arrayObj.size(); i++){
            Integer jsonObj = arrayObj.get(i).getAsInt();

            productIdList.add(jsonObj);
        }
        return productIdList;
    }
}
