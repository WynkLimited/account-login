package com.wynk.wcf.dto;

import com.wynk.constants.WCFChannelEnum;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MsisdnIdentificationResponse extends ResponseData<MsisdnIdentificationResponse> {
  private String msisdn;
  private OperatorDetails operatorDetails;
  private WCFChannelEnum channel;

  @Setter
  @Getter
  public class OperatorDetails {
    private String msisdn;
    private UserMobilityInfo ndsUserInfo;
  }

  public MsisdnIdentificationResponse() {
    super(MsisdnIdentificationResponse.class);
  }
}
