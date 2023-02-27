package com.wynk.auth;

import com.wynk.db.ShardedRedisServiceManager;
import com.wynk.music.dto.MusicPlatformType;
import com.wynk.server.HttpResponseService;
import com.wynk.utils.EncryptUtils;
import com.wynk.utils.UserDeviceUtils;
import com.wynk.utils.Utils;
import io.netty.handler.codec.http.HttpResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Auth {

  static final String secretSequence = "knywmzap";
  static final String inserted = "OK";
  static final String imagePath = "resources/images/";
  static final char[] headerSequence = {'a', 'w', 'k', 'q', 'p', 'n', 'm', 'z', 'y', 'c'};
  static RandomAccessFile imgFirst;
  static RandomAccessFile imgSecond;
  static byte[] imgFirstB;
  static byte[] imgSecondB;

  @Value("${music.wap.device.id.len}")
  private int didLength;

  @Value("${music.auth.ttl}")
  private int cacheTtl;

  @Autowired private ShardedRedisServiceManager musicUserShardRedisServiceManager;
  private Logger logger = LoggerFactory.getLogger(getClass().getCanonicalName());

  @PostConstruct
  public void init() {
    try {
      imgFirst = new RandomAccessFile(imagePath + "img1.svg", "r");
      imgSecond = new RandomAccessFile(imagePath + "img2.svg", "r");
      imgFirstB = new byte[(int) imgFirst.length()];
      imgSecondB = new byte[(int) imgSecond.length()];
      imgFirst.readFully(imgFirstB);
      imgSecond.readFully(imgSecondB);
      imgFirst.close();
      imgSecond.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public int resolveBeaconKey(String uri) {
    /*    Request :
    https://img.wynk.in/webassets-stage/6M8T6U046O3T8gewaM9D7gb40O2D0U65NzgxODg0ObT2U4y2L2jbI9y0N3jbIfz3McDfY5=d_1.jpg'*/
    StringBuilder mergedDeviceIdBecon = new StringBuilder(uri.split("webassets.*/")[1]);
    logger.info("[resolveBeaconKey] mergedDeviceIdBecon : {} ", mergedDeviceIdBecon);
    if (StringUtils.isBlank(mergedDeviceIdBecon) || mergedDeviceIdBecon.length() < didLength) {
      //failure
      return 3;
    }
    List<String> keyToDeviceId = getBecanKeyToDeviceId(mergedDeviceIdBecon, (byte) 16);
    String becankey = keyToDeviceId.get(0);
    String currentpartialDid = keyToDeviceId.get(1);
    if (currentpartialDid.length() != (didLength / 2)) {
      //failure
      return 1;
    }
    String status =
        musicUserShardRedisServiceManager.setIfNotExistsWithExp(
            becankey, currentpartialDid, cacheTtl);
    logger.info("[resolveBeaconKey] status : {} ", status);
    // getting part value
    int currentPartialDidIndex = Integer.parseInt(uri.charAt(uri.length() - 5) + "");
    if (status == null) {
      String otherPartialDid = musicUserShardRedisServiceManager.get(becankey);
      String completeDid = "";
      switch (currentPartialDidIndex) {
        case 1:
          completeDid = currentpartialDid.concat(otherPartialDid);
          musicUserShardRedisServiceManager.setex(becankey, completeDid, cacheTtl);
          return currentPartialDidIndex;
        case 2:
          completeDid = otherPartialDid.concat(currentpartialDid);
          musicUserShardRedisServiceManager.setex(becankey, completeDid, cacheTtl);
          return currentPartialDidIndex;
        default:
          return currentPartialDidIndex;
      }
    } else if (status.equalsIgnoreCase(inserted)) {
      return currentPartialDidIndex;
    }
    return 1;
  }

  public String genBeaconSecret(String becanPart1, String becanPart2) {
    String becankey = becanPart1.concat(becanPart2);
    String completeDid = musicUserShardRedisServiceManager.get(becankey);
    logger.info(
        "[genBeaconSecret] becankey {} : ,completeDid from redis {} ", becankey, completeDid);
    // returning in case of exception
    String becanSeckey = "";
    if (StringUtils.isEmpty(completeDid) || completeDid.length() != didLength) {
      return null;
    }
    try {
      becanSeckey = EncryptUtils.encrypt_256_numeric(becankey, completeDid.substring(0, 16), 8);
      musicUserShardRedisServiceManager.setex(becanSeckey, completeDid, cacheTtl);
      logger.info("[genBeaconSecret] becanSeckey : {} ", becanSeckey);
    } catch (Exception e) {
      return becanSeckey;
    }
    return becanSeckey;
  }

  public List<String> getBecanKeyToDeviceId(StringBuilder mergedBkDid, byte bitSize) {
    logger.info("[getBecanKeyToDeviceId] processing mergedBkDid : {} ", mergedBkDid.toString());
    String[] partialDid = new String[bitSize * 2];
    // 6M8T6U046O3T8gewaM9D7gb40O2D0U65NzgxODg0ObT2U4y2L2jbI9y0N3jbIfz3McDfY5=d_1.jpg
    mergedBkDid.delete(mergedBkDid.lastIndexOf("_"), mergedBkDid.length());
    for (byte i = 0; i < bitSize; i++) {
      byte lastDidPartIndex = (byte) (((mergedBkDid.length() - 1) - i));
      partialDid[i] = mergedBkDid.charAt(i) + "";
      partialDid[(partialDid.length - 1) - i] = mergedBkDid.charAt(lastDidPartIndex) + "";
      mergedBkDid.deleteCharAt(i);
      mergedBkDid.deleteCharAt((mergedBkDid.length() - 1) - i);
    }
    logger.info(
        "[getBecanKeyToDeviceId] becanKey : {} ,partialDid :  {} ",
        mergedBkDid.toString(),
        String.join("", partialDid));
    return Arrays.asList(mergedBkDid.toString(), String.join("", partialDid));
  }

  public Map<String, String> generateTotp(String did) {
    if (StringUtils.isNotBlank(did)) {
      try {
        Map<String, String> generateTotp = new HashMap<>();
        String totpDeviceId =
            UserDeviceUtils.generateUUID(
                    null,
                    String.valueOf(System.currentTimeMillis()).concat(did),
                    null,
                    MusicPlatformType.WYNK_DEVICE_BASED)
                .substring(0, 13);
        String totpKey =
            Utils.getSha1Hash(System.currentTimeMillis() + totpDeviceId).substring(0, 13);
        logger.info("[generateTotp] totpDeviceId : {} ,totpKey :  {} ", totpDeviceId, totpKey);
        generateTotp.put("did", did);
        generateTotp.put("totpDeviceId", totpDeviceId);
        generateTotp.put("totpKey", totpKey);
        return generateTotp;
      } catch (Exception e) {
        logger.error(
            "Exception in Generating totps for did : {} ,Exception :{}", did, e.getMessage());
      }
    }
    return null;
  }

  public String getCachedDidFromBecanSecret(String becankey) {
    String did = musicUserShardRedisServiceManager.get(becankey);
    logger.info("[generateTotp] found did : {} ,from beacan secret key :  {} ", did, becankey);
    return did;
  }

  public static int generateRandomIntIntRange(int min, int max) {
    Random r = new Random();
    return r.nextInt((max - min) + 1) + min;
  }

  public void encryptBecanSecretInHeaders(
      HttpResponse response, boolean spoofResult, String becanSecret) {
    if (StringUtils.isEmpty(becanSecret)) spoofResult = true;
    logger.info("[AuthBeacon2RequestHandler] spoofing value is :{}", spoofResult);
    for (char value : headerSequence) {
      if (secretSequence.contains(value + "")) {
        int index = secretSequence.indexOf(value);
        // if spoofing is true setting random value which will later on fail
        String bsParts =
            spoofResult
                ? String.valueOf(generateRandomIntIntRange(0, 9))
                : becanSecret.charAt(index) + "";
        response.headers().add(value + "", bsParts);
      } else {
        response.headers().add(value + "", generateRandomIntIntRange(0, 9));
      }
    }
  }

  public HttpResponse returnImageResponse(int imageNumber) {
    HttpResponse response =
        HttpResponseService.createImageResponse(imageNumber == 1 ? imgFirstB : imgSecondB);
    return response;
  }
}
