package com.wynk.service;

import com.wynk.common.PortalException;
import com.wynk.config.MusicConfig;
import com.wynk.constants.MusicConstants;
import com.wynk.server.ChannelContext;
import com.wynk.user.dto.User;
import com.wynk.utils.HTTPUtils;
import com.wynk.utils.MusicBuildUtils;
import com.wynk.utils.MusicDeviceUtils;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Objects;

import static com.wynk.common.WynkAppType.MUSIC_HEADER_APP;
import static com.wynk.utils.UserDeviceUtils.isRequestFromWAP;
import static com.wynk.utils.UserDeviceUtils.isWAPUser;
import static com.wynk.utils.Utils.getSha1Hash;

/**
 * Created by Aakash on 07/07/17.
 */

@Service
public class ClientAuthorizationService {

    private static final Logger logger = LoggerFactory.getLogger(ClientAuthorizationService.class.getCanonicalName());

    @Autowired
    private MusicConfig musicConfig;

    private static final Set<String> CLIENT_VERIFICATION_BYPASS_URLS = new HashSet<String>();

    static {
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v2|v3)/account/s2s/login.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v2|v3)/account/s2s/profile.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v1|v2)/account/s2s/offercachepurge.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v1|v2)/unisearch.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v1|v2|v3)/account/headers.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v1|v2)/clearcache.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v1|v2)/ipayyconfirmoperator.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v1|v2|v3)/account/operatorconfirmation");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v1|v2)/ipayyconfirmotp.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v1|v2|v3)/account/doipayypayment");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v1|v2|v3)/account/resendotp.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v1|v2)/debugUnisearch.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v1|v2)/debugSearch.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v1|v2)/clearservercache.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v1|v2)/acdcgwcallback.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v1|v2)/wcdcgwcallback.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v1|v2)/cdcgwcallback.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v1|v2)/paytmcb");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v1|v2)/jackpot");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v1|v2)/wcfcb");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v1|v2|v3)/galaxy/minusonepage.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/(v1|v2)/galaxy/spotlightimage.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/v1/account/s2s/validate.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/v1/account/s2s/ispaiduser.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/v1/account/s2s/getmsisdn.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/v1/account/s2s/circle.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/v1/account/s2s/langsByCircle.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/v1/account/s2s/onboardingLangs.*");
        CLIENT_VERIFICATION_BYPASS_URLS.add("/music/v1/account/s2s/token.*");
    }

    /**
     *  Client authentication has been done only for request containing wid and cid.
     *  Some of wap post calls having "u" as query param are not part of client(salt) checks.
     */
    public boolean authenticateClient(HttpRequest request, String requestUri) {

        if (!musicConfig.isEnableAuth())
            return true;

        // For some callbacks and wap calls we do not recieve any client verification header
        if (StringUtils.isNotBlank(requestUri)) {
            for (String bypass_url : CLIENT_VERIFICATION_BYPASS_URLS) {
                if (requestUri.matches(bypass_url)) {
                    return true;
                }
            }
        }

        String wapID = request.headers().get(MusicConstants.MUSIC_HEADER_WAPID);

        Map<String, List<String>> params;

        try {
            params = HTTPUtils.getUrlParameters(requestUri);
        } catch (PortalException e) {
            params = null;
        }

        if (StringUtils.isNotEmpty(wapID)) {

            logger.info("Verifying wap client");
            return checkWAPClientSalt(request, requestUri, wapID);
        } else if (HTTPUtils.getStringParameter(params, "u") != null) {

            // Update this with not Empty check
            logger.info("Verifying client with query param");
            return true;

        } else if (isWAPUser(ChannelContext.getUid()) || isRequestFromWAP()) {

            // if it is a wap user, then use salt and uri to generate the hash.
            return authenticateWapClient(requestUri, request);
        } else {
            // remove build check
/*            if (!MusicBuildUtils.isClientVerificationSupported()) {
                logger.info("Verifying client with old build");
                return true;
            }*/
            logger.info("Verifying app client");
            return checkAppClientSalt(request);
        }
    }

    /**
     * Some of the WAP request doesn't contains WAPUID and are made directly.
     * SO authentication in case of WAP is done using x-bsy-wid header and client verification
     * x-bsy-wid = SHA1 ( WAPUID + URI + SALT )
     */
    private boolean checkWAPClientSalt(HttpRequest request, String requestUri, String wapid) {
        String wapUID = request.headers().get(MusicConstants.MUSIC_HEADER_WAP);
        String salt = musicConfig.getWapSalt();
        String wapIdSalt =	requestUri + salt;

        if (StringUtils.isNotEmpty(wapUID)) {
            wapIdSalt =	wapUID + wapIdSalt;
        }

        String wapIdHash = getSha1Hash(wapIdSalt);
        if (wapIdHash != null && wapIdHash.equals(wapid)) {
            return true;
        }
        return false;
    }

    private boolean authenticateWapClient(String requestUri, HttpRequest request) {
        String cpid = request.headers().get(MusicConstants.MUSIC_HEADER_CID);

        User user = ChannelContext.getUser();

/*        // if no user found return false..
        if (user == null)
            return false;*/

        String salt = musicConfig.getNewWapSalt();
        String deviceIdSalt = requestUri + salt;

        String deviceIdHash = getSha1Hash(deviceIdSalt);

        logger.info("Comparing wap client hashes found : " + cpid + " calculated : " + deviceIdHash);

        if (deviceIdHash != null && deviceIdHash.equals(cpid)) {
            return true;
        }
        return false;
    }

    private boolean checkAppClientSalt(HttpRequest request) {
        String cpid = request.headers().get(MusicConstants.MUSIC_HEADER_CID);

        String deviceId = MusicDeviceUtils.getDeviceId();
        String salt = musicConfig.getAppSalt();
        String appHeaderVal = request.headers().get(MUSIC_HEADER_APP);
        String saltVal = musicConfig.getSaltValue(appHeaderVal);
        if (Objects.nonNull(appHeaderVal)
                && Objects.nonNull(saltVal)) {
            salt = saltVal;
        }
        String deviceIdSalt = deviceId + salt;

        String deviceIdHash = getSha1Hash(deviceIdSalt);

        if (deviceIdHash != null && deviceIdHash.equals(cpid)) {
            return true;
        }
        return false;
    }
}
