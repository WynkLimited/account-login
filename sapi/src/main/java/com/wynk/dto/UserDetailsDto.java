package com.wynk.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author : Kunal Sharma
 * @since : 11/07/22, Monday
 **/

@Data
@Builder
public class UserDetailsDto {

    private String uid;
    private String username;
    private String msisdn;
}
