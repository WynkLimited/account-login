package com.wynk.user.dto;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserDob {

    private String date;
    private String month;
    private String year;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put(UserEntityKey.Dob.date, getDate());
        jsonObj.put(UserEntityKey.Dob.month, getMonth());
        jsonObj.put(UserEntityKey.Dob.year, getYear());
        return jsonObj;
    }

    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }

    public void fromJsonObject(JSONObject jsonObj) {
        Object date = jsonObj.get((UserEntityKey.Dob.date));
        if(date instanceof String) {
            setDate((String) date);
        }
        Object month = jsonObj.get((UserEntityKey.Dob.month));
        if(month instanceof String) {
            setMonth((String) month);
        }
        Object year = jsonObj.get((UserEntityKey.Dob.year));
        if(year instanceof String) {
            setYear((String) year);
        }
    }

    public void fromString(String dob) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = dateFormat.parse(dob);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        setDate(String.valueOf(calendar.get(Calendar.DATE)));
        setMonth(String.valueOf(calendar.get(Calendar.MONTH)));
        setYear(String.valueOf(calendar.get(Calendar.YEAR)));
    }
}
