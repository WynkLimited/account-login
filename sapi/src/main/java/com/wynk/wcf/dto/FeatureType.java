package com.wynk.wcf.dto;

import lombok.Getter;

@Getter
public enum FeatureType {
  HELLOTUNES("hellotunes"),
  DOWNLOADS("downloads"),
  SHOW_ADS("showAds"),
  STREAMING("streams"),
  LYRICS("lyrics"),
  UNSUBSCRIBED("unsubscribed"),
  HELLOTUNES_TRIAL("ht_trial");

  private String name;

  FeatureType(String featureName) {
    this.name = featureName;
  }
}
