package com.wynk.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.wynk.constants.MusicSubscriptionPackConstants.*;

public class SubscriptionPack {

    private int                           packValidatyInDays;

    private int                           packPrice;

    private int                           songsLimit;

    private static String purchaseUrlFormat = "http://125.21.241.25/wps/portal/consent?pn=twang+music+app&pp=%s&pi=%s&m=%s&mth=handleNewSubcription&pu=Re"
                                                                    + "&pv=%s+Days&pc=Unlimited+downloads+@+Rs+%s+per+month&dt=%s&aoc=CP-OK&ci=143"
                                                                    + "&cn=BSB&ru=%s&opt1=%s&opt2=%s&opt3=vc&opt4=%s&cpw=%s";

    private static final SimpleDateFormat sdf               = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");

    public static final SubscriptionPack  musicPack;
    public static final SubscriptionPack  nonAirtelMusicPack;

    static {
        musicPack = new SubscriptionPack();
        musicPack.setPackPrice(AIRTEL_MUSIC_PACK_PRICE);
        musicPack.setPackValidatyInDays(PACK_VALIDITY);
        musicPack.setSongsLimit(-1);

        nonAirtelMusicPack = new SubscriptionPack();
        nonAirtelMusicPack.setPackPrice(NON_AIRTEL_MUSIC_PACK_PRICE);
        nonAirtelMusicPack.setPackValidatyInDays(PACK_VALIDITY);
        nonAirtelMusicPack.setSongsLimit(-1);
    }

    public int getPackValidatyInDays() {
        return packValidatyInDays;
    }

    public void setPackValidatyInDays(int packValidatyInDays) {
        this.packValidatyInDays = packValidatyInDays;
    }

    public int getPackPrice() {
        return packPrice;
    }

    public void setPackPrice(int packPrice) {
        this.packPrice = packPrice;
    }

    public int getSongsLimit() {
        return songsLimit;
    }

    public void setSongsLimit(int songsLimit) {
        this.songsLimit = songsLimit;
    }

    public static String getPurchaseUrl(String msisdn, String returnUrl, String imageUrl, String lang, int packId, String price) {
        return String.format(purchaseUrlFormat, price, packId, msisdn, PACK_VALIDITY, price,
                sdf.format(new Date(System.currentTimeMillis())), returnUrl, imageUrl, lang, returnUrl,
                "B%2BxsCJBZe3kzbKBfiqR7Mg%3D%3D");
    }

}
