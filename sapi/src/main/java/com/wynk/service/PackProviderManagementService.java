package com.wynk.service;

import com.wynk.common.*;
import com.wynk.dto.PackProvider;
import com.wynk.utils.ConfigFile;
import com.wynk.utils.EncryptUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PackProviderManagementService {

    private static final Logger logger            = LoggerFactory.getLogger(PackProviderManagementService.class);

    private static final long                ALLOWED_TIME_DIFF = 100000;

    private static Map<String, PackProvider> packProviderCache  = new HashMap<>();

    @Value("${wynk.externalapplications:comptel}")
    private String[]                         packProviders;

    @Autowired
    private ConfigFile                       properties;

    @PostConstruct
    public void readApplications() {
        for(String appName : packProviders) {
            String appDetails = properties.getStringProperty("wynk.externalapplications." + appName + ".details", StringUtils.EMPTY);
            if(StringUtils.isNotBlank(appDetails)) {
                String[] tokens = appDetails.split("\\|", 10);
                String appId = tokens[0];
                String appSecret = tokens[1];
                String email = tokens[2];
                String contact = tokens[3];
                String type = tokens[4];
                String notificationRequired = tokens[5];
                String url = tokens[6];
                String auth = tokens[7];
                String username = tokens[8];
                String password = tokens[9];
                PackProviderNotificationDetails notifyDetails = new PackProviderNotificationDetails.Builder().notificationRequired(
                        Boolean.valueOf(notificationRequired))
                        .auth(NotificationAuthentication.fromValue(auth)).url(url).username(username).password(password).build();
                PackProvider application = new PackProvider.Builder().appName(appName).appId(appId).appSecret(appSecret).email(email).contact(contact).type(PackProviderType.fromValue(type))
                        .notifyDetails(notifyDetails).build();
                packProviderCache.put(appId, application);
            }
        }
    }

    public PackProviderAuthResponse _authenticate(PackProviderAuthRequest request) {
        boolean authenticated = false;
        PackProvider application = null;
        if(StringUtils.isBlank(request.getAppId()) || StringUtils.isBlank(request.getSignature()) || 0 == request.getRequestTimestamp()) {
            return new PackProviderAuthResponse(authenticated, application);
        }

        application = getApplicationByAppId(request.getAppId());
        if(null == application) {
            return new PackProviderAuthResponse(authenticated, null);
        }

        String digestString = new StringBuilder(request.getMethod()).append(request.getRequestUri()).append(request.getRequestPayload()).append(request.getRequestTimestamp()).toString();
        try {
            String computedSignature = EncryptUtils.calculateRFC2104HMAC(digestString, application.getAppSecret());
            if(StringUtils.equals(computedSignature, request.getSignature())) {
                long timeDiff = System.currentTimeMillis() - request.getRequestTimestamp();
                if(ALLOWED_TIME_DIFF >= timeDiff) {
                    authenticated = true;
                }
                else {
                    logger.info("AppId: {} is trying for replay attack, time difference: {}", request.getAppId(), request, timeDiff);
                }
            }
            else {
                logger.info("Signature mismatch for appId: {}, computed signature: {}, request signature: {}", request.getAppId(), computedSignature, request.getSignature());
            }
        }
        catch (SignatureException e) {
            logger.error("Error while computing signature, ERROR: {}", e.getMessage(), e);
            return new PackProviderAuthResponse(authenticated, application);
        }
        return new PackProviderAuthResponse(authenticated, application);
    }

    private PackProvider getApplicationByAppId(String appId) {
        return packProviderCache.get(appId);
    }
}
