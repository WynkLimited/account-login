package com.wynk.newcode.user.core.entity;

import java.util.Date;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

@Table(value = "user_recent_songs")
public class RecentSong implements Comparable<RecentSong> {

  @PrimaryKeyColumn(name = "uid", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
  private String uid;

  @PrimaryKeyColumn(name = "songid", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
  private String songId;

  @Column(value = "updated_on")
  private Date updatedOn;

  @Column(value = "visibility")
  boolean visible;

  @Column(value = "dpc")
  float decayedPlayCount;

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getSongId() {
    return songId;
  }

  public void setSongId(String songId) {
    this.songId = songId;
  }

  public Date getUpdatedOn() {
    return updatedOn;
  }

  public void setUpdatedOn(Date updatedOn) {
    this.updatedOn = updatedOn;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public float getDecayedPlayCount() {
    return decayedPlayCount;
  }

  public void setDecayedPlayCount(float decayedPlayCount) {
    this.decayedPlayCount = decayedPlayCount;
  }

  public RecentSong(
      String uid, String songId, Date updatedOn, boolean visible, float decayedPlayCount) {
    this.uid = uid;
    this.songId = songId;
    this.updatedOn = updatedOn;
    this.visible = visible;
    this.decayedPlayCount = decayedPlayCount;
  }

  @Override
  public int compareTo(RecentSong o) {
    // This ensures that sorting gives most recent songs first
    return o.getUpdatedOn()
        .compareTo(getUpdatedOn());
  }

}
