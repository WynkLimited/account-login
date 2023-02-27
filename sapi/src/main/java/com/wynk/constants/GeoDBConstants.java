package com.wynk.constants;

public class GeoDBConstants {

  public static final String GEODB_BUCKET = "wynk-location";
  public static final String GEODB_COUNTRY_S3_HASH_FILE =
      "maxmind/db_version_hashes/GeoIP2-Country";
  public static final String GEODB_COUNTRY_S3_FILE = "maxmind/GeoIP2-Country.mmdb";

  public static final String GEODB_COUNTRY_LOCAL_FILE = "/data/db/geodb/GeoIP2-Country.mmdb";
  public static final String GEODB_COUNTRY_HASH_LOCAL_FILE = "/data/db/geodb/GeoIP2-Country";

  public static final String GEODB_CITY_S3_HASH_FILE = "maxmind/db_version_hashes/GeoLite2-City";
  public static final String GEODB_CITY_S3_FILE = "maxmind/GeoLite2-City.mmdb";

  public static final String GEODB_CITY_LOCAL_FILE = "/data/db/geodb/GeoLite2-City.mmdb";
  public static final String GEODB_CITY_HASH_LOCAL_PATH = "/data/db/geodb/GeoLite2-City";

  public static final String PRODUCT_GEOIP2_COUNTRY = "GeoIP2-Country";
  public static final String PRODUCT_GEOLITE_CITY = "GeoLite2-City";

  public static final String COUNTRY_CODE = "countryCode";

  public static final String STATE_CODE = "stateCode";

  public static final String IP_ADDRESS = "ip";
}
