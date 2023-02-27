package com.wynk.common;

import java.util.HashSet;

public enum NetworkQuality {
    AWFUL(0), INDIAN_POOR(1), POOR(2), MODERATE(3), GOOD(4), EXCELLENT(5), UNKNOWN(-1);
	
	private final static HashSet<NetworkQuality> goodQuality = new HashSet<NetworkQuality>();
    private final static HashSet<NetworkQuality> badQuality = new HashSet<NetworkQuality>();
	
    private int value;
    
    private NetworkQuality(int value)
    {
    	this.value = value;
    }
    
	static{
		goodQuality.add(EXCELLENT);
		goodQuality.add(GOOD);
		
		badQuality.add(AWFUL);
		badQuality.add(INDIAN_POOR);
		badQuality.add(POOR);
    }
	
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public static Boolean isNetworkQualityGood(NetworkQuality networkQuality)
	{
		return goodQuality.contains(networkQuality);
	}
	
	public static Boolean isNetworkQualityBad(NetworkQuality networkQuality)
	{
		return badQuality.contains(networkQuality);
	}
	
	public static Boolean isNetworkQualityModerate(NetworkQuality networkQuality)
	{
		return networkQuality==MODERATE;
	}
}
