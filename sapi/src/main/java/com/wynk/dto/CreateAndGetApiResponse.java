package com.wynk.dto;

import lombok.Data;

/**
 * @author : Kunal Sharma
 * @since : 25/10/22, Tuesday
 **/

@Data
public class CreateAndGetApiResponse {

    public String uid;
    public int version;
    public String status;
    public String user_name;
}
