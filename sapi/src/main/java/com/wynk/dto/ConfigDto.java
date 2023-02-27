package com.wynk.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author : Kunal Sharma
 * @since : 28/07/22, Thursday
 **/

@Getter
@Setter
public class ConfigDto {
    private Boolean internationalRoaming;
    private Geo geo;

    @Getter
    @Setter
    public static class Description {
        private Integer code;
        private Boolean success;
        private String line2;
    }

    @Getter
    @Setter
    public static class Geo {
        private Boolean isGeoRestrictionPassed;
        private Description description;
    }


}
