package com.wynk.music.dto;

import java.util.Map;

public class Accreditation {
  private Map<String, String> submittedBy;
  private Map<String, String> publisher;
  private Map<String, String> songWriter;
  private Map<String, String> org;

  public Accreditation() {
  }

  public Map<String, String> getSubmittedBy() {
    return submittedBy;
  }

  public void setSubmittedBy(Map<String, String> submittedBy) {
    this.submittedBy = submittedBy;
  }

  public Map<String, String> getPublisher() {
    return publisher;
  }

  public void setPublisher(Map<String, String> publisher) {
    this.publisher = publisher;
  }

  public Map<String, String> getSongWriter() {
    return songWriter;
  }

  public void setSongWriter(Map<String, String> songWriter) {
    this.songWriter = songWriter;
  }

  public Map<String, String> getOrg() {
    return org;
  }

  public void setOrg(Map<String, String> org) {
    this.org = org;
  }
}
