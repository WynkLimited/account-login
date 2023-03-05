package com.wynk.subscription;

import com.wynk.constants.MusicConstants;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Aakash on 15/12/16.
 */
public class WCFProductInfo {

    private String title;
    private String titleColour;
    private String subTitle;
    private String subTitleColour;
    private String infoColour;
    private List<String> info;
    private String message;
    private String statusMessage;
    private String buttonText;
    private String statusMessageColour;
    private String buttonColour;
    private String buttonTextColour;
    private String nativeTitle;
    private String nativeSubTitle;
    private String pricePoint;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleColour() {
        return titleColour;
    }

    public void setTitleColour(String titleColour) {
        this.titleColour = titleColour;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getSubTitleColour() {
        return subTitleColour;
    }

    public void setSubTitleColour(String subTitleColour) {
        this.subTitleColour = subTitleColour;
    }

    public String getInfoColour() {
        return infoColour;
    }

    public void setInfoColour(String infoColour) {
        this.infoColour = infoColour;
    }

    public List<String> getInfo() {
        return info;
    }

    public void setInfo(List<String> info) {
        this.info = info;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatusMessageColour() {
        return statusMessageColour;
    }

    public void setStatusMessageColour(String statusMessageColour) {
        this.statusMessageColour = statusMessageColour;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getButtonColour() {
        return buttonColour;
    }

    public void setButtonColour(String buttonColour) {
        this.buttonColour = buttonColour;
    }

    public String getButtonTextColour() {
        return buttonTextColour;
    }

    public void setButtonTextColour(String buttonTextColour) {
        this.buttonTextColour = buttonTextColour;
    }

    public String getNativeTitle() {
        return nativeTitle;
    }

    public void setNativeTitle(String nativeTitle) {
        this.nativeTitle = nativeTitle;
    }

    public String getNativeSubTitle() {
        return nativeSubTitle;
    }

    public void setNativeSubTitle(String nativeSubTitle) {
        this.nativeSubTitle = nativeSubTitle;
    }

    public String getPricePoint() {
        return pricePoint;
    }

    public void setPricePoint(String pricePoint) {
        this.pricePoint = pricePoint;
    }

    public void fromJson(String json, String prefix) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj,prefix);
    }

    public void fromJsonObject(JSONObject jsonObj, String prefix) {

        setTitle("");
        if(jsonObj.get(prefix+"title") != null) {
            setTitle((jsonObj.get(prefix+"title").toString()));
        }

        if(jsonObj.get(prefix+"titleColour") != null) {
            try {
                setTitleColour(getColour(jsonObj.get(prefix + "titleColour").toString(), MusicConstants.DEFAULT_TITLE_COLOUR));
            }catch (Throwable th){
                setTitleColour(MusicConstants.DEFAULT_TITLE_COLOUR);
            }
        }

        setSubTitle("");
        if(jsonObj.get(prefix+"subTitle") != null){
            setSubTitle(jsonObj.get(prefix+"subTitle").toString());
        }

        if(jsonObj.get(prefix+"subTitleColour") != null){
            try {
                setSubTitleColour(getColour(jsonObj.get(prefix + "subTitleColour").toString(), MusicConstants.DEFAULT_SUBTITLE_COLOUR));
            }catch (Throwable th){
                setSubTitleColour(MusicConstants.DEFAULT_SUBTITLE_COLOUR);
            }
        }

        if(jsonObj.get(prefix+"infoColour") != null){
            try {
                setInfoColour(jsonObj.get(prefix + "infoColour").toString());
            }catch (Throwable th){
                setInfoColour(MusicConstants.DEFAULT_BUTTON_TEXT_COLOUR);
            }
        }

        if(jsonObj.get(prefix+"statusMessageColour") != null){
            try {
                setStatusMessageColour(getColour(jsonObj.get(prefix + "statusMessageColour").toString(), MusicConstants.DEFAULT_STATUS_MESSAGE_COLOUR));
            }catch (Throwable th){
                setStatusMessageColour(MusicConstants.DEFAULT_STATUS_MESSAGE_COLOUR);
            }
        }

        if(jsonObj.get(prefix+"info") != null) {
            String infoMessages = jsonObj.get(prefix+"info").toString();
            List<String> stringList = Arrays.asList(infoMessages.split("\\|"));
            setInfo(stringList);
        }

        setMessage("");
        if(jsonObj.get(prefix+"message") != null) {
            setMessage(jsonObj.get(prefix+"message").toString());
        }

        setStatusMessage("");
        if(jsonObj.get(prefix+"statusMessage") != null) {
            setStatusMessage(jsonObj.get(prefix+"statusMessage").toString());
        }

        setButtonText("");
        if(jsonObj.get(prefix+"buttonText") != null) {
            setButtonText(jsonObj.get(prefix+"buttonText").toString());
        }

        if(jsonObj.get(prefix+"buttonColour") != null) {
            try {
                setButtonColour(getColour(jsonObj.get(prefix + "buttonColour").toString(), MusicConstants.DEFAULT_BUTTON_COLOUR));
            }catch (Throwable th){
                setButtonColour(MusicConstants.DEFAULT_BUTTON_COLOUR);
            }
        }

        if(jsonObj.get(prefix+"buttonTextColour") != null) {
            try {
                setButtonTextColour(getColour(jsonObj.get(prefix + "buttonTextColour").toString(), MusicConstants.DEFAULT_BUTTON_TEXT_COLOUR));
            }catch (Throwable th){
                setButtonTextColour(MusicConstants.DEFAULT_BUTTON_TEXT_COLOUR);
            }
        }

        setNativeTitle("");
        if(jsonObj.get(prefix+"nativeTitle") != null){
            setNativeTitle(jsonObj.get(prefix+"nativeTitle").toString());
        }

        setNativeSubTitle("");
        if(jsonObj.get(prefix+"nativeSubTitle") != null){
            setNativeSubTitle(jsonObj.get(prefix+"nativeSubTitle").toString());
        }

        setPricePoint("");
        if(jsonObj.get(prefix+"pricePoint") != null){
            setPricePoint(jsonObj.get(prefix+"pricePoint").toString());
        }
    }

    public String toJson(){
        JSONObject jsonObject = toJsonObject();
        return jsonObject.toString();
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();

        if(getTitle() != null){
            jsonObj.put("title", getTitle());
        }
        if(getTitleColour() != null){
            jsonObj.put("titleColour", getTitleColour());
        }
        if(getSubTitle() != null){
            jsonObj.put("subTitle", getSubTitle());
        }
        if(getSubTitleColour() != null){
            jsonObj.put("titleColour", getTitleColour());
        }
        if(getInfoColour() != null){
            jsonObj.put("infoColour", getInfoColour());
        }
        if(getInfo() != null){
            JSONArray jsonArray = new JSONArray();
            if(!CollectionUtils.isEmpty(getInfo())){
                for(String info : getInfo()) {
                    jsonArray.add(info);
                }
            }
            jsonObj.put("info",jsonArray);
        }
        if(getMessage() != null){
            jsonObj.put("message", getMessage());
        }
        if(getStatusMessage() != null){
            jsonObj.put("statusMessage", getStatusMessage());
        }
        if(getButtonText() != null){
            jsonObj.put("buttonText", getButtonText());
        }
        if(getStatusMessageColour() != null){
            jsonObj.put("statusMessageColour", getStatusMessageColour());
        }
        if(getButtonColour() != null){
            jsonObj.put("buttonColour", getButtonColour());
        }
        if(getButtonTextColour() != null){
            jsonObj.put("buttonTextColour", getButtonTextColour());
        }
        if(getNativeTitle() != null){
            jsonObj.put("nativeTitle",getNativeTitle());
        }
        if (getSubTitle() != null){
            jsonObj.put("nativeSubTitle",getNativeSubTitle());
        }
        if(StringUtils.isNotBlank(getPricePoint())){
            jsonObj.put("pricePoint",getPricePoint());
        }
        return jsonObj;
    }

    public String getColour(String colourParameter,String defaultValue) {
        if (StringUtils.isNotBlank(colourParameter)) {
            if (colourParameter.startsWith("#") && colourParameter.length() == 7) {
                return colourParameter;
            }
        }
        return defaultValue;
    }

    @Override
    public String toString() {
        return "WCFProductInfo{" +
                "title='" + title + '\'' +
                ", titleColour='" + titleColour + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", subTitleColour='" + subTitleColour + '\'' +
                ", infoColour='" + infoColour + '\'' +
                ", info=" + info +
                ", message='" + message + '\'' +
                ", statusMessage='" + statusMessage + '\'' +
                ", buttonText='" + buttonText + '\'' +
                ", statusMessageColour='" + statusMessageColour + '\'' +
                ", buttonColour='" + buttonColour + '\'' +
                ", buttonTextColour='" + buttonTextColour + '\'' +
                ", nativeTitle='" + nativeTitle + '\'' +
                ", nativeSubTitle='" + nativeSubTitle + '\'' +
                ", pricePoint='" + pricePoint + '\'' +
                '}';
    }
}
