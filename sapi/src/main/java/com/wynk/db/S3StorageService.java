package com.wynk.db;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.wynk.constants.Constants;
import com.wynk.config.S3Config;
import com.wynk.utils.S3Utils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * Notes: 1. There's a limit of 100 buckets per AWS account, if I remember correctly. 2. Follow the
 * structure : - bucket_name - user_images user_1 user_2 user_n+1 other_stuff more_stuff
 */
public class S3StorageService {

    private static AmazonS3 amazonS3Service;

    private Bucket newsImageBucket;
    private Bucket photoImageBucket;

    public static final long geoDbFileModificationTime = 1L * 12 * 3600 * 1000;
    public static final long sevenDaysModificationTime = 1L * 7 * 24 * 3600 * 1000;

    private S3Config config = null;
    private final static String DEFAULT_BUCKET_ENDPOINT = "http://s3.amazonaws.com";
    private static Logger logger = LoggerFactory.getLogger(S3StorageService.class.getCanonicalName());
    private static Map<String, Long> lastModifiedDateCache = new HashMap<String, Long>();

    public S3StorageService(S3Config s3Config) {
        config = s3Config;
        //init(DEFAULT_BUCKET_ENDPOINT);
    }

    public S3StorageService(S3Config s3Config, String endPoint) {
        config = s3Config;
        //init(endPoint);
    }

    @PostConstruct
    private void init(){
        amazonS3Service = new AmazonS3Client(S3Utils.getAWSCreds());
    }

    public List<String> getAllFilesinFolder(String bucketName,String folderPath){
        List<String> fileNameList = new ArrayList<>();
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName).withPrefix(folderPath).withDelimiter("/");
        final ObjectListing objectListing = amazonS3Service.listObjects(listObjectsRequest);

        for (final S3ObjectSummary objectSummary: objectListing.getObjectSummaries()) {
            fileNameList.add(objectSummary.getKey());
        }
        return fileNameList;
    }

    public String store(String bucketName, String filename, File resourceFile) {
        PutObjectResult result = amazonS3Service.putObject(new PutObjectRequest(bucketName, filename, resourceFile).withCannedAcl(
                CannedAccessControlList.PublicRead));
        return result.getETag();
    }
    
    public String store(Constants.S3_BUCKETS type, String basepath, String filename, String contentType,
                        InputStream inputStream) {
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType(contentType);
        PutObjectResult result = amazonS3Service.putObject(new PutObjectRequest(getBucketName(type), basepath + "/" + filename, inputStream, meta).withCannedAcl(
                CannedAccessControlList.PublicRead));

        return result.getETag();
    }
    
    public InputStream fetchData(String bucketName, String filename){
        S3Object object = amazonS3Service.getObject(new GetObjectRequest(bucketName, filename));
        return object.getObjectContent();        
    }
    
    public String getBucketName(Constants.S3_BUCKETS bucket) {
        switch (bucket) {
            case IMAGE:
                return config.getPhotoBucketName();
            case VIDEO:
                return config.getVideoBucketName();
            case TEXTIMAGE:
                return config.getTextImageBucketName();
            case AVATARS:
                return config.getAvatarImageBucketName();
            case RDLOGS:
                return config.getRdLogsBucketName();
            default:
                return "";
        }
    }

    public static InputStream getDbS3ObjectInputStream(String bucket, String key) {
        S3Object s3Object = amazonS3Service.getObject(bucket, key);
        return s3Object.getObjectContent();
    }

    public static BufferedReader getReaderForS3File(final String bucket, final String fileName) {
        S3Object obj = amazonS3Service.getObject(new GetObjectRequest(bucket, fileName));
        return new BufferedReader(new InputStreamReader(obj.getObjectContent()));
    }

}
