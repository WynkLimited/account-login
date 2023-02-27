package com.wynk.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : Kunal Sharma
 * @since : 25/10/22, Tuesday
 **/

@Getter
@Setter
@ToString
public class WcfApiResponseDTO<T> {

    public boolean success;
    public String rid;
    public T data;

}
