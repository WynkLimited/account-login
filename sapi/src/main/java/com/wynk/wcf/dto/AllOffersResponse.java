package com.wynk.wcf.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllOffersResponse extends ResponseData<AllOffersResponse> {

  List<Offer> allOffers;

  public AllOffersResponse() {
    super(AllOffersResponse.class);
  }
}
