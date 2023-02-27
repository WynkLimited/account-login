package com.wynk.wcf.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class SubscriptionStatusResponse {

    private boolean success;
    private String rid;
    private List<SubscriptionStatus> data;
}
