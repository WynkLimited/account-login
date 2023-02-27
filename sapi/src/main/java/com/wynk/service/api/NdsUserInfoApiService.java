package com.wynk.service.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wynk.config.MusicConfig;
import com.wynk.dto.NdsUserInfo;
import com.wynk.user.dto.UserProfile;
import com.wynk.server.ChannelContext;
import com.wynk.utils.HttpClient;
import com.wynk.utils.Utils;
import com.wynk.common.Circle;
import io.netty.handler.codec.http.HttpHeaders;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cassandra.core.WriteOptions;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;

//import com.google.common.cache.CacheBuilder;
//import com.google.common.cache.CacheLoader;
//import com.google.common.cache.LoadingCache;

@Service
public class NdsUserInfoApiService {

    private Logger logger        = LoggerFactory.getLogger(NdsUserInfoApiService.class.getCanonicalName());

    private final String baseRestUrl   = "http://125.19.42.99:3131/v1/ndsuserinfo/";
    
    private final String tpRestUrl   = "http://125.16.71.168:8080/v1/ndsuserinfo/";

    private final String opRestUrl     = "http://125.21.246.72/v1/operator/userinfo";

    private RestTemplate restApi       = new RestTemplate();

    private Gson gson          = new GsonBuilder().disableHtmlEscaping().create();

    private static final int    cTimeout      = 2000;

    public static final NdsUserInfo emptyNDSUserInfo = new NdsUserInfo();
    public static final NdsUserInfo fallbackNDSUserInfo = new NdsUserInfo(Circle.ALL.getCircleName().toLowerCase());

    private final String emptyUserInfo = gson.toJson(emptyNDSUserInfo);

    private final String WCF_BSIC_AUTH = "Basic dTEwMTpwMTAx";

    @Value("${hystrix.wcf.nds.timeout}")
    public int NDS_EXECUTION_TIMEOUT = 300;

    @Autowired
    private MusicConfig musicConfig;
    
    private CassandraOperations userCassandraTemplate;

    private WriteOptions options       = new WriteOptions();
    
    public static NdsUserInfoApiService instance;

    // private LoadingCache<String, String> ndsUserInfoCache =
    // CacheBuilder.newBuilder().maximumSize(100000).build(new CacheLoader<String, String>() {
    //
    // public String load(String key) throws Exception {
    // return _getNdsUserInfo(key);
    // }
    // });

    public NdsUserInfoApiService(CassandraOperations userCassandraTemplate) {
        this.userCassandraTemplate = userCassandraTemplate;
    }

    public NdsUserInfoApiService() {

    }

    @PostConstruct
    public void setRestApiTimeout() {
        SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restApi.getRequestFactory();
        rf.setReadTimeout(cTimeout);
        rf.setConnectTimeout(cTimeout);
        options.setTtl(60 * 60 * 2);
    }

    public NdsUserInfo getNdsUserInfoObj(String msisdn) {
        if (StringUtils.isEmpty(msisdn)) {
            return emptyNDSUserInfo;
        }
        try {
            return _getNdsUserInfo2(msisdn);
        } catch (Throwable thr) {
            logger.error("Error fetching user info from NDS for msisdn [{}]", msisdn, thr);
            return emptyNDSUserInfo;
        }
    }

    public NdsUserInfo getNdsUserInfoFromWCFCache(String msisdn) {
        NdsUserInfo ndsUserInfo = null;
        if (StringUtils.isEmpty(msisdn)) {
            return emptyNDSUserInfo;
        }
        String validMsisdn=Utils.getTenDigitMsisdnWithoutCountryCode(msisdn);
        try {
            String wcfNDSURL = musicConfig.getWcfNDSCache() + "v2/ndsuserinfo/user?client=WCF&msisdn=" + validMsisdn ;
            logger.info("owcfNDSURL url : " + wcfNDSURL);
            HashMap<String, String> headers = new HashMap<>();
            headers.put(HttpHeaders.Names.AUTHORIZATION, WCF_BSIC_AUTH);
            long st = System.currentTimeMillis();
            String data = HttpClient.getContent(wcfNDSURL, 2500, headers);
            logger.info("NDS WCF cache hit URL " + wcfNDSURL + " , time taken : " + (System.currentTimeMillis() - st) + " Response : " + data);
            if(StringUtils.isEmpty(data))
                return emptyNDSUserInfo;
            JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(data);
            ndsUserInfo = new NdsUserInfo();
            ndsUserInfo.fromJsonObject(jsonObject);

            if (StringUtils.isEmpty(ndsUserInfo.getDataRating())) {
                ndsUserInfo.setDataRating("DATA_NON_PACK");
            }
            ndsUserInfo.setErrorCode(null);
            
            
            if ("unknown".equalsIgnoreCase(ndsUserInfo.getCircle()))
            	return emptyNDSUserInfo;
            
            return ndsUserInfo;

        }
        catch (Exception e) {
            logger.error("[NDS] Error fetching user info from WCF cache for msisdn [{}]", validMsisdn + " error : " + e .getMessage());
            return emptyNDSUserInfo;
        }
    }
    public NdsUserInfo getNdsUserInfoViaHystrix(String msisdn) throws ParseException {
        if (StringUtils.isEmpty(msisdn)) {
            return emptyNDSUserInfo;
        }
        String wcfNDSURL = musicConfig.getWcfNDSCache() + "v2/ndsuserinfo/user?client=WCF&msisdn=" + msisdn ;
        logger.info("owcfNDSURL url : " + wcfNDSURL);
        HashMap<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.Names.AUTHORIZATION, WCF_BSIC_AUTH);
        long st = System.currentTimeMillis();
        String data;
        try{
            data = HttpClient.getNdsContent(wcfNDSURL, NDS_EXECUTION_TIMEOUT, headers);
        }
        catch (HttpException ex){
            logger.info("received HTTP exception in nds for user "+msisdn ,ex );
            return emptyNDSUserInfo;
        }
        logger.info("NDS WCF cache hit URL " + wcfNDSURL + " , time taken : " + (System.currentTimeMillis() - st) + " Response : " + data);
        if(StringUtils.isEmpty(data))
            throw new RuntimeException("WCF NDS api failed for "+ msisdn);
        JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(data);
        NdsUserInfo ndsUserInfo = new NdsUserInfo();
        ndsUserInfo.fromJsonObject(jsonObject);

        if (StringUtils.isEmpty(ndsUserInfo.getDataRating())) {
            ndsUserInfo.setDataRating("DATA_NON_PACK");
        }
        ndsUserInfo.setErrorCode(null);

        if ("unknown".equalsIgnoreCase(ndsUserInfo.getCircle()))
            return emptyNDSUserInfo;

        return ndsUserInfo;
    }

    public String getNdsUserInfo(String msisdn) {
        return gson.toJson(getNdsUserInfoFromWCFCache(msisdn));
    }

    public String getEmptyUserInfo() {
        return emptyUserInfo;
    }

    private NdsUserInfo _getNdsUserInfo2(String msisdn) {
		msisdn = Utils.getTenDigitMsisdn(msisdn);
        NdsUserInfo ndsUserInfo = null;
        boolean store = false;
        try {
            String uuid = Utils.getUUIDFromMsisdnWithoutEx(msisdn, msisdn);
            if (musicConfig.isAirtelStoreEnable()) {
            	if(musicConfig.isEnableNdsBaseUrl()){
            		ndsUserInfo = restApi.getForObject(tpRestUrl + "user?msisdn=" + msisdn + "&client=" + musicConfig.getNdsClient(), NdsUserInfo.class);
            	}else{
            		ndsUserInfo = restApi.getForObject(opRestUrl + "?msisdn=" + msisdn, NdsUserInfo.class);
            	}
            } else {
                if (null != userCassandraTemplate) {
                    UserProfile profile = userCassandraTemplate.selectOne("select * from userOperatorInfo where uid='" + uuid + "'", UserProfile.class);
                    if (null != profile) {
                        ndsUserInfo = UserProfile.toNdsUserInfo(profile);
                    }

                    boolean forceUpdateNDSInfo = ChannelContext.getRequestContext().isForceUpdateNDSInfo();
                    if (null == ndsUserInfo || profile.update(forceUpdateNDSInfo, 24 * 7)) {
                        NdsUserInfo newNdsUserInfo = restApi.getForObject(tpRestUrl + "user?msisdn=" + msisdn, NdsUserInfo.class);
                        if (newNdsUserInfo != null) {
                            ndsUserInfo = newNdsUserInfo;
                        }
                        store = true;
                    }
                }
            }

            if (ndsUserInfo != null) {
                if (StringUtils.isEmpty(ndsUserInfo.getDataRating())) {
                    ndsUserInfo.setDataRating("DATA_NON_PACK");
                }
                if (null != userCassandraTemplate && store) {
                    UserProfile profile = UserProfile.newUserProfileFromNdsUserInfo(ndsUserInfo, uuid);
                    userCassandraTemplate.insert(profile);
                }

                ndsUserInfo.setErrorCode(null);
                ndsUserInfo.setGender(null);
                return ndsUserInfo;
            }
        } catch (Throwable thr) {
            logger.error("Error fetching user info from NDS for msisdn [{}]", msisdn, thr);
        }
        return emptyNDSUserInfo;
    }

	public NdsUserInfo getEmptyNDSUserInfo() {
		return emptyNDSUserInfo;
	}

	public static NdsUserInfoApiService getInstance() {
		return instance;
	}

	public static void setInstance(NdsUserInfoApiService instance) {
		NdsUserInfoApiService.instance = instance;
	}
	
	public String getUserCircleFromNDS(String msisdn, String circle) {
        NdsUserInfo user = getNdsUserInfoFromWCFCache(msisdn);
        return getUserCircleFromNDS(msisdn, user, circle);
    }

    public String getUserCircleFromNDS(String msisdn, NdsUserInfo user, String circle) {
        String ndsCircle = user.getCircle();
        logger.info(String.format("msisdn : %s circle : %s ndsCircle : %s", msisdn, circle, ndsCircle));
        if(!StringUtils.isEmpty(ndsCircle)) {
            String userCircle = Circle.getCircleShortName(ndsCircle);
            if(!StringUtils.isEmpty(userCircle) && !userCircle.equalsIgnoreCase("all")) {
                return userCircle;
            }
        }
        return circle;
    }
    
}
