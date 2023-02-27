package com.wynk.notification;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum NotificationReceiver {
    MAU("mau",2),
    ALLUSERS("allusers",3),
    UNR("unr",4),
    CUSTOM("custom",1);
    
    private static Map<String, NotificationReceiver> notificationReceiverMapping = new HashMap<String, NotificationReceiver>();
    static {
        for (NotificationReceiver receiver: NotificationReceiver.values()) {
        	notificationReceiverMapping.put(receiver.getReceiver().toLowerCase(), receiver);
        }
    }
    
    private String receiver;
    private int priority;
    
    private NotificationReceiver(String receiver, int priority) {
        this.receiver = receiver;
        this.priority = priority;
    }
    
    public static NotificationReceiver getNotificationReceiver(String receiver) {
    	
    	if(StringUtils.isEmpty(receiver)) {
            return NotificationReceiver.MAU;
        }
    	
    	NotificationReceiver value=notificationReceiverMapping.get(receiver.toLowerCase());
    	if(value == null) {
    		value = NotificationReceiver.MAU;
    	}
        return value;
    }
    
    
    public String getReceiver() {
        return this.receiver.toLowerCase();
    }

	public int getPriority() {
		return priority;
	}

}
