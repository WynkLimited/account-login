package com.wynk.music.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bhuvangupta on 28/01/14.
 */
public enum MusicSongStatus {

    ERROR(-1), NONE (0), INITIATED(1), INPROGRESS(2), COMPLETED(3);


    private static Map<Integer, MusicSongStatus> statusIdMapping = new HashMap<>();

    static {
        for (MusicSongStatus sts : MusicSongStatus.values()) {
            statusIdMapping.put(sts.getStatus(), sts);
        }
    }

    private int status = 0;

    private MusicSongStatus(int status)
    {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static MusicSongStatus getSongStatus(int statusId) {
        MusicSongStatus musicStatusType = statusIdMapping.get(statusId);
        if (musicStatusType != null)
            return musicStatusType;

        return NONE; // default
    }

}
