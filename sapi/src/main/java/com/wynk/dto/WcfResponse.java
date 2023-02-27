package com.wynk.dto;
/**
 * @author : Kunal Sharma
 * @since : 06/10/22, Thursday
 **/


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class WcfResponse<T> {

    private String rid;
    private T data;
    private boolean success;
}