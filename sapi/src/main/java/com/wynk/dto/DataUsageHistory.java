package com.wynk.dto;

import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

@Table(value = "dataUsageHistory")
public class DataUsageHistory {

    @PrimaryKey
    private DataUsageKey key;

    private String uid;

    private String description;

    private int          dataLimit;

    private int          dataConsumed;

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

    public int getDataConsumed() {
        return dataConsumed;
    }

    public void setDataConsumed(int dataConsumed) {
        this.dataConsumed = dataConsumed;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public DataUsageKey getKey() {
        return key;
    }

    public void setKey(DataUsageKey key) {
        this.key = key;
    }

    public static DataUsageHistory newDataUsageSnapshot(DataUsage dataUsage, String msisdn) {
        DataUsageHistory snapshot = new DataUsageHistory();
        snapshot.dataBalance = dataUsage.getDataBalance();
        snapshot.dataConsumed = dataUsage.getDataComsumed();
        snapshot.dataLimit = dataUsage.getDataLimit();
        snapshot.description = dataUsage.getDescription();
        snapshot.errorMessage = dataUsage.getErrorMessage();
        snapshot.percentageConsumed = dataUsage.getPercentageConsumed();
        snapshot.validUpto = dataUsage.getValidUpto();
        DataUsageKey key = new DataUsageKey();
        key.setMsisdn(msisdn);
        key.setCaptureTimestamp(System.currentTimeMillis());
        snapshot.key = key;
        return snapshot;
    }

    public static DataUsage toDataUsage(DataUsageHistory snapshot) {
        DataUsage dataUsage = new DataUsage();
        if(null != snapshot) {
            dataUsage.setDataBalance(snapshot.dataBalance);
            dataUsage.setDataComsumed(snapshot.dataConsumed);
            dataUsage.setDataLimit(snapshot.dataLimit);
            dataUsage.setDescription(snapshot.description);
            dataUsage.setErrorMessage(snapshot.errorMessage);
            dataUsage.setPercentageConsumed(snapshot.percentageConsumed);
            dataUsage.setValidUpto(snapshot.validUpto);
        }
        return dataUsage;
    }

}
