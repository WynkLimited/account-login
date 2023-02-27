package com.wynk.dto;

public class DataUsage {

    private String description;

    private int    dataLimit;

    private int    dataComsumed;
    
    private String dataBalance;

    private String percentageConsumed;

    private String errorMessage;
    
    private String validUpto;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDataLimit() {
        return dataLimit;
    }

    public void setDataLimit(int dataLimit) {
        this.dataLimit = dataLimit;
    }

    public int getDataComsumed() {
        return dataComsumed;
    }

    public void setDataComsumed(int dataComsumed) {
        this.dataComsumed = dataComsumed;
    }

    public String getPercentageConsumed() {
        return percentageConsumed;
    }

    public void setPercentageConsumed(String percentageConsumed) {
        this.percentageConsumed = percentageConsumed;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getDataBalance() {
        return dataBalance;
    }

    public void setDataBalance(String dataBalance) {
        this.dataBalance = dataBalance;
    }

    public String getValidUpto() {
        return validUpto;
    }

    public void setValidUpto(String validUpto) {
        this.validUpto = validUpto;
    }

}
