package com.wynk.wcf.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DeviceType {
  PHONE("phone"),
  TABLET("TABLET"),
  TV("TV"),
  STB("STB"),
  STICK("STICK"),
  BROWSER("BROWSER");
  public final String name;
}
