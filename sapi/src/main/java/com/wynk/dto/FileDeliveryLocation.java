package com.wynk.dto;

import com.wynk.music.constants.BitRateInfo;
import com.wynk.common.FileDeliveryType;
import com.wynk.common.FileType;
import com.wynk.common.NetworkType;
import com.wynk.utils.ObjectUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.net.URL;

/**
 * Created by bhuvangupta on 10/12/13.
 */
public class FileDeliveryLocation extends BaseObject{

    private URL fileUrl;
    private NetworkType networkType;
    private String devicetType;
    private FileDeliveryType fileDeliveryType;
    private BitRateInfo bitRateInfo;
    private FileType fileType;
    private Number fileSize;

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

    @Override
    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("fileUrl", fileUrl.toString());
        if(networkType != null) {
            jsonObj.put("networkType", networkType.toString());
        }
        jsonObj.put("devicetType", devicetType);
        if(fileDeliveryType != null) {
            jsonObj.put("fileDeliveryType", fileDeliveryType.toString());
        }
        if(bitRateInfo != null) {
            jsonObj.put("bitRateInfo", bitRateInfo.toJsonObject());
        }
        if(fileType != null) {
            jsonObj.put("fileType", fileType.toString());
        }
        jsonObj.put("fileSize", fileSize);
        return jsonObj;
    }

    @Override
    public void fromJsonObject(JSONObject jsonObject) {
        try {
            setFileUrl(new URL((String) jsonObject.get("fileUrl")));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(jsonObject.get("networkType") instanceof String) {
            setNetworkType(NetworkType.valueOf((String) jsonObject.get("networkType")));
        }
        setDevicetType((String) jsonObject.get("devicetType"));
        if(jsonObject.get("fileDeliveryType") instanceof String) {
            setFileDeliveryType(FileDeliveryType.valueOf((String) jsonObject.get("fileDeliveryType")));
        }
        if(jsonObject.get("bitRateInfo") instanceof JSONObject) {
            JSONObject jsonObj = (JSONObject) jsonObject.get("bitRateInfo");
            BitRateInfo bri = new BitRateInfo();
            bri.fromJsonObject(jsonObj);
            setBitRateInfo(bri);
        }
        if(jsonObject.get("fileType") instanceof String) {
            String fileTypeStr = (String) jsonObject.get("fileType");
            setFileType(FileType.valueOf(fileTypeStr));
        }

        if(jsonObject.get("fileSize") instanceof Number) {
            Number fileSz = (Number) jsonObject.get("fileSize");
            setFileSize(fileSz);
        }
    }

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

    public FileDeliveryType getFileDeliveryType() {
        return fileDeliveryType;
    }

    public void setFileDeliveryType(FileDeliveryType fileDeliveryType) {
        this.fileDeliveryType = fileDeliveryType;
    }

    public BitRateInfo getBitRateInfo() {
        return bitRateInfo;
    }

    public void setBitRateInfo(BitRateInfo bitRateInfo) {
        this.bitRateInfo = bitRateInfo;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public Number getFileSize() {
        return fileSize;
    }

    public void setFileSize(Number fileSize) {
        this.fileSize = fileSize;
    }
}
