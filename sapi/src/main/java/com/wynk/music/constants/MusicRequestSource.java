package com.wynk.music.constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum MusicRequestSource {

	WAP(0,"wap"), APP(1,"app");
	
	int id;
	String name;
	
	private static final Map<Integer, MusicRequestSource> idToMusicRequestSourceMap = getIdToMusicRequestSourceMap();
	
	MusicRequestSource(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	private static Map<Integer, MusicRequestSource> getIdToMusicRequestSourceMap() {
		Map<Integer, MusicRequestSource> temp = new HashMap<>();
		for(MusicRequestSource musicRequestSource: MusicRequestSource.values()) {
			temp.put(musicRequestSource.id, musicRequestSource);
		}
		return Collections.unmodifiableMap(temp);
	}
	
	public static MusicRequestSource getRequestSourceById(int id) {
		if(idToMusicRequestSourceMap.get(id) != null) {
			return idToMusicRequestSourceMap.get(id);
		}
		return WAP;
	}
}
