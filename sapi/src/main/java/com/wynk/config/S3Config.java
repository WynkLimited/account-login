package com.wynk.config;

/**
 * Created with IntelliJ IDEA. User: bhuvangupta Date: 02/01/13 Time: 12:38 AM To change this
 * template use File | Settings | File Templates.
 */
public class S3Config {

    private String awsAccessKeyId;
    private String awsSecretKey;
    private String photoBucketName;
    private String newsImageBucketName;
    private String videoBucketName;
    private String textImageBucketName;
    private String imageBucketName;
    private String imageBaseUrl;
    private String avatarImageBucketName;
    private String rdLogsBucketName;
    
    public String getImageBaseUrl() {
        return imageBaseUrl;
    }
    
    public void setImageBaseUrl(String imageBaseUrl) {
        this.imageBaseUrl = imageBaseUrl;
    }

    public String getAwsAccessKeyId() {
        return awsAccessKeyId;
    }

    public void setAwsAccessKeyId(String awsAccessKeyId) {
        this.awsAccessKeyId = awsAccessKeyId;
    }

    public String getAwsSecretKey() {
        return awsSecretKey;
    }

    public void setAwsSecretKey(String awsSecretKey) {
        this.awsSecretKey = awsSecretKey;
    }

    public String getPhotoBucketName() {
        return photoBucketName;
    }

    public void setPhotoBucketName(String photoBucketName) {
        this.photoBucketName = photoBucketName;
    }

    public String getNewsImageBucketName() {
        return newsImageBucketName;
    }

    public void setNewsImageBucketName(String newsImageBucketName) {
        this.newsImageBucketName = newsImageBucketName;
    }

    public String getVideoBucketName() {
        return videoBucketName;
    }

    public void setVideoBucketName(String videoBucketName) {
        this.videoBucketName = videoBucketName;
    }

    public String getTextImageBucketName() {
        return textImageBucketName;
    }

    public void setAvatarImageBucketName(String avatarImageBucketName) {
        this.avatarImageBucketName = avatarImageBucketName;
    }

    public String getAvatarImageBucketName() {
        return avatarImageBucketName;
    }

    public void setTextImageBucketName(String textImageBucketName) {
        this.textImageBucketName = textImageBucketName;
    }
    
    public String getImageBucketName() {
        return imageBucketName;
    }

    public void setImageBucketName(String imageBucketName) {
        this.imageBucketName = imageBucketName;
    }    

    public String getRdLogsBucketName() {
        return rdLogsBucketName;
    }
    
    public void setRdLogsBucketName(String rdLogsBucketName) {
        this.rdLogsBucketName = rdLogsBucketName;
    }

}
