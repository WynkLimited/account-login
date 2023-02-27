package com.wynk.utils;

import com.bsb.portal.core.common.LoadProperty;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.net.util.SubnetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class IPRangeUtil {
	
    private static LoadProperty property = null;
    private static List<SubnetUtils> whitelistedSubnets = null;
    private static List<String> whitelistedIps = null;
    
    private static final Logger logger               = LoggerFactory.getLogger(IPRangeUtil.class.getCanonicalName());

    private static final Pattern IP_PATTERN = Pattern
            .compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    static
    {
        try {
            property = new LoadProperty();
            whitelistedSubnets = new ArrayList<>();
            whitelistedIps = new ArrayList<>();
            for (String subnet : property.getWhitelist()) {
            	if (subnet.indexOf("/") == -1)
            		whitelistedIps.add(subnet);
            	else
            		whitelistedSubnets.add(new SubnetUtils(subnet));
            }
        }
        catch (Exception e) {
            logger.error("Error while initializing subnets and IPds" , e);
        }
    }
    
    public static boolean validateIP(final String ip) {
        return IP_PATTERN.matcher(ip).matches();
    }
    
	public static Boolean isInRange(String ip) {
		
		if (!validateIP(ip))
			return false;
		
		boolean status = false;
		
		if (whitelistedIps.contains(ip))
			return true;
		
		for (SubnetUtils whitelistedSubnet : whitelistedSubnets) {
			if (whitelistedSubnet.getInfo().isInRange(ip))
				status = true;
		}
		return status;
	}
	
	public static boolean isAirtelIPRange(String ip, HttpRequest request)
    {
		
		if (!validateIP(ip))
			return false;
		
        try {
            boolean res = IPRangeUtil.isInRange(ip);
            if (res) {
                return res;
            }
            return isIpRangeFromXForwardedFor(request);
        }
        catch (Exception e) {
            logger.error("Error in isAirtelIPRange",e);
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
            if (IPRangeUtil.isInRange(ip).booleanValue()) {
                return true;
            }
        }
        return false;
    }
	
	public static void main(String[] args) {
		System.out.println(isInRange("223.234.0.0"));
		System.out.println(isInRange("223.234.1.0"));
		System.out.println(isInRange("223.232.0.0"));
		System.out.println(isInRange("221.234.0.0"));
		System.out.println(isInRange("125.18.235.213"));
		
		System.out.println(isInRange("unknown"));
		System.out.println(validateIP("unknown"));
		System.out.println(validateIP("2402:8100:2005:acf5:d4ef:2354:bf43:331b"));
		System.out.println(isInRange("2402:8100:2005:acf5:d4ef:2354:bf43:331b"));


	}

	public static boolean isAirtelMobileIP(HttpRequest request)
	{
	    String ip = request.headers().get(UserDeviceUtils.X_BSY_IP);
	    return (isAirtelIPRange(ip,request));
	}
	
}