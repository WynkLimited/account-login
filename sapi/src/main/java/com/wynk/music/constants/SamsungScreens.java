package com.wynk.music.constants;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anurag on 7/20/16.
 */
public enum SamsungScreens {

	XXXHDPI  ("xxxhdpi",768,464),
	XXHDPI   ("xxhdpi", 576,348),
	XHDPI	 ("xhdpi",  384,232),
	HDPI	 ("hdpi",   288,174),
	MDPI	 ("mdpi",   192,116),
	WVGA	 ("wvga",   256,155);


	String name;
	int    minusonepixel;
	int    spotlightpixel;

	private static Map<String,SamsungScreens> nameMap = new HashMap<String,SamsungScreens>();

	SamsungScreens(String name, int spotlightpixel, int minusonepixel){
		this.name = name;
		this.minusonepixel = minusonepixel;
		this.spotlightpixel = spotlightpixel;
	}


	static {
		for (SamsungScreens bucket : SamsungScreens.values()) {
			nameMap.put(bucket.getName(), bucket);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMinusonepixel() {
		return minusonepixel;
	}

	public void setMinusonepixel(int minusonepixel) {
		this.minusonepixel = minusonepixel;
	}

	public int getSpotlightpixel() {
		return spotlightpixel;
	}

	public void setSpotlightpixel(int spotlightpixel) {
		this.spotlightpixel = spotlightpixel;
	}

	public static Map<String, SamsungScreens> getNameMap() {
		return nameMap;
	}

	public static void setNameMap(Map<String, SamsungScreens> nameMap) {
		SamsungScreens.nameMap = nameMap;
	}

	public static SamsungScreens getScreenByName(String name) {

		if(StringUtils.isEmpty(name))
			return SamsungScreens.HDPI;

		SamsungScreens bucket = nameMap.get(name.toLowerCase());
		if(bucket ==null) {
			return SamsungScreens.HDPI;
		}

		return bucket;
	}
}
