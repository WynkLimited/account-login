package com.wynk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Kunal Sharma
 * @since : 30/09/22, Friday
 **/

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailDto {
     public Long planId;
     public String skuId;
}
