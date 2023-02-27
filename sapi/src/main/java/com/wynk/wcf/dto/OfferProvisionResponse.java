package com.wynk.wcf.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferProvisionResponse extends ResponseData<OfferProvisionResponse> {

  private Userdata userdata;

  private List<SubscriptionStatus> subscriptionStatus;

  private List<OfferStatus> offerStatus;

  public OfferProvisionResponse() {
    super(OfferProvisionResponse.class);
  }
}
