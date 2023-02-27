package com.wynk.user.dto;

import com.wynk.common.UserEventType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import static com.wynk.constants.JsonKeyNames.URL;
import static com.wynk.constants.JsonKeyNames.VERSION;

/**
 * Created by bhuvangupta on 01/01/14.
 */
public class UserActivityEvent {

  private String id;
  private String version; //event format version
  private UserEventType type;
  private long timestamp;
  private String lang;
  private String appType;
  private String url;
  private long snet = -1;
  private long net = -1;
  private long netq = -1;
  private JSONObject meta;

  // new parameters WA-8529
  private String uid;
  private String did;

  private String os;
  private String browser;
  private boolean isdesktop;
  private String userAgent;
  private String operator;
  private String circle;
  private String referrerFrom;
  private String URI;
  private String customReferrer;
  private String referrer;

  public String getReferrer() {
    return referrer;
  }

  public void setReferrer(String referrer) {
    this.referrer = referrer;
  }

  public String getOs() {
    return os;
  }

  public void setOs(String os) {
    this.os = os;
  }

  public String getBrowser() {
    return browser;
  }

  public void setBrowser(String browser) {
    this.browser = browser;
  }

  public boolean getIsdesktop() {
    return isdesktop;
  }

  public void setIsdesktop(boolean isdesktop) {
    this.isdesktop = isdesktop;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public String getCircle() {
    return circle;
  }

  public void setCircle(String circle) {
    this.circle = circle;
  }

  public String getReferrerFrom() {
    return referrerFrom;
  }

  public void setReferrerFrom(String referrerFrom) {
    this.referrerFrom = referrerFrom;
  }

  public String getURI() {
    return URI;
  }

  public void setURI(String URI) {
    this.URI = URI;
  }

  public String getCustomReferrer() {
    return customReferrer;
  }

  public void setCustomReferrer(String customReferrer) {
    this.customReferrer = customReferrer;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getDid() {
    return did;
  }

  public void setDid(String did) {
    this.did = did;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public UserEventType getType() {
    return type;
  }

  public void setType(UserEventType type) {
    this.type = type;
  }


  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public String getAppType() {
    return appType;
  }

  public void setAppType(String appType) {
    this.appType = appType;
  }

  public long getSnet() {
    return snet;
  }

  public void setSnet(long snet) {
    this.snet = snet;
  }

  public long getNet() {
    return net;
  }

  public void setNet(long net) {
    this.net = net;
  }

  public long getNetq() {
    return netq;
  }

  public void setNetq(long netq) {
    this.netq = netq;
  }

  public JSONObject getMeta() {
    return meta;
  }

  public void setMeta(JSONObject meta) {
    this.meta = meta;
  }

  public JSONObject toJsonObject() {
    JSONObject jsonObj = new JSONObject();
    if (getId() != null)
      jsonObj.put(ID, getId());
    if (getType() != null)
      jsonObj.put(EVENT_TYPE, getType().name());

    jsonObj.put(TIMESTAMP, getTimestamp());
    if (getVersion() != null)
      jsonObj.put(VERSION, getVersion());

    if (getLang() != null)
      jsonObj.put(LANG, getLang());

    if (getMeta() != null)
      jsonObj.put(META, getMeta());

    if (getAppType() != null)
      jsonObj.put(APPTYPE, getAppType());

    if (getUrl() != null) {
      jsonObj.put(URL, getUrl());
    }

    jsonObj.put(SNET, getSnet());
    jsonObj.put(NET, getNet());
    jsonObj.put(NETQ, getNetq());

    jsonObj.put("uid", getUid());
    jsonObj.put("did", getDid());

    if (getOs() != null) {
      jsonObj.put("os", getOs());
    }

    if (getBrowser() != null) {
      jsonObj.put("browser", getBrowser());
    }

    jsonObj.put("isdesktop", getIsdesktop());

    if (getUserAgent() != null) {
      jsonObj.put("userAgent", getUserAgent());
    }

    if (getOperator() != null) {
      jsonObj.put("operator", getOperator());
    }

    if (getCircle() != null) {
      jsonObj.put("circle", getCircle());
    }

    if (getReferrerFrom() != null) {
      jsonObj.put("referrerFrom", getReferrerFrom());
    }

    if (getReferrer() != null) {
      jsonObj.put("referrer", getReferrer());
    }

    if (getURI() != null) {
      jsonObj.put("URI", getURI());
    }

    if (getCustomReferrer() != null) {
      jsonObj.put("customReferrer", getCustomReferrer());
    }

    return jsonObj;
  }

  public void fromJson(String json) throws Exception {
    JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
    fromJsonObject(jsonObj);
  }

  public void fromJsonObject(JSONObject jsonObj) {
    Object idobj = jsonObj.get(ID);
    if (idobj instanceof String) {
      setId((String) idobj);
    }
    if (jsonObj.get(EVENT_TYPE) != null)
      setType(UserEventType.get(((String) jsonObj.get(EVENT_TYPE)).toUpperCase()));

    if (jsonObj.get(VERSION) != null)
      setVersion(((String) jsonObj.get(VERSION)));

    if (jsonObj.get(LANG) != null)
      setLang(((String) jsonObj.get(LANG)));


    if (jsonObj.get(META) != null)
      setMeta(((JSONObject) jsonObj.get(META)));

    if (jsonObj.get(URL) != null)
      setUrl(((String) jsonObj.get(URL)));

    if (jsonObj.get(TIMESTAMP) != null) {
      try {
        setTimestamp(((Number) jsonObj.get(TIMESTAMP)).longValue());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    if (jsonObj.get(APPTYPE) != null)
      setAppType(((String) jsonObj.get(APPTYPE)));

    if (jsonObj.get(SNET) != null) {
      long snet = (long) jsonObj.get(SNET);
      setSnet(snet);
    }


    if (jsonObj.get("did") != null) {
      String did = (String) jsonObj.get("did");
      setDid(did);
    }


    if (jsonObj.get("uid") != null) {
      String uid = (String) jsonObj.get("uid");
      setUid(uid);

    }

    if (jsonObj.get(NET) != null) {
      String net = null;
      Object object = jsonObj.get(NET);

      if (object instanceof String)
        net = (String) object;
      else if (object instanceof Number)
        setNet(((Number) object).longValue());

      if (net != null) {
        String res[] = net.split("\\s*/\\s*");
        if (res.length == 3) {
          setNet(Long.parseLong(res[0]));
          setSnet(Long.parseLong(res[1]));
          setNetq(Long.parseLong(res[2]));
        }
      }
    }

    if (jsonObj.get("os") != null) {
      setOs((String)jsonObj.get("os"));
    }

    if (jsonObj.get("browser") != null) {
      setBrowser((String)jsonObj.get("browser"));
    }

    if (jsonObj.get("isdesktop") != null) {
      setIsdesktop((boolean)jsonObj.get("isdesktop"));
    }

    if (jsonObj.get("userAgent") != null) {
      setUserAgent((String)jsonObj.get("userAgent"));
    }

    if (jsonObj.get("operator") != null) {
      setOperator((String)jsonObj.get("operator"));
    }

    if (jsonObj.get("circle") != null) {
      setCircle((String)jsonObj.get("circle"));
    }

    if (jsonObj.get("referrerFrom") != null) {
      setReferrerFrom((String)jsonObj.get("referrerFrom"));
    }

    if (jsonObj.get("referrer") != null) {
      setReferrer((String)jsonObj.get("referrer"));
    }

    if (jsonObj.get("URI") != null) {
      setURI((String)jsonObj.get("URI"));
    }

    if (jsonObj.get("customReferrer") != null) {
      setCustomReferrer((String)jsonObj.get("customReferrer"));
    }

  }


  public static final String ID = "id";
  public static final String EVENT_TYPE = "event_type";
  public static final String TIMESTAMP = "timestamp";
  public static final String LANG = "lang";
  public static final String META = "meta";
  public static final String DATA = "data";
  public static final String EVENTS = "events";
  public static final String PRIORITY = "priority";
  public static final String APPTYPE = "apptype";
  public static final String SNET = "snet";
  public static final String NET = "net";
  public static final String NETQ = "netq";

    public static void main(String[] args) {
        try {
           String testJson = "{\"id\":1392907745,\"events\":[{\"event_type\":\"SCREEN_OPENED\",\"meta\":{\"id\":\"NOW_PLAYING\"},\"timestamp\":1392907745,\"lang\":\"en\"},{\"event_type\":\"SONG_PLAYED\",\"meta\":{\"id\":\"srch_saregama_INH109801870\"},\"timestamp\":1392907750,\"lang\":\"en\"}],\"timestamp\":1392907745}";
            JSONObject eventGroup = (JSONObject) JSONValue.parseWithException(testJson);
            JSONArray events = (JSONArray) eventGroup.get("events");
            if(events == null)
                events = (JSONArray) eventGroup.get("items");

            System.out.println(testJson);

            for (int i = 0; i < events.size(); i++) {
                JSONObject eventJson = (JSONObject) events.get(i);
                UserActivityEvent event = new UserActivityEvent();
                event.fromJsonObject(eventJson);

                System.out.println(event.toJsonObject().toString());

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

  //TODO: url inside meta object ,need to fix dto
  public String getUrl() {
    if (getMeta() != null && !getMeta().isEmpty())
      return (String) getMeta().get(URL);
    return null;
  }

  public void setUrl(String url) {
    this.url = url;
  }

}
