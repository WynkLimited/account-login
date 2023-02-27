package com.wynk.common;

import java.util.HashMap;
import java.util.Map;

public enum ScreenCode {
    ABOUT_US(1),
    APP_TOUR(2),
    CREATE_PROFILE(3),
    HOME(4),
    PLAYER(5),
    PLAYER_QUEUE(6),
    SONG(7),
    ALBUM(8),
    PLAYLIST(9),
    USER_PLAYLIST(10),
    SHARED_PLAYLIST(89),
    ARTIST(11),
    MOOD(12),
    GENRE(13),
    TRENDING_ARTIST(14), 
    TRENDING_MOOD(15), 
    TRENDING_GENRE(16), 
    MODULE(17),
    REGISTER(18),
    VERIFY_PIN(19), 
    SEARCH(20), 
    SETTINGS(21),
    UNABLE_TO_REGISTER(22),
    USER_ACCOUNT(23),
    USER_JOURNEY(24),
    USER_PROFILE(25),
    MY_MUSIC(26),
    WEB_VIEW(27),
    LANG_SELECTION(28),
    CREATE_USER_PLAYLIST(29),
    RADIO(30),
    CONTENT_LANG_SETTINGS(31),
    APP_LANG_SETTINGS(32),
    NOTIFICATIONS(33),
    USER_LIKES(34),
    ALBUM_INFO(38),
    RADIO_PLAYER(39),
    PLAYSTORE(40),
    PROMO_CODE(46),
    REGISTER_DIALOG(47), 
    AUTO_REGISTER_DIALOG(48),
    DATA_SAVE(49),
    ALL_DOWNLOADED(50),
    ALL_PURCHASED(51),
    ALL_LIKED(52),
    SETTINGS_APP_INFO(53),
    SETTINGS_DEV(54),
    USER_PLAYLISTS(57),
    EXTERNAL_WEBVIEW(58),
    ADHM_PACKAGE_GRID(59),
    ON_DEVICE(60),
    PACKAGE_LIST(61),
    PACKAGE_GRID(62),
    DOWNLOADED_REMOVE_SONGS(63),
    FETCH_FP_RESULT(64),
    CREATE_ADHM_RUNNING_MIX(65),
    SEARCH_RESULT(66),
	SILENT_NOTIFICATION(67),
	DOWNLOAD_COMPLETED(72),
	DOWNLOAD_UNFINISHED(73),
	ALL_OFFLINE_DOWNLOADED(74),
    NONE(75);
    
    private int opcode;
    
    private static Map<Integer, ScreenCode> opcodeToScreenCode = new HashMap<Integer, ScreenCode>();
    private static Map<String, ScreenCode> nameToScreenCode = new HashMap<String, ScreenCode>();

    static{
        for(ScreenCode code : ScreenCode.values()) {
            opcodeToScreenCode.put(code.getOpcode(), code);
            nameToScreenCode.put(code.name().toLowerCase(),code);
        }
    }
    
    public static ScreenCode getByOpcode(int opcode){
        return opcodeToScreenCode.get(opcode);
    }

    public static ScreenCode getScreenCodeByName(String screenName){
        ScreenCode screenCode = nameToScreenCode.get(screenName.toLowerCase());
        if(screenCode == null)
            return HOME;
        return screenCode;
    }
    
    ScreenCode(int opcode) {
        this.opcode = opcode;
    }
    
    public int getOpcode(){
        return opcode;
    }
}
