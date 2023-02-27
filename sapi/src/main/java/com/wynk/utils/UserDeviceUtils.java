package com.wynk.utils;

import com.bsb.portal.core.common.*;
import com.wynk.common.Country;
import com.wynk.common.MobileNetwork;
import com.wynk.constants.MusicConstants;
import com.wynk.constants.MusicSubscriptionPackConstants;
import com.wynk.constants.WCFChannelEnum;
import com.wynk.dto.WCFRegistrationChannel;
import com.wynk.music.WCFServiceType;
import com.wynk.music.dto.MusicPlatformType;
import com.wynk.server.ChannelContext;
import com.wynk.service.WCFService;
import com.wynk.service.helper.InjectionHelper;
import com.wynk.service.helper.WcfHelper;
import com.wynk.user.dto.User;
import com.wynk.user.dto.UserDevice;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

import static com.wynk.constants.MusicConstants.BACKEND_SERVICE_NAME;
import static com.wynk.music.dto.MusicPlatformType.isWynkApp;

/**
 * Created by bhuvangupta on 28/12/13.
 */
public class UserDeviceUtils {

    static final String X_BSY_IP = "x-bsy-ip";
	private static CircleCatalog circleCatalog = null;
    private static LoadProperty property = null;
    private static IpRange ipRange = null;
    private static final Integer MSN_UUV0_SIZE_MAX = Integer.valueOf(17);
    private static final Integer MSN_UUV1_SIZE_MAX = Integer.valueOf(27);
    private static String key;

    private static final Logger logger               = LoggerFactory.getLogger(UserDeviceUtils.class.getCanonicalName());


    static
    {
        try {
            property = new LoadProperty();
            circleCatalog = new CircleCatalog();
            ipRange = new IpRange(property.getWhitelist());
            key = property.getHashKey();

        }
        catch (Exception e) {
            e.printStackTrace();
//            logger.error("Error creating circle detector : "+e.getMessage(),e);
        }
    }

    public static String getOperatorViaCircleInfo(String msisdn) {
    	CircleInf circleInfo = getCircleInfo(msisdn);
    	try {
			return circleInfo.getOperatorName();
		} catch (Exception e) {
			logger.error("Error while getting operator from circle", e);
		}
    	return null;
    }
    
    public static CircleInf getCircleInfo(String msisdn)
    {
        if(StringUtils.isEmpty(msisdn))
            return null;

        msisdn = Utils.get12DigitMsisdn(msisdn);

        if(msisdn.startsWith("+"))
            msisdn = msisdn.substring(1,msisdn.length());
        CircleInf circleInf = circleCatalog.getCircleInfObj(msisdn);
        return circleInf;
    }

    public static boolean isIOS(String userAgent) {
        if (StringUtils.isBlank(userAgent)) {
            return false;
        }
        // True if matches.
        if (userAgent.contains("/iPhone/i") || userAgent.contains("/iPad/i") || userAgent.contains("/iPod/i")) {

            return true;
        }
        return false;
    }

    public static String getMsisdnFromBsyHeader(HttpRequest request)
    {
        if (request == null)
            return null;

        String msisdn = StringUtils.EMPTY;

        Set<String> headernames = request.headers().names();
        Iterator<String> headerNameItr = headernames.iterator();

        String xMsisdn = "";

        while (headerNameItr.hasNext()) {
            String name = headerNameItr.next();
            if (name.equalsIgnoreCase("x-bsy-msisdn")) {
                xMsisdn = request.headers().get(name);
                break;
            }
        }

        if(!StringUtils.isBlank(xMsisdn))
            msisdn = xMsisdn;

        return Utils.normalizePhoneNumber(msisdn);
    }

    public static String getNetworkEnrichedMsisdn(HttpRequest request) {
    	if (request == null)
    		return null;

        String msisdn = StringUtils.EMPTY;

        String ip = request.headers().get(X_BSY_IP);

         //if valid airtel IP
        if (!StringUtils.isBlank(ip) && !IPRangeUtil.isAirtelIPRange(ip, request))
            return null;

        Set<String> headernames = request.headers().names();
        Iterator<String> headerNameItr = headernames.iterator();

        String xMsisdn = "";
        Country country=Country.INDIA;
        while (headerNameItr.hasNext()) {
            String name = headerNameItr.next();
            if (name.equalsIgnoreCase("x-msisdn")) {
                xMsisdn = request.headers().get(name);
            }
            else if (name.equalsIgnoreCase("msisdn")) {
                msisdn = request.headers().get(name);
            } else if (name.equalsIgnoreCase("http_msisdn")) {
                //sri lanka changes
                String sl_msisdn = request.headers().get(name);
                if (sl_msisdn.startsWith("94")) {
                    country= Country.SRILANKA;
                    msisdn = sl_msisdn.substring(2, sl_msisdn.length());
                }
            }
        }

        if(!StringUtils.isBlank(xMsisdn))
            msisdn = xMsisdn;

        //DiwaliPfRatType ratObj = new DiwaliPfRatType(request);
        //obj.setRatType(ratObj.getRatType());
        return Utils.normalizePhoneNumber(msisdn,country);
    }
    
    public static String getMsisdn(HttpRequest request)
    {
    	if (request == null)
    		return null;

    	if (UserDeviceUtils.isThirdPartyPlatformRequest())
    		return UserDeviceUtils.getMsisdnFromBsyHeader(request);
    	
    	return getNetworkEnrichedMsisdn(request);
    }

    public static String getMsisdnFromRequest(HttpRequest request, Boolean isSamsungGTMDevice){
        String msisdn = null;
        if(isSamsungGTMDevice || UserDeviceUtils.isThirdPartyPlatformRequest(request))
            msisdn = UserDeviceUtils.getMsisdnFromBsyHeader(request);
        else {
            msisdn = getMsisdn(request);
        }
        return msisdn;
    }
    
    public static boolean isThirdPartyPlatformRequest() {
		return MusicPlatformType.isThirdPartyPlatformId(getPlatform().getPlatform());
	}
	
	public static boolean isThirdPartyPlatformRequest(HttpRequest request) {
		MusicPlatformType platform = getUserPlatformFromRequest(request);
		return MusicPlatformType.isThirdPartyPlatformId(platform.getPlatform());
	}
    
	    
    public static MusicPlatformType getPlatform() {
    	if (ChannelContext.getPlatform() != null)
    		return MusicPlatformType.getPlatformById(ChannelContext.getPlatform());
    	else
    		return getUserPlatformFromRequest(ChannelContext.getRequest());
    }
    
    public static MusicPlatformType getUserPlatformFromRequest(HttpRequest request)
    {
        MusicPlatformType platform = MusicPlatformType.WYNK_APP;

        if (request == null)
            return platform;

        Set<String> headernames = request.headers().names();
        Iterator<String> headerNameItr = headernames.iterator();

        while (headerNameItr.hasNext()) {
            String name = headerNameItr.next();
            if (name.equalsIgnoreCase("x-bsy-platform")) {
                String platformName = request.headers().get(name);
                platform = MusicPlatformType.getPlatformByName(platformName);
                break;
            }
        }

        return platform;
    }

    public static String getClientIPOnly(HttpRequest request)
    {
        String ip = request.headers().get("x-forwarded-for");
        if(StringUtils.isBlank(ip))
            return request.headers().get(X_BSY_IP);

        return ip;
    }


    public static String getClientIP(HttpRequest request)
    {
        List<String> forwardedForHeaderValues = request.headers().getAll("x-forwarded-for");
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < forwardedForHeaderValues.size(); i++) {

            String ips = (String) forwardedForHeaderValues.get(i);
            if(StringUtils.isBlank(ips))
                continue;
            ips = ips.replaceAll(",","|");
            buffer.append(ips);
            if(i < (forwardedForHeaderValues.size() -1))
                buffer.append("|");
        }

        String ip = buffer.toString();
        if(!StringUtils.isBlank(ip))
            return ip;
        return request.headers().get(X_BSY_IP);
    }

    public static boolean isAirtelMobileIP(HttpRequest request)
    {
        String ip = request.headers().get(X_BSY_IP);
        return (UserDeviceUtils.isAirtelIPRange(ip,request));
    }
    
    public static boolean isAirtelIPRange(String ip, HttpRequest request)
    {
        try {
            boolean res = ipRange.isIpRange(ip);
            if (res) {
                return res;
            }
            return isIpRangeFromXForwardedFor(request);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isIpRangeFromXForwardedFor(HttpRequest request)
            throws Exception
    {
        List<String> forwardedForHeaderValues = request.headers().getAll("x-forwarded-for");

        if (forwardedForHeaderValues == null) {
            return false;
        }

        List ipAddressList = new ArrayList();

        for (int i = 0; i < forwardedForHeaderValues.size(); i++) {
            ipAddressList.addAll(Arrays.asList(((String) forwardedForHeaderValues.get(i)).split(",")));

        }

        return isIpRangeFromList(ipAddressList);
    }

    private static boolean isIpRangeFromList(List<String> list)
            throws Exception
    {
        if (list == null) {
            return false;
        }

        String ip = null;
        int max = list.size();

        if (max > 3) {
            max = 3;
        }

        for (int cnt = 0; max > cnt; cnt++) {
            ip = ((String)list.get(cnt)).trim();
            if (ipRange.isIpRange(ip).booleanValue()) {
                return true;
            }
        }
        return false;
    }


//    public static String generateUUID(String msisdn,HttpRequest request)
//            throws Exception
//    {
//        String uuid = null;
//
//        if(!StringUtils.isBlank(msisdn))
//            uuid = String.valueOf(hmacSha1Enc(key, msisdn, MSN_UUV0_SIZE_MAX.intValue())) + "0";
//        else
//        {
//            if(request == null)
//                return null;
//            String ip = request.getHeader("x-bsy-ip");
//            String keyword = creUuidkeywordV1(ip, request, Calendar.getInstance());
//            if (keyword != null) {
//                uuid = String.valueOf(hmacSha1Enc(key, keyword, MSN_UUV1_SIZE_MAX.intValue())) + "1";
//            }
//        }
//
//        return uuid;
//
//    }

    public static String generateUUIDWithExceptionHandling(String deviceId,HttpRequest request) {
    
        try {
            return String.valueOf(hmacSha1Enc(key, deviceId, MSN_UUV1_SIZE_MAX.intValue())) +  MusicPlatformType.WYNK_WAP.getPlatformString();
        }
        catch (Exception e) {
            
            logger.error("error generating uuid for details  device Id " + deviceId );
            return null;
        }
        
    }
    
    public static MusicPlatformType getPlatformFromPdtId(int productId) {
    	if (productId  == MusicSubscriptionPackConstants.SAMSUNG_SDK_PACK || productId == MusicSubscriptionPackConstants.SAMSUNG_SDK_FREE_PACK)
    		return MusicPlatformType.SAMSUNG_SDK;
    	return MusicPlatformType.WYNK_APP;
    }
    
    public static MusicPlatformType getUserPlatform(String msisdn, String deviceId, HttpRequest request) {
        MusicPlatformType platform = MusicPlatformType.WYNK_APP;
        if(!StringUtils.isBlank(msisdn)) {
            platform = UserDeviceUtils.getUserPlatformFromRequest(request);
        }
        else if(!StringUtils.isBlank(deviceId)) {
            platform = MusicPlatformType.WYNK_DEVICE_BASED;
        } else {
            if(request == null)
                return platform;
            String ip = request.headers().get(X_BSY_IP);
            String keyword = creUuidkeywordV1(ip, request, Calendar.getInstance());
            if (keyword != null) {
                platform = MusicPlatformType.WYNK_OLD_WAP;
            }
        }
        return platform;
    }
    
    public static String generateUUID(String msisdn,String deviceId,HttpRequest request, MusicPlatformType platform)
            throws Exception
    {
        String uuid = null;
        if(!StringUtils.isBlank(msisdn)) {
            msisdn = Utils.normalizePhoneNumber(msisdn);
            if (true) {
                uuid = createAndGetUuidFromIdentity(msisdn);
            } else {
                uuid = String.valueOf(hmacSha1Enc(key, msisdn, MSN_UUV0_SIZE_MAX.intValue())) + platform.getPlatformString();
                /**** this was old logic to create uid  ****/
            }
        }
        else if(!StringUtils.isBlank(deviceId)) {
            uuid = String.valueOf(hmacSha1Enc(key, deviceId, MSN_UUV1_SIZE_MAX.intValue())) + MusicPlatformType.WYNK_DEVICE_BASED.getPlatformString();
        } else {
            if(request == null)
                return null;
            String ip = request.headers().get(X_BSY_IP);
            String keyword = creUuidkeywordV1(ip, request, Calendar.getInstance());
            if (keyword != null) {
                uuid = String.valueOf(hmacSha1Enc(key, keyword, MSN_UUV1_SIZE_MAX.intValue())) + MusicPlatformType.WYNK_OLD_WAP.getPlatformString();
            }
        }

        return uuid;

    }

    private static String createAndGetUuidFromIdentity(String msisdn) {
        WCFService wcfServiceBean = InjectionHelper.getBean(WCFService.class);
        String requestBody = WcfHelper.prepareBodyForCreateOrGetUserId(msisdn, BACKEND_SERVICE_NAME);
        return wcfServiceBean.createOrGetUserId(requestBody);
    }

    public static void main(String[] args) throws Exception {
    	String generateUUID = generateUUID("9717100641", null, null, MusicPlatformType.WYNK_APP);
    	System.out.println(generateUUID);
	}
    
    public static String generateUUIDForChromecastUser(String uuid)
            throws Exception
    {        
        String newUuid = String.valueOf(hmacSha1Enc(key, uuid, MSN_UUV1_SIZE_MAX.intValue())) + MusicPlatformType.WYNK_CHROMECAST.getPlatformString();
        
        return newUuid;
    }


    private static String creUuidkeywordV1(String ip, HttpRequest request, Calendar calendar)
    {
        String ua = request.headers().get("User-Agent");
        String ips = "";
        List<String> forwardedForHeaderValues = request.headers().getAll("x-forwarded-for");
        if (forwardedForHeaderValues != null)
        {
            for (int i = 0; i < forwardedForHeaderValues.size(); i++) {
                String ipObj = forwardedForHeaderValues.get(i);
                ips = String.valueOf(ips) + ipObj;
            }
            ip = String.valueOf(ips) + "," + ip;
        }

        if ((ip == null) || (ua == null) || (calendar == null)) {
            return null;
        }

        int year = calendar.get(1);
        int month = calendar.get(2) + 1;
        int day = calendar.get(5);
        int hour = calendar.get(11);
        int minute = calendar.get(12);
        int second = calendar.get(13);
        return String.valueOf(ua) + ip + String.format("%1$04d%2$02d%3$02d%4$02d%5$02d%6$02d",
                new Object[] { Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day),
                        Integer.valueOf(hour), Integer.valueOf(minute), Integer.valueOf(second) });
    }

    private static String hmacSha1Enc(String key, String in, int max)
            throws Exception
    {
        if (max > 0) {
            String Base64Data = base64(hmacsha1(key, in));
            String Base64urlencodeData = Base64Data.replace("+", "-").replace("/", "_").replace("=", "");

            if (Base64urlencodeData.length() > max) {
                return Base64urlencodeData.substring(0, max);
            }
            return Base64urlencodeData;
        }

        return "";
    }

    private static byte[] hmacsha1(String key, String in)
            throws Exception
    {
        try
        {
            SecretKeySpec sk = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(sk);
            return mac.doFinal(in.getBytes());
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    private static String base64(byte[] hmacsha1_data)
            throws Exception
    {
        String Base64EncodeData = new String(Base64.encodeBase64(hmacsha1_data));

        return Base64EncodeData;
    }

    public static AccessMsnInf getUserOperatorInfo(HttpRequest request)
            throws Exception
    {
        HttpServletRequest servletRequest = createHttpServletRequest(request);
        AccessMsn accessMsn = new AccessMsn();
        AccessMsnInf accessMsnInf = accessMsn.getMsnInfInstance(request.headers().get(X_BSY_IP),
                servletRequest, Calendar.getInstance());


        StringBuffer debugLog = new StringBuffer();
        debugLog.append("[circleName : ").append(accessMsnInf.getCircleName());
        debugLog.append(",UUID : ").append(accessMsnInf.getMsnKey());
        debugLog.append(",MSISDN : ").append(accessMsnInf.getMsisdn());
        debugLog.append(",Operator : ").append(accessMsnInf.getOperatorName());
        debugLog.append(",RAT : ").append(accessMsnInf.getRatType());
        debugLog.append("]");

        //logger.info("UserDeviceUtils : "+debugLog.toString());

        return accessMsnInf;
    }

    public static final int RATTYPE_UNKNOWN = 0;    // unknown
    public static final int RATTYPE_UTRAN = 1;        // 3G
    public static final int RATTYPE_GERAN = 2;        // 2G

    public static boolean isWifi(HttpRequest request)
    {
        //in case of android app, we get following two headers:
        //(a) x-bsy-net (mobile or wifi)
        //(b) x-bsy-snet  (type of network see MobileNetwork.java)
        //
        HttpHeaders headers = request.headers();
        String networkType = null;
        Map<String,String> networkHeaderMap = parseNetworkHeader(headers.get(MusicConstants.MUSIC_HEADER_NET));

        if(networkHeaderMap != null && networkHeaderMap.size() > 0)
            networkType = networkHeaderMap.get(MusicConstants.NETWORK_TYPE);

        int netType = MobileNetwork.CONNECTION_TYPE_MOBILE;
        if(!StringUtils.isEmpty(networkType))
        {
            try {
                netType = Integer.parseInt(networkType);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if(netType == MobileNetwork.CONNECTION_TYPE_WIFI)
                return true;
        }
        return false;
    }
    
    /*
       Android 
       Build Number < 37 - 0-poor , 1-moderate, 2-good, 3-excellent
       Build Number =< 44 - 0-awful, 1-poor , 2-moderate, 3-good, 4-excellent
       Build Number =< 46 - 0-awful, 1-indian_poor , 2-poor, 3-moderate, 4-good, 5-excellent
       
       iOS
       
    */
    
    public static String getNetworkType(HttpRequest request) {
        if (isWifi(request)) {
            return "wifi";
        } else if (is2G(request,null)) {
            return "2G";
        } else {
            return "3G";
        }
    }

    public static boolean is2G(HttpRequest request, AccessMsnInf userOpInfo)
    {
        int ratTp =  RATTYPE_UNKNOWN;
        try {
            if(userOpInfo != null)
                ratTp = userOpInfo.getRatType();
        }
        catch (Exception e) {
            logger.warn("Error parsing RAT type : "+e.getMessage(),e);
        }


        String ratTypeStr = String.valueOf(ratTp);
        int ratType = RATTYPE_UNKNOWN;

        if(ratTypeStr != null && !StringUtils.isBlank(ratTypeStr))
        {
            try {
                ratType = Integer.parseInt(ratTypeStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }


        //in case of android app, we get following two headers:
        //(a) x-bsy-net (mobile or wifi)
        //(b) x-bsy-snet  (type of network see MobileNetwork.java)
        //
        String networkTypeStr = null;
        String networkQuality = null;
        HttpHeaders headers = request.headers();
        networkQuality = headers.get("x-bsy-snet");
        Map<String,String> netHeaderMap = parseNetworkHeader(headers.get(MusicConstants.MUSIC_HEADER_NET));

        if(netHeaderMap != null && netHeaderMap.size() > 0) {
            networkTypeStr = netHeaderMap.get(MusicConstants.NETWORK_TYPE);

            if(networkQuality == null && netHeaderMap.size() > 2)
                networkQuality = netHeaderMap.get(MusicConstants.SUB_NETWORK_TYPE);
        }

        if((ratTp == RATTYPE_UNKNOWN) && !StringUtils.isEmpty(networkTypeStr))
        {
            try {
                int networkType = Integer.parseInt(networkTypeStr);
                if(networkType == MobileNetwork.CONNECTION_TYPE_WIFI)
                    return false;

                if(!StringUtils.isEmpty(networkQuality))
                    return  !MobileNetwork.isConnectionFast(networkType, Integer.parseInt(networkQuality));
            }
            catch (Exception e) {
                logger.warn("Error parsing App Network type : "+networkQuality+","+networkQuality,e);
            }
        }

        return ratType == RATTYPE_GERAN;
    }

    public static String getServiceForPurchaseSong(Boolean isWapRequest, Boolean isSamsung, WCFServiceType wcfServiceType){
        if(isWapRequest && isSamsung){
            return "samsungWap";
        }
        else if(isWapRequest){
            return "musicWap";
        }
        else {
            return wcfServiceType.getServiceName();
        }

    }

    public static final Map<String,String> parseNetworkHeader(String net) {

        if(StringUtils.isBlank(net))
        {
            HttpRequest request = ChannelContext.getRequest();
            if(request == null)
                return null;
            net = request.headers().get(MusicConstants.MUSIC_HEADER_NET);
        }

        Map<String,String> result=new LinkedHashMap<>();

        if(net != null) {
            String res[] = net.split("\\s*/\\s*");
            if (res.length == 3) {
                result.put(MusicConstants.NETWORK_TYPE , res[0]);               //networkType
                result.put(MusicConstants.SUB_NETWORK_TYPE , res[1]);          //subNetworkType
                result.put(MusicConstants.NETWORK_QUALITY , res[2]);                //networkQuality
            }
            else if(res.length == 1) {
                result.put(MusicConstants.NETWORK_TYPE, res[0]);    //Old builds only networkType
            }
        }
        return result;
    }

    private static HttpServletRequest createHttpServletRequest(final HttpRequest request)
    {
        HttpServletRequest httpServletRequest = new HttpServletRequest() {
            @Override
            public String getAuthType() {
                return null;
            }

            @Override
            public Cookie[] getCookies() {
                return new Cookie[0];
            }

            @Override
            public long getDateHeader(String s) {
                return 0;
            }

            @Override
            public String getHeader(String s) {
                return request.headers().get(s);
            }

            @Override
            public Enumeration<String> getHeaders(String s) {
                return Collections.enumeration(request.headers().getAll(s));
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                return Collections.enumeration(request.headers().names());
            }

            @Override
            public int getIntHeader(String s) {
                return Integer.parseInt(getHeader(s));
            }

            @Override
            public String getMethod() {
                return request.getMethod().name();
            }

            @Override
            public String getPathInfo() {
                return request.getUri();
            }

            @Override
            public String getPathTranslated() {
                return request.getUri();
            }

            @Override
            public String getContextPath() {
                return request.getUri();
            }

            @Override
            public String getQueryString() {
                return null;
            }

            @Override
            public String getRemoteUser() {
                return null;
            }

            @Override
            public boolean isUserInRole(String s) {
                return false;
            }

            @Override
            public Principal getUserPrincipal() {
                return null;
            }

            @Override
            public String getRequestedSessionId() {
                return null;
            }

            @Override
            public String getRequestURI() {
                return request.getUri();
            }

            @Override
            public StringBuffer getRequestURL() {
                return null;
            }

            @Override
            public String getServletPath() {
                return null;
            }

            @Override
            public HttpSession getSession(boolean b) {
                return null;
            }

            @Override
            public HttpSession getSession() {
                return null;
            }

            @Override
            public boolean isRequestedSessionIdValid() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromCookie() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromURL() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromUrl() {
                return false;
            }

            @Override
            public boolean authenticate(HttpServletResponse httpServletResponse)
                    throws IOException, ServletException {
                return false;
            }

            @Override
            public void login(String s, String s2)
                    throws ServletException {

            }

            @Override
            public void logout()
                    throws ServletException {

            }

            @Override
            public Collection<Part> getParts()
                    throws IOException, ServletException {
                return null;
            }

            @Override
            public Part getPart(String s)
                    throws IOException, ServletException {
                return null;
            }

            @Override
            public Object getAttribute(String s) {
                return null;
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return null;
            }

            @Override
            public String getCharacterEncoding() {
                return null;
            }

            @Override
            public void setCharacterEncoding(String s)
                    throws UnsupportedEncodingException {

            }

            @Override
            public int getContentLength() {
                return 0;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public ServletInputStream getInputStream()
                    throws IOException {
                return null;
            }

            @Override
            public String getParameter(String s) {
                return null;
            }

            @Override
            public Enumeration<String> getParameterNames() {
                return null;
            }

            @Override
            public String[] getParameterValues(String s) {
                return new String[0];
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                return null;
            }

            @Override
            public String getProtocol() {
                return null;
            }

            @Override
            public String getScheme() {
                return null;
            }

            @Override
            public String getServerName() {
                return null;
            }

            @Override
            public int getServerPort() {
                return 0;
            }

            @Override
            public BufferedReader getReader()
                    throws IOException {
                return null;
            }

            @Override
            public String getRemoteAddr() {
                return request.headers().get(X_BSY_IP);
            }

            @Override
            public String getRemoteHost() {
                return "";//request.getHeader("x-bsy-host");
            }

            @Override
            public void setAttribute(String s, Object o) {

            }

            @Override
            public void removeAttribute(String s) {

            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public Enumeration<Locale> getLocales() {
                return null;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(String s) {
                return null;
            }

            @Override
            public String getRealPath(String s) {
                return null;
            }

            @Override
            public int getRemotePort() {
                return 0;//Integer.parseInt(request.getHeader("x-bsy-port"));
            }

            @Override
            public String getLocalName() {
                return null;
            }

            @Override
            public String getLocalAddr() {
                return null;
            }

            @Override
            public int getLocalPort() {
                return 0;
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public AsyncContext startAsync()
                    throws IllegalStateException {
                return null;
            }

            @Override
            public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
                    throws IllegalStateException {
                return null;
            }

            @Override
            public boolean isAsyncStarted() {
                return false;
            }

            @Override
            public boolean isAsyncSupported() {
                return false;
            }

            @Override
            public AsyncContext getAsyncContext() {
                return null;
            }

            @Override
            public DispatcherType getDispatcherType() {
                return null;
            }
        };
        return httpServletRequest;
    }

    public static Boolean isWAPUser(String uid) {
        Boolean isWAPUser = Boolean.FALSE;

        if(uid != null && uid.endsWith("4")){
            isWAPUser = Boolean.TRUE;
        }
        return isWAPUser;
    }

    public static Boolean isSamsungGTMDevice(String medium) {
        Boolean isSamsungGTMDevice = Boolean.FALSE;
        if (StringUtils.isNotBlank(medium) && medium.equalsIgnoreCase(MusicConstants.SAMSUNG_GTM)) {
            isSamsungGTMDevice = Boolean.TRUE;
        }

        return isSamsungGTMDevice;
    }

    public static boolean isRequestFromWAP() {
        try {
            if (ChannelContext.getRequest() != null && ChannelContext.getRequest().headers().get("x-bsy-iswap") != null) {
                if (true == Boolean.parseBoolean(ChannelContext.getRequest().headers().get("x-bsy-iswap"))) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.info("Exception occured in getting wap user based on the header");
        }

        return false;
    }

    public static WCFRegistrationChannel getRegistrationChannelBody(WCFChannelEnum wcfChannelEnum,Boolean isVerifiedBefore){
        WCFRegistrationChannel wcfRegistrationChannel = null;
        if(wcfChannelEnum != null){
            wcfRegistrationChannel = new WCFRegistrationChannel();
            wcfRegistrationChannel.setChannelName(wcfChannelEnum.getChannel());
            Boolean isVerifiedUser = wcfChannelEnum.getVerifiedUser();
            if(isVerifiedBefore != null){
                isVerifiedUser = isVerifiedUser || isVerifiedBefore;
            }
            wcfRegistrationChannel.setVerifiedUser(isVerifiedUser);
        }

        return wcfRegistrationChannel;
    }

    public static List<UserDevice> getUpdatedUserDeviceList(String deviceId,User user){
        Boolean isUpdatedRequired = Boolean.FALSE;
        List<UserDevice> currentUserDeviceList = new ArrayList<>();
        if(StringUtils.isBlank(deviceId)){
            return null;
        }

        List<UserDevice> userDeviceList = user.getDevices();
        if(CollectionUtils.isEmpty(userDeviceList)){
            return null;
        }

        for(UserDevice userDevice : userDeviceList){
            if(deviceId.equalsIgnoreCase(userDevice.getDeviceId())){
                WCFRegistrationChannel wcfRegistrationChannel = getRegistrationChannelBody(WCFChannelEnum.OTP,Boolean.TRUE);
                userDevice.setRegistrationChannel(wcfRegistrationChannel);
                isUpdatedRequired = Boolean.TRUE;
            }
            currentUserDeviceList.add(userDevice);
        }
        if(isUpdatedRequired) {
            return currentUserDeviceList;
        }
        return null;
    }

}
