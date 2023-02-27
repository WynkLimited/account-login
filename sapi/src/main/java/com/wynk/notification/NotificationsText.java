package com.wynk.notification;

import com.wynk.constants.JsonKeyNames;
import com.wynk.common.Language;
import com.wynk.constants.MusicConstants;
import com.wynk.music.constants.MusicNotificationType;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum NotificationsText {
	
	FUP_UNSUBSCRIBED_EN(MusicNotificationType.FUP_UNSUBSCRIBED, Language.ENGLISH, "You have used 95% of your streaming benefit.You can play ${"+
	MusicConstants.REMAINING_KEY+"} more songs from your monthly FUP limit of ${"+MusicConstants.TOTAL_KEY+"} songs."),
	FUP_99_EN(MusicNotificationType.FUP_99, Language.ENGLISH,"You have used 95% of your subscription benefit."+
    		" You can play or download ${"+ MusicConstants.REMAINING_KEY+"} more songs from your monthly FUP limit of ${"+MusicConstants.TOTAL_KEY+"} songs."),
	FUP_UNSUBSCRIBED_HI(MusicNotificationType.FUP_UNSUBSCRIBED, Language.HINDI, "आप अपने स्ट्रीमिंग का 95% लाभ ले चुके हैं. अब आप अपने महीने की ${"+MusicConstants.TOTAL_KEY+"} गानो की FUP " + 
	"लिमिट में से ${"+MusicConstants.REMAINING_KEY+"} गानो और प्ले कर सकते है."),
	FUP_99_HI(MusicNotificationType.FUP_99, Language.HINDI, "आप अपने सब्स्क्रिप्शन का 95% लाभ ले चुके हैं. अब आप अपने महीने की ${"+MusicConstants.TOTAL_KEY+"} गानो की FUP लिमिट में से " + 
    		"${"+MusicConstants.REMAINING_KEY+"} गानो और प्ले या डाउनलोड कर सकते है."),
	FUP_UNSUBSCRIBED_TOTAL_EN(MusicNotificationType.FUP_UNSUBSCRIBED_TOTAL, Language.ENGLISH, "Your monthly FUP limit is ${"+MusicConstants.TOTAL_KEY+"} songs."),
	FUP_UNSUBSCRIBED_TOTAL_HI(MusicNotificationType.FUP_UNSUBSCRIBED_TOTAL, Language.HINDI, "आपकी महीने की FUP लिमिट ${"+MusicConstants.TOTAL_KEY+"} गाने हैं."),
	FUP_UNSUBSCRIBED_REMAINING_EN(MusicNotificationType.FUP_UNSUBSCRIBED_REMAINING, Language.ENGLISH, "You have ${"+MusicConstants.REMAINING_KEY+"} songs remaining to play."),
	FUP_UNSUBSCRIBED_REMAINING_HI(MusicNotificationType.FUP_UNSUBSCRIBED_REMAINING, Language.HINDI, "आपके ${"+MusicConstants.REMAINING_KEY+"} गाने प्ले होने बाकी हैं."),
	FUP_UNSUBSCRIBED_PURCHASE_EN(MusicNotificationType.FUP_UNSUBSCRIBED_PURCHASE, Language.ENGLISH, "Purchase music subscription pack for unlimited benefits"),
	FUP_UNSUBSCRIBED_PURCHASE_HI(MusicNotificationType.FUP_UNSUBSCRIBED_PURCHASE, Language.HINDI, "अनलिमिटेड सुविधा के लिए म्यूज़िक सब्स्क्रिप्शन पैक खरीदें"),
	FUP_99_TOTAL_EN(MusicNotificationType.FUP_99_TOTAL, Language.ENGLISH, "Your monthly FUP limit is ${"+MusicConstants.TOTAL_KEY+"} songs."),
	FUP_99_TOTAL_HI(MusicNotificationType.FUP_99_TOTAL, Language.HINDI, "आपकी महीने की FUP लिमिट ${"+MusicConstants.TOTAL_KEY+"} गाने हैं."),
	FUP_99_REMAINING_EN(MusicNotificationType.FUP_99_REMAINING, Language.ENGLISH, "You have ${"+MusicConstants.REMAINING_KEY+"} songs remaining to play/download."),
	FUP_99_REMAINING_HI(MusicNotificationType.FUP_99_REMAINING, Language.HINDI, "आपके ${"+MusicConstants.REMAINING_KEY+"} गाने प्ले या डाउनलोड होने बाकी हैं."),
	FUP_99_REMAINING_TOTAL_EN(MusicNotificationType.FUP_99_REMAINING_TOTAL, Language.ENGLISH, "${"+MusicConstants.REMAINING_KEY+"} of ${"+MusicConstants.TOTAL_KEY+"} songs left."),
    FUP_99_REMAINING_TOTAL_HI(MusicNotificationType.FUP_99_REMAINING_TOTAL, Language.HINDI, "${"+MusicConstants.TOTAL_KEY+"} गानों में से ${"+MusicConstants.REMAINING_KEY+"} गाने बचे हैं"),
	FUP_99_DATACHARGES_EN(MusicNotificationType.FUP_99_DATACHARGES, Language.ENGLISH, "Data charges will apply for streaming or downloading the songs."),
	FUP_99_DATACHARGES_HI(MusicNotificationType.FUP_99_DATACHARGES, Language.HINDI, "स्ट्रीमिंग या गाने डाउनलोड करने के लिए डाटा शुल्क लागू होगा."),
	FUP_95_EN(MusicNotificationType.FUP_95, Language.ENGLISH, "You have used 95% of your streaming benefit"),
    FUP_95_HI(MusicNotificationType.FUP_95, Language.HINDI, "आपने अपनी मुफ़्त स्ट्रिमिंग को 95% इस्तेमाल कर लिया हे"),
	FUP_95_TITLE_EN(MusicNotificationType.FUP_95_TITLE, Language.ENGLISH, "FUP 95% Alert"),
    FUP_95_TITLE_HI(MusicNotificationType.FUP_95_TITLE, Language.HINDI, "FUP 95% चेतावनी"),
    
    OFFER_PACK_COMPLETED_TITLE_EN(MusicNotificationType.OFFER_PACK_COMPLETED_TITLE, Language.ENGLISH, "You have consumed 100 songs of your total quota."),
    OFFER_PACK_COMPLETED_TITLE_HI(MusicNotificationType.OFFER_PACK_COMPLETED_TITLE, Language.HINDI, "आपने अपनी मुफ़्त स्ट्रिमिंग को 90% इस्तेमाल कर लिया हे"),
    OFFER_PACK_COMPLETED_EN(MusicNotificationType.OFFER_PACK_COMPLETED, Language.ENGLISH, "Now data charges will apply."),
    OFFER_PACK_COMPLETED_HI(MusicNotificationType.OFFER_PACK_COMPLETED, Language.HINDI, "आपने अपनी मुफ़्त स्ट्रिमिंग को 90% इस्तेमाल कर लिया हे"),
    OFFER_PACK_90_EN(MusicNotificationType.OFFER_PACK_90, Language.ENGLISH, "You have used 90% of your streaming benefit"),
    OFFER_PACK_90_HI(MusicNotificationType.OFFER_PACK_90, Language.HINDI, "आपने अपनी मुफ़्त स्ट्रिमिंग को 90% इस्तेमाल कर लिया हे"),
    OFFER_PACK_90_TITLE_EN(MusicNotificationType.OFFER_PACK_90_TITLE, Language.ENGLISH, "Offer Pack 90% Alert"),
    OFFER_PACK_90_TITLE_HI(MusicNotificationType.OFFER_PACK_90_TITLE, Language.HINDI, "Offer Pack 90% चेतावनी"),
    OFFER_PACK_80_EN(MusicNotificationType.OFFER_PACK_80, Language.ENGLISH, "You have used 80% of your streaming benefit"),
    OFFER_PACK_80_HI(MusicNotificationType.OFFER_PACK_80, Language.HINDI, "आपने अपनी मुफ़्त स्ट्रिमिंग को 80% इस्तेमाल कर लिया हे"),
    OFFER_PACK_80_TITLE_EN(MusicNotificationType.OFFER_PACK_80_TITLE, Language.ENGLISH, "Offer Pack 80% Alert"),
    OFFER_PACK_80_TITLE_HI(MusicNotificationType.OFFER_PACK_80_TITLE, Language.HINDI, "Offer Pack 80% चेतावनी"),
    
    IFB_TITLE_EN(MusicNotificationType.IFB_TITLE, Language.ENGLISH, "${"+MusicConstants.TOTAL_KEY+"} Free Songs @ OTI"),
    IFB_TITLE_HI(MusicNotificationType.IFB_TITLE, Language.HINDI, "OTI के जरिए ${"+MusicConstants.TOTAL_KEY+"} गाने मुफ्त"),
    IFB_TITLE_MESSAGE_EN(MusicNotificationType.IFB_TITLE_MESSAGE, Language.ENGLISH, "Stream ${"+MusicConstants.TOTAL_KEY+"} songs free till ${"+JsonKeyNames.VALID_TILL+"} without data charges."),
    IFB_TITLE_MESSAGE_HI(MusicNotificationType.IFB_TITLE_MESSAGE, Language.HINDI, "${"+MusicConstants.TOTAL_KEY+"} गाने सुनें बिलकुल मुफ्त बिना किसी डाटा शुल्क के, ${"+JsonKeyNames.VALID_TILL+"} तक."),
    IFB_BENIFIT_TITLE_EN(MusicNotificationType.IFB_BENIFIT_TITLE, Language.ENGLISH, "You have streamed ${"+MusicConstants.CURRENT_KEY+"} out of ${"+MusicConstants.TOTAL_KEY+"} songs."),
    IFB_BENIFIT_TITLE_HI(MusicNotificationType.IFB_BENIFIT_TITLE, Language.HINDI, "आपने ${"+MusicConstants.TOTAL_KEY+"} मुफ्त गानों में से ${"+MusicConstants.CURRENT_KEY+"} की स्ट्रीमिंग कर ली है."),
    DATA_CHARGES_EN(MusicNotificationType.DATA_CHARGES, Language.ENGLISH, "Data charges will apply"),
    DATA_CHARGES_HI(MusicNotificationType.DATA_CHARGES, Language.HINDI, "डाटा शुल्क लागू होगा"),
    NODATA_CHARGES_EN(MusicNotificationType.NODATA_CHARGES, Language.ENGLISH, "No data charges will apply"),
    NODATA_CHARGES_HI(MusicNotificationType.NODATA_CHARGES, Language.HINDI, "कोई डाटा शुल्क लागू नहीं होगा"),
    IFB_STARTED_EN(MusicNotificationType.IFB_STARTED, Language.ENGLISH, "Enjoy free streaming of ${"+MusicConstants.TOTAL_KEY+"} songs without data charges. Offer is valid only up to 60 days."),
    IFB_STARTED_HI(MusicNotificationType.IFB_STARTED, Language.HINDI, "${"+MusicConstants.TOTAL_KEY+"} गानों का आनंद लें बिलकुल मुफ्त बिना किसी डाटा शुल्क के. यह ऑफर केवल 60 दिनों तक वैध है."),
    IFB_80_EN(MusicNotificationType.IFB_80, Language.ENGLISH, "You have streamed ${"+MusicConstants.CURRENT_KEY+"} out of ${"+MusicConstants.TOTAL_KEY+"} free songs."),
    IFB_80_HI(MusicNotificationType.IFB_80, Language.HINDI, "आपने ${"+MusicConstants.TOTAL_KEY+"} मुफ्त गानों में से ${"+MusicConstants.CURRENT_KEY+"} की स्ट्रीमिंग कर ली है."),
    IFB_LIMIT_EN(MusicNotificationType.IFB_LIMIT, Language.ENGLISH, "You have exhausted limit of ${"+MusicConstants.TOTAL_KEY+"} free songs. Data charges will apply for streaming songs."),
    IFB_LIMIT_HI(MusicNotificationType.IFB_LIMIT, Language.HINDI, "आप ${"+MusicConstants.TOTAL_KEY+"} मुफ्त गानों की सीमा खत्म हो रही है. अधिक गाने स्ट्रीम करने के लिए डाटा शुल्क लागू होगा."),
	FUP_UNLIMITED_EN(MusicNotificationType.FUP_UNLIMITED, Language.ENGLISH, "Unlimited"),
	FUP_UNLIMITED_HI(MusicNotificationType.FUP_UNLIMITED, Language.HINDI, "अनलिमिटेड"),
	VALID_TILL_EN(MusicNotificationType.VALID_TILL, Language.ENGLISH, "Benefit valid till"),
	VALID_TILL_HI(MusicNotificationType.VALID_TILL, Language.HINDI, "लाभ वैधता"),
	STREAMING_BENEFIT_EN(MusicNotificationType.STREAMING_BENEFIT, Language.ENGLISH, "Streaming benefit"),
	STREAMING_BENEFIT_HI(MusicNotificationType.STREAMING_BENEFIT, Language.HINDI, "स्ट्रीमिंग लाभ"),
	DATA_BENEFIT_EN(MusicNotificationType.DATA_BENEFIT, Language.ENGLISH, "Data benefit"),
	DATA_BENEFIT_HI(MusicNotificationType.DATA_BENEFIT, Language.HINDI, "डाटा लाभ"),
	DOWNLOAD_BENEFIT_EN(MusicNotificationType.DOWNLOAD_BENEFIT, Language.ENGLISH, "Download benefit"),
    DOWNLOAD_BENEFIT_HI(MusicNotificationType.DOWNLOAD_BENEFIT, Language.HINDI, "डाउनलोडा लाभ"),
	ACTIVE_EN(MusicNotificationType.ACTIVE, Language.ENGLISH, "Active"),
	ACTIVE_HI(MusicNotificationType.ACTIVE, Language.HINDI, "एक्टिव"),
	INACTIVE_EN(MusicNotificationType.INACTIVE, Language.ENGLISH, "Inactive"),
    INACTIVE_HI(MusicNotificationType.INACTIVE, Language.HINDI, "असक्रिय"),
	WYNK_MUSIC_EN(MusicNotificationType.WYNK_MUSIC, Language.ENGLISH, "Wynk Free (Rs99 Free Offer)"),
	WYNK_MUSIC_HI(MusicNotificationType.WYNK_MUSIC, Language.HINDI, "विंक फ्री (Rs99 Free Offer)"),
	WYNK_PLUS_EN(MusicNotificationType.WYNK_PLUS, Language.ENGLISH, "Wynk Plus"),
	WYNK_PLUS_HI(MusicNotificationType.WYNK_PLUS, Language.HINDI, "विंक प्लस"),
	WYNK_PLUS_FREE_EN(MusicNotificationType.WYNK_PLUS_FREE, Language.ENGLISH, "Free Wynk Plus Offer (Worth Rs. 99)"),
	WYNK_PLUS_FREE_HI(MusicNotificationType.WYNK_PLUS_FREE, Language.HINDI, "फ्री Wynk Plus ऑफ़र (99 रुपये के मूल्य का)"),
	
	WYNK_PLUS_FREE_NOBRACKETS_EN(MusicNotificationType.WYNK_PLUS_FREE_NOBRACKETS, Language.ENGLISH, "Free Wynk Plus Offer"),
	WYNK_PLUS_FREE_NOBRACKETS_HI(MusicNotificationType.WYNK_PLUS_FREE_NOBRACKETS, Language.HINDI, "फ्री Wynk Plus ऑफ़र"),
	
	OFFER_PACK_EN(MusicNotificationType.OFFER_PACK, Language.ENGLISH, "Free Data Offer"),
	OFFER_PACK_HI(MusicNotificationType.OFFER_PACK, Language.HINDI, "फ्री डेटा ऑफर"),
    
	OFFER_PACK_DETAIL1_EN(MusicNotificationType.OFFER_PACK_DETAIL1, Language.ENGLISH, "Offer is available for users on Airtel mobile internet only."),
	OFFER_PACK_DEATIL1_HI(MusicNotificationType.OFFER_PACK_DETAIL1, Language.HINDI, "यह ऑफर एयरटेल मोबाइल इंटरनेट पर ही उपयोगकर्ताओं के लिए उपलब्ध है"),
	
	OFFER_PACK_DETAIL2_EN(MusicNotificationType.OFFER_PACK_DETAIL2, Language.ENGLISH, "If you subscribe to Wynk freedom during the offer period, benefits of Wynk freedom will apply."),
	OFFER_PACK_DETAIL2_HI(MusicNotificationType.OFFER_PACK_DETAIL2, Language.HINDI, "अगर आप ऑफर की अवधि के दौरान Wynk Freedom सब्स्क्राइब करते हैं तो Wynk Freedom का लाभ लागू होगा"),
	
	ACTIVATION_OFFER_PACK_EN(MusicNotificationType.ACTIVATION_OFFER_PACK, Language.ENGLISH, "FREE : Listen or download songs for next 7 days"),
	ACTIVATION_OFFER_PACK_HI(MusicNotificationType.ACTIVATION_OFFER_PACK, Language.HINDI, ""),
	
    ACTIVATION_OFFER_BENEFIT_EN(MusicNotificationType.BENEFIT_7, Language.ENGLISH, "No data charges for first ${"+MusicConstants.TOTAL_KEY+"} songs of the week."),
    ACTIVATION_OFFER_BENEFIT_HI(MusicNotificationType.BENEFIT_7, Language.HINDI, "सप्ताह के पहले ${"+MusicConstants.TOTAL_KEY+"} गानों के लिए कोई डाटा शुल्क नहीं है."),
	
    ACTIVATION_OFFER_INFO1_EN(MusicNotificationType.INFO_ACTIVATION_OFFER_PACK1, Language.ENGLISH, "We value your association with wynk."),
    ACTIVATION_OFFER_INFO1_HI(MusicNotificationType.INFO_ACTIVATION_OFFER_PACK1, Language.HINDI, "Wynk के साथ आपका सहयोग बहुमूल्य है"),

    ACTIVATION_OFFER_INFO2_EN(MusicNotificationType.INFO_ACTIVATION_OFFER_PACK2, Language.ENGLISH, "We thought to add more value to this relationship by offering you 50 free songs for streaming / download."),
    ACTIVATION_OFFER_INFO2_HI(MusicNotificationType.INFO_ACTIVATION_OFFER_PACK2, Language.HINDI, "हम आपको स्ट्रीमिंग / डाउनलोड करने के लिए 50 मुफ्त गाने की पेशकश करके इस रिश्ते को और मूल्यवान बनाना चाहते हैं"),

    
	SPECIAL_DAY_OFFER_PACK_EN(MusicNotificationType.SPECIAL_DAY_OFFER_PACK, Language.ENGLISH, "Happy ${"+MusicConstants.SPECIAL_DAY+"}, Listen or download songs FREE for today!"),
	SPECIAL_DAY_OFFER_PACK_HI(MusicNotificationType.SPECIAL_DAY_OFFER_PACK, Language.HINDI, ""),
	
	SPECIAL_DAY_OFFER_INFO1_EN(MusicNotificationType.INFO_SPECIAL_DAY_OFFER_PACK1, Language.ENGLISH, "Celebrating the spirit of ${"+MusicConstants.SPECIAL_DAY+"}."),
	SPECIAL_DAY_OFFER_INFO1_HI(MusicNotificationType.INFO_SPECIAL_DAY_OFFER_PACK1, Language.HINDI, ""),

	SPECIAL_DAY_OFFER_INFO2_EN(MusicNotificationType.INFO_SPECIAL_DAY_OFFER_PACK2, Language.ENGLISH, "Let's enjoy it together with music. We added more fun to this festivities by offering you free songs for streaming / download today."),
	SPECIAL_DAY_OFFER_INFO2_HI(MusicNotificationType.INFO_SPECIAL_DAY_OFFER_PACK2, Language.HINDI, ""),

	
    WYNK_FREEDOM_EN(MusicNotificationType.WYNK_FREEDOM, Language.ENGLISH, "Wynk Freedom"),
    WYNK_FREEDOM_HI(MusicNotificationType.WYNK_FREEDOM, Language.HINDI, "विंक फ्रीडम"),
    CRITICAL_UDPATE_MESSAGE_EN(MusicNotificationType.CRITICAL_UPDATE_MESSAGE, Language.ENGLISH, "This version of Wynk is no longer supported. To continue using Wynk, update now to new version from Play/App Store."),
    CRITICAL_UDPATE_MESSAGE_HI(MusicNotificationType.CRITICAL_UPDATE_MESSAGE, Language.HINDI, "Wynk म्यूज़िक का नया वर्ज़न डाउनलोड करने के लिए उपलब्ध है. Wynk ​का नॉन-स्टॉप मज़ा लेते रहने के लिए प्ले/ऐप स्टोर पर जाकर इस ऐप को अभी अपडेट करें."),
    CRITICAL_UPDATE_TITLE_EN(MusicNotificationType.CRITICAL_UPDATE_TITLE, Language.ENGLISH, "Critical Update"),
    CRITICAL_UPDATE_TITLE_HI(MusicNotificationType.CRITICAL_UPDATE_TITLE, Language.HINDI, "महत्वपूर्ण अपडेट"),
    UPDATE_AVAILABLE_MESSAGE_EN(MusicNotificationType.UPDATE_AVAILABLE_MESSAGE, Language.ENGLISH, "Try our New Redesigned Wynk app that allows you to Follow your favorite Artists, get Personalised Recommendations, and Much More. Upgrade Now!"),
    UPDATE_AVAILABLE_MESSAGE_HI(MusicNotificationType.UPDATE_AVAILABLE_MESSAGE, Language.HINDI, "आप Wynk के पुराने वर्ज़न पर हैं. हमारा नया वर्ज़न डाउनलोड कर बेहेतरीन स्ट्रीमिंग अनुभव करें."),
	UPDATE_AVAILABLE_TITLE_EN(MusicNotificationType.UPDATE_AVAILABLE_TITLE, Language.ENGLISH, "Brand New Wynk App launched!"),
    UPDATE_AVAILABLE_TITLE_HI(MusicNotificationType.UPDATE_AVAILABLE_TITLE, Language.HINDI, "Wynk अपग्रेड करें"),
    OTP_INVALID_TITLE_EN(MusicNotificationType.OTP_INVALID_TITLE, Language.ENGLISH, "That's not right"),
    OTP_INVALID_TITLE_HI(MusicNotificationType.OTP_INVALID_TITLE, Language.HINDI, "सही नहीं था."),
    OTP_INVALID_MESSAGE_EN(MusicNotificationType.OTP_INVALID_MESSAGE, Language.ENGLISH, "Incorrect OTP entered. Please enter the correct OTP"),
    OTP_INVALID_MESSAGE_HI(MusicNotificationType.OTP_INVALID_MESSAGE, Language.HINDI, "आपके द्वारा डाला हुआ OTP गलत है| कृपया सही OTP डालें|"),
    OTP_EXPIRED_TITLE_EN(MusicNotificationType.OTP_EXPIRED_TITLE, Language.ENGLISH, "Your PIN expired"),
    OTP_EXPIRED_TITLE_HI(MusicNotificationType.OTP_EXPIRED_TITLE, Language.HINDI, "Your PIN expired"),
    OTP_EXPIRED_MESSAGE_EN(MusicNotificationType.OTP_EXPIRED_MESSAGE, Language.ENGLISH, "Please enter the new 4 digit PIN you received via SMS."),
    OTP_EXPIRED_MESSAGE_HI(MusicNotificationType.OTP_EXPIRED_MESSAGE, Language.HINDI, "Please enter the new 4 digit PIN you received via SMS."),
    SUBSCRIBE_EN(MusicNotificationType.SUBSCRIBE, Language.ENGLISH, "Subscribe"),
    SUBSCRIBE_HI(MusicNotificationType.SUBSCRIBE, Language.HINDI, "सब्स्क्राइब"),
    CONTINUE_EN(MusicNotificationType.CONTINUE, Language.ENGLISH, "Continue"),
    CONTINUE_HI(MusicNotificationType.CONTINUE, Language.HINDI, "Continue"),
    UNSUBSCRIBE_EN(MusicNotificationType.UNSUBSCRIBE, Language.ENGLISH, "Unsubscribe"),
    UNSUBSCRIBE_HI(MusicNotificationType.UNSUBSCRIBE, Language.HINDI, "अनसब्स्क्राइब"),
    CANCEL_EN(MusicNotificationType.CANCEL, Language.ENGLISH, "Cancel"),
    CANCEL_HI(MusicNotificationType.CANCEL, Language.HINDI, "Cancel"),
    CHANGE_PLAN_EN(MusicNotificationType.CHANGE_PLAN, Language.ENGLISH, "Change Plan"),
    CHANGE_PLAN_HI(MusicNotificationType.CHANGE_PLAN, Language.HINDI, "प्लान बदलें"),
    UNSUBSCRIBE_ACTIVE_LINE1_EN(MusicNotificationType.UNSUBSCRIBE_ACTIVE_LINE1, Language.ENGLISH, "Your subscription is active till ${"+JsonKeyNames.VALID_TILL+"}."),
    UNSUBSCRIBE_ACTIVE_LINE1_HI(MusicNotificationType.UNSUBSCRIBE_ACTIVE_LINE1, Language.HINDI, "आपका सब्स्क्रिप्शन ${"+JsonKeyNames.VALID_TILL+"} तक एक्टिव है."),
    UNSUBSCRIBE_ACTIVE_LINE2_EN(MusicNotificationType.UNSUBSCRIBE_ACTIVE_LINE2, Language.ENGLISH, "If you unsubscribe now, your subscription will not renew on ${"+JsonKeyNames.VALID_TILL+"} but will remain active till then."),
    UNSUBSCRIBE_ACTIVE_LINE2_HI(MusicNotificationType.UNSUBSCRIBE_ACTIVE_LINE2, Language.HINDI, "यदि आप अभी अनसब्स्क्राइब करते हैं तो आपका सब्स्क्रिप्शन ${"+JsonKeyNames.VALID_TILL+"} को रिन्यू नहीं होगा, परंतु तब तक एक्टिव रहेगा."),
    UNSUBSCRIBE_INACTIVE_LINE1_EN(MusicNotificationType.UNSUBSCRIBE_INACTIVE_LINE1, Language.ENGLISH, "If you unsubscribe now, your subscription will not be attempted to renew."),
    UNSUBSCRIBE_INACTIVE_LINE1_HI(MusicNotificationType.UNSUBSCRIBE_INACTIVE_LINE1, Language.HINDI, "यदि आप अभी अनसब्स्क्राइब करते हैं तो आपके सब्स्क्रिप्शन को रिन्यू करने की कोशिश नहीं होगी."),
    UNSUBSCRIBE_INACTIVE_LINE2_EN(MusicNotificationType.UNSUBSCRIBE_INACTIVE_LINE2, Language.ENGLISH, "You will be able to renew your subscription manually though."),
    UNSUBSCRIBE_INACTIVE_LINE2_HI(MusicNotificationType.UNSUBSCRIBE_INACTIVE_LINE2, Language.HINDI, "हालांकि आप अपने सब्स्क्रिप्शन को मेन्यूली रिन्यू कर पाने में सक्षम होंगे."),
    UNSUBSCRIBE_PENDING_LINE1_EN(MusicNotificationType.UNSUBSCRIBE_PENDING_LINE1, Language.ENGLISH, "Attempt to renew subscription with your PayTM account has been made and we are waiting for response. Ref #${"+JsonKeyNames.TXN_ID+"}."),
    UNSUBSCRIBE_PENDING_LINE1_HI(MusicNotificationType.UNSUBSCRIBE_PENDING_LINE1, Language.HINDI, "Attempt to renew subscription with your PayTM account has been made and we are waiting for response. Ref #${"+JsonKeyNames.TXN_ID+"}."),
    UNSUBSCRIBE_PENDING_LINE2_EN(MusicNotificationType.UNSUBSCRIBE_PENDING_LINE2, Language.ENGLISH, "If you unsubscribe now and we receive your payment through PayTM account later, we may not be able to honour the payment. We recommend you to try again later."),
    UNSUBSCRIBE_PENDING_LINE2_HI(MusicNotificationType.UNSUBSCRIBE_PENDING_LINE2, Language.HINDI, "If you unsubscribe now and we receive your payment through PayTM account later, we may not be able to honour the payment. We recommend you to try again later."),
    CHANGE_PLAN_LINE1_EN(MusicNotificationType.CHANGE_PLAN_LINE1, Language.ENGLISH, "If you choose to change plan, first your subscription will be unsubscribed for renewal. After successfully changing the plan, your current plan will be dropped. Do you wish to continue?"),
    CHANGE_PLAN_LINE1_HI(MusicNotificationType.CHANGE_PLAN_LINE1, Language.HINDI, "यदि आप प्लान बदलना चाहते हैं तो पहले आपका सब्स्क्रिप्शन रिन्यूल के लिए अनसब्स्क्राइब होगा. प्लान के सफलतापूर्वक बदलने के बाद, आपका वर्तमान प्लान हट जाएगा. क्या आप जारी रखना चाहेंगे?"),
    CHANGE_PLAN_LINE2_EN(MusicNotificationType.CHANGE_PLAN_LINE2, Language.ENGLISH, "If you choose to change plan, your current plan will be dropped after successfully changing the plan. Do you wish to continue?"),
    CHANGE_PLAN_LINE2_HI(MusicNotificationType.CHANGE_PLAN_LINE2, Language.HINDI, "यदि आप प्लान बदलने का चुनाव करते हैं तो नया प्लान शुरू होने के बाद आपका वर्तमान प्लान हट जाएगा. क्या आप जारी रखने के इच्छुक हैं?"),
    UNSUBSCRIBE_FAILURE1_EN(MusicNotificationType.UNSUBSCRIBE_FAILURE1, Language.ENGLISH, "Sorry! Your request to unsubscribe did not go through."),
	UNSUBSCRIBE_FAILURE1_HI(MusicNotificationType.UNSUBSCRIBE_FAILURE1, Language.HINDI, "क्षमा करें! अनसब्स्क्राइब के लिए आपका निवेदन आगे नहीं बढ़ पाया है."),
	UNSUBSCRIBE_FAILURE2_EN(MusicNotificationType.UNSUBSCRIBE_FAILURE2, Language.ENGLISH, "Your Subscription will renew on ${"+JsonKeyNames.VALID_TILL+"} and will remain active till then. Please try again to unsubscribe later"),
	UNSUBSCRIBE_FAILURE2_HI(MusicNotificationType.UNSUBSCRIBE_FAILURE2, Language.HINDI, "Your Subscription will renew on ${"+JsonKeyNames.VALID_TILL+"} and will remain active till then. Please try again to unsubscribe later"),
	
	UNSUBSCRIBE_INVALID_PRODUCT_ID1_EN(MusicNotificationType.UNSUBSCRIBE_INVALID_PRODUCT_ID1, Language.ENGLISH, "Sorry! Your request to unsubscribe did not go through. No product ID found."),
	UNSUBSCRIBE_INVALID_PRODUCT_ID1_HI(MusicNotificationType.UNSUBSCRIBE_INVALID_PRODUCT_ID1, Language.HINDI, "क्षमा करें! अनसब्स्क्राइब के लिए आपका निवेदन आगे नहीं बढ़ पाया है."),
	UNSUBSCRIBE_INVALID_PRODUCT_ID2_EN(MusicNotificationType.UNSUBSCRIBE_INVALID_PRODUCT_ID2, Language.ENGLISH, "Your Subscription will renew on ${"+JsonKeyNames.VALID_TILL+"} and will remain active till then. Please try again to unsubscribe later"),
	UNSUBSCRIBE_INVALID_PRODUCT_ID2_HI(MusicNotificationType.UNSUBSCRIBE_INVALID_PRODUCT_ID2, Language.HINDI, "Your Subscription will renew on ${"+JsonKeyNames.VALID_TILL+"} and will remain active till then. Please try again to unsubscribe later"),
	
	UNSUBSCRIBE_SUCCESS1_EN(MusicNotificationType.UNSUBSCRIBE_SUCCESS1, Language.ENGLISH, "Your request to unsubscribe has been processed successfully."),
	UNSUBSCRIBE_SUCCESS1_HI(MusicNotificationType.UNSUBSCRIBE_SUCCESS1, Language.HINDI, "अनसब्स्क्राइब करने का आपका निवेदन सफल हो गया है."),
	UNSUBSCRIBE_SUCCESS2_EN(MusicNotificationType.UNSUBSCRIBE_SUCCESS2, Language.ENGLISH, "Your subscription will not renew on ${"+JsonKeyNames.VALID_TILL+"} but will remain active till then."),
	UNSUBSCRIBE_SUCCESS2_HI(MusicNotificationType.UNSUBSCRIBE_SUCCESS2, Language.HINDI, "अब आपका सब्स्क्रिप्शन ${"+JsonKeyNames.VALID_TILL+"} को रिन्यू नहीं होगा, परंतु तब तक एक्टिव रहेगा."),
	UNSUBSCRIBE_SUCCESS_HEADER_EN(MusicNotificationType.UNSUBSCRIBE_SUCCESS_HEADER, Language.ENGLISH, "Success"),
	UNSUBSCRIBE_SUCCESS_HEADER_HI(MusicNotificationType.UNSUBSCRIBE_SUCCESS_HEADER, Language.HINDI, "सफल"),
	UNSUBSCRIBE_FAILURE_HEADER_EN(MusicNotificationType.UNSUBSCRIBE_FAILURE_HEADER, Language.ENGLISH, "Failure"),
	UNSUBSCRIBE_FAILURE_HEADER_HI(MusicNotificationType.UNSUBSCRIBE_FAILURE_HEADER, Language.HINDI, "असफल"),
	WYNK_FREE_INFO1_EN(MusicNotificationType.WYNK_FREE_INFO1, Language.ENGLISH, "You can stream unlimited songs with applicable data charges."),
	WYNK_FREE_INFO1_HI(MusicNotificationType.WYNK_FREE_INFO1, Language.HINDI, "आप तय लागू डाटा दरों पर अनलिमिटेड म्यूज़िक का मज़ा ले सकते हैं."),
	WYNK_FREE_INFO2_EN(MusicNotificationType.WYNK_FREE_INFO2, Language.ENGLISH, "Subscribe to download unlimited songs & play them without internet saving your data cost."),
    WYNK_FREE_INFO2_HI(MusicNotificationType.WYNK_FREE_INFO2, Language.HINDI, "सब्स्क्राइब करें डाउनलोड अनलिमिटेड सॉन्ग्स और गानों को बिना इंटरनेट के चलाकर अपनी डाटा लागत को कम करें."),
    WYNK_FREE_INFO_OFFER_NOT_AVAILED_EN(MusicNotificationType.WYNK_FREE_INFO_OFFER_NOT_AVAILED, Language.ENGLISH, "First month subscription is Free."),
    WYNK_FREE_INFO_OFFER_NOT_AVAILED_HI(MusicNotificationType.WYNK_FREE_INFO_OFFER_NOT_AVAILED, Language.HINDI, "पहले महीने का सब्स्क्रिप्शन फ्री है."),
    
    WYNK_NEW_FREE_INFO_OFFER_NOT_AVAILED_EN(MusicNotificationType.WYNK_NEW_FREE_INFO_OFFER_NOT_AVAILED, Language.ENGLISH, "One month subscription is Free."),
    WYNK_NEW_FREE_INFO_OFFER_NOT_AVAILED_HI(MusicNotificationType.WYNK_NEW_FREE_INFO_OFFER_NOT_AVAILED, Language.HINDI, "एक महीने का सब्स्क्रिप्शन फ्री है."),
    
    WYNK_FREE_INFO_OFFER_AVAILED_EN(MusicNotificationType.WYNK_FREE_INFO_OFFER_AVAILED, Language.ENGLISH, "Your download history is removed."),
    WYNK_FREE_INFO_OFFER_AVAILED_HI(MusicNotificationType.WYNK_FREE_INFO_OFFER_AVAILED, Language.HINDI, "आपकी डाउनलोड हिस्टरी हटा दी गई है."),
    INFO_EN(MusicNotificationType.INFO, Language.ENGLISH, "Info"),
    INFO_HI(MusicNotificationType.INFO, Language.HINDI, "Info"),
    BENEFIT_EN(MusicNotificationType.BENEFIT, Language.ENGLISH, "Benefit"),
    BENEFIT_HI(MusicNotificationType.BENEFIT, Language.HINDI, "Benefit"),
    BENEFIT_1_EN(MusicNotificationType.BENEFIT_1, Language.ENGLISH, "Unlimited Streaming & Download."),
    BENEFIT_1_HI(MusicNotificationType.BENEFIT_1, Language.HINDI, "अनलिमिटेड स्ट्रीमिंग और डाउनलोड."),
    BENEFIT_29_2_EN(MusicNotificationType.BENEFIT_29_2, Language.ENGLISH, "Data charges apply."),
    BENEFIT_29_2_HI(MusicNotificationType.BENEFIT_29_2, Language.HINDI, "डाटा शुल्क लागू."),
    BENEFIT_129_2_EN(MusicNotificationType.BENEFIT_129_2, Language.ENGLISH, "No data charges for first ${"+MusicConstants.TOTAL_KEY+"} songs every 30 days."),
    BENEFIT_129_2_HI(MusicNotificationType.BENEFIT_129_2, Language.HINDI, "प्रत्येक 30 दिनों में पहले ${"+MusicConstants.TOTAL_KEY+"} गानों के लिए कोई डाटा शुल्क नहीं है."),
	
	WYNK_FUP_SUBS_INFO1_EN(MusicNotificationType.WYNK_FUP_SUBS_INFO1, Language.ENGLISH, "Your subscription will auto renew on ${"+JsonKeyNames.VALID_TILL+"}."),
	WYNK_FUP_SUBS_INFO1_HI(MusicNotificationType.WYNK_FUP_SUBS_INFO1, Language.HINDI, "आपका सब्स्क्रिप्शन ${"+ JsonKeyNames.VALID_TILL+"} को अपने आप रिन्यू हो जाएगा."),
	WYNK_FUP_UNSUBS_INFO1_EN(MusicNotificationType.WYNK_FUP_UNSUBS_INFO1, Language.ENGLISH, "You have unsubscribed your subscription."),
	WYNK_FUP_UNSUBS_INFO1_HI(MusicNotificationType.WYNK_FUP_UNSUBS_INFO1, Language.HINDI, "आपने अपने सब्स्क्रिप्शन को अनसब्स्क्राइब कर दिया है."),
	WYNK_FUP_UNSUBS_INFO2_EN(MusicNotificationType.WYNK_FUP_UNSUBS_INFO2, Language.ENGLISH, "Your subscription will not auto renew but you can subscribe again to enjoy unlimited songs downloads."),
	WYNK_FUP_UNSUBS_INFO2_HI(MusicNotificationType.WYNK_FUP_UNSUBS_INFO2, Language.HINDI, "आपका सब्स्क्रिप्शन ऑटो-रिन्यू नहीं होगा, परंतु आप फिर से सब्स्क्राइब करके अनलिमिटेड गाने डाउनलोड करने का मज़ा ले सकते हैं."),
	WYNK_129_STATUS_EN(MusicNotificationType.WYNK_129_STATUS, Language.ENGLISH, "${"+MusicConstants.REMAINING_KEY+"} of ${"+MusicConstants.TOTAL_KEY+"} songs / ${"+MusicConstants.REMAINING_DAYS_KEY+"} days left"),
	WYNK_129_STATUS_HI(MusicNotificationType.WYNK_129_STATUS, Language.HINDI, "${"+MusicConstants.TOTAL_KEY+"} में से ${"+MusicConstants.REMAINING_KEY+"} गाने / ${"+MusicConstants.REMAINING_DAYS_KEY+"} दिन शेष"),
	STATUS_EN(MusicNotificationType.STATUS, Language.ENGLISH, "Status"),
    STATUS_HI(MusicNotificationType.STATUS, Language.HINDI, "Status"),
    SUBS_VALID_TILL_EN(MusicNotificationType.SUBS_VALID_TILL, Language.ENGLISH, "till ${"+JsonKeyNames.VALID_TILL+"}"),
    SUBS_VALID_TILL_HI(MusicNotificationType.SUBS_VALID_TILL, Language.HINDI, "till ${"+JsonKeyNames.VALID_TILL+"}"),
    INACTIVE_INFO_2_EN(MusicNotificationType.INACTIVE_INFO_2, Language.ENGLISH, "You will be able to play already downloaded songs without internet till ${"+JsonKeyNames.VALID_FROM+"} and with internet till ${"+JsonKeyNames.VALID_TILL+"}. Download history will be removed after that."),
    INACTIVE_INFO_2_HI(MusicNotificationType.INACTIVE_INFO_2, Language.HINDI, "डाउनलोड किए गए गानों को आप बिना इंटरनेट के ${"+JsonKeyNames.VALID_FROM+"} तक और इंटरनेट के साथ ${"+JsonKeyNames.VALID_TILL+"} तक सुन सकते हैं. उसके बाद डाउनलोडेड हिस्टरी हटा दी जाएगी."),
    INACTIVE_INFO_3_EN(MusicNotificationType.INACTIVE_INFO_3, Language.ENGLISH, "Airtel users can also take Wynk subscription at nearest outlet."),
    INACTIVE_INFO_3_HI(MusicNotificationType.INACTIVE_INFO_3, Language.HINDI, "एयरटेल उपभोक्ता भी नजदीकी आउटलेट से विंक का सब्स्क्रिप्शन ले सकते हैं."),
    INACTIVE_SUBSCRIBED_INFO_EN(MusicNotificationType.INACTIVE_SUBSCRIBED_INFO, Language.ENGLISH, "Your subscription is being attempted for renewal."),
    INACTIVE_SUBSCRIBED_INFO_HI(MusicNotificationType.INACTIVE_SUBSCRIBED_INFO, Language.HINDI, "आपके सब्स्क्रिप्शन को रिन्यू करने की कोशिश की गई है."),
    INACTIVE_UNSUBSCRIBED_INFO_EN(MusicNotificationType.INACTIVE_UNSUBSCRIBED_INFO, Language.ENGLISH, "Please subscribe again to continue to enjoy downloaded music offline."),
    INACTIVE_UNSUBSCRIBED_INFO_HI(MusicNotificationType.INACTIVE_UNSUBSCRIBED_INFO, Language.HINDI, "डाउनलोड किए गए म्यूज़िक का मज़ा ऑफ़लाइन लेते रहने के लिए फिर से सब्स्क्राइब करें."),
	
	PROMO_CODE_INVALID_EN(MusicNotificationType.PROMO_CODE_INVALID,  Language.ENGLISH, "Promo code not valid for this user"),
	PROMO_CODE_INVALID_HI(MusicNotificationType.PROMO_CODE_INVALID,  Language.HINDI, "इस उपयोगकर्ता के लिए यह प्रोमो कोड अमान्य है"),
	PROMO_CODE_USED_EN(MusicNotificationType.PROMO_CODE_USED,  Language.ENGLISH, "This promo code has already been used"),
	PROMO_CODE_USED_HI(MusicNotificationType.PROMO_CODE_USED,  Language.HINDI, "इस प्रोमो कोड का उपयोग पहले से ही किया जा चुका है"),
	PROMO_CODE_EXPIRED_EN(MusicNotificationType.PROMO_CODE_EXPIRED,  Language.ENGLISH, "Please enter a valid promo code to activate your Wynk subscription"),
	PROMO_CODE_EXPIRED_HI(MusicNotificationType.PROMO_CODE_EXPIRED,  Language.HINDI,  "कृपया 6 अंकों का सही प्रोमो कोड डालें"),
	PROMO_CODE_INVALID_COUPON_EN(MusicNotificationType.PROMO_CODE_INVALID_COUPON,  Language.ENGLISH, "Promo code invalid"),
	PROMO_CODE_INVALID_COUPON_HI(MusicNotificationType.PROMO_CODE_INVALID_COUPON,  Language.HINDI, "यह प्रोमो कोड अमान्य है"),
	PROMO_CODE_INPROGRESS_EN(MusicNotificationType.PROMO_CODE_INPROGRESS,  Language.ENGLISH, "Please wait while we process your request"),
	PROMO_CODE_INPROGRESS_HI(MusicNotificationType.PROMO_CODE_INPROGRESS,  Language.HINDI, "हम आपका निवेदन प्रोसेस कर रहे हैं | कृपया प्रतीक्षा करें" ),
	PROMO_CODE_NON_AIRTEL_EN(MusicNotificationType.PROMO_CODE_NON_AIRTEL,  Language.ENGLISH, "Wynk Freedom plan is available for Airtel users only. Please contact your promo code issuer"),
	PROMO_CODE_NON_AIRTEL_HI(MusicNotificationType.PROMO_CODE_NON_AIRTEL,  Language.HINDI, "Wynk फ़्रीडम प्लान केवल एयरटेल उपभोक्ताओं के लिए उपलब्ध है. कृपया प्रोमो कोड जारीकर्ता से संपर्क करें."),
	PROMO_CODE_SUCCESS_EN(MusicNotificationType.PROMO_CODE_SUCCESS,  Language.ENGLISH, "Wynk subscription has been activated for you.Please check My Account for subscription details"),
	PROMO_CODE_SUCCESS_HI(MusicNotificationType.PROMO_CODE_SUCCESS,  Language.HINDI, "Wynk सब्स्क्रिप्शन आपके लिए एक्टिवेट कर दी गयी है. सब्स्क्रिप्शन की जानकारी के लिए कृपया 'My Account' में जाएं. "),
	PROMO_CODE_ICR_ERROR_EN(MusicNotificationType.PROMO_CODE_ICR_ERROR,  Language.ENGLISH, "Wynk Freedom plan is available for Airtel users in circles having Airtel 3G services only. Please contact your promo code issuer"),
	PROMO_CODE_ICR_ERROR_HI(MusicNotificationType.PROMO_CODE_ICR_ERROR,  Language.HINDI, "Wynk फ़्रीडम प्लान केवल एयरटेल 3G सेवाओं के सर्कल में आने वाले उपभोक्ताओं के लिए उपलब्ध है. कृपया प्रोमो कोड जारीकर्ता से संपर्क करें."),
	WYNK_PLUS_4G_EN(MusicNotificationType.WYNK_PLUS_4G, Language.ENGLISH, "Wynk Plus 4G Offer"),
    WYNK_PLUS_4G_HI(MusicNotificationType.WYNK_PLUS_4G, Language.HINDI, "विंक प्लस 4G ऑफर"),
	ITUNES_RENEW_EN(MusicNotificationType.ITUNES_RENEW, Language.ENGLISH, "Please use iTunes to configure your subscription status."),
	ITUNES_RENEW_HI(MusicNotificationType.ITUNES_RENEW, Language.HINDI, "अपने सब्स्क्रिप्शन स्टेटस में किसी भी बदलाव के लिए iTunes पर जाएं।"),
	NOT_BEEN_CHARGED_EN(MusicNotificationType.NOT_BEEN_CHARGED, Language.ENGLISH, "You have not been charged for this subscription."),
	NOT_BEEN_CHARGED_HI(MusicNotificationType.NOT_BEEN_CHARGED, Language.HINDI, "इस सब्स्क्रिप्शन के लिए आपसे कोई शुल्क नहीं लिया गया है।"),
	RENEW_EN(MusicNotificationType.RENEW, Language.ENGLISH, "Renew"),
	RENEW_HI(MusicNotificationType.RENEW, Language.HINDI, "रिन्यू"),
	UPGRADE_EN(MusicNotificationType.UPGRADE, Language.ENGLISH, "Upgrade"),
	UPGRADE_HI(MusicNotificationType.UPGRADE, Language.HINDI, "अपग्रेड"),
	DEACTIVATED_EN(MusicNotificationType.DEACTIVATED, Language.ENGLISH, "Deactivated"),
	DEACTIVATED_HI(MusicNotificationType.DEACTIVATED, Language.HINDI, "डी-आक्टीवेटड"),
	DEACTIVATE_EN(MusicNotificationType.DEACTIVATE, Language.ENGLISH, "Deactivate"),
	DEACTIVATE_HI(MusicNotificationType.DEACTIVATE, Language.HINDI, "डी-आक्टीवेट"),
	AUTORENEWAL_EN(MusicNotificationType.AUTORENEWAL,Language.ENGLISH,"Renewal on ${"+JsonKeyNames.VALID_TILL+"}"),
	AUTORENEWAL_HI(MusicNotificationType.AUTORENEWAL,Language.HINDI,"${"+JsonKeyNames.VALID_TILL+"} को रिन्यू होगा"),
	VALID_TILL_DATE_EN(MusicNotificationType.VALID_TILL_DATE,Language.ENGLISH,"Valid till ${"+JsonKeyNames.VALID_TILL+"}"),
	VALID_TILL_DATE_HI(MusicNotificationType.VALID_TILL_DATE,Language.HINDI,"${"+JsonKeyNames.VALID_TILL+"} तक मान्य है"),
	PROMO_CODE_SERVER_ERROR_EN(MusicNotificationType.PROMO_CODE_SERVER_ERROR,Language.ENGLISH,"Internal error occured. Please try again in some time"),
	PROMO_CODE_SERVER_ERROR_HI(MusicNotificationType.PROMO_CODE_SERVER_ERROR,Language.HINDI,"एक तकनीकी त्रुटि हुई है | कृपया कुछ समय बाद फिर से प्रयास करें"),
	PROMO_CODE_REGISTRATION_REQUIRED_EN(MusicNotificationType.PROMO_CODE_REGISTRATION_REQUIRED,Language.ENGLISH,"Please register before applying promo code"),
	PROMO_CODE_REGISTRATION_REQUIRED_HI(MusicNotificationType.PROMO_CODE_REGISTRATION_REQUIRED,Language.HINDI,"प्रोमो कोड लागू करने से पहले कृपया रेगिस्टेर करें"),
	SEND_OTP_EN(MusicNotificationType.SEND_OTP,Language.ENGLISH,"Send Otp"),
	SEND_OTP_HI(MusicNotificationType.SEND_OTP,Language.HINDI,"OTP भेज"),
	PURCHASE_AIRTEL_BILLING_EN(MusicNotificationType.PURCHASE_AIRTEL_BILLING,Language.ENGLISH,"Bill My Airtel Account"),
	PURCHASE_AIRTEL_BILLING_HI(MusicNotificationType.PURCHASE_AIRTEL_BILLING,Language.HINDI,"बिल माय एयरटेल खाता");

	private MusicNotificationType notificationType;
	private Language lang;
	private String text;
	private static final Map<String, String> paramsToTextMap = constructParamsToTextMap();
	
	private static Map<String, String> constructParamsToTextMap() {
		Map<String, String> tempMap = new HashMap<>();
		for(NotificationsText nText: NotificationsText.values()) {
			String key = nText.notificationType.name() + '_' + nText.lang.getId();
			tempMap.put(key, nText.text);
		}
		return Collections.unmodifiableMap(tempMap);
	}
	
	NotificationsText(MusicNotificationType notificationType, Language lang, String text) {
		this.notificationType = notificationType;
		this.lang = lang;
		this.text = text;
	}
	
	public static String getLangNotificationWithParams(MusicNotificationType notificationType, Language lang, Map<String, String> paramValues){
		return getFUPNotificationWithParams(notificationType, lang, paramValues);
	}
	
	private static String getFUPNotificationWithParams(MusicNotificationType notificationType, Language lang, Map<String, String> paramValues) {
		String origText = paramsToTextMap.get(notificationType.name()+ '_' +lang.getId());
		if(StringUtils.isEmpty(origText)) {
			return origText;
		}
		if(paramValues != null) {
			for (String key : paramValues.keySet()) {
				String toBeReplacedText = "${" + key + "}";
				origText = origText.replace(toBeReplacedText, paramValues.get(key));
			}
		}
		return origText;
	}
}
