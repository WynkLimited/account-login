package com.wynk.music.constants;

import org.json.simple.JSONObject;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Unisearch {
	
	public enum UnisearchDataType {
		SONG, ALBUM, ARTIST, MOOD, PLAYLIST;
		
		public static UnisearchDataType getContentTypeFromString(String type)
	    {
			if("song".equalsIgnoreCase(type)){
				return SONG;
			}
			else if("album".equalsIgnoreCase(type)){
				return ALBUM;
			}
			if("artist".equalsIgnoreCase(type)){
				return ARTIST;
			}
			if("mood".equalsIgnoreCase(type)){
				return MOOD;
			}
			if("playlist".equalsIgnoreCase(type)){
				return PLAYLIST;
			}
			return SONG;
	    }
	}
	
	private class ScoreComparator implements Comparator<UnisearchDataType> {
		Map<UnisearchDataType, Number> baseMap;
		public ScoreComparator(Map<UnisearchDataType, Number> baseMap){
			this.baseMap = baseMap;
		}
		
		@Override
		public int compare(UnisearchDataType type1, UnisearchDataType type2) {
			if(type1 == null && type2 == null){
				return 0;
			}
			if(type1 == null || baseMap.get(type1) == null){
				return -1;
			}
			if(type2 == null || baseMap.get(type2) == null){
				return 1;
			}
			double score1 = baseMap.get(type1).doubleValue();
			double score2 = baseMap.get(type2).doubleValue();
			if(score1 < score2){
				return 1;
			}
			else{
				return -1;
			}
		}
	}
	
	private Map<UnisearchDataType, Number> unisearchMap;
	
	private Map<UnisearchDataType, JSONObject> dataMap = new HashMap<>();
	
	public Unisearch() {
		unisearchMap = new HashMap<>();
		updateTypeScore(MusicContentType.SONG.toString(), -1.0f);
		updateTypeScore(MusicContentType.ALBUM.toString(), -1.0f);
		updateTypeScore(MusicContentType.ARTIST.toString(), -1.0f);
		updateTypeScore(MusicContentType.PLAYLIST.toString(), -1.0f);
		updateTypeScore(MusicContentType.MOOD.toString(), -1.0f);
		
	}
	
	public void updateTypeScore(String type, Number value) {
		UnisearchDataType uniType = UnisearchDataType.getContentTypeFromString(type);
		if(!unisearchMap.containsKey(uniType)){
			unisearchMap.put(uniType, value);
			return;
		}
		double currentVal = unisearchMap.get(uniType).doubleValue();
		if(currentVal < value.doubleValue()){
			unisearchMap.put(uniType, value);
		}
	}
	
	public Map<UnisearchDataType, Number> getOrderedTypeMap(){
		ScoreComparator sc = new ScoreComparator(unisearchMap);
		TreeMap<UnisearchDataType, Number> unisearchOrderMap = new TreeMap<UnisearchDataType, Number>(sc);
		unisearchOrderMap.putAll(unisearchMap);
		return unisearchOrderMap;
	}
	
	public void updateDataMap(String type ,JSONObject obj){
		dataMap.put(UnisearchDataType.getContentTypeFromString(type), obj);
	}
	
	
	public JSONObject getDataMapValue(UnisearchDataType type) {
		return dataMap.get(type);
	}

	public void setDataMap(Map<UnisearchDataType, JSONObject> dataMap) {
		this.dataMap = dataMap;
	}
	
	
}
