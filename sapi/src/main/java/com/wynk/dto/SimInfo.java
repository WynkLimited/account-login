package com.wynk.dto;

import static com.wynk.utils.MusicBuildUtils.isSupportedByBuildNumber;
import com.wynk.constants.MusicBuildConstants;
import com.wynk.server.ChannelContext;
import com.wynk.user.dto.DeviceEnityKey;
import com.wynk.utils.EncryptUtils;
import com.wynk.utils.MusicDeviceUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

public class SimInfo { 
	
	private String name;
	private String carrier;
	private Boolean roaming;
	private String network;
	private String imeiNumber;
	private String mcc;
	private String mnc;
	private String imsiNumber;
	
	public String getMcc() {
		return mcc;
	}
	public void setMcc(String mcc) {
		this.mcc = mcc;
	}
	public String getMnc() {
		return mnc;
	}
	public void setMnc(String mnc) {
		this.mnc = mnc;
	}
	
	public Boolean getRoaming() {
		return roaming;
	}
	public void setRoaming(Boolean roaming) {
		this.roaming = roaming;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCarrier() {
		return carrier;
	}
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
	public String getNetwork() {
		return network;
	}
	public void setNetwork(String network) {
		this.network = network;
	}
	public String getImeiNumber() {
		return imeiNumber;
	}
	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}

	public String getImsiNumber() {
		return imsiNumber;
	}

	public void setImsiNumber(String imsiNumber) {
		this.imsiNumber = imsiNumber;
	}

	public void fromJsonObject(JSONObject jsonObj,boolean isEnrcypted) throws Exception {
		if(jsonObj.get(DeviceEnityKey.SimInfo.roaming) != null) {
			setRoaming((Boolean) jsonObj.get(DeviceEnityKey.SimInfo.roaming));
        }
		if(jsonObj.get(DeviceEnityKey.SimInfo.name) != null){
			setName((String) jsonObj.get(DeviceEnityKey.SimInfo.name));
		}
		if(jsonObj.get(DeviceEnityKey.SimInfo.carrier) != null){
			setCarrier((String) jsonObj.get(DeviceEnityKey.SimInfo.carrier));
		}
		if(jsonObj.get(DeviceEnityKey.SimInfo.network) != null){
			setNetwork((String) jsonObj.get(DeviceEnityKey.SimInfo.network));
		}
		if(jsonObj.get(DeviceEnityKey.SimInfo.imeiNumber) != null){
			setImeiNumber(
					isEnrcypted?EncryptUtils
							.decrypt_256((String)jsonObj.get(DeviceEnityKey.SimInfo.imeiNumber),EncryptUtils.getDeviceKey()):(String)jsonObj.get(DeviceEnityKey.SimInfo.imeiNumber));
		}
		if(jsonObj.get(DeviceEnityKey.SimInfo.mcc)!=null){
			String mccMnc = (String) jsonObj.get(DeviceEnityKey.SimInfo.mcc);
			if(StringUtils.isNotBlank(mccMnc) && mccMnc.length()>3){
				String mcc = mccMnc.substring(0, 3);
				String mnc = mccMnc.substring(3,mccMnc.length());
				if(StringUtils.isNotBlank(mcc)){
					setMcc(mcc);
				}
				if(StringUtils.isNotBlank(mnc)){
					setMnc(mnc);
				}	
			}	
		}
		if(jsonObj.get(DeviceEnityKey.SimInfo.imsiNumber) != null){
			setImsiNumber(
					isEnrcypted?EncryptUtils
							.decrypt_256((String)jsonObj.get(DeviceEnityKey.SimInfo.imsiNumber),EncryptUtils.getDeviceKey()):(String)jsonObj.get(DeviceEnityKey.SimInfo.imsiNumber));
			
		}
	}
	
	public JSONObject toJsonObject() {
		JSONObject jsonObj = new JSONObject();
        jsonObj.put(DeviceEnityKey.SimInfo.name, getName());
        jsonObj.put(DeviceEnityKey.SimInfo.carrier, getCarrier());
        jsonObj.put(DeviceEnityKey.SimInfo.roaming, getRoaming());
        jsonObj.put(DeviceEnityKey.SimInfo.network, getNetwork());
        jsonObj.put(DeviceEnityKey.SimInfo.imeiNumber, getImeiNumber());
        jsonObj.put(DeviceEnityKey.SimInfo.imsiNumber,getImsiNumber());
        return jsonObj;
	}

}


