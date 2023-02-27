package com.wynk.wcf.dto;

import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllProductsResponse extends ResponseData<AllProductsResponse> {

  List<Product> allProducts;

  public AllProductsResponse(){
    super(AllProductsResponse.class);
  }

  @Data
  public static class Product {
    Integer id;
    Integer hierarchy;
    State state;
    String service;
    String packGroup;
    String title;
    String cpName;
    List<String> eligibleAppIds;
    Map<String, Object> features;
  }
}
