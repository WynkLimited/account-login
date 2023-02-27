package com.wynk.exceptions;

import org.json.simple.JSONObject;

/**
 * Created by a1vlqlyy on 18/05/17.
 */
public class OTPAuthorizationException extends Exception{

    private JSONObject jsonObject;

    public OTPAuthorizationException(JSONObject jsonObject){
        super();
        this.jsonObject = jsonObject;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
