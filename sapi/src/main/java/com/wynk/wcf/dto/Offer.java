package com.wynk.wcf.dto;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

@Data
public class Offer {

  private int id;
  private String description;
  private int hierarchy;
  private Messages messages;
  private Map<String, String> products;
  private String packGroup;
  private List<Integer> plans;
  private ProvisionType provisionType;
  private String ruleExpression;
  private String service;
  private String title;
  private String subtitle;
  private String comboHeader;
  private String colorCode;
  private String startTitle;
  private Map<String, Object> meta;
  private State state;
  private boolean paid;
  private List<OfferDesc> descriptions;

  public boolean isUnlimitedDownloads() {
    return check(OfferDescTypes.UNLIMITED_DOWNLOADS);
  }

  public boolean isUnlimitedStreaming() {
    return check(OfferDescTypes.UNLIMITED_STREAMING);
  }

  public boolean isUnlimitedHT() {
    return check(OfferDescTypes.UNLIMITED_HT);
  }

  public boolean isAdsFree() {
    return check(OfferDescTypes.NO_ADS);
  }

  public boolean isHellotunes() { return check(OfferDescTypes.HELLOTUNES); }


  private boolean check(OfferDescTypes type) {
    if (CollectionUtils.isEmpty(descriptions)) return false;
    for (OfferDesc curr : descriptions) {
      if (Objects.nonNull(curr) && Objects.nonNull(curr.desc) && curr.desc.equalsIgnoreCase(type.name)) return true;
    }
    return false;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class OfferDesc {
    private String desc;
    private boolean value;
  }

  @AllArgsConstructor
  @Getter
  public enum OfferDescTypes {
    UNLIMITED_STREAMING("Unlimited Streaming"),
    UNLIMITED_HT("Unlimited Hellotunes"),
    UNLIMITED_DOWNLOADS("Unlimited Downloads"),
    NO_ADS("No Ads"),
    HELLOTUNES("HELLOTUNES");
    private String name;
  }
}
