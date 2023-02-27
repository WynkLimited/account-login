package com.wynk.constants;

public enum NotificationSubType {
  PLAYLIST_FOLLOW(1),
  ARTIST_FOLLOW(2),
  USER_PLAYLIST_FOLLOW(3),
  NEW_USER_PLAYLIST_FOLLOWER(10),
  CONTENT(4),
  UNKNOWN(100);

  private int value;

  NotificationSubType(int v){
    this.value = v;
  }
  public static NotificationSubType getNoitficationSubType(int value){
    switch (value){
      case 1:
        return PLAYLIST_FOLLOW;
      case 2:
        return ARTIST_FOLLOW;
      case 3:
        return USER_PLAYLIST_FOLLOW;
      case 4:
        return CONTENT;
      case 10:
        return NEW_USER_PLAYLIST_FOLLOWER;
      default:
        return UNKNOWN;
    }
  }
  public int getValue(){
    return this.value;
  }
}
