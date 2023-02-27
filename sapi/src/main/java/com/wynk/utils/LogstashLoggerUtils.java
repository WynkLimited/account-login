package com.wynk.utils;

import com.wynk.common.Circle;
import com.wynk.constants.MusicConstants;
import com.wynk.server.ChannelContext;
import com.wynk.solace.dto.SolaceNumberData;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;
import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wynk.common.ExceptionSeverityEnum;
import com.wynk.common.UserMusicActivity;
import com.wynk.common.UserMusicActivityStates;
import com.wynk.music.constants.MusicContentType;
import com.wynk.music.constants.MusicPaymentMode;

/**
 * Logs Error to logStashFile
 * "/data/log/music/logstashLogs.log"
 */
public class LogstashLoggerUtils {

    private static final Logger logstashLogger         = LoggerFactory.getLogger("logstashLogger");
    //  data/logs/music/sapi/logstash/{yyyy-MM-dd-HH}/analytics.*.log
		private static final Logger userDumpLogstashLogger         = LoggerFactory.getLogger("userdumpanalytics");

    private static final ObjectMapper objectMapper  = new ObjectMapper();

	public static void userDumpLogs(String uid, String msisdn, String circle, String operator, String name, Long creationDate, Long lastActivityDate,
			String os, String appVersion, String osVersion, String deviceType, Long dob, String email, String cid) {
		try {
			Map<String, Object> logObject = new HashMap();
			logObject.put("uid", uid);
			logObject.put("msisdn",msisdn );
			logObject.put("circle",circle );
			logObject.put("operator",operator );
			logObject.put("name",name );
			logObject.put("creationDate",creationDate );
			logObject.put("lastActivityDate",lastActivityDate );
			logObject.put("os",os );
			logObject.put("appVersion",appVersion );
			logObject.put("osVersion",osVersion );
			logObject.put("deviceType", deviceType);
			logObject.put("dob", dob);
			logObject.put("email", email);
			logObject.put("country", cid);
			String log = objectMapper.writeValueAsString(logObject);
			userDumpLogstashLogger.info(log);
		} catch (Exception e) {
			logException(e.toString(), e.getStackTrace() , "", "Error logging standardLog to logStash");
		}
	}


	public static void logException(String exceptionName, StackTraceElement[] stackTrace, String uid, String errorMessage) {

		// HACK to log no exception related to clearservercache
		if (errorMessage.contains("clearservercache"))
			return;

		try {
			stackTrace = limitStackTrace(stackTrace);
			String meta = "stacktrace = " + Arrays.toString(stackTrace) ;
			if (errorMessage != null && errorMessage != "" ) {
				meta = meta + ", errorMessage = " + errorMessage;
			}
			if (uid == null) {
				uid = "";
			}
			String log = "{" +  "\"logType\":\"MUSIC_EXCEPTION\"," +
								"\"uid\":\"" + uid + "\"," +
			                    "\"createTimestamp\":\"" + getTime()  + "\"," +
								"\"exceptionName\":\"" + exceptionName + "\"," +
								"\"meta\":\""+ meta + "\"" + "}";
			logstashLogger.info(log);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void logException(String exceptionName, StackTraceElement[] stackTrace, String uid) {
		logException(exceptionName, stackTrace, uid, "");
	}

	private static void createExceptionLogWithMessage(Exception exception,  String exceptionType , String exceptionSeverity, String uid, String moduleName,String errorMessage) {
		try {
			String exceptionName = "";
			StackTraceElement[] stackTrace = new StackTraceElement[] {};
			if (exception != null ) {
				stackTrace = limitStackTrace(exception.getStackTrace());
				exceptionName = exception.toString();
			}

			String meta = "stacktrace = " + Arrays.toString(stackTrace) ;
			meta = meta + ", moduleName = " + moduleName;
			if (errorMessage == null ) {
				errorMessage = "";
			}

			// HACK to log no exception related to clearservercache
			if (errorMessage.contains("clearservercache"))
				return;

			if (uid == null) {
				uid = "";
			}
			String log = "{" +  "\"logType\":\"MUSIC_EXCEPTION\"," +
								"\"uid\":\"" + uid + "\"," +
								"\"exceptionName\":\"" + exceptionName + "\"," +
								"\"errorMessage\":\"" + errorMessage + "\"," +
								"\"exceptionType\":\"" + exceptionType + "\"," +
			                    "\"createTimestamp\":\"" + getTime()  + "\"," +
								"\"exceptionSeverity\":\"" + exceptionSeverity + "\"," +
								"\"meta\":\""+ meta + "\"" + "}";
			logstashLogger.info(log);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static void createExceptionLog(Exception exception,  String exceptionType , String exceptionSeverity, String uid, String moduleName) {
		createExceptionLogWithMessage(exception, exceptionType, exceptionSeverity, uid, moduleName , "");
	}




	public static void createCriticalExceptionLog(Exception exception,  String exceptionType , String uid, String moduleName) {
        createExceptionLog(exception, exceptionType, ExceptionSeverityEnum.CRITICAL.name(), uid, moduleName);
    }

	public static void createCriticalExceptionLogWithMessage(Exception exception,  String exceptionType , String uid, String moduleName, String message) {
        createExceptionLogWithMessage(exception, exceptionType, ExceptionSeverityEnum.CRITICAL.name(), uid, moduleName, message);
    }

	public static void createFatalExceptionLog(Exception exception,  String exceptionType , String uid, String moduleName) {
        createExceptionLog(exception, exceptionType, ExceptionSeverityEnum.FATAL.name(), uid, moduleName);
    }

    public static void createFatalExceptionLogWithMessage(Exception exception,  String exceptionType , String uid, String moduleName, String message) {
        createExceptionLogWithMessage(exception, exceptionType, ExceptionSeverityEnum.FATAL.name(), uid, moduleName, message);
    }

    public static void createErrorExceptionLog(Exception exception,  String exceptionType , String uid, String moduleName) {
        createExceptionLog(exception, exceptionType, ExceptionSeverityEnum.ERROR.name(), uid, moduleName);
    }

    public static void createErrorExceptionLogWithMessage(Exception exception,  String exceptionType , String uid, String moduleName, String message) {
        createExceptionLogWithMessage(exception, exceptionType, ExceptionSeverityEnum.ERROR.name(), uid, moduleName, message);
    }


	private static StackTraceElement[] limitStackTrace(StackTraceElement[] stackTrace) {
		if (stackTrace.length < 5) {
			return stackTrace;
		} else {
			return Arrays.copyOfRange(stackTrace, 0, 5);
		}
	}

	/**
	 * Defines and logs a standard log.
	 * @param logType
	 * @param uid
	 * @param msisdn
	 * @param deviceId
	 * @param meta
	 */
	public static void createStandardLog(String logType, String uid, String msisdn, String deviceId, HashMap<String,String> meta) {
		try {
			String metaData = "";
	        for (String key: meta.keySet()) {
	            metaData += key + " : " + meta.get(key) + " ,";
	        }

	        msisdn = Utils.getTenDigitMsisdn(msisdn) ;

	        String log = "{"
	                + "\"logType\":\""      + logType                           + "\","
	                + "\"uid\":\""          + uid                               + "\",";

	        if (StringUtils.isNotEmpty(msisdn))
	                log+= "\"msisdn\":\""  + msisdn   + "\",";

	        log += "\"deviceId\":\""     + deviceId                          +"\","
	                + "\"meta\":\""         + metaData                          +"\","
	                + "\"createTimestamp\":\"" + getTime()  + "\""
	                + "}";

	        logstashLogger.info(log);
		} catch (Exception e) {
        	logException(e.toString(), e.getStackTrace() , "", "Error logging standardLog to logStash");
		}
    }
	public static void createHystrixLog(String logType, String uid, String msisdn, String deviceId, HashMap<String,String> meta ){
		try {
			msisdn = Utils.getTenDigitMsisdn(msisdn) ;
			Map<String, Object> logObject = new HashMap<>();
			logObject.put("logType", logType);
			logObject.put("uid", uid);
			logObject.put("msisdn", msisdn);
			logObject.put("deviceId", deviceId);
			logObject.put("meta", meta);
			logObject.put("createTimestamp", getTime());
			String log = objectMapper.writeValueAsString(logObject);
			logstashLogger.info(log);
		} catch (Exception e) {
			logException(e.toString(), e.getStackTrace() , "", "Error logging standardLog to logStash");
		}
	}

	public static void createFingerPrintCallLogs(String message , String timeTaken) {
		try {
			String log = "{"
					+ "\"logType\":\""              	+ "FINGERPRINT_PYTHON_CALL"         + "\","
					+ "\"message\":\""   				+ message							+ "\","
					+ "\"timeTake\":\""            		+ timeTaken   						+ "\","
					+ "\"createTimestamp\":\"" 			+ getTime()  						+ "\""
					+ "}";
			logstashLogger.info(log);
		} catch (Exception e) {
			logException(e.toString(), e.getStackTrace() , "", "Error logging notificationLog to logStash");
		}
	}

	public static void createNotificationLog(String logType, String id,String warning, String message, long startTime, long endTime){
		try {
			String log = "{"
					+ "\"logType\":\""              	+ logType               + "\","
					+ "\"notificationId\":\""       	+ id                    + "\","
					+ "\"notificationWarning\":\""     	+ warning               + "\","
					+ "\"message\":\""   				+ message				+ "\","
					+ "\"startTime\":\""            	+ startTime   			+ "\","
					+ "\"endTime\":\""              	+ endTime               + "\","
					+ "\"createTimestamp\":\"" + getTime()  + "\""
					+ "}";

			logstashLogger.info(log);
		} catch (Exception e) {
			logException(e.toString(), e.getStackTrace() , "", "Error logging notificationLog to logStash");
		}
	}

	public static void createInfoLog(String message) {
		try {
	        String log = "{"
	                + "\"logType\":\"MUSIC_INFO\","
	                + "\"message\":\""    + message +"\","
	               + "\"createTimestamp\":\"" + getTime()  + "\""
	                + "}";
	        logstashLogger.info(log);
		} catch (Exception e) {
        	logException(e.toString(), e.getStackTrace() , "", "Error logging infoLog to logStash");
		}
    }

	   public static void createSolrInfoLog(String message) {
	        try {
	            String log = "{"
	                    + "\"logType\":\"MUSIC_SOLR\","
	                    + "\"message\":\""    + message +"\","
	                   + "\"createTimestamp\":\"" + getTime()  + "\""
	                    + "}";
	            logstashLogger.info(log);
	        } catch (Exception e) {
	            logException(e.toString(), e.getStackTrace() , "", "Error logging infoLog to logStash");
	        }
	    }

	public static void createDebugLog(String message) {
		try {
	        String log = "{"
	                + "\"logType\":\"MUSIC_DEBUG\","
	                + "\"message\":\""    + message +"\","
	                + "\"createTimestamp\":\"" + getTime()  + "\""
	                + "}";
	        logstashLogger.info(log);
		} catch (Exception e) {
        	logException(e.toString(), e.getStackTrace() , "", "Error logging debugLog to logStash");
		}
    }

	public static void createAccessLog(FullHttpResponse response , String requestUriWoutParams, String requestUri,String requestPayload,long duration,
			HttpRequest request, String exception){
		if (!MusicConstants.EXCLUDE_HANDLERS.stream().anyMatch(requestUriWoutParams::contains)) {
			String uid = ChannelContext.getUid();
			Map<String, Object> logObject = new ConcurrentHashMap();
			String res = "";
			res = response.content().toString(CharsetUtil.UTF_8);
			try {
				logObject.put("logType", "MUSIC_ACCESS_LOGS");
				logObject.put("uid", ObjectUtils.isEmpty(uid) ? "" : uid);
				logObject.put("handler", requestUriWoutParams);
				logObject.put("headers", new ObjectMapper().writeValueAsString(request.headers().entries()));
				logObject.put("responseHeaders", new ObjectMapper().writeValueAsString(response.headers().entries()));
				logObject.put("method", request.method().toString());
				logObject.put("responseTime", duration);
				logObject.put("response", res);
				logObject.put("requestUri", requestUri);
				logObject.put("exception", exception);
				logObject.put("requestPayload", ObjectUtils.isEmpty(requestPayload) ? "" : requestPayload);
				logObject.put("responseCode", ObjectUtils.isEmpty(response) ? "5xx" : response.getStatus().toString());
				logstashLogger.info(ObjectUtils.writeAsString(logObject));
			} catch (Exception ignored) {
			}
		}
	}

	public static void createAccessLogLite(String logType, String info, String uid){
			Map<String, Object> logObject = new ConcurrentHashMap();
			logObject.put("logType", logType);
			logObject.put("uid", uid);
			logObject.put("info", info);
			logstashLogger.info(ObjectUtils.writeAsString(logObject));
	}

	public static void createAccessLogLiteV2(String logType, String info, String msisdn, String uid){
		Map<String, Object> logObject = new ConcurrentHashMap();
		logObject.put("logType", logType);
		logObject.put("uid", uid);
		logObject.put("info", info);
		logstashLogger.info(ObjectUtils.writeAsString(logObject));
	}

	public static void createGCMLogs(String uid, String deviceId, String deviceToken,String notificationId,String errorCode, HashMap<String,String> meta) {
		try {
			String metaData = "";
			for (String key: meta.keySet()) {
				metaData += key + " : " + meta.get(key) + " ,";
			}

			String log = "{"
					+ "\"logType\":\""      		+ "MUSIC_GCM_LOGS"               + "\","
					+ "\"uid\":\""         		    + uid                            + "\","
					+ "\"deviceId\":\""     		+ deviceId                       +"\","
					+ "\"deviceToken\":\""          + deviceToken                    +"\","
					+ "\"notificationId\":\""       + notificationId                 +"\","
					+ "\"errorCode\":\""          	+ errorCode                    	 +"\","
					+ "\"createTimestamp\":\"" 		+ getTime()  					 + "\","
					+ "\"meta\":\"" 			    + metaData                       +"\"";

			log += "}";

			logstashLogger.info(log);

		} catch (Exception e) {
			logException(e.toString(), e.getStackTrace() , "", "Error logging GCM logs to logStash");
		}
	}

	public static void createOnDeviceLogs(String uid, String deviceId,String matchType ,int totalProcessed,int  totalMatched,HashMap<String,String> meta) {
		try {
			String metaData = "";
			for (String key: meta.keySet()) {
				metaData += key + " : " + meta.get(key) + " ,";
			}

			String log = "{"
					+ "\"logType\":\""      		+ "ON_DEVICE_LOG"                + "\","
					+ "\"uid\":\""         		    + uid                            + "\","
					+ "\"deviceId\":\""     		+ deviceId                       +"\","
					+ "\"matchType\":\""     			+ matchType                       	 +"\","
					+ "\"totalMatched\":\""         + totalMatched                   +"\","
					+ "\"totalProcessed\":\""       + totalProcessed                 +"\","
					+ "\"timestamp\":\"" 			+ getTime()  					 + "\","
					+ "\"meta\":\"" 			    + metaData                       +"\"";

			log += "}";

			logstashLogger.info(log);

		} catch (Exception e) {
			logException(e.toString(), e.getStackTrace() , "", "Error logging OnDevice logs to logStash");
		}
	}

	public static void logToLogstash(String log) {
		logstashLogger.info(log);
	}

	public static String createMetaMatchLogLine(String uid, String deviceId,String reason ,HashMap<String,String> meta, String offlineId) {
		try {
			String metaData = "";
			for (String key: meta.keySet()) {
				metaData += key + " : " + meta.get(key) + " ,";
			}
			metaData = metaData.replaceAll("\"", "");
			String log = "{"
					+ "\"logType\":\""      		+ "META_MATCH_LOG"                + "\","
					+ "\"uid\":\""         		    + uid                            + "\","
					+ "\"deviceId\":\""     		+ deviceId                       +"\","
					+ "\"reason\":\""     			+ reason                       	 +"\","
					+ "\"offlineId\":\""     			+ offlineId                       	 +"\","
					+ "\"timestamp\":\"" 			+ getTime()  					 + "\","
					+ "\"meta\":\"" 			    + metaData                       +"\"";

			log += "}";
			return log;
		} catch (Exception e) {
			logException(e.toString(), e.getStackTrace() , "", "Error logging MetaMatch logs to logStash");
		}
		return null;
	}

    public static void createUserMusicActivityLog(String uid, String msisdn, String deviceId, String transactionId, MusicContentType musicContentType, UserMusicActivity activity, // rent/download/stream
            String source, // online/offline/initiate
            UserMusicActivityStates status, // success/failure
            String failureReason, String networkType, String contentId, String callbackUrl, MusicPaymentMode paymentMode, String metadata) {

        msisdn = Utils.getTenDigitMsisdn(msisdn);

        try {
            String meta = "source = " + source + " ," + "networkType = " + networkType + " ," + "paymentMode = " + paymentMode.name() + " ," + "transactionId = " + transactionId + " ," + "status = "
                    + status.name() + " ," + "failureReason = " + failureReason + " ," + "callbackUrl = " + callbackUrl + "metaData" + metadata;

            String log = "{" + "\"logType\":\"MUSIC_ACTIVITY\"," + "\"uid\":\"" + uid + "\",";

            if (StringUtils.isNotEmpty(msisdn))
                log += "\"msisdn\":\"" + msisdn + "\"," ;

            log += "\"deviceId\":\"" + deviceId + "\","
                    + "\"contentId\":\"" + contentId + "\"," + "\"activityType\":\"" + activity.name() + "\"," +
                    "\"createTimestamp\":\"" + getTime()  + "\"," +
                    "\"musicContentType\":\"" + musicContentType.name() + "\"," + "\"meta\":\"" + meta
                    + "\"" + "}";

            logstashLogger.info(log);

        } catch (Exception e) {
            logException(e.toString(), e.getStackTrace(), "", "Error logging music activity logs to logStash");
        }
    }


    private static String getTime() {
    	SimpleDateFormat sm = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.SSS");
 		sm.setTimeZone(TimeZone.getTimeZone("GMT"));
 		String strDate = sm.format(new Date());
 		return strDate;
    }

    public static void createBatchSuccessLogs(String jobName) {
    	try {

            String log = "{"
                    + "\"logType\":\""      + "MUSIC_BATCH_SUCCESS"               + "\","
                    + "\"uid\":\""          + jobName                             + "\","
                    + "\"createTimestamp\":\"" + getTime()  + "\""
                    + "}";
            logstashLogger.info(log);
        } catch (Exception e) {
            logException(e.toString(), e.getStackTrace() , "", "Error logging access logs to logStash");
        }
    }

	public static void createLAPUDebugLog(String msisdn,String productId, String transactionId, String requestTime, String message){
		try {
			String log = "{"
					+ "\"logType\":\""              	+ "LAPU_DEBUG_LOG"               + "\","
					+ "\"msisdn\":\""       			+ msisdn                    + "\","
					+ "\"productId\":\""     			+ productId               + "\","
					+ "\"transactionId\":\""   			+ transactionId				+ "\","
					+ "\"requestTime\":\""              + requestTime   			+ "\","
					+ "\"message\":\""              	+ message               + "\""
					+ "}";

			logstashLogger.info(log);
		} catch (Exception e) {
			logException(e.toString(), e.getStackTrace() , "", "Error logging notificationLog to logStash");
		}
	}

	public static void createOemBuildModuleRemovingLogs(String packageType,int count) {
		try {
			String moduleCount = String.valueOf(count);
			String log = "{"
					+ "\"logType\":\""      		+ "OEM_MODULE_LOG"               + "\","
					+ "\"packageType\":\""          + packageType                    + "\","
					+ "\"count\":\""       			+ moduleCount                    + "\","
					+ "\"createTimestamp\":\"" 		+ getTime()  + "\""
					+ "}";
			logstashLogger.info(log);
		} catch (Exception e) {
			logException(e.toString(), e.getStackTrace() , "", "Error logging oem build remove module logs to logStash");
		}
	}

	public static void createKinesisLog(String uid, String meta ,long endTime,String ops,String opsStatus){
		try {
			Map<String, Object> logObject = new HashMap<>();
			logObject.put("logType", "Kinesis");
			logObject.put("uid", uid);
			logObject.put("data", meta);
			logObject.put("operation", ops);
			logObject.put("operationStatus", opsStatus);
			logObject.put("finishedTime", getTime());
			String log = objectMapper.writeValueAsString(logObject);
			logstashLogger.info(log);
		} catch (Exception e) {
			logException(e.toString(), e.getStackTrace() , "", "Error logging standardLog to logStash");
		}
	}

	public static void moEngageEventLog(String uid, String payload, String url, String response, boolean isException, String eventName, String packId) {
		try {
			Map<String, Object> logObject = new HashMap<>();
			logObject.put("uid", uid);
			logObject.put("logType", "MOENGAGE_EVENT_LOG");
			logObject.put("eventName", eventName);
			logObject.put("payload", payload);
			logObject.put("endpoint", url);
			logObject.put("response", response);
			logObject.put("packId", packId);
			if (!isException) {
				logObject.put("status", "success");
				logstashLogger.info(objectMapper.writeValueAsString(logObject));
			} else {
				logObject.put("status", "failed");
				logstashLogger.error(objectMapper.writeValueAsString(logObject));
			}
		} catch (Exception e) {
			logException(e.toString(), e.getStackTrace(), uid, "Error logging standardLog to logStash");
		}
	}

	public static void createSolaceLog(String response, String url, SolaceNumberData solaceNumberData, String uid){
		try{
			Map<String, Object> logObject = new HashMap<>();
			logObject.put("logType", "SOLACE_EVENT_LOG");
			logObject.put("solaceUrl", url);
			logObject.put("solaceAPIResponse", response);
			logObject.put("solaceNumberData", objectMapper.writeValueAsString(solaceNumberData));
			logObject.put("uid", uid);
			logstashLogger.info(objectMapper.writeValueAsString(logObject));
		} catch (Exception e){
			logException(e.toString(), e.getStackTrace() , "", "Error logging standardLog to logStash");
		}
	}

	public static void createLogstashExceptionLog(Exception exception, String exceptionType, String uid, String moduleName, String errorMessage){
		try{
			Map<String, Object> logObject = new HashMap<>();
			logObject.put("exceptionName", exception.toString());
			logObject.put("exceptionType", exceptionType);
			logObject.put("uid", uid);
			logObject.put("moduleName", moduleName);
			logObject.put("errorMessage", errorMessage);
			logstashLogger.info(objectMapper.writeValueAsString(logObject));
		}catch (Exception e){
			logException(e.toString(), e.getStackTrace() , "", "Error logging exception log to logStash");
		}

	}

	public static void registeredUidInUnregisteredBugLogger(String channelContextUser, String appUser, String request) {
		try{
			Map<String, Object> logObject = new HashMap<>();
			logObject.put("logType", "UID_BUG");
			logObject.put("request", request);
			logObject.put("channelContextUser", channelContextUser);
			logObject.put("appUser", appUser);
			logstashLogger.info(objectMapper.writeValueAsString(logObject));
		} catch (Exception e) {
			logException(e.toString(), e.getStackTrace(), "", "Error logging uidBugLog to logStash");
		}
	}

	public static void autoLoginLogger(String uid, String msisdn) {
		try{
			Map<String, Object> logObject = new HashMap<>();
			logObject.put("logType", "AUTOLOGIN");
			logObject.put("uid", uid);
			logObject.put("msisdn", msisdn);
			logstashLogger.info(objectMapper.writeValueAsString(logObject));
		} catch (Exception e) {
			logException(e.toString(), e.getStackTrace(), "", "Error logging uidBugLog to logStash");
		}
	}

	public static void httpClientExceptionLogger(Exception exception, String uid, String request) {
		try{
			Map<String, Object> logObject = new HashMap<>();
			logObject.put("logType", "HTTP_CLIENT_EXCEPTION");
			logObject.put("uid", uid);
			logObject.put("request", request);
			logObject.put("exception", exception.toString());
			logObject.put("stackTrace", Arrays.toString(exception.getStackTrace()));
			logstashLogger.info(objectMapper.writeValueAsString(logObject));
		} catch (Exception e) {
			logException(e.toString(), e.getStackTrace(), "", "Error logging httpClientException to logStash");
		}
	}

	public static void otpCallExceptionLogger(Exception exception, String msisdn, String exceptionType) {
		try{
			Map<String, Object> logObject = new HashMap<>();
			logObject.put("logType", "OTP_CALL_EXCEPTION");
			logObject.put("msisdn", msisdn);
			logObject.put("exceptionType", exceptionType);
			logObject.put("exception", exception.toString());
			logObject.put("stackTrace", Arrays.toString(exception.getStackTrace()));
			logstashLogger.info(objectMapper.writeValueAsString(logObject));
		} catch (Exception e) {
			logException(e.toString(), e.getStackTrace(), "", "Error logging otpCallException to logStash");
		}
	}

	//use createAccessLogLite for generic success logging
	//use createFatalException or CriticalException logger methods for generic exception logging

	public static void logExecutionStep(String requestBody, String uid, String msisdn, String clientName, String responseBody) {
		Map<String, Object> logMap = new ConcurrentHashMap<>();
		logMap.put("logType", "WCF_REQ_RES");
		logMap.put("body", requestBody);
		logMap.put("msisdn", msisdn);
		logMap.put("uid", uid);
		logMap.put("client", clientName);
		logMap.put("receivedResponse", responseBody);
		try {
			logstashLogger.info(objectMapper.writeValueAsString(logMap));
		} catch (Exception e) {
			logException(e.toString(), e.getStackTrace(), "", "Error while logging logExecutionStep to logStash");
		}
	}

}
