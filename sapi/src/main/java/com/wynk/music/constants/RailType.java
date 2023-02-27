package com.wynk.music.constants;

public enum RailType {

  UNKNOWN(-1,"Unknown","Unknown"),
  FEATURED(1,"Featured","Featured"),
  SINGLES(2,"singles","singles"),
  ALBUM(3,"album","album"),
  PLAYLIST(4,"playlist","playlist"),
  ARTIST(5,"artist","artist"),
  MOOD(6,"mood","mood"),
  CONCERT(7,"concert","concert"),
  CONTEXTUAL(8,"contextual","contextual"),
  RADIO(9,"radio","radio"),
  HERO(10,"hero","hero");

  private int id;
  private String name;
  private String label;

  RailType(int id, String name, String label) {
    this.id = id;
    this.name = name;
    this.label = label;
  }

}
