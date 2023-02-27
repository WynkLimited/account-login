//
//  COPYRIGHT  (c) 2000 Akamai TechnoAlogies Inc.,
//      All Rights Reserved.
//
// PROPRIETARY - AKAMAI AND AUTHORIZED CLIENTS ONLY.
//
// This document contains proprietary information that shall
// be distributed or routed only within Akamai Technologies
// Inc. (Akamai), and its authorized clients, except
// with written permission of Akamai.
//

package com.akamai.authentication.URLToken;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class URLToken
{
    private String url;
    private String salt;
    private String extract;
    private long window;
    private long time;
    private long expires;
    private String token;

    public URLToken()
    {
        throw new IllegalArgumentException("No parameter constructor is not supported");
    }

    public URLToken(String userURL, long userWindow, String userSalt, String userExtract, long userTime)
    {
        StringBuffer MD5OutHex = new StringBuffer();
        MessageDigest md5;
        byte md5Digested[];
        byte expBytes[] = new byte[4];

            // check validity of input
        if (userURL == null || userURL.length() == 0)
            throw new IllegalArgumentException("You must provide a URL");
        if (userSalt == null || userSalt.length() == 0)
            throw new IllegalArgumentException("You must provide a salt");
        if (userWindow < 0)
            throw new IllegalArgumentException("Expiration Window must not be negative");

            // Copy user input to our local copies
        url = new String(userURL);
        salt = new String(userSalt);
        if (userExtract == null || userExtract.length() == 0)
            extract = null;
        else
            extract = new String(userExtract);
        window = userWindow;
        time = (userTime <= 0) ? (System.currentTimeMillis()/1000) : userTime;

            // set expires based on user input
        expires = time + window;

        try
        {
            md5 = MessageDigest.getInstance("MD5");
        }
        catch(NoSuchAlgorithmException e)
        {
            throw new IllegalArgumentException("Can't get MD5 instance " + e);
        }

        expBytes[0] = (byte) (expires & 0xff);
        expBytes[1] = (byte) ((expires >> 8) & 0xff);
        expBytes[2] = (byte) ((expires >> 16) & 0xff);
        expBytes[3] = (byte) ((expires >> 24) & 0xff);

            // Do the first MD5
        md5.update(expBytes);
        md5.update(url.getBytes());
        if (extract != null && extract.length() > 0)
            md5.update(extract.getBytes());
        md5.update(salt.getBytes());
        md5Digested = md5.digest();

        byte tempBytes[] = new byte[md5Digested.length];
        for (int i = 0; i < md5Digested.length; i++) {
            tempBytes[i] = md5Digested[i];
        }

           // Do the second MD5
        md5.reset();
        md5.update(salt.getBytes());
        md5.update(tempBytes);

           // Get the result, convert to hex
        md5Digested = md5.digest();
        for (int i = 0; i < md5Digested.length; i++) {
            String y = Integer.toHexString(md5Digested[i] & 0xff);// Adjust to 0 - 255
            if (y.length() == 1)
                MD5OutHex.append('0');
            MD5OutHex.append(y);
        }

        token = MD5OutHex.toString();
    }

    public URLToken(String userURL, long userWindow, String userSalt, String userExtract)
    {
        this(userURL, userWindow, userSalt, userExtract, 0);
    }

    public String getToken()
    {
        return token;
    }

    public String getURL()
    {
        return url;
    }

    public long getWindow()
    {
        return window;
    }

    public long getTime()
    {
        return time;
    }

    public long getExpires()
    {
        return expires;
    }

    public String getExtract()
    {
        return extract;
    }

    public String getSalt()
    {
        return salt;
    }
}
