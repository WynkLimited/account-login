package com.wynk.utils;

import com.wynk.config.S3Config;
import com.wynk.db.S3StorageService;
import com.wynk.user.dto.User;
import com.wynk.user.dto.UserDevice;
import com.wynk.common.ExceptionTypeEnum;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPOutputStream;

@Service
public class MusicMSISDNDump extends AbstractMusicUtils {

    public static final Logger logger = LoggerFactory.getLogger(MusicMSISDNDump.class.getCanonicalName());
    private static final int BATCH_SIZE = 10000;

    private static final String OUT_PATH = "/data/logs/";
    
    @Autowired
    S3StorageService s3ServiceManager;

    public static void main(String[] args) {

        MusicMSISDNDump dataFetcher = new MusicMSISDNDump();
        dataFetcher.runTask();
    }

    public void runTask() {

        logger.info("Started MusicMSISDNDump");

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");

            String day = dateFormat.format(System.currentTimeMillis());
            String month = monthFormat.format(System.currentTimeMillis());

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.add(Calendar.DATE, -1);
            long beginningOfDayTS = cal.getTimeInMillis();

            Map queryParams = new HashMap<>();
            BasicDBObject basicDBObjectGreaterThan = new BasicDBObject("$gt", beginningOfDayTS);
            queryParams.put("lastActivity", basicDBObjectGreaterThan);
            int totalPlayerCount = (int) mongoUserDBManager.getCount(USER_COLLECTION, queryParams);

            int nQueries = totalPlayerCount / BATCH_SIZE;
            if (totalPlayerCount % BATCH_SIZE != 0)
                nQueries++;

            String fileName = "userdump_" + day + ".tar.gz";

            File outFile = new File(OUT_PATH + fileName);
            GZIPOutputStream out = null;
            try {
                out = new GZIPOutputStream(new FileOutputStream(outFile));
            } catch (IOException e) {
                logger.error("error opening output file." + e.getMessage(), e);
                LogstashLoggerUtils.createFatalExceptionLogWithMessage(e,
                        ExceptionTypeEnum.BATCH.name(),
                        "",
                        "MusicMSISDNDump.runTask",
                        "error opening output file.");
                return;
            }

            System.out.println("Query count : " + nQueries);
            logger.info("MusicMSISDNDump Total Query count : " + nQueries);

            try {
                long stime = System.currentTimeMillis();
                List<DBObject> userObjects = new ArrayList<>();
                for (int ii = 0; ii < nQueries; ii++) {
                    stime = System.currentTimeMillis();
                    logger.info("DBQUery : " + (ii * BATCH_SIZE));
                    List<DBObject> objs = null;
                    try {
                        objs = mongoUserDBManager.getObjects(USER_COLLECTION, ii * BATCH_SIZE, BATCH_SIZE, queryParams);
                    } catch (Exception e) {
                        LogstashLoggerUtils.createFatalExceptionLogWithMessage(e,
                                ExceptionTypeEnum.INFRA.MONGO.name(),
                                "",
                                "MusicMSISDNDump.runTask",
                                "Error retrieving user objects from Db");
                    }
                    //queryParams.put("msisdn","+919717818026");
                    if (objs == null || objs.isEmpty())
                        break;
                    userObjects = objs;
                    logger.info(ii + " Read users in  : " + (System.currentTimeMillis() - stime) + " milliseconds");
                    System.out.println(ii + " Read users in  : " + (System.currentTimeMillis() - stime) + " milliseconds");

                    StringBuffer buffer = new StringBuffer();
                    for (int i = 0; i < userObjects.size(); i++) {
                        DBObject userDBObject = userObjects.get(i);
                        User user = new User();
                        try {
                            user.fromJson(userDBObject.toString());
                            if (StringUtils.isBlank(user.getMsisdn()))
                                continue;

                            String ucsv = (user.getUid() + "," + user.getMsisdn() + "," + user.getCircle() + "," + user.getOperator()) + "," + user.getName() + "," + user.getCreationDate() + "," + user.getLastActivityDate();
                            if (user.getDevices() != null && user.getDevices().size() > 0) {
                                UserDevice device = user.getDevices().get(0);
                                ucsv += "," + device.getOs() + "," + device.getAppVersion() + "," + device.getOsVersion() + "," + device.getDeviceType();
                            }

                            buffer.append(ucsv);
                            buffer.append("\r\n");

                        } catch (Exception e) {
                            logger.error("Error parsing user details ", e);
                        }
                    }

                    try {
                        out.write(buffer.toString().getBytes("UTF-8"));
                    } catch (IOException e) {
                        logger.error("Error writing msisdn dump to file ", e);
                    }
                    try {
                        out.flush();
                    } catch (IOException e) {
                        logger.error("Error flushing msisdn dump to file ", e);
                    }
                }
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        logger.error("Error closing msisdn dump to file ", e);
                    }
                }
            }

            S3Config s3Config = new S3Config();
/*            s3Config.setAwsAccessKeyId(S3Utils.getAWSCreds().getCredentials().getAWSAccessKeyId());
            s3Config.setAwsSecretKey(S3Utils.getAWSCreds().getCredentials().getAWSSecretKey());*/

            S3StorageService s3StorageService = new S3StorageService(s3Config);
            try {
                s3StorageService.store("twanganalytics", "userdump/" + month + "/" + day + "/" + fileName, outFile);
            } catch (Exception e) {
                logger.error("MusicMSISDNDump - Error storing user MSISDN dump to S3 - ",e.getMessage(),e);
                LogstashLoggerUtils.createFatalExceptionLogWithMessage(e,
                        ExceptionTypeEnum.THIRD_PARTY.AWS.name(),
                        "",
                        "MusicMSISDNDump.runTask",
                        "Error uploading to S3");
            }
            FileUtils.deleteQuietly(outFile);
        } catch (Exception e) {
            logger.error("MusicMSISDNDump - Error processing user MSISDN ",e.getMessage(),e);
            LogstashLoggerUtils.createFatalExceptionLogWithMessage(e,
                    ExceptionTypeEnum.THIRD_PARTY.AWS.name(),
                    "",
                    "MusicMSISDNDump.runTask",
                    "Error uploading to S3");
        }
        logger.info("Ended MusicMSISDNDump");
        LogstashLoggerUtils.createBatchSuccessLogs("musicMSISDNDump");

    }
    
    public void uploadMsisdnDump(String month, String day , String fileName, File outFile) {
        try {
        	s3ServiceManager.store("twanganalytics", "userdump/" + month + "/" + day + "/" + fileName, outFile);
        } catch (Exception e) {
            logger.error("MusicMSISDNDump - Error storing user MSISDN dump to S3 - ",e.getMessage(),e);
            LogstashLoggerUtils.createFatalExceptionLogWithMessage(e,
                    ExceptionTypeEnum.THIRD_PARTY.AWS.name(),
                    "",
                    "MusicMSISDNDump.runTask",
                    "Error uploading to S3");
        }
        FileUtils.deleteQuietly(outFile);
    }
}
