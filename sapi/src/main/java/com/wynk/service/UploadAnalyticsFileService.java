package com.wynk.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.wynk.db.ShardedRedisServiceManager;
import com.wynk.utils.ConfigFile;
import com.wynk.utils.S3Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Component
public class UploadAnalyticsFileService {

    private Logger logger = LoggerFactory.getLogger(UploadAnalyticsFileService.class.getCanonicalName());

    @Autowired
    private ConfigFile properties;

    @Autowired
    @Qualifier(value = "redisManagerUpload")
    private ShardedRedisServiceManager userPersistantRedisServiceManager;

    String duplicateFileName = "/data/logs/music/duplicate_uids_backup.log.";

    @Scheduled(cron = "0 10 00 * * *")
    public void uploadDuplicateUids() {
        String fileName=duplicateFileName+getDateKey();
        File file = new File(fileName);
        BufferedWriter writer = null;
        try {
            if (file.createNewFile())
                logger.info("file created");
            else
                logger.info("File already exists");
            writer = new BufferedWriter(new FileWriter(fileName));
            Set<String> duplicateUids =
                    userPersistantRedisServiceManager.smembers(getDateKey());
            for (String mapping : duplicateUids) {
                writer.write(mapping);
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            logger.error("Error while creating duplicate uid file " + e.getMessage());
        }
        uploadFileToS3("twanganalytics-in", file, fileName,
                "DuplicateUIDMapData/" + getDirStructure()+"duplicate_uids.csv."+getDateKey());
    }

    private String getDateKey() {
        TimeZone timeZone = TimeZone.getTimeZone("IST");
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.add(Calendar.DATE, -1);
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.format(calendar.getTime());
    }

    public void uploadFileToS3(String bucketName, File file, String uploadFileName, String key) {

        // Checking if File to be uploaded exists or not
        File uploadFile = new File(uploadFileName);
        if (!uploadFile.exists()) {
            logger.info("File not exist : " + uploadFileName);
            return;
        }

        AmazonS3 s3client = new AmazonS3Client(S3Utils.getAWSCreds());
        s3client.setEndpoint("http://s3.ap-south-1.amazonaws.com");

        List<PartETag> partETags = new ArrayList<PartETag>();

        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, key);
        InitiateMultipartUploadResult initResponse = s3client.initiateMultipartUpload(initRequest);
        long contentLength = file.length();
        long partSize = 100 * 1024 * 1024; // Set part size to 100 MB.
        try {
            // Step 2: Upload parts.
            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++) {
                // Last part can be less than 100 MB. Adjust part size.
                partSize = Math.min(partSize, (contentLength - filePosition));

                // Create request to upload a part.
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName).withKey(key)
                        .withUploadId(initResponse.getUploadId()).withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withFile(file)
                        .withPartSize(partSize);

                // Upload part and add response to our list.
                partETags.add(s3client.uploadPart(uploadRequest).getPartETag());

                filePosition += partSize;
            }

            // Step 3: Complete.
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucketName,
                    key,
                    initResponse.getUploadId(),
                    partETags);

            s3client.completeMultipartUpload(compRequest);
        } catch (Exception e) {
            logger.info("AmazonServiceException: Amazon Service encountered an error while uploading file " + uploadFileName);
            logger.info("Error Message: " + e.getMessage());
            s3client.abortMultipartUpload(new AbortMultipartUploadRequest(
                    bucketName, key, initResponse.getUploadId()));
        }
    }

    private String getDirStructure() {
        String dirStructure = null;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date date = calendar.getTime();
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
        String dayStr = sm.format(date);

        SimpleDateFormat sm2 = new SimpleDateFormat("yyyy-MM");
        String yearMonth = sm2.format(date);

        dirStructure = yearMonth + "/" + dayStr + "/";

        return dirStructure;
    }
}
