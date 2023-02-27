package com.wynk.utils;

import com.wynk.common.*;
import com.wynk.constants.MusicConstants;

import com.wynk.music.constants.MusicContentType;
import com.wynk.music.constants.MusicPackageType;
import com.wynk.music.constants.MusicRequestSource;
import com.wynk.music.dto.*;
import com.wynk.server.ChannelContext;
import com.wynk.service.GeoDBService;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SignatureException;
import java.util.*;


/**
 * Created by bhuvangupta on 24/03/14.
 */
public class MusicUtils {

    private static final Logger logger               = LoggerFactory.getLogger(MusicUtils.class.getCanonicalName());

    private static final String DND_REDIS_KEY = "";

    public static boolean isValidEmail(String email) {
    	String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
    	if (email == null || StringUtils.isEmpty(email))
    		return false;
    	
    	String[] emails = email.split(",");
    	for (String e : emails) {
    		if (!e.matches(EMAIL_REGEX)) {
    			return false;
    		}
    	}
    	return true;
    }

    public static String getFirstIPCountry(GeoDBService geoDBService, HttpRequest request){
        List<String> ipAddressList = getIPsListFromXForwardedFor(request);
        logger.info("We have Ip address list is {}", ipAddressList);
        if(!CollectionUtils.isEmpty(ipAddressList)){
           String ip= ipAddressList.get(0).trim();
           if(StringUtils.isNotEmpty(ip)){
               String countryCode=geoDBService.getCountry(ip);
               if(StringUtils.isNotEmpty(countryCode)){
                   return countryCode;
               }
           }
        }
        return null;
    }
    public static List<String> getCountryCodeFromRequest(GeoDBService geoDBService, HttpRequest request) {
        List<String> countryCodesList = new ArrayList<>();
        List<String> ipAddressList = getIPsListFromXForwardedFor(request);

        if (ipAddressList.isEmpty() ){
            return null;
        }

        int max = ipAddressList.size();

        if (max > 3) {
            max = 3;
        }
        String countryCode = null;
        String ip = null;

        for (int cnt = 0; max > cnt; cnt++) {
            ip = ((String)ipAddressList.get(cnt)).trim();
            countryCode = geoDBService.getCountry(ip);
            if(countryCode != null)
                countryCodesList.add(countryCode);
        }

        return countryCodesList;
    }
    public static boolean isAllowedCountry(GeoDBService geoDBService, HttpRequest request)
    {
        if (checkAllowedCountryOnBSYIP(geoDBService, request)){
            return true;
        }

        List<String> ipAddressList = getIPsListFromXForwardedFor(request);

        if (ipAddressList.isEmpty() ){
            return false;
        }

        int max = ipAddressList.size();

        if (max > 3) {
            max = 3;
        }
        String countryCode = null;
        String ip = null;
        
        boolean isBlacklistedCountry = false;
        
        
        long startTime = System.currentTimeMillis();
        for (int cnt = 0; max > cnt; cnt++) {
            ip = ((String)ipAddressList.get(cnt)).trim();
            countryCode = geoDBService.getCountry(ip);
            if(MusicUtils.isAllowedCountry(countryCode)) {
                return true;   
            } else if(!StringUtils.isBlank(countryCode)) {
                    logger.info("Blacklisting country code and not giving benefit of doubt, Country - "+countryCode+" for IP : "+ip+" : "+ChannelContext.getUid());
                    isBlacklistedCountry = true;
            }
        }
        
        //benefit of doubt
        if(countryCode == null && !isBlacklistedCountry)
        {
            logger.info("Not Blocking - Invalid Country : "+countryCode+" for IP : "+ip+" : "+ ChannelContext.getUid());
            return true;
        }

        logger.info("Blocking - Invalid Country : "+countryCode+" for IP : "+ip+" : "+ChannelContext.getUid());
        return false;
    }

    public static List<String> getIPsListFromXForwardedFor(HttpRequest request) {
        List<String> forwardedForHeaderValues = getXForwardedForHeaderData(request);

        return getIPAddressList(forwardedForHeaderValues);
    }

    private static List<String> getIPAddressList(List<String> forwardedForHeaderValues) {
        List<String> ipAddressList = new ArrayList();

        if (forwardedForHeaderValues == null) {
            return ipAddressList;
        }

        getXForwardedForIPs(forwardedForHeaderValues, ipAddressList);
        return ipAddressList;
    }

    private static void getXForwardedForIPs(List<String> forwardedForHeaderValues, List<String> ipAddressList) {
        for (int i = 0; i < forwardedForHeaderValues.size(); i++) {
            ipAddressList.addAll(Arrays.asList(((String) forwardedForHeaderValues.get(i)).split(",")));

        }
    }

    private static List<String> getXForwardedForHeaderData(HttpRequest request) {
        return request.headers().getAll("x-forwarded-for");
    }

    public static boolean checkAllowedCountryOnBSYIP(GeoDBService geoDBService, HttpRequest request) {
        String ipAddress = request.headers().get("x-bsy-ip");

        if(!StringUtils.isBlank(ipAddress)) {
            if (IPRangeUtil.isAirtelIPRange(ipAddress, request))
                return true;

            String countryCode = geoDBService.getCountry(ipAddress);
            if (MusicUtils.isAllowedCountry(countryCode))
                return true;
        }
        return false;
    }

    public static boolean isAllowedCountry(String countryCode)
    {
        logger.info("Received country code is {}", countryCode);
        if (!StringUtils.isBlank(countryCode) && MusicConstants.ALLOWED_COUNTRY_LIST.contains(countryCode))
            return true;
        return false;
    }

    private static final List<String> SEARCH_KEYWORDS_TO_IGNORE = new ArrayList<String>();
    static{
    	SEARCH_KEYWORDS_TO_IGNORE.add("top [0-9]{1,5} songs? of");
    	SEARCH_KEYWORDS_TO_IGNORE.add("movie songs?");
    	SEARCH_KEYWORDS_TO_IGNORE.add("hit songs? of");
    	SEARCH_KEYWORDS_TO_IGNORE.add("too?p songs? of");
    	SEARCH_KEYWORDS_TO_IGNORE.add("film songs?");
    	SEARCH_KEYWORDS_TO_IGNORE.add("songs? of");
    	SEARCH_KEYWORDS_TO_IGNORE.add("best of");
    	SEARCH_KEYWORDS_TO_IGNORE.add("best song? of");
    	SEARCH_KEYWORDS_TO_IGNORE.add("download song?");
    	SEARCH_KEYWORDS_TO_IGNORE.add("videos? of");
    	SEARCH_KEYWORDS_TO_IGNORE.add("video");
    	SEARCH_KEYWORDS_TO_IGNORE.add("hits? of");
    }

    public static int getDeviceRedisBucketId(String uid)
    {
        int hashCode = Math.abs(uid.hashCode());
        int mod = hashCode % MusicConstants.DID_UID_NUMBER_OF_BUCKETS;
        return mod;
    }
    
    public static String getOriginalImageUrl(String imgUrl)
    {
        if(StringUtils.isBlank(imgUrl))
            return imgUrl;
        if(!imgUrl.contains("ic.bsbportal") || !imgUrl.contains("img.wynk.in"))
            return imgUrl;

        return imgUrl.substring(imgUrl.lastIndexOf("http://"));
    }

    public static int getPlatfromIdFromUid(String uid) {
    	if (StringUtils.isEmpty(uid))
    		return MusicPlatformType.WYNK_DEVICE_BASED.getPlatform();
    	return Integer.parseInt( uid.substring(uid.length()-1));
    }
    
    public static String getAppType() {
    	MusicPlatformType platformType = UserDeviceUtils.getPlatform();
    	return platformType.getAppType();
    }
    
    public static List<String> getKeysFromContext()
    {
        HttpRequest request = ChannelContext.getRequest();
        List<String> keysList = null;
        try {
            Map<String, List<String>> urlParameters = HTTPUtils.getUrlParameters(request.getUri());
            String keys = HTTPUtils.getStringParameter(urlParameters, "keys");
            if(keys != null)
            {
                keysList = Arrays.asList(keys.split("\\s*,\\s*"));
            }
        }
        catch (PortalException e) {
            logger.error("Error getting keys from request in context : " + e.getMessage(), e);
        }
        return keysList;
    }

    public static JSONArray getItemTypesJson(MusicPackageType packageType)
    {
        JSONArray jsonArray = new JSONArray();
        if(packageType == MusicPackageType.FEATURED)
        {
            jsonArray.add(MusicContentType.PACKAGE.name());
        }
        else if((packageType == MusicPackageType.AIRTEL_TOP_25) ||
                (packageType == MusicPackageType.NEW_RELEASES) ||
                (packageType == MusicPackageType.INTERNATIONAL) ||
                (packageType == MusicPackageType.CLASSICS) ||
                (packageType == MusicPackageType.FLASHBACK_90s) ||
                (packageType == MusicPackageType.TAMIL_TOP_100) ||
                (packageType == MusicPackageType.DEVOTIONAL) ||
                (packageType == MusicPackageType.INSTRUMENTAL) ||
                (packageType == MusicPackageType.TRENDING) ||
                (packageType == MusicPackageType.GALAXY_SPECIAL) ||
                (packageType == MusicPackageType.MY_TOP_25) ||
                (packageType == MusicPackageType.REGIONAL) ||
                (packageType == MusicPackageType.USER_DOWNLOADS) ||
                (packageType == MusicPackageType.USER_FAVORITES) ||
                (packageType == MusicPackageType.USER_RENTALS)||
        		(packageType == MusicPackageType.ONDEVICE_SONGS))
        {
            jsonArray.add(MusicContentType.SONG.name());
        }
        else if(packageType == MusicPackageType.USER_COLLECTIONS || (packageType == MusicPackageType.MY_STATION))
        {
            jsonArray.add(MusicContentType.PACKAGE.name());
        }
        else if((packageType == MusicPackageType.TOP_ALBUMS) ||
                (packageType == MusicPackageType.REMIXES) ||
                (packageType == MusicPackageType.CAMPAIGN_MODULE_1) ||
                (packageType == MusicPackageType.CAMPAIGN_MODULE_2)||
        		(packageType == MusicPackageType.CAMPAIGN_MODULE_3)||
				(packageType == MusicPackageType.CAMPAIGN_MODULE_4)||
				(packageType == MusicPackageType.CAMPAIGN_MODULE_5)||
				(packageType == MusicPackageType.CAMPAIGN_MODULE_6)||
				(packageType == MusicPackageType.CAMPAIGN_MODULE_7)||
				(packageType == MusicPackageType.CAMPAIGN_MODULE_8))	
        {
            jsonArray.add(MusicContentType.ALBUM.name());
        }
        else if((packageType == MusicPackageType.BSB_PLAYLISTS) || (packageType == MusicPackageType.TOP_CHARTS) || (packageType == MusicPackageType.ADHM) || (packageType == MusicPackageType.BSB_ADHM_PLAYLISTS)
                || (packageType == MusicPackageType.RECOMMENDED))
        {
            jsonArray.add(MusicContentType.PLAYLIST.name());
        }
        else if(packageType == MusicPackageType.SEARCH_RESULT)
        {
            jsonArray.add(MusicContentType.SONG.name());
            jsonArray.add(MusicContentType.ALBUM.name());
            jsonArray.add(MusicContentType.PLAYLIST.name());
        }
        else if(packageType == MusicPackageType.MOODS)
        {
            jsonArray.add(MusicContentType.MOOD.name());
        }
        else if(packageType == MusicPackageType.RADIOS)
        {
            jsonArray.add(MusicContentType.RADIO.name());
        }
        else if(packageType == MusicPackageType.TRENDING_ARTISTS)
        {
            jsonArray.add(MusicContentType.ARTIST.name());
        }
        else if(packageType == MusicPackageType.USER_PLAYLIST)
        {
            jsonArray.add(MusicContentType.PLAYLIST.name());
        }
        else
        {
            jsonArray.add(MusicContentType.SONG.name());
        }
        return jsonArray;
    }

    public static boolean isInvalidString(String token)
    {
        if(StringUtils.isEmpty(token))
             return false;
        token = token.trim();

        if(token.equalsIgnoreCase(",") ||
                token.equalsIgnoreCase("unknown") || token.equalsIgnoreCase("na")
                || token.equalsIgnoreCase("n/a")
                || token.equalsIgnoreCase("not applicable")
                || token.equalsIgnoreCase("others")
                || token.equalsIgnoreCase("other")
                || token.equalsIgnoreCase("null"))
            return true;

        return false;
    }

    private static Set<String> skipPinMsisnds = new HashSet<>();
    static
    {
        skipPinMsisnds.add("9971099257");
        skipPinMsisnds.add("9971099243");
        skipPinMsisnds.add("9971099365");
        skipPinMsisnds.add("9958747612");
        skipPinMsisnds.add("9971099083");
        skipPinMsisnds.add("9818627961");
        skipPinMsisnds.add("9540890333");
        skipPinMsisnds.add("9717311445");
        
        // MSISDNs outside India for testing
        skipPinMsisnds.add("90238886");
        skipPinMsisnds.add("3107750775");
        skipPinMsisnds.add("3108900469");
        skipPinMsisnds.add("9172395759");
        skipPinMsisnds.add("763400000");
        skipPinMsisnds.add("703022004");
        skipPinMsisnds.add("709589969");
        skipPinMsisnds.add("6173880413");
        skipPinMsisnds.add("9175684379");
        skipPinMsisnds.add("1707922375");
        skipPinMsisnds.add("703522774");
        skipPinMsisnds.add("8515280017");
        
        skipPinMsisnds.add("54791111");
        skipPinMsisnds.add("564868771");
        skipPinMsisnds.add("83390788");
        skipPinMsisnds.add("6507144831");
        skipPinMsisnds.add("6506462078");
        skipPinMsisnds.add("731000639");
        skipPinMsisnds.add("731203333");
        skipPinMsisnds.add("8022229888");
        skipPinMsisnds.add("732905905");
        skipPinMsisnds.add("2017586678");
        
        skipPinMsisnds.add("99441787");
        skipPinMsisnds.add("24244719");
        
        skipPinMsisnds.add("6195497403");
        skipPinMsisnds.add("3475410589");
        skipPinMsisnds.add("99214990");
        skipPinMsisnds.add("91362315");
        skipPinMsisnds.add("93306770");
        
        skipPinMsisnds.add("98900125");
        skipPinMsisnds.add("99862004");
        
        // Nupurs request - 28/03/2016
        skipPinMsisnds.add("97709954");
        skipPinMsisnds.add("99690235");
        skipPinMsisnds.add("99005034");
        skipPinMsisnds.add("99111587");
        skipPinMsisnds.add("95550088");
        skipPinMsisnds.add("99005265");
        skipPinMsisnds.add("9916867362");
        skipPinMsisnds.add("54970002");
        skipPinMsisnds.add("54239777");

        //Nupurs request - 13/06/2016
        skipPinMsisnds.add("5497002");
        skipPinMsisnds.add("54791111");
        skipPinMsisnds.add("54991508");
        skipPinMsisnds.add("54949033");
        skipPinMsisnds.add("54771009");

        // MoodAgent users
        skipPinMsisnds.add("4553611936");
        skipPinMsisnds.add("4561677577");
        skipPinMsisnds.add("4526704005");
        skipPinMsisnds.add("4591488647");
        
        // ashok ganapathy to be by passed for OTP
        skipPinMsisnds.add("9987000098");

        //AnkitG request for build sanity by Apple - Non Airtel User
        skipPinMsisnds.add("7834929728");

        //for content operations team to check the changes they will make in different circles.
        skipPinMsisnds.add("8111111111");
        skipPinMsisnds.add("8222222222");
        skipPinMsisnds.add("8333333333");
        skipPinMsisnds.add("8444444444");
        skipPinMsisnds.add("8555555555");
        skipPinMsisnds.add("8666666666");
        skipPinMsisnds.add("8777777777");
        skipPinMsisnds.add("8888888888");
        skipPinMsisnds.add("9999124833");

        // Ankit - Second sim.
        skipPinMsisnds.add("9650644451");

        //Ankit's request for build sanity by Mayanmar user - Oreedo
//        skipPinMsisnds.add("95419435");

        //test number for mayanmar Oreedo
        skipPinMsisnds.add("9717477255");

        //test numbers for robi-telecom
        skipPinMsisnds.add("1817180007");
        skipPinMsisnds.add("1833182446");
        skipPinMsisnds.add("1833182463");

        //test numbers for Srilanka telecom
        skipPinMsisnds.add("718440316");
        skipPinMsisnds.add("718440340");
    }

    public static boolean isTestSIM(String msisdn)
    {
        if(StringUtils.isEmpty(msisdn))
            return false;
        msisdn = Utils.getTenDigitMsisdnWithoutCountryCode(msisdn);
        if(skipPinMsisnds.contains(msisdn))
            return true;
        return false;
    }


    private static Set<String> geoBlockDisabledMsisnds = new HashSet<>();
    static
    {
        geoBlockDisabledMsisnds.add("9971099364");
        geoBlockDisabledMsisnds.add("9971099674");
        geoBlockDisabledMsisnds.add("9971099128");
        geoBlockDisabledMsisnds.add("9971099118");
        geoBlockDisabledMsisnds.add("9971099105");
        geoBlockDisabledMsisnds.add("9971099224");
        geoBlockDisabledMsisnds.add("9971099175");
        geoBlockDisabledMsisnds.add("9650567451");
        
        // MSISDNs outside India for testing
        geoBlockDisabledMsisnds.add("90238886");
        geoBlockDisabledMsisnds.add("3107750775");
        geoBlockDisabledMsisnds.add("3108900469");
        geoBlockDisabledMsisnds.add("9172395759");
        geoBlockDisabledMsisnds.add("763400000");
        geoBlockDisabledMsisnds.add("703022004");
        geoBlockDisabledMsisnds.add("709589969");
        geoBlockDisabledMsisnds.add("6173880413");
        geoBlockDisabledMsisnds.add("9175684379");
        geoBlockDisabledMsisnds.add("1707922375");
        geoBlockDisabledMsisnds.add("703522774");
        geoBlockDisabledMsisnds.add("8515280017");
        
        geoBlockDisabledMsisnds.add("54791111");
        geoBlockDisabledMsisnds.add("564868771");
        geoBlockDisabledMsisnds.add("83390788");
        geoBlockDisabledMsisnds.add("6507144831");
        geoBlockDisabledMsisnds.add("6506462078");
        geoBlockDisabledMsisnds.add("731000639");
        geoBlockDisabledMsisnds.add("731203333");
        geoBlockDisabledMsisnds.add("8022229888");
        geoBlockDisabledMsisnds.add("732905905");
        geoBlockDisabledMsisnds.add("2017586678");
        
        geoBlockDisabledMsisnds.add("99441787");
        geoBlockDisabledMsisnds.add("24244719");
        
        geoBlockDisabledMsisnds.add("6195497403");
        geoBlockDisabledMsisnds.add("3475410589");
        geoBlockDisabledMsisnds.add("99214990");
        geoBlockDisabledMsisnds.add("91362315");
        geoBlockDisabledMsisnds.add("93306770");
        
        geoBlockDisabledMsisnds.add("98900125");
        geoBlockDisabledMsisnds.add("99862004");

        // Nupurs request - 28/03/2016
        geoBlockDisabledMsisnds.add("97709954");
        geoBlockDisabledMsisnds.add("99690235");
        geoBlockDisabledMsisnds.add("99005034");
        geoBlockDisabledMsisnds.add("99111587");
        geoBlockDisabledMsisnds.add("95550088");
        geoBlockDisabledMsisnds.add("99005265");
        geoBlockDisabledMsisnds.add("9916867362");
        geoBlockDisabledMsisnds.add("54970002");
        geoBlockDisabledMsisnds.add("54239777");

        //Nupurs request - 13/06/2016
        geoBlockDisabledMsisnds.add("5497002");
        geoBlockDisabledMsisnds.add("54791111");
        geoBlockDisabledMsisnds.add("54991508");
        geoBlockDisabledMsisnds.add("54949033");
        geoBlockDisabledMsisnds.add("54771009");

        // MoodAgent users
        geoBlockDisabledMsisnds.add("4553611936");
        geoBlockDisabledMsisnds.add("4561677577");
        geoBlockDisabledMsisnds.add("4526704005");
        geoBlockDisabledMsisnds.add("4591488647");

        //AnkitG request for build sanity by Apple - Non Airtel User
        geoBlockDisabledMsisnds.add("7834929728");

        geoBlockDisabledMsisnds.add("9650644451");

        //Ankit's request for build sanity by Mayanmar user - Oreedo
//        geoBlockDisabledMsisnds.add("95419435");

        //test number for mayanmar Oreedo
        geoBlockDisabledMsisnds.add("9717477255");

        //test numbers for robi-telecom
        geoBlockDisabledMsisnds.add("1817180007");
        geoBlockDisabledMsisnds.add("1833182446");
        geoBlockDisabledMsisnds.add("1833182463");

        //test numbers for Srilanka telecom
        geoBlockDisabledMsisnds.add("718440316");
        geoBlockDisabledMsisnds.add("718440340");

        //WMB-3503 : Shared with Google team : android tv app
        geoBlockDisabledMsisnds.add("9971099257");
    }
    public static boolean isGeoBlockingDisabled(String msisdn)
    {
        if(StringUtils.isEmpty(msisdn))
            return false;
        msisdn = Utils.getTenDigitMsisdnWithoutCountryCode(msisdn);
        if(geoBlockDisabledMsisnds.contains(msisdn))
            return true;
        return false;
    }

    private static Set<String> fpmetaDefaultUid = new HashSet<>();
    static
    {
        fpmetaDefaultUid.add("21lvMICtpiwICTAHttLhmCoDcDw2");
        fpmetaDefaultUid.add("CJmm5BH-x6Cui8mEe0");
    }

    public static boolean isFPMetaDefaultUid(String uid)
    {
        if(StringUtils.isEmpty(uid))
            return false;

        if(fpmetaDefaultUid.contains(uid))
            return true;
        return false;
    }

    public static long getTimestampInMS(long timestamp)
    {
        if(timestamp > 1388534401000L)
            return timestamp;
        return timestamp*1000;
    }

    public static String generateSignature(String httpVerb, String requestUri, String payload, long requestTimestamp, String secret) {
        String signature = StringUtils.EMPTY;
        String digestString = new StringBuilder(httpVerb).append(requestUri).append(payload).append(requestTimestamp).toString();
        try {
            signature = EncryptUtils.calculateRFC2104HMAC(digestString, secret);
        }
        catch (SignatureException e) {
            logger.error("Error while computing the signature, ERROR: {}", e.getMessage(), e);
        }
        return signature;
    }

    public static String decryptParamFromRequestUri(String encryptionKey, String requestUri, String param) {
        String value = StringUtils.EMPTY;
		try {
			Map<String, List<String>> urlParameters = HTTPUtils.getUrlParameters(requestUri);
	    	String encryptedVal = HTTPUtils.getStringParameter(urlParameters,param);
	    	if(StringUtils.isNotBlank(encryptedVal)) {
                value = EncryptUtils.decrypt(encryptedVal, encryptionKey);
            }
		} catch(Exception e) {
			logger.error("Exception while decrypting uid from subscription url ", e);
		}
		return value;
    }
    
    public static String encryptAndEncodeParam(String encryptionKey, String param) {
    	if(StringUtils.isBlank(param)) {
    		return StringUtils.EMPTY;
    	}
        try {
        	param = EncryptUtils.encrypt(param, encryptionKey);
        	param = URLEncoder.encode(param);
		} catch (Exception e) {
			logger.error("Exception while encrypting uid for subscription redirection ", e);
		}
    	return param;
    }

    public static MusicRequestSource getRequestSourceType() {
    	return StringUtils.isEmpty(ChannelContext.getRequest().headers().get(MusicConstants.MUSIC_HEADER_WAP)) ? MusicRequestSource.APP : MusicRequestSource.WAP;
    }

    public static boolean isDevEnv(HttpRequest request)
    {
        String env = request.headers().get(MusicConstants.MUSIC_HEADER_DEV);
        return (!StringUtils.isBlank(env) && env.toLowerCase().startsWith("dev"));
    }


    public static List getUniqueArtistList(String artists, List<String> existingArtists)
    {

        List<String> artistList = new ArrayList<>();
        LinkedHashSet<String> artistSet = new LinkedHashSet<>();
        if(!CollectionUtils.isEmpty(existingArtists)) {

            //note: intentionally not using list.addAll to cleanup the element in the process
            for (int i = 0; i < existingArtists.size(); i++) {
                Object unnormalizedName = existingArtists.get(i);
                if(!(unnormalizedName instanceof String))
                    continue;
                String artistName = normalizeArtistName((String)unnormalizedName);
                if(!org.apache.commons.lang.StringUtils.isBlank(artistName))
                    artistSet.add(artistName);

            }
        }
        if(!org.apache.commons.lang3.StringUtils.isBlank(artists)) {

            artists = artists.trim();

            //handle when its already a CSV of artists
            if (artists.contains(",")) {
                if (artists.startsWith(",")) {
                    artists = artists.substring(1);
                    artists = artists.trim();
                }
                String[] contentArtists = artists.split(",");
                for (int i = 0; i < contentArtists.length; i++) {
                    String contentArtist = contentArtists[i];
                    String normalizeArtistName = normalizeArtistName(contentArtist);
                    if(!org.apache.commons.lang.StringUtils.isBlank(normalizeArtistName))
                        artistSet.add(normalizeArtistName);
                }
            } else {
                String normalizeArtistName = normalizeArtistName(artists);
                if(!org.apache.commons.lang.StringUtils.isBlank(normalizeArtistName))
                    artistSet.add(normalizeArtistName);
            }
        }

        artistList.addAll(artistSet);
        return artistList;
    }


    public static String normalizeArtistName(String artist){
    	if(org.apache.commons.lang3.StringUtils.isEmpty(artist)){
    		return "";
    	}
    	StringBuilder sb = new StringBuilder();
	    for(int i=0;i<artist.length();i++) {
	        char ch = artist.charAt(i);
	        if (Character.isLetterOrDigit(ch) || Character.isWhitespace(ch))
	            sb.append(ch);
	    }
	    String sbs = sb.toString();
	    sbs = sbs.replaceAll("\\s+", " ");
	    return sbs;
    }


    public static String getLargeResizedImage(String imageUrl) {
        if(org.apache.commons.lang.StringUtils.isEmpty(imageUrl))
            return imageUrl;
        int width=600; int height=600;
        imageUrl = imageUrl.replace(" ","%20");
        return "http://img.wynk.in/unsafe/" + width + "x" + height + "/top/" + imageUrl;
    }

    public static String getMediumResizedImage(String imageUrl) {
        if(org.apache.commons.lang.StringUtils.isEmpty(imageUrl))
            return imageUrl;
        int width=300; int height=300;
        imageUrl = imageUrl.replace(" ","%20");
        return "http://img.wynk.in/unsafe/" + width + "x" + height + "/top/" + imageUrl;
    }

    public static String getSmallResizedImage(String imageUrl) {
        if(org.apache.commons.lang.StringUtils.isEmpty(imageUrl))
            return imageUrl;
        int width=200; int height=200;
        imageUrl = imageUrl.replace(" ","%20");
        return "http://img.wynk.in/unsafe/" + width + "x" + height + "/top/" + imageUrl;
    }
    public static String getResizedImage(String imageUrl, int width, int height) {
        if(org.apache.commons.lang.StringUtils.isEmpty(imageUrl))
            return imageUrl;
        if(imageUrl.contains("ic.bsbportal.com") || imageUrl.contains("img.wynk.in"))
            return imageUrl;
        //todo:
        
        // TODO - REMOVING BELOW LINE TO MAKE S3 AS ORIGIN OF THUMBOR
        // imageUrl = imageUrl.replace("s3-ap-southeast-1.amazonaws.com/bsbcms","d2n2xdxvkri1jk.cloudfront.net");
        imageUrl = imageUrl.replace(" ","%20");
        return "http://img.wynk.in/unsafe/" + width + "x" + height + "/top/" + imageUrl;
    }

 // Simple conversion of msisdn to base64 but adding a digit in middle of it
  	public static String encryptMSISDN(String valueToEncrypt)
     {
         try {
             if (null != valueToEncrypt && "-" != valueToEncrypt)
             {
            	 if (!valueToEncrypt.startsWith("+")){
            	 if ((valueToEncrypt.trim().length() == 10)){
         			valueToEncrypt="+91".concat(valueToEncrypt);
         		 }
            	 if ((valueToEncrypt.trim().length() == 12)){
          			valueToEncrypt="+".concat(valueToEncrypt);
          		 }
            	 }
                 StringBuilder modifiedMSISDN = new StringBuilder("");
                 modifiedMSISDN = modifiedMSISDN.append(valueToEncrypt.substring(0, valueToEncrypt.length()/2))
                                  .append("9").append(valueToEncrypt.substring(valueToEncrypt.length()/2,valueToEncrypt.length()));

                 byte[] encryptedValue = Base64.encodeBase64(modifiedMSISDN.toString().getBytes());
                 String result = new String(encryptedValue,"UTF-8");
                 return result;
             }
         } catch (UnsupportedEncodingException e) {
             logger.error("Error encrypting msisdn : "+e.getMessage(),e);
         }
         return valueToEncrypt;
      }
  	
  	 public static boolean isOnAirtelMobile(HttpRequest request, String msisdnStr) {
         String msisdnFromRequest = UserDeviceUtils.getNetworkEnrichedMsisdn(request);
         if(StringUtils.isNotBlank(msisdnFromRequest))
             msisdnFromRequest = Utils.getTenDigitMsisdn(msisdnFromRequest);
         
         if(StringUtils.isNotBlank(msisdnStr))
             msisdnStr = Utils.getTenDigitMsisdn(msisdnStr);
         
         boolean isOnAirtelMobile = false;
         if(StringUtils.isNotBlank(msisdnFromRequest) && StringUtils.isNotBlank(msisdnStr))
         {
             if(msisdnFromRequest.equals(msisdnStr))
                 isOnAirtelMobile = true;
         }
         if(UserDeviceUtils.isWifi(request))
             isOnAirtelMobile = false;
         return isOnAirtelMobile;
     }
  	 
    public static String generateDNDKey(String msisdn) {
        String keyPart = msisdn.substring(0, 7);
        return DND_REDIS_KEY+keyPart;
    }

    public static boolean isSamsungDevice(){
        boolean isSamsungDevice = false;
        HttpRequest samsungHeader = ChannelContext.getRequest();
        if(samsungHeader!= null && samsungHeader.headers().get("x-bsy-medium")!=null && samsungHeader.headers().get("x-bsy-medium").equals("samsung")){
            isSamsungDevice = true;
        }

        return isSamsungDevice;
    }

    public static JSONArray getAvailableCountryList(List<String> countryCodes) {
        JSONArray jsonArray = new JSONArray();
        List<CountryDetails> countryDetails = preparedCountryDetailsList();

        //Give priority to first country
        //Sri lanka Object
        if (!CollectionUtils.isEmpty(countryCodes)) {
            String intialCountry = countryCodes.get(0);
            for (int i = 0; i < countryDetails.size(); i++) {
                if (countryDetails.get(i).getIsoCode().equals(intialCountry)){
                    Collections.swap(countryDetails,0,i);
                }
            }
        }
        countryDetails.forEach(countryDetail -> {
            jsonArray.add(countryDetail.toJson());
        });
        return jsonArray;
    }


    public static boolean isSriLankaMsisdn(String msisdn) {
        if (StringUtils.isNotEmpty(msisdn) && ((msisdn.startsWith("+94") && msisdn.length() == 12) || (msisdn.startsWith("94") && msisdn.length() == 11))) {
            return true;
        }
        return false;
    }

    public static boolean isSingaporeMsisdn(String msisdn) {
        logger.info("Provided msisdn to check for isSingaporeMsisdn : {}", msisdn);
        if (StringUtils.isNotEmpty(msisdn) &&
                ((msisdn.startsWith("+65") && msisdn.length() == 11) ||
                        (msisdn.startsWith("65") && msisdn.length() == 10) ||
                        msisdn.length() == 8)) {
            return true;
        }
        return false;
    }

    public static Optional<CountryDetails> getCountryDetailsBasedOnCountry(List<CountryDetails> list, String country) {
        logger.info("Getting feature details for country {}", country);
        return list.stream().filter(countryDetails -> countryDetails.getIsoCode().equalsIgnoreCase(country)).findAny();
    }

    public static List<CountryDetails> preparedCountryDetailsList() {
        List<CountryDetails> countryDetails = new ArrayList<>();
        countryDetails.add(new CountryDetails("+91", "IN", "India", true, true, 10,"http://img.wynk.in/unsafe/http://s3-ap-south-1.amazonaws.com/wynk-music-cms/app/flags/india.png", true, true, true));
        countryDetails.add(new CountryDetails("+94", "LK", "Sri Lanka", false, false, 9,"http://img.wynk.in/unsafe/http://s3-ap-south-1.amazonaws.com/wynk-music-cms/app/flags/lanka.png", false, false, true));
       if(MusicBuildUtils.isInternationalSupportedBuild())
        {
        countryDetails.add(new CountryDetails("+65", "SG", "Singapore", true, false, 8,"https://img.wynk.in/unsafe/http://s3-ap-south-1.amazonaws.com/wynk-music-cms/app/flags/singapore.png", false,false, false));
        }
        return countryDetails;
    }


}
