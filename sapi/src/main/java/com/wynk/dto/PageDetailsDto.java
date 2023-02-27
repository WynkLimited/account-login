package com.wynk.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author : Kunal Sharma
 * @since : 01/10/22, Saturday
 **/

@AllArgsConstructor
@NoArgsConstructor
public class PageDetailsDto {

    private String successPageUrl = "";
    private String failurePageUrl = "";
    private String pendingPageUrl = "";
    private String unknownPageUrl = "";
}