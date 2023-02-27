package com.wynk.constants;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.wynk.music.constants.MusicContentLanguage;

public class RailPositions {
	
	public static final int LOCAL_MP3_RAIL_POS      = 3;
	
	public static final int WYNK_TOP100_RAIL_POS    = 2;
	
	public static int getLocalMp3RailPosition(){
		return LOCAL_MP3_RAIL_POS;
	}
	
	public static int getWynkTop100RailPosition(){
		return WYNK_TOP100_RAIL_POS;
	}
	
	public static int getMyFavRailPosition(List<MusicContentLanguage> userSelectedlanguages){
		if(!CollectionUtils.isEmpty(userSelectedlanguages)){
			return userSelectedlanguages.size() +2;
		}
		return 0;
	}
}
