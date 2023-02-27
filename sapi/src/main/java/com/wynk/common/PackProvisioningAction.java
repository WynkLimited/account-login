package com.wynk.common;

import org.apache.commons.lang3.StringUtils;

public enum PackProvisioningAction {
    ACT("act"), DEACT("deact"), FORCE_DEACT("force_deact"), UNKNOWN("unknown");

    private String value;

    PackProvisioningAction(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    static public PackProvisioningAction fromValue(String value) {
        PackProvisioningAction action = UNKNOWN;
        for(PackProvisioningAction proAction : PackProvisioningAction.values()) {
            if(StringUtils.equalsIgnoreCase(proAction.getValue(), value)) {
                action = proAction;
                break;
            }
        }
        return action;
    }
}
