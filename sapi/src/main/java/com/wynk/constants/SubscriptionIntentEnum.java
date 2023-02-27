package com.wynk.constants;

/**
 * Created by Aakash on 31/07/17.
 */
public enum SubscriptionIntentEnum {

    REMOVE_ADS("removeAds"),
    FUP_LIMIT("fupLimit"),
    FMF_EXPIRE("fmfExpire"),
    UNSUBSCRIBED_DOWNLOAD("unsubscribedDownload"),
    OFFLINE_DOWNLOADS("offlineDownloads");

    SubscriptionIntentEnum(String subscriptionIntent) {
        this.subscriptionIntent = subscriptionIntent;
    }

    private String subscriptionIntent;

    public String getSubscriptionIntent() {
        return subscriptionIntent;
    }
}
