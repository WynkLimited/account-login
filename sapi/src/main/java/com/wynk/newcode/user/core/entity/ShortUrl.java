package com.wynk.newcode.user.core.entity;

import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

@Table("shorturl")
public class ShortUrl {

  @PrimaryKeyColumn(name = "content_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
  private String contentId;

  @PrimaryKeyColumn(name = "type", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
  private String type;

  @Column(value = "short_url")
  private String shortUrl;

  public String getContentId() {
    return contentId;
  }

  public ShortUrl(String contentId, String type, String shortUrl) {
    this.contentId = contentId;
    this.type = type;
    this.shortUrl = shortUrl;
  }

  public void setContentId(String contentId) {
    this.contentId = contentId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getShortUrl() {
    return shortUrl;
  }

  public void setShortUrl(String shortUrl) {
    this.shortUrl = shortUrl;
  }

  @Override
  public String toString() {
    return "ShortUrl{" +
            "contentId='" + contentId + '\'' +
            ", type='" + type + '\'' +
            ", shortUrl='" + shortUrl + '\'' +
            '}';
  }
}
