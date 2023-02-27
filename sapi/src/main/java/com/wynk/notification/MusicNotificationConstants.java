package com.wynk.notification;

import com.wynk.common.ScreenCode;
import com.wynk.constants.MusicConstants;
import org.json.simple.JSONObject;

public class MusicNotificationConstants {
	public static JSONObject TAMIL_NOTIFICATION;
	public static JSONObject TELUGU_NOTIFICATION;
	
	public static JSONObject REGISTER_NOTIFICATION;
	public static JSONObject REGISTER_NOTIFICATION_AIRTEL;
	public static JSONObject REGISTER_NOTIFICATION_HT;
	public static JSONObject REGISTER_NOTIFICATION_AIRTEL_HT;
    
	public static JSONObject AIRTEL_FREE_SUBSCRIPTION_NOTIFICATION;
	
	public static JSONObject FREE_DATA_OFFER_NOTIFICATION;
	public static JSONObject FREE_DATA_OFFER_NOTIFICATION_WITH_SUBSCRIPTION;
	
	public static JSONObject NON_AIRTEL_NEVER_SUBSCRIBED_NOTIFICATION;
	public static JSONObject VODAFONE_SUBSCRIPTION_NOTIFICATION;

	public static JSONObject INTL_ROAMING_EXPIRY_NOTIFICATION;
    public static JSONObject INTL_ROAMING_EXPIRED_NOTIFICATION;
    public static JSONObject INTL_ROAMING_ACTIVATED_NOTIFICATION;
    public static JSONObject INTL_ROAMING_INELEIGIBLE_NOTIFICATION;

    public static JSONObject REGISTER_DOWNLOAD_NOTIFICATION_AIRTEL;
    public static JSONObject REGISTER_DOWNLOAD_NOTIFICATION_NON_AIRTEL;


    static{
    	MusicAdminNotification tamilNotification = new MusicAdminNotification();
        tamilNotification.setNotificationId(MusicConstants.WYNK_TAMIL_USER_POPUP);
        tamilNotification.setTargetScreen(ScreenCode.HOME);
        tamilNotification.setTitle("Enjoy Tamil!!");
        tamilNotification.setText("We have customized your Wynk experience for Tamil. You can change the language settings from top right corner of the app.");
        tamilNotification.setNonRichText("We have customized your Wynk experience for Tamil. You can change the language settings from top right corner of the app.");
        tamilNotification.setProcessed(false);
        tamilNotification.setDeleted(false);
        TAMIL_NOTIFICATION = tamilNotification.getRichAndroidMessageJsonObject();
        TAMIL_NOTIFICATION.put("aok", "OK");
        
        MusicAdminNotification telNotification = new MusicAdminNotification();
        telNotification.setNotificationId(MusicConstants.WYNK_TELUGU_USER_POPUP);
        telNotification.setTargetScreen(ScreenCode.HOME);
        telNotification.setTitle("Enjoy Telugu!!");
        telNotification.setText("We have customized your Wynk experience for Telugu. You can change the language settings from top right corner of the app.");
        telNotification.setNonRichText("We have customized your Wynk experience for Telugu. You can change the language settings from top right corner of the app.");
        telNotification.setProcessed(false);
        telNotification.setDeleted(false);
        TELUGU_NOTIFICATION = telNotification.getRichAndroidMessageJsonObject();
        TELUGU_NOTIFICATION.put("aok", "OK");
        
        
        MusicAdminNotification registerNotification = new MusicAdminNotification();
        registerNotification.setNotificationId(MusicConstants.WYNK_REGISTER);
        registerNotification.setTargetScreen(ScreenCode.REGISTER);
        registerNotification.setActionOpen(ActionOpen.ALERT);
        registerNotification.setTitle("Free Wynk Subscription");
        registerNotification.setText("Enjoy unlimited song downloads. Register now and get free 1 month Wynk subscription");
        registerNotification.setNonRichText("Enjoy unlimited song downloads. Register now and get free 1 month Wynk subscription");
        registerNotification.setProcessed(false);
        registerNotification.setDeleted(false);
        REGISTER_NOTIFICATION = registerNotification.getRichAndroidMessageJsonObject();
        REGISTER_NOTIFICATION.put("aok", "Avail Now");

        MusicAdminNotification registerNotificationHt = new MusicAdminNotification();
        registerNotificationHt.setNotificationId(MusicConstants.WYNK_REGISTER);
        registerNotificationHt.setTargetScreen(ScreenCode.REGISTER);
        registerNotificationHt.setActionOpen(ActionOpen.ALERT);
        registerNotificationHt.setTitle("Log in to get Wynk Subscription");
        registerNotificationHt.setText("Get unlimited downloads, ad-free music, free Hellotunes and more.");
        registerNotificationHt.setNonRichText("Get unlimited downloads, ad-free music, free Hellotunes and more.");
        registerNotificationHt.setProcessed(false);
        registerNotificationHt.setDeleted(false);
        REGISTER_NOTIFICATION_HT = registerNotificationHt.getRichAndroidMessageJsonObject();
        REGISTER_NOTIFICATION_HT.put("aok", "Avail Now");


        MusicAdminNotification registerNotificationAirtel = new MusicAdminNotification();
        registerNotificationAirtel.setNotificationId(MusicConstants.WYNK_REGISTER);
        registerNotificationAirtel.setTargetScreen(ScreenCode.REGISTER);
        registerNotificationAirtel.setActionOpen(ActionOpen.ALERT);
        registerNotificationAirtel.setTitle("Free Wynk Subscription");
        registerNotificationAirtel.setText("Free unlimited song downloads only for Airtel users. Register now to activate.");
        registerNotificationAirtel.setNonRichText("Free unlimited song downloads only for Airtel users. Register now to activate.");
        registerNotificationAirtel.setProcessed(false);
        registerNotificationAirtel.setDeleted(false);
        REGISTER_NOTIFICATION_AIRTEL = registerNotificationAirtel.getRichAndroidMessageJsonObject();
        REGISTER_NOTIFICATION_AIRTEL.put("aok", "Avail Now");


        MusicAdminNotification registerNotificationAirtelHt = new MusicAdminNotification();
        registerNotificationAirtelHt.setNotificationId(MusicConstants.WYNK_REGISTER);
        registerNotificationAirtelHt.setTargetScreen(ScreenCode.REGISTER);
        registerNotificationAirtelHt.setActionOpen(ActionOpen.ALERT);
        registerNotificationAirtelHt.setTitle("Log in to get Wynk Subscription");
        registerNotificationAirtelHt.setText("Get unlimited downloads, ad-free music, free Hellotunes and more.");
        registerNotificationAirtelHt.setNonRichText("Get unlimited downloads, ad-free music, free Hellotunes and more.");
        registerNotificationAirtelHt.setProcessed(false);
        registerNotificationAirtelHt.setDeleted(false);
        REGISTER_NOTIFICATION_AIRTEL_HT = registerNotificationAirtelHt.getRichAndroidMessageJsonObject();
        REGISTER_NOTIFICATION_AIRTEL_HT.put("aok", "Avail Now");

        
        MusicAdminNotification airtelFreeSubNotification = new MusicAdminNotification();
        airtelFreeSubNotification.setNotificationId(MusicConstants.WYNK_SUBSCRIPTION_OFFER_NOTIFICATION_ID);
        airtelFreeSubNotification.setActionOpen(ActionOpen.ALERT);
        airtelFreeSubNotification.setText("Free Wynk Plus subscription has been activated. Enjoy unlimited song downloads. Manage your library from ‘My Music’.");
        airtelFreeSubNotification.setNonRichText("Free Wynk Plus subscription has been activated. Enjoy unlimited song downloads. Manage your library from ‘My Music’.");
        airtelFreeSubNotification.setTitle("Special offers for Airtel Users");
        airtelFreeSubNotification.setProcessed(false);
        airtelFreeSubNotification.setDeleted(false);
        AIRTEL_FREE_SUBSCRIPTION_NOTIFICATION = airtelFreeSubNotification.getRichAndroidMessageJsonObject();
        AIRTEL_FREE_SUBSCRIPTION_NOTIFICATION.put("aok", "Ok");
        
        MusicAdminNotification freeDataOfferNotificationWithSubs = new MusicAdminNotification();
        freeDataOfferNotificationWithSubs.setNotificationId(MusicConstants.WYNK_FREE_DATA_OFFER_NOTIFICATION_ID);
        freeDataOfferNotificationWithSubs.setTargetScreen(ScreenCode.HOME);
        freeDataOfferNotificationWithSubs.setActionOpen(ActionOpen.ALERT);
        freeDataOfferNotificationWithSubs.setTitle("Special offers for Airtel Users");
        freeDataOfferNotificationWithSubs.setText("Free Wynk Plus subscription activated. Enjoy unlimited song downloads. Manage your library from ‘My Music’.You also get 100 MB Night data (12am - 6am), valid for 7 days.");
        freeDataOfferNotificationWithSubs.setNonRichText("Free Wynk Plus subscription activated. Enjoy unlimited song downloads. Manage your library from ‘My Music’.You also get 100 MB Night data (12am - 6am), valid for 7 days.");
        freeDataOfferNotificationWithSubs.setProcessed(false);
        freeDataOfferNotificationWithSubs.setDeleted(false);
        FREE_DATA_OFFER_NOTIFICATION_WITH_SUBSCRIPTION = freeDataOfferNotificationWithSubs.getRichAndroidMessageJsonObject();
        FREE_DATA_OFFER_NOTIFICATION_WITH_SUBSCRIPTION.put("aok", "Ok");
        
        MusicAdminNotification freeDataOfferNotification = new MusicAdminNotification();
        freeDataOfferNotification.setNotificationId(MusicConstants.WYNK_FREE_DATA_OFFER_NOTIFICATION_ID);
        freeDataOfferNotification.setTargetScreen(ScreenCode.HOME);
        freeDataOfferNotification.setActionOpen(ActionOpen.ALERT);
        freeDataOfferNotification.setTitle("100 MB Night Data");
        freeDataOfferNotification.setText("Congratulations! You get 100 MB Night data (12am - 6am), valid for 7 days.");
        freeDataOfferNotification.setNonRichText("Congratulations! You get 100 MB Night data (12am - 6am), valid for 7 days.");
        freeDataOfferNotification.setProcessed(false);
        freeDataOfferNotification.setDeleted(false);
        FREE_DATA_OFFER_NOTIFICATION = freeDataOfferNotification.getRichAndroidMessageJsonObject();
        FREE_DATA_OFFER_NOTIFICATION.put("aok", "Ok");
        
        MusicAdminNotification NAneverSubscribedNotification = new MusicAdminNotification();
        NAneverSubscribedNotification.setNotificationId(MusicConstants.WYNK_NON_AIRTEL_SUBSCRIPTION_OFFER_NOTIFICATION_ID_NEVER_SUBSCRIBED);
        NAneverSubscribedNotification.setActionOpen(ActionOpen.ALERT);
        NAneverSubscribedNotification.setText("Free Wynk Plus subscription activated for 1 month. Enjoy unlimited song downloads. Manage your library from ‘My Music’.");
        NAneverSubscribedNotification.setNonRichText("Free Wynk Plus subscription activated for 1 month. Enjoy unlimited song downloads. Manage your library from ‘My Music’.");
        NAneverSubscribedNotification.setTitle("You are all set on Wynk!");
        NAneverSubscribedNotification.setProcessed(false);
        NAneverSubscribedNotification.setDeleted(false);
        NON_AIRTEL_NEVER_SUBSCRIBED_NOTIFICATION = NAneverSubscribedNotification.getRichAndroidMessageJsonObject();
        NON_AIRTEL_NEVER_SUBSCRIBED_NOTIFICATION.put("aok", "Ok");
        
        MusicAdminNotification notification = new MusicAdminNotification();
        notification.setNotificationId(MusicConstants.WYNK_NON_AIRTEL_VODAFONE_NOTIFICATION_ID);
        notification.setActionOpen(ActionOpen.ALERT);
        notification.setText("Free subscription activated. Click on the download icon to start downloading songs. Manage your library from ‘My Music’");
        notification.setNonRichText("Free subscription activated. Click on the download icon to start downloading songs. Manage your library from ‘My Music’");
        notification.setTitle("You are all set!");
        notification.setProcessed(false);
        notification.setDeleted(false);
        VODAFONE_SUBSCRIPTION_NOTIFICATION = notification.getRichAndroidMessageJsonObject();
        VODAFONE_SUBSCRIPTION_NOTIFICATION.put("aok", "Ok");

        MusicAdminNotification intlRoamingExpiryNotification = new MusicAdminNotification();
        intlRoamingExpiryNotification.setNotificationId(MusicConstants.INTL_ROAMING_STATIC_ID);
        intlRoamingExpiryNotification.setTargetScreen(ScreenCode.HOME);
        intlRoamingExpiryNotification.setTitle("International Wynk Usage - 7 Days Left");
        intlRoamingExpiryNotification.setText(MusicConstants.INTL_ROAMING_NOTIFICATION_EXPIRY_WARNING);
        intlRoamingExpiryNotification.setNonRichText(MusicConstants.INTL_ROAMING_NOTIFICATION_EXPIRY_WARNING);
        intlRoamingExpiryNotification.setProcessed(false);
        intlRoamingExpiryNotification.setDeleted(false);
        INTL_ROAMING_EXPIRY_NOTIFICATION = intlRoamingExpiryNotification.getRichAndroidMessageJsonObject();
        INTL_ROAMING_EXPIRY_NOTIFICATION.put("aok", "OK");
        INTL_ROAMING_EXPIRY_NOTIFICATION.put("acncl", "Dismiss");

        MusicAdminNotification intlRoamingExpiredNotification = new MusicAdminNotification();
        intlRoamingExpiredNotification.setNotificationId(MusicConstants.INTL_ROAMING_STATIC_ID);
        intlRoamingExpiredNotification.setTargetScreen(ScreenCode.DOWNLOAD_COMPLETED);
        intlRoamingExpiredNotification.setTitle("International Wynk Usage Expired");
        intlRoamingExpiredNotification.setText(MusicConstants.INTL_ROAMING_NOTIFICATION_EXPIRED);
        intlRoamingExpiredNotification.setNonRichText(MusicConstants.INTL_ROAMING_NOTIFICATION_EXPIRED);
        intlRoamingExpiredNotification.setProcessed(false);
        intlRoamingExpiredNotification.setDeleted(false);
        INTL_ROAMING_EXPIRED_NOTIFICATION = intlRoamingExpiredNotification.getRichAndroidMessageJsonObject();
        INTL_ROAMING_EXPIRED_NOTIFICATION.put("aok", "Offline Music");
        INTL_ROAMING_EXPIRED_NOTIFICATION.put("acncl", "Dismiss");

        MusicAdminNotification intlRoamingActicatedNotification = new MusicAdminNotification();
        intlRoamingActicatedNotification.setNotificationId(MusicConstants.INTL_ROAMING_STATIC_ID);
        intlRoamingActicatedNotification.setTargetScreen(ScreenCode.HOME);
        intlRoamingActicatedNotification.setTitle(" International Network Detected");
        intlRoamingActicatedNotification.setText(MusicConstants.INTL_ROAMING_NOTIFICATION_ACTIVATED);
        intlRoamingActicatedNotification.setNonRichText(MusicConstants.INTL_ROAMING_NOTIFICATION_ACTIVATED);
        intlRoamingActicatedNotification.setProcessed(false);
        intlRoamingActicatedNotification.setDeleted(false);
        INTL_ROAMING_ACTIVATED_NOTIFICATION = intlRoamingActicatedNotification.getRichAndroidMessageJsonObject();
        INTL_ROAMING_ACTIVATED_NOTIFICATION.put("aok", "OK");
        INTL_ROAMING_ACTIVATED_NOTIFICATION.put("acncl", "Dismiss");

        MusicAdminNotification intlRoamingIneligibleNotification = new MusicAdminNotification();
        intlRoamingIneligibleNotification.setNotificationId(MusicConstants.INTL_ROAMING_STATIC_ID);
        intlRoamingIneligibleNotification.setTargetScreen(ScreenCode.DOWNLOAD_COMPLETED);
        intlRoamingIneligibleNotification.setTitle("WiFi Streaming Not Available");
        intlRoamingIneligibleNotification.setText("Music streaming on WiFi isn't available in your geography.");
        intlRoamingIneligibleNotification.setNonRichText("Music streaming on WiFi isn't available in your geography.");
        intlRoamingIneligibleNotification.setProcessed(false);
        intlRoamingIneligibleNotification.setDeleted(false);
        INTL_ROAMING_INELEIGIBLE_NOTIFICATION = intlRoamingIneligibleNotification.getRichAndroidMessageJsonObject();
        INTL_ROAMING_INELEIGIBLE_NOTIFICATION.put("aok", "Offline Music");
        INTL_ROAMING_INELEIGIBLE_NOTIFICATION.put("acncl", "Dismiss");

        MusicAdminNotification registerDownloadNotificationAirtel = new MusicAdminNotification();
        registerDownloadNotificationAirtel.setNotificationId(MusicConstants.WYNK_REGISTER);
        registerDownloadNotificationAirtel.setTargetScreen(ScreenCode.REGISTER);
        registerDownloadNotificationAirtel.setActionOpen(ActionOpen.ALERT);
        registerDownloadNotificationAirtel.setTitle("Take your music offline");
        registerDownloadNotificationAirtel.setText("Register now for unlimited streaming & offline music, worth Rs.99/month, only for Airtel users");
        registerDownloadNotificationAirtel.setNonRichText("Register now for unlimited streaming & offline music, worth Rs.99/month, only for Airtel users");
        registerDownloadNotificationAirtel.setProcessed(false);
        registerDownloadNotificationAirtel.setDeleted(false);
        REGISTER_DOWNLOAD_NOTIFICATION_AIRTEL = registerDownloadNotificationAirtel.getRichAndroidMessageJsonObject();
        REGISTER_DOWNLOAD_NOTIFICATION_AIRTEL.put("aok", "Avail Now");

        MusicAdminNotification registerDownloadNotificationNonAirtel = new MusicAdminNotification();
        registerDownloadNotificationNonAirtel.setNotificationId(MusicConstants.WYNK_REGISTER);
        registerDownloadNotificationNonAirtel.setTargetScreen(ScreenCode.REGISTER);
        registerDownloadNotificationNonAirtel.setActionOpen(ActionOpen.ALERT);
        registerDownloadNotificationNonAirtel.setTitle("Take your music offline");
        registerDownloadNotificationNonAirtel.setText("Register now for unlimited streaming & offline music, worth Rs.99/month, free for the 1st month");
        registerDownloadNotificationNonAirtel.setNonRichText("Register now for unlimited streaming & offline music, worth Rs.99/month, free for the 1st month");
        registerDownloadNotificationNonAirtel.setProcessed(false);
        registerDownloadNotificationNonAirtel.setDeleted(false);
        REGISTER_DOWNLOAD_NOTIFICATION_NON_AIRTEL = registerDownloadNotificationNonAirtel.getRichAndroidMessageJsonObject();
        REGISTER_DOWNLOAD_NOTIFICATION_NON_AIRTEL.put("aok", "Avail Now");
    }
}
