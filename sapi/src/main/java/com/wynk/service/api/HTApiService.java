package com.wynk.service.api;

import com.wynk.utils.*;
import com.wynk.wcf.WCFApisConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class HTApiService {

    private static final Logger logger = LoggerFactory.getLogger(HTApiService.class);

    private static final String AUTO_ACTIVATE_ENDPOINT_WITH_DOMAIN = "http://ht.wynk.in/v1/ht/s2s/autoactivate?uid=%s";
    private static final String AUTO_ACTIVATE_ENDPOINT = "/v1/ht/s2s/autoactivate?uid=%s";

    private static final String DEACTIVATE_HT = "/v1/ht/s2s/autodeactivate/forcefully";
    private static final String MIGRATE_HT = "/v2/ht/s2s/migrate/userht";

//    @Value("${ht.service.base.url}")
    //  add above key and cache value
    private String htServiceUrl = "http://ht.wynk.in";

    public String autoActivateHellotunes(String uid) {
        String url = String.format(AUTO_ACTIVATE_ENDPOINT_WITH_DOMAIN, uid);
        Map<String, String> headerMap = getHeaderMap(WCFApisConstants.METHOD_POST,
                String.format(AUTO_ACTIVATE_ENDPOINT, uid), null);
        long startTime = System.currentTimeMillis();
        logger.info("Ht auto activate Url : {} with header : {}", url, headerMap);
        String responseBody =
                HttpClient.postData(url, new HashMap<>(), headerMap);
        logger.info("HT auto activate Api response Data  : {}", responseBody);
        LogstashLoggerUtils.createAccessLogLite("HTAutoActivateResponse", responseBody, uid);
        logger.info("HT auto activate Api call time span : {}", (System.currentTimeMillis() - startTime));
        return responseBody;
    }

    public Map<String,String> getHeaderMap(String httpMethod , String requestUri, String requestBody){
        Map<String,String> headerMap = new HashMap();
        String timestamp = String.valueOf(System.currentTimeMillis());
        requestBody = org.apache.commons.lang3.StringUtils.isBlank(requestBody) ? "" : requestBody;
        String signature = createSignature(httpMethod,requestUri,requestBody,timestamp, "50de5a601c133a29c8db434fa9bf2db4");
        headerMap.put(WCFApisConstants.X_BSY_DATE_KEY,timestamp);
        headerMap.put(WCFApisConstants.X_BSY_ATKN_KEY, "543fbd6f96644406567079c00d8f33dc".concat(":").concat(signature));
        headerMap.put("Content-Type", "application/json");
        return headerMap;
    }

    public String createSignature(String httpMethod , String requestUri, String requestBody, String timestamp, String secretKey){
        String digestString = new StringBuilder(httpMethod).append(requestUri).append(requestBody).append(timestamp).toString();
        String computedSignature ="";
        try {
            computedSignature = EncryptUtils.calculateRFC2104HMAC(digestString, secretKey);
        }catch (Throwable th){
            logger.error("exception occuured in calculating signature for the requestBody {}",requestBody);
        }
        return computedSignature;
    }

    public void deactivateUserHt(String uid, String msisdn) {
        StringBuilder tenDigitMobileNumber = new StringBuilder(Utils.getTenDigitMsisdnWithoutCountryCode(msisdn));
        logger.info("Request received to deactivate ht for uid : {} , tenDigitMobileNumber : {}", uid, tenDigitMobileNumber);
        StringBuilder url = new StringBuilder(htServiceUrl);
        String endpoint = new StringBuilder(DEACTIVATE_HT).append("?uid=").append(uid).append("&retryCount=")
                .append(0)
                .append("&tenDigitMobileNumber=").append(tenDigitMobileNumber).toString();
        logger.info("Ht deactivate endpoint is : {}", endpoint);
        url = url.append(endpoint);
        Map<String, String> headerMap = getHeaderMap(WCFApisConstants.METHOD_POST,
                endpoint, null);
        long startTime = System.currentTimeMillis();
        logger.info("Complete ht deactivate Url : {} with header : {}", url, headerMap);
        StringBuilder finalUrl = url;
        CompletableFuture.supplyAsync(() -> HttpClient.postData(finalUrl.toString(), new HashMap<>(), headerMap)).exceptionally(e -> {
            logger.error("Error in deactivateUserHt for uid : {}, tenDigitMobileNumber : {}, {}, {}", uid,
                    tenDigitMobileNumber, e, e.getMessage());
            return null;
        });
        logger.info("HT deactivate Async Api call time span : {}", (System.currentTimeMillis() - startTime));
    }

    public void migrateHtData(String oldUid, String newUid) {
        if (StringUtils.isBlank(oldUid) || StringUtils.isBlank(newUid)) {
            logger.info("Either oldUid : {} or newUid {} is empty or null", oldUid, newUid);
            return;
        }
        logger.info("Request received with oldUid {} , newUid {} ", oldUid, newUid);
        StringBuilder url = new StringBuilder(htServiceUrl);
        String endpoint = new StringBuilder(MIGRATE_HT).append("?olduid=").append(oldUid).append("&newuid=")
                .append(newUid).toString();
        logger.debug("Migrate ht data endpoint is : {}", endpoint);
        url = url.append(endpoint);
        Map<String, String> headerMap = getHeaderMap(WCFApisConstants.METHOD_GET,
                endpoint, null);
        long startTime = System.currentTimeMillis();
        logger.debug("Complete ht migrate Url : {} with header : {}", url, headerMap);
        StringBuilder finalUrl = url;
        CompletableFuture.supplyAsync(() -> HttpClient.getContent(finalUrl.toString(), 750, headerMap)).exceptionally(e -> {
            logger.error("Error in HtApiService:migrateHtData for olduid : {}, newuid : {}, {},{}", oldUid,
                    newUid, e, e.getMessage());
            return null;
        });
        logger.info("HT deactivate Async Api call time span : {}", (System.currentTimeMillis() - startTime));
    }
}
