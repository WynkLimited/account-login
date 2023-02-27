package com.wynk.user.dto;

public class UserEntityKey {
    public static final String id = "id";

    public static final String uid = "uid";

    public static final String msisdn = "msisdn";

    public static final String name = "na";

    public static final String lname = "ln";

    public static final String email = "em";

    public static final String lang = "lg";

    public static final String gender = "gn";

    public static final String creationDate = "cd";

    public static final String lastActivityDate = "lad";

    public static final String circle = "cl";

    public static final String countryId = "cid";

    public static final String songQuality = "sq";

    public static final String avatar = "av";

    public static final String notifications = "notif";

    public static final String fbToken = "fbt";

    public static final String ndsTS = "ndsTS";

    public static final String iTunesSubscription = "its";

    public static final String contentLanguages = "clang";

    public static final String onboardingLanguages = "oblang";

    public static final String podcastCategories = "podcast_categories";

    public static final String currentOfferIds = "coids";

    public static final String devices = "ud";

    public static final String operator = "op";

    public static final String platform = "plt";

    public static final String downloadQuality = "dq";

    public static final String packs = "packs";

    public static final String token = "tkn";

    public static final String userType = "ut";

    public static final String subscription = "wcfs";

    public static final String userSubscription = "subs";

    public static final String preferredLang = "pl";

    public static final String source = "src";
    public static final String autoRenewal = "ar";
    public static final String lastAutoRenewalOffSettingTimestamp = "laost";
    public static final String isSystemGeneratedContentLang = "sgcl";

    public static final String expireAt = "eat";

    public static final String autoPlaylists = "apl";

    public static final String vasDND = "vasdnd";

    public static final String isDeleted = "isDeleted";

    public interface Dob {
        public String dob = "dob";
        public String date = "date";
        public String month = "month";
        public String year = "year";
    }

    public interface FupPack {

        public String FupPack = "FupPack";
        public String creationTime = "cd";

        public String packValidity = "pv";

        public String shownFUPWarning = "fw";

        public String shownFUP95Warning = "fw95";

        public String streamedCount = "sc";

        public String lastFUPResetDate = "lrd";

        public String rentalsCount = "rc";

    }

    public interface WCFSubcription{
        String expireTS = "ets";
        String offerTS = "ots";
        String productId = "pdId";
        String recommendedProductId = "rpdId";
        String eligibleOfferProductId = "eopdId";
        String isSubscribed = "isSub";
        String lastUpdatedTS = "lts";
        String renewalConsent="rc";
    }

    public interface UserSubscription{
        String productIds = "pdIds";
        String productId = "pdId";
        String offerId = "offerId";
        String expireTS = "ets";
        String offerTS = "ots";
    }

    interface WynkBasicKeys {
        /** selected artist at time of wynk basic onboarding */
        String basicSelectedArtist = "basic_osa";

        /** selected playlists at time of wynk basic onboarding */
        String basicSelectedPlaylist = "basic_osp";

        String basicHasManuallySelectedLang = "basic_hmsl";

        String basicContentLanguages = "basic_clang";
    }
}
