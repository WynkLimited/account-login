package com.wynk.utils;

/**
 * Created by bhuvangupta on 24/03/14.
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static int nthIndexOf(final String string, final String token,
                                 final int index)
    {
        int j = 0;

        for (int i = 0; i < index; i++)
        {
            j = string.indexOf(token, j + 1);
            if (j == -1) break;
        }

        return j;
    }

    public static int wordCount(String string){
        if (string == null)
            return 0;
        return string.trim().split("\\s+").length;
    }
}
