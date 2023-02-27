package com.wynk.music.dto;

import org.json.simple.JSONObject;

public enum 	MusicCPMapping {
	
	UNIVERSAL_MUSIC("srch_universalmusic","um"),
	SONG_MUSIC("srch_sonymusic","sm"),
	HUNGAMA("srch_hungama","hu"),
	TIMEWARNER("srch_timewarner","tw"),
	SIMCA("srch_simca","si"),
	SAREGAMA("srch_saregama","sa"),
	PPL_KOLKATA("srch_pplkolkata","pk"),
	TIMES_MUSIC("srch_timesmusic","ti"),
	VENUS("srch_venus","ve"),
	PPL_CHANDIGARH("srch_pplchandigarh","pc"),
	TIPS_MUSIC("srch_tipsmusic","tm"),
	ADITYA_MUSIC("srch_adityamusic","am"),
	RDC_MEDIA("srch_rdcmedia","rm"),
	MESHI_CREATIONS("srch_meshicreations","mc"),
	MUZIK_247("srch_muzik247","mk"),
	EROSINTL("srch_erosintl","ei"),
	ZEE_MUSIC("srch_zeemusic","zm"),
	BSB("srch_bsb","bb"),
	SONYMUSIC_SONY("srch_sonymusic_Sony","ss"),
	UNISYSINFO("srch_unisysinfo","un"),
	ADHM("adhm_srch_bsb","ad"),
	PPL_MUMBAI("srch_pplmumbai","pp"),
	PPL("srch_ppl", "pl"),
	SHEMAROO("srch_shemaroo", "sr"),
	ABCDIGITAL("srch_abcdigital","ab"),
	GKDIGITAL("srch_gkdigital","gk"),
	MONSTERCAT("srch_monstercat","mt"),
	ORCHARD("srch_orchard", "or"),
	SPEEDRECORDS("srch_speedrecords", "sp"),
	BELIEVE("srch_believe", "bl"),
    INGROOVES("srch_ingrooves","ig"),
    DIVO("srch_divo","dv"),
    ANANDA_MUSIC("srch_anandamusic","an"),
	WMG("srch_wmg", "wm");

	private String name;
	private String shortForm;
	
	MusicCPMapping(String name, String shortForm) {
		this.name = name;
		this.shortForm = shortForm;
	}
	
	public static JSONObject getCPMappings() {
		JSONObject mapping = new JSONObject();
		for (MusicCPMapping cp : MusicCPMapping.values()) {
			mapping.put(cp.getName(), cp.getShortForm() );
		}
		return mapping;
	}

	public String getName() {
		return name;
	}

	public String getShortForm() {
		return shortForm;
	}
    
}
