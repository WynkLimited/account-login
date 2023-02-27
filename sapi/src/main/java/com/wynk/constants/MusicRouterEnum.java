package com.wynk.constants;

/**
 * Created by Aakash on 14/06/17.
 */
public enum MusicRouterEnum {

    AKAMAI("akamai"),CLOUDFRONT("cloudfront");

    MusicRouterEnum(String musicRouterService) {
        this.musicRouterService = musicRouterService;
    }

    private String musicRouterService;

    public String getMusicRouterService() {
        return musicRouterService;
    }
}
