package com.wynk.dto;


public class Device {

    private String name;
    
    private String brandName;
    
    private String modelName;
    
    private String resolutionWidth;
    
    private String resolutionHeight;
    
    private String[] groupName;
    
    private String streamingProtocol;

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public String getBrandName() {
        return brandName;
    }

    
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    
    public String getModelName() {
        return modelName;
    }

    
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    
    public String getResolutionWidth() {
        return resolutionWidth;
    }

    
    public void setResolutionWidth(String resolutionWidth) {
        this.resolutionWidth = resolutionWidth;
    }

    
    public String getResolutionHeight() {
        return resolutionHeight;
    }

    
    public void setResolutionHeight(String resolutionHeight) {
        this.resolutionHeight = resolutionHeight;
    }

    
    public String[] getGroupName() {
        return groupName;
    }

    
    public void setGroupName(String[] groupName) {
        this.groupName = groupName;
    }

    
    public String getStreamingProtocol() {
        return streamingProtocol;
    }

    
    public void setStreamingProtocol(String streamingProtocol) {
        this.streamingProtocol = streamingProtocol;
    }
}
