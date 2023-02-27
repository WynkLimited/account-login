package com.wynk.dto;

/**
 * Created by Aakash on 17/01/17.
 */
public enum DefaultPackType {

    IOS_RECOMMENDED("IOSRecommend"),ANDROID_RECOMMENDED("AndroidRecommend"),SL_ANDROID_RECOMMENDED("SLAndroidRecommend");

    private String packType;

    DefaultPackType(String packType) {
        this.packType = packType;
    }

    public String getPackType() {
        return packType;
    }
}
