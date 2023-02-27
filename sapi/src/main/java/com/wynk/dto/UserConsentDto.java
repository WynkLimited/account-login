package com.wynk.dto;

import lombok.Data;

/**
 * @author : Kunal Sharma
 * @since : 11/07/22, Monday
 **/

@Data
public class UserConsentDto {
    private Long consentTimestamp;
    private String action;
    private String reason;
    private UserDetailsDto userDetails;
    private AppDetailsDto appDetails;
    private String user_name;
    private String service;

    public UserConsentDto _setUserName(String user_name) {
        this.user_name = user_name;
        return this;
    }

    public UserConsentDto _setService(String service) {
        this.service = service;
        return this;
    }
}
