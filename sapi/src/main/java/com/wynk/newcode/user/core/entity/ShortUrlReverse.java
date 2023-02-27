package com.wynk.newcode.user.core.entity;

import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

@Table("shorturl_rev")
public class ShortUrlReverse {

  @PrimaryKeyColumn(name = "short_url", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
  private String shortUrl;

  @Column(value = "content_id")
  private String contentId;

  @Column(value = "type")
  private String type;

  public String getShortUrl() {
    return shortUrl;
  }

  public void setShortUrl(String shortUrl) {
    this.shortUrl = shortUrl;
  }

  public String getContentId() {
    return contentId;
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

  public ShortUrlReverse(String shortUrl, String contentId, String type) {
    this.shortUrl = shortUrl;
    this.contentId = contentId;
    this.type = type;
  }

  public ShortUrlReverse() {
  }

  @Override
  public String toString() {
    return "ShortUrlReverse{"
        + "shortUrl='"
        + shortUrl
        + '\''
        + ", contentId='"
        + contentId
        + '\''
        + ", type='"
        + type
        + '\''
        + '}';
  }
}
