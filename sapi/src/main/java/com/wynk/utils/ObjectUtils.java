package com.wynk.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.util.CollectionUtils;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Map;
import world.ignite.common.exception.WynkRuntimeException;

/**
 * Created with IntelliJ IDEA.
 * User: dhruva
 * Date: 01/05/14
 * Time: 3:05 AM
 * To change this template use File | Settings | File Templates.
 */
public final class ObjectUtils {

  public static ObjectMapper objectMapper = new ObjectMapper();

    private ObjectUtils() {
        throw new RuntimeException("Should not be instantiated");
    }

    public static boolean isNotNullNumber(Object item) {
        try {
            if (item == null) {
                return false;
            }

            NumberFormat.getInstance().parse(item.toString());
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    public static Number getNumber(Object item, Number defaultValue) {
        if (item == null) {
            return defaultValue;
        }
        try {
            if (item instanceof Number) {
                return (Number) item;
            } else if (item instanceof String) {
                return NumberFormat.getInstance().parse((String) item);
            }
            return defaultValue;
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    public static Boolean getBoolean(Object item, Boolean defaultValue) {
        if (item == null) {
            return defaultValue;
        }

        try {
            if (item instanceof Boolean) {
                return (Boolean) item;
            } else if (item instanceof String) {
                if (StringUtils.isBlank((String) item)) {
                    return defaultValue;
                }
                return Boolean.parseBoolean((String) item);
            }
            return defaultValue;
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    public static String getString(Object item, String defaultValue) {
        if (item == null) {
            return defaultValue;
        }

        try {
            return item.toString();
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    public static String getEmptySafeString(String str, String defaultValue) {
        if (org.springframework.util.StringUtils.hasText(str)) {
            return str;
        }
        return defaultValue;
    }

    public static <T> T getNullSafeObject(T item, Object defaultValue) {
        if (item == null) {
            return (T) defaultValue;
        }
        return item;
    }


    public static Integer integerValue(String str, Integer defaultValue) {
        try {
            /**
             * use valueOf method so that internal Integer cache can be used
             */
            return Integer.valueOf(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static boolean isEmpty(Object obj) {
        if(obj == null) {
            return true;
        }
        return !notEmpty(obj);
    }

    public static boolean notEmpty(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Collection) {
            return !CollectionUtils.isEmpty((Collection) obj);
        } else if (obj instanceof String) {
            return org.springframework.util.StringUtils.hasText((String) obj);
        } else if (obj instanceof Map) {
            return !CollectionUtils.isEmpty((Map) obj);
        }
        return true;
    }

    public static String sliceString(String str, int startIndex, int endIndex, String suffix) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(str)) {
            return "";
        }
        int origLength = str.length();
        int end = endIndex;
        if (end > str.length()) {
            end = str.length();
        }
        if (startIndex > end) {
            return "";
        }
        str = str.substring(startIndex, end);
        if (origLength > end && suffix != null) {
            return str + suffix;
        }
        return str;
    }

    public static String capitalize(String str) {
        if (str.contains("_")) {
            str = str.replaceAll("_", " ");
        }
        return org.apache.commons.lang3.StringUtils.capitalize(str);
    }

    public static String subString(String str, int toIndex) {
        if (org.apache.commons.lang3.StringUtils.isBlank(str)) {
            return str;
        }

        return str.substring(0, toIndex);
    }

    public static String getStringifiedJson(JSONObject jsonObject){
        if (objectMapper == null){
              objectMapper = new ObjectMapper();
          }

        try {
              return objectMapper.writeValueAsString(jsonObject);
          } catch (JsonProcessingException e) {
              e.printStackTrace();
          }
        return jsonObject.toJSONString();
    }

  public static <T> String writeAsString(T object) {
    try {
      objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new WynkRuntimeException(e);
    }
  }
}
