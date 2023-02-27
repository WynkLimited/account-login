package com.wynk.utils;

import com.wynk.constants.MusicConstants;
import com.wynk.server.ChannelContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class UserUtils {
  private static final Logger logger = LoggerFactory.getLogger(UserUtils.class);
  public static String getUidFromRequest(HttpRequest request){
    try{
      String utoken = request.headers().get("x-bsy-utkn");
      return utoken.split(":")[0];
    }catch (Exception e){
      logger.error("Error while fetching uid");
      return null;
    }
  }

  public static String getHeaderValue(HttpHeaders headers, String key) {
    String headerValue = null;
    if (StringUtils.isNotBlank(key) && Objects.nonNull(headers)) {
      headerValue = headers.get(key);
    }
    return headerValue;
  }

  public static boolean isRequestFromWeb() {
    logger.info("Checking is Request from Web");
    String isWeb = UserUtils.getHeaderValue(ChannelContext.getRequest().headers(), MusicConstants.MUSIC_HEADER_IS_WAP);
    return (isWeb != null && isWeb.equalsIgnoreCase(Boolean.TRUE.toString()));
  }
}
