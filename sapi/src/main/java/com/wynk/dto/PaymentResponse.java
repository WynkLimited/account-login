package com.wynk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author : Kunal Sharma
 * @since : 06/10/22, Thursday
 **/

@Data
@Builder
public class PaymentResponse {
    public Long timestamp;
    public Payload payload;



    @Data
    @AllArgsConstructor
    public static class Payload {
        public String url;
        public boolean success;
    }

}