package com.wynk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WCFVerifyReceiptResponse {
    private WCFVerifyReceiptResponseData data;
    private String message;
    private String rid;
    private boolean success;
}
