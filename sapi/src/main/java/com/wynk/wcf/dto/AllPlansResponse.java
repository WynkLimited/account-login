package com.wynk.wcf.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllPlansResponse extends ResponseData<AllPlansResponse> {
  List<PlanDTO> plans;

  public AllPlansResponse() {
    super(AllPlansResponse.class);
  }
}
