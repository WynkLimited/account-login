package com.wynk.utils;


import com.wynk.config.*;
import com.wynk.db.MongoDBManager;
import com.wynk.db.ShardedRedisServiceManager;
import com.wynk.music.MusicCMSDataFetcher;
import com.wynk.service.MusicService;

import java.util.ArrayList;

public abstract class AbstractMusicUtils {

    protected ShardedRedisServiceManager musicShardRedisServiceManager;
    protected ShardedRedisServiceManager musicUserShardRedisServiceManager;
    protected MongoDBManager musicMongoDBManager;
    protected MongoDBManager      mongoUserDBManager;

    protected MusicCMSDataFetcher musicCMSDataFetcher;

    public static final String USER_COLLECTION = "users";

    protected AbstractMusicUtils()
    {
        init();
    }

    private void init()
    {
        System.out.println("Initializing ...");
        MusicService.BASE_CMS_API = "http://cms-be.wynkinternal.in/v1/content/discovery";

        ShardedRedisConfig shardredisConfig = shardRedisConfig();
        musicShardRedisServiceManager = new ShardedRedisServiceManager(shardredisConfig);
        
        musicUserShardRedisServiceManager = new ShardedRedisServiceManager(shardUserRedisConfig());
        
        musicMongoDBManager = new MongoDBManager(testMongoDBConfig());
        mongoUserDBManager = new MongoDBManager(testMongoUserDBConfig());

        MusicConfig mconfig = new MusicConfig();
        mconfig.setApiBaseUrl("http://api.wynk.in/music/v1/");
        mconfig.setOpApiBaseUrl("http://125.21.246.72/v1/operator/");
        mconfig.setHTPurchaseUrl("http://125.21.246.72/v1/operator/ht/provision?vcode=");

        musicCMSDataFetcher = new MusicCMSDataFetcher();
        musicCMSDataFetcher.setMusicConfig(mconfig);
        musicCMSDataFetcher.setMusicShardRedisServiceManager(musicShardRedisServiceManager);

    }


    public static MongoDBConfig testMongoDBConfig() {
        MongoDBConfig config = new MongoDBConfig();
        config.setMongoHosts("10.70.1.236:27017,10.70.0.211:27017");
        config.setMongodbHost("10.0.8.73");
        config.setMongodbPort(27017);
        config.setMongodbHostSlave("prmddata044acf78d767acdc8apse01.in.bsbportal.com");
        config.setMongodbPortSlave(27017);
        config.setMongoDBName("musicdb");
        config.setMongoDBPrefix("");
        config.setReadPrimary(false);
        config.setMongodbThreadsAllowedToBlock(50);
        config.setMongodbConnectionsPerHost(500);
        return config;
    }


    public static MongoDBConfig testMongoUserDBConfig() {
        MongoDBConfig config = new MongoDBConfig();
        config.setMongoHosts("10.70.1.236:27017,10.70.0.211:27017");
        config.setMongodbHost("prmddata068e56f5455db99b8apse01.in.bsbportal.com");
        config.setMongodbPort(27017);
        config.setMongodbHostSlave("prmddata044acf78d767acdc8apse01.in.bsbportal.com");
        config.setMongodbPortSlave(27017);
        config.setMongoDBName("userdb");
        config.setMongoDBPrefix("");
        config.setMongodbThreadsAllowedToBlock(50);
        config.setMongodbConnectionsPerHost(500);
        config.setReadPrimary(false);
        return config;
    }

    public static ShardedRedisConfig shardRedisConfig() {
    	ShardedRedisConfig shardedRedisConfig = new ShardedRedisConfig();
    	ArrayList<RedisHostConfig> redisHosts = new ArrayList<RedisHostConfig>();
    	String redisHostsConfig = "10.0.7.158:6379/dasfF2%4DD56d,10.0.7.53:6379/sd#0D8sFad,10.0.7.27:6379/dasfJ4fdDfD56d";
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
        return shardedRedisConfig;
    }
    
    public static ShardedRedisConfig shardUserRedisConfig() {
    	ShardedRedisConfig shardedRedisConfig = new ShardedRedisConfig();
    	ArrayList<RedisHostConfig> redisHosts = new ArrayList<RedisHostConfig>();
    	String redisHostsConfig = "10.70.0.236:6379/nPfrSEPqLu5Hms3PT37jdfzQ,10.70.0.34:6379/nPfrSEPqLu5Hms3PT37jdfzQ,10.70.3.92:6379/nPfrSEPqLu5Hms3PT37jdfzQ,10.70.3.93:6379/nPfrSEPqLu5Hms3PT37jdfzQ,10.70.0.84:6379/nPfrSEPqLu5Hms3PT37jdfzQ";
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
        return shardedRedisConfig;
    }
    
}


