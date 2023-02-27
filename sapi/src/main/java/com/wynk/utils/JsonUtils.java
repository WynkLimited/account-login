package com.wynk.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.wynk.common.Jsonable;
import com.wynk.common.PortalException;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import static com.wynk.constants.JsonKeyNames.*;

/**
 * Created by bhuvangupta on 01/01/14.
 */
public class JsonUtils {

    public static final JsonParser JSON_PARSER           = new JsonParser();
    public static final JSONObject EMPTY_JSON           = new JSONObject();
    public static final String     EMPTY_JSON_STR       = EMPTY_JSON.toJSONString();
    public static final String     EMPTY_JSON_ARRAY_STR = "{\"total\":0,\"count\":0,\"items\":[],\"offset\":0}";

    public static final Gson GSON = new GsonBuilder().create();

    public static JSONObject createErrorResponse(String errorcode, String errorMessage) {
        JSONObject response = new JSONObject();
        response.put("errorCode", errorcode);
        response.put("error", errorMessage);
        return response;
    }

    public static JSONObject createOkResponse() {
        JSONObject response = new JSONObject();
        response.put("status", "ok");
        return response;
    }

    @SuppressWarnings("unchecked")
    public static JSONObject createJSONObject(String id, String name, String type) {
        JSONObject jsonObject = new JSONObject();
        if(!StringUtils.isEmpty(id))
            jsonObject.put(ID, id);
        if(!StringUtils.isEmpty(name))
            jsonObject.put(NAME, name);
        if(!StringUtils.isEmpty(TYPE))
            jsonObject.put(TYPE, type);
        return jsonObject;
    }

    @SuppressWarnings("unchecked")
    public JSONArray toJSONArray(List<Jsonable> list) {
        JSONArray arr = new JSONArray();
        for(Jsonable entry : list) {
            arr.add(entry.toJSONObject());
        }
        return arr;
    }

    @SuppressWarnings("unchecked")
    public JSONObject toJsonObject(String keyName, Object obj) {
        JSONObject json = new JSONObject();
        json.put(keyName, obj);
        return json;
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

    public static <T> List<T> stringToList(String s, Class<T[]> clazz) {
        if(StringUtils.isEmpty(s)) {
            return new ArrayList<>();
        }
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr);
    }

    public static JSONObject createErrorResponse(String errorcode, String errorTitle, String errorMessage) {
        JSONObject response = new JSONObject();
        response.put("errorCode", errorcode);
        response.put("errorTitle", errorTitle);
        response.put("error", errorMessage);
        return response;
    }

    public static JSONObject getJsonObjectFromString(String requestPayload) throws PortalException {
        JSONObject requestJson = null;

        try {
            requestJson = (JSONObject) JSONValue.parseWithException(requestPayload);
        } catch (ParseException e) {
            throw new PortalException("Error parsing request : " + e.getMessage(), e);
        }
        return requestJson;
    }

}
