package com.wynk.music.dto;

import com.wynk.music.constants.MusicContentType;
import com.wynk.common.ScreenCode;
import com.wynk.dto.AppScreenMapping;

import java.util.HashMap;
import java.util.Map;

public enum ContentTypeToScreenEnum {
	
	
	SONG(MusicContentType.SONG, ScreenCode.SONG),
	ALBUM(MusicContentType.ALBUM, ScreenCode.SONG),
	PLAYLIST(MusicContentType.PLAYLIST, ScreenCode.SONG),
	USERPLAYLIST(MusicContentType.USERPLAYLIST, ScreenCode.SONG),
	PACKAGE(MusicContentType.PACKAGE, ScreenCode.SONG),
	VIDEO(MusicContentType.VIDEO, ScreenCode.SONG),
	HELLOTUNE(MusicContentType.HELLOTUNE, ScreenCode.SONG),
	ARTIST(MusicContentType.ARTIST, ScreenCode.SONG),
	MOOD(MusicContentType.MOOD, ScreenCode.SONG),
	GENRE(MusicContentType.GENRE, ScreenCode.SONG),
	RADIO(MusicContentType.RADIO, ScreenCode.SONG),
	USERPACKAGE(MusicContentType.USERPACKAGE, ScreenCode.SONG),
	SHORTURL(MusicContentType.SHORTURL, ScreenCode.SONG),
	COMPILATIONS(MusicContentType.COMPILATION, ScreenCode.SONG),
	CONTENT(MusicContentType.CONTENT, ScreenCode.SONG),
	ADHM_PLAYLIST(MusicContentType.ADHM_PLAYLIST, ScreenCode.SONG);
	
	
	private ScreenCode screenCode;
	private MusicContentType contentType;
	
	private static Map<MusicContentType, AppScreenMapping> mapping = new HashMap<MusicContentType, AppScreenMapping>();
	
	static {
		for (ContentTypeToScreenEnum contentTypeToScreenEnum : ContentTypeToScreenEnum.values()) {
			mapping.put(contentTypeToScreenEnum.getContentType(), new AppScreenMapping(contentTypeToScreenEnum.getScreenCode()));
		}
	}
	
	
	private ContentTypeToScreenEnum(MusicContentType contentType, ScreenCode screenCode) {
		this.screenCode = screenCode;
		this.contentType = contentType;
	}
	
	public MusicContentType getContentType() {
		return contentType;
	}
	public ScreenCode getScreenCode() {
		return screenCode;
	}
	
	
	public static AppScreenMapping getScreenMappingFromContentType(MusicContentType contentType) {
		return mapping.get(contentType);
	}
	
}