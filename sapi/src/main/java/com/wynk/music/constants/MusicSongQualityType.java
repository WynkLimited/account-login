package com.wynk.music.constants;

import java.util.Comparator;

/**
 * Created by bhuvangupta on 03/03/14.
 */
public enum MusicSongQualityType {
    a,l,m,h,hd;

    public static int getBitrateForSongQuality(MusicSongQualityType songQualityType)
    {
        if(songQualityType == null)
            return -1;

        switch (songQualityType)
        {
            case hd:
                return 320000;
            case h:
                return 128000;
            case m:
                return 64000;
            case l:
                return 32000;
        }
        return -1;
    }

    public static final Comparator<String> songQualityTypeComparator = new Comparator<String>() {
        @Override
        public int compare(final String sq1, final String sq2) {

            MusicSongQualityType sqt1 = MusicSongQualityType.valueOf(sq1);
            MusicSongQualityType sqt2 = MusicSongQualityType.valueOf(sq2);

            int sqtbr1 = getBitrateForSongQuality(sqt1);
            int sqtbr2 = getBitrateForSongQuality(sqt2);

            return (sqtbr1 < sqtbr2) ? 1 : ((sqtbr1 == sqtbr2) ? 0 : -1);
        }
    };


}
