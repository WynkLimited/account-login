package com.wynk.constants;

import java.util.ArrayList;
import java.util.List;
public class OnboardingPages {

    public static final String ONBOARDING_LANGUAGE = "onboardingLanguage";
    public static final String ONBOARDING_ARTIST = "onboardingArtist";

    private static final List<String> listOfPages;

    static {
        listOfPages = new ArrayList<>();
        listOfPages.add(ONBOARDING_LANGUAGE);
        listOfPages.add(ONBOARDING_ARTIST);
    }

    public static List<String> getListOfPages() {
        return listOfPages;
    }
}
