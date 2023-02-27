package com.wynk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WCFWebViewResponse {
    private WCFWebViewResponseData data;
    private String message;
    private String rid;
    private boolean success;
}
