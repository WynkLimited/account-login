package com.wynk.music;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by a1vlqlyy on 16/02/17.
 */
public enum WCFServiceType {
    WYNK_MUSIC("music"),
    SAMSUNG_MUSIC("samsungMusic");

    WCFServiceType(String serviceName) {
        this.serviceName = serviceName;
    }

    private String serviceName;

    public String getServiceName() {
        return serviceName;
    }

    public static WCFServiceType getWCFServiceType(String wcfService){

        if(StringUtils.isNotBlank(wcfService) && wcfService.equalsIgnoreCase(SAMSUNG_MUSIC.getServiceName())){
            return SAMSUNG_MUSIC;
        }
        return WYNK_MUSIC;
    }

}
