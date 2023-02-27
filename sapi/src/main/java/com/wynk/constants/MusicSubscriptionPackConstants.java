package com.wynk.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public interface MusicSubscriptionPackConstants {

    public static final long          DAY                               = 24 * 3600 * 1000L;
    
    public static final long          HOUR                              = 3600 * 1000L;

    public static final int           PRE_REMINDER_PERIOD               = 3;

    public static final int           GRACE_PERIOD                      = 3;                                                                                                  // 2;

    public static final int           SUSPENTION_PERIOD                 = 85;                                                                                                 // 3;

    public static final int           PACK_VALIDITY                     = 30;                                                                                                 // 5;

    public static final int           POSTPAID_PACK_VALIDITY            = 365;

    public static final String        UNCONSUMED_NOTIFICATIONS          = "unConsumedSENotification";

    public static final String        UNMAPPED_SUBSCRIPTION_ERROR_CODES = "unmappedSubscriptionErrorCodes";

    public static final int           IBM_OFFER_MUSIC_PACK_ID_29        = 74008;                                                                                              // 73978;

    public static final int           IBM_MUSIC_PACK_ID_29              = 74009;                                                                                              // 73978;

    public static final int           IBM_OFFER_MUSIC_PACK_ID_99        = 74010;                                                                                              // 73978;

    public static final int           IBM_MUSIC_PACK_ID_99              = 74007;                                                                                              // 73978;

    public static final int           MUSIC_PACK_ID_FREE_ONE_MONTH      = 77000;

    public static final int           MUSIC_PACK_ID_AIRTEL_SURPRISES    = 40041;

    public static final int           MUSIC_PACK_ID_FREE_THREE_MONTH_NON_AIRTEL     = 77001;

    public static final int           BSB_MUSIC_PACK_ID                 = 1;

    public static final int           BSB_MUSIC_IPAYY_PACK_ID           = 2;

    public static final int           BSB_MUSIC_IPAYY_IBM_29_PACK_ID    = 3;

    public static final int           BSB_MUSIC_IPAYY_IBM_129_PACK_ID   = 4;

    public static final int           BSB_MUSIC_IDEA_3_MONTH   = 6;
    public static final int           BSB_MUSIC_VODA_3_MONTH   = 7;

    public static final int           BSB_MUSIC_IDEA_99_PAID   = 8;

    //    public static final int           MUSIC_SUBSCRIPTION_PRODUCT_ID     = 73978;                                                                                              // 73823;
    //    public static final int           MUSIC_SUBSCRIPTION_PRICE          = 29;                                                                                                 // 10;

    public static final int           ITUNES_AIRTEL_MUSIC_PACK_ID       = 88001;

    public static final int           ITUNES_NON_AIRTEL_MUSIC_PACK_ID   = 88002;

    public static final int           ITUNES_AIRTEL_PACK_USER_MUSIC_PACK_ID       = 88003;

    public static final int           ITUNES_NON_AIRTEL_PACK_USER_MUSIC_PACK_ID   = 88004;
    
    public static final int           SAMSUNG_SDK_PACK     		= 9;
    public static final int           SAMSUNG_SDK_FREE_PACK     = 10;


    public static final List<Integer> PAID_PACKS_MUSIC          = Collections.unmodifiableList(Arrays.asList(BSB_MUSIC_PACK_ID,
            BSB_MUSIC_IPAYY_IBM_29_PACK_ID, BSB_MUSIC_IPAYY_IBM_129_PACK_ID,
            ITUNES_AIRTEL_MUSIC_PACK_ID, ITUNES_NON_AIRTEL_MUSIC_PACK_ID,
            BSB_MUSIC_IDEA_99_PAID,
            IBM_OFFER_MUSIC_PACK_ID_29, IBM_MUSIC_PACK_ID_29, IBM_OFFER_MUSIC_PACK_ID_99,
            IBM_MUSIC_PACK_ID_99, SAMSUNG_SDK_PACK));

    public static final int           LAPU_PREPAID_MUSIC_PACK_ID_29_PAPER = 64009;

    public static final int           LAPU_PREPAID_MUSIC_PACK_ID_29_CLM = 64005;

    public static final int           LAPU_PREPAID_MUSIC_PACK_ID_129    = 64007;

    public static final int           LAPU_PREPAID_MUSIC_PACK_ID_29     = 64001;

    public static final int           LAPU_PREPAID_MUSIC_PACK_ID_29_3G  = 64003;

    public static final int           LAPU_PREPAID_MUSIC_PACK_ID_3G_29     = 65000;

    public static final int           RAPU_PREPAID_MUSIC_PACK_29     = 41027;


    public static final int           PROMO_CODE_MUSIC_PACK_ID_129    = 34000;

    public static final int           PROMO_CODE_MUSIC_PACK_ID_29     = 34001;

    public static final int           PROMO_CODE_MUSIC_PACK_ID_NON_AIRTEL_29     = 34002;


    public static final int           PROMO_CODE_MUSIC_PACK_ID_ONE_MONTH        = 34010;
    public static final int           PROMO_CODE_MUSIC_PACK_ID_THREE_MONTHS     = 34011;
    public static final int           PROMO_CODE_MUSIC_PACK_ID_SIX_MONTHS       = 34012;
    public static final int           PROMO_CODE_MUSIC_PACK_ID_TWELVE_MONTHS    = 34013;

    public static final int           PROMO_CODE_MUSIC_PACK_ID_ONE_MONTH_NON_AIRTEL        = 34014;
    public static final int           PROMO_CODE_MUSIC_PACK_ID_THREE_MONTHS_NON_AIRTEL     = 34015;
    public static final int           PROMO_CODE_MUSIC_PACK_ID_SIX_MONTHS_NON_AIRTEL       = 34016;
    public static final int           PROMO_CODE_MUSIC_PACK_ID_TWELVE_MONTHS_NON_AIRTEL    = 34017;

    public static final int           PROMO_CODE_ONE_MONTH_PACK_VALIDITY        = 30;
    public static final int           PROMO_CODE_THREE_MONTHS_PACK_VALIDITY     = 90;
    public static final int           BSB_IDEA_THREE_MONTHS_PACK_VALIDITY       = 90;

    public static final int           PROMO_CODE_SIX_MONTHS_PACK_VALIDITY       = 180;
    public static final int           PROMO_CODE_TWELVE_MONTHS_PACK_VALIDITY    = 365;

    public static final int           POSTPAID_ONE_PACK_ID                      = 40027;
    public static final int           POSTPAID_TWO_PACK_ID                      = 40028;

    public static final int           CPE_ONE_PACK_ID                           = 50000;
    public static final int           CPE_TWO_PACK_ID                           = 50027;

    public static final int           FOURG_INFINITY_PACK_ID                      = 40000;
    public static final int           BB_LOWER_PACK_ID                            = 40031;
    public static final int           BB_HIGHER_PACK_ID                           = 40032;
    
    public static final String        PROMO_CODE_SUNBURN                          = "WYNKIT";

    public static final List<Integer> NEW_POSTPAID_MUSIC_PACKS  =
            Collections.unmodifiableList(Arrays.asList(CPE_ONE_PACK_ID, CPE_TWO_PACK_ID, POSTPAID_ONE_PACK_ID, POSTPAID_TWO_PACK_ID, FOURG_INFINITY_PACK_ID,
                    BB_LOWER_PACK_ID, BB_HIGHER_PACK_ID, RAPU_PREPAID_MUSIC_PACK_29));

    public static final List<Integer> PROMOTIONAL_AUTORENEW_MUSIC_PACKS  =
            Collections.unmodifiableList(Arrays.asList(CPE_ONE_PACK_ID, CPE_TWO_PACK_ID, MUSIC_PACK_ID_AIRTEL_SURPRISES, MUSIC_PACK_ID_FREE_ONE_MONTH,
                    POSTPAID_TWO_PACK_ID, POSTPAID_ONE_PACK_ID, FOURG_INFINITY_PACK_ID, BB_LOWER_PACK_ID, BB_HIGHER_PACK_ID, RAPU_PREPAID_MUSIC_PACK_29));


    public static final List<Integer> PROMO_CODE_MUSIC_PACKS       = Collections.unmodifiableList(Arrays.asList(PROMO_CODE_MUSIC_PACK_ID_ONE_MONTH,
            PROMO_CODE_MUSIC_PACK_ID_THREE_MONTHS,
            PROMO_CODE_MUSIC_PACK_ID_SIX_MONTHS,
            PROMO_CODE_MUSIC_PACK_ID_TWELVE_MONTHS,
            PROMO_CODE_MUSIC_PACK_ID_ONE_MONTH_NON_AIRTEL,
            PROMO_CODE_MUSIC_PACK_ID_THREE_MONTHS_NON_AIRTEL,
            PROMO_CODE_MUSIC_PACK_ID_SIX_MONTHS_NON_AIRTEL,
            PROMO_CODE_MUSIC_PACK_ID_TWELVE_MONTHS_NON_AIRTEL));



    public static final int           PROMO_CODE_PRICE    = 99;

    public static final int           SIX_MONTH_PACK_ID_129                     = 31001;
    public static final int           THREE_MONTH_PACK_ID_129                   = 31002;
    public static final int           BUNDLE_PACK_2_MONTHS                      = 20;
    public static final int           BUNDLE_PACK_6_MONTHS                      = 21;
    public static final int           BUNDLE_PACK_2_MONTHS_PACK_VALIDITY        = 60;
    public static final int           BUNDLE_PACK_6_MONTHS_PACK_VALIDITY        = 180;

    public static final HashMap<Integer, Integer> PRODUCT_ID_VALIDITY_MAP = new HashMap<Integer, Integer>(){{
        put(BUNDLE_PACK_2_MONTHS, BUNDLE_PACK_2_MONTHS_PACK_VALIDITY);
        put(BUNDLE_PACK_6_MONTHS, BUNDLE_PACK_6_MONTHS_PACK_VALIDITY);
        put(POSTPAID_MUSIC_SUBSCRIPTION_PRODUCT_ID, POSTPAID_PACK_VALIDITY);
        put(MUSIC_PACK_ID_FREE_ONE_MONTH, HALF_YEARLY_VALIDITY);
        put(CPE_ONE_PACK_ID, HALF_YEARLY_VALIDITY);
        put(CPE_TWO_PACK_ID, HALF_YEARLY_VALIDITY);
        put(MUSIC_PACK_ID_AIRTEL_SURPRISES, HALF_YEARLY_VALIDITY);
        put(POSTPAID_TWO_PACK_ID, HALF_YEARLY_VALIDITY);
        put(POSTPAID_ONE_PACK_ID, HALF_YEARLY_VALIDITY);
        put(BB_HIGHER_PACK_ID, HALF_YEARLY_VALIDITY);
        put(BB_LOWER_PACK_ID, HALF_YEARLY_VALIDITY);
        put(FOURG_INFINITY_PACK_ID, HALF_YEARLY_VALIDITY);
        put(RAPU_PREPAID_MUSIC_PACK_29, HALF_YEARLY_VALIDITY);
        // promo codes
        put(PROMO_CODE_MUSIC_PACK_ID_ONE_MONTH, PROMO_CODE_ONE_MONTH_PACK_VALIDITY);
        put(PROMO_CODE_MUSIC_PACK_ID_THREE_MONTHS, PROMO_CODE_THREE_MONTHS_PACK_VALIDITY);
        put(PROMO_CODE_MUSIC_PACK_ID_SIX_MONTHS, PROMO_CODE_SIX_MONTHS_PACK_VALIDITY);
        put(PROMO_CODE_MUSIC_PACK_ID_TWELVE_MONTHS, PROMO_CODE_TWELVE_MONTHS_PACK_VALIDITY);

        put(PROMO_CODE_MUSIC_PACK_ID_ONE_MONTH_NON_AIRTEL, PROMO_CODE_ONE_MONTH_PACK_VALIDITY);
        put(PROMO_CODE_MUSIC_PACK_ID_THREE_MONTHS_NON_AIRTEL, PROMO_CODE_THREE_MONTHS_PACK_VALIDITY);
        put(PROMO_CODE_MUSIC_PACK_ID_SIX_MONTHS_NON_AIRTEL, PROMO_CODE_SIX_MONTHS_PACK_VALIDITY);
        put(PROMO_CODE_MUSIC_PACK_ID_TWELVE_MONTHS_NON_AIRTEL, PROMO_CODE_TWELVE_MONTHS_PACK_VALIDITY);

        //Airtel 3 month pack for Airtel Sales team
        put(THREE_MONTH_PACK_ID_129, PROMO_CODE_THREE_MONTHS_PACK_VALIDITY);
        // Non Airtel promotional pack for 3 months
        put(MUSIC_PACK_ID_FREE_THREE_MONTH_NON_AIRTEL, PROMO_CODE_THREE_MONTHS_PACK_VALIDITY);
        put(BSB_MUSIC_IDEA_3_MONTH, BSB_IDEA_THREE_MONTHS_PACK_VALIDITY);
        put(BSB_MUSIC_VODA_3_MONTH, QUARTERLY_PACK_VALIDITY);

    }};

    // product id for postpaid subscribers
    public static final int           POSTPAID_MUSIC_SUBSCRIPTION_PRODUCT_ID     = 31000;

    public static final List<Integer> HBO_PACKS       = Collections.unmodifiableList(Arrays.asList(BUNDLE_PACK_2_MONTHS, BUNDLE_PACK_6_MONTHS));

    public static final List<Integer> POSTPAID_MUSIC_PACKS          = Collections.unmodifiableList(Arrays.asList(POSTPAID_MUSIC_SUBSCRIPTION_PRODUCT_ID));


    public static final List<Integer> LAPU_PREPAID_MUSIC_PACKS          = Collections.unmodifiableList(Arrays.asList(LAPU_PREPAID_MUSIC_PACK_ID_29,
            LAPU_PREPAID_MUSIC_PACK_ID_29_CLM,
            LAPU_PREPAID_MUSIC_PACK_ID_129,
            LAPU_PREPAID_MUSIC_PACK_ID_29_PAPER,
            LAPU_PREPAID_MUSIC_PACK_ID_29_3G,
            LAPU_PREPAID_MUSIC_PACK_ID_3G_29,
            POSTPAID_ONE_PACK_ID,
            POSTPAID_TWO_PACK_ID,
            CPE_ONE_PACK_ID,
            CPE_TWO_PACK_ID,
            FOURG_INFINITY_PACK_ID,
            BB_LOWER_PACK_ID,
            BB_HIGHER_PACK_ID,
            MUSIC_PACK_ID_AIRTEL_SURPRISES,
            RAPU_PREPAID_MUSIC_PACK_29));

    public static final List<Integer> LAPU_PREPAID_MUSIC_PACKS_29       = Collections.unmodifiableList(Arrays.asList(LAPU_PREPAID_MUSIC_PACK_ID_29,
            LAPU_PREPAID_MUSIC_PACK_ID_29_CLM,
            LAPU_PREPAID_MUSIC_PACK_ID_29_PAPER,
            LAPU_PREPAID_MUSIC_PACK_ID_29_3G,
            LAPU_PREPAID_MUSIC_PACK_ID_3G_29,
            MUSIC_PACK_ID_AIRTEL_SURPRISES));

    public static final List<Integer> IPAYY_MUSIC_PACKS       = Collections.unmodifiableList(Arrays.asList(BSB_MUSIC_IPAYY_PACK_ID, BSB_MUSIC_IPAYY_IBM_29_PACK_ID, BSB_MUSIC_IPAYY_IBM_129_PACK_ID));

    public static final String 		  ITUNES_AIRTEL_MUSIC_PACK_APPLE_ID = "in.bsb.twang.monthly";

    public static final String 		  ITUNES_NON_AIRTEL_MUSIC_PACK_APPLE_ID = "TwangApp_MonthlySubs_NonAirtel";

    public static final String 		  ITUNES_AIRTEL_MUSIC_PACK_APPLE_ID_DEV = "MusicApp_MonthlySubs";

    public static final String 		  ITUNES_NON_AIRTEL_MUSIC_PACK_APPLE_ID_DEV = "MusicApp_MonthlySubs_OTT";

    public static final List<Integer> IBM_MUSIC_PACKS                   = Collections.unmodifiableList(Arrays.asList(IBM_MUSIC_PACK_ID_29, IBM_OFFER_MUSIC_PACK_ID_29, IBM_MUSIC_PACK_ID_99,
            IBM_OFFER_MUSIC_PACK_ID_99));

    public static final List<Integer> IBM_FREE_DATA_MUSIC_PACKS         = Collections.unmodifiableList(Arrays.asList(IBM_MUSIC_PACK_ID_99, IBM_OFFER_MUSIC_PACK_ID_99, LAPU_PREPAID_MUSIC_PACK_ID_129, PROMO_CODE_MUSIC_PACK_ID_129, THREE_MONTH_PACK_ID_129));

    public static final List<Integer> IBM_MUSIC_OFFER_PACKS             = Collections.unmodifiableList(Arrays.asList(IBM_OFFER_MUSIC_PACK_ID_29, IBM_OFFER_MUSIC_PACK_ID_99));

    public static final List<Integer> YEARLY_PACKS                      = Collections.unmodifiableList(Arrays.asList(POSTPAID_MUSIC_SUBSCRIPTION_PRODUCT_ID, PROMO_CODE_MUSIC_PACK_ID_TWELVE_MONTHS, PROMO_CODE_MUSIC_PACK_ID_TWELVE_MONTHS_NON_AIRTEL));


    // for subscription engine to work add all the monthly packs here
    public static final List<Integer> MONTHLY_PACKS                     = Collections.unmodifiableList(Arrays.asList(LAPU_PREPAID_MUSIC_PACK_ID_29, LAPU_PREPAID_MUSIC_PACK_ID_29_CLM,
            LAPU_PREPAID_MUSIC_PACK_ID_129, LAPU_PREPAID_MUSIC_PACK_ID_29_PAPER, LAPU_PREPAID_MUSIC_PACK_ID_29_3G,
            IBM_MUSIC_PACK_ID_29, IBM_OFFER_MUSIC_PACK_ID_29, IBM_MUSIC_PACK_ID_99, IBM_OFFER_MUSIC_PACK_ID_99,
            ITUNES_AIRTEL_MUSIC_PACK_ID, ITUNES_NON_AIRTEL_MUSIC_PACK_ID, ITUNES_AIRTEL_PACK_USER_MUSIC_PACK_ID,
            ITUNES_NON_AIRTEL_PACK_USER_MUSIC_PACK_ID, PROMO_CODE_MUSIC_PACK_ID_129, PROMO_CODE_MUSIC_PACK_ID_29,
            PROMO_CODE_MUSIC_PACK_ID_NON_AIRTEL_29, PROMO_CODE_MUSIC_PACK_ID_ONE_MONTH, PROMO_CODE_MUSIC_PACK_ID_ONE_MONTH_NON_AIRTEL,
            LAPU_PREPAID_MUSIC_PACK_ID_3G_29
    ));

    public static final List<Integer> QUARTERLY_PACKS                      = Collections.unmodifiableList(Arrays.asList(PROMO_CODE_MUSIC_PACK_ID_THREE_MONTHS, PROMO_CODE_MUSIC_PACK_ID_THREE_MONTHS_NON_AIRTEL, THREE_MONTH_PACK_ID_129, MUSIC_PACK_ID_FREE_THREE_MONTH_NON_AIRTEL,
            BSB_MUSIC_IDEA_3_MONTH, BSB_MUSIC_VODA_3_MONTH));

    public static final List<Integer> HALF_YEARLY_PACKS                    = Collections.unmodifiableList(Arrays.asList(PROMO_CODE_MUSIC_PACK_ID_SIX_MONTHS,
            PROMO_CODE_MUSIC_PACK_ID_SIX_MONTHS_NON_AIRTEL,
            CPE_ONE_PACK_ID,CPE_TWO_PACK_ID,MUSIC_PACK_ID_AIRTEL_SURPRISES,
            MUSIC_PACK_ID_FREE_ONE_MONTH, POSTPAID_TWO_PACK_ID, POSTPAID_ONE_PACK_ID, FOURG_INFINITY_PACK_ID,
            BB_LOWER_PACK_ID, BB_HIGHER_PACK_ID, RAPU_PREPAID_MUSIC_PACK_29
    ));

    public static final int           MONTHLY_PACK_VALIDITY        = 30;
    public static final int           QUARTERLY_PACK_VALIDITY      = 90;
    public static final int           HALF_YEARLY_VALIDITY         = 180;
    public static final int           YEARLY_PACK_VALIDITY         = 365;


    public static final HashMap<Integer, List<Integer>> VALIDITY_PRODUCT_IDS_MAP_FOR_ALERTS = new HashMap<Integer, List<Integer>>(){{
        put(YEARLY_PACK_VALIDITY, YEARLY_PACKS);
        put(HALF_YEARLY_VALIDITY, HALF_YEARLY_PACKS);
        put(QUARTERLY_PACK_VALIDITY, QUARTERLY_PACKS);
        put(MONTHLY_PACK_VALIDITY, MONTHLY_PACKS);
    }};


    public static final List<Integer> ALL_IBM_PACKS                     = Collections.unmodifiableList(Arrays.asList(LAPU_PREPAID_MUSIC_PACK_ID_29, LAPU_PREPAID_MUSIC_PACK_ID_29_CLM,
            LAPU_PREPAID_MUSIC_PACK_ID_129, LAPU_PREPAID_MUSIC_PACK_ID_29_PAPER, LAPU_PREPAID_MUSIC_PACK_ID_29_3G,
            IBM_MUSIC_PACK_ID_29, IBM_OFFER_MUSIC_PACK_ID_29, IBM_MUSIC_PACK_ID_99, IBM_OFFER_MUSIC_PACK_ID_99,
            POSTPAID_MUSIC_SUBSCRIPTION_PRODUCT_ID, LAPU_PREPAID_MUSIC_PACK_ID_3G_29, LAPU_PREPAID_MUSIC_PACK_ID_3G_29, THREE_MONTH_PACK_ID_129));

    public static final int           NON_AIRTEL_MUSIC_PACK_PRICE       = 99;

    public static final int           IDEA_MUSIC_PACK_PRICE      		= 30;


    public static final int           AIRTEL_MUSIC_PACK_PRICE           = 29;

    public static final int           AIRTEL_FREE_DATA_MUSIC_PACK_PRICE = 129;

    public static final int           ITUNES_AIRTEL_MUSIC_PACK_PRICE    = 60;

    public static final int           ITUNES_NON_AIRTEL_MUSIC_PACK_PRICE = 120;

    public static final List<String>  validPricePoints                  = Collections.unmodifiableList(Arrays.asList("" + AIRTEL_MUSIC_PACK_PRICE, "" + NON_AIRTEL_MUSIC_PACK_PRICE, ""
            + AIRTEL_FREE_DATA_MUSIC_PACK_PRICE,"" + IDEA_MUSIC_PACK_PRICE));

    public static final List<Integer> RECURRING_SUBSCRIPTION_MUSIC_PACKS       = Collections.unmodifiableList(Arrays.asList(IBM_MUSIC_PACK_ID_29, IBM_OFFER_MUSIC_PACK_ID_29, IBM_MUSIC_PACK_ID_99,
            IBM_OFFER_MUSIC_PACK_ID_99, BSB_MUSIC_PACK_ID, SAMSUNG_SDK_PACK));

    public static final List<Integer> ALL_ITUNES_PACKS       = Collections.unmodifiableList(Arrays.asList(ITUNES_AIRTEL_PACK_USER_MUSIC_PACK_ID,
            ITUNES_AIRTEL_MUSIC_PACK_ID,ITUNES_NON_AIRTEL_MUSIC_PACK_ID,ITUNES_NON_AIRTEL_PACK_USER_MUSIC_PACK_ID ));

    public static final List<Integer> BUNDLE_PACKS = Collections.unmodifiableList(Arrays.asList(BUNDLE_PACK_2_MONTHS, BUNDLE_PACK_6_MONTHS));

    public static final List<Integer> OFFLINE_MUSIC_PACKS          = Collections.unmodifiableList(Arrays.asList(LAPU_PREPAID_MUSIC_PACK_ID_29, LAPU_PREPAID_MUSIC_PACK_ID_29_CLM,
            LAPU_PREPAID_MUSIC_PACK_ID_129, LAPU_PREPAID_MUSIC_PACK_ID_29_PAPER, LAPU_PREPAID_MUSIC_PACK_ID_29_3G, BUNDLE_PACK_2_MONTHS, BUNDLE_PACK_6_MONTHS, POSTPAID_MUSIC_SUBSCRIPTION_PRODUCT_ID));

    public static final List<Integer> FREE_OFFER_PACKS          = Collections.unmodifiableList(Arrays.asList(POSTPAID_MUSIC_SUBSCRIPTION_PRODUCT_ID, MUSIC_PACK_ID_FREE_ONE_MONTH, MUSIC_PACK_ID_FREE_THREE_MONTH_NON_AIRTEL, BSB_MUSIC_VODA_3_MONTH));
    public static final List<Integer> PAID_PACKS = Collections.unmodifiableList(Arrays.asList(SAMSUNG_SDK_PACK, BSB_MUSIC_PACK_ID,BSB_MUSIC_IPAYY_IBM_29_PACK_ID, BSB_MUSIC_IPAYY_IBM_129_PACK_ID,ITUNES_AIRTEL_MUSIC_PACK_ID,ITUNES_NON_AIRTEL_MUSIC_PACK_ID,IBM_MUSIC_PACK_ID_99,IBM_OFFER_MUSIC_PACK_ID_29,IBM_MUSIC_PACK_ID_29,IBM_OFFER_MUSIC_PACK_ID_99,BSB_MUSIC_IDEA_99_PAID));
}