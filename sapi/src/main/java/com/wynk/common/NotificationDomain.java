package com.wynk.common;

import java.util.HashMap;
import java.util.Map;

public enum NotificationDomain {
    QRIOUS("qrious"),
    MUSICAPP("musicapp");
    
    private static Map<String, NotificationDomain> domainNameMapping = new HashMap<String, NotificationDomain>();
    static {
        for (NotificationDomain domain: NotificationDomain.values()) {
            domainNameMapping.put(domain.getDomain().toLowerCase(), domain);
        }
    }
    private String domain;
    private NotificationDomain(String domain) {
        this.domain = domain;
    }
    
    public static NotificationDomain getNotificationDomain(String domain) {
        return domainNameMapping.get(domain.toLowerCase());
    }
    
    public String getDomain() {
        return this.domain.toLowerCase();
    }
}
