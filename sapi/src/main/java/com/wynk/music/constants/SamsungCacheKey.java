package com.wynk.music.constants;

/**
 * Created by anurag on 7/21/16.
 */
public class SamsungCacheKey {

	private SamsungScreens       screen;
	private MusicContentLanguage lang;
	private String spotlightSize;
	private int 				 count;

	public SamsungCacheKey(MusicContentLanguage lang, SamsungScreens screen,String spotlightSize,int count){
		this.screen = screen;
		this.lang = lang;
		this.spotlightSize = spotlightSize;
		this.count = count;
	}

	public SamsungScreens getScreen() {
		return screen;
	}

	public void setScreen(SamsungScreens screen) {
		this.screen = screen;
	}

	public MusicContentLanguage getLang() {
		return lang;
	}

	public void setLang(MusicContentLanguage lang) {
		this.lang = lang;
	}

	public String getSpotlightSize() {
		return spotlightSize;
	}

	public void setSpotlightSize(String spotlightSize) {
		this.spotlightSize = spotlightSize;
	}

	@Override
	public String toString() {
		return "MusicFeaturedPackageCachekey [" + (screen != null? "screen=" + screen + ", " : "") + (lang != null? "lang=" + lang + ", " : "") + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lang == null)? 0 : lang.hashCode());
		result = prime * result + ((screen == null)? 0 : screen.hashCode());
		result = prime * result + ((spotlightSize == null)? 0 : spotlightSize.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;

		SamsungCacheKey other = (SamsungCacheKey) obj;
		if(lang != other.lang)
			return false;
		if(screen != other.screen)
			return false;
		if(spotlightSize != other.spotlightSize)
			return false;

		return true;
	}
	public int getCount() {
		return count;
	}

}
