package com.wynk.utils.authentication;

import com.wynk.constants.Constants;
import com.wynk.utils.EncryptUtils;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SignatureException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author : Kunal Sharma
 * @since : 09/06/22, Saturday
 **/


@Deprecated
public class AuthenticationUtils {


    private static final Logger logger = LoggerFactory.getLogger(AuthenticationUtils.class);
    private static long ALLOWED_TIME_DIFF = 60000L;
    private static final Map<String, String> appIdMap;

    static {
        Map<String, String> tempMap = new HashMap<>();
        tempMap.put("put YOUR SECRET KEY", "put YOUR VALUE KEY");

        appIdMap = Collections.unmodifiableMap(tempMap);
    }


    public static ApplicationAuthResponse authenticateRequest(ApplicationAuthRequest request) {
        if (StringUtils.isBlank(request.getAppId()) || StringUtils.isBlank(request.getSignature())) {
            logger.error("Invalid request found");
            return new ApplicationAuthResponse(false);
        }
        String digestString = StringUtils.EMPTY;
        StringBuilder digestStringBuilder = new StringBuilder(request.getMethod()).append(request.getRequestUri());
        if (StringUtils.isNotBlank(request.getRequestPayload())) {
            digestStringBuilder.append(request.getRequestPayload());
        }
        if (request.getRequestTimestamp() != 0L) {
            digestStringBuilder.append(request.getRequestTimestamp());
        }
        digestString = digestStringBuilder.toString();
        String secretValue = appIdMap.get(request.getAppId());
        if (StringUtils.isBlank(secretValue)) {
            logger.info("Unkown appId : {} ", request.getAppId());
            return new ApplicationAuthResponse(false);
        }
        boolean authenticated = false;
        try {
            String computedSignature = EncryptUtils.calculateRFC2104HMAC(digestString, secretValue);
            if (StringUtils.equals(computedSignature, request.getSignature())) {
                logger.info("Signature Authenticated");
                authenticated = true;
                if (request.getRequestTimestamp() != 0L) {
                    authenticated = false;
                    long current = System.currentTimeMillis();
                    logger.info("Request Timestamp " + request.getRequestTimestamp() + " System current time " + current);
                    long timeDiff = current - request.getRequestTimestamp();
                    if (ALLOWED_TIME_DIFF >= timeDiff) {
                        authenticated = true;
                    } else {
                        logger.info("AppId: {} is trying for replay attack, time difference: {}", request.getAppId(), timeDiff);
                    }
                }
            } else {
                logger.info("Signature mismatch for appId: {}, computed signature: {}, request signature: {}", request.getAppId(), computedSignature, request.getSignature());
            }
        } catch (SignatureException e) {
            logger.error("Error while computing signature, ERROR: {}", e.getMessage(), e);
            return new ApplicationAuthResponse(false);
        }
        return new ApplicationAuthResponse(authenticated);
    }

    public static ApplicationAuthRequest prepareAuthRequest(String requestUri, String requestPayload, HttpRequest request) {
        try {
            String signature = StringUtils.EMPTY;
            String appId = StringUtils.EMPTY;
            String signatureHeader = request.headers().get(Constants.RequestHeaders.X_BSY_ATKN);
            String timestamp = request.headers().get(Constants.RequestHeaders.X_BSY_DATE);
            if (StringUtils.isBlank(signatureHeader) && StringUtils.isBlank(timestamp)) {
                signatureHeader = StringUtils.EMPTY;
                timestamp = StringUtils.EMPTY;
            }
            if (StringUtils.isNotBlank(signatureHeader)) {
                String[] tokens = signatureHeader.split(":");
                if (2 == tokens.length) {
                    appId = tokens[0];
                    signature = tokens[1]; // atkn = APPID : Signature
                    // header => date, atkn
                }
            }
            ApplicationAuthRequest appAuthRequest = new ApplicationAuthRequest()
                    ._method(request.getMethod().name())
                    ._requestUri(requestUri)
                    ._requestPayload(requestPayload)
                    ._signature(signature)
                    ._appId(appId);
            if (timestamp != null && Long.parseLong(timestamp) != 0L) {
                appAuthRequest.setRequestTimestamp(Long.parseLong(timestamp));
            }
            return appAuthRequest;
        } catch (Throwable e) {
            logger.error("Invalid signature", e);
            throw new RuntimeException("error");
        }
    }

    public static boolean isAtknValidForRequest(String requestUri, String requestPayload, HttpRequest request) {
        ApplicationAuthRequest authRequest = AuthenticationUtils.prepareAuthRequest(requestUri, requestPayload, request);
        ApplicationAuthResponse applicationAuthResponse = AuthenticationUtils.authenticateRequest(authRequest);
        return applicationAuthResponse.isAuthenticated();
    }

}
