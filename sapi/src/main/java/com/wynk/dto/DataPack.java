package com.wynk.dto;


public class DataPack {

    private String packDataLimit;

    private int    packPrice;

    private String packValidity;

    private String keyword;

    private String packType;
    
    private String title;
    
    private String thumbnailUrl;
    
    private String purchaseUrl;

    public String getPackDataLimit() {
        return packDataLimit;
    }

    public void setPackDataLimit(String packDataLimit) {
        this.packDataLimit = packDataLimit;
    }

    public int getPackPrice() {
        return packPrice;
    }

    public void setPackPrice(int packPrice) {
        this.packPrice = packPrice;
    }

    public String getPackValidity() {
        return packValidity;
    }

    public void setPackValidity(String packValidity) {
        this.packValidity = packValidity;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getPackType() {
        return packType;
    }

    public void setPackType(String packType) {
        this.packType = packType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getPurchaseUrl() {
        return purchaseUrl;
    }

    public void setPurchaseUrl(String purchaseUrl) {
        this.purchaseUrl = purchaseUrl;
    }



}
