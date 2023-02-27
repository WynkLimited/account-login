package com.wynk.service.api;

import com.wynk.common.Circle;
import com.wynk.common.PortalException;
import com.wynk.config.MusicConfig;
import com.wynk.dto.*;
import com.wynk.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.WriteOptions;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class DataPackApiService {

    private final Logger logger      = LoggerFactory.getLogger(DataPackApiService.class.getCanonicalName());

    private final Gson gson        = new GsonBuilder().disableHtmlEscaping().create();

    private final String baseRestUrl = "http://125.16.71.167:8080/v1/datapack/";

    private final RestTemplate restApi     = new RestTemplate();

    private final RestTemplate restRecoApi = new RestTemplate();

    private static final int                                cKeyExpiry  = 240 * 60;

    private static final int                                dKeyExpiry  = 2400 * 60;

    private static final Integer ONE_MONTH   = 30 * 24 * 60 * 60;

    private final int                                       cTimeout    = 1500;

    private final ConcurrentMap<String, Future<DataPack[]>> futureCache = new ConcurrentHashMap<>();

    private final int                                       recoConnTimeout = 2000;
    private final int                                       recoSoTimeout = 4000;

    private final ExecutorService exec        = Executors.newCachedThreadPool();

    @Autowired
    private NdsUserInfoApiService                           ndsUserInfoApiService;

    @Autowired
    private MusicConfig                                     musicConfig;

    private ConcurrentMap<String, String> packsCache  = new ConcurrentLinkedHashMap.Builder<String, String>().maximumWeightedCapacity(200000).build();

    private CassandraOperations userCassandraTemplate;

    private WriteOptions dataUsageWriteOptions;
    
    public static DataPackApiService instance;

    public DataPackApiService(CassandraOperations userCassandraTemplate) {
        this.userCassandraTemplate = userCassandraTemplate;
    }

    public DataPackApiService() {

    }

    @PostConstruct
    public void setRestApiTimeout() {
        SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restApi.getRequestFactory();
        rf.setReadTimeout(cTimeout);
        rf.setConnectTimeout(cTimeout);
        SimpleClientHttpRequestFactory recoRf = (SimpleClientHttpRequestFactory) restRecoApi.getRequestFactory();
        recoRf.setReadTimeout(recoSoTimeout);
        recoRf.setConnectTimeout(recoConnTimeout);
        dataUsageWriteOptions = new WriteOptions();
        dataUsageWriteOptions.setTtl(ONE_MONTH);
    }

    public String getDataPacksRecommendation(String msisdn, String circle, boolean threeG, int count, String lang, boolean music) {
        long starttime = System.currentTimeMillis();
        DataPack[] dataPacks = new DataPack[0];

        NdsUserInfo user = ndsUserInfoApiService.getNdsUserInfoFromWCFCache(msisdn);

        circle = getUserCircleFromNDS(msisdn, user, circle);

        long endtime = System.currentTimeMillis();
        long ndsCalltime = endtime - starttime;
        starttime = endtime;
        String defaultPacksCacheKey = circle.toLowerCase() + "-" + threeG + "-" + user.getUserType();
        logger.info("getDataPacksRecommendation called for msisdn : " + msisdn + ", circle : " + circle + ", threeG : " + threeG + ", count : " + count + ", lang : " + lang);
        try {
            String userCache = getUsersCachedPacks(msisdn);
            endtime = System.currentTimeMillis();
            long cacheReadTime = endtime - starttime;
            starttime = endtime;
            logger.info("profiler : " + msisdn + " nds : " + ndsCalltime + " cacheRead : " + cacheReadTime);
            logger.info("user cache for msisdn " + msisdn + " : " + userCache);
            if(!StringUtils.isEmpty(userCache)) {
                if("[]".equals(userCache) || "null".equals(userCache)) {
                    throw new PortalException("Empty cache for : " + msisdn);
                }
                return userCache;
            }
            String url = baseRestUrl + "recommend?msisdn=" + msisdn + "&circle=" + circle + "&threeG=" + threeG + "&count=" + count + "&type=" + user.getUserType();
            dataPacks = getAsyncDataPacks(url, getUsersPacksCacheKey(msisdn), defaultPacksCacheKey, lang, music);
            endtime = System.currentTimeMillis();
            long asyncTime = endtime - starttime;
            starttime = endtime;
            logger.info("profiler : getAsyncDataPacks " + msisdn + " time :" + asyncTime);
        }
        catch (Exception ex) {
            logger.error("getDataPacksRecommendation exception for msisdn : " + msisdn + " Exception : " + ex.getMessage(), ex);
            String cachedPacks = getPacksForKey(defaultPacksCacheKey);
            if(!StringUtils.isEmpty(cachedPacks)) {
                endtime = System.currentTimeMillis();
                logger.info("profiler : " + msisdn + " defaultCacheReadTime :" + (endtime - starttime));
                return cachedPacks;
            }
        }
        long trueStart = System.currentTimeMillis();
        String userCacheStr = getDataPacksJson(dataPacks);// gson.toJson(dataPacks);
        endtime = System.currentTimeMillis();
        logger.info("profiler : " + msisdn + " gson.toJson(dataPacks) : " + (endtime - trueStart));
        return userCacheStr;
    }

    private String getDataPacksJson(DataPack[] dataPacks) {
        StringBuilder sb = new StringBuilder("[");
        if(null != dataPacks) {
            for(int i = 0; i < dataPacks.length; i++) {
                DataPack dp = dataPacks[i];
                sb.append("{");
                sb.append("\"keyword\":\"" + dp.getKeyword() + "\",");
                sb.append("\"packDataLimit\":\"" + dp.getPackDataLimit() + "\",");
                sb.append("\"packPrice\":" + dp.getPackPrice() + ",");
                sb.append("\"packType\":\"" + dp.getPackType() + "\",");
                sb.append("\"packValidity\":\"" + dp.getPackValidity() + "\",");
                sb.append("\"purchaseUrl\":\"" + dp.getPurchaseUrl() + "\",");
                sb.append("\"thumbnailUrl\":\"" + dp.getThumbnailUrl() + "\",");
                sb.append("\"title\":\"" + dp.getTitle() + "\"}");
                if(i < dataPacks.length - 1) {
                    sb.append(",");
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public NdsUserInfo getUserFromNDS(String msisdn) {
		msisdn = Utils.getTenDigitMsisdn(msisdn);
        return ndsUserInfoApiService.getNdsUserInfoFromWCFCache(msisdn);
    }

    public String getUserCircleFromNDS(String msisdn, String circle) {
        NdsUserInfo user = ndsUserInfoApiService.getNdsUserInfoFromWCFCache(msisdn);
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

    public DataUsage getDataUsage(String msisdn) {
        DataUsage dataUsage = null;
        NdsUserInfo user = ndsUserInfoApiService.getNdsUserInfoFromWCFCache(msisdn);
        try {
            String url = baseRestUrl + "usage?msisdn=" + msisdn + "&type=" + user.getUserType();
            dataUsage = restApi.getForObject(url, DataUsage.class);
            if(null != userCassandraTemplate) {
                DataUsageHistory snapshot = DataUsageHistory.newDataUsageSnapshot(dataUsage, msisdn);
                userCassandraTemplate.insert(snapshot, dataUsageWriteOptions);
            }
        }
        catch (Exception ex) {
            logger.error("Exception in getting data usage for msisdn : " + msisdn + ". " + ex.getMessage());
            if(null != userCassandraTemplate) {
                dataUsage = DataUsageHistory.toDataUsage(userCassandraTemplate.selectOne("select * from dataUsageHistory where msisdn = '" + msisdn + "' order by captureTimestamp DESC limit 1",
                        DataUsageHistory.class));
            }
        }
        return dataUsage;
    }

    public MagicProvisioningResponse provisionDataPack(String msisdn, String packId, String circle, boolean deactivate) {
        circle = getUserCircleFromNDS(msisdn, circle);
        String url = baseRestUrl + "provision?msisdn=" + msisdn + "&packId=" + packId + "&circle=" + circle.toLowerCase() + "&deactivate=" + deactivate;
        System.out.println("DataUsageUrl : " + url);
        return restApi.getForObject(url, MagicProvisioningResponse.class);
    }

    public String createDCUrl(Map<String, List<String>> urlParameters) {
        StringBuilder sb = new StringBuilder("http://125.21.241.25/wps/portal/datapackconsent");
        return appendParamsToUrl(urlParameters, sb);
    }

    public String createHTRedirectUrl(Map<String, List<String>> urlParameters) {
        StringBuilder sb = new StringBuilder("http://125.21.246.73/dcg/htcallback.html");
        return appendParamsToUrl(urlParameters, sb);
    }

    private String appendParamsToUrl(Map<String, List<String>> urlParameters, StringBuilder url) {
        url.append("?");
        for(Map.Entry<String, List<String>> en : urlParameters.entrySet()) {
            if("msisdn".equals(en.getKey())) {
                continue;
            }
            url.append(en.getKey());
            url.append("=");
            if(null != en.getValue()) {
                for(String value : en.getValue()) {
                    try {
                        url.append(URLEncoder.encode(value, "utf-8"));
                    }
                    catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            url.append("&");
        }
        return url.substring(0, url.length() - 1);
    }

    private DataPack[] getAsyncDataPacks(final String url, final String redisKey, final String defaultCacheKey, final String lang, final boolean music)
            throws InterruptedException, ExecutionException, TimeoutException {
        logger.error("getAsyncDataPacks debug : params (" + url + ", " + redisKey + ", " + defaultCacheKey + ", " + lang + ")");
        DataPack[] dataPacks = new DataPack[0];
        Future<DataPack[]> future = futureCache.get(url);
        if(null == future) {
            final Callable<DataPack[]> eval = new Callable<DataPack[]>() {

                public DataPack[] call() throws InterruptedException {
                    return getDataPacks(url, redisKey, defaultCacheKey, lang, music);
                }
            };
            final FutureTask<DataPack[]> ft = new FutureTask<>(eval);
            future = futureCache.putIfAbsent(url, ft);
            if(future == null) {
                future = ft;
                exec.execute(ft);
            }
        }
        dataPacks = future.get(1100, TimeUnit.MILLISECONDS);
        return dataPacks;
    }

    protected DataPack[] getDataPacks(String url, String redisKey, String defaultCacheKey, String lang, boolean music) {
        long starttime = System.currentTimeMillis();
        DataPack[] dataPacks = new DataPack[0];
        try {
            long start = System.currentTimeMillis();
            dataPacks = restRecoApi.getForObject(url, DataPack[].class);
            logger.info("profiler : " + url + " time taken in api : " + (System.currentTimeMillis() - start));
        }
        catch (Exception ex) {
            logger.error("Unable to get datapacks for : " + url + ". Exception : " + ex.getMessage());
        }
        finally {
            if(dataPacks != null && dataPacks.length > 0) {
                for(DataPack dataPack : dataPacks) {
                    dataPack = addDisplayInfo(dataPack, lang, music);
                }
                setDataPackCache(redisKey, dataPacks, cKeyExpiry);
                String defaultPacksStr = getPacksForKey(defaultCacheKey);
                if(!StringUtils.isEmpty(defaultPacksStr)) {
                    DataPack[] defaultPacks = gson.fromJson(defaultPacksStr, DataPack[].class);
                    if(defaultPacks.length == 0 && dataPacks.length >= 2) {
                        setDataPackCache(defaultCacheKey, dataPacks, dKeyExpiry);
                    }
                }
                else if(dataPacks.length >= 2) {
                    setDataPackCache(defaultCacheKey, dataPacks, dKeyExpiry);
                }
            }
            else {
                setDataPackCache(redisKey, new DataPack[0], cKeyExpiry);
            }
            futureCache.remove(url);
        }
        logger.info("profiler : " + url + " getDataPacks : " + (System.currentTimeMillis() - starttime));
        return dataPacks;
    }

    private DataPack addDisplayInfo(DataPack dataPack, String lang, boolean music) {
        if("2G".equals(dataPack.getPackType()) && !dataPack.getPackDataLimit().toUpperCase().contains("MB") && !dataPack.getPackDataLimit().toUpperCase().contains("GB")) {
            dataPack.setPackDataLimit(dataPack.getPackDataLimit() + " MB");
        }
        dataPack.setPackPrice(getPrice(dataPack));
        dataPack.setTitle(getTitle(dataPack));
        dataPack.setThumbnailUrl(getThumnailUrl(dataPack));
        dataPack.setPurchaseUrl(createIntrimUrl(dataPack, lang, music));
        dataPack = applyFBPackChangesIfRequired(dataPack);
        return dataPack;
    }

    private DataPack applyFBPackChangesIfRequired(DataPack dataPack) {
        if("GPRSFBRC1".equals(dataPack.getKeyword())) {
            dataPack.setThumbnailUrl("http://s3-ap-southeast-1.amazonaws.com/airtelin/v3/images/fb_new_70_70.jpg");
            dataPack.setPackType("FB");
        }
        return dataPack;
    }

    private String getThumnailUrl(DataPack dataPack) {
        if("2G".equals(dataPack.getPackType())) {
            return "http://s3-ap-southeast-1.amazonaws.com/bsy/vernac/datapacks/2G_smart.gif";
        }
        else if("3G".equals(dataPack.getPackType())) {
            return "http://s3-ap-southeast-1.amazonaws.com/bsy/vernac/datapacks/3G_smart.gif";
        }
        return "http://s3-ap-southeast-1.amazonaws.com/bsy/vernac/datapacks/GET_smart.gif";
    }

    private String getTitle(DataPack dataPack) {
        if(dataPack.getPackDataLimit().toLowerCase().contains(" rs") || dataPack.getPackDataLimit().toLowerCase().startsWith("rs")) {
            return dataPack.getPackType() + " " + dataPack.getPackDataLimit();
        }
        return dataPack.getPackType() + " " + dataPack.getPackDataLimit() + " @ Rs " + dataPack.getPackPrice();
    }

    private int getPrice(DataPack dataPack) {
        int price = dataPack.getPackPrice();
        if(dataPack.getPackType() != null && dataPack.getPackType().toLowerCase().startsWith("3g")) {
            price = (dataPack.getPackPrice() / 100);
        }
        else {
            logger.warn("Null Packtype : " + dataPack.getTitle() + " : " + dataPack.getPackType());
        }
        return price;
    }

    private String createIntrimUrl(DataPack dataPack, String lang, boolean music) {
        StringBuilder sb = new StringBuilder();
        if(musicConfig != null)
            sb.append(musicConfig.getOpApiBaseUrl() + "datapack/transaction?");
        else {
            sb.append("http://125.21.246.72/v1/operator/datapack/transaction?");
        }
        sb.append("ln=" + lang);
        sb.append("&cp=143");
        sb.append("&pid=" + dataPack.getKeyword());
        String title = dataPack.getPackType() + " " + dataPack.getPackDataLimit();
        try {
            sb.append("&pn=" + URLEncoder.encode(title, "utf-8"));
            sb.append("&pd=" + URLEncoder.encode(title, "utf-8"));
        }
        catch (UnsupportedEncodingException e) {
            title = title.replace(" ", "+");
            sb.append("&pn=" + title);
            sb.append("&pd=" + title);
            logger.error("createIntrimUrl : error in encoding title : " + title, e);
        }
        sb.append("&pp=Rs+" + dataPack.getPackPrice());
        String pv = getPackValidity(dataPack);
        sb.append("&pv=" + pv + "&cpn=abc123&bpc=abc123&o1=" + dataPack.getPackType() + "&o2=VERNAC&o3=" + System
                .currentTimeMillis());
        if(music) {
            sb.append("&si=6&cn=BSB&tp=m1");// Changed from MUSICAPP to BSB as suggested by IBM.
        }
        else {
            sb.append("&tp=1");
            if(("2G").equals(dataPack.getPackType())) {
                sb.append("&si=1&cn=AIRTEL_BSB_VERNAC");
            }
            else {
                sb.append("&si=3&cn=DATAPACKCONSENT");
            }
        }
        return sb.toString();
    }

    private String getPackValidity(DataPack dataPack) {
        String validity;
        try {
            int days = Integer.parseInt(dataPack.getPackValidity());
            if(1 == days) {
                validity = days + "+day";
            }
            else {
                validity = days + "+days";
            }
        }
        catch (Exception ex) {
            validity = dataPack.getPackValidity();
        }
        return validity;
    }

    private String getUsersCachedPacks(String msisdn) {
        String cacheKey = getUsersPacksCacheKey(msisdn);
        return getPacksForKey(cacheKey);
    }

    private String getUsersPacksCacheKey(String msisdn) {
        return "dp-" + msisdn;
    }

    private String getPacksForKey(String cacheKey) {
        String dataPacks = packsCache.get(cacheKey);
        // String dataPacks = ndsRedisServiceManager.get(cacheKey);
        return dataPacks;
    }

    private void setDataPackCache(String redisKey, DataPack[] dataPacks, int keyExpiry) {
        String dataPacksStr = gson.toJson(dataPacks);
        packsCache.put(redisKey, dataPacksStr);
        // ndsRedisServiceManager.setex(redisKey, dataPacksStr, keyExpiry);
    }

    public static DataPackApiService getInstance() {
		return instance;
	}

	public static void setInstance(DataPackApiService instance) {
		DataPackApiService.instance = instance;
	}

	public static void main(String[] args) {
        String msisdn = "9897181052";
        DataPackApiService dataPackApiService = new DataPackApiService();
        String dp = dataPackApiService.getDataPacksRecommendation("9897181052", "dl", true, 3, "en", true);
        System.out.println("DP : " + dp);
        DataUsage dataUsage = dataPackApiService.getDataUsage(msisdn);
        System.out.println("dataUsage : " + dataUsage);

    }

}
