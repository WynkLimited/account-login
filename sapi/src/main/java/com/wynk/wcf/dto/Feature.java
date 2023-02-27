package com.wynk.wcf.dto;

import lombok.Data;

@Data
public class Feature {
  private int productId = 0;
  private int offerId = 0;
  private long validTill = 0l;
  private boolean isSubscribed = false;
  private String name = "";
  private Double streamCount = -1.0;
  private int price = 0;
  private int validity = 0;

  public Feature(FeatureType featureType){
    this.name = featureType.getName();
  }

}
