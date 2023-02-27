package com.wynk.dto;

import com.wynk.constants.WCFChannelEnum;
import com.wynk.user.dto.User;
import com.wynk.user.dto.UserDevice;

import com.wynk.wcf.dto.UserMobilityInfo;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginCreateAccountDTO {

  private User appUser;
  private Boolean isSimulator;
  private Boolean updatePlayList;
  private UserDevice currentDevice;
  private List<UserDevice> userDeviceList;
  private Boolean updateUserinCache;
  private String uuid;
  private String msisdn;
  private String applang;
  private WCFChannelEnum wcfRegistrationChannel;
  private UserMobilityInfo userMobilityInfo;
}
