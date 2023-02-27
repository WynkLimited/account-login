package com.wynk.music.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anurag on 10/30/15.
 */
public enum MusicTempo {
	HIGH("h","adhm_tempo_h"),MEDIUM("m","adhm_tempo_m"),LOW("l","adhm_tempo_l");

	private String code;
	private String name;

	MusicTempo(String code, String name){
		this.code= code;
		this.name= name;
	}

	private static Map<String,MusicTempo> tempoNameMap=new HashMap<String,MusicTempo>();
	private static Map<String,MusicTempo> tempoCodeMap=new HashMap<String,MusicTempo>();

	static {
		for (MusicTempo tempo: MusicTempo.values()) {
			tempoNameMap.put(tempo.getName().toLowerCase(), tempo);
			tempoCodeMap.put(tempo.getCode().toLowerCase(), tempo);
		}
	}

	public static MusicTempo getTempoByName(String name) {

		MusicTempo tempo=tempoNameMap.get(name.toLowerCase());
		if(tempo ==null) {
			return null;
		}
		return tempo;
	}

	public static MusicTempo getTempoByCode(String code) {

		MusicTempo tempo=tempoCodeMap.get(code.toLowerCase());
		if(tempo ==null) {
			return null;
		}
		return tempo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
