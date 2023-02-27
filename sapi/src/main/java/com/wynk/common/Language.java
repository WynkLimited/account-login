package com.wynk.common;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

public enum Language {

    ENGLISH("en", "english","english"),
    HINDI("hi","hindi", "हिंदी"),
    TAMIL("ta", "tamil","தமிழர்"),
    TELUGU("te","telugu", "తెలుగు"),
    MALAYALAM("ml","malayalam", "മലയാലമ്"),
    KANNADA("kn","kannada", "ಕನ್ನಡ"),
    BENGALI("bn","bengali", "বাংলা"),
    PUNJABI("pa", "punjabi","ਪਂਜਾਬੀ"),
    ORIYA("or", "oriya","ଔରିୟ"),
    HARYANVI("hr","haryanvi","हरयाणवी"),
    //ASSAMESE("as", "assamese"),
    //BHOJPURI("bh", "bhojpuri"),
    MARATHI("mr", "marathi","मराठी"),
    SINHALESE("si", "sinhalese","සින්හලෙසේ"),
    //RAJASTHANI("ra", "rajasthani"),
    GUJARATI("gu", "gujarati","ગુજરાતી");


    private static final Map<String, Language> idToEnum = new HashMap<>();
    private static final Map<String, Language> nameToEnum = new HashMap<>();

    private static final List<Language> vernacLangList = new ArrayList<>();


    public static List<Language> getVernacLangList() {
        return vernacLangList;
    }

    static {
        for(Language lang : Language.values()) {
            idToEnum.put(lang.getId(), lang);
            nameToEnum.put(lang.getName(), lang);
        }
    }

    static {
        vernacLangList.add(ENGLISH);
        vernacLangList.add(HINDI);
        //todo - later add all vernac supported langs
    }

    private String id;
    private String name;
    private String displayName;

    private Language(String id, String name, String displayName) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static Language getLanguageById(String id) {
        if(StringUtils.isEmpty(id)) {
            return Language.ENGLISH;
        }
        Language lang = idToEnum.get(id);
        if(null ==  lang){
            lang = Language.ENGLISH;
        }
        return lang;
    }
    public static Language getLanguageByName(String name) {
        if(StringUtils.isEmpty(name)) {
            return Language.ENGLISH;
        }
        Language lang = nameToEnum.get(name);
        if(null ==  lang){
            lang = Language.ENGLISH;
        }
        return lang;
    }
    
    public static Language getLanguageByIdWithoutDefault(String id) {
        if (id != null)
            id = id.toLowerCase();
        return idToEnum.get(id);
    }

    public static Language getLanguageByLangId(String id) {
        if(StringUtils.isEmpty(id)) {
            return Language.ENGLISH;
        }
        Language lang = idToEnum.get(id);
        if(null ==  lang){
            lang = Language.ENGLISH;
        }
        return lang;
    }

    public static Set<String> getAllLanguageNames()
    {
        return nameToEnum.keySet();
    }

    public String toJson() {
        JSONObject obj = toJsonObject();
        return obj.toJSONString();
    }

    @SuppressWarnings("unchecked")
    public JSONObject toJsonObject() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("name", name);
        return obj;
    }
}
