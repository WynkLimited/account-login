package com.wynk.utils;

import au.com.bytecode.opencsv.CSVReader;
import com.wynk.common.ExceptionTypeEnum;
import com.wynk.common.PortalException;
import com.wynk.server.ChannelContext;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;

/**
 * Created by IntelliJ IDEA. User: bhuvangupta Date: 10/10/12
 */
public class HttpClient {

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class.getCanonicalName());
    private static final int CONNECTION_TIMEOUT_MILLIS =2000;
    private static final int SOCKET_TIMEOUT_MILLIS = 4000;
    private static final int SOCKET_TIMEOUT_MILLIS_PAYMENT = 30000;

    // Create a trust manager that does not validate certificate chains
    static TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
        }
    }};

    private static Map<Integer, org.apache.http.client.HttpClient> httpClientMap = new ConcurrentHashMap<>();
    private static PoolingClientConnectionManager poolingClientConnectionManager;

    static {
        poolingClientConnectionManager = new PoolingClientConnectionManager();
        poolingClientConnectionManager.setMaxTotal(500);
        poolingClientConnectionManager.setDefaultMaxPerRoute(200);
    }

    private static org.apache.http.client.HttpClient getHttpClient(int timeOut) {
        org.apache.http.client.HttpClient httpClient = httpClientMap.get(timeOut);
        if (httpClient == null) {
            httpClient = new DecompressingHttpClient(new DefaultHttpClient(poolingClientConnectionManager));
            HttpParams params = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, timeOut);
            HttpConnectionParams.setSoTimeout(params, timeOut);
            httpClientMap.put(timeOut, httpClient);
        }
        return httpClient;
    }

    public static HttpURLConnection getHttpConnection(String destinationURL, String username, String password, boolean ssl, String requestMethod, int timeoutMS) throws
            Exception {
        HttpURLConnection httpConnection = null;
        URL testUrl = new URL(destinationURL);
        httpConnection = (HttpURLConnection) testUrl.openConnection();

        if (ssl) {
            SSLContext sc = SSLContext.getInstance("SSLv3");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory sslsocketfactory = sc.getSocketFactory();
            ((HttpsURLConnection) httpConnection).setSSLSocketFactory(sslsocketfactory);
            ((HttpsURLConnection) httpConnection).setHostnameVerifier(hostnameVerifier);
        }
        httpConnection.setRequestMethod(requestMethod);
        httpConnection.setInstanceFollowRedirects(false);
        httpConnection.setDoInput(true);
        httpConnection.setDoOutput(true);
        httpConnection.setConnectTimeout(timeoutMS);
        httpConnection.setReadTimeout(timeoutMS);

        logger.debug("Created HTTP Connection with : " + destinationURL);

        // add basic auth header if provided.
        if (username != null && !username.isEmpty()) {
            String authString = username + ":" + password;
            String encoding = new String(Base64.getEncoder().encode(authString.getBytes()));
            encoding = encoding.replaceAll("\\n", "");
            httpConnection.setRequestProperty("Authorization", "Basic " + encoding);
        }
        return httpConnection;
    }

    private static String getResponse(HttpURLConnection httpConnection) throws PortalException, IOException {
        int responseCode = httpConnection.getResponseCode();
        logger.info("Resp Code:" + responseCode);
        logger.info("Resp Message:" + httpConnection.getResponseMessage());

        if (responseCode >= 400) {
            InputStream errorStream = httpConnection.getErrorStream();
            String errorMessage = IOUtil.readData(errorStream, true);

            throw new PortalException("Error getting response data. " + responseCode + "-" + httpConnection.getResponseMessage() + ". Error : " + errorMessage);
        }

        return IOUtil.readData(httpConnection.getInputStream(),true);
    }

    private static String getResponseMessage(HttpURLConnection httpConnection) throws PortalException, IOException {
        int responseCode = httpConnection.getResponseCode();
        logger.info("Resp Code:" + responseCode);
        logger.info("Resp Message:" + httpConnection.getResponseMessage());

        if (responseCode >= 400) {
            InputStream errorStream = httpConnection.getErrorStream();
            String errorMessage = IOUtil.readData(errorStream, true);

            throw new PortalException("Error getting response data. " + responseCode + "-" + httpConnection.getResponseMessage() + ". Error : " + errorMessage);
        }

        return responseCode+"";
    }

    /**
     * Java internally maintains a pool of HTTPConnections to a destination. This is controlled by
     * System property "http.maxConnections"
     *
     * @param destinationURL
     * @param username
     * @param password
     * @param ssl
     * @param contentType
     * @param dataXml
     */
    public static String postData(String destinationURL, String username, String password, boolean ssl, String contentType, String dataXml, int timeoutMS) throws PortalException {
        try {
            return postData(destinationURL, username, password, ssl, contentType, dataXml.getBytes("utf-8"), timeoutMS);
        } catch (UnsupportedEncodingException e) {
            throw new PortalException("Error posting data. Error : " + e.getMessage(), e);
        }
    }

    public static String postData(String destinationURL, String username, String password, boolean ssl, String contentType, byte[] dataXml, int timeoutMS) throws PortalException {
        long sTime = System.currentTimeMillis();

        try {
            UUID uuid = UUID.randomUUID();
            String messageId = uuid.toString();
            logger.info("Solace Message Id is: {}", messageId);
            HttpURLConnection httpConnection = getHttpConnection(destinationURL, username, password, ssl, "POST", timeoutMS);
            httpConnection.setRequestProperty("Content-Type", contentType);
            httpConnection.setRequestProperty("Solace-Message-ID", messageId);
            //logger.info("Solace HTTP connection request headers are: {}", httpConnection.getHeaderFields());
            // httpsConnection.connect();
            long eTime = System.currentTimeMillis();
            logger.info("HTTP Request Time :  " + (eTime - sTime));
            DataOutputStream output = new DataOutputStream(httpConnection.getOutputStream());
            output.write(dataXml);
            output.flush();
            return getResponseMessage(httpConnection);
        } catch (Throwable thr) {
            throw new PortalException("Error posting data. Error : " + thr.getMessage(), thr);
        }
    }

    public static String postDataSolace(String destinationURL, String username, String password, boolean ssl, String contentType, String dataXml, int timeoutMS) throws PortalException {
        try {
            return postDataSolace(destinationURL, username, password, ssl, contentType, dataXml.getBytes("utf-8"), timeoutMS);
        } catch (UnsupportedEncodingException e) {
            throw new PortalException("Error posting data. Error : " + e.getMessage(), e);
        }
    }

    public static String postDataSolace(String destinationURL, String username, String password, boolean ssl, String contentType, byte[] dataXml, int timeoutMS) throws PortalException {
        long sTime = System.currentTimeMillis();

        try {
            HttpURLConnection httpConnection = getHttpConnection(destinationURL, username, password, ssl, "POST", timeoutMS);
            httpConnection.setRequestProperty("Content-Type", contentType);
            UUID uuid = UUID.randomUUID();
            String messageId = uuid.toString();
            logger.info("GUID for solace is :{}",messageId);
            httpConnection.setRequestProperty("Solace-Message-ID", messageId);
            logger.info("Solace HTTP connection request headers are: {}", httpConnection.getHeaderField("Content-Type"));
            // httpsConnection.connect();
            long eTime = System.currentTimeMillis();
            logger.info("HTTP Request Time :  " + (eTime - sTime));
            DataOutputStream output = new DataOutputStream(httpConnection.getOutputStream());
            output.write(dataXml);
            output.flush();
            return getResponseMessage(httpConnection);
        } catch (Throwable thr) {
            throw new PortalException("Error posting data. Error : " + thr.getMessage(), thr);
        }
    }
    
    public static void postJsonData(String url, String data) {
        logger.info("sending sms, URL : {}  , MESSAGE : {}",url,data);
        HttpPost post = null;
        try {
            post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json");
            ByteArrayInputStream bis = new ByteArrayInputStream(data.getBytes());
            HttpEntity entity = new InputStreamEntity(bis, bis.available());
            post.setEntity(entity);
            HttpResponse response = getHttpClient(SOCKET_TIMEOUT_MILLIS).execute(post);
            EntityUtils.consumeQuietly(response.getEntity());
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() >= 400) {
                throw new Exception(String.format("Got Http error code [%s]", statusLine.getStatusCode()));
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Got status code [{}] for url [{}]", statusLine.getStatusCode(), url);
                }
            }
        } catch (Exception e) {
            logger.error("Error requesting url [{}] with timeout [{}]", url, SOCKET_TIMEOUT_MILLIS, e);
        	
            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e, 
                    ExceptionTypeEnum.CODE.name(),
                    "",
                    "HttpClient.postData",
                    "Error requesting url:" + url + " timeout " + SOCKET_TIMEOUT_MILLIS);
            
        } finally {
            if (post != null)
                post.releaseConnection();
        }
    }


    public static void postData(String url, String data) {
        HttpPost post = null;
        try {
            post = new HttpPost(url);
            post.setHeader("Content-Type", "text/xml");
            ByteArrayInputStream bis = new ByteArrayInputStream(data.getBytes());
            HttpEntity entity = new InputStreamEntity(bis, bis.available());
            post.setEntity(entity);
            HttpResponse response = getHttpClient(SOCKET_TIMEOUT_MILLIS).execute(post);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() >= 400) {
                throw new Exception(String.format("Got Http error code [%s]", statusLine.getStatusCode()));
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Got status code [{}] for url [{}]", statusLine.getStatusCode(), url);
                }
            }
        } catch (Exception e) {
            logger.error("Error requesting url [{}] with timeout [{}]", url, SOCKET_TIMEOUT_MILLIS, e);
        	
            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e, 
                    ExceptionTypeEnum.CODE.name(),
                    "",
                    "HttpClient.postData",
                    "Error requesting url:" + url + " timeout " + SOCKET_TIMEOUT_MILLIS);
            
        } finally {
            if (post != null)
                post.releaseConnection();
        }
    }
    
    private static final String X_BSY_ATKN               = "x-bsy-atkn";
    private static final String X_BSY_DATE               = "x-bsy-date";
 
    
    public static String postDataWithAuthentication(String url, String data, String key, String appId) {
        long requestTimestamp = System.currentTimeMillis();
        String responseStr = null;
        HttpPost post = null;
        try {
            post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json");
           
            URI uri = post.getURI();
            String requestUri = uri.getPath();
            if (StringUtils.isNotBlank(uri.getQuery())) {
                requestUri += '?' + uri.getQuery();
            }
            
            String signature = MusicUtils.generateSignature(post.getMethod(), requestUri, data, requestTimestamp, key);
            
            post.setHeader(X_BSY_ATKN, new StringBuilder(appId).append(":").append(signature).toString());
            post.setHeader(X_BSY_DATE, String.valueOf(requestTimestamp));
            
            ByteArrayInputStream bis = new ByteArrayInputStream(data.getBytes());
            HttpEntity entity = new InputStreamEntity(bis, bis.available());
            post.setEntity(entity);
            
            HttpResponse response = getHttpClient(SOCKET_TIMEOUT_MILLIS).execute(post);
            StatusLine statusLine = response.getStatusLine();
            
            HttpEntity responseEntity = response.getEntity();
            responseStr = EntityUtils.toString(responseEntity);
            
            if (statusLine.getStatusCode() >= 400) {
                throw new Exception(String.format("Got Http error code [%s]", statusLine.getStatusCode()));
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Got status code [{}] for url [{}]", statusLine.getStatusCode(), url);
                }
            }
            
            
            
        } catch (Exception e) {
            logger.error("Error requesting url [{}] with timeout [{}]", url, SOCKET_TIMEOUT_MILLIS, e);
            
            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e, 
                    ExceptionTypeEnum.CODE.name(),
                    "",
                    "HttpClient.postData",
                    "Error requesting url:" + url + " timeout " + SOCKET_TIMEOUT_MILLIS);
            
        } finally {
            if (post != null)
                post.releaseConnection();
            
            
        }
        
        return responseStr;
    }

    public static String postData(String url, String data, Map<String,String> headers) throws Exception {
        return postDataWithTimeOut(url, data, headers, SOCKET_TIMEOUT_MILLIS);
    }

    public static String postDataWithTimeOut(String url, String data, Map<String,String> headers, int timeOut) throws Exception {
        HttpPost post = null;
        try {
            post = new HttpPost(url);
            for (Map.Entry<String, String> header : headers.entrySet())
        	{
        		post.setHeader(header.getKey(),header.getValue());
        	}
            ByteArrayInputStream bis = new ByteArrayInputStream(data.getBytes());
            HttpEntity entity = new InputStreamEntity(bis, bis.available());
            post.setEntity(entity);
            HttpResponse response = getHttpClient(timeOut).execute(post);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() >= 400) {
                throw new Exception(String.format("Got Http error code [%s]", statusLine.getStatusCode()));
            } else {
            	String responseString =  IOUtil.readData(response.getEntity().getContent(), true);
            	logger.info("Got status code [{}] for url [{}]", statusLine.getStatusCode(), url);
            	return responseString;
            }
        } catch (Exception e) {
            logger.error("Error requesting url [{}] with timeout [{}]", url, SOCKET_TIMEOUT_MILLIS, e);
        	
            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e, 
                    ExceptionTypeEnum.CODE.name(),
                    "",
                    "HttpClient.postData",
                    "Error requesting url:" + url + " timeout " + SOCKET_TIMEOUT_MILLIS);
            
            throw e;
        } finally {
            if (post != null)
                post.releaseConnection();
        }
    }
    
    public static String postData(String url, Map<String, String> postValues) {
        HttpPost post = null;

        try {
            post = new HttpPost(url);
            //post.setHeader("Content-Type", "text/xml");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Iterator<String> keys = postValues.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = postValues.get(key);
                nvps.add(new BasicNameValuePair(key, value));
            }

            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

            HttpResponse response = getHttpClient(SOCKET_TIMEOUT_MILLIS).execute(post);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() / 100 == 2) {
                HttpEntity httpEntity = response.getEntity();
                if (httpEntity != null) {
                    return readContent(httpEntity);
                }
            } else {
                if (statusLine.getStatusCode() >= 400) {
                    throw new Exception(String.format("Got Http error code [%s]", statusLine.getStatusCode()));
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Got status code [{}] for url [{}]", statusLine.getStatusCode(), url);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error requesting url [{}] with timeout [{}]", url, SOCKET_TIMEOUT_MILLIS, e);
        
            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e, 
                    ExceptionTypeEnum.CODE.name(),
                    "",
                    "HttpClient.postData",
                    "Error requesting url:" + url + " timeout " + SOCKET_TIMEOUT_MILLIS);
            
        } finally {
            if (post != null)
                post.releaseConnection();
        }
        return null;
    }

    public static String postData(String url, Map<String, String> postValues, Map<String, String> headers) {
        HttpPost post = null;

        try {
            post = new HttpPost(url);
            for (Map.Entry<String, String> header : headers.entrySet()) {
                post.setHeader(header.getKey(),header.getValue());
            }
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Iterator<String> keys = postValues.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = postValues.get(key);
                nvps.add(new BasicNameValuePair(key, value));
            }

            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

            HttpResponse response = getHttpClient(SOCKET_TIMEOUT_MILLIS).execute(post);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() / 100 == 2) {
                HttpEntity httpEntity = response.getEntity();
                if (httpEntity != null) {
                    return readContent(httpEntity);
                }
            } else {
                if (statusLine.getStatusCode() >= 400) {
                    throw new Exception(String.format("Got Http error code [%s]", statusLine.getStatusCode()));
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Got status code [{}] for url [{}]", statusLine.getStatusCode(), url);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error requesting url [{}] with timeout [{}]", url, SOCKET_TIMEOUT_MILLIS, e);

            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e,
                ExceptionTypeEnum.CODE.name(),
                "",
                "HttpClient.postData",
                "Error requesting url:" + url + " timeout " + SOCKET_TIMEOUT_MILLIS);

        } finally {
            if (post != null)
                post.releaseConnection();
        }
        return null;
    }

    public static String postData(String url, Map<String, String> postValues, String proxyIP, String proxyPort) {
        HttpPost post = null;

        try {
            post = new HttpPost(url);
            //post.setHeader("Content-Type", "text/xml");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Iterator<String> keys = postValues.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = postValues.get(key);
                nvps.add(new BasicNameValuePair(key, value));
            }

            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

            HttpResponse response = getHttpClient(SOCKET_TIMEOUT_MILLIS).execute(post);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() / 100 == 2) {
                HttpEntity httpEntity = response.getEntity();
                if (httpEntity != null) {
                    return readContent(httpEntity);
                }
            } else {
                if (statusLine.getStatusCode() >= 400) {
                    throw new Exception(String.format("Got Http error code [%s]", statusLine.getStatusCode()));
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Got status code [{}] for url [{}]", statusLine.getStatusCode(), url);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error requesting url [{}] with timeout [{}]", url, SOCKET_TIMEOUT_MILLIS, e);
            
            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e, 
                    ExceptionTypeEnum.CODE.name(),
                    "",
                    "HttpClient.postData",
                    "Error requesting url:" + url + " timeout " + SOCKET_TIMEOUT_MILLIS);
            
        } finally {
            if (post != null)
                post.releaseConnection();
        }
        return null;
    }

    public static String getData(String destinationURL, String username, String password, boolean ssl, int timeoutMS) throws PortalException {
        long sTime = System.currentTimeMillis();
        try {
            HttpURLConnection httpConnection = getHttpConnection(destinationURL, username, password, ssl, "GET", timeoutMS);
            // httpsConnection.connect();
            long eTime = System.currentTimeMillis();
            String response = getResponse(httpConnection);
            logger.info("HTTP Request Time :  " + (System.currentTimeMillis() - sTime));
            return response;
        } catch (Throwable thr) {
            throw new PortalException("Error getting data. Error : " + thr.getMessage(), thr);
        }
    }

    public static String getContent(String url) {
    	return getContentWithHeaders( url, null);
    }
    /**
     * User Apache HTTP Library
     *
     * @param url
     * @return
     */
    public static String getContentWithHeaders(String url, HashMap<String, String> headers) {
        HttpGet get = null;
        try {
            get = new HttpGet(url);

            
            /// add headers if passed.
            if (headers != null) {
            	for (String key: headers.keySet()) {
            		get.addHeader(key, headers.get(key));
            	}
            }
            
            
            HttpResponse response = getHttpClient(SOCKET_TIMEOUT_MILLIS_PAYMENT).execute(get);
            
            
            StatusLine statusLine = response.getStatusLine();
            HttpEntity httpEntity = response.getEntity();
            String responseStr = null;
            if (httpEntity != null) {
            	responseStr = readContent(httpEntity);
            }
            
            if (statusLine.getStatusCode() / 100 == 2) {
                return responseStr;
            } else {
                if (statusLine.getStatusCode() >= 400) {
                    throw new Exception(String.format("Got Http error code [%s]", statusLine.getStatusCode()));
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Got status code [{}] for url [{}]", statusLine.getStatusCode(), url);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error requesting url [{}] with timeout [{}]", url, SOCKET_TIMEOUT_MILLIS, e);
        	logger.info("Connection Pool Stats:: [{}]", poolingClientConnectionManager.getTotalStats());
            
            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e, 
                    ExceptionTypeEnum.CODE.name(),
                    "",
                    "HttpClient.getContent",
                    "Error requesting url:" + url + " timeout " + SOCKET_TIMEOUT_MILLIS);
            
        } finally {
            if (get != null) {
                get.releaseConnection();
            }
        }
        return null;
    }

    
    
    public static String getContent(String url, int timeOut) {

        HttpGet get = null;
        try {
            get = new HttpGet(url);

            HttpResponse response = getHttpClient(timeOut).execute(get);
            StatusLine statusLine = response.getStatusLine();
            HttpEntity httpEntity = response.getEntity();
            String responseStr = null;
            if (httpEntity != null) {
            	responseStr = readContent(httpEntity);
            }
            
            if (statusLine.getStatusCode() / 100 == 2) {
                return responseStr;
            } else {
                if (statusLine.getStatusCode() >= 400) {
                	
                    throw new Exception(String.format("Got Http error code [%s]", statusLine.getStatusCode()));
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Got status code [{}] for url [{}]", statusLine.getStatusCode(), url);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error requesting url [{}] with timeout [{}]", url, timeOut, e);
        	logger.info("Connection Pool Stats:: [{}]", poolingClientConnectionManager.getTotalStats());
            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e, 
                    ExceptionTypeEnum.CODE.name(),
                    "",
                    "HttpClient.getContent",
                    "Error requesting url:" + url + " timeout " + timeOut);
            
        } finally {
            if (get != null) {
                get.releaseConnection();
            }
        }
        return null;
    }

    static HostnameVerifier hostnameVerifier = new HostnameVerifier() {

        public boolean verify(String urlHostName, SSLSession session) {
            System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
            return true;
        }
    };
    public static String getData(String url, int timeout){

        String ResponseContent = new String();
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse httpResponse = getHttpClient(timeout).execute(get);
            if(httpResponse.getStatusLine().getStatusCode()==200){
                InputStream in = httpResponse.getEntity().getContent();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buff =new byte[1024];
                int i =0;
                while((i=in.read(buff))!=-1){
                    bos.write(buff,0,i);
                }
                ResponseContent = new String(bos.toByteArray(), "utf-8");
            }
        }catch (Exception e) {
            logger.error("Exception in getting data: " + url, e.getMessage());
//            System.out.println(e.getMessage());
        } finally{
            if (get != null) {
                get.releaseConnection();
            }
        }

        return ResponseContent;
    }

    public static String getContent(String url, String charset) {
        return getContent(url, charset, SOCKET_TIMEOUT_MILLIS + 10);
    }
    public static String getContent(String url, String charset, int timeout) {
        HttpPost postRequest = null;
        try {
            postRequest = new HttpPost(url);
            postRequest.addHeader("charset", charset);

            HttpResponse response = getHttpClient(timeout).execute(postRequest);
            StatusLine statusLine = response.getStatusLine();
            HttpEntity httpEntity = response.getEntity();
            String responseStr = null;
            if (httpEntity != null) {
            	responseStr = readContent(httpEntity);
            }
            
            if (statusLine.getStatusCode() / 100 == 2) {
                return responseStr;
            } else {
                if (statusLine.getStatusCode() >= 400) {
                    throw new Exception(String.format("Got Http error code [%s]", statusLine.getStatusCode()));
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Got status code [{}] for url [{}]", statusLine.getStatusCode(), url);
                    }
                }
            }
        } catch (Exception e) {
        	
            
            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e, 
                    ExceptionTypeEnum.CODE.name(),
                    "",
                    "HttpClient.getContent",
                    "Error requesting url:" + url + " timeout " + SOCKET_TIMEOUT_MILLIS);
            
            logger.error("Error requesting url [{}] with timeout [{}]", url, timeout, e);
        	logger.info("Connection Pool Stats:: [{}]", poolingClientConnectionManager.getTotalStats());
        } finally {
            if (postRequest != null) {
                postRequest.releaseConnection();
            }
        }
        return null;
    }

    public static String getNdsContent(String url, int timeOut, Map<String, String> headers) throws HttpException{

        HttpGet get = null;
        try {
            get = new HttpGet(url);
            if (headers != null && headers.size() > 0) {
                Iterator<String> headerItr = headers.keySet().iterator();
                while (headerItr.hasNext()) {
                    String name = headerItr.next();
                    String value = headers.get(name);
                    get.addHeader(name, value);
                }
            }

//            HttpHost proxy = new HttpHost("125.16.71.167", 1090, "http");
//
//            RequestConfig config = RequestConfig.custom()
//                    .setProxy(proxy)
//                    .build();
//            get.setConfig(config);
            HttpResponse response = getHttpClient(timeOut).execute(get);
            StatusLine statusLine = response.getStatusLine();
            HttpEntity httpEntity = response.getEntity();
            String responseStr = null;
            if (httpEntity != null) {
            	responseStr = readContent(httpEntity);
            }
            
            if (statusLine.getStatusCode() / 100 == 2) {
                return responseStr;
            } else {
                if (statusLine.getStatusCode() >= 400) {
                    throw new HttpException(String.format("Got Http error code [%s]", statusLine.getStatusCode()));
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Got status code [{}] for url [{}]", statusLine.getStatusCode(), url);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error requesting url [{}] with timeout [{}]", url, timeOut, e);
        	logger.info("Connection Pool Stats:: [{}]", poolingClientConnectionManager.getTotalStats());
            
            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e, 
                    ExceptionTypeEnum.CODE.name(),
                    "",
                    "HttpClient.getContent",
                    "Error requesting url:" + url + " timeout " + timeOut);
            
        } finally {
            if (get != null) {
                get.releaseConnection();
            }
        }
        return null;

    }    public static String getContent(String url, int timeOut, Map<String, String> headers) {

        HttpGet get = null;
        try {
            get = new HttpGet(url);
            if (headers != null && headers.size() > 0) {
                Iterator<String> headerItr = headers.keySet().iterator();
                while (headerItr.hasNext()) {
                    String name = headerItr.next();
                    String value = headers.get(name);
                    get.addHeader(name, value);
                }
            }

//            HttpHost proxy = new HttpHost("125.16.71.167", 1090, "http");
//
//            RequestConfig config = RequestConfig.custom()
//                    .setProxy(proxy)
//                    .build();
//            get.setConfig(config);
            HttpResponse response = getHttpClient(timeOut).execute(get);
            StatusLine statusLine = response.getStatusLine();
            HttpEntity httpEntity = response.getEntity();
            String responseStr = null;
            if (httpEntity != null) {
            	responseStr = readContent(httpEntity);
            }

            if (statusLine.getStatusCode() / 100 == 2) {
                return responseStr;
            } else {
                if (statusLine.getStatusCode() >= 400) {
                    throw new Exception(String.format("Got Http error code [%s]", statusLine.getStatusCode()));
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Got status code [{}] for url [{}]", statusLine.getStatusCode(), url);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error requesting url [{}] with timeout [{}]", url, timeOut, e);
        	logger.info("Connection Pool Stats:: [{}]", poolingClientConnectionManager.getTotalStats());

            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e,
                    ExceptionTypeEnum.CODE.name(),
                    "",
                    "HttpClient.getContent",
                    "Error requesting url:" + url + " timeout " + timeOut);

        } finally {
            if (get != null) {
                get.releaseConnection();
            }
        }
        return null;

    }

    public static List<String[]> extractCsvFromGz(String url, String charset) {
        HttpGet postRequest = null;
        try {
            postRequest = new HttpGet(url);
            postRequest.addHeader("charset", charset);
            HttpResponse response = getHttpClient(SOCKET_TIMEOUT_MILLIS).execute(postRequest);
            StatusLine statusLine = response.getStatusLine();
            HttpEntity httpEntity = response.getEntity();
            if (statusLine.getStatusCode() / 100 == 2) {
                if (httpEntity != null) {
                    BufferedReader br = null;
                    try {
                        InputStream is = httpEntity.getContent();
                        GZIPInputStream gis = new GZIPInputStream(is);
                        br = new BufferedReader(new InputStreamReader(gis, charset));
                        CSVReader reader = new CSVReader(br);
                        return reader.readAll();
                    } finally {
                        if (br != null) {
                            try {
                                br.close();
                            } catch (IOException ignore) {

                            }
                        }
                    }
                }
            } else {
                if (httpEntity != null) {
                	readContent(httpEntity); //Consuming inputstream
                }
            	if (statusLine.getStatusCode() >= 400) {
                    throw new Exception(String.format("Got Http error code [%s]", statusLine.getStatusCode()));
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Got status code [{}] for url [{}]", statusLine.getStatusCode(), url);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error requesting url [{}] with timeout [{}]", url, SOCKET_TIMEOUT_MILLIS, e);
        	logger.info("Connection Pool Stats:: [{}]", poolingClientConnectionManager.getTotalStats());
        
            LogstashLoggerUtils.createFatalExceptionLogWithMessage(e, 
                    ExceptionTypeEnum.THIRD_PARTY.AWS.name(),
                    "",
                    "HttpClient.extractCsvFromGz",
                    "Error requesting url " + url);
            
        } finally {
            if (postRequest != null) {
                postRequest.releaseConnection();
            }
        }
        return Collections.emptyList();
    }

    public static void copyData(OutputStream outputStream, String destinationURL, String username, String password, boolean ssl, int timeoutMS) throws PortalException {
        long sTime = System.currentTimeMillis();
        HttpURLConnection httpConnection = null;
        InputStream inputStream = null;
        try {
            httpConnection = getHttpConnection(destinationURL, username, password, ssl, "GET", timeoutMS);
            int responseCode = httpConnection.getResponseCode();
            logger.debug("Resp Code:" + responseCode);
            logger.debug("Resp Message:" + httpConnection.getResponseMessage());

            if (responseCode >= 400) {
                InputStream errorStream = httpConnection.getErrorStream();
                String errorMessage = IOUtil.readData(errorStream, true);
                throw new PortalException("Error posting data. " + responseCode + "-" + httpConnection.getResponseMessage() + ". Error : " + errorMessage);
            }

            inputStream = httpConnection.getInputStream();
            IOUtils.copy(inputStream, outputStream);

            logger.info("HTTP Request Time :  " + (System.currentTimeMillis() - sTime));
        } catch (Throwable thr) {
            throw new PortalException("Error getting data. Error : " + thr.getMessage(), thr);
        } finally {
            IOUtils.closeQuietly(inputStream);
            if (httpConnection != null)
                httpConnection.disconnect();
        }
    }    
    
	private static String readContent(HttpEntity httpEntity) throws IOException {
		StringBuilder content = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(
					httpEntity.getContent(), "UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				content.append(line).append("\n");
			}
			return content.toString();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ignore) {

				}
			}
		}
	}
	
	private static String readContent(InputStream contentStream) throws IOException {
        StringBuilder content = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(
                    contentStream, "UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignore) {

                }
            }
        }
    }
    
    public static String postData(String url, String data, Map<String, String> headerParams, int timeout) {
        HttpPost post = null;
        String responseString = null;
        try {
            post = new HttpPost(url);
            Iterator itr = headerParams.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry mapEntry = (Map.Entry) itr.next();
                String headerAttribute = (String) mapEntry.getKey();
                String headerValue = (String) mapEntry.getValue();
                post.setHeader(headerAttribute, headerValue);
            }
            ByteArrayInputStream bis = new ByteArrayInputStream(data.getBytes());
            HttpEntity entity = new InputStreamEntity(bis, bis.available());
            post.setEntity(entity);
            HttpResponse response = getHttpClient(timeout).execute(post);
            HttpEntity entityResponse = response.getEntity();
            responseString = EntityUtils.toString(entityResponse, "UTF-8");
        } catch (Exception e) {
            logger.error("Error requesting url [{}] with timeout [{}]", url, timeout, e);
            LogstashLoggerUtils.httpClientExceptionLogger(e, ChannelContext.getUid(), url);
        } finally {
            if (post != null)
                post.releaseConnection();
        }
        return responseString;
    }


    /**
     * User Apache HTTP Library
     *
     * @param url
     * @return
     */
    public static String getContent(String url, int timeOut, boolean throwEx) {
        HttpGet get = null;
        try {
            get = new HttpGet(url);
            URI photoUri = new URI(url);
            get.setURI(photoUri);
            HttpResponse response = getHttpClient(timeOut).execute(get);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() / 100 == 2) {
                HttpEntity httpEntity = response.getEntity();
                if (httpEntity != null) {
                    return readContent(httpEntity);
                }
            } else {
                if (statusLine.getStatusCode() >= 400) {
                    throw new Exception(String.format("Got Http error code [%s]", statusLine.getStatusCode()));
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Got status code [{}] for url [{}]", statusLine.getStatusCode(), url);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error requesting url [{}] with timeout [{}]", url, timeOut, e);
            if (throwEx) {
                throw new RuntimeException(e);
            }
        } finally {
            if (get != null) {
                get.releaseConnection();
            }
        }
        return null;
    }
    
    public static String postData(String url, String data, Map<String, String> headers, String charset) throws
            Exception {
        HttpPost post = null;
        try {
            post = new HttpPost(url);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("data", data));
            post.setEntity(new UrlEncodedFormEntity(nvps, charset));

            for(Map.Entry<String, String> header : headers.entrySet()) {
                post.setHeader(header.getKey(), header.getValue());
            }

            org.apache.http.client.HttpClient httpClient = getHttpClient(SOCKET_TIMEOUT_MILLIS);
            HttpResponse response = httpClient.execute(post);
            StatusLine statusLine = response.getStatusLine();
            logger.info( "post url ["+url+"] status code " +statusLine.getStatusCode()  );
            if(statusLine.getStatusCode() >= 400) {
                throw new Exception(String.format("Got Http error code [%s]", statusLine.getStatusCode()));
            }
            else {
                String responseString = IOUtil.readData(response.getEntity().getContent(), true);
                logger.info("Got status code [{}] for url [{}]", statusLine.getStatusCode(), url);
                return responseString;
            }
        }
        catch (Exception e) {
            logger.error("Error requesting url [{}] with timeout [{}]", url, SOCKET_TIMEOUT_MILLIS, e);

            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e, ExceptionTypeEnum.CODE.name(), ChannelContext.getUid(), "HttpClient.postData", "Error requesting url:" + url + " timeout "
                    + SOCKET_TIMEOUT_MILLIS);

            throw e;
        }
        finally {
            if(post != null)
                post.releaseConnection();
        }
    }
    
    private static final org.apache.commons.httpclient.MultiThreadedHttpConnectionManager connectionManager;
    private static final org.apache.commons.httpclient.HttpClient httpClient;

    static {
        connectionManager = new org.apache.commons.httpclient.MultiThreadedHttpConnectionManager();
        connectionManager.setMaxConnectionsPerHost( 500 );
        connectionManager.setMaxTotalConnections( 2000 );
        httpClient = new org.apache.commons.httpclient.HttpClient(connectionManager);
        httpClient.setConnectionTimeout( CONNECTION_TIMEOUT_MILLIS );
        httpClient.setTimeout(SOCKET_TIMEOUT_MILLIS);
    }

    public static void shutdown() {
        connectionManager.shutdown();
    }

    
    public static String getContentInMultiThreadEnv(String url){
        org.apache.commons.httpclient.HttpMethod method = new org.apache.commons.httpclient.methods.GetMethod(url);
    
        try {
            int statusCode = httpClient.executeMethod( method );
            if (statusCode / 100 == 2) {
                InputStream responseStream = method.getResponseBodyAsStream();
                if (responseStream != null) {
                    return readContent(responseStream);
                }
            } else {
                if (statusCode >= 400) {
                    throw new Exception(String.format("Got Http error code [%s]", statusCode));
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Got status code [{}] for url [{}]", statusCode, url);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error requesting url [{}] with timeout [{}]", url, SOCKET_TIMEOUT_MILLIS, e);
        } finally {
            method.releaseConnection();
        }
        return null;
    }
    
    public static void postDataInMultiThreadEnv(String url, String data) {
        org.apache.commons.httpclient.methods.PostMethod method = new org.apache.commons.httpclient.methods.PostMethod( url );
        try {
            StringRequestEntity requestEntity = new StringRequestEntity(data,"text/xml","UTF-8");
            method.setRequestEntity( requestEntity );
            int statusCode = httpClient.executeMethod( method );
            if (statusCode == 200) {
                logger.info("Got status code [{}] for url [{}]", statusCode, url);
            }
            if (statusCode >= 400) {
                throw new Exception(String.format("Got Http error code [%s]", statusCode));
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Got status code [{}] for url [{}]", statusCode, url);
                }
            }
        } catch (Exception e) {
            logger.error("Error requesting url [{}] with timeout [{}]", url, SOCKET_TIMEOUT_MILLIS, e);
        } finally {
            method.releaseConnection();
        }
    } 
}
