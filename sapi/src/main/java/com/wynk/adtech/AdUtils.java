package com.wynk.adtech;

import com.wynk.wcf.dto.Feature;
import com.wynk.wcf.dto.UserSubscription;

public class AdUtils {

	public static Boolean isPremiumAccount(UserSubscription userSubscription, Feature feature){
		Boolean isPremiumAccount = Boolean.FALSE;
        if(userSubscription != null && feature.isSubscribed()){
            isPremiumAccount = Boolean.TRUE;
        }
        return isPremiumAccount;
	}
	
	
	public static String getSubType(Feature hideAds) {
	    if (hideAds.isSubscribed()) {
	        return AdSubscriptionType.PAID.name().toLowerCase();
        }
        return AdSubscriptionType.BUNDLED.name().toLowerCase();
	}
}