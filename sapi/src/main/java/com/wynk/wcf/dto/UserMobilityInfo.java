package com.wynk.wcf.dto;

import com.wynk.common.Gender;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserMobilityInfo {
  private String location;
  private String preferredLanguage;
  private String circle;
  private String userType;
  private String errorCode;
  private boolean corporateUser;
  // Needed for YJ since by default it is false and user gets to see free content.
  private boolean dataUser = true;
  private Gender gender;
  private String dataRating;
  private Boolean threeGCapable;
  private Boolean gprsCapable;
  private String imei;
  private String firstName;
  private String middleName;
  private String lastName;
  private String dateOfBirth;
  private String emailID;
  private String alternateContactNumber;
  private String activationDate;
  private String networkTypeLTE;
  private Boolean device4gCapable;
  private String customerType;
  private String customerClassification;
  private String customerCategory;
  private String customerID;
  private String vasDND;
  // non-null in case of PREPAID
  private Long validity;
  private String countryCodeIP;
  private String operator;
  private String countryCodeMcc;
  private boolean isCircleDetectedFromMccMnc;
  private String circleShortName;
}
