package com.wynk.common;

/**
 * Created by Aakash on 09/08/17.
 */
public enum OriginPrefix {

    SECURE_BUCKET("wch4","wcm3","http://s3-ap-southeast-1.amazonaws.com/"),
    UNSECURE_BUCKET("bsbcms","bsbcms","http://s3-ap-southeast-1.amazonaws.com/");

    private String mp4BucketName;
    private String mp3BucketName;
    private String domainUrl;


    OriginPrefix(String mp4BucketName, String mp3BucketName, String domainUrl) {
        this.mp4BucketName = mp4BucketName;
        this.mp3BucketName = mp3BucketName;
        this.domainUrl = domainUrl;
    }

    public String getMp4BucketName() {
        return mp4BucketName;
    }

    public String getMp3BucketName() {
        return mp3BucketName;
    }

    public String getDomainUrl() {
        return domainUrl;
    }
}
