package com.wynk.adtech;

import org.json.simple.JSONObject;

public enum AD_Type {

	PREROLL("PREROLL", AdConstants.PREROLL_REFRESH_INTERVAL, AdConstants.PREROLL_REFRESH_INTERVAL_UNIT ),
	NATIVE("NATIVE", AdConstants.NATIVE_REFRESH_INTERVAL, AdConstants.NATIVE_REFRESH_INTERVAL_UNIT),
	FEATURED("FEATURED", AdConstants.NATIVE_REFRESH_INTERVAL, AdConstants.NATIVE_REFRESH_INTERVAL_UNIT),
	RAIL("RAIL", AdConstants.NATIVE_REFRESH_INTERVAL, AdConstants.NATIVE_REFRESH_INTERVAL_UNIT),
	AD_EX("AD_EX", AdConstants.NATIVE_REFRESH_INTERVAL, AdConstants.NATIVE_REFRESH_INTERVAL_UNIT),
	NATIVE_LIST_SLOT_2("NATIVE_LIST_SLOT_2", AdConstants.NATIVE_REFRESH_INTERVAL, AdConstants.NATIVE_REFRESH_INTERVAL_UNIT),
	APP_INSTALL_SLOT("APP_INSTALL_SLOT", AdConstants.NATIVE_REFRESH_INTERVAL, AdConstants.NATIVE_REFRESH_INTERVAL_UNIT),
	NATIVE_SLOT_LAST_RAIL("NATIVE_SLOT_LAST_RAIL", AdConstants.NATIVE_REFRESH_INTERVAL, AdConstants.NATIVE_REFRESH_INTERVAL_UNIT),
	WYNK_PREROLL_PREMIUM("WYNK_PREROLL_PREMIUM", AdConstants.NATIVE_REFRESH_INTERVAL, AdConstants.NATIVE_REFRESH_INTERVAL_UNIT),
	NATIVE_LIST_SLOT_8("NATIVE_LIST_SLOT_8", AdConstants.NATIVE_REFRESH_INTERVAL, AdConstants.NATIVE_REFRESH_INTERVAL_UNIT),
	NATIVE_GRID_SLOT("NATIVE_GRID_SLOT", AdConstants.NATIVE_REFRESH_INTERVAL, AdConstants.NATIVE_REFRESH_INTERVAL_UNIT),
	LYRICS_AD_SLOT("LYRICS_AD_SLOT",AdConstants.NATIVE_REFRESH_INTERVAL,AdConstants.NATIVE_REFRESH_INTERVAL_UNIT),
	NATIVE_INTERSTITIAL_AD("NATIVE_INTERSTITIAL_AD",AdConstants.NATIVE_REFRESH_INTERVAL,AdConstants.NATIVE_REFRESH_INTERVAL_UNIT);
	

	private String name;
	private int refreshInterval;
	private String refreshIntervalUnit;

	AD_Type(String name, int refreshInterval, String refreshIntervalUnit) {
		this.name = name;
		this.refreshInterval = refreshInterval;
		this.refreshIntervalUnit = refreshIntervalUnit;
	}

	public String getName() {
		return name;
	}

	public int getRefreshInterval() {
		return refreshInterval;
	}

	public static AD_Type fromName(String name) {
		for (AD_Type type : AD_Type.values()){
			if (type.getName().equals(name))
				return type;
		}
		return null;
	}
	
}
