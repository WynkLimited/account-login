package com.wynk.config;

import com.wynk.db.*;
import com.wynk.utils.ConfigFile;
import com.wynk.utils.ObjectUtils;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Logger;


/**
 * Created with IntelliJ IDEA. User: bhuvangupta Date: 20/09/12 Time: 11:46 PM To change this
 * template use File | Settings | File Templates.
 */
@Configuration
@EnableTransactionManagement
public class PortalConfig {

    private static final Logger logger = Logger.getLogger(PortalConfig.class.getCanonicalName());
    @Autowired
    private ConfigFile properties;

    @Bean
    public NettyConfig nettyConfig() {
        NettyConfig config = new NettyConfig();
        config.setHttpport(properties.getIntProperty("server.httpport", 8181));
        config.setBrokerId(properties.getIntProperty("server.brokerid", 0));
        config.setHostName(properties.getStringProperty("server.hostname", null));

        try {
            if (config.getHostName() == null || config.getHostName().isEmpty()) {
                config.setHostName(InetAddress.getLocalHost().getHostName());
            }
            config.setHostAddr(InetAddress.getLocalHost().getHostAddress());
        }
        catch (UnknownHostException e) {
            System.err.println("Unable to find hostname : " + e.getMessage());
            config.setHostName("localhost-" + config.getBrokerId());
        }

        config.setNumThreads(properties.getIntProperty("server.threads", (Runtime.getRuntime().availableProcessors() * 2 + 1)));
        config.setCorePoolSize(properties.getIntProperty("server.corePoolSize", 10));
        config.setMaxPoolSize(properties.getIntProperty("server.maxPoolSize", 100));
        return config;
    }


    private MongoDBConfig getMongoDBConfig(String propertyName, String defaultValue) {
        MongoDBConfig config = new MongoDBConfig();
        config.setMongoHosts(properties.getStringProperty("mongodb.hosts", "127.0.0.1:27017"));
        config.setMongodbHost(properties.getStringProperty("mongodb.host", "127.0.0.1"));
        config.setMongodbPort(properties.getIntProperty("mongodb.port", 27017));
        config.setMongodbHostSlave(properties.getStringProperty("mongodb.host.slave", null));
        config.setMongodbPortSlave(properties.getIntProperty("mongodb.port.slave", 27017));
        config.setMongodbHostArbiter(properties.getStringProperty("mongodb.host.arbiter", null));
        config.setMongodbPortArbiter(properties.getIntProperty("mongodb.port.arbiter", 27017));
        config.setReadPrimary(properties.getBooleanProperty("mongodb.is.read.primary", true));
        config.setMongoDBName(properties.getStringProperty(propertyName, defaultValue));
        config.setMongoDBPrefix(properties.getStringProperty("mongodb.prefix", ""));
        config.setMongodbLoggingEnabled(properties.getBooleanProperty("mongodb.loggingenabled", false));
        config.setMongodbThreadsAllowedToBlock(properties.getIntProperty("mongodb.threads.allowed.per.host", 50));
        config.setMongodbConnectionsPerHost(properties.getIntProperty("mongodb.connections.per.host", 500));
        return config;
    }

    ProducerConfig kafkaProducerConfig()
    {
        boolean producerEnabled = properties.getBooleanProperty("kafka.producer", false);
        if(producerEnabled) {
            Properties props = new Properties();
            props.put("metadata.broker.list", properties.getStringProperty("music.kakfa.broker", "localhost:9092"));
            props.put("serializer.class", "kafka.serializer.StringEncoder");
            props.put("zk.connect", properties.getStringProperty("music.zookeper.connection", "localhost:2181"));
            props.put("producer.type", properties.getStringProperty("music.kakfa.producer.type", "async"));
            // Blocking in the user-supplied serializers or partitioner will not be counted against this timeout
            props.put("max.block.ms", "5000");
            props.put("request.timeout.ms", "5000");
            ProducerConfig config = new ProducerConfig(props);
            return config;
        }
        else
            return null;
    }

    @Bean
    public ShardedRedisServiceManager userPersistantRedisServiceManager() {
        return new ShardedRedisServiceManager(userPersistantRedisDBConfig());
    }

    @Bean(name="redisManagerUpload")
    public ShardedRedisServiceManager duplicateUIDRedisServiceManager() {
        return new ShardedRedisServiceManager(userPersistantRedisDBConfig());
    }

    public ShardedRedisConfig userPersistantRedisDBConfig() {
        ShardedRedisConfig shardedRedisConfig = new ShardedRedisConfig();
        ArrayList<RedisHostConfig> redisHosts = new ArrayList<RedisHostConfig>();
        String redisHostsConfig = properties.getStringProperty("user.persistant.redis.hosts", "127.0.0.1:6379");
        String[] hostsConfig = redisHostsConfig.split(",");
        for (String hostConfig : hostsConfig) {
            String[] split = hostConfig.split(":");
            if(split.length > 1)
            {
                RedisHostConfig redisHostConfig = new RedisHostConfig();
                redisHostConfig.setRedisHost(split[0]);
                String portPassword = split[1];
                String[] split2 = portPassword.split("/");
                if(split2.length > 1)
                {
                    redisHostConfig.setRedisPort(ObjectUtils.integerValue(split2[0], 6379));
                    redisHostConfig.setRedisPassword(split2[1]);
                }
                else
                    redisHostConfig.setRedisPort(ObjectUtils.integerValue(split[1], 6379));

                redisHosts.add(redisHostConfig);
            }
        }
        shardedRedisConfig.setRedisHosts(redisHosts);
        shardedRedisConfig.setMaxActive(properties.getIntProperty("user.persistant.redis.pool.max.active", 8));
        shardedRedisConfig.setMaxIdle(properties.getIntProperty("user.persistant.redis.pool.max.idle", 8));
        return shardedRedisConfig;
    }

    @Bean
    public ShardedRedisServiceManager musicShardRedisServiceManager() {
        return new ShardedRedisServiceManager(musicShardRedisDBConfig());
    }

    @Bean
    public JedisCluster wcfPayRedisCluster() {
        Set<HostAndPort> jedisClusterNode = new HashSet<>();
        String host = properties.getStringProperty("wcf.pay.redis.cluster.host", "wynk-music-redis-staging-cluster.uxqgfy.clustercfg.use1.cache.amazonaws.com");
        jedisClusterNode.add(new HostAndPort(host, 6379));
        JedisPoolConfig cfg = new JedisPoolConfig();
        cfg.setMaxTotal(properties.getIntProperty("wcf.pay.redis.pool.max.active", 8));
        cfg.setMaxIdle(properties.getIntProperty("wcf.pay.redis.pool.max.idle", 8));
        cfg.setMaxWaitMillis(10000);
        cfg.setTestOnBorrow(true);
        JedisCluster jedisCluster = new JedisCluster(jedisClusterNode, 10000, 1, cfg);
        return jedisCluster;
    }

    @Bean
    public ShardedRedisServiceManager musicUserShardRedisServiceManager() {
        return new ShardedRedisServiceManager(musicUserShardRedisDBConfig());
    }

    public ShardedRedisConfig musicShardRedisDBConfig() {
        ShardedRedisConfig shardedRedisConfig = new ShardedRedisConfig();
        ArrayList<RedisHostConfig> redisHosts = new ArrayList<RedisHostConfig>();
        String redisHostsConfig = properties.getStringProperty("music.shard.redis.hosts", "127.0.0.1:6379");
        String[] hostsConfig = redisHostsConfig.split(",");
        for (String hostConfig : hostsConfig) {
            String[] split = hostConfig.split(":");
            if(split.length > 1)
            {
                RedisHostConfig redisHostConfig = new RedisHostConfig();
                redisHostConfig.setRedisHost(split[0]);
                String portPassword = split[1];
                String[] split2 = portPassword.split("/");
                if(split2.length > 1)
                {
                    redisHostConfig.setRedisPort(ObjectUtils.integerValue(split2[0], 6379));
                    redisHostConfig.setRedisPassword(split2[1]);
                }
                else
                    redisHostConfig.setRedisPort(ObjectUtils.integerValue(split[1], 6379));

                redisHosts.add(redisHostConfig);
            }
        }
        shardedRedisConfig.setRedisHosts(redisHosts);
        shardedRedisConfig.setMaxActive(properties.getIntProperty("music.shard.redis.pool.max.active", 8));
        shardedRedisConfig.setMaxIdle(properties.getIntProperty("music.shard.redis.pool.max.idle", 8));
        return shardedRedisConfig;
    }

    public ShardedRedisConfig musicUserShardRedisDBConfig() {
        ShardedRedisConfig shardedRedisConfig = new ShardedRedisConfig();
        ArrayList<RedisHostConfig> redisHosts = new ArrayList<RedisHostConfig>();
        String redisHostsConfig = properties.getStringProperty("music.user.shard.redis.hosts", "127.0.0.1:6379");
        String[] hostsConfig = redisHostsConfig.split(",");
        for (String hostConfig : hostsConfig) {
            String[] split = hostConfig.split(":");
            if(split.length > 1)
            {
                RedisHostConfig redisHostConfig = new RedisHostConfig();
                redisHostConfig.setRedisHost(split[0]);
                String portPassword = split[1];
                String[] split2 = portPassword.split("/");
                if(split2.length > 1)
                {
                    redisHostConfig.setRedisPort(ObjectUtils.integerValue(split2[0], 6379));
                    redisHostConfig.setRedisPassword(split2[1]);
                }
                else
                    redisHostConfig.setRedisPort(ObjectUtils.integerValue(split[1], 6379));

                redisHosts.add(redisHostConfig);
            }
        }
        shardedRedisConfig.setRedisHosts(redisHosts);
        shardedRedisConfig.setMaxActive(properties.getIntProperty("music.user.shard.redis.pool.max.active", 8));
        shardedRedisConfig.setMaxIdle(properties.getIntProperty("music.user.shard.redis.pool.max.idle", 8));
        return shardedRedisConfig;
    }

    public S3Config s3Config() {
        S3Config config = new S3Config();
/*        config.setAwsAccessKeyId(properties.getStringProperty("s3.accesskeyid", S3Utils.getAWSCreds().getCredentials().getAWSAccessKeyId()));
        config.setAwsSecretKey(properties.getStringProperty("s3.secretkey", S3Utils.getAWSCreds().getCredentials().getAWSSecretKey()));*/
        config.setPhotoBucketName(properties.getStringProperty("s3.photobucketname", "test"));
        config.setNewsImageBucketName(properties.getStringProperty("s3.newsbucketname", "test"));
        config.setVideoBucketName(properties.getStringProperty("s3.videobucketname", "test"));
        config.setTextImageBucketName(properties.getStringProperty("s3.textimagebucketname", "test"));
        config.setAvatarImageBucketName(properties.getStringProperty("s3.avatarimagebucketname", "test.bsbportal"));
        config.setImageBucketName(properties.getStringProperty("s3.imagebucketname", "test"));
        config.setImageBaseUrl(properties.getStringProperty("s3.imagebaseurl", "http://bsy.s3.amazonaws.com/"));
        config.setRdLogsBucketName(properties.getStringProperty("s3.rdlogsbucketname", "test"));
        return config;
    }

    @Bean
    public StatsdConfig statsdConfig() {
        StatsdConfig config = new StatsdConfig();
        config.setStatsdServerHost(properties.getStringProperty("statsd.host", "127.0.0.1"));
        config.setStatsdServerPort(properties.getIntProperty("statsd.port", 8125));
        return config;
    }

    @Bean(name = "mongoUserDBManager")
    public MongoDBManager mongoUserDBManager() {
        return new MongoDBManager(getMongoDBConfig("mongodb.userdb", "userdb"));
    }

    @Bean(name = "mongoMusicDBManager")
    public MongoDBManager mongoMusicDBManager() {
        return new MongoDBManager(getMongoDBConfig("mongodb.musicdb", "musicdb"));
    }

    @Bean(name = "restTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean(name = "s3ServiceManager")
    public S3StorageService s3ServiceManager() {
        return new S3StorageService(s3Config());
    }

    @Bean
    public MusicConfig musicConfig() {
        MusicConfig config = new MusicConfig();
        config.setTrialProductId(properties.getIntProperty("lyrics.trial.product.id", -1));
        config.setTrialAgainProductId(properties.getIntProperty("lyrics.trial.again.product.id", -1));

        config.setBundledTrialProductId(properties.getIntProperty("bundled.lyrics.trial.product.id", -1));
        config.setBundledTrialAgainProductId(properties.getIntProperty("bundled.lyrics.trial.again.product.id", -1));


        config.setEnableMusic(properties.getBooleanProperty("music.enable", false));

        if(config.isEnableMusic()){
            config.setApnsDevCertFile(properties.getStringProperty("music.apns.dev.cert",
                    System.getProperty("user.dir") + "/config/dev/keys/apns-dev-cert.p12"));
            config.setApnsCertFile(properties.getStringProperty("music.apns.cert", System.getProperty("user.dir") + "/config/music/keys/yourappkey.p12"));
            //config.setApnsCertFile(properties.getStringProperty("music.apns.cert", System.getProperty("user.dir") + "/config/music/keys/apns-dev-cert.p12"));
        }
        config.setGraphBaseUrl(properties.getStringProperty("graph.baseurl", "http://graph.wynkinternal.in"));
        config.setEnableProdApnsCert(properties.getBooleanProperty("music.enableProdApnsCert", true));
        config.setGalaxyFreeSongPurchaseCount(properties.getStringProperty("music.galaxyFreeSongPurchaseCount", "25"));
        config.setGalaxyOfferExpiryDate(properties.getStringProperty("music.galaxyOfferExpiryDate", "1505152355000"));
        config.setFeaturedOff(properties.getBooleanProperty("music.featured.off",false));
        config.setFeaturedOff2G(properties.getBooleanProperty("music.2Gfeatured.off",false));
        config.setUpdateGCMDeviceKey(properties.getBooleanProperty("gcm.devicekey.update",false));
        config.setGcmMultiCast(properties.getBooleanProperty("gcm.send.multicast",false));
        config.setEnableGoogleNow(properties.getBooleanProperty("google.now.enable",false));
        config.setMusicSixMonthsOffer(properties.getBooleanProperty("music.six.months.offer",false));
        config.setMusicOfferNonAirtel(properties.getBooleanProperty("music.offer.non.airtel",false));
        config.setMusicOfferIdea(properties.getBooleanProperty("music.offer.idea",false));

        config.setEnableRedirection(properties.getBooleanProperty("music.song.redirection", false));
        config.setWapSalt(properties.getStringProperty("music.authentication.wapsalt", "S1ymYn1M5"));
        config.setAppSalt(properties.getStringProperty("music.authentication.appsalt", "S1ymYn1M5"));
        config.setNewWapSalt(properties.getStringProperty("music.authentication.newwapsalt", "51ymYn1MS"));
        config.createSaltValueMap(properties.getStringProperty("music.authentication.thanks.salt.map", "in.bsb.myairtel.inhouse:d26b12bd2," +
                "com.BhartiMobile.myairtel:d26b12bd2,com.wynk.discoverios:d26b12bd2,com.myairtelapp.debug:d26b12bd2," +
                " com.myairtelapp:d26b12bd2, com.airtel.sample:d26b12bd2"));
        config.setHTPurchaseUrl(properties
                .getStringProperty("music.htpurchaseurl", "http://125.21.246.72/v1/operator/ht/provision?vcode="));
        config.setSmsapi(properties.getStringProperty("music.smsapi", "http://sms.wcf.internal/sms/send"));
        config.setApiBaseUrl(properties.getStringProperty("music.apibaseurl", "http://localhost:8080/music/v1/"));
        config.setApiSecureBaseUrl(properties.getStringProperty("music.apisecurebaseurl", "https://secure.twangmusic.in/music/v1/"));
        config.setPlayStreamingUrl(properties.getStringProperty("play.streaming.url", "https://staging.wynk.in"));
        config.setPlayStreamingEndpoint(properties.getStringProperty("play.streaming.endpoint", "/streaming/v4/"));
        config.setTwssUrl(properties.getStringProperty("music.twssurl", ""));
        config.setSmsgw(properties.getStringProperty("music.smsgw", "airtel"));
        config.setUseLocalSMSSender(properties.getBooleanProperty("music.localsms", true));
        config.setSmsSenderMaxPoolSize(properties.getIntProperty("music.smsSenderMaxPoolSize", 100));
        config.setEnableAnalytics(properties.getBooleanProperty("music.analytics", false));
        config.setEnableRE(properties.getBooleanProperty("music.re", false));
        config.setEnableNDS(properties.getBooleanProperty("music.nds", false));
        config.setEnableNotifications(properties.getBooleanProperty("music.notifications", false));
        config.setEnableTestNotifications(properties.getBooleanProperty("music.notifications.test", false));
        config.setAnalyticsbucketname(properties.getStringProperty("music.analyticsbucketname", "twanganalytics-dev"));
        config.setItemsPerPage(properties.getIntProperty("music.itemsperpage", 10));
        config.setEncryptionKey(properties.getStringProperty("music.encryptionkey", "BSB$PORTAL@2014"));
        config.setAwsEndPoint(properties.getStringProperty("music.awsEndPoint", ""));
        config.setPlatformBaseUrl(properties.getStringProperty("music.platform.base.url",
                "http://cms-be.wynkinternal.in/"));
        config.setBaseFeUrl(properties.getStringProperty("music.basefeurl", "http://localhost:8080/"));
        config.setSearchVoiceEnable(properties.getBooleanProperty("search.voice.enable", false));
        config.setWcfBaseUrl(properties.getStringProperty("wcf.baseurl", "https://subscription-staging.wynk.in"));
        config.setFupEnabled(properties.getBooleanProperty("music.fup.enabled", false));
        config.setAirtel99Limit(properties.getIntProperty("music.fup.airtel99Limit", 1000));
        config.setStreamingFUPLimit(properties.getIntProperty("music.streaming.fup.limit", 100));
        config.setWapStreamingLimit(properties.getIntProperty("music.fup.wapStreamingLimit", 10));
        config.setWynkContestEnabled(properties.getBooleanProperty("wynk.contest.enable", false));
        config.setIbmReconFileProcessing(properties.getBooleanProperty("ibm.recon.processing", false));
        config.setUnrInactivityNotification(properties.getBooleanProperty("unr.inactive.notification", false));
        config.setShowADHMHamburgerMenu(properties.getBooleanProperty("adhm.hamburger.menu", false));
        config.setShowADHMPopUp(properties.getBooleanProperty("adhm.popup.enable", false));
        config.setShowOnDeviceHamburgerMenu(properties.getBooleanProperty("ondevice.hamburger.menu", false));
        config.setExternalHandlerAuthenicationCode(properties.getStringProperty("extenal.handler.auth.code", "90de5a788c133a29c8db434fq7bf2db4"));
        config.setFreeDataOffer(properties.getBooleanProperty("free.data.offer.on", false));
        config.setEnableIpl(properties.getBooleanProperty("ipl.enable", false));

        config.setEnableStatsD(properties.getBooleanProperty("music.statsd.enabled", false));
        config.setEnableSE(properties.getBooleanProperty("music.se.enabled", false));
        config.setOpApiBaseUrl(properties.getStringProperty("music.op.apibaseurl", "http://125.21.246.72/v1/operator/"));

        config.setiTunesAirtelSubsProductId(
                properties.getStringProperty("music.itunes.airtelPackId", "in.bsb.twang.monthly"));
        config.setiTunesNonAirtelSubsProductId(
                properties.getStringProperty("music.itunes.nonAirtelPackId", "TwangApp_MonthlySubs_NonAirtel"));

        config.setiTunesAirtelSubsPackUserProductId(
                properties.getStringProperty("music.itunes.airtelPackId.packUser", "MusicApp_MonthlySubs_NoFree"));
        config.setiTunesNonAirtelSubsPackUserProductId(properties
                .getStringProperty("music.itunes.nonAirtelPackId.packUser", "MusicApp_MonthlySubs_OTT_NoFree"));

        config.setiTunesApiUrl(properties
                .getStringProperty("music.itunes.receipturl", "https://sandbox.itunes.apple.com/verifyReceipt"));
        config.setiTunesPassword(
                properties.getStringProperty("music.itunes.secret", "d577d1725a524ad189f9a0f1e33e1fd8"));
        config.setSubsDBUpdateByApi(properties.getBooleanProperty("music.subsDbUpdatebyApi", false));
        config.setEnableGeoBlocking(properties.getBooleanProperty("music.geo.enabled", true));
        config.setEnablePackages(properties.getBooleanProperty("music.packages.enable", false));
        config.setNotificationThreadPoolSize(properties.getIntProperty("music.notifications.threadpool.size", 10));
        config.setTwilioThreadPoolSize(properties.getIntProperty("music.twilio.threadpool.size", 10));

        // adding twilio properties
        config.setTwilioBaseUrl(properties.getStringProperty("music.twilio.baseurl",
                "http://121.241.244.238:9501/voiceapi/otp_wynk.php?account="));
        config.setTwilioId(properties.getStringProperty("music.twilio.account.id", "wynk"));
        config.setTwilioPin(properties.getStringProperty("music.twilio.account.pin", "wynk123"));
        config.setTwilioCampaignId(properties.getStringProperty("music.twilio.account.campaignId", "788"));

        config.setDisableProactiveFeedback(properties.getBooleanProperty("music.proactivefeedback.disable", true));
        config.setMusicServers(properties.getStringProperty("music.servers", "127.0.0.1"));

        config.setOperatorApiBaseUrl(
                properties.getStringProperty("music.operator.apibaseurl", "http://api.wynk.in/v1/operator/"));

        config.setFpKafkaQueue(properties.getStringProperty("kafka.fp.queue", "testfingerprint"));

        config.setClearCacheQueue(properties.getStringProperty("music.clearCache1.queue","clearCache"));
        config.setClearCmsCacheQueue(properties.getStringProperty("music.clearCache2.queue","clearCacheCmsKafkaQueue"));

        config.setEnableFPKafkaConsumer(properties.getBooleanProperty("fp.kafka.consumer.enable", false));

        config.setCmsPythonBaseUrl(properties.getStringProperty("music.fp.python.url", "http://fingerprint-python-153586938.ap-southeast-1.elb.amazonaws.com:8181/"));

        config.setVideosQueue(properties.getStringProperty("gtm.videos.queue", "videoGtmEvents"));
        config.setGamesQueue(properties.getStringProperty("gtm.games.queue", "gamesGtmEvents"));

        config.setStatsQueue(properties.getStringProperty("music.kafka.queue", "statsEvents"));
        config.setAdtechQueue(properties.getStringProperty("music.kafka.adtechqueue", "adtechEventsAsync"));

        config.setSegmentationURL(properties.getStringProperty("music.segmentation.url", "localhost:8081"));

        config.setIntentQueue(properties.getStringProperty("gtm.intent.queue", "offer100MBProvisioningRequest"));

        config.setFpDelimiterKafkaQueue(properties.getStringProperty("kafka.fpdelimiter.queue", "testfingerprintdelimiter"));
        config.setEnableDelimiterKafkaConsumer(properties.getBooleanProperty("delimiter.kafka.consumer.enable", false));

        config.setPrerollEnabled(properties.getBooleanProperty("ad.preroll.enable", false));
        config.setNativeAdEnabled(properties.getBooleanProperty("ad.native.enable", false));
        config.setWcfNDSCache(properties.getStringProperty("wcf.nds.cache", "http://cache.wynkinternal.in/wynk/cache"));
        config.setSamsungSDKKey(properties.getStringProperty("platform.samsung.sdk.secret", ""));


        config.setIosBsyExt(properties.getStringProperty("ios.app.bsyext","1"));
        config.setWcfBaseApiUrl(properties.getStringProperty("music.wcf.apibaseurl", "http://localhost:8080/music/wcf/"));
        config.setCronEnabled(properties.getStringProperty("cron.enabled", "false"));
        config.setAppSidePackagesShuffling(properties.getBooleanProperty("music.app.packageShuffling", false));
        config.setTpNotifyQueue(properties.getStringProperty("tp.kafka.notifyqueue", "tpNotifyQueue"));

        config.setIntlRoamingCycle(properties.getLongProperty("music.intlroaming.cycle", 7776000000L));
        config.setIntlRoamingQuota(properties.getLongProperty("music.intlroaming.quota", 2419200000L));
        config.setIntlRoamingExpiryNotif(properties.getLongProperty("music.intlroaming.expiryNotif", 604800000L));
        config.setIntlRoamingEnabled(properties.getBooleanProperty("music.intlroaming.enable", false));
        config.setIntlRoamingFreeTier(properties.getBooleanProperty("music.intlroaming.freetier", true));
        config.setIntlRoamingAndroidVersionsToUpgrade(properties.getIntProperty("music.intlroaming.androidVersion", Integer.MAX_VALUE));
        config.setIntlRoamingiOsVersionsToUpgrade(properties.getIntProperty("music.intlroaming.iOSVersion", Integer.MAX_VALUE));
        config.setMp3AkamaiCloudFrontRatio(properties.getIntProperty("music.mp3.akamai.cloudfront.ratio",Integer.MAX_VALUE));
        config.setMp4AkamaiCloudFrontRatio(properties.getIntProperty("music.mp4.akamai.cloudfront.ratio",Integer.MAX_VALUE));
        config.setMp3PerformaceTestAkamaiCloudRatio(properties.getIntProperty("music.mp3.perf.test.akamai.cloudfront.ratio",1));
        config.setMp4PerformanceTestAkamaiCloudRatio(properties.getIntProperty("music.mp4.perf.test.akamai.cloudfront.ratio",1));
        config.setFollowCountApiUrl(properties.getStringProperty("followApiUrl","http://10.1.2.16:8181"));
        config.setAppSMSHashCode(properties.getStringProperty("wynk.service.app.sms.hash","/NvYD4LFr69"));
        config.setEnableLyrics(properties.getBooleanProperty("sync.lyrics.enable", false));
        config.setAdsDisableExperimentId(properties.getStringProperty("ads.disable.ab.experiment.id","7502"));
        config.setWcfWebViewBaseUrl(properties.getStringProperty("wcf.pay.webview.base.url","https://pay-stage.wynk.in/"));
        config.setBasicSMSHashCode(properties.getStringProperty("basic.service.app.sms.hash", "IJKMWGrDfhk"));
        config.setWcfThanksStream(properties.getStringProperty("wcf.thanks.stream","stg-music-wcf-segments"));
        config.setEnableKinesisConsumer(properties.getBooleanProperty("wcf.thanks.enable.consumer",false));
        config.setWcfSmsDomain(properties.getStringProperty("wcf.sms.base.url", ""));
        config.setWcfSmsEndpoint(properties.getStringProperty("wcf.sms.endpoint", ""));
        // twitter consumer credentials
        config.setTwitterKey(properties.getStringProperty("twitter.consumer.key","mtEnAJGvTazMqLgftxho29ziQ"));
        config.setTwitterSecret(properties.getStringProperty("twitter.consumer.secret","0LGKjqjWXTfRmfKaYPY1qcRcgFFglZzXmhylzGveHC2UftHJaU"));

        // wcf payment
        config.setWcfPayBaseUrl(properties.getStringProperty("wcf.pay.base.url","https://payments-staging.wynk.in"));
        config.setWcfPaySqsUrl(properties.getStringProperty("wcf.pay.sqs.url","https://sqs.us-east-1.amazonaws.com/536123028970/wynk-music-staging-payment-data-failure-queue"));
        config.setWcfPaySqsRegion(properties.getStringProperty("wcf.pay.sqs.region","us-east-1"));
        config.setStaticWebViewSecretKey(properties.getStringProperty("wcf.pay.webview.secret.key", ""));

        config.setWcfOfferUrl(properties.getStringProperty("wcf.offers.endpoint", ""));
        config.setWcfIdentificationUrl(properties.getStringProperty("wcf.identification.endpoint", ""));
        config.setWcfAllProductsUrl(properties.getStringProperty("wcf.all.products.endpoint", ""));
        config.setWcfAllPlansUrl(properties.getStringProperty("wcf.all.plans.endpoint", ""));
        config.setWcfAllOffersUrl(properties.getStringProperty("wcf.all.offers.endpoint", ""));

        //auth
        config.setEnableAuth(properties.getBooleanProperty("auth.enable",false));

        config.setWcfApiAppId(properties.getStringProperty("wcf.api.app.id", ""));
        config.setWcfApiSecretKey(properties.getStringProperty("wcf.api.secret.key", ""));

        //TODO: Do I need to add my new url here in this config
        return config;
    }

    @Bean
    public Producer<String, String> kafkaProducerManager() {
        ProducerConfig kafkaProducerConfig = kafkaProducerConfig();
        if(kafkaProducerConfig != null) {
            Producer<String, String> kafkaProducer = new Producer<String, String>(kafkaProducerConfig);
            return kafkaProducer;
        }
        return null;
    }

}
