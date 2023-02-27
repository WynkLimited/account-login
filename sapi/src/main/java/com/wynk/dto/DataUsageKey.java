package com.wynk.dto;

import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;

import java.io.Serializable;

@PrimaryKeyClass
public class DataUsageKey implements Serializable {

    private static final long serialVersionUID = 1L;

    @PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String msisdn;

    @PrimaryKeyColumn(ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private long              captureTimestamp;

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public long getCaptureTimestamp() {
        return captureTimestamp;
    }

    public void setCaptureTimestamp(long captureTimestamp) {
        this.captureTimestamp = captureTimestamp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (captureTimestamp ^ (captureTimestamp >>> 32));
        result = prime * result + ((msisdn == null)? 0 : msisdn.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        DataUsageKey other = (DataUsageKey) obj;
        if(captureTimestamp != other.captureTimestamp)
            return false;
        if(msisdn == null) {
            if(other.msisdn != null)
                return false;
        }
        else if(!msisdn.equals(other.msisdn))
            return false;
        return true;
    }
}
