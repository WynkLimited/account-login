package com.wynk.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dhruva
 * Date: 12/08/14
 * Time: 12:14 AM
 * To change this template use File | Settings | File Templates.
 */
public enum CircleType {
    TWO_G("2G"), THREE_G("3G"), ICR("ICR");

    private CircleType(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIcr() {
        if (this == ICR) {
            return true;
        }
        return false;
    }

    public boolean is3G() {
        if (this == THREE_G) {
            return true;
        }
        return false;
    }

    public boolean is2G() {
        if (this == TWO_G) {
            return true;
        }
        return false;
    }

    private static Map<String, CircleType> nameToTypeMap = new HashMap<String, CircleType>();

    static {
        for (CircleType circleType : CircleType.values()) {
            nameToTypeMap.put(circleType.getName().toLowerCase(), circleType);
        }
    }

    public static Map<String, CircleType> getNameToTypeMap() {
        return nameToTypeMap;
    }
}
