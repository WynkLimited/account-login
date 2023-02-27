package com.wynk.dto;

import com.wynk.utils.ObjectUtils;
import com.wynk.utils.Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bhuvangupta on 10/12/13.
 */
public class Event extends BaseObject{

    private String eventType;
    private Map<String, List<String>> eventAttributes;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Map<String, List<String>> getEventAttributes() {
        return eventAttributes;
    }

    public void setEventAttributes(Map<String, List<String>> eventAttributes) {
        this.eventAttributes = eventAttributes;
    }

    @Override
    public String toJson()
            throws Exception {
        JSONObject obj = toJsonObject();
        return ObjectUtils.getStringifiedJson(obj);
    }

    public void fromJson(String json)
            throws Exception {
        Object obj = JSONValue.parseWithException(json);
        JSONObject valueMap = (JSONObject) obj;
        fromJsonObject(valueMap);
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("eventType", eventType);
        JSONObject attrMap = new JSONObject();
        if(eventAttributes != null) {
            for(Map.Entry<String, List<String>> en : eventAttributes.entrySet()) {
                String key = en.getKey();
                List<String> val = en.getValue();
                JSONArray arr = new JSONArray();
                arr.addAll(val);
                attrMap.put(key, arr);
            }
        }
        jsonObject.put("eventAttributes", attrMap);
        return jsonObject;
    }

    public void fromJsonObject(JSONObject jsonObject) {
        setEventType((String) jsonObject.get("eventType"));
        if(jsonObject.get("eventType") instanceof JSONObject) {
            JSONObject attrMap = (JSONObject) jsonObject.get("eventType");
            Map<String, List<String>> eventMap = new HashMap<>();
            for(Object en : attrMap.entrySet()) {
                if(en instanceof Map.Entry) {
                    Map.Entry entry = (Map.Entry) en;
                    String key = (String) entry.getKey();
                    JSONArray arr = (JSONArray) entry.getValue();
                    List<String> valList = Utils.convertToStringList(arr);
                    eventMap.put(key, valList);
                }
            }
            setEventAttributes(eventMap);
        }
    }
}
