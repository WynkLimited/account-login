package com.wynk.dto;

import com.wynk.common.FileType;
import com.wynk.common.NetworkType;
import com.wynk.music.constants.BitRateInfo;
import com.wynk.utils.ObjectUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by bhuvangupta on 10/12/13.
 */
public class FileDownloadLocation extends BaseObject {

    private URL fileUrl;
    private NetworkType networkType;
    private String devicetType;

    private String userName;
    private String password;

    private BitRateInfo bitRateInfo;
    private FileType fileType;

    public URL getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(URL fileUrl) {
        this.fileUrl = fileUrl;
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }

    public String getDevicetType() {
        return devicetType;
    }

    public void setDevicetType(String devicetType) {
        this.devicetType = devicetType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public BitRateInfo getBitRateInfo() {
        return bitRateInfo;
    }

    public void setBitRateInfo(BitRateInfo bitRateInfo) {
        this.bitRateInfo = bitRateInfo;
    }

    @Override
    public String toJson()
            throws Exception {
        JSONObject obj = toJsonObject();
        return ObjectUtils.getStringifiedJson(obj);
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fileUrl", fileUrl.toString());
        if(networkType != null) {
            jsonObject.put("networkType", networkType.toString());
        }
        jsonObject.put("devicetType", devicetType);
        jsonObject.put("userName", userName);
        jsonObject.put("password", password);

        if(fileType != null) {
            jsonObject.put("fileType", fileType.toString());
        }
        if(bitRateInfo != null) {
            jsonObject.put("bitRateInfo", bitRateInfo.toJsonObject());
        }
        return jsonObject;
    }

    public void fromJson(String json)
            throws Exception {
        Object obj = JSONValue.parseWithException(json);
        JSONObject valueMap = (JSONObject) obj;
        fromJsonObject(valueMap);
    }

    public void fromJsonObject(JSONObject jsonObject) throws RuntimeException {
        try {
            String urlStr = (String) jsonObject.get("fileUrl");
            URL url = new URL(urlStr);
            setFileUrl(url);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        if(jsonObject.get("networkType") instanceof String) {
            setNetworkType(NetworkType.valueOf((String) jsonObject.get("networkType")));
        }
        setDevicetType((String) jsonObject.get("devicetType"));
        setUserName((String) jsonObject.get("userName"));
        setPassword((String) jsonObject.get("password"));

        if(jsonObject.get("fileType") instanceof String) {
            setFileType(FileType.valueOf((String) jsonObject.get("fileType")));
        }
        if(jsonObject.get("bitRateInfo") instanceof JSONObject) {
            JSONObject object = (JSONObject) jsonObject.get("bitRateInfo");
            BitRateInfo bir = new BitRateInfo();
            bir.fromJsonObject(object);
            setBitRateInfo(bir);
        }
    }
}
