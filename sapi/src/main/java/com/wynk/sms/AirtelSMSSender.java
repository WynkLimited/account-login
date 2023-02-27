package com.wynk.sms;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wynk.common.EmailSender;
import com.wynk.db.ShardedRedisServiceManager;
import com.wynk.utils.MusicUtils;
import com.wynk.utils.Utils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.pool.PoolStats;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class AirtelSMSSender extends AbstractSMSSender {

    private static final Logger logger                         = LoggerFactory
                                                                                                                        .getLogger(
                                                                                                                                AirtelSMSSender.class
                                                                                                                                        .getCanonicalName());

    private static final int                                                     MAX_CONNECTIONS                = 150;
    private static final int                                                     MAX_NO_OF_THREADS              = 150;
    static final String SMS_STATS_FILE_DIR = "resources/";
    private static final String SMS_STATS_FILENAME             = "sms-stats.json";

    @Autowired
    ShardedRedisServiceManager userPersistantRedisServiceManager;

    private PoolingHttpClientConnectionManager connectionManager;

    private CloseableHttpClient httpClient;

    /**
     * ThreadPoolExecutor with unbounded queue will always have core poolsize threads and will never
     * grow to max pool size. Refer:
     * https://github.com/kimchy/kimchy.github.com/blob/master/_posts/2008
     * -11-23-juc-executorservice-gotcha.textile
     */
    private ExecutorService executorService                = Executors
                                                                                                                        .newFixedThreadPool(
                                                                                                                                MAX_NO_OF_THREADS);

    // Last 1 month response codes
    private ConcurrentHashMap<LocalDate, ConcurrentHashMap<Integer, AtomicLong>> responseCodeWithCountPerDayMap = new ConcurrentHashMap<LocalDate, ConcurrentHashMap<Integer, AtomicLong>>(
                                                                                                                        32, 1f, MAX_NO_OF_THREADS);

    @Autowired
    private EmailSender emailSender;

    @Value("${smsserver.responsecodes.stats.email:ankit.srivastava@wynk.in,sudipta.banerjee@wynk.in,tushar.garg@wynk.in}")
    private List<String> toEmailIds;

    private static ObjectMapper objectMapper                   = new ObjectMapper();
    private static SimpleModule module                         = new SimpleModule(
                                                                                                                        "MapKeySerializerModule",
                                                                                                                        new Version(1, 0, 0, "", "",
                                                                                                                                ""));

    static class LocalDateKeySerializer extends JsonSerializer<LocalDate> {

        @Override
        public void serialize(LocalDate value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
                JsonProcessingException {
            jgen.writeFieldName(String.valueOf(value.toDate().getTime()));
        }
    }

    static class LocalDateKeyDeserializer extends KeyDeserializer {

        @Override
        public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException,
                JsonProcessingException {
            return new LocalDate(Long.valueOf(key));
        }

    }

    static {
        module.addKeySerializer(LocalDate.class, new LocalDateKeySerializer());
        module.addKeyDeserializer(LocalDate.class, new LocalDateKeyDeserializer());
        objectMapper.registerModule(module);
    }

    @PostConstruct
    public void init() {
        loadSmsStatsFromFile();
        try {
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {

                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("https", sslsf)
                    .build();
            connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(60000).setConnectTimeout(10000).setSocketTimeout(7000).build();
            connectionManager.setMaxTotal(MAX_CONNECTIONS);
            connectionManager.setDefaultMaxPerRoute(MAX_CONNECTIONS);
            ConnectionKeepAliveStrategy keepAliveStrategy = new ConnectionKeepAliveStrategy() {

                @Override
                public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                    // Honor 'keep-alive' header
                    HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                    while(it.hasNext()) {
                        HeaderElement he = it.nextElement();
                        String param = he.getName();
                        String value = he.getValue();
                        if(value != null && param.equalsIgnoreCase("timeout")) {
                            try {
                                return Long.parseLong(value) * 1000;
                            }
                            catch (NumberFormatException ignore) {
                            }
                        }
                    }
                    // otherwise keep alive for 30 seconds
                    return 30 * 1000;
                }
            };

            httpClient = HttpClients.custom().setConnectionManager(connectionManager).setSSLSocketFactory(sslsf).setDefaultRequestConfig(config)
                    .setKeepAliveStrategy(keepAliveStrategy).evictIdleConnections(30L, TimeUnit.SECONDS).build();
        }
        catch (Throwable th) {
            throw new RuntimeException(th);
        }
    }

    @Override
    public void sendMessage(String msisdn, String shortCode, String text, Boolean useDND) {

        if(useDND == null)
            useDND = false;
        if(StringUtils.isNotBlank(msisdn))
            msisdn = Utils.getTenDigitMsisdn(msisdn);

        // If msisdn is not in DND list then only send sms
        Boolean val = false;
        // If msisdn is not in DND list then only send sms
        if(userPersistantRedisServiceManager != null) {
            // Check if the entry exists in redis cache
            // It it is not then send message
            try {
                if(useDND)
                    val = numberExistsInCache(msisdn);
            }
            catch (Exception e) {
                logger.info("Error fetching DND from Redis - Error : ", e.getMessage(), e);
            }
        }
        else {
            logger.info("Error fetching DND from Redis - Error : dndRedisServiceManager == null");
        }

        if(val == false) {
            try {
                SMSMsg sms = new SMSMsg();
                sms.shortcode = shortCode;
                sms.toMsisdn = msisdn;
                sms.message = text;
                sms.messageId = "" + System.currentTimeMillis(); // Utils.generateUUID(true,true);
                String mtRequestXML = createMTRequestXML(sms);
                logger.info("Submitted SMS: {}", mtRequestXML);
                executorService.submit(new PostSMS(mtRequestXML, msisdn));
            }
            catch (RejectedExecutionException th) {
                logger.error("Error while Delivering SMS, ERROR: {}", th.getMessage(), th);
                logger.info("Task got rejected, ThreadPoolStats: {}", getThreadPoolStats());
            }
            catch (Throwable th) {
                logger.error("Error while Delivering SMS, ERROR: {}", th.getMessage(), th);
            }
        }
        else {
            logger.info("Sms not sent to {} number exists in DND List", msisdn);
        }

    }

    private Boolean numberExistsInCache(String msisdn) {
        String result = userPersistantRedisServiceManager.get(MusicUtils.generateDNDKey(msisdn));
        String searchKey = msisdn.substring(7, msisdn.length());
        if(result == null)
            return false;
        else {

            String numbers[] = result.split(":");
            for(String number : numbers) {
                if(number.equals(searchKey))
                    return true;
            }

        }
        return false;
    }

    private class PostSMS implements Callable<Boolean> {

        private String smsXml;
        private String msisdn;

        PostSMS(String smsTxt, String msisdn) {
            this.smsXml = smsTxt;
            this.msisdn = msisdn;
        }

        @Override
        public Boolean call() throws Exception {
            logger.info("[{}] Sending SMS : {}", msisdn, smsXml);
            return postCoreJava(smsXml, msisdn);
        }
    }

    private boolean postCoreJava(String dataXml, String msisdn) {
        boolean success = false;
        HttpPost request = new HttpPost("https://mbnf.airtelworld.com:9443");
        int currentStatusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
        CloseableHttpResponse response = null;
        try {
            request.setEntity(new StringEntity(dataXml));
            final String username = "bs1b";
            final String password = "bs1b";
            if(username != null && username.trim().length() > 0) {
                String authString = username + ":" + password;
                String encValue = Base64.encodeBase64String(authString.getBytes());
                request.addHeader("Authorization", "Basic " + encValue);
            }
            request.addHeader("Content-Type", "text/xml");
            response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String responseStr = EntityUtils.toString(entity);
            StatusLine statusLine = response.getStatusLine();
            logger.info("Successfully sent message to msisdn: {}, responseCode: {}, response: {}", msisdn, statusLine.getStatusCode(), responseStr);
            currentStatusCode = statusLine.getStatusCode();
            success = true;
        }
        catch (ConnectionPoolTimeoutException th) {
            logger.error("Error Sending SMS to Msisdn: {} SMS: {}, ERROR: {}", msisdn, dataXml, th.getMessage(), th);
            logger.info("Timeout waiting for connection, Connection Pool Stats: {}", getConnectionPoolStats());
            success = false;
        }
        catch (Throwable th) {
            logger.error("Error Sending SMS to Msisdn: {} SMS: {}, ERROR: {}", msisdn, dataXml, th.getMessage(), th);
            success = false;
        }
        finally {
            logger.info("Going to populate Response Codes Daywise");
            populateResponseCodesDayWise(currentStatusCode);
            logger.info("Done populate Response Codes Daywise");
            try {
                response.close();
            }
            catch (IOException e) {
                // Not required to log
            }
        }
        return success;

    }

    private void populateResponseCodesDayWise(int currentStatusCode) {
        try {
            LocalDate today = new LocalDate();
            responseCodeWithCountPerDayMap.putIfAbsent(today, new ConcurrentHashMap<Integer, AtomicLong>(7, 1f, MAX_NO_OF_THREADS));
            ConcurrentHashMap<Integer, AtomicLong> statusCodesForToday = responseCodeWithCountPerDayMap.get(today);
            statusCodesForToday.putIfAbsent(currentStatusCode, new AtomicLong(0));
            AtomicLong currentStatusCodeCount = statusCodesForToday.get(currentStatusCode);
            currentStatusCodeCount.incrementAndGet();
        }
        catch (Exception e) {
            logger.error("Exception occured while populateResponseCodesDayWise" + e);
        }
    }

    private String createMTRequestXML(SMSMsg mrObject) {

        if(mrObject == null) {
            return null;
        }
        String toMsisdn = mrObject.getToMsisdn().startsWith("+")? mrObject.getToMsisdn().substring(1) : mrObject.getToMsisdn();
        StringBuilder strBuilder = new StringBuilder();

        strBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        strBuilder.append("<message>");
        strBuilder.append("<sms type=\"mt\">");
        if(mrObject.messageId != null && !mrObject.messageId.isEmpty()) {
            strBuilder.append("<destination messageid=\"").append(mrObject.messageId).append("\">");
        }
        else {
            strBuilder.append("<destination>");
        }
        strBuilder.append("<address>");
        strBuilder.append("<number type=\"international\">").append(toMsisdn).append("</number>");
        strBuilder.append("</address></destination>");
        if(mrObject.shortcode != null) {
            strBuilder.append("<source><address>").append("<alphanumeric>").append(mrObject.shortcode).append("</alphanumeric></address></source>");
        }
        else if(mrObject.getFromMsisdn() != null) {
            strBuilder.append("<source><address>").append(mrObject.getFromMsisdn()).append("</address></source>");
        }
        strBuilder.append("<rsr type=\"all\"/>");
        strBuilder.append("<ud type=\"").append(mrObject.udType).append("\">");
        // String is converted to hexString to support non english text too.
        strBuilder.append(convertToHexString(mrObject.message, false)[1]).append("</ud>");
        strBuilder.append("</sms></message>");
        return strBuilder.toString();
    }

    class SMSMsg {

        public boolean isMT;

        public String rsr        = "all";     // all | failure | success | delayed |
                                                // success_failure | success_delayed |
                                                // failure_delayed | sent | sent_delivered

        public String udType     = "text";    // text or bindary

        public String udEncoding = "default"; // unicode or default

        public String vpType     = "relative"; // absolute, relative

        // public Date vpDate;

        public String fromMsisdn;
        public String toMsisdn;

        public String message;
        public String messageId;
        public String shortcode;

        public String getToMsisdn() {
            return toMsisdn;
        }

        public void setToMsisdn(String toMsisdn) {
            this.toMsisdn = toMsisdn;
        }

        public String getFromMsisdn() {
            return fromMsisdn;
        }

        public void setFromMsisdn(String fromMsisdn) {
            this.fromMsisdn = fromMsisdn;
        }

        public String getMessageId() {
            return messageId;
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @Override
    public void shutdown() {
        writeSmsStatsToFile();
        try {
            if(null != httpClient) {
                httpClient.close();
            }
            if(null != executorService) {
                executorService.shutdown();
            }
        }
        catch (Throwable th) {
            logger.error("Error while destroying httpclient, ERROR: {}", th.getMessage(), th);
        }
    }

    public File getStatsFile() {
        File smsStatsFile = FileUtils.getFile(SMS_STATS_FILE_DIR, SMS_STATS_FILENAME);
        return smsStatsFile;
    }

    public void loadSmsStatsFromFile() {
        try {
            File statsFile = getStatsFile();
            String json = FileUtils.readFileToString(statsFile);
            TypeReference<ConcurrentHashMap<LocalDate, ConcurrentHashMap<Integer, AtomicLong>>> typeReference = new TypeReference<ConcurrentHashMap<LocalDate, ConcurrentHashMap<Integer, AtomicLong>>>() {
            };
            ObjectReader reader = objectMapper.reader(typeReference);
            responseCodeWithCountPerDayMap = reader.readValue(json);
            logger.info("Loaded sms stats from file :: {}", statsFile.getAbsolutePath());
            logger.info("Sms stats :: {}", responseCodeWithCountPerDayMap);
        }
        catch (Exception e) {
            logger.error("Exception while loading sms stats file", e);
        }
    }

    public void writeSmsStatsToFile() {
        try {
            File statsFile = getStatsFile();
            TypeReference<ConcurrentHashMap<LocalDate, ConcurrentHashMap<Integer, AtomicLong>>> typeReference = new TypeReference<ConcurrentHashMap<LocalDate, ConcurrentHashMap<Integer, AtomicLong>>>() {
            };
            ObjectWriter writer = objectMapper.writerWithType(typeReference);
            String json = writer.writeValueAsString(responseCodeWithCountPerDayMap);
            FileUtils.writeStringToFile(statsFile, json);
            logger.info("Successfully updated Sms stats file :: {}", statsFile.getAbsolutePath());
            logger.info("Sms stats :: {}", responseCodeWithCountPerDayMap);
        }
        catch (Exception e) {
            logger.error("Exception while saving sms stats file :: ", e);
        }
    }

    @Override
    public String getConnectionPoolStats() {
        String response = null;
        if(null != connectionManager) {
            PoolStats stats = connectionManager.getTotalStats();
            response = stats.toString();
        }
        return response;
    }

    @Override
    public String getThreadPoolStats() {
        String response = StringUtils.EMPTY;
        if(null != executorService) {
            ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) executorService;
            response = poolExecutor.toString();
        }
        return response;
    }

    @Override
    public String getResponseCodeStats() {
        return String.valueOf(responseCodeWithCountPerDayMap);
    }

    @Override
    public String getTodayResponseCodeStats() {
        LocalDate today = new LocalDate();
        return String.valueOf(responseCodeWithCountPerDayMap.get(today));
    }

    private ConcurrentHashMap<Integer, AtomicLong> getYesterdayResponseCodeStats() {
        LocalDate yesterday = new LocalDate().minusDays(1);
        return responseCodeWithCountPerDayMap.get(yesterday);
    }

    @Scheduled(cron = "0 0 10 * * *")
    public void emailYesterdayAirtelSMSServerResponseCodeStats() {
        LocalDate lastMonthDate = new LocalDate().minusDays(31);
        responseCodeWithCountPerDayMap.remove(lastMonthDate);

        writeSmsStatsToFile();

        logger.info("Going to email Yesterday's Airtel Sms Server Response code");
        StringBuilder summaryStr = new StringBuilder("Yesterday's SMS Server's Summary: <br/><br/>");
        ConcurrentHashMap<Integer, AtomicLong> yesterdaysResponseCodeMap = getYesterdayResponseCodeStats();
        Iterator<Entry<Integer, AtomicLong>> iterator = yesterdaysResponseCodeMap.entrySet().iterator();
        while(iterator.hasNext()) {
            Entry<Integer, AtomicLong> entry = iterator.next();
            Integer statusCode = entry.getKey();
            AtomicLong count = entry.getValue();
            summaryStr.append(statusCode).append(" (").append(HttpStatus.getStatusText(statusCode)).append(") : ").append(count).append("<br/>");
        }
        String summary = summaryStr.toString();
        emailSender.sendGmail("api.alert@wynk.in", toEmailIds, null, null, "Yesterday's Airtel Sms Server Response code Summary", summary,
                "api.alert@wynk.in", "wynk@123");
        logger.info("Email sent for Yesterday's Airtel Sms Server Response code");
    }

}
