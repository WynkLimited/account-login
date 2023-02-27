package com.wynk.music;

import com.wynk.config.MusicConfig;
import com.wynk.db.S3StorageService;
import com.wynk.db.ShardedRedisServiceManager;
import com.wynk.dto.IdNameType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by bhuvangupta on 25/12/13.
 */
@SuppressWarnings("unchecked")
public class MusicCMSDataFetcher {
	

    private static final Logger logger               = LoggerFactory
            .getLogger(MusicCMSDataFetcher.class.getCanonicalName());

    public static Map<String, IdNameType> getCuratedArtistMap() {
        return curatedArtistMap;
    }

    private static Map<String,IdNameType> curatedArtistMap = new LinkedHashMap<>();

    private ShardedRedisServiceManager musicShardRedisServiceManager;
    private MusicConfig musicConfig;
    protected S3StorageService s3ServiceManager;

    public void setMusicShardRedisServiceManager(ShardedRedisServiceManager musicShardRedisServiceManager)
    {
        this.musicShardRedisServiceManager = musicShardRedisServiceManager;
    }

    public void setMusicConfig(MusicConfig musicConfig)
    {
        this.musicConfig = musicConfig;
    }

    public MusicConfig getMusicConfig()
    {
        return musicConfig;
    }

    public String getJSONStringFromS3(String bucketName,String fileName) {

        String jsonString = "";
        BufferedReader br = null;
        try {
            InputStream inputStream = s3ServiceManager.fetchData(bucketName,fileName);
            br = new BufferedReader(new InputStreamReader(inputStream));

            String line = null;
            while((line = br.readLine()) != null) {
                jsonString += line;
            }
            br.close();
        }
        catch (Exception e) {
            logger.error("Error reading module ordering/renaming JSON " + e.getMessage(), e);
        }
        finally {
            try {
                if(br != null)
                    br.close();
            } catch (IOException e) {
                logger.error("Error closing S3 input stream " + e.getMessage(), e);
            }
        }

        return jsonString;
    }

    public void setS3ServiceManager(S3StorageService s3ServiceManager) {
        this.s3ServiceManager = s3ServiceManager;
    }
	
}
