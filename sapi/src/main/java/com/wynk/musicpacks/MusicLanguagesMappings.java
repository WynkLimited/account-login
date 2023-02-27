package com.wynk.musicpacks;

import com.wynk.common.Circle;
import com.wynk.common.Language;
import com.wynk.music.constants.MusicContentLanguage;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bhuvangupta on 12/13.
 *
 * Note: The first 3 languages of each circle are primary languages of the corresponding circle.  Next 13 languages are secondary languages of the circle.
 */
public class MusicLanguagesMappings {

    private static Map<Circle,List<Language>> circleLanguageMap = new HashMap<>();

    static
    {
        List<Language> languages = Arrays
                .asList(new Language[] { Language.TELUGU, Language.ENGLISH, Language.HINDI, Language.TAMIL });
        circleLanguageMap.put(Circle.ANDHRAPRADESH,languages);

        languages = Arrays.asList(new Language[] { Language.ENGLISH, Language.HINDI, Language.TAMIL, Language.TELUGU });
        circleLanguageMap.put(Circle.ASSAM,languages);

        languages = Arrays.asList(new Language[] { Language.HINDI, Language.ENGLISH, Language.TAMIL, Language.TELUGU });
        circleLanguageMap.put(Circle.BIHAR,languages);

        languages = Arrays.asList(new Language[] { Language.TAMIL, Language.ENGLISH, Language.TELUGU, Language.HINDI });
        circleLanguageMap.put(Circle.CHENNAI,languages);

        languages = Arrays.asList(new Language[] { Language.TAMIL, Language.ENGLISH, Language.TELUGU, Language.HINDI });
        circleLanguageMap.put(Circle.TAMILNADU,languages);

        languages = Arrays.asList(new Language[] { Language.ENGLISH, Language.HINDI, Language.TAMIL, Language.TELUGU });
        circleLanguageMap.put(Circle.DELHI,languages);

        languages = Arrays.asList(new Language[] { Language.ENGLISH, Language.HINDI, Language.TAMIL, Language.TELUGU });
        circleLanguageMap.put(Circle.GUJRAT,languages);

        languages = Arrays.asList(new Language[] { Language.ENGLISH, Language.HINDI, Language.TAMIL, Language.TELUGU });
        circleLanguageMap.put(Circle.HARYANA,languages);

        languages = Arrays.asList(new Language[] { Language.ENGLISH, Language.HINDI, Language.TAMIL, Language.TELUGU });
        circleLanguageMap.put(Circle.HIMACHAL,languages);

        languages = Arrays.asList(new Language[] { Language.ENGLISH, Language.HINDI, Language.TAMIL, Language.TELUGU });
        circleLanguageMap.put(Circle.JAMMUKASHMIR,languages);

        languages = Arrays.asList(new Language[] { Language.ENGLISH, Language.TELUGU, Language.HINDI, Language.TAMIL });
        circleLanguageMap.put(Circle.KARNATAKA,languages);

        languages = Arrays.asList(new Language[] { Language.ENGLISH, Language.TAMIL, Language.TELUGU, Language.HINDI });
        circleLanguageMap.put(Circle.KERALA,languages);


        languages = Arrays.asList(new Language[] { Language.ENGLISH, Language.HINDI, Language.TAMIL, Language.TELUGU });
        circleLanguageMap.put(Circle.KOLKATA,languages);

        languages = Arrays.asList(new Language[] { Language.ENGLISH, Language.HINDI, Language.TAMIL, Language.TELUGU });
        circleLanguageMap.put(Circle.WESTBENGAL,languages);

        languages = Arrays.asList(new Language[] { Language.ENGLISH, Language.HINDI, Language.TAMIL, Language.TELUGU });
        circleLanguageMap.put(Circle.MADHYAPRADESH,languages);

        languages = Arrays.asList(new Language[] { Language.ENGLISH, Language.HINDI, Language.TAMIL, Language.TELUGU });
        circleLanguageMap.put(Circle.MAHARASHTRA,languages);

        languages = Arrays.asList(new Language[] { Language.ENGLISH, Language.HINDI, Language.TAMIL, Language.TELUGU });
        circleLanguageMap.put(Circle.MUMBAI,languages);


        languages = Arrays.asList(new Language[] { Language.ENGLISH, Language.HINDI, Language.TAMIL, Language.TELUGU });
        circleLanguageMap.put(Circle.NORTHEAST,languages);

        languages = Arrays.asList(new Language[] { Language.ENGLISH, Language.HINDI, Language.TAMIL, Language.TELUGU });
        circleLanguageMap.put(Circle.ORISSA,languages);


        languages = Arrays.asList(new Language[] { Language.ENGLISH, Language.HINDI, Language.TAMIL, Language.TELUGU });
        circleLanguageMap.put(Circle.PUNJAB,languages);


        languages = Arrays.asList(new Language[] { Language.ENGLISH, Language.HINDI, Language.TAMIL, Language.TELUGU });
        circleLanguageMap.put(Circle.RAJSTHAN,languages);

        languages = Arrays.asList(new Language[] { Language.HINDI, Language.ENGLISH, Language.TAMIL, Language.TELUGU });
        circleLanguageMap.put(Circle.UPEAST,languages);


        languages = Arrays.asList(new Language[] { Language.ENGLISH, Language.HINDI, Language.TAMIL, Language.TELUGU });
        circleLanguageMap.put(Circle.UPWEST,languages);


        languages = Arrays.asList(new Language[] { Language.ENGLISH, Language.HINDI, Language.TAMIL, Language.TELUGU });
        circleLanguageMap.put(Circle.ALL,languages);
    }






    private static Map<Circle,List<MusicContentLanguage>> circleContentLanguageMap = new HashMap<>();

    static
    {
        List<MusicContentLanguage> languages = Arrays
                .asList(new MusicContentLanguage[] { MusicContentLanguage.TELUGU, MusicContentLanguage.ENGLISH,
                        MusicContentLanguage.HINDI, MusicContentLanguage.TAMIL, MusicContentLanguage.KANNADA,
                        MusicContentLanguage.MALAYALAM, MusicContentLanguage.MARATHI, MusicContentLanguage.GUJRATI,
                        MusicContentLanguage.PUNJABI, MusicContentLanguage.BENGALI, MusicContentLanguage.BHOJPURI,
                        MusicContentLanguage.RAJASTHANI, MusicContentLanguage.ORIYA, MusicContentLanguage.ASSAMESE, MusicContentLanguage.HARYANVI });
        circleContentLanguageMap.put(Circle.ANDHRAPRADESH, languages);

        languages = Arrays
                .asList(new MusicContentLanguage[] { MusicContentLanguage.ASSAMESE, MusicContentLanguage.ENGLISH,
                        MusicContentLanguage.HINDI, MusicContentLanguage.BENGALI, MusicContentLanguage.ORIYA,
                        MusicContentLanguage.BHOJPURI, MusicContentLanguage.RAJASTHANI, MusicContentLanguage.MARATHI,
                        MusicContentLanguage.GUJRATI, MusicContentLanguage.PUNJABI, MusicContentLanguage.TAMIL,
                        MusicContentLanguage.KANNADA, MusicContentLanguage.MALAYALAM, MusicContentLanguage.TELUGU , MusicContentLanguage.HARYANVI });
        circleContentLanguageMap.put(Circle.ASSAM, languages);

        languages = Arrays
                .asList(new MusicContentLanguage[] { MusicContentLanguage.HINDI, MusicContentLanguage.BHOJPURI,
                        MusicContentLanguage.ENGLISH, MusicContentLanguage.BENGALI, MusicContentLanguage.PUNJABI,
                        MusicContentLanguage.ORIYA, MusicContentLanguage.ASSAMESE, MusicContentLanguage.MARATHI,
                        MusicContentLanguage.RAJASTHANI, MusicContentLanguage.GUJRATI, MusicContentLanguage.TAMIL,
                        MusicContentLanguage.KANNADA, MusicContentLanguage.TELUGU, MusicContentLanguage.MALAYALAM , MusicContentLanguage.HARYANVI });
        circleContentLanguageMap.put(Circle.BIHAR, languages);

        languages = Arrays.asList(new MusicContentLanguage[] { MusicContentLanguage.TAMIL, MusicContentLanguage.ENGLISH,
                MusicContentLanguage.HINDI, MusicContentLanguage.TELUGU, MusicContentLanguage.KANNADA,
                MusicContentLanguage.MALAYALAM, MusicContentLanguage.MARATHI, MusicContentLanguage.GUJRATI,
                MusicContentLanguage.PUNJABI, MusicContentLanguage.BENGALI, MusicContentLanguage.BHOJPURI,
                MusicContentLanguage.RAJASTHANI, MusicContentLanguage.ORIYA, MusicContentLanguage.ASSAMESE, MusicContentLanguage.HARYANVI });
        circleContentLanguageMap.put(Circle.CHENNAI, languages);

        languages = Arrays.asList(new MusicContentLanguage[] { MusicContentLanguage.TAMIL, MusicContentLanguage.ENGLISH,
                MusicContentLanguage.HINDI, MusicContentLanguage.TELUGU, MusicContentLanguage.KANNADA,
                MusicContentLanguage.MALAYALAM, MusicContentLanguage.MARATHI, MusicContentLanguage.GUJRATI,
                MusicContentLanguage.PUNJABI, MusicContentLanguage.BENGALI, MusicContentLanguage.BHOJPURI,
                MusicContentLanguage.RAJASTHANI, MusicContentLanguage.ORIYA, MusicContentLanguage.ASSAMESE, MusicContentLanguage.HARYANVI });
        circleContentLanguageMap.put(Circle.TAMILNADU,languages);

        languages = Arrays.asList(new MusicContentLanguage[] { MusicContentLanguage.HINDI, MusicContentLanguage.ENGLISH,
                MusicContentLanguage.PUNJABI, MusicContentLanguage.RAJASTHANI, MusicContentLanguage.BHOJPURI,
                MusicContentLanguage.TAMIL, MusicContentLanguage.KANNADA, MusicContentLanguage.MALAYALAM,
                MusicContentLanguage.TELUGU, MusicContentLanguage.MARATHI, MusicContentLanguage.BENGALI,
                MusicContentLanguage.ORIYA, MusicContentLanguage.ASSAMESE, MusicContentLanguage.GUJRATI, MusicContentLanguage.HARYANVI });
        circleContentLanguageMap.put(Circle.DELHI,languages);

        languages = Arrays.asList(new MusicContentLanguage[] { MusicContentLanguage.HINDI, MusicContentLanguage.GUJRATI,
                MusicContentLanguage.ENGLISH, MusicContentLanguage.RAJASTHANI, MusicContentLanguage.MARATHI,
                MusicContentLanguage.PUNJABI, MusicContentLanguage.BHOJPURI, MusicContentLanguage.BENGALI,
                MusicContentLanguage.ORIYA, MusicContentLanguage.ASSAMESE, MusicContentLanguage.TAMIL,
                MusicContentLanguage.KANNADA, MusicContentLanguage.MALAYALAM, MusicContentLanguage.TELUGU , MusicContentLanguage.HARYANVI});
        circleContentLanguageMap.put(Circle.GUJRAT,languages);

        languages = Arrays.asList(new MusicContentLanguage[] { MusicContentLanguage.HINDI, MusicContentLanguage.PUNJABI,
                MusicContentLanguage.ENGLISH, MusicContentLanguage.RAJASTHANI, MusicContentLanguage.BHOJPURI,
                MusicContentLanguage.MARATHI, MusicContentLanguage.BENGALI, MusicContentLanguage.ORIYA,
                MusicContentLanguage.ASSAMESE, MusicContentLanguage.GUJRATI, MusicContentLanguage.TAMIL,
                MusicContentLanguage.TELUGU, MusicContentLanguage.MALAYALAM, MusicContentLanguage.KANNADA, MusicContentLanguage.HARYANVI });
        circleContentLanguageMap.put(Circle.HARYANA, languages);

        languages = Arrays.asList(new MusicContentLanguage[] { MusicContentLanguage.HINDI, MusicContentLanguage.ENGLISH,
                MusicContentLanguage.PUNJABI, MusicContentLanguage.BHOJPURI, MusicContentLanguage.MARATHI,
                MusicContentLanguage.BENGALI, MusicContentLanguage.ORIYA, MusicContentLanguage.ASSAMESE,
                MusicContentLanguage.GUJRATI, MusicContentLanguage.TAMIL, MusicContentLanguage.KANNADA,
                MusicContentLanguage.MALAYALAM, MusicContentLanguage.TELUGU, MusicContentLanguage.RAJASTHANI , MusicContentLanguage.HARYANVI});
        circleContentLanguageMap.put(Circle.HIMACHAL, languages);

        languages = Arrays.asList(new MusicContentLanguage[] { MusicContentLanguage.HINDI, MusicContentLanguage.ENGLISH,
                MusicContentLanguage.PUNJABI, MusicContentLanguage.BHOJPURI, MusicContentLanguage.RAJASTHANI,
                MusicContentLanguage.MARATHI, MusicContentLanguage.BENGALI, MusicContentLanguage.ORIYA,
                MusicContentLanguage.ASSAMESE, MusicContentLanguage.GUJRATI, MusicContentLanguage.TAMIL,
                MusicContentLanguage.TELUGU, MusicContentLanguage.MALAYALAM, MusicContentLanguage.KANNADA, MusicContentLanguage.HARYANVI });
        circleContentLanguageMap.put(Circle.JAMMUKASHMIR, languages);

        languages = Arrays
                .asList(new MusicContentLanguage[] { MusicContentLanguage.KANNADA, MusicContentLanguage.ENGLISH,
                        MusicContentLanguage.HINDI, MusicContentLanguage.TAMIL, MusicContentLanguage.TELUGU,
                        MusicContentLanguage.MALAYALAM, MusicContentLanguage.MARATHI, MusicContentLanguage.GUJRATI,
                        MusicContentLanguage.PUNJABI, MusicContentLanguage.BENGALI, MusicContentLanguage.BHOJPURI,
                        MusicContentLanguage.RAJASTHANI, MusicContentLanguage.ORIYA, MusicContentLanguage.ASSAMESE, MusicContentLanguage.HARYANVI });
        circleContentLanguageMap.put(Circle.KARNATAKA, languages);

        languages = Arrays
                .asList(new MusicContentLanguage[] { MusicContentLanguage.MALAYALAM, MusicContentLanguage.ENGLISH,
                        MusicContentLanguage.HINDI, MusicContentLanguage.TAMIL, MusicContentLanguage.KANNADA,
                        MusicContentLanguage.TELUGU, MusicContentLanguage.MARATHI, MusicContentLanguage.GUJRATI,
                        MusicContentLanguage.PUNJABI, MusicContentLanguage.BENGALI, MusicContentLanguage.BHOJPURI,
                        MusicContentLanguage.RAJASTHANI, MusicContentLanguage.ORIYA, MusicContentLanguage.ASSAMESE, MusicContentLanguage.HARYANVI });
        circleContentLanguageMap.put(Circle.KERALA, languages);

        languages = Arrays.asList(new MusicContentLanguage[] { MusicContentLanguage.BENGALI, MusicContentLanguage.ORIYA,
                MusicContentLanguage.ENGLISH, MusicContentLanguage.HINDI, MusicContentLanguage.BHOJPURI,
                MusicContentLanguage.ASSAMESE, MusicContentLanguage.MARATHI, MusicContentLanguage.GUJRATI,
                MusicContentLanguage.PUNJABI, MusicContentLanguage.RAJASTHANI, MusicContentLanguage.TAMIL,
                MusicContentLanguage.TELUGU, MusicContentLanguage.MALAYALAM, MusicContentLanguage.KANNADA, MusicContentLanguage.HARYANVI });
        circleContentLanguageMap.put(Circle.KOLKATA,languages);

        languages = Arrays
                .asList(new MusicContentLanguage[] { MusicContentLanguage.BENGALI, MusicContentLanguage.ENGLISH,
                        MusicContentLanguage.HINDI, MusicContentLanguage.BHOJPURI, MusicContentLanguage.ASSAMESE,
                        MusicContentLanguage.PUNJABI, MusicContentLanguage.MARATHI, MusicContentLanguage.GUJRATI,
                        MusicContentLanguage.RAJASTHANI, MusicContentLanguage.TAMIL, MusicContentLanguage.KANNADA,
                        MusicContentLanguage.MALAYALAM, MusicContentLanguage.TELUGU, MusicContentLanguage.ORIYA, MusicContentLanguage.HARYANVI });
        circleContentLanguageMap.put(Circle.WESTBENGAL, languages);

        languages = Arrays.asList(new MusicContentLanguage[] { MusicContentLanguage.HINDI, MusicContentLanguage.ENGLISH,
                MusicContentLanguage.MARATHI, MusicContentLanguage.GUJRATI, MusicContentLanguage.RAJASTHANI,
                MusicContentLanguage.PUNJABI, MusicContentLanguage.BENGALI, MusicContentLanguage.ORIYA,
                MusicContentLanguage.ASSAMESE, MusicContentLanguage.BHOJPURI, MusicContentLanguage.TAMIL,
                MusicContentLanguage.KANNADA, MusicContentLanguage.MALAYALAM, MusicContentLanguage.TELUGU, MusicContentLanguage.HARYANVI });
        circleContentLanguageMap.put(Circle.MADHYAPRADESH, languages);

        languages = Arrays.asList(new MusicContentLanguage[] { MusicContentLanguage.HINDI, MusicContentLanguage.MARATHI,
                MusicContentLanguage.ENGLISH, MusicContentLanguage.GUJRATI, MusicContentLanguage.PUNJABI,
                MusicContentLanguage.BENGALI, MusicContentLanguage.ORIYA, MusicContentLanguage.BHOJPURI,
                MusicContentLanguage.ASSAMESE, MusicContentLanguage.RAJASTHANI, MusicContentLanguage.TAMIL,
                MusicContentLanguage.KANNADA, MusicContentLanguage.MALAYALAM, MusicContentLanguage.TELUGU, MusicContentLanguage.HARYANVI });
        circleContentLanguageMap.put(Circle.MAHARASHTRA, languages);

        languages = Arrays.asList(new MusicContentLanguage[] { MusicContentLanguage.HINDI, MusicContentLanguage.ENGLISH,
                MusicContentLanguage.MARATHI, MusicContentLanguage.GUJRATI, MusicContentLanguage.RAJASTHANI,
                MusicContentLanguage.PUNJABI, MusicContentLanguage.BENGALI, MusicContentLanguage.ORIYA,
                MusicContentLanguage.BHOJPURI, MusicContentLanguage.ASSAMESE, MusicContentLanguage.TAMIL,
                MusicContentLanguage.KANNADA, MusicContentLanguage.MALAYALAM, MusicContentLanguage.TELUGU, MusicContentLanguage.HARYANVI });
        circleContentLanguageMap.put(Circle.MUMBAI, languages);


        languages = Arrays
                .asList(new MusicContentLanguage[] { MusicContentLanguage.ASSAMESE, MusicContentLanguage.ENGLISH,
                        MusicContentLanguage.HINDI, MusicContentLanguage.BENGALI, MusicContentLanguage.ORIYA,
                        MusicContentLanguage.PUNJABI, MusicContentLanguage.BHOJPURI, MusicContentLanguage.RAJASTHANI,
                        MusicContentLanguage.GUJRATI, MusicContentLanguage.TAMIL, MusicContentLanguage.KANNADA,
                        MusicContentLanguage.MALAYALAM, MusicContentLanguage.TELUGU, MusicContentLanguage.MARATHI, MusicContentLanguage.HARYANVI });
        circleContentLanguageMap.put(Circle.NORTHEAST, languages);

        languages = Arrays.asList(new MusicContentLanguage[] { MusicContentLanguage.ORIYA, MusicContentLanguage.HINDI,
                MusicContentLanguage.ENGLISH, MusicContentLanguage.BENGALI, MusicContentLanguage.TELUGU,
                MusicContentLanguage.ASSAMESE, MusicContentLanguage.BHOJPURI, MusicContentLanguage.MARATHI,
                MusicContentLanguage.GUJRATI, MusicContentLanguage.PUNJABI, MusicContentLanguage.RAJASTHANI,
                MusicContentLanguage.TAMIL, MusicContentLanguage.MALAYALAM, MusicContentLanguage.KANNADA, MusicContentLanguage.HARYANVI });
        circleContentLanguageMap.put(Circle.ORISSA, languages);

        languages = Arrays.asList(new MusicContentLanguage[] { MusicContentLanguage.PUNJABI, MusicContentLanguage.HINDI,
                MusicContentLanguage.ENGLISH, MusicContentLanguage.RAJASTHANI, MusicContentLanguage.GUJRATI,
                MusicContentLanguage.BENGALI, MusicContentLanguage.ORIYA, MusicContentLanguage.ASSAMESE,
                MusicContentLanguage.MARATHI, MusicContentLanguage.BHOJPURI, MusicContentLanguage.TAMIL,
                MusicContentLanguage.KANNADA, MusicContentLanguage.MALAYALAM, MusicContentLanguage.TELUGU, MusicContentLanguage.HARYANVI });
        circleContentLanguageMap.put(Circle.PUNJAB, languages);

        languages = Arrays
                .asList(new MusicContentLanguage[] { MusicContentLanguage.HINDI, MusicContentLanguage.RAJASTHANI,
                        MusicContentLanguage.ENGLISH, MusicContentLanguage.GUJRATI, MusicContentLanguage.PUNJABI,
                        MusicContentLanguage.BENGALI, MusicContentLanguage.ORIYA, MusicContentLanguage.ASSAMESE,
                        MusicContentLanguage.MARATHI, MusicContentLanguage.BHOJPURI, MusicContentLanguage.TAMIL,
                        MusicContentLanguage.KANNADA, MusicContentLanguage.MALAYALAM, MusicContentLanguage.TELUGU , MusicContentLanguage.HARYANVI});
        circleContentLanguageMap.put(Circle.RAJSTHAN, languages);

        languages = Arrays.asList(new MusicContentLanguage[] { MusicContentLanguage.HINDI, MusicContentLanguage.ENGLISH,
                MusicContentLanguage.BHOJPURI, MusicContentLanguage.BENGALI, MusicContentLanguage.MARATHI,
                MusicContentLanguage.ORIYA, MusicContentLanguage.ASSAMESE, MusicContentLanguage.GUJRATI,
                MusicContentLanguage.PUNJABI, MusicContentLanguage.RAJASTHANI, MusicContentLanguage.TAMIL,
                MusicContentLanguage.KANNADA, MusicContentLanguage.MALAYALAM, MusicContentLanguage.TELUGU , MusicContentLanguage.HARYANVI});
        circleContentLanguageMap.put(Circle.UPEAST, languages);

        languages = Arrays.asList(new MusicContentLanguage[] { MusicContentLanguage.HINDI, MusicContentLanguage.ENGLISH,
                MusicContentLanguage.PUNJABI, MusicContentLanguage.RAJASTHANI, MusicContentLanguage.BENGALI,
                MusicContentLanguage.GUJRATI, MusicContentLanguage.MALAYALAM, MusicContentLanguage.MARATHI,
                MusicContentLanguage.ORIYA, MusicContentLanguage.ASSAMESE, MusicContentLanguage.BHOJPURI,
                MusicContentLanguage.TAMIL, MusicContentLanguage.KANNADA, MusicContentLanguage.TELUGU, MusicContentLanguage.HARYANVI });
        circleContentLanguageMap.put(Circle.UPWEST, languages);

        languages = Arrays.asList(new MusicContentLanguage[] { MusicContentLanguage.HINDI, MusicContentLanguage.ENGLISH,
                MusicContentLanguage.PUNJABI, MusicContentLanguage.RAJASTHANI, MusicContentLanguage.BHOJPURI,
                MusicContentLanguage.TAMIL, MusicContentLanguage.KANNADA, MusicContentLanguage.MALAYALAM,
                MusicContentLanguage.TELUGU, MusicContentLanguage.GUJRATI, MusicContentLanguage.MARATHI,
                MusicContentLanguage.BENGALI, MusicContentLanguage.ORIYA, MusicContentLanguage.ASSAMESE , MusicContentLanguage.HARYANVI});
        circleContentLanguageMap.put(Circle.ALL, languages);
    }


    private static List<Language> defaultLanguages = Arrays.asList(new Language[] { Language.ENGLISH, Language.HINDI });
    public static List<Language> getLanguagesForCircle(String circleId)
    {
        return defaultLanguages;
//        List<Language> languages = null;
//        if(StringUtils.isBlank(circleId) || circleId.equalsIgnoreCase("-") || circleId.equalsIgnoreCase("all")
//                || circleId.equalsIgnoreCase("pan"))
//            languages = circleLanguageMap.get(Circle.ALL);
//        else
//            languages = circleLanguageMap.get(Circle.getCircleById(circleId));
//
//        if(languages == null)
//            languages = circleLanguageMap.get(Circle.ALL);
//        return languages;
    }


    

    public static List<MusicContentLanguage> getContentLanguagesForCircle(String circleId)
    {
        if(StringUtils.isBlank(circleId) || circleId.equalsIgnoreCase("-") || circleId.equalsIgnoreCase("all")
                || circleId.equalsIgnoreCase("pan"))
            return circleContentLanguageMap.get(Circle.ALL);

        List<MusicContentLanguage> languages = circleContentLanguageMap.get(Circle.getCircleById(circleId));
        if(languages == null)
            languages = circleContentLanguageMap.get(Circle.ALL);

        return languages;
    }

    public static boolean isPrimaryContentLanguage(String circleId, MusicContentLanguage cLang)
    {
        if(cLang == null || circleId == null)
            return false;
        List<MusicContentLanguage> languages = circleContentLanguageMap.get(Circle.getCircleById(circleId));
        if(languages == null)
            languages = circleContentLanguageMap.get(Circle.ALL);

        int cindex = languages.indexOf(cLang);
        if(cindex >= 0 && cindex < 3)
            return true;
        return false;
    }


    public static MusicContentLanguage getContentLangForCircle(String circle)
    {
        Circle crcle = Circle.getCircleById(circle);
        if(crcle == null)
            return  null;

        if(crcle == Circle.ANDHRAPRADESH)
            return MusicContentLanguage.TELUGU;
        else if(crcle == Circle.BIHAR)
            return MusicContentLanguage.BHOJPURI;
        else if(crcle == Circle.CHENNAI)
            return MusicContentLanguage.TAMIL;
        else if(crcle == Circle.TAMILNADU)
            return MusicContentLanguage.TAMIL;
        else if(crcle == Circle.KARNATAKA)
            return MusicContentLanguage.KANNADA;
        else if(crcle == Circle.PUNJAB)
            return MusicContentLanguage.PUNJABI;
        else if(crcle == Circle.WESTBENGAL)
            return MusicContentLanguage.BENGALI;
        else if(crcle == Circle.KOLKATA)
            return MusicContentLanguage.BENGALI;
        else if(crcle == Circle.MAHARASHTRA)
            return MusicContentLanguage.MARATHI;
        else if(crcle == Circle.RAJSTHAN)
            return MusicContentLanguage.RAJASTHANI;
        else if(crcle == Circle.GUJRAT)
            return MusicContentLanguage.GUJRATI;
        else if(crcle == Circle.KERALA)
            return MusicContentLanguage.MALAYALAM;
        else if(crcle == Circle.ORISSA)
            return MusicContentLanguage.ORIYA;
        else if(crcle == Circle.HARYANA)
            return MusicContentLanguage.HARYANVI;


        return null;
    }
}
