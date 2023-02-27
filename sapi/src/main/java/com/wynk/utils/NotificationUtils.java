package com.wynk.utils;

import com.wynk.common.Circle;
import com.wynk.common.DeviceOSType;
import com.wynk.common.ScreenCode;
import com.wynk.constants.MusicBuildConstants;
import com.wynk.dto.ThirdPartyNotifyDTO;
import com.wynk.music.constants.MusicContentLanguage;
import com.wynk.music.dto.MusicPlatformType;
import com.wynk.notification.MusicAdminNotification;
import com.wynk.user.dto.User;
import com.wynk.user.dto.UserDevice;
import com.wynk.wcf.WCFApisService;
import com.wynk.wcf.WCFApisUtils;
import com.wynk.wcf.dto.Feature;
import com.wynk.wcf.dto.Offer;
import com.wynk.wcf.dto.ProvisionType;
import com.wynk.wcf.dto.UserSubscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

import static com.wynk.constants.MusicConstants.MOENGAGE;

/**
 * Created with IntelliJ IDEA. User: bhuvangupta Date: 13/01/13 Time: 1:00 PM To change this
 * template use File | Settings | File Templates.
 */
@Component
public class NotificationUtils {

    @Autowired
    private WCFApisService wcfApisService;

    @Autowired
    private WCFApisUtils wcfApisUtils;

    public static DeviceOSType getDeviceType(UserDevice userDevice) {

        if(StringUtils.isBlank(userDevice.getDeviceKey()))
            return null;

        if(!StringUtils.isBlank(userDevice.getOs()) && userDevice.getOs().toLowerCase().contains("android")) {
            
            return DeviceOSType.ANDROID;
        } else if(!StringUtils.isBlank(userDevice.getOs()) && userDevice.getOs().toLowerCase().contains("ios")) {
            
            return DeviceOSType.IOS;
        }
        else if(MusicDeviceUtils.isWindowsDeviceFromOS(userDevice.getOs())) {
            return DeviceOSType.WINDOWS;
        }
        
        return null;
    }

    public static DeviceOSType isNotificationValidForDeviceAndGetDeviceType(MusicAdminNotification notification, UserDevice userDevice) {
        
        if(StringUtils.isBlank(userDevice.getDeviceKey()))
            return null;

        boolean isAndroid = false;
        boolean isWindows = false;
        if(!StringUtils.isBlank(userDevice.getOs()) && userDevice.getOs().toLowerCase().contains("android"))
            isAndroid = true;
        else if(!StringUtils.isBlank(userDevice.getDeviceSnsARN()) && userDevice.getDeviceSnsARN().toLowerCase().contains("gcm"))
            isAndroid = true;
        else if(!StringUtils.isBlank(userDevice.getOs()) && userDevice.getOs().toLowerCase().contains("windows"))
            isWindows = true;

        // todo : RADIO_PLAYER is currently supported only on android.
        if(notification.getTargetScreen() != null && (notification.getTargetScreen().getOpcode() == ScreenCode.RADIO_PLAYER.getOpcode())) {
            if(!isAndroid) {
                if(userDevice.getBuildNumber() < MusicBuildConstants.IOS_SUPPORT_FOR_RADIO_NOTIFICATIONS)
                    return null;
            }
        }

        //OnDevice build check for android.
        if(notification.getTargetScreen() != null && (notification.getTargetScreen().getOpcode() == ScreenCode.ON_DEVICE.getOpcode())) {
            if(isAndroid) {
                if(userDevice.getBuildNumber() < MusicBuildConstants.ANDROID_ONDEVICE_BUILD_NUMBER)
                    return null;
            }
            else
                return null;
        }

        if(isAndroid && MusicBuildConstants.oemBuildList.contains(userDevice.getBuildNumber()) && notification.getTargetContentId() != null && notification.getTargetContentId().contains("hungama"))
            return  null;
        if(notification.getTargetScreen() != null && (notification.getTargetScreen().getOpcode() == ScreenCode.EXTERNAL_WEBVIEW.getOpcode())) {
            if(isAndroid) {
                if(userDevice.getBuildNumber() <= MusicBuildConstants.ANDROID_SUPPORT_FOR_EXTERNAL_WEBVIEW)
                    return null;
            }
            else {
                if(userDevice.getBuildNumber() <= MusicBuildConstants.IOS_SUPPORT_FOR_EXTERNAL_WEBVIEW)
                    return null;
            }
        }

        if(notification.getTargetScreen() != null && (notification.getTargetScreen().getOpcode() == ScreenCode.PACKAGE_GRID.getOpcode() || notification.getTargetScreen().getOpcode() == ScreenCode.PACKAGE_LIST.getOpcode())) {
            if(isAndroid) {
                if(userDevice.getBuildNumber() < MusicBuildConstants.ANDROID_SUPPORT_FOR_PACKAGE_DEEP_LINKING)
                    return null;
            }
            else {
                if(userDevice.getBuildNumber() < MusicBuildConstants.IOS_SUPPORT_FOR_PACKAGE_DEEP_LINKING)
                    return null;
            }
        }

        boolean sendToAndroid = MusicAdminNotification.androidDeviceTypes.contains(notification.getDeviceType());
        boolean sendToiOS = MusicAdminNotification.iOSDeviceTypes.contains(notification.getDeviceType());

        // TODO - MOVE THESE strings to an ENUM !!!
        if(isAndroid && sendToAndroid)
            return DeviceOSType.ANDROID;
        else if(!isAndroid && sendToiOS && !isWindows)
            return DeviceOSType.IOS;
        else
            return null;

    }

    public static Boolean isNotificationValidForUser(MusicAdminNotification musicAdminNotificationToSend, User user) {
        return isNotificationValidForUser(musicAdminNotificationToSend, user, null);
    }

    public static Boolean isNotificationValidForUser(MusicAdminNotification musicAdminNotificationToSend, User user,MusicContentLanguage preferredLang) {
        // if notification is meant for certain regional (non eng or hindi) language
        // then send only to users which have that language set.
        boolean isValidCircle = isNotficationValidForCircle(musicAdminNotificationToSend, user.getCircle());
        if(!isValidCircle)
            return false;

        if (user.getPlatform() != null && MusicPlatformType.isThirdPartyPlatformId(user.getPlatform()))
            return false;

        List<String> contentLangs = user.getSelectedLanguages();
        String userLang = null;
        if(!CollectionUtils.isEmpty(contentLangs))
            userLang = contentLangs.get(0);

        List<MusicContentLanguage> targetContentLang = musicAdminNotificationToSend.getTargetContentLanguages();

        if(preferredLang != null) {
            if(!CollectionUtils.isEmpty(targetContentLang) && targetContentLang.contains(preferredLang)) {
                return true;
            }
        }
        else if (targetContentLang != null) {
            List<MusicContentLanguage> exclusionList = musicAdminNotificationToSend.getExclusionLangList();
            if (!CollectionUtils.isEmpty(exclusionList)) {
                for (MusicContentLanguage eLang : exclusionList) {
                    String exclusionLang = eLang.getId();
                    if ((!CollectionUtils.isEmpty(contentLangs)) && contentLangs.contains(exclusionLang))
                        return false;
                }
            }

            if (!StringUtils.isEmpty(userLang)) {
                if (targetContentLang.contains(MusicContentLanguage.getContentLanguageById(userLang)))
                    return true;

            } else {
                if(targetContentLang.contains(MusicContentLanguage.HINDI) || targetContentLang.contains(MusicContentLanguage.ENGLISH))
                    return true;
            }
        }

        return false;
    }

    public static boolean isNotficationValidForCircle(MusicAdminNotification musicAdminNotificationToSend, String circleShortName) {
        Set<Circle> targetCircleNames = musicAdminNotificationToSend.getTargetCircles();

        if(CollectionUtils.isEmpty(targetCircleNames))
            return true;

        if (targetCircleNames.contains(Circle.ALL))
            return true;

        Circle circle = Circle.getCircleById(circleShortName);

        if (circle == null)
            return false;

        if(circle.getCircleId().equalsIgnoreCase(Circle.ALL.getCircleId()))
            return true;

        return targetCircleNames.contains(circle);

    }
    
    /**
     * Checks for time notification timeslot
     * @param notification
     * @return
     */
    public static boolean isNotificationToBeSent(MusicAdminNotification notification) {
    	long currentTimeStamp = System.currentTimeMillis();
  	  	Date current = new Date(currentTimeStamp);
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(current);
        int currentHour = currentCal.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentCal.get(Calendar.MINUTE);

    	if (NotificationUtils.isSameDay(System.currentTimeMillis(), notification.getSentTime()) &&
    			( (notification.getSentTimeSlot().getStartHour() < currentHour) || 
    			  (notification.getSentTimeSlot().getStartHour() == currentHour && notification.getSentTimeSlot().getStartMinute() <= currentMinute))) {
    		return true;
    	}
    	return false;
    }
    
    private static boolean isSameDay(long timestamp1, long timestamp2) {
    	  Date d1 = new Date(timestamp1);
          Date d2 = new Date(timestamp2);
          		
          Calendar cal1 = Calendar.getInstance();
          Calendar cal2 = Calendar.getInstance();
          cal1.setTime(d1);
          cal2.setTime(d2);
          boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
          return sameDay;

    }

    public static List<String> readUidsFromS3File(String url) {
        List<String> uids = new ArrayList<>();
        try {
            URL fileUrl = new URL(url);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileUrl.openStream()));

            String line = null;
            while((line = br.readLine()) != null) {
                uids.add(line);
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return uids;
    }

    public static List<Integer> parseUnrInactivityDays(String value) {

        List<Integer> daysList = new ArrayList<Integer>();
        try {
            String result[] = value.split("\\|");
            for (int i = 0; i < result.length; i++) {
                String days[] = result[i].split("-");
                if (days.length > 1) {
                    int start = Integer.parseInt(days[0].trim());
                    int end = Integer.parseInt(days[1].trim());
                    for ( int j = start; j <= end; j++)
                        daysList.add(j);

                } else if(days.length == 1)
                    daysList.add(Integer.parseInt(days[0].trim()));

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return daysList;
    }

    public static MusicAdminNotification parseUnrInactivityNotification(String value) {
        MusicAdminNotification musicAdminNotification = new MusicAdminNotification();
        String result[] = value.split("\\|");
        if(result.length > 5) {
            //TODO - populate notification object
            musicAdminNotification.setNotificationId("srch_bsb_" + System.currentTimeMillis());
            musicAdminNotification.setTitle(result[0].trim());
            musicAdminNotification.setText(result[1].trim());
            musicAdminNotification.setNonRichText(result[2].trim());
            if(!StringUtils.isEmpty(result[3].trim()))
                musicAdminNotification.setImgUrl(result[3].trim());

            musicAdminNotification.setTargetScreen(ScreenCode.getScreenCodeByName(result[4].trim()));
            musicAdminNotification.setTargetContentId(result[5].trim());

        }
        return musicAdminNotification;
    }

    public static ThirdPartyNotifyDTO getUpdateTPNotifyObject(String email, String msisdn, String uid, Long expireTimestamp,Boolean isIOSDevice){
        ThirdPartyNotifyDTO thirdPartyNotifyDTO = new ThirdPartyNotifyDTO();
        thirdPartyNotifyDTO.setService(MOENGAGE);
        thirdPartyNotifyDTO.setUid(uid);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(email)){
            thirdPartyNotifyDTO.setEmail(email);
        }
        if(org.apache.commons.lang3.StringUtils.isNotBlank(msisdn)){
            thirdPartyNotifyDTO.setMsisdn(msisdn);
        }
        if(expireTimestamp != null){
            thirdPartyNotifyDTO.setExpireTimestamp(expireTimestamp);
        }
        if(isIOSDevice != null){
            thirdPartyNotifyDTO.setIOSDevice(isIOSDevice);
        }

        return thirdPartyNotifyDTO;
    }
    public ThirdPartyNotifyDTO getUpdateTPNotifyObjectFromUser(String uid, User user, UserSubscription wcfSubscription){
        ThirdPartyNotifyDTO thirdPartyNotifyDTO = new ThirdPartyNotifyDTO();
        Feature userFeature = wcfApisUtils.getUserFeature(wcfSubscription);
        thirdPartyNotifyDTO.setService(MOENGAGE);
        thirdPartyNotifyDTO.setUid(uid);
        if(user!=null && org.apache.commons.lang3.StringUtils.isNotBlank(user.getEmail())){
            thirdPartyNotifyDTO.setEmail(user.getEmail());
        }
        if(user!=null && org.apache.commons.lang3.StringUtils.isNotBlank(user.getMsisdn())){
            thirdPartyNotifyDTO.setMsisdn(user.getMsisdn());
        }
        if(wcfSubscription!=null && userFeature != null){
            thirdPartyNotifyDTO.setExpireTimestamp(userFeature.getValidTill());
        }
        thirdPartyNotifyDTO.setIOSDevice(MusicDeviceUtils.isIOSDevice());
        if (wcfSubscription != null) {
            Offer wcfSubscriptionPack = wcfApisService.getOffer(userFeature.getOfferId());
            if (wcfSubscriptionPack != null) {
                if (wcfSubscriptionPack.getProvisionType() != null) {
                    thirdPartyNotifyDTO.setPaid(wcfSubscriptionPack.getProvisionType() == ProvisionType.PAID);
                }
            }
        }
        return thirdPartyNotifyDTO;
    }

}
