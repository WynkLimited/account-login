package com.wynk.service.helper;

import com.google.gson.Gson;
import com.wynk.constants.Constants;
import com.wynk.dto.UserConsentDto;
import com.wynk.dto.UserDetailsDto;
import com.wynk.music.dto.GeoLocation;
import com.wynk.server.ChannelContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;

import static com.wynk.utils.MusicMSISDNDump.logger;
import static com.wynk.wcf.WCFApisConstants.APPID_MOBILITY;

/**
 * @author : Kunal Sharma
 * @since : 09/06/22, Friday
 **/
public class WcfHelper {

    private static final Logger log = LoggerFactory.getLogger(WcfHelper.class.getCanonicalName());

    public static HashSet<String> allowedMsisdn = new HashSet<>();

    static
    {
//        allowedMsisdn.add("+919899958157");
//        allowedMsisdn.add("+918800883509");
//        allowedMsisdn.add("+919999723485");
//        allowedMsisdn.add("+918527082820");
//        allowedMsisdn.add("+917404457122");
//        allowedMsisdn.add("+919051616602");
//        allowedMsisdn.add("+918979132556");
//        allowedMsisdn.add("+919654271006");
//        allowedMsisdn.add("+919036269730");
        allowedMsisdn.add("+918929829742"); // 3
//        allowedMsisdn.add("+919599557791");
//        allowedMsisdn.add("+919899639885");
//        allowedMsisdn.add("+919958945993");
//        allowedMsisdn.add("+919818365840");
//        allowedMsisdn.add("+919972425943");
//        allowedMsisdn.add("+919972428024");
//        allowedMsisdn.add("+917026608705");
//        allowedMsisdn.add("+919990625096");
//        allowedMsisdn.add("+919163963075");
//        allowedMsisdn.add("+917758838825");
//        allowedMsisdn.add("+919636122750");
//        allowedMsisdn.add("+919447342415");
//        allowedMsisdn.add("+919871305917");
        allowedMsisdn.add("+919717477255");  // 2
        allowedMsisdn.add("+919971443118");  // 1
        allowedMsisdn.add("+917709603513");  // 4
        allowedMsisdn.add("+918700063905");  // 5
    }

    public static String prepareBodyForUserConsent(String requestPayload) {
        UserConsentDto userConsentDto = null;
        Gson gson = new Gson();
        try {
            userConsentDto = gson.fromJson(requestPayload, UserConsentDto.class);
            UserDetailsDto userDetails = UserDetailsDto.builder()
                    .uid(ChannelContext.getUid())
                    .username(ChannelContext.getMsisdn()).build();
            userConsentDto.setUserDetails(userDetails);
            userConsentDto.setAction(Constants.ACTION_TYPE.ACCOUNT_DELETE);
            userConsentDto.getAppDetails().setAppId(APPID_MOBILITY);
        } catch (Exception e) {
            log.info("Exception while parsing request body {], {}", e.getMessage(), e);
        }
        return gson.toJson(userConsentDto);
    }

    public static String prepareBodyForCreateOrGetUserId(String msisdn, String serviceName) {
        Gson gson = new Gson();
        UserConsentDto userConsentDto = new UserConsentDto();
        userConsentDto._setUserName(msisdn)._setService(serviceName);
        return gson.toJson(userConsentDto);
    }
    public static GeoLocation addLocationInRequest() {
        logger.info("Going to add location for this request");
        GeoLocation geoLocation = new GeoLocation();
        if ((ChannelContext.getUser() != null && ChannelContext.getUser().getCountryId() != null)) {
            geoLocation.setCountryCode(ChannelContext.getUser().getCountryId());
        }
        geoLocation.setAccessCountryCode(ChannelContext.getUserCoaContext());
        log.info("Returning GeoLocation with values {}", geoLocation);
        return geoLocation;
    }


}

