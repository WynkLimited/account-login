package com.wynk.utils;

import com.akamai.authentication.URLToken.AkamaiToken;
import com.akamai.authentication.URLToken.URLTokenFactory;
import com.wynk.common.UrlPrefixForStreamAndDownload;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;

public class MusicAkamaiUtils {

	
	private static final Logger logger               = LoggerFactory
            .getLogger(MusicAkamaiUtils.class.getCanonicalName());
	
    private static final String HLS_KEY         = "5176FC123AF69EDCA8FE9187F2F5D4C5";
    private static final long    HLS_WINDOW      = 1800;
    
    public static String generateAkamaiUrl(String originalUrl,UrlPrefixForStreamAndDownload prefix,boolean isStreaming)
    {
        String url = originalUrl;
        if(StringUtils.isBlank(originalUrl)) {
        	return StringUtils.EMPTY;
        }
        if(originalUrl.startsWith("http"))
        {
            if(originalUrl.contains("cms.twangmusic.in/srch")  ||
                    originalUrl.contains("wynk.in/srch"))
            {
                url = originalUrl.substring(originalUrl.indexOf("/srch_"));
            }
            if(originalUrl.contains("cms.twangmusic.in/music") ||
                    originalUrl.contains("wynk.in/music"))
            {
                url = originalUrl.substring(originalUrl.indexOf("/music"));
            }
            else {
                if (originalUrl.indexOf("/wynk-music-cms/") != -1) {

                    //http://s3-ap-southeast-1.amazonaws.com/bsbcms/music/srch_adityamusic_INA091414664.mp3
                    //http://s3-ap-southeast-1.amazonaws.com/bsbcms/srch_adityamusic/music/64/srch_adityamusic_INA091414664.mp3
                    url = originalUrl.substring(originalUrl.indexOf("/wynk-music-cms/") + 15);
                } else if (originalUrl.indexOf("/srch_") != -1) {
                    url = originalUrl.substring(originalUrl.indexOf("/srch_"));
                }
            }

        }
        String salt = "b5bp0r74lmu51c4pp";
        //String extract = "ef057cd3b";
        long window = 21600;
        if(isStreaming)
            window = 1800;
        //long time = System.currentTimeMillis();
        String akamaiSecureUrl = URLTokenFactory.generateURL(url, "token", window, salt);
        String baseUrl = null;
        if(isStreaming)
        {
            baseUrl = prefix.getStreamingUrl();
        }
        else {

            baseUrl = prefix.getStreamingUrl();
        }

        return baseUrl+akamaiSecureUrl;
    }
    
    public static String generateHLSAkamaiUrl(String originalUrl,UrlPrefixForStreamAndDownload prefix)
    {
        String url = originalUrl;
        if(StringUtils.isBlank(originalUrl)) {
            return StringUtils.EMPTY;
        }
        if(originalUrl.startsWith("http"))
        {
            if(originalUrl.contains("cms.twangmusic.in/srch")  ||
                    originalUrl.contains("wynk.in/srch"))
            {
                url = originalUrl.substring(originalUrl.indexOf("/srch_"));
            }
            if(originalUrl.contains("cms.twangmusic.in/music") ||
                    originalUrl.contains("wynk.in/music"))
            {
                url = originalUrl.substring(originalUrl.indexOf("/music"));
            }
            else {
                if (originalUrl.indexOf("/wynk-music-cms/") != -1) {

                    //http://s3-ap-southeast-1.amazonaws.com/bsbcms/music/srch_adityamusic_INA091414664.mp3
                    //http://s3-ap-southeast-1.amazonaws.com/bsbcms/srch_adityamusic/music/64/srch_adityamusic_INA091414664.mp3
                    url = originalUrl.substring(originalUrl.indexOf("/wynk-music-cms/") + 15);
                } else if (originalUrl.indexOf("/srch_") != -1) {
                    url = originalUrl.substring(originalUrl.indexOf("/srch_"));
                }
            }
        }
        
        String token = null;
        try {
        	String aclUrl = generateAclUrl(url);
        	
            Dictionary token_config = new Hashtable();
            token_config.put("window_seconds", HLS_WINDOW);
            token_config.put("key", HLS_KEY);
            token_config.put("acl", String.format("*%s*", aclUrl));
            token = AkamaiToken.generateToken(token_config);
            if(StringUtils.isNotBlank(token))
                url += "?"+token;
        }
        catch (Exception e) {
            logger.error("Error generating Akamai token for HLS : "+e.getMessage(),e);
        }
        String baseUrl = prefix.getHlsUrl();
        
        // Replacing Base URL with Akamai staging URLs
        // baseUrl = "http://airtel-vh.akamaihd-staging.net/i";
        
        return baseUrl+url;
    }
    
    public static String getBitRateFromUrl(String url,Boolean isSecureUrl) {
        if(StringUtils.isBlank(url))
            return null;
        
        
        String bitRate = null;
        if(url.contains("/music/"))
        {
            String substring = url.substring(url.indexOf("/music/")+7);
            bitRate = substring.substring(0,substring.indexOf("/"));
        }
        else if(isSecureUrl){
            String substring = url.substring(url.indexOf("/mp4/")+5);
            bitRate = substring.substring(0,substring.indexOf("/"));
        }
        
        return bitRate;
    }
    
    public static String generateAdaptiveHLSPlaylist(String url, String bitRates) {
        if(StringUtils.isBlank(url))
            return null;
        
        String adaptiveUrl = null;
        if(url.contains("/music/"))
        {
            String stringBeginning = url.substring(0,url.indexOf("/music/")+7);
            String substring = url.substring(url.indexOf("/music/")+7);
            String stringEnding = substring.substring(substring.indexOf("/"));
            adaptiveUrl = stringBeginning.concat(bitRates).concat(stringEnding.replace(".mp4", ".mp4.csmil"));
        }
        
        return adaptiveUrl;
    }
    
    public static String generateAclUrl(String url) {
        if(StringUtils.isBlank(url))
            return null;
        
        String adaptiveUrl = null;
        if(url.contains("/music/"))
        {
            String stringBeginning = url.substring(0,url.indexOf("/music/")+7);
            String substring = url.substring(url.indexOf("/music/")+7);
            String stringEnding = substring.substring(substring.indexOf("/"));
            adaptiveUrl = stringBeginning.concat("*").concat(stringEnding).replace("/master.m3u8", "");
        }
        
        return adaptiveUrl;
    }
}
