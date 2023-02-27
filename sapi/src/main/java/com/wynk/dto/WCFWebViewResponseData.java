package com.wynk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WCFWebViewResponseData {
    private String redirectUrl;
    private String sid;
}
