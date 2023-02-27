package com.wynk.adtech;

import com.wynk.constants.MusicBuildConstants;
import com.wynk.server.ChannelContext;
import com.wynk.utils.MusicBuildUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

public class AD_Slot {

	private String adUnitId;
	private String adSlotName;
	private int adSlotPosition;
	private List<AD_Creative_Template> adTemplates;
	private AD_Type adType;
	private long refreshInterval;

	// adUnit build checks
	private int iosBuildNo;
	private int androidBuildNo;

	private int adBannerWidth;
	private int adBannerHeight;
	private boolean showBanner;

	private int bannerIosBuildNo;
	private int bannerAndroidBuildNo;

	public AD_Slot() {
	}

	AD_Slot(String adSlotName, String adUnitId, int adSlotPosition, List<AD_Creative_Template> adTemplates, AD_Type adType, long refreshInterval) {
		this.adSlotName =adSlotName;
		this.adUnitId = adUnitId;
		this.adSlotPosition = adSlotPosition;
		this.adTemplates = adTemplates;
		this.adType = adType;
		this.refreshInterval = refreshInterval;
		this.iosBuildNo = -1;
		this.androidBuildNo = -1;
		this.adBannerHeight = -1;
		this.adBannerWidth = -1;
		this.showBanner = false;
		this.bannerIosBuildNo = -1;
		this.bannerAndroidBuildNo = -1;
	}

	AD_Slot(String adSlotName, String adUnitId, int adSlotPosition, List<AD_Creative_Template> adTemplates, AD_Type adType, long refreshInterval,int iosBuildNo,int androidBuildNo,int adBannerWidth,int adBannerHeight) {
		this.adSlotName =adSlotName;
		this.adUnitId = adUnitId;
		this.adSlotPosition = adSlotPosition;
		this.adTemplates = adTemplates;
		this.adType = adType;
		this.refreshInterval = refreshInterval;
		this.iosBuildNo = iosBuildNo;
		this.androidBuildNo = androidBuildNo;
		this.adBannerWidth = adBannerWidth;
		this.adBannerHeight = adBannerHeight;
	}

	public int getAdBannerWidth() {
		return adBannerWidth;
	}

	public void setAdBannerWidth(int adBannerWidth) {
		this.adBannerWidth = adBannerWidth;
	}

	public int getAdBannerHeight() {
		return adBannerHeight;
	}

	public void setAdBannerHeight(int adBannerHeight) {
		this.adBannerHeight = adBannerHeight;
	}

	public int getIosBuildNo() {
		return iosBuildNo;
	}


	public int getAndroidBuildNo() {
		return androidBuildNo;
	}

	public void setIosBuildNo(int iosBuildNo) {
		this.iosBuildNo = iosBuildNo;
	}

	public void setAndroidBuildNo(int androidBuildNo) {
		this.androidBuildNo = androidBuildNo;
	}


	public String getName() {
		return adSlotName;
	}

	public List<AD_Creative_Template> getTemplates(){
		return adTemplates;
	}

	public boolean isShowBanner() {
		return showBanner;
	}

	public void setShowBanner(boolean showBanner) {
		this.showBanner = showBanner;
	}

	public int getBannerAndroidBuildNo() {
		return bannerAndroidBuildNo;
	}

	public void setBannerAndroidBuildNo(int bannerAndroidBuildNo) {
		this.bannerAndroidBuildNo = bannerAndroidBuildNo;
	}

	public int getBannerIosBuildNo() {
		return bannerIosBuildNo;
	}

	public void setBannerIosBuildNo(int bannerIosBuildNo) {
		this.bannerIosBuildNo = bannerIosBuildNo;
	}

	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		obj.put("adUnitId", adUnitId);
		//obj.put("adSlotPosition", adSlotPosition);
		if (refreshInterval != -1)
			obj.put("refreshInterval", refreshInterval);

		if(showBanner) {
			if(StringUtils.isNotEmpty(ChannelContext.getOS()) && ChannelContext.getOS().toLowerCase().contains("android") && ChannelContext.getBuildnumber() != null &&
					MusicBuildUtils.isSupportedByBuildNumber(ChannelContext.getBuildnumber(), bannerAndroidBuildNo)) {
				if(adBannerWidth != -1)
					obj.put("adBannerWidth",adBannerWidth);
				if(adBannerHeight != -1)
					obj.put("adBannerHeight",adBannerHeight);
			}
			if(StringUtils.isNotEmpty(ChannelContext.getOS()) && ChannelContext.getOS().toLowerCase().contains("ios") && ChannelContext.getBuildnumber() != null &&
					MusicBuildUtils.isSupportedByBuildNumber(ChannelContext.getBuildnumber(), bannerIosBuildNo)) {
				if(adBannerWidth != -1)
					obj.put("adBannerWidth",adBannerWidth);
				if(adBannerHeight != -1)
					obj.put("adBannerHeight",adBannerHeight);
			}
		}

		JSONArray adTemplatesArray = new JSONArray();
		for (AD_Creative_Template creative : adTemplates) {
			if ((!creative.isBuildCheckReq()) || (creative.isBuildCheckReq() && MusicBuildUtils.isSupported(creative.getIosBuildNos(), creative.getAndroidBuildNos(),MusicBuildConstants.WINDOWS_INITIAL_BUILD_NUMBER, MusicBuildConstants.NOT_SUPPORTED_ON_OS)))
				adTemplatesArray.add(creative.getTemplateId());
		}
		obj.put("adTemplates", adTemplatesArray);
		return obj;
	}

	public JSONObject toPaidJSONObject() {
		JSONObject obj = new JSONObject();
		obj.put("adUnitId", adUnitId);
		JSONArray adTemplatesArray = new JSONArray();
		for (AD_Creative_Template creative : adTemplates) {
			if(creative.showToPaidUser()){
				if ((!creative.isBuildCheckReq()) || (creative.isBuildCheckReq() && MusicBuildUtils.isSupported(creative.getIosBuildNos(), creative.getAndroidBuildNos(),MusicBuildConstants.WINDOWS_INITIAL_BUILD_NUMBER, MusicBuildConstants.NOT_SUPPORTED_ON_OS)))
					adTemplatesArray.add(creative.getTemplateId());
			}
		}
		obj.put("adTemplates", adTemplatesArray);
		return obj;
	}

	public String toJSONString() {
		JSONObject obj = toJSONObject();
		return obj.toJSONString();
	}

	public AD_Type getAdType() {
		return adType;
	}

	public static AD_Slot fromDbConfig(String adunitStr, List<AD_Creative_Template> templates) {
		String[] values = adunitStr.split(",");
		AD_Type type = AD_Type.fromName(values[3]);
		AD_Slot slot = new AD_Slot(values[0], values[1], Integer.valueOf(values[2]), AD_Creative_Template.getTemplateFromAdType(templates, type), type, Long.valueOf(values[4]));
		if(values.length >= 7) {
			slot.setIosBuildNo(Integer.valueOf(values[5]));
			slot.setAndroidBuildNo(Integer.valueOf(values[6]));
		}
		if(values.length >= 9) {
			slot.setAdBannerWidth(Integer.valueOf(values[7]));
			slot.setAdBannerHeight(Integer.valueOf(values[8]));
		}
		if(values.length >= 12) {
			slot.setShowBanner(Boolean.valueOf(values[9]));
			slot.setBannerIosBuildNo(Integer.valueOf(values[10]));
			slot.setBannerAndroidBuildNo(Integer.valueOf(values[11]));
		}
		return slot;
	}

}
