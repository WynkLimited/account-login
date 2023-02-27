package com.wynk.music.dto;

public enum MusicPlatformType {

	WYNK_APP(0, "app", "app"), 
	WYNK_OLD_WAP(1, "wap", "wap"), 
	WYNK_DEVICE_BASED(2, "wynk-device", "app"), 
	WYNK_CHROMECAST(3, "wynk-chromecast", "app"), 
	WYNK_WAP(4, "wynk-wap", "wap"), 
	SAMSUNG_SDK(5, "samsung-sdk", "samsung-sdk");
	
	private int platform;
	private String name;
	private String appType;
	
	MusicPlatformType(int platform, String name, String appType) {
		this.platform = platform;
		this.name = name;
		this.appType = appType;
	}

	public int getPlatform() {
		return platform;
	}
	
	public String getPlatformString() {
		return String.valueOf(platform);
	}
	
	public static boolean isThirdPartyPlatformId(int id) {
		if (id == SAMSUNG_SDK.getPlatform())
			return true;
		return false;
	}
	
	public static MusicPlatformType getPlatformByName(String name) {
		for (MusicPlatformType platform : MusicPlatformType.values()) {
			if (platform.getName().equals(name))
				return platform;
		}
		return MusicPlatformType.WYNK_APP;
	}
	
	public static MusicPlatformType getPlatformById(int id) {
		for (MusicPlatformType platform : MusicPlatformType.values()) {
			if (platform.getPlatform() == id)
				return platform;
		}
		return MusicPlatformType.WYNK_APP;
	}

	public String getName() {
		return name;
	}

	public String getAppType() {
		return appType;
	}

	public static boolean isWynkApp(MusicPlatformType platform) {
		return platform != null && WYNK_APP.equals(platform);
	}

}
