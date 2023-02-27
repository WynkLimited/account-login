package com.wynk.music.constants;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bhuvangupta on 25/12/13.
 */
public enum MusicPackageType {

    UNKNOWN(-1,"Unknown","Unknown"),FEATURED(1,"Featured","Featured"), NEW_RELEASES(2,"New Releases","Latest Hindi"),
    MY_TOP_25(3,"My Top 25","My Favourites"),
    RECOMMENDED(4,"recommended","recommended"),
    AIRTEL_TOP_25(5,"Airtel Top 25","Wynk Top 100"),
    TRENDING_ARTISTS(6,"bsb_artists","Popular Artists"),
    MOODS(7,"bsb_moods","Moods"),
    FRIENDS_ZONE(8,"Friends Zone","Friends Zone"),SEARCH_RESULT(9,"Search","Search"),
    RADIO(10,"Radio","Radio"),
    TOP_ALBUMS(11,"best_compilations","Top Albums"),
    USER_RENTALS(12,"rentals","User Rentals"),
    USER_DOWNLOADS(13,"downloads","User Downloads"),
    USER_JOURNEY(14,"journey","User Journey"),
    USER_FAVORITES(15,"favorites","User Favorites"),
    USER_FOLLOWS(16,"follows","User Follows"),
    REGIONAL(17,"bsb_regional","Regional"),
    INTERNATIONAL(18,"bsb_international","Latest English"),
    RADIOS(19,"Radios","Radios"),
    USER_PLAYLIST(20,"user_playlist","User Playlist"),
    USER_COLLECTIONS(21,"user_collections","User Collections"),
    BSB_PLAYLISTS(22, "bsb_playlists", "Top Playlists"),
    TOP_CHARTS(23,"bsb_charts","Top Charts"),
    CLASSICS(24,"Classics","Classics"),
    FLASHBACK_90s(25,"Flashback 90s","Flashback 90s"),
    REMIXES(26,"Remix","Special"),
    INSTRUMENTAL(27,"Instrumental","Instrumental"),
    TAMIL_TOP_100(28,"Tamil Top 100","Tamil Top 100"),
    DEVOTIONAL(29,"Devotional","Devotional"),
    ADHM(30,"adhm_playlist","Airtel Delhi Half Marathon"),
    BSB_ADHM_PLAYLISTS(32, "bsb_adhm_playlist", "Airtel Delhi Marathon"),
    CAMPAIGN_MODULE_1(33,"CAMPAIGN_MODULE_1","CAMPAIGN MODULE 1"),

  
    DOWNLOADED(39,"downloaded","DOWNLOADED"),
    UNFINISHED(40, "unfinished","UNFINISHED"),
    CAMPAIGN_MODULE_2(34,"CAMPAIGN_MODULE_2","The Classic Series"),
    TRENDING(35,"Trending","Latest Collection"),
    GALAXY_SPECIAL(36,"galaxy_special","Galaxy Special"),
    ONDEVICE_SONGS(38,"ondevice_songs", "Local MP3 Songs"),
    MY_MUSIC(37,"my_music","My Music"),
	LANG_SPLIT_RAIL(46,"lang_rail","Language Rail"),
	
	
	CAMPAIGN_MODULE_3(47,"campaign_module_3", "Playlist 1"),
	CAMPAIGN_MODULE_4(48,"campaign_module_4","Playlist 2"),
	CAMPAIGN_MODULE_5(49,"campaign_module_5","Playlist 3"),
	CAMPAIGN_MODULE_6(50,"campaign_module_6","Playlist 4"),
    CAMPAIGN_MODULE_7(51,"campaign_module_7","Playlist 5"),
    CAMPAIGN_MODULE_8(52,"campaign_module_8","Playlist 6"),
    MUSIC_ARTIST_RAIL(54,"music_artist_rail","music_artist_rail"),
    HERO_RAIL(55,"hero_rail","hero_rail"),
    MY_STATION(56,"My_station","My_station"),
    TRENDING_SONGS(57,"trending_songs","trending_songs"),
    REDESIGN_MOODS(58,"redesign_moods","Moods"),
    MY_STATION_USECASE(59,"My_station_usecase","My Stations For"),
    MY_MUSIC_CARD(60,"My_music_card","My_music_card"),
    FOLLOWED_ARTISTS_CARD(61,"followed_artists_card","followed_artists_card"),
    PERSONALISED_RADIO_CARD(62,"personalised_radio_card","personalised_radio_card"),
    LOCAL_MP3_SONGS_CARD(63,"local_mp3_songs_card","local_mp3_songs_card"),
    RECOMMENDED_PLAYLISTS_CARD(64,"recommended_playlists_card","recommended_playlists_card"),
    RECOMMENDED_ARTISTS_CARD(65,"recommended_artists_card","recommended_artists_card"),
    RECOMMENDED_SONGS_CARD(66,"recommended_songs_card","recommended_songs_card"),
    LANG_CARD(67,"lang_card","lang_card"),
    CONCERT_RAIL(68,"concert_rail","concert_rail"),
    REDESIGN_DEVOTIONAL(69,"redesign_devotional","Devotional Stations"),
    AUTOPLAY(70,"autoplay","Autoplay"),
    CONTEXTUAL_RAIL(71, "contextual_rail", "contextual_rail"),

    HARYANVI_MODULE_1(72,"haryanvi_module_1", "Haryanvi Playlist 1"),
    HARYANVI_MODULE_2(73,"haryanvi_module_2","Haryanvi Playlist 2"),
    HARYANVI_MODULE_3(74,"haryanvi_module_3","Haryanvi Playlist 3"),
    HARYANVI_MODULE_4(75,"haryanvi_module_4","Haryanvi Playlist 4"),
    HARYANVI_MODULE_5(76,"haryanvi_module_5","Haryanvi Playlist 5"),
    HARYANVI_MODULE_6(77,"haryanvi_module_6","Haryanvi Playlist 6"),
    NEW_RELEASES_2(78,"New Releases 2","New Releases 2"),
    CAMPAIGN_MODULE_9(79,"campaign_module_9","Playlist 7"),
    CAMPAIGN_MODULE_10(80,"campaign_module_10","Playlist 8"),
    CAMPAIGN_MODULE_11(81,"campaign_module_11","Playlist 9"),
    CAMPAIGN_MODULE_12(82,"campaign_module_12","Playlist 10"),
    CAMPAIGN_MODULE_13(83,"campaign_module_13","Playlist 11"),
    CAMPAIGN_MODULE_14(90,"campaign_module_14","Playlist 12"),
    CAMPAIGN_MODULE_15(91,"campaign_module_15","Playlist 13"),
    CAMPAIGN_MODULE_16(92,"campaign_module_16","Playlist 14"),
    CAMPAIGN_MODULE_17(93,"campaign_module_17","Playlist 15"),
    CAMPAIGN_MODULE_18(94,"campaign_module_18","Playlist 16"),
    CAMPAIGN_MODULE_19(95,"campaign_module_19","Playlist 17"),
    CAMPAIGN_MODULE_20(96,"campaign_module_20","Playlist 18"),
    CAMPAIGN_MODULE_21(97,"campaign_module_21","Playlist 19"),
    CAMPAIGN_MODULE_22(98,"campaign_module_22","Playlist 20"),
    CAMPAIGN_MODULE_23(99,"campaign_module_23","Playlist 21"),
    CAMPAIGN_MODULE_24(100,"campaign_module_24","Playlist 22"),
    CAMPAIGN_MODULE_25(101,"campaign_module_25","Playlist 23"),
    CAMPAIGN_MODULE_26(102,"campaign_module_26","Playlist 24"),
    CAMPAIGN_MODULE_27(103,"campaign_module_27","Playlist 25"),
    CAMPAIGN_MODULE_28(104,"campaign_module_28","Playlist 26"),
    CAMPAIGN_MODULE_29(105,"campaign_module_29","Playlist 27"),
    CAMPAIGN_MODULE_30(106,"campaign_module_30","Playlist 28"),
    CAMPAIGN_MODULE_31(107,"campaign_module_31","Playlist 29"),
    CAMPAIGN_MODULE_32(108,"campaign_module_32","Playlist 30"),
    CAMPAIGN_MODULE_33(109,"campaign_module_33","Playlist 31"),
    HT_MOODS(85,"ht_moods","ht moods");


    private int id;
    private String name;
    private String label;

    private static Map<String, MusicPackageType> pkgTypeIdMapping = new HashMap<String, MusicPackageType>();
    private static Map<Integer, MusicPackageType> pkgIdToTypeMapping = new HashMap<>();

    static {
        for (MusicPackageType pkg : MusicPackageType.values()) {
            pkgTypeIdMapping.put(pkg.name.toLowerCase(), pkg);
            if(StringUtils.isNotBlank(pkg.label))
                pkgTypeIdMapping.put(pkg.label.toLowerCase(), pkg);
            pkgIdToTypeMapping.put(pkg.getId(), pkg);
        }
    }

    private MusicPackageType(int id, String name, String label) {
        this.id = id;
        this.name = name;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static MusicPackageType getPackageType(int typeId) {
        MusicPackageType musicPackageType = pkgIdToTypeMapping.get(typeId);
        if (musicPackageType != null)
            return musicPackageType;

        return UNKNOWN; // default
    }

    public static MusicPackageType getPackageType(String name) {
        if(StringUtils.isBlank(name))
            return UNKNOWN;
        
        MusicPackageType musicPackageType = pkgTypeIdMapping.get(name.toLowerCase());
        if (musicPackageType != null)
            return musicPackageType;

        return UNKNOWN; // default
    }

    public static int getPackageTypeId(String name) {
        MusicPackageType musicPackageType = pkgTypeIdMapping.get(name.toLowerCase());
        if(musicPackageType == null)
            return UNKNOWN.getId();
        return musicPackageType.getId();
    }

}
