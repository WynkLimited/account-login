package com.wynk.music.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @author : Kunal Sharma
 * @since : 25/09/22, Sunday
 **/


@Data
@ToString
public class GeoLocation {

    String countryCode;
    String stateCode;
    String ip;

    String state;

    String accessCountryCode;

    public GeoLocation() {
    }

    public GeoLocation(String countryCode) {
        this.countryCode = countryCode;
    }

    public GeoLocation _setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public GeoLocation _setStateCode(String stateCode) {
        this.stateCode = stateCode;
        return this;
    }

    public GeoLocation _setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public GeoLocation _setAccessCountryCode(String accessCountryCode) {
        this.accessCountryCode = accessCountryCode;
        return this;
    }

}
