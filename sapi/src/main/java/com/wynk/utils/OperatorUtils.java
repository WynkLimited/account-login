package com.wynk.utils;

import com.wynk.server.ChannelContext;
import com.wynk.user.dto.User;
import org.apache.commons.lang3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wynk.service.AccountService.AIRTEL;

/**
 * Created by a1vlqlyy on 07/07/17.
 */
public class OperatorUtils {

    private static final Logger logger         = LoggerFactory.getLogger(OperatorUtils.class.getCanonicalName());

    public static boolean isAirtelDevice() {

        // TODO - We should use current device instead of active device?
        if (ChannelContext.getUser() != null && ChannelContext.getUser().getActiveDevice() != null) {

            String operator = ChannelContext.getUser().getActiveDevice().getOperator();
            if (StringUtils.isNotBlank(operator) && operator.toLowerCase().contains(AIRTEL))
                return true;
        }
        return false;
    }

    public static boolean isAirtelCurrentDevice() {
        User user = ChannelContext.getUser();
        if (user != null && user.getDevices() != null) {

            if (StringUtils.isNotBlank(user.getMsisdn())) { //if user is registered
                String userOp = user.getOperator();
                if (org.apache.commons.lang3.StringUtils.isNotBlank(userOp) && userOp.toLowerCase().contains(AIRTEL))
                    return true;
            }

            String did = ChannelContext.getDeviceId();
            if (ChannelContext.getUserCurrentDevice(did) == null)
                return false;

            String operator = ChannelContext.getUserCurrentDevice(did).getOperator();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(operator) && operator.toLowerCase().contains(AIRTEL))
                return true;
        }
        return false;
    }

    public static boolean isAirtelUser() {
        if (ChannelContext.getUser() != null) {
            String operator = ChannelContext.getUser().getOperator();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(operator) && operator.toLowerCase().contains(AIRTEL))
                return true;
        }
        return false;
    }

    public static boolean isAirtelMobileIp() {
        boolean isAirtelMobileIP = IPRangeUtil.isAirtelMobileIP(ChannelContext.getRequest());
        if(UserDeviceUtils.isWifi(ChannelContext.getRequest())) {
            isAirtelMobileIP = false;
        }
        return isAirtelMobileIP;
    }

    public static boolean isOperatorUpdateRequired(String newOperator, String currentOp) {
        if (StringUtils.isBlank(newOperator) && StringUtils.isBlank(currentOp)){
            return false;
        }
        if (StringUtils.isBlank(newOperator) || StringUtils.isBlank(currentOp)){
            return true;
        }
        return !newOperator.toLowerCase().equals(currentOp.toLowerCase());
    }
}
