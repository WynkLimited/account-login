package com.wynk.dto;

import org.json.simple.JSONObject;

/**
 * 
 * Class for uploading recently updated data on amazon s3 server.
 */
public class AmazonS3Publisher {

    private String price;
    private String categoryId;
    private String id;
    private String publisher;
    private String title;
    private long lastUpdated;
    
    public long getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AmazonS3Publisher(String price, String categoryId, String id, String publisher, String title) {
        super();
        this.price = price;
        this.categoryId = categoryId;
        this.id = id;
        this.publisher = publisher;
        this.title = title;
    }
    
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        
        obj.put("content_id", id.replaceAll(",", ""));
        obj.put("title", title.replaceAll(",", ""));
        obj.put("category", categoryId.replaceAll(",", ""));
        obj.put("publisher", publisher.replaceAll(",", ""));
        obj.put("price", price);
        
        obj.put("lastUpdated", lastUpdated);
        return obj;
    }

}
