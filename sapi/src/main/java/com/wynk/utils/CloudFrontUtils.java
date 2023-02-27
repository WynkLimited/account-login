package com.wynk.utils;

import com.amazonaws.services.cloudfront.CloudFrontCookieSigner;
import com.amazonaws.services.cloudfront.util.SignerUtils;
import com.wynk.common.UrlPrefixForStreamAndDownload;
import com.wynk.dto.SongUrlRouterResponseDTO;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

import static com.amazonaws.services.cloudfront.CloudFrontUrlSigner.getSignedURLWithCannedPolicy;
import static com.amazonaws.services.cloudfront.CloudFrontCookieSigner.getCookiesForCustomPolicy;

/**
 * Created by Aakash on 14/06/17.
 */
public class CloudFrontUtils {

    private static final Logger logger = LoggerFactory.getLogger(CloudFrontUtils.class);

    private static String keyPairId = "APKAJREYRYFCU2OXNSTA";
    private static Integer expireCookieTimeInMinutes = 1800;
    private static String filePathUrl = "/opt/aws_credentials/pk-APKAJREYRYFCU2OXNSTA.pem";

    public static String generateSignedSongURL(String url, Boolean isHLS, UrlPrefixForStreamAndDownload prefix,Integer expireTimeInMinutes){
        Date expirationDate = new Date(System.currentTimeMillis() + 60 * 1000 * expireTimeInMinutes);
        try {
            File file = new File(filePathUrl);
            String signedToken = getSignedURLWithCannedPolicy(SignerUtils.Protocol.http, getCloudFrontDistributionDomain(isHLS,prefix), file, url, keyPairId, expirationDate);
            return signedToken;
        }catch (Throwable th){
            th.printStackTrace();
        }
        return null;
    }

    public static String generateHTTPSSignedSongURL(String url, Boolean isHLS, UrlPrefixForStreamAndDownload prefix,Integer expireTimeInMinutes){
        Date expirationDate = new Date(System.currentTimeMillis() + 60 * 1000 * expireTimeInMinutes);
        try {
            File file = new File(filePathUrl);
            String signedToken = getSignedURLWithCannedPolicy(SignerUtils.Protocol.https, getCloudFrontDistributionDomain(isHLS,prefix), file, url, keyPairId, expirationDate);
            return signedToken;
        }catch (Throwable th){
            th.printStackTrace();
        }
        return null;
    }

    public static Map<String,String> generateSongCookiesURL(String url,Boolean isHLS,UrlPrefixForStreamAndDownload prefix){
        Date expirationDate = new Date(System.currentTimeMillis() + 60 * 1000 * expireCookieTimeInMinutes);
        String resourcePath = null;
        if(url.contains("/music/"))
        {
            String stringBeginning = url.substring(0,url.indexOf("/music/")+7);
            resourcePath = stringBeginning.concat("*");
        }
        try {
            Map<String,String> cookieSetMap = null;
            File file = new File(filePathUrl);
            CloudFrontCookieSigner.CookiesForCustomPolicy cookies = getCookiesForCustomPolicy(SignerUtils.Protocol.http,getCloudFrontDistributionDomain(isHLS,prefix),file,resourcePath,keyPairId,expirationDate,null,null);
            if(cookies != null){
                cookieSetMap = new HashMap<>();
                if(cookies.getPolicy() != null) {
                    cookieSetMap.put(cookies.getPolicy().getKey(), cookies.getPolicy().getValue());
                }
                if(cookies.getKeyPairId() != null){
                    cookieSetMap.put(cookies.getKeyPairId().getKey(),cookies.getKeyPairId().getValue());
                }
                if(cookies.getSignature() != null){
                    cookieSetMap.put(cookies.getSignature().getKey(),cookies.getSignature().getValue());
                }
            }
            return cookieSetMap;

        }catch (Throwable th){
            th.printStackTrace();
        }

        return null;
    }

    public static String getCloudFrontDistributionDomain(Boolean isHLS,UrlPrefixForStreamAndDownload prefix){
        if(isHLS){
            return prefix.getHlsHostName();
        }
        else {
            return prefix.getMp3HostName();
        }
    }

}
