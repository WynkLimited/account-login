package com.wynk.music.constants;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhuvangupta on 28/12/13.
 */
public enum MusicContentLanguage {

    ENGLISH("en", "English"), 
    HINDI("hi", "Hindi"), 
    TAMIL("ta", "Tamil"),
    TELUGU("te", "Telugu"),
    KANNADA("kn", "Kannada"),
    PUNJABI("pa", "Punjabi"),
    BENGALI("ba", "Bengali"),
    BHOJPURI("bj", "Bhojpuri"),
    MALAYALAM("ml", "Malayalam"),
    GUJRATI("gu", "Gujarati"),
    MARATHI("mr", "Marathi"),
    RAJASTHANI("ra", "Rajasthani"),
    ORIYA("or", "Oriya"),
    ASSAMESE("as", "Assamese"),
    HARYANVI("hr", "Haryanvi"),
    SINHALESE("si", "sinhalese"),
    //Adding this language for default packages
    DEFAULT("default","Default");

    //PAHARI("ph", "Pahari"), 
	//URDU("ur", "Urdu"); // Not Configured on App
	
	// App also supports - Haryanvi, Bihari, Sanskrit.

    private static List<String> version1ContentLangList = new ArrayList<>();
    static
    {
        version1ContentLangList.add(ENGLISH.getId());
        version1ContentLangList.add(HINDI.getId());
        version1ContentLangList.add(TAMIL.getId());
        version1ContentLangList.add(TELUGU.getId());
        version1ContentLangList.add(KANNADA.getId());
        version1ContentLangList.add(PUNJABI.getId());
        version1ContentLangList.add(BENGALI.getId());
        version1ContentLangList.add(BHOJPURI.getId());
        version1ContentLangList.add(HARYANVI.getId());
    }


    private String id;
    private String name;

    private MusicContentLanguage(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getId();
    }
    
    public static boolean isLangSupportedInOldAppVersions(String langId)
    {
        if(StringUtils.isBlank(langId))
            return false;

        return version1ContentLangList.contains(langId.toLowerCase());
    }

    public static MusicContentLanguage getContentLanguageById(String id) {
        if(StringUtils.isBlank(id))
            return MusicContentLanguage.HINDI;
        for(MusicContentLanguage lang : MusicContentLanguage.values()) {
            if(lang.getId().equalsIgnoreCase(id))
                return lang;
        }

        for(MusicContentLanguage lang : MusicContentLanguage.values()) {
            if(lang.getName().equalsIgnoreCase(id))
                return lang;
        }

        return MusicContentLanguage.HINDI;
    }

    public static boolean isOtherThanHindiEnglish(String langId)
    {
        if(langId.equalsIgnoreCase(MusicContentLanguage.ENGLISH.getId()) ||
                langId.equalsIgnoreCase(MusicContentLanguage.HINDI.getId()) )
            return false;
        return true;
    }
}
