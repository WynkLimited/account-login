package com.wynk.utils;

import com.wynk.common.Jsonable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * A Class to parse basic primitives from JSON.
 * 
 * @author Aakash Garg.
 *
 */
public class JSONParseUtils {

	public static String getString(JSONObject json, String keyName) {
		if (json.get(keyName) != null) {
			return (String) json.get(keyName);
		}
		return null;
	}

	public static Integer getInt(JSONObject json, String keyName) {
		if (json.get(keyName) != null) {
			if(json.get(keyName) instanceof String){
				return Integer.parseInt((String) json.get(keyName));
			}
			long val = (Long) json.get(keyName);
			return (int) val;
		}
		return null;
	}

	public static Long getLong(JSONObject json, String keyName) {
		if (json.get(keyName) != null) {
			return (Long) json.get(keyName);
		}
		return null;
	}

	public static Double getDouble(JSONObject json, String keyName) {
		if (json.get(keyName) != null) {
			return (Double) json.get(keyName);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray toJSONArray(List<Jsonable> list){
		JSONArray array = new JSONArray();
		for(Jsonable jsonable: list){
			array.add(jsonable.toJSONObject());
		}
		return array;
	}
}
