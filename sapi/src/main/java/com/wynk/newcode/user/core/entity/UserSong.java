package com.wynk.newcode.user.core.entity;

import com.wynk.newcode.user.core.constant.UserSongType;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Table(value = "user_songs")
public class UserSong implements Comparable {

    @PrimaryKeyColumn(name = "uid", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String uid;

    @PrimaryKeyColumn(name = "type", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private UserSongType songType;

    @PrimaryKeyColumn(name = "song_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private String songId;

    @Column(value = "updated_on")
    private Date updatedOn;

    @Column(value = "meta")
    private Map<String, String> meta;

    @Transient
    private final static String clickToDownloadFlag = "ctd";
    @Transient
    private final static String purchaseSourceFlag = "srcId";
    @Transient
    private final static String purchasePriceFlag = "pc";
    @Transient
    private final static String purchaseDeletedFlag = "del";
    @Transient
    private final static String purchaseDownloadCountFlag = "dc";
    @Transient
    private final static String purchaseLastDownloadTimeFlag = "ldt";


    public UserSong() {
    }

    public UserSong(
            String uid, UserSongType songType, String songId, long updatedOn, Boolean clickToDownload) {
        this.uid = uid;
        this.songType = songType;
        this.songId = songId;
        this.updatedOn = new Date(updatedOn);
        this.meta = new HashMap<>();
        if (clickToDownload != null) {
            this.meta.put(clickToDownloadFlag, clickToDownload.toString());
        }
    }

    public static UserSong newUserPurchase(
            String uid,
            String songId,
            Double price,
            String sourceId,
            Boolean deleted
    ) {
        UserSong userPurchase = new UserSong();
        userPurchase.uid = uid;
        userPurchase.songType = UserSongType.PURCHASE;
        userPurchase.songId = songId;
        userPurchase.updatedOn = new Date();
        userPurchase.meta = new HashMap<>();
        if (price != null) userPurchase.meta.put(purchasePriceFlag, String.valueOf(price));
        userPurchase.meta.put(purchaseSourceFlag, sourceId);
        if (deleted != null) userPurchase.meta.put(purchaseDeletedFlag, String.valueOf(deleted));
        userPurchase.meta.put(purchaseDownloadCountFlag, String.valueOf(1));
        userPurchase.meta.put(purchaseLastDownloadTimeFlag, String.valueOf(new Date().getTime()));
        return userPurchase;
    }

    public void updateLastDownloadTime() {
        this.getMeta().put(purchaseLastDownloadTimeFlag, String.valueOf(new Date().getTime()));
    }

    public void updateDeletedFlag(Boolean deleted) {
        this.getMeta().put(purchaseDeletedFlag, String.valueOf(deleted));
    }

    public void incDownloadCount() {
        String downloadCountStr = this.getMeta().get(purchaseDownloadCountFlag);
        int downloadCount = NumberUtils.toInt(downloadCountStr, 0);
        this.getMeta().put(purchaseDownloadCountFlag, String.valueOf(downloadCount));
    }

    public String getPurchaseDownloadCountFlag(){
        return purchaseDownloadCountFlag;
    }

    public String getUid() {
        return uid;
    }

    public String getSongId() {
        return songId;
    }

    public long getUpdatedOn() {
        return updatedOn.getTime();
    }

    public UserSongType getSongType() {
        return songType;
    }

    private Map<String, String> getMeta() {
        return meta == null ? new HashMap<>() : meta;
    }

    public Boolean getClickToDownload() {
        return Boolean.valueOf(getMeta().getOrDefault(clickToDownloadFlag, "false"));
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof UserSong)) return -1;

        if (getUpdatedOn() > ((UserSong) o).getUpdatedOn()) return -1;
        if (getUpdatedOn() < ((UserSong) o).getUpdatedOn()) return 1;

        return 0;
    }
}
