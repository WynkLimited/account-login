package com.wynk.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import com.wynk.music.dto.GeoLocation;
import com.wynk.music.dto.PaymentDetails;
import com.wynk.utils.JsonUtils;
import lombok.Data;

import java.util.Map;

/**
 * @author : Kunal Sharma
 * @since : 25/09/22, Sunday
 **/

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRequest {


    private String paymentCode;
    private Map<Object, Object> appDetails;
    private PageDetailsDto pageDetails = new PageDetailsDto();
    private UserDetailsDto userDetails;
    private GeoLocation geoLocation;
    private PaymentDetails paymentDetails;
    private ProductDetailDto productDetails;
    private SessionDetailsDto sessionDetails = new SessionDetailsDto();


    public static PaymentRequest createPaymentRequest(String requestBodyInJson) {
        PaymentRequest paymentRequest = JsonUtils.GSON.fromJson(requestBodyInJson, PaymentRequest.class);
        ProductDetailDto productDetailsDto = JsonUtils.GSON.fromJson(requestBodyInJson, ProductDetailDto.class);
        PaymentDetails.Payment paymentDetails = JsonUtils.GSON.fromJson(requestBodyInJson, PaymentDetails.Payment.class);
        SessionDetailsDto sessionDetailsDto = JsonUtils.GSON.fromJson(requestBodyInJson, SessionDetailsDto.class);
        paymentRequest.setSessionDetails(sessionDetailsDto);
        paymentRequest.setProductDetails(productDetailsDto);
        paymentRequest.setPaymentDetails(paymentDetails.payment);
        return paymentRequest;
    }

    public String toJson() {
        return JsonUtils.GSON.toJson(this);
    }

}
