package com.wynk.newcode.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class JsonUtils {

    public static final Gson       GSON       = new GsonBuilder().create();
    public static final JsonParser JSONPARSER = new JsonParser();

    public static String getSafeString(JSONObject json, String key) {
        if(json == null || StringUtils.isBlank(key)) {
            return StringUtils.EMPTY;
        }
        try {
            return (String) json.get(key);
        }
        catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    public static List<String> getSafeArray(JSONObject json, String key) {
        if(json == null || StringUtils.isBlank(key)) {
            return new ArrayList<>();
        }
        try {
            return (List<String>) json.get(key);
        }
        catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static int getSafeInt(JsonObject json, String key) {
        if(json == null || StringUtils.isBlank(key)) {
            return 0;
        }
        try {
            return (int) json.get(key).getAsInt();
        }
        catch (Exception e) {
            return 0;
        }
    }

    public static <T> List<T> stringToList(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr);
    }

    public static Map<String, String> jsonStringToMap(String jsonString) {
        if(StringUtils.isBlank(jsonString)) {
            return new HashMap<String, String>();
        }
        return GSON.fromJson(jsonString, new TypeToken<HashMap<String, String>>() {

        }.getType());
    }

    public static void main(String[] args) {
        JsonObject jsonObject = JsonUtils.GSON.fromJson("{\"code\":1403,\"message\":\"You\n" + " need an Eros Now Premium subscription to watch this title. Please upgrade now.\"}", JsonObject.class);
        int code = JsonUtils.getSafeInt(jsonObject, "code");
        System.out.println(code);
    }
}
