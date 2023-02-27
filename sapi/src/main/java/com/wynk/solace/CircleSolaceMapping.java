package com.wynk.solace;

import com.wynk.common.Circle;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

//Mapping taken from Circle.java class
public enum CircleSolaceMapping {
    ASSAM(1, "as", "ASSAM", 122, "EAST"),
    NORTHEAST(2, "ne", "NORTHEAST", 123, "EAST"),
    ORISSA(3, "or", "ORISSA", 120, "EAST"),
    WESTBENGAL(4, "wb", "WESTBENGAL", 118, "EAST"),
    KOLKATA(5, "ko", "KOLKATA", 115, "EAST"),
    BIHAR(6, "bh", "BIHAR", 119, "EAST"),
    HIMACHALPRADESH(7, "hp", "HIMACHALPRADESH", 101, "NORTH"),
    DELHI(8, "dl", "DELHI", 102, "NORTH"),
    PUNJAB(9, "pb", "PUNJAB", 107, "NORTH"),
    HARYANA(10, "hr", "HARYANA", 108, "NORTH"),
    JAMMUKASHMIR(11, "jk", "JAMMUKASHMIR", 117, "NORTH"),
    UPWEST(12, "uw", "UPWEST", 110, "NORTH"),
    UPEAST(13, "ue", "UPEAST", 116, "NORTH"),
    KERALA(14, "kl", "KERALA", 113, "SOUTH"),
    CHENNAI(15, "ch", "CHENNAI", 103, "SOUTH"),
    TAMILNADU(16, "tn", "TAMILNADU", 114, "SOUTH"),
    ANDHRAPRADESH(17, "ap", "ANDHRAPRADESH", 104, "SOUTH"),
    KARNATAKA(18, "ka", "KARNATAKA", 105, "SOUTH"),
    MADHYAPRADESH(19, "mp", "MADHYAPRADESH", 106, "WEST"),
    GUJARAT(20, "gj", "GUJARAT", 111, "WEST"),
    RAJASTHAN(21, "rj", "RAJASTHAN", 121, "WEST"),
    MUMBAI(22, "mb", "MUMBAI", 109, "WEST"),
    MAHARASHTRA(23, "mh", "MAHARASHTRA", 112, "WEST"),
    UNKNOWN(-1, "UNKNOWN", "UNKNOWN", -1, "UNKNOWN");

    private int catalogId;
    private int circleId;
    private String circleName;
    private String circleCode;
    private String solaceTopic;

    private static Map<String, CircleSolaceMapping> circleNameMapping = new HashMap<>();

    static {
        for (CircleSolaceMapping c : CircleSolaceMapping.values()) {
            circleNameMapping.put(c.circleCode, c);
        }
    }

    CircleSolaceMapping(int catalogId, String circleCode, String circleName, int circleId, String solaceTopic) {
        this.catalogId = catalogId;
        this.circleName = circleName;
        this.circleId = circleId;
        this.circleCode = circleCode;
        this.solaceTopic = solaceTopic;
    }

    public static CircleSolaceMapping getCircleMappingByName(String circleCode) {
        CircleSolaceMapping circleMapper = CircleSolaceMapping.UNKNOWN;
        if (StringUtils.isNotBlank(circleCode)) {
            circleMapper = circleNameMapping.get(circleCode);
        }
        return circleMapper;
    }

    public int getCatalogId() {
        return catalogId;
    }

    public int getCircleId() {
        return circleId;
    }

    public String getCircleName() {
        return circleName;
    }

    public String getCircleCode() {
        return circleCode;
    }

    public String getSolaceTopic() {
        return solaceTopic;
    }

    public static Map<String, CircleSolaceMapping> getCircleNameMapping() {
        return circleNameMapping;
    }

}
