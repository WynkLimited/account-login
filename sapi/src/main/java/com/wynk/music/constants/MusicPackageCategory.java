package com.wynk.music.constants;

import java.util.HashMap;
import java.util.Map;

public enum MusicPackageCategory {
	
	MUSIC_ALL("MUSIC_ALL"),
	MUSIC_APP("MUSIC_APP"),
	MUSIC_WAP("MUSIC_WAP");
	
	private String id;
	private static Map<String, MusicPackageCategory> idToCategoryMapping = new HashMap<>();
	
	static {
		for(MusicPackageCategory category : MusicPackageCategory.values()) {
			idToCategoryMapping.put(category.id, category);
		}
	}
	
	private MusicPackageCategory(String id) {
		this.id = id;
	}

	public String getid() {
		return id;
	}

	public void setid(String id) {
		this.id = id;
	}
	
	public static MusicPackageCategory getCategoryById(String id) {
		MusicPackageCategory category = idToCategoryMapping.get(id);
		if(category != null) {
			return category;
		}
		return MUSIC_ALL;
	}
	
	@Override
	public String toString() {
		return id;
	}
	
}
