package com.wynk.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;

/**
 * @author : Kunal Sharma
 * @since : 21/07/22, Thursday
 **/

@Getter
@Setter
public class BasicUserInfo {
    private String uid;
    private String token;
    private List<String> selectedContentLangs = new ArrayList<>();
    private ConfigDto config;
}