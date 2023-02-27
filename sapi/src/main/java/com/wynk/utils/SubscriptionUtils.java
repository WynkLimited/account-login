package com.wynk.utils;

import static com.wynk.constants.MusicSubscriptionPackConstants.DAY;
import static com.wynk.constants.MusicSubscriptionPackConstants.PRE_REMINDER_PERIOD;

import java.text.SimpleDateFormat;

import com.wynk.dto.MusicSubscriptionState;

public class SubscriptionUtils {

	 public static MusicSubscriptionState getSubscriptionState(long expiryTimestamp) {
	        long curTime = System.currentTimeMillis();
	        long days = (expiryTimestamp - curTime) / DAY;
	        if(curTime > expiryTimestamp) {
	            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
	            if(fmt.format(curTime).equals(fmt.format(expiryTimestamp))){
	                return MusicSubscriptionState.SUBSCRIBED_IN_REMINDER;
	            }
	            else {
	                return MusicSubscriptionState.SUSPENDED;
	            }
	        }
	        else if(days < PRE_REMINDER_PERIOD) {
	            return MusicSubscriptionState.SUBSCRIBED_IN_REMINDER;
	        }
	        else {
	            return MusicSubscriptionState.SUBSCRIBED_PRE_REMINDER;
	        }
	 }
	 
}
