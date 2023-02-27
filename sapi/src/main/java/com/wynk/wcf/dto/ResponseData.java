package com.wynk.wcf.dto;

import com.google.gson.Gson;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseData<T> {

  private boolean success;
  private String rid;
  private transient Class<T> type;
  T data;

  public ResponseData(Class<T> type) {
    this.type = type;
  }

  public T fromJson(String json) {
    return new Gson().fromJson(json, type);
  }
}
