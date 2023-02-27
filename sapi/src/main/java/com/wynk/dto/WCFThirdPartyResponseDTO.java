package com.wynk.dto;

/**
 * Created by a1vlqlyy on 15/06/17.
 */
public class WCFThirdPartyResponseDTO {

    private Boolean status;
    private WCFTransactionHistory wcfTransactionHistory;

    public WCFThirdPartyResponseDTO(Boolean status, WCFTransactionHistory wcfTransactionHistory) {
        this.status = status;
        this.wcfTransactionHistory = wcfTransactionHistory;
    }

    public Boolean getStatus() {
        return status;
    }

    public WCFTransactionHistory getWcfTransactionHistory() {
        return wcfTransactionHistory;
    }
}
